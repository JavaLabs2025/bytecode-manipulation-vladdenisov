package org.metrics.model;

import java.util.ArrayList;
import java.util.List;

public final class ByClass {
    public String name;
    public boolean isInterface;
    public String superName;
    public List<String> interfaces = new ArrayList<>();
    public int dit;
    public int fieldsCount;
    public MethodsSummary methods = new MethodsSummary();

    public static final class MethodsSummary {
        public int declared;
        public int overridden;
        public int abcStores;
        public int abcBranches;
        public int abcConditions;
    }
}


