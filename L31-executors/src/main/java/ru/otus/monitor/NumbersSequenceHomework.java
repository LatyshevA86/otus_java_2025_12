package ru.otus.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NumbersSequenceHomework {

    private static final Logger logger = LoggerFactory.getLogger(NumbersSequenceHomework.class);

    private static final int MIN = 1;
    private static final int MAX = 10;

    private int turn = 1;

    public static void main(String[] args) {
        NumbersSequenceHomework obj = new NumbersSequenceHomework();

        Thread t1 = new Thread(() -> obj.run(1), "Поток-1");
        Thread t2 = new Thread(() -> obj.run(2), "Поток-2");

        t1.start();
        t2.start();
    }

    private synchronized void run(int threadNumber) {
        int value = MIN;
        int step = 1;

        while (!Thread.currentThread().isInterrupted()) {
            try {
                while (turn != threadNumber) {
                    this.wait();
                }

                logger.info("Поток {}: {}", threadNumber, value);

                sleep();

                if (value == MAX) {
                    step = -1;
                } else if (value == MIN) {
                    step = 1;
                }
                value += step;

                turn = (threadNumber == 1) ? 2 : 1;
                this.notifyAll();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static void sleep() {
        try {
            Thread.sleep(1_000);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
