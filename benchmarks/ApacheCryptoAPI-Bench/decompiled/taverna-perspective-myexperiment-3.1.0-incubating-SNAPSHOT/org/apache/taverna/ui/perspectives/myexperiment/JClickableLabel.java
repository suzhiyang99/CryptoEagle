package org.apache.taverna.ui.perspectives.myexperiment;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.EventListener;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;

public class JClickableLabel extends JLabel implements MouseListener {
   private String strData;
   private ActionListener clickHandler;

   public JClickableLabel(String strLabel, String strDataForAction, EventListener eventHandler) {
      this(strLabel, strDataForAction, eventHandler, (Icon)null);
   }

   public JClickableLabel(String strLabel, String strDataForAction, EventListener eventHandler, Icon icon) {
      this(strLabel, strDataForAction, eventHandler, icon, 2);
   }

   public JClickableLabel(String strLabel, String strDataForAction, EventListener eventHandler, Icon icon, int horizontalAlignment) {
      this(strLabel, strDataForAction, eventHandler, icon, horizontalAlignment, (String)null);
   }

   public JClickableLabel(String strLabel, String strDataForAction, EventListener eventHandler, Icon icon, int horizontalAlignment, String strTooltip) {
      super(strLabel, icon, horizontalAlignment);
      this.strData = strDataForAction;
      this.clickHandler = (ActionListener)eventHandler;
      if (icon != null) {
         this.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
      }

      this.setToolTipText(strTooltip == null ? strLabel : strTooltip);
      this.setForeground(Color.BLUE);
      this.addMouseListener(this);
   }

   public void mouseClicked(MouseEvent e) {
      this.clickHandler.actionPerformed(new ActionEvent(this, e.getID(), this.strData));
   }

   public void mouseEntered(MouseEvent e) {
      this.setCursor(Cursor.getPredefinedCursor(12));
   }

   public void mouseExited(MouseEvent e) {
      this.setCursor(Cursor.getPredefinedCursor(0));
   }

   public void mousePressed(MouseEvent e) {
   }

   public void mouseReleased(MouseEvent e) {
   }
}
