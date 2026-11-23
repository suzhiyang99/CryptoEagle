package org.apache.manifoldcf.core.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class DateParser {
   public static final String _rcsid = "@(#)$Id$";
   protected static HashMap monthMap = new HashMap();
   protected static final HashMap milTzMap;

   public static Date parseISO8601Date(String isoDateValue) {
      if (isoDateValue == null) {
         return null;
      } else {
         boolean isMicrosoft = isoDateValue.indexOf("T") == -1;
         String formatString;
         if (isMicrosoft) {
            formatString = "yyyy-MM-dd' 'HH:mm:ss";
         } else {
            StringBuilder isoFormatString = new StringBuilder("yy");
            if (isoDateValue.length() > 2 && isoDateValue.charAt(2) != '-') {
               isoFormatString.append("yy");
            }

            isoFormatString.append("-MM-dd'T'HH:mm:ss");
            if (isoDateValue.indexOf(".") != -1) {
               isoFormatString.append(".SSS");
            }

            if (isoDateValue.endsWith("Z")) {
               isoDateValue = isoDateValue.substring(0, isoDateValue.length() - 1) + "-0000";
               isoFormatString.append("Z");
            } else {
               int colonIndex = isoDateValue.lastIndexOf(":");
               int dashIndex = isoDateValue.lastIndexOf("-");
               int plusIndex = isoDateValue.lastIndexOf("+");
               if (colonIndex != -1 && (dashIndex != -1 && colonIndex == dashIndex + 3 && isNumeral(isoDateValue, dashIndex - 1) || plusIndex != -1 && colonIndex == plusIndex + 3 && isNumeral(isoDateValue, plusIndex - 1))) {
                  isoDateValue = isoDateValue.substring(0, colonIndex) + isoDateValue.substring(colonIndex + 1);
               }

               isoFormatString.append("Z");
            }

            formatString = isoFormatString.toString();
         }

         DateFormat iso8601Format = new SimpleDateFormat(formatString, Locale.ROOT);

         try {
            return iso8601Format.parse(isoDateValue);
         } catch (ParseException var7) {
            return null;
         }
      }
   }

   protected static boolean isNumeral(String value, int index) {
      return index >= 0 && value.charAt(index) >= '0' && value.charAt(index) <= '9';
   }

   public static String formatISO8601Date(Date dateValue) {
      DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ROOT);
      df.setTimeZone(TimeZone.getTimeZone("GMT"));
      return df.format(dateValue);
   }

   public static Date parseRFC822Date(String dateValue) {
      if (dateValue == null) {
         return null;
      } else {
         dateValue = dateValue.trim();
         int commaIndex = dateValue.indexOf(",");
         String usable;
         if (commaIndex == -1) {
            usable = dateValue;
         } else {
            usable = dateValue.substring(commaIndex + 1).trim();
         }

         int index = usable.indexOf(" ");
         if (index == -1) {
            return null;
         } else {
            String day = usable.substring(0, index);
            usable = usable.substring(index + 1).trim();
            index = usable.indexOf(" ");
            if (index == -1) {
               return null;
            } else {
               String month = usable.substring(0, index).toLowerCase(Locale.ROOT);
               usable = usable.substring(index + 1).trim();
               String hour = null;
               String minute = null;
               String second = null;
               String timezone = null;
               index = usable.indexOf(" ");
               String year;
               if (index != -1) {
                  year = usable.substring(0, index);
                  usable = usable.substring(index + 1).trim();
                  index = usable.indexOf(":");
                  if (index == -1) {
                     return null;
                  }

                  hour = usable.substring(0, index);
                  usable = usable.substring(index + 1).trim();
                  index = usable.indexOf(":");
                  if (index != -1) {
                     minute = usable.substring(0, index);
                     usable = usable.substring(index + 1).trim();
                     index = usable.indexOf(" ");
                     if (index == -1) {
                        second = usable;
                     } else {
                        second = usable.substring(0, index);
                        timezone = usable.substring(index + 1).trim();
                     }
                  } else {
                     index = usable.indexOf(" ");
                     if (index == -1) {
                        minute = usable;
                     } else {
                        minute = usable.substring(0, index);
                        timezone = usable.substring(index + 1).trim();
                     }
                  }
               } else {
                  year = usable;
               }

               if (timezone != null && timezone.length() > 0) {
                  if (!timezone.startsWith("+") && !timezone.startsWith("-")) {
                     if (milTzMap.get(timezone) != null) {
                        timezone = (String)milTzMap.get(timezone);
                     }
                  } else {
                     if (timezone.indexOf(":") == -1 && timezone.length() > 3) {
                        timezone = timezone.substring(0, timezone.length() - 2) + ":" + timezone.substring(timezone.length() - 2);
                     }

                     timezone = "GMT" + timezone;
                  }
               } else {
                  timezone = "GMT";
               }

               TimeZone tz = TimeZone.getTimeZone(timezone);
               Calendar c = new GregorianCalendar(tz, Locale.ROOT);

               try {
                  int value = Integer.parseInt(year);
                  if (value < 1900) {
                     value += 1900;
                  }

                  c.set(1, value);
                  Integer x = (Integer)monthMap.get(month);
                  if (x == null) {
                     return null;
                  } else {
                     c.set(2, x - 1);
                     value = Integer.parseInt(day);
                     c.set(5, value);
                     if (hour != null) {
                        value = Integer.parseInt(hour);
                     } else {
                        value = 0;
                     }

                     c.set(11, value);
                     if (minute != null) {
                        value = Integer.parseInt(minute);
                     } else {
                        value = 0;
                     }

                     c.set(12, value);
                     if (second != null) {
                        value = Integer.parseInt(second);
                     } else {
                        value = 0;
                     }

                     c.set(13, value);
                     c.set(14, 0);
                     return new Date(c.getTimeInMillis());
                  }
               } catch (NumberFormatException var15) {
                  return null;
               }
            }
         }
      }
   }

   public static Date parseChinaDate(String dateValue) {
      if (dateValue == null) {
         return null;
      } else {
         dateValue = dateValue.trim();
         int index = dateValue.indexOf("/");
         if (index == -1) {
            return null;
         } else {
            String year = dateValue.substring(0, index);
            dateValue = dateValue.substring(index + 1);
            index = dateValue.indexOf("/");
            if (index == -1) {
               return null;
            } else {
               String month = dateValue.substring(0, index);
               dateValue = dateValue.substring(index + 1);
               index = dateValue.indexOf(" ");
               String hour = null;
               String minute = null;
               String second = null;
               String day;
               if (index == -1) {
                  day = dateValue;
               } else {
                  day = dateValue.substring(0, index);
                  dateValue = dateValue.substring(index + 1);
                  index = dateValue.indexOf(":");
                  if (index == -1) {
                     return null;
                  }

                  hour = dateValue.substring(0, index);
                  dateValue = dateValue.substring(index + 1);
                  index = dateValue.indexOf(":");
                  if (index != -1) {
                     minute = dateValue.substring(0, index);
                     dateValue = dateValue.substring(index + 1);
                     second = dateValue;
                  } else {
                     minute = dateValue;
                  }
               }

               TimeZone tz = TimeZone.getTimeZone("GMT");
               Calendar c = new GregorianCalendar(tz, Locale.ROOT);

               try {
                  int value = Integer.parseInt(year);
                  if (value < 1900) {
                     value += 1900;
                  }

                  c.set(1, value);
                  value = Integer.parseInt(month);
                  c.set(2, value - 1);
                  value = Integer.parseInt(day);
                  c.set(5, value);
                  if (hour != null) {
                     value = Integer.parseInt(hour);
                  } else {
                     value = 0;
                  }

                  c.set(11, value);
                  if (minute != null) {
                     value = Integer.parseInt(minute);
                  } else {
                     value = 0;
                  }

                  c.set(12, value);
                  if (second != null) {
                     value = Integer.parseInt(second);
                  } else {
                     value = 0;
                  }

                  c.set(13, value);
                  c.set(14, 0);
                  return new Date(c.getTimeInMillis());
               } catch (NumberFormatException var11) {
                  return null;
               }
            }
         }
      }
   }

   static {
      monthMap.put("jan", new Integer(1));
      monthMap.put("feb", new Integer(2));
      monthMap.put("mar", new Integer(3));
      monthMap.put("apr", new Integer(4));
      monthMap.put("may", new Integer(5));
      monthMap.put("jun", new Integer(6));
      monthMap.put("jul", new Integer(7));
      monthMap.put("aug", new Integer(8));
      monthMap.put("sep", new Integer(9));
      monthMap.put("oct", new Integer(10));
      monthMap.put("nov", new Integer(11));
      monthMap.put("dec", new Integer(12));
      milTzMap = new HashMap();
      milTzMap.put("Z", "GMT");
      milTzMap.put("UT", "GMT");
      milTzMap.put("A", "GMT-01:00");
      milTzMap.put("M", "GMT-12:00");
      milTzMap.put("N", "GMT+01:00");
      milTzMap.put("Y", "GMT+12:00");
   }
}
