package org.apache.taverna.ui.perspectives.myexperiment.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.ImageIcon;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

public class User extends Resource {
   private String name;
   private String city;
   private String country;
   private String email;
   private String website;
   private ImageIcon avatar;
   private String avatar_uri;
   private String avatar_resource;
   private ArrayList<HashMap<String, String>> workflows;
   private ArrayList<HashMap<String, String>> files;
   private ArrayList<HashMap<String, String>> packs;
   private ArrayList<HashMap<String, String>> friends;
   private ArrayList<HashMap<String, String>> groups;
   private ArrayList<HashMap<String, String>> tags;
   private ArrayList<Resource> favourites;

   public User() {
      this.setItemType(20);
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
      this.setTitle(name);
   }

   public String getCity() {
      return this.city;
   }

   public void setCity(String city) {
      this.city = city;
   }

   public String getCountry() {
      return this.country;
   }

   public void setCountry(String country) {
      this.country = country;
   }

   public String getEmail() {
      return this.email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public String getWebsite() {
      return this.website;
   }

   public void setWebsite(String website) {
      this.website = website;
   }

   public String getAvatarURI() {
      return this.avatar_uri;
   }

   public void setAvatarURI(String avatar_uri) {
      this.avatar_uri = avatar_uri;
   }

   public ImageIcon getAvatar() {
      return this.avatar;
   }

   public void setAvatar(Document doc) {
      Element root = doc.getRootElement();
      String strAvatarData = root.getChild("data").getText();
      this.avatar = new ImageIcon(Base64.decode(strAvatarData));
   }

   public void setAvatar(ImageIcon avatar) {
      this.avatar = avatar;
   }

   public String getAvatarResource() {
      return this.avatar_resource;
   }

   public void setAvatarResource(String avatar_resource) {
      this.avatar_resource = avatar_resource;
   }

   public ArrayList<HashMap<String, String>> getWorkflows() {
      return this.workflows;
   }

   public ArrayList<HashMap<String, String>> getFiles() {
      return this.files;
   }

   public ArrayList<HashMap<String, String>> getPacks() {
      return this.packs;
   }

   public ArrayList<HashMap<String, String>> getFriends() {
      return this.friends;
   }

   public ArrayList<HashMap<String, String>> getGroups() {
      return this.groups;
   }

   public ArrayList<Resource> getFavourites() {
      return this.favourites;
   }

   public ArrayList<HashMap<String, String>> getTags() {
      return this.tags;
   }

   public static String getRequiredAPIElements(int iRequestType) {
      String strElements = "";
      switch (iRequestType) {
         case 5005:
            strElements = strElements + "created-at,updated-at,email,website,city,country,friends,groups,workflows,files,packs,favourited,tags-applied,";
         case 5010:
            strElements = strElements + "";
         case 5015:
            strElements = strElements + "id,name,description,avatar";
            break;
         case 5050:
            strElements = strElements + "favourited";
            break;
         case 5051:
            strElements = strElements + "tags-applied";
      }

      return strElements;
   }

   public static User buildFromXML(Document doc, Logger logger) {
      return doc == null ? null : buildFromXML(doc.getRootElement(), logger);
   }

   public static User buildFromXML(Element docRootElement, Logger logger) {
      if (docRootElement == null) {
         return null;
      } else {
         User user = new User();

         try {
            user.setURI(docRootElement.getAttributeValue("uri"));
            user.setResource(docRootElement.getAttributeValue("resource"));
            user.setID(docRootElement.getChildText("id"));
            user.setName(docRootElement.getChildText("name"));
            user.setTitle(user.getName());
            user.setDescription(docRootElement.getChild("description").getText());
            user.setCity(docRootElement.getChildText("city"));
            user.setCountry(docRootElement.getChildText("country"));
            user.setEmail(docRootElement.getChildText("email"));
            user.setWebsite(docRootElement.getChildText("website"));
            Element avatarURIElement = docRootElement.getChild("avatar");
            if (avatarURIElement != null) {
               user.setAvatarURI(avatarURIElement.getAttributeValue("uri"));
            }

            Element avatarElement = docRootElement.getChild("avatar");
            if (avatarElement != null) {
               user.setAvatarResource(avatarElement.getAttributeValue("resource"));
            }

            String createdAt = docRootElement.getChildText("created-at");
            if (createdAt != null && !createdAt.equals("")) {
               user.setCreatedAt(MyExperimentClient.parseDate(createdAt));
            }

            String updatedAt = docRootElement.getChildText("updated-at");
            if (updatedAt != null && !updatedAt.equals("")) {
               user.setUpdatedAt(MyExperimentClient.parseDate(updatedAt));
            }

            user.workflows = new ArrayList();
            Element workflowsElement = docRootElement.getChild("workflows");
            if (workflowsElement != null) {
               Iterator<Element> iWorkflows = workflowsElement.getChildren().iterator();
               Util.getResourceCollectionFromXMLIterator(iWorkflows, user.workflows);
            }

            user.files = new ArrayList();
            Element filesElement = docRootElement.getChild("files");
            if (filesElement != null) {
               Iterator<Element> iFiles = filesElement.getChildren().iterator();
               Util.getResourceCollectionFromXMLIterator(iFiles, user.files);
            }

            user.packs = new ArrayList();
            Element packsElement = docRootElement.getChild("packs");
            if (packsElement != null) {
               Iterator<Element> iPacks = packsElement.getChildren().iterator();
               Util.getResourceCollectionFromXMLIterator(iPacks, user.packs);
            }

            user.friends = new ArrayList();
            Element friendsElement = docRootElement.getChild("friends");
            if (filesElement != null) {
               Iterator<Element> iFriends = friendsElement.getChildren().iterator();
               Util.getResourceCollectionFromXMLIterator(iFriends, user.friends);
            }

            user.groups = new ArrayList();
            Element groupsElement = docRootElement.getChild("groups");
            if (groupsElement != null) {
               Iterator<Element> iGroups = groupsElement.getChildren().iterator();
               Util.getResourceCollectionFromXMLIterator(iGroups, user.groups);
            }

            user.tags = new ArrayList();
            Element tagsElement = docRootElement.getChild("tags-applied");
            if (tagsElement != null) {
               Iterator<Element> iTags = tagsElement.getChildren().iterator();
               Util.getResourceCollectionFromXMLIterator(iTags, user.tags);
            }

            user.favourites = new ArrayList();
            user.favourites.addAll(Util.retrieveUserFavourites(docRootElement));
         } catch (Exception e) {
            logger.error("Failed midway through creating user object from XML", e);
         }

         return user;
      }
   }
}
