package org.apache.taverna.ui.perspectives.myexperiment;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.CountDownLatch;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import org.apache.taverna.lang.ui.ShadedLabel;
import org.apache.taverna.ui.perspectives.myexperiment.model.MyExperimentClient;
import org.apache.taverna.workbench.file.FileManager;

public class MyStuffTabContentPanel extends JPanel implements ActionListener, KeyListener {
   private final MainComponent pluginMainComponent;
   private final MyExperimentClient myExperimentClient;
   private final Logger logger;
   private JButton bLogin;
   private JCheckBox cbLoginAutomatically;
   private MyStuffSidebarPanel jpSidebar;
   public JSplitPane spMyStuff;
   protected CountDownLatch cdlComponentLoadingDone;
   int NUMBER_OF_SUBCOMPONENTS = 2;
   protected JComponent cTabContentComponentToSwitchToAfterLogin = null;
   private final FileManager fileManager;

   public MyStuffTabContentPanel(MainComponent component, MyExperimentClient client, Logger logger, FileManager fileManager) {
      this.pluginMainComponent = component;
      this.myExperimentClient = client;
      this.logger = logger;
      this.fileManager = fileManager;
   }

   public void createAndInitialiseInnerComponents() {
      this.removeAll();
      this.cdlComponentLoadingDone = new CountDownLatch(this.NUMBER_OF_SUBCOMPONENTS);
      if (this.myExperimentClient.isLoggedIn()) {
         this.jpSidebar = new MyStuffSidebarPanel(this.pluginMainComponent, this.myExperimentClient, this.logger, this.fileManager);
         JPanel jpSidebarContainer = new JPanel();
         jpSidebarContainer.setLayout(new BorderLayout());
         jpSidebarContainer.add(this.jpSidebar, "North");
         JScrollPane spSidebar = new JScrollPane(jpSidebarContainer);
         spSidebar.getVerticalScrollBar().setUnitIncrement(10);
         spSidebar.setMinimumSize(new Dimension(this.jpSidebar.getMyProfileBox().getPreferredSize().width + 30, 0));
         this.spMyStuff = new JSplitPane();
         this.spMyStuff.setLeftComponent(spSidebar);
         this.spMyStuff.setRightComponent(new MyStuffContributionsPanel(this.pluginMainComponent, this.myExperimentClient, this.logger));
         this.pluginMainComponent.getStatusBar().setCurrentUser(this.myExperimentClient.getCurrentUser().getName());
         this.pluginMainComponent.addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent e) {
               JOptionPane.showMessageDialog((Component)null, "component shown");

               try {
                  Thread.sleep(50L);
               } catch (Exception var3) {
               }

               MyStuffTabContentPanel.this.spMyStuff.setDividerLocation(400);
            }
         });
         this.spMyStuff.setResizeWeight(0.3);
         this.spMyStuff.setOneTouchExpandable(true);
         this.spMyStuff.setDividerLocation(400);
         this.spMyStuff.setDoubleBuffered(true);
         this.setLayout(new BorderLayout());
         this.add(this.spMyStuff);
         (new Thread("Waiting for myStuff data to load") {
            public void run() {
               try {
                  MyStuffTabContentPanel.this.cdlComponentLoadingDone.await();
                  MyStuffTabContentPanel.this.pluginMainComponent.getStatusBar().setStatus(this.getClass().getName(), (String)null);
               } catch (InterruptedException var2) {
               }

            }
         }).start();
      } else {
         this.pluginMainComponent.getStatusBar().setStatus(this.getClass().getName(), (String)null);
         JPanel jpLoginBoxContainer = new JPanel();
         jpLoginBoxContainer.setLayout(new GridBagLayout());
         GridBagConstraints c = new GridBagConstraints();
         jpLoginBoxContainer.add(this.createLoginBox(), c);
         this.setLayout(new BorderLayout());
         this.add(new ShadedLabel("Welcome to the myExperiment plugin. Please note that you can still use other tabs even if you don't have a user profile yet!", ShadedLabel.BLUE), "North");
         this.add(jpLoginBoxContainer, "Center");
      }

   }

   private JPanel createLoginBox() {
      JPanel jpLoginBox = new JPanel();
      jpLoginBox.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
      jpLoginBox.setLayout(new GridBagLayout());
      GridBagConstraints c = new GridBagConstraints();
      c.gridwidth = 0;
      c.insets = new Insets(0, 0, 15, 0);
      c.gridx = 0;
      c.gridy = 0;
      JLabel jlHeader = new JLabel("<html><b>Log in to myExperiment</b></html>");
      jlHeader.setFont(jlHeader.getFont().deriveFont(13.0F));
      jpLoginBox.add(jlHeader, c);
      c.weightx = (double)1.0F;
      c.gridwidth = 1;
      c.anchor = 21;
      c.insets = new Insets(0, 0, 3, 0);
      c.ipadx = 10;
      ++c.gridy;
      c.insets = new Insets(0, 0, 0, 3);
      this.cbLoginAutomatically = new JCheckBox("Log in automatically (next time)");
      this.cbLoginAutomatically.setBorder(BorderFactory.createEmptyBorder());
      this.cbLoginAutomatically.addActionListener(this);
      this.cbLoginAutomatically.addKeyListener(this);
      jpLoginBox.add(this.cbLoginAutomatically, c);
      ++c.gridy;
      c.gridx = 0;
      c.anchor = 10;
      c.gridwidth = 0;
      c.fill = 2;
      c.insets = new Insets(10, 0, 0, 0);
      this.bLogin = new JButton("Login", new ImageIcon(MyExperimentPerspective.getLocalResourceURL("login_icon")));
      this.bLogin.setDefaultCapable(true);
      this.bLogin.addKeyListener(this);
      this.bLogin.addActionListener(this);
      jpLoginBox.add(this.bLogin, c);
      return jpLoginBox;
   }

   public MyStuffSidebarPanel getSidebar() {
      return this.jpSidebar;
   }

   public void actionPerformed(ActionEvent e) {
      if (e.getSource().equals(this.bLogin)) {
         this.pluginMainComponent.getStatusBar().setStatus(this.getClass().getName(), "Logging in");
         (new Thread("Login to myExperiment") {
            public void run() {
               MyStuffTabContentPanel.this.logger.debug("Logging in to myExperiment");

               try {
                  boolean bLoginSuccessful = MyStuffTabContentPanel.this.myExperimentClient.doLogin();
                  if (bLoginSuccessful) {
                     MyStuffTabContentPanel.this.myExperimentClient.getSettings().put("auto_login", (new Boolean(MyStuffTabContentPanel.this.cbLoginAutomatically.isSelected())).toString());
                     MyStuffTabContentPanel.this.myExperimentClient.storeHistoryAndSettings();
                     MyStuffTabContentPanel.this.pluginMainComponent.getStatusBar().setStatus(this.getClass().getName(), "Fetching user data");
                     SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                           if (MyStuffTabContentPanel.this.myExperimentClient.isLoggedIn()) {
                              MyStuffTabContentPanel.this.createAndInitialiseInnerComponents();
                              MyStuffTabContentPanel.this.pluginMainComponent.getTagBrowserTab().setMyTagsShown(true);
                              MyStuffTabContentPanel.this.pluginMainComponent.getTagBrowserTab().getMyTagPanel().refresh();
                              MyStuffTabContentPanel.this.pluginMainComponent.getTagBrowserTab().rerunLastTagSearch();
                              MyStuffTabContentPanel.this.pluginMainComponent.getSearchTab().rerunLastSearch();
                              if (MyStuffTabContentPanel.this.cTabContentComponentToSwitchToAfterLogin != null) {
                                 MyStuffTabContentPanel.this.pluginMainComponent.getMainTabs().setSelectedComponent(MyStuffTabContentPanel.this.cTabContentComponentToSwitchToAfterLogin);
                                 MyStuffTabContentPanel.this.cTabContentComponentToSwitchToAfterLogin = null;
                              }

                              MyStuffTabContentPanel.this.logger.debug("Logged in to myExperiment successfully");
                           } else {
                              MyStuffTabContentPanel.this.pluginMainComponent.getStatusBar().setStatus(this.getClass().getName(), (String)null);
                              JOptionPane.showMessageDialog((Component)null, "Unable to login to myExperiment - please check your login details", "myExperiment Plugin - Couldn't Login", 0);
                           }

                        }
                     });
                  }
               } catch (Exception ex) {
                  MyStuffTabContentPanel.this.logger.error("Exception on attempt to login to myExperiment:\n", ex);
               }

            }
         }).start();
      } else if (e.getSource().equals(this.jpSidebar.bRefreshMyStuff)) {
         this.pluginMainComponent.getStatusBar().setStatus(this.getClass().getName(), "Refreshing user data");
         (new Thread("Refreshing myStuff tab data") {
            public void run() {
               MyStuffTabContentPanel.this.myExperimentClient.setCurrentUser(MyStuffTabContentPanel.this.myExperimentClient.fetchCurrentUser(MyStuffTabContentPanel.this.myExperimentClient.getCurrentUser().getURI()));
               MyStuffTabContentPanel.this.createAndInitialiseInnerComponents();
               MyStuffTabContentPanel.this.revalidate();
            }
         }).start();
      }

   }

   public void keyPressed(KeyEvent e) {
      if (e.getKeyCode() == 10 && (e.getSource().equals(this.cbLoginAutomatically) || e.getSource().equals(this.bLogin))) {
         this.actionPerformed(new ActionEvent(this.bLogin, 0, ""));
      }

   }

   public void keyReleased(KeyEvent e) {
   }

   public void keyTyped(KeyEvent e) {
   }
}
