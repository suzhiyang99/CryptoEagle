package org.apache.openejb.core.webservices;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.xml.namespace.QName;
import org.apache.openejb.OpenEJBException;

public class PortAddressRegistryImpl implements PortAddressRegistry {
   private final Map<String, PortAddress> portsById = new TreeMap();
   private final Map<String, Map<String, PortAddress>> portsByInterface = new TreeMap();
   private final Map<String, Map<String, PortAddress>> portsByServiceId = new TreeMap();
   private final Map<QName, Map<String, PortAddress>> portsByServiceQName = new HashMap();

   public synchronized void addPort(String serviceId, QName serviceQName, String portId, QName portQName, String portInterface, String address) throws OpenEJBException {
      if (serviceId == null) {
         throw new NullPointerException("serviceId is null");
      } else if (serviceQName == null) {
         throw new NullPointerException("serviceQName is null");
      } else if (portId == null) {
         throw new NullPointerException("portId is null");
      } else if (portQName == null) {
         throw new NullPointerException("portQName is null");
      } else if (address == null) {
         throw new NullPointerException("address is null");
      } else {
         PortAddress portAddress = (PortAddress)this.portsById.get(portId);
         if (portAddress != null) {
            throw new OpenEJBException("A webservice port with qname " + portAddress.getPortQName() + " is already registered to the portId " + portId);
         } else {
            portAddress = new PortAddress(portId, serviceQName, portQName, address, portInterface);
            this.portsById.put(portId, portAddress);
            Map<String, PortAddress> ports = null;
            if (portInterface != null) {
               ports = (Map)this.portsByInterface.get(portInterface);
               if (ports == null) {
                  ports = new TreeMap();
                  this.portsByInterface.put(portInterface, ports);
               }

               ports.put(portId, portAddress);
            }

            ports = (Map)this.portsByServiceId.get(serviceId);
            if (ports == null) {
               ports = new TreeMap();
               this.portsByServiceId.put(serviceId, ports);
            }

            ports.put(portId, portAddress);
            ports = (Map)this.portsByServiceQName.get(serviceQName);
            if (ports == null) {
               ports = new TreeMap();
               this.portsByServiceQName.put(serviceQName, ports);
            }

            ports.put(portId, portAddress);
         }
      }
   }

   public synchronized void removePort(String serviceId, QName serviceQName, String portId, String portInterface) {
      if (serviceId == null) {
         throw new NullPointerException("serviceId is null");
      } else if (serviceQName == null) {
         throw new NullPointerException("serviceQName is null");
      } else if (portId == null) {
         throw new NullPointerException("portId is null");
      } else {
         PortAddress portAddress = (PortAddress)this.portsById.remove(portId);
         if (portAddress == null) {
            Map<String, PortAddress> ports = null;
            if (portInterface != null) {
               ports = (Map)this.portsByInterface.get(portInterface);
               if (ports != null) {
                  ports.remove(portId);
                  if (ports.isEmpty()) {
                     this.portsByInterface.remove(portInterface);
                  }
               }
            }

            ports = (Map)this.portsByServiceId.get(serviceId);
            if (ports != null) {
               ports.remove(portId);
               if (ports.isEmpty()) {
                  this.portsByServiceId.remove(serviceId);
               }
            }

            ports = (Map)this.portsByServiceQName.get(serviceQName);
            if (ports != null) {
               ports.remove(portId);
               if (ports.isEmpty()) {
                  this.portsByServiceId.remove(serviceId);
               }
            }

         }
      }
   }

   public synchronized Set<PortAddress> getPorts(String id, QName serviceQName, String referenceClassName) {
      if (serviceQName == null) {
         throw new NullPointerException("serviceQName is null");
      } else {
         if (id != null) {
            PortAddress portAddress = (PortAddress)this.portsById.get(id);
            if (portAddress != null) {
               return Collections.singleton(portAddress);
            }
         }

         if (referenceClassName != null) {
            Map<String, PortAddress> interfacePorts = (Map)this.portsByInterface.get(referenceClassName);
            if (interfacePorts != null && interfacePorts.size() == 1) {
               PortAddress portAddress = (PortAddress)interfacePorts.values().iterator().next();
               return Collections.singleton(portAddress);
            }
         }

         Map<String, PortAddress> ports = new TreeMap();
         if (id != null) {
            Map<String, PortAddress> idPorts = (Map)this.portsByServiceId.get(id);
            if (idPorts != null) {
               ports.putAll(idPorts);
            }
         }

         if (ports.isEmpty()) {
            Map<String, PortAddress> qnamePorts = (Map)this.portsByServiceQName.get(serviceQName);
            if (qnamePorts != null) {
               ports.putAll(qnamePorts);
            }
         }

         Set<PortAddress> portAddresses = new HashSet(ports.values());
         return portAddresses;
      }
   }
}
