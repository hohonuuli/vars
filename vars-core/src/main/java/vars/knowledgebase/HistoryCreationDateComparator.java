package vars.knowledgebase;

import java.util.Comparator;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 1:42:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class HistoryCreationDateComparator implements Comparator<History> {

    public int compare(History h0, History h1) {
        return h0.getCreationDate().compareTo(h1.getCreationDate());
    }
}
