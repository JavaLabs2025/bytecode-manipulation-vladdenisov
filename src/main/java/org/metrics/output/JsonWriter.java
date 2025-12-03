package org.metrics.output;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.metrics.model.Report;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public final class JsonWriter {
    private final ObjectMapper mapper;

    public JsonWriter() {
        this.mapper = new ObjectMapper();
    }

    public void write(Report report, Path output) throws IOException {
        Report toWrite = report;
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        byte[] json = mapper.writeValueAsBytes(toWrite);
        Files.createDirectories(output.toAbsolutePath().getParent());
        Files.write(output, json);
    }
}


