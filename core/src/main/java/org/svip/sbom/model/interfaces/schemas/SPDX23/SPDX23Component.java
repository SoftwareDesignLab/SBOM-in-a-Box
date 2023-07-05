package org.svip.sbom.model.interfaces.schemas.SPDX23;

import org.svip.sbom.model.interfaces.generics.Component;

/**
 * File: SPDX23Component.java
 * <p>
 * SPDX 2.3 specific fields
 * <p>
 * Source: <a href="https://spdx.github.io/spdx-spec/v2.3/">https://spdx.github.io/spdx-spec/v2.3/</a>
 *
 * @author Derek Garcia
 */
public interface SPDX23Component extends Component {
    /**
     * @return General comments about the component
     */
    String getComment();

    /**
     * @return Acknowledgements that might be required to be communicated in some contexts
     */
    String getAttributionText();
}
