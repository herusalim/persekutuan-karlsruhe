package org.persekutuankarlsruhe.webapp.sendquestions;

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

public class QuestionDatastore {

    private static final String PROPERTY_TEXT = "text";
    private static final String PROPERTY_NAMA = "nama";
    private static final String PROPERTY_SHOW_PUBLIC = "tampilkan";
    private static final String PROPERTY_TIMESTAMP = "timestamp";
    private static final String PROPERTY_TIMESTAMP_SELESAI = "timestampSelesai";

    private static final Logger LOG = Logger.getLogger(QuestionDatastore.class.getName());

    private static QuestionDatastore SINGLETON = new QuestionDatastore();

    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    public static final String ENTITY_KIND = "Pertanyan";

    private QuestionDatastore() {
        // For Singleton
    }

    public static QuestionDatastore getInstance() {
        return SINGLETON;
    }

    public void addQuestion(String nama, String pertanyaan, boolean showPublic) {

        long timestamp = System.currentTimeMillis();

        Question question = new Question();
        question.setTimestamp(timestamp);
        question.setNama(nama);
        question.setText(pertanyaan);
        question.setShowPublic(showPublic);

        Entity questionEntity = toDataStoreEntity(question);
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Menambahkan entity " + questionEntity);
        }
        datastore.put(questionEntity);
    }

    private Entity toDataStoreEntity(Question question) {
        Text pertanyaanPanjang = new Text(question.getText());
        Key entityKey = KeyFactory.createKey(ENTITY_KIND, question.getTimestamp());
        Entity questionEntity = new Entity(entityKey);
        questionEntity.setProperty(PROPERTY_NAMA, question.getNama());
        questionEntity.setProperty(PROPERTY_TEXT, pertanyaanPanjang);
        questionEntity.setProperty(PROPERTY_SHOW_PUBLIC, question.isShowPublic());
        questionEntity.setProperty(PROPERTY_TIMESTAMP, question.getTimestamp());
        questionEntity.setProperty(PROPERTY_TIMESTAMP_SELESAI, question.getTimestampSelesai());
        return questionEntity;
    }

    public void updateQuestions(List<Question> questions) {
        List<Entity> entities = new ArrayList<Entity>();
        for (Question question : questions) {
            entities.add(toDataStoreEntity(question));
        }
        datastore.put(entities);
    }

    public List<Question> getQuestions() {
        List<Question> questions = new ArrayList<Question>();

        FetchOptions fetchOptions = FetchOptions.Builder.withDefaults();

        Query query = new Query(ENTITY_KIND);
        PreparedQuery preparedQuery = datastore.prepare(query);

        Iterator<Entity> resultsIterator = preparedQuery.asQueryResultList(fetchOptions).iterator();
        while (resultsIterator.hasNext()) {
            Entity entity = resultsIterator.next();
            Question question = new Question();

            Long timestamp = (Long) entity.getProperty(PROPERTY_TIMESTAMP);
            if (timestamp != null) {
                question.setTimestamp(timestamp);
            }

            String nama = (String) entity.getProperty(PROPERTY_NAMA);
            question.setNama(nama);

            Text text = (Text) entity.getProperty(PROPERTY_TEXT);
            if (text != null) {
                question.setText(text.getValue());
            }

            Boolean showPublic = (Boolean) entity.getProperty(PROPERTY_SHOW_PUBLIC);
            question.setShowPublic(showPublic);

            Long timestampSelesai = (Long) entity.getProperty(PROPERTY_TIMESTAMP_SELESAI);
            if (timestampSelesai != null) {
                question.setTimestampSelesai(timestampSelesai);
            }

            questions.add(question);
        }
        return questions;

    }
}
