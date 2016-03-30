package vars.queryfx.ui.sdkfx;

import com.guigarage.sdk.container.WorkbenchView;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigObject;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.queryfx.QueryService;
import vars.queryfx.StateLookup;
import vars.queryfx.ui.AbstractValuePanel;
import vars.queryfx.ui.ValuePanelFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Brian Schlining
 * @since 2015-07-28T12:39:00
 */
public class AdvancedSearchWorkbenchA extends WorkbenchView {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private ObservableList<AbstractValuePanel> valuePanels = FXCollections.observableArrayList();

    private final QueryService queryService;

    VBox root = new VBox();

    public AdvancedSearchWorkbenchA(QueryService queryService) {
        this.queryService = queryService;
        initialize();
        ScrollPane scrollPane = new javafx.scene.control.ScrollPane();
        scrollPane.setContent(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        setCenterNode(scrollPane);
    }

    public void initialize() {
        queryService.getAnnotationViewMetadata().thenAccept(metadata -> {
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
                root.getChildren().setAll(groupPanels());
                configureDefaultReturns();
            });
        });

    }

    public List<AbstractValuePanel> getValuePanels() {
        return new ArrayList<>(valuePanels);
    }

    private List<Node> groupPanels() {
        List<Node> panels = new ArrayList<>();

        Config config = StateLookup.getConfig();
        ConfigObject groups = config.getObject("vars.query.column.groups");
        Config groupsConfig = groups.toConfig();

        List<AbstractValuePanel> vps = new ArrayList<>(valuePanels);
        List<AbstractValuePanel> used = new ArrayList<>();

        Set<String> groupNames = groups.keySet();
        for (String name : groupNames) {
            VBox vbox = new VBox();
            vbox.setStyle("-fx-border-color: black");
            List<String> columns = groupsConfig.getStringList(name);
            List<AbstractValuePanel> matchingVps = vps.stream()
                    .filter(vp -> columns.contains(vp.getValueName()))
                    .sorted((vp1, vp2) ->
                            vp1.getValueName().toUpperCase().compareTo(vp2.getValueName().toUpperCase()))
                    .collect(Collectors.toList());
            vbox.getChildren().setAll(matchingVps);
            vbox.getChildren().add(0, new Label(name));
            panels.add(vbox);
            used.addAll(matchingVps);
        }

        vps.removeAll(used);
        if (!vps.isEmpty()) {
            VBox vbox = new VBox();
            vbox.getChildren().setAll(vps);
            panels.add(vbox);
        }

        return panels;

    }

    private void configureDefaultReturns() {
        Config config = StateLookup.getConfig();
        List<String> defaultReturnNames = config.getStringList("vars.query.column.default.returns");
        for (AbstractValuePanel valuePanel : valuePanels) {
            if (defaultReturnNames.contains(valuePanel.getValueName())) {
                valuePanel.setReturned(true);
            }
        }
    }


}
