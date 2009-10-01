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

    static String formatLinkAsString(ILink link) {
        return "${link.linkName}${ILink.DELIMITER}${link.linkValue}${ILink.DELIMITER}${link.toConcept}"
    }

    static String formatLinkAsLongString(ILink link) {
        return "${link.fromConcept}${ILink.DELIMITER}${link.linkName}${ILink.DELIMITER}${link.linkValue}${ILink.DELIMITER}${link.toConcept}"
    }

}