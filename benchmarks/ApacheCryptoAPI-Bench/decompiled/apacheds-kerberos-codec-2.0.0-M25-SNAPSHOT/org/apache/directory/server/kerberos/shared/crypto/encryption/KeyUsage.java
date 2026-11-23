package org.apache.directory.server.kerberos.shared.crypto.encryption;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.directory.server.i18n.I18n;

public final class KeyUsage implements Comparable<KeyUsage> {
   public static final KeyUsage AS_REQ_PA_ENC_TIMESTAMP_WITH_CKEY;
   public static final KeyUsage AS_OR_TGS_REP_TICKET_WITH_SRVKEY;
   public static final KeyUsage AS_REP_ENC_PART_WITH_CKEY;
   public static final KeyUsage TGS_REQ_KDC_REQ_BODY_AUTHZ_DATA_ENC_WITH_TGS_SESS_KEY;
   public static final KeyUsage TGS_REQ_KDC_REQ_BODY_AUTHZ_DATA_ENC_WITH_AUTHNT_SUB_KEY;
   public static final KeyUsage TGS_REQ_PA_TGS_REQ_PADATA_AP_REQ_AUTHNT_CKSUM_TGS_SESS_KEY;
   public static final KeyUsage TGS_REQ_PA_TGS_REQ_PADATA_AP_REQ_TGS_SESS_KEY;
   public static final KeyUsage TGS_REP_ENC_PART_TGS_SESS_KEY;
   public static final KeyUsage TGS_REP_ENC_PART_TGS_AUTHNT_SUB_KEY;
   public static final KeyUsage AP_REQ_AUTHNT_CKSUM_SESS_KEY;
   public static final KeyUsage AP_REQ_AUTHNT_SESS_KEY;
   public static final KeyUsage AP_REP_ENC_PART_SESS_KEY;
   public static final KeyUsage KRB_PRIV_ENC_PART_CHOSEN_KEY;
   private static final KeyUsage[] values;
   public static final List<KeyUsage> VALUES;
   private final int ordinal;
   private final String name;

   private KeyUsage(int ordinal, String name) {
      this.ordinal = ordinal;
      this.name = name;
   }

   public static KeyUsage getTypeByOrdinal(int type) {
      for(int ii = 0; ii < values.length; ++ii) {
         if (values[ii].ordinal == type) {
            return values[ii];
         }
      }

      return AS_REQ_PA_ENC_TIMESTAMP_WITH_CKEY;
   }

   public int getOrdinal() {
      return this.ordinal;
   }

   public int compareTo(KeyUsage that) {
      return this.ordinal - that.ordinal;
   }

   public String toString() {
      return this.name + " (" + this.ordinal + ")";
   }

   static {
      AS_REQ_PA_ENC_TIMESTAMP_WITH_CKEY = new KeyUsage(1, I18n.err(I18n.ERR_603, new Object[0]));
      AS_OR_TGS_REP_TICKET_WITH_SRVKEY = new KeyUsage(2, I18n.err(I18n.ERR_604, new Object[0]));
      AS_REP_ENC_PART_WITH_CKEY = new KeyUsage(3, I18n.err(I18n.ERR_605, new Object[0]));
      TGS_REQ_KDC_REQ_BODY_AUTHZ_DATA_ENC_WITH_TGS_SESS_KEY = new KeyUsage(4, I18n.err(I18n.ERR_606, new Object[0]));
      TGS_REQ_KDC_REQ_BODY_AUTHZ_DATA_ENC_WITH_AUTHNT_SUB_KEY = new KeyUsage(5, I18n.err(I18n.ERR_607, new Object[0]));
      TGS_REQ_PA_TGS_REQ_PADATA_AP_REQ_AUTHNT_CKSUM_TGS_SESS_KEY = new KeyUsage(6, I18n.err(I18n.ERR_608, new Object[0]));
      TGS_REQ_PA_TGS_REQ_PADATA_AP_REQ_TGS_SESS_KEY = new KeyUsage(7, I18n.err(I18n.ERR_609, new Object[0]));
      TGS_REP_ENC_PART_TGS_SESS_KEY = new KeyUsage(8, I18n.err(I18n.ERR_610, new Object[0]));
      TGS_REP_ENC_PART_TGS_AUTHNT_SUB_KEY = new KeyUsage(9, I18n.err(I18n.ERR_610, new Object[0]));
      AP_REQ_AUTHNT_CKSUM_SESS_KEY = new KeyUsage(10, I18n.err(I18n.ERR_612, new Object[0]));
      AP_REQ_AUTHNT_SESS_KEY = new KeyUsage(11, I18n.err(I18n.ERR_613, new Object[0]));
      AP_REP_ENC_PART_SESS_KEY = new KeyUsage(12, I18n.err(I18n.ERR_614, new Object[0]));
      KRB_PRIV_ENC_PART_CHOSEN_KEY = new KeyUsage(13, I18n.err(I18n.ERR_615, new Object[0]));
      values = new KeyUsage[]{AS_REQ_PA_ENC_TIMESTAMP_WITH_CKEY, AS_OR_TGS_REP_TICKET_WITH_SRVKEY, AS_REP_ENC_PART_WITH_CKEY, TGS_REQ_KDC_REQ_BODY_AUTHZ_DATA_ENC_WITH_TGS_SESS_KEY, TGS_REQ_KDC_REQ_BODY_AUTHZ_DATA_ENC_WITH_AUTHNT_SUB_KEY, TGS_REQ_PA_TGS_REQ_PADATA_AP_REQ_AUTHNT_CKSUM_TGS_SESS_KEY, TGS_REQ_PA_TGS_REQ_PADATA_AP_REQ_TGS_SESS_KEY, TGS_REP_ENC_PART_TGS_SESS_KEY, TGS_REP_ENC_PART_TGS_AUTHNT_SUB_KEY, AP_REQ_AUTHNT_CKSUM_SESS_KEY, AP_REQ_AUTHNT_SESS_KEY, AP_REP_ENC_PART_SESS_KEY, KRB_PRIV_ENC_PART_CHOSEN_KEY};
      VALUES = Collections.unmodifiableList(Arrays.asList(values));
   }
}
