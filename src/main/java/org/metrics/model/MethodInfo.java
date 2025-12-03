package org.metrics.model;

import java.util.Objects;

public final class MethodInfo {
    public final String name;
    public final String descriptor;
    public final int access;
    public int abcStores;
    public boolean overrides;

    public MethodInfo(String name, String descriptor, int access) {
        this.name = Objects.requireNonNull(name, "name");
        this.descriptor = Objects.requireNonNull(descriptor, "descriptor");
        this.access = access;
    }
}


