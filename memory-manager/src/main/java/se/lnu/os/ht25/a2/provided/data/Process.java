package se.lnu.os.ht25.a2.provided.data;

import java.util.concurrent.atomic.AtomicInteger;

public class Process {
    private static final AtomicInteger PROCESS_ID_GENERATOR = new AtomicInteger();
    private final int processId;
    private final int sizeInMemory;

    public Process(int sizeInMemory) {
        this.processId = PROCESS_ID_GENERATOR.incrementAndGet();
        this.sizeInMemory = sizeInMemory;
    }

    public int getProcessId() {
        return processId;
    }

    public int getSizeInMemory() {
        return sizeInMemory;
    }

    @Override
    public String toString() {
        return "Process [processId=" + processId + ", size=" + sizeInMemory + "]";
    }
}
