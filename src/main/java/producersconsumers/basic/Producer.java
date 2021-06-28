package producersconsumers.basic;

public class Producer implements Runnable {

    private int id;
    // though it is used in synchronized and is not final,
    // it is effectively immutable as it is private and there are no setters
    private SharedStorage sharedStorage;
    private int messagesCount;
    private int workDurationMin;
    private int workDurationMax;

    @Override
    public void run() {
        int currentMessageNumber = 0;

        while (currentMessageNumber < messagesCount) {
            synchronized (sharedStorage) {
                while (sharedStorage.isStorageFull()) {
                    try {
                        sharedStorage.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                String message = "producer " + id + ", msg " + currentMessageNumber;

                sharedStorage.put(message);

                try {
                    Thread.sleep(generateValueFromRange(workDurationMin, workDurationMax));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("Producer " + id + " wrote its " + currentMessageNumber + " message " +
                        "of " + messagesCount + " intended; " +
                        "messages in storage now: " + sharedStorage.getStorageSize());

                currentMessageNumber++;

                sharedStorage.notifyAll();
            }
        }
    }

    private static int generateValueFromRange(int min, int max) {
        return (int) (Math.random() * (max - min) + min);
    }

    public static class Builder {
        private final Producer producer;

        Builder() {
            producer = new Producer();
        }

        public Builder id(int id) {
            producer.id = id;
            return this;
        }

        public Builder sharedStorage(SharedStorage sharedStorage) {
            producer.sharedStorage = sharedStorage;
            return this;
        }

        public Builder messagesCountRange(int messagesCountMin, int messagesCountMax) {
            producer.messagesCount = Producer.generateValueFromRange(messagesCountMin, messagesCountMax);
            return this;
        }

        public Builder workDurationMin(int workDurationMin) {
            producer.workDurationMin = workDurationMin;
            return this;
        }

        public Builder workDurationMax(int workDurationMax) {
            producer.workDurationMax = workDurationMax;
            return this;
        }

        public Producer build() {
            return producer;
        }
    }
}
