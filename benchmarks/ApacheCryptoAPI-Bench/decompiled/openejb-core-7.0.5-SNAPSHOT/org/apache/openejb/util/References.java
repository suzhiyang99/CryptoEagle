package org.apache.openejb.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class References {
   public static <T> List<T> sort(List<T> objects, Visitor<T> visitor) {
      if (objects.size() <= 1) {
         return objects;
      } else {
         Map<String, Node> nodes = new LinkedHashMap();

         for(T obj : objects) {
            String name = visitor.getName(obj);
            Node node = new Node(name, obj);
            nodes.put(name, node);
         }

         for(Node node : nodes.values()) {
            for(String name : visitor.getReferences(node.object)) {
               Node ref = (Node)nodes.get(name);
               if (ref == null) {
                  throw new IllegalArgumentException("No such object in list: " + name);
               }

               node.references.add(ref);
               node.initialReferences.add(ref);
            }
         }

         boolean circuitFounded = false;

         for(Node node : nodes.values()) {
            Set<Node> visitedNodes = new HashSet();
            if (!normalizeNodeReferences(node, node, visitedNodes)) {
               circuitFounded = true;
               break;
            }

            node.references.addAll(visitedNodes);
         }

         if (circuitFounded) {
            Set<Circuit> circuits = new LinkedHashSet();

            for(Node node : nodes.values()) {
               findCircuits(circuits, node, new java.util.Stack());
            }

            ArrayList<Circuit> list = new ArrayList(circuits);
            Collections.sort(list);
            List<List> all = new ArrayList();

            for(Circuit circuit : list) {
               all.add(unwrap(circuit.nodes));
            }

            throw new CircularReferencesException(all);
         } else {
            Node rootNode = new Node((String)null, (Object)null);
            rootNode.previous = rootNode;
            rootNode.next = (Node)nodes.values().iterator().next();

            for(Node node : nodes.values()) {
               node.previous = rootNode.previous;
               rootNode.previous.next = node;
               node.next = rootNode;
               rootNode.previous = node;
            }

            for(Node node : nodes.values()) {
               for(Node reference : node.references) {
                  swap(node, reference, rootNode);
               }
            }

            List sortedList = new ArrayList(nodes.size());

            for(Node currentNode = rootNode.next; currentNode != rootNode; currentNode = currentNode.next) {
               sortedList.add(currentNode.object);
            }

            return sortedList;
         }
      }
   }

   private static boolean normalizeNodeReferences(Node rootNode, Node node, Set<Node> referenceNodes) {
      if (node.references.contains(rootNode)) {
         return false;
      } else {
         for(Node reference : node.references) {
            if (referenceNodes.add(reference) && !normalizeNodeReferences(rootNode, reference, referenceNodes)) {
               return false;
            }
         }

         return true;
      }
   }

   private static void swap(Node shouldAfterNode, Node shouldBeforeNode, Node rootNode) {
      for(Node currentNode = shouldBeforeNode; currentNode.next != rootNode; currentNode = currentNode.next) {
         if (currentNode.next == shouldAfterNode) {
            return;
         }
      }

      shouldAfterNode.previous.next = shouldAfterNode.next;
      shouldAfterNode.next.previous = shouldAfterNode.previous;
      shouldAfterNode.previous = shouldBeforeNode;
      shouldAfterNode.next = shouldBeforeNode.next;
      shouldBeforeNode.next = shouldAfterNode;
      shouldAfterNode.next.previous = shouldAfterNode;
   }

   private static <T> List<T> unwrap(List<Node> nodes) {
      ArrayList<T> referees = new ArrayList(nodes.size());

      for(Node node : nodes) {
         referees.add(node.object);
      }

      return referees;
   }

   private static void findCircuits(Set<Circuit> circuits, Node node, java.util.Stack<Node> stack) {
      if (stack.contains(node)) {
         int fromIndex = stack.indexOf(node);
         int toIndex = stack.size();
         ArrayList<Node> circularity = new ArrayList(stack.subList(fromIndex, toIndex));
         circularity.add(node);
         Circuit circuit = new Circuit(circularity);
         circuits.add(circuit);
      } else {
         stack.push(node);

         for(Node reference : node.initialReferences) {
            findCircuits(circuits, reference, stack);
         }

         stack.pop();
      }
   }

   private static class Node implements Comparable<Node> {
      private final String name;
      private final Object object;
      private final List<Node> initialReferences = new ArrayList();
      private final Set<Node> references = new HashSet();
      private Node next;
      private Node previous;

      public Node(String name, Object object) {
         this.name = name;
         this.object = object;
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            Node node = (Node)o;
            return this.name.equals(node.name);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return this.name.hashCode();
      }

      public int compareTo(Node o) {
         return this.name.compareTo(o.name);
      }

      public String toString() {
         return this.name;
      }
   }

   private static class Circuit implements Comparable<Circuit> {
      private final List<Node> nodes;
      private final List<Node> atomic;

      public Circuit(List<Node> nodes) {
         this.nodes = nodes;
         this.atomic = new ArrayList(nodes);
         this.atomic.remove(this.atomic.size() - 1);
         Collections.sort(this.atomic);
      }

      public List<Node> getNodes() {
         return this.nodes;
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            Circuit circuit = (Circuit)o;
            return this.atomic.equals(circuit.atomic);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return this.atomic.hashCode();
      }

      public int compareTo(Circuit o) {
         int i = this.atomic.size() - o.atomic.size();
         if (i != 0) {
            return i;
         } else {
            Iterator<Node> iterA = this.atomic.listIterator();
            Iterator<Node> iterB = o.atomic.listIterator();

            while(iterA.hasNext() && iterB.hasNext()) {
               Node a = (Node)iterA.next();
               Node b = (Node)iterB.next();
               i = a.compareTo(b);
               if (i != 0) {
                  return i;
               }
            }

            return 0;
         }
      }

      public String toString() {
         return "Circuit(" + Join.join(",", (Collection)References.unwrap(this.nodes)) + ")";
      }
   }

   public interface Visitor<T> {
      String getName(T var1);

      Set<String> getReferences(T var1);
   }
}
