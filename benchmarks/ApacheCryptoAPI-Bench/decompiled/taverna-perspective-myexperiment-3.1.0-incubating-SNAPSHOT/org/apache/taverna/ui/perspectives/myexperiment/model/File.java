package org.apache.taverna.ui.perspectives.myexperiment.model;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

public class File extends Resource {
   private int accessType;
   private User uploader;
   private License license;
   private String filename;
   private String visibleType;
   private String contentType;
   private List<Tag> tags;
   private List<Comment> comments;
   private List<Resource> credits;
   private List<Resource> attributions;

   public File() {
      this.setItemType(11);
   }

   public int getAccessType() {
      return this.accessType;
   }

   public void setAccessType(int accessType) {
      this.accessType = accessType;
   }

   public List<Tag> getTags() {
      return this.tags;
   }

   public User getUploader() {
      return this.uploader;
   }

   public void setUploader(User uploader) {
      this.uploader = uploader;
   }

   public License getLicense() {
      return this.license;
   }

   public void setLicense(License license) {
      this.license = license;
   }

   public String getFilename() {
      return this.filename;
   }

   public void setFilename(String filename) {
      this.filename = filename;
   }

   public String getContentType() {
      return this.contentType;
   }

   public void setContentType(String contentType) {
      this.contentType = contentType;
   }

   public String getVisibleType() {
      return this.visibleType;
   }

   public void setVisibleType(String visibleType) {
      this.visibleType = visibleType;
   }

   public List<Comment> getComments() {
      return this.comments;
   }

   public List<Resource> getCredits() {
      return this.credits;
   }

   public List<Resource> getAttributions() {
      return this.attributions;
   }

   public static String getRequiredAPIElements(int iRequestType) {
      String strElements = "";
      switch (iRequestType) {
         case 5005:
            strElements = strElements + "filename,content-type,created-at,updated-at,license-type,tags,comments,credits,attributions,";
         case 5010:
            strElements = strElements + "uploader,type,";
         case 5015:
            strElements = strElements + "id,title,description,privileges";
         default:
            return strElements;
      }
   }

   public static File buildFromXML(Document doc, Logger logger) {
      return doc == null ? null : buildFromXML(doc.getRootElement(), logger);
   }

   public static File buildFromXML(Element docRootElement, Logger logger) {
      if (docRootElement == null) {
         return null;
      } else {
         File f = new File();

         try {
            f.setAccessType(Util.getAccessTypeFromXMLElement(docRootElement.getChild("privileges")));
            f.setURI(docRootElement.getAttributeValue("uri"));
            f.setResource(docRootElement.getAttributeValue("resource"));
            String id = docRootElement.getChildText("id");
            if (id == null || id.equals("")) {
               id = "API Error - No file ID supplied";
               logger.error("Error while parsing file XML data - no ID provided for file with title: \"" + docRootElement.getChildText("title") + "\"");
            }

            f.setID(id);
            f.setFilename(docRootElement.getChildText("filename"));
            f.setTitle(docRootElement.getChildText("title"));
            f.setDescription(docRootElement.getChildText("description"));
            Element uploaderElement = docRootElement.getChild("uploader");
            f.setUploader(Util.instantiatePrimitiveUserFromElement(uploaderElement));
            String createdAt = docRootElement.getChildText("created-at");
            if (createdAt != null && !createdAt.equals("")) {
               f.setCreatedAt(MyExperimentClient.parseDate(createdAt));
            }

            String updatedAt = docRootElement.getChildText("updated-at");
            if (updatedAt != null && !updatedAt.equals("")) {
               f.setUpdatedAt(MyExperimentClient.parseDate(updatedAt));
            }

            f.setLicense(License.getInstance(docRootElement.getChildText("license-type")));
            f.setVisibleType(docRootElement.getChildText("type"));
            f.setContentType(docRootElement.getChildText("content-type"));
            f.tags = new ArrayList();
            f.getTags().addAll(Util.retrieveTags(docRootElement));
            f.comments = new ArrayList();
            f.getComments().addAll(Util.retrieveComments(docRootElement, f));
            f.credits = new ArrayList();
            f.getCredits().addAll(Util.retrieveCredits(docRootElement));
            f.attributions = new ArrayList();
            f.getAttributions().addAll(Util.retrieveAttributions(docRootElement));
            logger.debug("Found information for file with ID: " + f.getID() + ", Title: " + f.getTitle());
         } catch (Exception e) {
            logger.error("Failed midway through creating file object from XML", e);
         }

         return f;
      }
   }
}
