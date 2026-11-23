package org.apache.manifoldcf.core.database;

import java.util.ArrayList;
import java.util.List;
import org.apache.manifoldcf.core.cachemanager.BaseDescription;
import org.apache.manifoldcf.core.interfaces.ILimitChecker;
import org.apache.manifoldcf.core.interfaces.ResultSpecification;
import org.apache.manifoldcf.core.interfaces.StringSet;

public class QueryDescription extends BaseDescription {
   public static final String _rcsid = "@(#)$Id: QueryDescription.java 988245 2010-08-23 18:39:35Z kwright $";
   protected String databaseName;
   protected String query;
   protected List parameters;
   protected String criticalSectionName = null;
   protected String cacheClassName;
   protected StringSet keys;
   protected int maxReturn;
   protected ResultSpecification spec;
   protected ILimitChecker returnLimit;

   public QueryDescription(String databaseName, String query, List parameters, String cacheClassName, StringSet cacheKeys, int maxReturn, ResultSpecification spec, ILimitChecker returnLimit) {
      super(cacheClassName);
      this.databaseName = databaseName;
      this.query = query;
      this.parameters = null;
      if (parameters != null) {
         this.parameters = listClone(parameters);
      }

      this.keys = cacheKeys;
      this.maxReturn = maxReturn;
      this.spec = spec;
      if (returnLimit != null) {
         if (returnLimit.doesCompareWork()) {
            this.returnLimit = returnLimit.duplicate();
         } else {
            this.returnLimit = returnLimit;
            this.keys = null;
         }
      } else {
         this.returnLimit = null;
      }

   }

   public String getQuery() {
      return this.query;
   }

   public List getParameters() {
      return this.parameters == null ? null : listClone(this.parameters);
   }

   public int getMaxReturn() {
      return this.maxReturn;
   }

   public ResultSpecification getResultSpecification() {
      return this.spec;
   }

   public ILimitChecker getReturnLimit() {
      return this.returnLimit;
   }

   public int hashCode() {
      int rval = this.databaseName.hashCode() + this.query.hashCode();
      if (this.parameters != null) {
         rval += this.parameters.hashCode();
      }

      if (this.cacheClassName != null) {
         rval += this.cacheClassName.hashCode();
      }

      rval += this.maxReturn;
      if (this.spec != null) {
         rval += this.spec.hashCode();
      }

      if (this.returnLimit != null && this.returnLimit.doesCompareWork()) {
         rval += this.returnLimit.hashCode();
      }

      return rval;
   }

   public boolean equals(Object o) {
      if (!(o instanceof QueryDescription)) {
         return false;
      } else {
         QueryDescription other = (QueryDescription)o;
         if (this.parameters != null && other.parameters != null) {
            if (!this.parameters.equals(other.parameters)) {
               return false;
            }
         } else if (this.parameters != null || other.parameters != null) {
            return false;
         }

         if (this.cacheClassName != null && other.cacheClassName != null) {
            if (!this.cacheClassName.equals(other.cacheClassName)) {
               return false;
            }
         } else if (this.cacheClassName != null || other.cacheClassName != null) {
            return false;
         }

         if (this.spec != null && other.spec != null) {
            if (!this.spec.equals(other.spec)) {
               return false;
            }
         } else if (this.spec != null || other.spec != null) {
            return false;
         }

         if (this.returnLimit != null && other.returnLimit != null) {
            if (!this.returnLimit.doesCompareWork() || !other.returnLimit.doesCompareWork()) {
               return false;
            }

            if (!this.returnLimit.equals(other.returnLimit)) {
               return false;
            }
         } else if (this.returnLimit != null || other.returnLimit != null) {
            return false;
         }

         if (this.databaseName != null && other.databaseName != null) {
            if (!this.databaseName.equals(other.databaseName)) {
               return false;
            }
         } else if (this.databaseName != null || other.databaseName != null) {
            return false;
         }

         return this.query.equals(other.query) && this.maxReturn == other.maxReturn;
      }
   }

   public StringSet getObjectKeys() {
      return this.keys;
   }

   public String getCriticalSectionName() {
      if (this.criticalSectionName == null) {
         StringBuilder sb = new StringBuilder();
         if (this.databaseName != null) {
            sb.append(this.databaseName);
         }

         sb.append("-").append(this.getClass().getName()).append("-");
         if (this.cacheClassName != null) {
            sb.append(this.cacheClassName).append("-");
         }

         sb.append(this.query).append("-");
         sb.append(Integer.toString(this.maxReturn)).append("-");
         if (this.parameters != null) {
            int i = 0;

            while(i < this.parameters.size()) {
               sb.append(this.parameters.get(i++)).append("-");
            }
         }

         if (this.spec != null) {
            sb.append(":").append(this.spec.toString());
         }

         if (this.returnLimit != null && this.returnLimit.doesCompareWork()) {
            sb.append(":limited");
         }

         this.criticalSectionName = sb.toString();
      }

      return this.criticalSectionName;
   }

   protected static List listClone(List list) {
      List rval = new ArrayList(list.size());

      for(Object o : list) {
         rval.add(o);
      }

      return rval;
   }
}
