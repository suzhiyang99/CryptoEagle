package org.apache.openejb.assembler.classic;

import java.lang.reflect.Method;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.security.jacc.EJBMethodPermission;
import javax.security.jacc.EJBRoleRefPermission;
import javax.security.jacc.PolicyConfiguration;
import javax.security.jacc.PolicyConfigurationFactory;
import javax.security.jacc.PolicyContextException;
import org.apache.openejb.BeanContext;
import org.apache.openejb.InterfaceType;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.JavaSecurityManagers;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public class JaccPermissionsBuilder {
   private static final Logger log;

   public void install(PolicyContext policyContext) throws OpenEJBException {
      if (!SystemInstance.get().hasProperty("openejb.geronimo")) {
         try {
            PolicyConfigurationFactory factory = PolicyConfigurationFactory.getPolicyConfigurationFactory();
            PolicyConfiguration policy = factory.getPolicyConfiguration(policyContext.getContextID(), false);
            policy.addToExcludedPolicy(policyContext.getExcludedPermissions());
            policy.addToUncheckedPolicy(policyContext.getUncheckedPermissions());

            for(Map.Entry<String, PermissionCollection> entry : policyContext.getRolePermissions().entrySet()) {
               policy.addToRole((String)entry.getKey(), (PermissionCollection)entry.getValue());
            }

            policy.commit();
         } catch (ClassNotFoundException e) {
            throw new OpenEJBException("PolicyConfigurationFactory class not found", e);
         } catch (PolicyContextException e) {
            throw new OpenEJBException("JACC PolicyConfiguration failed: ContextId=" + policyContext.getContextID(), e);
         }
      }
   }

   public PolicyContext build(EjbJarInfo ejbJar, HashMap<String, BeanContext> deployments) throws OpenEJBException {
      List<MethodPermissionInfo> normalized = new ArrayList();
      List<MethodPermissionInfo> perms = ejbJar.methodPermissions;

      for(MethodInfo info : ejbJar.excludeList) {
         MethodPermissionInfo perm = new MethodPermissionInfo();
         perm.excluded = true;
         perm.methods.add(info);
         perms.add(perm);
      }

      perms = MethodInfoUtil.normalizeMethodPermissionInfos(perms);

      for(BeanContext beanContext : deployments.values()) {
         Map<Method, MethodAttributeInfo> attributes = MethodInfoUtil.resolveAttributes(perms, beanContext);
         if (log.isDebugEnabled()) {
            for(Map.Entry<Method, MethodAttributeInfo> entry : attributes.entrySet()) {
               Method method = (Method)entry.getKey();
               MethodPermissionInfo value = (MethodPermissionInfo)entry.getValue();
               log.debug("Security Attribute: " + method + " -- " + MethodInfoUtil.toString(value));
            }
         }

         for(Map.Entry<Method, MethodAttributeInfo> entry : attributes.entrySet()) {
            Method method = (Method)entry.getKey();
            MethodPermissionInfo a = (MethodPermissionInfo)entry.getValue();
            MethodPermissionInfo b = new MethodPermissionInfo();
            b.excluded = a.excluded;
            b.unchecked = a.unchecked;
            b.roleNames.addAll(a.roleNames);
            MethodInfo am = (MethodInfo)a.methods.get(0);
            MethodInfo bm = new MethodInfo();
            bm.ejbName = beanContext.getEjbName();
            bm.ejbDeploymentId = String.valueOf(beanContext.getDeploymentID());
            bm.methodIntf = am.methodIntf;
            bm.className = method.getDeclaringClass().getName();
            bm.methodName = method.getName();
            bm.methodParams = new ArrayList();

            for(Class<?> type : method.getParameterTypes()) {
               bm.methodParams.add(type.getName());
            }

            b.methods.add(bm);
            normalized.add(b);
         }
      }

      ejbJar.methodPermissions.clear();
      ejbJar.methodPermissions.addAll(normalized);
      ejbJar.excludeList.clear();
      PolicyContext policyContext = new PolicyContext(ejbJar.moduleUri.toString());

      for(EnterpriseBeanInfo enterpriseBean : ejbJar.enterpriseBeans) {
         BeanContext beanContext = (BeanContext)deployments.get(enterpriseBean.ejbDeploymentId);
         PermissionCollection permissions = DelegatePermissionCollection.getPermissionCollection();
         String ejbName = enterpriseBean.ejbName;

         for(InterfaceType type : InterfaceType.values()) {
            if (type != InterfaceType.UNKNOWN) {
               for(Class interfce : beanContext.getInterfaces(type)) {
                  this.addPossibleEjbMethodPermissions(permissions, ejbName, type.getSpecName(), interfce);
               }
            }
         }

         this.addPossibleEjbMethodPermissions(permissions, ejbName, (String)null, beanContext.getBeanClass());
         this.addDeclaredEjbPermissions(ejbJar, enterpriseBean, (String)null, permissions, policyContext);
      }

      return policyContext;
   }

   private void addDeclaredEjbPermissions(EjbJarInfo ejbJar, EnterpriseBeanInfo beanInfo, String defaultRole, PermissionCollection notAssigned, PolicyContext policyContext) throws OpenEJBException {
      PermissionCollection uncheckedPermissions = policyContext.getUncheckedPermissions();
      PermissionCollection excludedPermissions = policyContext.getExcludedPermissions();
      Map<String, PermissionCollection> rolePermissions = policyContext.getRolePermissions();
      String ejbName = beanInfo.ejbName;

      for(MethodPermissionInfo methodPermission : ejbJar.methodPermissions) {
         List<String> roleNames = methodPermission.roleNames;
         boolean unchecked = methodPermission.unchecked;
         boolean excluded = methodPermission.excluded;

         for(MethodInfo method : methodPermission.methods) {
            if (ejbName.equals(method.ejbName)) {
               String methodName = method.methodName;
               if ("*".equals(methodName)) {
                  methodName = null;
               }

               String methodIntf = method.methodIntf;
               String[] methodParams;
               if (method.methodParams != null) {
                  List<String> paramList = method.methodParams;
                  methodParams = (String[])paramList.toArray(new String[paramList.size()]);
               } else {
                  methodParams = null;
               }

               EJBMethodPermission permission = new EJBMethodPermission(ejbName, methodName, methodIntf, methodParams);
               notAssigned = this.cullPermissions(notAssigned, permission);
               if (unchecked) {
                  uncheckedPermissions.add(permission);
               } else if (excluded) {
                  excludedPermissions.add(permission);
               } else {
                  for(String roleName : roleNames) {
                     PermissionCollection permissions = (PermissionCollection)rolePermissions.get(roleName);
                     if (permissions == null) {
                        permissions = DelegatePermissionCollection.getPermissionCollection();
                        rolePermissions.put(roleName, permissions);
                     }

                     permissions.add(permission);
                  }
               }
            }
         }
      }

      for(SecurityRoleReferenceInfo securityRoleRef : beanInfo.securityRoleReferences) {
         if (securityRoleRef.roleLink == null) {
            throw new OpenEJBException("Missing role-link");
         }

         String roleLink = securityRoleRef.roleLink;
         PermissionCollection roleLinks = (PermissionCollection)rolePermissions.get(roleLink);
         if (roleLinks == null) {
            roleLinks = DelegatePermissionCollection.getPermissionCollection();
            rolePermissions.put(roleLink, roleLinks);
         }

         roleLinks.add(new EJBRoleRefPermission(ejbName, securityRoleRef.roleName));
      }

      PermissionCollection permissions;
      if (defaultRole == null) {
         permissions = uncheckedPermissions;
      } else {
         permissions = (PermissionCollection)rolePermissions.get(defaultRole);
         if (permissions == null) {
            permissions = DelegatePermissionCollection.getPermissionCollection();
            rolePermissions.put(defaultRole, permissions);
         }
      }

      Enumeration e = notAssigned.elements();

      while(e.hasMoreElements()) {
         Permission p = (Permission)e.nextElement();
         permissions.add(p);
      }

   }

   public void addPossibleEjbMethodPermissions(PermissionCollection permissions, String ejbName, String methodInterface, Class clazz) throws OpenEJBException {
      if (clazz != null) {
         for(Method method : clazz.getMethods()) {
            String methodIface = !"LocalBean".equals(methodInterface) && !"LocalBeanHome".equals(methodInterface) ? methodInterface : null;
            permissions.add(new EJBMethodPermission(ejbName, methodIface, method));
         }

      }
   }

   private PermissionCollection cullPermissions(PermissionCollection toBeChecked, Permission permission) {
      PermissionCollection result = DelegatePermissionCollection.getPermissionCollection();
      Enumeration e = toBeChecked.elements();

      while(e.hasMoreElements()) {
         Permission test = (Permission)e.nextElement();
         if (!permission.implies(test)) {
            result.add(test);
         }
      }

      return result;
   }

   static {
      JavaSecurityManagers.setSystemProperty("org.apache.security.jacc.EJBMethodPermission.methodInterfaces", "BusinessLocalHome,BusinessRemoteHome,BusinessRemote,BusinessLocal");
      log = Logger.getInstance(LogCategory.OPENEJB_STARTUP.createChild("attributes"), JaccPermissionsBuilder.class);
   }
}
