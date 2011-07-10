package er.solr.adaptor;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.params.FacetParams;

import com.ibm.icu.math.BigDecimal;
import com.webobjects.eoaccess.EOAdaptorChannel;
import com.webobjects.eoaccess.EOAdaptorContext;
import com.webobjects.eoaccess.EOAttribute;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOGeneralAdaptorException;
import com.webobjects.eoaccess.EOModel;
import com.webobjects.eoaccess.EOSQLExpression;
import com.webobjects.eoaccess.EOStoredProcedure;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSData;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSForwardException;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSPropertyListSerialization;
import com.webobjects.foundation.NSSelector;
import com.webobjects.foundation.NSTimestamp;

import er.extensions.foundation.ERXKeyValueCodingUtilities;
import er.extensions.foundation.ERXMutableURL;
import er.extensions.foundation.ERXStringUtilities;
import er.solr.ERXSolrFetchSpecification;
import er.solr.SolrFacet;
import er.solr.SolrFacet.FacetItem;


public class ERSolrAdaptorChannel extends EOAdaptorChannel {

    private NSArray<EOAttribute> _attributes;
    private NSMutableArray<NSMutableDictionary<String, Object>> _fetchedRows;
    private int _fetchIndex;
    private boolean _open;
    
    private static final Logger log = Logger.getLogger(ERSolrAdaptorChannel.class);
    
    
    public ERSolrAdaptorChannel(EOAdaptorContext context) {
        super(context);
        _fetchIndex = -1;
    }
    
    public ERSolrAdaptorContext context() {
        return (ERSolrAdaptorContext) _context;
    }

    @Override
    public NSArray<EOAttribute> attributesToFetch() {
        return _attributes;
    }

    @Override
    public void cancelFetch() {
        _fetchedRows = null;
        _fetchIndex = -1;
    }

    @Override
    public void closeChannel() {
        _open = false;
    }

    @Override
    // TODO
    public int deleteRowsDescribedByQualifier(EOQualifier qualifier, EOEntity entity) {
        throw new UnsupportedOperationException("ERSolrAdaptorChannel.deleteRowsDescribedByQualifier");
    }

    @Override
    public NSArray<EOAttribute> describeResults() {
        return _attributes;
    }
    
    @Override
    public NSArray describeTableNames() {
        return NSArray.EmptyArray;
    }
    
    @Override
    public EOModel describeModelWithTableNames(NSArray anArray) {
        return null;
    }

    @Override
    public void evaluateExpression(EOSQLExpression arg0) {
        throw new UnsupportedOperationException("ERSolrAdaptorChannel.evaluateExpression");
    }

    @Override
    public void executeStoredProcedure(EOStoredProcedure arg0, NSDictionary arg1) {
        throw new UnsupportedOperationException("ERSolrAdaptorChannel.executeStoredProcedure");
    }

    @Override
    public NSMutableDictionary<String, Object> fetchRow() {
        NSMutableDictionary<String, Object> row = null;
        if (_fetchedRows != null && _fetchIndex < _fetchedRows.count()) {
          row = _fetchedRows.objectAtIndex(_fetchIndex++);
        }
        return row;
    }

    @Override
    // TODO
    public void insertRow(NSDictionary<String, Object> arg0, EOEntity arg1) {
        throw new UnsupportedOperationException("ERSolrAdaptorChannel.insertRow");
    }

    @Override
    public boolean isFetchInProgress() {
        return _fetchedRows != null && _fetchIndex < _fetchedRows.count();
    }

    @Override
    public boolean isOpen() {
        return _open;
    }

    @Override
    public void openChannel() {
        if (!_open) {
            _open = true;
        }
    }

    @Override
    public NSDictionary returnValuesForLastStoredProcedureInvocation() {
        throw new UnsupportedOperationException("ERSolrAdaptorChannel.returnValuesForLastStoredProcedureInvocation");
    }
    
    @Override
    public void setAttributesToFetch(NSArray<EOAttribute> attributesToFetch) {
        if (attributesToFetch == null) {
            throw new IllegalArgumentException("ERSolrAdaptorChannel.setAttributesToFetch: null attributes.");
        }
        _attributes = attributesToFetch;
    }

    @Override
    // TODO
    public int updateValuesInRowsDescribedByQualifier(NSDictionary<String, Object> arg0, EOQualifier arg1, EOEntity arg2) {
        throw new UnsupportedOperationException("ERSolrAdaptorChannel.updateValuesInRowsDescribedByQualifier");
    }


    /**
     * Selects rows matching the specified qualifier.
     */
    @Override
    public void selectAttributes(NSArray<EOAttribute> attributesToFetch, EOFetchSpecification fetchSpecification, boolean shouldLock, EOEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("null entity.");
        }
        if (attributesToFetch == null) {
            throw new IllegalArgumentException("null attributes.");
        }
        
        ERXSolrFetchSpecification solrFetchSpecification = null;
        if (fetchSpecification instanceof ERXSolrFetchSpecification) {
            solrFetchSpecification = (ERXSolrFetchSpecification)fetchSpecification;
        }

        setAttributesToFetch(attributesToFetch);

        try {
            _fetchIndex = 0;
            _fetchedRows = new NSMutableArray<NSMutableDictionary<String, Object>>();
            
            NSDictionary connectionDictionary = adaptorContext().adaptor().connectionDictionary();
            String solrUrl = (String)connectionDictionary.objectForKey("URL");
            String solrCore = entity.externalName();
            
            if (ERXStringUtilities.stringIsNullOrEmpty(solrUrl)) {
                throw new IllegalArgumentException("There is no URL specified for the connection dictionary: " + connectionDictionary);
            }
            
            ERXMutableURL url = new ERXMutableURL(solrUrl);
            if (solrCore != null && !solrCore.equalsIgnoreCase("solr")) {
                url.setPath(url.path() + solrCore);
            }
            
            EOQualifier qualifier = fetchSpecification.qualifier();
            ERSolrExpression solrExpression = ERSolrExpression.newERSolrExpression(entity);
            String solrQueryString = solrExpression.solrStringForQualifier(qualifier);
            
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery(solrQueryString);
            solrQuery.setRows(Integer.MAX_VALUE);
            
            // Sorting
            _applySortOrderings(solrQuery, fetchSpecification.sortOrderings());
            
            if (solrFetchSpecification != null) {
                if (solrFetchSpecification.maxTime() != null) {
                    solrQuery.setTimeAllowed(solrFetchSpecification.maxTime());
                }
                
                // Batching
                if (solrFetchSpecification.isBatching()) {
                    Integer numberOfRowsPerBatch = solrFetchSpecification.batchSize() != null ? solrFetchSpecification.batchSize() : Integer.MAX_VALUE;
                    Integer rowOffset = (solrFetchSpecification.batchNumber().intValue() * numberOfRowsPerBatch.intValue()) - numberOfRowsPerBatch;
                    solrQuery.setStart(rowOffset);
                    solrQuery.setRows(numberOfRowsPerBatch);
                }
                
                // Facets
                if (solrFetchSpecification.facets() != null && solrFetchSpecification.facets().count() > 0) {
                    solrQuery.setFacet(true);
                    
                    if (solrFetchSpecification.defaultMinFacetSize() != null) {
                        solrQuery.setFacetMinCount(solrFetchSpecification.defaultMinFacetSize());
                    }
                    
                    if (solrFetchSpecification.defaultFacetLimit() != null) {
                        solrQuery.setFacetLimit(solrFetchSpecification.defaultFacetLimit());
                    }
                    
                    for (Enumeration e = solrFetchSpecification.facets().objectEnumerator(); e.hasMoreElements();) {
                        SolrFacet facet = (SolrFacet)e.nextElement();
                        
                        if (facet.sort() != null && facet.sort().solrValue() != null) {
                            solrQuery.setParam("f." + facet.key() + "." + (FacetParams.FACET_SORT), facet.sort().solrValue());
                        }
                        
                        if (facet.minCount() != null) {
                            solrQuery.setParam("f." + facet.key() + "." + (FacetParams.FACET_MINCOUNT), String.valueOf(facet.minCount()));
                        }
                        
                        if (facet.limit() != null) {
                            solrQuery.setParam("f." + facet.key() + "." + (FacetParams.FACET_LIMIT), String.valueOf(facet.limit()));
                        }
                        
                        
                        // A selected facet item can be excluded from the counts for other facet items, this is important
                        // for supporting multiple selection. The only case where it should not be excluded is when the 
                        // facet attribute is multi-value and the face operator is AND.
                        boolean isExcludingFromCounts = true;
                        EOAttribute attribute = entity.attributeNamed(facet.key());
                        
                        if (attribute == null) {
                            throw new IllegalStateException("Can not find EOAttribute in entity " + entity.name() + " for facet key " + facet.key());
                        }
                        
                        boolean isMultiValue = attribute.valueTypeClassName().endsWith("NSArray");
                        if (isMultiValue && SolrFacet.Operator.AND.equals(facet.operator())) {
                            isExcludingFromCounts = false;
                        }
                        
                        // Facet queries
                        if (facet.isQuery()) {
                            String qualifierKeyPrefix = facet.key() + ".";
                            for (Enumeration qualifierKeyEnumeration = facet.qualifierKeys().objectEnumerator(); qualifierKeyEnumeration.hasMoreElements();) {
                                String qualifierKey = (String)qualifierKeyEnumeration.nextElement();
                                String prefixedQualifierKey = qualifierKeyPrefix + qualifierKey;
                                NSMutableDictionary<String, String> parameters = new NSMutableDictionary<String, String>();
                                
                                // Set parameter on qualifier with its key so it can be pulled out of the results by the same key:
                                // facet.query={!key=facetKey.qualifierKey}some_query
                                parameters.takeValueForKey(prefixedQualifierKey, ERSolrExpression.PARAMETER_KEY);
                                
                                if (isExcludingFromCounts) {
                                    parameters.takeValueForKey(facet.key(), ERSolrExpression.PARAMETER_EXCLUSION);
                                }
                                
                                StringBuilder sb = new StringBuilder();
                                ERSolrExpression.appendLocalParams(sb, parameters);
                                EOQualifier facetQualifier = facet.qualifierForKey(qualifierKey);
                                sb.append(solrExpression.solrStringForQualifier(facetQualifier));
                                solrQuery.addFacetQuery(sb.toString());
                            }
                        }
                        
                        
                        // Facet fields
                        else {
                            StringBuilder sb = new StringBuilder();
                            if (isExcludingFromCounts) {
                                ERSolrExpression.appendLocalParams(sb, new NSDictionary<String, String>(facet.key(), ERSolrExpression.PARAMETER_EXCLUSION));  
                            }
                            sb.append(facet.key());
                            solrQuery.addFacetField(sb.toString());
                        }
                        
                        
                        // Create filter query based on selected facet items.
                        if (facet.selectedItems() != null && facet.selectedItems().count() > 0) {
                            StringBuilder filterQuery = new StringBuilder();
                            
                            if (isExcludingFromCounts) {
                                ERSolrExpression.appendLocalParams(filterQuery, new NSDictionary<String, String>(facet.key(), ERSolrExpression.PARAMETER_TAG));
                            }
                            
                            filterQuery.append("(");
                            for (Enumeration facetItemEnumeration = facet.selectedItems().objectEnumerator(); facetItemEnumeration.hasMoreElements();) {
                                FacetItem selectedFacetItem = (FacetItem)facetItemEnumeration.nextElement();
                                String operator = null; 
                                if (SolrFacet.Operator.NOT.equals(facet.operator())) {
                                    filterQuery.append(SolrFacet.Operator.NOT.toString()).append(" ");
                                    operator = SolrFacet.Operator.AND.toString();
                                }
                                else {
                                    operator = facet.operator().toString();
                                }
                                
                                if (selectedFacetItem.qualifier() != null) {
                                    filterQuery.append(solrExpression.solrStringForQualifier(selectedFacetItem.qualifier()));
                                }
                                else {
                                    filterQuery.append(facet.key()).append(":");
                                    ERSolrExpression.escapeAndAppend(selectedFacetItem.key(), filterQuery);
                                }
                                
                                if (facetItemEnumeration.hasMoreElements()) {
                                    filterQuery.append(" ").append(operator).append(" ");
                                }
                            }
                            filterQuery.append(")");
                            solrQuery.addFilterQuery(filterQuery.toString());
                        }
                    }
                }
            }
            
            CommonsHttpSolrServer solrServer = new CommonsHttpSolrServer(url.toURL());
            QueryResponse queryResponse = solrServer.query(solrQuery);
            
            //TODO: Handle error responses from Solr
            
            if (log.isDebugEnabled()) {
                log.debug("Original qualifier: " + qualifier);
                log.debug("Solr query: " + ERXStringUtilities.urlDecode(solrQuery.toString()));
                log.debug("Solr response time: " + queryResponse.getElapsedTime() + "ms");
            }
            
            if (solrFetchSpecification != null) {
                ERXSolrFetchSpecification.Result result = ERXSolrFetchSpecification.Result.newResult(queryResponse,solrFetchSpecification);
                solrFetchSpecification.setResult(result); //FIXME
            }
            
            boolean isQualifierMoreRestrictiveThanSolrQuery = false;
            for (SolrDocument solrDoc : queryResponse.getResults()) {
                NSMutableDictionary<String, Object> row = new NSMutableDictionary<String, Object>();
                for (EOAttribute attribute : attributesToFetch) {
                    if (solrDoc.containsKey(attribute.name())) {
                        Object value = solrDoc.getFieldValue(attribute.name());
                        
                        if (value == null) {
                            value = NSKeyValueCoding.NullValue;
                        }
                        else if (value instanceof List) {
                            value = new NSArray((List)value);
                        }
                        else if (value instanceof Map) {
                            value = new NSDictionary((Map)value);
                        }
                        
                        row.takeValueForKey(value, attribute.name());
                    }
                }
                
                if (qualifier != null && !qualifier.evaluateWithObject(row)) {  // Performance impact?
                    isQualifierMoreRestrictiveThanSolrQuery = true;
                }
                else {
                    _fetchedRows.addObject(row);
                }
            }
            
            if (isQualifierMoreRestrictiveThanSolrQuery) {
                log.warn("EOQualifier is more restrictive than generated Solr query: " + ERXStringUtilities.urlDecode(solrQuery.toString()));
            }
        }
        catch (EOGeneralAdaptorException e) {
            throw e;
        }
        catch (Throwable e) {
            e.printStackTrace();
            throw new EOGeneralAdaptorException("Failed to fetch '" + entity.name() + "' with fetch specification '" + fetchSpecification + "': " + e.getMessage());
        }
    }
    
    public static Object convertValue(String value, EOAttribute attribute) {
        if (attribute != null) {
            try {
                if (attribute.valueType() != null) {
                    char valueType = attribute.valueType().charAt(0);
                    switch (valueType) {
                        case 'i':
                            return Integer.valueOf(value);
                        case 'b':
                            return BigInteger.valueOf(Long.valueOf(value));
                        case 'l':
                            return Long.valueOf(value);
                        case 'd':
                            return Double.valueOf(value);
                        case 'B':
                            return BigDecimal.valueOf(Double.valueOf(value));
                    }
                }
                if (attribute.className().contains("NSTimestamp")) {
                    return new NSTimestamp(SimpleDateFormat.getDateInstance().parse(value));
                } 
                else if (attribute.className().contains("NSData")) {
                    return new NSData((NSData) NSPropertyListSerialization.propertyListFromString(value));
                } 
                else if (attribute.className().contains("NSArray")) {
                    return NSArray.componentsSeparatedByString(value, " ");
                }
                else if (attribute.className().contains("Boolean")) {
                    return Boolean.valueOf(value);
                }
            }
            catch (ParseException e) {
                throw NSForwardException._runtimeExceptionForThrowable(e);
            }
        }
        return value;
    }
    
    protected void _applySortOrderings(SolrQuery solrQuery, NSArray<EOSortOrdering> sortOrderings) {
        for (Enumeration sortOrderingEnum = sortOrderings.objectEnumerator(); sortOrderingEnum.hasMoreElements(); ) {
            EOSortOrdering sortOrdering = (EOSortOrdering)sortOrderingEnum.nextElement();
            SolrQuery.ORDER order;
            NSSelector sortSelector = sortOrdering.selector();
            if (sortSelector == EOSortOrdering.CompareAscending || sortSelector == EOSortOrdering.CompareCaseInsensitiveAscending) {
                order = SolrQuery.ORDER.asc;
            }
            else if (sortSelector == EOSortOrdering.CompareDescending || sortSelector == EOSortOrdering.CompareCaseInsensitiveDescending) {
                order = SolrQuery.ORDER.desc;
            }
            else {
                throw new IllegalArgumentException("Unknown sort ordering selector: " + sortSelector);
            }
            solrQuery.addSortField(sortOrdering.key(), order);
        }
    }
}
