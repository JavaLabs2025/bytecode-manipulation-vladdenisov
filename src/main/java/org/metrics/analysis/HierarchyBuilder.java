package org.metrics.analysis;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.metrics.model.ClassInfo;

public final class HierarchyBuilder {
    private final Map<String, Integer> ditCache = new HashMap<>();

    public void computeAllDit(Map<String, ClassInfo> classesByName, boolean failOnMissingSuper) {
        for (ClassInfo ci : classesByName.values()) {
            if (!ci.isInterface) {
                ci.dit = computeDit(ci.name, classesByName, failOnMissingSuper);
            } else {
                ci.dit = 0;
            }
        }
    }

    public int computeDit(String className, Map<String, ClassInfo> classesByName, boolean failOnMissingSuper) {
        Integer cached = ditCache.get(className);
        if (cached != null) return cached;
        ClassInfo ci = classesByName.get(className);
        if (ci == null) {
            if (failOnMissingSuper) {
                throw new IllegalStateException("Class not found for DIT: " + className);
            }
            ditCache.put(className, 0);
            return 0;
        }
        if (ci.isInterface) {
            ditCache.put(className, 0);
            return 0;
        }
        String parent = ci.superName;
        if (parent == null || "java/lang/Object".equals(parent)) {
            ditCache.put(className, 0);
            return 0;
        }
        if (!classesByName.containsKey(parent)) {
            if (failOnMissingSuper) {
                throw new IllegalStateException("Missing superclass: " + parent + " for " + ci.name);
            }
            ditCache.put(className, 0);
            return 0;
        }
        int depth = 1 + computeDit(parent, classesByName, failOnMissingSuper);
        ditCache.put(className, depth);
        return depth;
    }

    public List<String> ancestorsOf(String className, Map<String, ClassInfo> classesByName, boolean failOnMissingSuper) {
        List<String> result = new ArrayList<>();
        // цепочка суперклассов
        String current = className;
        while (true) {
            ClassInfo ci = classesByName.get(current);
            if (ci == null) {
                if (failOnMissingSuper) {
                    throw new IllegalStateException("Class not found: " + current);
                }
                break;
            }
            String parent = ci.superName;
            if (parent == null || "java/lang/Object".equals(parent)) break;
            result.add(parent);
            current = parent;
        }
        // интерфейсы по цепочке
        Set<String> visited = new HashSet<>();
        Deque<String> queue = new ArrayDeque<>();
        ClassInfo root = classesByName.get(className);
        if (root != null) {
            for (String itf : root.interfaces) {
                if (visited.add(itf)) queue.add(itf);
            }
        }
        // также интерфейсы суперклассов
        current = className;
        while (true) {
            ClassInfo ci = classesByName.get(current);
            if (ci == null) break;
            String parent = ci.superName;
            if (parent == null || "java/lang/Object".equals(parent)) break;
            ClassInfo pci = classesByName.get(parent);
            if (pci != null) {
                for (String itf : pci.interfaces) {
                    if (visited.add(itf)) queue.add(itf);
                }
            } else if (failOnMissingSuper) {
                throw new IllegalStateException("Missing superclass for interface scan: " + parent);
            } else {
                break;
            }
            current = parent;
        }
        // интерфейсы по цепочке (bfs)
        while (!queue.isEmpty()) {
            String itf = queue.removeFirst();
            result.add(itf);
            ClassInfo ici = classesByName.get(itf);
            if (ici != null) {
                for (String parentItf : ici.interfaces) {
                    if (visited.add(parentItf)) queue.addLast(parentItf);
                }
            }
        }
        return result;
    }
}


