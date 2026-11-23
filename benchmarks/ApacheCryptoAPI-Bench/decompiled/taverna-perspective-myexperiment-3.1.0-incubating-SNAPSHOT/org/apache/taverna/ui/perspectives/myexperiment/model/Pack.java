package org.apache.taverna.ui.perspectives.myexperiment.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

public class Pack extends Resource {
   private int accessType;
   private User creator;
   private List<Tag> tags;
   private List<Comment> comments;
   private ArrayList<PackItem> items;

   public Pack() {
      this.setItemType(12);
   }

   public int getAccessType() {
      return this.accessType;
   }

   public void setAccessType(int accessType) {
      this.accessType = accessType;
   }

   public User getCreator() {
      return this.creator;
   }

   public void setCreator(User creator) {
      this.creator = creator;
   }

   public List<Tag> getTags() {
      return this.tags;
   }

   public List<Comment> getComments() {
      return this.comments;
   }

   public int getItemCount() {
      return this.items.size();
   }

   public ArrayList<PackItem> getItems() {
      return this.items;
   }

   public static String getRequiredAPIElements(int iRequestType) {
      String strElements = "";
      switch (iRequestType) {
         case 5005:
            strElements = strElements + "created-at,updated-at,internal-pack-items,external-pack-items,tags,comments,";
         case 5010:
            strElements = strElements + "owner,";
         case 5015:
            strElements = strElements + "id,title,description,privileges";
         default:
            return strElements;
      }
   }

   public static Pack buildFromXML(Document doc, MyExperimentClient client, Logger logger) {
      return doc == null ? null : buildFromXML(doc.getRootElement(), client, logger);
   }

   public static Pack buildFromXML(Element docRootElement, MyExperimentClient client, Logger logger) {
      if (docRootElement == null) {
         return null;
      } else {
         Pack p = new Pack();

         try {
            p.setAccessType(Util.getAccessTypeFromXMLElement(docRootElement.getChild("privileges")));
            p.setURI(docRootElement.getAttributeValue("uri"));
            p.setResource(docRootElement.getAttributeValue("resource"));
            String id = docRootElement.getChildText("id");
            if (id == null || id.equals("")) {
               id = "API Error - No pack ID supplied";
               logger.error("Error while parsing pack XML data - no ID provided for pack with title: \"" + docRootElement.getChildText("title") + "\"");
            }

            p.setID(Integer.parseInt(id));
            p.setTitle(docRootElement.getChildText("title"));
            p.setDescription(docRootElement.getChildText("description"));
            Element ownerElement = docRootElement.getChild("owner");
            p.setCreator(Util.instantiatePrimitiveUserFromElement(ownerElement));
            String createdAt = docRootElement.getChildText("created-at");
            if (createdAt != null && !createdAt.equals("")) {
               p.setCreatedAt(MyExperimentClient.parseDate(createdAt));
            }

            String updatedAt = docRootElement.getChildText("updated-at");
            if (updatedAt != null && !updatedAt.equals("")) {
               p.setUpdatedAt(MyExperimentClient.parseDate(updatedAt));
            }

            p.tags = new ArrayList();
            p.getTags().addAll(Util.retrieveTags(docRootElement));
            p.comments = new ArrayList();
            p.getComments().addAll(Util.retrieveComments(docRootElement, p));
            p.items = new ArrayList();
            int iCount = 0;
            Element itemsElement = docRootElement.getChild("internal-pack-items");
            if (itemsElement != null) {
               for(Element e : itemsElement.getChildren()) {
                  Document docCurrentItem = client.getResource(14, e.getAttributeValue("uri"), 5100);
                  PackItem piCurrentItem = PackItem.buildFromXML(docCurrentItem, logger);
                  p.getItems().add(piCurrentItem);
                  ++iCount;
               }
            }

            itemsElement = docRootElement.getChild("external-pack-items");
            if (itemsElement != null) {
               for(Element e : itemsElement.getChildren()) {
                  Document docCurrentItem = client.getResource(15, e.getAttributeValue("uri"), 5100);
                  PackItem piCurrentItem = PackItem.buildFromXML(docCurrentItem, logger);
                  p.getItems().add(piCurrentItem);
                  ++iCount;
               }
            }

            Collections.sort(p.getItems());
            logger.debug("Found information for pack with ID: " + p.getID() + ", Title: " + p.getTitle());
         } catch (Exception e) {
            logger.error("Failed midway through creating pack object from XML", e);
         }

         return p;
      }
   }
}
