package your.app.components;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;

import er.extensions.appserver.ERXDisplayGroup;
import er.extensions.appserver.ERXRequest;
import er.solr.ERXSolrFetchSpecification;
import er.solr.example.eo.Inventory;

public class Main extends BaseComponent {
	
    public Inventory _inventoryItem;
    public int _rowIndex;
    
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
	
	public ERXSolrFetchSpecification<Inventory> fetchSpecification() {
	    if (_fetchSpecification == null) {
	        _fetchSpecification = new ERXSolrFetchSpecification<Inventory>(Inventory.ENTITY_NAME);
	        _fetchSpecification.setBatchSize(Integer.valueOf(5));
	    }
	    return _fetchSpecification;
	}
	
    public Integer maxNumberOfObjects() {
        return Integer.valueOf(fetchSpecification().result().totalCount().intValue());
    }
    
}
