package org.persekutuankarlsruhe.webapp.feedbacks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Text;

public class FeedbackDatastore {

    private static final String PROPERTY_TEXT = "text";
    private static final String PROPERTY_NAMA = "nama";
    private static final String PROPERTY_JUDUL = "judul";
    private static final String PROPERTY_TIMESTAMP = "timestamp";
    private static final String PROPERTY_PENERIMA = "penerima";

    private static final Logger LOG = Logger.getLogger(FeedbackDatastore.class.getName());

    private static FeedbackDatastore SINGLETON = new FeedbackDatastore();

    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    public static final String ENTITY_KIND = "Feedback";

    private FeedbackDatastore() {
        // For Singleton
    }

    public static FeedbackDatastore getInstance() {
        return SINGLETON;
    }

    public void addFeedback(String nama, String judul, String text, String penerima) {

        long timestamp = System.currentTimeMillis();

        Feedback feedback = new Feedback();
        feedback.setTimestamp(timestamp);
        feedback.setNama(nama);
        feedback.setJudul(judul);
        feedback.setText(text);
        feedback.setPenerima(penerima);

        Entity questionEntity = toDataStoreEntity(feedback);
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Menambahkan entity " + questionEntity);
        }
        datastore.put(questionEntity);
    }

    private Entity toDataStoreEntity(Feedback question) {
        Text pertanyaanPanjang = new Text(question.getText());
        Key entityKey = KeyFactory.createKey(ENTITY_KIND, question.getTimestamp());
        Entity questionEntity = new Entity(entityKey);
        questionEntity.setProperty(PROPERTY_NAMA, question.getNama());
        questionEntity.setProperty(PROPERTY_TEXT, pertanyaanPanjang);
        questionEntity.setProperty(PROPERTY_JUDUL, question.getJudul());
        questionEntity.setProperty(PROPERTY_TIMESTAMP, question.getTimestamp());
        questionEntity.setProperty(PROPERTY_PENERIMA, question.getPenerima());
        return questionEntity;
    }

    public void updateQuestions(List<Feedback> questions) {
        List<Entity> entities = new ArrayList<Entity>();
        for (Feedback question : questions) {
            entities.add(toDataStoreEntity(question));
        }
        datastore.put(entities);
    }

    public List<Feedback> getQuestions() {
        List<Feedback> feedbacks = new ArrayList<Feedback>();

        FetchOptions fetchOptions = FetchOptions.Builder.withDefaults();

        Query query = new Query(ENTITY_KIND);
        PreparedQuery preparedQuery = datastore.prepare(query);

        Iterator<Entity> resultsIterator = preparedQuery.asQueryResultList(fetchOptions).iterator();
        while (resultsIterator.hasNext()) {
            Entity entity = resultsIterator.next();
            Feedback feedback = new Feedback();

            Long timestamp = (Long) entity.getProperty(PROPERTY_TIMESTAMP);
            if (timestamp != null) {
                feedback.setTimestamp(timestamp);
            }

            String nama = (String) entity.getProperty(PROPERTY_NAMA);
            feedback.setNama(nama);

            Text text = (Text) entity.getProperty(PROPERTY_TEXT);
            if (text != null) {
                feedback.setText(text.getValue());
            }

            String judul = (String) entity.getProperty(PROPERTY_JUDUL);
            feedback.setJudul(judul);

            String penerima = (String) entity.getProperty(PROPERTY_PENERIMA);
            feedback.setPenerima(penerima);

            feedbacks.add(feedback);
        }
        return feedbacks;

    }
}
