package org.persekutuankarlsruhe.webapp.remindpersekutuan;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

public class ReminderPersekutuanDatastore {

	private static final String PROPERTY_HASH_VALUE = "hash";
	private static final String PROPERTY_REMINDER_LIST = "reminderList";

	private static final String PROPERTY_NAMA = "nama";

	private static final Logger LOG = Logger.getLogger(ReminderPersekutuanDatastore.class.getName());

	private static ReminderPersekutuanDatastore SINGLETON = new ReminderPersekutuanDatastore();

	private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	public static final String ENTITY_KIND = "ReminderPersekutuanRegister";
	public static final String ENTITY_KIND_INACTIVE = "ReminderPersekutuanRegisterInactive";

	private ReminderPersekutuanDatastore() {
		// For Singleton
	}

	public static ReminderPersekutuanDatastore getInstance() {
		return SINGLETON;
	}

	public void addRegister(String nama, String email, List<Integer> reminderList)
			throws ReminderRegisterAlreadyExistException {
		Key inactiveEntityKey = KeyFactory.createKey(ENTITY_KIND_INACTIVE, email);
		Key activeEntityKey = KeyFactory.createKey(ENTITY_KIND, email);
		try {
			datastore.get(inactiveEntityKey);
			LOG.warning("Entity dengan key " + inactiveEntityKey + " (inactive) sudah ada, tidak bisa menambahkan");
			throw new ReminderRegisterAlreadyExistException(
					"Entity inactive dengan key " + inactiveEntityKey + " sudah ada",
					ReminderRegisterAlreadyExistException.RegisterType.INACTIVE);
		} catch (EntityNotFoundException e) {
			// check di active entries
			try {
				datastore.get(activeEntityKey);
				LOG.warning("Entity dengan key " + inactiveEntityKey + " (active) sudah ada, tidak bisa menambahkan");
				throw new ReminderRegisterAlreadyExistException(
						"Entity active dengan key " + activeEntityKey + " sudah ada",
						ReminderRegisterAlreadyExistException.RegisterType.ACTIVE);
			} catch (EntityNotFoundException e1) {
				// entry belum ada, bisa ditambahkan yang baru
			}
		}
		Entity reminderEntity = new Entity(inactiveEntityKey);
		reminderEntity.setProperty(PROPERTY_NAMA, nama);
		reminderEntity.setProperty(PROPERTY_REMINDER_LIST, reminderList);
		reminderEntity.setProperty(PROPERTY_HASH_VALUE, calculateHash(nama, email));
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("Menambahkan entity " + reminderEntity + " (inactive)");
		}
		datastore.put(reminderEntity);
	}

	public String getHashValue(Entity entity) {
		return (String) entity.getProperty(PROPERTY_HASH_VALUE);
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getReminderList(Entity entity) {
		return (List<Integer>) entity.getProperty(PROPERTY_REMINDER_LIST);
	}

	public void activateRegister(String email) throws EntityNotFoundException {
		Key activeEntityKey = KeyFactory.createKey(ENTITY_KIND, email);
		Key inactiveEntityKey = KeyFactory.createKey(ENTITY_KIND_INACTIVE, email);
		// Not active yet, activate
		Entity inactiveEntity = datastore.get(inactiveEntityKey);
		Entity activeEntity = new Entity(activeEntityKey);
		activeEntity.setProperty(PROPERTY_NAMA, inactiveEntity.getProperty(PROPERTY_NAMA));
		activeEntity.setProperty(PROPERTY_REMINDER_LIST, inactiveEntity.getProperty(PROPERTY_REMINDER_LIST));
		activeEntity.setProperty(PROPERTY_HASH_VALUE, inactiveEntity.getProperty(PROPERTY_HASH_VALUE));

		// Activate the register
		datastore.put(activeEntity);
		// Remove from inactive entity
		datastore.delete(inactiveEntityKey);
	}

	private String calculateHash(String nama, String email) {
		String hashedText = email + nama + new Date();
		MessageDigest m;
		try {
			m = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException();
		}
		m.reset();
		m.update(hashedText.getBytes());
		byte[] digest = m.digest();
		BigInteger bigInt = new BigInteger(1, digest);
		String hashtext = bigInt.toString(16);

		return hashtext;
	}

	public Entity getInactiveEntity(String email) throws EntityNotFoundException {
		Key key = KeyFactory.createKey(ENTITY_KIND_INACTIVE, email);
		return datastore.get(key);
	}

	public Entity getActiveEntity(String email) throws EntityNotFoundException {
		Key key = KeyFactory.createKey(ENTITY_KIND, email);
		return datastore.get(key);
	}

	public String getName(Entity entity) {
		return (String) entity.getProperty(PROPERTY_NAMA);
	}

	public void updateRegister(String nama, String email, List<Integer> reminderList) throws EntityNotFoundException {
		Key activeEntityKey = KeyFactory.createKey(ENTITY_KIND, email);
		Entity entity = datastore.get(activeEntityKey);
		entity.setProperty(PROPERTY_NAMA, nama);
		entity.setProperty(PROPERTY_REMINDER_LIST, reminderList);
		datastore.put(entity);
	}

	public List<ReminderPersekutuanRegister> getAllRegisters() {

		List<ReminderPersekutuanRegister> registers = new ArrayList<ReminderPersekutuanRegister>();

		FetchOptions fetchOptions = FetchOptions.Builder.withDefaults();

		Query query = new Query(ENTITY_KIND);
		PreparedQuery preparedQuery = datastore.prepare(query);

		Iterator<Entity> resultsIterator = preparedQuery.asQueryResultList(fetchOptions).iterator();
		while (resultsIterator.hasNext()) {
			registers.add(buildReminderPersekutuanRegister(resultsIterator.next()));
		}
		return registers;
	}

	public ReminderPersekutuanRegister getReminderPersekutuanRegister(String email) throws EntityNotFoundException {

		Entity activeEntity = getActiveEntity(email);
		return buildReminderPersekutuanRegister(activeEntity);

	}

	private ReminderPersekutuanRegister buildReminderPersekutuanRegister(Entity activeEntity) {
		ReminderPersekutuanRegister register = new ReminderPersekutuanRegister(getName(activeEntity),
				activeEntity.getKey().getName());
		Map<String, Boolean> reminderListSelections = register.getReminderListSelections();
		List<Integer> reminderList = register.getReminderList();
		for (Number selection : getReminderList(activeEntity)) {
			reminderListSelections.put("selection" + selection, true);
			reminderList.add(selection.intValue());
		}
		register.setHashValue(getHashValue(activeEntity));
		return register;
	}

	public void deleteRegister(String email) throws EntityNotFoundException {
		datastore.delete(KeyFactory.createKey(ENTITY_KIND, email));
	}

}
