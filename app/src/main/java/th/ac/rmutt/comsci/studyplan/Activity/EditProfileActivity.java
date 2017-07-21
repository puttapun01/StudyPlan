package th.ac.rmutt.comsci.studyplan.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import th.ac.rmutt.comsci.studyplan.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static th.ac.rmutt.comsci.studyplan.R.id.editTextName;
import static th.ac.rmutt.comsci.studyplan.R.id.editTextStudentId;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseStatus;
    private FirebaseUser mCurrentUser;
    private StorageReference mStorageImage;

    private TextView textViewUserEmail;

    private ImageView btnBack;

    private FrameLayout frameChangProfile;
    private LinearLayout btnEditStid, btnEditName, btnEditLevel, btnEditFaculty, btnEditStatus;
    private TextView textViewStid, textViewName, textViewLevel, textViewFaculty, textViewStatus;

    private CircularImageView circularProfile;

    private static final int GALLERY_REQUEST = 1;

    private Uri mImageUri = null;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        final String current_uid = mCurrentUser.getUid();
        mStorageImage = FirebaseStorage.getInstance().getReference().child("Profile_images");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mDatabaseStatus = FirebaseDatabase.getInstance().getReference().child("Status");

        initView();
        initListener();

        progressDialog = new ProgressDialog(this);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String stid = dataSnapshot.child("stid").getValue().toString();
                String name = dataSnapshot.child("name").getValue().toString();
                String faculty = dataSnapshot.child("faculty").getValue().toString();
                String level = dataSnapshot.child("level").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                textViewStid.setText(stid);
                textViewName.setText(name);
                textViewFaculty.setText(faculty);
                textViewLevel.setText(level);
                textViewStatus.setText(status);

                Picasso.with(EditProfileActivity.this).load(image).placeholder(R.drawable.im_profile).into(circularProfile);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                /*throw databaseError.toException();*/
                finish();
            }
        });

        FirebaseUser user = firebaseAuth.getCurrentUser();

        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        textViewUserEmail.setText(user.getEmail());



        dialogControl();
    }



    private void dialogControl() {
        startBtnEditStid();
        startBtnEditName();
        startBtnEditLevel();
        startBtnEditFaculty();
        startBtnEditStatus();
    }

    private void startBtnEditStid() {
        btnEditStid.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(EditProfileActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_edit_stid, null);
                final EditText mStudentId = (EditText) mView.findViewById(editTextStudentId);
                TextView btnSave = (TextView) mView.findViewById(R.id.btnSave);

                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String stid = dataSnapshot.child("stid").getValue().toString();
                        mStudentId.setHint(stid);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        /*throw databaseError.toException();*/
                        finish();
                    }
                });

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!mStudentId.getText().toString().isEmpty()){
                            Toast.makeText(EditProfileActivity.this, "เปลี่ยนแปลงข้อมูลแล้ว", Toast.LENGTH_SHORT).show();
                            final String stid = mStudentId.getText().toString().trim();
                            mDatabase.child("stid").setValue(stid);
                            dialog.dismiss();
                        }else{
                            Toast.makeText(EditProfileActivity.this, "กรุณาใส่ข้อมูล",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        });
    }

    private void startBtnEditName() {
        btnEditName.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(EditProfileActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_edit_name, null);
                final EditText mName = (EditText) mView.findViewById(editTextName);
                TextView btnSave = (TextView) mView.findViewById(R.id.btnSave);

                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String stid = dataSnapshot.child("name").getValue().toString();
                        mName.setHint(stid);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        finish();
                        /*throw databaseError.toException();*/
                    }
                });

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!mName.getText().toString().isEmpty()){
                            Toast.makeText(EditProfileActivity.this, "เปลี่ยนแปลงข้อมูลแล้ว", Toast.LENGTH_SHORT).show();
                            final String name = mName.getText().toString().trim();
                            mDatabase.child("name").setValue(name);
                            dialog.dismiss();
                        }else{
                            Toast.makeText(EditProfileActivity.this, "กรุณาใส่ข้อมูล",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void startBtnEditLevel() {
        btnEditLevel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(EditProfileActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_edit_level, null);
                final TextView mLevel1 = (TextView) mView.findViewById(R.id.textViewNum1);
                final TextView mLevel2 = (TextView) mView.findViewById(R.id.textViewNum2);
                final TextView mLevel3 = (TextView) mView.findViewById(R.id.textViewNum3);
                final TextView mLevel4 = (TextView) mView.findViewById(R.id.textViewNum4);
                final TextView mLevel5 = (TextView) mView.findViewById(R.id.textViewNum5);

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                mLevel1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(EditProfileActivity.this, "เปลี่ยนแปลงข้อมูลแล้ว", Toast.LENGTH_SHORT).show();
                        final String level = mLevel1.getText().toString().trim();
                        mDatabase.child("level").setValue(level);
                        dialog.dismiss();
                    }
                });

                mLevel2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(EditProfileActivity.this, "เปลี่ยนแปลงข้อมูลแล้ว", Toast.LENGTH_SHORT).show();
                        final String level = mLevel2.getText().toString().trim();
                        mDatabase.child("level").setValue(level);
                        dialog.dismiss();
                    }
                });

                mLevel3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(EditProfileActivity.this, "เปลี่ยนแปลงข้อมูลแล้ว", Toast.LENGTH_SHORT).show();
                        final String level = mLevel3.getText().toString().trim();
                        mDatabase.child("level").setValue(level);
                        dialog.dismiss();
                    }
                });

                mLevel4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(EditProfileActivity.this, "เปลี่ยนแปลงข้อมูลแล้ว", Toast.LENGTH_SHORT).show();
                        final String level = mLevel4.getText().toString().trim();
                        mDatabase.child("level").setValue(level);
                        dialog.dismiss();
                    }
                });

                mLevel5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(EditProfileActivity.this, "เปลี่ยนแปลงข้อมูลแล้ว", Toast.LENGTH_SHORT).show();
                        final String level = mLevel5.getText().toString().trim();
                        mDatabase.child("level").setValue(level);
                        dialog.dismiss();
                    }
                });
            }
        });

    }

    private void startBtnEditFaculty() {
        btnEditFaculty.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(EditProfileActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_edit_faculty, null);
                final TextView mFac1 = (TextView) mView.findViewById(R.id.textViewFac1);
                final TextView mFac2 = (TextView) mView.findViewById(R.id.textViewFac2);
                final TextView mFac3 = (TextView) mView.findViewById(R.id.textViewFac3);
                final TextView mFac4 = (TextView) mView.findViewById(R.id.textViewFac4);
                final TextView mFac5 = (TextView) mView.findViewById(R.id.textViewFac5);
                final TextView mFac6 = (TextView) mView.findViewById(R.id.textViewFac6);
                final TextView mFac7 = (TextView) mView.findViewById(R.id.textViewFac7);
                final TextView mFac8 = (TextView) mView.findViewById(R.id.textViewFac8);
                final TextView mFac9 = (TextView) mView.findViewById(R.id.textViewFac9);
                final TextView mFac10 = (TextView) mView.findViewById(R.id.textViewFac10);
                final TextView mFac11 = (TextView) mView.findViewById(R.id.textViewFac11);
                final TextView mFac12 = (TextView) mView.findViewById(R.id.textViewFac12);

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                mFac1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(EditProfileActivity.this, "เปลี่ยนแปลงข้อมูลแล้ว", Toast.LENGTH_SHORT).show();
                        final String faculty = mFac1.getText().toString().trim();
                        mDatabase.child("faculty").setValue(faculty);
                        dialog.dismiss();
                    }
                });

                mFac2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(EditProfileActivity.this, "เปลี่ยนแปลงข้อมูลแล้ว", Toast.LENGTH_SHORT).show();
                        final String faculty = mFac2.getText().toString().trim();
                        mDatabase.child("faculty").setValue(faculty);
                        dialog.dismiss();
                    }
                });

                mFac3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(EditProfileActivity.this, "เปลี่ยนแปลงข้อมูลแล้ว", Toast.LENGTH_SHORT).show();
                        final String faculty = mFac3.getText().toString().trim();
                        mDatabase.child("faculty").setValue(faculty);
                        dialog.dismiss();
                    }
                });

                mFac4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(EditProfileActivity.this, "เปลี่ยนแปลงข้อมูลแล้ว", Toast.LENGTH_SHORT).show();
                        final String faculty = mFac4.getText().toString().trim();
                        mDatabase.child("faculty").setValue(faculty);
                        dialog.dismiss();
                    }
                });

                mFac5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(EditProfileActivity.this, "เปลี่ยนแปลงข้อมูลแล้ว", Toast.LENGTH_SHORT).show();
                        final String faculty = mFac5.getText().toString().trim();
                        mDatabase.child("faculty").setValue(faculty);
                        dialog.dismiss();
                    }
                });

                mFac6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(EditProfileActivity.this, "เปลี่ยนแปลงข้อมูลแล้ว", Toast.LENGTH_SHORT).show();
                        final String faculty = mFac6.getText().toString().trim();
                        mDatabase.child("faculty").setValue(faculty);
                        dialog.dismiss();
                    }
                });

                mFac7.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(EditProfileActivity.this, "เปลี่ยนแปลงข้อมูลแล้ว", Toast.LENGTH_SHORT).show();
                        final String faculty = mFac7.getText().toString().trim();
                        mDatabase.child("faculty").setValue(faculty);
                        dialog.dismiss();
                    }
                });

                mFac8.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(EditProfileActivity.this, "เปลี่ยนแปลงข้อมูลแล้ว", Toast.LENGTH_SHORT).show();
                        final String faculty = mFac8.getText().toString().trim();
                        mDatabase.child("faculty").setValue(faculty);
                        dialog.dismiss();
                    }
                });

                mFac9.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(EditProfileActivity.this, "เปลี่ยนแปลงข้อมูลแล้ว", Toast.LENGTH_SHORT).show();
                        final String faculty = mFac9.getText().toString().trim();
                        mDatabase.child("faculty").setValue(faculty);
                        dialog.dismiss();
                    }
                });

                mFac10.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(EditProfileActivity.this, "เปลี่ยนแปลงข้อมูลแล้ว", Toast.LENGTH_SHORT).show();
                        final String faculty = mFac10.getText().toString().trim();
                        mDatabase.child("faculty").setValue(faculty);
                        dialog.dismiss();
                    }
                });

                mFac11.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(EditProfileActivity.this, "เปลี่ยนแปลงข้อมูลแล้ว", Toast.LENGTH_SHORT).show();
                        final String faculty = mFac11.getText().toString().trim();
                        mDatabase.child("faculty").setValue(faculty);
                        dialog.dismiss();
                    }
                });

                mFac12.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(EditProfileActivity.this, "เปลี่ยนแปลงข้อมูลแล้ว", Toast.LENGTH_SHORT).show();
                        final String faculty = mFac12.getText().toString().trim();
                        mDatabase.child("faculty").setValue(faculty);
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private void startBtnEditStatus() {
        btnEditStatus.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(EditProfileActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_edit_status, null);
                final TextView mStatus1 = (TextView) mView.findViewById(R.id.textViewStatus1);
                final TextView mStatus2 = (TextView) mView.findViewById(R.id.textViewStatus2);
                final String getId = mDatabase.getKey().toString();

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                mStatus1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(EditProfileActivity.this, "เปลี่ยนแปลงข้อมูลแล้ว", Toast.LENGTH_SHORT).show();
                        final String status = mStatus1.getText().toString().trim();
                        mDatabase.child("status").setValue(status);
                        mDatabase.child("status_id").setValue("teacher");
                        mDatabaseStatus.child("teacher").child(getId).child("status").setValue("teacher");
                        mDatabaseStatus.child("student").child(getId).child("status").removeValue();

                        dialog.dismiss();
                    }
                });

                mStatus2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(EditProfileActivity.this, "เปลี่ยนแปลงข้อมูลแล้ว", Toast.LENGTH_SHORT).show();
                        final String status = mStatus2.getText().toString().trim();
                        mDatabase.child("status").setValue(status);
                        mDatabase.child("status_id").setValue("student");
                        mDatabaseStatus.child("student").child(getId).child("status").setValue("student");
                        mDatabaseStatus.child("teacher").child(getId).child("status").removeValue();
                        dialog.dismiss();
                    }
                });
            }
        });

    }

    private void initView() {
        //LinearLayout
        btnEditStid = (LinearLayout) findViewById(R.id.btnEditStid);
        btnEditName = (LinearLayout) findViewById(R.id.btnEditName);
        btnEditLevel = (LinearLayout) findViewById(R.id.btnEditLevel);
        btnEditFaculty = (LinearLayout) findViewById(R.id.btnEditFaculty);
        btnEditStatus = (LinearLayout) findViewById(R.id.btnEditStatus);
        //TextView
        textViewStid = (TextView) findViewById(R.id.textViewStid);
        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewLevel = (TextView) findViewById(R.id.textViewLevel);
        textViewFaculty = (TextView) findViewById(R.id.textViewFaculty);
        textViewStatus = (TextView) findViewById(R.id.textViewStatus);
        //CircularImageView
        circularProfile = (CircularImageView) findViewById(R.id.circularProfile);
        //FrameLayout
        frameChangProfile = (FrameLayout) findViewById(R.id.frameChangProfile);

        btnBack = (ImageView) findViewById(R.id.btnBack);
    }

    private void changProfilePicture() {
        Intent gallerryIntent = new Intent();
        gallerryIntent.setAction(Intent.ACTION_GET_CONTENT);
        gallerryIntent.setType("image/*");
        startActivityForResult(gallerryIntent, GALLERY_REQUEST);
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
                            mDatabase.child("image").setValue(downloadUri);
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

    private void initListener() {

        frameChangProfile.setOnClickListener(this);
        btnBack.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v == frameChangProfile){
            changProfilePicture();
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