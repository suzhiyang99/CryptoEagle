package org.apache.openejb.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import org.apache.openejb.loader.IO;

public class Debug {
   public static String printStackTrace(Throwable t) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      t.printStackTrace(new PrintStream(baos));
      return new String(baos.toByteArray());
   }

   public static Map<String, Object> contextToMap(Context context) throws NamingException {
      Map<String, Object> map = new TreeMap(String.CASE_INSENSITIVE_ORDER);
      contextToMap(context, "", map);
      return map;
   }

   public static void contextToMap(Context context, String baseName, Map<String, Object> results) throws NamingException {
      NamingEnumeration<Binding> namingEnumeration = context.listBindings("");

      while(namingEnumeration.hasMoreElements()) {
         Binding binding = (Binding)namingEnumeration.nextElement();
         String name = binding.getName();
         String fullName = baseName + name;
         Object object = binding.getObject();
         results.put(fullName, object);
         if (object instanceof Context) {
            contextToMap((Context)object, fullName + "/", results);
         }
      }

   }

   public static Map<String, Object> printContext(Context context) throws NamingException {
      return printContext(context, System.out);
   }

   public static Map<String, Object> printContext(Context context, PrintStream out) throws NamingException {
      Map<String, Object> map = contextToMap(context);

      for(Map.Entry<String, Object> entry : map.entrySet()) {
         out.println((String)entry.getKey() + "=" + entry.getValue().getClass().getName());
      }

      return map;
   }

   public static Map<String, Object> printContextValues(Context context) throws NamingException {
      return printContextValues(context, System.out);
   }

   public static Map<String, Object> printContextValues(Context context, PrintStream out) throws NamingException {
      Map<String, Object> map = contextToMap(context);

      for(Map.Entry<String, Object> entry : map.entrySet()) {
         out.println((String)entry.getKey() + "=" + entry.getValue());
      }

      return map;
   }

   public static List<Field> getFields(Class clazz) {
      if (clazz == null) {
         return Collections.EMPTY_LIST;
      } else {
         List<Field> fields = new ArrayList();
         fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
         fields.addAll(getFields(clazz.getSuperclass()));
         return fields;
      }
   }

   public static class Trace {
      private static final Trace trace = new Trace();
      private final Map<String, Node> elements = new LinkedHashMap();
      private final List<Event> events = new ArrayList();

      public static void record() {
         try {
            File file = new File("/tmp/trace.html");
            OutputStream write = IO.write(file);
            report(write);
         } catch (FileNotFoundException e) {
            e.printStackTrace();
         }

      }

      public static void report(OutputStream write) {
         PrintStream stream = new PrintStream(write);
         report(stream);
         stream.close();
      }

      public static void report(PrintStream stream) {
         trace.print(stream);
         stream.print("<br/><ul>");

         for(Event event : trace.events) {
            stream.println(event);
         }

         stream.print("</ul>");
      }

      public static void mark() {
         Throwable throwable = (new Exception()).fillInStackTrace();
         List<StackTraceElement> stackTraceElements = new ArrayList(Arrays.asList(throwable.getStackTrace()));
         Collections.reverse(stackTraceElements);
         Iterator<StackTraceElement> iterator = stackTraceElements.iterator();

         while(iterator.hasNext()) {
            StackTraceElement element = (StackTraceElement)iterator.next();
            if (!element.getClassName().startsWith("org.apache")) {
               iterator.remove();
            }

            if (element.getClassName().endsWith("Debug") && element.getMethodName().equals("mark")) {
               iterator.remove();
            }
         }

         trace.link(stackTraceElements);
      }

      public void print(PrintStream out) {
         Set<Node> seen = new HashSet();

         for(Node node : this.elements.values()) {
            if (node.parent == null) {
               out.println("<ul>");
               this.print(seen, out, node, "- ");
               out.println("</ul>");
            }
         }

      }

      private void print(Set<Node> seen, PrintStream out, Node node, String s) {
         if (seen.add(node)) {
            out.print("<li>\n");
            StackTraceElement e = node.getElement();
            out.printf("<b>%s</b> <i>%s <font color='gray'>(%s)</font></i>\n", this.escape(e.getMethodName()), this.reverse(e.getClassName()), e.getLineNumber());
            if (node.children.size() > 0) {
               out.println("<ul>");

               for(Node child : node.children) {
                  this.print(seen, out, child, s);
               }

               out.println("</ul>");
            }

            out.print("</li>\n");
         }
      }

      private String escape(String methodName) {
         return methodName.replace("<", "&lt;").replace(">", "&gt;");
      }

      private void printTxt(Set<Node> seen, PrintStream out, Node node, String s) {
         if (seen.add(node)) {
            out.print(s);
            StackTraceElement e = node.getElement();
            out.printf("**%s** *%s* (%s)\n", e.getMethodName(), this.reverse(e.getClassName()), e.getLineNumber());
            s = "  " + s;

            for(Node child : node.children) {
               this.print(seen, out, child, s);
            }

         }
      }

      private String reverse2(String className) {
         List<String> list = Arrays.asList(className.split("\\."));
         Collections.reverse(list);
         String string = Join.join(".", (Collection)list);
         string = string.replaceAll("(.*?)(\\..*)", "$1<font color=\"gray\">$2</font>");
         return string;
      }

      private String reverse(String string) {
         string = string.replaceAll("(.*)\\.([^.]+)", "$2 <font color=\"gray\">$1</font>");
         return string;
      }

      public static void reset() {
         trace.events.clear();
         trace.elements.clear();
      }

      public void link(List<StackTraceElement> elements) {
         this.events.add(new Event(elements));
         Iterator<StackTraceElement> iterator = elements.iterator();
         if (iterator.hasNext()) {
            for(Node parent = this.get((StackTraceElement)iterator.next()); iterator.hasNext(); parent = parent.addChild(this.get((StackTraceElement)iterator.next()))) {
            }

         }
      }

      private Node get(StackTraceElement element) {
         String key = element.toString();
         Node node = (Node)this.elements.get(key);
         if (node == null) {
            node = new Node(element);
            this.elements.put(key, node);
         }

         return node;
      }

      private static final class Event {
         private final long time;
         private final List<StackTraceElement> elements;

         private Event(List<StackTraceElement> elements) {
            this.time = System.currentTimeMillis();
            this.elements = Collections.unmodifiableList(elements);
         }

         public String toString() {
            return "<li>time=" + this.time + ", elements=" + this.elements + "</li>";
         }
      }

      public static class Node {
         private Node parent;
         private final String trace;
         private final StackTraceElement element;
         private final List<Node> children = new ArrayList();

         public Node(StackTraceElement element) {
            this.element = element;
            this.trace = element.toString();
         }

         public String getTrace() {
            return this.trace;
         }

         public StackTraceElement getElement() {
            return this.element;
         }

         public Node addChild(Node node) {
            node.parent = this;
            this.children.add(node);
            return node;
         }
      }
   }
}
