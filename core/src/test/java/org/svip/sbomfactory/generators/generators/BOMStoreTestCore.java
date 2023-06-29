package org.svip.sbomfactory.generators.generators;

import org.svip.sbom.model.old.SBOM;
import org.svip.sbomfactory.generators.utils.ParserComponent;

import java.util.List;

public abstract class BOMStoreTestCore<T extends BOMStore> {
    protected T bomStore;
    protected static SBOM internalSBOM;
    protected static List<ParserComponent> testComponents;

    static {
        internalSBOM = new SBOM();
        testComponents = SBOMGeneratorTest.addTestComponentsToSBOM(internalSBOM);
    }

    // TODO: Docstring
    protected BOMStoreTestCore(T bomStore) {
        this.bomStore = bomStore;
    }
}
