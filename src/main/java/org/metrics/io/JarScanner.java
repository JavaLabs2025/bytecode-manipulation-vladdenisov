package org.metrics.io;

import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class JarScanner {
    @FunctionalInterface
    public interface ReaderHandler {
        void accept(ClassReader reader) throws IOException;
    }

    public void scan(Path jarPath, ReaderHandler handler) throws IOException {
        Objects.requireNonNull(jarPath, "jarPath");
        Objects.requireNonNull(handler, "handler");
        try (JarFile jarFile = new JarFile(jarPath.toFile())) {
            var entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.isDirectory()) continue;
                if (!entry.getName().endsWith(".class")) continue;
                try (InputStream in = jarFile.getInputStream(entry)) {
                    ClassReader reader = new ClassReader(in);
                    handler.accept(reader);
                }
            }
        }
    }
}

