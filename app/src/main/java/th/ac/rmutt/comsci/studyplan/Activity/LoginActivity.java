package th.ac.rmutt.comsci.studyplan.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import th.ac.rmutt.comsci.studyplan.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewLogin;
    private TextView textViewRegister;
    private ImageView imageViewAbout;
    private TextView textViewAbout;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        textViewLogin = (TextView) findViewById(R.id.textViewLogin);
        textViewRegister = (TextView) findViewById(R.id.textViewRegister);
        textViewAbout = (TextView) findViewById(R.id.textViewAbout);

        imageViewAbout = (ImageView) findViewById(R.id.imageViewAbout);

        textViewLogin.setOnClickListener(this);
        textViewRegister.setOnClickListener(this);
        textViewAbout.setOnClickListener(this);

        imageViewAbout.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        if(v == textViewLogin){
//            userLogin();
        }
        if(v == textViewRegister){
            startActivity(new Intent(this, RegisterActivity.class));

        }
        if(v == textViewAbout || v == imageViewAbout){
            startActivity(new Intent(this, AboutActivity.class));
        }


    }
}
