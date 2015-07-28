package vars.queryfx.ui.sdkfx;

import com.guigarage.sdk.container.WorkbenchView;
import com.guigarage.sdk.form.EditorFormRow;
import com.guigarage.sdk.form.EditorType;
import com.guigarage.sdk.form.FormLayout;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import vars.queryfx.beans.ResultsCustomization;

/**
 * @author Brian Schlining
 * @since 2015-07-27T17:21:00
 */
public class CustomizeResultsWorkbench extends WorkbenchView {

    private EditorFormRow<CheckBox> relatedAssociationsCheckbox;
    private EditorFormRow<CheckBox> concurrentObservationsCheckbox;
    private EditorFormRow<CheckBox> conceptHierarchyCheckbox;
    private EditorFormRow<CheckBox> basicPhylogenyCheckbox;
    private EditorFormRow<CheckBox> detailedPhylogenyCheckbox;
    private EditorFormRow<CheckBox> categorizeAssociationsCheckbox;


    public CustomizeResultsWorkbench() {

        FormLayout formLayout = new FormLayout();
        formLayout.addHeader("Customize Results");

        relatedAssociationsCheckbox = new EditorFormRow<>("Return related associations",new CheckBox());
        concurrentObservationsCheckbox = new EditorFormRow<>("Return concurrent observations", new CheckBox());
        conceptHierarchyCheckbox = new EditorFormRow<>("Return concept hierarchy", new CheckBox());
        basicPhylogenyCheckbox = new EditorFormRow<>("Return basic organism phylogeny", new CheckBox());
        detailedPhylogenyCheckbox = new EditorFormRow<>("Return detailed organism phylogeny", new CheckBox());
        categorizeAssociationsCheckbox = new EditorFormRow<>("Categorize associations into columns", new CheckBox());

        formLayout.add(relatedAssociationsCheckbox);
        formLayout.add(concurrentObservationsCheckbox);
        formLayout.add(conceptHierarchyCheckbox);
        formLayout.add(basicPhylogenyCheckbox);
        formLayout.add(detailedPhylogenyCheckbox);
        formLayout.add(categorizeAssociationsCheckbox);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(formLayout);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        setCenterNode(scrollPane);
    }

    public ResultsCustomization getResultsCustomization() {
        return new ResultsCustomization(basicPhylogenyCheckbox.getEditor().isSelected(),
                concurrentObservationsCheckbox.getEditor().isSelected(),
                conceptHierarchyCheckbox.getEditor().isSelected(),
                detailedPhylogenyCheckbox.getEditor().isSelected(),
                categorizeAssociationsCheckbox.getEditor().isSelected());
    }
}
