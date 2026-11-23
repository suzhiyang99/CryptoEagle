package org.apache.spark.serializer;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import javax.annotation.concurrent.NotThreadSafe;
import org.apache.spark.annotation.DeveloperApi;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u0005a!B\u0001\u0003\u0003\u0003Y!AE*fe&\fG.\u001b>fe&s7\u000f^1oG\u0016T!a\u0001\u0003\u0002\u0015M,'/[1mSj,'O\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h\u0007\u0001\u0019\"\u0001\u0001\u0007\u0011\u00055\u0001R\"\u0001\b\u000b\u0003=\tQa]2bY\u0006L!!\u0005\b\u0003\r\u0005s\u0017PU3g\u0011\u0015\u0019\u0002\u0001\"\u0001\u0015\u0003\u0019a\u0014N\\5u}Q\tQ\u0003\u0005\u0002\u0017\u00015\t!\u0001C\u0003\u0019\u0001\u0019\u0005\u0011$A\u0005tKJL\u0017\r\\5{KV\u0011!D\f\u000b\u00037]\"\"\u0001\b\u0013\u0011\u0005u\u0011S\"\u0001\u0010\u000b\u0005}\u0001\u0013a\u00018j_*\t\u0011%\u0001\u0003kCZ\f\u0017BA\u0012\u001f\u0005)\u0011\u0015\u0010^3Ck\u001a4WM\u001d\u0005\bK]\t\t\u0011q\u0001'\u0003))g/\u001b3f]\u000e,G%\r\t\u0004O)bS\"\u0001\u0015\u000b\u0005%r\u0011a\u0002:fM2,7\r^\u0005\u0003W!\u0012\u0001b\u00117bgN$\u0016m\u001a\t\u0003[9b\u0001\u0001B\u00030/\t\u0007\u0001GA\u0001U#\t\tD\u0007\u0005\u0002\u000ee%\u00111G\u0004\u0002\b\u001d>$\b.\u001b8h!\tiQ'\u0003\u00027\u001d\t\u0019\u0011I\\=\t\u000ba:\u0002\u0019\u0001\u0017\u0002\u0003QDQA\u000f\u0001\u0007\u0002m\n1\u0002Z3tKJL\u0017\r\\5{KV\u0011Ah\u0010\u000b\u0003{\r#\"A\u0010!\u0011\u00055zD!B\u0018:\u0005\u0004\u0001\u0004bB!:\u0003\u0003\u0005\u001dAQ\u0001\u000bKZLG-\u001a8dK\u0012\u0012\u0004cA\u0014+}!)A)\u000fa\u00019\u0005)!-\u001f;fg\")!\b\u0001D\u0001\rV\u0011qI\u0013\u000b\u0004\u0011:{ECA%L!\ti#\nB\u00030\u000b\n\u0007\u0001\u0007C\u0004M\u000b\u0006\u0005\t9A'\u0002\u0015\u00154\u0018\u000eZ3oG\u0016$3\u0007E\u0002(U%CQ\u0001R#A\u0002qAQ\u0001U#A\u0002E\u000ba\u0001\\8bI\u0016\u0014\bC\u0001*V\u001b\u0005\u0019&B\u0001+!\u0003\u0011a\u0017M\\4\n\u0005Y\u001b&aC\"mCN\u001cHj\\1eKJDQ\u0001\u0017\u0001\u0007\u0002e\u000bqb]3sS\u0006d\u0017N_3TiJ,\u0017-\u001c\u000b\u00035v\u0003\"AF.\n\u0005q\u0013!aE*fe&\fG.\u001b>bi&|gn\u0015;sK\u0006l\u0007\"\u00020X\u0001\u0004y\u0016!A:\u0011\u0005\u0001\u001cW\"A1\u000b\u0005\t\u0004\u0013AA5p\u0013\t!\u0017M\u0001\u0007PkR\u0004X\u000f^*ue\u0016\fW\u000eC\u0003g\u0001\u0019\u0005q-A\teKN,'/[1mSj,7\u000b\u001e:fC6$\"\u0001[6\u0011\u0005YI\u0017B\u00016\u0003\u0005U!Um]3sS\u0006d\u0017N_1uS>t7\u000b\u001e:fC6DQAX3A\u00021\u0004\"\u0001Y7\n\u00059\f'aC%oaV$8\u000b\u001e:fC6D#\u0001\u00019\u0011\u0005EDX\"\u0001:\u000b\u0005M$\u0018AC2p]\u000e,(O]3oi*\u0011QO^\u0001\u000bC:tw\u000e^1uS>t'\"A<\u0002\u000b)\fg/\u0019=\n\u0005e\u0014(!\u0004(piRC'/Z1e'\u00064W\r\u000b\u0002\u0001wB\u0011AP`\u0007\u0002{*\u0011Q\u000fB\u0005\u0003\u007fv\u0014A\u0002R3wK2|\u0007/\u001a:Ba&\u0004"
)
@NotThreadSafe
public abstract class SerializerInstance {
   public abstract <T> ByteBuffer serialize(T var1, ClassTag<T> var2);

   public abstract <T> T deserialize(ByteBuffer var1, ClassTag<T> var2);

   public abstract <T> T deserialize(ByteBuffer var1, ClassLoader var2, ClassTag<T> var3);

   public abstract SerializationStream serializeStream(OutputStream var1);

   public abstract DeserializationStream deserializeStream(InputStream var1);
}
