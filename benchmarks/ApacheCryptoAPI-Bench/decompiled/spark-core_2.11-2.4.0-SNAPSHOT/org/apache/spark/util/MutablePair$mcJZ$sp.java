package org.apache.spark.util;

import org.apache.spark.annotation.DeveloperApi;
import scala.runtime.BoxesRunTime;

@DeveloperApi
public class MutablePair$mcJZ$sp extends MutablePair<Object, Object> {
   public long _1$mcJ$sp;
   public boolean _2$mcZ$sp;

   public long _1$mcJ$sp() {
      return this._1$mcJ$sp;
   }

   public long _1() {
      return this._1$mcJ$sp();
   }

   public void _1$mcJ$sp_$eq(long x$1) {
      this._1$mcJ$sp = x$1;
   }

   public void _1_$eq(long x$1) {
      this._1$mcJ$sp_$eq(x$1);
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

   public MutablePair<Object, Object> update(long n1, boolean n2) {
      return this.update$mcJZ$sp(n1, n2);
   }

   public MutablePair<Object, Object> update$mcJZ$sp(long n1, boolean n2) {
      this._1_$eq(n1);
      this._2_$eq(n2);
      return this;
   }

   public <T1, T2> long copy$default$1() {
      return this.copy$default$1$mcJ$sp();
   }

   public <T1, T2> long copy$default$1$mcJ$sp() {
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

   public MutablePair$mcJZ$sp(long _1$mcJ$sp, boolean _2$mcZ$sp) {
      this._1$mcJ$sp = _1$mcJ$sp;
      this._2$mcZ$sp = _2$mcZ$sp;
      super((Object)null, (Object)null);
   }

   public MutablePair$mcJZ$sp() {
      this(BoxesRunTime.unboxToLong((Object)null), BoxesRunTime.unboxToBoolean((Object)null));
   }
}
