package org.apache.taverna.ui.perspectives.myexperiment;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import org.apache.taverna.ui.perspectives.myexperiment.model.Base64;
import org.apache.taverna.ui.perspectives.myexperiment.model.MyExperimentClient;
import org.apache.taverna.ui.perspectives.myexperiment.model.SearchEngine;
import org.apache.taverna.ui.perspectives.myexperiment.model.Tag;

public class TagBrowserTabContentPanel extends JPanel implements ActionListener {
   private static final double TAG_CLOUD_BALANCE = 0.35;
   private static final double TAG_SIDEBAR_BALANCE = 0.4;
   private static final int TAG_SEARCH_HISTORY_LENGTH = 50;
   private MainComponent pluginMainComponent;
   private MyExperimentClient myExperimentClient;
   private Logger logger;
   private JSplitPane spMainSplitPane;
   private JSplitPane spTagCloudSplitPane;
   private TagCloudPanel jpMyTags;
   private TagCloudPanel jpAllTags;
   private SearchResultsPanel jpTagSearchResults;
   private JButton bLoginToSeeMyTags;
   private JPanel jpLoginToSeeMyTags;
   private String strCurrentTagCommand;
   private ArrayList<Tag> lTagSearchHistory;
   private SearchEngine searchEngine;
   private Vector<Long> vCurrentSearchThreadID;

   public TagBrowserTabContentPanel(MainComponent component, MyExperimentClient client, Logger logger) {
      this.pluginMainComponent = component;
      this.myExperimentClient = client;
      this.logger = logger;
      this.strCurrentTagCommand = null;
      String strTagSearchHistory = (String)this.myExperimentClient.getSettings().get("tag_search_history");
      if (strTagSearchHistory != null) {
         Object oTagSearchHistory = Base64.decodeToObject(strTagSearchHistory);
         this.lTagSearchHistory = (ArrayList)oTagSearchHistory;
      } else {
         this.lTagSearchHistory = new ArrayList();
      }

      this.initialiseUI();
      this.vCurrentSearchThreadID = new Vector(1);
      this.vCurrentSearchThreadID.add((Object)null);
      this.searchEngine = new SearchEngine(this.vCurrentSearchThreadID, true, this.jpTagSearchResults, this.pluginMainComponent, this.myExperimentClient, logger);
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            TagBrowserTabContentPanel.this.spTagCloudSplitPane.setDividerLocation(0.35);
            TagBrowserTabContentPanel.this.spMainSplitPane.setDividerLocation(0.4);
            TagBrowserTabContentPanel.this.spMainSplitPane.setOneTouchExpandable(true);
            TagBrowserTabContentPanel.this.spMainSplitPane.setDoubleBuffered(true);
         }
      });
   }

   private void initialiseUI() {
      this.bLoginToSeeMyTags = new JButton("Login to see your tags", new ImageIcon(MyExperimentPerspective.getLocalResourceURL("login_icon")));
      this.bLoginToSeeMyTags.addActionListener(this);
      this.jpLoginToSeeMyTags = new JPanel();
      this.jpLoginToSeeMyTags.setLayout(new GridBagLayout());
      this.jpLoginToSeeMyTags.add(this.bLoginToSeeMyTags);
      this.jpMyTags = new TagCloudPanel("My Tags", 1, this, this.pluginMainComponent, this.myExperimentClient, this.logger);
      this.jpAllTags = new TagCloudPanel("All Tags", 0, this, this.pluginMainComponent, this.myExperimentClient, this.logger);
      this.spTagCloudSplitPane = new JSplitPane(0);
      this.spTagCloudSplitPane.setTopComponent((Component)(this.myExperimentClient.isLoggedIn() ? this.jpMyTags : this.jpLoginToSeeMyTags));
      this.spTagCloudSplitPane.setBottomComponent(this.jpAllTags);
      this.jpTagSearchResults = new SearchResultsPanel(this, this.pluginMainComponent, this.myExperimentClient, this.logger);
      this.spMainSplitPane = new JSplitPane();
      this.spMainSplitPane.setLeftComponent(this.spTagCloudSplitPane);
      this.spMainSplitPane.setRightComponent(this.jpTagSearchResults);
      this.setLayout(new BorderLayout());
      this.add(this.spMainSplitPane, "Center");
   }

   public void setMyTagsShown(boolean bShow) {
      if (bShow) {
         this.spTagCloudSplitPane.setTopComponent(this.jpMyTags);
      } else {
         this.spTagCloudSplitPane.setTopComponent(this.jpLoginToSeeMyTags);
      }

      this.spTagCloudSplitPane.setDividerLocation(0.35);
   }

   public void refresh() {
      if (this.myExperimentClient.isLoggedIn()) {
         this.jpMyTags.refresh();
      }

      this.jpAllTags.refresh();
   }

   public void rerunLastTagSearch() {
      if (this.strCurrentTagCommand != null) {
         this.actionPerformed(new ActionEvent(this.jpAllTags, 0, this.strCurrentTagCommand));
      }

   }

   public TagCloudPanel getMyTagPanel() {
      return this.jpMyTags;
   }

   public TagCloudPanel getAllTagPanel() {
      return this.jpAllTags;
   }

   public SearchResultsPanel getTagSearchResultPanel() {
      return this.jpTagSearchResults;
   }

   public ArrayList<Tag> getTagSearchHistory() {
      return this.lTagSearchHistory;
   }

   public void actionPerformed(ActionEvent e) {
      if (e.getSource().equals(this.jpTagSearchResults.bRefresh)) {
         this.getTagSearchResultPanel().bClear.setEnabled(false);
         this.rerunLastTagSearch();
      } else if (e.getSource().equals(this.jpTagSearchResults.bClear)) {
         this.strCurrentTagCommand = null;
         this.vCurrentSearchThreadID.set(0, (Object)null);
         this.getTagSearchResultPanel().clear();
         this.getTagSearchResultPanel().setStatus("No searches have been done so far");
         this.getTagSearchResultPanel().bClear.setEnabled(false);
         this.getTagSearchResultPanel().bRefresh.setEnabled(false);
      } else if (!(e.getSource() instanceof JClickableLabel) && !(e.getSource() instanceof TagCloudPanel)) {
         if (e.getSource().equals(this.bLoginToSeeMyTags)) {
            this.pluginMainComponent.getMyStuffTab().cTabContentComponentToSwitchToAfterLogin = this;
            this.pluginMainComponent.getMainTabs().setSelectedComponent(this.pluginMainComponent.getMyStuffTab());
         }
      } else if (e.getActionCommand().startsWith("tag:")) {
         this.strCurrentTagCommand = e.getActionCommand();
         this.searchEngine.searchAndPopulateResults(this.strCurrentTagCommand);
         Tag t = Tag.instantiateTagFromActionCommand(this.strCurrentTagCommand);
         this.lTagSearchHistory.remove(t);
         this.lTagSearchHistory.add(t);
         if (this.lTagSearchHistory.size() > 50) {
            this.lTagSearchHistory.remove(0);
         }

         if (this.pluginMainComponent.getHistoryBrowser() != null) {
            this.pluginMainComponent.getHistoryBrowser().refreshTagSearchHistory();
         }
      }

   }
}
