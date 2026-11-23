package org.apache.openejb.config.sys;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = ""
)
@XmlRootElement(
   name = "Deployments"
)
public class Deployments {
   @XmlAttribute
   protected String dir;
   @XmlAttribute
   protected String file;
   @XmlAttribute
   protected boolean autoDeploy;
   @XmlTransient
   protected ClassLoader classpath;

   public boolean isAutoDeploy() {
      return this.autoDeploy;
   }

   public void setAutoDeploy(boolean autoDeploy) {
      this.autoDeploy = autoDeploy;
   }

   public String getDir() {
      return this.dir;
   }

   public void setDir(String value) {
      this.dir = value;
   }

   public Deployments dir(String name) {
      this.setDir(name);
      return this;
   }

   public String getFile() {
      return this.file;
   }

   public void setFile(String value) {
      this.file = value;
   }

   public Deployments file(String name) {
      this.setFile(name);
      return this;
   }

   public ClassLoader getClasspath() {
      return this.classpath;
   }

   public void setClasspath(ClassLoader classpath) {
      this.classpath = classpath;
   }
}
