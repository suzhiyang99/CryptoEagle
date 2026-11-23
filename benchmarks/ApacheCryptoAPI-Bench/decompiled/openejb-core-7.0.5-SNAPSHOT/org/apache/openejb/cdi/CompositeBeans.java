package org.apache.openejb.cdi;

import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.openejb.jee.Beans;

@XmlAccessorType(XmlAccessType.FIELD)
public class CompositeBeans extends Beans {
   @XmlTransient
   private final Map<URL, String> discoveryByUrl = new HashMap();
   @XmlTransient
   private final Map<URL, Collection<String>> interceptorsByUrl = new HashMap();
   @XmlTransient
   private final Map<URL, Collection<String>> decoratorsByUrl = new HashMap();
   @XmlTransient
   private final Map<URL, Collection<String>> alternativesByUrl = new HashMap();
   @XmlTransient
   private final Map<URL, Collection<String>> alternativeStereotypesByUrl = new HashMap();

   public Map<URL, String> getDiscoveryByUrl() {
      return this.discoveryByUrl;
   }

   public void mergeClasses(URL url, Beans beans) {
      this.getAlternativeClasses().addAll(beans.getAlternativeClasses());
      this.getAlternativeStereotypes().addAll(beans.getAlternativeStereotypes());
      this.getDecorators().addAll(beans.getDecorators());
      this.getInterceptors().addAll(beans.getInterceptors());
      this.interceptorsByUrl.put(url, beans.getInterceptors());
      this.decoratorsByUrl.put(url, beans.getDecorators());
      this.alternativesByUrl.put(url, beans.getAlternativeClasses());
      this.alternativeStereotypesByUrl.put(url, beans.getAlternativeStereotypes());
   }

   public Map<URL, Collection<String>> getInterceptorsByUrl() {
      return this.interceptorsByUrl;
   }

   public Map<URL, Collection<String>> getDecoratorsByUrl() {
      return this.decoratorsByUrl;
   }

   public Map<URL, Collection<String>> getAlternativesByUrl() {
      return this.alternativesByUrl;
   }

   public Map<URL, Collection<String>> getAlternativeStereotypesByUrl() {
      return this.alternativeStereotypesByUrl;
   }
}
