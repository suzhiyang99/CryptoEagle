package org.apache.wicket.util.convert.converter;

import java.util.Date;

public class DateConverter extends AbstractDateConverter<Date> {
   private static final long serialVersionUID = 1L;

   protected Date createDateLike(long date) {
      return new Date(date);
   }

   protected Class<Date> getTargetType() {
      return Date.class;
   }
}
