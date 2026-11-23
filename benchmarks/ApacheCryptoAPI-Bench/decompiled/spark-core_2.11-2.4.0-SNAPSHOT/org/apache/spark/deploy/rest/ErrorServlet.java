package org.apache.spark.deploy.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.immutable.$colon$colon;
import scala.collection.immutable.List;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001u2A!\u0001\u0002\u0005\u001b\taQI\u001d:peN+'O\u001e7fi*\u00111\u0001B\u0001\u0005e\u0016\u001cHO\u0003\u0002\u0006\r\u00051A-\u001a9m_fT!a\u0002\u0005\u0002\u000bM\u0004\u0018M]6\u000b\u0005%Q\u0011AB1qC\u000eDWMC\u0001\f\u0003\ry'oZ\u0002\u0001'\t\u0001a\u0002\u0005\u0002\u0010!5\t!!\u0003\u0002\u0012\u0005\tY!+Z:u'\u0016\u0014h\u000f\\3u\u0011\u0015\u0019\u0002\u0001\"\u0001\u0015\u0003\u0019a\u0014N\\5u}Q\tQ\u0003\u0005\u0002\u0010\u0001!9q\u0003\u0001b\u0001\n\u0013A\u0012!D:feZ,'OV3sg&|g.F\u0001\u001a!\tQr$D\u0001\u001c\u0015\taR$\u0001\u0003mC:<'\"\u0001\u0010\u0002\t)\fg/Y\u0005\u0003Am\u0011aa\u0015;sS:<\u0007B\u0002\u0012\u0001A\u0003%\u0011$\u0001\btKJ4XM\u001d,feNLwN\u001c\u0011\t\u000b\u0011\u0002A\u0011K\u0013\u0002\u000fM,'O^5dKR\u0019a\u0005\f\u001d\u0011\u0005\u001dRS\"\u0001\u0015\u000b\u0003%\nQa]2bY\u0006L!a\u000b\u0015\u0003\tUs\u0017\u000e\u001e\u0005\u0006[\r\u0002\rAL\u0001\be\u0016\fX/Z:u!\tyc'D\u00011\u0015\t\t$'\u0001\u0003iiR\u0004(BA\u001a5\u0003\u001d\u0019XM\u001d<mKRT\u0011!N\u0001\u0006U\u00064\u0018\r_\u0005\u0003oA\u0012!\u0003\u0013;uaN+'O\u001e7fiJ+\u0017/^3ti\")\u0011h\ta\u0001u\u0005A!/Z:q_:\u001cX\r\u0005\u00020w%\u0011A\b\r\u0002\u0014\u0011R$\boU3sm2,GOU3ta>t7/\u001a"
)
public class ErrorServlet extends RestServlet {
   private final String serverVersion;

   private String serverVersion() {
      return this.serverVersion;
   }

   public void service(HttpServletRequest request, HttpServletResponse response) {
      String path = request.getPathInfo();
      List parts = Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps((Object[])(new StringOps(Predef$.MODULE$.augmentString(path))).stripPrefix("/").split("/")).filter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(String x$5) {
            return (new StringOps(Predef$.MODULE$.augmentString(x$5))).nonEmpty();
         }
      })).toList();
      boolean versionMismatch = false;
      boolean var7 = false;
      $colon$colon var8 = null;
      String msg;
      if (Nil$.MODULE$.equals(parts)) {
         msg = "Missing protocol version.";
      } else {
         label53: {
            if (parts instanceof $colon$colon) {
               label47: {
                  var7 = true;
                  var8 = ($colon$colon)parts;
                  String var11 = (String)var8.head();
                  List var12 = var8.tl$1();
                  String var10000 = this.serverVersion();
                  if (var10000 == null) {
                     if (var11 != null) {
                        break label47;
                     }
                  } else if (!var10000.equals(var11)) {
                     break label47;
                  }

                  if (Nil$.MODULE$.equals(var12)) {
                     msg = "Missing the /submissions prefix.";
                     break label53;
                  }
               }
            }

            if (var7) {
               label38: {
                  String var14 = (String)var8.head();
                  List var15 = var8.tl$1();
                  String var21 = this.serverVersion();
                  if (var21 == null) {
                     if (var14 != null) {
                        break label38;
                     }
                  } else if (!var21.equals(var14)) {
                     break label38;
                  }

                  if (var15 instanceof $colon$colon) {
                     $colon$colon var17 = ($colon$colon)var15;
                     String var18 = (String)var17.head();
                     if ("submissions".equals(var18)) {
                        msg = "Missing an action: please specify one of /create, /kill, or /status.";
                        break label53;
                     }
                  }
               }
            }

            if (var7) {
               String unknownVersion = (String)var8.head();
               versionMismatch = true;
               msg = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Unknown protocol version '", "'."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{unknownVersion}));
            } else {
               msg = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Malformed path ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{path}));
            }
         }
      }

      String msg = (new StringBuilder()).append((Object)msg).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" Please submit requests through http://[host]:[port]/", "/submissions/..."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.serverVersion()}))).toString();
      ErrorResponse error = this.handleError(msg);
      if (versionMismatch) {
         error.highestProtocolVersion_$eq(this.serverVersion());
         response.setStatus(RestSubmissionServer$.MODULE$.SC_UNKNOWN_PROTOCOL_VERSION());
      } else {
         response.setStatus(400);
      }

      this.sendResponse(error, response);
   }

   public ErrorServlet() {
      this.serverVersion = RestSubmissionServer$.MODULE$.PROTOCOL_VERSION();
   }
}
