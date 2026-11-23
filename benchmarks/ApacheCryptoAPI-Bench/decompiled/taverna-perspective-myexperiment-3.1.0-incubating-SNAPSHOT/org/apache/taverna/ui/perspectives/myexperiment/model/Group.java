package org.apache.taverna.ui.perspectives.myexperiment.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

public class Group extends Resource {
   private User admin;
   private List<Tag> tags;
   private List<Comment> comments;
   private List<User> members;
   private List<Resource> sharedItems;

   public Group() {
      this.setItemType(21);
   }

   public User getAdmin() {
      return this.admin;
   }

   public void setAdmin(User admin) {
      this.admin = admin;
   }

   public List<Tag> getTags() {
      return this.tags;
   }

   public List<Comment> getComments() {
      return this.comments;
   }

   public int getSharedItemCount() {
      return this.sharedItems.size();
   }

   public int getMemberCount() {
      return this.members.size();
   }

   public List<Resource> getSharedItems() {
      return this.sharedItems;
   }

   public List<User> getMembers() {
      return this.members;
   }

   public static String getRequiredAPIElements(int iRequestType) {
      String strElements = "";
      switch (iRequestType) {
         case 5005:
            strElements = strElements + "created-at,updated-at,members,shared-items,tags,comments,";
         case 5010:
            strElements = strElements + "owner,";
         case 5015:
            strElements = strElements + "id,title,description";
         default:
            return strElements;
      }
   }

   public static Group buildFromXML(Document doc, Logger logger) {
      return doc == null ? null : buildFromXML(doc.getRootElement(), logger);
   }

   public static Group buildFromXML(Element docRootElement, Logger logger) {
      if (docRootElement == null) {
         return null;
      } else {
         Group g = new Group();

         try {
            g.setURI(docRootElement.getAttributeValue("uri"));
            g.setResource(docRootElement.getAttributeValue("resource"));
            String id = docRootElement.getChildText("id");
            if (id == null || id.equals("")) {
               id = "API Error - No group ID supplied";
               logger.error("Error while parsing group XML data - no ID provided for group with title: \"" + docRootElement.getChildText("title") + "\"");
            }

            g.setID(Integer.parseInt(id));
            g.setTitle(docRootElement.getChildText("title"));
            g.setDescription(docRootElement.getChildText("description"));
            Element ownerElement = docRootElement.getChild("owner");
            g.setAdmin(Util.instantiatePrimitiveUserFromElement(ownerElement));
            String createdAt = docRootElement.getChildText("created-at");
            if (createdAt != null && !createdAt.equals("")) {
               g.setCreatedAt(MyExperimentClient.parseDate(createdAt));
            }

            String updatedAt = docRootElement.getChildText("updated-at");
            if (updatedAt != null && !updatedAt.equals("")) {
               g.setUpdatedAt(MyExperimentClient.parseDate(updatedAt));
            }

            g.tags = new ArrayList();
            g.getTags().addAll(Util.retrieveTags(docRootElement));
            g.comments = new ArrayList();
            g.getComments().addAll(Util.retrieveComments(docRootElement, g));
            g.members = new ArrayList();
            Element membersElement = docRootElement.getChild("members");
            if (membersElement != null) {
               for(Element e : membersElement.getChildren()) {
                  g.getMembers().add(Util.instantiatePrimitiveUserFromElement(e));
               }
            }

            Collections.sort(g.getMembers());
            g.sharedItems = new ArrayList();
            Element sharedItemsElement = docRootElement.getChild("shared-items");
            if (sharedItemsElement != null) {
               for(Element e : sharedItemsElement.getChildren()) {
                  g.getSharedItems().add(Util.instantiatePrimitiveResourceFromElement(e));
               }
            }

            Collections.sort(g.getSharedItems());
            logger.debug("Found information for group with ID: " + g.getID() + ", Title: " + g.getTitle());
         } catch (Exception e) {
            logger.error("Failed midway through creating group object from XML", e);
         }

         return g;
      }
   }
}
