package th.ac.rmutt.comsci.studyplan;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

/**
 * Created by Puttapan on 17/7/2560.
 */

public class ClassroomViewHolder extends RecyclerView.ViewHolder{

    public View mView;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseUser;

    public ClassroomViewHolder(View itemView) {
        super(itemView);
        mView = itemView;

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());



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

    public void setUid(String uid){

        TextView tvReg = (TextView) mView.findViewById(R.id.btnRegClassroom);
        String u = mDatabaseUser.toString();

        if(uid.equals(u)) {
            tvReg.setVisibility(View.GONE);
        }
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
            imLock.setVisibility(View.GONE);
        }

    }
}
