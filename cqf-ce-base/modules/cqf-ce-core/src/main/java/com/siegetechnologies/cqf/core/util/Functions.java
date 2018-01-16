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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Static utilities for working with Functions
 *
 * @author taylorj
 */
public class Functions {

    private Functions() {
    }

    /**
     * Like a function, but can throw an {@link IOException}.
     *
     * @param <T> the input type
     * @param <R> the output type
     * @author taylorj
     */
    @FunctionalInterface
    public interface CheckedIOFunction<T, R> {
        /**
         * Applies the function to the argument.
         *
         * @param t the argument
         * @return the result
         * @throws IOException if an I/O error occurs
         */
        R apply(T t) throws IOException;
    }

    /**
     * Like a supplier, but can throw an {@link IOException}.
     *
     * @param <T> the result type
     * @author taylorj
     */
    @FunctionalInterface
    public interface CheckedIOSupplier<T> {
        /**
         * Gets the value.
         *
         * @return the value
         * @throws IOException if an I/O error occurs
         */
        T get() throws IOException;
    }

    /**
     * Returns a function like a provided checked IO function, that throws an
     * unchecked IO exception if and only if the original function throws an
     * IOException.
     *
     * @param function the checked function
     * @return the unchecked function
     */
    public static <T, R> Function<T, R> uncheck(CheckedIOFunction<T, R> function) {
        return t -> {
            try {
                return function.apply(t);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };
    }

    /**
     * Returns a supplier like a provided checked IO supplier, that throws an
     * unchecked IO exception if and only if the original supplier throws an
     * IOException.
     *
     * @param supplier the checked supplier
     * @return the unchecked supplier
     */
    public static <T> Supplier<T> uncheck(CheckedIOSupplier<T> supplier) {
        return () -> {
            try {
                return supplier.get();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };
    }

    /**
     * Exception wrapping another exception.
     *
     * @author taylorj
     */
    public static class WrappedException extends RuntimeException {
        private static final long serialVersionUID = -2087703033725094857L;

        /**
         * Creates a new instance wrapping a cause.
         *
         * @param cause the cause
         */
        public WrappedException(Exception cause) {
            super(cause.getMessage(), cause);
        }
    }

    /**
     * Coalesces the result of a function applied to one or more values.
     * First, the mapper function is applied to <code>u1</code>.  If the
     * result is non-null, is is returned.  Otherwise, the function is applied
     * to <code>u2</code> and the result is returned, even if it is null.
     *
     * @param mapper the mapping function
     * @param u1     the first input
     * @param u2     the second input
     * @param <U>    the type of the input to the function
     * @param <T>    the type of the result of the function
     * @return the coalesced result
     */
    public static <T, U> T coalesce(Function<U, T> mapper, U u1, U u2) {
        return Optional.ofNullable(mapper.apply(u1))
                .orElse(mapper.apply(u2));
    }

    /**
     * Combines the set of consumers into a single operation
     *
     * @param consumers
     * @param <T>
     * @return
     */
    @SafeVarargs
    public static <T> Consumer<T> combine(Consumer<T>... consumers) {
        Consumer<T> first = consumers[0];
        return Arrays.stream(consumers, 1, consumers.length).reduce(first, Consumer::andThen);
    }

    /**
     * Represents a function that maps one value to another, like
     * {@link Function}, but whose {@link #apply(Object)} method may throw an
     * exception.
     *
     * @param <T> the type of the input to the function
     * @param <R> the type of the result of the function
     * @param <E> the type of the exception thrown by the function
     * @author taylorj
     */
    @FunctionalInterface
    public interface ThrowingFunction<T, R, E extends Exception> {
        /**
         * Applies this function to an argument and returns the result.
         *
         * @param object the argument
         * @return the result
         * @throws Exception if an exception occurs
         */
        R apply(T object) throws E;

        /**
         * Returns a function that applies <code>function</code> to an argument
         * and throws a wrapped exception if <code>function</code> throws an
         * exception.
         *
         * @param function the throwing function
         * @return the function
         */
        static <T, R> Function<T, R> uncheck(ThrowingFunction<T, R, ?> function) {
            Objects.requireNonNull(function, "function must not be null");
            return t -> {
                try {
                    return function.apply(t);
                } catch (Exception e) {
                    throw new WrappedException(e);
                }
            };
        }

        /**
         * Returns a unchecked version of this function.  This is equivalent
         * to <code>ThrowingFunction.uncheck(this);</code>.
         *
         * @return the function
         * @see ThrowingFunction#uncheck(ThrowingFunction)
         */
        default Function<T, R> uncheck() {
            return ThrowingFunction.uncheck(this);
        }
    }

    /**
     * Represents a function that maps two values to another, like
     * {@link BiFunction}, but whose {@link #apply(Object)} method may throw an
     * exception.
     *
     * @param <T> the type of the input to the function
     * @param <U> the type of the input to the function
     * @param <R> the type of the result of the function
     * @param <E> the type of the exception thrown by the function
     * @author taylorj
     */
    @FunctionalInterface
    public interface ThrowingBiFunction<T, U, R, E extends Exception> {
        /**
         * Applies this function to an argument and returns the result.
         *
         * @param object the argument
         * @param other  the other argument
         * @return the result
         * @throws Exception if an exception occurs
         */
        R apply(T object, U other) throws E;

        /**
         * Returns a function that applies <code>function</code> to an argument
         * and throws a wrapped exception if <code>function</code> throws an
         * exception.
         *
         * @param function the throwing function
         * @return the function
         */
        static <T, U, R> BiFunction<T, U, R> uncheck(ThrowingBiFunction<T, U, R, ?> function) {
            Objects.requireNonNull(function, "function must not be null");
            return (t, u) -> {
                try {
                    return function.apply(t, u);
                } catch (Exception e) {
                    throw new WrappedException(e);
                }
            };
        }

        /**
         * Returns a unchecked version of this function.  This is equivalent
         * to <code>ThrowingFunction.uncheck(this);</code>.
         *
         * @return the function
         * @see ThrowingFunction#uncheck(ThrowingFunction)
         */
        default BiFunction<T, U, R> uncheck() {
            return ThrowingBiFunction.uncheck(this);
        }
    }

}
