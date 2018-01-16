package com.siegetechnologies.cqf.core.experiment.design.variant;

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

import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementImpl;
import com.siegetechnologies.cqf.core.experiment.design.util.ParameterImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates variants based on the versioned files required by an item.
 *
 * This class parses all files in the requirements section of an ExperimentDesignElement
 * configuration and if any version files are present, will generate
 * a variant specifier for each of them.
 */
public class VariantGeneratorBasedOnElementRequiredFileVersions implements VariantGenerator {
    private static final String CQF_VERSION = "-cqfversion-"; // FIXME: STRING: srogers
    private static final String CQF_SUBVERSION = "-cqfsubversion-"; // FIXME: STRING: srogers
    private static final String VERSION_SPLIT_REGEX = "-cqfversion-|-cqfsubversion-"; // FIXME: STRING: srogers
    private static final String REPLACE_SUFFIX_REGEX = "\\.[a-zA-Z]{2,}$";

    /**
     * Generates a list of variant specifiers for a design element.  This can be
     * fed into another engine to do the actual construction of ExperimentDesignElement
     * variants
     *
     * @param designElement ExperimentDesignElement to generate variants of
     *
     * @return List of specifiers for new items.
     */
    @Override
    public List<VariantSpec> generate(ExperimentDesignElementImpl designElement) {

        return new ArrayList<>(); // TODO: REVIEW: srogers: design elements no longer have requirements

//      return designElement.getRequirements().files.stream()
//              .filter( fn -> fn.contains("-cqfversion-")) // FIXME: STRING: srogers
//              .map( fn -> buildVariantSpecFor(designElement, fn ) )
//              .filter( Objects::nonNull )
//              .collect(Collectors.toList());
    }

    private VariantSpec buildVariantSpecFor(ExperimentDesignElementImpl designElement, String fileName )
    {
        if( !fileName.contains(CQF_VERSION))
        {
            return null;
        }

        String versionName = fileName.split(VERSION_SPLIT_REGEX)[1].replaceFirst( REPLACE_SUFFIX_REGEX, "");
        String subVersionName = null;

        String variantName = String.format("%s %s", designElement.getName(), versionName);
        if( fileName.contains(CQF_SUBVERSION) )
        {
            subVersionName = fileName.split(VERSION_SPLIT_REGEX)[2].replaceFirst(REPLACE_SUFFIX_REGEX, "");
            variantName = variantName + " " + subVersionName;
        }

        VariantSpec newSpec = new VariantSpec(designElement, variantName);

        newSpec.addParameter( buildVersionParameter( versionName ) );

        if( subVersionName != null ) {
            newSpec.addParameter(buildSubversionParameter(subVersionName));
        }

        return newSpec;
    }

    private ParameterImpl buildVersionParameter(String version )
    {
        return new ParameterImpl.Builder()
                .setName("version")        // FIXME: STRING: srogers
                .setLabel("Version")       // FIXME: STRING: srogers
                .setType("string")
                .setDefaultValue(version)
                .setRequired(true)
                .build();
    }

    private ParameterImpl buildSubversionParameter(String subversion )
    {
        return new ParameterImpl.Builder()
                .setName("subversion")        // FIXME: STRING: srogers
                .setLabel("Subversion")       // FIXME: STRING: srogers
                .setType("string")
                .setDefaultValue(subversion)
                .setRequired(true)
                .build();
    }
}
