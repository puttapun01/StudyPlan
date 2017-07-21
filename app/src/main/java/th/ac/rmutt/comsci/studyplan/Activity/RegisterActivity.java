package th.ac.rmutt.comsci.studyplan.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import th.ac.rmutt.comsci.studyplan.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private TextView textViewRegister;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        startViewFullScreen();
        startFirebase();
        initView();
        initListener();

        progressDialog = new ProgressDialog(this);
    }

    private void initListener() {
        textViewRegister.setOnClickListener(this);
    }

    private void startViewFullScreen() {
        // คำสั่งซ่อน Status Bar

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        // คำสั่งแสดง Status Bar

        decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private void startFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

    }

    private void initView() {
        textViewRegister = (TextView) findViewById(R.id.textViewRegister);

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextConfirmPassword = (EditText) findViewById(R.id.editTextConfirmPassword);
    }

    private void registerUser(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String conPass = editTextConfirmPassword.getText().toString();


        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "กรุณาใส่ Email", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "กรุณาใส่รหัสผ่าน", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(conPass)){
            Toast.makeText(this, "กรุณาใส่รหัสผ่าน", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!password.equals(conPass)){
            Toast.makeText(this, "รหัสผ่านไม่ตรงกัน", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            final String image = "https://firebasestorage.googleapis.com/v0/b/studyplan-cb45d.appspot.com/o/Profile_images%2Fnobody_profile_image.jpg?alt=media&token=aa0b2ce6-9da2-45af-813e-87431cf2e2cb";
                            final String stid = "(กรุณาเพิ่มข้อมูล)";
                            final String name = "(กรุณาเพิ่มข้อมูล)";
                            final String level = "(กรุณาเพิ่มข้อมูล)";
                            final String faculty = "(กรุณาเพิ่มข้อมูล)";
                            final String status = "(กรุณาเพิ่มข้อมูล)";
                            final String status_id = "student";

                            final String user_id = firebaseAuth.getCurrentUser().getUid();

                            if(user_id == null){

                                databaseReference.child(user_id).child("name").setValue(name);
                                databaseReference.child(user_id).child("stid").setValue(stid);
                                databaseReference.child(user_id).child("level").setValue(level);
                                databaseReference.child(user_id).child("faculty").setValue(faculty);
                                databaseReference.child(user_id).child("status").setValue(status);
                                databaseReference.child(user_id).child("image").setValue(image);
                                databaseReference.child(user_id).child("status_id").setValue(status_id);

                                Toast.makeText(RegisterActivity.this, "สมัครสมาชิกสำเร็จ", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            }

                        } else {
                            Toast.makeText(RegisterActivity.this, "ไม่สามารถสมัครได้ กรุณาลองใหม่อีกครั้ง", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if(v == textViewRegister){
            registerUser();
        }
    }

    @Override
    protected void attachBaseContext (Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
