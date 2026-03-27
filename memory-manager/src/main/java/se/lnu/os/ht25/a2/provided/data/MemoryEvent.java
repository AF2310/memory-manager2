package se.lnu.os.ht25.a2.provided.data;

public class MemoryEvent {
    public EventType getType() {
        return type;
    }

    public Process getRelatedProcess() {
        return relatedProcess;
    }

    public enum EventType {
        ALLOCATE {
            @Override
            public String toString() { return "Allocate"; }
        },
        FREE {
            @Override
            public String toString() { return "Free"; }
        },
    }

    private final EventType type;
    private final Process relatedProcess;

    public MemoryEvent(EventType type, Process relatedProcess) {
        this.type = type;
        this.relatedProcess = relatedProcess;
    }

    @Override
    public String toString() {
        return "MemoryEvent -> " + type + " " + relatedProcess;
    }
}
