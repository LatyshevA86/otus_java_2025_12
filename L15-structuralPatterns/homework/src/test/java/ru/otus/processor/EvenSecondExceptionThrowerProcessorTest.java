package ru.otus.processor;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import ru.otus.model.Message;
import ru.otus.processor.homework.EvenSecondExceptionThrowerProcessor;
import ru.otus.processor.homework.TimeProvider;

class EvenSecondExceptionThrowerProcessorTest {

    @Test
    void processTest() {
        // given
        TimeProvider timeProvider = () -> LocalDateTime.of(2026, 3, 11, 19, 0, 2); // even second
        var processor = new EvenSecondExceptionThrowerProcessor(timeProvider);
        var message = new Message.Builder(1L).build();

        // when
        assertThrows(RuntimeException.class, () -> processor.process(message));
    }
}
