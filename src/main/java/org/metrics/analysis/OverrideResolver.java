package org.metrics.analysis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.metrics.model.ClassInfo;
import org.metrics.model.MethodInfo;
import org.objectweb.asm.Opcodes;

public final class OverrideResolver {
    private final HierarchyBuilder hierarchyBuilder = new HierarchyBuilder();

    public void resolve(Map<String, ClassInfo> classesByName, boolean failOnMissingSuper) {
        Objects.requireNonNull(classesByName, "classesByName");
        // индекс методов по классам
        Map<String, Map<String, MethodInfo>> methodsByClassAndSig = new HashMap<>();
        for (ClassInfo ci : classesByName.values()) {
            Map<String, MethodInfo> bySig = new HashMap<>();
            for (MethodInfo mi : ci.methods) {
                bySig.put(signatureOf(mi), mi);
            }
            methodsByClassAndSig.put(ci.name, bySig);
        }

        for (ClassInfo ci : classesByName.values()) {
            if (ci.isInterface) continue;
            int overridden = 0;
            List<String> ancestors = hierarchyBuilder.ancestorsOf(ci.name, classesByName, failOnMissingSuper);
            for (MethodInfo mi : ci.methods) {
                if (isSkippable(mi)) continue;
                String sig = signatureOf(mi);
                boolean found = false;
                for (String anc : ancestors) {
                    Map<String, MethodInfo> ancMethods = methodsByClassAndSig.get(anc);
                    if (ancMethods != null && ancMethods.containsKey(sig)) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    mi.overrides = true;
                    overridden++;
                }
            }
            ci.overriddenCount = overridden;
        }
    }

    private static boolean isSkippable(MethodInfo mi) {
        if ("<init>".equals(mi.name) || "<clinit>".equals(mi.name)) return true;
        int acc = mi.access;
        if ((acc & Opcodes.ACC_PRIVATE) != 0) return true;
        if ((acc & Opcodes.ACC_STATIC) != 0) return true;
        return false;
    }

    private static String signatureOf(MethodInfo mi) {
        return mi.name + mi.descriptor;
    }
}


