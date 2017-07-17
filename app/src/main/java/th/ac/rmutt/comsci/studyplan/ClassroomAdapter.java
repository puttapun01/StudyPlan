package th.ac.rmutt.comsci.studyplan;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Puttapan on 17/7/2560.
 */

public class ClassroomAdapter extends FirebaseRecyclerAdapter<Classroom, ClassroomViewHolder> {

    public ClassroomAdapter(Query ref) {
        super(Classroom.class, R.layout.view_classroom, ClassroomViewHolder.class, ref);
    }

    @Override
    protected void populateViewHolder(ClassroomViewHolder viewHolder, Classroom model, int position) {
        viewHolder.setSubject_id(model.getSubject_id());
        viewHolder.setSubject_name(model.getSubject_name());
        viewHolder.setSec(model.getSec());
        viewHolder.setUsername(model.getUsername());
        viewHolder.setImage(getApplicationContext(), model.getImage());
        viewHolder.setLock(model.getLock());
    }
}
