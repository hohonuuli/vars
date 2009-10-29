package vars.knowledgebase.ui.persistence;

import vars.DAO;

abstract class UpdateSubscriber<T> extends PersistenceSubscriber<T> {


    public UpdateSubscriber(String deleteTopic, DAO dao) {
        super(deleteTopic, dao);
    }

    @Override
    T doPersistenceThing(T obj) {
        return dao.update(obj);
    }

    T prepareForTransaction(T obj) {
        return obj;
    }
}
