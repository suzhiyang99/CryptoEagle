package org.apache.openejb.core.timer;

import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ejb.ScheduleExpression;
import org.apache.openejb.quartz.impl.triggers.CronTriggerImpl;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public class EJBCronTrigger extends CronTriggerImpl {
   private static final long serialVersionUID = 1L;
   private static final Logger log;
   private static final Pattern INCREMENTS;
   private static final Pattern LIST;
   private static final Pattern WEEKDAY;
   private static final Pattern DAYS_TO_LAST;
   private static final Pattern VALID_YEAR;
   private static final Pattern VALID_MONTH;
   private static final Pattern VALID_DAYS_OF_WEEK;
   private static final Pattern VALID_DAYS_OF_MONTH;
   private static final Pattern VALID_HOUR;
   private static final Pattern VALID_MINUTE;
   private static final Pattern VALID_SECOND;
   private static final Pattern RANGE;
   public static final String DELIMITER = ";";
   private static final String LAST_IDENTIFIER = "LAST";
   private static final Map<String, Integer> WEEKDAYS_MAP;
   private static final Map<String, Integer> MONTHS_MAP;
   private static final int[] ORDERED_CALENDAR_FIELDS;
   private static final Map<Integer, Integer> CALENDAR_FIELD_TYPE_ORDERED_INDEX_MAP;
   private final FieldExpression[] expressions = new FieldExpression[7];
   private final TimeZone timezone;
   private final String rawValue;

   public EJBCronTrigger(ScheduleExpression expr) throws ParseException {
      Map<Integer, String> fieldValues = new LinkedHashMap();
      fieldValues.put(1, expr.getYear());
      fieldValues.put(2, expr.getMonth());
      fieldValues.put(5, expr.getDayOfMonth());
      fieldValues.put(7, expr.getDayOfWeek());
      fieldValues.put(11, expr.getHour());
      fieldValues.put(12, expr.getMinute());
      fieldValues.put(13, expr.getSecond());
      this.timezone = expr.getTimezone() == null ? TimeZone.getDefault() : TimeZone.getTimeZone(expr.getTimezone());
      this.setStartTime(expr.getStart() == null ? new Date() : expr.getStart());
      this.setEndTime(expr.getEnd());
      Map<Integer, ParseException> errors = new HashMap();
      int index = 0;

      for(Map.Entry<Integer, String> entry : fieldValues.entrySet()) {
         int field = (Integer)entry.getKey();
         String value = (String)entry.getValue();

         try {
            this.expressions[index++] = this.parseExpression(field, value);
         } catch (ParseException e) {
            errors.put(field, e);
         }
      }

      if (!errors.isEmpty()) {
         throw new ParseException(errors);
      } else {
         this.rawValue = expr.getYear() + ";" + expr.getMonth() + ";" + expr.getDayOfMonth() + ";" + expr.getDayOfWeek() + ";" + expr.getHour() + ";" + expr.getMinute() + ";" + expr.getSecond();
      }
   }

   protected FieldExpression parseExpression(int field, String expr) throws ParseException {
      if (expr != null && !expr.isEmpty()) {
         expr = expr.replaceAll("\\s+", "").toUpperCase(Locale.ENGLISH);
         if (expr.length() > 1 && expr.indexOf(",") > 0) {
            String[] expressions = expr.split(",");

            for(String subExpression : expressions) {
               this.validateExpression(field, subExpression);
            }
         } else {
            this.validateExpression(field, expr);
         }

         if (expr.equals("*")) {
            return new AsteriskExpression(field);
         } else {
            Matcher m = RANGE.matcher(expr);
            if (m.matches()) {
               return new RangeExpression(m, field);
            } else {
               switch (field) {
                  case 5:
                     if (expr.equals("LAST")) {
                        return new DaysFromLastDayExpression();
                     }

                     m = DAYS_TO_LAST.matcher(expr);
                     if (m.matches()) {
                        return new DaysFromLastDayExpression(m);
                     }

                     m = WEEKDAY.matcher(expr);
                     if (m.matches()) {
                        return new WeekdayExpression(m);
                     }
                  case 6:
                  case 7:
                  case 8:
                  case 9:
                  case 10:
                  default:
                     break;
                  case 11:
                  case 12:
                  case 13:
                     m = INCREMENTS.matcher(expr);
                     if (m.matches()) {
                        return new IncrementExpression(m, field);
                     }
               }

               m = LIST.matcher(expr);
               if (m.matches()) {
                  return new ListExpression(m, field);
               } else {
                  throw new ParseException(field, expr, "Unparseable time expression");
               }
            }
         }
      } else {
         throw new ParseException(field, expr, "expression can't be null");
      }
   }

   private void validateExpression(int field, String expression) throws ParseException {
      Matcher rangeMatcher = RANGE.matcher(expression);
      Matcher incrementsMatcher = INCREMENTS.matcher(expression);
      if (expression.length() > 2 && rangeMatcher.matches()) {
         this.validateSingleToken(field, rangeMatcher.group(1));
         this.validateSingleToken(field, rangeMatcher.group(2));
      } else if (expression.length() > 2 && incrementsMatcher.matches()) {
         this.validateSingleToken(field, incrementsMatcher.group(1));
         this.validateSingleToken(field, incrementsMatcher.group(2));
      } else {
         this.validateSingleToken(field, expression);
      }

   }

   private void validateSingleToken(int field, String token) throws ParseException {
      if (token != null && !token.isEmpty()) {
         switch (field) {
            case 1:
               Matcher m = VALID_YEAR.matcher(token);
               if (!m.matches()) {
                  throw new ParseException(field, token, "Valid YEAR is four digit");
               }
               break;
            case 2:
               Matcher m = VALID_MONTH.matcher(token);
               if (!m.matches() && !MONTHS_MAP.containsKey(token)) {
                  throw new ParseException(field, token, "Valid MONTH is 1-12 or {'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', Dec'}");
               }
            case 3:
            case 4:
            case 6:
            case 8:
            case 9:
            case 10:
            default:
               break;
            case 5:
               Matcher m = VALID_DAYS_OF_MONTH.matcher(token);
               if (!m.matches()) {
                  throw new ParseException(field, token, "Valid DAYS_OF_MONTH is 0-7 or {'Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'} ");
               }
               break;
            case 7:
               Matcher m = VALID_DAYS_OF_WEEK.matcher(token);
               if (!m.matches() && !WEEKDAYS_MAP.containsKey(token)) {
                  throw new ParseException(field, token, "Valid DAYS_OF_WEEK is 1-31  -(1-7) or {'1st', '2nd', '3rd', '4th',  '5th', 'Last'} + {'Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'} ");
               }
               break;
            case 11:
               Matcher m = VALID_HOUR.matcher(token);
               if (!m.matches()) {
                  throw new ParseException(field, token, "Valid HOUR_OF_DAY value is 0-23");
               }
               break;
            case 12:
               Matcher m = VALID_MINUTE.matcher(token);
               if (!m.matches()) {
                  throw new ParseException(field, token, "Valid MINUTE value is 0-59");
               }
               break;
            case 13:
               Matcher m = VALID_SECOND.matcher(token);
               if (!m.matches()) {
                  throw new ParseException(field, token, "Valid SECOND value is 0-59");
               }
         }

      } else {
         throw new ParseException(field, token, "expression can't be null");
      }
   }

   public Date getFinalFireTime() {
      Calendar calendar = new GregorianCalendar(this.timezone);
      calendar.setFirstDayOfWeek(1);
      if (this.getEndTime() == null) {
         if (this.expressions[0] instanceof AsteriskExpression) {
            return null;
         }

         this.resetFields(calendar, 0, true);
         calendar.set(14, 0);
      } else {
         calendar.setTime(this.getEndTime());
      }

      Calendar stopCalendar = new GregorianCalendar(this.timezone);
      if (this.getStartTime() != null) {
         stopCalendar.setTime(this.getStartTime());
      } else {
         stopCalendar.setTimeInMillis(0L);
      }

      int currentFieldIndex = 0;

      while(currentFieldIndex <= 6 && calendar.after(stopCalendar)) {
         FieldExpression expr = this.expressions[currentFieldIndex];
         Integer value = expr.getPreviousValue(calendar);
         if (value != null) {
            int oldValue = calendar.get(expr.field);
            if (oldValue != value) {
               calendar.set(expr.field, value);
               this.resetFields(calendar, expr.field, true);
               if (expr.field == 7) {
                  --currentFieldIndex;
               } else {
                  ++currentFieldIndex;
               }
            } else {
               ++currentFieldIndex;
            }
         } else {
            if (currentFieldIndex < 1) {
               return null;
            }

            int maxAffectedFieldType = this.upadteCalendar(calendar, this.expressions[currentFieldIndex - 1].field, -1);
            currentFieldIndex = (Integer)CALENDAR_FIELD_TYPE_ORDERED_INDEX_MAP.get(maxAffectedFieldType);
            this.resetFields(calendar, maxAffectedFieldType, true);
         }
      }

      return calendar.after(stopCalendar) ? calendar.getTime() : null;
   }

   public Date getFireTimeAfter(Date afterTime) {
      log.debug("start to getFireTimeAfter:" + afterTime);
      Calendar calendar = new GregorianCalendar(this.timezone);
      calendar.setFirstDayOfWeek(1);
      if (this.getStartTime() != null && this.getStartTime().after(afterTime)) {
         calendar.setTime(this.getStartTime());
      } else {
         calendar.setTime(afterTime);
         calendar.add(13, 1);
      }

      Calendar stopCalendar = new GregorianCalendar(this.timezone);
      if (this.getEndTime() != null) {
         stopCalendar.setTime(this.getEndTime());
      } else {
         int stopYear = calendar.get(1) + 100;
         stopCalendar.set(1, stopYear);
      }

      int currentFieldIndex = 0;

      while(currentFieldIndex <= 6 && calendar.before(stopCalendar)) {
         FieldExpression expr = this.expressions[currentFieldIndex];
         Integer value = expr.getNextValue(calendar);
         if (currentFieldIndex == 2 && !(this.expressions[3] instanceof AsteriskExpression)) {
            Calendar clonedCalendarDayOfWeek = (Calendar)calendar.clone();

            Integer nextDayOfWeek;
            for(nextDayOfWeek = this.expressions[3].getNextValue(clonedCalendarDayOfWeek); nextDayOfWeek == null; nextDayOfWeek = this.expressions[3].getNextValue(clonedCalendarDayOfWeek)) {
               clonedCalendarDayOfWeek.add(5, 1);
            }

            if (nextDayOfWeek != null) {
               clonedCalendarDayOfWeek.set(this.expressions[3].field, nextDayOfWeek);
               int newDayOfMonth = clonedCalendarDayOfWeek.get(this.expressions[2].field);
               if (value == null) {
                  value = newDayOfMonth;
               } else if (clonedCalendarDayOfWeek.get(this.expressions[1].field) == calendar.get(this.expressions[1].field)) {
                  value = Math.min(value, newDayOfMonth);
               }

               if (this.expressions[1].getNextValue(clonedCalendarDayOfWeek) == null) {
                  return null;
               }

               if (value != calendar.get(this.expressions[2].field) && clonedCalendarDayOfWeek.get(this.expressions[1].field) > calendar.get(this.expressions[1].field)) {
                  calendar.set(2, clonedCalendarDayOfWeek.get(2));
               }
            }
         }

         if (currentFieldIndex >= 1 && value == null) {
            if (currentFieldIndex == 3 && !(this.expressions[2] instanceof AsteriskExpression)) {
               ++currentFieldIndex;
            } else {
               int parentFieldIndex = currentFieldIndex == 4 ? currentFieldIndex - 2 : currentFieldIndex - 1;
               int maxAffectedFieldType = this.upadteCalendar(calendar, this.expressions[parentFieldIndex].field, 1);
               currentFieldIndex = (Integer)CALENDAR_FIELD_TYPE_ORDERED_INDEX_MAP.get(maxAffectedFieldType);
               this.resetFields(calendar, maxAffectedFieldType, false);
            }
         } else {
            if (value == null) {
               log.debug("end of getFireTimeAfter, result is:" + null);
               return null;
            }

            int oldValue = calendar.get(expr.field);
            if (oldValue != value) {
               if (currentFieldIndex == 3 && !(this.expressions[2] instanceof AsteriskExpression)) {
                  ++currentFieldIndex;
               } else {
                  calendar.set(expr.field, value);
                  this.resetFields(calendar, expr.field, false);
                  ++currentFieldIndex;
               }
            } else {
               ++currentFieldIndex;
            }
         }
      }

      log.debug("end of getFireTimeAfter, result is:" + (calendar.before(stopCalendar) ? calendar.getTime() : null));
      return calendar.before(stopCalendar) ? calendar.getTime() : null;
   }

   private int upadteCalendar(Calendar calendar, int field, int amount) {
      Calendar old = new GregorianCalendar(this.timezone);
      old.setTime(calendar.getTime());
      calendar.add(field, amount);

      for(int fieldType : ORDERED_CALENDAR_FIELDS) {
         if (calendar.get(fieldType) != old.get(fieldType)) {
            return fieldType;
         }
      }

      return -1;
   }

   public String getRawValue() {
      return this.rawValue;
   }

   private void resetFields(Calendar calendar, int currentField, boolean max) {
      for(int index = ORDERED_CALENDAR_FIELDS.length - 1; index >= 0; --index) {
         int calendarField = ORDERED_CALENDAR_FIELDS[index];
         if (calendarField <= currentField) {
            break;
         }

         int value = max ? calendar.getActualMaximum(calendarField) : calendar.getActualMinimum(calendarField);
         calendar.set(calendarField, value);
      }

   }

   public boolean hasAdditionalProperties() {
      return true;
   }

   static {
      log = Logger.getInstance(LogCategory.TIMER, EJBCronTrigger.class);
      INCREMENTS = Pattern.compile("(\\d+|\\*)/(\\d+)*");
      LIST = Pattern.compile("(([A-Za-z0-9]+)(-[A-Za-z0-9]+)?)?((1ST|2ND|3RD|4TH|5TH|LAST)([A-za-z]+))?(-([0-7]+))?(LAST)?(?:,(([A-Za-z0-9]+)(-[A-Za-z0-9]+)?)?((1ST|2ND|3RD|4TH|5TH|LAST)([A-za-z]+))?(-([0-7]+))?(LAST)?)*");
      WEEKDAY = Pattern.compile("(1ST|2ND|3RD|4TH|5TH|LAST)(SUN|MON|TUE|WED|THU|FRI|SAT)");
      DAYS_TO_LAST = Pattern.compile("-([0-7]+)");
      VALID_YEAR = Pattern.compile("([0-9][0-9][0-9][0-9])|\\*");
      VALID_MONTH = Pattern.compile("(([0]?[1-9])|(1[0-2]))|\\*");
      VALID_DAYS_OF_WEEK = Pattern.compile("[0-7]|\\*");
      VALID_DAYS_OF_MONTH = Pattern.compile("((1ST|2ND|3RD|4TH|5TH|LAST)(SUN|MON|TUE|WED|THU|FRI|SAT))|(([1-9])|(0[1-9])|([12])([0-9]?)|(3[01]?))|(LAST)|-([0-7])|[*]");
      VALID_HOUR = Pattern.compile("(([0-1]?[0-9])|([2][0-3]))|\\*");
      VALID_MINUTE = Pattern.compile("([0-5]?[0-9])|\\*");
      VALID_SECOND = Pattern.compile("([0-5]?[0-9])|\\*");
      RANGE = Pattern.compile("(-?[A-Za-z0-9]+)-(-?[A-Za-z0-9]+)");
      WEEKDAYS_MAP = new HashMap();
      MONTHS_MAP = new HashMap();
      int i = 0;

      for(String month : (new DateFormatSymbols(Locale.US)).getShortMonths()) {
         MONTHS_MAP.put(month.toUpperCase(Locale.US), i++);
      }

      i = 0;

      for(String weekday : (new DateFormatSymbols(Locale.US)).getShortWeekdays()) {
         WEEKDAYS_MAP.put(weekday.toUpperCase(Locale.US), i++);
      }

      ORDERED_CALENDAR_FIELDS = new int[]{1, 2, 5, 11, 12, 13};
      CALENDAR_FIELD_TYPE_ORDERED_INDEX_MAP = new LinkedHashMap();
      CALENDAR_FIELD_TYPE_ORDERED_INDEX_MAP.put(1, 0);
      CALENDAR_FIELD_TYPE_ORDERED_INDEX_MAP.put(2, 1);
      CALENDAR_FIELD_TYPE_ORDERED_INDEX_MAP.put(5, 2);
      CALENDAR_FIELD_TYPE_ORDERED_INDEX_MAP.put(7, 3);
      CALENDAR_FIELD_TYPE_ORDERED_INDEX_MAP.put(11, 4);
      CALENDAR_FIELD_TYPE_ORDERED_INDEX_MAP.put(12, 5);
      CALENDAR_FIELD_TYPE_ORDERED_INDEX_MAP.put(13, 6);
   }

   public static class ParseException extends Exception {
      private final Map<Integer, ParseException> children;
      private final Integer field;
      private final String value;
      private final String error;

      protected ParseException(int field, String value, String message) {
         this.children = null;
         this.field = field;
         this.value = value;
         this.error = message;
      }

      protected ParseException(Map<Integer, ParseException> children) {
         this.children = children;
         this.field = null;
         this.value = null;
         this.error = null;
      }

      public Map<Integer, ParseException> getChildren() {
         return this.children != null ? Collections.unmodifiableMap(this.children) : null;
      }

      public Integer getField() {
         return this.field;
      }

      public String getValue() {
         return this.value;
      }

      public String getError() {
         return this.error;
      }

      public String toString() {
         return "ParseException [field=" + this.field + ", value=" + this.value + ", error=" + this.error + "]";
      }
   }

   private abstract static class FieldExpression implements Serializable {
      protected static final Calendar CALENDAR;
      public final int field;

      protected static int convertValue(String value, int field) throws ParseException {
         if (Character.isDigit(value.charAt(0))) {
            int numValue;
            try {
               numValue = Integer.parseInt(value);
            } catch (NumberFormatException var4) {
               throw new ParseException(field, value, "Unparseable value");
            }

            if (field == 7) {
               ++numValue;
            } else if (field == 2) {
               --numValue;
            }

            return numValue;
         } else {
            switch (field) {
               case 2:
                  return (Integer)EJBCronTrigger.MONTHS_MAP.get(value);
               case 7:
                  return (Integer)EJBCronTrigger.WEEKDAYS_MAP.get(value);
               default:
                  throw new ParseException(field, value, "Unparseable value");
            }
         }
      }

      protected FieldExpression(int field) {
         this.field = field;
      }

      protected int convertValue(String value) throws ParseException {
         return convertValue(value, this.field);
      }

      protected boolean isValidResult(Calendar calendar, Integer result) {
         return result != null && result >= calendar.getActualMinimum(this.field) && result <= calendar.getActualMaximum(this.field);
      }

      public abstract Integer getNextValue(Calendar var1);

      public abstract Integer getPreviousValue(Calendar var1);

      static {
         CALENDAR = new GregorianCalendar(Locale.US);
      }
   }

   private static class RangeExpression extends FieldExpression {
      private int start;
      private int end;
      private int start2 = -1;
      private String startWeekDay;
      private String endWeekDay;
      private WeekdayExpression startWeekdayExpr;
      private WeekdayExpression endWeekdayExpr;
      private DaysFromLastDayExpression startDaysFromLastDayExpr;
      private DaysFromLastDayExpression endDaysFromLastDayExpr;
      private boolean isDynamicRangeExpression;

      public boolean isDynamicRangeExpression() {
         return this.isDynamicRangeExpression;
      }

      public RangeExpression(int field, int start, int end, int start2) {
         super(field);
         this.start = start;
         this.end = end;
         this.start2 = start2;
      }

      public RangeExpression(Matcher m, int field) throws ParseException {
         super(field);
         this.startWeekDay = m.group(1);
         this.endWeekDay = m.group(2);
         if (field == 5) {
            Matcher startWeekDayMatcher = EJBCronTrigger.WEEKDAY.matcher(m.group(1));
            Matcher endWeekDayMatcher = EJBCronTrigger.WEEKDAY.matcher(m.group(2));
            Matcher startDaysFromLastDayMatcher = EJBCronTrigger.DAYS_TO_LAST.matcher(m.group(1));
            Matcher endDaysFromLastDayMatcher = EJBCronTrigger.DAYS_TO_LAST.matcher(m.group(2));
            if (startWeekDayMatcher.matches()) {
               this.startWeekdayExpr = new WeekdayExpression(startWeekDayMatcher);
            }

            if (endWeekDayMatcher.matches()) {
               this.endWeekdayExpr = new WeekdayExpression(endWeekDayMatcher);
            }

            if (startDaysFromLastDayMatcher.matches()) {
               this.startDaysFromLastDayExpr = new DaysFromLastDayExpression(startDaysFromLastDayMatcher);
            }

            if (endDaysFromLastDayMatcher.matches()) {
               this.endDaysFromLastDayExpr = new DaysFromLastDayExpression(endDaysFromLastDayMatcher);
            }

            if (this.startWeekdayExpr != null || this.endWeekdayExpr != null || this.startDaysFromLastDayExpr != null || this.endDaysFromLastDayExpr != null || this.startWeekDay.equals("LAST") || this.endWeekDay.equals("LAST")) {
               this.isDynamicRangeExpression = true;
               return;
            }
         }

         this.initStartEndValues((Calendar)null);
      }

      private void initStartEndValues(Calendar calendar) throws ParseException {
         int beginValue;
         int endValue;
         if (this.isDynamicRangeExpression) {
            if (this.startWeekDay.equals("LAST")) {
               beginValue = calendar.getActualMaximum(this.field);
            } else if (this.startWeekdayExpr != null) {
               beginValue = this.startWeekdayExpr.getWeekdayInMonth(calendar);
            } else if (this.startDaysFromLastDayExpr != null) {
               Integer next = this.startDaysFromLastDayExpr.getNextValue(calendar);
               beginValue = next == null ? calendar.get(this.field) : next;
            } else {
               beginValue = this.convertValue(this.startWeekDay);
            }

            if (this.endWeekDay.equals("LAST")) {
               endValue = calendar.getActualMaximum(this.field);
            } else if (this.endWeekdayExpr != null) {
               endValue = this.endWeekdayExpr.getWeekdayInMonth(calendar);
            } else if (this.endDaysFromLastDayExpr != null) {
               Integer next = this.endDaysFromLastDayExpr.getNextValue(calendar);
               endValue = next == null ? calendar.get(this.field) : next;
            } else {
               endValue = this.convertValue(this.endWeekDay);
            }
         } else {
            beginValue = this.convertValue(this.startWeekDay);
            endValue = this.convertValue(this.endWeekDay);
         }

         if (this.field == 7) {
            if ((beginValue != 8 || endValue != 1) && (endValue != 8 || beginValue != 1)) {
               if (beginValue == 8) {
                  beginValue = 1;
               }

               if (endValue == 8) {
                  endValue = 1;
               }
            } else {
               beginValue = 1;
               endValue = 7;
            }
         }

         if (this.endWeekDay.equals("LAST")) {
            this.start = -1;
            this.end = -1;
            this.start2 = beginValue;
         } else if (beginValue > endValue) {
            this.start = CALENDAR.getMinimum(this.field);
            this.end = endValue;
            this.start2 = beginValue;
         } else {
            this.start = beginValue;
            this.end = endValue;
         }

      }

      public Integer getNextValue(Calendar calendar) {
         if (this.isDynamicRangeExpression) {
            Integer nextStartWeekday = this.startWeekdayExpr == null ? this.start : this.startWeekdayExpr.getWeekdayInMonth(calendar);
            Integer nextendWeekday = this.endWeekdayExpr == null ? this.end : this.endWeekdayExpr.getWeekdayInMonth(calendar);
            if (nextStartWeekday == null || nextendWeekday == null) {
               return null;
            }

            try {
               this.initStartEndValues(calendar);
            } catch (ParseException var5) {
               return null;
            }
         }

         int currValue = calendar.get(this.field);
         if (this.start2 != -1) {
            if (currValue >= this.start2) {
               return this.isValidResult(calendar, currValue) ? currValue : null;
            }

            if (currValue > this.end) {
               return this.isValidResult(calendar, this.start2) ? this.start2 : null;
            }
         }

         if (currValue <= this.start) {
            return this.isValidResult(calendar, this.start) ? this.start : null;
         } else if (currValue <= this.end) {
            return this.isValidResult(calendar, currValue) ? currValue : null;
         } else {
            return null;
         }
      }

      public Integer getPreviousValue(Calendar calendar) {
         if (this.isDynamicRangeExpression) {
            try {
               this.initStartEndValues(calendar);
            } catch (ParseException var3) {
               return null;
            }
         }

         int currValue = calendar.get(this.field);
         if (this.start2 != -1 && currValue >= this.start2) {
            return this.isValidResult(calendar, currValue) ? currValue : null;
         } else if (currValue <= this.start) {
            return null;
         } else if (currValue <= this.end) {
            return this.isValidResult(calendar, currValue) ? currValue : null;
         } else {
            return this.isValidResult(calendar, this.end) ? this.end : null;
         }
      }

      public List<Integer> getAllValuesInRange(Calendar calendar) {
         List<Integer> values = new ArrayList();
         if (this.isDynamicRangeExpression) {
            try {
               this.initStartEndValues(calendar);
            } catch (ParseException var4) {
               return values;
            }
         }

         if (this.start2 == -1) {
            for(int i = this.start; i <= this.end; ++i) {
               values.add(i);
            }
         } else {
            for(int i = this.start; i <= this.end; ++i) {
               values.add(i);
            }

            for(int i = this.start2; i <= CALENDAR.getMaximum(this.field); ++i) {
               values.add(i);
            }
         }

         return values;
      }
   }

   private static class ListExpression extends FieldExpression {
      private final Set<Integer> values = new TreeSet();
      private final List<RangeExpression> weekDayRangeExpressions = new ArrayList();
      private final List<WeekdayExpression> weekDayExpressions = new ArrayList();
      private final List<DaysFromLastDayExpression> daysFromLastDayExpressions = new ArrayList();

      public ListExpression(Matcher m, int field) throws ParseException {
         super(field);
         this.initialize(m);
      }

      private void initialize(Matcher m) throws ParseException {
         for(String value : m.group().split("[,]")) {
            Matcher rangeMatcher = EJBCronTrigger.RANGE.matcher(value);
            Matcher weekDayMatcher = EJBCronTrigger.WEEKDAY.matcher(value);
            Matcher daysToLastMatcher = EJBCronTrigger.DAYS_TO_LAST.matcher(value);
            if (value.equals("LAST")) {
               this.daysFromLastDayExpressions.add(new DaysFromLastDayExpression());
            } else if (daysToLastMatcher.matches()) {
               this.daysFromLastDayExpressions.add(new DaysFromLastDayExpression(daysToLastMatcher));
            } else if (weekDayMatcher.matches()) {
               this.weekDayExpressions.add(new WeekdayExpression(weekDayMatcher));
            } else if (rangeMatcher.matches()) {
               RangeExpression rangeExpression = new RangeExpression(rangeMatcher, this.field);
               if (rangeExpression.isDynamicRangeExpression()) {
                  this.weekDayRangeExpressions.add(new RangeExpression(rangeMatcher, this.field));
               } else {
                  this.values.addAll(rangeExpression.getAllValuesInRange((Calendar)null));
               }
            } else {
               int individualValue = this.convertValue(value);
               if (this.field == 7 && individualValue == 8) {
                  individualValue = 1;
               }

               this.values.add(individualValue);
            }
         }

      }

      private TreeSet<Integer> getNewValuesFromDynamicExpressions(Calendar calendar) {
         TreeSet<Integer> newValues = new TreeSet();
         newValues.addAll(this.values);

         for(RangeExpression weekDayRangeExpression : this.weekDayRangeExpressions) {
            newValues.addAll(weekDayRangeExpression.getAllValuesInRange(calendar));
         }

         for(WeekdayExpression weekdayExpression : this.weekDayExpressions) {
            Integer value = weekdayExpression.getNextValue(calendar);
            if (value != null) {
               newValues.add(value);
            }
         }

         for(DaysFromLastDayExpression daysFromLastDayExpression : this.daysFromLastDayExpressions) {
            Integer value = daysFromLastDayExpression.getNextValue(calendar);
            if (value != null) {
               newValues.add(value);
            }
         }

         return newValues;
      }

      public Integer getNextValue(Calendar calendar) {
         TreeSet<Integer> newValues = this.getNewValuesFromDynamicExpressions(calendar);
         int currValue = calendar.get(this.field);
         Integer result = (Integer)newValues.ceiling(currValue);
         return this.isValidResult(calendar, result) ? result : null;
      }

      public Integer getPreviousValue(Calendar calendar) {
         TreeSet<Integer> newValues = this.getNewValuesFromDynamicExpressions(calendar);
         int currValue = calendar.get(this.field);
         Integer result = (Integer)newValues.floor(currValue);
         return this.isValidResult(calendar, result) ? result : null;
      }
   }

   private static class IncrementExpression extends FieldExpression {
      private final int start;
      private final int interval;

      public IncrementExpression(Matcher m, int field) {
         super(field);
         int minValue = CALENDAR.getMinimum(field);
         this.start = m.group(1).equals("*") ? minValue : Integer.parseInt(m.group(1));
         this.interval = Integer.parseInt(m.group(2));
      }

      public Integer getNextValue(Calendar calendar) {
         int currValue = calendar.get(this.field);
         if (currValue > this.start) {
            for(Integer nextValue = this.start + this.interval; this.isValidResult(calendar, nextValue); nextValue = nextValue + this.interval) {
               if (nextValue >= currValue) {
                  return nextValue;
               }
            }

            return null;
         } else {
            return new Integer(this.start);
         }
      }

      public Integer getPreviousValue(Calendar calendar) {
         int currValue = calendar.get(this.field);
         if (currValue < this.start) {
            for(Integer previousValue = this.start - this.interval; this.isValidResult(calendar, previousValue); previousValue = previousValue - this.interval) {
               if (previousValue < currValue) {
                  return previousValue;
               }
            }

            return null;
         } else {
            return new Integer(this.start);
         }
      }
   }

   private static class WeekdayExpression extends FieldExpression {
      private final Integer ordinal;
      private final int weekday;

      public WeekdayExpression(Matcher m) throws ParseException {
         super(5);
         Character firstChar = m.group(1).charAt(0);
         this.ordinal = Character.isDigit(firstChar) ? Integer.valueOf(firstChar.toString()) : null;
         this.weekday = convertValue(m.group(2), 7);
      }

      public Integer getNextValue(Calendar calendar) {
         int currDay = calendar.get(5);
         Integer nthDay = this.getWeekdayInMonth(calendar);
         Integer result = nthDay != null && nthDay >= currDay ? nthDay : null;
         return this.isValidResult(calendar, result) ? result : null;
      }

      public Integer getWeekdayInMonth(Calendar calendar) {
         int currDay = calendar.get(5);
         int currWeekday = calendar.get(7);
         int maxDay = calendar.getActualMaximum(5);
         int firstWeekday = currDay % 7 - (currWeekday - this.weekday);
         firstWeekday = firstWeekday == 0 ? 7 : firstWeekday;
         int numWeekdays = firstWeekday >= 0 ? (maxDay - firstWeekday) / 7 + 1 : (maxDay - firstWeekday) / 7;
         int multiplier = this.ordinal != null ? this.ordinal : numWeekdays;
         int nthDay = firstWeekday >= 0 ? firstWeekday + (multiplier - 1) * 7 : firstWeekday + multiplier * 7;
         return nthDay <= maxDay ? nthDay : null;
      }

      public Integer getPreviousValue(Calendar calendar) {
         int currDay = calendar.get(5);
         Integer nthDay = this.getWeekdayInMonth(calendar);
         Integer result = nthDay != null && nthDay <= currDay ? nthDay : null;
         return this.isValidResult(calendar, result) ? result : null;
      }
   }

   private static class DaysFromLastDayExpression extends FieldExpression {
      private final int days;

      public DaysFromLastDayExpression(Matcher m) {
         super(5);
         this.days = new Integer(m.group(1));
      }

      public DaysFromLastDayExpression() {
         super(5);
         this.days = 0;
      }

      public Integer getNextValue(Calendar calendar) {
         int currValue = calendar.get(this.field);
         int maxValue = calendar.getActualMaximum(this.field);
         int value = maxValue - this.days;
         Integer result = currValue <= value ? value : null;
         return this.isValidResult(calendar, result) ? result : null;
      }

      public Integer getPreviousValue(Calendar calendar) {
         int maxValue = calendar.getActualMaximum(this.field);
         Integer result = maxValue - this.days;
         return this.isValidResult(calendar, result) ? result : null;
      }
   }

   private static class AsteriskExpression extends FieldExpression {
      public AsteriskExpression(int field) {
         super(field);
      }

      public Integer getNextValue(Calendar calendar) {
         return calendar.get(this.field);
      }

      public Integer getPreviousValue(Calendar calendar) {
         return calendar.get(this.field);
      }
   }
}
