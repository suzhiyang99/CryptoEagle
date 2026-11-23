package org.apache.taverna.ui.perspectives.myexperiment;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.apache.log4j.Logger;
import org.apache.taverna.ui.perspectives.myexperiment.model.MyExperimentClient;
import org.apache.taverna.ui.perspectives.myexperiment.model.Util;

public class PluginStatusBar extends JPanel implements ActionListener {
   private static final String STATUS_MESSAGE_READY = "Ready";
   private MainComponent pluginMainComponent;
   private MyExperimentClient myExperimentClient;
   private Logger logger;
   private JLabel lSpinnerIcon;
   private JLabel lStatusMsg;
   private JLabel lCurrentUser;
   ArrayList<String> alTabClassNames;
   ArrayList<String> alTabStatuses;
   ImageIcon iconSpinner;
   ImageIcon iconSpinnerStopped;

   public PluginStatusBar(MainComponent component, MyExperimentClient client, Logger logger) {
      this.pluginMainComponent = component;
      this.myExperimentClient = client;
      this.logger = logger;
      this.alTabClassNames = new ArrayList();
      this.alTabStatuses = new ArrayList();
      this.iconSpinner = new ImageIcon(MyExperimentPerspective.getLocalResourceURL("spinner"));
      this.iconSpinnerStopped = new ImageIcon(MyExperimentPerspective.getLocalResourceURL("spinner_stopped"));
      this.setLayout(new BorderLayout());
      this.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 1));
      this.lSpinnerIcon = new JLabel("", this.iconSpinnerStopped, 2);
      this.lStatusMsg = new JLabel("Ready");
      this.lCurrentUser = new JLabel("Please log in to access your profile", 0);
      JPanel pWestStatusBarSection = new JPanel();
      pWestStatusBarSection.add(this.lSpinnerIcon);
      pWestStatusBarSection.add(this.lStatusMsg);
      this.add(pWestStatusBarSection, "West");
      this.add(this.lCurrentUser, "East");
   }

   public void setCurrentUser(String strUsername) {
      if (strUsername != null && strUsername.length() != 0) {
         this.lCurrentUser.setText("<html>Logged in as <b>" + strUsername + "</b></html>");
      } else {
         this.lCurrentUser.setText("Please log in to access your profile");
      }

   }

   public void displayStatus(String strTabClassName) {
      int iTabIdx = -1;
      String strBaseClassName = Util.getBaseClassName(strTabClassName);
      if ((iTabIdx = this.alTabClassNames.indexOf(strBaseClassName)) != -1) {
         String strCurStatus = (String)this.alTabStatuses.get(iTabIdx);
         this.lStatusMsg.setText(strCurStatus);
         this.startSpinner(!strCurStatus.equals("Ready"));
      } else {
         this.setStatus(strBaseClassName, (String)null);
      }

   }

   public void setStatus(String strTabClassName, String strStatus) {
      if (strStatus == null || strStatus.length() == 0) {
         strStatus = "Ready";
      }

      String strBaseClassName = Util.getBaseClassName(strTabClassName);
      int iTabIdx = -1;
      if ((iTabIdx = this.alTabClassNames.indexOf(strBaseClassName)) != -1) {
         this.alTabStatuses.set(iTabIdx, strStatus);
      } else {
         this.alTabClassNames.add(strBaseClassName);
         this.alTabStatuses.add(strStatus);
      }

      if (this.isTabActive(strBaseClassName)) {
         this.displayStatus(strBaseClassName);
      }

   }

   public void startSpinner(boolean bStart) {
      this.lSpinnerIcon.setIcon(bStart ? this.iconSpinner : this.iconSpinnerStopped);
   }

   private boolean isTabActive(String strTabClassName) {
      String strCurSelectedTabClassName = this.pluginMainComponent.getMainTabs().getSelectedComponent().getClass().getName();
      String strBaseClassName = Util.getBaseClassName(strTabClassName);
      return strBaseClassName.equals(strCurSelectedTabClassName);
   }

   public void actionPerformed(ActionEvent e) {
   }
}
