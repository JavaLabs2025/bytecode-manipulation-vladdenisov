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
        System.out.println("ABC stores: total=" + s.abc.totalStores
                + ", avg/class=" + DF.format(s.abc.avgStoresPerClass)
                + ", avg/method=" + DF.format(s.abc.avgStoresPerMethod));
        System.out.println("Overrides: avg/class=" + DF.format(s.overrides.avgOverriddenMethodsPerClass));
        System.out.println("Fields: avg/class=" + DF.format(s.fields.avgFieldsPerClass));
    }
}


