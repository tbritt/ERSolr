package er.solr.adaptor;

import com.webobjects.eoaccess.EOAdaptor;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOSQLExpression;
import com.webobjects.eoaccess.EOSQLExpressionFactory;

public class ERSolrExpressionFactory extends EOSQLExpressionFactory {

    public ERSolrExpressionFactory(EOAdaptor adaptor) {
        super(adaptor);
    }
    
    @Override
    public EOSQLExpression createExpression(EOEntity entity) {
        return ERSolrExpression.newERSolrExpression(entity);
    }

}
