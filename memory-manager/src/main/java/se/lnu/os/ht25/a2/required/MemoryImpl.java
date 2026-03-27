package se.lnu.os.ht25.a2.required;

import se.lnu.os.ht25.a2.provided.data.AllocatedMemoryChunk;
import se.lnu.os.ht25.a2.provided.data.FreeMemoryChunk;
import se.lnu.os.ht25.a2.provided.data.Process;
import se.lnu.os.ht25.a2.provided.data.StrategyType;
import se.lnu.os.ht25.a2.provided.abstract_.Memory;
import se.lnu.os.ht25.a2.provided.abstract_.MemoryChunk;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

public class MemoryImpl extends Memory {
  private final List<MemoryChunk> memory = new ArrayList<>();

  public MemoryImpl(int size) {
    super(size);
    FreeMemoryChunk emptyMemory = new FreeMemoryChunk(0, size - 1);
    memory.add(emptyMemory);
  }

  @Override
  public boolean containsProcess(int processId) {
    for (MemoryChunk chunk : memory) {
      if (chunk instanceof AllocatedMemoryChunk) {
        AllocatedMemoryChunk allocatedChunk = (AllocatedMemoryChunk) chunk;
        if (allocatedChunk.getProcess().getProcessId() == processId) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public int processSize(int processId) {
    for (MemoryChunk chunk : memory) {
      if (chunk instanceof AllocatedMemoryChunk) {
        AllocatedMemoryChunk allocatedChunk = (AllocatedMemoryChunk) chunk;
        if (allocatedChunk.getProcess().getProcessId() == processId) {
          return allocatedChunk.getSize();
        }
      }
    }
    return 0;
  }

  @Override
  public AllocatedMemoryChunk getProcessChunk(int processId) {
    for (MemoryChunk chunk : memory) {
      if (chunk instanceof AllocatedMemoryChunk) {
        AllocatedMemoryChunk allocatedChunk = (AllocatedMemoryChunk) chunk;
        if (allocatedChunk.getProcess().getProcessId() == processId) {
          return allocatedChunk;
        }
      }
    }
    return null;
  }

  @Override
  public List<Integer> contiguousProcesses(int processId) {
    List<Integer> result = new ArrayList<>();
    int index = -1;

    for (int i = 0; i < memory.size(); i++) {
      MemoryChunk chunk = memory.get(i);
      if (chunk instanceof AllocatedMemoryChunk) {
        AllocatedMemoryChunk allocatedChunk = (AllocatedMemoryChunk) chunk;
        if (allocatedChunk.getProcess().getProcessId() == processId) {
          index = i;
          break;
        }
      }
    }

    if (index == -1)
      return result;

    if (index > 0 && memory.get(index - 1) instanceof AllocatedMemoryChunk) {
      result.add(((AllocatedMemoryChunk) memory.get(index - 1)).getProcess().getProcessId());
    }

    if (index < memory.size() - 1 && memory.get(index + 1) instanceof AllocatedMemoryChunk) {
      result.add(((AllocatedMemoryChunk) memory.get(index + 1)).getProcess().getProcessId());
    }

    return result;
  }

  @Override
  public int totalFreeMemory() {
    int total = 0;
    for (MemoryChunk chunk : memory) {
      if (chunk instanceof FreeMemoryChunk) {
        total += chunk.getSize();
      }
    }
    return total;
  }

  @Override
  public int largestHoleSize() {
    int max = 0;
    for (MemoryChunk chunk : memory) {
      if (chunk instanceof FreeMemoryChunk) {
        if (chunk.getSize() > max) {
          max = chunk.getSize();
        }
      }
    }
    return max;
  }

  @Override
  public List<FreeMemoryChunk> holes() {
    List<FreeMemoryChunk> holes = new ArrayList<>();
    for (MemoryChunk chunk : memory) {
      if (chunk instanceof FreeMemoryChunk) {
        holes.add((FreeMemoryChunk) chunk);
      }
    }
    return holes;
  }

  @Override
  public List<AllocatedMemoryChunk> allocatedChunks() {
    List<AllocatedMemoryChunk> allocated = new ArrayList<>();
    for (MemoryChunk chunk : memory) {
      if (chunk instanceof AllocatedMemoryChunk) {
        allocated.add((AllocatedMemoryChunk) chunk);
      }
    }
    return allocated;
  }

  @Override
  public List<Integer> allocatedProcessIDs() {
    List<Integer> ids = new ArrayList<>();
    for (MemoryChunk chunk : memory) {
      if (chunk instanceof AllocatedMemoryChunk) {
        int pid = ((AllocatedMemoryChunk) chunk).getProcess().getProcessId();
        if (!ids.contains(pid)) {
          ids.add(pid);
        }
      }
    }
    return ids;
  }

  public boolean allocateProcess(Process process, StrategyType strategyType) {
    FreeMemoryChunk selectedHole = findSuitableHole(process, strategyType);

    if (selectedHole == null) {
      // If no suitable hole found, try compacting memory
      compact();
      selectedHole = findSuitableHole(process, strategyType);
    }

    if (selectedHole == null)
      return false;

    allocateInHole(selectedHole, process);
    return true;
  }

  private FreeMemoryChunk findSuitableHole(Process process, StrategyType strategyType) {
    List<FreeMemoryChunk> holes = holes();
    FreeMemoryChunk selectedHole = null;

    switch (strategyType) {
      case FIRST_FIT:
        for (FreeMemoryChunk hole : holes) {
          if (hole.getSize() >= process.getSizeInMemory()) {
            selectedHole = hole;
            break;
          }
        }
        break;

      case BEST_FIT:
        int bestSize = Integer.MAX_VALUE;
        int bestAddress = Integer.MAX_VALUE;
        for (FreeMemoryChunk hole : holes) {
          if (hole.getSize() >= process.getSizeInMemory()) {
            if (hole.getSize() < bestSize || 
                (hole.getSize() == bestSize && hole.getLowAddress() < bestAddress)) {
              selectedHole = hole;
              bestSize = hole.getSize();
              bestAddress = hole.getLowAddress();
            }
          }
        }
        break;

      case WORST_FIT:
        int worstSize = -1;
        int worstAddress = Integer.MAX_VALUE;
        for (FreeMemoryChunk hole : holes) {
          if (hole.getSize() >= process.getSizeInMemory()) {
            if (hole.getSize() > worstSize || 
                (hole.getSize() == worstSize && hole.getLowAddress() < worstAddress)) {
              selectedHole = hole;
              worstSize = hole.getSize();
              worstAddress = hole.getLowAddress();
            }
          }
        }
        break;
    }

    return selectedHole;
  }

  private void compact() {
    // Move all allocated processes towards address 0
    List<AllocatedMemoryChunk> allocated = allocatedChunks();
    
    // Sort by current low address to maintain order
    allocated.sort(Comparator.comparingInt(MemoryChunk::getLowAddress));
    
    // Clear memory and rebuild it with processes moved to lowest address
    memory.clear();
    
    int currentAddress = 0;
    for (AllocatedMemoryChunk chunk : allocated) {
      int size = chunk.getSize();
      AllocatedMemoryChunk newChunk = new AllocatedMemoryChunk(currentAddress, currentAddress + size - 1, chunk.getProcess());
      memory.add(newChunk);
      currentAddress += size;
    }
    
    // Add remaining free space at the end
    if (currentAddress < getSize()) {
      memory.add(new FreeMemoryChunk(currentAddress, getSize() - 1));
    }
  }

  public void deallocateProcess(int processId) {
    AllocatedMemoryChunk target = null;
    for (AllocatedMemoryChunk chunk : allocatedChunks()) {
      if (chunk.getProcess().getProcessId() == processId) {
        target = chunk;
        break;
      }
    }

    if (target != null) {
      removeChunk(target);
      addChunk(new FreeMemoryChunk(target.getLowAddress(), target.getHighAddress()));
      mergeHoles(); // optional but recommended
    }
  }

  private void allocateInHole(FreeMemoryChunk hole, Process process) {
    int start = hole.getLowAddress();
    int end = start + process.getSizeInMemory() - 1;

    AllocatedMemoryChunk allocated = new AllocatedMemoryChunk(start, end, process);
    removeChunk(hole);
    addChunk(allocated);

    if (hole.getSize() > process.getSizeInMemory()) {
      addChunk(new FreeMemoryChunk(end + 1, hole.getHighAddress()));
    }

    sortMemory();
  }

  private void removeChunk(MemoryChunk chunk) {
    memory.remove(chunk);
  }

  private void addChunk(MemoryChunk chunk) {
    memory.add(chunk);
  }

  private void sortMemory() {
    memory.sort(Comparator.comparingInt(MemoryChunk::getLowAddress));
  }

  private void mergeHoles() {
    sortMemory();
    List<MemoryChunk> merged = new ArrayList<>();
    MemoryChunk prev = null;

    for (MemoryChunk curr : memory) {
      if (prev == null) {
        prev = curr;
        continue;
      }

      if (prev instanceof FreeMemoryChunk && curr instanceof FreeMemoryChunk) {
        prev = new FreeMemoryChunk(prev.getLowAddress(), curr.getHighAddress());
      } else {
        merged.add(prev);
        prev = curr;
      }
    }

    if (prev != null)
      merged.add(prev);
    memory.clear();
    memory.addAll(merged);
  }

}
