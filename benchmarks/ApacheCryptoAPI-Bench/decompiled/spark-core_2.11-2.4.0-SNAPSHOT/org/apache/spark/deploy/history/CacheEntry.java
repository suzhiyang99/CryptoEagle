package org.apache.spark.deploy.history;

import scala.Predef$;
import scala.StringContext;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001U2Q!\u0001\u0002\u0003\u00051\u0011!bQ1dQ\u0016,e\u000e\u001e:z\u0015\t\u0019A!A\u0004iSN$xN]=\u000b\u0005\u00151\u0011A\u00023fa2|\u0017P\u0003\u0002\b\u0011\u0005)1\u000f]1sW*\u0011\u0011BC\u0001\u0007CB\f7\r[3\u000b\u0003-\t1a\u001c:h'\t\u0001Q\u0002\u0005\u0002\u000f#5\tqBC\u0001\u0011\u0003\u0015\u00198-\u00197b\u0013\t\u0011rB\u0001\u0004B]f\u0014VM\u001a\u0005\t)\u0001\u0011)\u0019!C\u0001-\u0005AAn\\1eK\u0012,\u0016j\u0001\u0001\u0016\u0003]\u0001\"\u0001G\r\u000e\u0003\tI!A\u0007\u0002\u0003\u00171{\u0017\rZ3e\u0003B\u0004X+\u0013\u0005\t9\u0001\u0011\t\u0011)A\u0005/\u0005IAn\\1eK\u0012,\u0016\n\t\u0005\t=\u0001\u0011)\u0019!C\u0001?\u0005I1m\\7qY\u0016$X\rZ\u000b\u0002AA\u0011a\"I\u0005\u0003E=\u0011qAQ8pY\u0016\fg\u000e\u0003\u0005%\u0001\t\u0005\t\u0015!\u0003!\u0003)\u0019w.\u001c9mKR,G\r\t\u0005\u0006M\u0001!\taJ\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0007!J#\u0006\u0005\u0002\u0019\u0001!)A#\na\u0001/!)a$\na\u0001A!)A\u0006\u0001C![\u0005AAo\\*ue&tw\rF\u0001/!\ty#G\u0004\u0002\u000fa%\u0011\u0011gD\u0001\u0007!J,G-\u001a4\n\u0005M\"$AB*ue&twM\u0003\u00022\u001f\u0001"
)
public final class CacheEntry {
   private final LoadedAppUI loadedUI;
   private final boolean completed;

   public LoadedAppUI loadedUI() {
      return this.loadedUI;
   }

   public boolean completed() {
      return this.completed;
   }

   public String toString() {
      return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"UI ", ", completed=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.loadedUI().ui(), BoxesRunTime.boxToBoolean(this.completed())}));
   }

   public CacheEntry(LoadedAppUI loadedUI, boolean completed) {
      this.loadedUI = loadedUI;
      this.completed = completed;
   }
}
