package org.apache.spark.util.random;

import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.collection.mutable.ArrayBuffer;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u00055a!B\u0001\u0003\u0001\ta!\u0001E!dG\u0016\u0004H/\u00198dKJ+7/\u001e7u\u0015\t\u0019A!\u0001\u0004sC:$w.\u001c\u0006\u0003\u000b\u0019\tA!\u001e;jY*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014xmE\u0002\u0001\u001bM\u0001\"AD\t\u000e\u0003=Q\u0011\u0001E\u0001\u0006g\u000e\fG.Y\u0005\u0003%=\u0011a!\u00118z%\u00164\u0007C\u0001\b\u0015\u0013\t)rB\u0001\u0007TKJL\u0017\r\\5{C\ndW\r\u0003\u0005\u0018\u0001\t\u0005\r\u0011\"\u0001\u001a\u0003!qW/\\%uK6\u001c8\u0001A\u000b\u00025A\u0011abG\u0005\u00039=\u0011A\u0001T8oO\"Aa\u0004\u0001BA\u0002\u0013\u0005q$\u0001\u0007ok6LE/Z7t?\u0012*\u0017\u000f\u0006\u0002!GA\u0011a\"I\u0005\u0003E=\u0011A!\u00168ji\"9A%HA\u0001\u0002\u0004Q\u0012a\u0001=%c!Aa\u0005\u0001B\u0001B\u0003&!$A\u0005ok6LE/Z7tA!A\u0001\u0006\u0001BA\u0002\u0013\u0005\u0011$A\u0006ok6\f5mY3qi\u0016$\u0007\u0002\u0003\u0016\u0001\u0005\u0003\u0007I\u0011A\u0016\u0002\u001f9,X.Q2dKB$X\rZ0%KF$\"\u0001\t\u0017\t\u000f\u0011J\u0013\u0011!a\u00015!Aa\u0006\u0001B\u0001B\u0003&!$\u0001\u0007ok6\f5mY3qi\u0016$\u0007\u0005C\u00031\u0001\u0011\u0005\u0011'\u0001\u0004=S:LGO\u0010\u000b\u0004eQ*\u0004CA\u001a\u0001\u001b\u0005\u0011\u0001bB\f0!\u0003\u0005\rA\u0007\u0005\bQ=\u0002\n\u00111\u0001\u001b\u0011\u001d9\u0004A1A\u0005\u0002a\n\u0001b^1ji2K7\u000f^\u000b\u0002sA\u0019!hP!\u000e\u0003mR!\u0001P\u001f\u0002\u000f5,H/\u00192mK*\u0011ahD\u0001\u000bG>dG.Z2uS>t\u0017B\u0001!<\u0005-\t%O]1z\u0005V4g-\u001a:\u0011\u00059\u0011\u0015BA\"\u0010\u0005\u0019!u.\u001e2mK\"1Q\t\u0001Q\u0001\ne\n\u0011b^1ji2K7\u000f\u001e\u0011\t\u000f\u001d\u0003\u0001\u0019!C\u0001\u0011\u0006Y\u0011mY2faR\u0014u.\u001e8e+\u0005\t\u0005b\u0002&\u0001\u0001\u0004%\taS\u0001\u0010C\u000e\u001cW\r\u001d;C_VtGm\u0018\u0013fcR\u0011\u0001\u0005\u0014\u0005\bI%\u000b\t\u00111\u0001B\u0011\u0019q\u0005\u0001)Q\u0005\u0003\u0006a\u0011mY2faR\u0014u.\u001e8eA!9\u0001\u000b\u0001a\u0001\n\u0003A\u0015!D<bSRd\u0015n\u001d;C_VtG\rC\u0004S\u0001\u0001\u0007I\u0011A*\u0002#]\f\u0017\u000e\u001e'jgR\u0014u.\u001e8e?\u0012*\u0017\u000f\u0006\u0002!)\"9A%UA\u0001\u0002\u0004\t\u0005B\u0002,\u0001A\u0003&\u0011)\u0001\bxC&$H*[:u\u0005>,h\u000e\u001a\u0011\t\u000ba\u0003A\u0011A-\u0002\u001d\u0005\u0014XMQ8v]\u0012\u001cX)\u001c9usV\t!\f\u0005\u0002\u000f7&\u0011Al\u0004\u0002\b\u0005>|G.Z1o\u0011\u0015q\u0006\u0001\"\u0001`\u0003\u0015iWM]4f)\t\u0001\u0003\rC\u0003b;\u0002\u0007!-A\u0003pi\",'\u000fE\u0002\u000fGJJ!\u0001Z\b\u0003\r=\u0003H/[8o\u000f!1'!!A\t\u0002\t9\u0017\u0001E!dG\u0016\u0004H/\u00198dKJ+7/\u001e7u!\t\u0019\u0004N\u0002\u0005\u0002\u0005\u0005\u0005\t\u0012\u0001\u0002j'\rAWb\u0005\u0005\u0006a!$\ta\u001b\u000b\u0002O\"9Q\u000e[I\u0001\n\u0003q\u0017a\u0007\u0013mKN\u001c\u0018N\\5uI\u001d\u0014X-\u0019;fe\u0012\"WMZ1vYR$\u0013'F\u0001pU\tQ\u0002oK\u0001r!\t\u0011x/D\u0001t\u0015\t!X/A\u0005v]\u000eDWmY6fI*\u0011aoD\u0001\u000bC:tw\u000e^1uS>t\u0017B\u0001=t\u0005E)hn\u00195fG.,GMV1sS\u0006t7-\u001a\u0005\bu\"\f\n\u0011\"\u0001o\u0003m!C.Z:tS:LG\u000fJ4sK\u0006$XM\u001d\u0013eK\u001a\fW\u000f\u001c;%e!9A\u0010[A\u0001\n\u0013i\u0018a\u0003:fC\u0012\u0014Vm]8mm\u0016$\u0012A \t\u0004\u007f\u0006%QBAA\u0001\u0015\u0011\t\u0019!!\u0002\u0002\t1\fgn\u001a\u0006\u0003\u0003\u000f\tAA[1wC&!\u00111BA\u0001\u0005\u0019y%M[3di\u0002"
)
public class AcceptanceResult implements Serializable {
   private long numItems;
   private long numAccepted;
   private final ArrayBuffer<Object> waitList;
   private double acceptBound;
   private double waitListBound;

   public static long $lessinit$greater$default$2() {
      return AcceptanceResult$.MODULE$.$lessinit$greater$default$2();
   }

   public static long $lessinit$greater$default$1() {
      return AcceptanceResult$.MODULE$.$lessinit$greater$default$1();
   }

   public long numItems() {
      return this.numItems;
   }

   public void numItems_$eq(long x$1) {
      this.numItems = x$1;
   }

   public long numAccepted() {
      return this.numAccepted;
   }

   public void numAccepted_$eq(long x$1) {
      this.numAccepted = x$1;
   }

   public ArrayBuffer<Object> waitList() {
      return this.waitList;
   }

   public double acceptBound() {
      return this.acceptBound;
   }

   public void acceptBound_$eq(double x$1) {
      this.acceptBound = x$1;
   }

   public double waitListBound() {
      return this.waitListBound;
   }

   public void waitListBound_$eq(double x$1) {
      this.waitListBound = x$1;
   }

   public boolean areBoundsEmpty() {
      return Predef$.MODULE$.double2Double(this.acceptBound()).isNaN() || Predef$.MODULE$.double2Double(this.waitListBound()).isNaN();
   }

   public void merge(Option<AcceptanceResult> other) {
      if (other.isDefined()) {
         this.waitList().$plus$plus$eq(((AcceptanceResult)other.get()).waitList());
         this.numAccepted_$eq(this.numAccepted() + ((AcceptanceResult)other.get()).numAccepted());
         this.numItems_$eq(this.numItems() + ((AcceptanceResult)other.get()).numItems());
      }

   }

   public AcceptanceResult(long numItems, long numAccepted) {
      this.numItems = numItems;
      this.numAccepted = numAccepted;
      super();
      this.waitList = new ArrayBuffer<Object>();
      this.acceptBound = Double.NaN;
      this.waitListBound = Double.NaN;
   }
}
