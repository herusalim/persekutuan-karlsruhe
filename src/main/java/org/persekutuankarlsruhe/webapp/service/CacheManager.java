package org.persekutuankarlsruhe.webapp.service;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;

import org.persekutuankarlsruhe.webapp.sheets.JadwalPelayanan;
import org.persekutuankarlsruhe.webapp.sheets.Orang;

import com.google.appengine.api.memcache.stdimpl.GCacheFactory;

public class CacheManager {

	private static final Logger LOG = Logger.getLogger(GoogleAPIServiceProvider.class.getName());

	private static final String DAFTAR_ORANG_KEY = "DaftarOrang";

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

	private Cache cache;
	private CacheFactory cacheFactory;
	private Map<Object, Object> cacheProperties;

	public CacheManager() {
		try {
			cacheFactory = javax.cache.CacheManager.getInstance().getCacheFactory();
			cacheProperties = new HashMap<Object, Object>();
			cacheProperties.put(GCacheFactory.EXPIRATION_DELTA, TimeUnit.MINUTES.toSeconds(10));
			cache = cacheFactory.createCache(cacheProperties);
		} catch (CacheException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void addJadwalPelayanan(JadwalPelayanan jadwalPelayanan) {
		String cacheKey = getKeyOfJadwal(jadwalPelayanan);

		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("Menyimpan cache dengan key: " + cacheKey + ", value: " + jadwalPelayanan);
		}
		cache.put(cacheKey, jadwalPelayanan);

	}

	private String getKeyOfJadwal(JadwalPelayanan jadwal) {
		return DATE_FORMAT.format(jadwal.getTanggal());
	}

	public Object getCacheValue(String key) {
		return cache.get(key);
	}

	@SuppressWarnings("unchecked")
	public List<Orang> getDaftarOrang(String sheetId) {
		return (List<Orang>) cache.get(DAFTAR_ORANG_KEY + sheetId);
	}

	@SuppressWarnings("unchecked")
	public void putDaftarOrang(String sheetId, List<Orang> daftarOrang) {
		String cacheKey = DAFTAR_ORANG_KEY + sheetId;

		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("Menyimpan cache dengan key: " + cacheKey + ", value: " + daftarOrang);
		}

		cache.put(cacheKey, daftarOrang);
	}
}
