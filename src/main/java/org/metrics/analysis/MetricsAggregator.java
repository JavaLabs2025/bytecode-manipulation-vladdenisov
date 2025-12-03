package org.metrics.analysis;

import org.metrics.model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class MetricsAggregator {
    public Report aggregate(String projectName, Map<String, ClassInfo> classesByName) {
        Report report = new Report();
        report.project = projectName;

        Summary summary = report.summary;
        Collection<ClassInfo> all = classesByName.values();
        int classesCount = 0;
        int interfacesCount = 0;
        int maxDit = 0;
        long ditSum = 0L;
        long totalAbcStores = 0L;
        long totalMethods = 0L;
        long totalOverridden = 0L;
        long totalFields = 0L;

        List<ByClass> byClassList = new ArrayList<>();
        for (ClassInfo ci : all) {
            if (ci.isInterface) {
                interfacesCount++;
            } else {
                classesCount++;
                maxDit = Math.max(maxDit, ci.dit);
                ditSum += ci.dit;
                totalFields += ci.fieldsCount;
            }
            int classMethodStores = 0;
            int declared = ci.methods.size();
            int overridden = ci.overriddenCount;
            for (MethodInfo mi : ci.methods) {
                classMethodStores += mi.abcStores;
            }
            ci.abcStoresTotal = classMethodStores;
            totalAbcStores += classMethodStores;
            totalMethods += declared;
            totalOverridden += overridden;

            ByClass bc = new ByClass();
            bc.name = ci.name;
            bc.isInterface = ci.isInterface;
            bc.superName = ci.superName;
            bc.interfaces = ci.interfaces;
            bc.dit = ci.dit;
            bc.fieldsCount = ci.fieldsCount;
            bc.methods.declared = declared;
            bc.methods.overridden = overridden;
            bc.methods.abcStores = classMethodStores;
            byClassList.add(bc);
        }

        summary.classesAnalyzed = classesCount;
        summary.interfacesAnalyzed = interfacesCount;
        summary.inheritanceDepth.max = maxDit;
        summary.inheritanceDepth.avg = classesCount == 0 ? 0.0 : (double) ditSum / classesCount;
        summary.abc.totalStores = (int) totalAbcStores;
        summary.abc.avgStoresPerClass = classesCount == 0 ? 0.0 : (double) totalAbcStores / classesCount;
        summary.abc.avgStoresPerMethod = totalMethods == 0 ? 0.0 : (double) totalAbcStores / totalMethods;
        summary.overrides.avgOverriddenMethodsPerClass = classesCount == 0 ? 0.0 : (double) totalOverridden / classesCount;
        summary.fields.avgFieldsPerClass = classesCount == 0 ? 0.0 : (double) totalFields / classesCount;
        report.byClass = byClassList;
        return report;
    }
}


