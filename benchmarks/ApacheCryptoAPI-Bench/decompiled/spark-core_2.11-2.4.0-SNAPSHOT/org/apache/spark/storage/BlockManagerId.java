package org.apache.spark.storage;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.spark.SparkContext$;
import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.util.Utils$;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u001df\u0001B\u0001\u0003\u0001-\u0011aB\u00117pG.l\u0015M\\1hKJLEM\u0003\u0002\u0004\t\u000591\u000f^8sC\u001e,'BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0004\u0001M\u0019\u0001\u0001\u0004\u000b\u0011\u00055\u0011R\"\u0001\b\u000b\u0005=\u0001\u0012\u0001\u00027b]\u001eT\u0011!E\u0001\u0005U\u00064\u0018-\u0003\u0002\u0014\u001d\t1qJ\u00196fGR\u0004\"!\u0006\r\u000e\u0003YQ!a\u0006\t\u0002\u0005%|\u0017BA\r\u0017\u00059)\u0005\u0010^3s]\u0006d\u0017N_1cY\u0016D\u0001b\u0007\u0001\u0003\u0002\u0004%I\u0001H\u0001\fKb,7-\u001e;pe&#w,F\u0001\u001e!\tqBE\u0004\u0002 E5\t\u0001EC\u0001\"\u0003\u0015\u00198-\u00197b\u0013\t\u0019\u0003%\u0001\u0004Qe\u0016$WMZ\u0005\u0003K\u0019\u0012aa\u0015;sS:<'BA\u0012!\u0011!A\u0003A!a\u0001\n\u0013I\u0013aD3yK\u000e,Ho\u001c:JI~{F%Z9\u0015\u0005)j\u0003CA\u0010,\u0013\ta\u0003E\u0001\u0003V]&$\bb\u0002\u0018(\u0003\u0003\u0005\r!H\u0001\u0004q\u0012\n\u0004\u0002\u0003\u0019\u0001\u0005\u0003\u0005\u000b\u0015B\u000f\u0002\u0019\u0015DXmY;u_JLEm\u0018\u0011\t\u0011I\u0002!\u00111A\u0005\nq\tQ\u0001[8ti~C\u0001\u0002\u000e\u0001\u0003\u0002\u0004%I!N\u0001\nQ>\u001cHoX0%KF$\"A\u000b\u001c\t\u000f9\u001a\u0014\u0011!a\u0001;!A\u0001\b\u0001B\u0001B\u0003&Q$\u0001\u0004i_N$x\f\t\u0005\tu\u0001\u0011\t\u0019!C\u0005w\u0005)\u0001o\u001c:u?V\tA\b\u0005\u0002 {%\u0011a\b\t\u0002\u0004\u0013:$\b\u0002\u0003!\u0001\u0005\u0003\u0007I\u0011B!\u0002\u0013A|'\u000f^0`I\u0015\fHC\u0001\u0016C\u0011\u001dqs(!AA\u0002qB\u0001\u0002\u0012\u0001\u0003\u0002\u0003\u0006K\u0001P\u0001\u0007a>\u0014Ho\u0018\u0011\t\u0011\u0019\u0003!\u00111A\u0005\n\u001d\u000bQ\u0002^8q_2|w-_%oM>|V#\u0001%\u0011\u0007}IU$\u0003\u0002KA\t1q\n\u001d;j_:D\u0001\u0002\u0014\u0001\u0003\u0002\u0004%I!T\u0001\u0012i>\u0004x\u000e\\8hs&sgm\\0`I\u0015\fHC\u0001\u0016O\u0011\u001dq3*!AA\u0002!C\u0001\u0002\u0015\u0001\u0003\u0002\u0003\u0006K\u0001S\u0001\u000fi>\u0004x\u000e\\8hs&sgm\\0!\u0011\u0015\u0011\u0006\u0001\"\u0003T\u0003\u0019a\u0014N\\5u}Q)AKV,Y3B\u0011Q\u000bA\u0007\u0002\u0005!)1$\u0015a\u0001;!)!'\u0015a\u0001;!)!(\u0015a\u0001y!)a)\u0015a\u0001\u0011\")!\u000b\u0001C\u00057R\tA\u000bC\u0003^\u0001\u0011\u0005A$\u0001\u0006fq\u0016\u001cW\u000f^8s\u0013\u0012DQa\u0018\u0001\u0005\u0002q\t\u0001\u0002[8tiB{'\u000f\u001e\u0005\u0006C\u0002!\t\u0001H\u0001\u0005Q>\u001cH\u000fC\u0003d\u0001\u0011\u00051(\u0001\u0003q_J$\b\"B3\u0001\t\u00039\u0015\u0001\u0004;pa>dwnZ=J]\u001a|\u0007\"B4\u0001\t\u0003A\u0017\u0001C5t\tJLg/\u001a:\u0016\u0003%\u0004\"a\b6\n\u0005-\u0004#a\u0002\"p_2,\u0017M\u001c\u0005\u0006[\u0002!\tE\\\u0001\u000eoJLG/Z#yi\u0016\u0014h.\u00197\u0015\u0005)z\u0007\"\u00029m\u0001\u0004\t\u0018aA8viB\u0011QC]\u0005\u0003gZ\u0011Ab\u00142kK\u000e$x*\u001e;qkRDQ!\u001e\u0001\u0005BY\fAB]3bI\u0016CH/\u001a:oC2$\"AK<\t\u000ba$\b\u0019A=\u0002\u0005%t\u0007CA\u000b{\u0013\tYhCA\u0006PE*,7\r^%oaV$\b\"B?\u0001\t\u0013q\u0018a\u0003:fC\u0012\u0014Vm]8mm\u0016$\u0012\u0001\u0004\u0015\u0006y\u0006\u0005\u0011Q\u0002\t\u0006?\u0005\r\u0011qA\u0005\u0004\u0003\u000b\u0001#A\u0002;ie><8\u000fE\u0002\u0016\u0003\u0013I1!a\u0003\u0017\u0005-Iu*\u0012=dKB$\u0018n\u001c8$\u0005\u0005\u001d\u0001bBA\t\u0001\u0011\u0005\u00131C\u0001\ti>\u001cFO]5oOR\tQ\u0004C\u0004\u0002\u0018\u0001!\t%!\u0007\u0002\u0011!\f7\u000f[\"pI\u0016$\u0012\u0001\u0010\u0005\b\u0003;\u0001A\u0011IA\u0010\u0003\u0019)\u0017/^1mgR\u0019\u0011.!\t\t\u0011\u0005\r\u00121\u0004a\u0001\u0003K\tA\u0001\u001e5biB\u0019q$a\n\n\u0007\u0005%\u0002EA\u0002B]fD3\u0001AA\u0017!\u0011\ty#!\u000e\u000e\u0005\u0005E\"bAA\u001a\t\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\t\u0005]\u0012\u0011\u0007\u0002\r\t\u00164X\r\\8qKJ\f\u0005/[\u0004\t\u0003w\u0011\u0001\u0012\u0001\u0003\u0002>\u0005q!\t\\8dW6\u000bg.Y4fe&#\u0007cA+\u0002@\u00199\u0011A\u0001E\u0001\t\u0005\u00053CBA \u0003\u0007\nI\u0005E\u0002 \u0003\u000bJ1!a\u0012!\u0005\u0019\te.\u001f*fMB\u0019q$a\u0013\n\u0007\u00055\u0003E\u0001\u0007TKJL\u0017\r\\5{C\ndW\rC\u0004S\u0003\u007f!\t!!\u0015\u0015\u0005\u0005u\u0002\u0002CA+\u0003\u007f!\t!a\u0016\u0002\u000b\u0005\u0004\b\u000f\\=\u0015\u0013Q\u000bI&!\u0018\u0002`\u0005\u0005\u0004bBA.\u0003'\u0002\r!H\u0001\u0007Kb,7-\u00133\t\r\u0005\f\u0019\u00061\u0001\u001e\u0011\u0019\u0019\u00171\u000ba\u0001y!AQ-a\u0015\u0011\u0002\u0003\u0007\u0001\n\u0003\u0005\u0002V\u0005}B\u0011AA3)\r!\u0016q\r\u0005\u0007q\u0006\r\u0004\u0019A=\t\u0015\u0005-\u0014q\bb\u0001\n\u0003\ti'A\ncY>\u001c7.T1oC\u001e,'/\u00133DC\u000eDW-\u0006\u0002\u0002pA1\u0011\u0011OA>)Rk!!a\u001d\u000b\t\u0005U\u0014qO\u0001\u000bG>t7-\u001e:sK:$(bAA=!\u0005!Q\u000f^5m\u0013\u0011\ti(a\u001d\u0003#\r{gnY;se\u0016tG\u000fS1tQ6\u000b\u0007\u000fC\u0005\u0002\u0002\u0006}\u0002\u0015!\u0003\u0002p\u0005!\"\r\\8dW6\u000bg.Y4fe&#7)Y2iK\u0002B\u0001\"!\"\u0002@\u0011\u0005\u0011qQ\u0001\u0018O\u0016$8)Y2iK\u0012\u0014En\\2l\u001b\u0006t\u0017mZ3s\u0013\u0012$2\u0001VAE\u0011\u001d\tY)a!A\u0002Q\u000b!!\u001b3\t\u0015\u0005=\u0015qHI\u0001\n\u0003\t\t*A\bbaBd\u0017\u0010\n3fM\u0006,H\u000e\u001e\u00135+\t\t\u0019JK\u0002I\u0003+[#!a&\u0011\t\u0005e\u0015\u0011U\u0007\u0003\u00037SA!!(\u0002 \u0006IQO\\2iK\u000e\\W\r\u001a\u0006\u0004\u0003g\u0001\u0013\u0002BAR\u00037\u0013\u0011#\u001e8dQ\u0016\u001c7.\u001a3WCJL\u0017M\\2f\u0011!i\u0018qHA\u0001\n\u0013q\b"
)
public class BlockManagerId implements Externalizable {
   private String org$apache$spark$storage$BlockManagerId$$executorId_;
   private String org$apache$spark$storage$BlockManagerId$$host_;
   private int org$apache$spark$storage$BlockManagerId$$port_;
   private Option<String> org$apache$spark$storage$BlockManagerId$$topologyInfo_;

   public static Option<String> apply$default$4() {
      return BlockManagerId$.MODULE$.apply$default$4();
   }

   public static BlockManagerId getCachedBlockManagerId(BlockManagerId var0) {
      return BlockManagerId$.MODULE$.getCachedBlockManagerId(var0);
   }

   public static ConcurrentHashMap<BlockManagerId, BlockManagerId> blockManagerIdCache() {
      return BlockManagerId$.MODULE$.blockManagerIdCache();
   }

   public static BlockManagerId apply(ObjectInput var0) {
      return BlockManagerId$.MODULE$.apply(var0);
   }

   public static BlockManagerId apply(String var0, String var1, int var2, Option<String> var3) {
      return BlockManagerId$.MODULE$.apply(var0, var1, var2, var3);
   }

   public String org$apache$spark$storage$BlockManagerId$$executorId_() {
      return this.org$apache$spark$storage$BlockManagerId$$executorId_;
   }

   public void org$apache$spark$storage$BlockManagerId$$executorId__$eq(String x$1) {
      this.org$apache$spark$storage$BlockManagerId$$executorId_ = x$1;
   }

   public String org$apache$spark$storage$BlockManagerId$$host_() {
      return this.org$apache$spark$storage$BlockManagerId$$host_;
   }

   public void org$apache$spark$storage$BlockManagerId$$host__$eq(String x$1) {
      this.org$apache$spark$storage$BlockManagerId$$host_ = x$1;
   }

   public int org$apache$spark$storage$BlockManagerId$$port_() {
      return this.org$apache$spark$storage$BlockManagerId$$port_;
   }

   public void org$apache$spark$storage$BlockManagerId$$port__$eq(int x$1) {
      this.org$apache$spark$storage$BlockManagerId$$port_ = x$1;
   }

   public Option<String> org$apache$spark$storage$BlockManagerId$$topologyInfo_() {
      return this.org$apache$spark$storage$BlockManagerId$$topologyInfo_;
   }

   public void org$apache$spark$storage$BlockManagerId$$topologyInfo__$eq(Option<String> x$1) {
      this.org$apache$spark$storage$BlockManagerId$$topologyInfo_ = x$1;
   }

   public String executorId() {
      return this.org$apache$spark$storage$BlockManagerId$$executorId_();
   }

   public String hostPort() {
      Utils$.MODULE$.checkHost(this.host());
      Predef$.MODULE$.assert(this.port() > 0);
      return (new StringBuilder()).append((Object)this.host()).append((Object)":").append((Object)BoxesRunTime.boxToInteger(this.port())).toString();
   }

   public String host() {
      return this.org$apache$spark$storage$BlockManagerId$$host_();
   }

   public int port() {
      return this.org$apache$spark$storage$BlockManagerId$$port_();
   }

   public Option<String> topologyInfo() {
      return this.org$apache$spark$storage$BlockManagerId$$topologyInfo_();
   }

   public boolean isDriver() {
      boolean var4;
      label32: {
         String var10000 = this.executorId();
         String var1 = SparkContext$.MODULE$.DRIVER_IDENTIFIER();
         if (var10000 == null) {
            if (var1 == null) {
               break label32;
            }
         } else if (var10000.equals(var1)) {
            break label32;
         }

         var10000 = this.executorId();
         String var2 = SparkContext$.MODULE$.LEGACY_DRIVER_IDENTIFIER();
         if (var10000 == null) {
            if (var2 == null) {
               break label32;
            }
         } else if (var10000.equals(var2)) {
            break label32;
         }

         var4 = false;
         return var4;
      }

      var4 = true;
      return var4;
   }

   public void writeExternal(ObjectOutput out) {
      Utils$.MODULE$.tryOrIOException(new Serializable(this, out) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BlockManagerId $outer;
         public final ObjectOutput out$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            this.out$1.writeUTF(this.$outer.org$apache$spark$storage$BlockManagerId$$executorId_());
            this.out$1.writeUTF(this.$outer.org$apache$spark$storage$BlockManagerId$$host_());
            this.out$1.writeInt(this.$outer.org$apache$spark$storage$BlockManagerId$$port_());
            this.out$1.writeBoolean(this.$outer.org$apache$spark$storage$BlockManagerId$$topologyInfo_().isDefined());
            this.$outer.topologyInfo().foreach(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final void apply(String x$1) {
                  this.$outer.out$1.writeUTF(x$1);
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.out$1 = out$1;
            }
         }
      });
   }

   public void readExternal(ObjectInput in) {
      Utils$.MODULE$.tryOrIOException(new Serializable(this, in) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BlockManagerId $outer;
         private final ObjectInput in$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            this.$outer.org$apache$spark$storage$BlockManagerId$$executorId__$eq(this.in$1.readUTF());
            this.$outer.org$apache$spark$storage$BlockManagerId$$host__$eq(this.in$1.readUTF());
            this.$outer.org$apache$spark$storage$BlockManagerId$$port__$eq(this.in$1.readInt());
            boolean isTopologyInfoAvailable = this.in$1.readBoolean();
            this.$outer.org$apache$spark$storage$BlockManagerId$$topologyInfo__$eq((Option)(isTopologyInfoAvailable ? Option$.MODULE$.apply(this.in$1.readUTF()) : None$.MODULE$));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.in$1 = in$1;
            }
         }
      });
   }

   private Object readResolve() throws IOException {
      return BlockManagerId$.MODULE$.getCachedBlockManagerId(this);
   }

   public String toString() {
      return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"BlockManagerId(", ", ", ", ", ", ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.executorId(), this.host(), BoxesRunTime.boxToInteger(this.port()), this.topologyInfo()}));
   }

   public int hashCode() {
      return ((this.executorId().hashCode() * 41 + this.host().hashCode()) * 41 + this.port()) * 41 + this.topologyInfo().hashCode();
   }

   public boolean equals(Object that) {
      boolean var4;
      if (that instanceof BlockManagerId) {
         boolean var10;
         label40: {
            label39: {
               label44: {
                  BlockManagerId var3 = (BlockManagerId)that;
                  String var10000 = this.executorId();
                  String var5 = var3.executorId();
                  if (var10000 == null) {
                     if (var5 != null) {
                        break label44;
                     }
                  } else if (!var10000.equals(var5)) {
                     break label44;
                  }

                  if (this.port() == var3.port()) {
                     label45: {
                        var10000 = this.host();
                        String var6 = var3.host();
                        if (var10000 == null) {
                           if (var6 != null) {
                              break label45;
                           }
                        } else if (!var10000.equals(var6)) {
                           break label45;
                        }

                        Option var9 = this.topologyInfo();
                        Option var7 = var3.topologyInfo();
                        if (var9 == null) {
                           if (var7 == null) {
                              break label39;
                           }
                        } else if (var9.equals(var7)) {
                           break label39;
                        }
                     }
                  }
               }

               var10 = false;
               break label40;
            }

            var10 = true;
         }

         var4 = var10;
      } else {
         var4 = false;
      }

      return var4;
   }

   public BlockManagerId(String executorId_, String host_, int port_, Option<String> topologyInfo_) {
      this.org$apache$spark$storage$BlockManagerId$$executorId_ = executorId_;
      this.org$apache$spark$storage$BlockManagerId$$host_ = host_;
      this.org$apache$spark$storage$BlockManagerId$$port_ = port_;
      this.org$apache$spark$storage$BlockManagerId$$topologyInfo_ = topologyInfo_;
      super();
      if (this.org$apache$spark$storage$BlockManagerId$$host_() != null) {
         Utils$.MODULE$.checkHost(this.org$apache$spark$storage$BlockManagerId$$host_());
         Predef$.MODULE$.assert(this.org$apache$spark$storage$BlockManagerId$$port_() > 0);
      }

   }

   public BlockManagerId() {
      this((String)null, (String)null, 0, None$.MODULE$);
   }
}
