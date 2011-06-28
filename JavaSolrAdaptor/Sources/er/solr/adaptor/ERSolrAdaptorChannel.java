package er.solr.adaptor;

import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.params.FacetParams;

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
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;

import er.extensions.foundation.ERXMutableURL;
import er.extensions.foundation.ERXStringUtilities;
import er.solr.ERXSolrFetchSpecification;
import er.solr.SolrFacet;


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
        else {
            // TODO: Turn this back on?
            //throw new IllegalArgumentException("Fetch specification must be of type " + ERXSolrFetchSpecification.class.getName());
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
                        
                        // Arbitrary query facets
                        if (facet.isQuery()) {
                            for (Enumeration qualifierKeyEnumeration = facet.qualifierKeys().objectEnumerator(); qualifierKeyEnumeration.hasMoreElements();) {
                                String qualifierKey = (String)qualifierKeyEnumeration.nextElement();
                                EOQualifier facetQualifier = facet.qualifierForKey(qualifierKey);
                                //TODO: set label to the qual's key:  facet.field={!ex=dt key=mylabel}doctype
                                solrQuery.setParam(FacetParams.FACET_QUERY, solrExpression.solrStringForQualifier(facetQualifier));
                            }
                        }
                        
                        // Field value facets
                        else {
                            solrQuery.setParam(FacetParams.FACET_FIELD, facet.key());  
                        }
                        
                        
                        
                        
                        // Create filter query based on selected facet items.
                        if (facet.selectedItems() != null && facet.selectedItems().count() > 0) {
                            
                        }
                        /*
                        if (isMultiValue() && Operator.And.equals(operator())) {
                            query.addFacetField(key());
                        }
                        else {
                            query.addFacetField(exclusionTag() + key());
                        }
                        */
                        
                        //TODO
                    }
                    
                }
            }
            
            
            System.out.println(" Original qualifier: " + qualifier);
            System.out.println("         Solr query: " + ERXStringUtilities.urlDecode(solrQuery.toString()));
            
            CommonsHttpSolrServer solrServer = new CommonsHttpSolrServer(url.toURL());
            QueryResponse queryResponse = solrServer.query(solrQuery);
            
            if (log.isDebugEnabled()) {
                log.debug("Solr query: " + ERXStringUtilities.urlDecode(solrQuery.toString()));
                log.debug("Solr response time: " + queryResponse.getElapsedTime() + "ms");
            }
            
            if (solrFetchSpecification != null) {
                ERXSolrFetchSpecification.Result result = ERXSolrFetchSpecification.Result.newResult(queryResponse);
                solrFetchSpecification.setResult(result);
                //TODO: facets
                
            }
            
            for (SolrDocument solrDoc : queryResponse.getResults()) {
                NSMutableDictionary<String, Object> row = new NSMutableDictionary<String, Object>();
                for (EOAttribute attribute : attributesToFetch) {
                    if (solrDoc.containsKey(attribute.name())) {
                        Object value = solrDoc.getFieldValue(attribute.name());
                        
                        if (value == null) {
                            value = NSKeyValueCoding.NullValue;
                        }
                        
                        // TODO: Create a real value for multivalue fields, can be list of 1 or n.
                        if (value instanceof List) {
                            value = value.toString();
                        }
                        row.takeValueForKey(value, attribute.name());
                    }
                }
                
                // TODO: Take this out unless I'm planning to support qualifiers that can't be translated to Solr query.
                // if (qualifier == null || qualifier.evaluateWithObject(row)) {
                _fetchedRows.addObject(row);
                // }
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
    
    
}
