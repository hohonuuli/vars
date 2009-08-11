package vars.annotation.jpa;

import vars.jpa.DAO;
import vars.annotation.IObservationDAO;
import vars.annotation.IObservation;
import vars.knowledgebase.IConcept;
import vars.knowledgebase.IConceptDAO;
import org.mbari.jpax.EAO;

import java.util.Set;

import com.google.inject.Inject;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 4:40:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class ObservationDAO extends DAO implements IObservationDAO{

    private final IConceptDAO conceptDAO;

    @Inject
    public ObservationDAO(EAO eao, IConceptDAO conceptDao) {
        super(eao);
        this.conceptDAO = conceptDao;
    }

    public Set<IObservation> findAllByConceptName(String conceptName) {
        return null;  // TODO implement this method.
    }

    public Set<IObservation> findAllByConcept(IConcept concept, boolean cascade) {
        return null;  // TODO implement this method.
    }

    public Set<String> findAllConceptNamesUsedInAnnotations() {
        return null;  // TODO implement this method.
    }

    public void validateName(IObservation object) {
        // TODO implement this method.
    }
}
