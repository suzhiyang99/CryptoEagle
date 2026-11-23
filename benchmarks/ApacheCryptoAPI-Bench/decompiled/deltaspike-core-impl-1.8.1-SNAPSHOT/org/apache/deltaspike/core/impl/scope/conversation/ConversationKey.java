package org.apache.deltaspike.core.impl.scope.conversation;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.inject.Named;
import org.apache.deltaspike.core.api.scope.ConversationGroup;

public class ConversationKey implements Serializable {
   private static final long serialVersionUID = 6565204223928766263L;
   private final Class<?> groupKey;
   private HashSet<Annotation> qualifiers;

   public ConversationKey(Class<?> groupKey, Annotation... qualifiers) {
      this.groupKey = groupKey;

      for(Annotation qualifier : qualifiers) {
         Class<? extends Annotation> annotationType = qualifier.annotationType();
         if (!Any.class.isAssignableFrom(annotationType) && !Default.class.isAssignableFrom(annotationType) && !Named.class.isAssignableFrom(annotationType) && !ConversationGroup.class.isAssignableFrom(annotationType)) {
            if (this.qualifiers == null) {
               this.qualifiers = new HashSet();
            }

            this.qualifiers.add(qualifier);
         }
      }

   }

   public Class<?> getConversationGroup() {
      return this.groupKey;
   }

   public Set<Annotation> getQualifiers() {
      return this.qualifiers == null ? Collections.emptySet() : Collections.unmodifiableSet(this.qualifiers);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof ConversationKey)) {
         return false;
      } else {
         ConversationKey that = (ConversationKey)o;
         if (!this.groupKey.equals(that.groupKey)) {
            return false;
         } else if (this.qualifiers == null && that.qualifiers == null) {
            return true;
         } else if (this.qualifiers != null && that.qualifiers == null) {
            return false;
         } else {
            return that.qualifiers.equals(this.qualifiers);
         }
      }
   }

   public int hashCode() {
      int result = this.groupKey.hashCode();
      result = 31 * result + (this.qualifiers != null ? this.qualifiers.hashCode() : 0);
      return result;
   }

   public String toString() {
      StringBuilder result = new StringBuilder("conversation-key\n");
      result.append("\n");
      result.append("\tgroup:\t\t");
      result.append(this.groupKey.getName());
      result.append("\n");
      result.append("\tqualifiers:\t");
      if (this.qualifiers != null) {
         for(Annotation qualifier : this.qualifiers) {
            result.append(qualifier.annotationType().getName());
            result.append(" ");
         }
      } else {
         result.append("---");
      }

      return result.toString();
   }
}
