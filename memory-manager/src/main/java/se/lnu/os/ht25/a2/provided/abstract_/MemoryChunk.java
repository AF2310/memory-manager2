package se.lnu.os.ht25.a2.provided.abstract_;

import java.util.Objects;

public abstract class MemoryChunk {
    private final int lowAddress;
    private final int highAddress;

    public MemoryChunk(int lowAddress, int highAddress) {
        if(lowAddress > highAddress){
            throw new RuntimeException("The low address is greater than the high address.");
        }
        this.lowAddress = lowAddress;
        this.highAddress = highAddress;
    }

    public int getHighAddress() {
        return highAddress;
    }

    public int getLowAddress() {
        return lowAddress;
    }

    public int getSize() {
        return highAddress - lowAddress + 1;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MemoryChunk that = (MemoryChunk) o;
        return getLowAddress() == that.getLowAddress() && getHighAddress() == that.getHighAddress();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLowAddress(), getHighAddress());
    }
}
