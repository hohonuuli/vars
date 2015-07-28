package vars.queryfx.ui.sdkfx;

import com.guigarage.sdk.action.Action;
import com.guigarage.sdk.container.WorkbenchView;
import com.guigarage.sdk.footer.ActionFooter;
import com.guigarage.sdk.list.MediaList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import vars.queryfx.beans.ConceptSelection;
import vars.queryfx.ui.AppIcons;

/**
 * @author Brian Schlining
 * @since 2015-07-20T15:03:00
 */
public class BasicSearchWorkbench extends WorkbenchView {

    private final MediaList<ConceptMedia> mediaList = new MediaList<>();
    private final ObservableList<ConceptSelection> conceptSelections = FXCollections.observableArrayList();

    public BasicSearchWorkbench() {
        ActionFooter footer = new ActionFooter();
        footer.addAction(new Action(AppIcons.TRASH, "Remove All",
                () -> mediaList.getItems().clear()));
        setFooterNode(footer);
        setCenterNode(mediaList);
    }

    public ObservableList<ConceptMedia> getConceptMedia() {
        return mediaList.getItems();
    }


}
