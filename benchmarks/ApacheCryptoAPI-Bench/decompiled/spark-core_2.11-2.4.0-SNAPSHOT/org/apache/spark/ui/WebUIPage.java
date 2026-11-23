package org.apache.spark.ui;

import javax.servlet.http.HttpServletRequest;
import org.json4s.JsonAST;
import org.json4s.JsonAST.JNothing.;
import scala.collection.Seq;
import scala.reflect.ScalaSignature;
import scala.xml.Node;

@ScalaSignature(
   bytes = "\u0006\u0001y3a!\u0001\u0002\u0002\u0002\u0011Q!!C,fEVK\u0005+Y4f\u0015\t\u0019A!\u0001\u0002vS*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xm\u0005\u0002\u0001\u0017A\u0011AbD\u0007\u0002\u001b)\ta\"A\u0003tG\u0006d\u0017-\u0003\u0002\u0011\u001b\t1\u0011I\\=SK\u001aD\u0001B\u0005\u0001\u0003\u0002\u0004%\t\u0001F\u0001\u0007aJ,g-\u001b=\u0004\u0001U\tQ\u0003\u0005\u0002\u001739\u0011AbF\u0005\u000315\ta\u0001\u0015:fI\u00164\u0017B\u0001\u000e\u001c\u0005\u0019\u0019FO]5oO*\u0011\u0001$\u0004\u0005\t;\u0001\u0011\t\u0019!C\u0001=\u0005Q\u0001O]3gSb|F%Z9\u0015\u0005}\u0011\u0003C\u0001\u0007!\u0013\t\tSB\u0001\u0003V]&$\bbB\u0012\u001d\u0003\u0003\u0005\r!F\u0001\u0004q\u0012\n\u0004\u0002C\u0013\u0001\u0005\u0003\u0005\u000b\u0015B\u000b\u0002\u000fA\u0014XMZ5yA!)q\u0005\u0001C\u0001Q\u00051A(\u001b8jiz\"\"!K\u0016\u0011\u0005)\u0002Q\"\u0001\u0002\t\u000bI1\u0003\u0019A\u000b\t\u000b5\u0002a\u0011\u0001\u0018\u0002\rI,g\u000eZ3s)\ty\u0013\tE\u00021qmr!!\r\u001c\u000f\u0005I*T\"A\u001a\u000b\u0005Q\u001a\u0012A\u0002\u001fs_>$h(C\u0001\u000f\u0013\t9T\"A\u0004qC\u000e\\\u0017mZ3\n\u0005eR$aA*fc*\u0011q'\u0004\t\u0003y}j\u0011!\u0010\u0006\u0003}5\t1\u0001_7m\u0013\t\u0001UH\u0001\u0003O_\u0012,\u0007\"\u0002\"-\u0001\u0004\u0019\u0015a\u0002:fcV,7\u000f\u001e\t\u0003\t.k\u0011!\u0012\u0006\u0003\r\u001e\u000bA\u0001\u001b;ua*\u0011\u0001*S\u0001\bg\u0016\u0014h\u000f\\3u\u0015\u0005Q\u0015!\u00026bm\u0006D\u0018B\u0001'F\u0005IAE\u000f\u001e9TKJ4H.\u001a;SKF,Xm\u001d;\t\u000b9\u0003A\u0011A(\u0002\u0015I,g\u000eZ3s\u0015N|g\u000e\u0006\u0002Q;B\u0011\u0011K\u0017\b\u0003%^s!aU+\u000f\u0005I\"\u0016\"A\u0005\n\u0005YC\u0011A\u00026t_:$4/\u0003\u0002Y3\u00069!j]8o\u0003N#&B\u0001,\t\u0013\tYFL\u0001\u0004K-\u0006dW/\u001a\u0006\u00031fCQAQ'A\u0002\r\u0003"
)
public abstract class WebUIPage {
   private String prefix;

   public String prefix() {
      return this.prefix;
   }

   public void prefix_$eq(String x$1) {
      this.prefix = x$1;
   }

   public abstract Seq<Node> render(HttpServletRequest var1);

   public JsonAST.JValue renderJson(HttpServletRequest request) {
      return .MODULE$;
   }

   public WebUIPage(String prefix) {
      this.prefix = prefix;
      super();
   }
}
