package org.apache.deltaspike.core.impl.exception.control;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import javax.enterprise.inject.Typed;
import org.apache.deltaspike.core.api.exception.control.HandlerMethod;
import org.apache.deltaspike.core.api.literal.AnyLiteral;
import org.apache.deltaspike.core.util.HierarchyDiscovery;

@Typed
public final class ExceptionHandlerComparator implements Comparator<HandlerMethod<?>> {
   public int compare(HandlerMethod<?> lhs, HandlerMethod<?> rhs) {
      if (lhs.equals(rhs)) {
         return 0;
      } else if (lhs.getExceptionType().equals(rhs.getExceptionType()) && lhs.getQualifiers().equals(rhs.getQualifiers())) {
         int precedenceReturnValue = this.comparePrecedence(lhs.getOrdinal(), rhs.getOrdinal(), lhs.isBeforeHandler());
         return precedenceReturnValue == 0 ? -1 : precedenceReturnValue;
      } else if (lhs.getExceptionType().equals(rhs.getExceptionType()) && !lhs.getQualifiers().equals(rhs.getQualifiers())) {
         return lhs.getQualifiers().contains(new AnyLiteral()) ? -1 : 1;
      } else {
         return this.compareHierarchies(lhs.getExceptionType(), rhs.getExceptionType());
      }
   }

   private int compareHierarchies(Type lhsExceptionType, Type rhsExceptionType) {
      HierarchyDiscovery lhsHierarchy = new HierarchyDiscovery(lhsExceptionType);
      Set<Type> lhsTypeclosure = lhsHierarchy.getTypeClosure();
      if (lhsTypeclosure.contains(rhsExceptionType)) {
         int indexOfLhsType = (new ArrayList(lhsTypeclosure)).indexOf(lhsExceptionType);
         int indexOfRhsType = (new ArrayList(lhsTypeclosure)).indexOf(rhsExceptionType);
         if (indexOfLhsType > indexOfRhsType) {
            return 1;
         }
      }

      return -1;
   }

   private int comparePrecedence(int lhs, int rhs, boolean isLhsBefore) {
      return !isLhsBefore ? lhs - rhs : (lhs - rhs) * -1;
   }
}
