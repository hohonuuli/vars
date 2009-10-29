package vars.knowledgebase.ui.persistence;

import vars.DAO;

abstract class InsertSubscriber<T> extends PersistenceSubscriber<T> {


    public InsertSubscriber(String deleteTopic, DAO dao) {
        super(deleteTopic, dao);
    }

    @Override
    T doPersistenceThing(T obj) {
        return dao.makePersistent(obj);
    }

    T prepareForTransaction(T obj) {
        return obj;
    }
}
