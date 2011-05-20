package your.app.components;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eoaccess.EODatabaseDataSource;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;

import er.extensions.batching.ERXBatchingDisplayGroup;
import er.extensions.components.ERXComponent;
import er.extensions.eof.ERXEC;
import er.solr.example.eo.Inventory;

public class Main extends ERXComponent {
	
    public int index;
    public Inventory _inventoryItem;
    
    private ERXBatchingDisplayGroup<Inventory> _displayGroup;
    
    public Main(WOContext context) {
		super(context);
	}
	
    public ERXBatchingDisplayGroup<Inventory> displayGroup() {
        if (_displayGroup == null) {
            EOEditingContext ec = ERXEC.newEditingContext();
            EODatabaseDataSource dataSource = new EODatabaseDataSource(ec, Inventory.ENTITY_NAME);
            _displayGroup = new ERXBatchingDisplayGroup<Inventory>();
            _displayGroup.setDataSource(dataSource);
            _displayGroup.setNumberOfObjectsPerBatch(5);
            _displayGroup.fetch();
        }
        return _displayGroup;
    }
    
    @Override
	public void appendToResponse(WOResponse response, WOContext context) {
	    EOQualifier qualifier = null;
        
        //qualifier = Inventory.NAME.contains("iPod");
        
	    
        super.appendToResponse(response, context);
	}
	
	public String rowClass() {
	    return (index % 2) == 0 ? "" : "alternate";
	}
}
