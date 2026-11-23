package org.apache.taverna.ui.perspectives.myexperiment;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import org.apache.log4j.Logger;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.ui.perspectives.myexperiment.model.License;
import org.apache.taverna.ui.perspectives.myexperiment.model.MyExperimentClient;
import org.apache.taverna.ui.perspectives.myexperiment.model.Resource;
import org.apache.taverna.ui.perspectives.myexperiment.model.ServerResponse;
import org.apache.taverna.ui.perspectives.myexperiment.model.Util;
import org.apache.taverna.ui.perspectives.myexperiment.model.Workflow;
import org.apache.taverna.workbench.file.FileManager;
import org.apache.taverna.workbench.file.impl.actions.SaveWorkflowAsAction;
import org.apache.taverna.workbench.helper.HelpEnabledDialog;
import org.apache.taverna.workbench.icons.WorkbenchIcons;

public class UploadWorkflowDialog extends HelpEnabledDialog implements ActionListener, CaretListener, ComponentListener, KeyListener, FocusListener {
   private final MainComponent pluginMainComponent;
   private final MyExperimentClient myExperimentClient;
   private final Logger logger;
   private JTextArea taDescription;
   private JTextField tfTitle;
   private JButton bUpload;
   private JButton bCancel;
   private JLabel lStatusMessage;
   private JComboBox jcbLicences;
   private JComboBox jcbSharingPermissions;
   private String licence;
   private String sharing;
   private File localWorkflowFile;
   private Resource updateResource;
   private File uploadFile;
   private String strDescription;
   private String strTitle;
   private boolean bUploadingSuccessful;
   private int gridYPositionForStatusLabel;
   private JRadioButton rbSelectLocalFile;
   private JRadioButton rbSelectOpenWorkflow;
   private JButton bSelectFile;
   private JComboBox jcbOpenWorkflows;
   private final JLabel selectedFileLabel;
   private boolean uploadWorkflowFromLocalFile;
   JFileChooser jfsSelectFile;
   private boolean userRequestedWorkflowUpload;
   private final FileManager fileManager;

   public UploadWorkflowDialog(JFrame parent, boolean doUpload, FileManager fileManager) {
      super(parent, "Upload workflow to myExperiment", true);
      this.pluginMainComponent = MainComponent.MAIN_COMPONENT;
      this.myExperimentClient = MainComponent.MY_EXPERIMENT_CLIENT;
      this.logger = MainComponent.LOGGER;
      this.strDescription = null;
      this.strTitle = null;
      this.bUploadingSuccessful = false;
      this.selectedFileLabel = new JLabel("no wokflow file selected");
      this.jfsSelectFile = new JFileChooser();
      this.fileManager = fileManager;
      this.initVarsAndUI(doUpload, (Resource)null);
   }

   public UploadWorkflowDialog(JFrame parent, boolean doUpload, Resource resource, FileManager fileManager) {
      super(parent, doUpload ? "Upload new workflow version" : "Update workflow information", true);
      this.pluginMainComponent = MainComponent.MAIN_COMPONENT;
      this.myExperimentClient = MainComponent.MY_EXPERIMENT_CLIENT;
      this.logger = MainComponent.LOGGER;
      this.strDescription = null;
      this.strTitle = null;
      this.bUploadingSuccessful = false;
      this.selectedFileLabel = new JLabel("no wokflow file selected");
      this.jfsSelectFile = new JFileChooser();
      this.fileManager = fileManager;
      this.initVarsAndUI(doUpload, resource);
   }

   private void initVarsAndUI(boolean doUpload, Resource resource) {
      this.updateResource = resource;
      this.userRequestedWorkflowUpload = doUpload;
      this.setDefaultCloseOperation(2);
      this.initialiseUI();
      this.setMinimumSize(new Dimension(525, 375));
   }

   private JPanel createSelectSource() {
      ButtonGroup radioButtons = new ButtonGroup();
      this.rbSelectOpenWorkflow = new JRadioButton("Already Opened Workflow");
      this.rbSelectOpenWorkflow.addFocusListener(this);
      this.rbSelectLocalFile = new JRadioButton("Select Local File");
      this.rbSelectLocalFile.addFocusListener(this);
      radioButtons.add(this.rbSelectOpenWorkflow);
      this.rbSelectOpenWorkflow.setSelected(true);
      radioButtons.add(this.rbSelectLocalFile);
      JPanel source = new JPanel(new GridBagLayout());
      source.setBorder(BorderFactory.createTitledBorder("Workflow source"));
      GridBagConstraints c = new GridBagConstraints();
      c.anchor = 18;
      c.gridy = 0;
      c.gridx = 0;
      c.gridwidth = 1;
      c.weightx = (double)1.0F;
      c.insets = new Insets(3, 0, 3, 0);
      c.fill = 1;
      ++c.gridy;
      source.add(this.rbSelectOpenWorkflow, c);
      c.gridx = 1;
      c.gridwidth = 2;
      this.createDropdown();
      source.add(this.jcbOpenWorkflows, c);
      c.gridwidth = 1;
      ++c.gridy;
      c.gridx = 0;
      source.add(this.rbSelectLocalFile, c);
      c.gridx = 1;
      source.add(this.selectedFileLabel, c);
      this.bSelectFile = new JButton(WorkbenchIcons.openIcon);
      this.bSelectFile.addActionListener(this);
      this.bSelectFile.setToolTipText("Select the file you would like to upload to myExperiment");
      c.gridx = 2;
      source.add(this.bSelectFile, c);
      return source;
   }

   private void createDropdown() {
      List<WorkflowBundleSelection> openDataflows = new ArrayList();
      int currentlyOpenedIndex = 0;
      boolean foundIndex = false;

      for(WorkflowBundle df : this.fileManager.getOpenDataflows()) {
         Object source = this.fileManager.getDataflowSource(df);
         String name = "";
         boolean getLocalName = source instanceof InputStream;
         if (source != null) {
            name = getLocalName ? df.getName() : source.toString();
         }

         if (df.equals(this.fileManager.getCurrentDataflow())) {
            name = "<html><body>" + name + " -  <i>(current)</i></body></html>";
            foundIndex = true;
         }

         openDataflows.add(new WorkflowBundleSelection(df, name));
         if (!foundIndex) {
            ++currentlyOpenedIndex;
         }
      }

      this.jcbOpenWorkflows = new JComboBox(openDataflows.toArray());
      this.jcbOpenWorkflows.setSelectedIndex(currentlyOpenedIndex);
   }

   private JPanel createMetadataPanel() {
      Insets fieldInset = new Insets(0, 5, 4, 5);
      Insets labelInset = new Insets(3, 5, 4, 5);
      GridBagConstraints c = new GridBagConstraints();
      c.gridx = 0;
      c.weightx = (double)1.0F;
      c.gridy = 0;
      c.anchor = 17;
      c.gridwidth = 2;
      c.fill = 2;
      JPanel metaPanel = new JPanel(new GridBagLayout());
      metaPanel.setBorder(BorderFactory.createTitledBorder("Workflow information"));
      JLabel lTitle = new JLabel("Workflow title:");
      c.insets = labelInset;
      ++c.gridy;
      metaPanel.add(lTitle, c);
      this.tfTitle = new JTextField();
      if (this.updateResource != null) {
         this.tfTitle.setText(this.updateResource.getTitle());
      }

      ++c.gridy;
      c.insets = fieldInset;
      metaPanel.add(this.tfTitle, c);
      JLabel lDescription = new JLabel("Workflow description:");
      ++c.gridy;
      c.insets = labelInset;
      metaPanel.add(lDescription, c);
      this.taDescription = new JTextArea(5, 35);
      this.taDescription.setLineWrap(true);
      this.taDescription.setWrapStyleWord(true);
      if (this.updateResource != null) {
         this.taDescription.setText(this.updateResource.getDescription());
      }

      JScrollPane spDescription = new JScrollPane(this.taDescription);
      ++c.gridy;
      c.insets = fieldInset;
      metaPanel.add(spDescription, c);
      String[] licenseText = new String[License.SUPPORTED_TYPES.length];

      for(int x = 0; x < License.SUPPORTED_TYPES.length; ++x) {
         licenseText[x] = License.getInstance(License.SUPPORTED_TYPES[x]).getText();
      }

      this.jcbLicences = new JComboBox(licenseText);
      String defaultLicenseText = License.getInstance(License.DEFAULT_LICENSE).getText();
      this.jcbLicences.setSelectedItem(defaultLicenseText);
      if (this.updateResource != null) {
         Workflow wf = (Workflow)this.updateResource;
         String wfText = wf.getLicense().getText();

         for(int x = 0; x < licenseText.length; ++x) {
            if (wfText.equals(licenseText[x])) {
               this.jcbLicences.setSelectedIndex(x);
               break;
            }
         }
      }

      this.jcbLicences.addActionListener(this);
      this.jcbLicences.setEditable(false);
      JLabel lLicense = new JLabel("Please select the licence to apply:");
      ++c.gridy;
      c.insets = labelInset;
      metaPanel.add(lLicense, c);
      ++c.gridy;
      c.insets = fieldInset;
      metaPanel.add(this.jcbLicences, c);
      String[] permissions = new String[]{"This workflow is private.", "Anyone can view, but noone can download.", "Anyone can view, and anyone can download"};
      this.jcbSharingPermissions = new JComboBox(permissions);
      this.jcbSharingPermissions.addActionListener(this);
      this.jcbSharingPermissions.setEditable(false);
      JLabel jSharing = new JLabel("Please select your sharing permissions:");
      ++c.gridy;
      c.insets = labelInset;
      metaPanel.add(jSharing, c);
      ++c.gridy;
      c.insets = fieldInset;
      metaPanel.add(this.jcbSharingPermissions, c);
      return metaPanel;
   }

   private void initialiseUI() {
      Container contentPane = this.getContentPane();
      Insets fieldInset = new Insets(3, 5, 3, 5);
      contentPane.setLayout(new GridBagLayout());
      GridBagConstraints c = new GridBagConstraints();
      c.gridx = 0;
      c.gridy = 0;
      c.anchor = 18;
      c.gridwidth = 2;
      c.fill = 2;
      if (this.userRequestedWorkflowUpload) {
         c.insets = fieldInset;
         contentPane.add(this.createSelectSource(), c);
         ++c.gridy;
      }

      contentPane.add(this.createMetadataPanel(), c);
      this.bUpload = new JButton(this.userRequestedWorkflowUpload ? "Upload Workflow" : "Update Workflow");
      this.bUpload.setDefaultCapable(true);
      this.getRootPane().setDefaultButton(this.bUpload);
      this.bUpload.addActionListener(this);
      this.bUpload.addKeyListener(this);
      ++c.gridy;
      c.anchor = 13;
      c.gridwidth = 1;
      c.fill = 0;
      c.weightx = (double)0.5F;
      c.insets = new Insets(10, 5, 10, 5);
      contentPane.add(this.bUpload, c);
      this.bCancel = new JButton("Cancel");
      this.bCancel.setPreferredSize(this.bUpload.getPreferredSize());
      this.bCancel.addActionListener(this);
      c.gridx = 1;
      c.anchor = 17;
      c.weightx = (double)0.5F;
      contentPane.add(this.bCancel, c);
      this.pack();
      this.addComponentListener(this);
      this.gridYPositionForStatusLabel = c.gridy;
   }

   public boolean launchUploadDialogAndPostIfRequired() {
      this.setVisible(true);
      return this.bUploadingSuccessful;
   }

   private File performSourceCheck() {
      if (!this.rbSelectLocalFile.isSelected() && !this.rbSelectOpenWorkflow.isSelected()) {
         JOptionPane.showConfirmDialog(this, "You have not selected a source for you workflow.\nPlease select a source and try again.", "Select Workflow Source", -1, 0);
         return null;
      } else if (this.rbSelectOpenWorkflow.isSelected()) {
         WorkflowBundle dataflowToUpload = ((WorkflowBundleSelection)this.jcbOpenWorkflows.getSelectedItem()).getWorkflowBundle();
         SaveWorkflowAsAction saveAction = new SaveWorkflowAsAction(this.fileManager);
         boolean skipPrompt = false;
         if (this.fileManager.isDataflowChanged(dataflowToUpload)) {
            JOptionPane.showConfirmDialog(this, "The workflow you are trying to upload has\nchanged since the last time it was saved.\n\nPlease save your file to proceed...", "Save Workflow", -1, 1);
            saveAction.saveDataflow(this, dataflowToUpload);
            skipPrompt = true;
         }

         File dataflowFile = (File)this.fileManager.getDataflowSource(dataflowToUpload);
         if (dataflowFile == null && !skipPrompt) {
            JOptionPane.showConfirmDialog(this, "You cannot upload an empty workflow.\nPlease select a different workflow before\nyou attempt the upload again.", "Upload Error", -1, 0);
            return null;
         } else {
            return dataflowFile;
         }
      } else if (this.localWorkflowFile == null) {
         JOptionPane.showConfirmDialog(this, "You have not selected a file to upload.\nPlease select a file and try again.", "Select Workflow Source", -1, 0);
         return null;
      } else {
         return this.localWorkflowFile;
      }
   }

   private void getMetadata() {
      switch (this.jcbSharingPermissions.getSelectedIndex()) {
         case 0:
            this.sharing = "private";
            break;
         case 1:
            this.sharing = "view";
            break;
         case 2:
            this.sharing = "download";
      }

      this.licence = License.SUPPORTED_TYPES[this.jcbLicences.getSelectedIndex()];
      this.strTitle = this.tfTitle.getText();
      this.strTitle = this.strTitle.trim();
      this.strDescription = this.taDescription.getText();
      this.strDescription = this.strDescription.trim();
   }

   public void actionPerformed(ActionEvent e) {
      if (e.getSource().equals(this.bUpload)) {
         if (this.userRequestedWorkflowUpload) {
            this.uploadFile = this.performSourceCheck();
            if (this.uploadFile == null) {
               return;
            }
         }

         this.getMetadata();
         boolean proceedWithUpload = false;
         if (this.strDescription.length() == 0 && this.strTitle.length() == 0) {
            String strInfo = "The workflow 'title' field and the 'description' field\n(or both) are empty.  Any metadata found within the\nworkflow will be used instead.  Do you wish to proceed?";
            int confirm = JOptionPane.showConfirmDialog(this, strInfo, "Empty fields", 0, 1);
            if (confirm == 0) {
               proceedWithUpload = true;
            }
         } else {
            proceedWithUpload = true;
         }

         if (proceedWithUpload) {
            final JRootPane rootPane = this.getRootPane();
            final Container contentPane = this.getContentPane();
            contentPane.remove(this.bUpload);
            contentPane.remove(this.bCancel);
            if (this.lStatusMessage != null) {
               contentPane.remove(this.lStatusMessage);
            }

            this.taDescription.setEditable(false);
            final GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = this.gridYPositionForStatusLabel;
            c.gridwidth = 2;
            c.anchor = 10;
            c.fill = 0;
            c.insets = new Insets(10, 5, 10, 5);
            this.lStatusMessage = new JLabel((this.updateResource == null ? "Uploading" : "Updating") + " your workflow...", new ImageIcon(MyExperimentPerspective.getLocalResourceURL("spinner")), 0);
            contentPane.add(this.lStatusMessage, c);
            this.setDefaultCloseOperation(0);
            this.pack();
            this.validate();
            this.repaint();
            (new Thread("Posting workflow") {
               boolean formatRecognized = false;

               public void run() {
                  String workflowFileContent = "";
                  if (UploadWorkflowDialog.this.userRequestedWorkflowUpload && UploadWorkflowDialog.this.uploadFile != null) {
                     try {
                        BufferedReader reader = new BufferedReader(new FileReader(UploadWorkflowDialog.this.uploadFile));
                        String scuflSchemaDef = "xmlns:s=\"http://org.embl.ebi.escience/xscufl/0.1alpha\"";

                        String line;
                        for(String t2flowSchemaDef = "xmlns=\"http://taverna.sf.net/2008/xml/t2flow\""; (line = reader.readLine()) != null; workflowFileContent = workflowFileContent + line + "\n") {
                           if (!this.formatRecognized && (line.contains(scuflSchemaDef) || line.contains(t2flowSchemaDef))) {
                              this.formatRecognized = true;
                           }
                        }
                     } catch (Exception e) {
                        UploadWorkflowDialog.this.lStatusMessage = new JLabel("Error occurred:" + e.getMessage(), new ImageIcon(MyExperimentPerspective.getLocalResourceURL("failure_icon")), 2);
                        UploadWorkflowDialog.this.logger.error(e.getCause() + "\n" + e.getMessage());
                     }
                  }

                  if ((!UploadWorkflowDialog.this.userRequestedWorkflowUpload || !this.formatRecognized) && UploadWorkflowDialog.this.userRequestedWorkflowUpload) {
                     UploadWorkflowDialog.this.bUploadingSuccessful = false;
                     ServerResponse response = null;
                  } else {
                     ServerResponse response;
                     if (UploadWorkflowDialog.this.updateResource == null) {
                        response = UploadWorkflowDialog.this.myExperimentClient.postWorkflow(workflowFileContent, Util.stripAllHTML(UploadWorkflowDialog.this.strTitle), Util.stripAllHTML(UploadWorkflowDialog.this.strDescription), UploadWorkflowDialog.this.licence, UploadWorkflowDialog.this.sharing);
                     } else {
                        response = UploadWorkflowDialog.this.myExperimentClient.updateWorkflowVersionOrMetadata(UploadWorkflowDialog.this.updateResource, workflowFileContent, Util.stripAllHTML(UploadWorkflowDialog.this.strTitle), Util.stripAllHTML(UploadWorkflowDialog.this.strDescription), UploadWorkflowDialog.this.licence, UploadWorkflowDialog.this.sharing);
                     }

                     UploadWorkflowDialog.this.bUploadingSuccessful = response.getResponseCode() == 200;
                  }

                  SwingUtilities.invokeLater(new Runnable() {
                     public void run() {
                        if (UploadWorkflowDialog.this.bUploadingSuccessful) {
                           UploadWorkflowDialog.this.setDefaultCloseOperation(2);
                           UploadWorkflowDialog.this.tfTitle.setEnabled(false);
                           UploadWorkflowDialog.this.taDescription.setEnabled(false);
                           UploadWorkflowDialog.this.jcbLicences.setEnabled(false);
                           UploadWorkflowDialog.this.jcbSharingPermissions.setEnabled(false);
                           if (UploadWorkflowDialog.this.userRequestedWorkflowUpload) {
                              UploadWorkflowDialog.this.rbSelectOpenWorkflow.setEnabled(false);
                              UploadWorkflowDialog.this.rbSelectLocalFile.setEnabled(false);
                              UploadWorkflowDialog.this.selectedFileLabel.setEnabled(false);
                              UploadWorkflowDialog.this.bSelectFile.setEnabled(false);
                              UploadWorkflowDialog.this.jcbOpenWorkflows.setEnabled(false);
                           }

                           contentPane.remove(UploadWorkflowDialog.this.lStatusMessage);
                           c.insets = new Insets(10, 5, 5, 5);
                           UploadWorkflowDialog.this.lStatusMessage = new JLabel("Your workflow was successfully " + (UploadWorkflowDialog.this.updateResource == null ? "uploaded." : "updated."), new ImageIcon(MyExperimentPerspective.getLocalResourceURL("success_icon")), 2);
                           contentPane.add(UploadWorkflowDialog.this.lStatusMessage, c);
                           UploadWorkflowDialog.this.bCancel.setText("OK");
                           UploadWorkflowDialog.this.bCancel.setDefaultCapable(true);
                           rootPane.setDefaultButton(UploadWorkflowDialog.this.bCancel);
                           c.insets = new Insets(5, 5, 10, 5);
                           ++c.gridy;
                           contentPane.add(UploadWorkflowDialog.this.bCancel, c);
                           UploadWorkflowDialog.this.pack();
                           UploadWorkflowDialog.this.bCancel.requestFocusInWindow();
                           MainComponent.MAIN_COMPONENT.getHistoryBrowser().getUploadedItemsHistoryList().remove(UploadWorkflowDialog.this.updateResource);
                           MainComponent.MAIN_COMPONENT.getHistoryBrowser().getUploadedItemsHistoryList().add(UploadWorkflowDialog.this.updateResource);
                           if (MainComponent.MAIN_COMPONENT.getHistoryBrowser().getUploadedItemsHistoryList().size() > 50) {
                              MainComponent.MAIN_COMPONENT.getHistoryBrowser().getUploadedItemsHistoryList().remove(0);
                           }

                           if (MainComponent.MAIN_COMPONENT.getHistoryBrowser() != null) {
                              MainComponent.MAIN_COMPONENT.getHistoryBrowser().refreshHistoryBox(4);
                           }
                        } else {
                           UploadWorkflowDialog.this.setDefaultCloseOperation(2);
                           UploadWorkflowDialog.this.taDescription.setEditable(true);
                           contentPane.remove(UploadWorkflowDialog.this.lStatusMessage);
                           c.insets = new Insets(10, 5, 5, 5);
                           String msg;
                           if (!formatRecognized) {
                              msg = "Error occured: Invalid Taverna workflow.";
                           } else {
                              msg = "An error occured while processing your request.";
                           }

                           UploadWorkflowDialog.this.lStatusMessage = new JLabel(msg, new ImageIcon(MyExperimentPerspective.getLocalResourceURL("failure_icon")), 2);
                           contentPane.add(UploadWorkflowDialog.this.lStatusMessage, c);
                           UploadWorkflowDialog.this.bUpload.setText("Try again");
                           UploadWorkflowDialog.this.bUpload.setToolTipText("Please review your workflow or myExperiment base URL");
                           c.anchor = 13;
                           c.insets = new Insets(5, 5, 10, 5);
                           c.gridwidth = 1;
                           c.weightx = (double)0.5F;
                           c.gridx = 0;
                           ++c.gridy;
                           contentPane.add(UploadWorkflowDialog.this.bUpload, c);
                           rootPane.setDefaultButton(UploadWorkflowDialog.this.bUpload);
                           c.anchor = 17;
                           c.gridx = 1;
                           UploadWorkflowDialog.this.bCancel.setPreferredSize(UploadWorkflowDialog.this.bUpload.getPreferredSize());
                           contentPane.add(UploadWorkflowDialog.this.bCancel, c);
                           UploadWorkflowDialog.this.pack();
                           UploadWorkflowDialog.this.validate();
                           UploadWorkflowDialog.this.repaint();
                        }

                     }
                  });
               }
            }).start();
         }
      } else if (e.getSource().equals(this.bCancel)) {
         if (!this.bUploadingSuccessful) {
            this.strDescription = null;
            this.tfTitle = null;
         }

         this.dispose();
      } else if (e.getSource().equals(this.bSelectFile) && this.jfsSelectFile.showOpenDialog(this) == 0) {
         this.localWorkflowFile = this.jfsSelectFile.getSelectedFile();
         if (this.localWorkflowFile != null) {
            this.selectedFileLabel.setText(this.localWorkflowFile.getAbsolutePath());
            this.selectedFileLabel.setEnabled(true);
         }

         this.pack();
      }

   }

   public void keyPressed(KeyEvent e) {
      if ((e.getSource().equals(this.tfTitle) || e.getSource().equals(this.taDescription)) && e.getKeyCode() == 9) {
         if ((e.getModifiersEx() & 64) == 64) {
            ((Component)e.getSource()).transferFocusBackward();
         } else {
            ((Component)e.getSource()).transferFocus();
         }

         e.consume();
      }

   }

   public void focusGained(FocusEvent e) {
      if (e.getSource().equals(this.rbSelectLocalFile)) {
         this.uploadWorkflowFromLocalFile = true;
         this.bSelectFile.setEnabled(this.uploadWorkflowFromLocalFile);
         this.jcbOpenWorkflows.setEnabled(!this.uploadWorkflowFromLocalFile);
         if (this.localWorkflowFile != null) {
            this.selectedFileLabel.setEnabled(this.uploadWorkflowFromLocalFile);
            this.selectedFileLabel.setText(this.localWorkflowFile.getAbsolutePath());
            this.pack();
         } else {
            this.selectedFileLabel.setEnabled(!this.uploadWorkflowFromLocalFile);
         }
      } else if (e.getSource().equals(this.rbSelectOpenWorkflow)) {
         this.uploadWorkflowFromLocalFile = false;
         this.selectedFileLabel.setEnabled(this.uploadWorkflowFromLocalFile);
         this.bSelectFile.setEnabled(this.uploadWorkflowFromLocalFile);
         this.jcbOpenWorkflows.setEnabled(!this.uploadWorkflowFromLocalFile);
      }

   }

   public void componentShown(ComponentEvent e) {
      if (this.updateResource == null) {
         Util.centerComponentWithinAnother(this.pluginMainComponent, this);
      } else {
         Util.centerComponentWithinAnother(this.pluginMainComponent.getPreviewBrowser(), this);
      }

   }

   public void focusLost(FocusEvent e) {
   }

   public void caretUpdate(CaretEvent e) {
   }

   public void componentHidden(ComponentEvent e) {
   }

   public void componentMoved(ComponentEvent e) {
   }

   public void keyReleased(KeyEvent e) {
   }

   public void keyTyped(KeyEvent e) {
   }

   public void componentResized(ComponentEvent e) {
   }

   private class WorkflowBundleSelection {
      private final WorkflowBundle workflowBundle;
      private final String name;

      public WorkflowBundleSelection(WorkflowBundle df, String name) {
         this.workflowBundle = df;
         this.name = name;
      }

      public WorkflowBundle getWorkflowBundle() {
         return this.workflowBundle;
      }

      public String getName() {
         return this.name;
      }

      public String toString() {
         return this.name;
      }
   }
}
