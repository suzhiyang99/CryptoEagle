package org.apache.taverna.ui.perspectives.myexperiment;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.log4j.Logger;
import org.apache.taverna.ui.perspectives.myexperiment.model.MyExperimentClient;
import org.apache.taverna.ui.perspectives.myexperiment.model.Workflow;
import org.apache.taverna.workbench.icons.WorkbenchIcons;

public class ExampleWorkflowsPanel extends JPanel implements ActionListener, ChangeListener {
   private static final String ACTION_REFRESH = "refresh_example_workflows";
   private MainComponent pluginMainComponent;
   private MyExperimentClient myExperimentClient;
   private Logger logger;
   private JLabel statusLabel;
   private JButton refreshButton;
   private List<Workflow> workflows = new ArrayList();
   private ResourceListPanel workflowsListPanel;

   public ExampleWorkflowsPanel(MainComponent component, MyExperimentClient client, Logger logger) {
      this.pluginMainComponent = component;
      this.myExperimentClient = client;
      this.logger = logger;
      this.initialiseUI();
   }

   public void actionPerformed(ActionEvent event) {
      if ("refresh_example_workflows".equals(event.getActionCommand())) {
         this.refresh();
      }

   }

   public void stateChanged(ChangeEvent event) {
   }

   public void clear() {
      this.statusLabel.setText("");
      this.workflowsListPanel.clear();
   }

   public void refresh() {
      this.pluginMainComponent.getStatusBar().setStatus(this.getClass().getName(), "Fetching example workflows from myExperiment");
      this.statusLabel.setText("");
      (new Thread("Refresh for ExampleWorkflowsPanel") {
         public void run() {
            ExampleWorkflowsPanel.this.logger.debug("Refreshing Example Workflows tab");

            try {
               ExampleWorkflowsPanel.this.workflows = ExampleWorkflowsPanel.this.myExperimentClient.getExampleWorkflows();
               SwingUtilities.invokeLater(new Runnable() {
                  public void run() {
                     ExampleWorkflowsPanel.this.repopulate();
                  }
               });
            } catch (Exception ex) {
               ExampleWorkflowsPanel.this.logger.error("Failed to refresh Example Workflows panel", ex);
            }

         }
      }).start();
   }

   public void repopulate() {
      this.logger.debug("Repopulating Example Workflows tab");
      this.pluginMainComponent.getStatusBar().setStatus(this.getClass().getName(), (String)null);
      this.statusLabel.setText("This will contain example resources to help get you started in Taverna.  Coming Soon.");
      this.statusLabel.setText(this.workflows.size() + " example workflows found");
      this.workflowsListPanel.setListItems(new ArrayList(this.workflows));
      this.revalidate();
   }

   private void initialiseUI() {
      this.setLayout(new BorderLayout());
      JPanel topPanel = new JPanel(new BorderLayout());
      topPanel.setBorder(BorderFactory.createEtchedBorder());
      this.statusLabel = new JLabel();
      this.statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 7, 0, 0));
      topPanel.add(this.statusLabel, "Center");
      this.refreshButton = new JButton("Refresh", WorkbenchIcons.refreshIcon);
      this.refreshButton.setActionCommand("refresh_example_workflows");
      this.refreshButton.addActionListener(this);
      this.refreshButton.setToolTipText("Click this button to refresh the Example Workflows list");
      topPanel.add(this.refreshButton, "East");
      this.add(topPanel, "North");
      this.workflowsListPanel = new ResourceListPanel(this.pluginMainComponent, this.myExperimentClient, this.logger);
      JScrollPane spExampleWorkflowList = new JScrollPane(this.workflowsListPanel);
      spExampleWorkflowList.getVerticalScrollBar().setUnitIncrement(10);
      this.add(spExampleWorkflowList, "Center");
   }
}
