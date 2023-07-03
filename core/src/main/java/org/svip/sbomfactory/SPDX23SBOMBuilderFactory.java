package org.svip.sbomfactory;

import org.svip.builders.component.interfaces.SPDX23.SPDX23FileBuilder_I;
import org.svip.sbombuilder.interfaces.SBOMBuilder;
import org.svip.sbombuilder.interfaces.SPDX23SBOMBuilder;

public class SPDX23SBOMBuilderFactory implements SBOMBuilderFactory {
    private SPDX23PackageBuilder packageBuilder;
    private SPDX23FileBuilder fileBuilder;
    @Override
    public SPDX23SBOMBuilder createBuilder() {
        return null;
    }
}
