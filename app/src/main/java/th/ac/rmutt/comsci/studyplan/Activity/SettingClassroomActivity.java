package th.ac.rmutt.comsci.studyplan.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import co.ceryle.segmentedbutton.SegmentedButtonGroup;
import th.ac.rmutt.comsci.studyplan.Adapter.AllUser;
import th.ac.rmutt.comsci.studyplan.Adapter.AllUserViewHolder;
import th.ac.rmutt.comsci.studyplan.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SettingClassroomActivity extends AppCompatActivity implements View.OnClickListener{

    private String mClass_key = null;

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUser;
    private DatabaseReference updateClass;
    private DatabaseReference mDatabaseAll;
    private DatabaseReference mDatabaseRegClass;
    private DatabaseReference mDatabaseStatus;
    private DatabaseReference mDatabaseRegClassUser;

    private DatabaseReference mDatabaseTeacher;
    private DatabaseReference mDatabaseStudent;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private StorageReference mStorageImage;

    private ProgressDialog mProgress;

    private View mView;

    private TextView tv_subject_username;
    private TextView tv_subject_sec;

    private ImageView btnBack;

    private Switch switchPassword;

    //Get Setting
    private FrameLayout frameChangProfile;
    private TextView tv_subject_id;
    private TextView tv_subject_name;
    private LinearLayout btnChangeSec;
    private LinearLayout btnChangeUsername;

    private LinearLayout mLayoutNewPassword;
    private LinearLayout mLayoutOldPassword;


    private TextView tvPassword;
    //------------

    private LinearLayout layoutPassword;

    private CircularImageView circularProfile;

    private TextView btnSettingClass;
    private TextView btnOutClass;

    private ProgressDialog progressDialog;

    private static final int GALLERY_REQUEST = 1;

    private Uri mImageUri = null;

    private FirebaseRecyclerAdapter<AllUser, AllUserViewHolder> teacherAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_classroom);

        initView();
        initListener();
        startFirebase();
        startDatabaseChange();
        startSwitchPassword();

        progressDialog = new ProgressDialog(this);
        mProgress = new ProgressDialog(this);

    }

    private void startSwitchPassword() {

    }

    private void startFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("ClassRoom");

        final String current_uid = mCurrentUser.getUid();

        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mDatabaseAll = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseRegClass = FirebaseDatabase.getInstance().getReference().child("RegClass");
        mDatabaseStatus = FirebaseDatabase.getInstance().getReference().child("Status");
        mDatabaseTeacher = mDatabaseStatus.child("teacher");
        mDatabaseStudent = mDatabaseStatus.child("student");

        mStorageImage = FirebaseStorage.getInstance().getReference().child("Classroom_images");
    }

    private void startDatabaseChange() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("ClassRoom");

        mClass_key = getIntent().getExtras().getString("class_id");
        updateClass = mDatabase.child(mClass_key).child("lock");
        mDatabaseRegClassUser = mDatabaseRegClass.child(mClass_key);

        final DatabaseReference updatePassword = mDatabase.child(mClass_key).child("password");

        mDatabase.child(mClass_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String image = (String) dataSnapshot.child("image").getValue();
                final String password = (String) dataSnapshot.child("password").getValue();
                final String lock = (String) dataSnapshot.child("lock").getValue();
                String sec = (String) dataSnapshot.child("sec").getValue();
                String subject_id = (String) dataSnapshot.child("subject_id").getValue();
                String subject_name = (String) dataSnapshot.child("subject_name").getValue();
                String uid = (String) dataSnapshot.child("uid").getValue();
                String username = (String) dataSnapshot.child("username").getValue();

                tv_subject_id.setText(subject_id);
                tv_subject_name.setText(subject_name);
                tv_subject_sec.setText(sec);
                tvPassword.setText(password);
                tv_subject_username.setText(username);

                Picasso.with(SettingClassroomActivity.this).load(image).into(circularProfile);

                /*----------------------- Start Chang "log" Zone ---------------------------*/

                if(lock.equals("yes")){
                    switchPassword.setChecked(true);
                    layoutPassword.setVisibility(View.VISIBLE);
                }
                if(lock.equals("no")){
                    switchPassword.setChecked(false);
                    layoutPassword.setVisibility(View.GONE);
                }

                switchPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        if(switchPassword.isChecked() && password.equals("null") && lock.equals("yes")) {
                            startChangePassword();
                        }

                        if(switchPassword.isChecked() && password.equals("null") && lock.equals("no")) {
                            startChangePassword();
                        }

                        if(switchPassword.isChecked() && !password.equals("null") && lock.equals("yes")) {
                        }

                        if(switchPassword.isChecked() && !password.equals("null") && lock.equals("no")) {
                        }

                        if(!switchPassword.isChecked() && password.equals("null") && lock.equals("yes")) {
                        }

                        if(!switchPassword.isChecked() && password.equals("null") && lock.equals("no")) {
                        }

                        if(!switchPassword.isChecked() && !password.equals("null") && lock.equals("yes")) {
                            startChangePassword();
                        }

                        if(!switchPassword.isChecked() && !password.equals("null") && lock.equals("no")) {
                        }

                    }
                });

                /*--------------------------------------------------------------------------*/
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });
    }

    private void initView() {

        tv_subject_sec = (TextView) findViewById(R.id.tv_subject_sec);

        tv_subject_username = (TextView) findViewById(R.id.tv_subject_username);

        switchPassword = (Switch) findViewById(R.id.switchPassword);

        layoutPassword = (LinearLayout) findViewById(R.id.layoutPassword);

        circularProfile = (CircularImageView) findViewById(R.id.circularProfile);

        //Get Setting
        frameChangProfile = (FrameLayout) findViewById(R.id.frameChangProfile);
        tv_subject_id = (TextView) findViewById(R.id.tv_subject_id);
        tv_subject_name = (TextView) findViewById(R.id.tv_subject_name);
        btnChangeSec = (LinearLayout) findViewById(R.id.btnChangeSec);
        btnChangeUsername = (LinearLayout) findViewById(R.id.btnChangeUsername);
        tvPassword = (TextView) findViewById(R.id.tvPassword);

        btnBack = (ImageView) findViewById(R.id.btnBack);

    }

    private void initListener() {

        frameChangProfile.setOnClickListener(this);
        tv_subject_id.setOnClickListener(this);
        tv_subject_name.setOnClickListener(this);
        btnChangeSec.setOnClickListener(this);
        btnChangeUsername.setOnClickListener(this);
        tvPassword.setOnClickListener(this);

        btnBack.setOnClickListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                progressDialog.setTitle("อัพโหลดรูปภาพ");
                progressDialog.setMessage("กรุณารอซักครู่...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                mImageUri = result.getUri();
                circularProfile.setImageURI(mImageUri);

                if(mImageUri != null) {
                    StorageReference filepath = mStorageImage.child(mImageUri.getLastPathSegment());
                    filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            String downloadUri = taskSnapshot.getDownloadUrl().toString();
                            mDatabase.child(mClass_key).child("image").setValue(downloadUri);
                        }
                    });

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    progressDialog.setTitle("ไฟล์ภาพมีปัญหา");
                    progressDialog.setMessage("กรุณาตรวจสอบไฟล์รูปภาพใหม่อีกครั้ง");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                }

            }
        }
    }

    private void changClassroomPicture() {
        Intent gallerryIntent = new Intent();
        gallerryIntent.setAction(Intent.ACTION_GET_CONTENT);
        gallerryIntent.setType("image/*");
        startActivityForResult(gallerryIntent, GALLERY_REQUEST);
    }

    private void startChangeId() {

            AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingClassroomActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.dialog_edit_subject_id, null);

            final EditText mSubjectId = (EditText) mView.findViewById(R.id.editTextSubjectID);
            TextView btnSave = (TextView) mView.findViewById(R.id.btnSave);
            TextView btnClose = (TextView) mView.findViewById(R.id.btnClose);

            final DatabaseReference Getkey = mDatabase.child(mClass_key);

            Getkey.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String sjid = dataSnapshot.child("subject_id").getValue().toString();
                    mSubjectId.setHint(sjid);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            });

            mBuilder.setView(mView);
            final AlertDialog dialog = mBuilder.create();
            dialog.show();

            //Setting Prevent Dialog
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!mSubjectId.getText().toString().isEmpty()){
                        Toast.makeText(SettingClassroomActivity.this, "เปลี่ยนแปลงข้อมูลแล้ว", Toast.LENGTH_SHORT).show();
                        final String sjid = mSubjectId.getText().toString().trim();
                        Getkey.child("subject_id").setValue(sjid);
                        dialog.dismiss();
                    }else{
                        Toast.makeText(SettingClassroomActivity.this, "กรุณาใส่ข้อมูล",Toast.LENGTH_SHORT).show();
                    }
                }
            });

            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

    }

    private void startChangeName() {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingClassroomActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_edit_subject_name, null);

                final EditText mSubjectName = (EditText) mView.findViewById(R.id.editTextSubjectName);
                TextView btnSave = (TextView) mView.findViewById(R.id.btnSave);
                TextView btnClose = (TextView) mView.findViewById(R.id.btnClose);

                final DatabaseReference Getkey = mDatabase.child(mClass_key);

                Getkey.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String sjName = dataSnapshot.child("subject_name").getValue().toString();
                        mSubjectName.setHint(sjName);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }
                });

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

        //Setting Prevent Dialog
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!mSubjectName.getText().toString().isEmpty()){
                            Toast.makeText(SettingClassroomActivity.this, "เปลี่ยนแปลงข้อมูลแล้ว", Toast.LENGTH_SHORT).show();
                            final String sjName = mSubjectName.getText().toString().trim();
                            Getkey.child("subject_name").setValue(sjName);
                            dialog.dismiss();
                        }else{
                            Toast.makeText(SettingClassroomActivity.this, "กรุณาใส่ข้อมูล",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    private void startChangeSec() {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingClassroomActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_edit_subject_sec, null);

                final EditText mSubjectSec = (EditText) mView.findViewById(R.id.editTextSubjectSec);
                TextView btnSave = (TextView) mView.findViewById(R.id.btnSave);
                TextView btnClose = (TextView) mView.findViewById(R.id.btnClose);

                final DatabaseReference Getkey = mDatabase.child(mClass_key);

                Getkey.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String sjSec = dataSnapshot.child("sec").getValue().toString();
                        mSubjectSec.setHint(sjSec);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }
                });

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();


        //Setting Prevent Dialog
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!mSubjectSec.getText().toString().isEmpty()){
                            Toast.makeText(SettingClassroomActivity.this, "เปลี่ยนแปลงข้อมูลแล้ว", Toast.LENGTH_SHORT).show();
                            final String sjSec = mSubjectSec.getText().toString().trim();
                            Getkey.child("sec").setValue(sjSec);
                            dialog.dismiss();
                        }else{
                            Toast.makeText(SettingClassroomActivity.this, "กรุณาใส่ข้อมูล",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    private void startChangeUsername() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingClassroomActivity.this);
        mView = getLayoutInflater().inflate(R.layout.dialog_edit_subject_username, null);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        //Setting Prevent Dialog
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        final LinearLayout layoutManual = (LinearLayout) mView.findViewById(R.id.layoutManual);
        final LinearLayout layoutAuto = (LinearLayout) mView.findViewById(R.id.layoutAuto);
        final EditText mSubjectUsername = (EditText) mView.findViewById(R.id.editTextSubjectUsername);
        final RecyclerView rvTeacher = (RecyclerView) mView.findViewById(R.id.rvTeacher);
        TextView btnSave = (TextView) mView.findViewById(R.id.btnSave);
        TextView btnClose = (TextView) mView.findViewById(R.id.btnClose);

        final DatabaseReference Getkey = mDatabase.child(mClass_key);

        layoutAuto.setVisibility(View.GONE);

        final SegmentedButtonGroup sbg = (SegmentedButtonGroup) mView.findViewById(R.id.sbUsername);

        sbg.setOnClickedButtonPosition(new SegmentedButtonGroup.OnClickedButtonPosition() {
            @Override
            public void onClickedButtonPosition(int position) {

                if(position == 0){
                    Toast.makeText(SettingClassroomActivity.this, "Clicked: " + position, Toast.LENGTH_SHORT).show();
                    layoutAuto.setVisibility(View.GONE);
                    layoutManual.setVisibility(View.VISIBLE);
                }
                if(position == 1){

                    mProgress.setMessage("กำลังโหลดข้อมูล...");
                    mProgress.show();

                    Toast.makeText(SettingClassroomActivity.this, "Clicked: " + position, Toast.LENGTH_SHORT).show();
                    layoutManual.setVisibility(View.GONE);
                    layoutAuto.setVisibility(View.VISIBLE);

                    /*---------------------- "Teacher" Zone ---------------------------*/

                    rvTeacher.setHasFixedSize(true);
                    rvTeacher.setLayoutManager(new LinearLayoutManager(SettingClassroomActivity.this));

                    LinearLayoutManager layoutTeacher = new LinearLayoutManager(SettingClassroomActivity.this);
                    layoutTeacher.setReverseLayout(true);
                    layoutTeacher.setStackFromEnd(true);

                    rvTeacher.setLayoutManager(layoutTeacher);

                    teacherAdapter = new FirebaseRecyclerAdapter<AllUser, AllUserViewHolder>(
                            AllUser.class,
                            R.layout.view_auth_user_dialog,
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


                                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Getkey.child("username").setValue(name);
                                            dialog.dismiss();
                                            Toast.makeText(SettingClassroomActivity.this, "เปลี่ยนเชื่อผู้สอนแล้ว",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    };

                    rvTeacher.setAdapter(teacherAdapter);

                    /*---------------------- "Teacher" Zone ---------------------------*/

                    mProgress.dismiss();

                    /*---------------------- "Student" Zone ---------------------------*/


                }

            }
        });
        sbg.setPosition(0, 0);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mSubjectUsername.getText().toString().isEmpty()){
                    Toast.makeText(SettingClassroomActivity.this, "เปลี่ยนแปลงข้อมูลแล้ว", Toast.LENGTH_SHORT).show();
                    final String sjUsername = mSubjectUsername.getText().toString().trim();
                    Getkey.child("username").setValue(sjUsername);
                    dialog.dismiss();
                }else{
                    Toast.makeText(SettingClassroomActivity.this, "กรุณาใส่ข้อมูล",Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Getkey.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String sjUsername = dataSnapshot.child("username").getValue().toString();
                mSubjectUsername.setHint(sjUsername);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });



    }

    private void startChangePassword() {

                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingClassroomActivity.this);
                final View mView = getLayoutInflater().inflate(R.layout.dialog_edit_subject_password, null);

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                //Setting Prevent Dialog
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);

                final EditText mSubjectPassword_Old = (EditText) mView.findViewById(R.id.editTextSubjectPassword_Old);
                final EditText mSubjectPassword_New = (EditText) mView.findViewById(R.id.editTextSubjectPassword_New);

                mLayoutOldPassword = (LinearLayout) mView.findViewById(R.id.layoutOldPassword);
                mLayoutNewPassword = (LinearLayout) mView.findViewById(R.id.layoutNewPassword);

                final TextView btnSave = (TextView) mView.findViewById(R.id.btnSave);
                final TextView btnClose = (TextView) mView.findViewById(R.id.btnClose);

                final DatabaseReference Getkey = mDatabase.child(mClass_key);
                final DatabaseReference updatePassword = mDatabase.child(mClass_key).child("password");


                Getkey.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String sjPassword = dataSnapshot.child("password").getValue().toString();
                        final String sjLock = dataSnapshot.child("lock").getValue().toString();
                        final String lock = (String) dataSnapshot.child("lock").getValue();

                        //Control Show / UnShow mLayout new/ old Password

                        if(switchPassword.isChecked() && sjPassword.equals("null") && lock.equals("yes")) {
                            mLayoutOldPassword.setVisibility(View.VISIBLE);
                            mLayoutNewPassword.setVisibility(View.GONE);
                        }

                        if(switchPassword.isChecked() && sjPassword.equals("null") && lock.equals("no")) {
                            mLayoutOldPassword.setVisibility(View.GONE);
                            mLayoutNewPassword.setVisibility(View.VISIBLE);
                        }

                        if(switchPassword.isChecked() && !sjPassword.equals("null") && lock.equals("yes")) {
                            mLayoutOldPassword.setVisibility(View.VISIBLE);
                            mLayoutNewPassword.setVisibility(View.VISIBLE);
                        }

                        if(switchPassword.isChecked() && !sjPassword.equals("null") && lock.equals("no")) {
                            mLayoutOldPassword.setVisibility(View.VISIBLE);
                            mLayoutNewPassword.setVisibility(View.GONE);
                        }

                        if(!switchPassword.isChecked() && sjPassword.equals("null") && lock.equals("yes")) {
                            mLayoutOldPassword.setVisibility(View.VISIBLE);
                            mLayoutNewPassword.setVisibility(View.GONE);
                        }

                        if(!switchPassword.isChecked() && sjPassword.equals("null") && lock.equals("no")) {
                            mLayoutOldPassword.setVisibility(View.VISIBLE);
                            mLayoutNewPassword.setVisibility(View.GONE);
                        }

                        if(!switchPassword.isChecked() && !sjPassword.equals("null") && lock.equals("yes")) {
                            mLayoutOldPassword.setVisibility(View.VISIBLE);
                            mLayoutNewPassword.setVisibility(View.GONE);
                        }

                        if(!switchPassword.isChecked() && !sjPassword.equals("null") && lock.equals("no")) {
                            mLayoutOldPassword.setVisibility(View.VISIBLE);
                            mLayoutNewPassword.setVisibility(View.GONE);
                        }

                        btnSave.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final String pOld = mSubjectPassword_Old.getText().toString();
                                final String pNew = mSubjectPassword_New.getText().toString();

                                if(switchPassword.isChecked() && sjPassword.equals("null") && lock.equals("yes")) {
                                    if(pOld.isEmpty()){
                                        Toast.makeText(SettingClassroomActivity.this, "กรุณากรอกข้อมูล", Toast.LENGTH_SHORT).show();
                                    }
                                    if(!pOld.isEmpty()){
                                        Toast.makeText(SettingClassroomActivity.this, "กรอกข้อมูลแล้ว", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                if(switchPassword.isChecked() && sjPassword.equals("null") && lock.equals("no")) {
                                    if(pNew.isEmpty()){
                                        Toast.makeText(SettingClassroomActivity.this, "กรุณากรอกข้อมูล", Toast.LENGTH_SHORT).show();
                                    }
                                    if(!pNew.isEmpty()){
                                        Toast.makeText(SettingClassroomActivity.this, "กรอกข้อมูลแล้ว", Toast.LENGTH_SHORT).show();
                                        updateClass.setValue("yes");
                                        updatePassword.setValue(pNew);
                                        switchPassword.setChecked(true);
                                        dialog.dismiss();

                                    }
                                }

                                if(switchPassword.isChecked() && !sjPassword.equals("null") && lock.equals("yes")) {
                                    if(pNew.isEmpty() && pOld.isEmpty()){
                                        Toast.makeText(SettingClassroomActivity.this, "กรุณากรอกข้อมูล", Toast.LENGTH_SHORT).show();
                                    }
                                    if(!pNew.isEmpty() && pOld.isEmpty()){
                                        Toast.makeText(SettingClassroomActivity.this, "กรุณากรอกหรัสเก่า", Toast.LENGTH_SHORT).show();
                                    }
                                    if(pNew.isEmpty() && !pOld.isEmpty()){
                                        Toast.makeText(SettingClassroomActivity.this, "กรุณากรอกรหัสใหม่", Toast.LENGTH_SHORT).show();
                                    }
                                    if(!pNew.isEmpty() && !pOld.isEmpty() && !pOld.equals(sjPassword)){
                                        Toast.makeText(SettingClassroomActivity.this, "รหัสเก่าไม่ถูกต้อง", Toast.LENGTH_SHORT).show();
                                    }

                                    if(!pNew.isEmpty() && !pOld.isEmpty() && pOld.equals(sjPassword) && pOld.equals(pNew)){
                                        Toast.makeText(SettingClassroomActivity.this, "รหัสใหม่ เหมือน รหัสเก่า", Toast.LENGTH_SHORT).show();
                                    }
                                    if(!pNew.isEmpty() && !pOld.isEmpty() && pOld.equals(sjPassword) && !pOld.equals(pNew)){
                                        Toast.makeText(SettingClassroomActivity.this, "เปลี่ยนรหัสแล้ว", Toast.LENGTH_SHORT).show();
                                        updateClass.setValue("yes");
                                        updatePassword.setValue(pNew);
                                        switchPassword.setChecked(true);
                                        dialog.dismiss();

                                    }
                                }

                                if(switchPassword.isChecked() && !sjPassword.equals("null") && lock.equals("no")) {

                                }

                                if(!switchPassword.isChecked() && sjPassword.equals("null") && lock.equals("yes")) {

                                }

                                if(!switchPassword.isChecked() && sjPassword.equals("null") && lock.equals("no")) {

                                }

                                if(!switchPassword.isChecked() && !sjPassword.equals("null") && lock.equals("yes")) {
                                    if(pOld.isEmpty()){
                                        Toast.makeText(SettingClassroomActivity.this, "กรุณากรอกข้อมูล", Toast.LENGTH_SHORT).show();
                                    }
                                    if(!pOld.isEmpty()&& !pOld.equals(sjPassword)){
                                        Toast.makeText(SettingClassroomActivity.this, "รหัสเดิมไม่ถูกต้อง", Toast.LENGTH_SHORT).show();
                                    }
                                    if(!pOld.isEmpty()&& pOld.equals(sjPassword)){
                                        Toast.makeText(SettingClassroomActivity.this, "เปลี่ยนรหัสแล้ว", Toast.LENGTH_SHORT).show();
                                        updateClass.setValue("no");
                                        updatePassword.setValue("null");
                                        switchPassword.setChecked(false);
                                        dialog.dismiss();

                                    }
                                }

                                if(!switchPassword.isChecked() && !sjPassword.equals("null") && lock.equals("no")) {

                                }

                            }
                        });

                        btnClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();

                                if(!sjPassword.equals("null")){
                                    switchPassword.setChecked(true);
                                }

                                if(sjPassword.equals("null")){
                                    switchPassword.setChecked(false);
                                }

                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }
                });



    }

    @Override
    public void onClick(View v) {

        if(v == frameChangProfile){
            changClassroomPicture();
        }

        if(v == tv_subject_id){
            startChangeId();
        }

        if(v == tv_subject_name){
            startChangeName();
        }

        if(v == btnChangeSec){
            startChangeSec();
        }

        if(v == btnChangeUsername){
            startChangeUsername();
        }

        if(v == tvPassword){
            startChangePassword();
        }

        if(v == btnBack){
            finish();
        }

    }

    @Override
    protected void attachBaseContext (Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


}
