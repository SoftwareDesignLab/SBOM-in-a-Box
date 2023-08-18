package org.svip.repair.fix;

import org.svip.metrics.resultfactory.Result;

import java.util.List;

public class EmptyOrNullFixes implements Fixes{
    @Override
    public List<Fix<?>> fix(Result r) {
        return null;
    }
}
