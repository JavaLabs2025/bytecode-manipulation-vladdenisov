package org.metrics;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.metrics.analysis.MetricsAggregator;
import org.metrics.model.ClassInfo;
import org.metrics.model.MethodInfo;
import org.metrics.model.Report;
import org.metrics.output.JsonWriter;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonSnapshotTest {
    @Test
    void simpleSnapshot() throws Exception {
        Map<String, ClassInfo> classes = new LinkedHashMap<>();
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

        Path tmp = Files.createTempFile("snapshot", ".json");
        new JsonWriter().write(r, tmp);

        ObjectMapper mapper = new ObjectMapper();
        var actual = mapper.readTree(Files.readString(tmp));
        try (InputStream in = getClass().getResourceAsStream("/snapshots/simple.json")) {
            var expected = mapper.readTree(in);
            assertEquals(expected, actual);
        }
    }
}


