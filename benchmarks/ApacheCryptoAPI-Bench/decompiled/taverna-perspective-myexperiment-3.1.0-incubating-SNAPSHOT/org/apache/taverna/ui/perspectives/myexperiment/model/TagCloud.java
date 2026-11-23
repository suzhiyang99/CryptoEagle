package org.apache.taverna.ui.perspectives.myexperiment.model;

import java.util.ArrayList;
import java.util.List;

public class TagCloud {
   private List<Tag> tags = new ArrayList();

   public List<Tag> getTags() {
      return this.tags;
   }

   public void clear() {
      this.tags.clear();
   }

   public void addAll(List<Tag> newTags) {
      this.tags.addAll(newTags);
   }

   public int getMaxTagCount() {
      int iMax = 0;

      for(Tag t : this.tags) {
         if (t.getCount() > iMax) {
            iMax = t.getCount();
         }
      }

      return iMax;
   }
}
