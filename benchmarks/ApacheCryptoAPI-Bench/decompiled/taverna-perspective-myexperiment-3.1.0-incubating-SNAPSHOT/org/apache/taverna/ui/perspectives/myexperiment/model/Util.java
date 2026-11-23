package org.apache.taverna.ui.perspectives.myexperiment.model;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import org.apache.log4j.Logger;
import org.apache.taverna.ui.perspectives.myexperiment.JClickableLabel;
import org.apache.taverna.ui.perspectives.myexperiment.MyExperimentPerspective;
import org.jdom.Document;
import org.jdom.Element;

public class Util {
   private static Logger logger = Logger.getLogger(Util.class);
   private static final String PBE_PASSWORD = System.getProperty("user.home");
   private static final String PBE_SALT;

   public static byte[] encrypt(String str) {
      return doEncryption(str, 1);
   }

   public static byte[] decrypt(String str) {
      return doEncryption(str, 2);
   }

   private static byte[] doEncryption(String str, int mode) {
      try {
         SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
         PBEKeySpec keySpec = new PBEKeySpec(PBE_PASSWORD.toCharArray());
         SecretKey key = keyFactory.generateSecret(keySpec);
         MessageDigest md = MessageDigest.getInstance("MD5");
         md.update(PBE_SALT.getBytes());
         byte[] digest = md.digest();
         byte[] salt = new byte[8];

         for(int i = 0; i < 8; ++i) {
            salt[i] = digest[i];
         }

         PBEParameterSpec paramSpec = new PBEParameterSpec(salt, 20);
         Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
         cipher.init(mode, key, paramSpec);
         byte[] encrypted = cipher.doFinal(str.getBytes());
         return encrypted;
      } catch (Exception e) {
         logger.error("Could not encrypt and store password");
         logger.error(e.getCause() + "\n" + e.getMessage());
         return new byte[1];
      }
   }

   public static ImageIcon getResizedImageIcon(ImageIcon sourceImageIcon, int iRequiredWidth, int iRequiredHeight) {
      int iWidth = sourceImageIcon.getIconWidth();
      int iHeight = sourceImageIcon.getIconHeight();
      float fWidthResizeRatio = (float)iWidth / (float)iRequiredWidth;
      float fHeightResizeRatio = (float)iHeight / (float)iRequiredHeight;
      float fResizeRatio = Math.max(fWidthResizeRatio, fHeightResizeRatio);
      int iNewWidth = Math.round((float)iWidth / fResizeRatio);
      int iNewHeight = Math.round((float)iHeight / fResizeRatio);
      BufferedImage resizedImage = new BufferedImage(iNewWidth, iNewHeight, 1);
      Graphics2D g2 = resizedImage.createGraphics();
      g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      g2.drawImage(sourceImageIcon.getImage(), 0, 0, iNewWidth, iNewHeight, (ImageObserver)null);
      g2.dispose();
      return new ImageIcon(resizedImage, "");
   }

   public static String getResizedImageIconTempFileURL(URL sourceImageURL, int iRequiredWidth, int iRequiredHeight) {
      java.io.File fDestinationTempFile = null;

      try {
         fDestinationTempFile = java.io.File.createTempFile("resized_image", "jpg");
         fDestinationTempFile.deleteOnExit();
         ImageIcon sourceImageIcon = new ImageIcon(sourceImageURL);
         ImageIcon resizedImageIcon = getResizedImageIcon(sourceImageIcon, iRequiredWidth, iRequiredHeight);
         Image img = resizedImageIcon.getImage();
         BufferedImage bi = new BufferedImage(img.getWidth((ImageObserver)null), img.getHeight((ImageObserver)null), 1);
         Graphics2D g2 = bi.createGraphics();
         g2.drawImage(img, 0, 0, (ImageObserver)null);
         g2.dispose();
         ImageIO.write(bi, "jpg", fDestinationTempFile);
      } catch (Exception var9) {
      }

      return "file:///" + fDestinationTempFile.getAbsolutePath();
   }

   public static Resource instantiatePrimitiveResourceFromElement(Element e) {
      if (e != null) {
         Resource r = new Resource();
         r.setItemType(e.getName());
         r.setTitle(e.getText());
         r.setURI(e.getAttributeValue("uri"));
         r.setResource(e.getAttributeValue("resource"));
         return r;
      } else {
         return null;
      }
   }

   public static User instantiatePrimitiveUserFromElement(Element e) {
      if (e != null) {
         User u = new User();
         u.setName(e.getText());
         u.setTitle(e.getText());
         u.setURI(e.getAttributeValue("uri"));
         u.setResource(e.getAttributeValue("resource"));
         return u;
      } else {
         return null;
      }
   }

   public static Tag instantiatePrimitiveTagFromElement(Element e) {
      if (e != null) {
         Tag t = new Tag();
         t.setTitle(e.getText());
         t.setTagName(e.getText());
         t.setResource(e.getAttributeValue("resource"));
         t.setURI(e.getAttributeValue("uri"));
         return t;
      } else {
         return null;
      }
   }

   public static Comment instantiatePrimitiveCommentFromElement(Element e, Resource r) {
      if (e != null) {
         Comment c = new Comment();
         c.setTitle(e.getText());
         c.setComment(e.getText());
         c.setTypeOfCommentedResource(r.getItemType());
         c.setURIOfCommentedResource(r.getURI());
         c.setResource(e.getAttributeValue("resource"));
         c.setURI(e.getAttributeValue("uri"));
         return c;
      } else {
         return null;
      }
   }

   public static int getResourceCollectionFromXMLIterator(Iterator<Element> iterator, ArrayList<HashMap<String, String>> collection) {
      int iCount;
      for(iCount = 0; iterator.hasNext(); ++iCount) {
         Element element = (Element)iterator.next();
         HashMap<String, String> itemDetails = new HashMap();
         itemDetails.put("name", element.getText());
         itemDetails.put("uri", element.getAttributeValue("uri"));
         itemDetails.put("resource", element.getAttributeValue("resource"));
         collection.add(itemDetails);
      }

      return iCount;
   }

   public static int getAccessTypeFromXMLElement(Element privileges) {
      int iAccessType = 1000;

      for(Element privilege : privileges.getChildren("privilege")) {
         String strValue = privilege.getAttributeValue("type");
         int iCurrentPrivilege = 1000;
         if (strValue.equals("download")) {
            iCurrentPrivilege = 1001;
         } else if (strValue.equals("edit")) {
            iCurrentPrivilege = 1002;
         }

         if (iCurrentPrivilege > iAccessType) {
            iAccessType = iCurrentPrivilege;
         }
      }

      return iAccessType;
   }

   public static String retrieveReasonFromErrorXMLDocument(Document doc) {
      if (doc != null) {
         Element root = doc.getRootElement();
         return root.getChildText("reason");
      } else {
         return "unknown reason";
      }
   }

   public static List<Resource> retrieveUserFavourites(Element docRootElement) {
      List<Resource> favouritedItemList = new ArrayList();
      Element favouritesElement = docRootElement.getChild("favourited");
      if (favouritesElement != null) {
         Iterator<Element> iFavourites = favouritesElement.getChildren().iterator();

         while(iFavourites.hasNext()) {
            favouritedItemList.add(instantiatePrimitiveResourceFromElement((Element)iFavourites.next()));
         }
      }

      return favouritedItemList;
   }

   public static List<Resource> retrieveCredits(Element docRootElement) {
      List<Resource> credits = new ArrayList();
      Element creditsElement = docRootElement.getChild("credits");
      if (creditsElement != null) {
         for(Element e : creditsElement.getChildren()) {
            credits.add(instantiatePrimitiveResourceFromElement(e));
         }
      }

      return credits;
   }

   public static List<Resource> retrieveAttributions(Element docRootElement) {
      List<Resource> attributions = new ArrayList();
      Element attributionsElement = docRootElement.getChild("attributions");
      if (attributionsElement != null) {
         for(Element e : attributionsElement.getChildren()) {
            attributions.add(instantiatePrimitiveResourceFromElement(e));
         }
      }

      return attributions;
   }

   public static List<Tag> retrieveTags(Element docRootElement) {
      List<Tag> tags = new ArrayList();
      Element tagsElement = docRootElement.getChild("tags");
      if (tagsElement != null) {
         for(Element e : tagsElement.getChildren()) {
            tags.add(instantiatePrimitiveTagFromElement(e));
         }
      }

      return tags;
   }

   public static List<Comment> retrieveComments(Element docRootElement, Resource r) {
      List<Comment> comments = new ArrayList();
      Element tagsElement = docRootElement.getChild("comments");
      if (tagsElement != null) {
         for(Element e : tagsElement.getChildren()) {
            comments.add(instantiatePrimitiveCommentFromElement(e, r));
         }
      }

      return comments;
   }

   public static String stripHTML(String source) {
      if (source == null) {
         return "";
      } else {
         source = source.replaceAll("</p>[\r\n]*<p>", "<br>");
         source = source.replaceAll("\\<br/?\\>", "[-=BR=-]");
         source = source.replaceAll("\\<.*?\\>", "");
         source = source.replaceAll("\\[-=BR=-\\]", "<br><br>");
         return source;
      }
   }

   public static String stripAllHTML(String source) {
      if (source == null) {
         return "";
      } else {
         source = source.replaceAll("</p>[\r\n]*<p>", "<br>");
         source = source.replaceAll("\\<br/?\\>", "\n\n");
         source = source.replaceAll("\\<.*?\\>", "");
         source = source.replaceAll("&\\w{1,4};", "");
         return source;
      }
   }

   public static void centerComponentWithinAnother(Component mainComponent, Component dependentComponent) {
      int iMainComponentCenterX = (int)Math.round(mainComponent.getLocationOnScreen().getX() + (double)(mainComponent.getWidth() / 2));
      int iPosX = iMainComponentCenterX - dependentComponent.getWidth() / 2;
      int iMainComponentCenterY = (int)Math.round(mainComponent.getLocationOnScreen().getY() + (double)(mainComponent.getHeight() / 2));
      int iPosY = iMainComponentCenterY - dependentComponent.getHeight() / 2;
      dependentComponent.setLocation(iPosX, iPosY);
   }

   public static JClickableLabel generateClickableLabelFor(Resource r, ActionListener clickHandler) {
      return new JClickableLabel(r.getTitle(), "preview:" + r.getItemType() + ":" + r.getURI(), clickHandler, new ImageIcon(MyExperimentPerspective.getLocalIconURL(r.getItemType())), 2, r.getItemTypeName() + ": " + r.getTitle());
   }

   public static JLabel generateNoneTextLabel(String strLabel) {
      JLabel lNoneText = new JLabel(strLabel);
      lNoneText.setFont(lNoneText.getFont().deriveFont(2));
      lNoneText.setForeground(Color.GRAY);
      lNoneText.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      return lNoneText;
   }

   public static String composeAPIQueryElements(SearchEngine.QuerySearchInstance queryInstance) {
      String strElements = "";
      if (queryInstance == null || queryInstance.getSearchWorkflows()) {
         strElements = strElements + Workflow.getRequiredAPIElements(5010) + ",";
      }

      if (queryInstance == null || queryInstance.getSearchFiles()) {
         strElements = strElements + File.getRequiredAPIElements(5010) + ",";
      }

      if (queryInstance == null || queryInstance.getSearchPacks()) {
         strElements = strElements + Pack.getRequiredAPIElements(5010) + ",";
      }

      if (queryInstance == null || queryInstance.getSearchUsers()) {
         strElements = strElements + User.getRequiredAPIElements(5010) + ",";
      }

      if (queryInstance == null || queryInstance.getSearchGroups()) {
         strElements = strElements + Group.getRequiredAPIElements(5010) + ",";
      }

      TreeSet<String> elementSet = new TreeSet(Arrays.asList(strElements.split(",")));
      Iterator<String> elementSetIterator = elementSet.iterator();
      strElements = "&elements=";

      while(elementSetIterator.hasNext()) {
         strElements = strElements + (String)elementSetIterator.next();
         if (elementSetIterator.hasNext()) {
            strElements = strElements + ",";
         }
      }

      return strElements;
   }

   public static String getBaseClassName(String strClassName) {
      String strResult = strClassName;
      int iDollarIdx = strClassName.indexOf("$");
      if (iDollarIdx != -1) {
         strResult = strClassName.substring(0, iDollarIdx);
      }

      return strResult;
   }

   static {
      String host_name = "";

      try {
         host_name = InetAddress.getLocalHost().toString();
      } catch (UnknownHostException var2) {
         host_name = "unknown_localhost";
      }

      PBE_SALT = host_name;
   }
}
