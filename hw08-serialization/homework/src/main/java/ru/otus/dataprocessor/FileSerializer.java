package ru.otus.dataprocessor;

import jakarta.json.Json;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Comparator;
import java.util.Map;

public class FileSerializer implements Serializer {
    private final String fileName;

    public FileSerializer(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void serialize(Map<String, Double> data) {
        //формирует результирующий json и сохраняет его в файл
        var resultingJsonBuilder = Json.createObjectBuilder();
        data.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> resultingJsonBuilder.add(entry.getKey(), entry.getValue()));
        try {
            Json.createWriter(new FileOutputStream(fileName)).write(resultingJsonBuilder.build());
        } catch (FileNotFoundException e) {
            throw new FileProcessException(e);
        }
    }
}
