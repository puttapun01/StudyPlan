package th.ac.rmutt.comsci.studyplan.Adapter;

/**
 * Created by Puttapan on 20/7/2560.
 */

public class AllUser {

    private String image;
    private String faculty;
    private String level;
    private String name;
    private String status;
    private String status_id;
    private String stid;

    public AllUser(){

    }

    public AllUser(String image, String faculty, String level, String name, String status, String status_id, String stid) {
        this.image = image;
        this.faculty = faculty;
        this.level = level;
        this.name = name;
        this.status = status;
        this.status_id = status_id;
        this.stid = stid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus_id() {
        return status_id;
    }

    public void setStatus_id(String status_id) {
        this.status_id = status_id;
    }

    public String getStid() {
        return stid;
    }

    public void setStid(String stid) {
        this.stid = stid;
    }
}
