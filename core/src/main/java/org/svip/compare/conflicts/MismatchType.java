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

package org.svip.compare.conflicts;

public enum MismatchType {
    /**
     * Types of conflicts that can occur in SBOM or Component comparison
     *
     * @author Matt London
     * @author Thomas Roman
     */
    // Special case
    MISSING,
    MISSING_COMPONENT,
    /**
     * Component found in both SBOMs, but has different versions
     */
    VERSION_MISMATCH,
    /**
     * Component found in both SBOMs, but has different licenses
     */
    LICENSE_MISMATCH,
    /**
     * When the publisher of the code are not the same
     */
    PUBLISHER_MISMATCH,
    /**
     * When the supplier of the code are not the same
     */
    SUPPLIER_MISMATCH,
    /**
     * Component found in both SBOMs, but has different name
     */
    NAME_MISMATCH,
    /**
     * When the SBOMs have different authors
     */
    AUTHOR_MISMATCH,
    /**
     * When the SBOMs have different timestamps
     */
    TIMESTAMP_MISMATCH,
    /**
     * When the SBOMs have different origin formats
     */
    ORIGIN_FORMAT_MISMATCH,
    /**
     * SBOMs have different schema versions
     */
    SCHEMA_VERSION_MISMATCH,
    /**
     * SBOMs have different versions
     */
    SBOM_VERSION_MISMATCH,
    /**
     * When SBOMs have different serial numbers
     */
    SERIAL_NUMBER_MISMATCH,
    /**
     * Component found in both SBOMs, but has different PURL
     */
    PURL_MISMATCH,
    /**
     * Component found in both SBOMs, but has different CPE
     */
    CPE_MISMATCH,
    /**
     * Component found in both SBOMs, but has different SWID
     */
    SWID_MISMATCH,
    /**
     * Component found in both SBOMs, but has different SPDX ID
     */
    SPDXID_MISMATCH,
    /**
     * Component found in both SBOMs, but has different Hashes
     */
    HASH_MISMATCH,
    /**
     * Component found in both SBOMs, conflict was called, but cannot be determined
     */
    COMPONENT_UNKNOWN_MISMATCH,
    // todo replace with more detailed field
    MISC_MISMATCH
}
