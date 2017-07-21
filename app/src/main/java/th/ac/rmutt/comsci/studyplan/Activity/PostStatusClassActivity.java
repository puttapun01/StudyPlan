package th.ac.rmutt.comsci.studyplan.Activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import th.ac.rmutt.comsci.studyplan.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PostStatusClassActivity extends AppCompatActivity implements View.OnClickListener {

    private String mClass_key = null;

    private ImageView btnBack;

    private int mYear, mMonth, mDay, mHour, mMinute;

    //Get Database
    private TextView tvClassroom;
    private TextView tvAuthName;
    private CircularImageView circularAuth;

    //Write Database
    private EditText etPost;
    private TextView btnPost;
    private Switch switchDate;
    private LinearLayout layoutDate;
    private TextView tvTime;
    private TextView tvDate;

    private ImageView imImage;
    private ImageView imFile;
    private TextView tvImages;
    private TextView tvFile;

    private TextView tvViewImage;
    private TextView tvViewFile;


    //Database Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_status_class);

        initView();
        initListener();
        startDatabaseChange();
        startReadData();

    }

    private void startDatabaseChange() {
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        String current_uid = mCurrentUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("ClassRoom");
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        //Get Data To Read
        mClass_key = getIntent().getExtras().getString("class_id");
        final String class_key = mClass_key;

    }

    private void startReadData() {

        mDatabase.child(mClass_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String subject_name = (String) dataSnapshot.child("subject_name").getValue();
                tvClassroom.setText(subject_name);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String image = (String) dataSnapshot.child("image").getValue();
                String name = (String) dataSnapshot.child("name").getValue();

                tvAuthName.setText(name);
                Picasso.with(PostStatusClassActivity.this).load(image).into(circularAuth);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void initView() {
        //Get Database
        tvClassroom = (TextView) findViewById(R.id.tvClassroom);
        tvAuthName = (TextView) findViewById(R.id.tvAuthName);
        circularAuth = (CircularImageView) findViewById(R.id.circularAuth);

        //Write Database
        etPost = (EditText) findViewById(R.id.etPost);
        btnPost = (TextView) findViewById(R.id.btnPost);
        switchDate = (Switch) findViewById(R.id.switchDate);
        layoutDate = (LinearLayout) findViewById(R.id.layoutDate);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvDate = (TextView) findViewById(R.id.tvDate);

        imImage = (ImageView) findViewById(R.id.imImage);
        imFile = (ImageView) findViewById(R.id.imFile);
        tvImages = (TextView) findViewById(R.id.tvImages);
        tvFile = (TextView) findViewById(R.id.tvFile);

        tvViewImage = (TextView) findViewById(R.id.tvViewImage);
        tvViewFile = (TextView) findViewById(R.id.tvViewFile);

        //Oth Button
        btnBack = (ImageView) findViewById(R.id.btnBack);
    }

    private void initListener() {

        btnBack.setOnClickListener(this);

        btnPost.setOnClickListener(this);

        tvTime.setOnClickListener(this);
        tvDate.setOnClickListener(this);

        imImage.setOnClickListener(this);
        imFile.setOnClickListener(this);
        tvImages.setOnClickListener(this);
        tvFile.setOnClickListener(this);

        startSwitch();

    }



    @Override
    public void onClick(View v) {

        if(v == btnBack){
            finish();
        }

        if(v == btnPost){
            startPost();
        }

        if(v == tvTime){
            startSelectTime();
        }

        if(v == tvDate){
            startSelectDate();
        }

        if(v == imImage && v == tvImages){
            startUploadImage();
        }

        if(v == imFile && v == tvFile){
            startUploadFile();
        }

    }

    private void startPost() {

    }

    private void startSwitch() {

        final LinearLayout.LayoutParams paramClose = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                115.0f);

        final LinearLayout.LayoutParams paramOpen = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                90.0f);

        layoutDate.setVisibility(View.GONE);
        tvTime.setVisibility(View.GONE);
        tvDate.setVisibility(View.GONE);
        etPost.setLayoutParams(paramClose);

        switchDate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(!switchDate.isChecked()){

                    etPost.setLayoutParams(paramClose);

                    layoutDate.setVisibility(View.GONE);
                    tvTime.setVisibility(View.GONE);
                    tvDate.setVisibility(View.GONE);
                }

                if(switchDate.isChecked()) {

                    etPost.setLayoutParams(paramOpen);

                    layoutDate.setVisibility(View.VISIBLE);
                    tvTime.setVisibility(View.VISIBLE);
                    tvDate.setVisibility(View.VISIBLE);
                }

            }
        });

    }

    private void startSelectTime() {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                tvTime.setText(hourOfDay + ":" + minute);
            }
        }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private void startSelectDate() {

        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        // Launch Date Picker Dialog
        DatePickerDialog dpd = new DatePickerDialog(PostStatusClassActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        tvDate.setText(dayOfMonth + " / " + (monthOfYear + 1) + " / " + year);
                    }
                }, mYear, mMonth, mDay);
        dpd.show();

    }

    private void startUploadImage() {

    }

    private void startUploadFile() {

    }



    @Override
    protected void attachBaseContext (Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
