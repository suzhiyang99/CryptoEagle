package org.apache.spark.util;

import org.apache.spark.annotation.DeveloperApi;
import scala.Product2$mcIJ$sp;
import scala.Product2$mcIJ$sp$class;
import scala.runtime.BoxesRunTime;

@DeveloperApi
public class MutablePair$mcIJ$sp extends MutablePair<Object, Object> implements Product2$mcIJ$sp {
   public int _1$mcI$sp;
   public long _2$mcJ$sp;

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

   public MutablePair<Object, Object> update(int n1, long n2) {
      return this.update$mcIJ$sp(n1, n2);
   }

   public MutablePair<Object, Object> update$mcIJ$sp(int n1, long n2) {
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

   public <T1, T2> long copy$default$2() {
      return this.copy$default$2$mcJ$sp();
   }

   public <T1, T2> long copy$default$2$mcJ$sp() {
      return this._2();
   }

   public boolean specInstance$() {
      return true;
   }

   public MutablePair$mcIJ$sp(int _1$mcI$sp, long _2$mcJ$sp) {
      this._1$mcI$sp = _1$mcI$sp;
      this._2$mcJ$sp = _2$mcJ$sp;
      super((Object)null, (Object)null);
      Product2$mcIJ$sp$class.$init$(this);
   }

   public MutablePair$mcIJ$sp() {
      this(BoxesRunTime.unboxToInt((Object)null), BoxesRunTime.unboxToLong((Object)null));
   }
}
