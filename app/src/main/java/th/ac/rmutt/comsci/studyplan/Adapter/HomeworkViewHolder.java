package th.ac.rmutt.comsci.studyplan.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import th.ac.rmutt.comsci.studyplan.R;

/**
 * Created by Puttapan on 27/7/2560.
 */

public class HomeworkViewHolder extends RecyclerView.ViewHolder {

    public View mView;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    private String current_uid;

    public HomeworkViewHolder(View itemView) {
        super(itemView);

        mView = itemView;

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        current_uid = mCurrentUser.getUid();
    }

    public void setH_image(String h_image) {

        LinearLayout llImage = (LinearLayout) mView.findViewById(R.id.llImage);

        if(h_image.equals("null")){
            llImage.setVisibility(View.GONE);
        }

        if(!h_image.equals("null")){
            llImage.setVisibility(View.VISIBLE);
        }

    }

    public void setH_file(String h_file) {

        LinearLayout llFile = (LinearLayout) mView.findViewById(R.id.llFile);

        if(h_file.equals("null")){
            llFile.setVisibility(View.GONE);
        }

        if(!h_file.equals("null")){
            llFile.setVisibility(View.VISIBLE);
        }

    }

    public void setH_text(String h_text) {
        TextView tvText = (TextView) mView.findViewById(R.id.tvText);
        tvText.setText(h_text);

    }

    public void setImageProfile(Context ctx, String imageProfile){
        ImageView imProfile = (ImageView) mView.findViewById(R.id.imProfile);
        Picasso.with(ctx).load(imageProfile).into(imProfile);
    }

    public void setUsername(String username){
        TextView tvPostUsername = (TextView) mView.findViewById(R.id.tvPostUsername);
        tvPostUsername.setText(username);
    }

    public void setStid (String stid){
        TextView tvStudentId = (TextView) mView.findViewById(R.id.tvStudentId);
        tvStudentId.setText(stid);
    }

    public void setUid(String uid) {

    }

    public void setTimestamp(String timestamp) {
        TextView tvTime = (TextView) mView.findViewById(R.id.tvTime);
        tvTime.setText(timestamp);

    }

    public void setDatestamp(String datestamp) {
        TextView tvDate = (TextView) mView.findViewById(R.id.tvDate);
        tvDate.setText(datestamp);
    }
}
