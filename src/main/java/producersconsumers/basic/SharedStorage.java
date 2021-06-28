package producersconsumers.basic;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicBoolean;

public class SharedStorage {
    private final Integer maxStorageSize;
    private final Deque<String> deque;

    private final AtomicBoolean finishedFilling;

    SharedStorage(Integer maxStorageSize) {
        this.maxStorageSize = maxStorageSize;
        this.deque = new ArrayDeque<>(maxStorageSize);
        this.finishedFilling = new AtomicBoolean(false);
    }

    synchronized String get() {
        return deque.pop();
    }

    synchronized void put(String message) {
        if (deque.size() + 1 > maxStorageSize) {
            throw new IllegalStateException("Intended to write more messages than the expected storage size");
        }
        deque.push(message);
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
        this.finishedFilling.set(true);
    }

    Boolean isFinishedFilling() {
        return finishedFilling.get();
    }
}
