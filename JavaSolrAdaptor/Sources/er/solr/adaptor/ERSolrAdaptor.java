package er.solr.adaptor;

import com.webobjects.eoaccess.EOAdaptor;
import com.webobjects.eoaccess.EOAdaptorContext;
import com.webobjects.eoaccess.EOModel;
import com.webobjects.eoaccess.EOSQLExpressionFactory;
import com.webobjects.foundation.NSDictionary;

public class ERSolrAdaptor extends EOAdaptor {

    public ERSolrAdaptor(String name) {
        super(name);
    }
    
    @Override
    public void assertConnectionDictionaryIsValid() {
        // DO NOTHING ON PURPOSE
    }
    
    @Override
    public void setConnectionDictionary(NSDictionary dictionary) {
        super.setConnectionDictionary((dictionary == null) ? new NSDictionary() : dictionary);
    }

    @Override
    public Class defaultExpressionClass() {
        return ERSolrExpression.class; // Should I throw? I don't really subclass EOSQLExpression
    }

    @Override
    public EOSQLExpressionFactory expressionFactory() {
        throw new UnsupportedOperationException("ERSolrAdaptor.expressionFactory");
    }

    @Override
    public com.webobjects.eoaccess.EOSchemaGeneration synchronizationFactory() {
        throw new UnsupportedOperationException("ERSolrAdaptor.synchronizationFactory");
    }

    @Override
    public com.webobjects.eoaccess.EOSynchronizationFactory schemaSynchronizationFactory() {
        throw new UnsupportedOperationException("ERSolrAdaptor.schemaSynchronizationFactory");
    }

    @Override
    public EOAdaptorContext createAdaptorContext() {
        return new ERSolrAdaptorContext(this);
    }

    @Override
    public boolean isValidQualifierType(String typeName, EOModel model) {
        //TODO
        return true;
    }

}
