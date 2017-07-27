package th.ac.rmutt.comsci.studyplan.Activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import java.util.Calendar;

import th.ac.rmutt.comsci.studyplan.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class EditPostActivity extends AppCompatActivity implements View.OnClickListener{

    private String mClass_key = null;
    private String post_key = null;

    private ImageView btnBack;

    private ProgressDialog mProgress;

    //Change Image
    private static final int GALLERY_REQUEST = 1;
    private static final int FILE_REQUEST = 1;
    private Uri mImageUri = null;
    private Uri mFileUri = null;

    //Change Date Time
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

    private ImageView imClearImage;
    private ImageView imClearFile;

    //Database Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUser;
    private DatabaseReference mPost;
    private StorageReference mStorageImage;
    private StorageReference mStorageFile;

    private String fileImport = null;
    private String imageImport = null;

    private Uri file_key = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        initView();
        initListener();
        startDatabaseChange();
        startReadData();

        startGetValueToEdit();

        mProgress = new ProgressDialog(this);

    }


    private void startDatabaseChange() {
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        String current_uid = mCurrentUser.getUid();
        mClass_key = getIntent().getExtras().getString("class_id");
        final String class_key = mClass_key;

        post_key = getIntent().getExtras().getString("post_key");

        mStorageImage = FirebaseStorage.getInstance().getReference().child("Post_images");
        mStorageFile = FirebaseStorage.getInstance().getReference().child("Post_files");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("ClassRoom");
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mPost = FirebaseDatabase.getInstance().getReference().child("Posts").child(class_key);

        //Get Data To Read


    }

    private void startGetValueToEdit() {
        mPost.child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String switchStatus = dataSnapshot.child("switchDate").getValue().toString();
                String date = dataSnapshot.child("date").getValue().toString();
                String time = dataSnapshot.child("time").getValue().toString();
                String text = dataSnapshot.child("text").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String file = dataSnapshot.child("file").getValue().toString();

                if(switchStatus.equals("on")){
                    switchDate.setChecked(true);
                }
                if(switchStatus.equals("off")){
                    switchDate.setChecked(false);
                }


                if(date.equals("null")){
                    tvDate.setText("วัน/เดือน/ปี");
                }
                if(!date.equals("null")){
                    tvDate.setText(date);
                }


                if(time.equals("null")){
                    tvTime.setText("เวลา");
                }
                if(!time.equals("null")){
                    tvTime.setText(time);
                }


                etPost.setText(text);


                if(image.equals("null")){
                    tvImages.setText("รูปภาพ");
                    tvViewImage.setVisibility(View.GONE);
                    imClearImage.setVisibility(View.GONE);

                }
                if(!image.equals("null")){
                    tvImages.setText("เปลี่ยนรูปภาพ");
                    tvViewImage.setVisibility(View.VISIBLE);
                    imClearImage.setVisibility(View.VISIBLE);
                    final Uri uri = Uri.parse(image);
                    mImageUri = uri;
                }


                if(file.equals("null")){
                    tvFile.setText("ไฟล์");
                    tvViewFile.setVisibility(View.GONE);
                    imClearFile.setVisibility(View.GONE);

                }
                if(!file.equals("null")){
                    tvFile.setText("เปลี่ยนไฟล์");
                    tvViewFile.setVisibility(View.VISIBLE);
                    imClearFile.setVisibility(View.VISIBLE);
                    final Uri uri = Uri.parse(file);
                    file_key = uri;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
                Picasso.with(EditPostActivity.this).load(image).into(circularAuth);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
                tvTime.setText(hourOfDay + ":" + minute + " น.");
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
        DatePickerDialog dpd = new DatePickerDialog(EditPostActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        final String newMonth;
                        String newMonth1 = null;
                        if(monthOfYear+1 == 1){
                            newMonth1 = "มกราคม";
                        }
                        if(monthOfYear+1 == 2){
                            newMonth1 = "กุมภาพันธ์";
                        }
                        if(monthOfYear+1 == 3){
                            newMonth1 = "มีนาคม";
                        }
                        if(monthOfYear+1 == 4){
                            newMonth1 = "เมษายน";
                        }
                        if(monthOfYear+1 == 5){
                            newMonth1 = "พฤษภาคม";
                        }
                        if(monthOfYear+1 == 6){
                            newMonth1 = "มิถุนายน";
                        }
                        if(monthOfYear+1 == 7){
                            newMonth1 = "กรกฎาคม";
                        }
                        if(monthOfYear+1 == 8){
                            newMonth1 = "สิงหาคม";
                        }
                        if(monthOfYear+1 == 9){
                            newMonth1 = "กันยายน";
                        }
                        if(monthOfYear+1 == 10){
                            newMonth1 = "ตุลาคม";
                        }
                        if(monthOfYear+1 == 11){
                            newMonth1 = "พฤศจิกายน";
                        }
                        if(monthOfYear+1 == 12){
                            newMonth1 = "ธันวาคม";
                        }
                        newMonth = newMonth1;
                        tvDate.setText("วันที่ "+ dayOfMonth+ " " + newMonth + " " + (year+543));
                        tvDate.setTextSize(16);
                    }
                }, mYear, mMonth, mDay);
        dpd.show();

    }

    private void startUploadImage() {
        Intent gallerryIntent = new Intent();
        gallerryIntent.setAction(Intent.ACTION_GET_CONTENT);
        gallerryIntent.setType("image/*");
        startActivityForResult(gallerryIntent, GALLERY_REQUEST);
        imageImport = new String(imageImport = "yes");
    }

    private void startUploadFile() {
        Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        fileIntent.setType("file/*");
        startActivityForResult(fileIntent, FILE_REQUEST);
        fileImport = new String(fileImport = "OK");
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri imageUri = data.getData();
        Uri fileUri = data.getData();

        String imageCheck = "yes";
        String fileCheck = "OK";

        if(requestCode == FILE_REQUEST && resultCode == RESULT_OK && fileCheck.equals(fileImport)){
            file_key = fileUri;
            tvViewFile.setVisibility(View.VISIBLE);
            imClearFile.setVisibility(View.VISIBLE);
            tvFile.setText("เปลี่ยนไฟล์");

//            Toast.makeText(EditPostActivity.this, "*File Image : " + file_key, Toast.LENGTH_LONG).show();
        }

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && imageCheck.equals(imageImport)) {
            CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            mImageUri = result.getUri();
            tvViewImage.setVisibility(View.VISIBLE);
            imClearImage.setVisibility(View.VISIBLE);
            tvImages.setText("เปลี่ยนรูปภาพ");

//            Toast.makeText(EditPostActivity.this, "Uri Image : " + mImageUri, Toast.LENGTH_LONG).show();
        }

        imageImport = new String(imageImport = "no");
        fileImport = new String(fileImport = "O");

    }



    private void startViewImage() {

        String imageUri = mImageUri.toString();
        Intent singleClassIntent = new Intent(EditPostActivity.this, ViewImagePostActivity.class);
        singleClassIntent.putExtra("imagePostUri", imageUri);
        startActivity(singleClassIntent);

    }

    private void startViewFile() {

        String FileUri = file_key.toString();
        Intent singleClassIntent = new Intent(EditPostActivity.this, ViewFilePostActivity.class);
        singleClassIntent.putExtra("filePostUri", FileUri);
        startActivity(singleClassIntent);

    }


    private void startPost() {

        final DatabaseReference addPost = mPost.child(post_key);

        String text = etPost.getText().toString();
        String time = tvTime.getText().toString();
        String date = tvDate.getText().toString();
        String uid = mCurrentUser.getUid();

        if(text.equals("")){
            Toast.makeText(EditPostActivity.this, "กรุณาพิมพ์ข้อความ", Toast.LENGTH_LONG).show();
        }

        if(!text.equals("")){

            mProgress.setMessage("กำลังโพส...");
            mProgress.setCanceledOnTouchOutside(true);
            mProgress.show();

            addPost.child("text").setValue(text);
            addPost.child("uid").setValue(uid);
            addPost.child("classroom").setValue(mClass_key);

            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            String timeStamp = mHour + ":" + mMinute +" น.";
            addPost.child("timestamp").setValue(timeStamp);

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

            String dateStamp = mDay + " "  + newMonth + " " + (mYear+543);
            addPost.child("datestamp").setValue(dateStamp);

            if(switchDate.isChecked()){
                addPost.child("time").setValue(time);
                addPost.child("date").setValue(date);
                addPost.child("switchDate").setValue("on");
            }

            if(!switchDate.isChecked()){
                addPost.child("time").setValue("null");
                addPost.child("date").setValue("null");
                addPost.child("switchDate").setValue("off");
            }

            mPost.child(post_key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String image = dataSnapshot.child("image").getValue().toString();
                    String file = dataSnapshot.child("file").getValue().toString();

                    if(!image.equals("null") && !file.equals("null")){
                        if(mImageUri != null && file_key != null) {
                            StorageReference imagepath = mStorageImage.child(mImageUri.getLastPathSegment());
                            imagepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    String downloadUri = taskSnapshot.getDownloadUrl().toString();
                                    addPost.child("image").setValue(downloadUri);
                                }
                            });

                            final StorageReference filepath = mStorageFile.child(file_key.getLastPathSegment());
                            filepath.putFile(file_key).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    String downloadUri = taskSnapshot.getDownloadUrl().toString();
                                    addPost.child("file").setValue(downloadUri);
                                    mProgress.dismiss();
                                    finish();
                                }
                            });

                        }

                        if(mImageUri == null && file_key != null) {
                            StorageReference filepath = mStorageFile.child(file_key.getLastPathSegment());
                            filepath.putFile(file_key).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    String downloadUri = taskSnapshot.getDownloadUrl().toString();
                                    addPost.child("file").setValue(downloadUri);
                                    addPost.child("image").setValue("null");
                                    mProgress.dismiss();
                                    finish();
                                }
                            });
                        }

                        if(mImageUri != null && file_key == null) {
                            StorageReference imagepath = mStorageImage.child(mImageUri.getLastPathSegment());
                            imagepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    String downloadUri = taskSnapshot.getDownloadUrl().toString();
                                    addPost.child("image").setValue(downloadUri);
                                    addPost.child("file").setValue("null");
                                    mProgress.dismiss();
                                    finish();
                                }
                            });
                        }

            /*- - ------------ Upload Zone -------------------*/

                        if(mImageUri == null && file_key == null) {
                            addPost.child("image").setValue("null");
                            addPost.child("file").setValue("null");
                            mProgress.dismiss();
                            finish();
                        }

                    }

                    if(image.equals("null") && !file.equals("null")){

                        if(mImageUri != null && file_key != null) {
                            StorageReference imagepath = mStorageImage.child(mImageUri.getLastPathSegment());
                            imagepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    String downloadUri = taskSnapshot.getDownloadUrl().toString();
                                    addPost.child("image").setValue(downloadUri);
                                }
                            });

                            final StorageReference filepath = mStorageFile.child(file_key.getLastPathSegment());
                            filepath.putFile(file_key).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    String downloadUri = taskSnapshot.getDownloadUrl().toString();
                                    addPost.child("file").setValue(downloadUri);
                                    mProgress.dismiss();
                                    finish();
                                }
                            });

                        }

                        if(mImageUri == null && file_key != null) {
                            StorageReference filepath = mStorageFile.child(file_key.getLastPathSegment());
                            filepath.putFile(file_key).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    String downloadUri = taskSnapshot.getDownloadUrl().toString();
                                    addPost.child("file").setValue(downloadUri);
                                    addPost.child("image").setValue("null");
                                    mProgress.dismiss();
                                    finish();
                                }
                            });
                        }

                        if(mImageUri != null && file_key == null) {
                            StorageReference imagepath = mStorageImage.child(mImageUri.getLastPathSegment());
                            imagepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    String downloadUri = taskSnapshot.getDownloadUrl().toString();
                                    addPost.child("image").setValue(downloadUri);
                                    addPost.child("file").setValue("null");
                                    mProgress.dismiss();
                                    finish();
                                }
                            });
                        }

            /*- - ------------ Upload Zone -------------------*/

                        if(mImageUri == null && file_key == null) {
                            addPost.child("image").setValue("null");
                            addPost.child("file").setValue("null");
                            mProgress.dismiss();
                            finish();
                        }

                    }

                    if(!image.equals("null") && file.equals("null")){

                        if(mImageUri != null && file_key != null) {
                            StorageReference imagepath = mStorageImage.child(mImageUri.getLastPathSegment());
                            imagepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    String downloadUri = taskSnapshot.getDownloadUrl().toString();
                                    addPost.child("image").setValue(downloadUri);
                                }
                            });

                            final StorageReference filepath = mStorageFile.child(file_key.getLastPathSegment());
                            filepath.putFile(file_key).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    String downloadUri = taskSnapshot.getDownloadUrl().toString();
                                    addPost.child("file").setValue(downloadUri);
                                    mProgress.dismiss();
                                    finish();
                                }
                            });

                        }

                        if(mImageUri == null && file_key != null) {
                            StorageReference filepath = mStorageFile.child(file_key.getLastPathSegment());
                            filepath.putFile(file_key).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    String downloadUri = taskSnapshot.getDownloadUrl().toString();
                                    addPost.child("file").setValue(downloadUri);
                                    addPost.child("image").setValue("null");
                                    mProgress.dismiss();
                                    finish();
                                }
                            });
                        }

                        if(mImageUri != null && file_key == null) {
                            StorageReference imagepath = mStorageImage.child(mImageUri.getLastPathSegment());
                            imagepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    String downloadUri = taskSnapshot.getDownloadUrl().toString();
                                    addPost.child("image").setValue(downloadUri);
                                    addPost.child("file").setValue("null");
                                    mProgress.dismiss();
                                    finish();
                                }
                            });
                        }

            /*- - ------------ Upload Zone -------------------*/

                        if(mImageUri == null && file_key == null) {
                            addPost.child("image").setValue("null");
                            addPost.child("file").setValue("null");
                            mProgress.dismiss();
                            finish();
                        }

                    }

                    if(image.equals("null") && file.equals("null")){

                        if(mImageUri != null && file_key != null) {
                            StorageReference imagepath = mStorageImage.child(mImageUri.getLastPathSegment());
                            imagepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    String downloadUri = taskSnapshot.getDownloadUrl().toString();
                                    addPost.child("image").setValue(downloadUri);
                                }
                            });

                            final StorageReference filepath = mStorageFile.child(file_key.getLastPathSegment());
                            filepath.putFile(file_key).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    String downloadUri = taskSnapshot.getDownloadUrl().toString();
                                    addPost.child("file").setValue(downloadUri);
                                    mProgress.dismiss();
                                    finish();
                                }
                            });

                        }

                        if(mImageUri == null && file_key != null) {
                            StorageReference filepath = mStorageFile.child(file_key.getLastPathSegment());
                            filepath.putFile(file_key).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    String downloadUri = taskSnapshot.getDownloadUrl().toString();
                                    addPost.child("file").setValue(downloadUri);
                                    addPost.child("image").setValue("null");
                                    mProgress.dismiss();
                                    finish();
                                }
                            });
                        }

                        if(mImageUri != null && file_key == null) {
                            StorageReference imagepath = mStorageImage.child(mImageUri.getLastPathSegment());
                            imagepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    String downloadUri = taskSnapshot.getDownloadUrl().toString();
                                    addPost.child("image").setValue(downloadUri);
                                    addPost.child("file").setValue("null");
                                    mProgress.dismiss();
                                    finish();
                                }
                            });
                        }

            /*- - ------------ Upload Zone -------------------*/

                        if(mImageUri == null && file_key == null) {
                            addPost.child("image").setValue("null");
                            addPost.child("file").setValue("null");
                            mProgress.dismiss();
                            finish();
                        }

                    }

                    mProgress.dismiss();
                    finish();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            /*- - ------------ Upload Zone -------------------*/



        }

    }


    private void clearImage() {

        mImageUri = null;
        mPost.child(post_key).child("image").setValue("null");
        tvViewImage.setVisibility(View.GONE);
        imClearImage.setVisibility(View.GONE);
        tvImages.setText("รูปภาพ");

    }

    private void clearFile() {

        file_key = null;
        mPost.child(post_key).child("file").setValue("null");
        tvViewFile.setVisibility(View.GONE);
        imClearFile.setVisibility(View.GONE);
        tvFile.setText("ไฟล์");

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

        //set Clear
        imClearImage = (ImageView) findViewById(R.id.imClearImage);
        imClearFile = (ImageView) findViewById(R.id.imClearFile);

        //Oth Button
        btnBack = (ImageView) findViewById(R.id.btnBack);

        //Set GONE
        tvViewImage.setVisibility(View.GONE);
        tvViewFile.setVisibility(View.GONE);
        imClearImage.setVisibility(View.GONE);
        imClearFile.setVisibility(View.GONE);
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

        tvViewImage.setOnClickListener(this);
        tvViewFile.setOnClickListener(this);

        imClearImage.setOnClickListener(this);
        imClearFile.setOnClickListener(this);

        startSwitch();

    }

    @Override
    public void onClick(View v) {

        if(v == btnBack){
            finish();
        }

        if(v == tvTime){
            startSelectTime();
        }

        if(v == tvDate){
            startSelectDate();
        }

        if(v == btnPost){
            startPost();
        }

        if(v == imImage || v == tvImages){
            startUploadImage();
        }

        if(v == imFile || v == tvFile){
            startUploadFile();
        }

        if(v == tvViewImage){
            startViewImage();
        }

        if(v == tvViewFile){
            startViewFile();
        }

        if(v == imClearImage){
            clearImage();
        }

        if(v == imClearFile){
            clearFile();
        }

    }

    @Override
    protected void attachBaseContext (Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
