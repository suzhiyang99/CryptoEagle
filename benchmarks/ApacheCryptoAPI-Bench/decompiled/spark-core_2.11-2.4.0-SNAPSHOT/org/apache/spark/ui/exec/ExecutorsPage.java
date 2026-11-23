package org.apache.spark.ui.exec;

import javax.servlet.http.HttpServletRequest;
import org.apache.spark.ui.SparkUITab;
import org.apache.spark.ui.UIUtils$;
import org.apache.spark.ui.WebUIPage;
import scala.Function0;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.collection.Seq;
import scala.collection.TraversableLike;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.xml.Elem;
import scala.xml.MetaData;
import scala.xml.Node;
import scala.xml.NodeBuffer;
import scala.xml.Null;
import scala.xml.Text;
import scala.xml.TopScope;
import scala.xml.UnprefixedAttribute;
import scala.xml.Null.;

@ScalaSignature(
   bytes = "\u0006\u0001\u001d3Q!\u0001\u0002\u0001\t1\u0011Q\"\u0012=fGV$xN]:QC\u001e,'BA\u0002\u0005\u0003\u0011)\u00070Z2\u000b\u0005\u00151\u0011AA;j\u0015\t9\u0001\"A\u0003ta\u0006\u00148N\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<7C\u0001\u0001\u000e!\tqq\"D\u0001\u0005\u0013\t\u0001BAA\u0005XK\n,\u0016\nU1hK\"A!\u0003\u0001B\u0001B\u0003%A#\u0001\u0004qCJ,g\u000e^\u0002\u0001!\tqQ#\u0003\u0002\u0017\t\tQ1\u000b]1sWVKE+\u00192\t\u0011a\u0001!\u0011!Q\u0001\ne\t\u0011\u0003\u001e5sK\u0006$G)^7q\u000b:\f'\r\\3e!\tQR$D\u0001\u001c\u0015\u0005a\u0012!B:dC2\f\u0017B\u0001\u0010\u001c\u0005\u001d\u0011un\u001c7fC:DQ\u0001\t\u0001\u0005\u0002\u0005\na\u0001P5oSRtDc\u0001\u0012%KA\u00111\u0005A\u0007\u0002\u0005!)!c\ba\u0001)!)\u0001d\ba\u00013!)q\u0005\u0001C\u0001Q\u00051!/\u001a8eKJ$\"!K\u001e\u0011\u0007)\u0012TG\u0004\u0002,a9\u0011AfL\u0007\u0002[)\u0011afE\u0001\u0007yI|w\u000e\u001e \n\u0003qI!!M\u000e\u0002\u000fA\f7m[1hK&\u00111\u0007\u000e\u0002\u0004'\u0016\f(BA\u0019\u001c!\t1\u0014(D\u00018\u0015\tA4$A\u0002y[2L!AO\u001c\u0003\t9{G-\u001a\u0005\u0006y\u0019\u0002\r!P\u0001\be\u0016\fX/Z:u!\tqT)D\u0001@\u0015\t\u0001\u0015)\u0001\u0003iiR\u0004(B\u0001\"D\u0003\u001d\u0019XM\u001d<mKRT\u0011\u0001R\u0001\u0006U\u00064\u0018\r_\u0005\u0003\r~\u0012!\u0003\u0013;uaN+'O\u001e7fiJ+\u0017/^3ti\u0002"
)
public class ExecutorsPage extends WebUIPage {
   private final SparkUITab parent;
   private final boolean threadDumpEnabled;

   public Seq<Node> render(HttpServletRequest request) {
      Null var10004 = .MODULE$;
      TopScope var10005 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      MetaData $md = .MODULE$;
      MetaData var15 = new UnprefixedAttribute("class", new Text("row-fluid"), $md);
      var15 = new UnprefixedAttribute("id", new Text("active-executors"), var15);
      Elem var10008 = new Elem((String)null, "div", var15, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0])));
      MetaData $md = .MODULE$;
      MetaData var17 = new UnprefixedAttribute("src", UIUtils$.MODULE$.prependBaseUri("/static/utils.js", UIUtils$.MODULE$.prependBaseUri$default$2()), $md);
      TraversableLike var19 = (TraversableLike)var10008.$plus$plus(new Elem((String)null, "script", var17, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))), scala.xml.NodeSeq..MODULE$.canBuildFrom());
      MetaData $md = .MODULE$;
      MetaData var18 = new UnprefixedAttribute("src", UIUtils$.MODULE$.prependBaseUri("/static/executorspage.js", UIUtils$.MODULE$.prependBaseUri$default$2()), $md);
      var19 = (TraversableLike)var19.$plus$plus(new Elem((String)null, "script", var18, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))), scala.xml.NodeSeq..MODULE$.canBuildFrom());
      Null var10013 = .MODULE$;
      TopScope var10014 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("setThreadDumpEnabled("));
      $buf.$amp$plus(BoxesRunTime.boxToBoolean(this.threadDumpEnabled));
      $buf.$amp$plus(new Text(")"));
      $buf.$amp$plus(var19.$plus$plus(new Elem((String)null, "script", var10013, var10014, false, $buf), scala.xml.NodeSeq..MODULE$.canBuildFrom()));
      $buf.$amp$plus(new Text("\n      "));
      Elem content = new Elem((String)null, "div", var10004, var10005, false, $buf);
      String x$1 = "Executors";
      Function0 x$2 = new Serializable(this, content) {
         public static final long serialVersionUID = 0L;
         private final Elem content$1;

         public final Elem apply() {
            return this.content$1;
         }

         public {
            this.content$1 = content$1;
         }
      };
      SparkUITab x$3 = this.parent;
      boolean x$4 = true;
      Option x$5 = UIUtils$.MODULE$.headerSparkPage$default$4();
      Option x$6 = UIUtils$.MODULE$.headerSparkPage$default$5();
      boolean x$7 = UIUtils$.MODULE$.headerSparkPage$default$6();
      return UIUtils$.MODULE$.headerSparkPage(x$1, x$2, x$3, x$5, x$6, x$7, x$4);
   }

   public ExecutorsPage(SparkUITab parent, boolean threadDumpEnabled) {
      super("");
      this.parent = parent;
      this.threadDumpEnabled = threadDumpEnabled;
   }
}
