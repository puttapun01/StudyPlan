package th.ac.rmutt.comsci.studyplan.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import th.ac.rmutt.comsci.studyplan.R;

/**
 * Created by Puttapan on 25/7/2560.
 */

public class StatusClassroomViewHolder extends RecyclerView.ViewHolder{

    public View mView;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseLike;

    private ImageView ivLike;
    private TextView tvLike;
    private LinearLayout btnLike;

    private LinearLayout llEditPost;
    private ImageView ivEditPost;

    private String current_uid;

    public StatusClassroomViewHolder(View itemView) {
        super(itemView);

        mView = itemView;

        ivLike = (ImageView) mView.findViewById(R.id.ivLike);
        tvLike = (TextView) mView.findViewById(R.id.tvLike);
        btnLike = (LinearLayout) mView.findViewById(R.id.btnLike);
        llEditPost = (LinearLayout) mView.findViewById(R.id.llEditPost);
        ivEditPost = (ImageView) mView.findViewById(R.id.ivEditPost);

        mView = itemView;
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        current_uid = mCurrentUser.getUid();
        mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");

        mDatabaseLike.keepSynced(true);
    }

    public void setDate(String date) {
        TextView tvDate = (TextView) mView.findViewById(R.id.tvDate);

        if(date.equals("null")){
            tvDate.setVisibility(View.GONE);
        }
        if(!date.equals("null")){
            tvDate.setVisibility(View.VISIBLE);
            tvDate.setText(date);
        }

    }

    public void setDatestamp(String datestamp) {
        TextView tvDateStamp = (TextView) mView.findViewById(R.id.tvDateStamp);
        tvDateStamp.setText(datestamp);
    }

    public void setNumLike(String numLike) {
        TextView tvNumLike = (TextView) mView.findViewById(R.id.tvNumLike);
        tvNumLike.setText(numLike);
    }

    public void setNumComment(String numComment) {
        TextView tvNumComment = (TextView) mView.findViewById(R.id.tvNumComment);
        tvNumComment.setText(numComment);
    }

    public void setNumHomework (String numHomework) {
        TextView tvNumHomework = (TextView) mView.findViewById(R.id.tvNumHomework);
        tvNumHomework.setText(numHomework);
    }

    public void setUsername(String username){
        TextView tvPostUsername = (TextView) mView.findViewById(R.id.tvPostUsername);
        tvPostUsername.setText(username);
    }

    public void setImageProfile(Context ctx, String imageProfile){
        ImageView imProfile = (ImageView) mView.findViewById(R.id.imProfile);
        Picasso.with(ctx).load(imageProfile).into(imProfile);
    }

    public void setFile(String file) {

        LinearLayout llFile = (LinearLayout) mView.findViewById(R.id.llFile);

        if(file.equals("null")){
            llFile.setVisibility(View.GONE);
        }

        if(!file.equals("null")){
            llFile.setVisibility(View.VISIBLE);
        }
    }

    public void setImage(Context ctx, String image) {
        ImageView ivImage = (ImageView) mView.findViewById(R.id.ivImage);

        if(image.equals("null")){
            ivImage.setVisibility(View.GONE);
        }

        if(!image.equals("null")){
            ivImage.setVisibility(View.VISIBLE);
            Picasso.with(ctx).load(image).into(ivImage);
        }
    }

    public void setSwitchDate(String switchDate) {

        LinearLayout llSwitchDate = (LinearLayout) mView.findViewById(R.id.llSwitchDate);
        TextView tvZZ1 = (TextView) mView.findViewById(R.id.tvZZ1);

        if(switchDate.equals("off")){
            llSwitchDate.setVisibility(View.GONE);
            tvZZ1.setVisibility(View.GONE);
        }

        if(switchDate.equals("on")){
            llSwitchDate.setVisibility(View.VISIBLE);
            tvZZ1.setVisibility(View.VISIBLE);
        }
    }

    public void setText(String text) {
        TextView tvText = (TextView) mView.findViewById(R.id.tvText);
        tvText.setText(text);
    }

    public void setTime(String time) {
        TextView tvTime = (TextView) mView.findViewById(R.id.tvTime);

        if(time.equals("null")){
            tvTime.setVisibility(View.GONE);
        }

        if(!time.equals("null")){
            tvTime.setVisibility(View.VISIBLE);
            tvTime.setText(time);
        }

    }

    public void setTimestamp(String timestamp) {
        TextView tvTimeStamp = (TextView) mView.findViewById(R.id.tvTimeStamp);
        tvTimeStamp.setText(timestamp);
    }

    public void setTimestamp2(String timestamp2) {
    }

    public void setUid(String uid) {
    }

    public void setCoutLike(String like){
        LinearLayout llCountLike = (LinearLayout) mView.findViewById(R.id.llCountLike);
        llCountLike.setVisibility(View.GONE);
    }

    public void setBtnLike(final String post_key){

        mDatabaseLike.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(post_key).hasChild(current_uid)){
                    ivLike.setColorFilter(Color.parseColor("#43A047"));
                    tvLike.setTextColor(Color.parseColor("#43A047"));
                } else {
                    ivLike.setColorFilter(Color.parseColor("#7c7c7c"));
                    tvLike.setTextColor(Color.parseColor("#7c7c7c"));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void setPostSelect(String uid){

        if(uid.equals(current_uid)){
            llEditPost.setVisibility(View.VISIBLE);
            ivEditPost.setVisibility(View.VISIBLE);
        }

        if(!uid.equals(current_uid)){
            llEditPost.setVisibility(View.GONE);
            ivEditPost.setVisibility(View.GONE);
        }

    }

}
