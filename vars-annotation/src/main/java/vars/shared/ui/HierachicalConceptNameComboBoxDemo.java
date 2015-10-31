package vars.shared.ui;

import com.google.inject.Injector;
import vars.annotation.AnnotationPersistenceService;
import vars.annotation.ui.Lookup;
import vars.knowledgebase.Concept;

import javax.swing.*;
import java.awt.*;

/**
 * Created by rachelorange on 10/30/15.
 */
public class HierachicalConceptNameComboBoxDemo {

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        Injector injector = (Injector) Lookup.getGuiceInjectorDispatcher().getValueObject();
        HierachicalConceptNameComboBox cb = injector.getInstance(HierachicalConceptNameComboBox.class);
        frame.add(cb);
        frame.setVisible(true);

        AnnotationPersistenceService aps = injector.getInstance(AnnotationPersistenceService.class);
        Concept concept = aps.findConceptByName("physical-object");
        cb.setConcept(concept);
    }
}
