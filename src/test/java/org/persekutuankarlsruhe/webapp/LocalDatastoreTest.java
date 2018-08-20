package org.persekutuankarlsruhe.webapp;

import java.util.Calendar;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.persekutuankarlsruhe.webapp.remindpelayanan.ReminderPelayananDatastore;
import org.persekutuankarlsruhe.webapp.remindpelayanan.ReminderType;
import org.persekutuankarlsruhe.webapp.sheets.JadwalPelayanan;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class LocalDatastoreTest {

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  @Before
  public void setUp() {
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  // Run this test twice to prove we're not leaking any state across tests.
  private void doTest() {
//    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
//    assertEquals(0, ds.prepare(new Query("yam")).countEntities(withLimit(10)));
//    ds.put(new Entity("yam"));
//    ds.put(new Entity("yam"));
//    assertEquals(2, ds.prepare(new Query("yam")).countEntities(withLimit(10)));
  }

  @Test
  public void testInsert1() {
//    doTest();
		ReminderPelayananDatastore datastore = ReminderPelayananDatastore.getInstance();
		
		Calendar calendar = Calendar.getInstance();
		
		calendar.add(Calendar.HOUR_OF_DAY, -24);
		
		datastore.addReminderEntity(new JadwalPelayanan(calendar.getTime()), "dummy", ReminderType.FIRST);
		
		calendar.add(Calendar.HOUR_OF_DAY, 72);
		
		datastore.addReminderEntity(new JadwalPelayanan(calendar.getTime()), "dummy", ReminderType.FIRST);
		
		datastore.removeExpiredEntities(new Date());
  }

  @Test
  public void testInsert2() {
//    doTest();
  }
}