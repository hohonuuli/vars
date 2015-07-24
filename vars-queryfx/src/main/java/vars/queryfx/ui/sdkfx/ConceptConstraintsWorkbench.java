package vars.queryfx.ui.sdkfx;

import com.guigarage.sdk.action.Action;
import com.guigarage.sdk.container.WorkbenchView;
import com.guigarage.sdk.form.EditorFormRow;
import com.guigarage.sdk.form.FormLayout;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.ILink;
import vars.LinkUtilities;
import vars.queryfx.Lookup;
import vars.queryfx.QueryService;
import vars.queryfx.beans.ConceptSelection;
import vars.shared.javafx.scene.control.AutoCompleteComboBoxListener;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;


/**
 * @author Brian Schlining
 * @since 2015-07-22T14:29:00
 */
public class ConceptConstraintsWorkbench extends WorkbenchView {

    private FormLayout formLayout = new FormLayout();

    private EditorFormRow<ComboBox<String>> conceptRow;
    private EditorFormRow<CheckBox> extendToParentRow = new EditorFormRow<>("parent", new CheckBox());
    private EditorFormRow<CheckBox> extendToSiblingsRow = new EditorFormRow<>("siblings", new CheckBox());
    private EditorFormRow<CheckBox> extendToChildrenRow = new EditorFormRow<>("children", new CheckBox());
    private EditorFormRow<CheckBox> extendToDescendantsRow = new EditorFormRow<>("descendants", new CheckBox());

    private EditorFormRow<TextField> associationSearchRow;
    private EditorFormRow<ComboBox<ILink>>  associationSelectionRow;
    private ObservableList<ILink> linksForConceptSelection = FXCollections.observableArrayList();
    private FilteredList<ILink> filteredLinks = new FilteredList<>(linksForConceptSelection);

    private EditorFormRow<TextField> linkNameRow = new EditorFormRow<>("link", new TextField());
    private EditorFormRow<ComboBox<String>> toConceptRow = new EditorFormRow<>("to", new ComboBox<>());
    private EditorFormRow<TextField> linkValueRow = new EditorFormRow<>("value", new TextField());


    private final Executor executor;
    private final QueryService queryService;
    private volatile CompletableFuture<List<ILink>> runningFuture;

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    public ConceptConstraintsWorkbench(QueryService queryService, Executor executor) {
        this.queryService = queryService;
        this.executor = executor;
        linksForConceptSelection.add(Lookup.WILD_CARD_LINK);

        // --- Do Layout

        formLayout.addHeader("", "Search by concept");
        formLayout.add(getConceptRow());
        formLayout.addHeader("", "extend to");
        formLayout.add(extendToParentRow);
        formLayout.add(extendToSiblingsRow);
        formLayout.add(extendToChildrenRow);
        formLayout.add(extendToDescendantsRow);

        formLayout.addSeperator();

        formLayout.addHeader("", "Search by association");
        formLayout.add(getAssociationSearchRow());
        formLayout.add(getAssociationSelectionRow());

        formLayout.addHeader("", "modify association");
        formLayout.add(linkNameRow);
        formLayout.add(toConceptRow);
        formLayout.add(linkValueRow);


        // --- Do event wiring
        ChangeListener<Boolean> cl = (ov, oldVal, newVal) -> setSelectedConceptName(getConceptSelection());

        extendToParentRow.getEditor().selectedProperty().addListener(cl);
        extendToSiblingsRow.getEditor().selectedProperty().addListener(cl);
        extendToChildrenRow.getEditor().selectedProperty().addListener(cl);
        extendToDescendantsRow.getEditor().selectedProperty().addListener(cl);

        new AutoCompleteComboBoxListener<>(toConceptRow.getEditor());

        // --- Wrap in scroll pane
        javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane();
        scrollPane.setContent(formLayout);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        setCenterNode(scrollPane);

        getConceptRow().getEditor().getSelectionModel().selectFirst();

    }

    private EditorFormRow<ComboBox<String>> getConceptRow() {
        if (conceptRow == null) {
            final ComboBox<String> conceptComboBox = new ComboBox<>();
            conceptRow = new EditorFormRow<>("named", conceptComboBox);
            queryService.findAllConceptNamesAsStrings().thenAccept(names -> {
                Platform.runLater(() -> {
                    conceptComboBox.setEditable(false);
                    conceptComboBox.setPromptText("Loading ...");
                    ObservableList<String> obs = FXCollections.observableArrayList(names);

                    // Need to add wild card (i.e. Formerly NIL)
                    obs.add(0, Lookup.WILD_CARD);
                    conceptComboBox.setItems(obs);
                    conceptComboBox.setPromptText("Enter search term (* is wildcard)");
                    conceptComboBox.getSelectionModel().select(0);
                    conceptComboBox.setEditable(true);
                    conceptComboBox.getSelectionModel()
                            .selectedItemProperty()
                            .addListener((ov, oldVal, newVal) ->
                                    setSelectedConceptName(getConceptSelection(newVal)));
                    new AutoCompleteComboBoxListener<>(conceptComboBox);
                });
            });
        }
        return conceptRow;
    }

    private EditorFormRow<TextField> getAssociationSearchRow() {
        if (associationSearchRow == null) {
            TextField textField = new TextField();
            textField.setPromptText("Filter associations ...");
            textField.setOnKeyReleased(event -> {
                String text = textField.getText();
                ComboBox<ILink> comboBox = getAssociationSelectionRow().getEditor();

                if (event.getCode() == KeyCode.DOWN) {
                    comboBox.show();
                }
                else if (event.getCode() == KeyCode.RIGHT
                        || event.getCode() == KeyCode.LEFT
                        || event.isControlDown()
                        || event.getCode() == KeyCode.HOME
                        || event.getCode() == KeyCode.END
                        || event.getCode() == KeyCode.TAB
                        || event.getCode() == KeyCode.UP) {
                    return;
                }
                else if (event.getCode() == KeyCode.ENTER) {
                    comboBox.hide();
                }
                else {
                    comboBox.hide();
                    if (text != null && !text.isEmpty()) {
                        String upperCaseText = text.toUpperCase();
                        filteredLinks.setPredicate(link ->
                                link.getLinkName().toUpperCase().contains(upperCaseText)
                                        || link.getLinkValue().toUpperCase().contains(upperCaseText));
                    }
                    else {
                        filteredLinks.setPredicate(link -> true);
                    }

                    if (!filteredLinks.isEmpty() && !comboBox.isShowing()) {
                        comboBox.show();
                    }
                }

            });

            associationSearchRow = new EditorFormRow<>("filter by", textField);
        }
        return associationSearchRow;
    }

    private EditorFormRow<ComboBox<ILink>> getAssociationSelectionRow() {
        if (associationSelectionRow == null) {
            ComboBox<ILink> comboBox = new ComboBox<>(filteredLinks);
            comboBox.setCellFactory(listView -> new ListCell<ILink>() {
                @Override
                protected void updateItem(ILink item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null) {
                        setText(LinkUtilities.formatAsString(item));
                    }
                }
            });

            // --- When selection changes we update the parts
            comboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                linkNameRow.getEditor().setText(newVal.getLinkName());
                linkValueRow.getEditor().setText(newVal.getLinkValue());
                ComboBox<String> cb = toConceptRow.getEditor();
                cb.setDisable(true);
                cb.getItems().clear();

                // TODO this should first find the matching link template(s) and add the
                // concept used their
                queryService.findDescendantNamesAsStrings(newVal.getToConcept()).thenAccept(list -> {

                    runOnFXThread(() -> {
                        ObservableList<String> items = cb.getItems();
                        items.add(Lookup.WILD_CARD);
                        if (list.isEmpty()) {
                            items.add(newVal.getToConcept());
                        }
                        items.addAll(list);
                        cb.getSelectionModel().select(newVal.getToConcept());
                        cb.setDisable(false);
                    });
                });
            });

            associationSelectionRow = new EditorFormRow<>("using", comboBox);

        }
        return associationSelectionRow;
    }


    private void setSelectedConceptName(ConceptSelection selection) {

        if (selection.getConceptName() == null) {
            return;
        }

        Runnable resetForm = () -> {

            // Reset view of links
            linksForConceptSelection.clear();
            filteredLinks.setPredicate(link -> true);
            linkNameRow.getEditor().setText("");
            toConceptRow.getEditor().getItems().clear();
            linkValueRow.getEditor().setText("");

            // reset association filter
            getAssociationSearchRow().getEditor().setText("");

            // reset association combobox
            EditorFormRow<ComboBox<ILink>> row = getAssociationSelectionRow();
            ComboBox<ILink> editor = row.getEditor();
            editor.hide();
            editor.setDisable(true);
            editor.getEditor().setText("");
            editor.setPromptText("Loading ...");

        };

        runOnFXThread(resetForm);

        // Stop existing searches
        if (runningFuture != null && !runningFuture.isDone()) {
            runningFuture.cancel(true);
        }

        if (selection.getConceptName().equals(Lookup.WILD_CARD)) {
            runningFuture = queryService.findAllLinks();
        }
        else {
            runningFuture = queryService.findConceptNamesAsStrings(selection.getConceptName(),
                    selection.isExtendToChildren(),
                    selection.isExtendToSiblings(),
                    selection.isExtendToChildren(),
                    selection.isExtendToDescendants())
                    .thenCompose(queryService::findLinksByConceptNames);
        }

        runningFuture.thenAccept(links -> {
            runOnFXThread(() -> {
                linksForConceptSelection.add(Lookup.WILD_CARD_LINK);
                linksForConceptSelection.addAll(links);
                EditorFormRow<ComboBox<ILink>> row = getAssociationSelectionRow();
                ComboBox<ILink> editor = row.getEditor();
                editor.setPromptText("");
                editor.setDisable(false);
            });
        });
    }

    //    public void updateWith(ConceptSelection conceptSelection) {
//
//    }
//
    public ConceptSelection getConceptSelection() {
        return getConceptSelection(conceptRow.getEditor().getSelectionModel().getSelectedItem());
    }

    private ConceptSelection getConceptSelection(String name) {
        return new ConceptSelection(name,
                extendToParentRow.getEditor().isSelected(),
                extendToSiblingsRow.getEditor().isSelected(),
                extendToChildrenRow.getEditor().isSelected(),
                extendToDescendantsRow.getEditor().isSelected());
    }

    public FormLayout getFormLayout() {
        return formLayout;
    }

    private void runOnFXThread(Runnable r) {
        if (Platform.isFxApplicationThread()) {
            r.run();
        }
        else {
            Platform.runLater(r);
        }

    }


}

