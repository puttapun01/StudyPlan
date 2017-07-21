package th.ac.rmutt.comsci.studyplan.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
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
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ClassStatusActivity extends AppCompatActivity implements View.OnClickListener{

    private String mClass_key = null;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser mCurrentUser;

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUser;

    private TextView tv_subject_id;
    private TextView tv_subject_name;
    private TextView tv_subject_sec;
    private ImageView imageViewSelect;
    private ImageView imageViewProfile;
    private TextView btnSettingClass;
    private TextView btnOutClass;
    private ImageView btnBack;

    private TextView tvPost;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_status);

        initView();
        initListener();
        startDatabaseChange();

    }

    private void startDatabaseChange() {
        firebaseAuth = FirebaseAuth.getInstance();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        String current_uid = mCurrentUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("ClassRoom");
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        mClass_key = getIntent().getExtras().getString("class_id");

        mDatabase.child(mClass_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String image = (String) dataSnapshot.child("image").getValue();
                String password = (String) dataSnapshot.child("password").getValue();
                String sec = (String) dataSnapshot.child("sec").getValue();
                String subject_id = (String) dataSnapshot.child("subject_id").getValue();
                String subject_name = (String) dataSnapshot.child("subject_name").getValue();
                String uid = (String) dataSnapshot.child("uid").getValue();
                String username = (String) dataSnapshot.child("username").getValue();

                tv_subject_id.setText(subject_id);
                tv_subject_name.setText(subject_name);
                tv_subject_sec.setText(sec);


//                Picasso.with(ClassStatusActivity.this).load(image).into(...);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String image = dataSnapshot.child("image").getValue().toString();
                Picasso.with(ClassStatusActivity.this).load(image).into(imageViewProfile);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initView() {

        tv_subject_id = (TextView) findViewById(R.id.tv_subject_id);
        tv_subject_name = (TextView) findViewById(R.id.tv_subject_name);
        tv_subject_sec = (TextView) findViewById(R.id.tv_subject_sec);
        tvPost = (TextView) findViewById(R.id.tvPost);

        imageViewSelect = (ImageView) findViewById(R.id.imageViewSelect);
        imageViewProfile = (ImageView) findViewById(R.id.imageViewProfile);
        btnBack = (ImageView) findViewById(R.id.btnBack);

    }

    private void initListener() {

        imageViewSelect.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        tvPost.setOnClickListener(this);

    }

    private void startButtomSheetSelect() {
        final BottomSheetDialog buttomSheetDialog = new BottomSheetDialog(ClassStatusActivity.this);
        View parentView = getLayoutInflater().inflate(R.layout.view_classroom_status_select, null);
        buttomSheetDialog.setContentView(parentView);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View) parentView.getParent());
        bottomSheetBehavior.setPeekHeight(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics()));
        buttomSheetDialog.show();

        btnSettingClass = (TextView) parentView.findViewById(R.id.btnSettingClass);
        btnOutClass = (TextView) parentView.findViewById(R.id.btnOutClass);

        final String class_key = mClass_key;

        btnSettingClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttomSheetDialog.dismiss();
                Intent singleClassIntent = new Intent(ClassStatusActivity.this, SettingClassroomActivity.class);
                singleClassIntent.putExtra("class_id", class_key);
                startActivity(singleClassIntent);
            }
        });

    }

    private void startPost() {
        final String class_key = mClass_key;
        Intent singleClassIntent = new Intent(ClassStatusActivity.this, PostStatusClassActivity.class);
        singleClassIntent.putExtra("class_id", class_key);
        startActivity(singleClassIntent);
    }

    @Override
    public void onClick(View v) {
        if (v == imageViewSelect) {
            startButtomSheetSelect();
        }

        if (v == btnBack) {
            finish();
        }

        if (v == tvPost) {
            startPost();
        }
    }



    @Override
    protected void attachBaseContext (Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


}
