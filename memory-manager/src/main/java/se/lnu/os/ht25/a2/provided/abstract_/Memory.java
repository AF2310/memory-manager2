package se.lnu.os.ht25.a2.provided.abstract_;

import se.lnu.os.ht25.a2.provided.data.AllocatedMemoryChunk;
import se.lnu.os.ht25.a2.provided.data.FreeMemoryChunk;

import java.util.List;

public abstract class Memory {
    private final int size;

    public Memory(int size) {
        this.size = size;
    }

    public abstract boolean containsProcess(int processId);
    public abstract int processSize(int processId);
    public abstract AllocatedMemoryChunk getProcessChunk(int processId);
    public abstract List<Integer> contiguousProcesses(int processId);
    public abstract int totalFreeMemory();
    public abstract int largestHoleSize();
    public abstract List<FreeMemoryChunk> holes();
    public abstract List<AllocatedMemoryChunk> allocatedChunks();
    public abstract List<Integer> allocatedProcessIDs();

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        StringBuilder retStr = new StringBuilder("Memory Size = " + getSize() + "\n");
        if(!allocatedChunks().isEmpty()) {
            for (AllocatedMemoryChunk chunk : allocatedChunks()) {
                retStr.append("(").append(chunk.getLowAddress()).append("-").append(chunk.getHighAddress()).append(")")
                        .append(" --> ").append("ID ").append(chunk.getProcess().getProcessId()).append("\n");
            }
        }
        if(holes() != null) {
            for (FreeMemoryChunk h : holes()) {
                retStr.append("(").append(h.getLowAddress()).append("-").append(h.getHighAddress()).append(")")
                        .append(" --> ").append("EMPTY").append("\n");
            }
        }
        return retStr.toString();
    }
}
