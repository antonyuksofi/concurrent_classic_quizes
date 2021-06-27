package producersconsumers.basic;

public class Producer implements Runnable {

    int id;
    SharedStorage sharedStorage;
    int messagesCount;
    int workDurationMin;
    int workDurationMax;

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

    public void setId(int id) {
        this.id = id;
    }

    public void setSharedStorage(SharedStorage sharedStorage) {
        this.sharedStorage = sharedStorage;
    }

    public void setMessagesCount(int messagesCount) {
        this.messagesCount = messagesCount;
    }

    public void setWorkDurationMin(int workDurationMin) {
        this.workDurationMin = workDurationMin;
    }

    public void setWorkDurationMax(int workDurationMax) {
        this.workDurationMax = workDurationMax;
    }

    public static class ProducerBuilder {
        private final Producer producer;

        ProducerBuilder() {
            producer = new Producer();
        }

        public ProducerBuilder id(int id) {
            producer.setId(id);
            return this;
        }

        public ProducerBuilder sharedStorage(SharedStorage sharedStorage) {
            producer.setSharedStorage(sharedStorage);
            return this;
        }

        public ProducerBuilder messagesCountRange(int messagesCountMin, int messagesCountMax) {
            producer.setMessagesCount(Producer.generateValueFromRange(messagesCountMin, messagesCountMax));
            return this;
        }

        public ProducerBuilder workDurationMin(int workDurationMin) {
            producer.setWorkDurationMin(workDurationMin);
            return this;
        }

        public ProducerBuilder workDurationMax(int workDurationMax) {
            producer.setWorkDurationMax(workDurationMax);
            return this;
        }

        public Producer build() {
            return producer;
        }
    }
}
