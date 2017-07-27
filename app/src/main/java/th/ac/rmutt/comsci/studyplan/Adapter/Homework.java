package th.ac.rmutt.comsci.studyplan.Adapter;

/**
 * Created by Puttapan on 27/7/2560.
 */

public class Homework {

    private String h_image;
    private String h_file;
    private String h_text;
    private String uid;
    private String timestamp;
    private String datestamp;

    public Homework(){

    }

    public Homework(String h_image, String h_file, String h_text, String uid, String timestamp, String datestamp) {
        this.h_image = h_image;
        this.h_file = h_file;
        this.h_text = h_text;
        this.uid = uid;
        this.timestamp = timestamp;
        this.datestamp = datestamp;
    }

    public String getH_image() {
        return h_image;
    }

    public void setH_image(String h_image) {
        this.h_image = h_image;
    }

    public String getH_file() {
        return h_file;
    }

    public void setH_file(String h_file) {
        this.h_file = h_file;
    }

    public String getH_text() {
        return h_text;
    }

    public void setH_text(String h_text) {
        this.h_text = h_text;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDatestamp() {
        return datestamp;
    }

    public void setDatestamp(String datestamp) {
        this.datestamp = datestamp;
    }
}
