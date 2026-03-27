package se.lnu.os.ht25.a2.provided.abstract_;

import se.lnu.os.ht25.a2.provided.data.MemoryEvent;
import se.lnu.os.ht25.a2.provided.data.Process;
import se.lnu.os.ht25.a2.provided.data.StrategyType;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public abstract class SimulationInstance<Memory> {
    private final Queue<MemoryEvent> simulationEvents;
    private final Memory memory;
    private final StrategyType strategyType;
    private final List<Process> rejectedProcesses;

    public SimulationInstance(Queue<MemoryEvent> events,
                              Memory memory,
                              StrategyType strategyType) {
        this.simulationEvents = events;
        this.memory = memory;
        this.strategyType = strategyType;
        this.rejectedProcesses = new ArrayList<>();
    }

    public abstract void runAll();
    public abstract void run(int eventsNumber);

    public Memory getMemory() {
        return memory;
    }

    public Queue<MemoryEvent> getRemainingEvents() {
        return simulationEvents;
    }

    public StrategyType getStrategyType() {
        return strategyType;
    }

    public List<Process> getRejectedProcesses() {
        return rejectedProcesses;
    }

    public boolean isFinished() {
        return simulationEvents.isEmpty();
    }

    @Override
    public String toString() {
        return "Simulation Details:\n" +
                "Strategy: " + getStrategyType() + "\n" +
                "List of Remaining Events: " + getRemainingEvents() + "\n" +
                "Current Memory Structure:\n\n" + getMemory() + "\n" +
                "List of Rejected Processes: " + getRejectedProcesses();
    }
}
