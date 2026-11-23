package org.apache.openejb.assembler.classic;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.openejb.BeanContext;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.core.transaction.TransactionType;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public class MethodTransactionBuilder {
   public static final Logger logger;

   public void build(HashMap<String, BeanContext> deployments, List<MethodTransactionInfo> methodTransactions) throws OpenEJBException {
      for(BeanContext beanContext : deployments.values()) {
         applyTransactionAttributes(beanContext, methodTransactions);
      }

   }

   public static void applyTransactionAttributes(BeanContext beanContext, List<MethodTransactionInfo> methodTransactionInfos) throws OpenEJBException {
      if (!beanContext.isBeanManagedTransaction()) {
         methodTransactionInfos = normalize(methodTransactionInfos);
         Map<MethodInfoUtil.ViewMethod, MethodAttributeInfo> attributes = MethodInfoUtil.resolveViewAttributes(methodTransactionInfos, beanContext);
         Logger log = Logger.getInstance(LogCategory.OPENEJB_STARTUP.createChild("attributes"), MethodTransactionBuilder.class);
         boolean debug = log.isDebugEnabled();

         for(Map.Entry<MethodInfoUtil.ViewMethod, MethodAttributeInfo> entry : attributes.entrySet()) {
            MethodInfoUtil.ViewMethod viewMethod = (MethodInfoUtil.ViewMethod)entry.getKey();
            Method method = viewMethod.getMethod();
            String view = viewMethod.getView();
            MethodTransactionInfo transactionInfo = (MethodTransactionInfo)entry.getValue();
            if (debug) {
               log.debug("Transaction Attribute: " + method + " -- " + MethodInfoUtil.toString(transactionInfo));
            }

            beanContext.setMethodTransactionAttribute(method, TransactionType.get(transactionInfo.transAttribute), view);
         }

      }
   }

   public static List<MethodTransactionInfo> normalize(List<MethodTransactionInfo> infos) {
      List<MethodTransactionInfo> normalized = new ArrayList();

      for(MethodTransactionInfo oldInfo : infos) {
         for(MethodInfo methodInfo : oldInfo.methods) {
            MethodTransactionInfo newInfo = new MethodTransactionInfo();
            newInfo.description = oldInfo.description;
            newInfo.methods.add(methodInfo);
            newInfo.transAttribute = oldInfo.transAttribute;
            normalized.add(newInfo);
         }
      }

      Collections.reverse(normalized);
      Collections.sort(normalized, new MethodTransactionComparator());
      return normalized;
   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB_STARTUP, MethodTransactionBuilder.class);
   }

   public static class MethodTransactionComparator extends MethodInfoUtil.BaseComparator<MethodTransactionInfo> {
      public int compare(MethodTransactionInfo a, MethodTransactionInfo b) {
         return this.compare((MethodInfo)((MethodInfo)a.methods.get(0)), (MethodInfo)((MethodInfo)b.methods.get(0)));
      }
   }
}
