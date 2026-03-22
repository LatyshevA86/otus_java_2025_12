package ru.otus.nio;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Демонстрация чтения файла через FileChannel и ByteBuffer.
 * Channel -- это двунаправленный канал для операций ввода-вывода.
 * В отличие от потоков (streams), каналы работают только с буферами.
 */
public class ChannelExample {
    private static final Logger logger = LoggerFactory.getLogger(ChannelExample.class);

    public static void main(String[] args) throws IOException, URISyntaxException {
        new ChannelExample().go();
    }

    private void go() throws IOException, URISyntaxException {
        // Читаем файл share.xml
        // Получаем путь к файлу из ресурсов classpath
        var path = Paths.get(ClassLoader.getSystemResource("share.xml").toURI());

        // Открываем файловый канал в режиме чтения (try-with-resources автоматически закроет)
        try (var fileChannel = FileChannel.open(path)) {
            // Создаём маленький буфер (10 байт) для демонстрации чтения порциями
            var buffer = ByteBuffer.allocate(10);

            int bytesCount;
            var sb = new StringBuilder();

            // Читаем файл порциями, пока есть данные
            do {
                // read() читает данные из канала в буфер, возвращает кол-во прочитанных байт
                // -1 при достижении конца файла
                bytesCount = fileChannel.read(buffer);

                // Переключаем буфер в режим чтения
                buffer.flip();

                // Извлекаем все прочитанные байты из буфера
                while (buffer.hasRemaining()) {
                    sb.append((char) buffer.get());
                }

                buffer.flip();
            } while (bytesCount > 0);

            logger.info("result:\n{}", sb);
        }
    }
}
