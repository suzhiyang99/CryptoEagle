package org.apache.meecrowave.tomcat;

import java.util.Set;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.meecrowave.Meecrowave;

public class TomcatAutoInitializer implements ServletContainerInitializer {
   public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
      Meecrowave.Builder builder = (Meecrowave.Builder)Meecrowave.Builder.class.cast(ctx.getAttribute("meecrowave.configuration"));
      if (builder.isTomcatAutoSetup()) {
         ServletRegistration.Dynamic def = ctx.addServlet("default", DefaultServlet.class);
         def.setInitParameter("listings", "false");
         def.setInitParameter("debug", "0");
         def.setLoadOnStartup(1);
         def.addMapping(new String[]{"/"});

         try {
            String jsp = "org.apache.jasper.servlet.JspServlet";
            TomcatAutoInitializer.class.getClassLoader().loadClass("org.apache.jasper.servlet.JspServlet");
            ServletRegistration.Dynamic jspDef = ctx.addServlet("jsp", "org.apache.jasper.servlet.JspServlet");
            if (jspDef != null) {
               jspDef.setInitParameter("fork", "false");
               jspDef.setInitParameter("xpoweredBy", "false");
               jspDef.setInitParameter("development", "false");
               jspDef.setLoadOnStartup(3);
               def.addMapping(new String[]{"*.jsp"});
               def.addMapping(new String[]{"*.jspx"});
            }
         } catch (ClassNotFoundException | NoClassDefFoundError var7) {
         }

      }
   }
}
