package vars.testing;

import vars.knowledgebase.KnowledgebaseFactory;
import vars.knowledgebase.IConcept;
import vars.knowledgebase.IConceptName;
import vars.knowledgebase.ConceptNameTypes;
import vars.knowledgebase.ConceptTypes;
import vars.knowledgebase.IHistory;
import vars.knowledgebase.IMedia;
import vars.knowledgebase.MediaTypes;
import vars.knowledgebase.IUsage;
import vars.knowledgebase.ILinkTemplate;
import vars.knowledgebase.ILinkRealization;
import vars.knowledgebase.IConceptMetadata;

import java.util.Date;

/**
 * Factory for making test objects populated with some values.
 */
public class KnowledgebaseTestObjectFactory {

    private final KnowledgebaseFactory factory;

    public KnowledgebaseTestObjectFactory(KnowledgebaseFactory factory) {
        this.factory = factory;
    }


    private static long randomNumber(long min, long max) {
        long range = max - min;
        long value = (long) (Math.random() * range + min);
        return value;
    }


    public IConcept makeObjectGraph(int i) {
        return makeObjectGraph("TEST", i);
    }

    /**
     * Create a concept based on the suplied name. The concept will have 3
     * ConceptNames associated with it with variations of the argument as its
     * name.
     *
     * @param name
     * @return
     */
    public IConcept makeConcept(String name) {
        IConcept c = factory.newConcept();
        IConceptName cn1 = factory.newConceptName();
        cn1.setName(name + "-primary");
        cn1.setNameType(ConceptNameTypes.PRIMARY.getName());
        c.addConceptName(cn1);

        IConceptName cn2 = factory.newConceptName();
        cn2.setName(name + "-common");
        cn2.setNameType(ConceptNameTypes.COMMON.getName());
        c.addConceptName(cn2);

        IConceptName cn3 = factory.newConceptName();
        cn3.setName(name + "-synonym");
        cn3.setNameType(ConceptNameTypes.SYNONYM.getName());
        c.addConceptName(cn3);

        c.setNodcCode("dunno");
        c.setOriginator("Unit test");
        c.setRankLevel("1");
        c.setRankName("Phylum" + name);
        c.setReference("Some reference");
        c.setStructureType(ConceptTypes.TAXONOMY.getName());
        return c;
    }

    public IHistory makeHistory() {

        IHistory h = factory.newHistory();
        h.setCreationDate(new Date());
        h.setAction(IHistory.ACTION_ADD);
        h.setComment("test");
        h.setCreatorName("testy-the-testor");
        h.setField("TEST");
        h.setNewValue("NEW VALUE");
        h.setOldValue("OLD VALUE");

        return h;
    }

    public IMedia makeMedia() {
        IMedia m = factory.newMedia();
        m.setCaption("Caption " + randomNumber(0, 99999999));
        m.setCredit("Credit " + randomNumber(0, 9999999));
        m.setUrl("filename" + randomNumber(0, 99999999));
        m.setType(MediaTypes.IMAGE.getType());
        m.setPrimary(true);
        return m;
    }

    public IUsage makeUsage() {
        IUsage u = factory.newUsage();
        u.setEmbargoExpirationDate(new Date());
        u.setSpecification("Specification can be a looooooong message." + randomNumber(0, 99999999));
        return u;
    }

    public ILinkTemplate makeLinkTemplate() {
        ILinkTemplate lt = factory.newLinkTemplate();
        lt.setLinkName("link-template");
        lt.setLinkValue("0");
        lt.setToConcept("self");
        return lt;
    }

    public ILinkRealization makeLinkRealization() {
        ILinkRealization lr = factory.newLinkRealization();
        lr.setLinkName("link-realization");
        lr.setLinkValue(randomNumber(0, 9999) + "");
        lr.setToConcept("self");
        return lr;
    }

    public IConcept makeObjectGraph(String name, int depth) {

        IConcept root = makeFancyConcept(name, depth);
        for (int i = 0; i < depth; i++) {
            IConcept child = makeObjectGraph(name + "_" + i + "_" + randomNumber(0, 1000), depth - 1);
            root.addChildConcept(child);
        }

        return root;
    }

    public IConcept makeFancyConcept(String name, int x) {
        IConcept c = makeConcept(name);
        IConceptMetadata cm = c.getConceptMetadata();
        cm.setUsage(makeUsage());
        for (int i = 0; i < x; i++) {
            cm.addHistory(makeHistory());
            cm.addLinkRealization(makeLinkRealization());
            cm.addLinkTemplate(makeLinkTemplate());
            cm.addMedia(makeMedia());
        }
        return c;
    }

}
