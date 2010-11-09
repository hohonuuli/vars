package vars.query.ui.jdbc;

import vars.query.ui.ValuePanel;

/**
 * @author Brian Schlining
 * @since Nov 9, 2010
 */
public abstract class AbstractValuePanelWrapper implements ValuePanelWrapper {

    private final ValuePanel valuePanel;

    public AbstractValuePanelWrapper(ValuePanel valuePanel) {
        this.valuePanel = valuePanel;
    }

    public boolean isConstrained() {
        return valuePanel.isConstrained();
    }

    public ValuePanel getValuePanel() {
        return valuePanel;
    }
}
