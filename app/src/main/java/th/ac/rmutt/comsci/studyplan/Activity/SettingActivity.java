package th.ac.rmutt.comsci.studyplan.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;

import th.ac.rmutt.comsci.studyplan.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout blogItemEditProfile, blogItemAbout, blogItemLogout;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        blogItemEditProfile = (LinearLayout) findViewById(R.id.blogItemEditProfile);
        blogItemAbout = (LinearLayout) findViewById(R.id.blogItemAbout);
        blogItemLogout = (LinearLayout) findViewById(R.id.blogItemLogout);

        blogItemEditProfile.setOnClickListener(this);
        blogItemAbout.setOnClickListener(this);
        blogItemLogout.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if(v == blogItemEditProfile){
            startActivity(new Intent(this, EditProfileActivity.class));
        }
        if(v == blogItemAbout){
            startActivity(new Intent(this, AboutActivity.class));
        }
        if(v == blogItemLogout){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

    }

    @Override
    protected void attachBaseContext (Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
