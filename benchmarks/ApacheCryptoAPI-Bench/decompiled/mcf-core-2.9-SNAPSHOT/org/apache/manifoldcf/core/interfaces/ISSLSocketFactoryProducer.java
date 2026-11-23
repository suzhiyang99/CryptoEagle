package org.apache.manifoldcf.core.interfaces;

import javax.net.ssl.SSLSocketFactory;

public interface ISSLSocketFactoryProducer {
   String _rcsid = "@(#)$Id$";

   SSLSocketFactory getSecureSocketFactory() throws ManifoldCFException;
}
