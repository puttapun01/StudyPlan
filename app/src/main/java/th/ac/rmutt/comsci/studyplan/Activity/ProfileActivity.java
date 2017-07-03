package th.ac.rmutt.comsci.studyplan.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import th.ac.rmutt.comsci.studyplan.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser mCurrentUser;

    private TextView textViewEditProfile;
    private TextView textViewLogout;

    private TextView textViewUserEmail;
    private TextView textViewName;
    private TextView textViewStudentId;

    private CircularImageView circularProfile;

    private Button buttonHomework;
    private Button buttonTable;
    private Button buttonPlan;
    private Button buttonSetting;

    private Uri mImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        String current_uid = mCurrentUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        //Profile Changer
        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewStudentId = (TextView) findViewById(R.id.textViewStudentId);
        circularProfile = (CircularImageView) findViewById(R.id.circularProfile);

        buttonHomework = (Button) findViewById(R.id.buttonHomework);
        buttonTable = (Button) findViewById(R.id.buttonTable);
        buttonPlan = (Button) findViewById(R.id.buttonPlan);
        buttonSetting = (Button) findViewById(R.id.buttonSetting);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    String name = dataSnapshot.child("name").getValue().toString();
                    String stid = dataSnapshot.child("stid").getValue().toString();
                    String image = dataSnapshot.child("image").getValue().toString();

                    textViewName.setText("ชื่อ-สกุล : " + name);
                    textViewStudentId.setText("รหัส นศ./ประจำตัว : " + stid);

                    Picasso.with(ProfileActivity.this).load(image).into(circularProfile);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });



        FirebaseUser user = firebaseAuth.getCurrentUser();

        //Button
        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        textViewEditProfile = (TextView) findViewById(R.id.textViewEditProfile);

        textViewUserEmail.setText("Email : " + user.getEmail());

        textViewLogout = (TextView) findViewById(R.id.textViewLogout);

        textViewEditProfile.setOnClickListener(this);
        textViewLogout.setOnClickListener(this);

        buttonHomework.setOnClickListener(this);
        buttonTable.setOnClickListener(this);
        buttonPlan.setOnClickListener(this);
        buttonSetting.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v == textViewLogout){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        if (v == textViewEditProfile){
            startActivity(new Intent(this, EditProfileActivity.class));
        }

        //Button Set

        if (v == buttonHomework){
            startActivity(new Intent(this, HomeworkActivity.class));
        }
        if (v == buttonTable){
            startActivity(new Intent(this, TableActivity.class));
        }
        if (v == buttonPlan){
            startActivity(new Intent(this, PlanActivity.class));
        }
        if (v == buttonSetting){
            startActivity(new Intent(this, SettingActivity.class));
        }
        
    }
    @Override
    protected void attachBaseContext (Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
