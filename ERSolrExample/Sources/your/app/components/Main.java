package your.app.components;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;

import er.extensions.appserver.ERXDisplayGroup;
import er.extensions.foundation.ERXStringUtilities;
import er.solr.ERXSolrFetchSpecification;
import er.solr.SolrFacet;
import er.solr.SolrFacet.FacetItem;
import er.solr.SolrFacet.Sort;
import er.solr.example.eo.Inventory;

public class Main extends BaseComponent {
	
    public Inventory _inventoryItem;
    public SolrFacet _facet;
    public FacetItem _facetItem;
    public int _rowIndex;
    public String _featureItem;
    
    private ERXDisplayGroup<Inventory> _displayGroup;
    private ERXSolrFetchSpecification<Inventory> _fetchSpecification;
    
    public Main(WOContext context) {
		super(context);
	}
	
    public ERXDisplayGroup<Inventory> displayGroup() {
        if (_displayGroup == null) {
            _displayGroup = new ERXDisplayGroup<Inventory>();
        }
        _displayGroup.setObjectArray(fetchSpecification().result().objects());
        return _displayGroup;
    }
    
    @Override
	public void appendToResponse(WOResponse response, WOContext context) {
	    super.appendToResponse(response, context);
	}
	
	public String rowClass() {
	    return (_rowIndex % 2) == 0 ? "" : "alternate";
	}
	
	public WOActionResults selectBatch() {
        return null;
    }
	
	public NSArray facets() {
	    return fetchSpecification().facets();
	}
	
	public String facetDisplayValue() {
	    return ERXStringUtilities.displayNameForKey(_facet.key());
	}
	
	public NSArray<FacetItem> facetItems() {
	    return fetchSpecification().result().facetItems(_facet.key());
	}
	
	public boolean isFacetItemSelected() {
	    return _facet.isFacetItemSelected(_facetItem);
	}
	
	public void setIsFacetItemSelected(boolean isFacetItemSelected) {
	    if (isFacetItemSelected) {
            _facet.selectItem(_facetItem);
        }
        else {
            _facet.deselectItem(_facetItem);
        }
	}
	
	public ERXSolrFetchSpecification<Inventory> fetchSpecification() {
	    if (_fetchSpecification == null) {
	        _fetchSpecification = new ERXSolrFetchSpecification<Inventory>(Inventory.ENTITY_NAME);
	        _fetchSpecification.setBatchSize(Integer.valueOf(5));
	        
	        _fetchSpecification.setSortOrderings(Inventory.PRICE.ascs());
	        
	        // Facets
	        SolrFacet priceQueryFacet = SolrFacet.newSolrFacet(Inventory.PRICE_KEY);
	        priceQueryFacet.addQualifierForKey(Inventory.PRICE.lessThanOrEqualTo(Double.valueOf(100)), "Low Price");
	        priceQueryFacet.addQualifierForKey(Inventory.PRICE.greaterThan(Double.valueOf(100)), "High Price");
	        _fetchSpecification.addFacet(priceQueryFacet);
	        
	        SolrFacet weightQueryFacet = SolrFacet.newSolrFacet(Inventory.WEIGHT_KEY);
	        weightQueryFacet.addQualifierForKey(Inventory.WEIGHT.lessThanOrEqualTo(Double.valueOf(50)), "50 lbs or under");
	        weightQueryFacet.addQualifierForKey(Inventory.WEIGHT.greaterThan(Double.valueOf(50)), "Over 50 lbs");
            _fetchSpecification.addFacet(weightQueryFacet);
	        
            SolrFacet stockFacet = SolrFacet.newSolrFacet(Inventory.IN_STOCK_KEY);
            stockFacet.setSort(Sort.Boolean);
            _fetchSpecification.addFacet(stockFacet);
            
            SolrFacet popularityFacet = SolrFacet.newSolrFacet(Inventory.POPULARITY_KEY);
            popularityFacet.setSort(Sort.Numeric);
            _fetchSpecification.addFacet(popularityFacet);
	    }
	    return _fetchSpecification;
	}
	
    public Integer maxNumberOfObjects() {
        return Integer.valueOf(fetchSpecification().result().totalCount().intValue());
    }
    
}
