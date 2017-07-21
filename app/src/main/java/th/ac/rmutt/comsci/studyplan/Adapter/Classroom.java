package th.ac.rmutt.comsci.studyplan.Adapter;

/**
 * Created by Puttapan on 14/7/2560.
 */

public class Classroom {

    private String subject_id;
    private String subject_name;
    private String image;
    private String username;
    private String sec;
    private String lock;
    private String password;
    private String uid;

    public Classroom(){

    }

    public Classroom(String subject_id, String subject_name, String image, String username, String sec, String lock, String password, String uid) {
        this.subject_id = subject_id;
        this.subject_name = subject_name;
        this.image = image;
        this.username = username;
        this.sec = sec;
        this.lock = lock;
        this.password = password;
        this.uid = uid;
    }

    public String getSubject_id() {
        return subject_id;
    }

    public void setSubject_id(String subject_id) {
        this.subject_id = subject_id;
    }

    public String getSubject_name() {
        return subject_name;
    }

    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSec() {
        return sec;
    }

    public void setSec(String sec) {
        this.sec = sec;
    }

    public String getLock() {
        return lock;
    }

    public void setLock(String lock) {
        this.lock = lock;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
