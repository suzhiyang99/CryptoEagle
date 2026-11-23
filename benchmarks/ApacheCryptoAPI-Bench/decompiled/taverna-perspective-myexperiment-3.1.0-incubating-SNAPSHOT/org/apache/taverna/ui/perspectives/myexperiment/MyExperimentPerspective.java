package org.apache.taverna.ui.perspectives.myexperiment;

import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import org.apache.taverna.ui.perspectives.myexperiment.model.Resource;
import org.apache.taverna.workbench.ui.zaria.PerspectiveSPI;

public class MyExperimentPerspective implements PerspectiveSPI {
   private static final String BASE_RESOURCE_PATH = "/net/sf/taverna/t2/ui/perspectives/myexperiment/";
   public static final String PERSPECTIVE_NAME = "myExperiment";
   public static final String PLUGIN_VERSION = "0.2beta";
   private MainComponent perspectiveMainComponent;
   private boolean visible = true;

   public ImageIcon getButtonIcon() {
      URL iconURL = getLocalResourceURL("myexp_icon16x16");
      return iconURL == null ? null : new ImageIcon(iconURL);
   }

   public String getText() {
      return "myExperiment";
   }

   public boolean isVisible() {
      return this.visible;
   }

   public int positionHint() {
      return 30;
   }

   public void setVisible(boolean visible) {
      this.visible = visible;
   }

   public void setMainComponent(MainComponent component) {
      this.perspectiveMainComponent = component;
   }

   public MainComponent getMainComponent() {
      return this.perspectiveMainComponent;
   }

   public static URL getLocalResourceURL(String strResourceName) {
      String strResourcePath = "/net/sf/taverna/t2/ui/perspectives/myexperiment/";
      if (strResourceName.equals("not_authorized_icon")) {
         strResourcePath = strResourcePath + "denied.png";
      }

      if (strResourceName.equals("failure_icon")) {
         strResourcePath = strResourcePath + "denied.png";
      } else if (strResourceName.equals("success_icon")) {
         strResourcePath = strResourcePath + "tick.png";
      } else if (strResourceName.equals("spinner")) {
         strResourcePath = strResourcePath + "ajax-loader.gif";
      } else if (strResourceName.equals("spinner_stopped")) {
         strResourcePath = strResourcePath + "ajax-loader-still.gif";
      } else if (strResourceName.equals("external_link_small_icon")) {
         strResourcePath = strResourcePath + "external_link_listing_small.png";
      } else if (strResourceName.equals("back_icon")) {
         strResourcePath = strResourcePath + "arrow_left.png";
      } else if (strResourceName.equals("forward_icon")) {
         strResourcePath = strResourcePath + "arrow_right.png";
      } else if (strResourceName.equals("refresh_icon")) {
         strResourcePath = strResourcePath + "arrow_refresh.png";
      } else if (strResourceName.equals("favourite_icon")) {
         strResourcePath = strResourcePath + "star.png";
      } else if (strResourceName.equals("add_favourite_icon")) {
         strResourcePath = strResourcePath + "favourite_add.png";
      } else if (strResourceName.equals("delete_favourite_icon")) {
         strResourcePath = strResourcePath + "favourite_delete.png";
      } else if (strResourceName.equals("destroy_icon")) {
         strResourcePath = strResourcePath + "cross.png";
      } else if (strResourceName.equals("add_comment_icon")) {
         strResourcePath = strResourcePath + "comment_add.png";
      } else if (strResourceName.equals("myexp_icon")) {
         strResourcePath = strResourcePath + "myexp_icon.png";
      } else if (strResourceName.equals("myexp_icon16x16")) {
         strResourcePath = strResourcePath + "myexp_icon16x16.png";
      } else if (strResourceName.equals("open_in_my_experiment_icon")) {
         strResourcePath = strResourcePath + "open_in_myExperiment.png";
      } else if (strResourceName.equals("login_icon")) {
         strResourcePath = strResourcePath + "login.png";
      } else if (strResourceName.equals("logout_icon")) {
         strResourcePath = strResourcePath + "logout.png";
      } else {
         if (!strResourceName.equals("css_stylesheet")) {
            throw new IllegalArgumentException("Unknown myExperiment plugin resource requested; requested resource name was: " + strResourceName);
         }

         strResourcePath = strResourcePath + "styles.css";
      }

      return MyExperimentPerspective.class.getResource(strResourcePath);
   }

   public static URL getLocalIconURL(int iResourceType) {
      String strResourcePath = "/net/sf/taverna/t2/ui/perspectives/myexperiment/";
      switch (iResourceType) {
         case 10:
            strResourcePath = strResourcePath + "workflow.png";
            break;
         case 11:
            strResourcePath = strResourcePath + "file.png";
            break;
         case 12:
            strResourcePath = strResourcePath + "pack.png";
            break;
         case 13:
         case 14:
         case 16:
         case 17:
         case 18:
         case 19:
         case 22:
         case 23:
         case 24:
         case 25:
         case 26:
         case 27:
         case 28:
         case 29:
         default:
            throw new IllegalArgumentException("Unknown myExperiment plugin resource requested; requested resource name was: " + Resource.getResourceTypeName(iResourceType));
         case 15:
            strResourcePath = strResourcePath + "remote_resource.png";
            break;
         case 20:
            strResourcePath = strResourcePath + "user.png";
            break;
         case 21:
            strResourcePath = strResourcePath + "group.png";
            break;
         case 30:
            strResourcePath = strResourcePath + "tag_blue.png";
      }

      return MyExperimentPerspective.class.getResource(strResourcePath);
   }

   public String getID() {
      return "myExperiment";
   }

   public JComponent getPanel() {
      return this.getMainComponent();
   }
}
