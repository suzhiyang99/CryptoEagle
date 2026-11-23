package org.spark_project.jetty.plus.jndi;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.LinkRef;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingException;
import org.spark_project.jetty.jndi.NamingUtil;
import org.spark_project.jetty.util.log.Logger;

public abstract class NamingEntry {
   private static Logger __log;
   public static final String __contextName = "__";
   protected final Object _scope;
   protected final String _jndiName;
   protected String _namingEntryNameString;
   protected String _objectNameString;

   public String toString() {
      return this._jndiName;
   }

   protected NamingEntry(Object scope, String jndiName) throws NamingException {
      if (jndiName == null) {
         throw new NamingException("jndi name is null");
      } else {
         this._scope = scope;
         this._jndiName = jndiName;
      }
   }

   protected NamingEntry(String jndiName) throws NamingException {
      this((Object)null, jndiName);
   }

   public void bindToENC(String localName) throws NamingException {
      InitialContext ic = new InitialContext();
      Context env = (Context)ic.lookup("java:comp/env");
      __log.debug("Binding java:comp/env/" + localName + " to " + this._objectNameString);
      NamingUtil.bind(env, localName, new LinkRef(this._objectNameString));
   }

   public void unbindENC() {
      try {
         InitialContext ic = new InitialContext();
         Context env = (Context)ic.lookup("java:comp/env");
         __log.debug("Unbinding java:comp/env/" + this.getJndiName());
         env.unbind(this.getJndiName());
      } catch (NamingException e) {
         __log.warn(e);
      }

   }

   public void release() {
      try {
         InitialContext ic = new InitialContext();
         ic.unbind(this._objectNameString);
         ic.unbind(this._namingEntryNameString);
         this._namingEntryNameString = null;
         this._objectNameString = null;
      } catch (NamingException e) {
         __log.warn(e);
      }

   }

   public String getJndiName() {
      return this._jndiName;
   }

   public String getJndiNameInScope() {
      return this._objectNameString;
   }

   protected void save(Object object) throws NamingException {
      __log.debug("SAVE {} in {}", this, this._scope);
      InitialContext ic = new InitialContext();
      NameParser parser = ic.getNameParser("");
      Name prefix = NamingEntryUtil.getNameForScope(this._scope);
      Name namingEntryName = NamingEntryUtil.makeNamingEntryName(parser, this.getJndiName());
      namingEntryName.addAll(0, prefix);
      this._namingEntryNameString = namingEntryName.toString();
      NamingUtil.bind(ic, this._namingEntryNameString, this);
      Name objectName = parser.parse(this.getJndiName());
      objectName.addAll(0, prefix);
      this._objectNameString = objectName.toString();
      NamingUtil.bind(ic, this._objectNameString, object);
   }

   static {
      __log = NamingUtil.__log;
   }
}
