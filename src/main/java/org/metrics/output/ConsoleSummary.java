package org.metrics.output;

import org.metrics.model.Report;
import org.metrics.model.Summary;

import java.text.DecimalFormat;

public final class ConsoleSummary {
    private static final DecimalFormat DF = new DecimalFormat("#.###");

    public void print(Report report) {
        Summary s = report.summary;
        System.out.println("== Metrics Summary ==");
        System.out.println("Project: " + report.project);
        System.out.println("Analyzed: classes=" + s.classesAnalyzed + ", interfaces=" + s.interfacesAnalyzed);
        System.out.println("DIT: max=" + s.inheritanceDepth.max + ", avg=" + DF.format(s.inheritanceDepth.avg));
        System.out.println("ABC: A(stores)=" + s.abc.totalStores
                + ", B(branches)=" + s.abc.totalBranches
                + ", C(conditions)=" + s.abc.totalConditions);
        System.out.println("ABC avg/class: A=" + DF.format(s.abc.avgStoresPerClass)
                + ", B=" + DF.format(s.abc.avgBranchesPerClass)
                + ", C=" + DF.format(s.abc.avgConditionsPerClass));
        System.out.println("ABC avg/method: A=" + DF.format(s.abc.avgStoresPerMethod)
                + ", B=" + DF.format(s.abc.avgBranchesPerMethod)
                + ", C=" + DF.format(s.abc.avgConditionsPerMethod));
        System.out.println("Overrides: avg/class=" + DF.format(s.overrides.avgOverriddenMethodsPerClass));
        System.out.println("Fields: avg/class=" + DF.format(s.fields.avgFieldsPerClass));
    }
}


