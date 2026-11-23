package org.apache.taverna.ui.perspectives.myexperiment;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import org.apache.taverna.ui.perspectives.myexperiment.model.MyExperimentClient;
import org.apache.taverna.ui.perspectives.myexperiment.model.Resource;
import org.apache.taverna.ui.perspectives.myexperiment.model.User;
import org.apache.taverna.ui.perspectives.myexperiment.model.Util;
import org.apache.taverna.workbench.file.FileManager;
import org.apache.taverna.workbench.icons.WorkbenchIcons;

public class MyStuffSidebarPanel extends JPanel implements ActionListener {
   private final MainComponent pluginMainComponent;
   private final MyExperimentClient myExperimentClient;
   private final Logger logger;
   private final JPanel jpMyProfileBox;
   private final JPanel jpMyFriendsBox;
   private final JPanel jpMyGroupsBox;
   private final JPanel jpMyFavouritesBox;
   private final JPanel jpMyTagsBox;
   private JButton bLogout;
   protected JButton bRefreshMyStuff;
   private JButton bUpload;
   private final ImageIcon iconUser;
   private final ImageIcon iconLogout;
   private final FileManager fileManager;

   public MyStuffSidebarPanel(MainComponent component, MyExperimentClient client, Logger logger, FileManager fileManager) {
      this.pluginMainComponent = component;
      this.myExperimentClient = client;
      this.logger = logger;
      this.fileManager = fileManager;
      this.iconUser = new ImageIcon(MyExperimentPerspective.getLocalIconURL(20));
      this.iconLogout = new ImageIcon(MyExperimentPerspective.getLocalResourceURL("logout_icon"));
      this.setLayout(new GridBagLayout());
      GridBagConstraints gbConstraints = new GridBagConstraints();
      gbConstraints.anchor = 18;
      gbConstraints.fill = 2;
      gbConstraints.weightx = (double)1.0F;
      gbConstraints.gridx = 0;
      gbConstraints.gridy = 0;
      this.jpMyProfileBox = this.createMyProfileBox();
      this.add(this.jpMyProfileBox, gbConstraints);
      gbConstraints.gridy = 1;
      this.jpMyFriendsBox = this.createMyFriendsBox();
      this.add(this.jpMyFriendsBox, gbConstraints);
      gbConstraints.gridy = 2;
      this.jpMyGroupsBox = this.createMyGroupsBox();
      this.add(this.jpMyGroupsBox, gbConstraints);
      gbConstraints.gridy = 3;
      this.jpMyFavouritesBox = this.createMyFavouritesBox();
      this.repopulateFavouritesBox();
      this.add(this.jpMyFavouritesBox, gbConstraints);
      gbConstraints.gridy = 4;
      this.jpMyTagsBox = this.createMyTagsBox();
      this.add(this.jpMyTagsBox, gbConstraints);
      this.pluginMainComponent.getMyStuffTab().cdlComponentLoadingDone.countDown();
   }

   private JPanel createMyProfileBox() {
      JPanel jpAvatar = new JPanel();
      jpAvatar.setLayout(new BoxLayout(jpAvatar, 0));
      User currentUser = this.myExperimentClient.getCurrentUser();
      ImageIcon userAvatar = currentUser.getAvatar();
      JLabel jlUserAvatar = new JLabel("No Profile Picture Found");
      if (userAvatar != null) {
         jlUserAvatar = new JLabel(Util.getResizedImageIcon(userAvatar, 80, 80));
      }

      jlUserAvatar.setAlignmentX(0.0F);
      jpAvatar.add(jlUserAvatar);
      String name = "<html>";

      for(int x = 0; x < currentUser.getName().split(" ").length; ++x) {
         name = name + currentUser.getName().split(" ")[x] + "<br>";
      }

      name = name + "</html>";
      JClickableLabel jclUserName = new JClickableLabel(name, "preview:20:" + currentUser.getURI(), this.pluginMainComponent.getPreviewBrowser(), this.iconUser);
      jpAvatar.add(jclUserName);
      JPanel jpEverythingInProfileBox = new JPanel();
      jpEverythingInProfileBox.setMaximumSize(new Dimension(1024, 0));
      jpEverythingInProfileBox.setLayout(new BoxLayout(jpEverythingInProfileBox, 0));
      jpEverythingInProfileBox.add(jpAvatar);
      this.bLogout = new JButton("Logout", this.iconLogout);
      this.bLogout.addActionListener(this);
      this.bRefreshMyStuff = new JButton("Refresh", WorkbenchIcons.refreshIcon);
      this.bRefreshMyStuff.addActionListener(this.pluginMainComponent.getMyStuffTab());
      this.bUpload = new JButton("Upload Workflow", WorkbenchIcons.upArrowIcon);
      this.bUpload.addActionListener(this);
      JPanel jpButtons = new JPanel();
      jpButtons.setLayout(new GridBagLayout());
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridwidth = 1;
      gbc.gridy = 0;
      gbc.anchor = 11;
      gbc.fill = 1;
      gbc.gridx = 0;
      jpButtons.add(this.bUpload, gbc);
      ++gbc.gridy;
      jpButtons.add(this.bRefreshMyStuff, gbc);
      ++gbc.gridy;
      jpButtons.add(this.bLogout, gbc);
      jpEverythingInProfileBox.add(jpButtons);
      jpEverythingInProfileBox.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " My Profile "), BorderFactory.createEmptyBorder(1, 8, 8, 5)));
      return jpEverythingInProfileBox;
   }

   private JPanel createMyFriendsBox() {
      JPanel jpFriends = new JPanel();
      jpFriends.setLayout(new BoxLayout(jpFriends, 1));
      Iterator<HashMap<String, String>> iFriends = this.myExperimentClient.getCurrentUser().getFriends().iterator();
      if (iFriends.hasNext()) {
         while(iFriends.hasNext()) {
            HashMap<String, String> hmCurFriend = (HashMap)iFriends.next();
            jpFriends.add(new JClickableLabel((String)hmCurFriend.get("name"), "preview:20:" + (String)hmCurFriend.get("uri"), this.pluginMainComponent.getPreviewBrowser(), this.iconUser));
         }
      } else {
         JLabel lNone = new JLabel("None");
         lNone.setFont(lNone.getFont().deriveFont(2));
         lNone.setForeground(Color.GRAY);
         jpFriends.add(lNone);
      }

      jpFriends.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " My Friends "), BorderFactory.createEmptyBorder(1, 8, 8, 5)));
      return jpFriends;
   }

   private JPanel createMyGroupsBox() {
      JPanel jpGroups = new JPanel();
      jpGroups.setLayout(new BoxLayout(jpGroups, 1));
      ImageIcon iconGroup = new ImageIcon(MyExperimentPerspective.getLocalIconURL(21));
      Iterator<HashMap<String, String>> iGroups = this.myExperimentClient.getCurrentUser().getGroups().iterator();
      if (iGroups.hasNext()) {
         while(iGroups.hasNext()) {
            HashMap<String, String> hmCurGroup = (HashMap)iGroups.next();
            jpGroups.add(new JClickableLabel((String)hmCurGroup.get("name"), "preview:21:" + (String)hmCurGroup.get("uri"), this.pluginMainComponent.getPreviewBrowser(), iconGroup));
         }
      } else {
         JLabel lNone = new JLabel("None");
         lNone.setFont(lNone.getFont().deriveFont(2));
         lNone.setForeground(Color.GRAY);
         jpGroups.add(lNone);
      }

      jpGroups.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " My Groups "), BorderFactory.createEmptyBorder(1, 8, 8, 5)));
      return jpGroups;
   }

   private JPanel createMyFavouritesBox() {
      JPanel jpFavourites = new JPanel();
      jpFavourites.setLayout(new BoxLayout(jpFavourites, 1));
      jpFavourites.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " My Favourites "), BorderFactory.createEmptyBorder(1, 8, 8, 5)));
      return jpFavourites;
   }

   public void repopulateFavouritesBox() {
      this.jpMyFavouritesBox.removeAll();
      Iterator<Resource> iFavourites = this.myExperimentClient.getCurrentUser().getFavourites().iterator();
      if (iFavourites.hasNext()) {
         while(iFavourites.hasNext()) {
            Resource rFavourite = (Resource)iFavourites.next();
            this.jpMyFavouritesBox.add(new JClickableLabel(rFavourite.getTitle(), "preview:" + rFavourite.getItemType() + ":" + rFavourite.getURI(), this.pluginMainComponent.getPreviewBrowser(), new ImageIcon(MyExperimentPerspective.getLocalIconURL(rFavourite.getItemType()))));
         }
      } else {
         JLabel lNone = new JLabel("None");
         lNone.setFont(lNone.getFont().deriveFont(2));
         lNone.setForeground(Color.GRAY);
         this.jpMyFavouritesBox.add(lNone);
      }

   }

   private JPanel createMyTagsBox() {
      JPanel jpTags = new JPanel();
      jpTags.setLayout(new BoxLayout(jpTags, 1));
      ImageIcon iconTag = new ImageIcon(MyExperimentPerspective.getLocalIconURL(30));
      Iterator<HashMap<String, String>> iTags = this.myExperimentClient.getCurrentUser().getTags().iterator();
      if (iTags.hasNext()) {
         while(iTags.hasNext()) {
            String strCurTag = (String)((HashMap)iTags.next()).get("name");
            jpTags.add(new JClickableLabel(strCurTag, "tag:" + strCurTag, this.pluginMainComponent.getPreviewBrowser(), iconTag));
         }
      } else {
         JLabel lNone = new JLabel("None");
         lNone.setFont(lNone.getFont().deriveFont(2));
         lNone.setForeground(Color.GRAY);
         jpTags.add(lNone);
      }

      jpTags.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " My Tags "), BorderFactory.createEmptyBorder(1, 8, 8, 5)));
      return jpTags;
   }

   public JPanel getMyProfileBox() {
      return this.jpMyProfileBox;
   }

   public void actionPerformed(ActionEvent e) {
      if (e.getSource().equals(this.bLogout)) {
         try {
            this.myExperimentClient.doLogout();
         } catch (Exception ex) {
            this.logger.error("Error while trying to logout from myExperiment, exception:\n" + ex);
         }

         this.pluginMainComponent.getStatusBar().setStatus(this.pluginMainComponent.getMyStuffTab().getClass().getName(), "Logging out");
         this.pluginMainComponent.getMyStuffTab().createAndInitialiseInnerComponents();
         this.pluginMainComponent.getMyStuffTab().revalidate();
         this.pluginMainComponent.getMyStuffTab().repaint();
         this.pluginMainComponent.getStatusBar().setStatus(this.pluginMainComponent.getMyStuffTab().getClass().getName(), (String)null);
         this.pluginMainComponent.getStatusBar().setCurrentUser((String)null);
         this.pluginMainComponent.getTagBrowserTab().setMyTagsShown(false);
         this.pluginMainComponent.getTagBrowserTab().getTagSearchResultPanel().clear();
         this.pluginMainComponent.getTagBrowserTab().rerunLastTagSearch();
         this.pluginMainComponent.getSearchTab().getSearchResultPanel().clear();
         this.pluginMainComponent.getSearchTab().rerunLastSearch();
      } else if (e.getSource().equals(this.bUpload)) {
         JFrame containingFrame = (JFrame)SwingUtilities.windowForComponent(this);
         UploadWorkflowDialog uploadWorkflowDialog = new UploadWorkflowDialog(containingFrame, true, this.fileManager);
         if (uploadWorkflowDialog.launchUploadDialogAndPostIfRequired()) {
            this.actionPerformed(new ActionEvent(this.bRefreshMyStuff, 0, ""));
         }
      }

   }
}
