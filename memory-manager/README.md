
# Assignment 2: Contiguous Memory Allocation

In this assignment, you need to simulate contiguous memory allocation using first fit, best fit, and worst fit strategies. Refer to Chapter 9.2.2 and the lecture notes/slides for more information. Please note that Paging is not used in this assignment.

## Problem statement: Simulation of process allocations in memory

In this assignment, you need to implement a **simulation mechanism** that allocates and de-allocates processes on a memory, performing compact operations only when needed. The simulation will try to allocate processes starting from the lowest memory addresses (i.e., from 0) to the highest ones. Each process will have a pre-assigned integer ID. You can use any data structure you see fit to represent and handle your memory as long as your code only imports elements from the `java` library. Remember that each memory address has a unary capacity, so a process with a size of 5 will be positioned, starting from the lowest address, from 0 to 4.

The `Simulation` class contains:

1.  **The remaining `Memory Events` to simulate:** A queue of events (`Queue<MemoryEvent> simulationEvents`).
2.  **The memory:** The implemented memory to use during the simulation (`Memory memory`).
3.  **The allocation strategy:** The applied allocation strategy for the simulation [**Best fit, worst fit or first fit**] (`StrategyType strategyType`).
4.  **The list of rejected process:** A summary of the processes that could not be allocated during the simulation due to insufficient memory space (`List<Process> rejectedProcesses`).

### Assignment tasks

Your goal is to implement the missing methods in the `MemoryImpl` and `SimulationInstanceImpl<Memory>` classes. Each missing method has a `TODO` section that clearly explains what the method is expected to do and/or return.

### Simulation Execution Flow

The simulation instance can run one or more queued events with the `run(int eventsNumber)` method. The simulation can be executed until the end in any moment by invoking the `runAll()`method. The events should be removed from the queue and processed in the same order they appear in the `simulationEvents` field.

Depending on the chosen strategy, a `MemoryEvent` of type `ALLOCATE` tries to store the `Process` referenced as `relatedProcess` in the memory. In case the memory cannot store the process due to an excessive external fragmentation, the memory is allowed to compact the currently stored processes towards the lowest address (0) before allocating. Otherwise, the process should be added to the `rejectedProcesses` list.

A `MemoryEvent` of type `FREE` should instead be executed by removing the `relatedProcess` from memory. Make sure to properly **coalesce** the holes in memory after this operation.

### Additional remarks

Remember that, if you are adopting **Best Fit** or **Worst Fit** and you have more than one eligible
hole for allocating a process, you should choose the one with the **lowest** address among them.
For **First Fit**, always start from the address 0 when searching for a valid hole.


### Implementation and Submission Constraints

You have to write all the necessary code for this assignment inside the `se.lnu.os.ht25.a2.required` package.

*   Any code written outside the `se.lnu.os.ht25.a2.required` package will be ignored when evaluating the assignment.
*   Do not move any of the existing classes/interfaces provided in the `provided` package to the `required` package.


Submit your work by creating a merge request targeting the `lnu/submit` branch by December 30 23:59.
Make sure your merge request includes all required files and that your code runs correctly before submitting.


**Hints:**

* Hint 1: Use the `element()` or `peek()` method of a queue to look at the next event to execute if you don't want to remove it from the queue yet.

* Hint 2: The internal structure of `MemoryImpl` is yours to realize. Make sure to adapt it to your needs, but also consider in your choice how simple or difficult would be to return what the required `TODO` methods need.

* Hint 3: **DO NOT** rearrange or reorder the processes in any way when the memory needs to be compacted, but just move all the existing processes in the memory towards the lowest address in the order they appear

**Other considerations:**

The tests contained under `src/test/java/se/lnu/os/ht25/a2/SimulationTests` clearly delineate how the simulation is expected to work and show how all the relevant values are going to be checked. It is **warmly** recommended to fully read and understand them before beginning with your implementation. They also contain a commented example of the same scenario applied to two different allocation strategies.

You must fill the `names.txt` file with the student IDs of the participants. 