package org.apache.taverna.ui.perspectives.myexperiment;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.net.URI;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.html.StyleSheet;
import org.apache.log4j.Logger;
import org.apache.taverna.lang.ui.ShadedLabel;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.ui.menu.MenuManager;
import org.apache.taverna.ui.perspectives.myexperiment.model.MyExperimentClient;
import org.apache.taverna.ui.perspectives.myexperiment.model.Resource;
import org.apache.taverna.ui.perspectives.myexperiment.model.Workflow;
import org.apache.taverna.workbench.configuration.colour.ColourManager;
import org.apache.taverna.workbench.configuration.workbench.WorkbenchConfiguration;
import org.apache.taverna.workbench.edits.EditManager;
import org.apache.taverna.workbench.file.FileManager;
import org.apache.taverna.workbench.file.FileType;
import org.apache.taverna.workbench.file.exceptions.OpenException;
import org.apache.taverna.workbench.file.importworkflow.gui.ImportWorkflowWizard;
import org.apache.taverna.workbench.icons.WorkbenchIcons;
import org.apache.taverna.workbench.selection.SelectionManager;
import org.apache.taverna.workbench.ui.zaria.UIComponentSPI;

public final class MainComponent extends JPanel implements UIComponentSPI, ChangeListener {
   private final Logger logger = Logger.getLogger(MainComponent.class);
   private final StyleSheet css;
   private final ResourcePreviewFactory previewFactory;
   private final ResourcePreviewBrowser previewBrowser;
   private JTabbedPane tpMainTabs;
   private MyStuffTabContentPanel pMyStuffContainer;
   private ExampleWorkflowsPanel pExampleWorkflows;
   private TagBrowserTabContentPanel pTagBrowser;
   private SearchTabContentPanel pSearchTab;
   private HistoryBrowserTabContentPanel pHistoryBrowserTab;
   private PluginStatusBar pStatusBar;
   private PluginPreferencesDialog jdPreferences;
   public static MainComponent MAIN_COMPONENT;
   public static MyExperimentClient MY_EXPERIMENT_CLIENT;
   public static Logger LOGGER;
   private final EditManager editManager;
   private final FileManager fileManager;
   private final MyExperimentClient myExperimentClient;
   private final MenuManager menuManager;
   private final ColourManager colourManager;
   private final WorkbenchConfiguration workbenchConfiguration;
   private final SelectionManager selectionManager;

   public MainComponent(EditManager editManager, FileManager fileManager, MyExperimentClient myExperimentClient, MenuManager menuManager, ColourManager colourManager, WorkbenchConfiguration workbenchConfiguration, SelectionManager selectionManager) {
      this.editManager = editManager;
      this.fileManager = fileManager;
      this.myExperimentClient = myExperimentClient;
      this.menuManager = menuManager;
      this.colourManager = colourManager;
      this.workbenchConfiguration = workbenchConfiguration;
      this.selectionManager = selectionManager;
      MAIN_COMPONENT = this;
      MyExperimentClient y = this.myExperimentClient;
      MY_EXPERIMENT_CLIENT = y;
      Logger z = this.logger;
      LOGGER = z;
      this.previewFactory = new ResourcePreviewFactory(this, myExperimentClient, this.logger);
      this.previewBrowser = new ResourcePreviewBrowser(this, myExperimentClient, this.logger, fileManager);
      this.css = new StyleSheet();
      this.css.importStyleSheet(MyExperimentPerspective.getLocalResourceURL("css_stylesheet"));
      this.logger.debug("Stylesheet loaded: \n" + this.css.toString());
      if (myExperimentClient.getSettings().getProperty("default_tab_for_anonymous_users") == null) {
         myExperimentClient.getSettings().put("default_tab_for_anonymous_users", "3");
      }

      if (myExperimentClient.getSettings().getProperty("default_tab_for_logged_in_users") == null) {
         myExperimentClient.getSettings().put("default_tab_for_logged_in_users", "0");
      }

      this.initialisePerspectiveUI();
      new StyledHTMLEditorKit(this.css);
      (new Thread("Data initialisation for Taverna 2 - myExperiment plugin") {
         public void run() {
            MainComponent.this.initialiseData();
         }
      }).start();
   }

   public ImageIcon getIcon() {
      return WorkbenchIcons.databaseIcon;
   }

   public String getName() {
      return "myExperiment Perspective Main Component";
   }

   public void onDisplay() {
   }

   public void onDispose() {
   }

   public MyExperimentClient getMyExperimentClient() {
      return this.myExperimentClient;
   }

   public Logger getLogger() {
      return this.logger;
   }

   public StyleSheet getStyleSheet() {
      return this.css;
   }

   public PluginStatusBar getStatusBar() {
      return this.pStatusBar;
   }

   public PluginPreferencesDialog getPreferencesDialog() {
      return this.jdPreferences;
   }

   public ResourcePreviewFactory getPreviewFactory() {
      return this.previewFactory;
   }

   public ResourcePreviewBrowser getPreviewBrowser() {
      return this.previewBrowser;
   }

   public HistoryBrowserTabContentPanel getHistoryBrowser() {
      return this.pHistoryBrowserTab;
   }

   public JTabbedPane getMainTabs() {
      return this.tpMainTabs;
   }

   public MyStuffTabContentPanel getMyStuffTab() {
      return this.pMyStuffContainer;
   }

   public ExampleWorkflowsPanel getExampleWorkflowsTab() {
      return this.pExampleWorkflows;
   }

   public TagBrowserTabContentPanel getTagBrowserTab() {
      return this.pTagBrowser;
   }

   public SearchTabContentPanel getSearchTab() {
      return this.pSearchTab;
   }

   private void initialisePerspectiveUI() {
      new ShadedLabel("test", ShadedLabel.BLUE);
      this.pStatusBar = new PluginStatusBar(this, this.myExperimentClient, this.logger);
      this.pMyStuffContainer = new MyStuffTabContentPanel(this, this.myExperimentClient, this.logger, this.fileManager);
      this.pExampleWorkflows = new ExampleWorkflowsPanel(this, this.myExperimentClient, this.logger);
      this.pTagBrowser = new TagBrowserTabContentPanel(this, this.myExperimentClient, this.logger);
      this.pSearchTab = new SearchTabContentPanel(this, this.myExperimentClient, this.logger);
      this.pHistoryBrowserTab = new HistoryBrowserTabContentPanel(this, this.myExperimentClient, this.logger);
      this.tpMainTabs = new JTabbedPane();
      this.tpMainTabs.add("My Stuff", this.pMyStuffContainer);
      this.tpMainTabs.add("Starter Pack", this.pExampleWorkflows);
      this.tpMainTabs.add("Tag Browser", this.pTagBrowser);
      this.tpMainTabs.add("Search", this.pSearchTab);
      this.tpMainTabs.add("Local History", this.pHistoryBrowserTab);
      this.setLayout(new BorderLayout());
      this.add(this.tpMainTabs, "Center");
      this.add(this.pStatusBar, "South");
      this.tpMainTabs.addChangeListener(this);
      this.jdPreferences = new PluginPreferencesDialog(this.getPreviewBrowser(), this, this.myExperimentClient, this.logger);
   }

   private void initialiseData() {
      this.logger.debug("Initialising myExperiment Perspective data");
      Object oAutoLogin = this.myExperimentClient.getSettings().get("auto_login");
      if (oAutoLogin != null && oAutoLogin.equals("true")) {
         this.getStatusBar().setStatus(this.getMyStuffTab().getClass().getName(), "Performing autologin");
         this.myExperimentClient.doLogin();
         this.getStatusBar().setStatus(this.getMyStuffTab().getClass().getName(), "Autologin finished. Fetching user data");
      }

      this.pMyStuffContainer.createAndInitialiseInnerComponents();
      if (this.myExperimentClient.isLoggedIn()) {
         this.tpMainTabs.setSelectedIndex(Integer.parseInt(this.myExperimentClient.getSettings().getProperty("default_tab_for_logged_in_users")));
         this.pTagBrowser.setMyTagsShown(true);
      } else {
         this.tpMainTabs.setSelectedIndex(Integer.parseInt(this.myExperimentClient.getSettings().getProperty("default_tab_for_anonymous_users")));
      }

      this.pExampleWorkflows.refresh();
      this.pTagBrowser.refresh();
   }

   public void stateChanged(ChangeEvent e) {
      if (e.getSource().equals(this.tpMainTabs)) {
         this.getStatusBar().displayStatus(this.getMainTabs().getSelectedComponent().getClass().getName());
      }

   }

   public class PreviewResourceAction extends AbstractAction {
      private int iResourceType = 0;
      private String strResourceURI = "";

      public PreviewResourceAction(int iResourceType, String strResourceURI) {
         this.putValue("SmallIcon", WorkbenchIcons.zoomIcon);
         this.putValue("Name", "Preview");
         this.putValue("ShortDescription", "Preview this " + Resource.getResourceTypeName(iResourceType).toLowerCase() + " in the Preview Browser window");
         this.iResourceType = iResourceType;
         this.strResourceURI = strResourceURI;
      }

      public void actionPerformed(ActionEvent actionEvent) {
         MainComponent.this.getPreviewBrowser().preview("preview:" + this.iResourceType + ":" + this.strResourceURI);
      }
   }

   public class DownloadResourceAction extends AbstractAction {
      private Resource resource;

      public DownloadResourceAction(Resource resource) {
         this(resource, true);
      }

      public DownloadResourceAction(Resource resource, boolean bShowButtonLabel) {
         this.resource = null;
         this.resource = resource;
         String strResourceType = resource.getItemTypeName().toLowerCase();
         this.putValue("SmallIcon", WorkbenchIcons.saveIcon);
         if (bShowButtonLabel) {
            this.putValue("Name", "Download");
         }

         String strTooltip = "Downloading " + strResourceType + "s is currently not possible";
         boolean bDownloadAllowed = false;
         if (resource.isDownloadable()) {
            if (resource.isDownloadAllowed()) {
               strTooltip = "Download this " + strResourceType + " and store it locally";
               bDownloadAllowed = true;
            } else {
               strTooltip = "You don't have permissions to download this " + strResourceType;
            }
         }

         this.setEnabled(bDownloadAllowed);
         this.putValue("ShortDescription", strTooltip);
      }

      public void actionPerformed(ActionEvent actionEvent) {
         try {
            Desktop.getDesktop().browse(new URI(this.resource.getResource() + "/download"));
            MainComponent.this.getHistoryBrowser().getDownloadedItemsHistoryList().remove(this.resource);
            MainComponent.this.getHistoryBrowser().getDownloadedItemsHistoryList().add(this.resource);
            if (MainComponent.this.getHistoryBrowser().getDownloadedItemsHistoryList().size() > 50) {
               MainComponent.this.getHistoryBrowser().getDownloadedItemsHistoryList().remove(0);
            }

            if (MainComponent.this.getHistoryBrowser() != null) {
               MainComponent.this.getHistoryBrowser().refreshHistoryBox(1);
            }
         } catch (Exception ex) {
            MainComponent.this.logger.error("Failed while trying to open download URL in a standard browser; URL was: " + this.resource.getURI() + "\nException was: " + ex);
         }

      }
   }

   public class LoadResourceInTavernaAction extends AbstractAction {
      private final Resource resource;

      public LoadResourceInTavernaAction(Resource resource) {
         this(resource, true);
      }

      public LoadResourceInTavernaAction(Resource resource, boolean bShowButtonLabel) {
         this.resource = resource;
         String strResourceType = resource.getItemTypeName().toLowerCase();
         this.putValue("SmallIcon", WorkbenchIcons.openIcon);
         if (bShowButtonLabel) {
            this.putValue("Name", "Open");
         }

         boolean bLoadingAllowed = false;
         String strTooltip = "Loading " + strResourceType + "s into Taverna Workbench is currently not possible";
         if (resource.getItemType() == 10) {
            if (((Workflow)resource).isTavernaWorkflow()) {
               if (resource.isDownloadAllowed()) {
                  bLoadingAllowed = true;
                  strTooltip = "Download and load this workflow in Design mode of Taverna Workbench";
               } else {
                  strTooltip = "You don't have permissions to download this workflow, and thus to load into Taverna Workbench";
               }
            } else {
               strTooltip = "Loading workflow of unsupported type into Taverna Workbench is not possible.";
            }
         }

         this.setEnabled(bLoadingAllowed);
         this.putValue("ShortDescription", strTooltip);
      }

      public void actionPerformed(ActionEvent actionEvent) {
         if (MainComponent.this.getPreviewBrowser().isActive()) {
            MainComponent.this.getPreviewBrowser().toBack();
         }

         final String strCallerTabClassName = MainComponent.this.getMainTabs().getSelectedComponent().getClass().getName();
         MainComponent.this.getStatusBar().setStatus(strCallerTabClassName, "Downloading and opening workflow...");
         MainComponent.this.logger.debug("Downloading and opening workflow from URI: " + this.resource.getURI());
         (new Thread("Download and open workflow") {
            public void run() {
               try {
                  Workflow w = MainComponent.this.myExperimentClient.fetchWorkflowBinary(LoadResourceInTavernaAction.this.resource.getURI());
                  ByteArrayInputStream workflowDataInputStream = new ByteArrayInputStream(w.getContent());
                  FileType fileTypeType = (FileType)(w.isTaverna1Workflow() ? new ScuflFileType() : new T2FlowFileType());
                  MainComponent.this.fileManager.openDataflow(fileTypeType, workflowDataInputStream);
               } catch (Exception e) {
                  JOptionPane.showMessageDialog((Component)null, "An error has occurred while trying to load a workflow from myExperiment.\n\n" + e, "Error", 0);
                  MainComponent.this.logger.error("Failed to open connection to URL to download and open workflow, from myExperiment.", e);
               }

               MainComponent.this.getStatusBar().setStatus(strCallerTabClassName, (String)null);
               MainComponent.this.getHistoryBrowser().getOpenedItemsHistoryList().remove(LoadResourceInTavernaAction.this.resource);
               MainComponent.this.getHistoryBrowser().getOpenedItemsHistoryList().add(LoadResourceInTavernaAction.this.resource);
               if (MainComponent.this.getHistoryBrowser().getOpenedItemsHistoryList().size() > 50) {
                  MainComponent.this.getHistoryBrowser().getOpenedItemsHistoryList().remove(0);
               }

               if (MainComponent.this.getHistoryBrowser() != null) {
                  MainComponent.this.getHistoryBrowser().refreshHistoryBox(2);
               }

            }
         }).start();
      }
   }

   public class ImportIntoTavernaAction extends AbstractAction {
      private final Resource resource;
      private boolean importAsNesting;

      public ImportIntoTavernaAction(Resource r) {
         this.resource = r;
         String strResourceType = this.resource.getItemTypeName().toLowerCase();
         this.putValue("SmallIcon", WorkbenchIcons.importIcon);
         this.putValue("Name", "Import");
         boolean bLoadingAllowed = false;
         String strTooltip = "Loading " + strResourceType + "s into Taverna Workbench is currently not possible";
         if (this.resource.getItemType() == 10) {
            if (((Workflow)this.resource).isTavernaWorkflow()) {
               if (this.resource.isDownloadAllowed()) {
                  bLoadingAllowed = true;
                  strTooltip = "Import this workflow into one that is currently open in the Design mode of Taverna Workbench";
               } else {
                  strTooltip = "You don't have permissions to download this workflow, and thus to load into Taverna Workbench";
               }
            } else {
               strTooltip = "Loading workflow of unsupported type into Taverna Workbench is not possible.";
            }
         }

         this.setEnabled(bLoadingAllowed);
         this.putValue("ShortDescription", strTooltip);
      }

      public void actionPerformed(ActionEvent actionEvent) {
         if (MainComponent.this.getPreviewBrowser().isActive()) {
            MainComponent.this.getPreviewBrowser().toBack();
         }

         ImportWorkflowWizard importWorkflowDialog = new ImportWorkflowWizard(MainComponent.this.getPreviewBrowser(), MainComponent.this.editManager, MainComponent.this.fileManager, MainComponent.this.menuManager, MainComponent.this.colourManager, MainComponent.this.workbenchConfiguration, MainComponent.this.selectionManager);

         Workflow w;
         try {
            w = MainComponent.MY_EXPERIMENT_CLIENT.fetchWorkflowBinary(this.resource.getURI());
         } catch (Exception e) {
            JOptionPane.showMessageDialog((Component)null, "An error has occurred while trying to load a workflow from myExperiment.\n\n" + e, "Error", 0);
            MainComponent.LOGGER.error("Failed to open connection to URL to download and open workflow, from myExperiment.", e);
            return;
         }

         ByteArrayInputStream workflowDataInputStream = new ByteArrayInputStream(w.getContent());
         FileType fileTypeType = (FileType)(w.isTaverna1Workflow() ? new ScuflFileType() : new T2FlowFileType());

         WorkflowBundle toBeImported;
         try {
            toBeImported = MainComponent.this.fileManager.openDataflowSilently(fileTypeType, workflowDataInputStream).getDataflow();
         } catch (OpenException e) {
            JOptionPane.showMessageDialog((Component)null, "An error has occurred while trying to load a workflow from myExperiment.\n\n" + e, "Error", 0);
            MainComponent.LOGGER.error("Failed to open connection to URL to download and open workflow, from myExperiment.", e);
            return;
         }

         importWorkflowDialog.setCustomSourceDataflow(toBeImported, "From myExperiment: " + this.resource.getTitle());
         importWorkflowDialog.setSourceEnabled(false);
         importWorkflowDialog.setVisible(true);
         MainComponent.this.getHistoryBrowser().getOpenedItemsHistoryList().remove(this.resource);
         MainComponent.this.getHistoryBrowser().getOpenedItemsHistoryList().add(this.resource);
         if (MainComponent.this.getHistoryBrowser().getOpenedItemsHistoryList().size() > 50) {
            MainComponent.this.getHistoryBrowser().getOpenedItemsHistoryList().remove(0);
         }

         if (MainComponent.this.getHistoryBrowser() != null) {
            MainComponent.this.getHistoryBrowser().refreshHistoryBox(2);
         }

      }
   }

   public static class ScuflFileType extends FileType {
      public String getDescription() {
         return "Taverna 1 SCUFL workflow";
      }

      public String getExtension() {
         return "xml";
      }

      public String getMimeType() {
         return "application/vnd.taverna.scufl+xml";
      }
   }

   public static class T2FlowFileType extends FileType {
      public String getDescription() {
         return "Taverna 2 workflow";
      }

      public String getExtension() {
         return "t2flow";
      }

      public String getMimeType() {
         return "application/vnd.taverna.t2flow+xml";
      }
   }

   public static class WorkflowBundleFileType extends FileType {
      public String getDescription() {
         return "Taverna 3 workflow bundle";
      }

      public String getExtension() {
         return "wfbundle";
      }

      public String getMimeType() {
         return "application/vnd.taverna.scufl2.workflow-bundle";
      }
   }
}
