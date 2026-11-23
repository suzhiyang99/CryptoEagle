package org.apache.openejb.util.proxy;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;
import org.apache.commons.lang3.StringUtils;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public class QueryProxy implements InvocationHandler {
   private static final Logger LOGGER;
   public static final String PERSIST_NAME = "save";
   public static final String MERGE_NAME = "update";
   public static final String REMOVE_NAME = "delete";
   public static final String NAMED_QUERY_NAME = "namedQuery";
   public static final String NATIVE_QUERY_NAME = "nativeQuery";
   public static final String QUERY_NAME = "query";
   public static final String FIND_PREFIX = "find";
   public static final String BY = "By";
   public static final String AND = "And";
   private final Map<String, Class<?>> returnsTypes = new ConcurrentHashMap();
   private final Map<String, List<String>> conditions = new ConcurrentHashMap();
   private EntityManager em;

   public void setEntityManager(EntityManager entityManager) {
      this.em = entityManager;
   }

   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      if (method.getDeclaringClass().equals(Object.class)) {
         return method.invoke(this, args);
      } else {
         String methodName = method.getName();
         Class<?> returnType = method.getReturnType();
         if ("save".equals(methodName)) {
            this.persist(args, returnType);
            return null;
         } else if ("update".equals(methodName)) {
            return this.merge(args, returnType);
         } else if ("delete".equals(methodName)) {
            this.remove(args, returnType);
            return null;
         } else if ("namedQuery".equals(methodName)) {
            return this.query(method, args, QueryProxy.QueryType.NAMED);
         } else if ("nativeQuery".equals(methodName)) {
            return this.query(method, args, QueryProxy.QueryType.NATIVE);
         } else if ("query".equals(methodName)) {
            return this.query(method, args, QueryProxy.QueryType.OTHER);
         } else if (methodName.startsWith("find")) {
            return this.find(method, args);
         } else {
            throw new IllegalArgumentException("method not yet managed");
         }
      }
   }

   private Object query(Method method, Object[] args, QueryType type) {
      if (args.length < 1) {
         throw new IllegalArgumentException("query() needs at least the query name");
      } else {
         int matched = 0;
         if (!String.class.isAssignableFrom(args[0].getClass())) {
            throw new IllegalArgumentException("query() needs at least the query name of type String");
         } else {
            Query query;
            switch (type) {
               case NAMED:
                  query = this.em.createNamedQuery((String)args[0]);
                  break;
               case NATIVE:
                  query = this.em.createNativeQuery((String)args[0]);
                  break;
               default:
                  query = this.em.createQuery((String)args[0]);
            }

            ++matched;

            for(int i = 1; i < args.length; ++i) {
               if (args[i] != null) {
                  if (Map.class.isAssignableFrom(args[i].getClass())) {
                     for(Map.Entry<String, ?> entry : ((Map)args[i]).entrySet()) {
                        query = query.setParameter((String)entry.getKey(), entry.getValue());
                     }

                     ++matched;
                  } else if (args[i].getClass().isArray()) {
                     Object[] array = args[i];

                     for(int j = 0; j < array.length; ++j) {
                        query = query.setParameter(j, array[j]);
                     }

                     ++matched;
                  } else {
                     if (!this.isInt(args[i].getClass())) {
                        throw new IllegalArgumentException("not managed parameter " + args[i] + " of type " + args[i].getClass());
                     }

                     int next = i + 1;
                     if (args.length == next || !this.isInt(args[next].getClass())) {
                        throw new IllegalArgumentException("if you provide a firstResult (first int parameter)you should provide a maxResult too");
                     }

                     int first = (Integer)args[i];
                     int max = (Integer)args[next];
                     query = query.setFirstResult(first);
                     query = query.setMaxResults(max);
                     matched += 2;
                     ++i;
                  }
               }
            }

            if (matched != args.length) {
               throw new IllegalArgumentException("all argument was not used, please check you signature looks like: <ReturnType> query(String name, Map<String, ?> parameters, int firstResult, int maxResult)");
            } else {
               return this.getQueryResult(method, query);
            }
         }
      }
   }

   private Class<?> getReturnedType(Method method) {
      String methodName = method.getName();
      Class<?> type;
      if (this.returnsTypes.containsKey(methodName)) {
         type = (Class)this.returnsTypes.get(methodName);
      } else {
         type = this.getGenericType(method.getGenericReturnType());
         this.returnsTypes.put(methodName, type);
      }

      return type;
   }

   private Object getQueryResult(Method method, Query query) {
      return Collection.class.isAssignableFrom(method.getReturnType()) ? query.getResultList() : query.getSingleResult();
   }

   private Object find(Method method, Object[] args) {
      String methodName = method.getName();
      Class<?> type = this.getReturnedType(method);
      Query query = this.createFinderQuery(this.em, methodName, type, args);
      return this.getQueryResult(method, query);
   }

   private void remove(Object[] args, Class<?> returnType) {
      if (args != null && args.length == 1 && returnType.equals(Void.TYPE)) {
         Object entity = args[0];
         if (!this.em.contains(entity)) {
            Class<?> entityClass = entity.getClass();
            EntityType<? extends Object> et = this.em.getMetamodel().entity(entityClass);
            if (!et.hasSingleIdAttribute()) {
               throw new IllegalArgumentException("Dynamic EJB doesn't manage IdClass yet");
            }

            SingularAttribute<?, ?> id = null;

            for(SingularAttribute<?, ?> sa : et.getSingularAttributes()) {
               if (sa.isId()) {
                  id = sa;
                  break;
               }
            }

            if (id == null) {
               throw new IllegalArgumentException("id field not found");
            }

            String idName = id.getName();
            Object idValue = this.getProperty(entity, idName);
            entity = this.em.getReference(et.getJavaType(), idValue);
            if (entity == null) {
               throw new IllegalArgumentException("entity " + entity + " is not managed and can't be found.");
            }
         }

         this.em.remove(entity);
      } else {
         throw new IllegalArgumentException("delete should have only one parameter and return void");
      }
   }

   private Object getProperty(Object entity, String idName) {
      try {
         Method method = entity.getClass().getMethod("get" + StringUtils.capitalize(idName));
         if (!method.isAccessible()) {
            method.setAccessible(true);
         }

         return method.invoke(entity);
      } catch (IllegalAccessException | NoSuchMethodException var6) {
         try {
            Field declaredField = entity.getClass().getDeclaredField(idName);
            if (!declaredField.isAccessible()) {
               declaredField.setAccessible(true);
            }

            return declaredField.get(entity);
         } catch (IllegalAccessException | NoSuchFieldException var5) {
            throw new IllegalArgumentException("Bad id: " + idName + " for " + entity);
         }
      } catch (InvocationTargetException e) {
         throw new IllegalStateException(e.getCause());
      }
   }

   private Object merge(Object[] args, Class<?> returnType) {
      if (args != null && args.length == 1 && returnType.equals(args[0].getClass())) {
         return this.em.merge(args[0]);
      } else {
         throw new IllegalArgumentException("update should have only one parameter and return the same type than the parameter type");
      }
   }

   private void persist(Object[] args, Class<?> returnType) {
      if (args != null && args.length == 1 && returnType.equals(Void.TYPE)) {
         this.em.persist(args[0]);
      } else {
         throw new IllegalArgumentException("save should have only one parameter and return void");
      }
   }

   private Class<?> getGenericType(Type type) {
      if (type instanceof ParameterizedType) {
         ParameterizedType pt = (ParameterizedType)type;
         if (pt.getActualTypeArguments().length == 1) {
            return (Class)pt.getActualTypeArguments()[0];
         }
      }

      return (Class)Class.class.cast(type);
   }

   private <T> Query createFinderQuery(EntityManager entityManager, String methodName, Class<T> entityType, Object[] args) {
      List<String> conditions = this.parseMethodName(methodName);
      EntityType<T> et = entityManager.getMetamodel().entity(entityType);
      CriteriaBuilder cb = entityManager.getCriteriaBuilder();
      CriteriaQuery<Object> query = cb.createQuery();
      Root<T> from = query.from(entityType);
      query = query.select(from);
      int i = 0;
      Predicate where = null;

      for(String condition : conditions) {
         SingularAttribute<? super T, ?> attribute = et.getSingularAttribute(condition);
         Path<?> path = from.get(attribute);
         Class<?> javaType = attribute.getType().getJavaType();
         Predicate currentClause;
         if (javaType.equals(String.class)) {
            currentClause = cb.like(path, (String)args[i++]);
         } else {
            if (!Number.class.isAssignableFrom(javaType) && !javaType.isPrimitive()) {
               LOGGER.warning("field " + condition + " not found, ignoring");
               continue;
            }

            currentClause = cb.equal(path, args[i++]);
         }

         if (where == null) {
            where = currentClause;
         } else {
            where = cb.and(where, currentClause);
         }
      }

      if (where != null) {
         query = query.where(where);
      }

      TypedQuery<?> emQuery = entityManager.createQuery(query);
      if (args != null && args.length == conditions.size() + 2 && this.isInt(args[args.length - 2].getClass()) && this.isInt(args[args.length - 1].getClass())) {
         int first = (Integer)args[args.length - 2];
         int max = (Integer)args[args.length - 1];
         emQuery.setFirstResult(first);
         emQuery.setMaxResults(max);
      }

      return emQuery;
   }

   private boolean isInt(Class<?> aClass) {
      return Integer.TYPE.equals(aClass) || Integer.class.equals(aClass);
   }

   private List<String> parseMethodName(String methodName) {
      List<String> parsed;
      if (this.conditions.containsKey(methodName)) {
         parsed = (List)this.conditions.get(methodName);
      } else {
         parsed = new ArrayList();
         String toParse = methodName.substring("find".length());
         if (toParse.startsWith("By")) {
            toParse = toParse.substring(2);
            String[] columns = toParse.split("And");

            for(String column : columns) {
               parsed.add(StringUtils.uncapitalize(column));
            }
         }

         this.conditions.put(methodName, parsed);
      }

      return parsed;
   }

   public String toString() {
      return "OpenEJB :: QueryProxy";
   }

   static {
      LOGGER = Logger.getInstance(LogCategory.OPENEJB, QueryProxy.class);
   }

   private static enum QueryType {
      NAMED,
      NATIVE,
      OTHER;
   }
}
