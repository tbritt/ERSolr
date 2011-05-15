package er.solr;

import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

import er.extensions.eof.ERXKey;

public class SolrFacet {
    
    private Operator _operator;
    private ERXKey _attribute;
    private NSArray<Item> _items;
    private NSMutableArray<Item> _selectedItems;
    private NSArray<EOSortOrdering> _sortOrderings;
    
    public static enum Operator {
        AND,
        OR;
    }
    public static class Item {
        
        private Object _value;
        private Integer _count;
        private EOQualifier _qualifier;
        
    }
}
