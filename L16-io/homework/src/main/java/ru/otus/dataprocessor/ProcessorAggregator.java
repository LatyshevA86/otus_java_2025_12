package ru.otus.dataprocessor;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingDouble;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import ru.otus.model.Measurement;

public class ProcessorAggregator implements Processor {

    @Override
    public Map<String, Double> process(List<Measurement> data) {
        return new TreeMap<>(data.stream().collect(groupingBy(Measurement::name, summingDouble(Measurement::value))));
    }
}
