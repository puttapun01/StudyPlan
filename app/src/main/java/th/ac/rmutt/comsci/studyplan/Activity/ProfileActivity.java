package th.ac.rmutt.comsci.studyplan.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import th.ac.rmutt.comsci.studyplan.R;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;

    private TextView textViewEditProfile;

    private TextView textViewUserEmail;
    private TextView textViewLogout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }


        FirebaseUser user = firebaseAuth.getCurrentUser();

        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        textViewEditProfile = (TextView) findViewById(R.id.textViewEditProfile);

        textViewUserEmail.setText("Email : " + user.getEmail());

        textViewLogout = (TextView) findViewById(R.id.textViewLogout);

        textViewEditProfile.setOnClickListener(this);
        textViewLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v == textViewLogout){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        if (v == textViewEditProfile){
            startActivity(new Intent(this, EditProfileActivity.class));
        }

    }
}
