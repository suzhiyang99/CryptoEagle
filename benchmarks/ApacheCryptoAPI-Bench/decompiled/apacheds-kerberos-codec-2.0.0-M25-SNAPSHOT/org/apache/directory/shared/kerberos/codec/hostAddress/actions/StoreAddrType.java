package org.apache.directory.shared.kerberos.codec.hostAddress.actions;

import org.apache.directory.api.asn1.actions.AbstractReadInteger;
import org.apache.directory.shared.kerberos.codec.hostAddress.HostAddressContainer;
import org.apache.directory.shared.kerberos.codec.types.HostAddrType;
import org.apache.directory.shared.kerberos.components.HostAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoreAddrType extends AbstractReadInteger<HostAddressContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(StoreAddrType.class);
   private static final boolean IS_DEBUG;

   public StoreAddrType() {
      super("Creates a HostAddress instance", 0, Integer.MAX_VALUE);
   }

   protected void setIntegerValue(int value, HostAddressContainer hostAddressContainer) {
      HostAddress hostAddressData = hostAddressContainer.getHostAddress();
      HostAddrType hostAddrType = HostAddrType.getTypeByOrdinal(value);
      hostAddressData.setAddrType(hostAddrType);
      if (IS_DEBUG) {
         LOG.debug("addr-type : {}", hostAddrType);
      }

   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
