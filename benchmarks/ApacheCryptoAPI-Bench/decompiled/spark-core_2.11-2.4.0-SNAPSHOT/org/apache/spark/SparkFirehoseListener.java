package org.apache.spark;

import org.apache.spark.scheduler.SparkListenerApplicationEnd;
import org.apache.spark.scheduler.SparkListenerApplicationStart;
import org.apache.spark.scheduler.SparkListenerBlockManagerAdded;
import org.apache.spark.scheduler.SparkListenerBlockManagerRemoved;
import org.apache.spark.scheduler.SparkListenerBlockUpdated;
import org.apache.spark.scheduler.SparkListenerEnvironmentUpdate;
import org.apache.spark.scheduler.SparkListenerEvent;
import org.apache.spark.scheduler.SparkListenerExecutorAdded;
import org.apache.spark.scheduler.SparkListenerExecutorBlacklisted;
import org.apache.spark.scheduler.SparkListenerExecutorBlacklistedForStage;
import org.apache.spark.scheduler.SparkListenerExecutorMetricsUpdate;
import org.apache.spark.scheduler.SparkListenerExecutorRemoved;
import org.apache.spark.scheduler.SparkListenerExecutorUnblacklisted;
import org.apache.spark.scheduler.SparkListenerInterface;
import org.apache.spark.scheduler.SparkListenerJobEnd;
import org.apache.spark.scheduler.SparkListenerJobStart;
import org.apache.spark.scheduler.SparkListenerNodeBlacklisted;
import org.apache.spark.scheduler.SparkListenerNodeBlacklistedForStage;
import org.apache.spark.scheduler.SparkListenerNodeUnblacklisted;
import org.apache.spark.scheduler.SparkListenerSpeculativeTaskSubmitted;
import org.apache.spark.scheduler.SparkListenerStageCompleted;
import org.apache.spark.scheduler.SparkListenerStageSubmitted;
import org.apache.spark.scheduler.SparkListenerTaskEnd;
import org.apache.spark.scheduler.SparkListenerTaskGettingResult;
import org.apache.spark.scheduler.SparkListenerTaskStart;
import org.apache.spark.scheduler.SparkListenerUnpersistRDD;

public class SparkFirehoseListener implements SparkListenerInterface {
   public void onEvent(SparkListenerEvent event) {
   }

   public final void onStageCompleted(SparkListenerStageCompleted stageCompleted) {
      this.onEvent(stageCompleted);
   }

   public final void onStageSubmitted(SparkListenerStageSubmitted stageSubmitted) {
      this.onEvent(stageSubmitted);
   }

   public final void onTaskStart(SparkListenerTaskStart taskStart) {
      this.onEvent(taskStart);
   }

   public final void onTaskGettingResult(SparkListenerTaskGettingResult taskGettingResult) {
      this.onEvent(taskGettingResult);
   }

   public final void onTaskEnd(SparkListenerTaskEnd taskEnd) {
      this.onEvent(taskEnd);
   }

   public final void onJobStart(SparkListenerJobStart jobStart) {
      this.onEvent(jobStart);
   }

   public final void onJobEnd(SparkListenerJobEnd jobEnd) {
      this.onEvent(jobEnd);
   }

   public final void onEnvironmentUpdate(SparkListenerEnvironmentUpdate environmentUpdate) {
      this.onEvent(environmentUpdate);
   }

   public final void onBlockManagerAdded(SparkListenerBlockManagerAdded blockManagerAdded) {
      this.onEvent(blockManagerAdded);
   }

   public final void onBlockManagerRemoved(SparkListenerBlockManagerRemoved blockManagerRemoved) {
      this.onEvent(blockManagerRemoved);
   }

   public final void onUnpersistRDD(SparkListenerUnpersistRDD unpersistRDD) {
      this.onEvent(unpersistRDD);
   }

   public final void onApplicationStart(SparkListenerApplicationStart applicationStart) {
      this.onEvent(applicationStart);
   }

   public final void onApplicationEnd(SparkListenerApplicationEnd applicationEnd) {
      this.onEvent(applicationEnd);
   }

   public final void onExecutorMetricsUpdate(SparkListenerExecutorMetricsUpdate executorMetricsUpdate) {
      this.onEvent(executorMetricsUpdate);
   }

   public final void onExecutorAdded(SparkListenerExecutorAdded executorAdded) {
      this.onEvent(executorAdded);
   }

   public final void onExecutorRemoved(SparkListenerExecutorRemoved executorRemoved) {
      this.onEvent(executorRemoved);
   }

   public final void onExecutorBlacklisted(SparkListenerExecutorBlacklisted executorBlacklisted) {
      this.onEvent(executorBlacklisted);
   }

   public void onExecutorBlacklistedForStage(SparkListenerExecutorBlacklistedForStage executorBlacklistedForStage) {
      this.onEvent(executorBlacklistedForStage);
   }

   public void onNodeBlacklistedForStage(SparkListenerNodeBlacklistedForStage nodeBlacklistedForStage) {
      this.onEvent(nodeBlacklistedForStage);
   }

   public final void onExecutorUnblacklisted(SparkListenerExecutorUnblacklisted executorUnblacklisted) {
      this.onEvent(executorUnblacklisted);
   }

   public final void onNodeBlacklisted(SparkListenerNodeBlacklisted nodeBlacklisted) {
      this.onEvent(nodeBlacklisted);
   }

   public final void onNodeUnblacklisted(SparkListenerNodeUnblacklisted nodeUnblacklisted) {
      this.onEvent(nodeUnblacklisted);
   }

   public void onBlockUpdated(SparkListenerBlockUpdated blockUpdated) {
      this.onEvent(blockUpdated);
   }

   public void onSpeculativeTaskSubmitted(SparkListenerSpeculativeTaskSubmitted speculativeTask) {
      this.onEvent(speculativeTask);
   }

   public void onOtherEvent(SparkListenerEvent event) {
      this.onEvent(event);
   }
}
