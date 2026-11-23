package org.apache.meecrowave.runner;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.enterprise.inject.Vetoed;
import org.apache.catalina.connector.Connector;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.apache.meecrowave.Meecrowave;
import org.apache.meecrowave.runner.cli.CliOption;
import org.apache.xbean.recipe.ObjectRecipe;

@Vetoed
public class Cli implements Runnable {
   private final String[] args;

   public Cli(String[] args) {
      this.args = args;
   }

   public void run() {
      ParsedCommand parsedCommand = (new ParsedCommand(this.args)).invoke();
      if (!parsedCommand.isFailed()) {
         Meecrowave.Builder builder = parsedCommand.getBuilder();
         CommandLine line = parsedCommand.getLine();

         try (Meecrowave meecrowave = new Meecrowave(builder)) {
            String ctx = line.getOptionValue("context", "");
            String fixedCtx = !ctx.isEmpty() && !ctx.startsWith("/") ? '/' + ctx : ctx;
            String war = line.getOptionValue("webapp");
            meecrowave.start();
            if (war == null) {
               meecrowave.deployClasspath(new Meecrowave.DeploymentMeta(ctx, (File)Optional.ofNullable(line.getOptionValue("docbase")).map(File::new).orElse((Object)null), (Consumer)null));
            } else {
               meecrowave.deployWebapp(fixedCtx, new File(war));
            }

            this.doWait(meecrowave, line);
         }

      }
   }

   protected void doWait(Meecrowave meecrowave, CommandLine line) {
      meecrowave.getTomcat().getServer().await();
   }

   public static void main(String[] args) {
      (new Cli(args)).run();
   }

   public static Meecrowave.Builder create(String[] args) {
      ParsedCommand command = (new ParsedCommand(args)).invoke();
      return command.isFailed() ? null : command.getBuilder();
   }

   private static void bind(Meecrowave.Builder builder, CommandLine line, List<Field> fields, Object config) {
      fields.forEach((f) -> {
         CliOption opt = (CliOption)f.getAnnotation(CliOption.class);
         Stream var10000 = Stream.of(Stream.of(opt.name()), Stream.of(opt.alias())).flatMap((a) -> a);
         line.getClass();
         Optional<String> first = var10000.filter(line::hasOption).findFirst();
         if (first.isPresent()) {
            String name = (String)first.get();
            Optional.ofNullable(f.getType() == Boolean.TYPE ? Optional.ofNullable(line.getOptionValue(name)).map(Boolean::parseBoolean).orElse(true) : toValue(builder, name, line.getOptionValues(name), f.getType())).ifPresent((v) -> {
               if (!f.isAccessible()) {
                  f.setAccessible(true);
               }

               try {
                  f.set(config, v);
               } catch (IllegalAccessException e) {
                  throw new IllegalStateException(e);
               }
            });
         }

      });
   }

   private static Object toValue(Meecrowave.Builder builder, String name, String[] optionValues, Class<?> type) {
      if (optionValues != null && optionValues.length != 0) {
         IntStream.range(0, optionValues.length).forEach((i) -> optionValues[i] = ((Meecrowave.ValueTransformers)builder.getExtension(Meecrowave.ValueTransformers.class)).apply(optionValues[i]));
         if (String.class == type) {
            return optionValues[0];
         } else if (Integer.TYPE == type) {
            return Integer.parseInt(optionValues[0]);
         } else if (File.class == type) {
            return new File(optionValues[0]);
         } else if (Properties.class == type) {
            Properties props = new Properties();
            Stream.of(optionValues).map((v) -> v.split("=")).forEach((v) -> props.setProperty(v[0], v[1]));
            return props;
         } else if (Map.class == type) {
            Map<String, String> props = new HashMap();
            Stream.of(optionValues).map((v) -> v.split("=")).forEach((v) -> {
               String var10000 = (String)props.put(v[0], v[1]);
            });
            return props;
         } else {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            switch (name) {
               case "realm":
                  try {
                     int end = optionValues[0].indexOf(58);
                     if (end < 0) {
                        return loader.loadClass(optionValues[0]).newInstance();
                     }

                     ObjectRecipe recipe = new ObjectRecipe(optionValues[0].substring(0, end));
                     Stream.of(optionValues[0].substring(end + 1, optionValues[0].length()).split(";")).map((v) -> v.split("=")).forEach((v) -> recipe.setProperty(v[0], v[1]));
                     return recipe.create(loader);
                  } catch (Exception var10) {
                     throw new IllegalArgumentException(optionValues[0]);
                  }
               case "security-constraint":
                  return Stream.of(optionValues).map((item) -> {
                     try {
                        ObjectRecipe recipe = new ObjectRecipe(Meecrowave.SecurityConstaintBuilder.class);
                        Stream.of(item.split(";")).map((v) -> v.split("=")).forEach((v) -> recipe.setProperty(v[0], v[1]));
                        return recipe.create(loader);
                     } catch (Exception var4) {
                        throw new IllegalArgumentException(optionValues[0]);
                     }
                  }).collect(Collectors.toList());
               case "login-config":
                  try {
                     ObjectRecipe recipe = new ObjectRecipe(Meecrowave.LoginConfigBuilder.class);
                     Stream.of(optionValues[0].split(";")).map((v) -> v.split("=")).forEach((v) -> recipe.setProperty(v[0], v[1]));
                     return recipe.create(loader);
                  } catch (Exception var9) {
                     throw new IllegalArgumentException(optionValues[0]);
                  }
               case "connector":
                  return Stream.of(optionValues).map((v) -> {
                     try {
                        int end = v.indexOf(58);
                        if (end < 0) {
                           return new Connector(v);
                        } else {
                           Connector connector = new Connector(optionValues[0].substring(0, end));
                           Stream.of(v.substring(end + 1, v.length()).split(";")).map((i) -> i.split("=")).forEach((i) -> connector.setProperty(i[0], i[1]));
                           return connector;
                        }
                     } catch (Exception var4) {
                        throw new IllegalArgumentException(optionValues[0]);
                     }
                  }).collect(Collectors.toList());
               default:
                  throw new IllegalArgumentException("Unsupported " + name);
            }
         }
      } else {
         return null;
      }
   }

   private static final class ParsedCommand {
      private final String[] args;
      private boolean failed;
      private CommandLine line;
      private Meecrowave.Builder builder;

      private ParsedCommand(String... args) {
         this.args = args;
      }

      private static void help(org.apache.commons.cli.Options options) {
         (new HelpFormatter()).printHelp("java -jar meecrowave-runner.jar", options);
      }

      private static Meecrowave.Builder buildConfig(CommandLine line, List<Field> fields, Map<Object, List<Field>> propertiesOptions) {
         Meecrowave.Builder config = new Meecrowave.Builder();
         Cli.bind(config, line, fields, config);
         propertiesOptions.forEach((o, f) -> {
            Cli.bind(config, line, f, o);
            config.setExtension(o.getClass(), o);
         });
         return config;
      }

      boolean isFailed() {
         return this.failed;
      }

      public CommandLine getLine() {
         return this.line;
      }

      public Meecrowave.Builder getBuilder() {
         return this.builder;
      }

      public ParsedCommand invoke() {
         org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
         options.addOption((String)null, "help", false, "Show help");
         options.addOption((String)null, "context", true, "The context to use to deploy the webapp");
         options.addOption((String)null, "webapp", true, "Location of the webapp, if not set the classpath will be deployed");
         options.addOption((String)null, "docbase", true, "Location of the docbase for a classpath deployment");
         List<Field> fields = (List)Stream.of(Meecrowave.Builder.class.getDeclaredFields()).filter((f) -> f.isAnnotationPresent(CliOption.class)).collect(Collectors.toList());
         Map<Object, List<Field>> propertiesOptions = (Map)StreamSupport.stream(ServiceLoader.load(Options.class).spliterator(), false).collect(Collectors.toMap(Function.identity(), (o) -> (List)Stream.of(o.getClass().getDeclaredFields()).filter((f) -> f.isAnnotationPresent(CliOption.class)).collect(Collectors.toList())));
         fields.forEach((f) -> {
            CliOption opt = (CliOption)f.getAnnotation(CliOption.class);
            String description = opt.description();
            options.addOption((String)null, opt.name(), true, description);
            Stream.of(opt.alias()).forEach((a) -> options.addOption((String)null, a, true, description));
         });
         propertiesOptions.values().forEach((all) -> all.forEach((f) -> {
               CliOption opt = (CliOption)f.getAnnotation(CliOption.class);
               String description = opt.description();
               options.addOption((String)null, opt.name(), true, description);
               Stream.of(opt.alias()).forEach((a) -> options.addOption((String)null, a, true, description));
            }));
         CommandLineParser parser = new DefaultParser();

         try {
            this.line = parser.parse(options, this.args, true);
         } catch (ParseException var6) {
            help(options);
            this.failed = true;
            return this;
         }

         if (this.line.hasOption("help")) {
            help(options);
            this.failed = true;
            return this;
         } else {
            this.builder = buildConfig(this.line, fields, propertiesOptions);
            this.failed = false;
            return this;
         }
      }
   }

   public interface Options {
   }
}
