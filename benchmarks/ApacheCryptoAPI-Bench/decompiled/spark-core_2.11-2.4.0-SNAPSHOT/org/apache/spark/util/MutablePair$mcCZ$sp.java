package org.apache.spark.util;

import org.apache.spark.annotation.DeveloperApi;
import scala.runtime.BoxesRunTime;

@DeveloperApi
public class MutablePair$mcCZ$sp extends MutablePair<Object, Object> {
   public char _1$mcC$sp;
   public boolean _2$mcZ$sp;

   public char _1$mcC$sp() {
      return this._1$mcC$sp;
   }

   public char _1() {
      return this._1$mcC$sp();
   }

   public void _1$mcC$sp_$eq(char x$1) {
      this._1$mcC$sp = x$1;
   }

   public void _1_$eq(char x$1) {
      this._1$mcC$sp_$eq(x$1);
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

   public MutablePair<Object, Object> update(char n1, boolean n2) {
      return this.update$mcCZ$sp(n1, n2);
   }

   public MutablePair<Object, Object> update$mcCZ$sp(char n1, boolean n2) {
      this._1_$eq(n1);
      this._2_$eq(n2);
      return this;
   }

   public <T1, T2> char copy$default$1() {
      return this.copy$default$1$mcC$sp();
   }

   public <T1, T2> char copy$default$1$mcC$sp() {
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

   public MutablePair$mcCZ$sp(char _1$mcC$sp, boolean _2$mcZ$sp) {
      this._1$mcC$sp = _1$mcC$sp;
      this._2$mcZ$sp = _2$mcZ$sp;
      super((Object)null, (Object)null);
   }

   public MutablePair$mcCZ$sp() {
      this(BoxesRunTime.unboxToChar((Object)null), BoxesRunTime.unboxToBoolean((Object)null));
   }
}
