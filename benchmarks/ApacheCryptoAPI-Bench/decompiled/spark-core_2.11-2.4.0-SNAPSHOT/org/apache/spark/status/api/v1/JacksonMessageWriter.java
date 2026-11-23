package org.apache.spark.status.api.v1;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.scala.DefaultScalaModule.;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;

@Provider
@Produces({"application/json"})
@ScalaSignature(
   bytes = "\u0006\u0001\u0005%e!B\u0001\u0003\u0001\tq!\u0001\u0006&bG.\u001cxN\\'fgN\fw-Z,sSR,'O\u0003\u0002\u0004\t\u0005\u0011a/\r\u0006\u0003\u000b\u0019\t1!\u00199j\u0015\t9\u0001\"\u0001\u0004ti\u0006$Xo\u001d\u0006\u0003\u0013)\tQa\u001d9be.T!a\u0003\u0007\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005i\u0011aA8sON\u0019\u0001aD\f\u0011\u0005A)R\"A\t\u000b\u0005I\u0019\u0012\u0001\u00027b]\u001eT\u0011\u0001F\u0001\u0005U\u00064\u0018-\u0003\u0002\u0017#\t1qJ\u00196fGR\u00042\u0001G\u0011\u0010\u001b\u0005I\"B\u0001\u000e\u001c\u0003\r)\u0007\u0010\u001e\u0006\u00039u\t!A]:\u000b\u0005yy\u0012AA<t\u0015\u0005\u0001\u0013!\u00026bm\u0006D\u0018B\u0001\u0012\u001a\u0005EiUm]:bO\u0016\u0014u\u000eZ=Xe&$XM\u001d\u0005\u0006I\u0001!\tAJ\u0001\u0007y%t\u0017\u000e\u001e \u0004\u0001Q\tq\u0005\u0005\u0002)\u00015\t!\u0001C\u0004+\u0001\t\u0007I\u0011A\u0016\u0002\r5\f\u0007\u000f]3s+\u0005a\u0003CA\u00177\u001b\u0005q#BA\u00181\u0003!!\u0017\r^1cS:$'BA\u00193\u0003\u001dQ\u0017mY6t_:T!a\r\u001b\u0002\u0013\u0019\f7\u000f^3sq6d'\"A\u001b\u0002\u0007\r|W.\u0003\u00028]\taqJ\u00196fGRl\u0015\r\u001d9fe\"1\u0011\b\u0001Q\u0001\n1\nq!\\1qa\u0016\u0014\b\u0005C\u0003<\u0001\u0011\u0005C(A\u0006jg^\u0013\u0018\u000e^3bE2,G#B\u001fD1\u0002\\\u0007C\u0001 B\u001b\u0005y$\"\u0001!\u0002\u000bM\u001c\u0017\r\\1\n\u0005\t{$a\u0002\"p_2,\u0017M\u001c\u0005\u0006\tj\u0002\r!R\u0001\u0007C\u000ec\u0017m]:1\u0005\u0019{\u0005cA$K\u001b:\u0011a\bS\u0005\u0003\u0013~\na\u0001\u0015:fI\u00164\u0017BA&M\u0005\u0015\u0019E.Y:t\u0015\tIu\b\u0005\u0002O\u001f2\u0001A!\u0003)D\u0003\u0003\u0005\tQ!\u0001R\u0005\ryF%M\t\u0003%V\u0003\"AP*\n\u0005Q{$a\u0002(pi\"Lgn\u001a\t\u0003}YK!aV \u0003\u0007\u0005s\u0017\u0010C\u0003Zu\u0001\u0007!,\u0001\u0003usB,\u0007CA._\u001b\u0005a&BA/\u0012\u0003\u001d\u0011XM\u001a7fGRL!a\u0018/\u0003\tQK\b/\u001a\u0005\u0006Cj\u0002\rAY\u0001\fC:tw\u000e^1uS>t7\u000fE\u0002?G\u0016L!\u0001Z \u0003\u000b\u0005\u0013(/Y=\u0011\u0005\u0019LW\"A4\u000b\u0005!\f\u0012AC1o]>$\u0018\r^5p]&\u0011!n\u001a\u0002\u000b\u0003:tw\u000e^1uS>t\u0007\"\u00027;\u0001\u0004i\u0017!C7fI&\fG+\u001f9f!\tq\u0017/D\u0001p\u0015\t\u00018$\u0001\u0003d_J,\u0017B\u0001:p\u0005%iU\rZ5b)f\u0004X\rC\u0003u\u0001\u0011\u0005S/A\u0004xe&$X\rV8\u0015\u001bYL80a\u0001\u0002\u0006\u0005\u001d\u0011\u0011BA\u0010!\tqt/\u0003\u0002y\u007f\t!QK\\5u\u0011\u0015Q8\u000f1\u0001\u0010\u0003\u0005!\b\"\u0002#t\u0001\u0004a\bGA?\u0000!\r9%J \t\u0003\u001d~$!\"!\u0001|\u0003\u0003\u0005\tQ!\u0001R\u0005\ryFE\r\u0005\u00063N\u0004\rA\u0017\u0005\u0006CN\u0004\rA\u0019\u0005\u0006YN\u0004\r!\u001c\u0005\b\u0003\u0017\u0019\b\u0019AA\u0007\u00039iW\u000f\u001c;jm\u0006dW/\u001a3NCB\u0004rA\\A\b\u0003'\tI\"C\u0002\u0002\u0012=\u0014a\"T;mi&4\u0018\r\\;fI6\u000b\u0007\u000fE\u0002H\u0003+I1!a\u0006M\u0005\u0019\u0019FO]5oOB\u0019a(a\u0007\n\u0007\u0005uqH\u0001\u0004B]f\u0014VM\u001a\u0005\b\u0003C\u0019\b\u0019AA\u0012\u00031yW\u000f\u001e9viN#(/Z1n!\u0011\t)#a\u000b\u000e\u0005\u0005\u001d\"bAA\u0015'\u0005\u0011\u0011n\\\u0005\u0005\u0003[\t9C\u0001\u0007PkR\u0004X\u000f^*ue\u0016\fW\u000eC\u0004\u00022\u0001!\t%a\r\u0002\u000f\u001d,GoU5{KRa\u0011QGA\u001e\u0003{\tI%a\u0013\u0002NA\u0019a(a\u000e\n\u0007\u0005erH\u0001\u0003M_:<\u0007B\u0002>\u00020\u0001\u0007q\u0002C\u0004E\u0003_\u0001\r!a\u00101\t\u0005\u0005\u0013Q\t\t\u0005\u000f*\u000b\u0019\u0005E\u0002O\u0003\u000b\"1\"a\u0012\u0002>\u0005\u0005\t\u0011!B\u0001#\n\u0019q\fJ\u001a\t\re\u000by\u00031\u0001[\u0011\u0019\t\u0017q\u0006a\u0001E\"1A.a\fA\u00025Ds\u0001AA)\u00033\nY\u0006\u0005\u0003\u0002T\u0005US\"A\u000e\n\u0007\u0005]3D\u0001\u0005Qe>$WoY3t\u0003\u00151\u0018\r\\;fY\t\ti&\t\u0002\u0002`\u0005\u0001\u0012\r\u001d9mS\u000e\fG/[8o_)\u001cxN\u001c\u0015\u0004\u0001\u0005\r\u0004c\u0001\r\u0002f%\u0019\u0011qM\r\u0003\u0011A\u0013xN^5eKJ<\u0001\"a\u001b\u0003\u0011\u0003A\u0011QN\u0001\u0015\u0015\u0006\u001c7n]8o\u001b\u0016\u001c8/Y4f/JLG/\u001a:\u0011\u0007!\nyGB\u0004\u0002\u0005!\u0005\u0001\"!\u001d\u0014\t\u0005=\u0014\u0011\u0004\u0005\bI\u0005=D\u0011AA;)\t\ti\u0007\u0003\u0005\u0002z\u0005=D\u0011AA>\u0003Ei\u0017m[3J'>#\u0015\r^3G_Jl\u0017\r^\u000b\u0003\u0003{\u0002B!a \u0002\u00066\u0011\u0011\u0011\u0011\u0006\u0004\u0003\u0007\u001b\u0012\u0001\u0002;fqRLA!a\"\u0002\u0002\n\u00012+[7qY\u0016$\u0015\r^3G_Jl\u0017\r\u001e"
)
public class JacksonMessageWriter implements MessageBodyWriter<Object> {
   private final ObjectMapper mapper = new ObjectMapper(this) {
      public String writeValueAsString(Object t) {
         return super.writeValueAsString(t);
      }
   };

   public static SimpleDateFormat makeISODateFormat() {
      return JacksonMessageWriter$.MODULE$.makeISODateFormat();
   }

   public ObjectMapper mapper() {
      return this.mapper;
   }

   public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
      return true;
   }

   public void writeTo(Object t, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> multivaluedMap, OutputStream outputStream) {
      if (t instanceof ErrorWrapper) {
         ErrorWrapper var9 = (ErrorWrapper)t;
         String err = var9.s();
         outputStream.write(err.getBytes(StandardCharsets.UTF_8));
         BoxedUnit var11 = BoxedUnit.UNIT;
      } else {
         this.mapper().writeValue(outputStream, t);
         BoxedUnit var12 = BoxedUnit.UNIT;
      }

   }

   public long getSize(Object t, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
      return -1L;
   }

   public JacksonMessageWriter() {
      this.mapper().registerModule(.MODULE$);
      this.mapper().enable(SerializationFeature.INDENT_OUTPUT);
      this.mapper().setSerializationInclusion(Include.NON_NULL);
      this.mapper().setDateFormat(JacksonMessageWriter$.MODULE$.makeISODateFormat());
   }
}
