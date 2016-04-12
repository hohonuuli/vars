package vars.queryfx.ui.sdkfx;

import com.guigarage.sdk.action.Action;
import com.guigarage.sdk.container.WorkbenchView;
import com.guigarage.sdk.footer.ActionFooter;
import com.guigarage.sdk.list.MediaList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import vars.queryfx.beans.ConceptSelection;
import vars.queryfx.beans.ResolvedConceptSelection;
import vars.queryfx.rx.messages.ShowConceptConstraintsWorkbenchMsg;
import vars.queryfx.ui.AppIcons;
import vars.shared.rx.RXEventBus;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Brian Schlining
 * @since 2015-07-20T15:03:00
 */
public class BasicSearchWorkbench extends WorkbenchView {

    private final MediaList<ConceptMedia> mediaList = new MediaList<>();
    private final ObservableList<ConceptSelection> conceptSelections = FXCollections.observableArrayList();
    private final RXEventBus eventBus;

    public BasicSearchWorkbench(RXEventBus eventBus) {
        this.eventBus = eventBus;
        ActionFooter footer = new ActionFooter();
        footer.addAction(new Action(AppIcons.PLUS, "Add Search Term",
                () -> eventBus.send(new ShowConceptConstraintsWorkbenchMsg())));
        footer.addAction(new Action(AppIcons.TRASH, "Remove All",
                () -> mediaList.getItems().clear()));
        setFooterNode(footer);
        setCenterNode(mediaList);
    }

    public ObservableList<ConceptMedia> getConceptMedia() {
        return mediaList.getItems();
    }

    public List<ResolvedConceptSelection> getConceptSelections() {
        return getConceptMedia().stream()
                .map(ConceptMedia::getConceptSelection)
                .collect(Collectors.toList());
    }

}
