package org.metrics.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ClassInfo {
    public final String name;
    public final String superName;
    public final List<String> interfaces;
    public final boolean isInterface;
    public final int fieldsCount;

    public final List<MethodInfo> methods = new ArrayList<>();

    public int dit;
    public int overriddenCount;
    public int abcStoresTotal;
    public int abcBranchesTotal;
    public int abcConditionsTotal;

    public ClassInfo(String name, String superName, List<String> interfaces, boolean isInterface, int fieldsCount) {
        this.name = Objects.requireNonNull(name, "name");
        this.superName = superName;
        this.interfaces = List.copyOf(interfaces);
        this.isInterface = isInterface;
        this.fieldsCount = fieldsCount;
    }
}


