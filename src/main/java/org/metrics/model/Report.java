package org.metrics.model;

import java.util.ArrayList;
import java.util.List;

public final class Report {
    public String project;
    public Summary summary = new Summary();
    public List<ByClass> byClass = new ArrayList<>();
}


