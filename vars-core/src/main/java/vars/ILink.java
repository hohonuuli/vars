package vars;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 4, 2009
 * Time: 10:43:53 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ILink {

    String PROP_LINKNAME = "linkName";
    String PROP_LINKVALUE = "linkValue";
    String PROP_TOCONCEPT = "toConcept";

    String getFromConcept();

    String getLinkName();

    void setLinkName(String linkName);

    String getToConcept();

    void setToConcept(String toConcept);

    String getLinkValue();

    void setLinkValue(String linkValue);

}
