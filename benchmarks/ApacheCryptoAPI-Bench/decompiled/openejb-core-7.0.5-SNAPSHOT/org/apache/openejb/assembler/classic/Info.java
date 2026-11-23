package org.apache.openejb.assembler.classic;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.openejb.OpenEJBRuntimeException;

@XmlRootElement
public class Info {
   private static final JAXBContext JAXB_CONTEXT;
   public AppInfo appInfo;

   public Info(AppInfo appInfo) {
      this.appInfo = appInfo;
   }

   public Info() {
   }

   public static void marshal(AppInfo appInfo) throws JAXBException {
      marshal(appInfo, (OutputStream)System.out);
   }

   public static void marshal(AppInfo appInfo, OutputStream out) throws JAXBException {
      marshaller().marshal(new Info(appInfo), out);
   }

   public static void marshal(AppInfo appInfo, Writer out) throws JAXBException {
      marshaller().marshal(new Info(appInfo), out);
   }

   public static AppInfo unmarshal(InputStream in) throws JAXBException {
      return ((Info)unmarshaller().unmarshal(in)).appInfo;
   }

   private static Marshaller marshaller() throws JAXBException {
      Marshaller marshaller = JAXB_CONTEXT.createMarshaller();
      marshaller.setProperty("jaxb.formatted.output", true);
      return marshaller;
   }

   private static Unmarshaller unmarshaller() throws JAXBException {
      return JAXB_CONTEXT.createUnmarshaller();
   }

   static {
      try {
         JAXB_CONTEXT = JAXBContext.newInstance(new Class[]{Info.class});
      } catch (JAXBException var1) {
         throw new OpenEJBRuntimeException("can't create jaxbcontext for Info class");
      }
   }
}
