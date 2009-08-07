package vars.knowledgebase.jpa;

import vars.jpa.DAO;
import vars.knowledgebase.ILinkRealizationDAO;
import vars.knowledgebase.ILinkRealization;
import org.mbari.jpax.IEAO;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 4:46:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class LinkRealizationDAO extends DAO implements ILinkRealizationDAO {

    public LinkRealizationDAO(IEAO eao) {
        super(eao);
    }

    public Set<ILinkRealization> findAllByLinkName() {
        return null;  // TODO implement this method.
    }

    public void validateName(ILinkRealization object) {
        // TODO implement this method.
    }
}
