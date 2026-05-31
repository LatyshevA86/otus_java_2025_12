package ru.otus.protobuf.service;

import java.util.List;

public interface NumberGeneratorService {

    List<Long> generate(Long start, Long end);
}
