/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.sdo.impl;


import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tuscany.sdo.SDOFactory;
import org.apache.tuscany.sdo.SDOPackage;
import org.apache.tuscany.sdo.util.BasicSequence;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.UniqueEList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.change.ChangeDescription;
import org.eclipse.emf.ecore.change.FeatureChange;
import org.eclipse.emf.ecore.change.impl.ChangeDescriptionImpl;
import org.eclipse.emf.ecore.change.util.ChangeRecorder;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData;
import org.eclipse.emf.ecore.util.DelegatingFeatureMap;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.FeatureMapUtil;
import org.eclipse.emf.ecore.util.InternalEList;

import commonj.sdo.ChangeSummary;
//import commonj.sdo.ChangeSummary.Setting;

import commonj.sdo.DataGraph;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;


/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>EChange Summary</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.apache.tuscany.sdo.impl.ChangeSummaryImpl#getEDataGraph <em>EData Graph</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ChangeSummaryImpl extends ChangeDescriptionImpl implements ChangeSummary
{
  /**
   * The cached value of the '{@link #getEDataGraph() <em>EData Graph</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEDataGraph()
   * @generated
   * @ordered
   */
  protected DataGraph eDataGraph = null;

  protected ChangeRecorder changeRecorder = null;
  protected EList cachedObjectsToDetach = null;
  protected HashMap cachedSDOObjectChanges = new HashMap();  

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ChangeSummaryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected EClass eStaticClass()
  {
    return SDOPackage.eINSTANCE.getChangeSummary();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean isLogging()
  {
    return changeRecorder != null;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DataGraph getEDataGraph()
  {
    return eDataGraph;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetEDataGraph(DataGraph newEDataGraph, NotificationChain msgs)
  {
    DataGraph oldEDataGraph = eDataGraph;
    eDataGraph = newEDataGraph;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, SDOPackage.CHANGE_SUMMARY__EDATA_GRAPH, oldEDataGraph, newEDataGraph);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setEDataGraph(DataGraph newEDataGraph)
  {
    if (newEDataGraph != eDataGraph)
    {
      NotificationChain msgs = null;
      if (eDataGraph != null)
        msgs = ((InternalEObject)eDataGraph).eInverseRemove(this, SDOPackage.DATA_GRAPH__ECHANGE_SUMMARY, DataGraph.class, msgs);
      if (newEDataGraph != null)
        msgs = ((InternalEObject)newEDataGraph).eInverseAdd(this, SDOPackage.DATA_GRAPH__ECHANGE_SUMMARY, DataGraph.class, msgs);
      msgs = basicSetEDataGraph(newEDataGraph, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SDOPackage.CHANGE_SUMMARY__EDATA_GRAPH, newEDataGraph, newEDataGraph));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public void beginLogging()
  {
    if (isLogging())
    {
      throw new IllegalStateException("Already logging");
    }

    getObjectsToAttach().clear();
    getObjectChanges().clear();
    getResourceChanges().clear();

    oldContainmentInformation = null;
    changeRecorder = new SDOChangeRecorder();
    changeRecorder.beginRecording(Collections.singleton(((DataGraphImpl)getEDataGraph()).getRootResource()));
//    if (eNotificationRequired())
//      eNotify(new ENotificationImpl(this, Notification.SET, SDOPackage.ECHANGE_SUMMARY__LOGGING, false, true));
  }
  
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public void resumeLogging()
  {
    if (isLogging())
    {
      throw new IllegalStateException("Already logging");
    }

    oldContainmentInformation = null;
    
    changeRecorder = new SDOChangeRecorder();
    changeRecorder.beginRecording(this, Collections.singleton(((DataGraphImpl)getEDataGraph()).getRootResource()));

//    if (eNotificationRequired())
//      eNotify(new ENotificationImpl(this, Notification.SET, SDOPackage.ECHANGE_SUMMARY__LOGGING, false, true));
  }  

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs)
  {
    if (featureID >= 0)
    {
      switch (eDerivedStructuralFeatureID(featureID, baseClass))
      {
        case SDOPackage.CHANGE_SUMMARY__EDATA_GRAPH:
          if (eDataGraph != null)
            msgs = ((InternalEObject)eDataGraph).eInverseRemove(this, SDOPackage.DATA_GRAPH__ECHANGE_SUMMARY, DataGraph.class, msgs);
          return basicSetEDataGraph((DataGraph)otherEnd, msgs);
        default:
          return eDynamicInverseAdd(otherEnd, featureID, baseClass, msgs);
      }
    }
    if (eInternalContainer() != null)
      msgs = eBasicRemoveFromContainer(msgs);
    return eBasicSetContainer(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs)
  {
    if (featureID >= 0)
    {
      switch (eDerivedStructuralFeatureID(featureID, baseClass))
      {
        case SDOPackage.CHANGE_SUMMARY__OBJECT_CHANGES:
          return ((InternalEList)getObjectChanges()).basicRemove(otherEnd, msgs);
        case SDOPackage.CHANGE_SUMMARY__OBJECTS_TO_ATTACH:
          return ((InternalEList)getObjectsToAttach()).basicRemove(otherEnd, msgs);
        case SDOPackage.CHANGE_SUMMARY__RESOURCE_CHANGES:
          return ((InternalEList)getResourceChanges()).basicRemove(otherEnd, msgs);
        case SDOPackage.CHANGE_SUMMARY__EDATA_GRAPH:
          return basicSetEDataGraph(null, msgs);
        default:
          return eDynamicInverseRemove(otherEnd, featureID, baseClass, msgs);
      }
    }
    return eBasicSetContainer(null, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
      case SDOPackage.CHANGE_SUMMARY__OBJECT_CHANGES:
        if (coreType) return getObjectChanges();
        else return getObjectChanges().map();
      case SDOPackage.CHANGE_SUMMARY__OBJECTS_TO_DETACH:
        return getObjectsToDetach();
      case SDOPackage.CHANGE_SUMMARY__OBJECTS_TO_ATTACH:
        return getObjectsToAttach();
      case SDOPackage.CHANGE_SUMMARY__RESOURCE_CHANGES:
        return getResourceChanges();
      case SDOPackage.CHANGE_SUMMARY__EDATA_GRAPH:
        return getEDataGraph();
    }
    return eDynamicGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case SDOPackage.CHANGE_SUMMARY__OBJECT_CHANGES:
        ((EStructuralFeature.Setting)getObjectChanges()).set(newValue);
        return;
      case SDOPackage.CHANGE_SUMMARY__OBJECTS_TO_DETACH:
        getObjectsToDetach().clear();
        getObjectsToDetach().addAll((Collection)newValue);
        return;
      case SDOPackage.CHANGE_SUMMARY__OBJECTS_TO_ATTACH:
        getObjectsToAttach().clear();
        getObjectsToAttach().addAll((Collection)newValue);
        return;
      case SDOPackage.CHANGE_SUMMARY__RESOURCE_CHANGES:
        getResourceChanges().clear();
        getResourceChanges().addAll((Collection)newValue);
        return;
      case SDOPackage.CHANGE_SUMMARY__EDATA_GRAPH:
        setEDataGraph((DataGraph)newValue);
        return;
    }
    eDynamicSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
      case SDOPackage.CHANGE_SUMMARY__OBJECT_CHANGES:
        getObjectChanges().clear();
        return;
      case SDOPackage.CHANGE_SUMMARY__OBJECTS_TO_DETACH:
        getObjectsToDetach().clear();
        return;
      case SDOPackage.CHANGE_SUMMARY__OBJECTS_TO_ATTACH:
        getObjectsToAttach().clear();
        return;
      case SDOPackage.CHANGE_SUMMARY__RESOURCE_CHANGES:
        getResourceChanges().clear();
        return;
      case SDOPackage.CHANGE_SUMMARY__EDATA_GRAPH:
        setEDataGraph((DataGraph)null);
        return;
    }
    eDynamicUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
      case SDOPackage.CHANGE_SUMMARY__OBJECT_CHANGES:
        return objectChanges != null && !objectChanges.isEmpty();
      case SDOPackage.CHANGE_SUMMARY__OBJECTS_TO_DETACH:
        return objectsToDetach != null && !objectsToDetach.isEmpty();
      case SDOPackage.CHANGE_SUMMARY__OBJECTS_TO_ATTACH:
        return objectsToAttach != null && !objectsToAttach.isEmpty();
      case SDOPackage.CHANGE_SUMMARY__RESOURCE_CHANGES:
        return resourceChanges != null && !resourceChanges.isEmpty();
      case SDOPackage.CHANGE_SUMMARY__EDATA_GRAPH:
        return eDataGraph != null;
    }
    return eDynamicIsSet(featureID);
  }

  protected class SDOChangeRecorder extends ChangeRecorder
  {
    public SDOChangeRecorder()
    {
      super();
    }
    
    public void beginRecording(ChangeDescription changeDescription, Collection rootObjects)
    {
      deletedObjects = null;
      cachedObjectsToDetach = null;
      cachedSDOObjectChanges.clear();
      super.beginRecording(changeDescription, rootObjects);
    }

    protected ChangeDescription createChangeDescription()
    {
      return ChangeSummaryImpl.this;
    }

    protected FeatureChange createFeatureChange(EObject eObject, EStructuralFeature eStructuralFeature, Object value, boolean isSet)
    {
      Property property = (Property)eStructuralFeature;
      if (property.isReadOnly())
      {
        if (((DataObject)eObject).getDataGraph() != null)
        {
          throw 
            new IllegalStateException
              ("The property '" + property.getName() + "' of type '" + 
                 property.getContainingType().getName() + "' is read only");
        }
      }
      return (FeatureChange)SDOFactory.eINSTANCE.createChangeSummarySetting(eStructuralFeature, value, isSet);
    }
    
    protected void consolidateChanges()
    {
      deletedObjects = null;
      cachedObjectsToDetach = null;
      cachedSDOObjectChanges.clear();
      super.consolidateChanges();
    }

    protected void addAdapter(Notifier notifier)
    {
      if (notifier instanceof DataObjectImpl)
        ((DataObjectImpl)notifier).setChangeRecorder(this);
      else
        super.addAdapter(notifier);
    }

    protected void removeAdapter(Notifier notifier)
    {
      if (notifier instanceof DataObjectImpl)
        ((DataObjectImpl)notifier).setChangeRecorder(null);
      else
        super.removeAdapter(notifier);
    }
    
  } 

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public void endLogging()
  {
    if (!isLogging())
    {
      throw new IllegalStateException("Not currently logging");
    }
    
    changeRecorder.endRecording();
    changeRecorder.dispose();
    changeRecorder = null;
//    if (eNotificationRequired())
//      eNotify(new ENotificationImpl(this, Notification.SET, SDOPackage.ECHANGE_SUMMARY__LOGGING, true, false));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean isCreated(DataObject dataObject)
  {
    return getObjectsToDetach().contains(dataObject);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean isDeleted(DataObject dataObject)
  {
    return getDeletedObjects().contains(dataObject);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public List getOldValues(DataObject dataObject)
  {
    List sdoSettings = (List)cachedSDOObjectChanges.get(dataObject);
    if (sdoSettings != null)
    {
      return sdoSettings;
    }
    
    List settings = (List)getObjectChanges().get(dataObject);
    if (settings == null)
    {
      settings = Collections.EMPTY_LIST;
    }
    else
    {
      for (int i = 0; i < settings.size(); i++)
      {
        FeatureChange change = (FeatureChange)settings.get(i);
        EStructuralFeature feature = change.getFeature();
        if (FeatureMapUtil.isFeatureMap(feature))
        {
          final List values = (List)change.getValue();
          if (sdoSettings == null)
          {
            sdoSettings = new BasicEList(settings);
          }
          DelegatingFeatureMap featureMap = new DelegatingFeatureMap(((InternalEObject)dataObject), feature)
            {
              protected final List theList = values;
  
              protected List delegateList()
              {
                return theList;
              }
            };
  
          // create new settings and replace the setting for mixed feature
          sdoSettings.set(i, SDOFactory.eINSTANCE.createChangeSummarySetting(feature, new BasicSequence(featureMap), change.isSet()));
          // add all derived features
          for (int k = 0; k < featureMap.size(); k++)
          {
            EStructuralFeature f = featureMap.getEStructuralFeature(k);
            sdoSettings.add(SDOFactory.eINSTANCE.createChangeSummarySetting(f, featureMap.get(f, false), true));
          }
        }
      }
    }
    sdoSettings = (sdoSettings != null) ? sdoSettings : settings;
    cachedSDOObjectChanges.put(dataObject, sdoSettings);
    return sdoSettings;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean isModified(DataObject dataObject)
  {
    return getObjectChanges().containsKey(dataObject) && !isDeleted(dataObject) && !isCreated(dataObject);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public void summarize()
  {
    if (!isLogging())
    {
      throw new IllegalStateException("Not currently logging");
    }
    
    changeRecorder.summarize();
  }

  public Setting getOldValue(DataObject dataObject, Property property)
  {
    for (Iterator i = getOldValues(dataObject).iterator(); i.hasNext(); )
    {
      Setting setting = (Setting)i.next();
      if (setting.getProperty() == property)
      {
        return setting;
      }
    }

    return null;
  }

  public DataObject getOldContainer(DataObject dataObject)
  {
    return (DataObject)getOldContainer((EObject)dataObject);
  }

  public Property getOldContainmentProperty(DataObject dataObject)
  {
    return (Property)getOldContainmentFeature((EObject)dataObject);
  }
  
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Sequence getOldSequence(DataObject dataObject)
  {
    EAttribute mixedFeature = BasicExtendedMetaData.INSTANCE.getMixedFeature((EClass)dataObject.getType());
    if (mixedFeature != null)
    {
      return (Sequence)getOldValue(dataObject, (Property)mixedFeature).getValue();
    }
    return null;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public void undoChanges()
  {
    if (isLogging())
    {
      changeRecorder.summarize();
    }
    apply();
  }

  public EList getObjectsToDetach()
  {
    if (cachedObjectsToDetach == null)
    {
      cachedObjectsToDetach = super.getObjectsToDetach();
    }
    return cachedObjectsToDetach;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public DataGraph getDataGraph()
  {
    return getEDataGraph();
  }

  protected Set deletedObjects;

  protected void preApply(boolean reverse)
  {
    super.preApply(reverse);
    deletedObjects = null;
  }

  protected Set getDeletedObjects()
  {
    if (deletedObjects == null)
    {
      deletedObjects = new HashSet();
      for (Iterator i = EcoreUtil.getAllContents(getObjectsToAttach()); i.hasNext(); )
      {
        deletedObjects.add(i.next());
      }
    }
    return deletedObjects;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public List getChangedDataObjects()
  {
    EList result = new UniqueEList.FastCompare(getDeletedObjects());
    result.addAll(getObjectsToDetach());
    for (Iterator i = getObjectChanges().iterator(); i.hasNext(); )
    {
      Map.Entry entry = (Map.Entry)i.next();
      result.add(entry.getKey());
    }
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public DataObject getRootObject()
  {
    DataGraph dataGraph = getDataGraph();
    if (dataGraph != null)
    {
      return dataGraph.getRootObject();
    }
    // TODO: handle ChangeSummary-type property
    return null;
  }

} //EChangeSummaryImpl
