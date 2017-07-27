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
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import th.ac.rmutt.comsci.studyplan.Adapter.AllUser;
import th.ac.rmutt.comsci.studyplan.Adapter.AllUserViewHolder;
import th.ac.rmutt.comsci.studyplan.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ClassroomMemberActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseAll;
    private DatabaseReference mDatabaseStatus;
    private DatabaseReference mDatabaseRegClass;
    private DatabaseReference mDatabaseTeacher;
    private DatabaseReference mDatabaseStudent;

    private String mClass_key = null;

    private FirebaseRecyclerAdapter<AllUser, AllUserViewHolder> memberAdapter;

    private SwipeRefreshLayout mSwipe;

    private ImageView btnBack;
    private TextView tvClassroomName;
    private RecyclerView rvAllUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom_member);

        initView();
        initListener();
        startRefresh();
        startFirebase();
        startChangeText();
        startList();

    }

    private void startChangeText() {

        mDatabase.child(mClass_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String subject_name = (String) dataSnapshot.child("subject_name").getValue();
                tvClassroomName.setText(subject_name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

    private void startList() {

        rvAllUser.setHasFixedSize(true);
        rvAllUser.setLayoutManager(new LinearLayoutManager(ClassroomMemberActivity.this));

        LinearLayoutManager layoutTeacher = new LinearLayoutManager(ClassroomMemberActivity.this);
        layoutTeacher.setReverseLayout(true);
        layoutTeacher.setStackFromEnd(true);

        rvAllUser.setLayoutManager(layoutTeacher);

        memberAdapter = new FirebaseRecyclerAdapter<AllUser, AllUserViewHolder>(
                AllUser.class,
                R.layout.view_auth_user,
                AllUserViewHolder.class,
                mDatabaseRegClass.child(mClass_key)
        ) {
            @Override
            protected void populateViewHolder(final AllUserViewHolder viewHolder, final AllUser model, int position) {
                final String reg_key = getRef(position).getKey();

                mDatabaseAll.child(reg_key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String status_id = dataSnapshot.child("status_id").getValue().toString();
                        final String image = dataSnapshot.child("image").getValue().toString();
                        final String name = dataSnapshot.child("name").getValue().toString();
                        final String stid = dataSnapshot.child("stid").getValue().toString();
                        final String status = dataSnapshot.child("status").getValue().toString();

                        viewHolder.setImage(getApplicationContext(), image);
                        viewHolder.setName(name);
                        viewHolder.setStid(stid);
                        viewHolder.setStatus(status);
                        viewHolder.setStatus_id(status_id);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };


        rvAllUser.setAdapter(memberAdapter);
    }

    private void startFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("ClassRoom");
        mDatabaseAll = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseStatus = FirebaseDatabase.getInstance().getReference().child("Status");
        mDatabaseRegClass = FirebaseDatabase.getInstance().getReference().child("RegClass");
        mDatabaseTeacher = mDatabaseStatus.child("teacher");
        mDatabaseStudent = mDatabaseStatus.child("student");

        mClass_key = getIntent().getExtras().getString("class_id");
    }

    private void initView() {
        mSwipe = (SwipeRefreshLayout) findViewById(R.id.layoutAuto);

        btnBack = (ImageView) findViewById(R.id.btnBack);
        tvClassroomName = (TextView) findViewById(R.id.tvClassroomName);
        rvAllUser = (RecyclerView) findViewById(R.id.rvAllUser);

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
    protected void attachBaseContext (Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
