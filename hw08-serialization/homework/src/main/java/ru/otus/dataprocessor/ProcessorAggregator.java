package ru.otus.dataprocessor;

import ru.otus.model.Measurement;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProcessorAggregator implements Processor {

    @Override
    public Map<String, Double> process(List<Measurement> data) {
        //группирует выходящий список по name, при этом суммирует поля value
        Map<String, List<Measurement>> groupedData = data.stream()
                .collect(Collectors.groupingBy(Measurement::getName));
        return groupedData.entrySet().stream()
                .map(entry -> {
                    String name = entry.getKey();
                    double sum = entry.getValue().stream()
                            .mapToDouble(Measurement::getValue)
                            .sum();
                    return new AbstractMap.SimpleEntry<>(name, sum);
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
