package vars.queryfx.beans;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import vars.ILink;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brian Schlining
 * @since 2015-07-25T18:00:00
 */
public class ResolvedConceptSelection extends ConceptSelection {

    private List<String> concepts;
    private ObjectProperty<Image> image = new SimpleObjectProperty<>();

    public ResolvedConceptSelection(String conceptName, ILink link, boolean extendToParent,
            boolean extendToSiblings, boolean extendToChildren, boolean extendToDescendants,
            List<String> concepts) {
        super(conceptName, link, extendToParent, extendToSiblings, extendToChildren, extendToDescendants);
        this.concepts = concepts;
    }

    public ResolvedConceptSelection(String conceptName, ILink link, boolean extendToParent,
            boolean extendToSiblings, boolean extendToChildren, boolean extendToDescendants,
            List<String> concepts, Image image) {
        this(conceptName, link, extendToParent, extendToSiblings, extendToChildren, extendToDescendants, concepts);
        this.image.set(image);
    }

    public ResolvedConceptSelection(ConceptSelection conceptSelection, List<String> concepts) {
        this(conceptSelection.getConceptName(),
                conceptSelection.getLink(),
                conceptSelection.isExtendToParent(),
                conceptSelection.isExtendToSiblings(),
                conceptSelection.isExtendToChildren(),
                conceptSelection.isExtendToDescendants(),
                concepts);
    }

    public List<String> getConcepts() {
        return concepts;
    }

    public Image getImage() {
        return image.get();
    }

    public ObjectProperty<Image> imageProperty() {
        return image;
    }
}
