package org.apache.spark.util;

import org.apache.spark.annotation.DeveloperApi;
import scala.runtime.BoxesRunTime;

@DeveloperApi
public class MutablePair$mcCJ$sp extends MutablePair<Object, Object> {
   public char _1$mcC$sp;
   public long _2$mcJ$sp;

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

   public long _2$mcJ$sp() {
      return this._2$mcJ$sp;
   }

   public long _2() {
      return this._2$mcJ$sp();
   }

   public void _2$mcJ$sp_$eq(long x$1) {
      this._2$mcJ$sp = x$1;
   }

   public void _2_$eq(long x$1) {
      this._2$mcJ$sp_$eq(x$1);
   }

   public MutablePair<Object, Object> update(char n1, long n2) {
      return this.update$mcCJ$sp(n1, n2);
   }

   public MutablePair<Object, Object> update$mcCJ$sp(char n1, long n2) {
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

   public <T1, T2> long copy$default$2() {
      return this.copy$default$2$mcJ$sp();
   }

   public <T1, T2> long copy$default$2$mcJ$sp() {
      return this._2();
   }

   public boolean specInstance$() {
      return true;
   }

   public MutablePair$mcCJ$sp(char _1$mcC$sp, long _2$mcJ$sp) {
      this._1$mcC$sp = _1$mcC$sp;
      this._2$mcJ$sp = _2$mcJ$sp;
      super((Object)null, (Object)null);
   }

   public MutablePair$mcCJ$sp() {
      this(BoxesRunTime.unboxToChar((Object)null), BoxesRunTime.unboxToLong((Object)null));
   }
}
