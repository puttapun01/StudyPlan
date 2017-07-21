package th.ac.rmutt.comsci.studyplan.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import th.ac.rmutt.comsci.studyplan.R;

/**
 * Created by Puttapan on 20/7/2560.
 */

public class AllUserViewHolder extends RecyclerView.ViewHolder{

    public View mView;

    public AllUserViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void setImage(Context ctx, String image) {
        CircularImageView classroom_image = (CircularImageView) mView.findViewById(R.id.circularClassroom);
        Picasso.with(ctx).load(image).into(classroom_image);
    }

    public void setFaculty(String faculty) {

    }

    public void setLevel(String level) {

    }

    public void setName(String name) {
        TextView tvName = (TextView) mView.findViewById(R.id.tvAuthName);
        tvName.setText(name);
    }

    public void setStatus(String status) {
        TextView tvStatus = (TextView) mView.findViewById(R.id.tvStatus);
        tvStatus.setText(status);
    }

    public void setStatus_id(String status_id) {
        LinearLayout layoutStatus = (LinearLayout) mView.findViewById(R.id.layoutStatus);

        if(status_id.equals("student")){
            layoutStatus.setBackgroundResource(R.color.st_green);
        }

        if(status_id.equals("teacher")){
            layoutStatus.setBackgroundResource(R.color.st_blue);
        }

        if(status_id.equals("null")) {
            layoutStatus.setBackgroundResource(R.color.red);
        }
    }

    public void setStid(String stid) {
        TextView tvStid = (TextView) mView.findViewById(R.id.tvUserId);
        tvStid.setText(stid);
    }

    public void setShow() {
        mView.setVisibility(View.VISIBLE);
    }

    public void setHide() {
        CircularImageView classroom_image = (CircularImageView) mView.findViewById(R.id.circularClassroom);
        TextView tvName = (TextView) mView.findViewById(R.id.tvAuthName);
        TextView tvStid = (TextView) mView.findViewById(R.id.tvUserId);
        TextView tvStatus = (TextView) mView.findViewById(R.id.tvStatus);
        TextView zz = (TextView) mView.findViewById(R.id.zz);
        LinearLayout layoutStatus = (LinearLayout) mView.findViewById(R.id.layoutStatus);
        LinearLayout dd = (LinearLayout) mView.findViewById(R.id.dd);
        LinearLayout zz2 = (LinearLayout) mView.findViewById(R.id.zz2);
        LinearLayout zz3 = (LinearLayout) mView.findViewById(R.id.zz3);
        LinearLayout zz5 = (LinearLayout) mView.findViewById(R.id.zz5);
        LinearLayout zz6 = (LinearLayout) mView.findViewById(R.id.zz6);
        FrameLayout zz4 = (FrameLayout) mView.findViewById(R.id.zz4);

        classroom_image.setVisibility(View.GONE);
        tvName.setVisibility(View.GONE);
        tvStid.setVisibility(View.GONE);
        tvStatus.setVisibility(View.GONE);
        layoutStatus.setVisibility(View.GONE);
        zz.setVisibility(View.GONE);
        zz2.setVisibility(View.GONE);
        zz3.setVisibility(View.GONE);
        zz4.setVisibility(View.GONE);
        zz5.setVisibility(View.GONE);
        zz6.setVisibility(View.GONE);
        dd.setVisibility(View.GONE);

        mView.setVisibility(View.GONE);

    }

}
