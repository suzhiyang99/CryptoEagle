package org.apache.taverna.ui.perspectives.myexperiment.model;

import java.awt.Component;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import org.apache.taverna.ui.perspectives.myexperiment.MainComponent;
import org.apache.taverna.ui.perspectives.myexperiment.SearchResultsPanel;
import org.jdom.Document;
import org.jdom.Element;

public class SearchEngine {
   private Vector<Long> vCurrentSearchThreadID;
   private boolean bSearchByTag;
   private String strSearchQuery;
   private SearchResultsPanel jpResultsPanel;
   private MainComponent pluginMainComponent;
   private MyExperimentClient myExperimentClient;
   private Logger logger;

   public SearchEngine(Vector<Long> currentSearchThreadIDVector, boolean bSearchByTag, SearchResultsPanel resultsPanel, MainComponent pluginMainComponent, MyExperimentClient client, Logger logger) {
      this.vCurrentSearchThreadID = currentSearchThreadIDVector;
      this.bSearchByTag = bSearchByTag;
      this.jpResultsPanel = resultsPanel;
      this.pluginMainComponent = pluginMainComponent;
      this.myExperimentClient = client;
      this.logger = logger;
   }

   public void searchAndPopulateResults(String tagQuery) {
      this.strSearchQuery = tagQuery;
      (new Thread("Search by tag") {
         public void run() {
            long lThisSearchThreadID = Thread.currentThread().getId();
            SearchEngine.this.vCurrentSearchThreadID.set(0, lThisSearchThreadID);
            SearchEngine.this.pluginMainComponent.getStatusBar().setStatus(SearchEngine.this.pluginMainComponent.getTagBrowserTab().getClass().getName(), "Searching");
            SearchEngine.this.strSearchQuery = Tag.instantiateTagFromActionCommand(SearchEngine.this.strSearchQuery).getTagName();
            SearchEngine.this.jpResultsPanel.setCurrentSearchTerm(SearchEngine.this.strSearchQuery);
            SearchEngine.this.jpResultsPanel.setStatus("Starting to search for a tag '" + SearchEngine.this.strSearchQuery + "'...");
            Document doc = SearchEngine.this.myExperimentClient.searchByTag(SearchEngine.this.strSearchQuery);
            if (doc == null) {
               SearchEngine.this.logger.error("Failed while fetching tagged items. See the error message above. Can't continue.");
               JOptionPane.showMessageDialog((Component)null, "An error occurred while searching for the results of your query. Please try again.", "Error", 0);
            } else {
               Element root = doc.getRootElement();
               SearchEngine.this.processSearchResults(lThisSearchThreadID, root.getChildren());
            }

         }
      }).start();
   }

   public void searchAndPopulateResults(final QuerySearchInstance querySearchDetails) {
      this.strSearchQuery = querySearchDetails.getSearchQuery();
      (new Thread("Search by query") {
         public void run() {
            long lThisSearchThreadID = Thread.currentThread().getId();
            SearchEngine.this.vCurrentSearchThreadID.set(0, lThisSearchThreadID);
            SearchEngine.this.pluginMainComponent.getStatusBar().setStatus(SearchEngine.this.pluginMainComponent.getSearchTab().getClass().getName(), "Searching");
            SearchEngine.this.strSearchQuery = SearchEngine.this.strSearchQuery.replaceFirst("search:", "");
            SearchEngine.this.jpResultsPanel.setCurrentSearchTerm(SearchEngine.this.strSearchQuery);
            SearchEngine.this.jpResultsPanel.setStatus("Starting to search for '" + SearchEngine.this.strSearchQuery + "'...");
            Document doc = SearchEngine.this.myExperimentClient.searchByQuery(querySearchDetails);
            if (doc == null) {
               SearchEngine.this.logger.error("Failed while fetching search result XML document. See the error message above. Can't continue.");
               JOptionPane.showMessageDialog((Component)null, "An error occurred while searching for the results of your query. Please try again.", "Error", 0);
            } else {
               Element root = doc.getRootElement();
               SearchEngine.this.processSearchResults(lThisSearchThreadID, root.getChildren());
            }

         }
      }).start();
   }

   private void processSearchResults(Long thisSearchThreadID, final List<Element> foundSearchItemElements) {
      Long lThisSearchThreadID = thisSearchThreadID;
      final HashMap<Integer, ArrayList<Resource>> hmClassifiedResults = new HashMap();

      for(Element foundItem : foundSearchItemElements) {
         if (!lThisSearchThreadID.equals(this.vCurrentSearchThreadID.get(0))) {
            return;
         }

         Resource res = Resource.buildFromXML(foundItem, this.myExperimentClient, this.logger);
         if (!hmClassifiedResults.containsKey(res.getItemType())) {
            hmClassifiedResults.put(res.getItemType(), new ArrayList());
         }

         ((ArrayList)hmClassifiedResults.get(res.getItemType())).add(res);
      }

      if (lThisSearchThreadID.equals(this.vCurrentSearchThreadID.get(0))) {
         SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               SearchEngine.this.jpResultsPanel.setStatus(foundSearchItemElements.size() + " items found " + (SearchEngine.this.bSearchByTag ? "with tag '" : "by query '") + SearchEngine.this.jpResultsPanel.getCurrentSearchTerm() + "'");
               SearchEngine.this.jpResultsPanel.setSearchResultsData(hmClassifiedResults);
               SearchEngine.this.jpResultsPanel.refresh();
               JComponent jcTabForWhichToSetStatus = (JComponent)(SearchEngine.this.bSearchByTag ? SearchEngine.this.pluginMainComponent.getTagBrowserTab() : SearchEngine.this.pluginMainComponent.getSearchTab());
               SearchEngine.this.pluginMainComponent.getStatusBar().setStatus(jcTabForWhichToSetStatus.getClass().getName(), (String)null);
            }
         });
      }

   }

   public static class QuerySearchInstance implements Comparable<QuerySearchInstance>, Serializable {
      private String strSearchQuery;
      private int iResultCountLimit;
      private boolean bSearchWorkflows;
      private boolean bSearchFiles;
      private boolean bSearchPacks;
      private boolean bSearchUsers;
      private boolean bSearchGroups;

      public QuerySearchInstance(String searchQuery, int resultCountLimit, boolean searchWorkflows, boolean searchFiles, boolean searchPacks, boolean searchUsers, boolean searchGroups) {
         this.strSearchQuery = searchQuery;
         this.iResultCountLimit = resultCountLimit;
         this.bSearchWorkflows = searchWorkflows;
         this.bSearchFiles = searchFiles;
         this.bSearchPacks = searchPacks;
         this.bSearchUsers = searchUsers;
         this.bSearchGroups = searchGroups;
      }

      public boolean equals(Object other) {
         if (!(other instanceof QuerySearchInstance)) {
            return false;
         } else {
            QuerySearchInstance si = (QuerySearchInstance)other;
            return this.strSearchQuery.equals(si.getSearchQuery()) && this.iResultCountLimit == si.getResultCountLimit() && this.bSearchWorkflows == si.getSearchWorkflows() && this.bSearchFiles == si.getSearchFiles() && this.bSearchPacks == si.getSearchPacks() && this.bSearchUsers == si.getSearchUsers() && this.bSearchGroups == si.getSearchGroups();
         }
      }

      public int compareTo(QuerySearchInstance other) {
         return this.equals(other) ? 0 : -1 * this.toString().compareTo(other.toString());
      }

      public String toString() {
         return "Search: '" + this.strSearchQuery + "' " + this.detailsAsString();
      }

      public String detailsAsString() {
         String str = "";
         int iCnt = 0;
         if (this.bSearchWorkflows) {
            str = str + "workflows, ";
            ++iCnt;
         }

         if (this.bSearchFiles) {
            str = str + "files, ";
            ++iCnt;
         }

         if (this.bSearchPacks) {
            str = str + "packs, ";
            ++iCnt;
         }

         if (this.bSearchUsers) {
            str = str + "users, ";
            ++iCnt;
         }

         if (this.bSearchGroups) {
            str = str + "groups, ";
            ++iCnt;
         }

         if (iCnt == 5) {
            str = "all";
         } else {
            str = str.substring(0, str.length() - 2);
         }

         str = "[" + str + "; limit: " + this.iResultCountLimit + "]";
         return str;
      }

      public String getSearchQuery() {
         return this.strSearchQuery;
      }

      public int getResultCountLimit() {
         return this.iResultCountLimit;
      }

      public boolean getSearchWorkflows() {
         return this.bSearchWorkflows;
      }

      public boolean getSearchFiles() {
         return this.bSearchFiles;
      }

      public boolean getSearchPacks() {
         return this.bSearchPacks;
      }

      public boolean getSearchUsers() {
         return this.bSearchUsers;
      }

      public boolean getSearchGroups() {
         return this.bSearchGroups;
      }
   }
}
