package org.apache.taverna.ui.perspectives.myexperiment;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.apache.log4j.Logger;
import org.apache.taverna.ui.perspectives.myexperiment.model.MyExperimentClient;
import org.apache.taverna.ui.perspectives.myexperiment.model.Util;
import org.apache.taverna.workbench.helper.HelpEnabledDialog;

public class PluginPreferencesDialog extends HelpEnabledDialog implements ComponentListener, ActionListener {
   private final MainComponent pluginMainComponent;
   private final MyExperimentClient myExperimentClient;
   private final Logger logger;
   private JTextField tfMyExperimentURL;
   private JComboBox cbDefaultLoggedInTab;
   private JComboBox cbDefaultNotLoggedInTab;
   private JCheckBox cbMyStuffWorkflows;
   private JCheckBox cbMyStuffFiles;
   private JCheckBox cbMyStuffPacks;
   private JButton bSave;
   private JButton bCancel;
   private JClickableLabel jclClearPreviewHistory;
   private JClickableLabel jclClearSearchHistory;
   private JClickableLabel jclClearFavouriteSearches;
   private final Component[] pluginTabComponents;
   private final ArrayList<String> alPluginTabComponentNames;

   public PluginPreferencesDialog(JFrame owner, MainComponent component, MyExperimentClient client, Logger logger) {
      super(owner, "Plugin preferences", true);
      this.pluginMainComponent = component;
      this.myExperimentClient = client;
      this.logger = logger;
      this.addComponentListener(this);
      this.alPluginTabComponentNames = new ArrayList();
      this.pluginTabComponents = this.pluginMainComponent.getMainTabs().getComponents();

      for(int i = 0; i < this.pluginTabComponents.length; ++i) {
         this.alPluginTabComponentNames.add(this.pluginMainComponent.getMainTabs().getTitleAt(i));
      }

      this.initialiseUI();
      this.initialiseData();
   }

   private void initialiseUI() {
      GridBagConstraints c = new GridBagConstraints();
      JPanel jpApiLocation = new JPanel();
      jpApiLocation.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " myExperiment Location "), BorderFactory.createEmptyBorder(0, 5, 5, 5)));
      jpApiLocation.setLayout(new GridBagLayout());
      c.gridx = 0;
      c.gridy = 0;
      c.weightx = (double)1.0F;
      c.anchor = 17;
      jpApiLocation.add(new JLabel("Base URL of myExperiment instance to connect to"), c);
      c.gridy = 1;
      c.fill = 2;
      this.tfMyExperimentURL = new JTextField();
      this.tfMyExperimentURL.setToolTipText("<html>Here you can specify the base URL of the myExperiment instance that you wish to connect to.<br>This allows the plugin to connect not only to the <b>main myExperiment website</b> (default value:<br><b>http://www.myexperiment.org</b>) but also to any other myExperiment instance that might<br>exist elsewhere.<br><br>It is recommended that you only change this setting if you are certain in your actions.</html>");
      jpApiLocation.add(this.tfMyExperimentURL, c);
      JPanel jpStartupTabChoice = new JPanel();
      jpStartupTabChoice.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " Plugin Start-up Settings "), BorderFactory.createEmptyBorder(0, 5, 5, 5)));
      jpStartupTabChoice.setLayout(new GridBagLayout());
      c.gridx = 0;
      c.gridy = 0;
      c.weightx = (double)0.0F;
      c.insets = new Insets(0, 0, 0, 10);
      jpStartupTabChoice.add(new JLabel("Default startup tab for anonymous user"), c);
      c.gridx = 1;
      c.weightx = (double)1.0F;
      c.insets = new Insets(0, 0, 2, 0);
      this.cbDefaultNotLoggedInTab = new JComboBox(this.alPluginTabComponentNames.toArray());
      this.cbDefaultNotLoggedInTab.setToolTipText("<html>This tab will be automatically opened at plugin start up time if you are <b>not</b> logged id to myExperiment.</html>");
      jpStartupTabChoice.add(this.cbDefaultNotLoggedInTab, c);
      c.gridx = 0;
      c.gridy = 1;
      c.weightx = (double)0.0F;
      c.insets = new Insets(0, 0, 0, 10);
      jpStartupTabChoice.add(new JLabel("Default startup tab after successful auto-login"), c);
      c.gridx = 1;
      c.weightx = (double)1.0F;
      c.insets = new Insets(2, 0, 0, 0);
      this.cbDefaultLoggedInTab = new JComboBox(this.alPluginTabComponentNames.toArray());
      this.cbDefaultLoggedInTab.setToolTipText("<html>This tab will be automatically opened at plugin start up time if you have chosen to use <b>auto logging in</b> to myExperiment.</html>");
      jpStartupTabChoice.add(this.cbDefaultLoggedInTab, c);
      JPanel jpMyStuffPrefs = new JPanel();
      jpMyStuffPrefs.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " 'My Stuff' Tab Settings "), BorderFactory.createEmptyBorder(0, 5, 5, 5)));
      jpMyStuffPrefs.setLayout(new GridBagLayout());
      c.gridx = 0;
      c.gridy = 0;
      c.weightx = (double)0.0F;
      c.insets = new Insets(0, 0, 0, 0);
      jpMyStuffPrefs.add(new JLabel("Sections to show in this tab:"), c);
      c.gridx = 1;
      c.gridy = 0;
      c.weightx = (double)1.0F;
      c.insets = new Insets(0, 10, 0, 0);
      this.cbMyStuffWorkflows = new JCheckBox("My Workflows");
      jpMyStuffPrefs.add(this.cbMyStuffWorkflows, c);
      c.gridy = 1;
      this.cbMyStuffFiles = new JCheckBox("My Files");
      jpMyStuffPrefs.add(this.cbMyStuffFiles, c);
      c.gridy = 2;
      this.cbMyStuffPacks = new JCheckBox("My Packs");
      jpMyStuffPrefs.add(this.cbMyStuffPacks, c);
      JPanel jpPrivacySettings = new JPanel();
      jpPrivacySettings.setLayout(new BoxLayout(jpPrivacySettings, 1));
      jpPrivacySettings.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " Privacy Settings "), BorderFactory.createEmptyBorder(0, 7, 5, 5)));
      this.jclClearPreviewHistory = new JClickableLabel("Clear browsing history", "clear_preview_history", this);
      jpPrivacySettings.add(this.jclClearPreviewHistory);
      this.jclClearSearchHistory = new JClickableLabel("Clear search history", "clear_search_history", this);
      this.jclClearSearchHistory.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
      jpPrivacySettings.add(this.jclClearSearchHistory);
      this.jclClearFavouriteSearches = new JClickableLabel("Clear favourite searches", "clear_favourite_searches", this);
      this.jclClearFavouriteSearches.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
      jpPrivacySettings.add(this.jclClearFavouriteSearches);
      this.bSave = new JButton("Save");
      this.bSave.addActionListener(this);
      this.bCancel = new JButton("Cancel");
      this.bCancel.addActionListener(this);
      JPanel jpButtons = new JPanel();
      jpButtons.setLayout(new GridBagLayout());
      c.gridx = 0;
      c.gridy = 0;
      c.weightx = (double)0.0F;
      c.insets = new Insets(0, 0, 0, 2);
      jpButtons.add(this.bSave, c);
      c.gridx = 1;
      c.insets = new Insets(0, 2, 0, 0);
      jpButtons.add(this.bCancel, c);
      this.setTitle("myExperiment Plugin Preferences");
      BorderLayout layout = new BorderLayout();
      JPanel jpEverything = new JPanel();
      GridBagLayout jpEverythingLayout = new GridBagLayout();
      jpEverything.setLayout(jpEverythingLayout);
      this.getContentPane().setLayout(layout);
      GridBagConstraints gbConstraints = new GridBagConstraints();
      gbConstraints.fill = 1;
      gbConstraints.weightx = (double)1.0F;
      gbConstraints.gridx = 0;
      gbConstraints.gridy = 0;
      jpEverything.add(jpApiLocation, gbConstraints);
      gbConstraints.gridy = 1;
      jpEverything.add(jpStartupTabChoice, gbConstraints);
      gbConstraints.gridy = 2;
      jpEverything.add(jpMyStuffPrefs, gbConstraints);
      gbConstraints.gridy = 3;
      jpEverything.add(jpPrivacySettings, gbConstraints);
      gbConstraints.gridy = 4;
      jpEverything.add(jpButtons, gbConstraints);
      this.add(jpEverything);
      this.setResizable(false);
      this.pack();
      this.setMinimumSize(this.getPreferredSize());
   }

   private void initialiseData() {
      this.tfMyExperimentURL.setText(this.myExperimentClient.getSettings().getProperty("my_experiment_base_url"));
      this.cbDefaultNotLoggedInTab.setSelectedIndex(Integer.parseInt(this.myExperimentClient.getSettings().getProperty("default_tab_for_anonymous_users")));
      this.cbDefaultLoggedInTab.setSelectedIndex(Integer.parseInt(this.myExperimentClient.getSettings().getProperty("default_tab_for_logged_in_users")));
      this.cbMyStuffWorkflows.setSelected(Boolean.parseBoolean(this.myExperimentClient.getSettings().getProperty("show_workflows_in_my_stuff")));
      this.cbMyStuffFiles.setSelected(Boolean.parseBoolean(this.myExperimentClient.getSettings().getProperty("show_files_in_my_stuff")));
      this.cbMyStuffPacks.setSelected(Boolean.parseBoolean(this.myExperimentClient.getSettings().getProperty("show_packs_in_my_stuff")));
   }

   public void componentShown(ComponentEvent e) {
      Util.centerComponentWithinAnother(this.pluginMainComponent, this);
      this.initialiseData();
   }

   public void componentHidden(ComponentEvent e) {
   }

   public void componentResized(ComponentEvent e) {
   }

   public void componentMoved(ComponentEvent e) {
   }

   public void actionPerformed(ActionEvent e) {
      if (e.getSource().equals(this.bSave)) {
         String strNewMyExperimentURL = this.tfMyExperimentURL.getText().trim();
         if (strNewMyExperimentURL.length() == 0) {
            JOptionPane.showMessageDialog((Component)null, "Please specify a base URL of myExperiment instance that you wish to connect to", "Error", 2);
            this.tfMyExperimentURL.requestFocusInWindow();
            return;
         }

         if (!this.cbMyStuffWorkflows.isSelected() && !this.cbMyStuffFiles.isSelected() && !this.cbMyStuffPacks.isSelected()) {
            JOptionPane.showMessageDialog((Component)null, "Please choose at least one section to display in 'My Stuff' tab", "Error", 2);
            this.cbMyStuffWorkflows.requestFocusInWindow();
            return;
         }

         if (!strNewMyExperimentURL.equals(this.myExperimentClient.getBaseURL())) {
            this.myExperimentClient.getSettings().put("auto_login", (new Boolean(false)).toString());
            JOptionPane.showMessageDialog((Component)null, "You have selected a new Base URL for myExperiment. Your new setting has been saved,\nbut will not take effect until you restart Taverna.\n\nAuto-login feature has been switched off for you to check the login details at the next launch.", "myExperiment Plugin - Info", 1);
         }

         this.myExperimentClient.getSettings().put("my_experiment_base_url", strNewMyExperimentURL);
         this.myExperimentClient.getSettings().put("default_tab_for_anonymous_users", (new Integer(this.cbDefaultNotLoggedInTab.getSelectedIndex())).toString());
         this.myExperimentClient.getSettings().put("default_tab_for_logged_in_users", (new Integer(this.cbDefaultLoggedInTab.getSelectedIndex())).toString());
         this.myExperimentClient.getSettings().put("show_workflows_in_my_stuff", (new Boolean(this.cbMyStuffWorkflows.isSelected())).toString());
         this.myExperimentClient.getSettings().put("show_files_in_my_stuff", (new Boolean(this.cbMyStuffFiles.isSelected())).toString());
         this.myExperimentClient.getSettings().put("show_packs_in_my_stuff", (new Boolean(this.cbMyStuffPacks.isSelected())).toString());
         this.setVisible(false);
      } else if (e.getSource().equals(this.bCancel)) {
         this.setVisible(false);
      } else if (e.getSource().equals(this.jclClearPreviewHistory)) {
         if (JOptionPane.showConfirmDialog((Component)null, "This will delete the browsing history - the lists of previously previewed,\ndownloaded, opened and commented on items will be emptied.\n\nDo you want to proceed?", "myExperiment Plugin - Confirmation Required", 0) == 0) {
            this.pluginMainComponent.getPreviewBrowser().clearPreviewHistory();
            this.pluginMainComponent.getHistoryBrowser().clearDownloadedItemsHistory();
            this.pluginMainComponent.getHistoryBrowser().clearOpenedItemsHistory();
            this.pluginMainComponent.getHistoryBrowser().clearCommentedOnItemsHistory();
            this.pluginMainComponent.getHistoryBrowser().refreshHistoryBox(0);
            this.pluginMainComponent.getHistoryBrowser().refreshHistoryBox(1);
            this.pluginMainComponent.getHistoryBrowser().refreshHistoryBox(2);
            this.pluginMainComponent.getHistoryBrowser().refreshHistoryBox(3);
         }
      } else if (e.getSource().equals(this.jclClearSearchHistory)) {
         if (JOptionPane.showConfirmDialog((Component)null, "This will delete both query and tag search history.\nDo you want to proceed?", "myExperiment Plugin - Confirmation Required", 0) == 0) {
            this.pluginMainComponent.getSearchTab().getSearchHistory().clear();
            this.pluginMainComponent.getTagBrowserTab().getTagSearchHistory().clear();
            this.pluginMainComponent.getSearchTab().updateSearchHistory();
            this.pluginMainComponent.getHistoryBrowser().refreshSearchHistory();
            this.pluginMainComponent.getHistoryBrowser().refreshTagSearchHistory();
         }
      } else if (e.getSource().equals(this.jclClearFavouriteSearches) && JOptionPane.showConfirmDialog((Component)null, "This will delete all your favourite search settings.\nDo you want to proceed?", "myExperiment Plugin - Confirmation Required", 0) == 0) {
         this.pluginMainComponent.getSearchTab().getSearchFavouritesList().clear();
         this.pluginMainComponent.getSearchTab().updateFavouriteSearches();
      }

   }
}
