package er.solr;

import java.util.Map;

import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;

import er.extensions.eof.ERXEC;
import er.extensions.eof.ERXFetchSpecification;
import er.extensions.eof.ERXKey;
import er.extensions.foundation.ERXStringUtilities;
import er.solr.SolrFacet.FacetItem;


public class ERXSolrFetchSpecification<T extends EOEnterpriseObject> extends ERXFetchSpecification implements SolrFacet.Delegate {

    private NSArray<EOSortOrdering> _sortOrderings;
    private NSArray<ERXKey<T>> _statisticsAttributes;
    private NSMutableArray<SolrFacet> _facets;
    private EOQualifier _qualifier;
    private EOEditingContext _editingContext;
    private Integer _maxTime;
    private Integer _defaultMinFacetSize;
    private Integer _defaultFacetLimit;
    private Integer _batchSize;
    private Integer _batchNumber;
    private Result _result;
    
    public ERXSolrFetchSpecification(String entityName, EOQualifier qualifier, NSArray sortOrderings, EOEditingContext editingContext) {
        super(entityName, qualifier, sortOrderings);
        _facets = new NSMutableArray<SolrFacet>();
        _editingContext = editingContext;
    }
    
    public ERXSolrFetchSpecification(String entityName, EOQualifier qualifier, NSArray sortOrderings) {
        this(entityName, qualifier, sortOrderings, ERXEC.newEditingContext());
    }
    
    public ERXSolrFetchSpecification(String entityName) {
        this(entityName, null, null);
    }
    
    /**
     * SolrFacet.Delegate method. 
     */
    public void facetDidChange() {
        queryDidChange(true);
    }
    
    /**
     * Resets the results so that the next access will cause a fetch.
     * @param resetBatchNumber Whether to reset the current batch number or not.
     */
    protected void queryDidChange(boolean resetBatchNumber) {
        _result = null;
        if (resetBatchNumber) {
            _batchNumber = Integer.valueOf(0);
        }
    }

    /**
     * @return the sortOrderings
     */
    public NSArray<EOSortOrdering> sortOrderings() {
        return _sortOrderings;
    }

    /**
     * @param sortOrderings the sortOrderings to set. Will result in new fetch.
     */
    public void setSortOrderings(NSArray<EOSortOrdering> sortOrderings) {
        _sortOrderings = sortOrderings;
        queryDidChange(true);
    }

    /**
     * @return the statisticsAttributes.
     */
    public NSArray<ERXKey<T>> statisticsAttributes() {
        return _statisticsAttributes;
    }

    /**
     * @param statisticsAttributes the statisticsAttributes to set. Will result in new fetch.
     */
    public void setStatisticsAttributes(NSArray<ERXKey<T>> statisticsAttributes) {
        _statisticsAttributes = statisticsAttributes;
        queryDidChange(false);
    }

    /**
     * @return the facets
     */
    public NSArray<SolrFacet> facets() {
        return _facets.immutableClone();
    }

    /**
     * @param facet the facet to add. Will result in a new fetch.
     */
    public void addFacet(SolrFacet facet) {
        facet.setDelegate(this);
        _facets.addObject(facet);
        queryDidChange(true);
    }
    
    public SolrFacet facetForKey(String key) {
        for (SolrFacet facet: facets()) {
            if (facet.key().equals(key)) return facet;
        }
        return null;
    }
    
    /**
     * @param facet the facet to remove. Will result in a new fetch.
     */
    public void removeFacet(SolrFacet facet) {
        _facets.removeObject(facet);
        queryDidChange(true);
    }

    /**
     * @return the qualifier
     */
    public EOQualifier qualifier() {
        return _qualifier;
    }

    /**
     * @param qualifier the qualifier to set. Will result in a new fetch.
     */
    public void setQualifier(EOQualifier qualifier) {
        // TODO: Compare old and new qualifiers?
        _qualifier = qualifier;
        queryDidChange(true);
    }

    /**
     * @param entityName the entityName to set. Will result in a new fetch. Cannot be null.
     */
    public void setEntityName(String entityName) {
        if (ERXStringUtilities.stringIsNullOrEmpty(entityName)) {
            throw new IllegalArgumentException("ERXSolrFetchSpecification.entityName cannot be null or empty.");
        }
        else if (!super.entityName().equals(entityName)) {
            super.setEntityName(entityName);
            queryDidChange(true);
        }
    }

    /**
     * Specified in milliseconds.
     * @return the maxTime in milliseconds.
     */
    public Integer maxTime() {
        return _maxTime;
    }

    /**
     * Maximum mount of time allowed for the fetch. Specified in milliseconds.
     * @param maxTime the maxTime to set in milliseconds.
     */
    public void setMaxTime(Integer maxTime) {
        _maxTime = maxTime;
    }

    /**
     * @return the minFacetSize
     */
    public Integer defaultMinFacetSize() {
        return _defaultMinFacetSize;
    }

    /**
     * The minimum number of facet items a facet must have to be included in the results. Will result in new fetch.
     * @param minFacetSize the minFacetSize to set
     */
    public void setDefaultMinFacetSize(Integer minFacetSize) {
        if (_defaultMinFacetSize != minFacetSize) {
            _defaultMinFacetSize = minFacetSize;
            queryDidChange(false);
        }
    }
    
    /**
     * @return the facetLimit
     */
    public Integer defaultFacetLimit() {
        return _defaultFacetLimit;
    }

    /**
     * The maximum number of facet items included in the results. Will result in new fetch.
     * @param facetLimit the facetLimit to set
     */
    public void setDefaultFacetLimit(Integer facetLimit) {
        if (_defaultFacetLimit != facetLimit) {
            _defaultFacetLimit = facetLimit;
            queryDidChange(false);
        }
    }

    /**
     * @return the batchSize
     */
    public Integer batchSize() {
        return _batchSize;
    }

    /**
     * Changes the current batch size. Will result in a new fetch.
     * @param batchSize the batchSize to set
     */
    public void setBatchSize(Integer batchSize) {
        if (_batchSize != batchSize) {
            queryDidChange(true);
            _batchSize = batchSize;
        }
    }

    /**
     * Will always be at least 1.
     * @return the batchNumber
     */
    public Integer batchNumber() {
        return _batchNumber == null || _batchNumber < 1 ? Integer.valueOf(1) : _batchNumber;
    }

    /**
     * Changes the current batch number.
     * @param batchNumber the batchNumber to set
     */
    public void setBatchNumber(Integer batchNumber) {
        if (_batchNumber != batchNumber) {
            _batchNumber = batchNumber;
            queryDidChange(false);
        }
    }

    protected void fetch() {
        NSArray objects = _editingContext.objectsWithFetchSpecification(this);
        if (_result != null) {
            _result._objects = objects;
        }
    }
    
    public void setResult(Result<T> result) {
        _result = result;
    }
    
    /**
     * @return the current Result
     */
    public Result<T> result() {
        if (_result == null) {
            fetch();
        }
        return _result;
    }
    
    public boolean isBatching() {
        return (_batchSize != null && _batchSize.intValue() > 0 && _batchSize < Integer.MAX_VALUE);
    }
    
    public static class Result<T> {
        private NSArray<T> _objects;
        private QueryResponse _queryResponse;
        private ERXSolrFetchSpecification _solrFetchSpecification;
        private NSMutableDictionary<String, NSArray<FacetItem>> _facetResults;
        
        private Result(){};
        
        public static Result newResult(QueryResponse queryResponse, ERXSolrFetchSpecification solrFetchSpecification) {
            Result result = new Result();
            result._queryResponse = queryResponse;
            result._solrFetchSpecification = solrFetchSpecification;
            result._processResponse();
            return result;
        }

        public Long totalCount() {
            return _queryResponse.getResults().getNumFound();
        }
        
        public Long queryTime() {
            return _queryResponse.getElapsedTime();
        }
        
        public NSArray<T> objects() {
            return _objects;
        }
        
        public NSArray<FacetItem> facetItems(String facetKey) {
            return _facetResults.objectForKey(facetKey);
        }
        
        private void _processResponse() {
            _facetResults = new NSMutableDictionary<String, NSArray<FacetItem>>();
            
            // Facet fields
            for (FacetField facetField : _queryResponse.getFacetFields()) {
                NSMutableArray<FacetItem> facetItems = new NSMutableArray<FacetItem>();
                for (Count count : facetField.getValues()) {
                    FacetItem facetItem = FacetItem.newFacetItem(count.getName(), count.getCount(), _solrFetchSpecification.facetForKey(facetField.getName()));
                    facetItems.addObject(facetItem);
                }
                _facetResults.takeValueForKey(facetItems, facetField.getName());
            }
            
            // Facet queries
            Map<String, Integer> facetQueries = _queryResponse.getFacetQuery();
            if (facetQueries.size() > 0) {
                for (String key : facetQueries.keySet()) {
                    String queryPrefix = ERXStringUtilities.firstPropertyKeyInKeyPath(key);
                    String originalKey = key.substring(queryPrefix.length() + 1);
                    FacetItem facetItem = FacetItem.newFacetItem(originalKey, facetQueries.get(key), _solrFetchSpecification.facetForKey(queryPrefix));
                    if (_facetResults.containsKey(queryPrefix)) {
                        ((NSMutableArray)_facetResults.objectForKey(queryPrefix)).add(facetItem);
                    }
                    else {
                        _facetResults.takeValueForKey(new NSMutableArray<FacetItem>(facetItem), queryPrefix);
                    }
                }
            }
            
        }
    }
    
}
