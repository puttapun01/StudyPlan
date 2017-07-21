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

    private Switch switchPassword;

    //Get Setting
    private FrameLayout frameChangProfile;
    private TextView tv_subject_id;
    private TextView tv_subject_name;
    private LinearLayout btnChangeSec;
    private LinearLayout btnChangeUsername;
    private LinearLayout mLayoutNewPassword;
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
    private FirebaseRecyclerAdapter<AllUser, AllUserViewHolder> studentAdapter;

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
                String password = (String) dataSnapshot.child("password").getValue();
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
                    layoutPassword.setVisibility(View.GONE);
                }

                switchPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        if(switchPassword.isChecked()){
                            layoutPassword.setVisibility(View.VISIBLE);
                            updateClass.setValue("yes");
                            startChangePassword();
                        }

                        if(!switchPassword.isChecked()) {
                            startChangePassword();
                            layoutPassword.setVisibility(View.GONE);

                            if(lock.equals("yes")){
                                switchPassword.setChecked(true);
                            }

                            if(lock.equals("no")){
                                switchPassword.setChecked(false);
                            }
//                            updateClass.setValue("no");
//                            updatePassword.setValue("null");

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

    }

    private void initListener() {

        frameChangProfile.setOnClickListener(this);
        tv_subject_id.setOnClickListener(this);
        tv_subject_name.setOnClickListener(this);
        btnChangeSec.setOnClickListener(this);
        btnChangeUsername.setOnClickListener(this);
        tvPassword.setOnClickListener(this);

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

    }

    private void startChangeName() {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingClassroomActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_edit_subject_name, null);

                final EditText mSubjectName = (EditText) mView.findViewById(R.id.editTextSubjectName);
                TextView btnSave = (TextView) mView.findViewById(R.id.btnSave);

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

    }

    private void startChangeSec() {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingClassroomActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_edit_subject_sec, null);

                final EditText mSubjectSec = (EditText) mView.findViewById(R.id.editTextSubjectSec);
                TextView btnSave = (TextView) mView.findViewById(R.id.btnSave);

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

    }

    private void startChangeUsername() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingClassroomActivity.this);
        mView = getLayoutInflater().inflate(R.layout.dialog_edit_subject_username, null);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        final LinearLayout layoutManual = (LinearLayout) mView.findViewById(R.id.layoutManual);
        final LinearLayout layoutAuto = (LinearLayout) mView.findViewById(R.id.layoutAuto);
        final EditText mSubjectUsername = (EditText) mView.findViewById(R.id.editTextSubjectUsername);
        final RecyclerView rvTeacher = (RecyclerView) mView.findViewById(R.id.rvTeacher);
        final RecyclerView rvStudent = (RecyclerView) mView.findViewById(R.id.rvStudent);
        TextView btnSave = (TextView) mView.findViewById(R.id.btnSave);



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
                            R.layout.view_auth_user,
                            AllUserViewHolder.class,
                            mDatabaseTeacher
                    ) {
                        @Override
                        protected void populateViewHolder(final AllUserViewHolder viewHolder, final AllUser model, int position) {

                            final String teacher_key = getRef(position).getKey();

                            final String ping_key = mDatabaseRegClassUser.child(teacher_key).getKey();

                            mDatabaseAll.child(ping_key).addValueEventListener(new ValueEventListener() {
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

                    rvTeacher.setAdapter(teacherAdapter);

                    /*---------------------- "Teacher" Zone ---------------------------*/


                    /*---------------------- "Student" Zone ---------------------------*/

                    rvStudent.setHasFixedSize(true);
                    rvStudent.setLayoutManager(new LinearLayoutManager(SettingClassroomActivity.this));

                    LinearLayoutManager layoutStudent = new LinearLayoutManager(SettingClassroomActivity.this);
                    layoutStudent.setReverseLayout(true);
                    layoutStudent.setStackFromEnd(true);

                    rvStudent.setLayoutManager(layoutStudent);

                    studentAdapter = new FirebaseRecyclerAdapter<AllUser, AllUserViewHolder>(
                            AllUser.class,
                            R.layout.view_auth_user,
                            AllUserViewHolder.class,
                            mDatabaseStudent
                    ) {
                        @Override
                        protected void populateViewHolder(final AllUserViewHolder viewHolder, final AllUser model, int position) {

                            final String student_key = getRef(position).getKey();

                            final String ping_key = mDatabaseRegClassUser.child(student_key).getKey();

                            mDatabaseAll.child(ping_key).addValueEventListener(new ValueEventListener() {
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

                    rvStudent.setAdapter(studentAdapter);

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

                final EditText mSubjectPassword_Old = (EditText) mView.findViewById(R.id.editTextSubjectPassword_Old);
                final EditText mSubjectPassword_New = (EditText) mView.findViewById(R.id.editTextSubjectPassword_New);
                final LinearLayout mLayoutOldPassword = (LinearLayout) mView.findViewById(R.id.layoutOldPassword);
                mLayoutNewPassword = (LinearLayout) mView.findViewById(R.id.layoutNewPassword);

                final TextView btnSave = (TextView) mView.findViewById(R.id.btnSave);

                final DatabaseReference Getkey = mDatabase.child(mClass_key);
                final DatabaseReference updatePassword = mDatabase.child(mClass_key).child("password");

                if(!switchPassword.isChecked()) {
                    mLayoutNewPassword.setVisibility(View.GONE);
                }

                Getkey.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String sjPassword = dataSnapshot.child("password").getValue().toString();
                        final String sjLock = dataSnapshot.child("lock").getValue().toString();

                        if(sjPassword.equals("null")){
                            mLayoutOldPassword.setVisibility(View.GONE);
                        }

                        btnSave.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final String pOld = mSubjectPassword_Old.getText().toString();
                                final String pNew = mSubjectPassword_New.getText().toString();

                                if(sjPassword.equals("null")&& !pNew.isEmpty()){
                                    Toast.makeText(SettingClassroomActivity.this, "สร้างรหัสผ่านใหม่แล้ว",Toast.LENGTH_SHORT).show();
                                    updatePassword.setValue(pNew);
                                    dialog.dismiss();
                                }

                                if(!sjPassword.equals("null") && sjPassword.equals(pOld) && !pNew.isEmpty() && !pNew.equals(sjPassword)){
                                    Toast.makeText(SettingClassroomActivity.this,"#1" + "เปลี่ยนรหัสผ่านแล้ว",Toast.LENGTH_SHORT).show();
                                    updatePassword.setValue(pNew);
                                    dialog.dismiss();
                                }

                                if(!sjPassword.equals("null")&& sjLock.equals("no") && !pOld.isEmpty() && sjPassword.equals(pOld)){
                                    Toast.makeText(SettingClassroomActivity.this,"#2" + "เปลี่ยนรหัสผ่านแล้ว",Toast.LENGTH_SHORT).show();
                                    updateClass.setValue("no");
                                    updatePassword.setValue("null");
                                    dialog.dismiss();
                                }

                                if(!sjPassword.equals("null") && sjPassword.equals(pOld) && pNew.isEmpty() && sjLock.equals("yes")){
                                    Toast.makeText(SettingClassroomActivity.this, "###",Toast.LENGTH_SHORT).show();
                                    updateClass.setValue("no");
                                    updatePassword.setValue("null");
                                    dialog.dismiss();
                                    Intent intent = getIntent();
                                    finish();
                                    startActivity(intent);
                                }

                                if(!sjPassword.equals("null")&& !pOld.isEmpty() && !sjPassword.equals(pOld)){
                                    Toast.makeText(SettingClassroomActivity.this, "รหัสผ่านเดิมไม่ถูกต้อง",Toast.LENGTH_SHORT).show();
                                }

                                if(sjPassword.equals("null")&& pNew.isEmpty()){
                                    Toast.makeText(SettingClassroomActivity.this, "#1 กรุณากรอกรหัสผ่านใหม่",Toast.LENGTH_SHORT).show();
                                }

                                if(!sjPassword.equals("null") && pOld.isEmpty()){
                                    Toast.makeText(SettingClassroomActivity.this, "กรุณากรอกรหัสผ่านเดิม",Toast.LENGTH_SHORT).show();
                                }

                                if(!sjPassword.equals("null") && pNew.isEmpty()){
                                    Toast.makeText(SettingClassroomActivity.this, "#2 กรุณากรอกรหัสผ่านใหม่",Toast.LENGTH_SHORT).show();
                                }

                                if(!sjPassword.equals("null") && pOld.isEmpty() && pNew.isEmpty()){
                                    Toast.makeText(SettingClassroomActivity.this, "กรุณากรอกข้อมูล",Toast.LENGTH_SHORT).show();
                                }

                                if(!sjPassword.equals("null") && !sjPassword.equals(pOld)){
                                    Toast.makeText(SettingClassroomActivity.this, "รหัสผ่านเดิมไม่ถูกต้อง",Toast.LENGTH_SHORT).show();
                                }

                                if(!sjPassword.equals("null") && sjPassword.equals(pOld) && pNew.isEmpty()){
                                    Toast.makeText(SettingClassroomActivity.this, "#3 กรุณากรอกรหัสผ่านใหม่",Toast.LENGTH_SHORT).show();
                                }

                                if(!sjPassword.equals("null") && sjPassword.equals(pOld) && !pNew.isEmpty() && pNew.equals(sjPassword)){
                                    Toast.makeText(SettingClassroomActivity.this, "รหัสผ่านใหม่ ซ้ำ กับรหัสผ่านเดิม",Toast.LENGTH_SHORT).show();
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

    }

    @Override
    protected void attachBaseContext (Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


}
