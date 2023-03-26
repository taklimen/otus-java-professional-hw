package ru.otus.dataprocessor;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import ru.otus.model.Measurement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ResourcesFileLoader implements Loader {
    private final String fileName;

    public ResourcesFileLoader(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public List<Measurement> load() {
        //читает файл, парсит и возвращает результат
        return readFromFile();
    }

    private List<Measurement> readFromFile() {
        try (var jsonReader = Json.createReader(this.getClass().getClassLoader().getResourceAsStream(this.fileName))) {
            JsonStructure jsonFromTheFile = jsonReader.read();
            List<Measurement> measurements = new ArrayList<>();
            parseMeasurementsArray(jsonFromTheFile, measurements);
            return measurements;
        }
    }

    private void parseMeasurementsArray(JsonValue tree, List<Measurement> measurements) {
        switch (tree.getValueType()) {
            case OBJECT -> {
                var jsonObject = (JsonObject) tree;
                parseMeasurement(measurements, jsonObject);
            }
            case ARRAY -> {
                JsonArray array = (JsonArray) tree;
                for (JsonValue val : array) {
                    parseMeasurementsArray(val, measurements);
                }
            }
            default -> throw new FileProcessException("Unexpected value: " + tree.getValueType());
        }
    }

    private static void parseMeasurement(List<Measurement> measurements, JsonObject jsonObject) {
        String name = null;
        double value = 0;
        for (Map.Entry<String, JsonValue> entry : jsonObject.entrySet()) {
            switch (entry.getKey()) {
                case "name" -> name = ((JsonString) entry.getValue()).getString();
                case "value" -> value = ((JsonNumber) entry.getValue()).doubleValue();
            }
        }
        if (Objects.nonNull(name)) {
            measurements.add(new Measurement(name, value));
        }
    }
}
