package producersconsumers.basic;

public class Consumer implements Runnable {

    Integer id;
    final SharedStorage sharedStorage;
    Integer msgCount;

    Consumer(Integer id, SharedStorage sharedStorage) {
        this.id = id;
        this.sharedStorage = sharedStorage;
        this.msgCount = 0;
    }

    @Override
    public void run() {
        while (!sharedStorage.isFinishedFilling()) {
            synchronized (sharedStorage) {
                while (sharedStorage.getStorageSize() == 0 && !sharedStorage.isFinishedFilling()) {
                    try {
                        sharedStorage.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (!sharedStorage.isStorageEmpty()) {
                    var readValue = sharedStorage.get();
                    System.out.println("Consumer " + id + " read its " + msgCount + " message: " + readValue + "; " +
                            "messages in storage left: " + sharedStorage.getStorageSize());

                    msgCount++;
                }

                sharedStorage.notifyAll();
            }
        }
    }
}
