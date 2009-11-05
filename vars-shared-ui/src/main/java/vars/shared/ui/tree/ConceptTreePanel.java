/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.shared.ui.tree;

import com.google.inject.Inject;
import foxtrot.Job;
import foxtrot.Worker;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.bushe.swing.event.EventBus;
import org.mbari.swing.LabeledSpinningDialWaitIndicator;
import org.mbari.swing.SearchableTreePanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.ConceptNameDAO;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.shared.ui.GlobalLookup;


/**
 *
 * @author brian
 */
public class ConceptTreePanel extends SearchableTreePanel {

    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
         * Store previous searches so that we don't try to do database lookup on them again.
         */
    private final Collection<String> cachedGlobSearches;

    /**
         * Store previous searches so that we don't try to do database lookup on them again.
         * @uml.property  name="cachedWordSearches"
         * @uml.associationEnd  multiplicity="(0 -1)" elementType="java.lang.String"
         */
    private final Collection<String> cachedWordSearches;

    private final KnowledgebaseDAOFactory knowledgebaseDAOFactory;


    /**
     * Constructor
     *
     * @param conceptDAO
     * @param conceptNameDAO
     * @param persistenceCache
     */
    @Inject
    public ConceptTreePanel(KnowledgebaseDAOFactory knowledgebaseDAOFactory) {
        super();
        this.knowledgebaseDAOFactory = knowledgebaseDAOFactory;
        cachedWordSearches = new HashSet<String>();
        cachedGlobSearches = new HashSet<String>();
    }


    public void refresh() {
        cachedGlobSearches.clear();
        cachedWordSearches.clear();
        ConceptTreeModel model = (ConceptTreeModel) getJTree().getModel();
        model.refresh();
    }

    public void refreshAndOpenNode(Concept concept) {
        if (log.isDebugEnabled()) {
            log.debug("Refreshing ConceptTree and opening it to '" +
                    concept.getPrimaryConceptName().getName() + "', " + concept);
        }
        refresh();
        openNode(concept);
    }

    /**
     * @param node
     *
     * @return
     */
    @Override
    public String getNodeTextToSearch(final DefaultMutableTreeNode node) {
        final Object userObject = node.getUserObject();
        final StringBuffer textToSearch = new StringBuffer();

        // Objects whos children have not been loaded yet will return a Boolean
        // as a user object. We should ignore these as much as we can.
        if (userObject instanceof Concept) {
            final Concept concept = (Concept) node.getUserObject();

            /*
             * The text is actually a composite of all names,
             * including primary, secondary, and common
             */
            final Collection<ConceptName> conceptNames = new ArrayList<ConceptName>(concept.getConceptNames());
            for (ConceptName conceptName1 : conceptNames) {
                textToSearch.append(conceptName1.getName());
                textToSearch.append(" ");
            }

        }

        return textToSearch.toString();
    }

    /**
     * This overridden method does a database lookup for searches. This is a
     * woorkaournd needed for lazy loading. This method will load the branches
     * of all matches from the database.
     *
     * @param text
     * @param useGlobSearch
     *
     * @return
     */
    @Override
    public boolean goToMatchingNode(final String text, final boolean useGlobSearch) {

        /*
         * Disable so that folks can't start multiple searches.
         */
        getSearchBtn().setEnabled(false);
        getSearchTextField().setEnabled(false);
        LabeledSpinningDialWaitIndicator waitIndicator = new LabeledSpinningDialWaitIndicator(this, "Searching for '" + text + "'");
        loadNodes(text, useGlobSearch, waitIndicator);
        boolean ok = super.goToMatchingNode(text, useGlobSearch);
        waitIndicator.dispose();
        getSearchBtn().setEnabled(true);
        getSearchTextField().setEnabled(true);
        getSearchTextField().requestFocus();

        return ok;
    }

    /**
     * Perfroms the database lookup of all matching Concepts.
     * @param text
     * @param useGlobSearch
     */
    private void loadNodes(final String text, final boolean useGlobSearch, final LabeledSpinningDialWaitIndicator waitIndicator) {
        Collection<ConceptName> matches = (Collection) Worker.post(new Job() {
            public Object run() {
                Collection<ConceptName> matches = null;
                try {

                    if (useGlobSearch) {
                        if (!cachedGlobSearches.contains(text)) {
                            ConceptNameDAO dao = knowledgebaseDAOFactory.newConceptNameDAO();
                            dao.startTransaction();
                            matches = dao.findByNameContaining(text);
                            dao.endTransaction();
                            cachedGlobSearches.add(text);
                            cachedWordSearches.add(text);
                        }
                    }
                    else {
                        if (!cachedWordSearches.contains(text)) {
                            ConceptNameDAO dao = knowledgebaseDAOFactory.newConceptNameDAO();
                            dao.startTransaction();
                            matches = dao.findByNameStartingWith(text);
                            dao.endTransaction();
                            cachedWordSearches.add(text);
                        }
                    }
                }
                catch (Exception e) {
                    if (log.isErrorEnabled()) {
                        log.error("Database lookup of " + text + " failed", e);
                        EventBus.publish(GlobalLookup.TOPIC_NONFATAL_ERROR, e);
                    }
                }
                return matches;
            }
        });


        /*
         * If we loaded the matched names from the database then we need
         * to open the Concept such that it gets cached under the root
         * concept.
         */
        if (matches != null) {
            int n = 0;
            for (Iterator i = matches.iterator(); i.hasNext(); ) {
                n++;
                final ConceptName cn = (ConceptName) i.next();
                waitIndicator.setLabel("Loading '" + cn.getName() + "'");

                /*
                 * Have to open the node in a seperate thread for the
                 * progress monitor to update. Here we're using foxtrot.
                 */
                Worker.post(new Job() {

                    public Object run() {
                        openNode(cn.getConcept());

                        return null;
                    }
                });
            }

        }
    }

    /**
     * Loads the branch of a particular concept. This method does the following
     * <ol>
     *      <li>Walks from the concept up the tree to the root concept, storing
     *      the concepts in a list. (This is very fast)</li>
     *  <li>Starts walking from the root down (using lazyExpand), searching each
     *      childnode for a matching primary name (which was stored in the first
     *      step</li>
     *  <li>If a matching primary name is found this stops otherwise
     *              it opens the next level and searches for the next mathc in the list.</li>
     *  <li></li>
     * </ol>
     * @param concept
     */
    public synchronized void openNode(final Concept concept) {
        if (log.isDebugEnabled()) {
            log.debug("Opening node containing '" + concept.getPrimaryConceptName().getName() +
                    "', " + concept);
        }

        if (concept == null) {
            return;
        }

        final JTree tree = getJTree();
        final ConceptTreeModel model = (ConceptTreeModel) tree.getModel();
        final TreeNode node = (TreeNode) Worker.post(new Job() {
            public Object run() {
                return model.loadNode(concept.getPrimaryConceptName().getName());
            }
        });

        final TreePath path = new TreePath(model.getPathToRoot(node));

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                model.reload(node);
                tree.scrollPathToVisible(path);
            }
        });
        validate();
    }


}

