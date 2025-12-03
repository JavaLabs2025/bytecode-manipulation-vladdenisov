package org.metrics.classpath;

import org.metrics.model.ClassInfo;
import org.metrics.visit.CollectClassVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class ClasspathLoader implements AutoCloseable {
    private final URLClassLoader loader;

    public ClasspathLoader(String classpath) throws IOException {
        this.loader = new URLClassLoader(toUrls(classpath), ClassLoader.getSystemClassLoader());
    }

    public ClassInfo load(String internalName) throws IOException {
        Objects.requireNonNull(internalName, "internalName");
        String resource = internalName + ".class";
        try (InputStream in = loader.getResourceAsStream(resource)) {
            if (in == null) {
                return null;
            }
            final ClassInfo[] holder = new ClassInfo[1];
            Consumer<ClassInfo> sink = ci -> holder[0] = ci;
            new ClassReader(in).accept(new CollectClassVisitor(Opcodes.ASM9, sink), ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            return holder[0];
        }
    }

    private static URL[] toUrls(String classpath) throws IOException {
        if (classpath == null || classpath.isBlank()) return new URL[0];
        String[] parts = classpath.split(File.pathSeparator);
        List<URL> urls = new ArrayList<>();
        for (String p : parts) {
            if (p.endsWith("/*")) {
                Path dir = Path.of(p.substring(0, p.length() - 2));
                if (Files.isDirectory(dir)) {
                    try (var stream = Files.list(dir)) {
                        stream.filter(f -> f.toString().endsWith(".jar"))
                                .forEach(j -> {
                                    try {
                                        urls.add(j.toUri().toURL());
                                    } catch (Exception ignored) {
                                    }
                                });
                    }
                }
            } else {
                Path path = Path.of(p);
                if (Files.isDirectory(path) || p.endsWith(".jar")) {
                    urls.add(path.toUri().toURL());
                }
            }
        }
        return urls.toArray(URL[]::new);
    }

    @Override
    public void close() throws IOException {
        loader.close();
    }
}


