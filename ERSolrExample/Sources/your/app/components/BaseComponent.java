package your.app.components;

import com.webobjects.appserver.WOContext;
import com.webobjects.eocontrol.EOEditingContext;

import er.extensions.components.ERXComponent;
import er.extensions.eof.ERXEC;

public class BaseComponent extends ERXComponent {

    private EOEditingContext _editingContext;
    
    
    public BaseComponent(WOContext context) {
        super(context);
    }
    
    public EOEditingContext editingContext() {
        if (_editingContext == null) _editingContext = ERXEC.newEditingContext();
        return _editingContext;
    }
    
    public EOEditingContext pageEditingContext() {
        return ((BaseComponent)context().page()).editingContext();
    }
    
}
