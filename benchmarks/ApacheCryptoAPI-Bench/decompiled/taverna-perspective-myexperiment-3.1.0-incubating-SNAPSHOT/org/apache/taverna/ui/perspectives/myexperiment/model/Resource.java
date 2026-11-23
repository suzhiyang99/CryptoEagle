package org.apache.taverna.ui.perspectives.myexperiment.model;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.Serializable;
import java.net.URI;
import java.util.Date;
import java.util.EventListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import org.apache.log4j.Logger;
import org.apache.taverna.ui.perspectives.myexperiment.MainComponent;
import org.apache.taverna.ui.perspectives.myexperiment.MyExperimentPerspective;
import org.apache.taverna.ui.perspectives.myexperiment.StyledHTMLEditorKit;
import org.jdom.Document;
import org.jdom.Element;

public class Resource implements Comparable<Resource>, Serializable {
   public static final int UNEXPECTED_TYPE = -1;
   public static final int UNKNOWN = 0;
   public static final int WORKFLOW = 10;
   public static final int FILE = 11;
   public static final int PACK = 12;
   public static final int PACK_INTERNAL_ITEM = 14;
   public static final int PACK_EXTERNAL_ITEM = 15;
   public static final int USER = 20;
   public static final int GROUP = 21;
   public static final int TAG = 30;
   public static final int COMMENT = 31;
   public static final String WORKFLOW_VISIBLE_NAME = "Workflow";
   public static final String FILE_VISIBLE_NAME = "File";
   public static final String PACK_VISIBLE_NAME = "Pack";
   public static final String USER_VISIBLE_NAME = "User";
   public static final String GROUP_VISIBLE_NAME = "Group";
   public static final String TAG_VISIBLE_NAME = "Tag";
   public static final String COMMENT_VISIBLE_NAME = "Comment";
   public static final String UNKWNOWN_VISIBLE_NAME = "Unknown";
   public static final String UNEXPECTED_TYPE_VISIBLE_NAME = "ERROR: Unexpected unknown type!";
   public static final int ACCESS_VIEWING = 1000;
   public static final int ACCESS_DOWNLOADING = 1001;
   public static final int ACCESS_EDITING = 1002;
   public static final int REQUEST_ALL_DATA = 5000;
   public static final int REQUEST_FULL_PREVIEW = 5005;
   public static final int REQUEST_FULL_LISTING = 5010;
   public static final int REQUEST_SHORT_LISTING = 5015;
   public static final int REQUEST_USER_FAVOURITES_ONLY = 5050;
   public static final int REQUEST_USER_APPLIED_TAGS_ONLY = 5051;
   public static final int REQUEST_WORKFLOW_CONTENT_ONLY = 5055;
   public static final int REQUEST_DEFAULT_FROM_API = 5100;
   private int iID;
   private String uri;
   private String resource;
   private String title;
   private int itemType;
   private Date createdAt;
   private Date updatedAt;
   private String description;

   public int getID() {
      return this.iID;
   }

   public void setID(int id) {
      this.iID = id;
   }

   public void setID(String id) {
      this.iID = Integer.parseInt(id);
   }

   public String getURI() {
      return this.uri;
   }

   public String getResource() {
      return this.resource;
   }

   public int getItemType() {
      return this.itemType;
   }

   public String getItemTypeName() {
      return getResourceTypeName(this.itemType);
   }

   public String getTitle() {
      return this.title;
   }

   public String getDescription() {
      return this.description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public void setURI(String uri) {
      this.uri = uri;
   }

   public void setResource(String resource) {
      this.resource = resource;
   }

   public void setItemType(int type) {
      this.itemType = type;
   }

   public void setItemType(String type) {
      this.itemType = getResourceTypeFromVisibleName(type);
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public Date getCreatedAt() {
      return this.createdAt;
   }

   public void setCreatedAt(Date createdAt) {
      this.createdAt = createdAt;
   }

   public Date getUpdatedAt() {
      return this.updatedAt;
   }

   public void setUpdatedAt(Date updatedAt) {
      this.updatedAt = updatedAt;
   }

   public String toString() {
      return "(" + this.getItemTypeName() + ", " + this.getURI() + "," + this.getTitle() + ")";
   }

   public int compareTo(Resource other) {
      int iTypesCompared = this.getItemType() - other.getItemType();
      return iTypesCompared == 0 ? this.getTitle().compareTo(other.getTitle()) : iTypesCompared;
   }

   public boolean equals(Object other) {
      if (!(other instanceof Resource)) {
         return false;
      } else {
         Resource otherRes = (Resource)other;
         return this.itemType == otherRes.itemType && this.uri.equals(otherRes.uri) && this.resource.equals(otherRes.resource);
      }
   }

   public boolean hasUploader() {
      return this.itemType == 10 || this.itemType == 11;
   }

   public User getUploader() {
      switch (this.itemType) {
         case 10:
            return ((Workflow)this).getUploader();
         case 11:
            return ((File)this).getUploader();
         default:
            return null;
      }
   }

   public boolean hasCreator() {
      return this.itemType == 12;
   }

   public User getCreator() {
      switch (this.itemType) {
         case 12:
            return ((Pack)this).getCreator();
         default:
            return null;
      }
   }

   public boolean hasAdmin() {
      return this.itemType == 21;
   }

   public User getAdmin() {
      switch (this.itemType) {
         case 21:
            return ((Group)this).getAdmin();
         default:
            return null;
      }
   }

   public boolean isFavouritable() {
      switch (this.itemType) {
         case 10:
         case 11:
         case 12:
            return true;
         default:
            return false;
      }
   }

   public boolean isFavouritedBy(User user) {
      for(Resource r : user.getFavourites()) {
         if (r.getURI().equals(this.getURI())) {
            return true;
         }
      }

      return false;
   }

   public boolean isCommentableOn() {
      switch (this.itemType) {
         case 10:
         case 11:
         case 12:
         case 21:
            return true;
         default:
            return false;
      }
   }

   public List<Comment> getComments() {
      switch (this.itemType) {
         case 10:
            return ((Workflow)this).getComments();
         case 11:
            return ((File)this).getComments();
         case 12:
            return ((Pack)this).getComments();
         case 21:
            return ((Group)this).getComments();
         default:
            return null;
      }
   }

   public boolean isDownloadable() {
      return this.itemType == 10 || this.itemType == 11 || this.itemType == 12;
   }

   public boolean isDownloadAllowed() {
      int iAccessType = 0;
      switch (this.itemType) {
         case 10:
            iAccessType = ((Workflow)this).getAccessType();
            break;
         case 11:
            iAccessType = ((File)this).getAccessType();
            break;
         case 12:
            iAccessType = ((Pack)this).getAccessType();
            break;
         default:
            iAccessType = 0;
      }

      return iAccessType >= 1001;
   }

   public boolean hasVisibleType() {
      return this.itemType == 10 || this.itemType == 11;
   }

   public String getVisibleType() {
      switch (this.itemType) {
         case 10:
            return ((Workflow)this).getVisibleType();
         case 11:
            return ((File)this).getVisibleType();
         default:
            return null;
      }
   }

   public JPanel createListViewPanel(boolean bCreateFullSizeView, MainComponent pluginMainComponent, EventListener eventHandler, Logger logger) {
      try {
         JPanel mainPanel = new JPanel(new BorderLayout());
         mainPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
         JTextPane infoTextPane = new JTextPane();
         infoTextPane.setBorder(BorderFactory.createEmptyBorder());
         infoTextPane.setEditable(false);
         StringBuffer content = new StringBuffer();
         content.append("<div class='list_item_container'>");
         content.append("<div class='list_item'>");
         content.append("<p class='title'>");
         content.append("<a href='preview:" + this.getItemType() + ":" + this.getURI() + "'>" + this.getTitle() + (this.getItemType() == 10 ? " (version " + ((Workflow)this).getVersion() + ")" : "") + "</a>");
         content.append("</p>");
         if (bCreateFullSizeView) {
            if (this.hasUploader()) {
               content.append("<p class='uploader'>");
               content.append("Uploader: <a href='preview:20:" + this.getUploader().getURI() + "'>" + this.getUploader().getName() + "</a>");
               content.append("</p>");
            } else if (this.hasCreator()) {
               content.append("<p class='uploader'>");
               content.append("Creator: <a href='preview:20:" + this.getCreator().getURI() + "'>" + this.getCreator().getName() + "</a>");
               content.append("</p>");
            } else if (this.hasAdmin()) {
               content.append("<p class='uploader'>");
               content.append("Administrator: <a href='preview:20:" + this.getAdmin().getURI() + "'>" + this.getAdmin().getName() + "</a>");
               content.append("</p>");
            }

            if (this.hasVisibleType()) {
               content.append("<p class='uploader'>");
               content.append("Type: " + this.getVisibleType());
               content.append("</p>");
            }
         }

         content.append("<div class='desc'>");
         content.append("<table style='margin-top: 5px; margin-bottom: 5px;'>");
         content.append("<tr>");
         if (this.itemType == 10 || this.itemType == 20) {
            boolean bManualResizeNeeded = false;
            URI previewURI = null;
            if (this.itemType == 10) {
               previewURI = ((Workflow)this).getThumbnail();
            } else if (this.itemType == 20 && ((User)this).getAvatarResource() != null) {
               previewURI = new URI(((User)this).getAvatarResource());
               bManualResizeNeeded = true;
            }

            content.append("<td valign='top'>");
            content.append("<a href='preview:" + this.itemType + ":" + this.getURI() + "'>");
            if (bCreateFullSizeView) {
               if (!bManualResizeNeeded) {
                  content.append("<img class='preview' src='" + previewURI + "'></img>");
               } else {
                  String resizedImageURL = Util.getResizedImageIconTempFileURL(previewURI.toURL(), 90, 90);
                  content.append("<img class='preview' src='" + resizedImageURL + "'></img>");
               }
            } else {
               String resizedImageURL = Util.getResizedImageIconTempFileURL(previewURI.toURL(), 60, 45);
               content.append("<img class='preview' src='" + resizedImageURL + "'></img>");
            }

            content.append("</a>");
            content.append("</td>");
         }

         content.append("<td>");
         content.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
         content.append("</td>");
         content.append("<td valign='top' style='margin-bottom: 10px;'>");
         if (this.getDescription() != null && this.getDescription().length() > 0) {
            if (bCreateFullSizeView) {
               content.append(this.getDescription());
            } else {
               String strTruncatedDescription = this.getDescription();
               if (strTruncatedDescription.length() > 150) {
                  strTruncatedDescription = strTruncatedDescription.substring(0, 150);
                  strTruncatedDescription = strTruncatedDescription + " ...";
               }

               content.append(strTruncatedDescription);
            }
         } else {
            content.append("<span class='none_text'>No description</span>");
         }

         content.append("</td>");
         content.append("</tr>");
         content.append("</table>");
         content.append("</div>");
         if (bCreateFullSizeView) {
            content.append("<p style='text-align: left;'><b><a href='" + this.getResource() + "'>Open in myExperiment</a></b>&nbsp;<img style='border: 0px;' src='" + MyExperimentPerspective.getLocalResourceURL("external_link_small_icon") + "' /></p>");
         }

         content.append("</div>");
         content.append("</div>");
         HTMLEditorKit kit = new StyledHTMLEditorKit(pluginMainComponent.getStyleSheet());
         HTMLDocument doc = (HTMLDocument)kit.createDefaultDocument();
         doc.insertAfterStart(doc.getRootElements()[0].getElement(0), content.toString());
         infoTextPane.setEditorKit(kit);
         infoTextPane.setDocument(doc);
         infoTextPane.setContentType("text/html");
         infoTextPane.addHyperlinkListener((HyperlinkListener)eventHandler);
         infoTextPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 7, 0));
         mainPanel.add(infoTextPane, "Center");
         if (bCreateFullSizeView) {
            JPanel jpButtonsPanel = new JPanel();
            jpButtonsPanel.setLayout(new BoxLayout(jpButtonsPanel, 2));
            JButton previewButton = new JButton();
            previewButton.setAction(pluginMainComponent.new PreviewResourceAction(this.getItemType(), this.getURI()));
            jpButtonsPanel.add(previewButton);
            if (this.isDownloadable()) {
               JButton downloadButton = new JButton();
               downloadButton.setAction(pluginMainComponent.new DownloadResourceAction(this));
               jpButtonsPanel.add(downloadButton);
            }

            if (this.getItemType() == 10) {
               JButton loadButton = new JButton();
               loadButton.setAction(pluginMainComponent.new LoadResourceInTavernaAction(this));
               jpButtonsPanel.add(loadButton);
            }

            if (this.getItemType() == 10) {
               JButton importButton = new JButton();
               importButton.setAction(pluginMainComponent.new ImportIntoTavernaAction(this));
               jpButtonsPanel.add(importButton);
            }

            mainPanel.add(jpButtonsPanel, "South");
            jpButtonsPanel.setBackground(new Color(247, 247, 247));
            jpButtonsPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(235, 235, 235)), BorderFactory.createEmptyBorder(3, 3, 3, 3)));
         }

         if (bCreateFullSizeView) {
            mainPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5), BorderFactory.createLineBorder(Color.GRAY)));
         } else {
            mainPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
         }

         return mainPanel;
      } catch (Exception e) {
         logger.error("Failed while creating " + this.getItemTypeName() + " list view:\n" + e);
         return null;
      }
   }

   public static String getResourceTypeName(int resourceTypeCode) {
      switch (resourceTypeCode) {
         case 0:
            return "Unknown";
         case 10:
            return "Workflow";
         case 11:
            return "File";
         case 12:
            return "Pack";
         case 20:
            return "User";
         case 21:
            return "Group";
         case 30:
            return "Tag";
         case 31:
            return "Comment";
         default:
            return "ERROR: Unexpected unknown type!";
      }
   }

   public static int getResourceTypeFromVisibleName(String name) {
      if (name.toLowerCase().equals("Workflow".toLowerCase())) {
         return 10;
      } else if (name.toLowerCase().equals("File".toLowerCase())) {
         return 11;
      } else if (name.toLowerCase().equals("Pack".toLowerCase())) {
         return 12;
      } else if (name.toLowerCase().equals("User".toLowerCase())) {
         return 20;
      } else if (name.toLowerCase().equals("Group".toLowerCase())) {
         return 21;
      } else if (name.toLowerCase().equals("Tag".toLowerCase())) {
         return 30;
      } else {
         return name.toLowerCase().equals("Comment".toLowerCase()) ? 31 : 0;
      }
   }

   public static Resource buildFromXML(Document resourceXMLDocument, MyExperimentClient client, Logger logger) {
      Element root = resourceXMLDocument.getRootElement();
      return buildFromXML(root, client, logger);
   }

   public static Resource buildFromXML(Element docRootElement, MyExperimentClient client, Logger logger) {
      Resource res = null;
      switch (getResourceTypeFromVisibleName(docRootElement.getName())) {
         case 10:
            res = Workflow.buildFromXML(docRootElement, logger);
            break;
         case 11:
            res = File.buildFromXML(docRootElement, logger);
            break;
         case 12:
            res = Pack.buildFromXML(docRootElement, client, logger);
         case 13:
         case 14:
         case 15:
         case 16:
         case 17:
         case 18:
         case 19:
         default:
            break;
         case 20:
            res = User.buildFromXML(docRootElement, logger);
            break;
         case 21:
            res = Group.buildFromXML(docRootElement, logger);
      }

      return res;
   }
}
