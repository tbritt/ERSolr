package er.solr;

import org.apache.solr.client.solrj.response.QueryResponse;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;

import er.extensions.eof.ERXEC;
import er.extensions.eof.ERXFetchSpecification;
import er.extensions.eof.ERXKey;
import er.extensions.foundation.ERXStringUtilities;


public class ERXSolrFetchSpecification<T extends EOEnterpriseObject> extends ERXFetchSpecification {

    private NSArray<EOSortOrdering> _sortOrderings;
    private NSArray<ERXKey<T>> _statisticsAttributes;
    private NSArray<SolrFacet> _facets;
    private EOQualifier _qualifier;
    private EOEditingContext _editingContext;
    private Integer _maxTime;
    private Integer _minFacetSize;
    private Integer _batchSize;
    private Integer _batchNumber;
    private Result _result;
    
    public ERXSolrFetchSpecification(String entityName, EOQualifier qualifier, NSArray sortOrderings, EOEditingContext editingContext) {
        super(entityName, qualifier, sortOrderings);
        _editingContext = editingContext;
    }
    
    public ERXSolrFetchSpecification(String entityName, EOQualifier qualifier, NSArray sortOrderings) {
        this(entityName, qualifier, sortOrderings, ERXEC.newEditingContext());
    }
    
    public ERXSolrFetchSpecification(String entityName) {
        this(entityName, null, null);
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
        return _facets;
    }

    /**
     * @param facets the facets to set. Will result in a new fetch.
     */
    public void setFacets(NSArray<SolrFacet> facets) {
        _facets = facets;
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
     * @return the maxTime
     */
    public Integer maxTime() {
        return _maxTime;
    }

    /**
     * Maximum mount of time allowed for the fetch. Specified in milliseconds.
     * @param maxTime the maxTime to set
     */
    public void setMaxTime(Integer maxTime) {
        _maxTime = maxTime;
    }

    /**
     * @return the minFacetSize
     */
    public Integer minFacetSize() {
        return _minFacetSize;
    }

    /**
     * The minimum number of facet items a facet must have to be included in the results. Will result in new fetch.
     * @param minFacetSize the minFacetSize to set
     */
    public void setMinFacetSize(Integer minFacetSize) {
        _minFacetSize = minFacetSize;
        queryDidChange(false);
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
        NSArray<T> objects = _editingContext.objectsWithFetchSpecification(this);
        _result._objects = objects;
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
        private NSDictionary<ERXKey, NSDictionary<String, Object>> statistics;
        private NSDictionary<ERXKey, NSDictionary<String, Object>> facetResults;
        
        private Result(){};
        
        public Result(QueryResponse queryResponse) {
            _queryResponse = queryResponse;
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
        
    }
}
