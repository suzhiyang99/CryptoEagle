package org.apache.spark.ui.env;

import javax.servlet.http.HttpServletRequest;
import org.apache.spark.SparkConf;
import org.apache.spark.status.AppStatusStore;
import org.apache.spark.status.api.v1.ApplicationEnvironmentInfo;
import org.apache.spark.ui.UIUtils$;
import org.apache.spark.ui.WebUIPage;
import org.apache.spark.util.Utils$;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.immutable.Map;
import scala.reflect.ScalaSignature;
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
   bytes = "\u0006\u0001u4Q!\u0001\u0002\u0001\t1\u0011q\"\u00128wSJ|g.\\3oiB\u000bw-\u001a\u0006\u0003\u0007\u0011\t1!\u001a8w\u0015\t)a!\u0001\u0002vS*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014xm\u0005\u0002\u0001\u001bA\u0011abD\u0007\u0002\t%\u0011\u0001\u0003\u0002\u0002\n/\u0016\u0014W+\u0013)bO\u0016D\u0001B\u0005\u0001\u0003\u0002\u0003\u0006I\u0001F\u0001\u0007a\u0006\u0014XM\u001c;\u0004\u0001A\u0011QCF\u0007\u0002\u0005%\u0011qC\u0001\u0002\u000f\u000b:4\u0018N]8o[\u0016tG\u000fV1c\u0011!I\u0002A!A!\u0002\u0013Q\u0012\u0001B2p]\u001a\u0004\"a\u0007\u000f\u000e\u0003\u0019I!!\b\u0004\u0003\u0013M\u0003\u0018M]6D_:4\u0007\u0002C\u0010\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u0011\u0002\u000bM$xN]3\u0011\u0005\u0005\"S\"\u0001\u0012\u000b\u0005\r2\u0011AB:uCR,8/\u0003\u0002&E\tq\u0011\t\u001d9Ti\u0006$Xo]*u_J,\u0007\"B\u0014\u0001\t\u0003A\u0013A\u0002\u001fj]&$h\b\u0006\u0003*U-b\u0003CA\u000b\u0001\u0011\u0015\u0011b\u00051\u0001\u0015\u0011\u0015Ib\u00051\u0001\u001b\u0011\u0015yb\u00051\u0001!\u0011\u0015q\u0003\u0001\"\u00010\u0003\u0019\u0011XM\u001c3feR\u0011\u0001\u0007\u0012\t\u0004cmrdB\u0001\u001a9\u001d\t\u0019d'D\u00015\u0015\t)4#\u0001\u0004=e>|GOP\u0005\u0002o\u0005)1oY1mC&\u0011\u0011HO\u0001\ba\u0006\u001c7.Y4f\u0015\u00059\u0014B\u0001\u001f>\u0005\r\u0019V-\u001d\u0006\u0003si\u0002\"a\u0010\"\u000e\u0003\u0001S!!\u0011\u001e\u0002\u0007alG.\u0003\u0002D\u0001\n!aj\u001c3f\u0011\u0015)U\u00061\u0001G\u0003\u001d\u0011X-];fgR\u0004\"a\u0012(\u000e\u0003!S!!\u0013&\u0002\t!$H\u000f\u001d\u0006\u0003\u00172\u000bqa]3sm2,GOC\u0001N\u0003\u0015Q\u0017M^1y\u0013\ty\u0005J\u0001\nIiR\u00048+\u001a:wY\u0016$(+Z9vKN$\b\"B)\u0001\t\u0013\u0011\u0016A\u00049s_B,'\u000f^=IK\u0006$WM]\u000b\u0002'B\u0019Ak\u0016-\u000e\u0003US!A\u0016\u001e\u0002\u0015\r|G\u000e\\3di&|g.\u0003\u0002=+B\u0011\u0011LX\u0007\u00025*\u00111\fX\u0001\u0005Y\u0006twMC\u0001^\u0003\u0011Q\u0017M^1\n\u0005}S&AB*ue&tw\rC\u0003b\u0001\u0011%!+\u0001\tdY\u0006\u001c8\u000fU1uQ\"+\u0017\rZ3sg\")1\r\u0001C\u0005I\u00061!N^7S_^$\"!\u001a5\u0011\u0005}2\u0017BA4A\u0005\u0011)E.Z7\t\u000b%\u0014\u0007\u0019\u00016\u0002\u0005-4\b\u0003B6m]:l\u0011AO\u0005\u0003[j\u0012a\u0001V;qY\u0016\u0014\u0004CA8s\u001d\tY\u0007/\u0003\u0002ru\u00051\u0001K]3eK\u001aL!aX:\u000b\u0005ET\u0004\"B;\u0001\t\u00131\u0018a\u00039s_B,'\u000f^=S_^$\"!Z<\t\u000b%$\b\u0019\u00016\t\u000be\u0004A\u0011\u0002>\u0002\u0019\rd\u0017m]:QCRD'k\\<\u0015\u0005\u0015\\\b\"\u0002?y\u0001\u0004Q\u0017\u0001\u00023bi\u0006\u0004"
)
public class EnvironmentPage extends WebUIPage {
   private final EnvironmentTab parent;
   private final SparkConf conf;
   private final AppStatusStore store;

   public Seq<Node> render(HttpServletRequest request) {
      ApplicationEnvironmentInfo appEnv = this.store.environmentInfo();
      Map jvmInformation = (Map)Predef$.MODULE$.Map().apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Java Version"), appEnv.runtime().javaVersion()), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Java Home"), appEnv.runtime().javaHome()), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Scala Version"), appEnv.runtime().scalaVersion())}));
      Seq runtimeInformationTable = UIUtils$.MODULE$.listingTable(this.propertyHeader(), new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final EnvironmentPage $outer;

         public final Elem apply(Tuple2<String, String> kv) {
            return this.$outer.org$apache$spark$ui$env$EnvironmentPage$$jvmRow(kv);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, jvmInformation, true, UIUtils$.MODULE$.listingTable$default$5(), UIUtils$.MODULE$.listingTable$default$6(), UIUtils$.MODULE$.listingTable$default$7(), UIUtils$.MODULE$.listingTable$default$8());
      Seq sparkPropertiesTable = UIUtils$.MODULE$.listingTable(this.propertyHeader(), new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final EnvironmentPage $outer;

         public final Elem apply(Tuple2<String, String> kv) {
            return this.$outer.org$apache$spark$ui$env$EnvironmentPage$$propertyRow(kv);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, Utils$.MODULE$.redact(this.conf, appEnv.sparkProperties().toSeq()), true, UIUtils$.MODULE$.listingTable$default$5(), UIUtils$.MODULE$.listingTable$default$6(), UIUtils$.MODULE$.listingTable$default$7(), UIUtils$.MODULE$.listingTable$default$8());
      Seq systemPropertiesTable = UIUtils$.MODULE$.listingTable(this.propertyHeader(), new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final EnvironmentPage $outer;

         public final Elem apply(Tuple2<String, String> kv) {
            return this.$outer.org$apache$spark$ui$env$EnvironmentPage$$propertyRow(kv);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, appEnv.systemProperties(), true, UIUtils$.MODULE$.listingTable$default$5(), UIUtils$.MODULE$.listingTable$default$6(), UIUtils$.MODULE$.listingTable$default$7(), UIUtils$.MODULE$.listingTable$default$8());
      Seq classpathEntriesTable = UIUtils$.MODULE$.listingTable(this.classPathHeaders(), new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final EnvironmentPage $outer;

         public final Elem apply(Tuple2<String, String> data) {
            return this.$outer.org$apache$spark$ui$env$EnvironmentPage$$classPathRow(data);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, appEnv.classpathEntries(), true, UIUtils$.MODULE$.listingTable$default$5(), UIUtils$.MODULE$.listingTable$default$6(), UIUtils$.MODULE$.listingTable$default$7(), UIUtils$.MODULE$.listingTable$default$8());
      Null var10004 = .MODULE$;
      TopScope var10005 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      MetaData $md = .MODULE$;
      MetaData var38 = new UnprefixedAttribute("onClick", new Text("collapseTable('collapse-aggregated-runtimeInformation',\n            'aggregated-runtimeInformation')"), $md);
      var38 = new UnprefixedAttribute("class", new Text("collapse-aggregated-runtimeInformation collapse-table"), var38);
      TopScope var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n          "));
      Null var10020 = .MODULE$;
      TopScope var10021 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n            "));
      MetaData $md = .MODULE$;
      MetaData var40 = new UnprefixedAttribute("class", new Text("collapse-table-arrow arrow-open"), $md);
      $buf.$amp$plus(new Elem((String)null, "span", var40, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      $buf.$amp$plus(new Text("\n            "));
      Null var10028 = .MODULE$;
      TopScope var10029 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Runtime Information"));
      $buf.$amp$plus(new Elem((String)null, "a", var10028, var10029, false, $buf));
      $buf.$amp$plus(new Text("\n          "));
      $buf.$amp$plus(new Elem((String)null, "h4", var10020, var10021, false, $buf));
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(new Elem((String)null, "span", var38, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n        "));
      MetaData $md = .MODULE$;
      MetaData var41 = new UnprefixedAttribute("class", new Text("aggregated-runtimeInformation collapsible-table"), $md);
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n          "));
      $buf.$amp$plus(runtimeInformationTable);
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(new Elem((String)null, "div", var41, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n        "));
      MetaData $md = .MODULE$;
      MetaData var42 = new UnprefixedAttribute("onClick", new Text("collapseTable('collapse-aggregated-sparkProperties',\n            'aggregated-sparkProperties')"), $md);
      var42 = new UnprefixedAttribute("class", new Text("collapse-aggregated-sparkProperties collapse-table"), var42);
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n          "));
      var10020 = .MODULE$;
      var10021 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n            "));
      MetaData $md = .MODULE$;
      MetaData var44 = new UnprefixedAttribute("class", new Text("collapse-table-arrow arrow-open"), $md);
      $buf.$amp$plus(new Elem((String)null, "span", var44, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      $buf.$amp$plus(new Text("\n            "));
      var10028 = .MODULE$;
      var10029 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Spark Properties"));
      $buf.$amp$plus(new Elem((String)null, "a", var10028, var10029, false, $buf));
      $buf.$amp$plus(new Text("\n          "));
      $buf.$amp$plus(new Elem((String)null, "h4", var10020, var10021, false, $buf));
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(new Elem((String)null, "span", var42, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n        "));
      MetaData $md = .MODULE$;
      MetaData var45 = new UnprefixedAttribute("class", new Text("aggregated-sparkProperties collapsible-table"), $md);
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n          "));
      $buf.$amp$plus(sparkPropertiesTable);
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(new Elem((String)null, "div", var45, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n        "));
      MetaData $md = .MODULE$;
      MetaData var46 = new UnprefixedAttribute("onClick", new Text("collapseTable('collapse-aggregated-systemProperties',\n            'aggregated-systemProperties')"), $md);
      var46 = new UnprefixedAttribute("class", new Text("collapse-aggregated-systemProperties collapse-table"), var46);
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n          "));
      var10020 = .MODULE$;
      var10021 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n            "));
      MetaData $md = .MODULE$;
      MetaData var48 = new UnprefixedAttribute("class", new Text("collapse-table-arrow arrow-open"), $md);
      $buf.$amp$plus(new Elem((String)null, "span", var48, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      $buf.$amp$plus(new Text("\n            "));
      var10028 = .MODULE$;
      var10029 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("System Properties"));
      $buf.$amp$plus(new Elem((String)null, "a", var10028, var10029, false, $buf));
      $buf.$amp$plus(new Text("\n          "));
      $buf.$amp$plus(new Elem((String)null, "h4", var10020, var10021, false, $buf));
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(new Elem((String)null, "span", var46, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n        "));
      MetaData $md = .MODULE$;
      MetaData var49 = new UnprefixedAttribute("class", new Text("aggregated-systemProperties collapsible-table"), $md);
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n          "));
      $buf.$amp$plus(systemPropertiesTable);
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(new Elem((String)null, "div", var49, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n        "));
      MetaData $md = .MODULE$;
      MetaData var50 = new UnprefixedAttribute("onClick", new Text("collapseTable('collapse-aggregated-classpathEntries',\n            'aggregated-classpathEntries')"), $md);
      var50 = new UnprefixedAttribute("class", new Text("collapse-aggregated-classpathEntries collapse-table"), var50);
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n          "));
      var10020 = .MODULE$;
      var10021 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n            "));
      MetaData $md = .MODULE$;
      MetaData var52 = new UnprefixedAttribute("class", new Text("collapse-table-arrow arrow-open"), $md);
      $buf.$amp$plus(new Elem((String)null, "span", var52, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      $buf.$amp$plus(new Text("\n            "));
      var10028 = .MODULE$;
      var10029 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Classpath Entries"));
      $buf.$amp$plus(new Elem((String)null, "a", var10028, var10029, false, $buf));
      $buf.$amp$plus(new Text("\n          "));
      $buf.$amp$plus(new Elem((String)null, "h4", var10020, var10021, false, $buf));
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(new Elem((String)null, "span", var50, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n        "));
      MetaData $md = .MODULE$;
      MetaData var53 = new UnprefixedAttribute("class", new Text("aggregated-classpathEntries collapsible-table"), $md);
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n          "));
      $buf.$amp$plus(classpathEntriesTable);
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(new Elem((String)null, "div", var53, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      Elem content = new Elem((String)null, "span", var10004, var10005, false, $buf);
      return UIUtils$.MODULE$.headerSparkPage("Environment", new Serializable(this, content) {
         public static final long serialVersionUID = 0L;
         private final Elem content$1;

         public final Elem apply() {
            return this.content$1;
         }

         public {
            this.content$1 = content$1;
         }
      }, this.parent, UIUtils$.MODULE$.headerSparkPage$default$4(), UIUtils$.MODULE$.headerSparkPage$default$5(), UIUtils$.MODULE$.headerSparkPage$default$6(), UIUtils$.MODULE$.headerSparkPage$default$7());
   }

   private Seq<String> propertyHeader() {
      return (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Name", "Value"})));
   }

   private Seq<String> classPathHeaders() {
      return (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Resource", "Source"})));
   }

   public Elem org$apache$spark$ui$env$EnvironmentPage$$jvmRow(Tuple2<String, String> kv) {
      Null var10004 = .MODULE$;
      TopScope var10005 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      Null var10012 = .MODULE$;
      TopScope var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(kv._1());
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      var10012 = .MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(kv._2());
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      return new Elem((String)null, "tr", var10004, var10005, false, $buf);
   }

   public Elem org$apache$spark$ui$env$EnvironmentPage$$propertyRow(Tuple2<String, String> kv) {
      Null var10004 = .MODULE$;
      TopScope var10005 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      Null var10012 = .MODULE$;
      TopScope var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(kv._1());
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      var10012 = .MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(kv._2());
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      return new Elem((String)null, "tr", var10004, var10005, false, $buf);
   }

   public Elem org$apache$spark$ui$env$EnvironmentPage$$classPathRow(Tuple2<String, String> data) {
      Null var10004 = .MODULE$;
      TopScope var10005 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      Null var10012 = .MODULE$;
      TopScope var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(data._1());
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      var10012 = .MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(data._2());
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      return new Elem((String)null, "tr", var10004, var10005, false, $buf);
   }

   public EnvironmentPage(EnvironmentTab parent, SparkConf conf, AppStatusStore store) {
      super("");
      this.parent = parent;
      this.conf = conf;
      this.store = store;
   }
}
