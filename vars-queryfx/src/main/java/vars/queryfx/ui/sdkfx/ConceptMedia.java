package vars.queryfx.ui.sdkfx;

import com.google.common.base.Preconditions;
import com.guigarage.sdk.util.Media;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import vars.LinkUtilities;
import vars.knowledgebase.Concept;
import vars.queryfx.StateLookup;
import vars.queryfx.beans.ResolvedConceptSelection;

import java.util.stream.Collectors;

/**
 * @author Brian Schlining
 * @since 2015-07-20T15:06:00
 */
public class ConceptMedia implements Media {


    private final ResolvedConceptSelection conceptSelection;
    private Concept concept;

    private StringProperty title = new SimpleStringProperty();

    private StringProperty description = new SimpleStringProperty();

    private ObjectProperty<Image> image = new SimpleObjectProperty<>();

    public ConceptMedia(ResolvedConceptSelection conceptSelection) {
        Preconditions.checkArgument(conceptSelection != null, "Concept arg can not be null");
        this.conceptSelection = conceptSelection;
        init();
    }

    private void init() {

        String titleString = conceptSelection.getConceptName();
        if (!LinkUtilities.formatAsString(conceptSelection.getLink())
                .equals(LinkUtilities.formatAsString(StateLookup.WILD_CARD_LINK))) {
            titleString = titleString + " | " + LinkUtilities.formatAsString(conceptSelection.getLink());
        }

        title.set(titleString);

        String desc = conceptSelection.getConcepts().stream()
                .filter(s -> !s.equals(conceptSelection.getConceptName()))
                .collect(Collectors.joining(", "));

        description.set(desc);

        image.set(conceptSelection.getImage());


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

    public ResolvedConceptSelection getConceptSelection() {
        return conceptSelection;
    }
}
