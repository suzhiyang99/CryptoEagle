package org.apache.tika.sax;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StandardsText {
   private static final String REGEX_HEADER = "(\\d+\\.(\\d+\\.?)*)\\p{Blank}+([A-Z]+(\\s[A-Z]+)*){5,}";
   private static final String REGEX_APPLICABLE_DOCUMENTS = "(?i:.*APPLICABLE\\sDOCUMENTS|REFERENCE|STANDARD|REQUIREMENT|GUIDELINE|COMPLIANCE.*)";
   private static final String REGEX_IDENTIFIER = "(?<identifier>([0-9]{3,}|([A-Z]+(-|_|\\.)?[0-9]{2,}))((-|_|\\.)?[A-Z0-9]+)*)";
   private static final String REGEX_ORGANIZATION = StandardOrganizations.getOrganzationsRegex();
   private static final String REGEX_STANDARD_TYPE = "(\\s(?i:Publication|Standard))";
   private static final String REGEX_FALLBACK = "\\(?(?<mainOrganization>[A-Z]\\w+)\\)?((\\s?(?<separator>\\/)\\s?)(\\w+\\s)*\\(?(?<secondOrganization>[A-Z]\\w+)\\)?)?(\\s(?i:Publication|Standard))?(-|\\s)?(?<identifier>([0-9]{3,}|([A-Z]+(-|_|\\.)?[0-9]{2,}))((-|_|\\.)?[A-Z0-9]+)*)";
   private static final String REGEX_STANDARD;

   public static ArrayList<StandardReference> extractStandardReferences(String text, double threshold) {
      Map<Integer, String> headers = findHeaders(text);
      ArrayList<StandardReference> standardReferences = findStandards(text, headers, threshold);
      return standardReferences;
   }

   private static Map<Integer, String> findHeaders(String text) {
      Map<Integer, String> headers = new TreeMap();
      Pattern pattern = Pattern.compile("(\\d+\\.(\\d+\\.?)*)\\p{Blank}+([A-Z]+(\\s[A-Z]+)*){5,}");
      Matcher matcher = pattern.matcher(text);

      while(matcher.find()) {
         headers.put(matcher.start(), matcher.group());
      }

      return headers;
   }

   private static ArrayList<StandardReference> findStandards(String text, Map<Integer, String> headers, double threshold) {
      ArrayList<StandardReference> standards = new ArrayList();
      double score = (double)0.0F;
      Pattern pattern = Pattern.compile("\\(?(?<mainOrganization>[A-Z]\\w+)\\)?((\\s?(?<separator>\\/)\\s?)(\\w+\\s)*\\(?(?<secondOrganization>[A-Z]\\w+)\\)?)?(\\s(?i:Publication|Standard))?(-|\\s)?(?<identifier>([0-9]{3,}|([A-Z]+(-|_|\\.)?[0-9]{2,}))((-|_|\\.)?[A-Z0-9]+)*)");
      Matcher matcher = pattern.matcher(text);

      while(matcher.find()) {
         StandardReference.StandardReferenceBuilder builder = (new StandardReference.StandardReferenceBuilder(matcher.group("mainOrganization"), matcher.group("identifier"))).setSecondOrganization(matcher.group("separator"), matcher.group("secondOrganization"));
         score = (double)0.25F;
         if (matcher.group().matches(REGEX_STANDARD)) {
            score += (double)0.25F;
         }

         if (matcher.group().matches(".*(\\s(?i:Publication|Standard)).*")) {
            score += (double)0.25F;
         }

         int startHeader = 0;
         int endHeader = 0;
         boolean headerFound = false;
         Iterator<Map.Entry<Integer, String>> iterator = headers.entrySet().iterator();

         while(iterator.hasNext() && !headerFound) {
            startHeader = endHeader;
            endHeader = (Integer)((Map.Entry)iterator.next()).getKey();
            if (endHeader > matcher.start()) {
               headerFound = true;
            }
         }

         String header = (String)headers.get(startHeader);
         if (header != null && ((String)headers.get(startHeader)).matches("(?i:.*APPLICABLE\\sDOCUMENTS|REFERENCE|STANDARD|REQUIREMENT|GUIDELINE|COMPLIANCE.*)")) {
            score += (double)0.25F;
         }

         builder.setScore(score);
         if (score >= threshold) {
            standards.add(builder.build());
         }
      }

      return standards;
   }

   static {
      REGEX_STANDARD = ".*" + REGEX_ORGANIZATION + ".+" + REGEX_ORGANIZATION + "?.*";
   }
}
