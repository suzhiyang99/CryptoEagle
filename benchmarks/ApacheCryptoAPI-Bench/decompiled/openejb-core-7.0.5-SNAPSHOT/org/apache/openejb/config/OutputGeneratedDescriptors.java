package org.apache.openejb.config;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.jee.Connector;
import org.apache.openejb.jee.EjbJar;
import org.apache.openejb.jee.JAXBContextFactory;
import org.apache.openejb.jee.jpa.EntityMappings;
import org.apache.openejb.jee.jpa.JpaJaxbUtil;
import org.apache.openejb.jee.jpa.unit.Persistence;
import org.apache.openejb.jee.jpa.unit.PersistenceUnit;
import org.apache.openejb.jee.oejb2.GeronimoEjbJarType;
import org.apache.openejb.jee.oejb2.JaxbOpenejbJar2;
import org.apache.openejb.jee.oejb3.JaxbOpenejbJar3;
import org.apache.openejb.jee.oejb3.OpenejbJar;
import org.apache.openejb.loader.IO;
import org.apache.openejb.loader.Options;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.sxc.EjbJarXml;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public class OutputGeneratedDescriptors implements DynamicDeployer {
   private static final Logger logger;
   public static final String OUTPUT_DESCRIPTORS = "openejb.descriptors.output";
   public static final String OUTPUT_DESCRIPTORS_FOLDER = "openejb.descriptors.output.folder";

   public AppModule deploy(AppModule appModule) throws OpenEJBException {
      boolean output = SystemInstance.get().getOptions().get("openejb.descriptors.output", false);
      if (output && appModule.getCmpMappings() != null) {
         this.writeGenratedCmpMappings(appModule);
      }

      for(EjbModule ejbModule : appModule.getEjbModules()) {
         Options options = new Options(ejbModule.getOpenejbJar().getProperties(), SystemInstance.get().getOptions());
         ValidationContext context = ejbModule.getValidation();
         boolean invalid = context.hasErrors() || context.hasFailures();
         output = options.get("openejb.descriptors.output", invalid);
         if (output) {
            if (ejbModule.getEjbJar() != null) {
               this.writeEjbJar(ejbModule);
            }

            if (ejbModule.getOpenejbJar() != null) {
               this.writeOpenejbJar(ejbModule);
            }

            this.writeGeronimoOpenejb(ejbModule);
         }
      }

      for(ConnectorModule connectorModule : appModule.getConnectorModules()) {
         this.writeRaXml(connectorModule);
      }

      return appModule;
   }

   private void writeRaXml(ConnectorModule connectorModule) {
      try {
         Connector connector = connectorModule.getConnector();
         File tempFile = this.tempFile("ra-", connectorModule.getModuleId() + ".xml");
         OutputStream out = IO.write(tempFile);

         try {
            JAXBContext ctx = JAXBContextFactory.newInstance(new Class[]{Connector.class});
            Marshaller marshaller = ctx.createMarshaller();
            marshaller.marshal(connector, out);
            logger.info("Dumping Generated ra.xml to: " + tempFile.getAbsolutePath());
         } catch (JAXBException var11) {
         } finally {
            IO.close(out);
         }
      } catch (IOException var13) {
      }

   }

   private File tempFile(String start, String end) throws IOException {
      if (SystemInstance.get().getOptions().get("openejb.descriptors.output.folder", (String)null) != null) {
         File tmp = new File(SystemInstance.get().getOptions().get("openejb.descriptors.output.folder", ""));
         if (!tmp.exists() && !tmp.mkdirs()) {
            throw new IOException("can't create " + tmp.getAbsolutePath());
         } else {
            return new File(tmp, start + Long.toString((long)(new Random()).nextInt()) + end);
         }
      } else {
         try {
            return File.createTempFile(start, end);
         } catch (Throwable var5) {
            File tmp = new File("tmp");
            if (!tmp.exists() && !tmp.mkdirs()) {
               throw new IOException("Failed to create local tmp directory: " + tmp.getAbsolutePath());
            } else {
               return File.createTempFile(start, end, tmp);
            }
         }
      }
   }

   private void writeGenratedCmpMappings(AppModule appModule) {
      for(PersistenceModule persistenceModule : appModule.getPersistenceModules()) {
         try {
            Persistence persistence = persistenceModule.getPersistence();
            if (this.hasCmpPersistenceUnit(persistence)) {
               File tempFile = this.tempFile("persistence-", ".xml");
               OutputStream out = IO.write(tempFile);

               try {
                  JpaJaxbUtil.marshal(Persistence.class, persistence, out);
                  logger.info("Dumping Generated CMP persistence.xml to: " + tempFile.getAbsolutePath());
               } catch (JAXBException var25) {
               } finally {
                  IO.close(out);
               }
            }
         } catch (IOException var27) {
         }
      }

      try {
         File tempFile = this.tempFile("openejb-cmp-generated-orm-", ".xml");
         OutputStream out = IO.write(tempFile);

         try {
            JpaJaxbUtil.marshal(EntityMappings.class, appModule.getCmpMappings(), out);
            logger.info("Dumping Generated CMP mappings.xml to: " + tempFile.getAbsolutePath());
         } catch (JAXBException var22) {
         } finally {
            IO.close(out);
         }
      } catch (IOException var24) {
      }

   }

   private boolean hasCmpPersistenceUnit(Persistence persistence) {
      for(PersistenceUnit unit : persistence.getPersistenceUnit()) {
         if (unit.getName().startsWith("cmp")) {
            return true;
         }
      }

      return false;
   }

   private void writeOpenejbJar(EjbModule ejbModule) {
      try {
         OpenejbJar openejbJar = ejbModule.getOpenejbJar();
         File tempFile = this.tempFile("openejb-jar-", ejbModule.getModuleId() + ".xml");
         OutputStream out = IO.write(tempFile);

         try {
            JaxbOpenejbJar3.marshal(OpenejbJar.class, openejbJar, out);
            logger.info("Dumping Generated openejb-jar.xml to: " + tempFile.getAbsolutePath());
         } catch (JAXBException var10) {
         } finally {
            IO.close(out);
         }
      } catch (Exception var12) {
      }

   }

   private void writeGeronimoOpenejb(EjbModule ejbModule) {
      try {
         GeronimoEjbJarType geronimoEjbJarType = (GeronimoEjbJarType)ejbModule.getAltDDs().get("geronimo-openejb.xml");
         if (geronimoEjbJarType == null) {
            return;
         }

         File tempFile = this.tempFile("geronimo-openejb-", ejbModule.getModuleId() + ".xml");
         OutputStream out = IO.write(tempFile);

         try {
            JaxbOpenejbJar2.marshal(GeronimoEjbJarType.class, geronimoEjbJarType, out);
            logger.info("Dumping Generated geronimo-openejb.xml to: " + tempFile.getAbsolutePath());
         } catch (JAXBException var10) {
         } finally {
            IO.close(out);
         }
      } catch (Exception var12) {
      }

   }

   private void writeEjbJar(EjbModule ejbModule) {
      try {
         EjbJar ejbJar = ejbModule.getEjbJar();
         File tempFile = this.tempFile("ejb-jar-", ejbModule.getModuleId() + ".xml");
         OutputStream out = IO.write(tempFile);

         try {
            EjbJarXml.marshal(ejbJar, out);
            logger.info("Dumping Generated ejb-jar.xml to: " + tempFile.getAbsolutePath());
         } catch (JAXBException var10) {
         } finally {
            IO.close(out);
         }
      } catch (Exception var12) {
      }

   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB_STARTUP_CONFIG, "org.apache.openejb.util.resources");
   }
}
