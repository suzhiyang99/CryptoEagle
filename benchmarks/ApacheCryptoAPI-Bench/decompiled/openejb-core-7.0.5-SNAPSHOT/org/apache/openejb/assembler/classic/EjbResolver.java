package org.apache.openejb.assembler.classic;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.openejb.util.LinkResolver;

public class EjbResolver {
   private final Map<String, EnterpriseBeanInfo> deployments;
   private final LinkResolver<String> resolver;
   private final Map<Interfaces, List<Interfaces>> interfaces;
   private final EjbResolver parent;
   private final Scope scope;

   public EjbResolver(EjbResolver parent, Scope scope, EjbJarInfo... ejbJars) {
      this(parent, scope, Arrays.asList(ejbJars));
   }

   public EjbResolver(EjbResolver parent, Scope scope, List<EjbJarInfo> ejbJars) {
      this.deployments = new TreeMap();
      this.resolver = new LinkResolver<String>();
      this.interfaces = new TreeMap();
      this.scope = scope;
      this.parent = parent;

      for(EjbJarInfo ejbJarInfo : ejbJars) {
         this.add(ejbJarInfo);
      }

   }

   public void addAll(List<EjbJarInfo> ejbJars) {
      for(EjbJarInfo ejbJarInfo : ejbJars) {
         this.add(ejbJarInfo);
      }

   }

   public void add(EjbJarInfo ejbJarInfo) {
      for(EnterpriseBeanInfo bean : ejbJarInfo.enterpriseBeans) {
         this.index(ejbJarInfo.moduleUri, bean);
      }

   }

   private void index(URI moduleURI, EnterpriseBeanInfo bean) {
      this.deployments.put(bean.ejbDeploymentId, bean);
      this.resolver.add((URI)moduleURI, bean.ejbName, bean.ejbDeploymentId);
      if (bean.remote != null) {
         this.addInterfaces(new Interfaces(bean.home, bean.remote, EjbResolver.Type.REMOTE, bean.ejbDeploymentId));
         this.addInterfaces(new Interfaces(bean.remote, EjbResolver.Type.REMOTE, bean.ejbDeploymentId));
      }

      for(String businessRemote : bean.businessRemote) {
         this.addInterfaces(new Interfaces(businessRemote, EjbResolver.Type.REMOTE, bean.ejbDeploymentId));
      }

      if (bean.local != null) {
         this.addInterfaces(new Interfaces(bean.localHome, bean.local, EjbResolver.Type.LOCAL, bean.ejbDeploymentId));
         this.addInterfaces(new Interfaces(bean.local, EjbResolver.Type.LOCAL, bean.ejbDeploymentId));
      }

      for(String businessLocal : bean.businessLocal) {
         this.addInterfaces(new Interfaces(businessLocal, EjbResolver.Type.LOCAL, bean.ejbDeploymentId));
      }

      if (bean.localbean) {
         this.addInterfaces(new Interfaces(bean.ejbClass, EjbResolver.Type.LOCAL, bean.ejbDeploymentId));

         for(String parent : bean.parents) {
            this.addInterfaces(new Interfaces(parent, EjbResolver.Type.LOCAL, bean.ejbDeploymentId));
         }
      }

   }

   private void addInterfaces(Interfaces interfaces) {
      List<Interfaces> similar = (List)this.interfaces.get(interfaces);
      if (similar == null) {
         similar = new ArrayList();
         this.interfaces.put(interfaces, similar);
      }

      similar.add(interfaces);
   }

   private String resolveLink(String link, URI moduleUri) {
      if (link != null && link.length() != 0) {
         String id = (String)this.resolver.resolveLink(link, moduleUri);
         if (id == null && this.parent != null) {
            id = this.parent.resolveLink(link, moduleUri);
         }

         return id;
      } else {
         return null;
      }
   }

   private String resolveInterface(Reference ref) {
      String id = null;
      if (ref.getInterface() == null) {
         return null;
      } else {
         List<Interfaces> matches = (List)this.interfaces.get(new Interfaces(ref.getHome(), ref.getInterface()));
         if (matches != null && matches.size() > 0) {
            List<Interfaces> nameMatches = this.filter(matches, ref.getName());
            id = this.first(this.filter(nameMatches, ref.getRefType()));
            if (id == null) {
               id = this.first(nameMatches);
            }

            if (id == null) {
               id = this.first(this.filter(matches, ref.getRefType()));
            }

            if (id == null) {
               id = this.first(matches);
            }
         }

         if (id == null && this.parent != null) {
            id = this.parent.resolveInterface(ref);
         }

         return id;
      }
   }

   private String first(List<Interfaces> list) {
      return list.size() == 0 ? null : ((Interfaces)list.get(0)).getId();
   }

   private List<Interfaces> filter(List<Interfaces> list, String name) {
      String shortName = name.replaceAll(".*/", "");
      List<Interfaces> matches = new ArrayList();

      for(Interfaces entry : list) {
         if (name.equalsIgnoreCase(entry.getId())) {
            matches.add(entry);
         } else if (shortName.equalsIgnoreCase(entry.getId())) {
            matches.add(entry);
         }
      }

      return matches;
   }

   private List<Interfaces> filter(List<Interfaces> list, Type type) {
      List<Interfaces> matches = new ArrayList();

      for(Interfaces entry : list) {
         if (type == EjbResolver.Type.UNKNOWN || type == entry.type) {
            matches.add(entry);
         }
      }

      return matches;
   }

   public Scope getScope(String deploymentId) {
      if (this.deployments.containsKey(deploymentId)) {
         return this.scope;
      } else {
         return this.parent != null ? this.parent.getScope(deploymentId) : null;
      }
   }

   public EnterpriseBeanInfo getEnterpriseBeanInfo(String deploymentId) {
      EnterpriseBeanInfo info = (EnterpriseBeanInfo)this.deployments.get(deploymentId);
      if (info == null && this.parent != null) {
         info = this.parent.getEnterpriseBeanInfo(deploymentId);
      }

      return info;
   }

   public String resolve(Reference ref, URI moduleUri) {
      if (ref.getMappedName() != null && !ref.getMappedName().equals("")) {
         return ref.getMappedName();
      } else {
         String targetId = this.resolveLink(ref.getEjbLink(), moduleUri);
         if (targetId == null && ref.getEjbLink() == null) {
            targetId = this.resolveInterface(ref);
         }

         return targetId;
      }
   }

   public static enum Type {
      UNKNOWN,
      LOCAL,
      REMOTE;
   }

   public static enum Scope {
      GLOBAL,
      EAR,
      EJBJAR;
   }

   private static class Interfaces implements Comparable {
      private final String id;
      private final Type type;
      private final String homeInterface;
      private final String objectInterface;

      public Interfaces(String objectInterface, Type type, String id) {
         if (objectInterface == null) {
            throw new NullPointerException("objectInterface is null");
         } else {
            this.homeInterface = "<none>";
            this.objectInterface = objectInterface;
            this.type = type;
            this.id = id;
         }
      }

      public Interfaces(String homeInterface, String objectInterface, Type type, String id) {
         if (homeInterface == null) {
            homeInterface = "<none>";
         }

         if (objectInterface == null) {
            throw new NullPointerException("objectInterface is null");
         } else {
            this.homeInterface = homeInterface;
            this.objectInterface = objectInterface;
            this.type = type;
            this.id = id;
         }
      }

      public Interfaces(String homeInterface, String objectInterface) {
         if (homeInterface == null) {
            homeInterface = "<none>";
         }

         if (objectInterface == null) {
            throw new NullPointerException("objectInterface is null");
         } else {
            this.homeInterface = homeInterface;
            this.objectInterface = objectInterface;
            this.type = null;
            this.id = null;
         }
      }

      public String getId() {
         return this.id;
      }

      public Type getType() {
         return this.type;
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            Interfaces that = (Interfaces)o;
            return this.homeInterface.equals(that.homeInterface) && this.objectInterface.equals(that.objectInterface);
         } else {
            return false;
         }
      }

      public int hashCode() {
         int result = this.homeInterface.hashCode();
         result = 31 * result + this.objectInterface.hashCode();
         return result;
      }

      public int compareTo(Object o) {
         if (this == o) {
            return 0;
         } else {
            Interfaces that = (Interfaces)o;
            return this.toString().compareTo(that.toString());
         }
      }

      public String toString() {
         return this.homeInterface + ":" + this.objectInterface;
      }
   }

   public interface Reference {
      String getName();

      Type getRefType();

      String getHome();

      String getInterface();

      String getMappedName();

      String getEjbLink();
   }
}
