package org.apache.directory.shared.kerberos.codec.adKdcIssued.actions;

import org.apache.directory.shared.kerberos.codec.actions.AbstractReadCheckSum;
import org.apache.directory.shared.kerberos.codec.adKdcIssued.AdKdcIssuedContainer;
import org.apache.directory.shared.kerberos.components.Checksum;

public class StoreChecksum extends AbstractReadCheckSum<AdKdcIssuedContainer> {
   public StoreChecksum() {
      super("AD-KDCIssued cksum");
   }

   protected void setChecksum(Checksum checksum, AdKdcIssuedContainer adKdcIssuedContainer) {
      adKdcIssuedContainer.getAdKdcIssued().setAdChecksum(checksum);
   }
}
