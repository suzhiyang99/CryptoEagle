package org.apache.spark.ui.jobs;

import java.util.Date;
import org.apache.spark.status.api.v1.StageData;
import scala.Option;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005mb!B\u0001\u0003\u0001\u0011a!!E*uC\u001e,G+\u00192mKJ{w\u000fR1uC*\u00111\u0001B\u0001\u0005U>\u00147O\u0003\u0002\u0006\r\u0005\u0011Q/\u001b\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sON\u0011\u0001!\u0004\t\u0003\u001dEi\u0011a\u0004\u0006\u0002!\u0005)1oY1mC&\u0011!c\u0004\u0002\u0007\u0003:L(+\u001a4\t\u0011Q\u0001!Q1A\u0005\u0002Y\tQa\u001d;bO\u0016\u001c\u0001!F\u0001\u0018!\tAr$D\u0001\u001a\u0015\tQ2$\u0001\u0002wc)\u0011A$H\u0001\u0004CBL'B\u0001\u0010\u0007\u0003\u0019\u0019H/\u0019;vg&\u0011\u0001%\u0007\u0002\n'R\fw-\u001a#bi\u0006D\u0001B\t\u0001\u0003\u0002\u0003\u0006IaF\u0001\u0007gR\fw-\u001a\u0011\t\u0011\u0011\u0002!Q1A\u0005\u0002\u0015\naa\u001c9uS>tW#\u0001\u0014\u0011\u000799s#\u0003\u0002)\u001f\t1q\n\u001d;j_:D\u0001B\u000b\u0001\u0003\u0002\u0003\u0006IAJ\u0001\b_B$\u0018n\u001c8!\u0011!a\u0003A!b\u0001\n\u0003i\u0013aB:uC\u001e,\u0017\nZ\u000b\u0002]A\u0011abL\u0005\u0003a=\u00111!\u00138u\u0011!\u0011\u0004A!A!\u0002\u0013q\u0013\u0001C:uC\u001e,\u0017\n\u001a\u0011\t\u0011Q\u0002!Q1A\u0005\u00025\n\u0011\"\u0019;uK6\u0004H/\u00133\t\u0011Y\u0002!\u0011!Q\u0001\n9\n!\"\u0019;uK6\u0004H/\u00133!\u0011!A\u0004A!b\u0001\n\u0003I\u0014AD:dQ\u0016$W\u000f\\5oOB{w\u000e\\\u000b\u0002uA\u00111H\u0010\b\u0003\u001dqJ!!P\b\u0002\rA\u0013X\rZ3g\u0013\ty\u0004I\u0001\u0004TiJLgn\u001a\u0006\u0003{=A\u0001B\u0011\u0001\u0003\u0002\u0003\u0006IAO\u0001\u0010g\u000eDW\rZ;mS:<\u0007k\\8mA!AA\t\u0001BC\u0002\u0013\u0005Q)A\teKN\u001c'/\u001b9uS>tw\n\u001d;j_:,\u0012A\u0012\t\u0004\u001d\u001dR\u0004\u0002\u0003%\u0001\u0005\u0003\u0005\u000b\u0011\u0002$\u0002%\u0011,7o\u0019:jaRLwN\\(qi&|g\u000e\t\u0005\t\u0015\u0002\u0011)\u0019!C\u0001\u0017\u0006q1/\u001e2nSN\u001c\u0018n\u001c8US6,W#\u0001'\u0011\u00055\u0013V\"\u0001(\u000b\u0005=\u0003\u0016\u0001B;uS2T\u0011!U\u0001\u0005U\u00064\u0018-\u0003\u0002T\u001d\n!A)\u0019;f\u0011!)\u0006A!A!\u0002\u0013a\u0015aD:vE6L7o]5p]RKW.\u001a\u0011\t\u0011]\u0003!Q1A\u0005\u0002e\nqCZ8s[\u0006$H/\u001a3Tk\nl\u0017n]:j_:$\u0016.\\3\t\u0011e\u0003!\u0011!Q\u0001\ni\n\u0001DZ8s[\u0006$H/\u001a3Tk\nl\u0017n]:j_:$\u0016.\\3!\u0011!Y\u0006A!b\u0001\n\u0003a\u0016\u0001\u00033ve\u0006$\u0018n\u001c8\u0016\u0003u\u0003\"A\u00040\n\u0005}{!\u0001\u0002'p]\u001eD\u0001\"\u0019\u0001\u0003\u0002\u0003\u0006I!X\u0001\nIV\u0014\u0018\r^5p]\u0002B\u0001b\u0019\u0001\u0003\u0006\u0004%\t!O\u0001\u0012M>\u0014X.\u0019;uK\u0012$UO]1uS>t\u0007\u0002C3\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u001e\u0002%\u0019|'/\\1ui\u0016$G)\u001e:bi&|g\u000e\t\u0005\tO\u0002\u0011)\u0019!C\u00019\u0006I\u0011N\u001c9viJ+\u0017\r\u001a\u0005\tS\u0002\u0011\t\u0011)A\u0005;\u0006Q\u0011N\u001c9viJ+\u0017\r\u001a\u0011\t\u0011-\u0004!Q1A\u0005\u0002e\n\u0011#\u001b8qkR\u0014V-\u00193XSRDWK\\5u\u0011!i\u0007A!A!\u0002\u0013Q\u0014AE5oaV$(+Z1e/&$\b.\u00168ji\u0002B\u0001b\u001c\u0001\u0003\u0006\u0004%\t\u0001X\u0001\f_V$\b/\u001e;Xe&$X\r\u0003\u0005r\u0001\t\u0005\t\u0015!\u0003^\u00031yW\u000f\u001e9vi^\u0013\u0018\u000e^3!\u0011!\u0019\bA!b\u0001\n\u0003I\u0014aE8viB,Ho\u0016:ji\u0016<\u0016\u000e\u001e5V]&$\b\u0002C;\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u001e\u0002)=,H\u000f];u/JLG/Z,ji\",f.\u001b;!\u0011!9\bA!b\u0001\n\u0003a\u0016aC:ik\u001a4G.\u001a*fC\u0012D\u0001\"\u001f\u0001\u0003\u0002\u0003\u0006I!X\u0001\rg\",hM\u001a7f%\u0016\fG\r\t\u0005\tw\u0002\u0011)\u0019!C\u0001s\u0005\u00192\u000f[;gM2,'+Z1e/&$\b.\u00168ji\"AQ\u0010\u0001B\u0001B\u0003%!(\u0001\u000btQV4g\r\\3SK\u0006$w+\u001b;i+:LG\u000f\t\u0005\t\u007f\u0002\u0011)\u0019!C\u00019\u0006a1\u000f[;gM2,wK]5uK\"I\u00111\u0001\u0001\u0003\u0002\u0003\u0006I!X\u0001\u000eg\",hM\u001a7f/JLG/\u001a\u0011\t\u0013\u0005\u001d\u0001A!b\u0001\n\u0003I\u0014\u0001F:ik\u001a4G.Z,sSR,w+\u001b;i+:LG\u000fC\u0005\u0002\f\u0001\u0011\t\u0011)A\u0005u\u0005)2\u000f[;gM2,wK]5uK^KG\u000f[+oSR\u0004\u0003bBA\b\u0001\u0011\u0005\u0011\u0011C\u0001\u0007y%t\u0017\u000e\u001e \u0015M\u0005M\u0011qCA\r\u00037\ti\"a\b\u0002\"\u0005\r\u0012QEA\u0014\u0003S\tY#!\f\u00020\u0005E\u00121GA\u001b\u0003o\tI\u0004E\u0002\u0002\u0016\u0001i\u0011A\u0001\u0005\u0007)\u00055\u0001\u0019A\f\t\r\u0011\ni\u00011\u0001'\u0011\u0019a\u0013Q\u0002a\u0001]!1A'!\u0004A\u00029Ba\u0001OA\u0007\u0001\u0004Q\u0004B\u0002#\u0002\u000e\u0001\u0007a\t\u0003\u0004K\u0003\u001b\u0001\r\u0001\u0014\u0005\u0007/\u00065\u0001\u0019\u0001\u001e\t\rm\u000bi\u00011\u0001^\u0011\u0019\u0019\u0017Q\u0002a\u0001u!1q-!\u0004A\u0002uCaa[A\u0007\u0001\u0004Q\u0004BB8\u0002\u000e\u0001\u0007Q\f\u0003\u0004t\u0003\u001b\u0001\rA\u000f\u0005\u0007o\u00065\u0001\u0019A/\t\rm\fi\u00011\u0001;\u0011\u0019y\u0018Q\u0002a\u0001;\"9\u0011qAA\u0007\u0001\u0004Q\u0004"
)
public class StageTableRowData {
   private final StageData stage;
   private final Option<StageData> option;
   private final int stageId;
   private final int attemptId;
   private final String schedulingPool;
   private final Option<String> descriptionOption;
   private final Date submissionTime;
   private final String formattedSubmissionTime;
   private final long duration;
   private final String formattedDuration;
   private final long inputRead;
   private final String inputReadWithUnit;
   private final long outputWrite;
   private final String outputWriteWithUnit;
   private final long shuffleRead;
   private final String shuffleReadWithUnit;
   private final long shuffleWrite;
   private final String shuffleWriteWithUnit;

   public StageData stage() {
      return this.stage;
   }

   public Option<StageData> option() {
      return this.option;
   }

   public int stageId() {
      return this.stageId;
   }

   public int attemptId() {
      return this.attemptId;
   }

   public String schedulingPool() {
      return this.schedulingPool;
   }

   public Option<String> descriptionOption() {
      return this.descriptionOption;
   }

   public Date submissionTime() {
      return this.submissionTime;
   }

   public String formattedSubmissionTime() {
      return this.formattedSubmissionTime;
   }

   public long duration() {
      return this.duration;
   }

   public String formattedDuration() {
      return this.formattedDuration;
   }

   public long inputRead() {
      return this.inputRead;
   }

   public String inputReadWithUnit() {
      return this.inputReadWithUnit;
   }

   public long outputWrite() {
      return this.outputWrite;
   }

   public String outputWriteWithUnit() {
      return this.outputWriteWithUnit;
   }

   public long shuffleRead() {
      return this.shuffleRead;
   }

   public String shuffleReadWithUnit() {
      return this.shuffleReadWithUnit;
   }

   public long shuffleWrite() {
      return this.shuffleWrite;
   }

   public String shuffleWriteWithUnit() {
      return this.shuffleWriteWithUnit;
   }

   public StageTableRowData(StageData stage, Option<StageData> option, int stageId, int attemptId, String schedulingPool, Option<String> descriptionOption, Date submissionTime, String formattedSubmissionTime, long duration, String formattedDuration, long inputRead, String inputReadWithUnit, long outputWrite, String outputWriteWithUnit, long shuffleRead, String shuffleReadWithUnit, long shuffleWrite, String shuffleWriteWithUnit) {
      this.stage = stage;
      this.option = option;
      this.stageId = stageId;
      this.attemptId = attemptId;
      this.schedulingPool = schedulingPool;
      this.descriptionOption = descriptionOption;
      this.submissionTime = submissionTime;
      this.formattedSubmissionTime = formattedSubmissionTime;
      this.duration = duration;
      this.formattedDuration = formattedDuration;
      this.inputRead = inputRead;
      this.inputReadWithUnit = inputReadWithUnit;
      this.outputWrite = outputWrite;
      this.outputWriteWithUnit = outputWriteWithUnit;
      this.shuffleRead = shuffleRead;
      this.shuffleReadWithUnit = shuffleReadWithUnit;
      this.shuffleWrite = shuffleWrite;
      this.shuffleWriteWithUnit = shuffleWriteWithUnit;
   }
}
