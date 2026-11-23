package org.apache.activemq.artemis.utils;

import java.util.ArrayList;
import java.util.List;

public class SelectorTranslator {
   public static String convertToActiveMQFilterString(String selectorString) {
      if (selectorString == null) {
         return null;
      } else {
         String filterString = parse(selectorString, "JMSDeliveryMode", "AMQDurable");
         filterString = parse(filterString, "'PERSISTENT'", "'DURABLE'");
         filterString = parse(filterString, "'NON_PERSISTENT'", "'NON_DURABLE'");
         filterString = parse(filterString, "JMSPriority", "AMQPriority");
         filterString = parse(filterString, "JMSTimestamp", "AMQTimestamp");
         filterString = parse(filterString, "JMSMessageID", "AMQUserID");
         filterString = parse(filterString, "JMSExpiration", "AMQExpiration");
         return filterString;
      }
   }

   private static String parse(String input, String match, String replace) {
      char quote = '\'';
      boolean inQuote = false;
      int matchPos = 0;
      List<Integer> positions = new ArrayList();
      boolean replaceInQuotes = match.charAt(0) == '\'';

      for(int i = 0; i < input.length(); ++i) {
         char c = input.charAt(i);
         if (c == '\'') {
            inQuote = !inQuote;
         }

         if ((!inQuote || replaceInQuotes) && c == match.charAt(matchPos)) {
            ++matchPos;
            if (matchPos == match.length()) {
               boolean matched = true;
               if (i < input.length() - 1 && Character.isJavaIdentifierPart(input.charAt(i + 1))) {
                  matched = false;
               }

               int posBeforeStart = i - match.length();
               if (posBeforeStart >= 0 && Character.isJavaIdentifierPart(input.charAt(posBeforeStart))) {
                  matched = false;
               }

               if (matched) {
                  positions.add(i - match.length() + 1);
               }

               matchPos = 0;
            }
         } else {
            matchPos = 0;
         }
      }

      if (positions.isEmpty()) {
         return input;
      } else {
         StringBuffer buff = new StringBuffer();
         int startPos = 0;

         for(int pos : positions) {
            String substr = input.substring(startPos, pos);
            buff.append(substr);
            buff.append(replace);
            startPos = pos + match.length();
         }

         if (startPos < input.length()) {
            buff.append(input.substring(startPos, input.length()));
         }

         return buff.toString();
      }
   }
}
