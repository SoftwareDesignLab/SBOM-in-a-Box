package org.svip.repair.fix;

import org.svip.metrics.resultfactory.Result;

import java.util.List;

public interface Fixes {
    public List<Fix<?>> fix(Result r);
}
