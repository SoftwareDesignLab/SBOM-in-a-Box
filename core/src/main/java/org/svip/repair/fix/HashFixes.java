package org.svip.repair.fix;

import org.svip.metrics.resultfactory.Result;
import org.svip.sbom.model.interfaces.generics.SBOM;

import java.util.List;

public class HashFixes implements Fixes{
    @Override
    public List<Fix<?>> fix(Result result, SBOM sbom, String repairSubType) {
        return null;
    }
}
