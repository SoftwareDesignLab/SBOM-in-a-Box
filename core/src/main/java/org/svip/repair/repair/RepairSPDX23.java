package org.svip.repair.repair;

import org.svip.repair.fix.Fix;
import org.svip.sbom.model.interfaces.generics.SBOM;

import java.util.List;
import java.util.Map;

public class RepairSPDX23 implements Repair {

    @Override
    public SBOM repairSBOM(String uid, SBOM sbom, Map<String, Map<String, List<Fix<?>>>> repairs) {
        return null;
    }


}
