package org.apache.spark.ui.jobs;

import org.apache.spark.status.api.v1.JobData;
import scala.reflect.ScalaSignature;
import scala.xml.NodeSeq;

@ScalaSignature(
   bytes = "\u0006\u0001\u00114Q!\u0001\u0002\u0001\t1\u0011qBS8c)\u0006\u0014G.\u001a*po\u0012\u000bG/\u0019\u0006\u0003\u0007\u0011\tAA[8cg*\u0011QAB\u0001\u0003k&T!a\u0002\u0005\u0002\u000bM\u0004\u0018M]6\u000b\u0005%Q\u0011AB1qC\u000eDWMC\u0001\f\u0003\ry'oZ\n\u0003\u00015\u0001\"AD\t\u000e\u0003=Q\u0011\u0001E\u0001\u0006g\u000e\fG.Y\u0005\u0003%=\u0011a!\u00118z%\u00164\u0007\u0002\u0003\u000b\u0001\u0005\u000b\u0007I\u0011\u0001\f\u0002\u000f)|'\rR1uC\u000e\u0001Q#A\f\u0011\u0005ayR\"A\r\u000b\u0005iY\u0012A\u0001<2\u0015\taR$A\u0002ba&T!A\b\u0004\u0002\rM$\u0018\r^;t\u0013\t\u0001\u0013DA\u0004K_\n$\u0015\r^1\t\u0011\t\u0002!\u0011!Q\u0001\n]\t\u0001B[8c\t\u0006$\u0018\r\t\u0005\tI\u0001\u0011)\u0019!C\u0001K\u0005iA.Y:u'R\fw-\u001a(b[\u0016,\u0012A\n\t\u0003O)r!A\u0004\u0015\n\u0005%z\u0011A\u0002)sK\u0012,g-\u0003\u0002,Y\t11\u000b\u001e:j]\u001eT!!K\b\t\u00119\u0002!\u0011!Q\u0001\n\u0019\na\u0002\\1tiN#\u0018mZ3OC6,\u0007\u0005\u0003\u00051\u0001\t\u0015\r\u0011\"\u0001&\u0003Qa\u0017m\u001d;Ti\u0006<W\rR3tGJL\u0007\u000f^5p]\"A!\u0007\u0001B\u0001B\u0003%a%A\u000bmCN$8\u000b^1hK\u0012+7o\u0019:jaRLwN\u001c\u0011\t\u0011Q\u0002!Q1A\u0005\u0002U\n\u0001\u0002Z;sCRLwN\\\u000b\u0002mA\u0011abN\u0005\u0003q=\u0011A\u0001T8oO\"A!\b\u0001B\u0001B\u0003%a'A\u0005ekJ\fG/[8oA!AA\b\u0001BC\u0002\u0013\u0005Q%A\tg_Jl\u0017\r\u001e;fI\u0012+(/\u0019;j_:D\u0001B\u0010\u0001\u0003\u0002\u0003\u0006IAJ\u0001\u0013M>\u0014X.\u0019;uK\u0012$UO]1uS>t\u0007\u0005\u0003\u0005A\u0001\t\u0015\r\u0011\"\u00016\u00039\u0019XOY7jgNLwN\u001c+j[\u0016D\u0001B\u0011\u0001\u0003\u0002\u0003\u0006IAN\u0001\u0010gV\u0014W.[:tS>tG+[7fA!AA\t\u0001BC\u0002\u0013\u0005Q%A\fg_Jl\u0017\r\u001e;fIN+(-\\5tg&|g\u000eV5nK\"Aa\t\u0001B\u0001B\u0003%a%\u0001\rg_Jl\u0017\r\u001e;fIN+(-\\5tg&|g\u000eV5nK\u0002B\u0001\u0002\u0013\u0001\u0003\u0006\u0004%\t!S\u0001\u000fU>\u0014G)Z:de&\u0004H/[8o+\u0005Q\u0005CA&O\u001b\u0005a%BA'\u0010\u0003\rAX\u000e\\\u0005\u0003\u001f2\u0013qAT8eKN+\u0017\u000f\u0003\u0005R\u0001\t\u0005\t\u0015!\u0003K\u0003=QwN\u0019#fg\u000e\u0014\u0018\u000e\u001d;j_:\u0004\u0003\u0002C*\u0001\u0005\u000b\u0007I\u0011A\u0013\u0002\u0013\u0011,G/Y5m+Jd\u0007\u0002C+\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u0014\u0002\u0015\u0011,G/Y5m+Jd\u0007\u0005C\u0003X\u0001\u0011\u0005\u0001,\u0001\u0004=S:LGO\u0010\u000b\u000b3ncVLX0aC\n\u001c\u0007C\u0001.\u0001\u001b\u0005\u0011\u0001\"\u0002\u000bW\u0001\u00049\u0002\"\u0002\u0013W\u0001\u00041\u0003\"\u0002\u0019W\u0001\u00041\u0003\"\u0002\u001bW\u0001\u00041\u0004\"\u0002\u001fW\u0001\u00041\u0003\"\u0002!W\u0001\u00041\u0004\"\u0002#W\u0001\u00041\u0003\"\u0002%W\u0001\u0004Q\u0005\"B*W\u0001\u00041\u0003"
)
public class JobTableRowData {
   private final JobData jobData;
   private final String lastStageName;
   private final String lastStageDescription;
   private final long duration;
   private final String formattedDuration;
   private final long submissionTime;
   private final String formattedSubmissionTime;
   private final NodeSeq jobDescription;
   private final String detailUrl;

   public JobData jobData() {
      return this.jobData;
   }

   public String lastStageName() {
      return this.lastStageName;
   }

   public String lastStageDescription() {
      return this.lastStageDescription;
   }

   public long duration() {
      return this.duration;
   }

   public String formattedDuration() {
      return this.formattedDuration;
   }

   public long submissionTime() {
      return this.submissionTime;
   }

   public String formattedSubmissionTime() {
      return this.formattedSubmissionTime;
   }

   public NodeSeq jobDescription() {
      return this.jobDescription;
   }

   public String detailUrl() {
      return this.detailUrl;
   }

   public JobTableRowData(JobData jobData, String lastStageName, String lastStageDescription, long duration, String formattedDuration, long submissionTime, String formattedSubmissionTime, NodeSeq jobDescription, String detailUrl) {
      this.jobData = jobData;
      this.lastStageName = lastStageName;
      this.lastStageDescription = lastStageDescription;
      this.duration = duration;
      this.formattedDuration = formattedDuration;
      this.submissionTime = submissionTime;
      this.formattedSubmissionTime = formattedSubmissionTime;
      this.jobDescription = jobDescription;
      this.detailUrl = detailUrl;
   }
}
