package org.apache.openejb.config.typed.util;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.openejb.config.provider.ProviderManager;
import org.apache.openejb.config.provider.ServiceJarXmlLoader;
import org.apache.openejb.config.sys.Resource;
import org.apache.openejb.config.sys.ServiceProvider;
import org.apache.openejb.loader.IO;
import org.apache.openejb.util.Duration;
import org.apache.openejb.util.Join;
import org.apache.openejb.util.Strings;

public class ProviderGenerator extends Resource {
   public static void main(String[] args) throws Exception {
      ProviderManager manager = new ProviderManager(new ServiceJarXmlLoader());
      Set<String> seen = new HashSet();

      for(ServiceProvider provider : manager.load("org.apache.tomee")) {
         List<String> types = provider.getTypes();
         String name = guessBuilder(types);
         String builder = name + "Builder";
         if (!seen.contains(builder)) {
            seen.add(builder);
            String service = provider.getService();
            File file = new File("/Users/dblevins/work/all/trunk/openejb/container/openejb-core/src/main/java/org/apache/openejb/config/typed/" + builder + ".java");
            OutputStream write = IO.write(file);
            PrintStream out = new PrintStream(write);
            out.println("/*\n * Licensed to the Apache Software Foundation (ASF) under one or more\n * contributor license agreements.  See the NOTICE file distributed with\n * this work for additional information regarding copyright ownership.\n * The ASF licenses this file to You under the Apache License, Version 2.0\n * (the \"License\"); you may not use this file except in compliance with\n * the License.  You may obtain a copy of the License at\n *\n *     http://www.apache.org/licenses/LICENSE-2.0\n *\n *  Unless required by applicable law or agreed to in writing, software\n *  distributed under the License is distributed on an \"AS IS\" BASIS,\n *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n *  See the License for the specific language governing permissions and\n *  limitations under the License.\n */");
            out.println("package org.apache.openejb.config.typed;");
            out.println();
            out.println("import org.apache.openejb.config.typed.util.*;");
            out.println("import org.apache.openejb.config.sys.*;");
            out.println("import javax.xml.bind.annotation.*;");
            out.println("import " + Duration.class.getName() + ";");
            out.println("import java.util.*;");
            out.println("import java.util.concurrent.*;");
            out.println("import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;");
            out.println();
            out.println(template("@XmlAccessorType(XmlAccessType.FIELD)\n@XmlRootElement(name = \"${name}\")\npublic class ${builder} extends ${service} {\n").apply("builder", builder, "service", service, "name", name));

            for(Map.Entry<Object, Object> entry : provider.getProperties().entrySet()) {
               String key = Strings.lcfirst(entry.getKey().toString());
               String value = entry.getValue().toString();
               String type = guessType(key, value);
               if (Duration.class.getName().endsWith(type)) {
                  out.println("    @XmlJavaTypeAdapter(DurationAdapter.class)");
               }

               out.println(template("    @XmlAttribute\n    private ${type} ${key} = ${value};").apply("builder", builder, "key", key, "value", asValue(type, value), "type", type));
            }

            out.println();
            out.println(template("    public ${builder}() {\n        setClassName(\"${className}\");\n        setType(\"${type}\");\n        setId(\"${name}\");\n").apply("builder", builder, "className", String.valueOf(provider.getClassName()), "type", (String)types.get(0), "name", name));
            if (provider.getConstructor() != null) {
               out.println(template("        setConstructor(\"${constructor}\");\n").apply("constructor", fixConstructor(provider)));
            }

            if (provider.getFactoryName() != null) {
               out.println(template("        setFactoryName(\"${factoryName}\");\n").apply("factoryName", provider.getFactoryName()));
            }

            out.println("    }\n");
            out.println(template("    public ${builder} id(String id) {\n        setId(id);\n        return this;\n    }\n").apply("builder", builder));

            for(Map.Entry<Object, Object> entry : provider.getProperties().entrySet()) {
               String lcFirstKey = Strings.lcfirst(entry.getKey().toString());
               String ucFirstKey = Strings.ucfirst(lcFirstKey);
               String value = entry.getValue().toString();
               String type = guessType(lcFirstKey, value);
               out.println(template("    public ${builder} with${Key}(${type} ${key}) {\n        this.${key} = ${key};\n        return this;\n    }\n").apply("builder", builder, "key", lcFirstKey, "Key", ucFirstKey, "value", value, "type", type));
               out.println(template("    public void set${Key}(${type} ${key}) {\n        this.${key} = ${key};\n    }\n").apply("key", lcFirstKey, "Key", ucFirstKey, "value", value, "type", type));
               out.println(template("    public ${type} get${Key}() {\n        return ${key};\n    }\n").apply("key", lcFirstKey, "Key", ucFirstKey, "value", value, "type", type));
               if (Duration.class.getName().equals(type)) {
                  out.println(template("    public ${builder} with${Key}(long time, TimeUnit unit) {\n        return with${Key}(new Duration(time, unit));\n    }\n").apply("builder", builder, "key", lcFirstKey, "Key", ucFirstKey, "value", value, "type", type));
                  out.println(template("    public void set${Key}(long time, TimeUnit unit) {\n        set${Key}(new Duration(time, unit));\n    }\n").apply("key", lcFirstKey, "Key", ucFirstKey, "value", value, "type", type));
               }

               String s = lcFirstKey.toLowerCase(Locale.ENGLISH);
               if ("long".equals(type) && s.contains("time")) {
                  TimeUnit unit = null;
                  if (s.endsWith("millis")) {
                     unit = TimeUnit.MILLISECONDS;
                  } else if (s.endsWith("milliseconds")) {
                     unit = TimeUnit.MILLISECONDS;
                  } else if (s.endsWith("seconds")) {
                     unit = TimeUnit.SECONDS;
                  } else if (s.endsWith("minutes")) {
                     unit = TimeUnit.MINUTES;
                  } else if (s.endsWith("hours")) {
                     unit = TimeUnit.HOURS;
                  }

                  if (unit != null) {
                     Pattern pattern = Pattern.compile("(millis(econds)?|seconds|minutes|hours)", 2);
                     String lcFirstKey2 = pattern.matcher(lcFirstKey).replaceAll("");
                     String ucFirstKey2 = pattern.matcher(ucFirstKey).replaceAll("");
                     out.println(template("    public ${builder} with${Key2}(long time, TimeUnit unit) {\n        return with${Key}(TimeUnit.${unit}.convert(time, unit));\n    }\n").apply("builder", builder, "key2", lcFirstKey2, "Key2", ucFirstKey2, "key", lcFirstKey, "Key", ucFirstKey, "value", value, "unit", unit.name(), "type", type));
                     out.println(template("    public void set${Key2}(long time, TimeUnit unit) {\n        set${Key}(TimeUnit.${unit}.convert(time, unit));\n    }\n").apply("key2", lcFirstKey2, "Key2", ucFirstKey2, "key", lcFirstKey, "Key", ucFirstKey, "value", value, "unit", unit.name(), "type", type));
                  }
               }
            }

            out.println("    public Properties getProperties() {\n        return Builders.getProperties(this);\n    }\n");
            out.println("}");
            out.flush();
            out.close();
         }
      }

   }

   private static String fixConstructor(ServiceProvider provider) {
      String s = String.valueOf(provider.getConstructor());
      String[] split = s.split(" *, *");

      for(int i = 0; i < split.length; ++i) {
         split[i] = Strings.lcfirst(split[i]);
      }

      return Join.join(", ", (Object[])split);
   }

   private static String asValue(String type, String value) {
      if ("".equals(value)) {
         return "null";
      } else if ("String".equals(type)) {
         return "\"" + value + "\"";
      } else if (URI.class.getName().equals(type)) {
         return URI.class.getName() + ".create(\"" + value + "\")";
      } else {
         return Duration.class.getName().equals(type) ? Duration.class.getName() + ".parse(\"" + value + "\")" : value;
      }
   }

   private static String guessBuilder(List<String> types) {
      String s = (String)types.get(0);
      if ("STATEFUL".equals(s)) {
         return "StatefulContainer";
      } else if ("SINGLETON".equals(s)) {
         return "SingletonContainer";
      } else if ("MANAGED".equals(s)) {
         return "ManagedContainer";
      } else if ("STATELESS".equals(s)) {
         return "StatelessContainer";
      } else if ("MESSAGE".equals(s)) {
         return "MessageDrivenContainer";
      } else if ("BMP_ENTITY".equals(s)) {
         return "BmpEntityContainer";
      } else if ("CMP_ENTITY".equals(s)) {
         return "CmpEntityContainer";
      } else if ("javax.jms.ConnectionFactory".equals(s)) {
         return "JmsConnectionFactory";
      } else if ("javax.mail.Session".equals(s)) {
         return "JavaMailSession";
      } else {
         s = s.replaceAll(".*\\.", "");
         return s;
      }
   }

   private static String guessType(String key, String value) {
      if (!value.equals("true") && !value.equals("false")) {
         if (key.toLowerCase(Locale.ENGLISH).endsWith("timeout")) {
            return Duration.class.getName();
         } else if (value.matches("-?[0-9]+ +(m|h|s|M|H|S)[^ ]*")) {
            return Duration.class.getName();
         } else {
            if (key.toLowerCase(Locale.ENGLISH).contains("time")) {
               try {
                  Long.parseLong(value);
                  return "long";
               } catch (NumberFormatException var4) {
               }
            }

            try {
               Integer.parseInt(value);
               return "int";
            } catch (NumberFormatException var3) {
               if (key.toLowerCase(Locale.ENGLISH).endsWith("url")) {
                  return URI.class.getName();
               } else {
                  return key.toLowerCase(Locale.ENGLISH).endsWith("uri") ? URI.class.getName() : "String";
               }
            }
         }
      } else {
         return "boolean";
      }
   }

   public static Template template(String template) {
      return new Template(template);
   }

   public static class Template {
      public static final Pattern PATTERN = Pattern.compile("(\\$\\{)((\\.|\\w)+)(})");
      private final String template;

      public Template(String template) {
         this.template = template;
      }

      public String apply(String... args) {
         Map<String, String> map = new HashMap();

         for(int i = 0; i < args.length; i += 2) {
            String key = args[i];
            String value = args[i + 1];
            map.put(key, value);
         }

         return this.apply(map);
      }

      public String apply(Map<String, String> map) {
         Matcher matcher = PATTERN.matcher(this.template);
         StringBuffer buf = new StringBuffer();

         while(matcher.find()) {
            String key = matcher.group(2);
            if (key == null) {
               throw new IllegalStateException("Key is null. Template '" + this.template + "'");
            }

            String value = (String)map.get(key);
            if (key.toLowerCase(Locale.ENGLISH).endsWith(".lc")) {
               value = ((String)map.get(key.substring(0, key.length() - 3))).toLowerCase(Locale.ENGLISH);
            } else if (key.toLowerCase(Locale.ENGLISH).endsWith(".uc")) {
               value = ((String)map.get(key.substring(0, key.length() - 3))).toUpperCase(Locale.ENGLISH);
            } else if (key.toLowerCase(Locale.ENGLISH).endsWith(".cc")) {
               value = Strings.camelCase((String)map.get(key.substring(0, key.length() - 3)));
            }

            if (value == null) {
               throw new IllegalStateException("Value is null for key '" + key + "'. Template '" + this.template + "'. Keys: " + Join.join(", ", (Collection)map.keySet()));
            }

            matcher.appendReplacement(buf, value);
         }

         matcher.appendTail(buf);
         return buf.toString();
      }
   }
}
