package org.apache.tika.sax;

class LinkBuilder {
   private final String type;
   private String uri = "";
   private String title = "";
   private String rel = "";
   private final StringBuilder text = new StringBuilder();

   public LinkBuilder(String type) {
      this.type = type;
   }

   public String getType() {
      return this.type;
   }

   public void setURI(String uri) {
      if (uri != null) {
         this.uri = uri;
      } else {
         this.uri = "";
      }

   }

   public void setTitle(String title) {
      if (title != null) {
         this.title = title;
      } else {
         this.title = "";
      }

   }

   public void setRel(String rel) {
      if (rel != null) {
         this.rel = rel;
      } else {
         this.rel = "";
      }

   }

   public void characters(char[] ch, int offset, int length) {
      this.text.append(ch, offset, length);
   }

   public Link getLink() {
      return this.getLink(false);
   }

   public Link getLink(boolean collapseWhitespace) {
      String anchor = this.text.toString();
      if (collapseWhitespace) {
         anchor = anchor.replaceAll("\\s+", " ").trim();
      }

      return new Link(this.type, this.uri, this.title, anchor, this.rel);
   }
}
