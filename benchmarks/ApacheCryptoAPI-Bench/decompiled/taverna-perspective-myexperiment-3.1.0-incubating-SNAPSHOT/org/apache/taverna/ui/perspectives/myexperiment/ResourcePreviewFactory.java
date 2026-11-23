package org.apache.taverna.ui.perspectives.myexperiment;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import org.apache.log4j.Logger;
import org.apache.taverna.lang.ui.DialogTextArea;
import org.apache.taverna.lang.ui.ShadedLabel;
import org.apache.taverna.ui.perspectives.myexperiment.model.Comment;
import org.apache.taverna.ui.perspectives.myexperiment.model.File;
import org.apache.taverna.ui.perspectives.myexperiment.model.Group;
import org.apache.taverna.ui.perspectives.myexperiment.model.MyExperimentClient;
import org.apache.taverna.ui.perspectives.myexperiment.model.Pack;
import org.apache.taverna.ui.perspectives.myexperiment.model.PackItem;
import org.apache.taverna.ui.perspectives.myexperiment.model.Resource;
import org.apache.taverna.ui.perspectives.myexperiment.model.Tag;
import org.apache.taverna.ui.perspectives.myexperiment.model.User;
import org.apache.taverna.ui.perspectives.myexperiment.model.Util;
import org.apache.taverna.ui.perspectives.myexperiment.model.Workflow;
import org.jdom.Document;

public class ResourcePreviewFactory {
   private static final int PREFERRED_LOWER_TABBED_PANE_HEIGHT = 250;
   private final MainComponent pluginMainComponent;
   private final MyExperimentClient myExperimentClient;
   private final Logger logger;
   private final ImageIcon iconWorkflow;
   private final ImageIcon iconFile;
   private final ImageIcon iconPack;
   private final ImageIcon iconUser;
   private final ImageIcon iconGroup;

   public ResourcePreviewFactory(MainComponent component, MyExperimentClient client, Logger logger) {
      this.pluginMainComponent = component;
      this.myExperimentClient = client;
      this.logger = logger;
      this.iconWorkflow = new ImageIcon(MyExperimentPerspective.getLocalIconURL(10));
      this.iconFile = new ImageIcon(MyExperimentPerspective.getLocalIconURL(11));
      this.iconPack = new ImageIcon(MyExperimentPerspective.getLocalIconURL(12));
      this.iconUser = new ImageIcon(MyExperimentPerspective.getLocalIconURL(20));
      this.iconGroup = new ImageIcon(MyExperimentPerspective.getLocalIconURL(21));
   }

   public ResourcePreviewContent createPreview(String action, EventListener eventHandler) {
      JPanel panelToPopulate = new JPanel();
      if (!action.startsWith("preview:")) {
         this.logger.error("Bad preview request: \"" + action + "\"");
         panelToPopulate.add(new JLabel("An error has occurred."));
         Resource r = new Resource();
         r.setItemType(-1);
         r.setTitle("Bad preview request");
         r.setURI(action);
         return new ResourcePreviewContent(r, panelToPopulate);
      } else {
         action = action.substring(action.indexOf(":") + 1);
         int iType = Integer.parseInt(action.substring(0, action.indexOf(":")));
         action = action.substring(action.indexOf(":") + 1);
         String strURI = action;
         Document doc = null;

         try {
            doc = this.myExperimentClient.getResource(iType, strURI, 5005);
         } catch (Exception e) {
            this.logger.error("Error while fetching resource data from myExperiment to generate a preview.\nResource type: " + Resource.getResourceTypeName(iType) + "\nResource URI: " + action + "\nException: " + e);
         }

         Resource resource = null;
         switch (iType) {
            case 10:
               Workflow w = Workflow.buildFromXML(doc, this.logger);
               resource = w;
               this.generateWorkflowPreviewContent(w, panelToPopulate, eventHandler);
               break;
            case 11:
               File f = File.buildFromXML(doc, this.logger);
               resource = f;
               this.generateFilePreviewContent(f, panelToPopulate, eventHandler);
               break;
            case 12:
               Pack p = Pack.buildFromXML(doc, this.myExperimentClient, this.logger);
               resource = p;
               this.generatePackPreviewContent(p, panelToPopulate, eventHandler);
               break;
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            default:
               this.logger.error("Failed generating preview. Reason: unknown resource type - \"" + Resource.getResourceTypeName(iType) + "\"");
               panelToPopulate.add(new JLabel("Cannot generate preview for unknown resource types."));
               Resource r = new Resource();
               r.setItemType(iType);
               r.setTitle("Error: unknown resource type");
               r.setURI(action);
               return new ResourcePreviewContent(r, panelToPopulate);
            case 20:
               User u = User.buildFromXML(doc, this.logger);
               resource = u;
               this.generateUserPreviewContent(u, panelToPopulate, eventHandler);
               break;
            case 21:
               Group g = Group.buildFromXML(doc, this.logger);
               resource = g;
               this.generateGroupPreviewContent(g, panelToPopulate, eventHandler);
         }

         return new ResourcePreviewContent(resource, panelToPopulate);
      }
   }

   private void generateWorkflowPreviewContent(Workflow w, JPanel panelToPopulate, EventListener eventHandler) {
      if (w != null) {
         try {
            StringBuffer content = new StringBuffer();
            content.append("<div class='outer'>");
            content.append("<div class='workflow'>");
            content.append("<br>");
            content.append("<p class='title'>");
            content.append("Workflow Entry: <a href='preview:10:" + w.getURI() + "'>" + w.getTitle() + "</a> (version " + w.getVersion() + ")");
            content.append("</p>");
            content.append("<br>");
            content.append("<p class='info'>");
            content.append("<b>Type:</b> " + w.getVisibleType() + "<br><br>");
            content.append("<b>Uploader:</b> <a href='preview:20:" + w.getUploader().getURI() + "'>" + w.getUploader().getName() + "</a><br>");
            content.append("<b>Created at: </b> " + w.getCreatedAt() + "<br>");
            content.append("<b>License: </b> <a href='" + w.getLicense().getLink() + "'>" + w.getLicense().getText() + "</a>&nbsp;<img src='" + MyExperimentPerspective.getLocalResourceURL("external_link_small_icon") + "' />");
            content.append("</p>");
            content.append("<br>");
            content.append("<a href='" + w.getPreview() + "'>");
            content.append("<img class='preview' src='" + w.getThumbnailBig() + "'></img>");
            content.append("</a>");
            content.append("<br>");
            content.append("<br>");
            if (!w.getDescription().equals("")) {
               content.append("<p class='desc'>");
               content.append("<br>");
               content.append(Util.stripHTML(w.getDescription()));
               content.append("<br>");
               content.append("</p>");
            } else {
               content.append("<span class='none_text'>No description</span>");
            }

            content.append("<br>");
            content.append("</div>");
            content.append("</div>");
            HTMLEditorKit kit = new StyledHTMLEditorKit(this.pluginMainComponent.getStyleSheet());
            HTMLDocument doc = (HTMLDocument)kit.createDefaultDocument();
            doc.insertAfterStart(doc.getRootElements()[0].getElement(0), content.toString());
            JScrollPane spComponentsTab = this.createWorkflowComponentPreviewTab(w);
            JScrollPane spTagsTab = this.createTagPreviewTab(w.getTags());
            JScrollPane spCommentsTab = this.createCommentsPreviewTab(w.getComments());
            JScrollPane spCreditsTab = this.createCreditsPreviewTab(w.getCredits());
            JScrollPane spAttributionsTab = this.createAttributionsPreviewTab(w.getAttributions());
            JTabbedPane tpTabbedView = new JTabbedPane();
            tpTabbedView.add("Components", spComponentsTab);
            tpTabbedView.add("Tags (" + w.getTags().size() + ")", spTagsTab);
            tpTabbedView.add("Comments (" + w.getComments().size() + ")", spCommentsTab);
            tpTabbedView.addTab("Credits (" + w.getCredits().size() + ")", spCreditsTab);
            tpTabbedView.addTab("Attributions (" + w.getAttributions().size() + ")", spAttributionsTab);
            JTextPane tpWorkflowPreview = new JTextPane();
            tpWorkflowPreview.setEditable(false);
            tpWorkflowPreview.setEditorKit(kit);
            tpWorkflowPreview.setDocument(doc);
            tpWorkflowPreview.addHyperlinkListener((HyperlinkListener)eventHandler);
            JPanel jpFullWorkflowPreview = this.wrapTextPaneAndTabbedViewIntoFullPreview(tpWorkflowPreview, tpTabbedView);
            panelToPopulate.setLayout(new BorderLayout());
            panelToPopulate.add(jpFullWorkflowPreview, "Center");
         } catch (Exception e) {
            this.logger.error("Failed to populate Workflow Preview pane", e);
         }
      }

   }

   private void generateFilePreviewContent(File f, JPanel panelToPopulate, EventListener eventHandler) {
      if (f != null) {
         try {
            StringBuffer content = new StringBuffer();
            content.append("<div class='outer'>");
            content.append("<div class='file'>");
            content.append("<br>");
            content.append("<p class='title'>");
            content.append("File: <a href='preview:11:" + f.getURI() + "'>" + f.getTitle() + "</a>");
            content.append("</p>");
            content.append("<br>");
            content.append("<p class='info'>");
            content.append("<b>Type:</b> " + f.getVisibleType() + "<br>");
            content.append("<b>Filename:</b> " + f.getFilename() + "<br><br>");
            content.append("<b>Uploader:</b> <a href='preview:20:" + f.getUploader().getURI() + "'>" + f.getUploader().getName() + "</a><br>");
            content.append("<b>Created at: </b> " + f.getCreatedAt() + "<br>");
            content.append("<b>Last updated at: </b> " + f.getUpdatedAt() + "<br>");
            content.append("<b>License: </b> <a href='" + f.getLicense().getLink() + "'>" + f.getLicense().getText() + "</a>&nbsp;<img src='" + MyExperimentPerspective.getLocalResourceURL("external_link_small_icon") + "' />");
            content.append("</p>");
            content.append("<br>");
            if (!f.getDescription().equals("")) {
               content.append("<p class='desc'>");
               content.append("<br>");
               content.append(Util.stripHTML(f.getDescription()));
               content.append("<br>");
               content.append("</p>");
            } else {
               content.append("<span class='none_text'>No description</span>");
            }

            content.append("<br>");
            content.append("</div>");
            content.append("</div>");
            HTMLEditorKit kit = new StyledHTMLEditorKit(this.pluginMainComponent.getStyleSheet());
            HTMLDocument doc = (HTMLDocument)kit.createDefaultDocument();
            doc.insertAfterStart(doc.getRootElements()[0].getElement(0), content.toString());
            JScrollPane spTagsTab = this.createTagPreviewTab(f.getTags());
            JScrollPane spCommentsTab = this.createCommentsPreviewTab(f.getComments());
            JScrollPane spCreditsTab = this.createCreditsPreviewTab(f.getCredits());
            JScrollPane spAttributionsTab = this.createAttributionsPreviewTab(f.getAttributions());
            JTabbedPane tpTabbedView = new JTabbedPane();
            tpTabbedView.add("Tags (" + f.getTags().size() + ")", spTagsTab);
            tpTabbedView.add("Comments (" + f.getComments().size() + ")", spCommentsTab);
            tpTabbedView.add("Credits (" + f.getCredits().size() + ")", spCreditsTab);
            tpTabbedView.add("Attributions (" + f.getAttributions().size() + ")", spAttributionsTab);
            JTextPane tpFilePreview = new JTextPane();
            tpFilePreview.setEditable(false);
            tpFilePreview.setEditorKit(kit);
            tpFilePreview.setDocument(doc);
            tpFilePreview.addHyperlinkListener((HyperlinkListener)eventHandler);
            JPanel jpFullFilePreview = new JPanel();
            jpFullFilePreview.setBackground(Color.WHITE);
            jpFullFilePreview.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            c.weighty = (double)0.0F;
            jpFullFilePreview.add(tpFilePreview, c);
            c.gridx = 0;
            c.gridy = 1;
            c.weighty = (double)1.0F;
            c.fill = 3;
            c.insets = new Insets(20, 0, 5, 0);
            jpFullFilePreview.add(tpTabbedView, c);
            panelToPopulate.setLayout(new BorderLayout());
            panelToPopulate.add(jpFullFilePreview, "Center");
         } catch (Exception e) {
            this.logger.error("Failed to populate File Preview pane", e);
         }
      }

   }

   private void generatePackPreviewContent(Pack p, JPanel panelToPopulate, EventListener eventHandler) {
      if (p != null) {
         try {
            StringBuffer content = new StringBuffer();
            content.append("<div class='outer'>");
            content.append("<div class='pack'>");
            content.append("<br>");
            content.append("<p class='title'>");
            content.append("Pack: <a href='preview:12:" + p.getURI() + "'>" + p.getTitle() + "</a>");
            content.append("</p>");
            content.append("<br>");
            content.append("<p class='info'>");
            content.append("<b>Creator:</b> <a href='preview:20:" + p.getCreator().getURI() + "'>" + p.getCreator().getName() + "</a><br>");
            content.append("<b>Created at: </b> " + p.getCreatedAt() + "<br>");
            content.append("<b>Last updated at: </b> " + p.getUpdatedAt() + "<br>");
            content.append("</p>");
            content.append("<br>");
            if (!p.getDescription().equals("")) {
               content.append("<p class='desc'>");
               content.append("<br>");
               content.append(Util.stripHTML(p.getDescription()));
               content.append("<br>");
               content.append("<br>");
               content.append("</p>");
            } else {
               content.append("<span class='none_text'>No description</span>");
            }

            content.append("<br>");
            content.append("</div>");
            content.append("</div>");
            HTMLEditorKit kit = new StyledHTMLEditorKit(this.pluginMainComponent.getStyleSheet());
            HTMLDocument doc = (HTMLDocument)kit.createDefaultDocument();
            doc.insertAfterStart(doc.getRootElements()[0].getElement(0), content.toString());
            JScrollPane spPackItemsTab = this.createPackItemPreviewTab(p);
            JScrollPane spTagsTab = this.createTagPreviewTab(p.getTags());
            JScrollPane spCommentsTab = this.createCommentsPreviewTab(p.getComments());
            JTabbedPane tpTabbedView = new JTabbedPane();
            tpTabbedView.addTab("Pack Items (" + p.getItemCount() + ")", spPackItemsTab);
            tpTabbedView.add("Tags (" + p.getTags().size() + ")", spTagsTab);
            tpTabbedView.add("Comments (" + p.getComments().size() + ")", spCommentsTab);
            JTextPane tpPackPreview = new JTextPane();
            tpPackPreview.setEditable(false);
            tpPackPreview.setEditorKit(kit);
            tpPackPreview.setDocument(doc);
            tpPackPreview.addHyperlinkListener((HyperlinkListener)eventHandler);
            JPanel jpFullPackPreview = new JPanel();
            jpFullPackPreview.setBackground(Color.WHITE);
            jpFullPackPreview.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            c.weighty = (double)0.0F;
            jpFullPackPreview.add(tpPackPreview, c);
            c.gridx = 0;
            c.gridy = 1;
            c.weighty = (double)1.0F;
            c.fill = 3;
            c.insets = new Insets(20, 0, 5, 0);
            jpFullPackPreview.add(tpTabbedView, c);
            panelToPopulate.setLayout(new BorderLayout());
            panelToPopulate.add(jpFullPackPreview, "Center");
         } catch (Exception e) {
            this.logger.error("Failed to populate Pack Preview pane", e);
         }
      }

   }

   private void generateUserPreviewContent(User u, JPanel panelToPopulate, EventListener eventHandler) {
      if (u != null) {
         try {
            StringBuffer content = new StringBuffer();
            content.append("<div class='outer'>");
            content.append("<div class='user'>");
            content.append("<br>");
            content.append("<p class='name'>");
            content.append("User: <a href=preview:20:" + u.getURI() + "'>" + u.getName() + "</a>");
            content.append("</p>");
            content.append("<br>");
            content.append("<p class='info'>");
            String strLocation;
            if (u.getCity().length() == 0 && u.getCountry().length() == 0) {
               strLocation = "<span class='none_text'>Not specified</span>";
            } else {
               strLocation = u.getCity() + (u.getCity().length() != 0 && u.getCountry().length() != 0 ? ", " : "") + u.getCountry();
            }

            content.append("<b>Location:</b> " + strLocation + "<br>");
            content.append("<b>Joined at: </b> " + u.getCreatedAt() + "<br>");
            content.append("<b>Last seen at: </b> " + u.getUpdatedAt() + "<br>");
            content.append("</p>");
            content.append("<br>");
            content.append("<a href='" + u.getAvatarResource() + "'>");
            content.append("<img class='avatar' src='" + u.getAvatarResource() + "'></img>");
            content.append("</a>");
            content.append("<br>");
            content.append("<br>");
            if (!u.getDescription().equals("")) {
               content.append("<p class='desc'>" + Util.stripHTML(u.getDescription()) + "<br><br></p>");
            } else {
               content.append("<span class='none_text'>No description</span>");
            }

            content.append("<p class='contact_details_header'>Contact Details</p>");
            content.append("<p class='contact_details'>");
            content.append("<b>Email: </b>" + (u.getEmail().length() == 0 ? "<span class='none_text'>Not specified</span>" : u.getEmail()) + "<br>");
            content.append("<b>Website: </b>" + (u.getWebsite().length() == 0 ? "<span class='none_text'>Not specified</span>" : u.getWebsite()));
            content.append("</p>");
            content.append("</div>");
            content.append("</div>");
            HTMLEditorKit kit = new StyledHTMLEditorKit(this.pluginMainComponent.getStyleSheet());
            HTMLDocument doc = (HTMLDocument)kit.createDefaultDocument();
            doc.insertAfterStart(doc.getRootElements()[0].getElement(0), content.toString());
            JPanel jpWorkflowsTabContent = new JPanel();
            jpWorkflowsTabContent.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            jpWorkflowsTabContent.setLayout(new BoxLayout(jpWorkflowsTabContent, 1));

            for(HashMap<String, String> hmCurWF : u.getWorkflows()) {
               jpWorkflowsTabContent.add(new JClickableLabel((String)hmCurWF.get("name"), "preview:10:" + (String)hmCurWF.get("uri"), this.pluginMainComponent.getPreviewBrowser(), this.iconWorkflow));
            }

            JPanel jpFilesTabContent = new JPanel();
            jpFilesTabContent.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            jpFilesTabContent.setLayout(new BoxLayout(jpFilesTabContent, 1));

            for(HashMap<String, String> hmCurFile : u.getFiles()) {
               jpFilesTabContent.add(new JClickableLabel((String)hmCurFile.get("name"), "preview:11:" + (String)hmCurFile.get("uri"), this.pluginMainComponent.getPreviewBrowser(), this.iconFile));
            }

            JPanel jpPacksTabContent = new JPanel();
            jpPacksTabContent.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            jpPacksTabContent.setLayout(new BoxLayout(jpPacksTabContent, 1));

            for(HashMap<String, String> hmCurPack : u.getPacks()) {
               jpPacksTabContent.add(new JClickableLabel((String)hmCurPack.get("name"), "preview:12:" + (String)hmCurPack.get("uri"), this.pluginMainComponent.getPreviewBrowser(), this.iconPack));
            }

            JPanel jpFriendsTabContent = new JPanel();
            jpFriendsTabContent.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            jpFriendsTabContent.setLayout(new BoxLayout(jpFriendsTabContent, 1));

            for(HashMap<String, String> hmCurFriend : u.getFriends()) {
               jpFriendsTabContent.add(new JClickableLabel((String)hmCurFriend.get("name"), "preview:20:" + (String)hmCurFriend.get("uri"), this.pluginMainComponent.getPreviewBrowser(), this.iconUser));
            }

            JPanel jpGroupsTabContent = new JPanel();
            jpGroupsTabContent.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            jpGroupsTabContent.setLayout(new BoxLayout(jpGroupsTabContent, 1));

            for(HashMap<String, String> hmCurGroup : u.getGroups()) {
               jpGroupsTabContent.add(new JClickableLabel((String)hmCurGroup.get("name"), "preview:21:" + (String)hmCurGroup.get("uri"), this.pluginMainComponent.getPreviewBrowser(), this.iconGroup));
            }

            Dimension dPreferredTabSize = new Dimension(700, 250);
            JScrollPane spWorkflowsTab = new JScrollPane(jpWorkflowsTabContent);
            spWorkflowsTab.setBorder(BorderFactory.createEmptyBorder());
            spWorkflowsTab.setPreferredSize(dPreferredTabSize);
            spWorkflowsTab.getVerticalScrollBar().setUnitIncrement(10);
            JScrollPane spFilesTab = new JScrollPane(jpFilesTabContent);
            spFilesTab.setBorder(BorderFactory.createEmptyBorder());
            spFilesTab.setPreferredSize(dPreferredTabSize);
            spFilesTab.getVerticalScrollBar().setUnitIncrement(10);
            JScrollPane spPacksTab = new JScrollPane(jpPacksTabContent);
            spPacksTab.setBorder(BorderFactory.createEmptyBorder());
            spPacksTab.setPreferredSize(dPreferredTabSize);
            spPacksTab.getVerticalScrollBar().setUnitIncrement(10);
            JScrollPane spFriendsTab = new JScrollPane(jpFriendsTabContent);
            spFriendsTab.setBorder(BorderFactory.createEmptyBorder());
            spFriendsTab.setPreferredSize(dPreferredTabSize);
            spFriendsTab.getVerticalScrollBar().setUnitIncrement(10);
            JScrollPane spGroupsTab = new JScrollPane(jpGroupsTabContent);
            spGroupsTab.setBorder(BorderFactory.createEmptyBorder());
            spGroupsTab.setPreferredSize(dPreferredTabSize);
            spGroupsTab.getVerticalScrollBar().setUnitIncrement(10);
            JTabbedPane tpTabbedItems = new JTabbedPane();
            tpTabbedItems.addTab("Workflows (" + u.getWorkflows().size() + ")", spWorkflowsTab);
            tpTabbedItems.addTab("Files (" + u.getFiles().size() + ")", spFilesTab);
            tpTabbedItems.addTab("Packs (" + u.getPacks().size() + ")", spPacksTab);
            tpTabbedItems.addTab("Friends (" + u.getFriends().size() + ")", spFriendsTab);
            tpTabbedItems.addTab("Groups (" + u.getGroups().size() + ")", spGroupsTab);
            JTextPane tpUserPreview = new JTextPane();
            tpUserPreview.setEditable(false);
            tpUserPreview.setEditorKit(kit);
            tpUserPreview.setDocument(doc);
            tpUserPreview.addHyperlinkListener((HyperlinkListener)eventHandler);
            JPanel jpFullUserPreview = new JPanel();
            jpFullUserPreview.setBackground(Color.WHITE);
            jpFullUserPreview.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            c.weighty = (double)0.0F;
            jpFullUserPreview.add(tpUserPreview, c);
            c.gridx = 0;
            c.gridy = 1;
            c.weighty = (double)1.0F;
            c.fill = 3;
            c.insets = new Insets(20, 0, 5, 0);
            jpFullUserPreview.add(tpTabbedItems, c);
            panelToPopulate.setLayout(new BorderLayout());
            panelToPopulate.add(jpFullUserPreview, "Center");
         } catch (Exception e) {
            this.logger.error("Failed to populate Workflow Preview pane", e);
         }
      }

   }

   private void generateGroupPreviewContent(Group g, JPanel panelToPopulate, EventListener eventHandler) {
      if (g != null) {
         try {
            StringBuffer content = new StringBuffer();
            content.append("<div class='outer'>");
            content.append("<div class='group'>");
            content.append("<br>");
            content.append("<p class='title'>");
            content.append("Group: <a href='preview:21:" + g.getURI() + "'>" + g.getTitle() + "</a>");
            content.append("</p>");
            content.append("<br>");
            content.append("<p class='info'>");
            content.append("<b>Administrator:</b> <a href='preview:20:" + g.getAdmin().getURI() + "'>" + g.getAdmin().getName() + "</a><br>");
            content.append("<b>Created at: </b> " + g.getCreatedAt() + "<br>");
            content.append("</p>");
            content.append("<br>");
            if (!g.getDescription().equals("")) {
               content.append("<p class='desc'>");
               content.append("<br>");
               content.append(Util.stripHTML(g.getDescription()));
               content.append("<br>");
               content.append("</p>");
            } else {
               content.append("<span class='none_text'>No description</span>");
            }

            content.append("<br>");
            content.append("</div>");
            content.append("</div>");
            HTMLEditorKit kit = new StyledHTMLEditorKit(this.pluginMainComponent.getStyleSheet());
            HTMLDocument doc = (HTMLDocument)kit.createDefaultDocument();
            doc.insertAfterStart(doc.getRootElements()[0].getElement(0), content.toString());
            JPanel jpMembersTabContent = this.createStandardTabContentPanel();

            for(User uCurMember : g.getMembers()) {
               jpMembersTabContent.add(new JClickableLabel(uCurMember.getName(), "preview:" + uCurMember.getItemType() + ":" + uCurMember.getURI(), this.pluginMainComponent.getPreviewBrowser(), new ImageIcon(MyExperimentPerspective.getLocalIconURL(uCurMember.getItemType()))));
            }

            JScrollPane spMembersTabContent = this.wrapPreviewTabContentIntoScrollPane(jpMembersTabContent);
            JPanel jpSharedItemsTabContent = this.createStandardTabContentPanel();

            for(Resource rCurItem : g.getSharedItems()) {
               jpSharedItemsTabContent.add(new JClickableLabel(rCurItem.getTitle(), "preview:" + rCurItem.getItemType() + ":" + rCurItem.getURI(), this.pluginMainComponent.getPreviewBrowser(), new ImageIcon(MyExperimentPerspective.getLocalIconURL(rCurItem.getItemType()))));
            }

            JScrollPane spSharedItemsTabContent = this.wrapPreviewTabContentIntoScrollPane(jpSharedItemsTabContent);
            JScrollPane spTagsTabContent = this.createTagPreviewTab(g.getTags());
            JScrollPane spCommentsTab = this.createCommentsPreviewTab(g.getComments());
            JTabbedPane tpTabbedItems = new JTabbedPane();
            tpTabbedItems.addTab("Members (" + g.getMemberCount() + ")", spMembersTabContent);
            tpTabbedItems.addTab("Shared Items (" + g.getSharedItemCount() + ")", spSharedItemsTabContent);
            tpTabbedItems.addTab("Tags (" + g.getTags().size() + ")", spTagsTabContent);
            tpTabbedItems.addTab("Comments (" + g.getComments().size() + ")", spCommentsTab);
            JTextPane tpGroupPreview = new JTextPane();
            tpGroupPreview.setEditable(false);
            tpGroupPreview.setEditorKit(kit);
            tpGroupPreview.setDocument(doc);
            tpGroupPreview.addHyperlinkListener((HyperlinkListener)eventHandler);
            JPanel jpFullGroupPreview = new JPanel();
            jpFullGroupPreview.setBackground(Color.WHITE);
            jpFullGroupPreview.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            c.weighty = (double)0.0F;
            jpFullGroupPreview.add(tpGroupPreview, c);
            c.gridx = 0;
            c.gridy = 1;
            c.weighty = (double)1.0F;
            c.fill = 3;
            c.insets = new Insets(20, 0, 5, 0);
            jpFullGroupPreview.add(tpTabbedItems, c);
            panelToPopulate.setLayout(new BorderLayout());
            panelToPopulate.add(jpFullGroupPreview, "Center");
         } catch (Exception e) {
            this.logger.error("Failed to populate Group Preview pane", e);
         }
      }

   }

   private JScrollPane createWorkflowComponentPreviewTab(Workflow w) {
      JPanel jpWorkflowComponentsTabContent = this.createStandardTabContentPanel();
      if (!w.isTavernaWorkflow()) {
         JLabel lNotSupported = new JLabel("<html>This is a " + w.getVisibleType() + " workflow;<br>myExperiment can only display Taverna workflow components at the moment.</html>");
         lNotSupported.setFont(lNotSupported.getFont().deriveFont(2));
         lNotSupported.setForeground(Color.GRAY);
         jpWorkflowComponentsTabContent.add(lNotSupported);
      } else if (!w.isDownloadAllowed()) {
         JLabel lNotAuthorized = new JLabel("You are not authorised to download this workflow, and hence component preview is not available.");
         lNotAuthorized.setFont(lNotAuthorized.getFont().deriveFont(2));
         lNotAuthorized.setForeground(Color.GRAY);
         jpWorkflowComponentsTabContent.add(lNotAuthorized);
      } else {
         Vector<String> vColumnNames = new Vector();
         vColumnNames.clear();
         vColumnNames.addAll(Arrays.asList("Name", "Description"));
         Vector<Vector<String>> vInputsData = new Vector();
         ArrayList<HashMap<String, String>> inputs = (ArrayList)w.getComponents().get("inputs");
         if (inputs != null) {
            for(HashMap<String, String> curInput : inputs) {
               Vector<String> vCurData = new Vector();
               vCurData.add(curInput.get("name"));
               vCurData.add(curInput.get("description"));
               vInputsData.add(vCurData);
            }
         }

         JTable jtInputs = new JTable(vInputsData, vColumnNames);
         jtInputs.getColumnModel().getColumn(0).setPreferredWidth(100);
         jtInputs.getTableHeader().setFont(jtInputs.getTableHeader().getFont().deriveFont(1));
         JPanel jpInputs = new JPanel();
         jpInputs.setLayout(new BorderLayout());
         jpInputs.add(jtInputs.getTableHeader(), "North");
         jpInputs.add(jtInputs, "Center");
         JPanel jpInputsWithTitle = new JPanel();
         jpInputsWithTitle.setBorder(BorderFactory.createEtchedBorder());
         jpInputsWithTitle.setLayout(new BorderLayout());
         jpInputsWithTitle.add(new ShadedLabel("Workflow input ports (" + vInputsData.size() + ")", ShadedLabel.BLUE, true), "North");
         if (vInputsData.size() > 0) {
            jpInputsWithTitle.add(jpInputs, "Center");
         }

         vColumnNames.clear();
         vColumnNames.addAll(Arrays.asList("Name", "Type", "Description"));
         Vector<Vector<String>> vProcessorsData = new Vector();
         ArrayList<HashMap<String, String>> processors = (ArrayList)w.getComponents().get("processors");
         if (processors != null) {
            for(HashMap<String, String> curProcessor : processors) {
               Vector<String> vCurData = new Vector();
               vCurData.add(curProcessor.get("name"));
               vCurData.add(curProcessor.get("type"));
               vCurData.add(curProcessor.get("description"));
               vProcessorsData.add(vCurData);
            }
         }

         JTable jtProcessors = new JTable(vProcessorsData, vColumnNames);
         jtProcessors.getTableHeader().setFont(jtProcessors.getTableHeader().getFont().deriveFont(1));
         JPanel jpProcessors = new JPanel();
         jpProcessors.setLayout(new BorderLayout());
         jpProcessors.add(jtProcessors.getTableHeader(), "North");
         jpProcessors.add(jtProcessors, "Center");
         JPanel jpProcessorsWithTitle = new JPanel();
         jpProcessorsWithTitle.setBorder(BorderFactory.createEtchedBorder());
         jpProcessorsWithTitle.setLayout(new BorderLayout());
         jpProcessorsWithTitle.add(new ShadedLabel("Services (" + vProcessorsData.size() + ")", ShadedLabel.BLUE, true), "North");
         if (vProcessorsData.size() > 0) {
            jpProcessorsWithTitle.add(jpProcessors, "Center");
         }

         vColumnNames.clear();
         vColumnNames.addAll(Arrays.asList("Source", "Sink"));
         Vector<Vector<String>> vLinksData = new Vector();
         ArrayList<HashMap<String, String>> links = (ArrayList)w.getComponents().get("links");
         if (links != null) {
            for(HashMap<String, String> curLink : links) {
               Vector<String> vCurData = new Vector();
               vCurData.add(curLink.get("source"));
               vCurData.add(curLink.get("sink"));
               vLinksData.add(vCurData);
            }
         }

         JTable jtLinks = new JTable(vLinksData, vColumnNames);
         jtLinks.getColumnModel().getColumn(0).setPreferredWidth(100);
         jtLinks.getTableHeader().setFont(jtLinks.getTableHeader().getFont().deriveFont(1));
         JPanel jpLinks = new JPanel();
         jpLinks.setLayout(new BorderLayout());
         jpLinks.add(jtLinks.getTableHeader(), "North");
         jpLinks.add(jtLinks, "Center");
         JPanel jpLinksWithTitle = new JPanel();
         jpLinksWithTitle.setBorder(BorderFactory.createEtchedBorder());
         jpLinksWithTitle.setLayout(new BorderLayout());
         jpLinksWithTitle.add(new ShadedLabel("Links (" + vLinksData.size() + ")", ShadedLabel.BLUE, true), "North");
         if (vLinksData.size() > 0) {
            jpLinksWithTitle.add(jpLinks, "Center");
         }

         vColumnNames.clear();
         vColumnNames.addAll(Arrays.asList("Name", "Description"));
         Vector<Vector<String>> vOutputsData = new Vector();
         ArrayList<HashMap<String, String>> outputs = (ArrayList)w.getComponents().get("outputs");
         if (outputs != null) {
            for(HashMap<String, String> curOutput : outputs) {
               Vector<String> vCurData = new Vector();
               vCurData.add(curOutput.get("name"));
               vCurData.add(curOutput.get("description"));
               vOutputsData.add(vCurData);
            }
         }

         JTable jtOutputs = new JTable(vOutputsData, vColumnNames);
         jtOutputs.getColumnModel().getColumn(0).setPreferredWidth(100);
         jtOutputs.getTableHeader().setFont(jtOutputs.getTableHeader().getFont().deriveFont(1));
         JPanel jpOutputs = new JPanel();
         jpOutputs.setLayout(new BorderLayout());
         jpOutputs.add(jtOutputs.getTableHeader(), "North");
         jpOutputs.add(jtOutputs, "Center");
         JPanel jpOutputsWithTitle = new JPanel();
         jpOutputsWithTitle.setBorder(BorderFactory.createEtchedBorder());
         jpOutputsWithTitle.setLayout(new BorderLayout());
         jpOutputsWithTitle.add(new ShadedLabel("Workflow output ports (" + vOutputsData.size() + ")", ShadedLabel.BLUE, true), "North");
         if (vOutputsData.size() > 0) {
            jpOutputsWithTitle.add(jpOutputs, "Center");
         }

         jpWorkflowComponentsTabContent.setLayout(new GridBagLayout());
         GridBagConstraints c = new GridBagConstraints();
         c.gridx = 0;
         c.gridy = -1;
         c.weightx = (double)1.0F;
         c.fill = 2;
         c.anchor = 11;
         jpWorkflowComponentsTabContent.add(jpInputsWithTitle, c);
         c.insets = new Insets(10, 0, 0, 0);
         jpWorkflowComponentsTabContent.add(jpProcessorsWithTitle, c);
         jpWorkflowComponentsTabContent.add(jpLinksWithTitle, c);
         c.weighty = (double)1.0F;
         jpWorkflowComponentsTabContent.add(jpOutputsWithTitle, c);
      }

      return this.wrapPreviewTabContentIntoScrollPane(jpWorkflowComponentsTabContent);
   }

   private JScrollPane createPackItemPreviewTab(Pack p) {
      JPanel jpPackItemsTabContent = this.createStandardTabContentPanel();
      GridBagConstraints c = new GridBagConstraints();
      jpPackItemsTabContent.setLayout(new GridBagLayout());
      c.anchor = 18;
      if (p.getItems().size() > 0) {
         int iCnt = 0;
         boolean bNoCommentForPrevItem = false;

         for(PackItem piCurItem : p.getItems()) {
            c.gridx = 0;
            c.gridy = 3 * iCnt;
            c.weightx = (double)1.0F;
            c.insets = bNoCommentForPrevItem ? new Insets(7, 0, 0, 0) : new Insets(0, 0, 0, 0);
            c.fill = 0;
            if (piCurItem.isInternalItem()) {
               jpPackItemsTabContent.add(new JClickableLabel(piCurItem.getItem().getTitle(), "preview:" + piCurItem.getItem().getItemType() + ":" + piCurItem.getItem().getURI(), this.pluginMainComponent.getPreviewBrowser(), new ImageIcon(MyExperimentPerspective.getLocalIconURL(piCurItem.getItem().getItemType()))), c);
            } else {
               jpPackItemsTabContent.add(new JClickableLabel(piCurItem.getTitle(), piCurItem.getLink(), this.pluginMainComponent.getPreviewBrowser(), new ImageIcon(MyExperimentPerspective.getLocalIconURL(piCurItem.getItemType())), 2, piCurItem.getTitle() + " (link: " + piCurItem.getLink() + ")"), c);
            }

            String strComment = Util.stripAllHTML(piCurItem.getComment());
            bNoCommentForPrevItem = strComment == null || strComment.length() == 0;
            JPanel jpWhoAddedTheItem = new JPanel();
            jpWhoAddedTheItem.setLayout(new GridBagLayout());
            GridBagConstraints c1 = new GridBagConstraints();
            c1.anchor = 18;
            jpWhoAddedTheItem.setBorder(BorderFactory.createEmptyBorder());
            jpWhoAddedTheItem.add(new JLabel("Added by "), c1);
            jpWhoAddedTheItem.add(new JClickableLabel(piCurItem.getUserWhoAddedTheItem().getName(), "preview:20:" + piCurItem.getUserWhoAddedTheItem().getURI(), this.pluginMainComponent.getPreviewBrowser()), c1);
            String strAddedOnDate = MyExperimentClient.formatDate(piCurItem.getCreatedAt());
            c1.weightx = (double)1.0F;
            jpWhoAddedTheItem.add(new JLabel(" [" + strAddedOnDate + "]"), c1);
            c.gridx = 0;
            c.gridy = 3 * iCnt + 1;
            c.insets = new Insets(0, 25, 0, 0);
            c.weightx = (double)1.0F;
            if (bNoCommentForPrevItem && iCnt + 1 == p.getItems().size()) {
               c.weighty = (double)1.0F;
            }

            jpPackItemsTabContent.add(jpWhoAddedTheItem, c);
            if (!bNoCommentForPrevItem) {
               c.gridx = 0;
               c.gridy = 3 * iCnt + 2;
               c.fill = 2;
               c.insets = new Insets(0, 25, 7, 25);
               c.weightx = (double)1.0F;
               if (iCnt + 1 == p.getItems().size()) {
                  c.weighty = (double)1.0F;
               }

               DialogTextArea taCommentText = new DialogTextArea("Comment: " + strComment);
               taCommentText.setOpaque(false);
               taCommentText.setEditable(false);
               taCommentText.setLineWrap(true);
               taCommentText.setWrapStyleWord(true);
               jpPackItemsTabContent.add(taCommentText, c);
            }

            ++iCnt;
         }
      } else {
         c.weighty = (double)1.0F;
         c.weightx = (double)1.0F;
         jpPackItemsTabContent.add(Util.generateNoneTextLabel("None"), c);
      }

      return this.wrapPreviewTabContentIntoScrollPane(jpPackItemsTabContent);
   }

   private JScrollPane createTagPreviewTab(List<Tag> lTags) {
      TagCloudPanel jpTagTabContent = new TagCloudPanel("Resource tag cloud", 2, this.pluginMainComponent.getPreviewBrowser(), this.pluginMainComponent, this.myExperimentClient, this.logger);
      jpTagTabContent.getTagCloudData().clear();
      jpTagTabContent.getTagCloudData().addAll(lTags);
      jpTagTabContent.refresh();
      JScrollPane spTagTabContent = this.wrapPreviewTabContentIntoScrollPane(jpTagTabContent);
      spTagTabContent.setHorizontalScrollBarPolicy(31);
      spTagTabContent.setVerticalScrollBarPolicy(21);
      jpTagTabContent.setPreferredSize(spTagTabContent.getPreferredSize());
      return spTagTabContent;
   }

   private JScrollPane createCommentsPreviewTab(final List<Comment> comments) {
      final JPanel jpCommentsTabContent = this.createStandardTabContentPanel();
      if (comments.size() > 0) {
         final GridBagConstraints c = new GridBagConstraints();
         jpCommentsTabContent.setLayout(new GridBagLayout());
         c.anchor = 18;
         JLabel lLoading = new JLabel("Loading comments...", new ImageIcon(MyExperimentPerspective.getLocalResourceURL("spinner")), 2);
         lLoading.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 10));
         c.weightx = (double)1.0F;
         c.weighty = (double)1.0F;
         jpCommentsTabContent.add(lLoading, c);
         (new Thread("Load comments for preview") {
            public void run() {
               ResourcePreviewFactory.this.myExperimentClient.updateCommentListWithExtraData(comments);
               SwingUtilities.invokeLater(new Runnable() {
                  public void run() {
                     jpCommentsTabContent.removeAll();
                     int iCnt = 0;

                     for(Comment comment : comments) {
                        c.gridx = 0;
                        c.gridy = 2 * iCnt;
                        c.weightx = (double)0.0F;
                        c.weighty = (double)0.0F;
                        c.gridwidth = 1;
                        JClickableLabel lCommentAuthor = new JClickableLabel(comment.getUser().getName(), "preview:" + comment.getUser().getItemType() + ":" + comment.getUser().getURI(), ResourcePreviewFactory.this.pluginMainComponent.getPreviewBrowser(), ResourcePreviewFactory.this.iconUser);
                        jpCommentsTabContent.add(lCommentAuthor, c);
                        c.gridx = 1;
                        c.gridy = 2 * iCnt;
                        c.weightx = (double)1.0F;
                        String strCommentDate = MyExperimentClient.formatDate(comment.getCreatedAt());
                        JLabel lCommentDate = new JLabel(" - [" + strCommentDate + "]");
                        lCommentDate.setBorder(lCommentAuthor.getBorder());
                        jpCommentsTabContent.add(lCommentDate, c);
                        c.gridx = 0;
                        c.gridy = 2 * iCnt + 1;
                        c.weightx = (double)1.0F;
                        c.gridwidth = 2;
                        c.fill = 2;
                        if (iCnt + 1 == comments.size()) {
                           c.weighty = (double)1.0F;
                        }

                        DialogTextArea taCommentText = new DialogTextArea(Util.stripAllHTML(comment.getComment()));
                        taCommentText.setBorder(BorderFactory.createEmptyBorder(0, 25, 10, 25));
                        taCommentText.setOpaque(false);
                        taCommentText.setEditable(false);
                        taCommentText.setLineWrap(true);
                        taCommentText.setWrapStyleWord(true);
                        jpCommentsTabContent.add(taCommentText, c);
                        ++iCnt;
                     }

                     jpCommentsTabContent.validate();
                     jpCommentsTabContent.repaint();
                     SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                           jpCommentsTabContent.scrollRectToVisible(new Rectangle());
                        }
                     });
                  }
               });
            }
         }).start();
      } else {
         jpCommentsTabContent.add(Util.generateNoneTextLabel("None"));
      }

      JScrollPane spCommentsTabContent = this.wrapPreviewTabContentIntoScrollPane(jpCommentsTabContent);
      spCommentsTabContent.setHorizontalScrollBarPolicy(31);
      return spCommentsTabContent;
   }

   private JScrollPane createCreditsPreviewTab(List<Resource> lCreditedUsersOrGroups) {
      JPanel jpCreditsTabContent = this.createStandardTabContentPanel();
      if (lCreditedUsersOrGroups.size() > 0) {
         for(Resource r : lCreditedUsersOrGroups) {
            jpCreditsTabContent.add(new JClickableLabel(r.getTitle(), "preview:" + r.getItemType() + ":" + r.getURI(), this.pluginMainComponent.getPreviewBrowser(), new ImageIcon(MyExperimentPerspective.getLocalIconURL(r.getItemType()))));
         }
      } else {
         jpCreditsTabContent.add(Util.generateNoneTextLabel("None"));
      }

      return this.wrapPreviewTabContentIntoScrollPane(jpCreditsTabContent);
   }

   private JScrollPane createAttributionsPreviewTab(List<Resource> lAttributions) {
      JPanel jpAttributionsTabContent = this.createStandardTabContentPanel();
      if (lAttributions.size() > 0) {
         for(Resource r : lAttributions) {
            jpAttributionsTabContent.add(new JClickableLabel(r.getTitle(), "preview:" + r.getItemType() + ":" + r.getURI(), this.pluginMainComponent.getPreviewBrowser(), new ImageIcon(MyExperimentPerspective.getLocalIconURL(r.getItemType()))));
         }
      } else {
         jpAttributionsTabContent.add(Util.generateNoneTextLabel("None"));
      }

      return this.wrapPreviewTabContentIntoScrollPane(jpAttributionsTabContent);
   }

   private JPanel createStandardTabContentPanel() {
      JPanel jpTabContentPanel = new JPanel();
      jpTabContentPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
      jpTabContentPanel.setLayout(new BoxLayout(jpTabContentPanel, 1));
      return jpTabContentPanel;
   }

   private JScrollPane wrapPreviewTabContentIntoScrollPane(JPanel jpTabContentPanel) {
      Dimension dPreferredTabSize = new Dimension(700, 250);
      JScrollPane spTabContent = new JScrollPane(jpTabContentPanel);
      spTabContent.setBorder(BorderFactory.createEmptyBorder());
      spTabContent.setPreferredSize(dPreferredTabSize);
      spTabContent.getVerticalScrollBar().setUnitIncrement(10);
      return spTabContent;
   }

   private JPanel wrapTextPaneAndTabbedViewIntoFullPreview(JTextPane tpHTMLPreview, JTabbedPane tpTabbedView) {
      JPanel jpFullPreview = new JPanel();
      jpFullPreview.setBackground(Color.WHITE);
      jpFullPreview.setLayout(new GridBagLayout());
      GridBagConstraints c = new GridBagConstraints();
      c.gridx = 0;
      c.gridy = 0;
      c.weighty = (double)0.0F;
      jpFullPreview.add(tpHTMLPreview, c);
      c.gridx = 0;
      c.gridy = 1;
      c.weighty = (double)1.0F;
      c.fill = 3;
      c.insets = new Insets(20, 0, 5, 0);
      jpFullPreview.add(tpTabbedView, c);
      return jpFullPreview;
   }
}
