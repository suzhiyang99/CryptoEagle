package org.apache.taverna.ui.perspectives.myexperiment;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import org.apache.log4j.Logger;
import org.apache.taverna.lang.ui.ShadedLabel;
import org.apache.taverna.ui.perspectives.myexperiment.model.MyExperimentClient;
import org.apache.taverna.ui.perspectives.myexperiment.model.Tag;
import org.apache.taverna.ui.perspectives.myexperiment.model.TagCloud;
import org.apache.taverna.workbench.icons.WorkbenchIcons;

public class TagCloudPanel extends JPanel implements ChangeListener, ItemListener, ActionListener, HyperlinkListener {
   private static final int TAGCLOUD_MAX_FONTSIZE = 36;
   private static final int TAGCLOUD_MIN_FONTSIZE = 12;
   private static final int TAGCLOUD_DEFAULT_MAX_SIZE = 350;
   private static final int TAGCLOUD_DEFAULT_DISPLAY_SIZE = 50;
   public static final int TAGCLOUD_TYPE_GENERAL = 0;
   public static final int TAGCLOUD_TYPE_USER = 1;
   public static final int TAGCLOUD_TYPE_RESOURCE_PREVIEW = 2;
   private MainComponent pluginMainComponent;
   private MyExperimentClient myExperimentClient;
   private Logger logger;
   private String strTitle;
   private int iType;
   private ShadedLabel lCloudTitle;
   private JSlider jsCloudSizeSlider;
   private JCheckBox cbShowAllTags;
   private JButton bRefresh;
   private JTextPane tpTagCloudBody;
   private ActionListener clickHandler;
   private TagCloud tcData = new TagCloud();
   private boolean bUserTagCloudSliderValueNeverSet = true;

   public TagCloudPanel(String title, int iTagCloudType, ActionListener clickHandler, MainComponent component, MyExperimentClient client, Logger logger) {
      this.strTitle = title;
      this.iType = iTagCloudType;
      this.clickHandler = clickHandler;
      this.pluginMainComponent = component;
      this.myExperimentClient = client;
      this.logger = logger;
      this.initialiseUI();
   }

   private void initialiseUI() {
      this.lCloudTitle = new ShadedLabel(this.strTitle, ShadedLabel.BLUE);
      JPanel jpCloudControlsPanel = new JPanel();
      jpCloudControlsPanel.setLayout(new BoxLayout(jpCloudControlsPanel, 2));
      this.jsCloudSizeSlider = new JSlider(1, 350, 50);
      this.cbShowAllTags = new JCheckBox("All tags", false);
      this.bRefresh = new JButton("Refresh", WorkbenchIcons.refreshIcon);
      if (this.iType != 2) {
         this.jsCloudSizeSlider.addChangeListener(this);
         this.jsCloudSizeSlider.setToolTipText("Drag the slider to select how big the tag cloud should be, or check the \"All tags\" box to get the full tag cloud.");
         jpCloudControlsPanel.add(this.jsCloudSizeSlider);
         this.cbShowAllTags.addItemListener(this);
         jpCloudControlsPanel.add(this.cbShowAllTags);
         this.bRefresh.addActionListener(this);
         this.bRefresh.setToolTipText("Click this button to refresh the Tag Cloud");
         jpCloudControlsPanel.add(this.bRefresh);
      }

      JPanel jpCloudHeader = new JPanel(new BorderLayout());
      jpCloudHeader.add(jpCloudControlsPanel, "Center");
      jpCloudHeader.setBorder(BorderFactory.createEtchedBorder());
      this.tpTagCloudBody = new JTextPane();
      this.tpTagCloudBody.setBorder(BorderFactory.createEmptyBorder());
      this.tpTagCloudBody.setEditable(false);
      this.tpTagCloudBody.setContentType("text/html");
      this.tpTagCloudBody.addHyperlinkListener(this);
      JScrollPane spTagCloudBody = new JScrollPane(this.tpTagCloudBody, 20, 30);
      spTagCloudBody.setBorder(BorderFactory.createEmptyBorder());
      spTagCloudBody.setOpaque(true);
      JPanel jpTagCloudContentWithControls = new JPanel();
      jpTagCloudContentWithControls.setLayout(new BorderLayout());
      jpTagCloudContentWithControls.add(spTagCloudBody, "Center");
      if (this.iType != 2) {
         jpTagCloudContentWithControls.add(jpCloudHeader, "North");
      }

      this.setLayout(new BorderLayout());
      if (this.iType != 2) {
         this.add(this.lCloudTitle, "North");
      }

      this.add(jpTagCloudContentWithControls, "Center");
      this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2), BorderFactory.createEtchedBorder()));
   }

   public void refresh() {
      this.lCloudTitle.setText(this.strTitle + " <span style='color: gray;'>(Loading...)</span>");
      (new Thread("Get '" + this.strTitle + "' tag cloud data") {
         public void run() {
            TagCloudPanel.this.logger.debug("Getting '" + TagCloudPanel.this.strTitle + "' tag cloud data");

            try {
               int size = -1;
               if (!TagCloudPanel.this.cbShowAllTags.isSelected()) {
                  size = TagCloudPanel.this.jsCloudSizeSlider.getValue();
               }

               switch (TagCloudPanel.this.iType) {
                  case 0:
                     TagCloudPanel.this.tcData = TagCloudPanel.this.myExperimentClient.getGeneralTagCloud(size);
                     break;
                  case 1:
                     TagCloudPanel.this.tcData = TagCloudPanel.this.myExperimentClient.getUserTagCloud(TagCloudPanel.this.myExperimentClient.getCurrentUser(), size);
                     break;
                  case 2:
                     TagCloudPanel.this.myExperimentClient.convertTagListIntoTagCloudData(TagCloudPanel.this.tcData.getTags());
                     break;
                  default:
                     TagCloudPanel.this.tcData = new TagCloud();
               }

               SwingUtilities.invokeLater(new Runnable() {
                  public void run() {
                     TagCloudPanel.this.repopulate();
                  }
               });
            } catch (Exception ex) {
               TagCloudPanel.this.logger.error("Failed to get tag cloud data from myExperiment", ex);
            }

         }
      }).start();
   }

   public void repopulate() {
      this.logger.debug("Building '" + this.strTitle + "' tag cloud...");

      try {
         this.jsCloudSizeSlider.removeChangeListener(this);
         if (this.iType == 1) {
            this.jsCloudSizeSlider.setMinimum(1);
            this.jsCloudSizeSlider.setMaximum(this.myExperimentClient.getCurrentUser().getTags().size());
            if (this.bUserTagCloudSliderValueNeverSet) {
               this.jsCloudSizeSlider.setValue(this.jsCloudSizeSlider.getMaximum());
               this.bUserTagCloudSliderValueNeverSet = false;
            } else if (this.jsCloudSizeSlider.getValue() > this.jsCloudSizeSlider.getMaximum() || this.cbShowAllTags.isSelected()) {
               this.jsCloudSizeSlider.setValue(this.jsCloudSizeSlider.getMaximum());
            }
         } else if (this.cbShowAllTags.isSelected()) {
            int size = this.tcData.getTags().size();
            this.jsCloudSizeSlider.setMaximum(size);
            this.jsCloudSizeSlider.setValue(size);
         }

         this.jsCloudSizeSlider.addChangeListener(this);
         int iMaxCount = this.tcData.getMaxTagCount();
         StringBuffer content = new StringBuffer();
         if (this.tcData.getTags().size() <= 0) {
            content.append("<br>");
            content.append("<span style='color: gray; font-weight: italic;'>&nbsp;&nbsp;No tags to display</span>");
         } else {
            content.append("<div class='outer'>");
            content.append("<br>");
            content.append("<div class='tag_cloud'>");

            for(Tag t : this.tcData.getTags()) {
               int fontSize = (int)((double)t.getCount() / ((double)iMaxCount / (double)3.0F) * (double)36.0F);
               if (fontSize < 12) {
                  fontSize = 12;
               }

               if (fontSize > 36) {
                  fontSize = 36;
               }

               content.append("<a style='font-size: " + fontSize + "pt;' href='tag:" + t.getTagName() + "'>" + t.getTagName() + "</a>");
               content.append("&nbsp;&nbsp;&nbsp;");
            }

            content.append("<br>");
            content.append("</div>");
            content.append("</div>");
         }

         HTMLEditorKit kit = new StyledHTMLEditorKit(this.pluginMainComponent.getStyleSheet());
         HTMLDocument doc = (HTMLDocument)kit.createDefaultDocument();
         doc.insertAfterStart(doc.getRootElements()[0].getElement(0), content.toString());
         this.tpTagCloudBody.setEditorKit(kit);
         this.tpTagCloudBody.setDocument(doc);
      } catch (Exception e) {
         this.logger.error("Failed to populate tag cloud", e);
      }

      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            TagCloudPanel.this.lCloudTitle.setText(TagCloudPanel.this.strTitle + " <span style='color: gray;'>(currently showing " + TagCloudPanel.this.tcData.getTags().size() + ")</span>");
            TagCloudPanel.this.revalidate();
         }
      });
   }

   public TagCloud getTagCloudData() {
      return this.tcData;
   }

   public void stateChanged(ChangeEvent e) {
      if (e.getSource().equals(this.jsCloudSizeSlider) && !this.jsCloudSizeSlider.getValueIsAdjusting()) {
         this.cbShowAllTags.removeItemListener(this);
         this.cbShowAllTags.setSelected(false);
         this.cbShowAllTags.addItemListener(this);
         this.refresh();
      }

   }

   public void itemStateChanged(ItemEvent e) {
      if (e.getItemSelectable().equals(this.cbShowAllTags)) {
         this.refresh();
      }

   }

   public void actionPerformed(ActionEvent e) {
      if (e.getSource().equals(this.bRefresh)) {
         this.refresh();
      }

   }

   public void hyperlinkUpdate(HyperlinkEvent e) {
      if (e.getSource().equals(this.tpTagCloudBody) && e.getEventType() == EventType.ACTIVATED) {
         this.clickHandler.actionPerformed(new ActionEvent(this, (this.getClass().getName() + e.getDescription()).hashCode(), e.getDescription()));
      }

   }
}
