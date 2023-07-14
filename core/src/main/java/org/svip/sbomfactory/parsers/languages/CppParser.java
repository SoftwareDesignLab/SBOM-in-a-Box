package org.svip.sbomfactory.parsers.languages;

import org.svip.builders.component.SVIPComponentBuilder;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbomfactory.parsers.Parser;
import org.svip.utils.VirtualPath;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.svip.utils.Debug.LOG_TYPE;
import static org.svip.utils.Debug.log;

/**
 * file: CppParser.java
 * Description: Language specific implementation of the Parser (C++)
 *
 * @author Derek Garcia
 * @author Ian Dunn
 */
public class CppParser extends LanguageParser {
    public CppParser() { super("https://cplusplus.com/reference/"); }

    ///
    /// Abstract Method Implementation
    ///

    /**
     * Determines if the component is Internal
     *
     * @param component component to search for
     * @return true if internal, false otherwise
     */
    @Override
    protected boolean isInternalComponent(SVIPComponentObject component) {
        String name = component.getName();
        String group = component.getGroup();

        for(VirtualPath internalComponent : sourceFiles) {
            if(internalComponent.endsWith(new VirtualPath(name))) return true;
            if(group != null && internalComponent.endsWith(new VirtualPath(group))) return true;
        }

        return false;
    }

    /**
     * Query C++ Reference to see if component is from the library
     *
     * @param component component to search for
     * @return true if language, false otherwise
     */
    @Override
    protected boolean isLanguageComponent(SVIPComponentObject component) {
        // Attempt to find component
        try{
            if(Parser.queryURL(STD_LIB_URL + component.getName(), true).getResponseCode() == 200) return true;

            // if external, test if header is in C Library (https://cplusplus.com/reference/clibrary/)
            if(component.getType().equalsIgnoreCase("external") && component.getName().contains(".h")){
                String clib = "c"+component.getName().split("\\.")[0];
                log(LOG_TYPE.DEBUG, "EXTERNAL [ " + component.getName() + " ] not found, attempting clib");
                return Parser.queryURL(STD_LIB_URL + clib, true).getResponseCode() == 200;
            }
        } catch (Exception e){
            log(LOG_TYPE.EXCEPTION, e);
        }
        // Exception
        return false;
    }


    /**
     * Get the C++ regex to check against the file
     *
     * @return C++ regex
     */
    @Override
    protected Pattern getRegex() {
        // Regex Breakdown
        /*
        // (?=//).*
        if (line has '//'){
            match rest of line

        // (?=/\*)
        } else if ( line has open block comment ){
            // [\S\s]*?\* /
            match until close block comment

        // #include.*
        } else if( line contains '#include'){
            // (?=<.*>)
            if( start and end with '<' and '>' )
                // <(.*)>
                group 1: External component, capture inside '<' and '>'

            // (?=".*")
            if( start and end with '"' and '"' )
                // "(.*)"
                group 2: 'Internal' component, capture inside '"' and '"'

        }
        */

        return Pattern.compile("(?=//).*|(?=/\\*)[\\S\\s]*?\\*/|#include.*(?:(?=<.*>)<(.*)>|(?=\".*\")\"(.*)\")", Pattern.MULTILINE);

    }


    /**
     * Given a regex match, parse the result accordingly to get the correct component information
     *
     * @param matcher regex match pattern
     * @return new component
     */
    @Override
    protected void parseRegexMatch(List<SVIPComponentObject> components, Matcher matcher) {
        SVIPComponentBuilder builder = new SVIPComponentBuilder();
        // group 1: External component, capture inside '<' and '>'
        if (matcher.group(1) != null) {
            builder.setName(matcher.group(1));
            // group 2: 'Internal' component, capture inside '"' and '"'
        } else if (matcher.group(2) != null) {
            builder.setName(matcher.group(2));
            builder.setType("INTERNAL"); // "foo" files are internal
        } else {
            // Exclude warnings about comments
            if (matcher.group(0).contains("\\\\"))
                log(LOG_TYPE.WARN, "Match (" + matcher.group(0) + ") has no Groups; Skipping. . .");
            return;
        }

        // Check if internal
        boolean isInternal = isInternalComponent(builder.build());

        // If already marked as internal and is not, change to external
        if (!isInternal && getType(builder).equalsIgnoreCase("internal")) {
            log(LOG_TYPE.WARN, "ParserComponent [ " + getName(builder) + " ] " +
                    "was marked as INTERNAL, but not found. Changing to EXTERNAL");
            builder.setType("EXTERNAL");
            // Else mark as internal
        } else if (isInternal) {
            builder.setType("INTERNAL");
        }

        // Only check EXTERNAL if Language components
        if (getType(builder).equalsIgnoreCase("EXTERNAL") && isLanguageComponent(builder.build()))
            builder.setType("LANGUAGE");

        // Add Component
        components.add(builder.build());
    }
}
