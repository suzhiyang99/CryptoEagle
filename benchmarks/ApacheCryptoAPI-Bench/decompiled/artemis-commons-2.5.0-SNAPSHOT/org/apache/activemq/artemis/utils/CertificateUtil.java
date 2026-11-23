package org.apache.activemq.artemis.utils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.handler.ssl.SslHandler;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.security.cert.X509Certificate;

public class CertificateUtil {
   public static X509Certificate[] getCertsFromChannel(Channel channel) {
      X509Certificate[] certificates = null;
      ChannelHandler channelHandler = channel.pipeline().get("ssl");
      if (channelHandler != null && channelHandler instanceof SslHandler) {
         SslHandler sslHandler = (SslHandler)channelHandler;

         try {
            certificates = sslHandler.engine().getSession().getPeerCertificateChain();
         } catch (SSLPeerUnverifiedException var5) {
         }
      }

      return certificates;
   }
}
