package org.apache.taverna.ui.perspectives.myexperiment;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import org.apache.log4j.Logger;
import org.apache.taverna.ui.perspectives.myexperiment.model.MyExperimentClient;
import org.apache.taverna.workbench.icons.WorkbenchIcons;

public class SearchOptionsPanel extends JPanel implements ItemListener, KeyListener {
   protected static final int SEARCH_RESULT_LIMIT_MIN = 1;
   protected static final int SEARCH_RESULT_LIMIT_INIT = 20;
   protected static final int SEARCH_RESULT_LIMIT_MAX = 100;
   private final MainComponent pluginMainComponent;
   private final MyExperimentClient myExperimentClient;
   private final Logger logger;
   private final ActionListener clickHandler;
   protected JButton bSearch;
   private JTextField tfSearchQuery;
   private JCheckBox cbSearchAllTypes;
   private JCheckBox cbWorkflows;
   private JCheckBox cbFiles;
   private JCheckBox cbPacks;
   private JCheckBox cbUsers;
   private JCheckBox cbGroups;
   protected JSpinner jsResultLimit;
   protected JTextField tfResultLimitTextField;
   ArrayList<JCheckBox> alDataTypeCheckboxes;

   public SearchOptionsPanel(ActionListener actionListener, MainComponent component, MyExperimentClient client, Logger logger) {
      this.pluginMainComponent = component;
      this.myExperimentClient = client;
      this.logger = logger;
      this.clickHandler = actionListener;
      this.initialiseUI();
      this.alDataTypeCheckboxes = new ArrayList(Arrays.asList(this.cbWorkflows, this.cbFiles, this.cbPacks, this.cbUsers, this.cbGroups));
   }

   private void initialiseUI() {
      this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " Search Settings "), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
      this.setLayout(new GridBagLayout());
      GridBagConstraints c = new GridBagConstraints();
      c.gridx = 0;
      c.gridy = 0;
      c.anchor = 17;
      this.add(new JLabel("Query"), c);
      c.gridx = 0;
      c.gridy = 1;
      c.gridwidth = 4;
      c.fill = 2;
      c.weightx = (double)1.0F;
      this.tfSearchQuery = new JTextField();
      this.tfSearchQuery.setToolTipText("<html>Tips for creating search queries:<br>1) Use wildcards to make more flexible queries. Asterisk (<b>*</b>) matches any zero or more<br>&nbsp;&nbsp;&nbsp;&nbsp;characters (e.g. <b><i>Seq*</i></b> would match <b><i>Sequence</i></b>), question mark (<b>?</b>) matches any single<br>&nbsp;&nbsp;&nbsp;&nbsp;character (e.g. <b><i>Tave?na</i></b> would match <b><i>Taverna</i></b>).<br>2) Enclose the <b><i>\"search query\"</i></b> in double quotes to make exact phrase matching, otherwise<br>&nbsp;&nbsp;&nbsp;&nbsp;items that contain any (or all) words in the <b><i>search query</i></b> will be found.</html>");
      this.tfSearchQuery.addKeyListener(this);
      this.add(this.tfSearchQuery, c);
      c.gridx = 4;
      c.gridy = 1;
      c.gridwidth = 1;
      c.fill = 0;
      c.weightx = (double)0.0F;
      c.insets = new Insets(0, 5, 0, 0);
      this.bSearch = new JButton("Search", WorkbenchIcons.searchIcon);
      this.bSearch.addActionListener(this.clickHandler);
      this.bSearch.addKeyListener(this);
      this.add(this.bSearch, c);
      c.gridx = 0;
      c.gridy = 2;
      c.insets = new Insets(10, 0, 3, 0);
      this.add(new JLabel("Search for..."), c);
      c.gridx = 0;
      c.gridy = 3;
      c.gridwidth = 2;
      c.insets = new Insets(0, 0, 0, 0);
      this.cbSearchAllTypes = new JCheckBox("all resource types", true);
      this.cbSearchAllTypes.addItemListener(this);
      this.cbSearchAllTypes.addKeyListener(this);
      this.add(this.cbSearchAllTypes, c);
      c.gridx = 0;
      c.gridy = 4;
      c.gridwidth = 1;
      c.ipady = 0;
      this.cbWorkflows = new JCheckBox("workflows", true);
      this.cbWorkflows.addItemListener(this);
      this.cbWorkflows.addKeyListener(this);
      this.add(this.cbWorkflows, c);
      c.gridx = 0;
      c.gridy = 5;
      this.cbFiles = new JCheckBox("files", true);
      this.cbFiles.addItemListener(this);
      this.cbFiles.addKeyListener(this);
      this.add(this.cbFiles, c);
      c.gridx = 0;
      c.gridy = 6;
      this.cbPacks = new JCheckBox("packs", true);
      this.cbPacks.addItemListener(this);
      this.cbPacks.addKeyListener(this);
      this.add(this.cbPacks, c);
      c.gridx = 1;
      c.gridy = 4;
      this.cbUsers = new JCheckBox("users", true);
      this.cbUsers.addItemListener(this);
      this.cbUsers.addKeyListener(this);
      this.add(this.cbUsers, c);
      c.gridx = 1;
      c.gridy = 5;
      this.cbGroups = new JCheckBox("groups", true);
      this.cbGroups.addItemListener(this);
      this.cbGroups.addKeyListener(this);
      this.add(this.cbGroups, c);
      c.gridx = 3;
      c.gridy = 2;
      c.insets = new Insets(10, 25, 3, 0);
      JLabel jlResultLimit = new JLabel("Result limit");
      this.add(jlResultLimit, c);
      c.gridx = 3;
      c.gridy = 3;
      c.insets = new Insets(0, 25, 0, 0);
      this.jsResultLimit = new JSpinner(new SpinnerNumberModel(20, 1, 100, 1));
      this.jsResultLimit.setPreferredSize(new Dimension(jlResultLimit.getPreferredSize().width, this.jsResultLimit.getPreferredSize().height));
      this.add(this.jsResultLimit, c);
      this.tfResultLimitTextField = ((JSpinner.DefaultEditor)this.jsResultLimit.getEditor()).getTextField();
      this.tfResultLimitTextField.addKeyListener(this);
   }

   public String getSearchQuery() {
      return this.tfSearchQuery.getText();
   }

   public void setSearchQuery(String strSearchQuery) {
      this.tfSearchQuery.setText(strSearchQuery);
   }

   public void focusSearchQueryField() {
      this.tfSearchQuery.selectAll();
      this.tfSearchQuery.requestFocusInWindow();
   }

   public void setSearchAllResourceTypes(boolean bSearchAllTypes) {
      this.cbSearchAllTypes.setSelected(bSearchAllTypes);
   }

   public boolean getSearchWorkflows() {
      return this.cbWorkflows.isSelected();
   }

   public void setSearchWorkflows(boolean bSearchWorkflows) {
      this.cbWorkflows.setSelected(bSearchWorkflows);
   }

   public boolean getSearchFiles() {
      return this.cbFiles.isSelected();
   }

   public void setSearchFiles(boolean bSearchFiles) {
      this.cbFiles.setSelected(bSearchFiles);
   }

   public boolean getSearchPacks() {
      return this.cbPacks.isSelected();
   }

   public void setSearchPacks(boolean bSearchPacks) {
      this.cbPacks.setSelected(bSearchPacks);
   }

   public boolean getSearchUsers() {
      return this.cbUsers.isSelected();
   }

   public void setSearchUsers(boolean bSearchUsers) {
      this.cbUsers.setSelected(bSearchUsers);
   }

   public boolean getSearchGroups() {
      return this.cbGroups.isSelected();
   }

   public void setSearchGroups(boolean bSearchGroups) {
      this.cbGroups.setSelected(bSearchGroups);
   }

   public int getResultCountLimit() {
      return Integer.parseInt(this.jsResultLimit.getValue().toString());
   }

   public void setResultCountLimit(int iLimit) {
      this.jsResultLimit.setValue(iLimit);
   }

   public void itemStateChanged(ItemEvent e) {
      if (e.getItemSelectable().equals(this.cbSearchAllTypes)) {
         for(JCheckBox cb : this.alDataTypeCheckboxes) {
            cb.removeItemListener(this);
            cb.setSelected(this.cbSearchAllTypes.isSelected());
            cb.addItemListener(this);
         }

         this.bSearch.setEnabled(this.cbSearchAllTypes.isSelected());
      } else if (this.alDataTypeCheckboxes.contains(e.getItemSelectable())) {
         int iSelectedCnt = 0;

         for(JCheckBox cb : this.alDataTypeCheckboxes) {
            if (cb.isSelected()) {
               ++iSelectedCnt;
            }
         }

         this.cbSearchAllTypes.removeItemListener(this);
         this.cbSearchAllTypes.setSelected(iSelectedCnt == this.alDataTypeCheckboxes.size());
         this.cbSearchAllTypes.addItemListener(this);
         this.bSearch.setEnabled(iSelectedCnt > 0);
      }

   }

   public void keyPressed(KeyEvent e) {
      if (e.getKeyCode() == 10 && this.bSearch.isEnabled() && (Arrays.asList(this.tfSearchQuery, this.bSearch, this.cbSearchAllTypes, this.tfResultLimitTextField).contains(e.getSource()) || this.alDataTypeCheckboxes.contains(e.getSource()))) {
         this.clickHandler.actionPerformed(new ActionEvent(this.bSearch, 0, ""));
      }

   }

   public void keyReleased(KeyEvent e) {
   }

   public void keyTyped(KeyEvent e) {
   }
}
