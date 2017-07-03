package th.ac.rmutt.comsci.studyplan.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import th.ac.rmutt.comsci.studyplan.R;

public class PlanActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView btnAddPlan;

    private RecyclerView mBlogList;

    private DatabaseReference mDatabase;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    Intent loginIntent = new Intent(PlanActivity.this, RegisterActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
            }
        };

        btnAddPlan = (ImageView) findViewById(R.id.btnAddPlan);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Plan");

        mBlogList = (RecyclerView) findViewById(R.id.blog_list);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));

        btnAddPlan.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);

        FirebaseRecyclerAdapter<PlanBlog, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<PlanBlog, BlogViewHolder>(

                PlanBlog.class,
                R.layout.view_plan_item,
                BlogViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, PlanBlog model, int position) {

                viewHolder.setTime(model.getTime());
                viewHolder.setType(model.getType());
                viewHolder.setDetail(model.getDetail());
                viewHolder.setIc(getApplicationContext(), model.getIc());

            }
        };

        mBlogList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView ;
        }

        public void setTime(String time){
            TextView tv_plan_time = (TextView) mView.findViewById(R.id.tv_plan_time);
            tv_plan_time.setText(time);
        }

        public void setType(String type){
            TextView tv_plan_type = (TextView) mView.findViewById(R.id.tv_plan_type);
            tv_plan_type.setText(type);
        }

        public void setDetail(String detail){
            TextView tv_plan_detail = (TextView) mView.findViewById(R.id.tv_plan_detail);
            tv_plan_detail.setText(detail);
        }

        public void setIc(Context ctx, String ic){
            ImageView ic_plan_select = (ImageView) mView.findViewById(R.id.ic_plan_select);
            Picasso.with(ctx).load(ic).into(ic_plan_select);
        }

    }
    
    private void StartDialogAddPlan() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(PlanActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_add_item_plan, null);

        mBuilder.setView(mView);
        AlertDialog dialog = mBuilder.create();
        dialog.show();

    }

    @Override
    public void onClick(View v) {
        if (v == btnAddPlan){
            StartDialogAddPlan();
//            startActivity(new Intent(this, PostActivity.class));
        }
    }

    
}
