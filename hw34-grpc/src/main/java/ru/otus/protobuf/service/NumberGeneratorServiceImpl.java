package ru.otus.protobuf.service;

import java.util.List;
import java.util.stream.LongStream;

public class NumberGeneratorServiceImpl implements NumberGeneratorService {

    @Override
    public List<Long> generate(Long start, Long end) {
        return LongStream.rangeClosed(start + 1, end).boxed().toList();
    }
}
