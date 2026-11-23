package org.apache.taverna.ui.perspectives.myexperiment.model;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.apache.taverna.configuration.app.ApplicationConfiguration;
import org.apache.taverna.security.credentialmanager.CMException;
import org.apache.taverna.security.credentialmanager.CredentialManager;
import org.apache.taverna.security.credentialmanager.UsernamePassword;
import org.apache.taverna.ui.perspectives.myexperiment.MainComponent;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

public class MyExperimentClient {
   public static final String DEFAULT_BASE_URL = "http://www.myexperiment.org";
   public static final String PLUGIN_USER_AGENT = "Taverna2-myExperiment-plugin/0.2beta Java/" + System.getProperty("java.version");
   private static final String INI_FILE_NAME = "myexperiment-plugin.ini";
   private static final int EXAMPLE_WORKFLOWS_PACK_ID = 254;
   public static final String INI_BASE_URL = "my_experiment_base_url";
   public static final String INI_AUTO_LOGIN = "auto_login";
   public static final String INI_FAVOURITE_SEARCHES = "favourite_searches";
   public static final String INI_SEARCH_HISTORY = "search_history";
   public static final String INI_TAG_SEARCH_HISTORY = "tag_search_history";
   public static final String INI_PREVIEWED_ITEMS_HISTORY = "previewed_items_history";
   public static final String INI_OPENED_ITEMS_HISTORY = "opened_items_history";
   public static final String INI_UPLOADED_ITEMS_HISTORY = "uploaded_items_history";
   public static final String INI_DOWNLOADED_ITEMS_HISTORY = "downloaded_items_history";
   public static final String INI_COMMENTED_ITEMS_HISTORY = "commented_items_history";
   public static final String INI_DEFAULT_LOGGED_IN_TAB = "default_tab_for_logged_in_users";
   public static final String INI_DEFAULT_ANONYMOUS_TAB = "default_tab_for_anonymous_users";
   public static final String INI_MY_STUFF_WORKFLOWS = "show_workflows_in_my_stuff";
   public static final String INI_MY_STUFF_FILES = "show_files_in_my_stuff";
   public static final String INI_MY_STUFF_PACKS = "show_packs_in_my_stuff";
   private final String DO_PUT = "_DO_UPDATE_SIGNAL_";
   public static boolean baseChangedSinceLastStart = false;
   private static final DateFormat OLD_DATE_FORMATTER;
   private static final DateFormat OLD_SHORT_DATE_FORMATTER;
   private static final DateFormat NEW_DATE_FORMATTER;
   private String BASE_URL;
   private java.io.File fIniFileDir;
   private Properties iniSettings;
   private Logger logger = Logger.getLogger(MyExperimentClient.class);
   private boolean LOGGED_IN = false;
   private String AUTH_STRING = "";
   private User current_user = null;
   private CredentialManager credentialManager;

   public MyExperimentClient(CredentialManager credentialManager, ApplicationConfiguration applicationConfig) {
      this.credentialManager = credentialManager;
      this.fIniFileDir = new java.io.File(applicationConfig.getApplicationHomeDir().toFile(), "conf");
      this.iniSettings = new Properties();
      this.loadSettings();
      if (this.BASE_URL == null || this.BASE_URL.length() == 0) {
         this.BASE_URL = "http://www.myexperiment.org";
      }

      this.iniSettings.put("my_experiment_base_url", this.BASE_URL);
   }

   public boolean isLoggedIn() {
      return this.LOGGED_IN;
   }

   public String getBaseURL() {
      return this.BASE_URL;
   }

   public void setBaseURL(String baseURL) {
      this.BASE_URL = baseURL;
   }

   public User getCurrentUser() {
      return this.current_user;
   }

   public void setCurrentUser(User user) {
      this.current_user = user;
   }

   public Properties getSettings() {
      return this.iniSettings;
   }

   public synchronized void loadSettings() {
      try {
         FileInputStream fIniInputStream = new FileInputStream(new java.io.File(this.fIniFileDir, "myexperiment-plugin.ini"));
         this.iniSettings.load(fIniInputStream);
         fIniInputStream.close();
         this.BASE_URL = this.iniSettings.getProperty("my_experiment_base_url");
      } catch (FileNotFoundException var2) {
         this.logger.debug("myExperiment plugin INI file was not found, defaults will be used.");
      } catch (IOException e) {
         this.logger.error("Error on reading settings from INI file:\n" + e);
      }

   }

   private void storeSettings() {
      try {
         this.fIniFileDir.mkdirs();
         FileOutputStream fIniOutputStream = new FileOutputStream(new java.io.File(this.fIniFileDir, "myexperiment-plugin.ini"));
         this.iniSettings.store(fIniOutputStream, "Test comment");
         fIniOutputStream.close();
      } catch (IOException e) {
         this.logger.error("Error while trying to store settings to INI file:\n" + e);
      }

   }

   public void storeHistoryAndSettings() {
      this.iniSettings.put("favourite_searches", Base64.encodeObject(MainComponent.MAIN_COMPONENT.getSearchTab().getSearchFavouritesList()));
      this.iniSettings.put("search_history", Base64.encodeObject(MainComponent.MAIN_COMPONENT.getSearchTab().getSearchHistory()));
      this.iniSettings.put("tag_search_history", Base64.encodeObject(MainComponent.MAIN_COMPONENT.getTagBrowserTab().getTagSearchHistory()));
      this.iniSettings.put("previewed_items_history", Base64.encodeObject(MainComponent.MAIN_COMPONENT.getPreviewBrowser().getPreviewHistory()));
      this.iniSettings.put("downloaded_items_history", Base64.encodeObject(MainComponent.MAIN_COMPONENT.getHistoryBrowser().getDownloadedItemsHistoryList()));
      this.iniSettings.put("opened_items_history", Base64.encodeObject(MainComponent.MAIN_COMPONENT.getHistoryBrowser().getOpenedItemsHistoryList()));
      this.iniSettings.put("uploaded_items_history", Base64.encodeObject(MainComponent.MAIN_COMPONENT.getHistoryBrowser().getUploadedItemsHistoryList()));
      this.iniSettings.put("commented_items_history", Base64.encodeObject(MainComponent.MAIN_COMPONENT.getHistoryBrowser().getCommentedOnItemsHistoryList()));
      this.storeSettings();
   }

   private UsernamePassword getUserPass(String urlString) {
      try {
         URI userpassUrl = URI.create(urlString);
         UsernamePassword userAndPass = this.credentialManager.getUsernameAndPasswordForService(userpassUrl, true, (String)null);
         return userAndPass;
      } catch (CMException e) {
         throw new RuntimeException("Error in Taverna Credential Manager", e);
      }
   }

   public boolean doLogin() {
      ServerResponse response = null;
      Document doc = null;

      try {
         response = this.doMyExperimentGET(this.BASE_URL + "/whoami.xml");
      } catch (Exception e) {
         this.logger.error("Error while attempting to verify login credentials from INI file:\n" + e);
      }

      if (response.getResponseCode() == 401) {
         try {
            for(URI uri : this.credentialManager.getServiceURIsForAllUsernameAndPasswordPairs()) {
               if (uri.toASCIIString().startsWith(this.BASE_URL)) {
                  this.credentialManager.deleteUsernameAndPasswordForService(uri);
               }
            }

            doc = null;
         } catch (Exception e) {
            this.logger.error(e);
         }
      } else {
         doc = response.getResponseBody();
      }

      if (doc == null) {
         this.LOGGED_IN = false;
         this.AUTH_STRING = "";
         this.iniSettings.put("auto_login", (new Boolean(false)).toString());
         JOptionPane.showMessageDialog((Component)null, "Your myExperiment login details appear to be incorrect.\nPlease check your details.");
         return false;
      } else {
         UsernamePassword userPass = this.getUserPass(this.BASE_URL + "/whoami.xml");
         if (userPass == null) {
            return false;
         } else {
            this.LOGGED_IN = true;
            this.AUTH_STRING = Base64.encodeBytes((userPass.getUsername() + ":" + userPass.getPasswordAsString()).getBytes());
            String strCurrentUserURI = doc.getRootElement().getAttributeValue("uri");

            try {
               this.current_user = this.fetchCurrentUser(strCurrentUserURI);
               this.logger.debug("Logged in to myExperiment successfully with credentials that were loaded from INI file.");
               return true;
            } catch (Exception e) {
               this.logger.error("Couldn't fetch user data from myExperiment (" + strCurrentUserURI + ")", e);
               return false;
            }
         }
      }
   }

   public void doLogout() throws Exception {
      this.LOGGED_IN = false;
      this.AUTH_STRING = "";
   }

   public ServerResponse doMyExperimentGET(String strURL) throws Exception {
      URL url = new URL(strURL);
      HttpURLConnection conn = (HttpURLConnection)url.openConnection();
      conn.setRequestProperty("User-Agent", PLUGIN_USER_AGENT);
      if (this.LOGGED_IN) {
         conn.setRequestProperty("Authorization", "Basic " + this.AUTH_STRING);
      }

      return this.doMyExperimentReceiveServerResponse(conn, strURL, true);
   }

   public ServerResponse doMyExperimentPOST(String strURL, String strXMLDataBody) throws Exception {
      if (!this.LOGGED_IN) {
         return null;
      } else {
         URL url = new URL(strURL);
         HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
         urlConn.setRequestMethod(strURL.contains("_DO_UPDATE_SIGNAL_") ? "PUT" : "POST");
         strURL = strURL.replace("_DO_UPDATE_SIGNAL_", "");
         urlConn.setDoOutput(true);
         urlConn.setRequestProperty("Content-Type", "application/xml");
         urlConn.setRequestProperty("User-Agent", PLUGIN_USER_AGENT);
         urlConn.setRequestProperty("Authorization", "Basic " + this.AUTH_STRING);
         String strPOSTContent = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + strXMLDataBody;
         OutputStreamWriter out = new OutputStreamWriter(urlConn.getOutputStream());
         out.write(strPOSTContent);
         out.close();
         return this.doMyExperimentReceiveServerResponse(urlConn, strURL, false);
      }
   }

   public ServerResponse doMyExperimentDELETE(String strURL) throws Exception {
      URL url = new URL(strURL);
      HttpURLConnection conn = (HttpURLConnection)url.openConnection();
      conn.setRequestMethod("DELETE");
      conn.setRequestProperty("User-Agent", PLUGIN_USER_AGENT);
      conn.setRequestProperty("Authorization", "Basic " + this.AUTH_STRING);
      return this.doMyExperimentReceiveServerResponse(conn, strURL, true);
   }

   private ServerResponse doMyExperimentReceiveServerResponse(HttpURLConnection conn, String strURL, boolean bIsGETRequest) throws Exception {
      int iResponseCode = conn.getResponseCode();
      switch (iResponseCode) {
         case 200:
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            Document doc = (new SAXBuilder()).build(new InputSource(reader));
            reader.close();
            return new ServerResponse(iResponseCode, doc);
         case 400:
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            Document errorDoc = (new SAXBuilder()).build(new InputSource(errorReader));
            errorReader.close();
            return new ServerResponse(iResponseCode, errorDoc);
         case 401:
            return new ServerResponse(iResponseCode, (Document)null);
         default:
            throw new IOException("Received unexpected HTTP response code (" + conn.getResponseCode() + ") while " + (bIsGETRequest ? "fetching data at " : "posting data to ") + strURL);
      }
   }

   public User fetchCurrentUser(String uri) {
      User user = null;

      try {
         Document doc = this.getResource(20, uri, 5005);
         user = User.buildFromXML(doc, this.logger);
      } catch (Exception ex) {
         this.logger.error("Failed to fetch user data from myExperiment (" + uri + "); exception:\n" + ex);
      }

      try {
         if (user.getAvatarURI() == null) {
            ImageIcon icon = new ImageIcon(user.getAvatarResource());
            user.setAvatar(icon);
         } else {
            Document doc = this.doMyExperimentGET(user.getAvatarURI()).getResponseBody();
            user.setAvatar(doc);
         }
      } catch (Exception ex) {
         this.logger.error("Failed to fetch user's avatar from myExperiment (" + user.getAvatarURI() + "); exception:\n" + ex);
      }

      return user;
   }

   public Document getResource(int iResourceType, String strURI, int iRequestType) throws Exception {
      if (iRequestType == 5000) {
         strURI = strURI + "&all_elements=yes";
      } else {
         switch (iResourceType) {
            case 10:
               strURI = strURI + "&elements=" + Workflow.getRequiredAPIElements(iRequestType);
               break;
            case 11:
               strURI = strURI + "&elements=" + File.getRequiredAPIElements(iRequestType);
               break;
            case 12:
               strURI = strURI + "&elements=" + Pack.getRequiredAPIElements(iRequestType);
            case 13:
            case 16:
            case 17:
            case 18:
            case 19:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            default:
               break;
            case 14:
               strURI = strURI + "&all_elements=yes";
               break;
            case 15:
               strURI = strURI + "&all_elements=yes";
               break;
            case 20:
               strURI = strURI + "&elements=" + User.getRequiredAPIElements(iRequestType);
               break;
            case 21:
               strURI = strURI + "&elements=" + Group.getRequiredAPIElements(iRequestType);
               break;
            case 30:
               strURI = strURI + "&elements=" + Tag.getRequiredAPIElements(iRequestType);
               break;
            case 31:
               strURI = strURI + "&elements=" + Comment.getRequiredAPIElements(iRequestType);
         }
      }

      return this.doMyExperimentGET(strURI).getResponseBody();
   }

   public Workflow fetchWorkflowBinary(String strWorkflowURI) throws Exception {
      Document doc = this.getResource(10, strWorkflowURI, 5055);
      Element root = doc.getRootElement();
      Workflow w = new Workflow();
      w.setVisibleType(root.getChildText("type"));
      w.setContentType(root.getChildText("content-type"));
      if (!w.isTavernaWorkflow()) {
         throw new Exception("Unsupported workflow type. Details:\nWorkflow type: " + w.getVisibleType() + "\nMime type: " + w.getContentType());
      } else {
         String strEncoding = root.getChild("content").getAttributeValue("encoding");
         String strDataFormat = root.getChild("content").getAttributeValue("type");
         if (strEncoding.toLowerCase().equals("base64") && strDataFormat.toLowerCase().equals("binary")) {
            byte[] arrWorkflowData = Base64.decode(root.getChildText("content"));
            w.setContent(arrWorkflowData);
            return w;
         } else {
            throw new Exception("Unsupported workflow data format. Details:\nContent encoding: " + strEncoding + "\nFormat: " + strDataFormat);
         }
      }
   }

   public List<Workflow> getExampleWorkflows() {
      List<Workflow> workflows = new ArrayList();

      try {
         String strExampleWorkflowsPackUrl = this.BASE_URL + "/pack.xml?id=" + 254 + "&elements=internal-pack-items";
         Document doc = this.doMyExperimentGET(strExampleWorkflowsPackUrl).getResponseBody();
         if (doc != null) {
            for(Element e : doc.getRootElement().getChild("internal-pack-items").getChildren("workflow")) {
               String itemUri = e.getAttributeValue("uri");
               Document itemDoc = this.doMyExperimentGET(itemUri).getResponseBody();
               String workflowUri = itemDoc.getRootElement().getChild("item").getChild("workflow").getAttributeValue("uri");
               Document docCurWorkflow = this.getResource(10, workflowUri, 5010);
               workflows.add(Workflow.buildFromXML(docCurWorkflow, this.logger));
            }
         }
      } catch (Exception e) {
         this.logger.error("Failed to retrieve example workflows", e);
      }

      this.logger.debug(workflows.size() + " example workflows retrieved from myExperiment");
      return workflows;
   }

   public TagCloud getGeneralTagCloud(int size) {
      TagCloud tcCloud = new TagCloud();

      try {
         String strTagCloudURL = this.BASE_URL + "/tag-cloud.xml?num=" + (size > 0 ? "" + size : "all");
         Document doc = this.doMyExperimentGET(strTagCloudURL).getResponseBody();
         if (doc != null) {
            for(Element e : doc.getRootElement().getChildren("tag")) {
               Tag t = new Tag();
               t.setTitle(e.getText());
               t.setTagName(e.getText());
               t.setResource(e.getAttributeValue("resource"));
               t.setURI(e.getAttributeValue("uri"));
               t.setCount(Integer.parseInt(e.getAttributeValue("count")));
               tcCloud.getTags().add(t);
            }
         }
      } catch (Exception e) {
         this.logger.error("ERROR: Failed to get tag cloud.\n", e);
      }

      this.logger.debug("Tag cloud retrieval successful; fetched " + tcCloud.getTags().size() + " tags from myExperiment");
      return tcCloud;
   }

   public TagCloud getUserTagCloud(User user, int size) {
      TagCloud tcCloud = new TagCloud();

      try {
         synchronized(user.getTags()) {
            user.getTags().clear();
            Document doc = this.getResource(20, user.getURI(), 5051);
            Iterator<Element> iNewUserTags = doc.getRootElement().getChild("tags-applied").getChildren().iterator();
            Util.getResourceCollectionFromXMLIterator(iNewUserTags, user.getTags());
         }

         Iterator<HashMap<String, String>> iTagsResourcesHashMaps = user.getTags().iterator();

         while(iTagsResourcesHashMaps.hasNext()) {
            String strCurTagURI = (String)((HashMap)iTagsResourcesHashMaps.next()).get("uri");
            Document doc = this.doMyExperimentGET(strCurTagURI).getResponseBody();
            Element root = doc.getRootElement();
            Tag t = new Tag();
            t.setTagName(root.getChild("name").getText());
            t.setCount(Integer.parseInt(root.getChild("count").getText()));
            tcCloud.getTags().add(t);
         }

         if (size <= 0) {
            size = tcCloud.getTags().size();
         }

         Comparator<Tag> byPopularity = new Tag.ReversePopularityComparator();
         Collections.sort(tcCloud.getTags(), byPopularity);
         int iSelectedTags = 0;
         List<Tag> tagListOfRequiredSize = new ArrayList();

         for(Iterator<Tag> iTags = tcCloud.getTags().iterator(); iTags.hasNext() && iSelectedTags < size; ++iSelectedTags) {
            tagListOfRequiredSize.add(iTags.next());
         }

         tcCloud.getTags().clear();
         tcCloud.getTags().addAll(tagListOfRequiredSize);
         Comparator<Tag> byAlphabet = new Tag.AlphanumericComparator();
         Collections.sort(tcCloud.getTags(), byAlphabet);
      } catch (Exception e) {
         this.logger.error("Failed midway through fetching user tags for user ID = " + user.getID() + "\n" + e);
      }

      return tcCloud;
   }

   public Document getUserContributions(User user, int iResourceType, int iRequestType, int page) {
      Document doc = null;
      String strURL = this.BASE_URL;
      String strElements = "&elements=";

      try {
         switch (iResourceType) {
            case 10:
               strURL = strURL + "/workflows.xml?uploader=";
               strElements = strElements + Workflow.getRequiredAPIElements(iRequestType);
               break;
            case 11:
               strURL = strURL + "/files.xml?uploader=";
               strElements = strElements + File.getRequiredAPIElements(iRequestType);
               break;
            case 12:
               strURL = strURL + "/packs.xml?owner=";
               strElements = strElements + Workflow.getRequiredAPIElements(iRequestType);
         }

         if (page != 0) {
            strElements = strElements + "&num=100&page=" + page;
         }

         strURL = strURL + urlEncodeQuery(user.getResource()) + strElements;
         doc = this.doMyExperimentGET(strURL).getResponseBody();
      } catch (Exception var9) {
         this.logger.error("ERROR: Failed to fetch user's contributions.");
      }

      return doc;
   }

   public Document searchByTag(String strTag) {
      Document doc = null;

      try {
         String strUrlEncodedTag = urlEncodeQuery(strTag);
         doc = this.doMyExperimentGET(this.BASE_URL + "/tagged.xml?tag=" + strUrlEncodedTag + Util.composeAPIQueryElements((SearchEngine.QuerySearchInstance)null)).getResponseBody();
      } catch (Exception e) {
         this.logger.error("ERROR: Failed to fetch tagged items from myExperiment. Query tag was '" + strTag + "'\n" + e);
      }

      return doc;
   }

   public void convertTagListIntoTagCloudData(List<Tag> tags) {
      try {
         Document doc = null;

         for(Tag t : tags) {
            doc = this.getResource(30, t.getURI(), 5000);
            Element rootElement = doc.getRootElement();
            t.setCount(Integer.parseInt(rootElement.getChild("count").getText()));
         }
      } catch (Exception e) {
         this.logger.error("Failed while getting tag application counts when turning tag list into tag cloud data", e);
      }

   }

   public void updateUserFavourites(User user) {
      try {
         Document doc = this.getResource(20, user.getURI(), 5050);
         List<Resource> newUserFavouritesList = Util.retrieveUserFavourites(doc.getRootElement());
         user.getFavourites().clear();
         user.getFavourites().addAll(newUserFavouritesList);
      } catch (Exception ex) {
         this.logger.error("Failed to fetch favourites data from myExperiment for a user (URI: " + user.getURI() + "); exception:\n" + ex);
         JOptionPane.showMessageDialog((Component)null, "Couldn't synchronise data about your favourite items with myExperiment.\nYou might not be able to add / remove other items to your favourites and.\nPlease refresh your profile data manually by clicking 'Refresh' button in 'My Stuff' tab.", "myExperiment Plugin - Error", 0);
      }

   }

   public void updateCommentListWithExtraData(List<Comment> comments) {
      try {
         Document doc = null;

         for(Comment c : comments) {
            doc = this.getResource(31, c.getURI(), 5000);
            Element rootElement = doc.getRootElement();
            Element userElement = rootElement.getChild("author");
            User u = new User();
            u.setTitle(userElement.getText());
            u.setName(userElement.getText());
            u.setResource(userElement.getAttributeValue("resource"));
            u.setURI(userElement.getAttributeValue("uri"));
            c.setUser(u);
            String createdAt = rootElement.getChildText("created-at");
            if (createdAt != null && !createdAt.equals("")) {
               c.setCreatedAt(parseDate(createdAt));
            }
         }
      } catch (Exception e) {
         this.logger.error("Failed while updating comment list for preview", e);
      }

   }

   public Document searchByQuery(SearchEngine.QuerySearchInstance searchQuery) {
      Document doc = null;
      String strSearchURL = null;

      try {
         String strUrlEncodedQuery = urlEncodeQuery(searchQuery.getSearchQuery());
         String strSearchFor = "";
         if (searchQuery.getSearchWorkflows()) {
            strSearchFor = strSearchFor + "workflow";
         }

         if (searchQuery.getSearchFiles()) {
            strSearchFor = strSearchFor + ",file";
         }

         if (searchQuery.getSearchPacks()) {
            strSearchFor = strSearchFor + ",pack";
         }

         if (searchQuery.getSearchUsers()) {
            strSearchFor = strSearchFor + ",user";
         }

         if (searchQuery.getSearchGroups()) {
            strSearchFor = strSearchFor + ",group";
         }

         if (strSearchFor.length() != 0) {
            if (strSearchFor.startsWith(",")) {
               strSearchFor = strSearchFor.replaceFirst(",", "");
            }

            strSearchFor = "type=" + strSearchFor;
         }

         String strParameters = strSearchFor;
         if (strSearchFor.length() != 0) {
            strParameters = strSearchFor + "&";
         }

         strParameters = strParameters + "num=" + searchQuery.getResultCountLimit();
         strParameters = strParameters + Util.composeAPIQueryElements(searchQuery);
         strSearchURL = this.BASE_URL + "/search.xml?query=" + strUrlEncodedQuery + "&" + strParameters;
         doc = this.doMyExperimentGET(strSearchURL).getResponseBody();
      } catch (Exception e) {
         this.logger.error("ERROR: Failed to run search on myExperiment. Query URL was'" + strSearchURL + "'\n" + e);
      }

      return doc;
   }

   public ServerResponse postComment(Resource resource, String strComment) {
      try {
         String strCommentData = "<comment><subject resource=\"" + resource.getResource() + "\"/><comment>" + strComment + "</comment></comment>";
         ServerResponse response = this.doMyExperimentPOST(this.BASE_URL + "/comment.xml", strCommentData);
         if (response.getResponseCode() == 200) {
            Comment cNew = Comment.buildFromXML(response.getResponseBody(), this.logger);
            resource.getComments().add(cNew);
         }

         return response;
      } catch (Exception e) {
         this.logger.error("Failed while trying to post a comment for " + resource.getURI() + "\n" + e);
         return new ServerResponse(ServerResponse.LOCAL_FAILURE, (Document)null);
      }
   }

   private String prepareWorkflowPostContent(String workflowContent, String title, String description, String license, String sharing) {
      String strWorkflowData = "<workflow>";
      if (title.length() > 0) {
         strWorkflowData = strWorkflowData + "<title>" + title + "</title>";
      }

      if (description.length() > 0) {
         strWorkflowData = strWorkflowData + "<description>" + description + "</description>";
      }

      if (license.length() > 0) {
         strWorkflowData = strWorkflowData + "<license-type>" + license + "</license-type>";
      }

      if (sharing.length() > 0) {
         if (sharing.contains("private")) {
            strWorkflowData = strWorkflowData + "<permissions />";
         } else {
            strWorkflowData = strWorkflowData + "<permissions><permission><category>public</category>";
            if (sharing.contains("view") || sharing.contains("download")) {
               strWorkflowData = strWorkflowData + "<privilege type=\"view\" />";
            }

            if (sharing.contains("download")) {
               strWorkflowData = strWorkflowData + "<privilege type=\"download\" />";
            }

            strWorkflowData = strWorkflowData + "</permission></permissions>";
         }
      }

      String encodedWorkflow = "";
      String scuflSchemaDef = "xmlns:s=\"http://org.embl.ebi.escience/xscufl/0.1alpha\"";
      String t2flowSchemaDef = "xmlns=\"http://taverna.sf.net/2008/xml/t2flow\"";
      if (workflowContent.length() > 0) {
         String contentType;
         if (workflowContent.contains(scuflSchemaDef)) {
            contentType = "application/vnd.taverna.scufl+xml";
         } else if (workflowContent.contains(t2flowSchemaDef)) {
            contentType = "application/vnd.taverna.t2flow+xml";
         } else {
            contentType = "";
         }

         encodedWorkflow = encodedWorkflow + "<content-type>" + contentType + "</content-type><content encoding=\"base64\" type=\"binary\">" + Base64.encodeBytes(workflowContent.getBytes()) + "</content>";
         strWorkflowData = strWorkflowData + encodedWorkflow;
      }

      strWorkflowData = strWorkflowData + "</workflow>";
      return strWorkflowData;
   }

   private void afterMyExperimentPost(ServerResponse response) {
   }

   public ServerResponse postWorkflow(String workflowContent, String title, String description, String license, String sharing) {
      try {
         String strWorkflowData = this.prepareWorkflowPostContent(workflowContent, title, description, license, sharing);
         ServerResponse response = this.doMyExperimentPOST(this.BASE_URL + "/workflow.xml", strWorkflowData);
         this.afterMyExperimentPost(response);
         return response;
      } catch (Exception var8) {
         this.logger.error("Failed while trying to upload the workflow");
         return new ServerResponse(ServerResponse.LOCAL_FAILURE, (Document)null);
      }
   }

   public ServerResponse updateWorkflowVersionOrMetadata(Resource resource, String workflowContent, String title, String description, String license, String sharing) {
      try {
         String strWorkflowData = this.prepareWorkflowPostContent(workflowContent, title, description, license, sharing);
         String doUpdateStatus = workflowContent.length() == 0 ? "_DO_UPDATE_SIGNAL_" : "";
         ServerResponse response = this.doMyExperimentPOST(this.BASE_URL + "/workflow.xml?id=" + resource.getID() + doUpdateStatus, strWorkflowData);
         this.afterMyExperimentPost(response);
         return response;
      } catch (Exception var10) {
         this.logger.error("Failed while trying to upload the workflow");
         return new ServerResponse(ServerResponse.LOCAL_FAILURE, (Document)null);
      }
   }

   public ServerResponse addFavourite(Resource resource) {
      try {
         String strData = "<favourite><object resource=\"" + resource.getResource() + "\"/></favourite>";
         ServerResponse response = this.doMyExperimentPOST(this.BASE_URL + "/favourite.xml", strData);
         return response;
      } catch (Exception e) {
         this.logger.error("Failed while trying to add an item (" + resource.getURI() + ") to favourites4", e);
         return new ServerResponse(ServerResponse.LOCAL_FAILURE, (Document)null);
      }
   }

   public ServerResponse deleteFavourite(Resource resource) {
      try {
         String strGetFavouriteObjectURL = this.BASE_URL + "/favourites.xml?user=" + urlEncodeQuery(this.getCurrentUser().getResource()) + "&object=" + urlEncodeQuery(resource.getResource());
         ServerResponse response = this.doMyExperimentGET(strGetFavouriteObjectURL);
         Element root = response.getResponseBody().getRootElement();
         String strFavouriteURI = root.getChild("favourite").getAttributeValue("uri");
         response = this.doMyExperimentDELETE(strFavouriteURI);
         return response;
      } catch (Exception e) {
         this.logger.error("Failed while trying to remove an item (" + resource.getURI() + ") from favourites\n" + e);
         return new ServerResponse(ServerResponse.LOCAL_FAILURE, (Document)null);
      }
   }

   public static Date parseDate(String date) {
      Date result = null;

      try {
         result = OLD_DATE_FORMATTER.parse(date);
      } catch (ParseException var7) {
         try {
            result = OLD_SHORT_DATE_FORMATTER.parse(date);
         } catch (ParseException var6) {
            try {
               result = NEW_DATE_FORMATTER.parse(date);
            } catch (ParseException var5) {
               result = null;
            }
         }
      }

      return result;
   }

   public static String formatDate(Date date) {
      return NEW_DATE_FORMATTER.format(date);
   }

   private static String urlEncodeQuery(String query) {
      String strRes = "";

      try {
         strRes = URLEncoder.encode(query, "UTF-8");
      } catch (UnsupportedEncodingException var3) {
      }

      return strRes;
   }

   static {
      OLD_DATE_FORMATTER = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
      OLD_SHORT_DATE_FORMATTER = new SimpleDateFormat("HH:mm 'on' dd/MM/yyyy", Locale.ENGLISH);
      NEW_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
   }
}
