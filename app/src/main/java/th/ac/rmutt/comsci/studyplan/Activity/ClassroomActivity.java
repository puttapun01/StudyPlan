package th.ac.rmutt.comsci.studyplan.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
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

import th.ac.rmutt.comsci.studyplan.Classroom;
import th.ac.rmutt.comsci.studyplan.ClassroomViewHolder;
import th.ac.rmutt.comsci.studyplan.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ClassroomActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvAddClassroom;
    private TextView tvSearchClassroom;

    private ProgressDialog mProgress;

    private RecyclerView viewClassroom;

    private CircularImageView mImage;

    // firebase Connect
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseAddClass;
    private DatabaseReference mDatabaseReg;
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

        mProgress = new ProgressDialog(this);

        viewClassroom.setHasFixedSize(true);
        viewClassroom.setLayoutManager(new LinearLayoutManager(this));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        viewClassroom.setLayoutManager(layoutManager);

        mImage = (CircularImageView) findViewById(R.id.circularProfile);

    }

    private void startFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference().child("Classroom_images");
        mCurrentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("ClassRoom");
        mDatabaseAddClass = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid()).child("RegClass");
        mDatabaseReg = FirebaseDatabase.getInstance().getReference().child("ClassRoom").child(mDatabase.getKey());
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
    }

    private void startDialogAddClassroom() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ClassroomActivity.this);
        final View mView = getLayoutInflater().inflate(R.layout.dialog_add_classroom, null);

        mImage = (CircularImageView) mView.findViewById(R.id.circularProfile);

        final EditText mSubjectID = (EditText) mView.findViewById(R.id.editTextSubjectID);
        final EditText mSubjectName = (EditText) mView.findViewById(R.id.editTexctSubjectName);
        final EditText mSec = (EditText) mView.findViewById(R.id.editTextSec);
        final EditText mClassPassword = (EditText) mView.findViewById(R.id.editTextClassPassword);
        final RadioButton mPassYes = (RadioButton) mView.findViewById(R.id.radioButtonHavePassword);
        final RadioButton mPassNo = (RadioButton) mView.findViewById(R.id.radioButtonNotPassword);

        TextView btnSave = (TextView) mView.findViewById(R.id.btnSave);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ClassroomActivity.this, "เพิ่มรูป",Toast.LENGTH_SHORT).show();
                changProfilePicture();
            }

            private void changProfilePicture() {
                Intent gallerryIntent = new Intent();
                gallerryIntent.setAction(Intent.ACTION_GET_CONTENT);
                gallerryIntent.setType("image/*");
                startActivityForResult(gallerryIntent, GALLERY_REQUEST);
            }

        });

        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mProgress.setMessage("กำลังเพิ่มกลุ่มเรียนใหม่...");

                if(mSubjectID.getText().toString().isEmpty() && mSubjectName.getText().toString().isEmpty() && mSec.getText().toString().isEmpty())
                {
                    Toast.makeText(ClassroomActivity.this, "กรุณาใส่ข้อมูลให้ครบ",Toast.LENGTH_SHORT).show();
                }

                if(!mSubjectID.getText().toString().isEmpty() &&
                        !mSubjectName.getText().toString().isEmpty() &&
                        !mSec.getText().toString().isEmpty())
                {
                    if(mPassYes.isChecked()){
                        if(mClassPassword.getText().toString().isEmpty()){
                            Toast.makeText(ClassroomActivity.this, "กรุณาใส่ข้อมูลให้ครบ",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    mProgress.show();

                    final DatabaseReference newPost = mDatabase.push();
                    final String SubjectID = mSubjectID.getText().toString().trim();
                    final String SubjectName = mSubjectName.getText().toString().trim();
                    final String Sec = mSec.getText().toString().trim();

                    mDatabaseUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            newPost.child("subject_id").setValue(SubjectID);
                            newPost.child("subject_name").setValue(SubjectName);
                            newPost.child("sec").setValue(Sec);
                            newPost.child("uid").setValue(mCurrentUser.getUid());

                            if(mImageUri != null) {
                                StorageReference filepath = mStorage.child(mImageUri.getLastPathSegment());
                                filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        String downloadUri = taskSnapshot.getDownloadUrl().toString();
                                        newPost.child("image").setValue(downloadUri);

                                    }
                                });
                            }

                            if(mImageUri == null) {
                                newPost.child("image").setValue("https://firebasestorage.googleapis.com/v0/b/studyplan-cb45d.appspot.com/o/Classroom_images%2Fim_profile_group.jpg?alt=media&token=19bc052a-0e8b-4e43-9e7e-ac66e0d71083");
                            }

                            if(mPassYes.isChecked()){
                                final String ClassPassword = mClassPassword.getText().toString().trim();
                                newPost.child("password").setValue(ClassPassword);
                                newPost.child("lock").setValue("yes");

                            }

                            if(mPassNo.isChecked()){
                                newPost.child("password").setValue("null");
                                newPost.child("lock").setValue("no");
                            }

                            newPost.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    /*mProgress.dismiss();*/
                                    Toast.makeText(ClassroomActivity.this, "เพิ่มกลุ่มเรียนใหม่แล้ว", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    dialog.dismiss();
                    finish();
                    startActivity(new Intent(ClassroomActivity.this, ClassroomActivity.class));
                }
            }
        });

    }

    private void startSearchClassroom() {

    }

    private void initView() {
        tvAddClassroom = (TextView) findViewById(R.id.tvAddClassroom);
        tvSearchClassroom = (TextView) findViewById(R.id.tvSearchClassroom);
        viewClassroom = (RecyclerView) findViewById(R.id.viewClassroom);

    }

    private void initListener() {
        tvAddClassroom.setOnClickListener(this);
        tvSearchClassroom.setOnClickListener(this);
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
            protected void populateViewHolder(final ClassroomViewHolder viewHolder, Classroom model, final int position) {

                final String Classroom_key = getRef(position).getKey();
                final String lock = model.getLock();
                final String password = model.getPassword();
                final DatabaseReference newClassroom = mDatabaseAddClass.push();

                final String image = model.getImage();
                final String sid = model.getSubject_id();
                final String name = model.getSubject_name();
                final String username = model.getUsername();
                final String sec = model.getSec();

                viewHolder.setSubject_id(model.getSubject_id());
                viewHolder.setSubject_name(model.getSubject_name());
                viewHolder.setSec(model.getSec());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setImage(getApplicationContext(), model.getImage());
                viewHolder.setLock(model.getLock());


                viewHolder.mView.findViewById(R.id.btnRegClassroom).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(lock.equals("yes")){
                            Toast.makeText(ClassroomActivity.this, "มีรหัสผ่าน",Toast.LENGTH_SHORT).show();

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

                                    if(!password.equals(p)){
                                        Toast.makeText(ClassroomActivity.this, "รหัสผ่าน ผิด",Toast.LENGTH_SHORT).show();
                                    }
                                    if(password.equals(p)){
                                        Toast.makeText(ClassroomActivity.this, "รหัสผ่าน ถูกต้อง" ,Toast.LENGTH_SHORT).show();

                                        //*Add Source for reg Classroom to...
                                        mDatabaseUser.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                String key = mDatabase.child(Classroom_key).getKey();

                                                newClassroom.child("uid").setValue(key);
                                                newClassroom.child("image").setValue(image);
                                                newClassroom.child("subject_id").setValue(sid);
                                                newClassroom.child("subject_name").setValue(name);
                                                newClassroom.child("username").setValue(username);
                                                newClassroom.child("sec").setValue(sec);
                                        }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });



                                        dialog.dismiss();
                                        finish();
                                        startActivity(new Intent(ClassroomActivity.this, ProfileActivity.class));
                                    }
                                }
                            });
                        }

                        if(lock.equals("no")){
                            Toast.makeText(ClassroomActivity.this, "ไม่มีรหัสผ่าน",Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClick(View v) {

        if(v == tvAddClassroom){
            startDialogAddClassroom();
        }
        if(v == tvSearchClassroom){
            startSearchClassroom();
        }

    }

    @Override
    protected void attachBaseContext (Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
