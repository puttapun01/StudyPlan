package th.ac.rmutt.comsci.studyplan.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.List;

import th.ac.rmutt.comsci.studyplan.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;

    private TextView textViewUserEmail;

    private DatabaseReference databaseReference;
    private StorageReference mStorageImage;

    private CircularImageView circularProfile;
    private EditText editTextName, editTextStudentId;
    private Spinner spinnerLevel, spinnerFaculty, spinnerStatus;

    private Uri mImageUri = null;

    private Button buttonSave;

    private static final int GALLERY_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        final List<String> levels = new ArrayList<String>();
        levels.add("1");
        levels.add("2");
        levels.add("3");
        levels.add("4");
        levels.add("5");

        final List<String> facultys = new ArrayList<String>();
        facultys.add("วิทยาศาสตร์และเทคโนโลยี");
        facultys.add("บริหารธุรกิจ");
        facultys.add("ศิลปกรรมศาสตร์");
        facultys.add("ศิลปศาสตร์");
        facultys.add("ครุศาสตร์อุตสาหกรรม");
        facultys.add("วิศวกรรมศาสตร์");
        facultys.add("สถาปัตยกรรมศาสตร์");
        facultys.add("วิทยาลัยการแพทย์แผนไทย");
        facultys.add("เทคโนโลยีคหกรรมศาสตร์");
        facultys.add("เทคโนโลยีการเกษตร");
        facultys.add("เทคโนโลยีสื่อสารมวลชน");
        facultys.add("พยาบาลศาสตร์");

        final List<String> status = new ArrayList<String>();
        status.add("นักศึกษา");
        status.add("อาจารย์");

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        mStorageImage = FirebaseStorage.getInstance().getReference().child("Profile_images");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        circularProfile = (CircularImageView) findViewById(R.id.circularProfile);

        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextStudentId = (EditText) findViewById(R.id.editTextStudentId);

        spinnerLevel = (Spinner) findViewById(R.id.spinnerLevel);
        spinnerFaculty = (Spinner) findViewById(R.id.spinnerFaculty);
        spinnerStatus = (Spinner) findViewById(R.id.spinnerStatus);

        ArrayAdapter<String> arrayAdapterLevel = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, levels);
        ArrayAdapter<String> arrayAdapterFaculty = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, facultys);
        ArrayAdapter<String> arrayAdapterStatus = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, status);

        spinnerLevel.setAdapter(arrayAdapterLevel);
        spinnerFaculty.setAdapter(arrayAdapterFaculty);
        spinnerStatus.setAdapter(arrayAdapterStatus);

        buttonSave = (Button) findViewById(R.id.buttonSave);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);

        textViewUserEmail.setText("Welcome " + user.getEmail());

        buttonSave.setOnClickListener(this);
        circularProfile.setOnClickListener(this);
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

                mImageUri = result.getUri();
                circularProfile.setImageURI(mImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void saveUserInformation(){
        final String stid = editTextStudentId.getText().toString().trim();
        final String name = editTextName.getText().toString().trim();
        final String level = spinnerLevel.getSelectedItem().toString().trim();
        final String faculty = spinnerFaculty.getSelectedItem().toString().trim();
        final String status = spinnerStatus.getSelectedItem().toString().trim();

        final String user_id = firebaseAuth.getCurrentUser().getUid();

        if(mImageUri != null){
            StorageReference filepath = mStorageImage.child(mImageUri.getLastPathSegment());

            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    String downloadUri = taskSnapshot.getDownloadUrl().toString();

                    databaseReference.child(user_id).child("name").setValue(name);
                    databaseReference.child(user_id).child("stid").setValue(stid);
                    databaseReference.child(user_id).child("level").setValue(level);
                    databaseReference.child(user_id).child("faculty").setValue(faculty);
                    databaseReference.child(user_id).child("status").setValue(status);
                    databaseReference.child(user_id).child("image").setValue(downloadUri);

                }
            });
            Toast.makeText(this, "Information Saved...", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, ProfileActivity.class));
        }

        if(mImageUri == null || TextUtils.isEmpty(name) || TextUtils.isEmpty(stid)){
            Toast.makeText(this, "Nothing change data...", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, ProfileActivity.class));
        }
    }

    @Override
    public void onClick(View v) {
        if(v == circularProfile){
            changProfilePicture();
        }

        if(v == buttonSave){
            saveUserInformation();
        }
    }

    @Override
    protected void attachBaseContext (Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}