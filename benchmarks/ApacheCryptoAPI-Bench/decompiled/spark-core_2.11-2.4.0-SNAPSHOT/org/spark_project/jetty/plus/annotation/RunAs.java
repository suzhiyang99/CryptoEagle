package org.spark_project.jetty.plus.annotation;

import org.spark_project.jetty.servlet.ServletHolder;

public class RunAs {
   private String _className;
   private String _roleName;

   public void setTargetClassName(String className) {
      this._className = className;
   }

   public String getTargetClassName() {
      return this._className;
   }

   public void setRoleName(String roleName) {
      this._roleName = roleName;
   }

   public String getRoleName() {
      return this._roleName;
   }

   public void setRunAs(ServletHolder holder) {
      if (holder != null) {
         String className = holder.getClassName();
         if (className.equals(this._className) && holder.getRegistration().getRunAsRole() == null) {
            holder.getRegistration().setRunAsRole(this._roleName);
         }

      }
   }
}
