package org.svip.sbomfactory;

import org.svip.sbombuilder.interfaces.SBOMBuilder;

public interface SBOMBuilderFactory {
    SBOMBuilder createBuilder();
}
