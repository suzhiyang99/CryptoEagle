package org.apache.directory.shared.kerberos.codec.lastReq.actions;

import org.apache.directory.api.asn1.actions.AbstractReadInteger;
import org.apache.directory.shared.kerberos.codec.lastReq.LastReqContainer;
import org.apache.directory.shared.kerberos.codec.types.LastReqType;
import org.apache.directory.shared.kerberos.components.LastReq;

public class StoreLrType extends AbstractReadInteger<LastReqContainer> {
   public StoreLrType() {
      super("LastReq lrType");
   }

   protected void setIntegerValue(int value, LastReqContainer lastReqContainer) {
      LastReqType lrType = LastReqType.getTypeByValue(value);
      LastReq lastReq = lastReqContainer.getLastReq();
      lastReq.createNewLR();
      lastReq.setCurrentLrType(lrType);
   }
}
