package vars.query.ui.db.preparedstatement;

import vars.query.ui.*;

/**
 * @author Brian Schlining
 * @since Nov 9, 2010
 */
public class ValuePanelWrapperFactory {

    public ValuePanelWrapper wrap(BooleanValuePanel valuePanel) {
        return new BooleanValuePanelWrapper(valuePanel);
    }

    public ValuePanelWrapper wrap(JXDateValuePanel valuePanel) {
        return new JXDateValuePanelWrapper(valuePanel);
    }

    public ValuePanelWrapper wrap(NumberValuePanel valuePanel) {
        return new NumberValuePanelWrapper(valuePanel);
    }

    public ValuePanelWrapper wrap(StringLikeValuePanel valuePanel) {
        return new StringLikeValuePanelWrapper(valuePanel);
    }

    public ValuePanelWrapper wrap(StringValuePanel valuePanel) {
        return new StringValuePanelWrapper(valuePanel);
    }

    public ValuePanelWrapper wrap(AdvancedStringValuePanel valuePanel) {
        return new AdvancedStringValuePanelWrapper(valuePanel);
    }

    public ValuePanelWrapper wrap(ValuePanel valuePanel) {
        if (valuePanel instanceof JXDateValuePanel) {
            return wrap((JXDateValuePanel) valuePanel);
        }
        else if (valuePanel instanceof NumberValuePanel) {
            return wrap((NumberValuePanel) valuePanel);
        }
        else if (valuePanel instanceof StringLikeValuePanel) {
            return wrap((StringLikeValuePanel) valuePanel);
        }
        else if (valuePanel instanceof StringValuePanel) {
            return wrap((StringValuePanel) valuePanel);
        }
        else if (valuePanel instanceof  AdvancedStringValuePanel) {
            return wrap((AdvancedStringValuePanel) valuePanel);
        }
        else {
            return wrap((BooleanValuePanel) valuePanel);
        }
    }

}
