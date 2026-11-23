package org.apache.openejb.config;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.xml.bind.JAXBElement;
import org.apache.openejb.jee.ActivationConfig;
import org.apache.openejb.jee.ActivationConfigProperty;
import org.apache.openejb.jee.EjbJar;
import org.apache.openejb.jee.EjbRef;
import org.apache.openejb.jee.EnterpriseBean;
import org.apache.openejb.jee.MessageDrivenBean;
import org.apache.openejb.jee.jpa.AttributeOverride;
import org.apache.openejb.jee.jpa.Attributes;
import org.apache.openejb.jee.jpa.Basic;
import org.apache.openejb.jee.jpa.Column;
import org.apache.openejb.jee.jpa.Entity;
import org.apache.openejb.jee.jpa.EntityMappings;
import org.apache.openejb.jee.jpa.Field;
import org.apache.openejb.jee.jpa.GeneratedValue;
import org.apache.openejb.jee.jpa.GenerationType;
import org.apache.openejb.jee.jpa.Id;
import org.apache.openejb.jee.jpa.JoinColumn;
import org.apache.openejb.jee.jpa.JoinTable;
import org.apache.openejb.jee.jpa.NamedQuery;
import org.apache.openejb.jee.jpa.OneToMany;
import org.apache.openejb.jee.jpa.OneToOne;
import org.apache.openejb.jee.jpa.RelationField;
import org.apache.openejb.jee.jpa.Table;
import org.apache.openejb.jee.oejb2.ActivationConfigPropertyType;
import org.apache.openejb.jee.oejb2.ActivationConfigType;
import org.apache.openejb.jee.oejb2.EjbLocalRefType;
import org.apache.openejb.jee.oejb2.EjbRefType;
import org.apache.openejb.jee.oejb2.EjbRelationType;
import org.apache.openejb.jee.oejb2.EjbRelationshipRoleType;
import org.apache.openejb.jee.oejb2.EntityBeanType;
import org.apache.openejb.jee.oejb2.GeronimoEjbJarType;
import org.apache.openejb.jee.oejb2.JaxbOpenejbJar2;
import org.apache.openejb.jee.oejb2.MessageDrivenBeanType;
import org.apache.openejb.jee.oejb2.OpenejbJarType;
import org.apache.openejb.jee.oejb2.PatternType;
import org.apache.openejb.jee.oejb2.QueryType;
import org.apache.openejb.jee.oejb2.RpcBean;
import org.apache.openejb.jee.oejb2.SessionBeanType;
import org.apache.openejb.jee.oejb2.TssLinkType;
import org.apache.openejb.jee.oejb2.WebServiceBindingType;
import org.apache.openejb.jee.oejb2.WebServiceSecurityType;
import org.apache.openejb.jee.oejb3.EjbDeployment;
import org.apache.openejb.jee.oejb3.EjbLink;
import org.apache.openejb.jee.oejb3.Jndi;
import org.apache.openejb.jee.oejb3.OpenejbJar;
import org.apache.openejb.loader.IO;
import org.slf4j.LoggerFactory;

public class OpenEjb2Conversion implements DynamicDeployer {
   public final AppModule deploy(AppModule appModule) {
      for(EjbModule ejbModule : appModule.getEjbModules()) {
         Object altDD = this.getOpenejbJarType(ejbModule);
         if (OpenejbJarType.class.isInstance(altDD)) {
            OpenejbJarType openejbJarType = (OpenejbJarType)OpenejbJarType.class.cast(altDD);
            this.convertEjbRefs(ejbModule.getEjbJar(), ejbModule.getOpenejbJar(), openejbJarType);
            this.convertMdbConfigs(ejbModule.getEjbJar(), openejbJarType);
            this.mergeEntityMappings(ejbModule.getModuleId(), appModule.getCmpMappings(), ejbModule.getOpenejbJar(), openejbJarType);
         }
      }

      return appModule;
   }

   private OpenejbJarType getOpenejbJarType(EjbModule ejbModule) {
      Object altDD = ejbModule.getAltDDs().get("openejb-jar.xml");
      if (altDD instanceof String) {
         try {
            altDD = JaxbOpenejbJar2.unmarshal(OpenejbJarType.class, new ByteArrayInputStream(((String)altDD).getBytes()), false);
         } catch (Exception var5) {
         }
      }

      if (altDD instanceof URL) {
         try {
            altDD = JaxbOpenejbJar2.unmarshal(OpenejbJarType.class, IO.read((URL)altDD), false);
         } catch (Exception var4) {
         }
      }

      if (altDD instanceof JAXBElement) {
         altDD = ((JAXBElement)altDD).getValue();
      }

      return altDD instanceof OpenejbJarType ? (OpenejbJarType)altDD : null;
   }

   public final void convertEjbRefs(EjbJar ejbJar, OpenejbJar openejbJar, OpenejbJarType openejbJarType) {
      openejbJar.getProperties().putAll(openejbJarType.getProperties());
      Map<String, EnterpriseBean> ejbs = ejbJar.getEnterpriseBeansByEjbName();
      Map<String, EjbDeployment> deployments = openejbJar.getDeploymentsByEjbName();

      for(org.apache.openejb.jee.oejb2.EnterpriseBean enterpriseBean : openejbJarType.getEnterpriseBeans()) {
         EnterpriseBean ejb = (EnterpriseBean)ejbs.get(enterpriseBean.getEjbName());
         if (ejb != null) {
            Map<String, EjbRef> ejbRefs = ejb.getEjbRefMap();
            EjbDeployment deployment = (EjbDeployment)deployments.get(enterpriseBean.getEjbName());
            if (deployment != null) {
               if (enterpriseBean instanceof SessionBeanType) {
                  SessionBeanType sessionBean = (SessionBeanType)enterpriseBean;
                  WebServiceSecurityType webServiceSecurityType = sessionBean.getWebServiceSecurity();
                  if (webServiceSecurityType != null) {
                     if (webServiceSecurityType.getRealmName() != null) {
                        deployment.addProperty("webservice.security.realm", webServiceSecurityType.getRealmName());
                     }

                     if (webServiceSecurityType.getSecurityRealmName() != null) {
                        deployment.addProperty("webservice.security.securityRealm", webServiceSecurityType.getSecurityRealmName());
                     }

                     if (webServiceSecurityType.getTransportGuarantee() != null) {
                        deployment.addProperty("webservice.security.transportGarantee", webServiceSecurityType.getTransportGuarantee().value());
                     } else {
                        deployment.addProperty("webservice.security.transportGarantee", "NONE");
                     }

                     if (webServiceSecurityType.getAuthMethod() != null) {
                        deployment.addProperty("webservice.security.authMethod", webServiceSecurityType.getAuthMethod().value());
                     } else {
                        deployment.addProperty("webservice.security.authMethod", "NONE");
                     }

                     deployment.getProperties().putAll(webServiceSecurityType.getProperties());
                  }

                  if (sessionBean.getWebServiceAddress() != null) {
                     deployment.getProperties().put("openejb.webservice.deployment.address", sessionBean.getWebServiceAddress());
                  }
               }

               deployment.getProperties().putAll(enterpriseBean.getProperties());

               for(String name : enterpriseBean.getLocalJndiName()) {
                  deployment.getJndi().add(new Jndi(name, "LocalHome"));
               }

               for(String name : enterpriseBean.getJndiName()) {
                  deployment.getJndi().add(new Jndi(name, "RemoteHome"));
               }

               for(org.apache.openejb.jee.oejb2.Jndi jndi : enterpriseBean.getJndi()) {
                  deployment.getJndi().add(new Jndi(jndi.getName(), jndi.getInterface()));
               }

               Set<String> ejbLinks = new TreeSet();

               for(EjbLink ejbLink : deployment.getEjbLink()) {
                  ejbLinks.add(ejbLink.getEjbRefName());
               }

               for(EjbRefType refType : enterpriseBean.getEjbRef()) {
                  String refName = refType.getRefName();
                  if (!ejbLinks.contains(refName)) {
                     String nsCorbaloc = refType.getNsCorbaloc();
                     if (nsCorbaloc != null) {
                        EjbRef ref = (EjbRef)ejbRefs.get(refName);
                        if (ref != null) {
                           ref.setMappedName("jndi:" + nsCorbaloc);
                        }
                     } else if (refType.getEjbLink() != null) {
                        EjbRef ref = (EjbRef)ejbRefs.get(refName);
                        if (ref != null) {
                           ref.setEjbLink(refType.getEjbLink());
                        }
                     } else {
                        PatternType pattern = refType.getPattern();
                        this.addEjbLink(deployment, refName, pattern);
                     }
                  }
               }

               for(EjbLocalRefType refType : enterpriseBean.getEjbLocalRef()) {
                  String refName = refType.getRefName();
                  if (!ejbLinks.contains(refName)) {
                     if (refType.getEjbLink() != null) {
                        EjbRef ref = (EjbRef)ejbRefs.get(refName);
                        if (ref != null) {
                           ref.setEjbLink(refType.getEjbLink());
                        }
                     } else {
                        PatternType pattern = refType.getPattern();
                        this.addEjbLink(deployment, refName, pattern);
                     }
                  }
               }
            }
         }
      }

   }

   private void addEjbLink(EjbDeployment deployment, String refName, PatternType pattern) {
      String module = pattern.getModule();
      if (module == null) {
         module = pattern.getArtifactId();
      }

      String ejbName = pattern.getName();
      String deploymentId = module + "/" + ejbName;
      EjbLink ejbLink = new EjbLink(refName, deploymentId);
      deployment.getEjbLink().add(ejbLink);
   }

   public final void convertMdbConfigs(EjbJar ejbJar, OpenejbJarType openejbJarType) {
      Map<String, MessageDrivenBean> mdbs = new TreeMap();

      for(EnterpriseBean enterpriseBean : ejbJar.getEnterpriseBeans()) {
         if (enterpriseBean instanceof MessageDrivenBean) {
            mdbs.put(enterpriseBean.getEjbName(), (MessageDrivenBean)enterpriseBean);
         }
      }

      for(org.apache.openejb.jee.oejb2.EnterpriseBean enterpriseBean : openejbJarType.getEnterpriseBeans()) {
         if (enterpriseBean instanceof MessageDrivenBeanType) {
            MessageDrivenBeanType bean = (MessageDrivenBeanType)enterpriseBean;
            MessageDrivenBean mdb = (MessageDrivenBean)mdbs.get(bean.getEjbName());
            if (mdb != null) {
               ActivationConfigType activationConfigType = bean.getActivationConfig();
               if (activationConfigType != null) {
                  ActivationConfig activationConfig = mdb.getActivationConfig();
                  if (activationConfig == null) {
                     activationConfig = new ActivationConfig();
                     mdb.setActivationConfig(activationConfig);
                  }

                  for(ActivationConfigPropertyType propertyType : activationConfigType.getActivationConfigProperty()) {
                     ActivationConfigProperty property = new ActivationConfigProperty(propertyType.getActivationConfigPropertyName(), propertyType.getActivationConfigPropertyValue());
                     activationConfig.getActivationConfigProperty().add(property);
                  }
               }
            }
         }
      }

   }

   public final void mergeEntityMappings(String moduleId, EntityMappings entityMappings, OpenejbJar openejbJar, OpenejbJarType openejbJarType) {
      Map<String, EntityData> entities = new TreeMap();
      if (entityMappings != null) {
         for(Entity entity : entityMappings.getEntity()) {
            try {
               entities.put(entity.getDescription(), new EntityData(entity));
            } catch (IllegalArgumentException e) {
               LoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
            }
         }
      }

      for(org.apache.openejb.jee.oejb2.EnterpriseBean enterpriseBean : openejbJarType.getEnterpriseBeans()) {
         if (enterpriseBean instanceof EntityBeanType) {
            EntityBeanType bean = (EntityBeanType)enterpriseBean;
            EntityData entityData = (EntityData)entities.get(moduleId + "#" + bean.getEjbName());
            if (entityData != null) {
               Table table = new Table();
               table.setName(bean.getTableName());
               entityData.entity.setTable(table);

               for(EntityBeanType.CmpFieldMapping cmpFieldMapping : bean.getCmpFieldMapping()) {
                  String cmpFieldName = cmpFieldMapping.getCmpFieldName();
                  Field field = (Field)entityData.fields.get(cmpFieldName);
                  if (field != null) {
                     Column column = new Column();
                     column.setName(cmpFieldMapping.getTableColumn());
                     field.setColumn(column);
                  }
               }

               if (bean.getKeyGenerator() != null) {
                  Attributes attributes = entityData.entity.getAttributes();
                  if (attributes != null && attributes.getId().size() == 1) {
                     Id id = (Id)attributes.getId().get(0);
                     id.setGeneratedValue(new GeneratedValue(GenerationType.IDENTITY));
                  }
               }

               for(QueryType query : bean.getQuery()) {
                  NamedQuery namedQuery = new NamedQuery();
                  QueryType.QueryMethod queryMethod = query.getQueryMethod();
                  StringBuilder name = new StringBuilder();
                  name.append(entityData.entity.getName()).append(".").append(queryMethod.getMethodName());
                  if (queryMethod.getMethodParams() != null && !queryMethod.getMethodParams().getMethodParam().isEmpty()) {
                     name.append('(');
                     boolean first = true;

                     for(String methodParam : queryMethod.getMethodParams().getMethodParam()) {
                        if (!first) {
                           name.append(",");
                        }

                        name.append(methodParam);
                        first = false;
                     }

                     name.append(')');
                  }

                  namedQuery.setName(name.toString());
                  namedQuery.setQuery(query.getEjbQl());
                  entityData.entity.getNamedQuery().add(namedQuery);
               }
            }
         }
      }

      Iterator var22 = openejbJarType.getEjbRelation().iterator();

      while(true) {
         EjbRelationshipRoleType leftRole;
         RelationField field;
         while(true) {
            if (!var22.hasNext()) {
               return;
            }

            EjbRelationType relation = (EjbRelationType)var22.next();
            List<EjbRelationshipRoleType> roles = relation.getEjbRelationshipRole();
            if (!roles.isEmpty()) {
               if (relation.getManyToManyTableName() == null) {
                  leftRole = (EjbRelationshipRoleType)roles.get(0);
                  EjbRelationshipRoleType.RelationshipRoleSource leftRoleSource = leftRole.getRelationshipRoleSource();
                  String leftEjbName = leftRoleSource == null ? null : leftRoleSource.getEjbName();
                  EntityData leftEntityData = (EntityData)entities.get(moduleId + "#" + leftEjbName);
                  EjbRelationshipRoleType.CmrField cmrField = leftRole.getCmrField();
                  String leftFieldName = null != cmrField ? cmrField.getCmrFieldName() : null;
                  if (leftRole.isForeignKeyColumnOnSource()) {
                     field = null != leftFieldName && null != leftEntityData ? (RelationField)leftEntityData.relations.get(leftFieldName) : null;
                     if (field != null) {
                        break;
                     }
                  } else {
                     RelationField other = null != leftFieldName && null != leftEntityData ? (RelationField)leftEntityData.relations.get(leftFieldName) : null;
                     if (other != null) {
                        field = other.getRelatedField();
                        if (field != null) {
                           break;
                        }

                        if (other instanceof OneToMany) {
                           field = other;
                           break;
                        }
                     }
                  }
               } else {
                  JoinTable joinTable = new JoinTable();
                  joinTable.setName(relation.getManyToManyTableName());
                  EjbRelationshipRoleType leftRole = (EjbRelationshipRoleType)roles.get(0);
                  RelationField left = null;
                  if (leftRole.getRelationshipRoleSource() != null) {
                     String leftEjbName = leftRole.getRelationshipRoleSource().getEjbName();
                     EntityData leftEntityData = (EntityData)entities.get(moduleId + "#" + leftEjbName);
                     if (leftEntityData == null) {
                        continue;
                     }

                     EjbRelationshipRoleType.CmrField lcf = leftRole.getCmrField();
                     left = null != lcf ? (RelationField)leftEntityData.relations.get(lcf.getCmrFieldName()) : null;
                  }

                  if (left != null) {
                     left.setJoinTable(joinTable);
                     EjbRelationshipRoleType.RoleMapping roleMapping = leftRole.getRoleMapping();

                     for(EjbRelationshipRoleType.RoleMapping.CmrFieldMapping cmrFieldMapping : roleMapping.getCmrFieldMapping()) {
                        JoinColumn joinColumn = new JoinColumn();
                        joinColumn.setName(cmrFieldMapping.getForeignKeyColumn());
                        joinColumn.setReferencedColumnName(cmrFieldMapping.getKeyColumn());
                        joinTable.getJoinColumn().add(joinColumn);
                     }
                  }

                  if (roles.size() > 1) {
                     EjbRelationshipRoleType rightRole = (EjbRelationshipRoleType)roles.get(1);
                     if (left == null) {
                        EjbRelationshipRoleType.CmrField rcf = rightRole.getCmrField();
                        if (rcf == null) {
                           continue;
                        }

                        if (rightRole.getRelationshipRoleSource() != null) {
                           String rightEjbName = rightRole.getRelationshipRoleSource().getEjbName();
                           EntityData rightEntityData = (EntityData)entities.get(moduleId + "#" + rightEjbName);
                           if (rightEntityData == null) {
                              continue;
                           }

                           RelationField right = (RelationField)rightEntityData.relations.get(rcf.getCmrFieldName());
                           right.setJoinTable(joinTable);
                        }
                     }

                     EjbRelationshipRoleType.RoleMapping roleMapping = rightRole.getRoleMapping();

                     for(EjbRelationshipRoleType.RoleMapping.CmrFieldMapping cmrFieldMapping : roleMapping.getCmrFieldMapping()) {
                        JoinColumn joinColumn = new JoinColumn();
                        joinColumn.setName(cmrFieldMapping.getForeignKeyColumn());
                        joinColumn.setReferencedColumnName(cmrFieldMapping.getKeyColumn());
                        joinTable.getInverseJoinColumn().add(joinColumn);
                     }
                  }
               }
            }
         }

         if (field instanceof OneToOne) {
            OneToOne left = (OneToOne)field;
            OneToOne right = (OneToOne)left.getRelatedField();
            if (right != null) {
               left.setMappedBy((String)null);
               right.setMappedBy(left.getName());
            }
         }

         EjbRelationshipRoleType.RoleMapping roleMapping = leftRole.getRoleMapping();

         for(EjbRelationshipRoleType.RoleMapping.CmrFieldMapping cmrFieldMapping : roleMapping.getCmrFieldMapping()) {
            JoinColumn joinColumn = new JoinColumn();
            joinColumn.setName(cmrFieldMapping.getForeignKeyColumn());
            joinColumn.setReferencedColumnName(cmrFieldMapping.getKeyColumn());
            field.getJoinColumn().add(joinColumn);
         }
      }
   }

   public static GeronimoEjbJarType convertToGeronimoOpenejbXml(OpenejbJarType o2) {
      GeronimoEjbJarType g2 = new GeronimoEjbJarType();
      g2.setEnvironment(o2.getEnvironment());
      g2.setSecurity(o2.getSecurity());
      g2.getService().addAll(o2.getService());
      g2.getMessageDestination().addAll(o2.getMessageDestination());
      g2.getPersistence().addAll(o2.getPersistence());

      for(org.apache.openejb.jee.oejb2.EnterpriseBean bean : o2.getEnterpriseBeans()) {
         g2.getAbstractNamingEntry().addAll(bean.getAbstractNamingEntry());
         g2.getPersistenceContextRef().addAll(bean.getPersistenceContextRef());
         g2.getPersistenceUnitRef().addAll(bean.getPersistenceUnitRef());
         g2.getEjbLocalRef().addAll(bean.getEjbLocalRef());
         g2.getEjbRef().addAll(bean.getEjbRef());
         g2.getResourceEnvRef().addAll(bean.getResourceEnvRef());
         g2.getResourceRef().addAll(bean.getResourceRef());
         g2.getServiceRef().addAll(bean.getServiceRef());
         if (bean instanceof RpcBean) {
            RpcBean rpcBean = (RpcBean)bean;
            if (rpcBean.getTssLink() != null) {
               g2.getTssLink().add(new TssLinkType(rpcBean.getEjbName(), rpcBean.getTssLink(), rpcBean.getJndiName()));
            }
         }

         if (bean instanceof SessionBeanType) {
            SessionBeanType sb = (SessionBeanType)bean;
            WebServiceBindingType b = new WebServiceBindingType();
            b.setEjbName(sb.getEjbName());
            b.setWebServiceAddress(sb.getWebServiceAddress());
            b.setWebServiceVirtualHost(sb.getWebServiceVirtualHost());
            b.setWebServiceSecurity(sb.getWebServiceSecurity());
            if (b.containsData()) {
               g2.getWebServiceBinding().add(b);
            }
         }
      }

      return g2;
   }

   private static class EntityData {
      private final Entity entity;
      private final Map<String, Field> fields = new TreeMap();
      private final Map<String, RelationField> relations = new TreeMap();

      public EntityData(Entity e) {
         this.entity = e;
         if (this.entity == null) {
            throw new IllegalArgumentException("entity is null");
         } else {
            Attributes attributes = this.entity.getAttributes();
            if (attributes != null) {
               for(Id id : attributes.getId()) {
                  this.fields.put(id.getName(), id);
               }

               for(Basic basic : attributes.getBasic()) {
                  this.fields.put(basic.getName(), basic);
               }

               for(RelationField relationField : attributes.getOneToOne()) {
                  this.relations.put(relationField.getName(), relationField);
               }

               for(RelationField relationField : attributes.getOneToMany()) {
                  this.relations.put(relationField.getName(), relationField);
               }

               for(RelationField relationField : attributes.getManyToOne()) {
                  this.relations.put(relationField.getName(), relationField);
               }

               for(RelationField relationField : attributes.getManyToMany()) {
                  this.relations.put(relationField.getName(), relationField);
               }
            }

            for(AttributeOverride attributeOverride : this.entity.getAttributeOverride()) {
               this.fields.put(attributeOverride.getName(), attributeOverride);
            }

         }
      }
   }
}
