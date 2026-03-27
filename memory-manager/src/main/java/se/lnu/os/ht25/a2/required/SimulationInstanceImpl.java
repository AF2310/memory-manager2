package se.lnu.os.ht25.a2.required;

import se.lnu.os.ht25.a2.provided.data.MemoryEvent;
import se.lnu.os.ht25.a2.provided.data.StrategyType;
import se.lnu.os.ht25.a2.provided.data.Process;
import se.lnu.os.ht25.a2.provided.abstract_.SimulationInstance;
import se.lnu.os.ht25.a2.provided.abstract_.Memory;

import java.util.Queue;

public class SimulationInstanceImpl<Memory> extends SimulationInstance<Memory> {

  public SimulationInstanceImpl(Queue<MemoryEvent> simulationEvents,
      Memory memory,
      StrategyType strategyType) {
    super(simulationEvents, memory, strategyType);
  }

  @Override
  public void runAll() {
    while (!getRemainingEvents().isEmpty()) {
      processNextEvent();
    }
  }

  @Override
  public void run(int eventsNumber) {
    for (int i = 0; i < eventsNumber && !getRemainingEvents().isEmpty(); i++) {
      processNextEvent();
    }
  }

  private void processNextEvent() {
    MemoryEvent event = getRemainingEvents().poll();
    if (event == null)
      return;

    Process process = event.getRelatedProcess();
    Memory memory = getMemory();

    switch (event.getType()) {
      case ALLOCATE:
        boolean success = memory instanceof MemoryImpl
            && ((MemoryImpl) memory).allocateProcess(process, getStrategyType());
        if (!success) {
          getRejectedProcesses().add(process);
        }
        break;

      case FREE:
        if (memory instanceof MemoryImpl) {
          ((MemoryImpl) memory).deallocateProcess(process.getProcessId());
        }
        break;
    }
  }
}
