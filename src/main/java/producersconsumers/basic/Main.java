package producersconsumers.basic;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Main {

    private int sharedStorageSize;
    private int producersCount;
    private int producersMessagesCountMin;
    private int producersMessagesCountMax;
    private int producersWorkDurationMin;
    private int producersWorkDurationMax;
    private int consumersCount;

    public static void main(String[] args) throws InterruptedException {
        new Main().runProducersConsumers();

        System.out.println("*** Finished ***");
    }

    private void runProducersConsumers() throws InterruptedException {

        loadProperties();

        var sharedStorage = new SharedStorage(sharedStorageSize);

        List<Thread> producers = new ArrayList<>(producersCount);
        List<Thread> consumers = new ArrayList<>(consumersCount);

        for (int i = 0; i < producersCount; i++) {
            var producer = new Producer.ProducerBuilder()
                    .id(i)
                    .sharedStorage(sharedStorage)
                    .messagesCountRange(producersMessagesCountMin, producersMessagesCountMax)
                    .workDurationMin(producersWorkDurationMin)
                    .workDurationMax(producersWorkDurationMax)
                    .build();

            var producerThread = new Thread(producer);
            producers.add(producerThread);
            producerThread.start();
        }

        Thread.sleep(2000);

        for (int i = 0; i < consumersCount; i++) {
            var consumer = new Thread(new Consumer(i, sharedStorage));
            consumers.add(consumer);
            consumer.start();
        }

        producers.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        sharedStorage.setFinishedFilling();

        consumers.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    void loadProperties() {
        try (InputStream input = Main.class.getClassLoader()
                .getResourceAsStream("producersconsumers.properties")) {

            var prop = new Properties();

            // load a properties file
            prop.load(input);

            // get the property value
            sharedStorageSize = Integer.parseInt(prop.getProperty("shared.storage.size"));
            producersCount = Integer.parseInt(prop.getProperty("producers.count"));

            producersMessagesCountMin = Integer.parseInt(prop.getProperty("producers.messages.count.min"));
            producersMessagesCountMax = Integer.parseInt(prop.getProperty("producers.messages.count.max"));
            producersWorkDurationMin = Integer.parseInt(prop.getProperty("producers.work.duration.min"));
            producersWorkDurationMax = Integer.parseInt(prop.getProperty("producers.work.duration.max"));

            consumersCount = Integer.parseInt(prop.getProperty("consumers.count"));

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
