package org.apache.taverna.ui.perspectives.myexperiment.model;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

public class Comment extends Resource {
   private User user;
   private String comment;
   private int typeOfCommentedResource;
   private String uriOfCommetedResource;

   public Comment() {
      this.setItemType(31);
   }

   public User getUser() {
      return this.user;
   }

   public void setUser(User user) {
      this.user = user;
   }

   public String getComment() {
      return this.comment;
   }

   public void setComment(String comment) {
      this.comment = comment;
   }

   public int getTypeOfCommentedResource() {
      return this.typeOfCommentedResource;
   }

   public void setTypeOfCommentedResource(int typeOfCommentedResource) {
      this.typeOfCommentedResource = typeOfCommentedResource;
   }

   public String getURIOfCommentedResource() {
      return this.uriOfCommetedResource;
   }

   public void setURIOfCommentedResource(String uriOfCommetedResource) {
      this.uriOfCommetedResource = uriOfCommetedResource;
   }

   public static String getRequiredAPIElements(int iRequestType) {
      String strElements = "";
      switch (iRequestType) {
         case 5100:
            strElements = strElements + "";
         default:
            return strElements;
      }
   }

   public static Comment buildFromXML(Document doc, Logger logger) {
      if (doc == null) {
         return null;
      } else {
         Comment c = new Comment();

         try {
            Element root = doc.getRootElement();
            c.setResource(root.getAttributeValue("resource"));
            c.setURI(root.getAttributeValue("uri"));
            c.setTitle(root.getChildText("comment"));
            c.setComment(root.getChildText("comment"));
            Element commentedResourceElement = root.getChild("subject");
            if (commentedResourceElement != null) {
               c.setTypeOfCommentedResource(Resource.getResourceTypeFromVisibleName(commentedResourceElement.getName()));
               c.setURIOfCommentedResource(commentedResourceElement.getAttributeValue("uri"));
            }

            Element userElement = root.getChild("author");
            c.setUser(Util.instantiatePrimitiveUserFromElement(userElement));
            String createdAt = root.getChildText("created-at");
            if (createdAt != null && !createdAt.equals("")) {
               c.setCreatedAt(MyExperimentClient.parseDate(createdAt));
            }

            logger.debug("Found information for comment with ID: " + c.getID() + ", URI: " + c.getURI());
         } catch (Exception e) {
            logger.error("Failed midway through creating comment object from XML", e);
         }

         return c;
      }
   }
}
