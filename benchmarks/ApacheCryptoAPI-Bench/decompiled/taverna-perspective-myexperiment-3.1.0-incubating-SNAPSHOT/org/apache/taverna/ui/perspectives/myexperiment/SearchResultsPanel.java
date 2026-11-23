package org.apache.taverna.ui.perspectives.myexperiment;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import org.apache.log4j.Logger;
import org.apache.taverna.ui.perspectives.myexperiment.model.MyExperimentClient;
import org.apache.taverna.ui.perspectives.myexperiment.model.Resource;
import org.apache.taverna.workbench.icons.WorkbenchIcons;

public class SearchResultsPanel extends JPanel {
   public static final String NO_SEARCHES_STATUS = "No searches have been done so far";
   private MainComponent pluginMainComponent;
   private MyExperimentClient myExperimentClient;
   private Logger logger;
   private JLabel lStatusLabel;
   public JButton bRefresh;
   public JButton bClear;
   private JPanel jpResultsBody;
   private ActionListener alClickHandler;
   boolean bNoSearchesMadeYet;
   String strCurrentSearchTerm;
   HashMap<Integer, ArrayList<Resource>> hmSearchResults;

   public SearchResultsPanel(ActionListener buttonClickHandler, MainComponent component, MyExperimentClient client, Logger logger) {
      this.pluginMainComponent = component;
      this.myExperimentClient = client;
      this.logger = logger;
      this.alClickHandler = buttonClickHandler;
      this.bNoSearchesMadeYet = true;
      this.hmSearchResults = new HashMap();
      this.initialiseUI();
   }

   private void initialiseUI() {
      this.lStatusLabel = new JLabel("No searches have been done so far");
      this.lStatusLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
      JPanel jpButtonsPanel = new JPanel();
      jpButtonsPanel.setLayout(new BoxLayout(jpButtonsPanel, 2));
      this.bClear = new JButton("Clear", WorkbenchIcons.deleteIcon);
      this.bClear.addActionListener(this.alClickHandler);
      this.bClear.setToolTipText("Click this button to clear the search results");
      this.bClear.setEnabled(false);
      jpButtonsPanel.add(this.bClear);
      this.bRefresh = new JButton("Refresh", WorkbenchIcons.refreshIcon);
      this.bRefresh.addActionListener(this.alClickHandler);
      this.bRefresh.setToolTipText("Click this button to refresh the search results");
      this.bRefresh.setEnabled(false);
      jpButtonsPanel.add(this.bRefresh);
      JPanel jpStatusPanel = new JPanel(new BorderLayout());
      jpStatusPanel.setBorder(BorderFactory.createEtchedBorder());
      jpStatusPanel.add(this.lStatusLabel, "Center");
      jpStatusPanel.add(jpButtonsPanel, "East");
      this.jpResultsBody = new JPanel();
      this.jpResultsBody.setLayout(new BorderLayout());
      this.setLayout(new BorderLayout());
      this.add(jpStatusPanel, "North");
      this.add(this.jpResultsBody, "Center");
   }

   public void setCurrentSearchTerm(String strSearchTerm) {
      this.strCurrentSearchTerm = strSearchTerm;
   }

   public String getCurrentSearchTerm() {
      return this.strCurrentSearchTerm;
   }

   public void setSearchResultsData(HashMap<Integer, ArrayList<Resource>> hmResults) {
      this.bNoSearchesMadeYet = false;
      this.hmSearchResults = hmResults;
   }

   public void setStatus(String status) {
      this.lStatusLabel.setText(status);
   }

   public void refresh() {
      this.jpResultsBody.removeAll();
      if (!this.bNoSearchesMadeYet) {
         ArrayList<Integer> alResourceTypes = new ArrayList(this.hmSearchResults.keySet());
         Collections.sort(alResourceTypes);
         if (alResourceTypes.isEmpty()) {
            this.jpResultsBody.add(new JLabel("No items to display"), "North");
            this.repaint();
         } else {
            JTabbedPane tpResults = new JTabbedPane();

            for(int iType : alResourceTypes) {
               String strTabLabel = Resource.getResourceTypeName(iType) + "s (" + ((ArrayList)this.hmSearchResults.get(iType)).size() + ")";
               ResourceListPanel jpTabContents = new ResourceListPanel(this.pluginMainComponent, this.myExperimentClient, this.logger);
               jpTabContents.setListItems((List)this.hmSearchResults.get(iType));
               JScrollPane spTabContents = new JScrollPane(jpTabContents);
               spTabContents.getVerticalScrollBar().setUnitIncrement(10);
               tpResults.add(strTabLabel, spTabContents);
            }

            this.jpResultsBody.add(tpResults, "Center");
            this.bClear.setEnabled(true);
            this.bRefresh.setEnabled(true);
         }
      }

      this.revalidate();
   }

   public void clear() {
      this.jpResultsBody.removeAll();
      this.repaint();
      this.revalidate();
   }
}
