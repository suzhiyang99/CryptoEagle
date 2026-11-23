package org.apache.taverna.ui.perspectives.myexperiment.model;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

public class PackItem extends Resource {
   private int id;
   private User userWhoAddedThisItem;
   private String strComment;
   private boolean bInternalItem;
   private Resource item;
   private String strLink;
   private String strAlternateLink;

   public PackItem() {
      this.setItemType(0);
   }

   public int getID() {
      return this.id;
   }

   public void setID(int id) {
      this.id = id;
   }

   public void setID(String id) {
      this.id = Integer.parseInt(id);
   }

   public boolean isInternalItem() {
      return this.bInternalItem;
   }

   public void setInternalItem(boolean bIsInternalItem) {
      this.bInternalItem = bIsInternalItem;
   }

   public User getUserWhoAddedTheItem() {
      return this.userWhoAddedThisItem;
   }

   public void setUserWhoAddedTheItem(User userWhoAddedTheItem) {
      this.userWhoAddedThisItem = userWhoAddedTheItem;
   }

   public String getComment() {
      return this.strComment;
   }

   public void setComment(String strComment) {
      this.strComment = strComment;
   }

   public Resource getItem() {
      return this.item;
   }

   public void setItem(Resource item) {
      this.item = item;
   }

   public String getLink() {
      return this.strLink;
   }

   public void setLink(String strLink) {
      this.strLink = strLink;
   }

   public String getAlternateLink() {
      return this.strAlternateLink;
   }

   public void setAlternateLink(String strAlternateLink) {
      this.strAlternateLink = strAlternateLink;
   }

   public static PackItem buildFromXML(Document doc, Logger logger) {
      if (doc == null) {
         return null;
      } else {
         PackItem p = new PackItem();

         try {
            Element root = doc.getRootElement();
            p.setURI(root.getAttributeValue("uri"));
            p.setResource(root.getAttributeValue("resource"));
            String strID = root.getChildText("id");
            if (strID != null && !strID.equals("")) {
               p.setID(strID);
            } else {
               strID = "API Error - No pack item ID supplied";
               logger.error("Error while parsing pack item XML data - no ID provided for pack item with uri: \"" + p.getURI() + "\"");
            }

            Element ownerElement = root.getChild("owner");
            p.setUserWhoAddedTheItem(Util.instantiatePrimitiveUserFromElement(ownerElement));
            String createdAt = root.getChildText("created-at");
            if (createdAt != null && !createdAt.equals("")) {
               p.setCreatedAt(MyExperimentClient.parseDate(createdAt));
            }

            Element commentElement = root.getChild("comment");
            if (commentElement != null) {
               p.setComment(commentElement.getText());
            }

            if (root.getName().equals("internal-pack-item")) {
               p.setInternalItem(true);
               Element itemElement = (Element)root.getChild("item").getChildren().get(0);
               if (itemElement != null) {
                  p.setItem(Util.instantiatePrimitiveResourceFromElement(itemElement));
               }

               p.setItemType(p.getItem().getItemType());
               p.setTitle(p.getItem().getTitle());
            } else {
               p.setInternalItem(false);
               p.setItemType(15);
               p.setTitle(root.getChildText("title"));
               p.setLink(root.getChildText("uri"));
               p.setAlternateLink(root.getChildText("alternate-uri"));
            }

            logger.debug("Found information for pack item with URI: " + p.getURI());
         } catch (Exception e) {
            logger.error("Failed midway through creating pack item object from XML", e);
         }

         return p;
      }
   }
}
