package producersconsumers.basic;

import java.util.ArrayDeque;
import java.util.Deque;

public class SharedStorage {
    private final Integer maxStorageSize;
    private final Deque<String> deque;

    private Boolean finishedFilling;

    SharedStorage(Integer maxStorageSize) {
        this.maxStorageSize = maxStorageSize;
        this.deque = new ArrayDeque<>(maxStorageSize);
        this.finishedFilling = false;
    }

    synchronized String get() {
        return deque.pop();
    }

    synchronized void put(String message) {
        deque.push(message);
        if (deque.size() > maxStorageSize) {
            throw new IllegalStateException("More messages were written than the expected storage size");
        }
    }

    Integer getStorageSize() {
        return deque.size();
    }

    boolean isStorageFull() {
        return deque.size() == maxStorageSize;
    }

    boolean isStorageEmpty() {
        return deque.size() == 0;
    }

    void setFinishedFilling() {
        this.finishedFilling = true;
    }

    Boolean isFinishedFilling() {
        return finishedFilling;
    }
}
