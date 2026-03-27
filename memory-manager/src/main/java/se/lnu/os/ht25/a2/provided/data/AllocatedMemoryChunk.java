package se.lnu.os.ht25.a2.provided.data;

import se.lnu.os.ht25.a2.provided.abstract_.MemoryChunk;

import java.util.Objects;

public class AllocatedMemoryChunk extends MemoryChunk {

    public Process getProcess() {
        return process;
    }

    final Process process;

    public AllocatedMemoryChunk(int lowAddress, int highAddress, Process process) {
        super(lowAddress, highAddress);
        if(getSize() != process.getSizeInMemory()){
            throw new RuntimeException("The chunk size doesn't match the process size.");
        }
        this.process = process;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AllocatedMemoryChunk that = (AllocatedMemoryChunk) o;
        return Objects.equals(getProcess(), that.getProcess());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getProcess());
    }
}
