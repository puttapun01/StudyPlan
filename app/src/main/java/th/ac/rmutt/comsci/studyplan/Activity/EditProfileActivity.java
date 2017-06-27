package th.ac.rmutt.comsci.studyplan.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import th.ac.rmutt.comsci.studyplan.R;
import th.ac.rmutt.comsci.studyplan.UserInformation;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;

    private TextView textViewUserEmail;

    private DatabaseReference databaseReference;

    private EditText editTextName, editTextStudentId;
    private Spinner spinnerLevel, spinnerFaculty, spinnerStatus;

    private Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        databaseReference = FirebaseDatabase.getInstance().getReference();

        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextStudentId = (EditText) findViewById(R.id.editTextStudentId);

        spinnerLevel = (Spinner) findViewById(R.id.spinnerLevel);
        spinnerFaculty = (Spinner) findViewById(R.id.spinnerFaculty);
        spinnerStatus = (Spinner) findViewById(R.id.spinnerStatus);

        buttonSave = (Button) findViewById(R.id.buttonSave);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);

        textViewUserEmail.setText("Welcome " + user.getEmail());

        buttonSave.setOnClickListener(this);
    }

    private void saveUserInformation(){
        String picture = ;
        String stid = editTextStudentId.getText().toString().trim();
        String name = editTextName.getText().toString().trim();
        String level = spinnerLevel.getSelectedItem().toString().trim();
        String faculty = spinnerFaculty.getSelectedItem().toString().trim();
        String status = spinnerStatus.getSelectedItem().toString().trim();


        UserInformation userInformation = new UserInformation(picture, stid, name, level, faculty,status);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        databaseReference.child("Users").child(user.getUid()).setValue(userInformation);

        Toast.makeText(this, "Information Saved...", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        if(v == buttonSave){
            saveUserInformation();
        }
    }
}