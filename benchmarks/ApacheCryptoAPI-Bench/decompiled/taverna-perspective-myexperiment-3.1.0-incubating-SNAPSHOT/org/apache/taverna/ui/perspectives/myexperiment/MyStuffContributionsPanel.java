package org.apache.taverna.ui.perspectives.myexperiment;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import org.apache.taverna.lang.ui.ShadedLabel;
import org.apache.taverna.ui.perspectives.myexperiment.model.File;
import org.apache.taverna.ui.perspectives.myexperiment.model.MyExperimentClient;
import org.apache.taverna.ui.perspectives.myexperiment.model.Pack;
import org.apache.taverna.ui.perspectives.myexperiment.model.Workflow;
import org.jdom.Document;
import org.jdom.Element;

public class MyStuffContributionsPanel extends JPanel implements ActionListener {
   private static final int SHADED_LABEL_HEIGHT = 20;
   private static final int SECTION_VSPACING = 5;
   private static final int SCROLL_PANE_PADDING = 3;
   private static final int TOTAL_SECTION_VSPACING = 33;
   private MainComponent pluginMainComponent;
   private MyExperimentClient myExperimentClient;
   private Logger logger;
   JPanel jpMyWorkflows;
   JPanel jpMyFiles;
   JPanel jpMyPacks;
   ResourceListPanel jpMyWorkflowsContent = null;
   ResourceListPanel jpMyFilesContent = null;
   ResourceListPanel jpMyPacksContent = null;
   JScrollPane spMyWorkflowsContent = null;
   JScrollPane spMyFilesContent = null;
   JScrollPane spMyPacksContent = null;
   private ArrayList<JPanel> alVisiblePanels;
   private ArrayList<JComponent[]> alVisiblePanelsWithHelperElements;

   public MyStuffContributionsPanel(MainComponent component, MyExperimentClient client, Logger logger) {
      this.pluginMainComponent = component;
      this.myExperimentClient = client;
      this.logger = logger;
      this.alVisiblePanels = new ArrayList();
      this.alVisiblePanelsWithHelperElements = new ArrayList();
      if (this.myExperimentClient.getSettings().getProperty("show_workflows_in_my_stuff") == null) {
         this.myExperimentClient.getSettings().put("show_workflows_in_my_stuff", (new Boolean(true)).toString());
      }

      if (this.myExperimentClient.getSettings().getProperty("show_files_in_my_stuff") == null) {
         this.myExperimentClient.getSettings().put("show_files_in_my_stuff", (new Boolean(true)).toString());
      }

      if (this.myExperimentClient.getSettings().getProperty("show_packs_in_my_stuff") == null) {
         this.myExperimentClient.getSettings().put("show_packs_in_my_stuff", (new Boolean(true)).toString());
      }

      this.initialiseUI();
      this.initialiseData();
   }

   private void initialiseUI() {
      JPanel jpMyWorkflowsContainer = new JPanel();
      jpMyWorkflowsContainer.setBorder(BorderFactory.createEmptyBorder());
      if (Boolean.parseBoolean(this.myExperimentClient.getSettings().getProperty("show_workflows_in_my_stuff"))) {
         jpMyWorkflowsContainer.setBorder(BorderFactory.createEtchedBorder());
         jpMyWorkflowsContainer.setLayout(new BorderLayout());
         ShadedLabel l0 = new ShadedLabel("My Workflows", ShadedLabel.BLUE);
         jpMyWorkflowsContainer.add(l0, "North");
         this.jpMyWorkflows = new JPanel();
         this.jpMyWorkflows.setLayout(new BorderLayout());
         this.jpMyWorkflows.setBackground(Color.WHITE);
         this.jpMyWorkflows.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
         this.jpMyWorkflows.add(new JLabel("Loading...", new ImageIcon(MyExperimentPerspective.getLocalResourceURL("spinner")), 0));
         jpMyWorkflowsContainer.add(this.jpMyWorkflows, "Center");
         this.alVisiblePanels.add(this.jpMyWorkflows);
      }

      JPanel jpMyFilesContainer = new JPanel();
      if (Boolean.parseBoolean(this.myExperimentClient.getSettings().getProperty("show_files_in_my_stuff"))) {
         jpMyFilesContainer.setBorder(BorderFactory.createEtchedBorder());
         jpMyFilesContainer.setLayout(new BorderLayout());
         ShadedLabel l1 = new ShadedLabel("My Files", ShadedLabel.BLUE);
         jpMyFilesContainer.add(l1, "North");
         this.jpMyFiles = new JPanel();
         this.jpMyFiles.setLayout(new BorderLayout());
         this.jpMyFiles.setBackground(Color.WHITE);
         this.jpMyFiles.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
         this.jpMyFiles.add(new JLabel("Loading...", new ImageIcon(MyExperimentPerspective.getLocalResourceURL("spinner")), 0));
         jpMyFilesContainer.add(this.jpMyFiles, "Center");
         this.alVisiblePanels.add(this.jpMyFiles);
      }

      JPanel jpMyPacksContainer = new JPanel();
      if (Boolean.parseBoolean(this.myExperimentClient.getSettings().getProperty("show_packs_in_my_stuff"))) {
         jpMyPacksContainer.setBorder(BorderFactory.createEtchedBorder());
         jpMyPacksContainer.setLayout(new BorderLayout());
         ShadedLabel l2 = new ShadedLabel("My Packs", ShadedLabel.BLUE);
         jpMyPacksContainer.add(l2, "North");
         this.jpMyPacks = new JPanel();
         this.jpMyPacks.setLayout(new BorderLayout());
         this.jpMyPacks.setBackground(Color.WHITE);
         this.jpMyPacks.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
         this.jpMyPacks.add(new JLabel("Loading...", new ImageIcon(MyExperimentPerspective.getLocalResourceURL("spinner")), 0));
         jpMyPacksContainer.add(this.jpMyPacks, "Center");
         this.alVisiblePanels.add(this.jpMyPacks);
      }

      JPanel jpEverything = new JPanel();
      jpEverything.setLayout(new GridBagLayout());
      GridBagConstraints gbConstraints = new GridBagConstraints();
      gbConstraints.anchor = 18;
      gbConstraints.fill = 1;
      gbConstraints.gridx = 0;
      gbConstraints.weightx = (double)1.0F;
      gbConstraints.weighty = (double)1.0F;
      int index = 0;
      gbConstraints.gridy = index++;
      jpEverything.add(jpMyWorkflowsContainer, gbConstraints);
      gbConstraints.gridy = index++;
      jpEverything.add(jpMyFilesContainer, gbConstraints);
      gbConstraints.gridy = index++;
      jpEverything.add(jpMyPacksContainer, gbConstraints);
      this.setLayout(new BorderLayout());
      this.add(jpEverything, "North");
   }

   private void initialiseData() {
      (new Thread("Loading data about contributions of current user.") {
         public void run() {
            MyStuffContributionsPanel.this.logger.debug("Loading contributions data for current user");

            try {
               final ArrayList<Workflow> alWorkflowInstances = new ArrayList();
               if (MyStuffContributionsPanel.this.alVisiblePanels.contains(MyStuffContributionsPanel.this.jpMyWorkflows)) {
                  boolean anyMore = true;

                  for(int page = 1; anyMore; ++page) {
                     Document doc = MyStuffContributionsPanel.this.myExperimentClient.getUserContributions(MyStuffContributionsPanel.this.myExperimentClient.getCurrentUser(), 10, 5015, page);
                     if (doc != null) {
                        List<Element> foundElements = doc.getRootElement().getChildren();
                        anyMore = !foundElements.isEmpty();

                        for(Element e : foundElements) {
                           Workflow wfCurrent = Workflow.buildFromXML(e, MyStuffContributionsPanel.this.logger);
                           alWorkflowInstances.add(wfCurrent);
                        }
                     }
                  }
               }

               final ArrayList<File> alFileInstances = new ArrayList();
               if (MyStuffContributionsPanel.this.alVisiblePanels.contains(MyStuffContributionsPanel.this.jpMyFiles)) {
                  boolean anyMore = true;

                  for(int page = 1; anyMore; ++page) {
                     Document doc = MyStuffContributionsPanel.this.myExperimentClient.getUserContributions(MyStuffContributionsPanel.this.myExperimentClient.getCurrentUser(), 11, 5015, page);
                     if (doc != null) {
                        List<Element> foundElements = doc.getRootElement().getChildren();
                        anyMore = !foundElements.isEmpty();

                        for(Element e : foundElements) {
                           File fCurrent = File.buildFromXML(e, MyStuffContributionsPanel.this.logger);
                           alFileInstances.add(fCurrent);
                        }
                     }
                  }
               }

               final ArrayList<Pack> alPackInstances = new ArrayList();
               if (MyStuffContributionsPanel.this.alVisiblePanels.contains(MyStuffContributionsPanel.this.jpMyPacks)) {
                  boolean anyMore = true;

                  for(int page = 1; anyMore; ++page) {
                     Document doc = MyStuffContributionsPanel.this.myExperimentClient.getUserContributions(MyStuffContributionsPanel.this.myExperimentClient.getCurrentUser(), 12, 5015, page);
                     if (doc != null) {
                        List<Element> foundElements = doc.getRootElement().getChildren();
                        anyMore = !foundElements.isEmpty();

                        for(Element e : foundElements) {
                           Pack pCurrent = Pack.buildFromXML(e, MyStuffContributionsPanel.this.myExperimentClient, MyStuffContributionsPanel.this.logger);
                           alPackInstances.add(pCurrent);
                        }
                     }
                  }
               }

               SwingUtilities.invokeLater(new Runnable() {
                  public void run() {
                     if (MyStuffContributionsPanel.this.alVisiblePanels.contains(MyStuffContributionsPanel.this.jpMyWorkflows)) {
                        MyStuffContributionsPanel.this.jpMyWorkflowsContent = new ResourceListPanel(MyStuffContributionsPanel.this.pluginMainComponent, MyStuffContributionsPanel.this.myExperimentClient, MyStuffContributionsPanel.this.logger);
                        MyStuffContributionsPanel.this.jpMyWorkflowsContent.setFullSizeItemsList(false);
                        MyStuffContributionsPanel.this.jpMyWorkflowsContent.setListItems(new ArrayList(alWorkflowInstances));
                        MyStuffContributionsPanel.this.spMyWorkflowsContent = new JScrollPane(MyStuffContributionsPanel.this.jpMyWorkflowsContent);
                        MyStuffContributionsPanel.this.spMyWorkflowsContent.getVerticalScrollBar().setUnitIncrement(10);
                        MyStuffContributionsPanel.this.jpMyWorkflows.removeAll();
                        MyStuffContributionsPanel.this.jpMyWorkflows.setBackground((Color)null);
                        MyStuffContributionsPanel.this.jpMyWorkflows.setBorder(BorderFactory.createEmptyBorder());
                        MyStuffContributionsPanel.this.jpMyWorkflows.add(MyStuffContributionsPanel.this.spMyWorkflowsContent);
                        MyStuffContributionsPanel.this.alVisiblePanelsWithHelperElements.add(new JComponent[]{MyStuffContributionsPanel.this.jpMyWorkflows, MyStuffContributionsPanel.this.spMyWorkflowsContent, MyStuffContributionsPanel.this.jpMyWorkflowsContent});
                     }

                     if (MyStuffContributionsPanel.this.alVisiblePanels.contains(MyStuffContributionsPanel.this.jpMyFiles)) {
                        MyStuffContributionsPanel.this.jpMyFilesContent = new ResourceListPanel(MyStuffContributionsPanel.this.pluginMainComponent, MyStuffContributionsPanel.this.myExperimentClient, MyStuffContributionsPanel.this.logger);
                        MyStuffContributionsPanel.this.jpMyFilesContent.setFullSizeItemsList(false);
                        MyStuffContributionsPanel.this.jpMyFilesContent.setListItems(new ArrayList(alFileInstances));
                        MyStuffContributionsPanel.this.spMyFilesContent = new JScrollPane(MyStuffContributionsPanel.this.jpMyFilesContent);
                        MyStuffContributionsPanel.this.spMyFilesContent.getVerticalScrollBar().setUnitIncrement(10);
                        MyStuffContributionsPanel.this.jpMyFiles.removeAll();
                        MyStuffContributionsPanel.this.jpMyFiles.setBackground((Color)null);
                        MyStuffContributionsPanel.this.jpMyFiles.setBorder(BorderFactory.createEmptyBorder());
                        MyStuffContributionsPanel.this.jpMyFiles.add(MyStuffContributionsPanel.this.spMyFilesContent);
                        MyStuffContributionsPanel.this.alVisiblePanelsWithHelperElements.add(new JComponent[]{MyStuffContributionsPanel.this.jpMyFiles, MyStuffContributionsPanel.this.spMyFilesContent, MyStuffContributionsPanel.this.jpMyFilesContent});
                     }

                     if (MyStuffContributionsPanel.this.alVisiblePanels.contains(MyStuffContributionsPanel.this.jpMyPacks)) {
                        MyStuffContributionsPanel.this.jpMyPacksContent = new ResourceListPanel(MyStuffContributionsPanel.this.pluginMainComponent, MyStuffContributionsPanel.this.myExperimentClient, MyStuffContributionsPanel.this.logger);
                        MyStuffContributionsPanel.this.jpMyPacksContent.setFullSizeItemsList(false);
                        MyStuffContributionsPanel.this.jpMyPacksContent.setListItems(new ArrayList(alPackInstances));
                        MyStuffContributionsPanel.this.spMyPacksContent = new JScrollPane(MyStuffContributionsPanel.this.jpMyPacksContent);
                        MyStuffContributionsPanel.this.spMyPacksContent.getVerticalScrollBar().setUnitIncrement(10);
                        MyStuffContributionsPanel.this.jpMyPacks.removeAll();
                        MyStuffContributionsPanel.this.jpMyPacks.setBackground((Color)null);
                        MyStuffContributionsPanel.this.jpMyPacks.setBorder(BorderFactory.createEmptyBorder());
                        MyStuffContributionsPanel.this.jpMyPacks.add(MyStuffContributionsPanel.this.spMyPacksContent);
                        MyStuffContributionsPanel.this.alVisiblePanelsWithHelperElements.add(new JComponent[]{MyStuffContributionsPanel.this.jpMyPacks, MyStuffContributionsPanel.this.spMyPacksContent, MyStuffContributionsPanel.this.jpMyPacksContent});
                     }

                     int iFullAvailableHeight = MyStuffContributionsPanel.this.getSize().height;
                     ArrayList<Integer> alIndexesToScale = new ArrayList();

                     for(int i = 0; i < MyStuffContributionsPanel.this.alVisiblePanelsWithHelperElements.size(); ++i) {
                        JScrollPane spContent = (JScrollPane)((JComponent[])MyStuffContributionsPanel.this.alVisiblePanelsWithHelperElements.get(i))[1];
                        JPanel jpContent = (JPanel)((JComponent[])MyStuffContributionsPanel.this.alVisiblePanelsWithHelperElements.get(i))[2];
                        if (jpContent.getPreferredSize().height + 33 < iFullAvailableHeight / MyStuffContributionsPanel.this.alVisiblePanels.size()) {
                           Dimension d = jpContent.getPreferredSize();
                           d.height += 3;
                           spContent.setPreferredSize(d);
                           iFullAvailableHeight -= jpContent.getPreferredSize().height + 33;
                        } else {
                           alIndexesToScale.add(i);
                        }
                     }

                     if (alIndexesToScale.size() > 0) {
                        Dimension d = new Dimension();

                        for(Integer i : alIndexesToScale) {
                           d.height = iFullAvailableHeight / alIndexesToScale.size() - 33;
                           if (d.height > ((JPanel)((JComponent[])MyStuffContributionsPanel.this.alVisiblePanelsWithHelperElements.get(i))[2]).getPreferredSize().height + 3) {
                              d.height = ((JPanel)((JComponent[])MyStuffContributionsPanel.this.alVisiblePanelsWithHelperElements.get(i))[2]).getPreferredSize().height + 3;
                           }

                           JScrollPane spCurrent = (JScrollPane)((JComponent[])MyStuffContributionsPanel.this.alVisiblePanelsWithHelperElements.get(i))[1];
                           spCurrent.setPreferredSize(d);
                        }
                     }

                     MyStuffContributionsPanel.this.pluginMainComponent.getMyStuffTab().cdlComponentLoadingDone.countDown();
                     MyStuffContributionsPanel.this.validate();
                     MyStuffContributionsPanel.this.repaint();
                  }
               });
            } catch (Exception ex) {
               MyStuffContributionsPanel.this.logger.error("Failed to populate some panel in My Stuff tab (User's files, workflows or packs)", ex);
            }

         }
      }).start();
   }

   public void actionPerformed(ActionEvent arg0) {
      JOptionPane.showMessageDialog((Component)null, "button clicked");
   }
}
