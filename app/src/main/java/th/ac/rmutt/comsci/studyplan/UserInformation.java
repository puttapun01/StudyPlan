package th.ac.rmutt.comsci.studyplan;

public class UserInformation {

    public String stid;
    public String name;
    public String level;
    public String faculty;
    public String status;

    public UserInformation() {

    }

    public UserInformation(String stid, String name, String level,String faculty, String status) {
        this.stid = stid;
        this.name = name;
        this.level = level;
        this.faculty = faculty;
        this.status = status;
    }

}
