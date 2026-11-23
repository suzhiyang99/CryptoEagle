package org.apache.spark.status.api.v1;

import java.util.zip.ZipOutputStream;
import org.apache.spark.SecurityManager;
import org.apache.spark.ui.SparkUI;
import scala.Function1;
import scala.Option;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001U4\u0001\"\u0001\u0002\u0011\u0002\u0007\u0005\u0001B\u0004\u0002\u0007+&\u0013vn\u001c;\u000b\u0005\r!\u0011A\u0001<2\u0015\t)a!A\u0002ba&T!a\u0002\u0005\u0002\rM$\u0018\r^;t\u0015\tI!\"A\u0003ta\u0006\u00148N\u0003\u0002\f\u0019\u00051\u0011\r]1dQ\u0016T\u0011!D\u0001\u0004_J<7C\u0001\u0001\u0010!\t\u00012#D\u0001\u0012\u0015\u0005\u0011\u0012!B:dC2\f\u0017B\u0001\u000b\u0012\u0005\u0019\te.\u001f*fM\")a\u0003\u0001C\u00011\u00051A%\u001b8ji\u0012\u001a\u0001\u0001F\u0001\u001a!\t\u0001\"$\u0003\u0002\u001c#\t!QK\\5u\u0011\u0015i\u0002A\"\u0001\u001f\u0003-9\u0018\u000e\u001e5Ta\u0006\u00148.V%\u0016\u0005}\u0019Cc\u0001\u00118\u0001R\u0011\u0011\u0005\f\t\u0003E\rb\u0001\u0001B\u0003%9\t\u0007QEA\u0001U#\t1\u0013\u0006\u0005\u0002\u0011O%\u0011\u0001&\u0005\u0002\b\u001d>$\b.\u001b8h!\t\u0001\"&\u0003\u0002,#\t\u0019\u0011I\\=\t\u000b5b\u0002\u0019\u0001\u0018\u0002\u0005\u0019t\u0007\u0003\u0002\t0c\u0005J!\u0001M\t\u0003\u0013\u0019+hn\u0019;j_:\f\u0004C\u0001\u001a6\u001b\u0005\u0019$B\u0001\u001b\t\u0003\t)\u0018.\u0003\u00027g\t91\u000b]1sWVK\u0005\"\u0002\u001d\u001d\u0001\u0004I\u0014!B1qa&#\u0007C\u0001\u001e>\u001d\t\u00012(\u0003\u0002=#\u00051\u0001K]3eK\u001aL!AP \u0003\rM#(/\u001b8h\u0015\ta\u0014\u0003C\u0003B9\u0001\u0007!)A\u0005biR,W\u000e\u001d;JIB\u0019\u0001cQ\u001d\n\u0005\u0011\u000b\"AB(qi&|g\u000eC\u0003G\u0001\u0019\u0005q)\u0001\fhKR\f\u0005\u000f\u001d7jG\u0006$\u0018n\u001c8J]\u001a|G*[:u+\u0005A\u0005cA%R):\u0011!j\u0014\b\u0003\u0017:k\u0011\u0001\u0014\u0006\u0003\u001b^\ta\u0001\u0010:p_Rt\u0014\"\u0001\n\n\u0005A\u000b\u0012a\u00029bG.\fw-Z\u0005\u0003%N\u0013\u0001\"\u0013;fe\u0006$xN\u001d\u0006\u0003!F\u0001\"!\u0016,\u000e\u0003\tI!a\u0016\u0002\u0003\u001f\u0005\u0003\b\u000f\\5dCRLwN\\%oM>DQ!\u0017\u0001\u0007\u0002i\u000b!cZ3u\u0003B\u0004H.[2bi&|g.\u00138g_R\u00111\f\u0018\t\u0004!\r#\u0006\"\u0002\u001dY\u0001\u0004I\u0004\"\u00020\u0001\t\u0003y\u0016AD<sSR,WI^3oi2{wm\u001d\u000b\u00053\u0001\f'\rC\u00039;\u0002\u0007\u0011\bC\u0003B;\u0002\u0007!\tC\u0003d;\u0002\u0007A-A\u0005{SB\u001cFO]3b[B\u0011Q\r\\\u0007\u0002M*\u0011q\r[\u0001\u0004u&\u0004(BA5k\u0003\u0011)H/\u001b7\u000b\u0003-\fAA[1wC&\u0011QN\u001a\u0002\u00105&\u0004x*\u001e;qkR\u001cFO]3b[\")q\u000e\u0001D\u0001a\u0006y1/Z2ve&$\u00180T1oC\u001e,'/F\u0001r!\t\u00118/D\u0001\t\u0013\t!\bBA\bTK\u000e,(/\u001b;z\u001b\u0006t\u0017mZ3s\u0001"
)
public interface UIRoot {
   <T> T withSparkUI(String var1, Option<String> var2, Function1<SparkUI, T> var3);

   Iterator<ApplicationInfo> getApplicationInfoList();

   Option<ApplicationInfo> getApplicationInfo(String var1);

   void writeEventLogs(String var1, Option<String> var2, ZipOutputStream var3);

   SecurityManager securityManager();
}
