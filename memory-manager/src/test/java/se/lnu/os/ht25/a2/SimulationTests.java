package se.lnu.os.ht25.a2;

import org.junit.jupiter.api.Test;
import se.lnu.os.ht25.a2.provided.data.FreeMemoryChunk;
import se.lnu.os.ht25.a2.provided.data.MemoryEvent;
import se.lnu.os.ht25.a2.provided.data.Process;
import se.lnu.os.ht25.a2.provided.data.StrategyType;
import se.lnu.os.ht25.a2.required.MemoryImpl;
import se.lnu.os.ht25.a2.required.SimulationInstanceImpl;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static se.lnu.os.ht25.a2.provided.data.MemoryEvent.EventType.*;

class SimulationTests {

    @Test
    void simpleTest() {
        // Processes involved in the simulation
        Process p1 = new Process(5);
        // Simulation initialization with Memory Size = 10 and Best Fit (Memory addresses go from 0 to 9)
        SimulationInstanceImpl<MemoryImpl> sim = new SimulationInstanceImpl<>(
                new ArrayDeque<>(List.of(
                        new MemoryEvent(ALLOCATE, p1),
                        new MemoryEvent(FREE, p1)
                )),
                new MemoryImpl(10),
                StrategyType.BEST_FIT);
        // These checks should already be verified before starting with your implementation
        assertEquals(ALLOCATE, sim.getRemainingEvents().element().getType());
        assertEquals(p1, sim.getRemainingEvents().element().getRelatedProcess());
        assertNotEquals(StrategyType.WORST_FIT, sim.getStrategyType());
        assertFalse(sim.isFinished());
        assertEquals(10, sim.getMemory().getSize());
        // Run the first event of the simulation
        sim.run(1);
        // Check that the Process was stored and not rejected, as there was enough free memory for it
        assertEquals(1, sim.getRemainingEvents().size());
        assertTrue(sim.getRejectedProcesses().isEmpty());
        // Check that the Process was correctly allocated in memory between addresses 0 and 4
        assertTrue(sim.getMemory().containsProcess(p1.getProcessId()));
        assertEquals(5, sim.getMemory().processSize(p1.getProcessId()));
        assertEquals(0, sim.getMemory().getProcessChunk(p1.getProcessId()).getLowAddress());
        assertEquals(4, sim.getMemory().getProcessChunk(p1.getProcessId()).getHighAddress());
        assertEquals(5, sim.getMemory().totalFreeMemory());
        // Run the simulation until the end
        sim.runAll();
        // Check that the Process is not allocated in memory anymore
        assertTrue(sim.isFinished());
        assertTrue(sim.getMemory().allocatedProcessIDs().isEmpty());
        assertTrue(sim.getMemory().allocatedChunks().isEmpty());
        assertTrue(sim.getMemory().holes().contains(new FreeMemoryChunk(0, 9)));
        // Every provided and required class was made printable for your convenience
        // Feel free to adapt any .toString() override to your needs
        System.out.println(sim);
    }

    @Test
    void firstFitTest() {
        // Processes involved in the simulation
        Process p1 = new Process(2);
        Process p2 = new Process(3);
        Process p3 = new Process(3);
        Process p4 = new Process(2);
        // Simulation initialization with Memory Size = 10 and First Fit (Memory addresses go from 0 to 9)
        SimulationInstanceImpl<MemoryImpl> simFF = new SimulationInstanceImpl<>(
                new ArrayDeque<>(List.of(
                        new MemoryEvent(ALLOCATE, p1),
                        new MemoryEvent(ALLOCATE, p2),
                        new MemoryEvent(ALLOCATE, p3),
                        new MemoryEvent(FREE, p2),
                        new MemoryEvent(ALLOCATE, p4),
                        new MemoryEvent(FREE, p3)
                )),
                new MemoryImpl(10),
                StrategyType.FIRST_FIT);
        // Run the first three events of the simulation
        simFF.run(3);
        // All the first three processes should be in memory, with process p2 bound to be removed at the execution
        // of the next event. Regardless of the strategy, they are allocated sequentially from address 0.
        assertEquals(3, simFF.getMemory().allocatedProcessIDs().size());
        assertTrue(simFF.getMemory().allocatedProcessIDs().containsAll(
                List.of(p2.getProcessId(), p3.getProcessId(), p1.getProcessId())
        ));
        assertTrue(simFF.getMemory().holes().contains(new FreeMemoryChunk(8, 9)));
        assertTrue(simFF.getMemory().contiguousProcesses(p2.getProcessId()).containsAll(
                List.of(p3.getProcessId(), p1.getProcessId())
        ));
        // Run the next two events of the simulation
        simFF.run(2);
        // Since the strategy is First Fit, process p4 is allocated starting from address 2
        // The memory should now look like this [1 1 4 4 / 3 3 3 / /]
        assertTrue(simFF.getRejectedProcesses().isEmpty());
        assertEquals(2, simFF.getMemory().getProcessChunk(p4.getProcessId()).getLowAddress());
        assertEquals(3, simFF.getMemory().getProcessChunk(p4.getProcessId()).getHighAddress());
        assertTrue(simFF.getMemory().holes().contains(new FreeMemoryChunk(4, 4)));
        assertTrue(simFF.getMemory().contiguousProcesses(p4.getProcessId()).contains(p1.getProcessId()));
        assertEquals(2, simFF.getMemory().largestHoleSize());
        // Run the rest of the simulation
        simFF.run(200);
        // Check that the largest hole was coalesced correctly
        assertEquals(6, simFF.getMemory().largestHoleSize());
    }

    @Test
    void bestFitTest() {
        // Processes involved in the simulation
        Process p1 = new Process(2);
        Process p2 = new Process(3);
        Process p3 = new Process(3);
        Process p4 = new Process(2);
        // Simulation initialization with Memory Size = 10 and Best Fit (Memory addresses go from 0 to 9)
        SimulationInstanceImpl<MemoryImpl> simBF = new SimulationInstanceImpl<>(
                new ArrayDeque<>(List.of(
                        new MemoryEvent(ALLOCATE, p1),
                        new MemoryEvent(ALLOCATE, p2),
                        new MemoryEvent(ALLOCATE, p3),
                        new MemoryEvent(FREE, p2),
                        new MemoryEvent(ALLOCATE, p4),
                        new MemoryEvent(FREE, p3)
                )),
                new MemoryImpl(10),
                StrategyType.BEST_FIT);
        // Run the first three events of the simulation
        simBF.run(3);
        // All the first three processes should be in memory, with process p2 bound to be removed at the execution
        // of the next event. Regardless of the strategy, they are allocated sequentially from address 0.
        assertEquals(3, simBF.getMemory().allocatedProcessIDs().size());
        assertTrue(simBF.getMemory().allocatedProcessIDs().containsAll(
                List.of(p2.getProcessId(), p3.getProcessId(), p1.getProcessId())
        ));
        assertTrue(simBF.getMemory().holes().contains(new FreeMemoryChunk(8, 9)));
        assertTrue(simBF.getMemory().contiguousProcesses(p2.getProcessId()).containsAll(
                List.of(p3.getProcessId(), p1.getProcessId())
        ));
        // Run the next two events of the simulation
        simBF.run(2);
        // Since the strategy is Best Fit, process p4 is allocated starting from address 8
        // The memory should now look like this [1 1 / / / 3 3 3 4 4]
        assertTrue(simBF.getRejectedProcesses().isEmpty());
        assertEquals(8, simBF.getMemory().getProcessChunk(p4.getProcessId()).getLowAddress());
        assertEquals(9, simBF.getMemory().getProcessChunk(p4.getProcessId()).getHighAddress());
        assertTrue(simBF.getMemory().holes().contains(new FreeMemoryChunk(2, 4)));
        assertTrue(simBF.getMemory().contiguousProcesses(p4.getProcessId()).contains(p3.getProcessId()));
        assertEquals(3, simBF.getMemory().largestHoleSize());
        // Run the rest of the simulation
        simBF.runAll();
        // Check that the largest hole was coalesced correctly
        assertEquals(6, simBF.getMemory().largestHoleSize());
    }
}
