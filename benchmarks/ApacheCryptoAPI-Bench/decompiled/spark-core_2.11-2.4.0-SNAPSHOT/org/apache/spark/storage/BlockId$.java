package org.apache.spark.storage;

import java.util.UUID;
import org.apache.spark.annotation.DeveloperApi;
import scala.Option;
import scala.Predef$;
import scala.collection.LinearSeqOptimized;
import scala.collection.immutable.StringOps;
import scala.util.matching.Regex;

@DeveloperApi
public final class BlockId$ {
   public static final BlockId$ MODULE$;
   private final Regex RDD;
   private final Regex SHUFFLE;
   private final Regex SHUFFLE_DATA;
   private final Regex SHUFFLE_INDEX;
   private final Regex BROADCAST;
   private final Regex TASKRESULT;
   private final Regex STREAM;
   private final Regex TEMP_LOCAL;
   private final Regex TEMP_SHUFFLE;
   private final Regex TEST;

   static {
      new BlockId$();
   }

   public Regex RDD() {
      return this.RDD;
   }

   public Regex SHUFFLE() {
      return this.SHUFFLE;
   }

   public Regex SHUFFLE_DATA() {
      return this.SHUFFLE_DATA;
   }

   public Regex SHUFFLE_INDEX() {
      return this.SHUFFLE_INDEX;
   }

   public Regex BROADCAST() {
      return this.BROADCAST;
   }

   public Regex TASKRESULT() {
      return this.TASKRESULT;
   }

   public Regex STREAM() {
      return this.STREAM;
   }

   public Regex TEMP_LOCAL() {
      return this.TEMP_LOCAL;
   }

   public Regex TEMP_SHUFFLE() {
      return this.TEMP_SHUFFLE;
   }

   public Regex TEST() {
      return this.TEST;
   }

   public BlockId apply(String name) {
      Option var3 = this.RDD().unapplySeq((CharSequence)name);
      Object var6;
      if (!var3.isEmpty() && var3.get() != null && ((LinearSeqOptimized)var3.get()).lengthCompare(2) == 0) {
         String rddId = (String)((LinearSeqOptimized)var3.get()).apply(0);
         String splitIndex = (String)((LinearSeqOptimized)var3.get()).apply(1);
         var6 = new RDDBlockId((new StringOps(Predef$.MODULE$.augmentString(rddId))).toInt(), (new StringOps(Predef$.MODULE$.augmentString(splitIndex))).toInt());
      } else {
         Option var7 = this.SHUFFLE().unapplySeq((CharSequence)name);
         if (!var7.isEmpty() && var7.get() != null && ((LinearSeqOptimized)var7.get()).lengthCompare(3) == 0) {
            String shuffleId = (String)((LinearSeqOptimized)var7.get()).apply(0);
            String mapId = (String)((LinearSeqOptimized)var7.get()).apply(1);
            String reduceId = (String)((LinearSeqOptimized)var7.get()).apply(2);
            var6 = new ShuffleBlockId((new StringOps(Predef$.MODULE$.augmentString(shuffleId))).toInt(), (new StringOps(Predef$.MODULE$.augmentString(mapId))).toInt(), (new StringOps(Predef$.MODULE$.augmentString(reduceId))).toInt());
         } else {
            Option var11 = this.SHUFFLE_DATA().unapplySeq((CharSequence)name);
            if (!var11.isEmpty() && var11.get() != null && ((LinearSeqOptimized)var11.get()).lengthCompare(3) == 0) {
               String shuffleId = (String)((LinearSeqOptimized)var11.get()).apply(0);
               String mapId = (String)((LinearSeqOptimized)var11.get()).apply(1);
               String reduceId = (String)((LinearSeqOptimized)var11.get()).apply(2);
               var6 = new ShuffleDataBlockId((new StringOps(Predef$.MODULE$.augmentString(shuffleId))).toInt(), (new StringOps(Predef$.MODULE$.augmentString(mapId))).toInt(), (new StringOps(Predef$.MODULE$.augmentString(reduceId))).toInt());
            } else {
               Option var15 = this.SHUFFLE_INDEX().unapplySeq((CharSequence)name);
               if (!var15.isEmpty() && var15.get() != null && ((LinearSeqOptimized)var15.get()).lengthCompare(3) == 0) {
                  String shuffleId = (String)((LinearSeqOptimized)var15.get()).apply(0);
                  String mapId = (String)((LinearSeqOptimized)var15.get()).apply(1);
                  String reduceId = (String)((LinearSeqOptimized)var15.get()).apply(2);
                  var6 = new ShuffleIndexBlockId((new StringOps(Predef$.MODULE$.augmentString(shuffleId))).toInt(), (new StringOps(Predef$.MODULE$.augmentString(mapId))).toInt(), (new StringOps(Predef$.MODULE$.augmentString(reduceId))).toInt());
               } else {
                  Option var19 = this.BROADCAST().unapplySeq((CharSequence)name);
                  if (!var19.isEmpty() && var19.get() != null && ((LinearSeqOptimized)var19.get()).lengthCompare(2) == 0) {
                     String broadcastId = (String)((LinearSeqOptimized)var19.get()).apply(0);
                     String field = (String)((LinearSeqOptimized)var19.get()).apply(1);
                     var6 = new BroadcastBlockId((new StringOps(Predef$.MODULE$.augmentString(broadcastId))).toLong(), (new StringOps(Predef$.MODULE$.augmentString(field))).stripPrefix("_"));
                  } else {
                     Option var22 = this.TASKRESULT().unapplySeq((CharSequence)name);
                     if (!var22.isEmpty() && var22.get() != null && ((LinearSeqOptimized)var22.get()).lengthCompare(1) == 0) {
                        String taskId = (String)((LinearSeqOptimized)var22.get()).apply(0);
                        var6 = new TaskResultBlockId((new StringOps(Predef$.MODULE$.augmentString(taskId))).toLong());
                     } else {
                        Option var24 = this.STREAM().unapplySeq((CharSequence)name);
                        if (!var24.isEmpty() && var24.get() != null && ((LinearSeqOptimized)var24.get()).lengthCompare(2) == 0) {
                           String streamId = (String)((LinearSeqOptimized)var24.get()).apply(0);
                           String uniqueId = (String)((LinearSeqOptimized)var24.get()).apply(1);
                           var6 = new StreamBlockId((new StringOps(Predef$.MODULE$.augmentString(streamId))).toInt(), (new StringOps(Predef$.MODULE$.augmentString(uniqueId))).toLong());
                        } else {
                           Option var27 = this.TEMP_LOCAL().unapplySeq((CharSequence)name);
                           if (!var27.isEmpty() && var27.get() != null && ((LinearSeqOptimized)var27.get()).lengthCompare(1) == 0) {
                              String uuid = (String)((LinearSeqOptimized)var27.get()).apply(0);
                              var6 = new TempLocalBlockId(UUID.fromString(uuid));
                           } else {
                              Option var29 = this.TEMP_SHUFFLE().unapplySeq((CharSequence)name);
                              if (!var29.isEmpty() && var29.get() != null && ((LinearSeqOptimized)var29.get()).lengthCompare(1) == 0) {
                                 String uuid = (String)((LinearSeqOptimized)var29.get()).apply(0);
                                 var6 = new TempShuffleBlockId(UUID.fromString(uuid));
                              } else {
                                 Option var31 = this.TEST().unapplySeq((CharSequence)name);
                                 if (var31.isEmpty() || var31.get() == null || ((LinearSeqOptimized)var31.get()).lengthCompare(1) != 0) {
                                    throw new UnrecognizedBlockId(name);
                                 }

                                 String value = (String)((LinearSeqOptimized)var31.get()).apply(0);
                                 var6 = new TestBlockId(value);
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return (BlockId)var6;
   }

   private BlockId$() {
      MODULE$ = this;
      this.RDD = (new StringOps(Predef$.MODULE$.augmentString("rdd_([0-9]+)_([0-9]+)"))).r();
      this.SHUFFLE = (new StringOps(Predef$.MODULE$.augmentString("shuffle_([0-9]+)_([0-9]+)_([0-9]+)"))).r();
      this.SHUFFLE_DATA = (new StringOps(Predef$.MODULE$.augmentString("shuffle_([0-9]+)_([0-9]+)_([0-9]+).data"))).r();
      this.SHUFFLE_INDEX = (new StringOps(Predef$.MODULE$.augmentString("shuffle_([0-9]+)_([0-9]+)_([0-9]+).index"))).r();
      this.BROADCAST = (new StringOps(Predef$.MODULE$.augmentString("broadcast_([0-9]+)([_A-Za-z0-9]*)"))).r();
      this.TASKRESULT = (new StringOps(Predef$.MODULE$.augmentString("taskresult_([0-9]+)"))).r();
      this.STREAM = (new StringOps(Predef$.MODULE$.augmentString("input-([0-9]+)-([0-9]+)"))).r();
      this.TEMP_LOCAL = (new StringOps(Predef$.MODULE$.augmentString("temp_local_([-A-Fa-f0-9]+)"))).r();
      this.TEMP_SHUFFLE = (new StringOps(Predef$.MODULE$.augmentString("temp_shuffle_([-A-Fa-f0-9]+)"))).r();
      this.TEST = (new StringOps(Predef$.MODULE$.augmentString("test_(.*)"))).r();
   }
}
