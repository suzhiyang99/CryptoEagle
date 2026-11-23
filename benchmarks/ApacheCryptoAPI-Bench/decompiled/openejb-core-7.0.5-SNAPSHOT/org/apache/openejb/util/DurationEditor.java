package org.apache.openejb.util;

import java.beans.PropertyEditorSupport;

public class DurationEditor extends PropertyEditorSupport {
   public void setAsText(String text) {
      Duration d = Duration.parse(text);
      this.setValue(d);
   }
}
