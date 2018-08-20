package org.persekutuankarlsruhe.webapp.remindpelayanan;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.persekutuankarlsruhe.webapp.sheets.JadwalPelayanan;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class ReminderPelayananDatastore {

	private static final Logger LOG = Logger.getLogger(ReminderPelayananDatastore.class.getName());

	private static ReminderPelayananDatastore SINGLETON = new ReminderPelayananDatastore();

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

	private static final String PROPERTY_TANGGAL_PERSEKUTUAN = "tanggalPersekutuan";
	private static final String PROPERTY_TANGGAL_TERKIRIM = "tanggalTerkirim";

	private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	public static final String ENTITY_KIND = "ReminderPelayanan";

	private ReminderPelayananDatastore() {
		// For Singleton
	}

	public static ReminderPelayananDatastore getInstance() {
		return SINGLETON;
	}

	public void addReminderEntity(JadwalPelayanan jadwal, String email, ReminderType reminderType) {
		Entity reminderEntity = new Entity(ENTITY_KIND, getReminderEntityId(jadwal, email, reminderType));
		reminderEntity.setProperty(PROPERTY_TANGGAL_PERSEKUTUAN, jadwal.getTanggal());
		datastore.put(reminderEntity);
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("Menambahkan entity baru ke datastore untuk tanggal " + jadwal.getTanggal() + ": "
					+ reminderEntity);
		}
	}

	public boolean isReminderSent(JadwalPelayanan jadwal, String email, ReminderType reminderType)
			throws EntityNotFoundException {
		Entity reminderEntity = datastore.get(getReminderEntityKey(jadwal, email, reminderType));
		boolean isReminderSent = reminderEntity.getProperty(PROPERTY_TANGGAL_TERKIRIM) != null;
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("Mengambil entity: " + reminderEntity + "; isReminderSent: " + isReminderSent);
		}
		return isReminderSent;
	}

	public void setReminderSent(JadwalPelayanan jadwal, String email, ReminderType reminderType)
			throws EntityNotFoundException {
		Entity reminderEntity = datastore.get(getReminderEntityKey(jadwal, email, reminderType));
		Date current = new Date();
		reminderEntity.setProperty(PROPERTY_TANGGAL_TERKIRIM, current);
		datastore.put(reminderEntity);
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("Mengubah status terkirim (" + current + "): " + reminderEntity);
		}
	}

	public void deleteReminderEntity(JadwalPelayanan jadwal, String email, ReminderType reminderType) {
		Key entityKey = getReminderEntityKey(jadwal, email, reminderType);
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("Menghapus entity dengan key: " + entityKey);
		}
		datastore.delete(entityKey);
	}

	private Key getReminderEntityKey(JadwalPelayanan jadwal, String email, ReminderType reminderType) {

		Key entityKey = KeyFactory.createKey(ENTITY_KIND, getReminderEntityId(jadwal, email, reminderType));
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("Mendapatkan entity key: " + entityKey + " dari jadwal: " + jadwal + ";Email: " + email
					+ "; type: " + reminderType);
		}
		return entityKey;
	}

	private String getReminderEntityId(JadwalPelayanan jadwal, String email, ReminderType reminderType) {
		return DATE_FORMAT.format(jadwal.getTanggal()) + "|" + email + "|" + reminderType;
	}

	public void removeExpiredEntities(Date dateLimit) {

		FetchOptions fetchOptions = FetchOptions.Builder.withDefaults();

		Filter propertyFilter = new FilterPredicate(PROPERTY_TANGGAL_PERSEKUTUAN, FilterOperator.LESS_THAN, dateLimit);
		Query query = new Query(ENTITY_KIND).setFilter(propertyFilter);
		PreparedQuery preparedQuery = datastore.prepare(query);

		Iterator<Entity> resultsIterator = preparedQuery.asQueryResultList(fetchOptions).iterator();
		while (resultsIterator.hasNext()) {
			Entity entity = resultsIterator.next();
			LOG.info("Menghapus entity: " + entity);
			datastore.delete(entity.getKey());
		}
	}

}
