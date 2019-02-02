package org.persekutuankarlsruhe.webapp.sheets;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.persekutuankarlsruhe.webapp.calendar.CalendarUtil;
import org.persekutuankarlsruhe.webapp.service.CacheManager;
import org.persekutuankarlsruhe.webapp.service.GoogleAPIServiceProvider;
import org.springframework.util.StringUtils;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.Sheets.Spreadsheets.Values.BatchGet;
import com.google.api.services.sheets.v4.model.BatchGetValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

public class SheetsDataProvider {

	private static final Logger LOG = Logger.getLogger(SheetsDataProvider.class.getName());

	private static final int JUMLAH_ANGGOTA_PRO_FETCH = 20;

	public static final String SHEET_NAME_JADWAL = "Jadwal";
	public static final String SHEET_NAME_EMAIL = "Email";

	private static final String HEADER_MATERI_RENUNGAN = "Materi Renungan";
	private static final String HEADER_LOKASI = "Lokasi";

	private String jadwalPelayananSpreadsheetId;
	private String anggotaSpreadSheetId;

	private Sheets service;

	private CacheManager cacheManager;

	private String rangeJadwalPelayanan;

	private String rangeDefaultLokasi;

	/**
	 * {@linkplain https://docs.google.com/spreadsheets/d/1-JxCMEywzrb3FwnIxJE_GJipNo4LmVboLv2_BwcTycE/edit}
	 */
	public static final String SPREADSHEET_ID_JADWAL_PELAYANAN = "19YlSZJtEmiqaMMbxLHkrCM8PMsODlHyuZzYCmOeduBo";
	/**
	 * {@linkplain https://docs.google.com/spreadsheets/d/1-sKoLqR3MRd8-dtkAiaOe5fy5FMOsU4zjWoA7kc7LMA/edit}
	 */
	public static final String SPREADSHEET_ID_DAFTAR_ANGGOTA = "1-sKoLqR3MRd8-dtkAiaOe5fy5FMOsU4zjWoA7kc7LMA";

	private static final String RANGE_DEFAULT_LOKASI = SHEET_NAME_JADWAL + "!H1";

	private static final String RANGE_JADWAL_PELAYANAN = SHEET_NAME_JADWAL + "!A2:H31";
	/**
	 * {@linkplain https://docs.google.com/spreadsheets/d/16qC7KZ-nAdLKtqp59uKfMg8i1R5DWU1BX2h8hlDq3FM/edit}
	 */
	public static final String SPREADSHEET_ID_JADWAL_PELAYANAN_IVENA = "16qC7KZ-nAdLKtqp59uKfMg8i1R5DWU1BX2h8hlDq3FM";
	/**
	 * {@linkplain https://docs.google.com/spreadsheets/d/1fcriIpEV8ERqRVuryP5ClRRJ7S8PRR0KEgSe_4YlMcs/edit}
	 */
	public static final String SPREADSHEET_ID_DAFTAR_ANGGOTA_IVENA = "1fcriIpEV8ERqRVuryP5ClRRJ7S8PRR0KEgSe_4YlMcs";

	private static final String RANGE_DEFAULT_LOKASI_IVENA = SHEET_NAME_JADWAL + "!L1";

	private static final String RANGE_JADWAL_PELAYANAN_IVENA = SHEET_NAME_JADWAL + "!A2:M58";

	public static SheetsDataProvider createProviderForPersekutuanIvena() {
		return new SheetsDataProvider(SPREADSHEET_ID_JADWAL_PELAYANAN_IVENA, SPREADSHEET_ID_DAFTAR_ANGGOTA_IVENA,
				RANGE_JADWAL_PELAYANAN_IVENA, RANGE_DEFAULT_LOKASI_IVENA);
	}

	public static SheetsDataProvider createProviderForPersekutuan() {
		return new SheetsDataProvider(SPREADSHEET_ID_JADWAL_PELAYANAN, SPREADSHEET_ID_DAFTAR_ANGGOTA,
				RANGE_JADWAL_PELAYANAN, RANGE_DEFAULT_LOKASI);
	}

	private SheetsDataProvider(String jadwalPelayananSpreadsheetId, String anggotaSpreadSheetId,
			String rangeJadwalPelayanan, String rangeDefaultLokasi) {
		this.jadwalPelayananSpreadsheetId = jadwalPelayananSpreadsheetId;
		this.anggotaSpreadSheetId = anggotaSpreadSheetId;
		this.rangeJadwalPelayanan = rangeJadwalPelayanan;
		this.rangeDefaultLokasi = rangeDefaultLokasi;
		List<Orang> daftarOrang;
		try {
			daftarOrang = getDaftarOrangFromSheets();

			if (LOG.isLoggable(Level.FINE)) {
				LOG.fine("Menyimpan data daftar orang (inisial) ke dalam cache: " + daftarOrang);
			}

			getCacheManager().putDaftarOrang(anggotaSpreadSheetId, daftarOrang);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	protected Sheets getService() throws IOException {
		if (service == null) {
			service = GoogleAPIServiceProvider.getSheetsService();
		}
		return service;
	}

	private ValueRange getDaftarEmailRaw(int size, int offset) throws IOException {

		int startRow = 2 + offset;
		int endRow = startRow + size - 1;
		String range = SHEET_NAME_EMAIL + "!A" + startRow + ":C" + endRow;

		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine(
					"Ambil data dari Google Sheet 'Anggota' dengan ID " + anggotaSpreadSheetId + " dan range " + range);
		}

		return getService().spreadsheets().values().get(anggotaSpreadSheetId, range).execute();

	}

	public List<Orang> getDaftarOrangFromSheets() throws IOException {
		List<Orang> daftarOrang = new ArrayList<Orang>();

		int index = 0;
		int offset = 0;
		do {
			ValueRange rawData = getDaftarEmailRaw(JUMLAH_ANGGOTA_PRO_FETCH, offset);
			List<List<Object>> rows = rawData.getValues();

			if (LOG.isLoggable(Level.FINE)) {
				LOG.fine("Mendapatkan data anggota sebanyak " + rows.size() + " entri: " + rows);
			}

			int size = (rows != null ? rows.size() : 0);
			for (index = 0; index < size; index++) {
				List<Object> row = rows.get(index);
				String nama = (String) row.get(0);
				String email = (row.size() >= 2 ? (String) row.get(1) : null);
				if (nama != null && email != null) {
					Orang orang = new Orang(nama.trim(), email.trim());
					if (row.size() >= 3) {
						String aliases = (String) row.get(2);
						for (String alias : aliases.split(",")) {
							orang.addAlias(alias.trim());
						}
					}
					daftarOrang.add(orang);
				} else {
					continue;
				}
			}
			offset += JUMLAH_ANGGOTA_PRO_FETCH;
		} while (index == JUMLAH_ANGGOTA_PRO_FETCH);

		return daftarOrang;

	}

	protected Orang createOrangDariNama(String nama, List<Orang> daftarOrang) {
		Orang petugas = cariOrang(nama, daftarOrang);
		if (petugas == null) {
			petugas = new Orang(nama, "");
		}

		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("Mendapatkan petugas " + petugas + " dari nama " + nama + " berdasarkan daftar orang berikut: "
					+ daftarOrang);
		}

		return petugas;
	}

	@SuppressWarnings("unlikely-arg-type") // equals of Orang supports String as Parameter
	private Orang cariOrang(String nama, List<Orang> daftarOrang) {
		for (Orang orang : daftarOrang) {
			if (orang.equals(nama)) {
				return orang;
			}
		}
		return null;
	}

	protected List<Orang> getDaftarOrang() throws IOException {
		List<Orang> daftarOrang = getCacheManager().getDaftarOrang(anggotaSpreadSheetId);
		if (daftarOrang == null) {
			daftarOrang = getDaftarOrangFromSheets();

			if (LOG.isLoggable(Level.FINE)) {
				LOG.fine("Menyimpan data daftar orang ke dalam cache: " + daftarOrang);
			}

			getCacheManager().putDaftarOrang(anggotaSpreadSheetId, daftarOrang);
		}
		return daftarOrang;
	}

	public List<JadwalPelayanan> getDaftarJadwalPelayananFromSheets() throws IOException {

		List<ValueRange> rawDataList = getDaftarJadwalPelayananRaw();

		String defaultLokasi = getDefaultLokasi(rawDataList);

		List<JadwalPelayanan> daftarJadwalPelayanan = extractJadwalPelayanan(rawDataList, defaultLokasi, true);

		for (ValueRange rawData : rawDataList) {
			if (rangeJadwalPelayanan.equals(rawData.getRange())) {
			} else if (rangeDefaultLokasi.equals(rawData.getRange())) {

			}
		}

		return daftarJadwalPelayanan;
	}

	public List<JadwalPelayanan> getDaftarJadwalPelayananFromSheetsFromBeginning() throws IOException {

		List<ValueRange> rawDataList = getDaftarJadwalPelayananRaw();

		String defaultLokasi = getDefaultLokasi(rawDataList);

		List<JadwalPelayanan> daftarJadwalPelayanan = extractJadwalPelayanan(rawDataList, defaultLokasi, false);
		for (ValueRange rawData : rawDataList) {
			if (rangeJadwalPelayanan.equals(rawData.getRange())) {
			} else if (rangeDefaultLokasi.equals(rawData.getRange())) {

			}
		}

		return daftarJadwalPelayanan;
	}

	protected List<JadwalPelayanan> extractJadwalPelayanan(List<ValueRange> rawDataList, String defaultLokasi,
			boolean futureOnly) throws IOException {
		List<JadwalPelayanan> daftarJadwalPelayanan = new ArrayList<JadwalPelayanan>();
		for (ValueRange rawData : rawDataList) {
			if (rangeJadwalPelayanan.equals(rawData.getRange())) {
				List<List<Object>> rows = rawData.getValues();
				if (LOG.isLoggable(Level.FINE)) {
					LOG.fine("Mendapatkan data jadwal pelayanan sebanyak " + rows.size() + " entri: " + rows);
				}

				List<Object> headerRow = rows.get(0);
				int indexMateriRenungan = -1;
				int indexLokasi = -1;
				int rowIndex = 0;
				for (Object headerColumn : headerRow) {
					if (HEADER_MATERI_RENUNGAN.equalsIgnoreCase(headerColumn.toString().trim())) {
						indexMateriRenungan = rowIndex;
					} else if (HEADER_LOKASI.equalsIgnoreCase(headerColumn.toString().trim())) {
						indexLokasi = rowIndex;
					}

					rowIndex++;
				}

				List<Orang> daftarOrang = getDaftarOrang();
				LOG.info(daftarOrang.toString());
				Calendar now = Calendar.getInstance(CalendarUtil.getTimeZone());

				for (int i = 1; i < rows.size(); i++) {
					List<Object> row = rows.get(i);

					if (row.size() > 0) {

						Object fieldTanggal = row.get(0);
						if (fieldTanggal instanceof BigDecimal) {
							// valid entry
							JadwalPelayanan jadwalPelayanan = createJadwalPelayanan(row, headerRow, indexMateriRenungan,
									indexLokasi, daftarOrang, defaultLokasi);
							String bahanRenungan = jadwalPelayanan.getBahanRenungan();

							if (!futureOnly
									|| CalendarUtil.getJadwalTimeValue(jadwalPelayanan) > now.getTimeInMillis()) {
								if ((!futureOnly || jadwalPelayanan.getTanggal().after(now.getTime()))
										&& !StringUtils.isEmpty(bahanRenungan)) {
									if (LOG.isLoggable(Level.FINE)) {
										LOG.fine("Menambahkan jadwal berikut ke daftar: " + jadwalPelayanan);
									}
									daftarJadwalPelayanan.add(jadwalPelayanan);
								} else {
									LOG.info("Entri " + row + " diabaikan, karena bahan renungan masih kosong");
								}
							}
						} else if (!StringUtils.isEmpty(fieldTanggal)) { // Abaikan
																			// empty
																			// string
							LOG.warning("Ditemukan entri fieldTanggal invalid: \"" + fieldTanggal + "\" ("
									+ fieldTanggal.getClass() + ")");
						}
					}
				}
			}
		}
		return daftarJadwalPelayanan;
	}

	private JadwalPelayanan createJadwalPelayanan(List<Object> row, List<Object> headerRow, int indexMateriRenungan,
			int indexLokasi, List<Orang> daftarOrang, String defaultLokasi) throws IOException {
		JadwalPelayanan jadwalPelayanan = new JadwalPelayanan((BigDecimal) row.get(0));
		for (int columnIndex = 1; columnIndex < row.size(); columnIndex++) {
			if (columnIndex == indexMateriRenungan) {
				jadwalPelayanan.setBahanRenungan(row.get(columnIndex).toString());
			} else if (columnIndex == indexLokasi) {
				jadwalPelayanan.setLokasi(row.get(columnIndex).toString());
			} else {
				String columnName = headerRow.get(columnIndex).toString();
				Pelayanan jenisPelayanan = ubahKeJenisPelayanan(columnName);

				// nama orang bisa dipisah dengan "&" ataupun ","
				for (String namaRaw : row.get(columnIndex).toString().split("&")) {
					for (String nama : namaRaw.split(",")) {
						jadwalPelayanan.tambahPetugas(jenisPelayanan, createOrangDariNama(nama.trim(), daftarOrang));
					}
				}
			}
		}

		if (StringUtils.isEmpty(jadwalPelayanan.getLokasi())) {
			jadwalPelayanan.setLokasi(defaultLokasi);
		}

		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("Berhasil mem-parsing data jadwal pelayanan: " + jadwalPelayanan);
		}

		return jadwalPelayanan;
	}

	private Pelayanan ubahKeJenisPelayanan(String value) {
		return Pelayanan.fromName(value);
	}

	private String getDefaultLokasi(List<ValueRange> rawDataList) {
		String defaultLokasi = "";

		for (ValueRange rawData : rawDataList) {
			if (rangeDefaultLokasi.equals(rawData.getRange())) {
				final int ROW_NUMBER = 0;
				final int COLUMN_NUMBER = 0;
				defaultLokasi = (String) rawData.getValues().get(ROW_NUMBER).get(COLUMN_NUMBER);
			}
		}
		return defaultLokasi;
	}

	protected List<ValueRange> getDaftarJadwalPelayananRaw() throws IOException {

		List<String> rangeList = Arrays.asList(rangeJadwalPelayanan, rangeDefaultLokasi);
		// pelayanan maksimal 30 entri
		BatchGet batchGet = getService().spreadsheets().values().batchGet(getJadwalPelayananSpreadsheetId());
		batchGet.setRanges(rangeList);
		batchGet.setValueRenderOption(SheetsValueRenderOption.UNFORMATTED_VALUE.getValue());
		batchGet.setDateTimeRenderOption(SheetsDateTimeRenderOption.FORMATTED_STRING.getValue());
		BatchGetValuesResponse response = batchGet.execute();
		// Selalu mengharapkan single result, karena meskipun batch, kita hanya
		// mengambil satu block dari range

		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("Ambil data dari Google Sheet 'Jadwal Pelayanan' dengan ID " + getJadwalPelayananSpreadsheetId()
					+ " dan range " + rangeJadwalPelayanan);
		}

		return response.getValueRanges();
	}

	private CacheManager getCacheManager() {
		if (cacheManager == null) {
			cacheManager = new CacheManager();
		}
		return cacheManager;
	}

	protected String getJadwalPelayananSpreadsheetId() {
		return jadwalPelayananSpreadsheetId;
	}

}
