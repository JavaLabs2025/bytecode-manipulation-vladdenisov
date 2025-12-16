package org.metrics.model;

public final class Summary {
    public int classesAnalyzed;
    public int interfacesAnalyzed;
    public InheritanceDepth inheritanceDepth = new InheritanceDepth();
    public AbcSummary abc = new AbcSummary();
    public OverridesSummary overrides = new OverridesSummary();
    public FieldsSummary fields = new FieldsSummary();

    public static final class InheritanceDepth {
        public int max;
        public double avg;
    }

    public static final class AbcSummary {
        public int totalStores;
        public int totalBranches;
        public int totalConditions;
        public double avgStoresPerClass;
        public double avgStoresPerMethod;
        public double avgBranchesPerClass;
        public double avgBranchesPerMethod;
        public double avgConditionsPerClass;
        public double avgConditionsPerMethod;
    }

    public static final class OverridesSummary {
        public double avgOverriddenMethodsPerClass;
    }

    public static final class FieldsSummary {
        public double avgFieldsPerClass;
    }
}


