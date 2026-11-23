package org.apache.activemq.artemis.utils;

import org.apache.activemq.artemis.utils.actors.ArtemisExecutor;

public interface ExecutorFactory {
   ArtemisExecutor getExecutor();
}
