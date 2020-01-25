package org.persekutuankarlsruhe.webapp.sheets;

import org.persekutuankarlsruhe.webapp.calendar.CalendarUtil;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

public class JadwalPelayanan implements Serializable {

    private static final int DEFAULT_JAM_MULAI_PERSEKUTUAN = 10;

    /**
     *
     */
    private static final long serialVersionUID = 4526458381387539869L;

    /**
     * Instructs date, time, datetime, and duration fields to be output as
     * doubles in "serial number" format, as popularized by Lotus 1-2-3. Days
     * are counted from December 31st 1899 and are incremented by 1, and times
     * are fractions of a day. For example, January 1st 1900 at noon would be
     * 1.5, 1 because it's 1 day offset from December 31st 1899, and .5 because
     * noon is half a day. February 1st 1900 at 3pm would be 32.625. This
     * correctly treats the year 1900 as not a leap year.
     */
    private static final Calendar KALENDER_REFERENSI;

    static {
        KALENDER_REFERENSI = Calendar.getInstance(CalendarUtil.getTimeZone());
        KALENDER_REFERENSI.set(1899, 11, 31);
    }

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);

    private Date tanggal;
    private Map<Pelayanan, List<Orang>> daftarPelayanan = new HashMap<Pelayanan, List<Orang>>();
    private String bahanRenungan;
    private String lokasi;

    /**
     * Constructor JadwalPelayanan
     *
     * @param tanggal Tanggal persekutuan (see
     *                {@link JadwalPelayanan#KALENDER_REFERENSI})
     */
    public JadwalPelayanan(BigDecimal tanggal) {
        Calendar kalender = (Calendar) KALENDER_REFERENSI.clone();
        kalender.add(Calendar.DAY_OF_MONTH, tanggal.intValue() - 1);
        setKalender(kalender);
    }

    public JadwalPelayanan(String tanggal) {
        Calendar kalender = Calendar.getInstance();
        try {
            kalender.setTime(DATE_FORMAT.parse(tanggal));// all done
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid tanggal: " + tanggal, e);
        }
        setKalender(kalender);
    }

    private void setKalender(Calendar kalender) {
        setJamPersekutuan(kalender);
        this.tanggal = kalender.getTime();
    }

    private void setJamPersekutuan(Calendar kalender) {
        kalender.set(Calendar.HOUR_OF_DAY, DEFAULT_JAM_MULAI_PERSEKUTUAN);
        kalender.set(Calendar.MINUTE, 0);
        kalender.set(Calendar.SECOND, 0);
        kalender.set(Calendar.MILLISECOND, 0);
    }

    /**
     * Constructor dengan tanggal dengan format serial number
     *
     * @param tanggal Tanggal persekutuan
     */
    public JadwalPelayanan(Date tanggal) {
        this.tanggal = tanggal;
    }

    /**
     * Menambahkan petugas ke daftar pelayanan
     *
     * @param pelayanan Jenis pelayanan
     * @param petugas   petugas
     */
    public void tambahPetugas(Pelayanan pelayanan, Orang petugas) {
        List<Orang> daftarPetugas = daftarPelayanan.get(pelayanan);
        if (daftarPetugas == null) {
            daftarPetugas = new ArrayList<Orang>();
        }
        daftarPetugas.add(petugas);
        daftarPelayanan.put(pelayanan, daftarPetugas);
    }

    /**
     * Mengambil daftar petugas dari pelayanan tertentu
     *
     * @param pelayanan Jenis pelayanan
     * @return List dari petugas, atau <code>null</code> kalau tidak ditemukan
     */
    public List<Orang> getPetugas(Pelayanan pelayanan) {
        return daftarPelayanan.get(pelayanan);
    }

    /**
     * @return semua daftar pelayanan
     */
    public Set<Entry<Pelayanan, List<Orang>>> listPelayanan() {
        return daftarPelayanan.entrySet();
    }

    public Date getTanggal() {
        return this.tanggal;
    }

    /**
     * Memberikan nama bahan renungan
     *
     * @param bahanRenungan Tema/bahan renungan
     */
    public void setBahanRenungan(String bahanRenungan) {
        this.bahanRenungan = bahanRenungan;
    }

    /**
     * @return Bahan renungan
     */
    public String getBahanRenungan() {
        return this.bahanRenungan;
    }

    public Map<Pelayanan, List<Orang>> getDaftarPelayanan() {
        return this.daftarPelayanan;
    }

    public String toString() {
        String daftarPelayananString = "Pelayanan: ";
        for (Entry<Pelayanan, List<Orang>> entry : daftarPelayanan.entrySet()) {
            Pelayanan pelayanan = entry.getKey();
            daftarPelayananString += (pelayanan == null ? "" : pelayanan.getNama()) + " [";
            int index = 0;
            for (Orang orang : entry.getValue()) {
                if (index++ > 0) {
                    daftarPelayananString += ", ";
                }
                daftarPelayananString += orang.getNama();
            }
            daftarPelayananString += "]; ";
        }
        return new SimpleDateFormat().format(tanggal) + "\t" + "Bahan Renungan: " + bahanRenungan + "\t"
                + daftarPelayananString + "Lokasi: " + getLokasi();
    }

    public List<Orang> getMc() {
        return getPetugas(Pelayanan.MC);
    }

    public List<Orang> getPemimpinRenungan() {
        return getPetugas(Pelayanan.PEMIMPIN_RENUNGAN);
    }

    public List<Orang> getSekolahMinggu() {
        return getPetugas(Pelayanan.SM);
    }

    public List<Orang> getMusik() {
        return getPetugas(Pelayanan.MUSIK);
    }

    public List<Orang> getMasak() {
        return getPetugas(Pelayanan.MASAK);
    }

    public String getLokasi() {
        return this.lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }
}
