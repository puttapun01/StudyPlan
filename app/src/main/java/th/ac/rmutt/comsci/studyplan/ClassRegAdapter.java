package th.ac.rmutt.comsci.studyplan;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Puttapan on 16/7/2560.
 */

public class ClassRegAdapter extends RecyclerView.Adapter<ClassRegAdapter.ClassroomViewHolder> {

    private String[] mDatabase;

    private String subject_id;
    private String subject_name;
    private String image;
    private String username;
    private String sec;
    private String lock;
    private String password;
    private String uid;

    public ClassRegAdapter(String[] mDatabase, String subject_id, String subject_name, String image, String username, String sec, String lock, String password, String uid) {
        this.mDatabase = mDatabase;
        this.subject_id = subject_id;
        this.subject_name = subject_name;
        this.image = image;
        this.username = username;
        this.sec = sec;
        this.lock = lock;
        this.password = password;
        this.uid = uid;
    }

    public String[] getmDatabase() {
        return mDatabase;
    }

    public void setmDatabase(String[] mDatabase) {
        this.mDatabase = mDatabase;
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


    public static class ClassroomViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ClassroomViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setSubject_id(String subject_id){
            TextView tv_subject_id = (TextView) mView.findViewById(R.id.tv_subject_id);
            tv_subject_id.setText(subject_id);
        }

        public void setSubject_name(String subject_name){
            TextView tv_subject_name = (TextView) mView.findViewById(R.id.tv_subject_name);
            tv_subject_name.setText(subject_name);
        }

        public void setSec(String sec){
            TextView tv_subject_sec = (TextView) mView.findViewById(R.id.tv_subject_sec);
            tv_subject_sec.setText(sec);
        }

        public void setUsername(String username){
            TextView tvUsername = (TextView) mView.findViewById(R.id.tvUsername);
            tvUsername.setText(username);
        }

        public void setImage(Context ctx, String image){

            CircularImageView classroom_image = (CircularImageView) mView.findViewById(R.id.circularClassroom);
            Picasso.with(ctx).load(image).into(classroom_image);
        }

        public void setLock(String lock){
            ImageView imLock = (ImageView) mView.findViewById(R.id.imLock);

            if(lock.equals("yes")){
                imLock.setImageResource(R.drawable.ic_lock_2);
            }

            if(lock.equals("no")){
                imLock.setImageResource(R.drawable.ic_lock);
            }

        }

    }

    public ClassRegAdapter(String[] myDataset){
        mDatabase = myDataset;
    }

    @Override
    public ClassRegAdapter.ClassroomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_classroom_reg, parent, false);
        ClassroomViewHolder vh = new ClassroomViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ClassroomViewHolder holder, int position) {
        holder.setSubject_id(mDatabase[position]);
        holder.setSubject_name(mDatabase[position]);
        holder.setSec(mDatabase[position]);
        holder.setUsername(mDatabase[position]);
        holder.setImage(getApplicationContext(), mDatabase[position]);
        holder.setLock(mDatabase[position]);
    }

    @Override
    public int getItemCount() {
        return mDatabase.length;
    }
}
