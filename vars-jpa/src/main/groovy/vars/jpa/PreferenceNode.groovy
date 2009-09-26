/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.jpa

import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.EntityListeners
import javax.persistence.Id
import org.mbari.jpaxx.TransactionLogger
import javax.persistence.Column
import javax.persistence.AttributeOverride
import javax.persistence.AttributeOverrides
import javax.persistence.Transient
import javax.persistence.NamedQuery
import javax.persistence.NamedQueries;

/**
 *
 * @author brian
 */
@Entity(name = "PreferenceNode")
@Table(name = "Prefs")
@EntityListeners( value = [TransactionLogger.class] )
@NamedQueries( value = [
    @NamedQuery(name = "PreferenceNode.findAllLikeNodeName",
                query = "SELECT p FROM PreferenceNode p WHERE p.nodeName LIKE :nodeName"),
    @NamedQuery(name = "PreferenceNode.findByNodeNameAndPrefKey",
                query = "SELECT p FROM PreferenceNode p WHERE p.nodeName = :nodeName AND p.prefKey = :prefKey"),
    @NamedQuery(name = "PreferenceNode.findAllByNodeName",
                query = "SELECT p FROM PreferenceNode p WHERE p.nodeName = :nodeName")
])
class PreferenceNode implements Serializable {

    @Id
	@AttributeOverrides(value = [
            @AttributeOverride(name = "nodeName", column = @Column(name = "NodeName")),
            @AttributeOverride(name = "prefKey", column = @Column(name = "PrefKey", length = 50))
	])

    String nodeName

    String prefKey

    @Column(name = "PrefValue", nullable = false)
    String prefValue

    @Transient
    private nodes

    void setNodeName(String nodeName) {
        this.nodeName = nodeName;
        nodes = null
    }

    String[] getNodes() {
        if (nodes == null && nodeName != null) {
            def nodes = nodeName.split("/")
            if (nodes[0] == "") {
                nodes = nodes[1..-1]
            }
        }
        return nodes
    }
	
}

