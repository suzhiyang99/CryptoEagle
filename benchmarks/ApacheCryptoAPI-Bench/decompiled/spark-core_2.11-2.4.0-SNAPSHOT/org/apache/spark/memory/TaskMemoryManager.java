package org.apache.spark.memory;

import java.io.IOException;
import java.nio.channels.ClosedByInterruptException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.concurrent.GuardedBy;
import org.apache.spark.unsafe.memory.MemoryBlock;
import org.apache.spark.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spark_project.guava.annotations.VisibleForTesting;

public class TaskMemoryManager {
   private static final Logger logger = LoggerFactory.getLogger(TaskMemoryManager.class);
   private static final int PAGE_NUMBER_BITS = 13;
   @VisibleForTesting
   static final int OFFSET_BITS = 51;
   private static final int PAGE_TABLE_SIZE = 8192;
   public static final long MAXIMUM_PAGE_SIZE_BYTES = 17179869176L;
   private static final long MASK_LONG_LOWER_51_BITS = 2251799813685247L;
   private final MemoryBlock[] pageTable = new MemoryBlock[8192];
   private final BitSet allocatedPages = new BitSet(8192);
   private final MemoryManager memoryManager;
   private final long taskAttemptId;
   final MemoryMode tungstenMemoryMode;
   @GuardedBy("this")
   private final HashSet<MemoryConsumer> consumers;
   private volatile long acquiredButNotUsed = 0L;

   public TaskMemoryManager(MemoryManager memoryManager, long taskAttemptId) {
      this.tungstenMemoryMode = memoryManager.tungstenMemoryMode();
      this.memoryManager = memoryManager;
      this.taskAttemptId = taskAttemptId;
      this.consumers = new HashSet();
   }

   public long acquireExecutionMemory(long required, MemoryConsumer consumer) {
      assert required >= 0L;

      assert consumer != null;

      MemoryMode mode = consumer.getMode();
      synchronized(this) {
         long got = this.memoryManager.acquireExecutionMemory(required, this.taskAttemptId, mode);
         if (got < required) {
            TreeMap<Long, List<MemoryConsumer>> sortedConsumers = new TreeMap();

            for(MemoryConsumer c : this.consumers) {
               if (c != consumer && c.getUsed() > 0L && c.getMode() == mode) {
                  long key = c.getUsed();
                  List<MemoryConsumer> list = (List)sortedConsumers.computeIfAbsent(key, (k) -> new ArrayList(1));
                  list.add(c);
               }
            }

            while(!sortedConsumers.isEmpty()) {
               Map.Entry<Long, List<MemoryConsumer>> currentEntry = sortedConsumers.ceilingEntry(required - got);
               if (currentEntry == null) {
                  currentEntry = sortedConsumers.lastEntry();
               }

               List<MemoryConsumer> cList = (List)currentEntry.getValue();
               MemoryConsumer c = (MemoryConsumer)cList.remove(cList.size() - 1);
               if (cList.isEmpty()) {
                  sortedConsumers.remove(currentEntry.getKey());
               }

               try {
                  long released = c.spill(required - got, consumer);
                  if (released > 0L) {
                     logger.debug("Task {} released {} from {} for {}", new Object[]{this.taskAttemptId, Utils.bytesToString(released), c, consumer});
                     got += this.memoryManager.acquireExecutionMemory(required - got, this.taskAttemptId, mode);
                     if (got >= required) {
                        break;
                     }
                  }
               } catch (ClosedByInterruptException e) {
                  logger.error("error while calling spill() on " + c, e);
                  throw new RuntimeException(e.getMessage());
               } catch (IOException e) {
                  logger.error("error while calling spill() on " + c, e);
                  throw new SparkOutOfMemoryError("error while calling spill() on " + c + " : " + e.getMessage());
               }
            }
         }

         if (got < required) {
            try {
               long released = consumer.spill(required - got, consumer);
               if (released > 0L) {
                  logger.debug("Task {} released {} from itself ({})", new Object[]{this.taskAttemptId, Utils.bytesToString(released), consumer});
                  got += this.memoryManager.acquireExecutionMemory(required - got, this.taskAttemptId, mode);
               }
            } catch (ClosedByInterruptException e) {
               logger.error("error while calling spill() on " + consumer, e);
               throw new RuntimeException(e.getMessage());
            } catch (IOException e) {
               logger.error("error while calling spill() on " + consumer, e);
               throw new SparkOutOfMemoryError("error while calling spill() on " + consumer + " : " + e.getMessage());
            }
         }

         this.consumers.add(consumer);
         logger.debug("Task {} acquired {} for {}", new Object[]{this.taskAttemptId, Utils.bytesToString(got), consumer});
         return got;
      }
   }

   public void releaseExecutionMemory(long size, MemoryConsumer consumer) {
      logger.debug("Task {} release {} from {}", new Object[]{this.taskAttemptId, Utils.bytesToString(size), consumer});
      this.memoryManager.releaseExecutionMemory(size, this.taskAttemptId, consumer.getMode());
   }

   public void showMemoryUsage() {
      logger.info("Memory used in task " + this.taskAttemptId);
      synchronized(this) {
         long memoryAccountedForByConsumers = 0L;

         for(MemoryConsumer c : this.consumers) {
            long totalMemUsage = c.getUsed();
            memoryAccountedForByConsumers += totalMemUsage;
            if (totalMemUsage > 0L) {
               logger.info("Acquired by " + c + ": " + Utils.bytesToString(totalMemUsage));
            }
         }

         long memoryNotAccountedFor = this.memoryManager.getExecutionMemoryUsageForTask(this.taskAttemptId) - memoryAccountedForByConsumers;
         logger.info("{} bytes of memory were used by task {} but are not associated with specific consumers", memoryNotAccountedFor, this.taskAttemptId);
         logger.info("{} bytes of memory are used for execution and {} bytes of memory are used for storage", this.memoryManager.executionMemoryUsed(), this.memoryManager.storageMemoryUsed());
      }
   }

   public long pageSizeBytes() {
      return this.memoryManager.pageSizeBytes();
   }

   public MemoryBlock allocatePage(long size, MemoryConsumer consumer) {
      assert consumer != null;

      assert consumer.getMode() == this.tungstenMemoryMode;

      if (size > 17179869176L) {
         throw new TooLargePageException(size);
      } else {
         long acquired = this.acquireExecutionMemory(size, consumer);
         if (acquired <= 0L) {
            return null;
         } else {
            int pageNumber;
            synchronized(this) {
               pageNumber = this.allocatedPages.nextClearBit(0);
               if (pageNumber >= 8192) {
                  this.releaseExecutionMemory(acquired, consumer);
                  throw new IllegalStateException("Have already allocated a maximum of 8192 pages");
               }

               this.allocatedPages.set(pageNumber);
            }

            MemoryBlock page = null;

            try {
               page = this.memoryManager.tungstenMemoryAllocator().allocate(acquired);
            } catch (OutOfMemoryError var12) {
               logger.warn("Failed to allocate a page ({} bytes), try again.", acquired);
               synchronized(this) {
                  this.acquiredButNotUsed += acquired;
                  this.allocatedPages.clear(pageNumber);
               }

               return this.allocatePage(size, consumer);
            }

            page.pageNumber = pageNumber;
            this.pageTable[pageNumber] = page;
            if (logger.isTraceEnabled()) {
               logger.trace("Allocate page number {} ({} bytes)", pageNumber, acquired);
            }

            return page;
         }
      }
   }

   public void freePage(MemoryBlock page, MemoryConsumer consumer) {
      assert page.pageNumber != -1 : "Called freePage() on memory that wasn't allocated with allocatePage()";

      assert page.pageNumber != -3 : "Called freePage() on a memory block that has already been freed";

      assert page.pageNumber != -2 : "Called freePage() on a memory block that has already been freed";

      assert this.allocatedPages.get(page.pageNumber);

      this.pageTable[page.pageNumber] = null;
      synchronized(this) {
         this.allocatedPages.clear(page.pageNumber);
      }

      if (logger.isTraceEnabled()) {
         logger.trace("Freed page number {} ({} bytes)", page.pageNumber, page.size());
      }

      long pageSize = page.size();
      page.pageNumber = -2;
      this.memoryManager.tungstenMemoryAllocator().free(page);
      this.releaseExecutionMemory(pageSize, consumer);
   }

   public long encodePageNumberAndOffset(MemoryBlock page, long offsetInPage) {
      if (this.tungstenMemoryMode == MemoryMode.OFF_HEAP) {
         offsetInPage -= page.getBaseOffset();
      }

      return encodePageNumberAndOffset(page.pageNumber, offsetInPage);
   }

   @VisibleForTesting
   public static long encodePageNumberAndOffset(int pageNumber, long offsetInPage) {
      assert pageNumber >= 0 : "encodePageNumberAndOffset called with invalid page";

      return (long)pageNumber << 51 | offsetInPage & 2251799813685247L;
   }

   @VisibleForTesting
   public static int decodePageNumber(long pagePlusOffsetAddress) {
      return (int)(pagePlusOffsetAddress >>> 51);
   }

   private static long decodeOffset(long pagePlusOffsetAddress) {
      return pagePlusOffsetAddress & 2251799813685247L;
   }

   public Object getPage(long pagePlusOffsetAddress) {
      if (this.tungstenMemoryMode != MemoryMode.ON_HEAP) {
         return null;
      } else {
         int pageNumber = decodePageNumber(pagePlusOffsetAddress);

         assert pageNumber >= 0 && pageNumber < 8192;

         MemoryBlock page = this.pageTable[pageNumber];

         assert page != null;

         assert page.getBaseObject() != null;

         return page.getBaseObject();
      }
   }

   public long getOffsetInPage(long pagePlusOffsetAddress) {
      long offsetInPage = decodeOffset(pagePlusOffsetAddress);
      if (this.tungstenMemoryMode == MemoryMode.ON_HEAP) {
         return offsetInPage;
      } else {
         int pageNumber = decodePageNumber(pagePlusOffsetAddress);

         assert pageNumber >= 0 && pageNumber < 8192;

         MemoryBlock page = this.pageTable[pageNumber];

         assert page != null;

         return page.getBaseOffset() + offsetInPage;
      }
   }

   public long cleanUpAllAllocatedMemory() {
      synchronized(this) {
         for(MemoryConsumer c : this.consumers) {
            if (c != null && c.getUsed() > 0L) {
               logger.debug("unreleased " + Utils.bytesToString(c.getUsed()) + " memory from " + c);
            }
         }

         this.consumers.clear();

         for(MemoryBlock page : this.pageTable) {
            if (page != null) {
               logger.debug("unreleased page: " + page + " in task " + this.taskAttemptId);
               page.pageNumber = -2;
               this.memoryManager.tungstenMemoryAllocator().free(page);
            }
         }

         Arrays.fill(this.pageTable, (Object)null);
      }

      this.memoryManager.releaseExecutionMemory(this.acquiredButNotUsed, this.taskAttemptId, this.tungstenMemoryMode);
      return this.memoryManager.releaseAllExecutionMemoryForTask(this.taskAttemptId);
   }

   public long getMemoryConsumptionForThisTask() {
      return this.memoryManager.getExecutionMemoryUsageForTask(this.taskAttemptId);
   }

   public MemoryMode getTungstenMemoryMode() {
      return this.tungstenMemoryMode;
   }
}
