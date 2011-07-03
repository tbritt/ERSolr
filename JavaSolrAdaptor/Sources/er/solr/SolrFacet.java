package er.solr;

import org.apache.solr.common.params.FacetParams;

import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSMutableSet;

import er.solr.ERXSolrFetchSpecification.Result;

public class SolrFacet {
    
    private Delegate _delegate;
    private Operator _operator;
    private String _key;
    private Sort _sort;
    private Integer _minCount;
    private Integer _limit;
    private NSMutableDictionary<String, EOQualifier> _qualifiers;
    private NSMutableSet<FacetItem> _selectedItems;
    
    public static interface Delegate {
        public void facetDidChange();
    }
    
    public static enum Operator {
        AND,
        OR,
        NOT;
    }
    
    public static enum Sort {
        Count(FacetParams.FACET_SORT_COUNT),
        
        // This is set to Count on purpose. We alpha sort the results to account for solr's lack of case 
        // insensitive sorting and inadequate handling of mincount. Desired behavior is for solr to sort
        // by count up to the max defined in the schema, then take those results and apply alpha sort.
        Alpha(FacetParams.FACET_SORT_COUNT),
     
        // These are not currently supported directly by Solr. Used as a marker so they can be sorted later.
        Numeric(null),
        Boolean(null); 
        
        private String _solrValue;
        
        Sort(String solrValue) {
            _solrValue = solrValue;
        }
        
        public String solrValue() {
            return _solrValue;
        }
    }
    
    private SolrFacet() {
        _selectedItems = new NSMutableSet<FacetItem>();
        _qualifiers = new NSMutableDictionary<String, EOQualifier>();
    }
    
    public static SolrFacet newSolrFacet(String key) {
        return newSolrFacet(key, null);
    }
    
    public static SolrFacet newSolrFacet(String key, Delegate delegate) {
        SolrFacet solrFacet = new SolrFacet();
        solrFacet._delegate = delegate;
        solrFacet._key = key;
        return solrFacet;
    }

    protected void facetDidChange() {
        if (_delegate != null) _delegate.facetDidChange();
    }
    
    /**
     * The facet's current operator. Defaults to Operator.OR.
     */
    public Operator operator() {
        return _operator != null ? _operator : Operator.OR;
    }

    /**
     * @param operator the operator to set
     */
    public void setOperator(Operator operator) {
        _operator = operator;
        facetDidChange();
    }

    public String key() {
        return _key;
    }
    
    /**
     * @return the selectedItems
     */
    public NSArray<FacetItem> selectedItems() {
        return _selectedItems.allObjects();
    }

    public void selectItem(FacetItem item) {
        if (item != null) {
            _selectedItems.addObject(item);
            facetDidChange();
        }
    }

    public void deselectItem(FacetItem item) {
        if (item != null) {
            FacetItem itemToRemove = selectedFacetItemForKey(item.key());
            _selectedItems.removeObject(itemToRemove);
            facetDidChange();
        }
    }

    /**
     * Returns an NSdictionary where the key is the item and the value is the count.
     */
    public NSArray<NSDictionary <Object, Integer>> itemCounts(Result result) {
        //TODO
        return NSArray.EmptyArray;
    }
    
    /**
     * @return the sort
     */
    public Sort sort() {
        return _sort;
    }

    /**
     * @param sort the sort to set
     */
    public void setSort(Sort sort) {
        _sort = sort;
        facetDidChange();
    }

    /**
     * @return the minCount
     */
    public Integer minCount() {
        return _minCount;
    }

    /**
     * @param minCount the minCount to set
     */
    public void setMinCount(Integer minCount) {
        _minCount = minCount;
        facetDidChange();
    }
    
    /**
     * @return the limit
     */
    public Integer limit() {
        return _limit;
    }

    /**
     * @param limit the limit to set
     */
    public void setLimit(Integer limit) {
        _limit = limit;
        facetDidChange();
    }
    
    public boolean isQuery() {
        return _qualifiers.count() > 0;
    }
    
    public EOQualifier qualifierForKey(String key) {
        return (EOQualifier) _qualifiers.valueForKey(key);
    }
    
    public NSArray<String> qualifierKeys() {
        return _qualifiers.allKeys();
    }
    
    public void addQualifierForKey(EOQualifier qualifier, String key) {
        _qualifiers.takeValueForKey(qualifier, key);
        facetDidChange();
    }

    public void removeQualifierForKey(String key) {
        _qualifiers.removeObjectForKey(key);
        facetDidChange();
    }
    
    /**
     * @return the delegate
     */
    public Delegate delegate() {
        return _delegate;
    }
    
    public void setDelegate(Delegate delegate) {
        _delegate = delegate;
    }
    
    public FacetItem selectedFacetItemForKey(Object key) {
        for (FacetItem facetItem : selectedItems()) {
            if (facetItem.key().equals(key)) return facetItem;
        }
        return null;
    }
    
    public boolean isFacetItemSelected(FacetItem facetItem) {
        return selectedFacetItemForKey(facetItem.key()) != null;
    }
    
    public static class FacetItem {
        private Number _count;
        private Object _key;
        private SolrFacet _facet;
        
        public static FacetItem newFacetItem(Object key, Number count, SolrFacet facet) {
            FacetItem facetItem = new FacetItem();
            facetItem._key = key;
            facetItem._count = count;
            facetItem._facet = facet;
            return facetItem;
        }
        
        public Number count() {
            return _count;
        }

        public Object key() {
            return _key;
        }
        
        public EOQualifier qualifier() {
            return _facet.qualifierForKey(String.valueOf(_key));
        }
        
        public SolrFacet facet() {
            return _facet;
        }
    }
}
