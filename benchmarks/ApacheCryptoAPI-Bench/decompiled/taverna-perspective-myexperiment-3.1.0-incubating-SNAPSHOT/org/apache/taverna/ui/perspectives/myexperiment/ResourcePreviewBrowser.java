package org.apache.taverna.ui.perspectives.myexperiment;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;
import org.apache.log4j.Logger;
import org.apache.taverna.ui.perspectives.myexperiment.model.Base64;
import org.apache.taverna.ui.perspectives.myexperiment.model.MyExperimentClient;
import org.apache.taverna.ui.perspectives.myexperiment.model.Resource;
import org.apache.taverna.workbench.file.FileManager;
import org.apache.taverna.workbench.icons.WorkbenchIcons;

public class ResourcePreviewBrowser extends JFrame implements ActionListener, HyperlinkListener, ComponentListener {
   protected static final int PREFERRED_WIDTH = 750;
   protected static final int PREFERRED_HEIGHT = 600;
   protected static final int PREFERRED_SCROLL = 10;
   protected static final int PREVIEW_HISTORY_LENGTH = 50;
   private int iCurrentHistoryIdx;
   private final ArrayList<String> alCurrentHistory;
   private ArrayList<Resource> alFullHistory;
   private final MainComponent pluginMainComponent;
   private final MyExperimentClient myExperimentClient;
   private final Logger logger;
   private ResourcePreviewContent rpcContent;
   private Resource resource;
   private JPanel jpMain;
   private JPanel jpStatusBar;
   private JLabel lSpinnerIcon;
   private JButton bBack;
   private JButton bForward;
   private JButton bRefresh;
   private JButton bOpenInMyExp;
   private JButton bDownload;
   private JButton bOpenInTaverna;
   private JButton bImportIntoTaverna;
   private JButton bAddComment;
   private JButton bAddRemoveFavourite;
   private JButton bUpload;
   private JButton bEditMetadata;
   private JScrollPane spContentScroller;
   private final ImageIcon iconOpenInMyExp = new ImageIcon(MyExperimentPerspective.getLocalResourceURL("open_in_my_experiment_icon"));
   private final ImageIcon iconAddFavourite = new ImageIcon(MyExperimentPerspective.getLocalResourceURL("add_favourite_icon"));
   private final ImageIcon iconDeleteFavourite = new ImageIcon(MyExperimentPerspective.getLocalResourceURL("delete_favourite_icon"));
   private final ImageIcon iconAddComment = new ImageIcon(MyExperimentPerspective.getLocalResourceURL("add_comment_icon"));
   private final ImageIcon iconSpinner = new ImageIcon(MyExperimentPerspective.getLocalResourceURL("spinner"));
   private final ImageIcon iconSpinnerStopped = new ImageIcon(MyExperimentPerspective.getLocalResourceURL("spinner_stopped"));
   private final FileManager fileManager;

   public ResourcePreviewBrowser(MainComponent component, MyExperimentClient client, Logger logger, FileManager fileManager) {
      this.pluginMainComponent = component;
      this.myExperimentClient = client;
      this.logger = logger;
      this.fileManager = fileManager;
      String strPreviewedItemsHistory = (String)this.myExperimentClient.getSettings().get("previewed_items_history");
      if (strPreviewedItemsHistory != null) {
         Object oPreviewedItemsHistory = Base64.decodeToObject(strPreviewedItemsHistory);
         this.alFullHistory = (ArrayList)oPreviewedItemsHistory;
      } else {
         this.alFullHistory = new ArrayList();
      }

      this.iCurrentHistoryIdx = -1;
      this.alCurrentHistory = new ArrayList();
      this.setIconImage((new ImageIcon(MyExperimentPerspective.getLocalResourceURL("myexp_icon"))).getImage());
      this.addComponentListener(this);
      this.initialiseUI();
   }

   public ArrayList<Resource> getPreviewHistory() {
      return this.alFullHistory;
   }

   public List<String> getCurrentPreviewHistory() {
      return this.alCurrentHistory;
   }

   public void clearPreviewHistory() {
      this.iCurrentHistoryIdx = -1;
      this.alCurrentHistory.clear();
      this.alFullHistory.clear();
   }

   public void preview(String action) {
      while(this.alCurrentHistory.size() > this.iCurrentHistoryIdx + 1) {
         this.alCurrentHistory.remove(this.alCurrentHistory.size() - 1);
      }

      boolean bPreviewNotTheSameAsTheLastOne = true;
      if (this.alCurrentHistory.size() > 0) {
         if (action.equals(this.alCurrentHistory.get(this.alCurrentHistory.size() - 1))) {
            bPreviewNotTheSameAsTheLastOne = false;
         }

         this.bBack.setEnabled(bPreviewNotTheSameAsTheLastOne || this.alCurrentHistory.size() > 1);
         this.bForward.setEnabled(false);
      } else if (this.alCurrentHistory.size() == 0) {
         this.bBack.setEnabled(false);
         this.bForward.setEnabled(false);
      }

      if (bPreviewNotTheSameAsTheLastOne) {
         ++this.iCurrentHistoryIdx;
         this.alCurrentHistory.add(action);
      }

      this.createPreview(action);
   }

   private void createPreview(final String action) {
      this.setTitle("Loading preview...");
      this.lSpinnerIcon.setIcon(this.iconSpinner);
      this.bOpenInMyExp.setEnabled(false);
      this.bDownload.setEnabled(false);
      this.bOpenInTaverna.setEnabled(false);
      this.bImportIntoTaverna.setEnabled(false);
      this.bAddRemoveFavourite.setEnabled(false);
      this.bAddComment.setEnabled(false);
      this.bUpload.setEnabled(false);
      this.bEditMetadata.setEnabled(false);
      (new Thread("Load myExperiment resource preview content") {
         public void run() {
            ResourcePreviewBrowser.this.logger.debug("Starting to fetch the preview content data");

            try {
               ResourcePreviewBrowser.this.rpcContent = ResourcePreviewBrowser.this.pluginMainComponent.getPreviewFactory().createPreview(action, ResourcePreviewBrowser.this);
               ResourcePreviewBrowser.this.alFullHistory.remove(ResourcePreviewBrowser.this.rpcContent.getResource());
               ResourcePreviewBrowser.this.alFullHistory.add(ResourcePreviewBrowser.this.rpcContent.getResource());
               if (ResourcePreviewBrowser.this.alFullHistory.size() > 50) {
                  ResourcePreviewBrowser.this.alFullHistory.remove(0);
               }

               ResourcePreviewBrowser.this.pluginMainComponent.getHistoryBrowser().refreshHistoryBox(0);
               SwingUtilities.invokeLater(new Runnable() {
                  public void run() {
                     ResourcePreviewBrowser.this.setTitle(Resource.getResourceTypeName(ResourcePreviewBrowser.this.rpcContent.getResourceType()) + ": " + ResourcePreviewBrowser.this.rpcContent.getResourceTitle());
                     ResourcePreviewBrowser.this.lSpinnerIcon.setIcon(ResourcePreviewBrowser.this.iconSpinnerStopped);
                     ResourcePreviewBrowser.this.updateButtonBarState(ResourcePreviewBrowser.this.rpcContent);
                     ResourcePreviewBrowser.this.spContentScroller = new JScrollPane(ResourcePreviewBrowser.this.rpcContent.getContent());
                     ResourcePreviewBrowser.this.spContentScroller.setBorder(BorderFactory.createEmptyBorder());
                     ResourcePreviewBrowser.this.spContentScroller.getVerticalScrollBar().setUnitIncrement(10);
                     ResourcePreviewBrowser.this.jpMain.removeAll();
                     ResourcePreviewBrowser.this.jpMain.add(ResourcePreviewBrowser.this.redrawStatusBar(), "North");
                     ResourcePreviewBrowser.this.jpMain.add(ResourcePreviewBrowser.this.spContentScroller, "Center");
                     ResourcePreviewBrowser.this.validate();
                     ResourcePreviewBrowser.this.repaint();
                  }
               });
            } catch (Exception ex) {
               ResourcePreviewBrowser.this.logger.error("Exception on attempt to login to myExperiment:\n", ex);
            }

         }
      }).start();
      this.setVisible(true);
   }

   private void initialiseUI() {
      this.createButtonsForStatusBar();
      this.jpMain = new JPanel();
      this.jpMain.setOpaque(true);
      this.jpMain.setLayout(new BorderLayout());
      this.jpMain.add(this.redrawStatusBar(), "North");
      this.getContentPane().add(this.jpMain);
   }

   private void createButtonsForStatusBar() {
      this.bBack = new JButton(new ImageIcon(MyExperimentPerspective.getLocalResourceURL("back_icon")));
      this.bBack.setToolTipText("Back");
      this.bBack.addActionListener(this);
      this.bBack.setEnabled(false);
      this.bForward = new JButton(new ImageIcon(MyExperimentPerspective.getLocalResourceURL("forward_icon")));
      this.bForward.setToolTipText("Forward");
      this.bForward.addActionListener(this);
      this.bForward.setEnabled(false);
      this.bRefresh = new JButton(new ImageIcon(MyExperimentPerspective.getLocalResourceURL("refresh_icon")));
      this.bRefresh.setToolTipText("Refresh");
      this.bRefresh.addActionListener(this);
      this.lSpinnerIcon = new JLabel(this.iconSpinner);
      this.bOpenInMyExp = new JButton(this.iconOpenInMyExp);
      this.bOpenInMyExp.setEnabled(false);
      this.bOpenInMyExp.addActionListener(this);
      this.bAddRemoveFavourite = new JButton(this.iconAddFavourite);
      this.bAddRemoveFavourite.setEnabled(false);
      this.bAddRemoveFavourite.addActionListener(this);
      this.bAddComment = new JButton(this.iconAddComment);
      this.bAddComment.setEnabled(false);
      this.bAddComment.addActionListener(this);
      this.bEditMetadata = new JButton("Update", WorkbenchIcons.editIcon);
      this.bEditMetadata.setEnabled(false);
      this.bEditMetadata.addActionListener(this);
      this.bUpload = new JButton("Upload", WorkbenchIcons.upArrowIcon);
      this.bUpload.setEnabled(false);
      this.bUpload.addActionListener(this);
      this.bOpenInTaverna = new JButton(WorkbenchIcons.openIcon);
      this.bOpenInTaverna.setEnabled(false);
      this.bOpenInTaverna.addActionListener(this);
      this.bImportIntoTaverna = new JButton();
      this.bImportIntoTaverna.setEnabled(false);
      this.bImportIntoTaverna.addActionListener(this);
      this.bDownload = new JButton(WorkbenchIcons.saveIcon);
      this.bDownload.setEnabled(false);
      this.bDownload.addActionListener(this);
   }

   private JPanel redrawStatusBar() {
      JPanel jpNavigationButtons = new JPanel();
      jpNavigationButtons.add(this.bBack);
      jpNavigationButtons.add(this.bForward);
      JPanel jpRefreshButtons = new JPanel();
      jpRefreshButtons.add(this.bRefresh);
      jpRefreshButtons.add(this.lSpinnerIcon);
      JPanel jpMyExperimentButtons = new JPanel();
      jpMyExperimentButtons.add(this.bOpenInMyExp);
      jpMyExperimentButtons.add(this.bAddRemoveFavourite);
      jpMyExperimentButtons.add(this.bAddComment);
      jpMyExperimentButtons.add(this.bEditMetadata);
      jpMyExperimentButtons.add(this.bUpload);
      JPanel jpTavernaButtons = new JPanel();
      jpTavernaButtons.add(this.bOpenInTaverna);
      jpTavernaButtons.add(this.bImportIntoTaverna);
      jpTavernaButtons.add(this.bDownload);
      JPanel jpStatusBar = new JPanel();
      jpStatusBar.setLayout(new GridBagLayout());
      int spaceBetweenSections = 40;
      GridBagConstraints c = new GridBagConstraints();
      c.insets = new Insets(0, spaceBetweenSections, 0, spaceBetweenSections / 2);
      c.anchor = 17;
      c.fill = 2;
      c.gridx = 0;
      c.gridy = 0;
      jpStatusBar.add(jpNavigationButtons, c);
      ++c.gridx;
      c.insets = new Insets(0, spaceBetweenSections / 2, 0, spaceBetweenSections / 2);
      jpStatusBar.add(jpMyExperimentButtons, c);
      ++c.gridx;
      jpStatusBar.add(jpTavernaButtons, c);
      ++c.gridx;
      c.insets = new Insets(0, spaceBetweenSections / 2, 0, spaceBetweenSections);
      jpStatusBar.add(jpRefreshButtons, c);
      return jpStatusBar;
   }

   private void updateButtonBarState(ResourcePreviewContent content) {
      Resource r = this.rpcContent.getResource();
      String strResourceType = Resource.getResourceTypeName(r.getItemType()).toLowerCase();
      this.bOpenInMyExp.setEnabled(true);
      this.bOpenInMyExp.setToolTipText("Open this " + strResourceType + " in myExperiment");
      String strTooltip = "It is currently not possible to edit the metadata for this workflow";
      boolean bUpdateMetaAvailable = false;
      if (this.myExperimentClient.isLoggedIn() && this.myExperimentClient.getCurrentUser().equals(r.getUploader()) && r.getItemTypeName().equals("Workflow")) {
         strTooltip = "Update the metadata of this workflow.";
         bUpdateMetaAvailable = true;
      } else {
         strTooltip = "Only the owners of workflows can change the metadata of workflows.";
      }

      this.bEditMetadata.setToolTipText(strTooltip);
      this.bEditMetadata.setEnabled(bUpdateMetaAvailable);
      strTooltip = "It is currently not possible to upload a new version of this workflow.";
      boolean bUploadAvailable = false;
      if (this.myExperimentClient.isLoggedIn() && this.myExperimentClient.getCurrentUser().equals(r.getUploader()) && r.getItemTypeName().equals("Workflow")) {
         strTooltip = "Upload a new version of this workflow.";
         bUploadAvailable = true;
      } else {
         strTooltip = "Only the owners of workflows can upload new versions of workflows.";
      }

      this.bUpload.setToolTipText(strTooltip);
      this.bUpload.setEnabled(bUploadAvailable);
      this.bDownload.setAction(this.pluginMainComponent.new DownloadResourceAction(r, false));
      this.bOpenInTaverna.setAction(this.pluginMainComponent.new LoadResourceInTavernaAction(r, true));
      this.bImportIntoTaverna.setAction(this.pluginMainComponent.new ImportIntoTavernaAction(r));
      strTooltip = "It is currently not possible to add " + strResourceType + "s to favourites";
      boolean bFavouritingAvailable = false;
      if (r.isFavouritable()) {
         if (this.myExperimentClient.isLoggedIn()) {
            if (r.isFavouritedBy(this.myExperimentClient.getCurrentUser())) {
               strTooltip = "Remove this " + strResourceType + " from your favourites";
               this.bAddRemoveFavourite.setIcon(this.iconDeleteFavourite);
            } else {
               strTooltip = "Add this " + strResourceType + " to your favourites";
               this.bAddRemoveFavourite.setIcon(this.iconAddFavourite);
            }

            bFavouritingAvailable = true;
         } else {
            strTooltip = "Only logged in users can add items to favourites";
         }
      }

      this.bAddRemoveFavourite.setToolTipText(strTooltip);
      this.bAddRemoveFavourite.setEnabled(bFavouritingAvailable);
      strTooltip = "It is currently not possible to comment on " + strResourceType + "s";
      boolean bCommentingAvailable = false;
      if (r.isCommentableOn()) {
         if (this.myExperimentClient.isLoggedIn()) {
            strTooltip = "Add a comment on this " + strResourceType;
            bCommentingAvailable = true;
         } else {
            strTooltip = "Only logged in users can make comments";
         }
      }

      this.bAddComment.setToolTipText(strTooltip);
      this.bAddComment.setEnabled(bCommentingAvailable);
   }

   public void hyperlinkUpdate(HyperlinkEvent e) {
      if (e.getEventType() == EventType.ACTIVATED) {
         String strAction = e.getDescription().toString();
         if (strAction.startsWith("preview:")) {
            this.preview(strAction);
         } else {
            try {
               Desktop.getDesktop().browse(new URI(strAction));
            } catch (Exception ex) {
               this.logger.error("Failed while trying to open the URL in a standard browser; URL was: " + strAction + "\nException was: " + ex);
            }
         }
      }

   }

   public void actionPerformed(ActionEvent e) {
      if (e.getSource().equals(this.bBack)) {
         --this.iCurrentHistoryIdx;
         this.bBack.setEnabled(this.iCurrentHistoryIdx > 0);
         this.bForward.setEnabled(this.iCurrentHistoryIdx < this.alCurrentHistory.size() - 1);
         this.createPreview((String)this.alCurrentHistory.get(this.iCurrentHistoryIdx));
      } else if (e.getSource().equals(this.bForward)) {
         ++this.iCurrentHistoryIdx;
         this.bBack.setEnabled(this.iCurrentHistoryIdx > 0);
         this.bForward.setEnabled(this.iCurrentHistoryIdx < this.alCurrentHistory.size() - 1);
         this.createPreview((String)this.alCurrentHistory.get(this.iCurrentHistoryIdx));
      } else if (e.getSource().equals(this.bRefresh)) {
         this.createPreview((String)this.alCurrentHistory.get(this.iCurrentHistoryIdx));
      } else if (e.getSource().equals(this.bOpenInMyExp)) {
         try {
            Desktop.getDesktop().browse(new URI(this.rpcContent.getResourceURL()));
         } catch (Exception ex) {
            this.logger.error("Failed while trying to open the URL in a standard browser; URL was: " + this.rpcContent.getResourceURL() + "\nException was: " + ex);
         }
      } else if (e.getSource().equals(this.bUpload)) {
         Resource resource = this.rpcContent.getResource();
         if (resource.getItemTypeName().equals("Workflow")) {
            UploadWorkflowDialog uploadWorkflowDialog = new UploadWorkflowDialog(this, true, resource, this.fileManager);
            if (uploadWorkflowDialog.launchUploadDialogAndPostIfRequired()) {
               this.actionPerformed(new ActionEvent(this.bRefresh, 0, ""));
            }
         }
      } else if (e.getSource().equals(this.bEditMetadata)) {
         Resource resource = this.rpcContent.getResource();
         if (resource.getItemTypeName().equals("Workflow")) {
            UploadWorkflowDialog uploadWorkflowDialog = new UploadWorkflowDialog(this, false, resource, this.fileManager);
            if (uploadWorkflowDialog.launchUploadDialogAndPostIfRequired()) {
               this.actionPerformed(new ActionEvent(this.bRefresh, 0, ""));
            }
         }
      } else if (e.getSource().equals(this.bAddComment)) {
         String strComment = null;
         AddCommentDialog commentDialog = new AddCommentDialog(this, this.rpcContent.getResource(), this.pluginMainComponent, this.myExperimentClient, this.logger);
         if (commentDialog.launchAddCommentDialogAndPostCommentIfRequired() != null) {
            this.actionPerformed(new ActionEvent(this.bRefresh, 0, ""));
            this.pluginMainComponent.getHistoryBrowser().getCommentedOnItemsHistoryList().remove(this.rpcContent.getResource());
            this.pluginMainComponent.getHistoryBrowser().getCommentedOnItemsHistoryList().add(this.rpcContent.getResource());
            if (this.pluginMainComponent.getHistoryBrowser().getCommentedOnItemsHistoryList().size() > 3) {
               this.pluginMainComponent.getHistoryBrowser().getCommentedOnItemsHistoryList().remove(0);
            }

            if (this.pluginMainComponent.getHistoryBrowser() != null) {
               this.pluginMainComponent.getHistoryBrowser().refreshHistoryBox(3);
            }
         }
      } else if (e.getSource().equals(this.bAddRemoveFavourite)) {
         boolean bItemIsFavourited = this.rpcContent.getResource().isFavouritedBy(this.myExperimentClient.getCurrentUser());
         AddRemoveFavouriteDialog favouriteDialog = new AddRemoveFavouriteDialog(this, !bItemIsFavourited, this.rpcContent.getResource(), this.pluginMainComponent, this.myExperimentClient, this.logger);
         int iFavouritingStatus = favouriteDialog.launchAddRemoveFavouriteDialogAndPerformNecessaryActionIfRequired();
         if (iFavouritingStatus != 0) {
            this.updateButtonBarState(this.rpcContent);
            this.pluginMainComponent.getMyStuffTab().getSidebar().repopulateFavouritesBox();
            this.pluginMainComponent.getMyStuffTab().getSidebar().revalidate();
         }
      } else if (e.getSource() instanceof JClickableLabel) {
         if (e.getActionCommand().startsWith("preview:")) {
            this.preview(e.getActionCommand());
         } else if (e.getActionCommand().startsWith("tag:")) {
            this.pluginMainComponent.getTagBrowserTab().actionPerformed(e);
            this.pluginMainComponent.getMainTabs().setSelectedComponent(this.pluginMainComponent.getTagBrowserTab());
         } else {
            try {
               Desktop.getDesktop().browse(new URI(e.getActionCommand()));
            } catch (Exception ex) {
               this.logger.error("Failed while trying to open the URL in a standard browser; URL was: " + e.getActionCommand() + "\nException was: " + ex);
            }
         }
      } else if (e.getSource() instanceof TagCloudPanel && e.getActionCommand().startsWith("tag:")) {
         this.setVisible(false);
         this.pluginMainComponent.getTagBrowserTab().actionPerformed(e);
         this.pluginMainComponent.getMainTabs().setSelectedComponent(this.pluginMainComponent.getTagBrowserTab());
      }

   }

   public void componentShown(ComponentEvent e) {
      this.jpMain.removeAll();
      this.jpMain.add(this.redrawStatusBar(), "North");
      this.repaint();
      this.setSize(750, 600);
      this.setMinimumSize(new Dimension(750, 600));
      int iMainComponentCenterX = (int)Math.round(this.pluginMainComponent.getLocationOnScreen().getX() + (double)(this.pluginMainComponent.getWidth() / 2));
      int iPosX = iMainComponentCenterX - this.getWidth() / 2;
      int iPosY = (int)this.pluginMainComponent.getLocationOnScreen().getY() + 30;
      this.setLocation(iPosX, iPosY);
      this.myExperimentClient.storeHistoryAndSettings();
   }

   public void componentHidden(ComponentEvent e) {
      this.myExperimentClient.storeHistoryAndSettings();
   }

   public void componentResized(ComponentEvent e) {
   }

   public void componentMoved(ComponentEvent e) {
   }

   public Resource getResource() {
      return this.resource;
   }
}
