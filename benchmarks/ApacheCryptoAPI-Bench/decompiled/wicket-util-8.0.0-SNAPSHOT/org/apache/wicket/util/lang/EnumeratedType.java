package org.apache.wicket.util.lang;

import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.wicket.util.string.StringValue;

public abstract class EnumeratedType extends StringValue {
   private static final long serialVersionUID = 1L;
   private static final Map<String, List<EnumeratedType>> valueListByClass = Generics.<String, List<EnumeratedType>>newConcurrentHashMap();

   public EnumeratedType(String name) {
      super(name);
      getValues(this.getClass()).add(this);
   }

   public static List<EnumeratedType> getValues(Class<? extends EnumeratedType> c) {
      List<EnumeratedType> valueList = (List)valueListByClass.get(c.getName());
      if (valueList == null) {
         valueList = new ArrayList();
         valueListByClass.put(c.getName(), valueList);
      }

      return valueList;
   }

   public Object readResolve() throws ObjectStreamException {
      EnumeratedType result = this;

      for(EnumeratedType value : getValues(this.getClass())) {
         if (value.toString() != null && value.toString().equals(this.toString())) {
            result = value;
            break;
         }
      }

      return result;
   }
}
