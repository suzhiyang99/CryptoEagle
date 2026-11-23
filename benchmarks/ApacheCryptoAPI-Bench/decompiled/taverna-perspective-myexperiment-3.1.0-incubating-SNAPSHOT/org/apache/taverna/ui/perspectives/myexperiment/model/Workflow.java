package org.apache.taverna.ui.perspectives.myexperiment.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

public class Workflow extends Resource {
   public static final String MIME_TYPE_TAVERNA_1 = "application/vnd.taverna.scufl+xml";
   public static final String MIME_TYPE_TAVERNA_2 = "application/vnd.taverna.t2flow+xml";
   private int accessType;
   private int version;
   private User uploader;
   private URI preview;
   private URI thumbnail;
   private URI thumbnailBig;
   private URI svg;
   private License license;
   private String visibleType;
   private String contentType;
   private URI contentUri;
   byte[] content;
   private List<Tag> tags;
   private List<Comment> comments;
   private List<Resource> credits;
   private List<Resource> attributions;
   private HashMap<String, ArrayList<HashMap<String, String>>> components;

   public Workflow() {
      this.setItemType(10);
   }

   public int getAccessType() {
      return this.accessType;
   }

   public void setAccessType(int accessType) {
      this.accessType = accessType;
   }

   public int getVersion() {
      return this.version;
   }

   public void setVersion(int version) {
      this.version = version;
   }

   public User getUploader() {
      return this.uploader;
   }

   public void setUploader(User uploader) {
      this.uploader = uploader;
   }

   public URI getPreview() {
      return this.preview;
   }

   public void setPreview(URI preview) {
      this.preview = preview;
   }

   public URI getThumbnail() {
      return this.thumbnail;
   }

   public void setThumbnail(URI thumbnail) {
      this.thumbnail = thumbnail;
   }

   public URI getSvg() {
      return this.svg;
   }

   public void setSvg(URI svg) {
      this.svg = svg;
   }

   public License getLicense() {
      return this.license;
   }

   public void setLicense(License license) {
      this.license = license;
   }

   public URI getContentUri() {
      return this.contentUri;
   }

   public void setContentUri(URI contentUri) {
      this.contentUri = contentUri;
   }

   public String getVisibleType() {
      return this.visibleType;
   }

   public void setVisibleType(String visibleType) {
      this.visibleType = visibleType;
   }

   public String getContentType() {
      return this.contentType;
   }

   public void setContentType(String contentType) {
      this.contentType = contentType;
   }

   public byte[] getContent() {
      return this.content;
   }

   public void setContent(byte[] content) {
      this.content = content;
   }

   public List<Tag> getTags() {
      return this.tags;
   }

   public List<Comment> getComments() {
      return this.comments;
   }

   public List<Resource> getCredits() {
      return this.credits;
   }

   public List<Resource> getAttributions() {
      return this.attributions;
   }

   public HashMap<String, ArrayList<HashMap<String, String>>> getComponents() {
      return this.components;
   }

   public URI getThumbnailBig() {
      return this.thumbnailBig;
   }

   public void setThumbnailBig(URI thumbnailBig) {
      this.thumbnailBig = thumbnailBig;
   }

   public boolean isTavernaWorkflow() {
      return this.contentType.equals("application/vnd.taverna.scufl+xml") || this.contentType.equals("application/vnd.taverna.t2flow+xml");
   }

   public boolean isTaverna1Workflow() {
      return this.contentType.equals("application/vnd.taverna.scufl+xml");
   }

   public boolean isTaverna2Workflow() {
      return this.contentType.equals("application/vnd.taverna.t2flow+xml");
   }

   public static String getRequiredAPIElements(int iRequestType) {
      String strElements = "";
      switch (iRequestType) {
         case 5005:
            strElements = strElements + "created-at,updated-at,preview,thumbnail-big,svg,license-type,content-uri,tags,comments,ratings,credits,attributions,components,";
         case 5010:
            strElements = strElements + "uploader,type,";
         case 5015:
            strElements = strElements + "id,title,thumbnail,description,privileges,content-type";
            break;
         case 5055:
            strElements = strElements + "type,content-type,content";
      }

      return strElements;
   }

   public static Workflow buildFromXML(Document doc, Logger logger) {
      return doc == null ? null : buildFromXML(doc.getRootElement(), logger);
   }

   public static Workflow buildFromXML(Element docRootElement, Logger logger) {
      if (docRootElement == null) {
         return null;
      } else {
         Workflow w = new Workflow();

         try {
            w.setAccessType(Util.getAccessTypeFromXMLElement(docRootElement.getChild("privileges")));
            w.setURI(docRootElement.getAttributeValue("uri"));
            w.setResource(docRootElement.getAttributeValue("resource"));
            String version = docRootElement.getAttributeValue("version");
            if (version != null && !version.equals("")) {
               w.setVersion(Integer.parseInt(version));
            }

            String id = docRootElement.getChildText("id");
            if (id == null || id.equals("")) {
               id = "API Error - No workflow ID supplied";
               logger.error("Error while parsing workflow XML data - no ID provided for workflow with title: \"" + docRootElement.getChildText("title") + "\"");
            }

            w.setID(id);
            w.setTitle(docRootElement.getChildText("title"));
            w.setDescription(docRootElement.getChildText("description"));
            Element uploaderElement = docRootElement.getChild("uploader");
            w.setUploader(Util.instantiatePrimitiveUserFromElement(uploaderElement));
            String createdAt = docRootElement.getChildText("created-at");
            if (createdAt != null && !createdAt.equals("")) {
               w.setCreatedAt(MyExperimentClient.parseDate(createdAt));
            }

            String updatedAt = docRootElement.getChildText("updated-at");
            if (updatedAt != null && !updatedAt.equals("")) {
               w.setUpdatedAt(MyExperimentClient.parseDate(updatedAt));
            }

            String preview = docRootElement.getChildText("preview");
            if (preview != null && !preview.equals("")) {
               w.setPreview(new URI(preview));
            }

            String thumbnail = docRootElement.getChildText("thumbnail");
            if (thumbnail != null && !thumbnail.equals("")) {
               w.setThumbnail(new URI(thumbnail));
            }

            String thumbnailBig = docRootElement.getChildText("thumbnail-big");
            if (thumbnailBig != null && !thumbnailBig.equals("")) {
               w.setThumbnailBig(new URI(thumbnailBig));
            }

            String svg = docRootElement.getChildText("svg");
            if (svg != null && !svg.equals("")) {
               w.setSvg(new URI(svg));
            }

            w.setLicense(License.getInstance(docRootElement.getChildText("license-type")));
            String contentUri = docRootElement.getChildText("content-uri");
            if (contentUri != null && !contentUri.equals("")) {
               w.setContentUri(new URI(contentUri));
            }

            w.setVisibleType(docRootElement.getChildText("type"));
            w.setContentType(docRootElement.getChildText("content-type"));
            w.tags = new ArrayList();
            w.tags.addAll(Util.retrieveTags(docRootElement));
            w.comments = new ArrayList();
            w.getComments().addAll(Util.retrieveComments(docRootElement, w));
            w.credits = new ArrayList();
            w.getCredits().addAll(Util.retrieveCredits(docRootElement));
            w.attributions = new ArrayList();
            w.getAttributions().addAll(Util.retrieveAttributions(docRootElement));
            w.components = new HashMap();
            Element componentsElement = docRootElement.getChild("components");
            if (componentsElement != null) {
               Element sourcesElement = componentsElement.getChild("sources");
               if (sourcesElement != null) {
                  ArrayList<HashMap<String, String>> inputs = new ArrayList();

                  for(Element e : sourcesElement.getChildren()) {
                     HashMap<String, String> curInput = new HashMap();
                     curInput.put("name", e.getChildText("name"));
                     curInput.put("description", e.getChildText("description"));
                     inputs.add(curInput);
                  }

                  w.getComponents().put("inputs", inputs);
               }

               Element outputsElement = componentsElement.getChild("sinks");
               if (outputsElement != null) {
                  ArrayList<HashMap<String, String>> sinks = new ArrayList();

                  for(Element e : outputsElement.getChildren()) {
                     HashMap<String, String> curOutput = new HashMap();
                     curOutput.put("name", e.getChildText("name"));
                     curOutput.put("description", e.getChildText("description"));
                     sinks.add(curOutput);
                  }

                  w.getComponents().put("outputs", sinks);
               }

               Element processorsElement = componentsElement.getChild("processors");
               if (processorsElement != null) {
                  ArrayList<HashMap<String, String>> processors = new ArrayList();

                  for(Element e : processorsElement.getChildren()) {
                     HashMap<String, String> curProcessor = new HashMap();
                     curProcessor.put("name", e.getChildText("name"));
                     curProcessor.put("type", e.getChildText("type"));
                     curProcessor.put("description", e.getChildText("description"));
                     processors.add(curProcessor);
                  }

                  w.getComponents().put("processors", processors);
               }

               Element linksElement = componentsElement.getChild("links");
               if (linksElement != null) {
                  ArrayList<HashMap<String, String>> links = new ArrayList();

                  for(Element e : linksElement.getChildren()) {
                     HashMap<String, String> curLink = new HashMap();
                     String strSource = e.getChild("source").getChildText("node") + (e.getChild("source").getChildText("port") == null ? "" : ":" + e.getChild("source").getChildText("port"));
                     curLink.put("source", strSource);
                     String strSink = e.getChild("sink").getChildText("node") + (e.getChild("sink").getChildText("port") == null ? "" : ":" + e.getChild("sink").getChildText("port"));
                     curLink.put("sink", strSink);
                     links.add(curLink);
                  }

                  w.getComponents().put("links", links);
               }
            }

            logger.debug("Found information for worklow with ID: " + w.getID() + ", Title: " + w.getTitle());
         } catch (Exception e) {
            logger.error("Failed midway through creating workflow object from XML", e);
         }

         return w;
      }
   }
}
