package org.apache.directory.shared.kerberos.codec.hostAddress.actions;

import org.apache.directory.api.asn1.actions.AbstractReadOctetString;
import org.apache.directory.api.util.Strings;
import org.apache.directory.shared.kerberos.codec.hostAddress.HostAddressContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoreAddress extends AbstractReadOctetString<HostAddressContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(StoreAddress.class);
   private static final boolean IS_DEBUG;

   public StoreAddress() {
      super("Store the HostAddress' address");
   }

   protected void setOctetString(byte[] data, HostAddressContainer hostAddressContainer) {
      hostAddressContainer.getHostAddress().setAddress(data);
      hostAddressContainer.setGrammarEndAllowed(true);
      if (IS_DEBUG) {
         LOG.debug("Address : {}", Strings.utf8ToString(data));
      }

   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
