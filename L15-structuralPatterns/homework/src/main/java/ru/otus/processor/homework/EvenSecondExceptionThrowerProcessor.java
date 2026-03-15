package ru.otus.processor.homework;

import ru.otus.model.Message;
import ru.otus.processor.Processor;

public class EvenSecondExceptionThrowerProcessor implements Processor {

    private final TimeProvider timeProvider;

    public EvenSecondExceptionThrowerProcessor(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    @Override
    public Message process(Message message) {
        if (timeProvider.getTime().getSecond() % 2 == 0) {
            throw new RuntimeException("Even second exception");
        }
        return message;
    }
}
