package org.apache.openejb.assembler.classic;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Locale;
import javax.sql.DataSource;
import org.apache.openejb.OpenEJBRuntimeException;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public class ImportSql {
   private static final Logger LOGGER;
   public static final String OPENEJB_SQL_IMPORT = "openejb.sql.import";
   public static final String IMPORT_FILE_PREFIX = "import-";
   public static final String IMPORT_FILE_EXTENSION = ".sql";
   private final DataSource dataSource;
   private boolean done;
   private final Enumeration<URL> imports;

   public ImportSql(ClassLoader cl, String resource, DataSource ds) {
      this.dataSource = ds;
      this.done = false;
      if (this.dataSource == null) {
         throw new NullPointerException("datasource can't be null");
      } else {
         try {
            this.imports = cl.getResources("import-".concat(resource).concat(".sql"));
         } catch (IOException e) {
            throw new OpenEJBRuntimeException("can't look for init sql script", e);
         }
      }
   }

   public boolean hasSomethingToImport() {
      return !this.done && this.imports != null && this.imports.hasMoreElements() && SystemInstance.get().getOptions().get("openejb.sql.import", true);
   }

   public void doImport() {
      if (this.hasSomethingToImport()) {
         Connection connection = null;

         Statement statement;
         try {
            connection = this.dataSource.getConnection();
            statement = connection.createStatement();
         } catch (SQLException e) {
            LOGGER.error("can't create a statement, import scripts will be ignored", (Throwable)e);
            if (connection != null) {
               try {
                  connection.close();
               } catch (SQLException var13) {
               }
            }

            return;
         }

         try {
            while(this.imports.hasMoreElements()) {
               URL scriptToImport = (URL)this.imports.nextElement();
               LOGGER.info("importing " + scriptToImport.toExternalForm());
               this.importSql(scriptToImport, statement);
            }
         } finally {
            try {
               connection.close();
            } catch (SQLException var12) {
            }

            this.done = true;
         }
      }

   }

   private void importSql(URL script, Statement statement) {
      BufferedReader bufferedReader;
      try {
         bufferedReader = new BufferedReader(new InputStreamReader(new BufferedInputStream(script.openStream())));
      } catch (IOException e) {
         LOGGER.error("can't open " + script.toExternalForm(), (Throwable)e);
         return;
      }

      try {
         for(String sql = bufferedReader.readLine(); sql != null; sql = bufferedReader.readLine()) {
            String trimmedSql = sql.trim();
            if (!trimmedSql.isEmpty() && !trimmedSql.startsWith("--") && !trimmedSql.startsWith("//") && !trimmedSql.startsWith("/*")) {
               if (trimmedSql.endsWith(";")) {
                  trimmedSql = trimmedSql.substring(0, trimmedSql.length() - 1);
               }

               try {
                  if (!trimmedSql.toLowerCase(Locale.ENGLISH).startsWith("select")) {
                     statement.executeUpdate(trimmedSql);
                  } else {
                     statement.executeQuery(trimmedSql);
                  }

                  for(SQLWarning warnings = statement.getWarnings(); warnings != null; warnings = warnings.getNextWarning()) {
                     LOGGER.warning(warnings.getMessage());
                  }
               } catch (SQLException e) {
                  LOGGER.error("error importing script " + script.toExternalForm(), (Throwable)e);
               }
            }
         }
      } catch (IOException e) {
         LOGGER.error("can't import " + script.toExternalForm(), (Throwable)e);
      }

   }

   static {
      LOGGER = Logger.getInstance(LogCategory.OPENEJB, EntityManagerFactoryCallable.class.getName());
   }
}
