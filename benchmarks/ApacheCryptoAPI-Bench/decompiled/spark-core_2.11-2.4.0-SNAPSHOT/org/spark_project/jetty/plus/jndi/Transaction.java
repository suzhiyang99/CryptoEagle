package org.spark_project.jetty.plus.jndi;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.LinkRef;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.transaction.UserTransaction;
import org.spark_project.jetty.jndi.NamingUtil;
import org.spark_project.jetty.util.log.Logger;

public class Transaction extends NamingEntry {
   private static Logger __log;
   public static final String USER_TRANSACTION = "UserTransaction";

   public static void bindToENC() throws NamingException {
      Transaction txEntry = (Transaction)NamingEntryUtil.lookupNamingEntry((Object)null, "UserTransaction");
      if (txEntry != null) {
         txEntry.bindToComp();
      } else {
         throw new NameNotFoundException("UserTransaction not found");
      }
   }

   public Transaction(UserTransaction userTransaction) throws NamingException {
      super("UserTransaction");
      this.save(userTransaction);
   }

   public void bindToENC(String localName) throws NamingException {
      InitialContext ic = new InitialContext();
      Context env = (Context)ic.lookup("java:comp/env");
      __log.debug("Binding java:comp/env" + this.getJndiName() + " to " + this._objectNameString);
      NamingUtil.bind(env, localName, new LinkRef(this._objectNameString));
   }

   private void bindToComp() throws NamingException {
      InitialContext ic = new InitialContext();
      Context env = (Context)ic.lookup("java:comp");
      __log.debug("Binding java:comp/" + this.getJndiName() + " to " + this._objectNameString);
      NamingUtil.bind(env, this.getJndiName(), new LinkRef(this._objectNameString));
   }

   public void unbindENC() {
      try {
         InitialContext ic = new InitialContext();
         Context env = (Context)ic.lookup("java:comp");
         __log.debug("Unbinding java:comp/" + this.getJndiName());
         env.unbind(this.getJndiName());
      } catch (NamingException e) {
         __log.warn(e);
      }

   }

   static {
      __log = NamingUtil.__log;
   }
}
