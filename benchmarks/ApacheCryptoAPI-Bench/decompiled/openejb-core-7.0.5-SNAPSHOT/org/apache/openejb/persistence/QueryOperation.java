package org.apache.openejb.persistence;

import javax.persistence.Query;

public interface QueryOperation {
   Query apply(Query var1);
}
