/ **
* Copyright 2021 Rochester Institute of Technology (RIT). Developed with
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
* /

package org.svip.metrics.resultfactory;

import org.svip.metrics.resultfactory.enumerations.STATUS;
import org.svip.metrics.tests.enumerations.ATTRIBUTE;
import org.svip.repair.fix.Fix;

import java.util.ArrayList;
import java.util.List;

/**
 * File: Result.java
 * Storage for the result of a Test
 *
 * @author Derek Garcia
 * @author Matthew Morrison
 */
public class Result {

    private final List<ATTRIBUTE> attributes;

    private final String test;

    private final String message;

    private final String details;

    private final STATUS status;

    private List<Fix<?>> fixes;

    /**
     * Create a new Result
     *
     * @param attributes list of attributes associated with result
     * @param test       test name
     * @param message    message about the result
     * @param details    details about the result
     * @param status     the status of the result
     */
    public Result(List<ATTRIBUTE> attributes, String test,
                  String message, String details, STATUS status) {
        this.attributes = attributes;
        this.test = test;
        this.message = message;
        this.details = details;
        this.status = status;
        this.fixes = new ArrayList<Fix<?>>();
    }

    /**
     * Adds a fix
     * @param fixes a list of potential fixes
     */
    public void addFixes(List<Fix<?>> fixes) {
        if(fixes != null)
            this.fixes.addAll(fixes);
    }

    /**
     * Getters
     */

    public List<ATTRIBUTE> getAttributes() {
        return this.attributes;
    }

    public String getTest() {
        return this.test;
    }

    public String getMessage() {
        return this.message;
    }

    public String getDetails() {
        return this.details;
    }

    public STATUS getStatus() {
        return this.status;
    }

    public List<Fix<?>> getFixes() { return  this.fixes; }

    @Override
    public int hashCode() {
        return (test + ":" + message + ":" + details + ":" + status.toString() + ":" + getFixes().toString() + ":" + getAttributes().toString()).hashCode();
    }
}
