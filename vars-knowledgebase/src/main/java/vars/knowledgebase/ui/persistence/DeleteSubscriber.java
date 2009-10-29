package vars.knowledgebase.ui.persistence;

import vars.DAO;

public abstract class DeleteSubscriber<T> extends PersistenceSubscriber<T> {


    public DeleteSubscriber(String deleteTopic, DAO dao) {
        super(deleteTopic, dao);
    }

    @Override
    T before(T obj) {
        return dao.findInDatastore(obj);
    }

    @Override
    T doPersistenceThing(T obj) {
        return dao.makeTransient(obj);
    }
}
