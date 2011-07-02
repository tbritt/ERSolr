package er.solr.adaptor;

import java.util.Enumeration;

import com.webobjects.eoaccess.EOAttribute;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOSQLExpression;
import com.webobjects.eocontrol.EOAndQualifier;
import com.webobjects.eocontrol.EOKeyComparisonQualifier;
import com.webobjects.eocontrol.EOKeyValueQualifier;
import com.webobjects.eocontrol.EONotQualifier;
import com.webobjects.eocontrol.EOOrQualifier;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSSelector;

import er.extensions.foundation.ERXStringUtilities;

public class ERSolrExpression extends EOSQLExpression {
    
    public ERSolrExpression(EOEntity entity) {
        super(entity);
    }

    public static final String SOLR_EMPTY_QUALIFIER = "*:*";
    public static final String PARAMETER_EXCLUSION = "ex";
    public static final String PARAMETER_TAG = "tag";
    public static final String PARAMETER_KEY = "key";
    
    public static ERSolrExpression newERSolrExpression(EOEntity entity) {
        ERSolrExpression solrExpression = new ERSolrExpression(entity);
        solrExpression._entity = entity;
        return solrExpression;
    }
    
    public static StringBuilder escapeAndAppend(Object value, StringBuilder sb) {
        sb.append("\"");
        sb.append(ERXStringUtilities.escape(new char[] { '\"' }, '\\', String.valueOf(value)));
        sb.append("\"");
        return sb;
    }
    
    public static StringBuilder appendLocalParams(StringBuilder sb, NSDictionary<String, String> attributes) {
        if (attributes != null) {    
            sb.append("{!");
            for (Enumeration e = attributes.allKeys().objectEnumerator(); e.hasMoreElements();) {
                String attributeKey = (String)e.nextElement();
                String attributeValue = (String)attributes.valueForKey(attributeKey);
                sb.append(attributeKey).append("=");
                escapeAndAppend(attributeValue, sb);
                if (e.hasMoreElements()) {
                    sb.append(" ");
                }
            }
            sb.append("}");
        }
        return sb;
    }
    
    public String solrStringForQualifier(EOQualifier qualifier) {
        if (qualifier == null) {
            return SOLR_EMPTY_QUALIFIER;
        }
        if (qualifier instanceof EOKeyValueQualifier) {
            return solrStringForKeyValueQualifier((EOKeyValueQualifier)qualifier);
        }
        else if (qualifier instanceof EOAndQualifier) {
            return solrStringForArrayOfQualifiers(((EOAndQualifier) qualifier).qualifiers(), true);
        }
        else if (qualifier instanceof EOOrQualifier) {
            return solrStringForArrayOfQualifiers(((EOOrQualifier) qualifier).qualifiers(), false);
        }
        else if (qualifier instanceof EONotQualifier) {
            return solrStringForNegatedQualifier(qualifier);
        }
        else if (qualifier instanceof EOKeyComparisonQualifier) {
            return solrStringForKeyComparisonQualifier((EOKeyComparisonQualifier)qualifier);
        }
        
        return null;
    }
    
    protected String solrStringForKeyValueQualifier(EOKeyValueQualifier qualifier) {
        String solrString = null;
        String key = qualifier.key();
        String solrKey = null;
        Object value = qualifier.value();
        String solrValue = null;
        NSSelector selector = qualifier.selector();
        String solrSelector = null;
        
        
        // TODO: Handle key paths/relationships?
        EOAttribute attribute = _entity.attributeNamed(key);
        if (attribute != null) solrKey = attribute.columnName();
        if (solrKey == null) solrKey = key;
        
        solrValue = solrStringForValue(value, key);
        solrSelector = solrStringForSelector(selector, solrValue);
        
        // TODO: need to put solr in case insensitive mode
        if (selector.equals(EOQualifier.QualifierOperatorCaseInsensitiveLike)) {
            solrValue = solrValue.toLowerCase();
        } 
        
        boolean isContains = (selector.equals(EOQualifier.QualifierOperatorContains));
        if (isContains) {
            solrValue = new StringBuilder("*").append(solrValue).append("*").toString();
        }
        
        if (ERXStringUtilities.containsAnyCharacter(solrValue, " ")) {
            solrValue = new StringBuilder("\"").append(solrValue).append("\"").toString();
        }
        
        boolean isRange = (selector.equals(EOQualifier.QualifierOperatorGreaterThan) || selector.equals(EOQualifier.QualifierOperatorGreaterThanOrEqualTo) || selector.equals(EOQualifier.QualifierOperatorLessThan) || selector.equals(EOQualifier.QualifierOperatorLessThanOrEqualTo));
        boolean isNot = EOQualifier.QualifierOperatorNotEqual.equals(selector);
        
        if (isRange) {
            solrString = new StringBuilder(solrKey).append(":").append(solrSelector).toString();
        }
        else if (isNot) {
            solrString = new StringBuilder(solrSelector).append(" ").append(solrKey).append(":").append(solrValue).toString();
        }
        else {
            solrString = new StringBuilder(solrKey).append(":").append(solrSelector).append(solrValue).toString();
        }
        
        
        return solrString;
    }
    
    protected String solrStringForArrayOfQualifiers(NSArray<EOQualifier> qualifiers, boolean isConjoined) {
        StringBuilder solrStringBuilder = null;
        String operator = isConjoined ? " AND " : " OR ";
        boolean isAddingParens = false;
        
        for (EOQualifier qualifier : qualifiers) {
            String solrString = solrStringForQualifier(qualifier);
            if (solrString != null) {
                if (solrStringBuilder != null) {
                    solrStringBuilder.append(operator);
                    solrStringBuilder.append(solrString);
                    isAddingParens = true;
                } 
                else {
                    solrStringBuilder = new StringBuilder(solrString);
                }
            }
        }
        
        if (isAddingParens) {
            solrStringBuilder.insert(0, '(');
            solrStringBuilder.append(')');
        }
        
        return solrStringBuilder.toString();
    }
    
    protected String solrStringForNegatedQualifier(EOQualifier qualifier) {
        // TODO
        return null;
    }
    
    protected String solrStringForKeyComparisonQualifier(EOKeyComparisonQualifier qualifier) {
        // TODO
        return null;
    }
    
    protected String solrStringForValue(Object value, String key) {
        // TODO
        return value.toString();
    }
    
    protected String solrStringForSelector(NSSelector selector, String solrValue) {
        if (selector.equals(EOQualifier.QualifierOperatorEqual) || selector.equals(EOQualifier.QualifierOperatorContains) || selector.equals(EOQualifier.QualifierOperatorLike) || selector.equals(EOQualifier.QualifierOperatorCaseInsensitiveLike)) {
            return "";
        } 
        else if (selector.equals(EOQualifier.QualifierOperatorNotEqual)) {
            return "NOT";
        } 
        else if (selector.equals(EOQualifier.QualifierOperatorLessThan)) {
            return new StringBuilder("{* TO ").append(solrValue).append("}").toString();
        } 
        else if (selector.equals(EOQualifier.QualifierOperatorGreaterThan)) {
            return new StringBuilder("{").append(solrValue).append(" TO *}").toString();
        } 
        else if (selector.equals(EOQualifier.QualifierOperatorLessThanOrEqualTo)) {
            return new StringBuilder("[* TO ").append(solrValue).append("]").toString();
        } 
        else if (selector.equals(EOQualifier.QualifierOperatorGreaterThanOrEqualTo)) {
            return new StringBuilder("[").append(solrValue).append(" TO *]").toString();
        }
        
        throw new IllegalStateException("solrStringForSelector:  Unknown operator: " + selector);
    }

    @Override
    public NSMutableDictionary<String, Object> bindVariableDictionaryForAttribute(EOAttribute arg0, Object arg1) {
        return new NSMutableDictionary<String, Object>();
    }
    
}
