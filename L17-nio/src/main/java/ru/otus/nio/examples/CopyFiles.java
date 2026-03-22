package ru.otus.nio.examples;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class CopyFiles {
    public static void main(String[] args) throws IOException {
        copyFiles("L17-nio/textFile.txt", "textFile.bak");
    }

    public static void copyFiles(String srcFile, String destFile) throws IOException {
        try (var channelSrc = FileChannel.open(Path.of(srcFile), StandardOpenOption.READ);
                var channelDest =
                        FileChannel.open(Path.of(destFile), StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {

            // transferTo() -- эффективное копирование через DMA (Direct Memory Access),
            // данные передаются напрямую между файловыми дескрипторами на уровне ОС
            channelSrc.transferTo(0, channelSrc.size(), channelDest);
        }
    }
}
