package th.ac.rmutt.comsci.studyplan.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import th.ac.rmutt.comsci.studyplan.Adapter.Classroom;
import th.ac.rmutt.comsci.studyplan.Adapter.ClassroomViewHolder;
import th.ac.rmutt.comsci.studyplan.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ClassroomActivity extends AppCompatActivity implements View.OnClickListener {

    private SwipeRefreshLayout mSwipe;

    private ImageView tvAddClassroom;
    private ImageView btnBack;

    private ProgressDialog mProgress;

    private String linkClass = "https://firebasestorage.googleapis.com/v0/b/studyplan-cb45d.appspot.com/o/Classroom_images%2Fim_profile_group.jpg?alt=media&token=19bc052a-0e8b-4e43-9e7e-ac66e0d71083" ;

    private RecyclerView viewClassroom;

    private CircularImageView mImage;

    // firebase Connect
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseAddClass;
    private DatabaseReference mDatabaseRagClass;
    private DatabaseReference mDatabaseUserGetClass;

    private DatabaseReference mDatabaseUser;
    private StorageReference mStorage;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    private static final int GALLERY_REQUEST = 1;
    private Uri mImageUri = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);

        initView();
        initListener();
        startFirebase();
        startRefresh();

        mProgress = new ProgressDialog(this);

        viewClassroom.setHasFixedSize(true);
        viewClassroom.setLayoutManager(new LinearLayoutManager(this));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        viewClassroom.setLayoutManager(layoutManager);

    }

    private void startFirebase() {

        //UserID
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference().child("Classroom_images");
        mCurrentUser = mAuth.getCurrentUser();

        //ClassRoom Database
        mDatabase = FirebaseDatabase.getInstance().getReference().child("ClassRoom");
        mDatabaseRagClass = FirebaseDatabase.getInstance().getReference().child("RegClass");
        mDatabaseUserGetClass = FirebaseDatabase.getInstance().getReference().child("UserGetClass");

        //Users Database
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

    }

    private void startDialogAddClassroom() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ClassroomActivity.this);
        final View mView = getLayoutInflater().inflate(R.layout.dialog_add_classroom, null);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        final EditText mSubjectID = (EditText) mView.findViewById(R.id.editTextSubjectID);
        final EditText mSubjectName = (EditText) mView.findViewById(R.id.editTexctSubjectName);
        final EditText mSec = (EditText) mView.findViewById(R.id.editTextSec);
        final TextView btnSave = (TextView) mView.findViewById(R.id.btnSave);
        final EditText tvPassword = (EditText) mView.findViewById(R.id.tvPassword);
        final Switch switchPassword = (Switch) mView.findViewById(R.id.switchPassword);
        final LinearLayout layoutPassword = (LinearLayout) mView.findViewById(R.id.layoutPassword);
        mImage = (CircularImageView) mView.findViewById(R.id.circularProfile);

        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ClassroomActivity.this, "เพิ่มรูป",Toast.LENGTH_SHORT).show();
                changProfilePicture();
            }
        });

        layoutPassword.setVisibility(View.GONE);

        switchPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(switchPassword.isChecked()){
                    layoutPassword.setVisibility(View.VISIBLE);
                } else {
                    layoutPassword.setVisibility(View.GONE);
                }
            }
        });

        final DatabaseReference addClassroom = mDatabase.push();
        final String uid = mDatabaseUser.getKey().toString();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mProgress.setMessage("กำลังเพิ่มห้องเรียนใหม่...");


                        final String id = mSubjectID.getText().toString().trim();
                        final String name = mSubjectName.getText().toString().trim();
                        final String sec = mSec.getText().toString().trim();
                        final String password = tvPassword.getText().toString().trim();

                        /*---------- Part of "Full" Input ------------------------------------------------------------------------*/

                        if(switchPassword.isChecked() && !id.isEmpty() && !name.isEmpty() && !sec.isEmpty() && !password.isEmpty()){
                            mProgress.show();
                            if(mImageUri != null) {
                                StorageReference filepath = mStorage.child(mImageUri.getLastPathSegment());
                                filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        String downloadUri = taskSnapshot.getDownloadUrl().toString();
                                        addClassroom.child("image").setValue(downloadUri);
                                    }
                                });
                            }

                            if(mImageUri == null){
                                addClassroom.child("image").setValue(linkClass);
                            }

                            addClassroom.child("subject_id").setValue(id);
                            addClassroom.child("subject_name").setValue(name);
                            addClassroom.child("sec").setValue(sec);
                            addClassroom.child("lock").setValue("yes");
                            addClassroom.child("password").setValue(password);
                            addClassroom.child("uid").setValue(uid);
                            addClassroom.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    dialog.dismiss();
                                    mProgress.dismiss();
                                    Toast.makeText(ClassroomActivity.this, "เพิ่มห้องเรียนใหม่แล้ว", Toast.LENGTH_SHORT).show();
                                    Intent intent = getIntent();
                                    finish();
                                    startActivity(intent);
                                }
                            });


                        }

                        if(!switchPassword.isChecked() && !id.isEmpty() && !name.isEmpty() && !sec.isEmpty()){
                            mProgress.show();
                            if(mImageUri != null) {
                                StorageReference filepath = mStorage.child(mImageUri.getLastPathSegment());
                                filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        String downloadUri = taskSnapshot.getDownloadUrl().toString();
                                        addClassroom.child("image").setValue(downloadUri);
                                    }
                                });
                            }

                            if(mImageUri == null){
                                addClassroom.child("image").setValue(linkClass);
                            }

                            addClassroom.child("subject_id").setValue(id);
                            addClassroom.child("subject_name").setValue(name);
                            addClassroom.child("sec").setValue(sec);
                            addClassroom.child("lock").setValue("no");
                            addClassroom.child("password").setValue("null");
                            addClassroom.child("uid").setValue(uid);
                            addClassroom.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    dialog.dismiss();
                                    mProgress.dismiss();
                                    Toast.makeText(ClassroomActivity.this, "เพิ่มห้องเรียนใหม่แล้ว", Toast.LENGTH_SHORT).show();
                                    Intent intent = getIntent();
                                    finish();
                                    startActivity(intent);
                                }
                            });

                        }

                        /*-----------------------------------------------------------------------------------------------------*/



                        /*---------- Part of "Some" Input ------------------------------------------------------------------------*/

                        /*----- Switch ON -----*/

                        if(switchPassword.isChecked() && id.isEmpty() && name.isEmpty() && sec.isEmpty() && password.isEmpty()){
                            Toast.makeText(ClassroomActivity.this,"#1 "  + "กรุณาใส่ข้อมูลให้ครบ", Toast.LENGTH_SHORT).show();
                        }

                        if(switchPassword.isChecked() && !id.isEmpty() && name.isEmpty() && sec.isEmpty() && password.isEmpty()){
                            Toast.makeText(ClassroomActivity.this, "#2 "  + "กรุณาใส่ข้อมูลให้ครบ", Toast.LENGTH_SHORT).show();
                        }

                        if(switchPassword.isChecked() && !id.isEmpty() && !name.isEmpty() && sec.isEmpty() && password.isEmpty()){
                            Toast.makeText(ClassroomActivity.this, "#3 "  + "กรุณาใส่ข้อมูลให้ครบ", Toast.LENGTH_SHORT).show();
                        }

                        if(switchPassword.isChecked() && !id.isEmpty() && !name.isEmpty() && !sec.isEmpty() && password.isEmpty()){
                            Toast.makeText(ClassroomActivity.this, "#4 "  + "กรุณาใส่ข้อมูลให้ครบ", Toast.LENGTH_SHORT).show();
                        }

                        if(switchPassword.isChecked() && id.isEmpty() && !name.isEmpty() && sec.isEmpty() && password.isEmpty()){
                            Toast.makeText(ClassroomActivity.this, "#5 "  + "กรุณาใส่ข้อมูลให้ครบ", Toast.LENGTH_SHORT).show();
                        }

                        if(switchPassword.isChecked() && id.isEmpty() && name.isEmpty() && !sec.isEmpty() && password.isEmpty()){
                            Toast.makeText(ClassroomActivity.this, "#6 "  + "กรุณาใส่ข้อมูลให้ครบ", Toast.LENGTH_SHORT).show();
                        }

                        if(switchPassword.isChecked() && id.isEmpty() && name.isEmpty() && sec.isEmpty() && !password.isEmpty()){
                            Toast.makeText(ClassroomActivity.this, "#7 "  + "กรุณาใส่ข้อมูลให้ครบ", Toast.LENGTH_SHORT).show();
                        }

                        if(switchPassword.isChecked() && id.isEmpty() && name.isEmpty() && !sec.isEmpty() && !password.isEmpty()){
                            Toast.makeText(ClassroomActivity.this, "#8 "  + "กรุณาใส่ข้อมูลให้ครบ", Toast.LENGTH_SHORT).show();
                        }

                        if(switchPassword.isChecked() && id.isEmpty() && !name.isEmpty() && !sec.isEmpty() && password.isEmpty()){
                            Toast.makeText(ClassroomActivity.this, "#9 "  + "กรุณาใส่ข้อมูลให้ครบ", Toast.LENGTH_SHORT).show();
                        }

                        if(switchPassword.isChecked() && id.isEmpty() && !name.isEmpty() && sec.isEmpty() && !password.isEmpty()){
                            Toast.makeText(ClassroomActivity.this, "#10 "  + "กรุณาใส่ข้อมูลให้ครบ", Toast.LENGTH_SHORT).show();
                        }

                        if(switchPassword.isChecked() && id.isEmpty() && !name.isEmpty() && !sec.isEmpty() && !password.isEmpty()){
                            Toast.makeText(ClassroomActivity.this, "#11 "  + "กรุณาใส่ข้อมูลให้ครบ", Toast.LENGTH_SHORT).show();
                        }

                        if(switchPassword.isChecked() && !id.isEmpty() && name.isEmpty() && sec.isEmpty() && !password.isEmpty()){
                            Toast.makeText(ClassroomActivity.this, "#12 "  + "กรุณาใส่ข้อมูลให้ครบ", Toast.LENGTH_SHORT).show();
                        }

                        if(switchPassword.isChecked() && !id.isEmpty() && name.isEmpty() && !sec.isEmpty() && password.isEmpty()){
                            Toast.makeText(ClassroomActivity.this, "#13 "  + "กรุณาใส่ข้อมูลให้ครบ", Toast.LENGTH_SHORT).show();
                        }

                        /*----- Switch OFF -----*/

                        if(!switchPassword.isChecked() && id.isEmpty() && name.isEmpty() && sec.isEmpty()){
                            Toast.makeText(ClassroomActivity.this, "#14 "  + "กรุณาใส่ข้อมูลให้ครบ", Toast.LENGTH_SHORT).show();
                        }

                        if(!switchPassword.isChecked() && !id.isEmpty() && name.isEmpty() && sec.isEmpty()){
                            Toast.makeText(ClassroomActivity.this, "#15 "  + "กรุณาใส่ข้อมูลให้ครบ", Toast.LENGTH_SHORT).show();
                        }

                        if(!switchPassword.isChecked() && !id.isEmpty() && !name.isEmpty() && sec.isEmpty()){
                            Toast.makeText(ClassroomActivity.this, "#16 "  + "กรุณาใส่ข้อมูลให้ครบ", Toast.LENGTH_SHORT).show();
                        }

                        if(!switchPassword.isChecked() && id.isEmpty() && name.isEmpty() && !sec.isEmpty()){
                            Toast.makeText(ClassroomActivity.this, "#17 "  + "กรุณาใส่ข้อมูลให้ครบ", Toast.LENGTH_SHORT).show();
                        }

                        if(!switchPassword.isChecked() && id.isEmpty() && !name.isEmpty() && sec.isEmpty()){
                            Toast.makeText(ClassroomActivity.this, "#18 "  + "กรุณาใส่ข้อมูลให้ครบ", Toast.LENGTH_SHORT).show();
                        }

                        if(!switchPassword.isChecked() && id.isEmpty() && !name.isEmpty() && !sec.isEmpty()){
                            Toast.makeText(ClassroomActivity.this, "#19 "  + "กรุณาใส่ข้อมูลให้ครบ", Toast.LENGTH_SHORT).show();
                        }

                        if(!switchPassword.isChecked() && !id.isEmpty() && name.isEmpty() && !sec.isEmpty()){
                            Toast.makeText(ClassroomActivity.this, "#20 "  + "กรุณาใส่ข้อมูลให้ครบ", Toast.LENGTH_SHORT).show();
                        }

                        /*-----------------------------------------------------------------------------------------------------*/
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }
                });
            }
        });

    }

    private void changProfilePicture() {
        Intent gallerryIntent = new Intent();
        gallerryIntent.setAction(Intent.ACTION_GET_CONTENT);
        gallerryIntent.setType("image/*");
        startActivityForResult(gallerryIntent, GALLERY_REQUEST);
    }

    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Classroom, ClassroomViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Classroom, ClassroomViewHolder>(
                Classroom.class,
                R.layout.view_classroom,
                ClassroomViewHolder.class,
                mDatabase
        )

        {
            @Override
            protected void populateViewHolder(final ClassroomViewHolder viewHolder, final Classroom model, final int position) {


                final String Classroom_key = getRef(position).getKey();
                final String key = mDatabase.child(Classroom_key).getKey();
                final String user = mDatabaseUser.getKey();

                final String lock = model.getLock();
                final String password = model.getPassword();
                final String image = model.getImage();
                final String sid = model.getSubject_id();
                final String name = model.getSubject_name();
                final String username = model.getUsername();
                final String sec = model.getSec();

                // Set View Holder
                viewHolder.setSubject_id(model.getSubject_id());
                viewHolder.setSubject_name(model.getSubject_name());
                viewHolder.setSec(model.getSec());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setImage(getApplicationContext(), model.getImage());
                viewHolder.setLock(model.getLock());
                viewHolder.setButtonReg(Classroom_key);

                        viewHolder.mView.findViewById(R.id.btnRegClassroom).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (lock.equals("yes")) {
                                    Toast.makeText(ClassroomActivity.this, "มีรหัสผ่าน", Toast.LENGTH_SHORT).show();

                                    final AlertDialog.Builder mBuilder = new AlertDialog.Builder(ClassroomActivity.this);
                                    final View mView = getLayoutInflater().inflate(R.layout.dialog_confirm_password, null);
                                    mBuilder.setView(mView);
                                    final AlertDialog dialog = mBuilder.create();
                                    dialog.show();

                                    TextView btnCheckPassword = (TextView) mView.findViewById(R.id.btnCheckPassword);

                                    btnCheckPassword.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            EditText passwordInput = (EditText) mView.findViewById(R.id.etConfirmClassPassword);
                                            String p = passwordInput.getText().toString();

                                            if (!password.equals(p)) {
                                                Toast.makeText(ClassroomActivity.this, "รหัสผ่าน ผิด", Toast.LENGTH_SHORT).show();
                                            }
                                            if (password.equals(p)) {
                                                Toast.makeText(ClassroomActivity.this, "รหัสผ่าน ถูกต้อง", Toast.LENGTH_SHORT).show();

                                                //*Add Source for reg Classroom to...
                                                mDatabaseUser.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        mDatabaseRagClass.child(key).child(user).child("status").setValue("reg");
                                                        mDatabaseUserGetClass.child(user).child(key).child("status").setValue("reg");
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                        Intent intent = getIntent();
                                                        finish();
                                                        startActivity(intent);
                                                    }
                                                });

                                                dialog.dismiss();
                                                finish();
                                                startActivity(new Intent(ClassroomActivity.this, ProfileActivity.class));
                                            }
                                        }
                                    });
                                }

                                if (lock.equals("no")) {
                                    Toast.makeText(ClassroomActivity.this, "ไม่มีรหัสผ่าน", Toast.LENGTH_SHORT).show();

                                    final AlertDialog.Builder mBuilder = new AlertDialog.Builder(ClassroomActivity.this);
                                    final View mView = getLayoutInflater().inflate(R.layout.dialog_confirm_regclass, null);

                                    final TextView mSubjectID = (TextView) mView.findViewById(R.id.tv_subject_id);
                                    final TextView mSubjectName = (TextView) mView.findViewById(R.id.tv_subject_name);
                                    final TextView mSec = (TextView) mView.findViewById(R.id.tv_subject_sec);

                                    mSubjectID.setText(sid);
                                    mSubjectName.setText(name);
                                    mSec.setText("Sec." + sec);

                                    mBuilder.setView(mView);
                                    final AlertDialog dialog = mBuilder.create();
                                    dialog.show();

                                    TextView btnConfirmReg = (TextView) mView.findViewById(R.id.btnConfirmReg);
                                    TextView btnConfirmCancle = (TextView) mView.findViewById(R.id.btnConfirmCancle);

                                    btnConfirmReg.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //*Add Source for reg Classroom to...
                                            mDatabaseUser.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    String key = mDatabase.child(Classroom_key).getKey();

                                                    mDatabaseRagClass.child(key).child(user).child("status").setValue("reg");
                                                    mDatabaseUserGetClass.child(user).child(key).child("status").setValue("reg");
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    Intent intent = getIntent();
                                                    finish();
                                                    startActivity(intent);
                                                }
                                            });
                                            dialog.dismiss();


                                        }
                                    });

                                    btnConfirmCancle.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                        }
                                    });
                                }

                            }

                        });

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ClassroomActivity.this);
                        View mView = getLayoutInflater().inflate(R.layout.dialog_view_classroom, null);

                        final TextView mSubjectID = (TextView) mView.findViewById(R.id.tv_subject_id);
                        final TextView mSubjectName = (TextView) mView.findViewById(R.id.tv_subject_name);
                        final TextView mUsername = (TextView) mView.findViewById(R.id.tv_subject_username);
                        final TextView mSec = (TextView) mView.findViewById(R.id.tv_subject_sec);
                        final CircularImageView mImage = (CircularImageView) mView.findViewById(R.id.circularProfile);

                        mDatabase.child(Classroom_key).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String classroom_subjectId = (String) dataSnapshot.child("subject_id").getValue();
                                String classroom_subjectName = (String) dataSnapshot.child("subject_name").getValue();
                                String classroom_username = (String) dataSnapshot.child("username").getValue();
                                String classroom_sec = (String) dataSnapshot.child("sec").getValue();
                                String classroom_image = (String) dataSnapshot.child("image").getValue();

                                mSubjectID.setText(classroom_subjectId);
                                mSubjectName.setText(classroom_subjectName);
                                mUsername.setText(classroom_username);
                                mSec.setText(classroom_sec);

                                Picasso.with(ClassroomActivity.this).load(classroom_image).into(mImage);

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

                        TextView btnExit = (TextView) mView.findViewById(R.id.btnExit);

                        btnExit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }
                });
            }
        };
        viewClassroom.setAdapter(firebaseRecyclerAdapter);
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
                mImageUri = result.getUri();
                mImage.setImageURI(mImageUri);
            }
        }
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
                        startFirebase();
                            /*---------*/
                    }
                },2000);
            }
        });
    }

    private void initView() {

        mSwipe = (SwipeRefreshLayout) findViewById(R.id.layoutAuto);

        tvAddClassroom = (ImageView) findViewById(R.id.tvAddClassroom);
        btnBack = (ImageView) findViewById(R.id.btnBack);
        viewClassroom = (RecyclerView) findViewById(R.id.viewClassroom);

    }

    private void initListener() {

        tvAddClassroom.setOnClickListener(this);
        btnBack.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if(v == tvAddClassroom){
            startDialogAddClassroom();
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
