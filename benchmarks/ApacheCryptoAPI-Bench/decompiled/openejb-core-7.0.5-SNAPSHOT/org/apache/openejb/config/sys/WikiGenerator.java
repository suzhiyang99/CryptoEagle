package org.apache.openejb.config.sys;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.util.SuperProperties;

public class WikiGenerator {
   private final PrintWriter out;
   protected ServicesJar servicesJar;

   public static void main(String[] args) throws Exception {
      System.out.println();
      System.out.println();
      System.out.println();
      (new WikiGenerator("org.apache.tomee", new PrintWriter(System.out))).generate();
      System.out.println();
      System.out.println();
      System.out.println();
   }

   public WikiGenerator(String providerName, PrintWriter printWriter) throws OpenEJBException {
      this(JaxbOpenejb.readServicesJar(providerName), printWriter);
   }

   public WikiGenerator(ServicesJar servicesJar, PrintWriter out) {
      this.servicesJar = servicesJar;
      this.out = out;
   }

   public void generate() throws Exception {
      List<ServiceProvider> serviceProvider = this.servicesJar.getServiceProvider();
      Collections.sort(serviceProvider, new Comparator<ServiceProvider>() {
         public int compare(ServiceProvider o1, ServiceProvider o2) {
            return this.grade(o2) - this.grade(o1);
         }

         private int grade(ServiceProvider i) {
            String name = i.getClassName();
            if (name.contains("stateless")) {
               return 10;
            } else if (name.contains("stateful")) {
               return 9;
            } else if (name.contains("singleton")) {
               return 8;
            } else if (name.contains("mdb")) {
               return 7;
            } else {
               return name.contains("managed") ? 6 : 0;
            }
         }
      });

      for(ServiceProvider provider : serviceProvider) {
         if ("Container".equals(provider.getService())) {
            this.generateService(provider, "Container");
         }
      }

      this.out.println();
      ArrayList<String> seen = new ArrayList();

      for(ServiceProvider provider : this.servicesJar.getServiceProvider()) {
         if ("Resource".equals(provider.getService()) && !seen.containsAll(provider.getTypes())) {
            this.generateService(provider, "Resource");
            seen.addAll(provider.getTypes());
         }
      }

      this.out.println();
      this.out.flush();
   }

   private void generateService(ServiceProvider provider, String serviceType) {
      String type = (String)provider.getTypes().get(0);
      this.out.println("# " + type + " <small>" + serviceType + " </small>");
      this.out.println();
      this.out.println("Declarable in openejb.xml via");
      this.out.println();
      this.out.println("    <" + provider.getService() + " id=\"Foo\" type=\"" + type + "\">");
      this.out.println("    </" + provider.getService() + ">");
      this.out.println();
      this.out.println("Declarable in properties via");
      this.out.println();
      this.out.println("    Foo = new://" + provider.getService() + "?type=" + type);
      this.out.println();
      SuperProperties properties = (SuperProperties)provider.getProperties();
      Map<String, String> defaults = new LinkedHashMap();
      if (properties.size() > 0) {
         this.out.println("## Properties");
         this.out.println();

         for(Object key : properties.keySet()) {
            if (key instanceof String) {
               String name = (String)key;
               Map<String, String> attributes = properties.getAttributes(name);
               if (!attributes.containsKey("hidden")) {
                  this.out.println("### " + key);
                  this.out.println();
                  String value = properties.getProperty(name);
                  String comment = properties.getComment(name);
                  comment = this.scrubText(comment);
                  defaults.put(name, String.valueOf(value));
                  if (comment.length() == 0) {
                     comment = "No description.";
                  }

                  this.out.println(comment);
                  this.out.println();
               }
            }
         }

         this.out.println("## Default declaration");
         this.out.println("    <" + provider.getService() + " id=\"" + provider.getId() + "\" type=\"" + type + "\">");

         for(Map.Entry<String, String> entry : defaults.entrySet()) {
            this.out.print("        ");
            this.out.print((String)entry.getKey());
            this.out.print(" = ");
            this.out.println((String)entry.getValue());
         }

         this.out.println("    </" + provider.getService() + ">");
      } else {
         this.out.println("No properties.");
      }

      this.out.println();
   }

   private String scrubText(String text) {
      if (text == null) {
         text = "";
      }

      return text;
   }
}
