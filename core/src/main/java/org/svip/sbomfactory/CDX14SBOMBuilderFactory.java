package org.svip.sbomfactory;

import org.svip.builders.component.interfaces.CycloneDX14.CDX14PackageBuilder_I;
import org.svip.sbombuilder.interfaces.CDX14SBOMBuilder;
import org.svip.sbombuilder.interfaces.SBOMBuilder;

public class CDX14SBOMBuilderFactory implements SBOMBuilderFactory {
    private CDX14PackageBuilder packageBuilder;
    @Override
    public CDX14SBOMBuilder createBuilder() {
        return null;
    }
}
