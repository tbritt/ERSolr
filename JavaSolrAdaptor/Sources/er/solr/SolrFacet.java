package er.solr;

import java.util.Enumeration;

import org.apache.solr.common.params.FacetParams;

import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableSet;

import er.extensions.eof.ERXKey;

public class SolrFacet {
    
    private Delegate _delegate;
    private Operator _operator;
    private ERXKey _attribute;
    private Sort _sort;
    private Integer _minCount;
    private Integer _limit;
    private NSArray<Item> _items;
    private NSMutableSet<Item> _selectedItems;
    
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
    
    public static class Item {
        private Object _value;
        private Integer _count;
        private EOQualifier _qualifier;
        
        private Item(){}
        
        public static Item newItem(Object value, EOQualifier qualifier) {
            Item item = new Item();
            item._value = value;
            item._qualifier = qualifier;
            return item;
        }
        
        /**
         * @return the value
         */
        public Object value() {
            return _value;
        }
        
        /**
         * @return the qualifier
         */
        public EOQualifier qualifier() {
            return _qualifier;
        }

        /**
         * @return the count
         */
        public Integer count() {
            return _count;
        }
        
        /**
         * @param count the count to set
         */
        public void setCount(Integer count) {
            _count = count;
        }

    }
    
    private SolrFacet(){
        _selectedItems = new NSMutableSet<SolrFacet.Item>();
    };
    
    public static SolrFacet newSolrFacet() {
        return newSolrFacet(null, null);
    }
    
    public static SolrFacet newSolrFacet(Delegate delegate) {
        return newSolrFacet(delegate, null);
    }
    
    public static SolrFacet newSolrFacet(Delegate delegate, NSArray<Item> items) {
        SolrFacet solrFacet = new SolrFacet();
        solrFacet._delegate = delegate;
        solrFacet._items = items;
        return solrFacet;
    }
    
    protected void facetDidChange() {
        if (_delegate != null) _delegate.facetDidChange();
    }
    
    /**
     * @return true if the facet is a query facet
     */
    public boolean isQuery() {
        for (Enumeration itemEnumerator = items().objectEnumerator(); itemEnumerator.hasMoreElements();) {
            SolrFacet.Item item = (SolrFacet.Item)itemEnumerator.nextElement();
            if (item.qualifier() != null) {
                return true;
            }
        }
        return false;
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

    public ERXKey attribute() {
        return _attribute;
    }
    
    /**
     * @return the items
     */
    public NSArray<Item> items() {
        if (_items == null) return NSArray.EmptyArray;
        return _items.immutableClone();
    }
    
    /**
     * @return the selectedItems
     */
    public NSArray<Item> selectedItems() {
        return _selectedItems.allObjects();
    }

    public void selectItem(Item item) {
        if (item != null) {
            _selectedItems.addObject(item);
            facetDidChange();
        }
    }

    public void deselectItem(Item item) {
        if (item != null) {
            _selectedItems.removeObject(item);
            facetDidChange();
        }
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

    /**
     * @return the delegate
     */
    public Delegate delegate() {
        return _delegate;
    }
    
    
}
