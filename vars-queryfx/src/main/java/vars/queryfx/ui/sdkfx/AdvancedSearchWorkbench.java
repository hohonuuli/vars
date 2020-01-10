package vars.queryfx.ui.sdkfx;

import com.guigarage.sdk.container.WorkbenchView;
import com.guigarage.sdk.form.EditorFormRow;
import com.guigarage.sdk.form.FormLayout;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigObject;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ScrollPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.queryfx.AsyncQueryService;
import vars.queryfx.StateLookup;
import vars.shared.rx.RXEventBus;
import vars.queryfx.beans.QueryParams;
import vars.shared.rx.messages.FatalExceptionMsg;
import vars.queryfx.ui.AbstractValuePanel;
import vars.queryfx.ui.ValuePanelFactory;
import vars.queryfx.ui.db.IConstraint;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Brian Schlining
 * @since 2015-07-28T12:39:00
 */
public class AdvancedSearchWorkbench extends WorkbenchView {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private ObservableList<AbstractValuePanel> valuePanels = FXCollections.observableArrayList();

    private final AsyncQueryService queryService;
    private final RXEventBus eventBus;
    private final FormLayout formLayout;


    public AdvancedSearchWorkbench(AsyncQueryService queryService, RXEventBus eventBus) {
        this.queryService = queryService;
        this.eventBus = eventBus;
        this.formLayout = new FormLayout();
        initialize();

        ScrollPane scrollPane = new javafx.scene.control.ScrollPane();
        scrollPane.setContent(formLayout);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        setCenterNode(scrollPane);
    }

    public void initialize() {
        queryService.getAnnotationViewMetadata().handle((map, ex) -> {
            if (map != null) {
                return map;
            }
            else {
                eventBus.send(new FatalExceptionMsg("Could not read annotations view in database", ex));
                return new HashMap<String, String>();
            }
        }).thenAccept(metadata -> {
            Platform.runLater(() -> {
                ValuePanelFactory factory = new ValuePanelFactory(queryService);
                for (Map.Entry<String, String> entry : metadata.entrySet()) {
                    String columnName = entry.getKey();
                    if (!columnName.toUpperCase().contains("ID_FK")) {
                        AbstractValuePanel valuePanel = null;
                        try {
                            valuePanel = factory.getValuePanel(entry.getKey(), entry.getValue());
                        }
                        catch (RuntimeException e) {
                            log.warn("Failed to create a ValuePanel for " + columnName, e);
                        }

                        if (valuePanel != null) {
                            valuePanels.addAll(valuePanel);
                        }
                    }
                }
                groupPanels();
                configureDefaultReturns();
            });
        });

    }

    public List<AbstractValuePanel> getValuePanels() {
        return new ArrayList<>(valuePanels);
    }

    private void groupPanels() {

        Config config = StateLookup.getConfig();
        ConfigObject groups = config.getObject("vars.query.column.groups");
        Config groupsConfig = groups.toConfig();

        List<AbstractValuePanel> vps = new ArrayList<>(valuePanels);
        List<AbstractValuePanel> used = new ArrayList<>();

        Set<String> groupNames = groups.keySet();
        for (String name : groupNames) {
            formLayout.addHeader(name);
            List<String> columns = groupsConfig.getStringList(name)
                    .stream()
                    .map(String::toUpperCase)
                    .collect(Collectors.toList());
            List<AbstractValuePanel> matchingVps = vps.stream()
                    .filter(vp -> columns.contains(vp.getValueName().toUpperCase()))
                    .sorted(Comparator.comparing(vp1 -> vp1.getValueName().toUpperCase()))
                    .collect(Collectors.toList());

            matchingVps.stream().forEach(vp ->
                        formLayout.add(new EditorFormRow<>(vp.getTitle(), vp)));

            used.addAll(matchingVps);
        }

        vps.removeAll(used);
        if (!vps.isEmpty()) {
            formLayout.addHeader("Other");
            vps.stream().forEach(vp -> formLayout.add(new EditorFormRow<>(vp.getTitle(), vp)));
        }

    }

    private void configureDefaultReturns() {
        Config config = StateLookup.getConfig();
        List<String> defaultReturnNames = config.getStringList("vars.query.column.default.returns")
                .stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList());
        for (AbstractValuePanel valuePanel : valuePanels) {
            if (defaultReturnNames.contains(valuePanel.getValueName().toUpperCase())) {
                valuePanel.setReturned(true);
            }
        }
    }

    public QueryParams getQueryParams() {
        List<AbstractValuePanel> vps = getValuePanels();
        List<String> returnedColumns = vps.stream()
                .filter(AbstractValuePanel::isReturned)
                .map(AbstractValuePanel::getValueName)
                .collect(Collectors.toList());
        List<IConstraint> constraints = vps.stream()
                .filter(AbstractValuePanel::isConstrained)
                .map(AbstractValuePanel::getConstraint)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        return new QueryParams(returnedColumns, constraints);
    }


}
