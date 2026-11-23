package org.apache.taverna.ui.perspectives.myexperiment;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.net.URI;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;
import org.apache.log4j.Logger;
import org.apache.taverna.ui.perspectives.myexperiment.model.MyExperimentClient;
import org.apache.taverna.ui.perspectives.myexperiment.model.Resource;

public class ResourceListPanel extends JPanel implements HyperlinkListener {
   public static final int DESCRIPTION_TRUNCATE_LENGTH_FOR_SHORT_LIST_VIEW = 150;
   public static final int THUMBNAIL_WIDTH_FOR_SHORT_LIST_VIEW = 60;
   public static final int THUMBNAIL_HEIGHT_FOR_SHORT_LIST_VIEW = 45;
   public static final int THUMBNAIL_WIDTH_FOR_FULL_LIST_VIEW = 90;
   public static final int THUMBNAIL_HEIGHT_FOR_FULL_LIST_VIEW = 90;
   private MainComponent pluginMainComponent;
   private MyExperimentClient myExperimentClient;
   private Logger logger;
   private JPanel listPanel;
   private GridBagConstraints gbConstraints;
   private List<Resource> listItems;
   private boolean bFullSizeItemsList = true;

   public ResourceListPanel(MainComponent component, MyExperimentClient client, Logger logger) {
      this.pluginMainComponent = component;
      this.myExperimentClient = client;
      this.logger = logger;
      this.initialiseUI();
   }

   public boolean isFullSizeItemsList() {
      return this.bFullSizeItemsList;
   }

   public void setFullSizeItemsList(boolean bFullSizeItemsList) {
      this.bFullSizeItemsList = bFullSizeItemsList;
   }

   public void hyperlinkUpdate(HyperlinkEvent e) {
      try {
         if (e.getEventType() == EventType.ACTIVATED) {
            String strAction = e.getDescription().toString();
            if (strAction.startsWith("preview:")) {
               this.pluginMainComponent.getPreviewBrowser().preview(strAction);
            } else {
               Desktop.getDesktop().browse(new URI(strAction));
            }
         }
      } catch (Exception ex) {
         this.logger.error("Error occurred whilst clicking a hyperlink", ex);
      }

   }

   public void setListItems(List<Resource> items) {
      this.listItems = items;
      this.repopulate();
   }

   public void clear() {
      this.listPanel.removeAll();
      this.invalidate();
   }

   public void refresh() {
      if (this.listItems != null) {
         this.repopulate();
      }

   }

   public void repopulate() {
      if (this.listItems != null) {
         this.clear();
         if (this.listItems.size() > 0) {
            Resource res = null;

            for(int i = 0; i < this.listItems.size(); ++i) {
               try {
                  if (i == this.listItems.size() - 1) {
                     this.gbConstraints.weighty = (double)1.0F;
                  }

                  res = (Resource)this.listItems.get(i);
                  this.listPanel.add(res.createListViewPanel(this.bFullSizeItemsList, this.pluginMainComponent, this, this.logger), this.gbConstraints);
                  this.logger.debug("Added entry in resource list panel for the resource (Type: " + res.getItemTypeName() + ", URI: " + res.getURI() + ")");
               } catch (Exception e) {
                  this.logger.error("Failed to add item entry to ResourceListPanel (Item Type : " + res.getItemTypeName() + ", URI: " + res.getURI() + ")", e);
               }
            }
         } else {
            JLabel lNone = new JLabel("None");
            lNone.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
            lNone.setForeground(Color.GRAY);
            lNone.setFont(lNone.getFont().deriveFont(2));
            this.gbConstraints.anchor = 17;
            this.listPanel.add(lNone, this.gbConstraints);
            this.listPanel.setPreferredSize(new Dimension(20, 40));
            this.listPanel.setBackground(Color.WHITE);
         }
      }

      this.validate();
      this.repaint();
   }

   private void initialiseUI() {
      this.listPanel = new JPanel();
      this.listPanel.setBorder(BorderFactory.createEmptyBorder());
      this.listPanel.setLayout(new GridBagLayout());
      this.gbConstraints = new GridBagConstraints();
      this.gbConstraints.anchor = 11;
      this.gbConstraints.fill = 2;
      this.gbConstraints.gridx = 0;
      this.gbConstraints.gridy = -1;
      this.gbConstraints.weightx = (double)1.0F;
      this.gbConstraints.weighty = (double)0.0F;
      this.setLayout(new BorderLayout());
      this.add(this.listPanel, "Center");
   }
}
