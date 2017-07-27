package th.ac.rmutt.comsci.studyplan.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import th.ac.rmutt.comsci.studyplan.R;

/**
 * Created by Puttapan on 26/7/2560.
 */

public class CommentViewHolder extends RecyclerView.ViewHolder {

    public View mView;

    private ImageView btnEditComment;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    private String current_uid;

    public CommentViewHolder(View itemView) {
        super(itemView);

        mView = itemView;

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        current_uid = mCurrentUser.getUid();

    }

    public void setText_comment(String text_comment) {
        TextView tvComment = (TextView) mView.findViewById(R.id.tvComment);
        tvComment.setText(text_comment);
    }

    public void setUid(String uid) {

    }

    public void setImageProfile(Context ctx, String imageProfile){
        ImageView ivProfile = (ImageView) mView.findViewById(R.id.ivProfile);
        Picasso.with(ctx).load(imageProfile).into(ivProfile);
    }

    public void setUsername(String username){
        TextView tvName = (TextView) mView.findViewById(R.id.tvName);
        tvName.setText(username);
    }

    public void setTimestamp(String timestamp) {
        TextView tvTimeStamp = (TextView) mView.findViewById(R.id.tvTimeStamp);
        tvTimeStamp.setText(timestamp);
    }


    public void setDatestamp(String datestamp) {
        TextView tvDateStamp = (TextView) mView.findViewById(R.id.tvDateStamp);
        tvDateStamp.setText(datestamp);
    }

    public void setEditComment(String uid){

        btnEditComment = (ImageView) mView.findViewById(R.id.btnEditComment);

        if(uid.equals(current_uid)){
            btnEditComment.setVisibility(View.VISIBLE);
        }

        if(!uid.equals(current_uid)){
            btnEditComment.setVisibility(View.GONE);
        }

    }

}
