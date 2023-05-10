package org.svip.sbomfactory.generators.parsers.packagemanagers;

import com.fasterxml.jackson.core.JsonFactory;
import org.svip.sbomfactory.generators.utils.ParserComponent;

import java.util.ArrayList;
import java.util.HashMap;

public class NugetParser extends PackageManagerParser{
    /**
     * Protected Constructor meant for use by parser implementations
     * to store their package-manager-specific static values in their respective
     * attributes.
     *
     * @param REPO_URL     a URL to the repository of the package manager packages
     */
    protected NugetParser(String REPO_URL, JsonFactory factory, String tokenPattern) {
        super(REPO_URL, factory, tokenPattern);
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

    }
}
