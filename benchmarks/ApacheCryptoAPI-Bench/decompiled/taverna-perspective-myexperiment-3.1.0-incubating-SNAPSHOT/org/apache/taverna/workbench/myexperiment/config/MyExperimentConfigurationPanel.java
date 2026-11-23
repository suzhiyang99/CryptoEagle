package org.apache.taverna.workbench.myexperiment.config;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import org.apache.taverna.ui.perspectives.myexperiment.MainComponent;
import org.apache.taverna.ui.perspectives.myexperiment.model.MyExperimentClient;
import org.apache.taverna.workbench.helper.Helper;
import org.apache.taverna.workbench.icons.WorkbenchIcons;

public class MyExperimentConfigurationPanel extends JPanel implements ActionListener {
   private final MainComponent pluginMainComponent;
   private final MyExperimentClient myExperimentClient;
   private JTextField tfMyExperimentURL;
   private JComboBox cbDefaultLoggedInTab;
   private JComboBox cbDefaultNotLoggedInTab;
   private JCheckBox cbMyStuffWorkflows;
   private JCheckBox cbMyStuffFiles;
   private JCheckBox cbMyStuffPacks;
   private JButton bApply;
   private JButton bReset;
   private JButton bHelp;
   private final Component[] pluginTabComponents;
   private final ArrayList<String> alPluginTabComponentNames;

   public MyExperimentConfigurationPanel() {
      this.pluginMainComponent = MainComponent.MAIN_COMPONENT;
      this.myExperimentClient = MainComponent.MY_EXPERIMENT_CLIENT;
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
      jpApiLocation.setLayout(new GridBagLayout());
      Insets insLabel = new Insets(0, 0, 0, 10);
      Insets insParam = new Insets(0, 3, 5, 3);
      JTextArea descriptionText = new JTextArea("Configure the myExperiment integration functionality");
      descriptionText.setLineWrap(true);
      descriptionText.setWrapStyleWord(true);
      descriptionText.setEditable(false);
      descriptionText.setFocusable(false);
      descriptionText.setBorder(new EmptyBorder(10, 10, 10, 10));
      c.anchor = 17;
      c.gridx = 0;
      c.gridy = 0;
      c.weightx = (double)1.0F;
      c.weighty = (double)0.0F;
      c.fill = 2;
      jpApiLocation.add(descriptionText, c);
      c.gridx = 0;
      c.gridy = 1;
      c.weightx = (double)1.0F;
      c.anchor = 17;
      c.insets = new Insets(10, 0, 0, 10);
      jpApiLocation.add(new JLabel("Base URL of myExperiment instance to connect to"), c);
      c.gridy = 2;
      c.fill = 2;
      c.insets = new Insets(0, 0, 0, 0);
      this.tfMyExperimentURL = new JTextField();
      this.tfMyExperimentURL.setToolTipText("<html>Here you can specify the base URL of the myExperiment instance that you wish to connect to.<br>This allows the plugin to connect not only to the <b>main myExperiment website</b> (default value:<br><b>http://www.myexperiment.org</b>) but also to any other myExperiment instance that might<br>exist elsewhere.<br><br>It is recommended that you only change this setting if you are certain in your actions.</html>");
      jpApiLocation.add(this.tfMyExperimentURL, c);
      JPanel jpStartupTabChoice = new JPanel();
      jpStartupTabChoice.setLayout(new GridBagLayout());
      c.gridy = 0;
      c.insets = insLabel;
      jpStartupTabChoice.add(new JLabel("Default startup tab for anonymous user"), c);
      c.gridx = 1;
      c.insets = insParam;
      this.cbDefaultNotLoggedInTab = new JComboBox(this.alPluginTabComponentNames.toArray());
      this.cbDefaultNotLoggedInTab.setToolTipText("<html>This tab will be automatically opened at plugin start up time if you are <b>not</b> logged id to myExperiment.</html>");
      jpStartupTabChoice.add(this.cbDefaultNotLoggedInTab, c);
      c.gridy = 1;
      c.gridx = 0;
      c.insets = insLabel;
      jpStartupTabChoice.add(new JLabel("Default startup tab after successful auto-login"), c);
      c.gridx = 1;
      c.insets = insParam;
      this.cbDefaultLoggedInTab = new JComboBox(this.alPluginTabComponentNames.toArray());
      this.cbDefaultLoggedInTab.setToolTipText("<html>This tab will be automatically opened at plugin start up time if you have chosen to use <b>auto logging in</b> to myExperiment.</html>");
      jpStartupTabChoice.add(this.cbDefaultLoggedInTab, c);
      JPanel jpMyStuffPrefs = new JPanel();
      jpMyStuffPrefs.setLayout(new GridBagLayout());
      c.gridx = 0;
      c.gridy = 0;
      c.insets = insLabel;
      jpMyStuffPrefs.add(new JLabel("Sections to show in this tab:"), c);
      c.gridx = 1;
      c.gridy = 0;
      c.weightx = (double)1.0F;
      c.insets = new Insets(insParam.top, 100, insParam.bottom / 3, insParam.right);
      this.cbMyStuffWorkflows = new JCheckBox("My Workflows");
      jpMyStuffPrefs.add(this.cbMyStuffWorkflows, c);
      c.gridy = 1;
      this.cbMyStuffFiles = new JCheckBox("My Files");
      jpMyStuffPrefs.add(this.cbMyStuffFiles, c);
      c.gridy = 2;
      this.cbMyStuffPacks = new JCheckBox("My Packs");
      jpMyStuffPrefs.add(this.cbMyStuffPacks, c);
      this.bApply = new JButton("Apply");
      this.bApply.addActionListener(this);
      this.bReset = new JButton("Reset");
      this.bReset.addActionListener(this);
      this.bHelp = new JButton("Help");
      this.bHelp.addActionListener(this);
      JPanel jpButtons = new JPanel();
      jpButtons.add(this.bHelp, c);
      jpButtons.add(this.bReset, c);
      jpButtons.add(this.bApply, c);
      JPanel jpEverything = new JPanel();
      GridBagLayout jpEverythingLayout = new GridBagLayout();
      jpEverything.setLayout(jpEverythingLayout);
      GridBagConstraints gbConstraints = new GridBagConstraints();
      gbConstraints.fill = 1;
      c.anchor = 18;
      gbConstraints.weightx = (double)1.0F;
      gbConstraints.gridx = 0;
      gbConstraints.gridy = 0;
      jpEverything.add(jpApiLocation, gbConstraints);
      ++gbConstraints.gridy;
      jpEverything.add(jpStartupTabChoice, gbConstraints);
      ++gbConstraints.gridy;
      jpEverything.add(jpMyStuffPrefs, gbConstraints);
      ++gbConstraints.gridy;
      c.insets = new Insets(10, 0, 0, 0);
      jpEverything.add(jpButtons, gbConstraints);
      BorderLayout layout = new BorderLayout();
      this.setLayout(layout);
      this.add(jpEverything, "North");
      if (MyExperimentClient.baseChangedSinceLastStart) {
         JPanel jpInfo = new JPanel();
         jpInfo.setLayout(new BoxLayout(jpInfo, 1));
         String info = "<html>Your myExperiment base url has been modified since Taverna was started;<br>this change will not take effect until you restart Taverna.</html>";
         jpInfo.add(new JLabel(info, WorkbenchIcons.leafIcon, 2));
         this.add(jpInfo, "South");
      }

   }

   private void initialiseData() {
      this.tfMyExperimentURL.setText(this.myExperimentClient.getSettings().getProperty("my_experiment_base_url"));
      this.cbDefaultNotLoggedInTab.setSelectedIndex(Integer.parseInt(this.myExperimentClient.getSettings().getProperty("default_tab_for_anonymous_users")));
      this.cbDefaultLoggedInTab.setSelectedIndex(Integer.parseInt(this.myExperimentClient.getSettings().getProperty("default_tab_for_logged_in_users")));
      this.cbMyStuffWorkflows.setSelected(Boolean.parseBoolean(this.myExperimentClient.getSettings().getProperty("show_workflows_in_my_stuff")));
      this.cbMyStuffFiles.setSelected(Boolean.parseBoolean(this.myExperimentClient.getSettings().getProperty("show_files_in_my_stuff")));
      this.cbMyStuffPacks.setSelected(Boolean.parseBoolean(this.myExperimentClient.getSettings().getProperty("show_packs_in_my_stuff")));
   }

   public void actionPerformed(ActionEvent e) {
      if (e.getSource().equals(this.bApply)) {
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

         this.myExperimentClient.getSettings().put("my_experiment_base_url", strNewMyExperimentURL);
         this.myExperimentClient.getSettings().put("default_tab_for_anonymous_users", (new Integer(this.cbDefaultNotLoggedInTab.getSelectedIndex())).toString());
         this.myExperimentClient.getSettings().put("default_tab_for_logged_in_users", (new Integer(this.cbDefaultLoggedInTab.getSelectedIndex())).toString());
         this.myExperimentClient.getSettings().put("show_workflows_in_my_stuff", (new Boolean(this.cbMyStuffWorkflows.isSelected())).toString());
         this.myExperimentClient.getSettings().put("show_files_in_my_stuff", (new Boolean(this.cbMyStuffFiles.isSelected())).toString());
         this.myExperimentClient.getSettings().put("show_packs_in_my_stuff", (new Boolean(this.cbMyStuffPacks.isSelected())).toString());
         if (MyExperimentClient.baseChangedSinceLastStart || !strNewMyExperimentURL.equals(this.myExperimentClient.getBaseURL())) {
            this.myExperimentClient.getSettings().put("auto_login", (new Boolean(false)).toString());
            JOptionPane.showMessageDialog((Component)null, "You have selected a new Base URL for myExperiment.\nYour new setting has been saved, but will not take\neffect until you restart Taverna.\n\nThe auto-login feature has been disabled for you to\ncheck the login details at the next launch.", "myExperiment Plugin - Info", 1);
            MyExperimentClient.baseChangedSinceLastStart = true;
         }

         this.myExperimentClient.storeHistoryAndSettings();
      } else if (e.getSource().equals(this.bHelp)) {
         Helper.showHelp(this);
      } else if (e.getSource().equals(this.bReset)) {
         this.initialiseData();
      }

   }
}
