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

package org.svip.metrics.tests;

import org.svip.metrics.resultfactory.Result;
import org.svip.metrics.tests.enumerations.ATTRIBUTE;

import java.util.List;
import java.util.Set;


/**
 * file: MetricTest.java
 * New template for MetricTests
 *
 * @author Derek Garcia
 * @author Matthew Morrison
 */
public abstract class MetricTest {

    /**
     * The list of attributes used for the Metric Tests
     */
    List<ATTRIBUTE> attributes;

    /**
     * Constructor to create a new MetricTest
     *
     * @param attributes the list of attributes used
     */
    public MetricTest(ATTRIBUTE... attributes) {
        this.attributes = List.of(attributes);
    }


    /**
     * Test the given SBOM
     *
     * @param field the field being tested
     * @param value the value being tested
     * @return Collection of Results
     */
    public abstract Set<Result> test(String field, String value);
}
