package vars.queryfx.ui.sdkfx;

import com.google.common.base.Preconditions;
import com.guigarage.sdk.util.Media;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import org.fxmisc.easybind.EasyBind;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptName;
import vars.queryfx.QueryService;

import java.util.stream.Collectors;

/**
 * @author Brian Schlining
 * @since 2015-07-20T15:06:00
 */
public class ConceptMedia implements Media {

    private QueryService queryService;

    private ObjectProperty<Concept> concept = new SimpleObjectProperty<>();

    private StringProperty title = new SimpleStringProperty();

    private StringProperty description = new SimpleStringProperty();

    private ObjectProperty<Image> image = new SimpleObjectProperty<>();

    private BooleanProperty extendToParent = new SimpleBooleanProperty();
    private BooleanProperty extendToSiblings = new SimpleBooleanProperty();
    private BooleanProperty extendToChildren = new SimpleBooleanProperty();
    private BooleanProperty extendToDescendants = new SimpleBooleanProperty();

    public ConceptMedia(QueryService queryService, Concept _concept, boolean _extendToParent,
                        boolean _extendToSiblings, boolean _extendToChildren,
                        boolean _extendToDescendants) {
        Preconditions.checkArgument(queryService != null, "MISSING QueryService!!");
        Preconditions.checkArgument(_concept != null, "Concept arg can not be null");
        this.queryService = queryService;
        extendToParent.set(_extendToParent);
        extendToSiblings.set(_extendToSiblings);
        extendToDescendants.set(_extendToDescendants);
        extendToChildren.set(_extendToChildren);
        init();
        updateDescription();
    }

    private void init() {
        title.bind(EasyBind.map(this.concept, c -> c.getPrimaryConceptName().getName()));
        image.bind(EasyBind.map(this.concept,
                c -> new Image(c.getConceptMetadata().getPrimaryImage().getUrl(), true)));

        extendToParent.addListener((obs, oldVal, newVal) -> updateDescription());
        extendToSiblings.addListener((obs, oldVal, newVal) -> updateDescription());
        extendToChildren.addListener((obs, oldVal, newVal) -> updateDescription());
        extendToDescendants.addListener((obs, oldVal, newVal) -> updateDescription());
    }

    private synchronized void updateDescription() {
        final String primaryName = concept.get().getPrimaryConceptName().getName();
        queryService.findDescendantNamesAsStrings(primaryName).thenAccept(list -> {
            Platform.runLater(() -> {
                String commaSeparated = list.stream()
                        .filter(s -> !s.equals(primaryName))
                        .collect(Collectors.joining(", "));
                description.set(commaSeparated);
            });

        });
    }

    @Override
    public StringProperty descriptionProperty() {
        return description;
    }

    @Override
    public StringProperty titleProperty() {
        return title;
    }

    @Override
    public ObjectProperty<Image> imageProperty() {
        return image;
    }
}
