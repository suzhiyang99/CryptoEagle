package org.apache.spark.ui;

import scala.Predef$;
import scala.collection.Seq;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.ArrayBuffer$;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001]3a!\u0001\u0002\u0002\u0002\u0011Q!\u0001C,fEVKE+\u00192\u000b\u0005\r!\u0011AA;j\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7C\u0001\u0001\f!\taq\"D\u0001\u000e\u0015\u0005q\u0011!B:dC2\f\u0017B\u0001\t\u000e\u0005\u0019\te.\u001f*fM\"A!\u0003\u0001B\u0001B\u0003%A#\u0001\u0004qCJ,g\u000e^\u0002\u0001!\t)b#D\u0001\u0003\u0013\t9\"AA\u0003XK\n,\u0016\n\u0003\u0005\u001a\u0001\t\u0015\r\u0011\"\u0001\u001b\u0003\u0019\u0001(/\u001a4jqV\t1\u0004\u0005\u0002\u001d?9\u0011A\"H\u0005\u0003=5\ta\u0001\u0015:fI\u00164\u0017B\u0001\u0011\"\u0005\u0019\u0019FO]5oO*\u0011a$\u0004\u0005\tG\u0001\u0011\t\u0011)A\u00057\u00059\u0001O]3gSb\u0004\u0003\"B\u0013\u0001\t\u00031\u0013A\u0002\u001fj]&$h\bF\u0002(Q%\u0002\"!\u0006\u0001\t\u000bI!\u0003\u0019\u0001\u000b\t\u000be!\u0003\u0019A\u000e\t\u000f-\u0002!\u0019!C\u0001Y\u0005)\u0001/Y4fgV\tQ\u0006E\u0002/gUj\u0011a\f\u0006\u0003aE\nq!\\;uC\ndWM\u0003\u00023\u001b\u0005Q1m\u001c7mK\u000e$\u0018n\u001c8\n\u0005Qz#aC!se\u0006L()\u001e4gKJ\u0004\"!\u0006\u001c\n\u0005]\u0012!!C,fEVK\u0005+Y4f\u0011\u0019I\u0004\u0001)A\u0005[\u00051\u0001/Y4fg\u0002Bqa\u000f\u0001C\u0002\u0013\u0005!$\u0001\u0003oC6,\u0007BB\u001f\u0001A\u0003%1$A\u0003oC6,\u0007\u0005C\u0003@\u0001\u0011\u0005\u0001)\u0001\u0006biR\f7\r\u001b)bO\u0016$\"!\u0011#\u0011\u00051\u0011\u0015BA\"\u000e\u0005\u0011)f.\u001b;\t\u000b\u0015s\u0004\u0019A\u001b\u0002\tA\fw-\u001a\u0005\u0006\u000f\u0002!\t\u0001S\u0001\u000bQ\u0016\fG-\u001a:UC\n\u001cX#A%\u0011\u0007)\u0013vE\u0004\u0002L!:\u0011AjT\u0007\u0002\u001b*\u0011ajE\u0001\u0007yI|w\u000e\u001e \n\u00039I!!U\u0007\u0002\u000fA\f7m[1hK&\u00111\u000b\u0016\u0002\u0004'\u0016\f(BA)\u000e\u0011\u00151\u0006\u0001\"\u0001\u001b\u0003!\u0011\u0017m]3QCRD\u0007"
)
public abstract class WebUITab {
   private final WebUI parent;
   private final String prefix;
   private final ArrayBuffer<WebUIPage> pages;
   private final String name;

   public String prefix() {
      return this.prefix;
   }

   public ArrayBuffer<WebUIPage> pages() {
      return this.pages;
   }

   public String name() {
      return this.name;
   }

   public void attachPage(WebUIPage page) {
      page.prefix_$eq((new StringOps(Predef$.MODULE$.augmentString((new StringBuilder()).append((Object)this.prefix()).append((Object)"/").append((Object)page.prefix()).toString()))).stripSuffix("/"));
      this.pages().$plus$eq(page);
   }

   public Seq<WebUITab> headerTabs() {
      return this.parent.getTabs();
   }

   public String basePath() {
      return this.parent.getBasePath();
   }

   public WebUITab(WebUI parent, String prefix) {
      this.parent = parent;
      this.prefix = prefix;
      this.pages = (ArrayBuffer)ArrayBuffer$.MODULE$.apply(Nil$.MODULE$);
      this.name = (new StringOps(Predef$.MODULE$.augmentString(prefix))).capitalize();
   }
}
