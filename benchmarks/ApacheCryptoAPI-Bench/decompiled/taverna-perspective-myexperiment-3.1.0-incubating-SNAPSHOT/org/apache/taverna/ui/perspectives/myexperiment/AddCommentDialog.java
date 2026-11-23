package org.apache.taverna.ui.perspectives.myexperiment;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import org.apache.log4j.Logger;
import org.apache.taverna.lang.ui.DialogTextArea;
import org.apache.taverna.ui.perspectives.myexperiment.model.MyExperimentClient;
import org.apache.taverna.ui.perspectives.myexperiment.model.Resource;
import org.apache.taverna.ui.perspectives.myexperiment.model.ServerResponse;
import org.apache.taverna.ui.perspectives.myexperiment.model.Util;
import org.apache.taverna.workbench.helper.HelpEnabledDialog;

public class AddCommentDialog extends HelpEnabledDialog implements ActionListener, CaretListener, ComponentListener, KeyListener {
   private MainComponent pluginMainComponent;
   private MyExperimentClient myExperimentClient;
   private Logger logger;
   private DialogTextArea taComment;
   private JButton bPost;
   private JButton bCancel;
   private JLabel lStatusMessage;
   private Resource resource;
   private String strComment = null;
   private boolean bPostingSuccessful = false;

   public AddCommentDialog(JFrame owner, Resource resource, MainComponent component, MyExperimentClient client, Logger logger) {
      super(owner, "Add comment for \"" + resource.getTitle() + "\" " + resource.getItemTypeName(), true);
      this.pluginMainComponent = component;
      this.myExperimentClient = client;
      this.logger = logger;
      this.resource = resource;
      this.setDefaultCloseOperation(2);
      this.initialiseUI();
   }

   private void initialiseUI() {
      Container contentPane = this.getContentPane();
      contentPane.setLayout(new GridBagLayout());
      GridBagConstraints c = new GridBagConstraints();
      JLabel lInfo = new JLabel("Please type in you comment:");
      c.gridx = 0;
      c.gridy = 0;
      c.anchor = 17;
      c.gridwidth = 2;
      c.fill = 0;
      c.insets = new Insets(10, 10, 5, 10);
      contentPane.add(lInfo, c);
      this.taComment = new DialogTextArea(5, 35);
      this.taComment.setLineWrap(true);
      this.taComment.setWrapStyleWord(true);
      this.taComment.addKeyListener(this);
      this.taComment.addCaretListener(this);
      JScrollPane spComment = new JScrollPane(this.taComment);
      c.gridy = 1;
      c.fill = 2;
      c.insets = new Insets(0, 10, 0, 10);
      contentPane.add(spComment, c);
      this.bPost = new JButton("Post Comment");
      this.bPost.setEnabled(false);
      this.bPost.setDefaultCapable(true);
      this.getRootPane().setDefaultButton(this.bPost);
      this.bPost.addActionListener(this);
      this.bPost.addKeyListener(this);
      c.gridy = 2;
      c.anchor = 13;
      c.gridwidth = 1;
      c.fill = 0;
      c.weightx = (double)0.5F;
      c.insets = new Insets(10, 5, 10, 5);
      contentPane.add(this.bPost, c);
      this.bCancel = new JButton("Cancel");
      this.bCancel.setPreferredSize(this.bPost.getPreferredSize());
      this.bCancel.addActionListener(this);
      c.gridx = 1;
      c.anchor = 17;
      c.weightx = (double)0.5F;
      contentPane.add(this.bCancel, c);
      this.pack();
      this.setMinimumSize(this.getPreferredSize());
      this.setMaximumSize(this.getPreferredSize());
      this.addComponentListener(this);
   }

   public String launchAddCommentDialogAndPostCommentIfRequired() {
      this.setVisible(true);
      return this.strComment;
   }

   public void actionPerformed(ActionEvent e) {
      if (e.getSource().equals(this.bPost)) {
         this.strComment = this.taComment.getText();
         final JRootPane rootPane = this.getRootPane();
         final Container contentPane = this.getContentPane();
         contentPane.remove(this.bPost);
         contentPane.remove(this.bCancel);
         if (this.lStatusMessage != null) {
            contentPane.remove(this.lStatusMessage);
         }

         this.taComment.setEditable(false);
         final GridBagConstraints c = new GridBagConstraints();
         c.gridx = 0;
         c.gridy = 2;
         c.gridwidth = 2;
         c.anchor = 10;
         c.fill = 0;
         c.insets = new Insets(10, 5, 10, 5);
         this.lStatusMessage = new JLabel("Posting your comment...", new ImageIcon(MyExperimentPerspective.getLocalResourceURL("spinner")), 0);
         contentPane.add(this.lStatusMessage, c);
         this.setDefaultCloseOperation(0);
         this.pack();
         this.validate();
         this.repaint();
         (new Thread("Posting comment") {
            public void run() {
               final ServerResponse response = AddCommentDialog.this.myExperimentClient.postComment(AddCommentDialog.this.resource, Util.stripAllHTML(AddCommentDialog.this.strComment));
               AddCommentDialog.this.bPostingSuccessful = response.getResponseCode() == 200;
               SwingUtilities.invokeLater(new Runnable() {
                  public void run() {
                     if (AddCommentDialog.this.bPostingSuccessful) {
                        AddCommentDialog.this.setDefaultCloseOperation(2);
                        AddCommentDialog.this.taComment.setEnabled(false);
                        contentPane.remove(AddCommentDialog.this.lStatusMessage);
                        c.insets = new Insets(10, 5, 5, 5);
                        AddCommentDialog.this.lStatusMessage = new JLabel("Your comment was posted successfully", new ImageIcon(MyExperimentPerspective.getLocalResourceURL("success_icon")), 2);
                        contentPane.add(AddCommentDialog.this.lStatusMessage, c);
                        AddCommentDialog.this.bCancel.setText("OK");
                        AddCommentDialog.this.bCancel.setDefaultCapable(true);
                        rootPane.setDefaultButton(AddCommentDialog.this.bCancel);
                        c.insets = new Insets(5, 5, 10, 5);
                        ++c.gridy;
                        contentPane.add(AddCommentDialog.this.bCancel, c);
                        AddCommentDialog.this.pack();
                        AddCommentDialog.this.bCancel.requestFocusInWindow();
                     } else {
                        AddCommentDialog.this.setDefaultCloseOperation(2);
                        AddCommentDialog.this.taComment.setEditable(true);
                        contentPane.remove(AddCommentDialog.this.lStatusMessage);
                        c.insets = new Insets(10, 5, 5, 5);
                        AddCommentDialog.this.lStatusMessage = new JLabel("Error occurred while posting comment: " + Util.retrieveReasonFromErrorXMLDocument(response.getResponseBody()), new ImageIcon(MyExperimentPerspective.getLocalResourceURL("failure_icon")), 2);
                        contentPane.add(AddCommentDialog.this.lStatusMessage, c);
                        AddCommentDialog.this.bPost.setText("Try again");
                        AddCommentDialog.this.bPost.setToolTipText("Please review your comment before trying to post it again");
                        c.anchor = 13;
                        c.insets = new Insets(5, 5, 10, 5);
                        c.gridwidth = 1;
                        c.weightx = (double)0.5F;
                        c.gridx = 0;
                        ++c.gridy;
                        contentPane.add(AddCommentDialog.this.bPost, c);
                        rootPane.setDefaultButton(AddCommentDialog.this.bPost);
                        c.anchor = 17;
                        c.gridx = 1;
                        AddCommentDialog.this.bCancel.setPreferredSize(AddCommentDialog.this.bPost.getPreferredSize());
                        contentPane.add(AddCommentDialog.this.bCancel, c);
                        AddCommentDialog.this.pack();
                        AddCommentDialog.this.validate();
                        AddCommentDialog.this.repaint();
                     }

                  }
               });
            }
         }).start();
      } else if (e.getSource().equals(this.bCancel)) {
         if (!this.bPostingSuccessful) {
            this.strComment = null;
         }

         this.dispose();
      }

   }

   public void keyPressed(KeyEvent e) {
      if (e.getSource().equals(this.taComment) && e.getKeyCode() == 9) {
         if ((e.getModifiersEx() & 64) == 64) {
            ((Component)e.getSource()).transferFocusBackward();
         } else {
            ((Component)e.getSource()).transferFocus();
         }

         e.consume();
      }

   }

   public void keyReleased(KeyEvent e) {
   }

   public void keyTyped(KeyEvent e) {
   }

   public void caretUpdate(CaretEvent e) {
      this.bPost.setEnabled(this.taComment.getText().length() > 0);
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
