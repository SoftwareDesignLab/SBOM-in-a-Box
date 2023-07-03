package org.svip.sbomfactory;

import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbombuilder.interfaces.SBOMBuilder;
import org.svip.sbombuilder.interfaces.SVIPSBOMBuilder;
public class SVIPSBOMBuilderFactory implements SBOMBuilderFactory {
    private SVIPComponentBuilder componentBuilder;
    @Override
    public SVIPSBOMBuilder createBuilder() {
        return null;
    }
}
