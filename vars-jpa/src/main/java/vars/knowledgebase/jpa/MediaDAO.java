package vars.knowledgebase.jpa;

import vars.knowledgebase.IMediaDAO;
import vars.jpa.DAO;
import org.mbari.jpax.IEAO;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 4:48:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class MediaDAO extends DAO implements IMediaDAO {

    public MediaDAO(IEAO eao) {
        super(eao);
    }
    
}
