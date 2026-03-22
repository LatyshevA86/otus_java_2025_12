package ru.otus.nio;

import java.nio.CharBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BufferExample {
    private static final Logger logger = LoggerFactory.getLogger(BufferExample.class);

    public static void main(String[] args) {
        new BufferExample().go();
    }

    private void go() {
        // Создаём буфер символов с ёмкостью 10
        // После создания: capacity=10, limit=10, position=0
        var buffer = CharBuffer.allocate(10);
        logger.info("capacity:{} limit:{} position:{}", buffer.capacity(), buffer.limit(), buffer.position());

        // Записываем текст в буфер порциями по 2 символа
        // После каждой записи position увеличивается на 2
        var text = "testText".toCharArray();
        for (var idx = 0; idx < text.length; idx += 2) {
            buffer.put(text, idx, 2);
            logger.info(
                    "idx:{} capacity:{} limit:{}} position:{}",
                    idx,
                    buffer.capacity(),
                    buffer.limit(),
                    buffer.position());
        }

        // flip() переключает буфер из режима записи в режим чтения:
        // - limit устанавливается в текущую position (сколько данных записали, столько можем считать)
        // - position сбрасывается в 0 (начало чтения)
        buffer.flip();

        // Теперь читаем данные из буфера посимвольно
        // При каждом get() position увеличивается на 1
        logger.info("-----");
        for (var idx = 0; idx < buffer.limit(); idx++) {
            logger.info(
                    "idx:{} char:{} capacity:{} limit:{} position:{}",
                    idx,
                    buffer.get(),
                    buffer.capacity(),
                    buffer.limit(),
                    buffer.position());
        }
    }
}
