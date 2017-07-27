package th.ac.rmutt.comsci.studyplan.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Calendar;

import th.ac.rmutt.comsci.studyplan.Adapter.Homework;
import th.ac.rmutt.comsci.studyplan.Adapter.HomeworkViewHolder;
import th.ac.rmutt.comsci.studyplan.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SendHomeworkActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout.LayoutParams setLayoutClose;
    private LinearLayout.LayoutParams setLayoutOpen;

    private StorageReference mStorageImage;
    private StorageReference mStorageFile;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseHomework;
    private DatabaseReference mDatabaseAll;
    private String current_uid;

    private int mYear, mMonth, mDay, mHour, mMinute;
    private String dateStamp = null;
    private String timeStamp = null;

    private static final int GALLERY_REQUEST = 1;
    private static final int FILE_REQUEST = 1;

    private String fileImport = null;
    private String imageImport = null;

    private Uri mFileUri = null;
    private Uri mImageUri = null;

    private SwipeRefreshLayout mSwipe;

    private EditText etHomework;

    private RecyclerView rvHomework;
    private LinearLayout llShowAll;

    private FrameLayout flImage;
    private FrameLayout flFile;

    private ImageView btnBack;
    private ImageView ivImage;
    private ImageView btnDeleteImage;
    private ImageView ivFile;
    private ImageView btnDeleteFile;
    private ImageView btnImage;
    private ImageView btnFile;
    private ImageView btnSend;

    private String post_key = null;

    private ProgressDialog mProgress;

    private FirebaseRecyclerAdapter<Homework, HomeworkViewHolder> homeworkAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_homework);

        initView();
        initListener();
        startFirebase();
        startStamp();
        startViewList();
        startRefresh();

        mProgress = new ProgressDialog(this);
        
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
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        current_uid = mCurrentUser.getUid();

        post_key = getIntent().getExtras().getString("post_key");

        mDatabaseHomework = FirebaseDatabase.getInstance().getReference().child("Homework");
        mDatabaseAll = FirebaseDatabase.getInstance().getReference().child("Users");

        mStorageImage = FirebaseStorage.getInstance().getReference().child("Homework_images");
        mStorageFile = FirebaseStorage.getInstance().getReference().child("Homework_files");

    }

    private void startStamp() {
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        timeStamp = mHour + ":" + mMinute +" น.";

        final String newMonth;
        String newMonth1 = null;
        if(mMonth+1 == 1){
            newMonth1 = "มกราคม";
        }
        if(mMonth+1 == 2){
            newMonth1 = "กุมภาพันธ์";
        }
        if(mMonth+1 == 3){
            newMonth1 = "มีนาคม";
        }
        if(mMonth+1 == 4){
            newMonth1 = "เมษายน";
        }
        if(mMonth+1 == 5){
            newMonth1 = "พฤษภาคม";
        }
        if(mMonth+1 == 6){
            newMonth1 = "มิถุนายน";
        }
        if(mMonth+1 == 7){
            newMonth1 = "กรกฎาคม";
        }
        if(mMonth+1 == 8){
            newMonth1 = "สิงหาคม";
        }
        if(mMonth+1 == 9){
            newMonth1 = "กันยายน";
        }
        if(mMonth+1 == 10){
            newMonth1 = "ตุลาคม";
        }
        if(mMonth+1 == 11){
            newMonth1 = "พฤศจิกายน";
        }
        if(mMonth+1 == 12){
            newMonth1 = "ธันวาคม";
        }
        newMonth = newMonth1;
        dateStamp = mDay + " "  + newMonth + " " + (mYear+543);
    }

    private void startViewList() {

        rvHomework.setHasFixedSize(true);
        rvHomework.setLayoutManager(new LinearLayoutManager(SendHomeworkActivity.this));

        LinearLayoutManager layoutHomework = new LinearLayoutManager(SendHomeworkActivity.this);
        layoutHomework.setReverseLayout(true);
        layoutHomework.setStackFromEnd(true);

        rvHomework.setLayoutManager(layoutHomework);

        homeworkAdapter = new FirebaseRecyclerAdapter<Homework, HomeworkViewHolder>(
                Homework.class,
                R.layout.view_homework,
                HomeworkViewHolder.class,
                mDatabaseHomework.child(post_key)
        ) {
            @Override
            protected void populateViewHolder(final HomeworkViewHolder viewHolder, Homework model, int position) {

                String hw_key = getRef(position).getKey();

                viewHolder.setH_text(model.getH_text());
                viewHolder.setTimestamp(model.getTimestamp());
                viewHolder.setDatestamp(model.getDatestamp());
                viewHolder.setH_image(model.getH_image());
                viewHolder.setH_file(model.getH_file());

                final String imageHw = model.getH_image();
                final String fileHw = model.getH_file();

                viewHolder.mView.findViewById(R.id.llFile).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent singleClassIntent = new Intent(SendHomeworkActivity.this, ViewFilePostActivity.class);
                        singleClassIntent.putExtra("filePostUri", fileHw);
                        startActivity(singleClassIntent);
                    }
                });

                viewHolder.mView.findViewById(R.id.llImage).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent singleClassIntent = new Intent(SendHomeworkActivity.this, ViewImagePostActivity.class);
                        singleClassIntent.putExtra("imagePostUri", imageHw);
                        startActivity(singleClassIntent);
                    }
                });

                mDatabaseHomework.child(post_key).child(current_uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String uid = dataSnapshot.child("uid").getValue().toString();

                        mDatabaseAll.child(uid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String username = dataSnapshot.child("name").getValue().toString();
                                String imageProfile = dataSnapshot.child("image").getValue().toString();
                                String stid = dataSnapshot.child("stid").getValue().toString();

                                viewHolder.setImageProfile(getApplicationContext(), imageProfile);
                                viewHolder.setUsername(username);
                                viewHolder.setStid(stid);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        rvHomework.setAdapter(homeworkAdapter);

    }

    private void startSelectImage() {

        Intent gallerryIntent = new Intent();
        gallerryIntent.setAction(Intent.ACTION_GET_CONTENT);
        gallerryIntent.setType("image/*");
        startActivityForResult(gallerryIntent, GALLERY_REQUEST);
        imageImport = new String(imageImport = "yes");
        rvHomework.setLayoutParams(setLayoutOpen);

    }

    private void startSelectFile() {

        Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        fileIntent.setType("file/*");
        startActivityForResult(fileIntent, FILE_REQUEST);
        fileImport = new String(fileImport = "OK");
        rvHomework.setLayoutParams(setLayoutOpen);

    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri imageUri = data.getData();
        Uri fileUri = data.getData();

        String imageCheck = "yes";
        String fileCheck = "OK";

        if(requestCode == FILE_REQUEST && resultCode == RESULT_OK && fileCheck.equals(fileImport)){
            mFileUri = fileUri;
            ivFile.setBackgroundResource(R.color.blue_info);
            btnDeleteFile.setVisibility(View.VISIBLE);
        }

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && imageCheck.equals(imageImport)) {
            CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageUri = result.getUri();
            btnDeleteImage.setVisibility(View.VISIBLE);
            ivImage.setImageURI(Uri.parse(String.valueOf(mImageUri)));

        }

        imageImport = new String(imageImport = "no");
        fileImport = new String(fileImport = "O");

    }

    private void startViewImage() {

        String imageUri = mImageUri.toString();

        if(imageUri != null){
            Intent singleClassIntent = new Intent(SendHomeworkActivity.this, ViewImageActivity.class);
            singleClassIntent.putExtra("imageUri", imageUri);
            startActivity(singleClassIntent);
        }

        else {
            Toast.makeText(SendHomeworkActivity.this, "ไม่มีไฟล์",Toast.LENGTH_SHORT).show();
        }

    }

    private void startViewFile() {

        String fileUri = mFileUri.toString();

        if(fileUri != null){
            Intent singleClassIntent = new Intent(SendHomeworkActivity.this, ViewFileActivity.class);
            singleClassIntent.putExtra("fileUri", fileUri);
            startActivity(singleClassIntent);
        }

        else {
            Toast.makeText(SendHomeworkActivity.this, "ไม่มีไฟล์",Toast.LENGTH_SHORT).show();
        }

    }

    private void clearImage() {

        mImageUri = null;
        ivImage.setImageResource(R.drawable.im_profile);
        btnDeleteImage.setVisibility(View.GONE);

    }

    private void clearFile() {

        mFileUri = null;
        ivFile.setBackgroundColor(Color.GRAY);
        btnDeleteFile.setVisibility(View.GONE);

    }

    private void startSendHomework() {

        final DatabaseReference addHomeWork = mDatabaseHomework.child(post_key).child(current_uid);

        String h_text = etHomework.getText().toString();
        String uid = mCurrentUser.getUid();

        if(h_text.equals("")){
            Toast.makeText(SendHomeworkActivity.this, "กรุณาพิมพ์ข้อความ", Toast.LENGTH_LONG).show();
        }

        if(!h_text.equals("")){

            mProgress.setMessage("กำลังโพสการบ้าน...");
            mProgress.setCanceledOnTouchOutside(true);
            mProgress.show();

            addHomeWork.child("h_text").setValue(h_text);
            addHomeWork.child("uid").setValue(uid);
            addHomeWork.child("timestamp").setValue(timeStamp);
            addHomeWork.child("datestamp").setValue(dateStamp);


            if(mImageUri != null && mFileUri != null) {

                Toast.makeText(SendHomeworkActivity.this, "#01",Toast.LENGTH_SHORT).show();

                StorageReference imagepath = mStorageImage.child(mImageUri.getLastPathSegment());
                imagepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String downloadUri = taskSnapshot.getDownloadUrl().toString();
                        addHomeWork.child("h_image").setValue(downloadUri);

                        final StorageReference filepath = mStorageFile.child(mFileUri.getLastPathSegment());
                        filepath.putFile(mFileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                String downloadUri = taskSnapshot.getDownloadUrl().toString();
                                addHomeWork.child("h_file").setValue(downloadUri);
                                mProgress.dismiss();
                            }
                        });
                    }
                });

            }

            if(mImageUri == null && mFileUri != null) {
                Toast.makeText(SendHomeworkActivity.this, "#02",Toast.LENGTH_SHORT).show();
                StorageReference filepath = mStorageFile.child(mFileUri.getLastPathSegment());
                filepath.putFile(mFileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String downloadUri = taskSnapshot.getDownloadUrl().toString();
                        addHomeWork.child("h_file").setValue(downloadUri);
                        addHomeWork.child("h_image").setValue("null");
                        mProgress.dismiss();
                    }
                });
            }

            if(mImageUri != null && mFileUri == null) {
                Toast.makeText(SendHomeworkActivity.this, "#03",Toast.LENGTH_SHORT).show();
                StorageReference imagepath = mStorageImage.child(mImageUri.getLastPathSegment());
                imagepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String downloadUri = taskSnapshot.getDownloadUrl().toString();
                        addHomeWork.child("h_image").setValue(downloadUri);
                        addHomeWork.child("h_file").setValue("null");
                        mProgress.dismiss();
                    }
                });
            }

            /*- - ------------ Upload Zone -------------------*/

            if(mImageUri == null && mFileUri == null) {
                Toast.makeText(SendHomeworkActivity.this, "#04",Toast.LENGTH_SHORT).show();
                addHomeWork.child("h_image").setValue("null");
                addHomeWork.child("h_file").setValue("null");
                mProgress.dismiss();
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }

            else {
                if(mImageUri != null && mFileUri != null) {
                    Toast.makeText(SendHomeworkActivity.this, "#05",Toast.LENGTH_SHORT).show();
                    StorageReference imagepath = mStorageImage.child(mImageUri.getLastPathSegment());
                    imagepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String downloadUri = taskSnapshot.getDownloadUrl().toString();
                            addHomeWork.child("h_image").setValue(downloadUri);

                            final StorageReference filepath = mStorageFile.child(mFileUri.getLastPathSegment());
                            filepath.putFile(mFileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    String downloadUri = taskSnapshot.getDownloadUrl().toString();
                                    addHomeWork.child("h_file").setValue(downloadUri);
                                    mProgress.dismiss();
                                    Intent intent = getIntent();
                                    finish();
                                    startActivity(intent);
                                }
                            });
                        }
                    });

                }

                if(mImageUri == null && mFileUri != null) {
                    Toast.makeText(SendHomeworkActivity.this, "#06",Toast.LENGTH_SHORT).show();
                    StorageReference filepath = mStorageFile.child(mFileUri.getLastPathSegment());
                    filepath.putFile(mFileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String downloadUri = taskSnapshot.getDownloadUrl().toString();
                            addHomeWork.child("h_file").setValue(downloadUri);
                            addHomeWork.child("h_image").setValue("null");
                            mProgress.dismiss();
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        }
                    });
                }

                if(mImageUri != null && mFileUri == null) {
                    Toast.makeText(SendHomeworkActivity.this, "#07",Toast.LENGTH_SHORT).show();
                    StorageReference imagepath = mStorageImage.child(mImageUri.getLastPathSegment());
                    imagepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String downloadUri = taskSnapshot.getDownloadUrl().toString();
                            addHomeWork.child("h_image").setValue(downloadUri);
                            addHomeWork.child("h_file").setValue("null");
                            mProgress.dismiss();
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        }
                    });
                }

                if(mImageUri == null && mFileUri == null) {
                    Toast.makeText(SendHomeworkActivity.this, "#08",Toast.LENGTH_SHORT).show();
                    addHomeWork.child("h_image").setValue("null");
                    addHomeWork.child("h_file").setValue("null");
                    mProgress.dismiss();
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }

            }

        }

    }

    private void initView() {

        mSwipe = (SwipeRefreshLayout) findViewById(R.id.mSwipe);

        etHomework = (EditText) findViewById(R.id.etHomework);

        rvHomework = (RecyclerView) findViewById(R.id.rvHomework);
        llShowAll = (LinearLayout) findViewById(R.id.llShowAll);

        flImage = (FrameLayout) findViewById(R.id.flImage);
        flFile = (FrameLayout) findViewById(R.id.flFile);

        btnBack = (ImageView) findViewById(R.id.btnBack);
        ivImage = (ImageView) findViewById(R.id.ivImage);
        btnDeleteImage = (ImageView) findViewById(R.id.btnDeleteImage);
        ivFile = (ImageView) findViewById(R.id.ivFile);
        btnDeleteFile = (ImageView) findViewById(R.id.btnDeleteFile);
        btnImage = (ImageView) findViewById(R.id.btnImage);
        btnFile = (ImageView) findViewById(R.id.btnFile);
        btnSend = (ImageView) findViewById(R.id.btnSend);

        setLayoutClose = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        setLayoutOpen = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        setLayoutClose.weight = 165;
        setLayoutOpen.weight = 130;

        mSwipe.setLayoutParams(setLayoutOpen);

        ivFile.setBackgroundColor(Color.GRAY);
        btnDeleteFile.setVisibility(View.GONE);
        btnDeleteImage.setVisibility(View.GONE);

    }

    private void initListener() {

        btnBack.setOnClickListener(this);
        btnImage.setOnClickListener(this);
        btnFile.setOnClickListener(this);
        btnSend.setOnClickListener(this);

        ivImage.setOnClickListener(this);
        ivFile.setOnClickListener(this);

        btnDeleteImage.setOnClickListener(this);
        btnDeleteFile.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if(v == btnBack){
            finish();
        }

        if(v == btnImage){
            startSelectImage();
        }

        if(v == btnFile){
            startSelectFile();
        }

        if(v == btnSend){
            startSendHomework();
        }

        if(v == ivImage){
            startViewImage();
        }

        if(v == ivFile){
            startViewFile();
        }

        if(v == btnDeleteImage){
            clearImage();
        }

        if(v == btnDeleteFile){
            clearFile();
        }

    }

    @Override
    protected void attachBaseContext (Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
