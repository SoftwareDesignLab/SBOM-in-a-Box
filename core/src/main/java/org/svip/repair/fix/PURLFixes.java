package org.svip.repair.fix;

import org.svip.metrics.resultfactory.Result;
import org.svip.sbom.model.uids.PURL;

import java.util.Collections;
import java.util.List;

public class PURLFixes implements Fixes{
    @Override
    public List<Fix<?>> fix(Result result) {

        if(result.getMessage().contains("does not match"))
            return purlMatchFix(result);


        return null;
    }

    public List<Fix<?>> purlMatchFix(Result result){

        //String todo generate pearl from result.getDetails() --- from Expected: _ Actual: _

        try{
            return Collections.singletonList(new Fix<>(new PURL(result.getMessage()), new PURL(result.getMessage())));
        }
        catch(Exception e){
            return null;
        }
    }

}
