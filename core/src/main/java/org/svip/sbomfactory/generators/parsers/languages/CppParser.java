package org.svip.sbomfactory.generators.parsers.languages;

import org.svip.sbomfactory.generators.utils.ParserComponent;
import org.svip.sbomfactory.generators.utils.virtualtree.VirtualPath;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.svip.sbomfactory.generators.utils.Debug.LOG_TYPE;
import static org.svip.sbomfactory.generators.utils.Debug.log;

/**
 * file: CppParser.java
 * Description: Language specific implementation of the Parser (C++)
 *
 * @author Derek Garcia
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
    protected boolean isInternalComponent(ParserComponent component) {
        for(VirtualPath internalComponent : internalFiles) {
            if(component.getName() != null && internalComponent.endsWith(new VirtualPath(component.getName()))){
                return true;
            } else if (component.getGroup() != null && internalComponent.endsWith(new VirtualPath(component.getGroup()))) {
                return true;
            }
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
    protected boolean isLanguageComponent(ParserComponent component) {
        // Attempt to find component
        try{
            if(queryURL(STD_LIB_URL + component.getName(), true).getResponseCode() == 200) return true;

            // if external, test if header is in C Library (https://cplusplus.com/reference/clibrary/)
            if(component.getType() == ParserComponent.Type.EXTERNAL && component.getName().contains(".h")){
                String clib = "c"+component.getName().split("\\.")[0];
                log(LOG_TYPE.DEBUG, "EXTERNAL [ " + component.getName() + " ] not found, attempting clib");
                return queryURL(STD_LIB_URL + clib, true).getResponseCode() == 200;
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
    protected void parseRegexMatch(ArrayList<ParserComponent> components, Matcher matcher) {
        ParserComponent c;
        // group 1: External component, capture inside '<' and '>'
        if (matcher.group(1) != null) {
            c = new ParserComponent(matcher.group(1));
            // group 2: 'Internal' component, capture inside '"' and '"'
        } else if (matcher.group(2) != null) {
            c = new ParserComponent(matcher.group(2));
            c.setType(ParserComponent.Type.INTERNAL); // "foo" files are internal
        } else {
            // Exclude warnings about comments
            if (matcher.group(0).contains("\\\\"))
                log(LOG_TYPE.WARN, "Match (" + matcher.group(0) + ") has no Groups; Skipping. . .");
            return;
        }

        // Check if internal
        boolean isInternal = isInternalComponent(c);

        // If already marked as internal and is not, change to external
        if (!isInternal && c.getType() == ParserComponent.Type.INTERNAL) {
            log(LOG_TYPE.WARN, "ParserComponent [ " + c.getName() + " ] was marked as INTERNAL, but not found. Changing to EXTERNAL");
            c.setType(ParserComponent.Type.EXTERNAL);
            // Else mark as internal
        } else if (isInternal) {
            c.setType(ParserComponent.Type.INTERNAL);
        }

        // Only check EXTERNAL if Language components
        if (c.getType() == ParserComponent.Type.EXTERNAL && isLanguageComponent(c))
            c.setType(ParserComponent.Type.LANGUAGE);

        // Add Component
        components.add(c);
    }
}
