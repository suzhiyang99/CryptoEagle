package org.apache.wicket.util.lang;

public class Exceptions {
   private Exceptions() {
   }

   public Throwable getRootCause(Throwable throwable) {
      Throwable cursor;
      for(cursor = throwable; cursor.getCause() != null; cursor = cursor.getCause()) {
      }

      return cursor;
   }

   public static <T extends Throwable> T findCause(Throwable throwable, final Class<T> causeType) {
      return (T)(visit(throwable, new IThrowableVisitor<T>() {
         public void visit(Throwable throwable, Visit<T> visit) {
            if (causeType.isAssignableFrom(throwable.getClass())) {
               visit.stop(throwable);
            }

         }
      }));
   }

   public static <T> T visit(Throwable throwable, IThrowableVisitor<T> visitor) {
      Visit<T> visit = new Visit<T>();

      for(Throwable cursor = throwable; cursor != null; cursor = cursor.getCause()) {
         visitor.visit(cursor, visit);
         if (visit.stopped) {
            return visit.result;
         }
      }

      return null;
   }

   public static class Visit<T> {
      private T result;
      private boolean stopped;

      public void stop(T result) {
         this.result = result;
         this.stop();
      }

      public void stop() {
         this.stopped = true;
      }
   }

   public interface IThrowableVisitor<T> {
      void visit(Throwable var1, Visit<T> var2);
   }
}
