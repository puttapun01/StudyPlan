package th.ac.rmutt.comsci.studyplan.Adapter;

/**
 * Created by Puttapan on 26/7/2560.
 */

public class Comment {

    private String text_comment;
    private String uid;
    private String timestamp;
    private String datestamp;

    public Comment(){

    }

    public Comment(String text_comment, String uid, String timestamp, String datestamp) {
        this.text_comment = text_comment;
        this.uid = uid;
        this.timestamp = timestamp;
        this.datestamp = datestamp;
    }

    public String getText_comment() {
        return text_comment;
    }

    public void setText_comment(String text_comment) {
        this.text_comment = text_comment;
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
