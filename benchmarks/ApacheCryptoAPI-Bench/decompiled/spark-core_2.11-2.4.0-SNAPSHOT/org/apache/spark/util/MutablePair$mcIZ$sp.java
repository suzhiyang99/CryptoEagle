package org.apache.spark.util;

import org.apache.spark.annotation.DeveloperApi;
import scala.runtime.BoxesRunTime;

@DeveloperApi
public class MutablePair$mcIZ$sp extends MutablePair<Object, Object> {
   public int _1$mcI$sp;
   public boolean _2$mcZ$sp;

   public int _1$mcI$sp() {
      return this._1$mcI$sp;
   }

   public int _1() {
      return this._1$mcI$sp();
   }

   public void _1$mcI$sp_$eq(int x$1) {
      this._1$mcI$sp = x$1;
   }

   public void _1_$eq(int x$1) {
      this._1$mcI$sp_$eq(x$1);
   }

   public boolean _2$mcZ$sp() {
      return this._2$mcZ$sp;
   }

   public boolean _2() {
      return this._2$mcZ$sp();
   }

   public void _2$mcZ$sp_$eq(boolean x$1) {
      this._2$mcZ$sp = x$1;
   }

   public void _2_$eq(boolean x$1) {
      this._2$mcZ$sp_$eq(x$1);
   }

   public MutablePair<Object, Object> update(int n1, boolean n2) {
      return this.update$mcIZ$sp(n1, n2);
   }

   public MutablePair<Object, Object> update$mcIZ$sp(int n1, boolean n2) {
      this._1_$eq(n1);
      this._2_$eq(n2);
      return this;
   }

   public <T1, T2> int copy$default$1() {
      return this.copy$default$1$mcI$sp();
   }

   public <T1, T2> int copy$default$1$mcI$sp() {
      return this._1();
   }

   public <T1, T2> boolean copy$default$2() {
      return this.copy$default$2$mcZ$sp();
   }

   public <T1, T2> boolean copy$default$2$mcZ$sp() {
      return this._2();
   }

   public boolean specInstance$() {
      return true;
   }

   public MutablePair$mcIZ$sp(int _1$mcI$sp, boolean _2$mcZ$sp) {
      this._1$mcI$sp = _1$mcI$sp;
      this._2$mcZ$sp = _2$mcZ$sp;
      super((Object)null, (Object)null);
   }

   public MutablePair$mcIZ$sp() {
      this(BoxesRunTime.unboxToInt((Object)null), BoxesRunTime.unboxToBoolean((Object)null));
   }
}
