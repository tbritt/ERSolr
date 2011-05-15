package er.solr;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;

import er.extensions.eof.ERXFetchSpecification;
import er.extensions.eof.ERXKey;


public class ERXSolrFetchSpecification<T extends EOEnterpriseObject> extends ERXFetchSpecification {

    private EOQualifier _qualifier;
    private String _entityName;
    private Long _maxTime;
    private NSArray<EOSortOrdering> _sortOrderings;
    private Integer _minFacetSize;
    private Integer _maxBatchSize;
    private Integer _batchSize;
    private Integer _batchNumber;
    private NSArray<ERXKey<T>> _statisticAttributes;
    private NSArray<SolrFacet> _facets;
    
    public ERXSolrFetchSpecification(String entityName, EOQualifier qualifier, NSArray sortOrderings) {
        super(entityName, qualifier, sortOrderings);
    }

    public Results results(EOEditingContext ec) {
        return null;
    }
    
    public static class Results<T> {
        
        private NSArray<T> _objects;
        private Integer _batchSize;
        private Integer _startIndex;
        private Long _totalCount;
        private Long _queryTime;
        private NSDictionary<ERXKey, NSDictionary<String, Object>> statistics;
        
        public NSArray<T> objects() {
            return _objects;
        }
    }
}
