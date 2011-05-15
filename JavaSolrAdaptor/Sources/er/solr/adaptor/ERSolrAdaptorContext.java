package er.solr.adaptor;

import com.webobjects.eoaccess.EOAdaptor;
import com.webobjects.eoaccess.EOAdaptorChannel;
import com.webobjects.eoaccess.EOAdaptorContext;

public class ERSolrAdaptorContext extends EOAdaptorContext {

    private boolean _hasTransaction;

    public ERSolrAdaptorContext(EOAdaptor adaptor) {
        super(adaptor);
    }

    @Override
    public void beginTransaction() {
        if (!_hasTransaction) {
            _hasTransaction = true;
            transactionDidBegin();
        }
    }

    @Override
    public void commitTransaction() {
        if (_hasTransaction) {
            _hasTransaction = false;
            transactionDidCommit();
        }
    }

    @Override
    public EOAdaptorChannel createAdaptorChannel() {
        return new ERSolrAdaptorChannel(this);
    }

    @Override
    public void handleDroppedConnection() {
        // TODO
    }

    @Override
    public void rollbackTransaction() {
        if (_hasTransaction) {
            _hasTransaction = false;
            transactionDidRollback();
        }
    }
}
