/** Copyright 2021 Rochester Institute of Technology (RIT). Developed with
* government support under contract 70RCSA22C00000008 awarded by the United
* States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the “Software”), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
 */

package org.svip.generation.parsers.packagemanagers;

import org.svip.sbom.builder.objects.SVIPComponentBuilder;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.generation.parsers.Parser;
import org.svip.generation.parsers.ParserTestCore;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>File</b>: ParseDepFileTestCore.java<br>
 * <b>Description</b>: Abstract test core for testing the parsing of generated
 * dependency files.
 *
 * @author Dylan Mulligan
 */
public abstract class ParseDepFileTestCore extends ParserTestCore<PackageManagerParser> {
    // Stores the ParserComponents to be tested
    protected final List<SVIPComponentBuilder> components;
    /**
     * Constructor calls super with parser and src, and parses the given
     * fileContents to test against.
     *
     * @param parser Parser object to be tested
     * @param fileContents fileContents to test against
     * @param src Relative path to dummy directory
     */
    protected ParseDepFileTestCore(PackageManagerParser parser, String fileContents, String src) {
        // Call super
        super(parser, TEST_DATA_PATH + src);

        // Init components array
        components = new ArrayList<>();

        // Parse fileContents into components array
        this.PARSER.parse(components, fileContents);
    }

    /**
     * Method to find an individual component from {@link this.components }, null is returned if not found
     * @param name Name used to find the component
     * @return The found component object or null
     */
    protected SVIPComponentObject getComponent(String name) {
        for(SVIPComponentBuilder i : this.components) {
            String cname = Parser.getName(i);
            if((cname != null) && cname.equals(name) ) {
                return i.build();
            }
        }
        return null;
    }

}
