/**
 *  Copyright (c) 2016 Siege Technologies.
 */
package com.siegetechnologies.cqf.core.util;

/*-
 * #%L
 * cqf-ce-core
 * %%
 * Copyright (C) 2009 - 2017 Siege Technologies, LLC
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.Validate.isTrue;

/**
 * Utility methods for handling concurrent execution of tasks, retrying tasks
 * depending on conditions, and awaiting conditions.
 * 
 * @author taylorj
 */
public class Concurrency {
    private static final Logger logger = LoggerFactory.getLogger(Concurrency.class);

    /**
     * Invokes each of a collection of callables using a executor service provided
     * by a supplier, and returns a list of the individual results.  This method makes an
     * attempt to cancel any tasks that have not completed before returning.
     * 
     * @param callables the collection of callables
     * @param executorServiceSupplier the executor service supplier
     * @return a list of the callable results
     * 
     * @throws InterruptedException if the callables or this method are interrupted
     * @throws ExecutionException if any of the callables throw an exception
     * 
     * @param <T> the result type of the callables
     */
    public static final <T> List<T> invokeAll(Collection<Callable<T>> callables, Supplier<ExecutorService> executorServiceSupplier) throws InterruptedException, ExecutionException {
        ExecutorService service = executorServiceSupplier.get();
        List<Future<T>> futures = callables.stream().map(service::submit).collect(Collectors.toList());
        // Try to get the result from each future, but if any of them
        // fail, catch the exception and throw a wrapper around it,
        // but make a good-faith attempt to ensure that they'll
        // all completed or cancelled, too.
        List<T> result = new ArrayList<>(callables.size());
        try {
            for (Future<T> future : futures) {
                result.add(future.get());
            }
            return result;
        }
        finally {
            futures.stream()
            .filter(f -> !f.isDone())
            .forEach(f -> f.cancel(true));
        }
    }

    /**
     * Attempts to invoke a callable and return its result, up to some maximum number of times,
     * continuing if any thrown exception satisfies a test, and executing some action between
     * attempts.
     *
     * @param tries the maximum number of attempts to make
     * @param callable the callable
     * @param test the exception test
     * @param inBetween action to call after a exception satisfying the test is thrown
     * @return the result from the callable
     *
     * @throws ExecutionException if an execution occurs during execution
     * @throws InterruptedException if the retry process is interrupted
     * 
     * @param <V> callable type
     */
    public static <V> V retry(int tries, Callable<V> callable, Predicate<? super Throwable> test, Runnable inBetween) throws ExecutionException, InterruptedException {
    	isTrue(tries > 0, "Number of tries must be greater than zero:", tries);
        Exception lastException = null;
        for (int i=0; i<tries; i++) {
            if (lastException != null) {
                inBetween.run();
            }
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            try {
                return callable.call();
            } catch (Exception e) {
                if (test.test(e)) {
                    logger.trace("Retrying after exception.",e);
                    lastException = e;
                }
                else {
                    throw new ExecutionException("Aborting retry; exception did not pass the test.", e);
                }
            }
        }
        throw new ExecutionException("Exhausted retries.",lastException);
    }

    /**
     * Uses a no-op <code>inBetween</code>.
     *
     * @see #retry(int, Callable, Predicate, Runnable)
     * 
     * @param tries the number of tries 
     * @param callable the callable
     * @param test a test 
     * @return the result from the callable
     * 
     * @throws ExecutionException if an exception occurs
     * @throws InterruptedException if retry is interrupted
     * 
     * @param <V> callable type
     */
    public static <V> V retry(int tries, Callable<V> callable, Predicate<? super Throwable> test) throws ExecutionException, InterruptedException {
        return retry(tries, callable, test, Concurrency::doNothing);
    }
    
    /**
     * Do nothing.  
     * 
     * <p>This is used as an argument to {@link #retry(int, Callable, Predicate, Runnable)}
     * by {@link #retry(int, Callable, Predicate)} as fourth argument.
     */
    private static void doNothing() {
    	// do nothing
    }

    /**
     * Uses a no-op <code>inBetween</code>, and a test that checks whether the
     * throwable is an instance of {@link Exception} (as opposed to {@link Error}).
     *
     * @see #retry(int, Callable, Predicate, Runnable)
     * 
     * @param tries number of tries
     * @param callable the callable
     * @return the result of the callable
     * 
     * @throws ExecutionException if the callable throws an exception
     * @throws InterruptedException if the retrying is interrupted
     * 
     * @param <V> callable type
     */
    public static <V> V retry(int tries, Callable<V> callable) throws ExecutionException, InterruptedException {
        return retry(tries, callable, Exception.class::isInstance);
    }

    /**
     * Retries up to a specified number of times, delaying a fixed amount
     * between each attempt.  The test used to determine whether a task
     * is retryable or not is simply whether the caught throwable is an
     * instance of {@link Exception};  that means that everything except
     * an {@link Error} or a non-Exception Throwable is retryable.
     *
     * @param tries the number of tries
     * @param callable the callable
     * @param timeUnit the unit of the delay
     * @param delay the measure of the delay
     * @return the result of the callable
     *
     * @throws ExecutionException if execution throws an exception
     * @throws InterruptedException if interrupted
     *
     * @param <V> callable type
     */
    public static <V> V retry(int tries, Callable<V> callable, TimeUnit timeUnit, long delay) throws ExecutionException, InterruptedException {
    	return retry(tries, callable, Exception.class::isInstance, timeUnit, delay);
    }

    /**
     * Like {@link #retry(int, Callable, Predicate, Runnable)}, using a
     * <code>inBetween</code> action that sleeps for a duration specified
     * by a <code>timeUnit</code> and <code>delay</code>
     *
     * @see #retry(int, Callable, Predicate, Runnable)
     * 
     * @param tries the number of tries
     * @param callable the callable
     * @param test the test
     * @param timeUnit the time unit
     * @param delay the delay
     * @return the result of the callable
     * 
     * @throws ExecutionException the callable throws an exception
     * @throws InterruptedException if retry is interrupted
     * 
     * @param <V> callable type
     */
    public static <V> V retry(int tries, Callable<V> callable, Predicate<? super Throwable> test, TimeUnit timeUnit, long delay) throws ExecutionException, InterruptedException {
        return retry(tries, callable, test, Interruptible.toRunnable(() -> timeUnit.sleep(delay)));
    }

    /**
     * A functional interface for actions that may throw
     * an {@link InterruptedException}.
     *
     * @author taylorj
     */
    @FunctionalInterface
    public interface Interruptible {
        /**
         * Perform the action.
         *
         * @throws InterruptedException if the action is interrupted
         */
        void run() throws InterruptedException;

        /**
         * Returns a runnable that performs the action, but catches the
         * any {@link InterruptedException} and simply interrupts the
         * thread.
         *
         * @param interruptible the interruptible
         * @return a runnable
         */
        static Runnable toRunnable(Interruptible interruptible) {
            return () -> {
                try {
                    interruptible.run();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            };
        }
    }

    /**
     * Similar to {@link Callable} and {@link Runnable}, but (unlike
     * Callable) the call() method is a void method, and (unlike Runnable)
     * the call() method may throw an Exception.
     * 
     * @param <E> the type of exception thrown by {@link #call()}
     *
     * @author taylorj
     */
    @FunctionalInterface
    public interface VoidCallable<E extends Exception> {
    	/**
    	 * Perform the void callable's action
    	 * @throws Exception
    	 */
        void call() throws E;

        /**
         * Returns a callable that calls {@link #call()} and 
         * returns null.
         *  
         * @return the callable
         */
        default <T> Callable<T> toCallable() {
            return () -> { call(); return null; };
        }
    }

    /**
     * Periodically polls condition at a specified interval until the condition
     * is true, or a timeout is reached.
     *
     * @param condition the condition
     * @param timeout the timeout quantity
     * @param timeoutUnit the unit of the timeout quantity
     * @param interval the interval quantity
     * @param intervalUnit the unit of the interval quantity
     * @param message a message describing the condition awaited
     * @return whether the condition became true
     *
     * @throws InterruptedException if the waiting thread is interrupted
     */
	public static boolean await(
			Supplier<Boolean> condition,
			long timeout,
			TimeUnit timeoutUnit,
			long interval,
			TimeUnit intervalUnit,
			String message) throws InterruptedException {
		long timeoutMillis = timeoutUnit.toMillis(timeout);
		long intervalMillis = intervalUnit.toMillis(interval);
		long elapsedMillis = 0;
		while (elapsedMillis < timeoutMillis) {
			if (condition.get()) {
				return true;
			}
			else {
				try {
					intervalUnit.sleep(interval);
					elapsedMillis += intervalMillis;
				}
				catch (InterruptedException e) {
					logger.debug("Interrupted while waiting on '{}'.", message, e);
					throw e;
				}
			}
		}
		return condition.get();
	}
}
