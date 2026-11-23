package org.apache.taverna.ui.perspectives.myexperiment.model;

import java.io.Serializable;
import java.util.Comparator;

public class Tag extends Resource implements Serializable {
   private String tagName;
   private int count;

   public Tag() {
      this.setItemType(30);
   }

   public String getTagName() {
      return this.tagName;
   }

   public void setTagName(String tagName) {
      this.tagName = tagName;
   }

   public int getCount() {
      return this.count;
   }

   public void setCount(int count) {
      this.count = count;
   }

   public boolean equals(Object other) {
      if (!(other instanceof Tag)) {
         return false;
      } else {
         Tag otherTag = (Tag)other;
         return this.count == otherTag.count && this.tagName.equals(otherTag.tagName);
      }
   }

   public String toString() {
      return "Tag (" + this.tagName + ", " + this.count + ")";
   }

   public static String getRequiredAPIElements(int iRequestType) {
      String strElements = "";
      switch (iRequestType) {
         case 5100:
            strElements = strElements + "";
         default:
            return strElements;
      }
   }

   public static Tag instantiateTagFromActionCommand(String strActionCommand) {
      if (!strActionCommand.startsWith("tag:")) {
         return null;
      } else {
         Tag t = new Tag();
         t.setTagName(strActionCommand.replaceFirst("tag:", ""));
         return t;
      }
   }

   public static class ReversePopularityComparator implements Comparator<Tag> {
      public int compare(Tag t1, Tag t2) {
         return t1.getCount() == t2.getCount() ? t1.getTagName().compareTo(t2.getTagName()) : t2.getCount() - t1.getCount();
      }
   }

   public static class AlphanumericComparator implements Comparator<Tag> {
      public int compare(Tag t1, Tag t2) {
         return t1.getTagName().compareTo(t2.getTagName());
      }
   }
}
