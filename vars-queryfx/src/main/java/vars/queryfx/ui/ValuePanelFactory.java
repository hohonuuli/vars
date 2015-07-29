package vars.queryfx.ui;

import vars.queryfx.QueryService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;

/**
 * @author Brian Schlining
 * @since 2015-07-28T12:55:00
 */
public class ValuePanelFactory {

    private final QueryService queryService;

    public ValuePanelFactory(QueryService queryService) {
        this.queryService = queryService;
    }

    public AbstractValuePanel getValuePanel(final String name, final String type) {

        AbstractValuePanel valuePanel = null;
        if (type.equals("java.lang.String")) {
            valuePanel = newStringValuePanel(name);
        }
        else if (type.equals("java.sql.Timestamp")) {
            valuePanel = newDateValuePanel(name);
        }
        else if (type.equals("java.lang.Boolean")) {
            valuePanel = newBooleanValuePanel(name);
        }
        else {
            valuePanel = newNumberValuePanel(name);
        }

        return valuePanel;

    }

    private StringValuePanel newStringValuePanel(String name) {
        StringValuePanel valuePanel = new StringValuePanel(name);
        valuePanel.setOnScan(() -> {
            queryService.getAnnotationViewsUniqueValuesForColumn(name).thenAccept(c -> {
                Collection<String> values = (Collection<String>) c;
                valuePanel.setValues(values);
            });
        });
        return valuePanel;
    }

    private NumberValuePanel newNumberValuePanel(String name) {
        NumberValuePanel valuePanel = new NumberValuePanel(name);
        valuePanel.setOnScan(() -> {
            queryService.getAnnotationViewsMinAndMaxForColumn(name).thenAccept( minMax -> {
                if (minMax != null && minMax.size() == 2) {
                    valuePanel.setMinValue(minMax.get(0));
                    valuePanel.setMaxValue(minMax.get(1));
                }
            });
        });
        return valuePanel;
    }

    private DateValuePanel newDateValuePanel(String name) {
        DateValuePanel valuePanel = new DateValuePanel(name);
        valuePanel.setOnScan(() -> {
            queryService.getAnnotationViewsMinAndMaxDatesforColumn(name).thenAccept( minMax -> {
                if (minMax != null && minMax.size() == 2) {
                    LocalDateTime start = LocalDateTime.ofInstant(minMax.get(0).toInstant(), ZoneId.of("UTC"));
                    LocalDateTime end = LocalDateTime.ofInstant(minMax.get(1).toInstant(), ZoneId.of("UTC"));
                    valuePanel.setStartDate(start);
                    valuePanel.setEndDate(end);
                }
            });
        });
        return valuePanel;
    }

    private BooleanValuePanel newBooleanValuePanel(String name) {
        BooleanValuePanel valuePanel = new BooleanValuePanel(name);
        return valuePanel;
    }

}
