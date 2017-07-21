package th.ac.rmutt.comsci.studyplan.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import th.ac.rmutt.comsci.studyplan.R;

/**
 * Created by Puttapan on 17/7/2560.
 */

public class ClassroomViewHolder extends RecyclerView.ViewHolder{

    public View mView;
    TextView tvReg;
    TextView key;

    private DatabaseReference mDatabaseRegClass;
    private FirebaseAuth mAuth;

    public ClassroomViewHolder(View itemView) {
        super(itemView);

        mView = itemView;

        tvReg = (TextView) mView.findViewById(R.id.btnRegClassroom);

        mDatabaseRegClass = FirebaseDatabase.getInstance().getReference().child("RegClass");
        mAuth = FirebaseAuth.getInstance();

        mDatabaseRegClass.keepSynced(true);
    }

    public void setKey(String key){
        TextView keytool = (TextView) mView.findViewById(R.id.key);
        keytool.setText(key);
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

    public void setButtonReg (final String Classroom_key){
        mDatabaseRegClass.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(Classroom_key).hasChild(mAuth.getCurrentUser().getUid())){
                    tvReg.setText("สมัครแล้ว");
                    tvReg.setBackgroundResource(R.drawable.bg_btn_color_gray);
                    tvReg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });

                } else {
                    tvReg.setText("ลงทะเบียน");
                    tvReg.setBackgroundResource(R.drawable.bg_btn_color_green);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setImage(Context ctx, String image){

        CircularImageView classroom_image = (CircularImageView) mView.findViewById(R.id.circularClassroom);
        Picasso.with(ctx).load(image).into(classroom_image);
    }

    public void setLock(String lock){

        CircularImageView imLock = (CircularImageView) mView.findViewById(R.id.imLock);

        if(lock.equals("yes")){
            imLock.setBackgroundResource(R.color.transparent);
        }

        if(lock.equals("no")){
            imLock.setVisibility(View.GONE);
        }

    }
}
