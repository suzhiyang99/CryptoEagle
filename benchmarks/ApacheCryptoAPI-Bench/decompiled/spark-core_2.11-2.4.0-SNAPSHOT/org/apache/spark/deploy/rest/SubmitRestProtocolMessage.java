package org.apache.spark.deploy.rest;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.apache.spark.util.Utils$;
import scala.Predef$;
import scala.StringContext;
import scala.reflect.ScalaSignature;

@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(
   getterVisibility = Visibility.ANY,
   setterVisibility = Visibility.ANY
)
@JsonPropertyOrder(
   alphabetic = true
)
@ScalaSignature(
   bytes = "\u0006\u0001\u0005edAB\u0001\u0003\u0003\u0003\u0011ABA\rTk\nl\u0017\u000e\u001e*fgR\u0004&o\u001c;pG>dW*Z:tC\u001e,'BA\u0002\u0005\u0003\u0011\u0011Xm\u001d;\u000b\u0005\u00151\u0011A\u00023fa2|\u0017P\u0003\u0002\b\u0011\u0005)1\u000f]1sW*\u0011\u0011BC\u0001\u0007CB\f7\r[3\u000b\u0003-\t1a\u001c:h'\t\u0001Q\u0002\u0005\u0002\u000f#5\tqBC\u0001\u0011\u0003\u0015\u00198-\u00197b\u0013\t\u0011rB\u0001\u0004B]f\u0014VM\u001a\u0005\u0006)\u0001!\tAF\u0001\u0007y%t\u0017\u000e\u001e \u0004\u0001Q\tq\u0003\u0005\u0002\u0019\u00015\t!\u0001C\u0004\u001b\u0001\t\u0007I\u0011A\u000e\u0002\u00175,7o]1hKRK\b/Z\u000b\u00029A\u0011Q\u0004\t\b\u0003\u001dyI!aH\b\u0002\rA\u0013X\rZ3g\u0013\t\t#E\u0001\u0004TiJLgn\u001a\u0006\u0003?=Aa\u0001\n\u0001!\u0002\u0013a\u0012\u0001D7fgN\fw-\u001a+za\u0016\u0004\u0003FA\u0012'!\t9\u0003'D\u0001)\u0015\tI#&\u0001\u0006b]:|G/\u0019;j_:T!a\u000b\u0017\u0002\u000f)\f7m[:p]*\u0011QFL\u0001\nM\u0006\u001cH/\u001a:y[2T\u0011aL\u0001\u0004G>l\u0017BA\u0019)\u0005)Q5o\u001c8JO:|'/\u001a\u0005\bg\u0001\u0011\r\u0011\"\u0001\u001c\u0003\u0019\t7\r^5p]\"1Q\u0007\u0001Q\u0001\nq\tq!Y2uS>t\u0007\u0005C\u00048\u0001\u0001\u0007I\u0011A\u000e\u0002\u000f5,7o]1hK\"9\u0011\b\u0001a\u0001\n\u0003Q\u0014aC7fgN\fw-Z0%KF$\"a\u000f \u0011\u00059a\u0014BA\u001f\u0010\u0005\u0011)f.\u001b;\t\u000f}B\u0014\u0011!a\u00019\u0005\u0019\u0001\u0010J\u0019\t\r\u0005\u0003\u0001\u0015)\u0003\u001d\u0003!iWm]:bO\u0016\u0004\u0003\"B\"\u0001\t\u0013!\u0015!C:fi\u0006\u001bG/[8o)\tYT\tC\u0003G\u0005\u0002\u0007A$A\u0001b\u0011\u0015A\u0005\u0001\"\u0001\u001c\u0003\u0019!xNS:p]\")!\n\u0001C\u0003\u0017\u0006Aa/\u00197jI\u0006$X\rF\u0001<\u0011\u0015i\u0005\u0001\"\u0005L\u0003)!wNV1mS\u0012\fG/\u001a\u0005\u0006\u001f\u0002!\t\u0002U\u0001\u0011CN\u001cXM\u001d;GS\u0016dG-S:TKR,\"!\u0015,\u0015\u0007m\u0012v\fC\u0003T\u001d\u0002\u0007A+A\u0003wC2,X\r\u0005\u0002V-2\u0001A!B,O\u0005\u0004A&!\u0001+\u0012\u0005ec\u0006C\u0001\b[\u0013\tYvBA\u0004O_RD\u0017N\\4\u0011\u00059i\u0016B\u00010\u0010\u0005\r\te.\u001f\u0005\u0006A:\u0003\r\u0001H\u0001\u0005]\u0006lW\rC\u0003c\u0001\u0011E1-\u0001\u0004bgN,'\u000f\u001e\u000b\u0004w\u0011L\u0007\"B3b\u0001\u00041\u0017!C2p]\u0012LG/[8o!\tqq-\u0003\u0002i\u001f\t9!i\\8mK\u0006t\u0007\"\u00026b\u0001\u0004a\u0012a\u00034bS2lUm]:bO\u0016DC\u0001\u00017paB\u0011q%\\\u0005\u0003]\"\u0012\u0011CS:p]B\u0013x\u000e]3sif|%\u000fZ3s\u0003)\tG\u000e\u001d5bE\u0016$\u0018nY\r\u0002\u0003!2\u0001A];w{Z\u0004\"aJ:\n\u0005QD#A\u0004&t_:\fU\u000f^8EKR,7\r^\u0001\u0011O\u0016$H/\u001a:WSNL'-\u001b7jif$\u0013a^\u0005\u0003qf\f1!\u0011(Z\u0015\tQ80\u0001\u0006WSNL'-\u001b7jifT!\u0001 \u0015\u0002\u001d)\u001bxN\\!vi>$U\r^3di\u0006\u00012/\u001a;uKJ4\u0016n]5cS2LG/\u001f\u0015\u0006\u0001}\u001c\u0016Q\u0001\t\u0004O\u0005\u0005\u0011bAA\u0002Q\tY!j]8o\u0013:\u001cG.\u001e3fI\t\t9!\u0003\u0003\u0002\n\u0005-\u0011\u0001\u0003(P\u001d~sU\u000b\u0014'\u000b\t\u00055\u0011qB\u0001\b\u0013:\u001cG.\u001e3f\u0015\r\t\t\u0002K\u0001\f\u0015N|g.\u00138dYV$Wm\u0002\u0005\u0002\u0016\tA\tABA\f\u0003e\u0019VOY7jiJ+7\u000f\u001e)s_R|7m\u001c7NKN\u001c\u0018mZ3\u0011\u0007a\tIBB\u0004\u0002\u0005!\u0005a!a\u0007\u0014\u0007\u0005eQ\u0002C\u0004\u0015\u00033!\t!a\b\u0015\u0005\u0005]\u0001BCA\u0012\u00033\u0011\r\u0011\"\u0003\u0002&\u0005i\u0001/Y2lC\u001e,\u0007K]3gSb,\"!a\n\u0011\t\u0005%\u00121G\u0007\u0003\u0003WQA!!\f\u00020\u0005!A.\u00198h\u0015\t\t\t$\u0001\u0003kCZ\f\u0017bA\u0011\u0002,!I\u0011qGA\rA\u0003%\u0011qE\u0001\u000fa\u0006\u001c7.Y4f!J,g-\u001b=!\u0011)\tY$!\u0007C\u0002\u0013%\u0011QH\u0001\u0007[\u0006\u0004\b/\u001a:\u0016\u0005\u0005}\u0002\u0003BA!\u0003\u000fj!!a\u0011\u000b\u0007\u0005\u0015#&\u0001\u0005eCR\f'-\u001b8e\u0013\u0011\tI%a\u0011\u0003\u0019=\u0013'.Z2u\u001b\u0006\u0004\b/\u001a:\t\u0013\u00055\u0013\u0011\u0004Q\u0001\n\u0005}\u0012aB7baB,'\u000f\t\u0005\t\u0003#\nI\u0002\"\u0001\u0002T\u0005Y\u0001/\u0019:tK\u0006\u001bG/[8o)\ra\u0012Q\u000b\u0005\b\u0003/\ny\u00051\u0001\u001d\u0003\u0011Q7o\u001c8\t\u0011\u0005m\u0013\u0011\u0004C\u0001\u0003;\n\u0001B\u001a:p[*\u001bxN\u001c\u000b\u0004/\u0005}\u0003bBA,\u00033\u0002\r\u0001\b\u0005\t\u00037\nI\u0002\"\u0001\u0002dU!\u0011QMA5)\u0019\t9'!\u001c\u0002pA\u0019Q+!\u001b\u0005\u000f]\u000b\tG1\u0001\u0002lE\u0011\u0011l\u0006\u0005\b\u0003/\n\t\u00071\u0001\u001d\u0011!\t\t(!\u0019A\u0002\u0005M\u0014!B2mCjT\b#B\u000f\u0002v\u0005\u001d\u0014bAA<E\t)1\t\\1tg\u0002"
)
public abstract class SubmitRestProtocolMessage {
   @JsonIgnore
   private final String messageType;
   private final String action;
   private String message;

   public static <T extends SubmitRestProtocolMessage> T fromJson(String var0, Class<T> var1) {
      return (T)SubmitRestProtocolMessage$.MODULE$.fromJson(var0, var1);
   }

   public static SubmitRestProtocolMessage fromJson(String var0) {
      return SubmitRestProtocolMessage$.MODULE$.fromJson(var0);
   }

   public static String parseAction(String var0) {
      return SubmitRestProtocolMessage$.MODULE$.parseAction(var0);
   }

   public String messageType() {
      return this.messageType;
   }

   public String action() {
      return this.action;
   }

   public String message() {
      return this.message;
   }

   public void message_$eq(String x$1) {
      this.message = x$1;
   }

   private void setAction(String a) {
   }

   public String toJson() {
      this.validate();
      return SubmitRestProtocolMessage$.MODULE$.org$apache$spark$deploy$rest$SubmitRestProtocolMessage$$mapper().writeValueAsString(this);
   }

   public final void validate() {
      try {
         this.doValidate();
      } catch (Exception var2) {
         throw new SubmitRestProtocolException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Validation of message ", " failed!"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.messageType()})), var2);
      }
   }

   public void doValidate() {
      if (this.action() == null) {
         throw new SubmitRestMissingFieldException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"The action field is missing in ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.messageType()})));
      }
   }

   public <T> void assertFieldIsSet(T value, String name) {
      if (value == null) {
         throw new SubmitRestMissingFieldException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"'", "' is missing in message ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{name, this.messageType()})));
      }
   }

   public void assert(boolean condition, String failMessage) {
      if (!condition) {
         throw new SubmitRestProtocolException(failMessage, SubmitRestProtocolException$.MODULE$.$lessinit$greater$default$2());
      }
   }

   public SubmitRestProtocolMessage() {
      this.messageType = Utils$.MODULE$.getFormattedClassName(this);
      this.action = this.messageType();
      this.message = null;
   }
}
