package org.apache.taverna.ui.perspectives.myexperiment;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.Border;
import org.apache.log4j.Logger;
import org.apache.taverna.lang.ui.ShadedLabel;
import org.apache.taverna.ui.perspectives.myexperiment.model.Base64;
import org.apache.taverna.ui.perspectives.myexperiment.model.MyExperimentClient;
import org.apache.taverna.ui.perspectives.myexperiment.model.Resource;
import org.apache.taverna.ui.perspectives.myexperiment.model.SearchEngine;
import org.apache.taverna.ui.perspectives.myexperiment.model.Tag;
import org.apache.taverna.ui.perspectives.myexperiment.model.Util;
import org.apache.taverna.workbench.icons.WorkbenchIcons;

public class HistoryBrowserTabContentPanel extends JPanel implements ActionListener {
   public static final int DOWNLOADED_ITEMS_HISTORY_LENGTH = 50;
   public static final int OPENED_ITEMS_HISTORY_LENGTH = 50;
   public static final int UPLOADED_ITEMS_HISTORY_LENGTH = 50;
   public static final int COMMENTED_ON_ITEMS_HISTORY_LENGTH = 50;
   public static final int PREVIEWED_ITEMS_HISTORY = 0;
   public static final int DOWNLOADED_ITEMS_HISTORY = 1;
   public static final int OPENED_ITEMS_HISTORY = 2;
   public static final int UPLOADED_ITEMS_HISTORY = 4;
   public static final int COMMENTED_ON_ITEMS_HISTORY = 3;
   private final MainComponent pluginMainComponent;
   private final MyExperimentClient myExperimentClient;
   private final Logger logger;
   private ArrayList<Resource> lDownloadedItems;
   private ArrayList<Resource> lOpenedItems;
   private ArrayList<Resource> lUploadedItems;
   private ArrayList<Resource> lCommentedOnItems;
   private JPanel jpPreviewHistory;
   private JPanel jpSearchHistory;
   private JPanel jpTagSearchHistory;
   private JPanel jpDownloadedItemsHistory;
   private JPanel jpOpenedItemsHistory;
   private JPanel jpUploadedItemsHistory;
   private JPanel jpCommentedOnHistory;
   private JSplitPane spMain;
   private JClickableLabel jclPreviewHistory;
   private JClickableLabel jclSearchHistory;
   private JClickableLabel jclTagSearchHistory;
   private JClickableLabel jclDownloadedItemsHistory;
   private JClickableLabel jclOpenedItemsHistory;
   private JClickableLabel jclUploadedItemsHistory;
   private JClickableLabel jclCommentedOnHistory;
   private JPanel jpPreviewHistoryBox;
   private JPanel jpSearchHistoryBox;
   private JPanel jpTagSearchHistoryBox;
   private JPanel jpDownloadedItemsHistoryBox;
   private JPanel jpOpenedItemsHistoryBox;
   private JPanel jpUploadedItemsHistoryBox;
   private JPanel jpCommentedOnHistoryBox;

   public HistoryBrowserTabContentPanel(MainComponent component, MyExperimentClient client, Logger logger) {
      this.pluginMainComponent = component;
      this.myExperimentClient = client;
      this.logger = logger;
      String strDownloadedItemsHistory = (String)this.myExperimentClient.getSettings().get("downloaded_items_history");
      if (strDownloadedItemsHistory != null) {
         Object oDownloadedItemsHistory = Base64.decodeToObject(strDownloadedItemsHistory);
         this.lDownloadedItems = (ArrayList)oDownloadedItemsHistory;
      } else {
         this.lDownloadedItems = new ArrayList();
      }

      String strOpenedItemsHistory = (String)this.myExperimentClient.getSettings().get("opened_items_history");
      if (strOpenedItemsHistory != null) {
         Object oOpenedItemsHistory = Base64.decodeToObject(strOpenedItemsHistory);
         this.lOpenedItems = (ArrayList)oOpenedItemsHistory;
      } else {
         this.lOpenedItems = new ArrayList();
      }

      String strUploadedItemsHistory = (String)this.myExperimentClient.getSettings().get("uploaded_items_history");
      if (strUploadedItemsHistory != null) {
         Object oUploadedItemsHistory = Base64.decodeToObject(strUploadedItemsHistory);
         this.lUploadedItems = (ArrayList)oUploadedItemsHistory;
      } else {
         this.lUploadedItems = new ArrayList();
      }

      String strCommentedItemsHistory = (String)this.myExperimentClient.getSettings().get("commented_items_history");
      if (strCommentedItemsHistory != null) {
         Object oCommentedItemsHistory = Base64.decodeToObject(strCommentedItemsHistory);
         this.lCommentedOnItems = (ArrayList)oCommentedItemsHistory;
      } else {
         this.lCommentedOnItems = new ArrayList();
      }

      this.initialiseUI();
      this.refreshAllData();
   }

   private void confirmHistoryDelete(int id, String strBoxTitle) {
      if (JOptionPane.showConfirmDialog((Component)null, "This will the " + strBoxTitle.toLowerCase() + " list.\nDo you want to proceed?", "myExperiment Plugin - Confirmation Required", 0) == 0) {
         switch (id) {
            case 1:
               this.pluginMainComponent.getPreviewBrowser().clearPreviewHistory();
               break;
            case 2:
               this.pluginMainComponent.getSearchTab().getSearchHistory().clear();
               this.pluginMainComponent.getSearchTab().updateSearchHistory();
               break;
            case 3:
               this.pluginMainComponent.getTagBrowserTab().getTagSearchHistory().clear();
               break;
            case 4:
               this.clearDownloadedItemsHistory();
               break;
            case 5:
               this.clearOpenedItemsHistory();
               break;
            case 6:
               this.clearCommentedOnItemsHistory();
               break;
            case 7:
               this.clearUploadedItemsHistory();
         }

         this.refreshAllData();
      }

   }

   private JPanel addSpecifiedPanel(final int id, final String strBoxTitle, JPanel jPanel) {
      JPanel jpTemp = new JPanel();
      jpTemp.setLayout(new BorderLayout());
      jpTemp.add(generateContentBox(strBoxTitle, jPanel), "Center");
      JButton bClear = new JButton("Clear " + strBoxTitle, WorkbenchIcons.deleteIcon);
      bClear.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            HistoryBrowserTabContentPanel.this.confirmHistoryDelete(id, strBoxTitle);
         }
      });
      jpTemp.add(bClear, "South");
      jpTemp.setMinimumSize(new Dimension(500, 0));
      return jpTemp;
   }

   private void initialiseUI() {
      ShadedLabel lHelper = new ShadedLabel("All history sections are local to myExperiment plugin usage. Detailed history of your actions on myExperiment is available in your profile on myExperiment.", ShadedLabel.BLUE);
      this.jpPreviewHistory = new JPanel();
      this.jpTagSearchHistory = new JPanel();
      this.jpSearchHistory = new JPanel();
      this.jpDownloadedItemsHistory = new JPanel();
      this.jpOpenedItemsHistory = new JPanel();
      this.jpUploadedItemsHistory = new JPanel();
      this.jpCommentedOnHistory = new JPanel();
      JPanel jpSidebar = new JPanel();
      jpSidebar.setLayout(new BoxLayout(jpSidebar, 1));
      Border border = BorderFactory.createEmptyBorder(5, 3, 10, 3);
      this.jclPreviewHistory = new JClickableLabel("Previewed Items", "preview_history", this, WorkbenchIcons.editIcon, 2, "tooltip");
      this.jclPreviewHistory.setBorder(border);
      jpSidebar.add(this.jclPreviewHistory);
      this.jclSearchHistory = new JClickableLabel("Search History", "search_history", this, WorkbenchIcons.editIcon, 2, "tooltip");
      this.jclSearchHistory.setBorder(border);
      jpSidebar.add(this.jclSearchHistory);
      this.jclTagSearchHistory = new JClickableLabel("Tag Searches Made", "tag_history", this, WorkbenchIcons.editIcon, 2, "tooltip");
      this.jclTagSearchHistory.setBorder(border);
      jpSidebar.add(this.jclTagSearchHistory);
      this.jclDownloadedItemsHistory = new JClickableLabel("Downloaded Items", "downloads_history", this, WorkbenchIcons.editIcon, 2, "tooltip");
      this.jclDownloadedItemsHistory.setBorder(border);
      jpSidebar.add(this.jclDownloadedItemsHistory);
      this.jclOpenedItemsHistory = new JClickableLabel("Opened Items", "opened_history", this, WorkbenchIcons.editIcon, 2, "tooltip");
      this.jclOpenedItemsHistory.setBorder(border);
      jpSidebar.add(this.jclOpenedItemsHistory);
      this.jclUploadedItemsHistory = new JClickableLabel("Updated Items", "uploaded_history", this, WorkbenchIcons.editIcon, 2, "tooltip");
      this.jclUploadedItemsHistory.setBorder(border);
      jpSidebar.add(this.jclUploadedItemsHistory);
      this.jclCommentedOnHistory = new JClickableLabel("Items Commented On", "comments_history", this, WorkbenchIcons.editIcon, 2, "tooltip");
      this.jclCommentedOnHistory.setBorder(border);
      jpSidebar.add(this.jclCommentedOnHistory);
      JPanel jpSidebarContainer = new JPanel();
      jpSidebarContainer.add(jpSidebar, "North");
      JScrollPane spSidebar = new JScrollPane(jpSidebarContainer);
      spSidebar.getVerticalScrollBar().setUnitIncrement(10);
      spSidebar.setMinimumSize(new Dimension(245, 0));
      spSidebar.setMaximumSize(new Dimension(300, 0));
      this.jpPreviewHistoryBox = this.addSpecifiedPanel(1, "Items you have previewed", this.jpPreviewHistory);
      this.jpSearchHistoryBox = this.addSpecifiedPanel(2, "Terms you have searched for", this.jpSearchHistory);
      this.jpTagSearchHistoryBox = this.addSpecifiedPanel(3, "Tags searches you have made", this.jpTagSearchHistory);
      this.jpDownloadedItemsHistoryBox = this.addSpecifiedPanel(4, "Items you have downloaded", this.jpDownloadedItemsHistory);
      this.jpOpenedItemsHistoryBox = this.addSpecifiedPanel(5, "Workflows you have opened in Taverna", this.jpOpenedItemsHistory);
      this.jpCommentedOnHistoryBox = this.addSpecifiedPanel(6, "Items you have commented on in myExperiment", this.jpCommentedOnHistory);
      this.jpUploadedItemsHistoryBox = this.addSpecifiedPanel(7, "Items you have updated on myExperiment", this.jpUploadedItemsHistory);
      this.spMain = new JSplitPane();
      this.spMain.setLeftComponent(spSidebar);
      this.spMain.setRightComponent(this.jpPreviewHistoryBox);
      this.spMain.setOneTouchExpandable(true);
      this.spMain.setDividerLocation(247);
      this.spMain.setDoubleBuffered(true);
      this.setLayout(new BorderLayout());
      this.add(this.spMain);
      this.add(lHelper, "North");
   }

   public ArrayList<Resource> getDownloadedItemsHistoryList() {
      return this.lDownloadedItems;
   }

   public void clearDownloadedItemsHistory() {
      this.lDownloadedItems.clear();
   }

   public ArrayList<Resource> getOpenedItemsHistoryList() {
      return this.lOpenedItems;
   }

   public ArrayList<Resource> getUploadedItemsHistoryList() {
      return this.lUploadedItems;
   }

   public void clearOpenedItemsHistory() {
      this.lOpenedItems.clear();
   }

   public void clearUploadedItemsHistory() {
      this.lUploadedItems.clear();
   }

   public ArrayList<Resource> getCommentedOnItemsHistoryList() {
      return this.lCommentedOnItems;
   }

   public void clearCommentedOnItemsHistory() {
      this.lCommentedOnItems.clear();
   }

   private void refreshAllData() {
      this.refreshHistoryBox(0);
      this.refreshHistoryBox(1);
      this.refreshHistoryBox(2);
      this.refreshHistoryBox(4);
      this.refreshHistoryBox(3);
      this.refreshSearchHistory();
      this.refreshTagSearchHistory();
   }

   public void refreshHistoryBox(int historyType) {
      switch (historyType) {
         case 0:
            this.jpPreviewHistory.removeAll();
            this.populateHistoryBox(this.pluginMainComponent.getPreviewBrowser().getPreviewHistory(), this.jpPreviewHistory, "No items were previewed yet");
            break;
         case 1:
            this.jpDownloadedItemsHistory.removeAll();
            this.populateHistoryBox(this.lDownloadedItems, this.jpDownloadedItemsHistory, "No items were downloaded");
            break;
         case 2:
            this.jpOpenedItemsHistory.removeAll();
            this.populateHistoryBox(this.lOpenedItems, this.jpOpenedItemsHistory, "No items were opened");
            break;
         case 3:
            this.jpCommentedOnHistory.removeAll();
            this.populateHistoryBox(this.lCommentedOnItems, this.jpCommentedOnHistory, "You didn't comment on any items");
            break;
         case 4:
            this.jpUploadedItemsHistory.removeAll();
            this.populateHistoryBox(this.lUploadedItems, this.jpUploadedItemsHistory, "No items were updated");
      }

   }

   private void populateHistoryBox(List<Resource> lHistory, JPanel jpPanelToPopulate, String strLabelIfNoItems) {
      if (lHistory.size() > 0) {
         for(int i = lHistory.size() - 1; i >= 0; --i) {
            Resource r = (Resource)lHistory.get(i);
            if (r != null) {
               JClickableLabel lResource = Util.generateClickableLabelFor(r, this.pluginMainComponent.getPreviewBrowser());
               jpPanelToPopulate.add(lResource);
            }
         }
      } else {
         jpPanelToPopulate.add(Util.generateNoneTextLabel(strLabelIfNoItems));
      }

      jpPanelToPopulate.revalidate();
      jpPanelToPopulate.repaint();
   }

   public void refreshSearchHistory() {
      this.jpSearchHistory.removeAll();
      this.populateSearchHistory();
   }

   private void populateSearchHistory() {
      List<SearchEngine.QuerySearchInstance> lSearchHistory = this.pluginMainComponent.getSearchTab().getSearchHistory();
      this.jpSearchHistory.setLayout(new GridBagLayout());
      GridBagConstraints c = new GridBagConstraints();
      c.anchor = 18;
      if (lSearchHistory.size() > 0) {
         for(int i = lSearchHistory.size() - 1; i >= 0; --i) {
            SearchEngine.QuerySearchInstance qsiCurrent = (SearchEngine.QuerySearchInstance)lSearchHistory.get(i);
            JClickableLabel jclCurrentEntryLabel = new JClickableLabel(qsiCurrent.getSearchQuery(), "searchFromHistory:" + i, this, WorkbenchIcons.findIcon, 2, qsiCurrent.toString());
            JLabel jlCurrentEntrySettings = new JLabel(qsiCurrent.detailsAsString());
            jlCurrentEntrySettings.setBorder(BorderFactory.createEmptyBorder(3, 5, 0, 0));
            JPanel jpCurrentSearchHistoryEntry = new JPanel();
            jpCurrentSearchHistoryEntry.setLayout(new GridBagLayout());
            c.gridx = 0;
            c.gridy = 0;
            c.weightx = (double)0.0F;
            jpCurrentSearchHistoryEntry.add(jclCurrentEntryLabel, c);
            c.gridx = 1;
            c.gridy = 0;
            c.weightx = (double)1.0F;
            jpCurrentSearchHistoryEntry.add(jlCurrentEntrySettings, c);
            c.gridy = lSearchHistory.size() - 1 - i;
            if (i == 0) {
               c.weighty = (double)1.0F;
            }

            this.jpSearchHistory.add(jpCurrentSearchHistoryEntry, c);
         }
      } else {
         c.weightx = (double)1.0F;
         c.weighty = (double)1.0F;
         this.jpSearchHistory.add(Util.generateNoneTextLabel("No searches have been done so far"), c);
      }

      this.jpSearchHistory.revalidate();
      this.jpSearchHistory.repaint();
   }

   public void refreshTagSearchHistory() {
      this.jpTagSearchHistory.removeAll();
      this.populateTagSearchHistory();
   }

   private void populateTagSearchHistory() {
      List<Tag> lTagSearchHistory = this.pluginMainComponent.getTagBrowserTab().getTagSearchHistory();
      if (lTagSearchHistory.size() > 0) {
         for(int i = lTagSearchHistory.size() - 1; i >= 0; --i) {
            Tag t = (Tag)lTagSearchHistory.get(i);
            JClickableLabel lTag = new JClickableLabel(t.getTagName(), "tag:" + t.getTagName(), this, new ImageIcon(MyExperimentPerspective.getLocalIconURL(30)), 2, "Tag: " + t.getTagName());
            this.jpTagSearchHistory.add(lTag);
         }
      } else {
         this.jpTagSearchHistory.add(Util.generateNoneTextLabel("No searches by tags have been made yet"));
      }

      this.jpTagSearchHistory.revalidate();
      this.jpTagSearchHistory.repaint();
   }

   private static JPanel generateContentBox(String strBoxTitle, JPanel jpContentPanel) {
      jpContentPanel.setLayout(new BoxLayout(jpContentPanel, 1));
      JScrollPane spContent = new JScrollPane(jpContentPanel);
      spContent.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
      spContent.setHorizontalScrollBarPolicy(31);
      spContent.getVerticalScrollBar().setUnitIncrement(10);
      JPanel jpBox = new JPanel();
      jpBox.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2), BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " " + strBoxTitle + " ")));
      jpBox.setLayout(new GridBagLayout());
      GridBagConstraints c = new GridBagConstraints();
      c.anchor = 18;
      c.fill = 1;
      c.weightx = (double)1.0F;
      c.weighty = (double)1.0F;
      jpBox.add(spContent, c);
      return jpBox;
   }

   public void actionPerformed(ActionEvent e) {
      if (e.getSource() instanceof JClickableLabel) {
         if (e.getActionCommand().startsWith("searchFromHistory")) {
            this.pluginMainComponent.getSearchTab().actionPerformed(e);
            this.pluginMainComponent.getMainTabs().setSelectedComponent(this.pluginMainComponent.getSearchTab());
         } else if (e.getActionCommand().startsWith("tag:")) {
            this.pluginMainComponent.getTagBrowserTab().actionPerformed(e);
            this.pluginMainComponent.getMainTabs().setSelectedComponent(this.pluginMainComponent.getTagBrowserTab());
         } else if (e.getActionCommand().contains("history")) {
            if (e.getActionCommand() == "preview_history") {
               this.spMain.setRightComponent(this.jpPreviewHistoryBox);
            } else if (e.getActionCommand() == "search_history") {
               this.spMain.setRightComponent(this.jpSearchHistoryBox);
            } else if (e.getActionCommand() == "tag_history") {
               this.spMain.setRightComponent(this.jpTagSearchHistoryBox);
            } else if (e.getActionCommand() == "downloads_history") {
               this.spMain.setRightComponent(this.jpDownloadedItemsHistoryBox);
            } else if (e.getActionCommand() == "opened_history") {
               this.spMain.setRightComponent(this.jpOpenedItemsHistoryBox);
            } else if (e.getActionCommand() == "uploaded_history") {
               this.spMain.setRightComponent(this.jpUploadedItemsHistoryBox);
            } else if (e.getActionCommand() == "comments_history") {
               this.spMain.setRightComponent(this.jpCommentedOnHistoryBox);
            }
         }
      }

   }
}
