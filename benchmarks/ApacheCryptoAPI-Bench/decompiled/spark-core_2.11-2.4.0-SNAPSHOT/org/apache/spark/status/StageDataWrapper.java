package org.apache.spark.status;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.spark.status.api.v1.StageData;
import org.apache.spark.status.api.v1.StageStatus;
import org.apache.spark.util.kvstore.KVIndex;
import scala.collection.immutable.Map;
import scala.collection.immutable.Set;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u0005a!B\u0001\u0003\u0001\u0011Q!\u0001E*uC\u001e,G)\u0019;b/J\f\u0007\u000f]3s\u0015\t\u0019A!\u0001\u0004ti\u0006$Xo\u001d\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sON\u0011\u0001a\u0003\t\u0003\u0019=i\u0011!\u0004\u0006\u0002\u001d\u0005)1oY1mC&\u0011\u0001#\u0004\u0002\u0007\u0003:L(+\u001a4\t\u0011I\u0001!Q1A\u0005\u0002Q\tA!\u001b8g_\u000e\u0001Q#A\u000b\u0011\u0005YYR\"A\f\u000b\u0005aI\u0012A\u0001<2\u0015\tQ\"!A\u0002ba&L!\u0001H\f\u0003\u0013M#\u0018mZ3ECR\f\u0007\u0002\u0003\u0010\u0001\u0005\u0003\u0005\u000b\u0011B\u000b\u0002\u000b%tgm\u001c\u0011\t\u0011\u0001\u0002!Q1A\u0005\u0002\u0005\naA[8c\u0013\u0012\u001cX#\u0001\u0012\u0011\u0007\r2\u0013F\u0004\u0002\rI%\u0011Q%D\u0001\u0007!J,G-\u001a4\n\u0005\u001dB#aA*fi*\u0011Q%\u0004\t\u0003\u0019)J!aK\u0007\u0003\u0007%sG\u000f\u0003\u0005.\u0001\t\u0005\t\u0015!\u0003#\u0003\u001dQwNY%eg\u0002B\u0001b\f\u0001\u0003\u0006\u0004%\t\u0001M\u0001\tY>\u001c\u0017\r\\5usV\t\u0011\u0007\u0005\u0003$eQ:\u0014BA\u001a)\u0005\ri\u0015\r\u001d\t\u0003GUJ!A\u000e\u0015\u0003\rM#(/\u001b8h!\ta\u0001(\u0003\u0002:\u001b\t!Aj\u001c8h\u0011!Y\u0004A!A!\u0002\u0013\t\u0014!\u00037pG\u0006d\u0017\u000e^=!\u0011\u0015i\u0004\u0001\"\u0001?\u0003\u0019a\u0014N\\5u}Q!q(\u0011\"D!\t\u0001\u0005!D\u0001\u0003\u0011\u0015\u0011B\b1\u0001\u0016\u0011\u0015\u0001C\b1\u0001#\u0011\u0015yC\b1\u00012Q\u0011\u0019Ui\u0015+\u0011\u0005\u0019\u000bV\"A$\u000b\u0005!K\u0015AC1o]>$\u0018\r^5p]*\u0011!jS\u0001\tI\u0006$\u0018MY5oI*\u0011A*T\u0001\bU\u0006\u001c7n]8o\u0015\tqu*A\u0005gCN$XM\u001d=nY*\t\u0001+A\u0002d_6L!AU$\u0003\u001f)\u001bxN\u001c#fg\u0016\u0014\u0018.\u00197ju\u0016\f\u0011bY8oi\u0016tG/Q:$\u0003U\u0003\"AV.\u000e\u0003]S!\u0001W-\u0002\t1\fgn\u001a\u0006\u00025\u0006!!.\u0019<b\u0013\tIt\u000b\u0003\u0004^\u0001\u0001\u0006IAX\u0001\u0003S\u0012\u00042\u0001D0*\u0013\t\u0001WBA\u0003BeJ\f\u0017\u0010\u000b\u0002]EB\u00111\r[\u0007\u0002I*\u0011QMZ\u0001\bWZ\u001cHo\u001c:f\u0015\t9G!\u0001\u0003vi&d\u0017BA5e\u0005\u001dYe+\u00138eKbD#\u0001X6\u0011\u00051tW\"A7\u000b\u0005![\u0015BA8n\u0005)Q5o\u001c8JO:|'/\u001a\u0005\u0006c\u0002!IA]\u0001\bgR\fw-Z%e+\u0005I\u0003\u0006\u00029ciV\fQA^1mk\u0016\f\u0013!\u001d\u0015\u0003a.DQ\u0001\u001f\u0001\u0005\ne\fa!Y2uSZ,W#\u0001>\u0011\u00051Y\u0018B\u0001?\u000e\u0005\u001d\u0011un\u001c7fC:DCa\u001e2u}\u0006\n\u0001\u0010\u000b\u0002xW\u0002"
)
public class StageDataWrapper {
   private final StageData info;
   private final Set<Object> jobIds;
   private final Map<String, Object> locality;
   @JsonIgnore
   @KVIndex
   private final int[] id;

   public StageData info() {
      return this.info;
   }

   public Set<Object> jobIds() {
      return this.jobIds;
   }

   public Map<String, Object> locality() {
      return this.locality;
   }

   @JsonIgnore
   @KVIndex("stageId")
   private int stageId() {
      return this.info().stageId();
   }

   @JsonIgnore
   @KVIndex("active")
   private boolean active() {
      boolean var2;
      label23: {
         StageStatus var10000 = this.info().status();
         StageStatus var1 = StageStatus.ACTIVE;
         if (var10000 == null) {
            if (var1 == null) {
               break label23;
            }
         } else if (var10000.equals(var1)) {
            break label23;
         }

         var2 = false;
         return var2;
      }

      var2 = true;
      return var2;
   }

   public StageDataWrapper(StageData info, Set<Object> jobIds, @JsonDeserialize(contentAs = Long.class) Map<String, Object> locality) {
      this.info = info;
      this.jobIds = jobIds;
      this.locality = locality;
      this.id = new int[]{info.stageId(), info.attemptId()};
   }
}
