package org.apache.taverna.ui.perspectives.myexperiment;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import org.apache.taverna.ui.perspectives.myexperiment.model.Base64;
import org.apache.taverna.ui.perspectives.myexperiment.model.MyExperimentClient;
import org.apache.taverna.ui.perspectives.myexperiment.model.SearchEngine;
import org.apache.taverna.ui.perspectives.myexperiment.model.Util;
import org.apache.taverna.workbench.icons.WorkbenchIcons;

public class SearchTabContentPanel extends JPanel implements ActionListener {
   private static final int SEARCH_HISTORY_LENGTH = 50;
   private static final int SEARCH_FAVOURITES_LENGTH = 30;
   protected static final String SEARCH_FROM_FAVOURITES = "searchFromFavourites";
   protected static final String SEARCH_FROM_HISTORY = "searchFromHistory";
   protected static final String ADD_FAVOURITE_SEARCH_INSTANCE = "addFavouriteSearchInstance";
   protected static final String REMOVE_FAVOURITE_SEARCH_INSTANCE = "removeFavouriteSearchInstance";
   private final MainComponent pluginMainComponent;
   private final MyExperimentClient myExperimentClient;
   private final Logger logger;
   private JSplitPane spMainSplitPane;
   private SearchOptionsPanel jpSearchOptions;
   private JPanel jpFavouriteSearches;
   private JPanel jpSearchHistory;
   private SearchResultsPanel jpSearchResults;
   private final ImageIcon iconFavourite = new ImageIcon(MyExperimentPerspective.getLocalResourceURL("favourite_icon"));
   private final ImageIcon iconRemove = new ImageIcon(MyExperimentPerspective.getLocalResourceURL("destroy_icon"));
   private SearchEngine.QuerySearchInstance siPreviousSearch;
   private LinkedList<SearchEngine.QuerySearchInstance> llFavouriteSearches;
   private LinkedList<SearchEngine.QuerySearchInstance> llSearchHistory;
   private final SearchEngine searchEngine;
   private final Vector<Long> vCurrentSearchThreadID;

   public SearchTabContentPanel(MainComponent component, MyExperimentClient client, Logger logger) {
      this.pluginMainComponent = component;
      this.myExperimentClient = client;
      this.logger = logger;
      String strFavouriteSearches = (String)this.myExperimentClient.getSettings().get("favourite_searches");
      if (strFavouriteSearches != null) {
         Object oFavouriteSearches = Base64.decodeToObject(strFavouriteSearches);
         this.llFavouriteSearches = (LinkedList)oFavouriteSearches;
      } else {
         this.llFavouriteSearches = new LinkedList();
      }

      String strSearchHistory = (String)this.myExperimentClient.getSettings().get("search_history");
      if (strSearchHistory != null) {
         Object oSearchHistory = Base64.decodeToObject(strSearchHistory);
         this.llSearchHistory = (LinkedList)oSearchHistory;
      } else {
         this.llSearchHistory = new LinkedList();
      }

      this.initialiseUI();
      this.updateFavouriteSearches();
      this.updateSearchHistory();
      this.vCurrentSearchThreadID = new Vector(1);
      this.vCurrentSearchThreadID.add((Object)null);
      this.searchEngine = new SearchEngine(this.vCurrentSearchThreadID, false, this.jpSearchResults, this.pluginMainComponent, this.myExperimentClient, logger);
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            SearchTabContentPanel.this.spMainSplitPane.setDividerLocation(390);
            SearchTabContentPanel.this.spMainSplitPane.setOneTouchExpandable(true);
            SearchTabContentPanel.this.spMainSplitPane.setDoubleBuffered(true);
         }
      });
   }

   private void initialiseUI() {
      this.jpSearchOptions = new SearchOptionsPanel(this, this.pluginMainComponent, this.myExperimentClient, this.logger);
      this.jpSearchOptions.setMaximumSize(new Dimension(1024, 0));
      this.jpFavouriteSearches = new JPanel();
      this.jpFavouriteSearches.setMaximumSize(new Dimension(1024, 0));
      this.jpFavouriteSearches.setLayout(new GridBagLayout());
      this.jpFavouriteSearches.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " Favourite Searches "), BorderFactory.createEmptyBorder(0, 5, 5, 5)));
      this.jpSearchHistory = new JPanel();
      this.jpSearchHistory.setMaximumSize(new Dimension(1024, 0));
      this.jpSearchHistory.setLayout(new GridBagLayout());
      this.jpSearchHistory.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " Search History "), BorderFactory.createEmptyBorder(0, 5, 5, 5)));
      JPanel jpSearchSidebar = new JPanel();
      jpSearchSidebar.setLayout(new GridBagLayout());
      GridBagConstraints gbConstraints = new GridBagConstraints();
      gbConstraints.anchor = 18;
      gbConstraints.fill = 1;
      gbConstraints.weightx = (double)1.0F;
      gbConstraints.gridx = 0;
      gbConstraints.gridy = 0;
      jpSearchSidebar.add(this.jpSearchOptions, gbConstraints);
      gbConstraints.gridy = 1;
      jpSearchSidebar.add(this.jpFavouriteSearches, gbConstraints);
      gbConstraints.gridy = 2;
      jpSearchSidebar.add(this.jpSearchHistory, gbConstraints);
      JPanel jpSidebarContainer = new JPanel();
      jpSidebarContainer.setLayout(new BorderLayout());
      jpSidebarContainer.add(jpSearchSidebar, "North");
      JScrollPane spSearchSidebar = new JScrollPane(jpSidebarContainer);
      spSearchSidebar.getVerticalScrollBar().setUnitIncrement(10);
      spSearchSidebar.setMinimumSize(new Dimension(jpSidebarContainer.getMinimumSize().width + 20, 0));
      this.jpSearchResults = new SearchResultsPanel(this, this.pluginMainComponent, this.myExperimentClient, this.logger);
      this.spMainSplitPane = new JSplitPane();
      this.spMainSplitPane.setLeftComponent(spSearchSidebar);
      this.spMainSplitPane.setRightComponent(this.jpSearchResults);
      this.setLayout(new BorderLayout());
      this.add(this.spMainSplitPane);
   }

   private void addToSearchListQueue(LinkedList<SearchEngine.QuerySearchInstance> searchInstanceList, SearchEngine.QuerySearchInstance searchInstanceToAdd, int queueSize) {
      int iDuplicateIdx = searchInstanceList.indexOf(searchInstanceToAdd);
      if (searchInstanceList.size() == 0 || iDuplicateIdx != searchInstanceList.size() - 1) {
         if (iDuplicateIdx >= 0) {
            searchInstanceList.remove(iDuplicateIdx);
         }

         if (searchInstanceList.size() >= queueSize) {
            searchInstanceList.remove();
         }

         searchInstanceList.offer(searchInstanceToAdd);
      }

   }

   private void addToFavouriteSearches(SearchEngine.QuerySearchInstance searchInstance) {
      this.addToSearchListQueue(this.llFavouriteSearches, searchInstance, 30);
      Collections.sort(this.llFavouriteSearches);
   }

   protected void updateFavouriteSearches() {
      this.jpFavouriteSearches.removeAll();
      if (this.llFavouriteSearches.size() == 0) {
         GridBagConstraints c = new GridBagConstraints();
         c.weightx = (double)1.0F;
         c.anchor = 17;
         this.jpFavouriteSearches.add(Util.generateNoneTextLabel("No favourite searches"), c);
      } else {
         for(int i = this.llFavouriteSearches.size() - 1; i >= 0; --i) {
            this.addEntryToSearchListingPanel(this.llFavouriteSearches, i, "searchFromFavourites", this.jpFavouriteSearches, this.iconRemove, "removeFavouriteSearchInstance", "<html>Click to remove from your local favourite searches.<br>(This will not affect your myExperiment profile settings.)</html>");
         }
      }

      this.jpFavouriteSearches.repaint();
      this.jpFavouriteSearches.revalidate();
   }

   private void addToSearchHistory(SearchEngine.QuerySearchInstance searchInstance) {
      this.addToSearchListQueue(this.llSearchHistory, searchInstance, 50);
   }

   protected void updateSearchHistory() {
      this.jpSearchHistory.removeAll();
      if (this.llSearchHistory.size() == 0) {
         GridBagConstraints c = new GridBagConstraints();
         c.weightx = (double)1.0F;
         c.anchor = 17;
         this.jpSearchHistory.add(Util.generateNoneTextLabel("No searches have been done so far"), c);
      } else {
         for(int i = this.llSearchHistory.size() - 1; i >= 0; --i) {
            this.addEntryToSearchListingPanel(this.llSearchHistory, i, "searchFromHistory", this.jpSearchHistory, this.iconFavourite, "addFavouriteSearchInstance", "<html>Click to add to your local favourite searches - these will be available every time you use Taverna.<br>(This will not affect your myExperiment profile settings.)</html>");
         }
      }

      this.jpSearchHistory.repaint();
      this.jpSearchHistory.revalidate();
      if (this.pluginMainComponent.getHistoryBrowser() != null) {
         this.pluginMainComponent.getHistoryBrowser().refreshSearchHistory();
      }

   }

   private void addEntryToSearchListingPanel(List<SearchEngine.QuerySearchInstance> searchInstanceList, int iIndex, String searchAction, JPanel panelToPopulate, ImageIcon entryIcon, String iconAction, String iconActionTooltip) {
      JClickableLabel jclCurrentEntryLabel = new JClickableLabel(((SearchEngine.QuerySearchInstance)searchInstanceList.get(iIndex)).getSearchQuery(), searchAction + ":" + iIndex, this, WorkbenchIcons.findIcon, 2, ((SearchEngine.QuerySearchInstance)searchInstanceList.get(iIndex)).toString());
      JLabel jlCurrentEntrySettings = new JLabel(((SearchEngine.QuerySearchInstance)searchInstanceList.get(iIndex)).detailsAsString());
      jlCurrentEntrySettings.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
      JPanel jpCurentEntryDetails = new JPanel();
      jpCurentEntryDetails.setLayout(new GridBagLayout());
      GridBagConstraints c = new GridBagConstraints();
      c.anchor = 17;
      jpCurentEntryDetails.add(jclCurrentEntryLabel, c);
      c.weightx = (double)1.0F;
      jpCurentEntryDetails.add(jlCurrentEntrySettings, c);
      JClickableLabel jclFavourite = new JClickableLabel("", iconAction + ":" + iIndex, this, entryIcon, 2, iconActionTooltip);
      jclFavourite.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
      JPanel jpCurrentEntry = new JPanel();
      jpCurrentEntry.setLayout(new GridBagLayout());
      c.anchor = 17;
      c.weightx = (double)1.0F;
      jpCurrentEntry.add(jpCurentEntryDetails, c);
      c.anchor = 17;
      c.weightx = (double)0.0F;
      jpCurrentEntry.add(jclFavourite, c);
      c.fill = 2;
      c.weightx = (double)1.0F;
      c.gridx = 0;
      c.gridy = -1;
      panelToPopulate.add(jpCurrentEntry, c);
   }

   public void actionPerformed(ActionEvent e) {
      if (e.getSource().equals(this.jpSearchOptions.bSearch)) {
         if (this.jpSearchOptions.getSearchQuery().length() == 0) {
            JOptionPane.showMessageDialog((Component)null, "Search query is empty. Please specify your search query and try again.", "Error", 2);
            this.jpSearchOptions.focusSearchQueryField();
         } else {
            try {
               this.jpSearchOptions.jsResultLimit.commitEdit();
            } catch (ParseException var4) {
               JOptionPane.showMessageDialog((Component)null, "Invalid search result limit value. This should be an\ninteger in the range of 1..100", "MyExperiment Plugin - Error", 2);
               this.jpSearchOptions.tfResultLimitTextField.selectAll();
               this.jpSearchOptions.tfResultLimitTextField.requestFocusInWindow();
               return;
            }

            this.siPreviousSearch = new SearchEngine.QuerySearchInstance(this.jpSearchOptions.getSearchQuery(), this.jpSearchOptions.getResultCountLimit(), this.jpSearchOptions.getSearchWorkflows(), this.jpSearchOptions.getSearchFiles(), this.jpSearchOptions.getSearchPacks(), this.jpSearchOptions.getSearchUsers(), this.jpSearchOptions.getSearchGroups());
            this.jpSearchOptions.focusSearchQueryField();
            this.runSearch();
         }
      } else if (e.getSource() instanceof JClickableLabel) {
         if (!e.getActionCommand().startsWith("searchFromHistory") && !e.getActionCommand().startsWith("searchFromFavourites")) {
            if (e.getActionCommand().startsWith("addFavouriteSearchInstance")) {
               int iHistID = Integer.parseInt(e.getActionCommand().substring(e.getActionCommand().indexOf(":") + 1));
               final SearchEngine.QuerySearchInstance si = (SearchEngine.QuerySearchInstance)this.llSearchHistory.get(iHistID);
               SwingUtilities.invokeLater(new Runnable() {
                  public void run() {
                     SearchTabContentPanel.this.addToFavouriteSearches(si);
                     SearchTabContentPanel.this.updateFavouriteSearches();
                  }
               });
            } else if (e.getActionCommand().startsWith("removeFavouriteSearchInstance")) {
               int iFavouriteID = Integer.parseInt(e.getActionCommand().substring(e.getActionCommand().indexOf(":") + 1));
               this.llFavouriteSearches.remove(iFavouriteID);
               SwingUtilities.invokeLater(new Runnable() {
                  public void run() {
                     SearchTabContentPanel.this.updateFavouriteSearches();
                  }
               });
            }
         } else {
            int iEntryID = Integer.parseInt(e.getActionCommand().substring(e.getActionCommand().indexOf(":") + 1));
            final SearchEngine.QuerySearchInstance si = e.getActionCommand().startsWith("searchFromHistory") ? (SearchEngine.QuerySearchInstance)this.llSearchHistory.remove(iEntryID) : (SearchEngine.QuerySearchInstance)this.llFavouriteSearches.get(iEntryID);
            SwingUtilities.invokeLater(new Runnable() {
               public void run() {
                  SearchTabContentPanel.this.jpSearchOptions.setSearchQuery(si.getSearchQuery());
                  SearchTabContentPanel.this.jpSearchOptions.setSearchAllResourceTypes(false);
                  SearchTabContentPanel.this.jpSearchOptions.setSearchWorkflows(si.getSearchWorkflows());
                  SearchTabContentPanel.this.jpSearchOptions.setSearchFiles(si.getSearchFiles());
                  SearchTabContentPanel.this.jpSearchOptions.setSearchPacks(si.getSearchPacks());
                  SearchTabContentPanel.this.jpSearchOptions.setSearchUsers(si.getSearchUsers());
                  SearchTabContentPanel.this.jpSearchOptions.setSearchGroups(si.getSearchGroups());
                  SearchTabContentPanel.this.jpSearchOptions.setResultCountLimit(si.getResultCountLimit());
                  SearchTabContentPanel.this.siPreviousSearch = si;
                  SearchTabContentPanel.this.runSearch();
               }
            });
         }
      } else if (e.getSource().equals(this.jpSearchResults.bRefresh)) {
         this.jpSearchResults.bClear.setEnabled(false);
         this.rerunLastSearch();
      } else if (e.getSource().equals(this.jpSearchResults.bClear)) {
         this.siPreviousSearch = null;
         this.vCurrentSearchThreadID.set(0, (Object)null);
         this.jpSearchResults.clear();
         this.jpSearchResults.setStatus("No searches have been done so far");
         this.jpSearchResults.bClear.setEnabled(false);
         this.jpSearchResults.bRefresh.setEnabled(false);
      }

   }

   private void runSearch() {
      this.runSearch(true);
   }

   private void runSearch(boolean bUpdateHistory) {
      if (bUpdateHistory) {
         this.addToSearchHistory(this.siPreviousSearch);
         SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               SearchTabContentPanel.this.updateSearchHistory();
            }
         });
      }

      this.searchEngine.searchAndPopulateResults(this.siPreviousSearch);
   }

   public void rerunLastSearch() {
      if (this.siPreviousSearch != null) {
         this.runSearch();
      }

   }

   public SearchResultsPanel getSearchResultPanel() {
      return this.jpSearchResults;
   }

   public LinkedList<SearchEngine.QuerySearchInstance> getSearchFavouritesList() {
      return this.llFavouriteSearches;
   }

   public LinkedList<SearchEngine.QuerySearchInstance> getSearchHistory() {
      return this.llSearchHistory;
   }
}
