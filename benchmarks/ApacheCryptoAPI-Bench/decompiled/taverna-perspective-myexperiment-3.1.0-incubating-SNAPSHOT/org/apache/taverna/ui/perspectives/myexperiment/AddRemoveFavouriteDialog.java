package org.apache.taverna.ui.perspectives.myexperiment;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import org.apache.taverna.ui.perspectives.myexperiment.model.MyExperimentClient;
import org.apache.taverna.ui.perspectives.myexperiment.model.Resource;
import org.apache.taverna.ui.perspectives.myexperiment.model.ServerResponse;
import org.apache.taverna.ui.perspectives.myexperiment.model.Util;
import org.apache.taverna.workbench.helper.HelpEnabledDialog;

public class AddRemoveFavouriteDialog extends HelpEnabledDialog implements ActionListener, ComponentListener {
   protected static final int OPERATION_SUCCESSFUL = 1;
   protected static final int OPERATION_CANCELLED = 0;
   protected static final int OPERATION_FAILED = -1;
   private final MainComponent pluginMainComponent;
   private final MyExperimentClient myExperimentClient;
   private final Logger logger;
   private JButton bAddRemoveFavourite;
   private JButton bCancel;
   private final Resource resource;
   private final boolean bIsFavouriteBeingAdded;
   private int iOperationStatus = 0;
   private ServerResponse response = null;

   public AddRemoveFavouriteDialog(JFrame owner, boolean isFavouriteAdded, Resource resource, MainComponent component, MyExperimentClient client, Logger logger) {
      super(owner, isFavouriteAdded ? "Add to" : "Remove from favourites - \"" + resource.getTitle() + "\" " + resource.getItemTypeName(), true);
      this.pluginMainComponent = component;
      this.myExperimentClient = client;
      this.logger = logger;
      this.bIsFavouriteBeingAdded = isFavouriteAdded;
      this.resource = resource;
      this.setDefaultCloseOperation(2);
      this.initialiseUI();
   }

   private void initialiseUI() {
      Container contentPane = this.getContentPane();
      contentPane.setLayout(new GridBagLayout());
      GridBagConstraints c = new GridBagConstraints();
      JLabel lInfo = new JLabel("<html><center>You are about to " + (this.bIsFavouriteBeingAdded ? "add" : "remove") + " \"" + this.resource.getTitle() + "\" " + this.resource.getItemTypeName() + (this.bIsFavouriteBeingAdded ? " to" : " from") + " your favourites.<br><br>Do you want to proceed?</center></html>");
      c.gridx = 0;
      c.gridy = 0;
      c.anchor = 17;
      c.gridwidth = 2;
      c.fill = 0;
      c.insets = new Insets(10, 10, 10, 10);
      contentPane.add(lInfo, c);
      if (this.bIsFavouriteBeingAdded) {
         this.bAddRemoveFavourite = new JButton("Add to Favourites");
      } else {
         this.bAddRemoveFavourite = new JButton("Remove from Favourites");
      }

      this.bAddRemoveFavourite.setDefaultCapable(true);
      this.bAddRemoveFavourite.addActionListener(this);
      c.gridy = 1;
      c.anchor = 13;
      c.gridwidth = 1;
      c.fill = 0;
      c.weightx = (double)0.5F;
      c.insets = new Insets(5, 5, 10, 5);
      contentPane.add(this.bAddRemoveFavourite, c);
      this.bCancel = new JButton("Cancel");
      this.bCancel.setPreferredSize(this.bAddRemoveFavourite.getPreferredSize());
      this.bCancel.addActionListener(this);
      c.gridx = 1;
      c.anchor = 17;
      contentPane.add(this.bCancel, c);
      this.pack();
      this.getRootPane().setDefaultButton(this.bAddRemoveFavourite);
      this.setMinimumSize(this.getPreferredSize());
      this.setMaximumSize(this.getPreferredSize());
      this.addComponentListener(this);
   }

   public int launchAddRemoveFavouriteDialogAndPerformNecessaryActionIfRequired() {
      this.setVisible(true);
      return this.iOperationStatus;
   }

   public void actionPerformed(ActionEvent e) {
      if (e.getSource().equals(this.bAddRemoveFavourite)) {
         final Container contentPane = this.getContentPane();
         contentPane.removeAll();
         final GridBagConstraints c = new GridBagConstraints();
         c.gridx = 0;
         c.gridy = 0;
         c.gridwidth = 2;
         c.anchor = 10;
         c.fill = 0;
         c.insets = new Insets(10, 5, 10, 5);
         JLabel lInfo = new JLabel(this.bIsFavouriteBeingAdded ? "Adding to favourites..." : "Removing from favourites...", new ImageIcon(MyExperimentPerspective.getLocalResourceURL("spinner")), 0);
         contentPane.add(lInfo, c);
         this.setDefaultCloseOperation(0);
         this.pack();
         this.validate();
         this.repaint();
         (new Thread("Execute add / remove favourite operation") {
            public void run() {
               AddRemoveFavouriteDialog.this.response = AddRemoveFavouriteDialog.this.bIsFavouriteBeingAdded ? AddRemoveFavouriteDialog.this.myExperimentClient.addFavourite(AddRemoveFavouriteDialog.this.resource) : AddRemoveFavouriteDialog.this.myExperimentClient.deleteFavourite(AddRemoveFavouriteDialog.this.resource);
               AddRemoveFavouriteDialog.this.iOperationStatus = AddRemoveFavouriteDialog.this.response.getResponseCode() == 200 ? 1 : -1;
               if (AddRemoveFavouriteDialog.this.iOperationStatus == 1) {
                  if (AddRemoveFavouriteDialog.this.bIsFavouriteBeingAdded) {
                     AddRemoveFavouriteDialog.this.myExperimentClient.getCurrentUser().getFavourites().add(AddRemoveFavouriteDialog.this.resource);
                  } else {
                     AddRemoveFavouriteDialog.this.myExperimentClient.getCurrentUser().getFavourites().remove(AddRemoveFavouriteDialog.this.resource);
                  }
               } else {
                  AddRemoveFavouriteDialog.this.myExperimentClient.updateUserFavourites(AddRemoveFavouriteDialog.this.myExperimentClient.getCurrentUser());
               }

               SwingUtilities.invokeLater(new Runnable() {
                  public void run() {
                     AddRemoveFavouriteDialog.this.setDefaultCloseOperation(2);
                     contentPane.removeAll();
                     c.insets = new Insets(10, 5, 5, 5);
                     if (AddRemoveFavouriteDialog.this.iOperationStatus == 1) {
                        contentPane.add(new JLabel("Item has been successfully " + (AddRemoveFavouriteDialog.this.bIsFavouriteBeingAdded ? "added to" : "removed from") + " your favourites", new ImageIcon(MyExperimentPerspective.getLocalResourceURL("success_icon")), 2), c);
                     } else {
                        contentPane.add(new JLabel("<html><center>Error occurred while " + (AddRemoveFavouriteDialog.this.bIsFavouriteBeingAdded ? "adding" : "removing") + " the item " + (AddRemoveFavouriteDialog.this.bIsFavouriteBeingAdded ? "to" : "from") + " your favourites:<br>" + Util.retrieveReasonFromErrorXMLDocument(AddRemoveFavouriteDialog.this.response.getResponseBody()) + "</center></html>", new ImageIcon(MyExperimentPerspective.getLocalResourceURL("failure_icon")), 2), c);
                     }

                     AddRemoveFavouriteDialog.this.bCancel.setText("OK");
                     AddRemoveFavouriteDialog.this.bCancel.setPreferredSize((Dimension)null);
                     AddRemoveFavouriteDialog.this.bCancel.setDefaultCapable(true);
                     c.insets = new Insets(5, 5, 10, 5);
                     ++c.gridy;
                     contentPane.add(AddRemoveFavouriteDialog.this.bCancel, c);
                     AddRemoveFavouriteDialog.this.pack();
                     AddRemoveFavouriteDialog.this.repaint();
                     AddRemoveFavouriteDialog.this.bCancel.requestFocusInWindow();
                     AddRemoveFavouriteDialog.this.getRootPane().setDefaultButton(AddRemoveFavouriteDialog.this.bCancel);
                  }
               });
            }
         }).start();
      } else if (e.getSource().equals(this.bCancel)) {
         this.dispose();
      }

   }

   public void componentShown(ComponentEvent e) {
      Util.centerComponentWithinAnother(this.pluginMainComponent.getPreviewBrowser(), this);
   }

   public void componentHidden(ComponentEvent e) {
   }

   public void componentMoved(ComponentEvent e) {
   }

   public void componentResized(ComponentEvent e) {
   }
}
