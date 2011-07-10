// DO NOT EDIT.  Make changes to Inventory.java instead.
package er.solr.example.eo;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import java.math.*;
import java.util.*;
import org.apache.log4j.Logger;

import er.extensions.eof.*;
import er.extensions.foundation.*;

@SuppressWarnings("all")
public abstract class _Inventory extends er.extensions.eof.ERXGenericRecord {
  public static final String ENTITY_NAME = "Inventory";

  // Solr UserInfo


  // Attribute Keys
  public static final ERXKey<String> CATEGORY = new ERXKey<String>("category");
  public static final ERXKey<com.webobjects.foundation.NSArray> FEATURES = new ERXKey<com.webobjects.foundation.NSArray>("features");
  public static final ERXKey<Boolean> IN_STOCK = new ERXKey<Boolean>("inStock");
  public static final ERXKey<String> MANUFACTURER = new ERXKey<String>("manufacturer");
  public static final ERXKey<String> NAME = new ERXKey<String>("name");
  public static final ERXKey<Integer> POPULARITY = new ERXKey<Integer>("popularity");
  public static final ERXKey<Double> PRICE = new ERXKey<Double>("price");
  public static final ERXKey<String> SKU = new ERXKey<String>("sku");
  public static final ERXKey<Double> WEIGHT = new ERXKey<Double>("weight");
  // Relationship Keys

  // Attributes
  public static final String CATEGORY_KEY = CATEGORY.key();
  public static final String FEATURES_KEY = FEATURES.key();
  public static final String IN_STOCK_KEY = IN_STOCK.key();
  public static final String MANUFACTURER_KEY = MANUFACTURER.key();
  public static final String NAME_KEY = NAME.key();
  public static final String POPULARITY_KEY = POPULARITY.key();
  public static final String PRICE_KEY = PRICE.key();
  public static final String SKU_KEY = SKU.key();
  public static final String WEIGHT_KEY = WEIGHT.key();
  // Relationships

  private static Logger LOG = Logger.getLogger(_Inventory.class);

  public Inventory localInstanceIn(EOEditingContext editingContext) {
    Inventory localInstance = (Inventory)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String category() {
    return (String) storedValueForKey(_Inventory.CATEGORY_KEY);
  }

  public void setCategory(String value) {
    if (_Inventory.LOG.isDebugEnabled()) {
    	_Inventory.LOG.debug( "updating category from " + category() + " to " + value);
    }
    takeStoredValueForKey(value, _Inventory.CATEGORY_KEY);
  }

  public com.webobjects.foundation.NSArray features() {
    return (com.webobjects.foundation.NSArray) storedValueForKey(_Inventory.FEATURES_KEY);
  }

  public void setFeatures(com.webobjects.foundation.NSArray value) {
    if (_Inventory.LOG.isDebugEnabled()) {
    	_Inventory.LOG.debug( "updating features from " + features() + " to " + value);
    }
    takeStoredValueForKey(value, _Inventory.FEATURES_KEY);
  }

  public Boolean inStock() {
    return (Boolean) storedValueForKey(_Inventory.IN_STOCK_KEY);
  }

  public void setInStock(Boolean value) {
    if (_Inventory.LOG.isDebugEnabled()) {
    	_Inventory.LOG.debug( "updating inStock from " + inStock() + " to " + value);
    }
    takeStoredValueForKey(value, _Inventory.IN_STOCK_KEY);
  }

  public String manufacturer() {
    return (String) storedValueForKey(_Inventory.MANUFACTURER_KEY);
  }

  public void setManufacturer(String value) {
    if (_Inventory.LOG.isDebugEnabled()) {
    	_Inventory.LOG.debug( "updating manufacturer from " + manufacturer() + " to " + value);
    }
    takeStoredValueForKey(value, _Inventory.MANUFACTURER_KEY);
  }

  public String name() {
    return (String) storedValueForKey(_Inventory.NAME_KEY);
  }

  public void setName(String value) {
    if (_Inventory.LOG.isDebugEnabled()) {
    	_Inventory.LOG.debug( "updating name from " + name() + " to " + value);
    }
    takeStoredValueForKey(value, _Inventory.NAME_KEY);
  }

  public Integer popularity() {
    return (Integer) storedValueForKey(_Inventory.POPULARITY_KEY);
  }

  public void setPopularity(Integer value) {
    if (_Inventory.LOG.isDebugEnabled()) {
    	_Inventory.LOG.debug( "updating popularity from " + popularity() + " to " + value);
    }
    takeStoredValueForKey(value, _Inventory.POPULARITY_KEY);
  }

  public Double price() {
    return (Double) storedValueForKey(_Inventory.PRICE_KEY);
  }

  public void setPrice(Double value) {
    if (_Inventory.LOG.isDebugEnabled()) {
    	_Inventory.LOG.debug( "updating price from " + price() + " to " + value);
    }
    takeStoredValueForKey(value, _Inventory.PRICE_KEY);
  }

  public String sku() {
    return (String) storedValueForKey(_Inventory.SKU_KEY);
  }

  public void setSku(String value) {
    if (_Inventory.LOG.isDebugEnabled()) {
    	_Inventory.LOG.debug( "updating sku from " + sku() + " to " + value);
    }
    takeStoredValueForKey(value, _Inventory.SKU_KEY);
  }

  public Double weight() {
    return (Double) storedValueForKey(_Inventory.WEIGHT_KEY);
  }

  public void setWeight(Double value) {
    if (_Inventory.LOG.isDebugEnabled()) {
    	_Inventory.LOG.debug( "updating weight from " + weight() + " to " + value);
    }
    takeStoredValueForKey(value, _Inventory.WEIGHT_KEY);
  }


  public static Inventory createInventory(EOEditingContext editingContext, String category
, com.webobjects.foundation.NSArray features
, Boolean inStock
, String manufacturer
, String name
, Integer popularity
, Double price
, String sku
, Double weight
) {
    Inventory eo = (Inventory) EOUtilities.createAndInsertInstance(editingContext, _Inventory.ENTITY_NAME);    
		eo.setCategory(category);
		eo.setFeatures(features);
		eo.setInStock(inStock);
		eo.setManufacturer(manufacturer);
		eo.setName(name);
		eo.setPopularity(popularity);
		eo.setPrice(price);
		eo.setSku(sku);
		eo.setWeight(weight);
    return eo;
  }

  public static ERXFetchSpecification<Inventory> fetchSpec() {
    return new ERXFetchSpecification<Inventory>(_Inventory.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<Inventory> fetchAllInventories(EOEditingContext editingContext) {
    return _Inventory.fetchAllInventories(editingContext, null);
  }

  public static NSArray<Inventory> fetchAllInventories(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _Inventory.fetchInventories(editingContext, null, sortOrderings);
  }

  public static NSArray<Inventory> fetchInventories(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<Inventory> fetchSpec = new ERXFetchSpecification<Inventory>(_Inventory.ENTITY_NAME, qualifier, sortOrderings);
    fetchSpec.setIsDeep(true);
    NSArray<Inventory> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static Inventory fetchInventory(EOEditingContext editingContext, String keyName, Object value) {
    return _Inventory.fetchInventory(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static Inventory fetchInventory(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<Inventory> eoObjects = _Inventory.fetchInventories(editingContext, qualifier, null);
    Inventory eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one Inventory that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Inventory fetchRequiredInventory(EOEditingContext editingContext, String keyName, Object value) {
    return _Inventory.fetchRequiredInventory(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static Inventory fetchRequiredInventory(EOEditingContext editingContext, EOQualifier qualifier) {
    Inventory eoObject = _Inventory.fetchInventory(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no Inventory that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Inventory localInstanceIn(EOEditingContext editingContext, Inventory eo) {
    Inventory localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}
