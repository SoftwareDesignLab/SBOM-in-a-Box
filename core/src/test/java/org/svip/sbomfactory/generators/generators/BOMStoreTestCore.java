package org.svip.sbomfactory.generators.generators;

import org.junit.jupiter.api.Test;
import org.svip.sbom.model.SBOM;
import org.svip.sbomfactory.generators.generators.utils.GeneratorSchema;
import org.svip.sbomfactory.generators.generators.utils.Tool;
import org.svip.sbomfactory.generators.utils.Debug;
import org.svip.sbomfactory.generators.utils.ParserComponent;
import org.svip.sbomfactory.translators.TranslatorCore;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
        Debug.enableDebug();
        this.bomStore = bomStore;
    }
}
