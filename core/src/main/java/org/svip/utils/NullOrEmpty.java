/*
 * Copyright 2021 Rochester Institute of Technology (RIT). Developed with
 *  government support under contract 70RCSA22C00000008 awarded by the United
 * States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
 *  <p>
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the “Software”), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *  <p>
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *  <p>
 *  THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package org.svip.utils;

import java.util.Collection;
import java.util.Map;

/**
 * <b>File:</b> NullOrEmpty.java
 * <p>
 * <b>Description:</b> Util check if field is null or empty during serialization
 *
 * @author Derek Garcia
 * @author Ian Dunn
 */

public class NullOrEmpty {
    /**
     * Check if the value is null or empty
     *
     * @param obj Object to check if null or empty
     * @return True if null or empty, false otherwise
     */
    public static boolean isNullOrEmpty(Object obj) {
        // obj is null
        if (obj == null)
            return true;

        // check if empty map
        if (obj instanceof Map<?,?>)
            return ((Map<?,?>) obj).isEmpty();

        // check if empty list
        if (obj instanceof Collection)
            return ((Collection<?>) obj).isEmpty();

        // check if empty string
        if (obj instanceof String)
            return ((String) obj).trim().isEmpty();

        // not null or empty list/string
        return false;
    }
}
