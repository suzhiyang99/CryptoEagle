package org.apache.openejb.testing;

public class TestInstance {
   protected Class<?> testClass;
   protected Object instance;

   public TestInstance(Class<?> testClass, Object instance) {
      this.testClass = testClass;
      this.instance = instance;
   }

   public Class<?> getTestClass() {
      return this.testClass;
   }

   public Object getInstance() {
      return this.instance;
   }
}
