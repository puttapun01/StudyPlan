package th.ac.rmutt.comsci.studyplan.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import th.ac.rmutt.comsci.studyplan.Adapter.AllUser;
import th.ac.rmutt.comsci.studyplan.Adapter.AllUserViewHolder;
import th.ac.rmutt.comsci.studyplan.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AllUserActivity extends AppCompatActivity implements View.OnClickListener {

    private SwipeRefreshLayout mSwipe;

    private DatabaseReference mDatabaseAll;
    private DatabaseReference mDatabaseStatus;
    private DatabaseReference mDatabaseTeacher;
    private DatabaseReference mDatabaseStudent;

    private FirebaseAuth mAuth;

    private ImageView btnBack;

    private FirebaseRecyclerAdapter<AllUser, AllUserViewHolder> teacherAdapter;
    private FirebaseRecyclerAdapter<AllUser, AllUserViewHolder> studentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_user);

        initView();
        initListener();
        startRefresh();
        startFirebase();
        startList();

    }

    private void startRefresh() {
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                mSwipe.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipe.setRefreshing(false);
                            /*Add Method*/
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                            /*---------*/
                    }
                },2000);
            }
        });
    }


    private void startFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mDatabaseAll = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseStatus = FirebaseDatabase.getInstance().getReference().child("Status");
        mDatabaseTeacher = mDatabaseStatus.child("teacher");
        mDatabaseStudent = mDatabaseStatus.child("student");
    }

    private void startList() {
        final RecyclerView rvAllUser = (RecyclerView) findViewById(R.id.rvAllUser);

        rvAllUser.setHasFixedSize(true);
        rvAllUser.setLayoutManager(new LinearLayoutManager(AllUserActivity.this));

        LinearLayoutManager layoutTeacher = new LinearLayoutManager(AllUserActivity.this);
        layoutTeacher.setReverseLayout(true);
        layoutTeacher.setStackFromEnd(true);

        rvAllUser.setLayoutManager(layoutTeacher);

        teacherAdapter = new FirebaseRecyclerAdapter<AllUser, AllUserViewHolder>(
                AllUser.class,
                R.layout.view_auth_user,
                AllUserViewHolder.class,
                mDatabaseAll
        ) {
            @Override
            protected void populateViewHolder(final AllUserViewHolder viewHolder, final AllUser model, int position) {
                viewHolder.setImage(getApplicationContext(), model.getImage());
                viewHolder.setName(model.getName());
                viewHolder.setStid(model.getStid());
                viewHolder.setStatus(model.getStatus());
                viewHolder.setStatus_id(model.getStatus_id());
            }
        };

        rvAllUser.setAdapter(teacherAdapter);


    }

    private void initView() {
        btnBack = (ImageView) findViewById(R.id.btnBack);
        mSwipe = (SwipeRefreshLayout) findViewById(R.id.layoutAuto);
    }

    private void initListener() {
        btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if(v == btnBack){
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*int i = 0;
        while (i < 1){ i++;
            if(i != 1){ break; }
            if(i == 1){

                mSwipe.setRefreshing(
                        (new Handler()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = getIntent();
                                finish(1);
                                mSwipe.setRefreshing(false);
                                startActivity(intent);
                            }
                        },2000));
            }
        }*/

    }

    private void finish(int i) {

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }


    @Override
    protected void attachBaseContext (Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
