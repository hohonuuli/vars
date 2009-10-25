/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.knowledgebase.ui;

import com.google.inject.Inject;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.AnnotationFactory;
import vars.knowledgebase.HistoryFactory;
import vars.knowledgebase.KnowledgebaseDAO;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.knowledgebase.KnowledgebaseFactory;
import vars.knowledgebase.ui.actions.ApproveHistoryTask;
import vars.query.QueryDAO;

/**
 * Container that holds on to a ton of shared objects that need to be widely
 * used across this application
 */
public class ToolBelt {

    private final ApproveHistoryTask approveHistoryTask;
    private final AnnotationDAOFactory annotationDAOFactory;
    private final AnnotationFactory annotationFactory;
    private final KnowledgebaseDAO knowledgebaseDAO;
    private final KnowledgebaseDAOFactory knowledgebaseDAOFactory;
    private final KnowledgebaseFactory knowledgebaseFactory;
    private final HistoryFactory historyFactory;
    private final QueryDAO queryDAO;

    @Inject
    public ToolBelt(AnnotationDAOFactory annotationDAOFactory, AnnotationFactory annotationFactory,
            KnowledgebaseDAO knowledgebaseDAO, KnowledgebaseDAOFactory knowledgebaseDAOFactory,
            KnowledgebaseFactory knowledgebaseFactory, QueryDAO queryDAO) {
        this.annotationDAOFactory = annotationDAOFactory;
        this.annotationFactory = annotationFactory;
        this.knowledgebaseDAO = knowledgebaseDAO;
        this.knowledgebaseDAOFactory = knowledgebaseDAOFactory;
        this.knowledgebaseFactory = knowledgebaseFactory;
        this.queryDAO = queryDAO;
        historyFactory = new HistoryFactory(knowledgebaseFactory);
        approveHistoryTask = new  ApproveHistoryTask(annotationDAOFactory, knowledgebaseDAO, knowledgebaseDAOFactory, knowledgebaseFactory);
    }

    public AnnotationDAOFactory getAnnotationDAOFactory() {
        return annotationDAOFactory;
    }

    public AnnotationFactory getAnnotationFactory() {
        return annotationFactory;
    }

    public ApproveHistoryTask getApproveHistoryTask() {
        return approveHistoryTask;
    }

    public HistoryFactory getHistoryFactory() {
        return historyFactory;
    }

    public KnowledgebaseDAO getKnowledgebaseDAO() {
        return knowledgebaseDAO;
    }

    public KnowledgebaseDAOFactory getKnowledgebaseDAOFactory() {
        return knowledgebaseDAOFactory;
    }

    public KnowledgebaseFactory getKnowledgebaseFactory() {
        return knowledgebaseFactory;
    }

    public QueryDAO getQueryDAO() {
        return queryDAO;
    }



}
