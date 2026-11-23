package org.apache.spark.scheduler;

import scala.runtime.BoxedUnit;

public abstract class SparkListenerBus$class {
   public static void doPostEvent(SparkListenerBus $this, SparkListenerInterface listener, SparkListenerEvent event) {
      if (event instanceof SparkListenerStageSubmitted) {
         SparkListenerStageSubmitted var4 = (SparkListenerStageSubmitted)event;
         listener.onStageSubmitted(var4);
         BoxedUnit var5 = BoxedUnit.UNIT;
      } else if (event instanceof SparkListenerStageCompleted) {
         SparkListenerStageCompleted var6 = (SparkListenerStageCompleted)event;
         listener.onStageCompleted(var6);
         BoxedUnit var29 = BoxedUnit.UNIT;
      } else if (event instanceof SparkListenerJobStart) {
         SparkListenerJobStart var7 = (SparkListenerJobStart)event;
         listener.onJobStart(var7);
         BoxedUnit var30 = BoxedUnit.UNIT;
      } else if (event instanceof SparkListenerJobEnd) {
         SparkListenerJobEnd var8 = (SparkListenerJobEnd)event;
         listener.onJobEnd(var8);
         BoxedUnit var31 = BoxedUnit.UNIT;
      } else if (event instanceof SparkListenerTaskStart) {
         SparkListenerTaskStart var9 = (SparkListenerTaskStart)event;
         listener.onTaskStart(var9);
         BoxedUnit var32 = BoxedUnit.UNIT;
      } else if (event instanceof SparkListenerTaskGettingResult) {
         SparkListenerTaskGettingResult var10 = (SparkListenerTaskGettingResult)event;
         listener.onTaskGettingResult(var10);
         BoxedUnit var33 = BoxedUnit.UNIT;
      } else if (event instanceof SparkListenerTaskEnd) {
         SparkListenerTaskEnd var11 = (SparkListenerTaskEnd)event;
         listener.onTaskEnd(var11);
         BoxedUnit var34 = BoxedUnit.UNIT;
      } else if (event instanceof SparkListenerEnvironmentUpdate) {
         SparkListenerEnvironmentUpdate var12 = (SparkListenerEnvironmentUpdate)event;
         listener.onEnvironmentUpdate(var12);
         BoxedUnit var35 = BoxedUnit.UNIT;
      } else if (event instanceof SparkListenerBlockManagerAdded) {
         SparkListenerBlockManagerAdded var13 = (SparkListenerBlockManagerAdded)event;
         listener.onBlockManagerAdded(var13);
         BoxedUnit var36 = BoxedUnit.UNIT;
      } else if (event instanceof SparkListenerBlockManagerRemoved) {
         SparkListenerBlockManagerRemoved var14 = (SparkListenerBlockManagerRemoved)event;
         listener.onBlockManagerRemoved(var14);
         BoxedUnit var37 = BoxedUnit.UNIT;
      } else if (event instanceof SparkListenerUnpersistRDD) {
         SparkListenerUnpersistRDD var15 = (SparkListenerUnpersistRDD)event;
         listener.onUnpersistRDD(var15);
         BoxedUnit var38 = BoxedUnit.UNIT;
      } else if (event instanceof SparkListenerApplicationStart) {
         SparkListenerApplicationStart var16 = (SparkListenerApplicationStart)event;
         listener.onApplicationStart(var16);
         BoxedUnit var39 = BoxedUnit.UNIT;
      } else if (event instanceof SparkListenerApplicationEnd) {
         SparkListenerApplicationEnd var17 = (SparkListenerApplicationEnd)event;
         listener.onApplicationEnd(var17);
         BoxedUnit var40 = BoxedUnit.UNIT;
      } else if (event instanceof SparkListenerExecutorMetricsUpdate) {
         SparkListenerExecutorMetricsUpdate var18 = (SparkListenerExecutorMetricsUpdate)event;
         listener.onExecutorMetricsUpdate(var18);
         BoxedUnit var41 = BoxedUnit.UNIT;
      } else if (event instanceof SparkListenerExecutorAdded) {
         SparkListenerExecutorAdded var19 = (SparkListenerExecutorAdded)event;
         listener.onExecutorAdded(var19);
         BoxedUnit var42 = BoxedUnit.UNIT;
      } else if (event instanceof SparkListenerExecutorRemoved) {
         SparkListenerExecutorRemoved var20 = (SparkListenerExecutorRemoved)event;
         listener.onExecutorRemoved(var20);
         BoxedUnit var43 = BoxedUnit.UNIT;
      } else if (event instanceof SparkListenerExecutorBlacklistedForStage) {
         SparkListenerExecutorBlacklistedForStage var21 = (SparkListenerExecutorBlacklistedForStage)event;
         listener.onExecutorBlacklistedForStage(var21);
         BoxedUnit var44 = BoxedUnit.UNIT;
      } else if (event instanceof SparkListenerNodeBlacklistedForStage) {
         SparkListenerNodeBlacklistedForStage var22 = (SparkListenerNodeBlacklistedForStage)event;
         listener.onNodeBlacklistedForStage(var22);
         BoxedUnit var45 = BoxedUnit.UNIT;
      } else if (event instanceof SparkListenerExecutorBlacklisted) {
         SparkListenerExecutorBlacklisted var23 = (SparkListenerExecutorBlacklisted)event;
         listener.onExecutorBlacklisted(var23);
         BoxedUnit var46 = BoxedUnit.UNIT;
      } else if (event instanceof SparkListenerExecutorUnblacklisted) {
         SparkListenerExecutorUnblacklisted var24 = (SparkListenerExecutorUnblacklisted)event;
         listener.onExecutorUnblacklisted(var24);
         BoxedUnit var47 = BoxedUnit.UNIT;
      } else if (event instanceof SparkListenerNodeBlacklisted) {
         SparkListenerNodeBlacklisted var25 = (SparkListenerNodeBlacklisted)event;
         listener.onNodeBlacklisted(var25);
         BoxedUnit var48 = BoxedUnit.UNIT;
      } else if (event instanceof SparkListenerNodeUnblacklisted) {
         SparkListenerNodeUnblacklisted var26 = (SparkListenerNodeUnblacklisted)event;
         listener.onNodeUnblacklisted(var26);
         BoxedUnit var49 = BoxedUnit.UNIT;
      } else if (event instanceof SparkListenerBlockUpdated) {
         SparkListenerBlockUpdated var27 = (SparkListenerBlockUpdated)event;
         listener.onBlockUpdated(var27);
         BoxedUnit var50 = BoxedUnit.UNIT;
      } else if (event instanceof SparkListenerSpeculativeTaskSubmitted) {
         SparkListenerSpeculativeTaskSubmitted var28 = (SparkListenerSpeculativeTaskSubmitted)event;
         listener.onSpeculativeTaskSubmitted(var28);
         BoxedUnit var51 = BoxedUnit.UNIT;
      } else {
         listener.onOtherEvent(event);
         BoxedUnit var52 = BoxedUnit.UNIT;
      }

   }

   public static void $init$(SparkListenerBus $this) {
   }
}
