package se.lnu.os.ht25.a2.provided.data;

import se.lnu.os.ht25.a2.provided.abstract_.MemoryChunk;

public class FreeMemoryChunk extends MemoryChunk {

    public FreeMemoryChunk(int lowAddress, int highAddress) {
        super(lowAddress, highAddress);
    }
}
