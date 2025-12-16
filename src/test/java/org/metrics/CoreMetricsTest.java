package org.metrics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.metrics.analysis.HierarchyBuilder;
import org.metrics.analysis.MetricsAggregator;
import org.metrics.analysis.OverrideResolver;
import org.metrics.model.ClassInfo;
import org.metrics.model.MethodInfo;
import org.metrics.model.Report;
import org.metrics.model.Summary;
import org.metrics.visit.CollectMethodVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class CoreMetricsTest {
    @Test
    void testDitComputationSimpleChain() {
        Map<String, ClassInfo> classes = new HashMap<>();
        classes.put("A", new ClassInfo("A", "java/lang/Object", List.of(), false, 0));
        classes.put("B", new ClassInfo("B", "A", List.of(), false, 0));
        classes.put("C", new ClassInfo("C", "B", List.of(), false, 0));

        HierarchyBuilder hb = new HierarchyBuilder();
        hb.computeAllDit(classes, true);

        assertEquals(0, classes.get("A").dit);
        assertEquals(1, classes.get("B").dit);
        assertEquals(2, classes.get("C").dit);
    }

    @Test
    void testAbcCounting() {
        MethodInfo mi = new MethodInfo("m", "()V", 0);
        MethodVisitor stub = new MethodVisitor(Opcodes.ASM9) {};
        CollectMethodVisitor v = new CollectMethodVisitor(Opcodes.ASM9, stub, mi);
        v.visitVarInsn(Opcodes.ISTORE, 1);
        v.visitVarInsn(Opcodes.ASTORE, 2);
        v.visitVarInsn(Opcodes.ISTORE, 0);
        v.visitVarInsn(Opcodes.LSTORE, 3);
        v.visitIincInsn(0, 1);
        var label = new org.objectweb.asm.Label();
        v.visitJumpInsn(Opcodes.GOTO, label);
        v.visitJumpInsn(Opcodes.IFEQ, label);
        v.visitJumpInsn(Opcodes.IFNULL, label);
        assertEquals(4, mi.abcStores);
        assertEquals(1, mi.abcBranches);
        assertEquals(2, mi.abcConditions);
    }

    @Test
    void testOverrideDetectionAcrossClassAndInterface() {
        Map<String, ClassInfo> classes = new HashMap<>();
        ClassInfo iface = new ClassInfo("I", null, List.of(), true, 0);
        MethodInfo iM = new MethodInfo("m", "()V", 0);
        iface.methods.add(iM);
        classes.put("I", iface);

        ClassInfo superC = new ClassInfo("S", "java/lang/Object", List.of("I"), false, 0);
        MethodInfo sM = new MethodInfo("m", "()V", 0);
        superC.methods.add(sM);
        classes.put("S", superC);

        ClassInfo c = new ClassInfo("C", "S", List.of("I"), false, 0);
        MethodInfo cM = new MethodInfo("m", "()V", 0);
        c.methods.add(cM);
        classes.put("C", c);

        HierarchyBuilder hb = new HierarchyBuilder();
        hb.computeAllDit(classes, true);
        OverrideResolver or = new OverrideResolver();
        or.resolve(classes, true);
        assertTrue(cM.overrides);
        assertEquals(1, c.overriddenCount);
    }

    @Test
    void testAggregatorMetrics() {
        Map<String, ClassInfo> classes = new HashMap<>();
        ClassInfo a = new ClassInfo("A", "java/lang/Object", List.of(), false, 2);
        a.dit = 0;
        MethodInfo am1 = new MethodInfo("m1", "()V", 0);
        am1.abcStores = 2;
        a.methods.add(am1);
        classes.put("A", a);

        ClassInfo b = new ClassInfo("B", "A", List.of(), false, 4);
        b.dit = 1;
        MethodInfo bm1 = new MethodInfo("m1", "()V", 0);
        bm1.abcStores = 1;
        b.methods.add(bm1);
        b.overriddenCount = 1;
        classes.put("B", b);

        MetricsAggregator agg = new MetricsAggregator();
        Report r = agg.aggregate("test.jar", classes);
        Summary s = r.summary;
        assertEquals(2, s.classesAnalyzed);
        assertEquals(1, s.inheritanceDepth.max);
        assertEquals(0.5, s.inheritanceDepth.avg);
        assertEquals(3, s.abc.totalStores);
        assertEquals(0, s.abc.totalBranches);
        assertEquals(0, s.abc.totalConditions);
        assertEquals(1.5, s.abc.avgStoresPerClass);
        assertEquals(1.5, s.abc.avgStoresPerMethod);
        assertEquals(0.0, s.abc.avgBranchesPerClass);
        assertEquals(0.0, s.abc.avgBranchesPerMethod);
        assertEquals(0.0, s.abc.avgConditionsPerClass);
        assertEquals(0.0, s.abc.avgConditionsPerMethod);
        assertEquals(0.5, s.overrides.avgOverriddenMethodsPerClass);
        assertEquals(3.0, s.fields.avgFieldsPerClass);
    }
}


