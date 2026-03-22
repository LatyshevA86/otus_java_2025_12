package ru.otus.nio.examples;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Реальный пример использования:
 * https://github.com/gridgain/gridgain/blob/d6222c6d892eabcbcfc60fd75fc2d38a7dd06bb6/modules/core/src/main/java/org/apache/ignite/internal/processors/cache/persistence/file/AsyncFileIO.java
 */
public class AsyncRead implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(AsyncRead.class);
    private final ByteBuffer buffer = ByteBuffer.allocate(2);
    private final AsynchronousFileChannel fileChannel;
    private final List<String> fileParts = new CopyOnWriteArrayList<>();
    private final CountDownLatch latch = new CountDownLatch(1);

    /**
     * CompletionHandler -- callback для обработки результата асинхронной операции.
     * Вызывается, когда операция чтения завершена (успешно или с ошибкой).
     */
    private final CompletionHandler<Integer, ByteBuffer> completionHandler = new CompletionHandler<>() {
        // Позиция в файле для следующего чтения
        private int lastPosition = 0;

        @Override
        public void completed(Integer readBytes, ByteBuffer attachment) {
            logger.info("readBytes:{}", readBytes);

            if (readBytes > 0) {
                // Извлекаем прочитанные данные из буфера
                byte[] destArray = new byte[readBytes];
                attachment.flip();
                attachment.get(destArray, 0, destArray.length);

                // Сохраняем фрагмент данных в список
                fileParts.add(new String(destArray));

                // Готовим буфер к следующему чтению
                buffer.clear();
                var position = lastPosition += readBytes;
                // Рекурсивно запускаем следующее асинхронное чтение
                fileChannel.read(buffer, position, buffer, completionHandler);
            } else {
                logger.info("read completed");
                latch.countDown();
            }
        }

        @Override
        public void failed(Throwable exc, ByteBuffer attachment) {
            // Обработка ошибки при асинхронном чтении
            logger.error("error:{}", exc.getMessage());
        }
    };

    public static void main(String[] args) throws Exception {
        var executor = Executors.newSingleThreadExecutor();
        try (var asyncRead = new AsyncRead(executor)) {
            asyncRead.read();
            executor.shutdown();
        }
    }

    public AsyncRead(ExecutorService executor) throws IOException {
        // Открываем асинхронный канал с указанием ExecutorService,
        // который будет использоваться для выполнения операций
        fileChannel = AsynchronousFileChannel.open(
                Path.of("L17-nio/textFile.txt"), Set.of(StandardOpenOption.READ), executor);
    }

    private void read() throws InterruptedException {
        // Читаем блок данных и вызываем метод completed() в completionHandler
        fileChannel.read(buffer, 0, buffer, completionHandler);

        Thread.sleep(2);
        logger.info("Hello");

        // Ждём завершения всех операций чтения
        latch.await();

        var fileContent = String.join("", fileParts);
        logger.info("fileContent:\n{}", fileContent);
    }

    @Override
    public void close() {
        try {
            fileChannel.close();
        } catch (Exception ex) {
            throw new AsyncReadException(ex);
        }
    }
}
