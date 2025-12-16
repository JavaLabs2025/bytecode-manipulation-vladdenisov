package org.metrics.cli;

import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.metrics.analysis.HierarchyBuilder;
import org.metrics.analysis.MetricsAggregator;
import org.metrics.analysis.OverrideResolver;
import org.metrics.classpath.ClasspathLoader;
import org.metrics.io.JarScanner;
import org.metrics.model.ClassInfo;
import org.metrics.model.Report;
import org.metrics.output.ConsoleSummary;
import org.metrics.output.JsonWriter;
import org.metrics.visit.CollectClassVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
        name = "metrics-analyzer",
        mixinStandardHelpOptions = true,
        version = "metrics-analyzer 0.1"
)
public final class Main implements Callable<Integer> {

    @Option(names = {"-i", "--input"}, required = true, description = "Input JAR to analyze")
    private Path inputJar;

    @Option(names = {"-o", "--json"}, description = "Path to write JSON report")
    private Path jsonOutput;

    @Option(names = {"--fail-on-missing-super"}, description = "Fail if a superclass/interface cannot be resolved")
    private boolean failOnMissingSuper;

    @Override
    public Integer call() {
        try {
            // сканируем jar и собираем классы
            Map<String, ClassInfo> classesByName = new HashMap<>();
            JarScanner scanner = new JarScanner();
            scanner.scan(inputJar, (ClassReader reader) -> {
                final ClassInfo[] holder = new ClassInfo[1];
                var sink = (java.util.function.Consumer<ClassInfo>) (ci) -> holder[0] = ci;
                reader.accept(new CollectClassVisitor(Opcodes.ASM9, sink), ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                ClassInfo ci = holder[0];
                if (ci != null) {
                    classesByName.put(ci.name, ci);
                }
            });

            // считаем dit и переопределения
            HierarchyBuilder hierarchy = new HierarchyBuilder();
            hierarchy.computeAllDit(classesByName, failOnMissingSuper);
            OverrideResolver overrides = new OverrideResolver();
            overrides.resolve(classesByName, failOnMissingSuper);

            MetricsAggregator aggregator = new MetricsAggregator();
            Report report = aggregator.aggregate(inputJar.getFileName().toString(), classesByName);

            new ConsoleSummary().print(report);
            if (jsonOutput != null) {
                new JsonWriter().write(report, jsonOutput);
            }
            return 0;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return 1;
        }
    }

    public static void main(String[] args) {
        int exit = new CommandLine(new Main()).execute(args);
        System.exit(exit);
    }
}

