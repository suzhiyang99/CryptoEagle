package org.apache.spark.util;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.Tuple2$mcCC$sp;
import scala.Tuple2$mcCD$sp;
import scala.Tuple2$mcCI$sp;
import scala.Tuple2$mcCJ$sp;
import scala.Tuple2$mcCZ$sp;
import scala.Tuple2$mcDC$sp;
import scala.Tuple2$mcDD$sp;
import scala.Tuple2$mcDI$sp;
import scala.Tuple2$mcDJ$sp;
import scala.Tuple2$mcDZ$sp;
import scala.Tuple2$mcIC$sp;
import scala.Tuple2$mcID$sp;
import scala.Tuple2$mcII$sp;
import scala.Tuple2$mcIJ$sp;
import scala.Tuple2$mcIZ$sp;
import scala.Tuple2$mcJC$sp;
import scala.Tuple2$mcJD$sp;
import scala.Tuple2$mcJI$sp;
import scala.Tuple2$mcJJ$sp;
import scala.Tuple2$mcJZ$sp;
import scala.Tuple2$mcZC$sp;
import scala.Tuple2$mcZD$sp;
import scala.Tuple2$mcZI$sp;
import scala.Tuple2$mcZJ$sp;
import scala.Tuple2$mcZZ$sp;

public final class MutablePair$ implements Serializable {
   public static final MutablePair$ MODULE$;

   static {
      new MutablePair$();
   }

   public final String toString() {
      return "MutablePair";
   }

   public <T1, T2> MutablePair<T1, T2> apply(T1 _1, T2 _2) {
      return new MutablePair<T1, T2>(_1, _2);
   }

   public <T1, T2> Option<Tuple2<T1, T2>> unapply(MutablePair<T1, T2> x$0) {
      return (Option<Tuple2<T1, T2>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2(x$0._1(), x$0._2())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   public MutablePair<Object, Object> apply$mZZc$sp(boolean _1, boolean _2) {
      return new MutablePair$mcZZ$sp(_1, _2);
   }

   public MutablePair<Object, Object> apply$mZCc$sp(boolean _1, char _2) {
      return new MutablePair$mcZC$sp(_1, _2);
   }

   public MutablePair<Object, Object> apply$mZDc$sp(boolean _1, double _2) {
      return new MutablePair$mcZD$sp(_1, _2);
   }

   public MutablePair<Object, Object> apply$mZIc$sp(boolean _1, int _2) {
      return new MutablePair$mcZI$sp(_1, _2);
   }

   public MutablePair<Object, Object> apply$mZJc$sp(boolean _1, long _2) {
      return new MutablePair$mcZJ$sp(_1, _2);
   }

   public MutablePair<Object, Object> apply$mCZc$sp(char _1, boolean _2) {
      return new MutablePair$mcCZ$sp(_1, _2);
   }

   public MutablePair<Object, Object> apply$mCCc$sp(char _1, char _2) {
      return new MutablePair$mcCC$sp(_1, _2);
   }

   public MutablePair<Object, Object> apply$mCDc$sp(char _1, double _2) {
      return new MutablePair$mcCD$sp(_1, _2);
   }

   public MutablePair<Object, Object> apply$mCIc$sp(char _1, int _2) {
      return new MutablePair$mcCI$sp(_1, _2);
   }

   public MutablePair<Object, Object> apply$mCJc$sp(char _1, long _2) {
      return new MutablePair$mcCJ$sp(_1, _2);
   }

   public MutablePair<Object, Object> apply$mDZc$sp(double _1, boolean _2) {
      return new MutablePair$mcDZ$sp(_1, _2);
   }

   public MutablePair<Object, Object> apply$mDCc$sp(double _1, char _2) {
      return new MutablePair$mcDC$sp(_1, _2);
   }

   public MutablePair<Object, Object> apply$mDDc$sp(double _1, double _2) {
      return new MutablePair$mcDD$sp(_1, _2);
   }

   public MutablePair<Object, Object> apply$mDIc$sp(double _1, int _2) {
      return new MutablePair$mcDI$sp(_1, _2);
   }

   public MutablePair<Object, Object> apply$mDJc$sp(double _1, long _2) {
      return new MutablePair$mcDJ$sp(_1, _2);
   }

   public MutablePair<Object, Object> apply$mIZc$sp(int _1, boolean _2) {
      return new MutablePair$mcIZ$sp(_1, _2);
   }

   public MutablePair<Object, Object> apply$mICc$sp(int _1, char _2) {
      return new MutablePair$mcIC$sp(_1, _2);
   }

   public MutablePair<Object, Object> apply$mIDc$sp(int _1, double _2) {
      return new MutablePair$mcID$sp(_1, _2);
   }

   public MutablePair<Object, Object> apply$mIIc$sp(int _1, int _2) {
      return new MutablePair$mcII$sp(_1, _2);
   }

   public MutablePair<Object, Object> apply$mIJc$sp(int _1, long _2) {
      return new MutablePair$mcIJ$sp(_1, _2);
   }

   public MutablePair<Object, Object> apply$mJZc$sp(long _1, boolean _2) {
      return new MutablePair$mcJZ$sp(_1, _2);
   }

   public MutablePair<Object, Object> apply$mJCc$sp(long _1, char _2) {
      return new MutablePair$mcJC$sp(_1, _2);
   }

   public MutablePair<Object, Object> apply$mJDc$sp(long _1, double _2) {
      return new MutablePair$mcJD$sp(_1, _2);
   }

   public MutablePair<Object, Object> apply$mJIc$sp(long _1, int _2) {
      return new MutablePair$mcJI$sp(_1, _2);
   }

   public MutablePair<Object, Object> apply$mJJc$sp(long _1, long _2) {
      return new MutablePair$mcJJ$sp(_1, _2);
   }

   public Option<Tuple2<Object, Object>> unapply$mZZc$sp(MutablePair<Object, Object> x$0) {
      return (Option<Tuple2<Object, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2$mcZZ$sp(x$0._1$mcZ$sp(), x$0._2$mcZ$sp())));
   }

   public Option<Tuple2<Object, Object>> unapply$mZCc$sp(MutablePair<Object, Object> x$0) {
      return (Option<Tuple2<Object, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2$mcZC$sp(x$0._1$mcZ$sp(), x$0._2$mcC$sp())));
   }

   public Option<Tuple2<Object, Object>> unapply$mZDc$sp(MutablePair<Object, Object> x$0) {
      return (Option<Tuple2<Object, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2$mcZD$sp(x$0._1$mcZ$sp(), x$0._2$mcD$sp())));
   }

   public Option<Tuple2<Object, Object>> unapply$mZIc$sp(MutablePair<Object, Object> x$0) {
      return (Option<Tuple2<Object, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2$mcZI$sp(x$0._1$mcZ$sp(), x$0._2$mcI$sp())));
   }

   public Option<Tuple2<Object, Object>> unapply$mZJc$sp(MutablePair<Object, Object> x$0) {
      return (Option<Tuple2<Object, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2$mcZJ$sp(x$0._1$mcZ$sp(), x$0._2$mcJ$sp())));
   }

   public Option<Tuple2<Object, Object>> unapply$mCZc$sp(MutablePair<Object, Object> x$0) {
      return (Option<Tuple2<Object, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2$mcCZ$sp(x$0._1$mcC$sp(), x$0._2$mcZ$sp())));
   }

   public Option<Tuple2<Object, Object>> unapply$mCCc$sp(MutablePair<Object, Object> x$0) {
      return (Option<Tuple2<Object, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2$mcCC$sp(x$0._1$mcC$sp(), x$0._2$mcC$sp())));
   }

   public Option<Tuple2<Object, Object>> unapply$mCDc$sp(MutablePair<Object, Object> x$0) {
      return (Option<Tuple2<Object, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2$mcCD$sp(x$0._1$mcC$sp(), x$0._2$mcD$sp())));
   }

   public Option<Tuple2<Object, Object>> unapply$mCIc$sp(MutablePair<Object, Object> x$0) {
      return (Option<Tuple2<Object, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2$mcCI$sp(x$0._1$mcC$sp(), x$0._2$mcI$sp())));
   }

   public Option<Tuple2<Object, Object>> unapply$mCJc$sp(MutablePair<Object, Object> x$0) {
      return (Option<Tuple2<Object, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2$mcCJ$sp(x$0._1$mcC$sp(), x$0._2$mcJ$sp())));
   }

   public Option<Tuple2<Object, Object>> unapply$mDZc$sp(MutablePair<Object, Object> x$0) {
      return (Option<Tuple2<Object, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2$mcDZ$sp(x$0._1$mcD$sp(), x$0._2$mcZ$sp())));
   }

   public Option<Tuple2<Object, Object>> unapply$mDCc$sp(MutablePair<Object, Object> x$0) {
      return (Option<Tuple2<Object, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2$mcDC$sp(x$0._1$mcD$sp(), x$0._2$mcC$sp())));
   }

   public Option<Tuple2<Object, Object>> unapply$mDDc$sp(MutablePair<Object, Object> x$0) {
      return (Option<Tuple2<Object, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2$mcDD$sp(x$0._1$mcD$sp(), x$0._2$mcD$sp())));
   }

   public Option<Tuple2<Object, Object>> unapply$mDIc$sp(MutablePair<Object, Object> x$0) {
      return (Option<Tuple2<Object, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2$mcDI$sp(x$0._1$mcD$sp(), x$0._2$mcI$sp())));
   }

   public Option<Tuple2<Object, Object>> unapply$mDJc$sp(MutablePair<Object, Object> x$0) {
      return (Option<Tuple2<Object, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2$mcDJ$sp(x$0._1$mcD$sp(), x$0._2$mcJ$sp())));
   }

   public Option<Tuple2<Object, Object>> unapply$mIZc$sp(MutablePair<Object, Object> x$0) {
      return (Option<Tuple2<Object, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2$mcIZ$sp(x$0._1$mcI$sp(), x$0._2$mcZ$sp())));
   }

   public Option<Tuple2<Object, Object>> unapply$mICc$sp(MutablePair<Object, Object> x$0) {
      return (Option<Tuple2<Object, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2$mcIC$sp(x$0._1$mcI$sp(), x$0._2$mcC$sp())));
   }

   public Option<Tuple2<Object, Object>> unapply$mIDc$sp(MutablePair<Object, Object> x$0) {
      return (Option<Tuple2<Object, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2$mcID$sp(x$0._1$mcI$sp(), x$0._2$mcD$sp())));
   }

   public Option<Tuple2<Object, Object>> unapply$mIIc$sp(MutablePair<Object, Object> x$0) {
      return (Option<Tuple2<Object, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2$mcII$sp(x$0._1$mcI$sp(), x$0._2$mcI$sp())));
   }

   public Option<Tuple2<Object, Object>> unapply$mIJc$sp(MutablePair<Object, Object> x$0) {
      return (Option<Tuple2<Object, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2$mcIJ$sp(x$0._1$mcI$sp(), x$0._2$mcJ$sp())));
   }

   public Option<Tuple2<Object, Object>> unapply$mJZc$sp(MutablePair<Object, Object> x$0) {
      return (Option<Tuple2<Object, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2$mcJZ$sp(x$0._1$mcJ$sp(), x$0._2$mcZ$sp())));
   }

   public Option<Tuple2<Object, Object>> unapply$mJCc$sp(MutablePair<Object, Object> x$0) {
      return (Option<Tuple2<Object, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2$mcJC$sp(x$0._1$mcJ$sp(), x$0._2$mcC$sp())));
   }

   public Option<Tuple2<Object, Object>> unapply$mJDc$sp(MutablePair<Object, Object> x$0) {
      return (Option<Tuple2<Object, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2$mcJD$sp(x$0._1$mcJ$sp(), x$0._2$mcD$sp())));
   }

   public Option<Tuple2<Object, Object>> unapply$mJIc$sp(MutablePair<Object, Object> x$0) {
      return (Option<Tuple2<Object, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2$mcJI$sp(x$0._1$mcJ$sp(), x$0._2$mcI$sp())));
   }

   public Option<Tuple2<Object, Object>> unapply$mJJc$sp(MutablePair<Object, Object> x$0) {
      return (Option<Tuple2<Object, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2$mcJJ$sp(x$0._1$mcJ$sp(), x$0._2$mcJ$sp())));
   }

   private MutablePair$() {
      MODULE$ = this;
   }
}
