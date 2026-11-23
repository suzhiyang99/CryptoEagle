package org.apache.taverna.workbench.myexperiment.config;

import java.awt.Dimension;
import javax.swing.JFrame;

public class TestJFrameForPreferencesLocalLaunch {
   public static void main(String[] args) {
      JFrame frame = new JFrame("myExperiment Preferences Test");
      frame.setDefaultCloseOperation(3);
      frame.setMinimumSize(new Dimension(500, 300));
      frame.setLocation(300, 150);
      frame.getContentPane().add(new MyExperimentConfigurationPanel());
      frame.pack();
      frame.setVisible(true);
   }
}
