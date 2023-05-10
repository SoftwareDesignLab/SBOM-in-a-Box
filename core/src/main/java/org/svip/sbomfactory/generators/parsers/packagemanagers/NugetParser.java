package org.svip.sbomfactory.generators.parsers.packagemanagers;

import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import org.svip.sbomfactory.generators.utils.ParserComponent;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * file: NugetParser.java
 * Description: Package-manager specific implementation of the PackageManagerParser (.nuspec/.json)
 *
 * @author Juan Francisco Patino
 */
public class NugetParser extends PackageManagerParser{
    /**
     * Protected Constructor meant for use by parser implementations
     * to store their package-manager-specific static values in their respective
     * attributes.
     *
     */
    protected NugetParser() {
        super(
                "https://www.nuget.org/api/v2",
                new XmlFactory(),
                "" // TODO: Token regex
        );
    }

    /**
     * Parses a given set of raw data into Components and adds them to the given list
     * of Components. This method is abstract and should be implemented to parse each specific
     * dependency file differently, as needed.
     *
     * @param components list of Components to add to
     * @param data       map of data to be parsed
     */
    @Override
    protected void parseData(ArrayList<ParserComponent> components, HashMap<String, Object> data) {

        /*
                <?xml version="1.0" encoding="utf-8"?>
        <package xmlns="http://schemas.microsoft.com/packaging/2010/07/nuspec.xsd">
            <metadata>
                <!-- Required elements-->
                <id></id>
                <version></version>
                <description></description>
                <authors></authors>

                <!-- Optional elements -->
                <!-- ... -->
            </metadata>
            <!-- Optional 'files' node -->
        </package>
         */

        int x = 0;

    }

    /**
     * Reformats to ParserComponent and adds the component to the list.
     * @param components list to add component to
     * @param type item type (Reference, Compile, etc)
     * @param component component properties table
     */
    private void addComponent(ArrayList<ParserComponent> components, String type, HashMap<String, String> component) {
        //todo. maybe not if this is specific to CSProjParser
    }

    //todo docstring in here and
    private String buildURL(ParserComponent component){
        return ""; //todo. same as addComponent
    }

}
