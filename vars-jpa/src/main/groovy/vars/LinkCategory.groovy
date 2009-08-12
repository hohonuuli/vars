package vars
/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 6, 2009
 * Time: 11:49:43 AM
 * To change this template use File | Settings | File Templates.
 */

@Category(ILink)
class LinkCategory {

    static final DELIMITER = " | "

    static String formatLinkAsString(ILink link) {
        return "${link.linkName}${DELIMITER}${link.linkValue}${DELIMITER}${link.toConcept}"
    }

    static String formatLinkAsLongString(ILink link) {
        return "${link.fromConcept}${DELIMITER}${link.linkName}${DELIMITER}${link.linkValue}${DELIMITER}${link.toConcept}"
    }

}