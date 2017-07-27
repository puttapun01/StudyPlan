package th.ac.rmutt.comsci.studyplan.Adapter;

/**
 * Created by Puttapan on 25/7/2560.
 */

public class StatusClassroom {

    private String classroom;
    private String date;
    private String datestamp;
    private String file;
    private String image;
    private String switchDate;
    private String text;
    private String time;
    private String timestamp;
    private String timestamp2;
    private String uid;

    public StatusClassroom(){

    }

    public StatusClassroom(String classroom, String date, String datestamp, String file, String image, String switchDate, String text, String time, String timestamp, String timestamp2, String uid) {
        this.classroom = classroom;
        this.date = date;
        this.datestamp = datestamp;
        this.file = file;
        this.image = image;
        this.switchDate = switchDate;
        this.text = text;
        this.time = time;
        this.timestamp = timestamp;
        this.timestamp2 = timestamp2;
        this.uid = uid;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDatestamp() {
        return datestamp;
    }

    public void setDatestamp(String datestamp) {
        this.datestamp = datestamp;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSwitchDate() {
        return switchDate;
    }

    public void setSwitchDate(String switchDate) {
        this.switchDate = switchDate;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimestamp2() {
        return timestamp2;
    }

    public void setTimestamp2(String timestamp2) {
        this.timestamp2 = timestamp2;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
