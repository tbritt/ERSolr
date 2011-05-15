package your.app.components;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;

import er.extensions.components.ERXComponent;
import er.extensions.eof.ERXEC;
import er.solr.example.eo.Inventory;

public class Main extends ERXComponent {
	
    public int index;
    public NSArray<Inventory> inventoryItems;
    public Inventory inventoryItem;
    
    public Main(WOContext context) {
		super(context);
	}
	
	public void appendToResponse(WOResponse response, WOContext context) {
	    EOEditingContext ec = ERXEC.newEditingContext();
        EOQualifier qualifier = null;
        
        //qualifier = Film.ADAM_ID.isNot(9999).and(Film.ADAM_ID.between(81750000, 81759999)).or(Film.ADAM_ID.is(3333)).and(Film.TITLE.like("*Bob*"));
	    //qualifier = Film.TITLE.contains("Bob");
        //qualifier = Film.ADAM_ID.between(81750000, 81759999);
        //qualifier = Film.ADAM_ID.lessThanOrEqualTo(81759999);
        //qualifier = Film.ADAM_ID.lessThan(81759999);
        //qualifier = Film.ADAM_ID.greaterThanOrEqualTo(81759999);
        
        inventoryItems = Inventory.fetchInventories(ec, qualifier, null);
        
        //inventoryItems = Inventory.fetchAllInventories(ec, null); 

	    super.appendToResponse(response, context);
	}
	
	public String rowClass() {
	    return (index % 2) == 0 ? "" : "alternate";
	}
}
