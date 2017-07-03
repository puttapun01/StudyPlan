package th.ac.rmutt.comsci.studyplan.Activity;

/**
 * Created by Puttapan on 29/6/2560.
 */

public class PlanBlog {

    private String ic, time, type, detail;

    public PlanBlog(){

    }

    public PlanBlog(String ic, String time, String type, String detail) {
        this.ic = ic;
        this.time = time;
        this.type = type;
        this.detail = detail;
    }

    public String getIc() {
        return ic;
    }

    public void setIc(String ic) {
        this.ic = ic;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
