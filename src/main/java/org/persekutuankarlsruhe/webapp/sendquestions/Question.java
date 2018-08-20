package org.persekutuankarlsruhe.webapp.sendquestions;

public class Question {
    private long timestamp;
    private String nama;
    private String text;
    private boolean showPublic;
    private long timestampSelesai;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isShowPublic() {
        return showPublic;
    }

    public void setShowPublic(boolean showPublic) {
        this.showPublic = showPublic;
    }

    public long getTimestampSelesai() {
        return timestampSelesai;
    }

    public void setTimestampSelesai(long timestampSelesai) {
        this.timestampSelesai = timestampSelesai;
    }

}
