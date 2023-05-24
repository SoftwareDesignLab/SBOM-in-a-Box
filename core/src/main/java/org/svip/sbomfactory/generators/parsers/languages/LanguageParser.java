package org.svip.sbomfactory.generators.parsers.languages;

import org.svip.sbomfactory.generators.parsers.Parser;
import org.svip.sbomfactory.generators.utils.Debug;
import org.svip.sbomfactory.generators.utils.ParserComponent;
import org.svip.sbomfactory.generators.utils.virtualtree.VirtualPath;
import org.svip.sbomfactory.generators.utils.virtualtree.VirtualTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.svip.sbomfactory.generators.utils.Debug.log;

/**
 * <b>File</b>: LanguageParser.java<br>
 * <b>Description</b>: Abstract core Class for language file parsers.
 * This handles all general parsing logic and defines the required
 * methods to be implemented for child LanguageParser instances.
 *
 * @author Dylan Mulligan
 * @author Derek Garcia
 */
public abstract class LanguageParser extends Parser {
    /**
     * Protected Constructor meant for use by language implementations
     * to store their language-specific static values in their respective
     * attributes.
     *
     * @param STD_LIB_URL a URL to the Standard Language Library of the language
     *                    parser
     */
    protected LanguageParser(String STD_LIB_URL) {
        super(STD_LIB_URL);
    }

    /**
     * Determines if the component is Internal
     *
     * @param component component to search for
     * @return true if internal, false otherwise
     */
    protected boolean isInternalComponent(ParserComponent component) {
        String name = component.getName();
        String group = component.getGroup();

        for(VirtualPath internalComponent : internalFiles) {
            VirtualPath noExtension = new VirtualPath(internalComponent.toString().substring(0, internalComponent.toString().indexOf(".")));
            VirtualPath internalPath = new VirtualPath((group == null ? "" : group) + "/" + name);

            if(internalComponent.endsWith(internalPath) || noExtension.endsWith(internalPath)) return true;

            if(internalComponent.getParent().endsWith(internalPath) || noExtension.getParent().endsWith(internalPath))
                return true;

            if(group != null && (internalPath.endsWith(new VirtualPath(group))
                    || noExtension.endsWith(new VirtualPath(group))))
                return true;
        }

        return false;
    }

    //#region Abstract Methods For Language Specific Implementation

    /**
     * Determines if the component is from the language maintainers
     * This will require access to some sort of database to check against
     *
     * @param component component to search for
     * @return true if language, false otherwise
     */
    protected abstract boolean isLanguageComponent(ParserComponent component);

    /**
     * Get the regex to parse with.
     * Implementation: return Pattern.compile("REGEX", Pattern.MULTILINE);
     *
     * @return a list of language specific regexes
     */
    protected abstract Pattern getRegex();

    /**
     * Given a regex match, parse the result accordingly to get the correct component information
     * and append found components to the provided ArrayList.
     *
     * @param components A list of ParserComponents that the found components will be appended to
     * @param matcher regex match pattern
     */
    protected abstract void parseRegexMatch(ArrayList<ParserComponent> components, Matcher matcher);

    //#endregion

    @Override
    public void parse(ArrayList<ParserComponent> components, String fileContents) {
        // Get regex
        final Matcher m = getRegex().matcher(fileContents);

        // Continue testing until find all matches
        while (m.find()) {
            log(Debug.LOG_TYPE.DEBUG, String.format("MATCH: [ %s ]; FILE: [ %s ]", m.group(0), this.PWD));

            // Parse match
            final long t1 = System.currentTimeMillis();

            parseRegexMatch(components, m);

            List<ParserComponent> componentsToRemove = new ArrayList<>();

            for(ParserComponent component : components) {
                if(component.getName().equals("*")) {
                    Debug.log(Debug.LOG_TYPE.DEBUG, String.format("Import wildcard found in component %s with group %s",
                            component.getName(), component.getGroup()));

                    if(component.getGroup() != null) {
                        // Get list of all subgroups in the component group
                        List<String> groups = new ArrayList<>(Arrays.stream(component.getGroup().split("/"))
                                .toList());

                        // Set name to last group of component TODO is this correct behavior?
                        component.setName(groups.remove(groups.size() - 1));

                        // Set new component group, exclude last element
                        component.setGroup(String.join("/", groups));

                        Debug.log(Debug.LOG_TYPE.DEBUG, String.format("Component renamed to %s with group %s",
                                component.getName(), component.getGroup()));
                    } else {
                        componentsToRemove.add(component); // This is an invalid component (something like import *)
                        Debug.log(Debug.LOG_TYPE.DEBUG, "Component has no group, removing from SBOM.");
                    }
                }
            }

            components.removeAll(componentsToRemove);

            final long t2 = System.currentTimeMillis();
            log(Debug.LOG_TYPE.DEBUG, String.format("Component parsing done in %s ms.", t2 - t1));
        }
    }
}
