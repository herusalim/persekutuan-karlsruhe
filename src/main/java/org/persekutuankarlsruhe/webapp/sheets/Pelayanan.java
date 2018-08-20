package org.persekutuankarlsruhe.webapp.sheets;

public enum Pelayanan {
    PEMIMPIN_RENUNGAN("Pemimpin Renungan"), MC("MC"), MUSIK("Musik"), PEMUSIK("Pemusik"), SM("Sekolah Minggu"), MASAK("Masak"), USHER(
            "Usher/Kolektan"), AV("AV"), PA_ANAK_BESAR("PA anak Kelas Besar"), PA_ANAK_KECIL(
                    "PA anak Kelas Kecil"), RINGKASAN_KHOTBAH("Ringkasan Kotbah"), KONSUMSI(
                            "Konsumsi (Akhir Bulan)"), MC_BIRTHDAY("MC Birthday (akhir bulan)");

    String nama;

    private Pelayanan(String nama) {
        this.nama = nama;
    }

    public static Pelayanan fromName(String nama) {
        for (Pelayanan pelayanan : Pelayanan.values()) {
            if (pelayanan.nama.equalsIgnoreCase(nama)) {
                return pelayanan;
            }
        }
        return null;
    }

    public String getNama() {
        return nama;
    }
}
