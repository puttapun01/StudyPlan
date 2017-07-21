package th.ac.rmutt.comsci.studyplan.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import th.ac.rmutt.comsci.studyplan.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewLogin;
    private TextView textViewRegister;
    private ImageView imageViewAbout;
    private TextView textViewAbout;

    private ProgressDialog mProgress;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    private static final int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "LoginActivity";
    private LinearLayout mGoogleBtn;

    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        startFullScreen();
        initView();
        initListener();

        startFirebaes();
        startGoogleSignIn();
        startFacebookSignIn();

        mProgress = new ProgressDialog(this);

    }




    private void startFullScreen() {
        // คำสั่งซ่อน Status Bar

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        // คำสั่งแสดง Status Bar

        decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private void initView() {
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        textViewLogin = (TextView) findViewById(R.id.textViewLogin);
        textViewRegister = (TextView) findViewById(R.id.textViewRegister);
        textViewAbout = (TextView) findViewById(R.id.textViewAbout);

        imageViewAbout = (ImageView) findViewById(R.id.imageViewAbout);

        mGoogleBtn = (LinearLayout) findViewById(R.id.btnGoogleLogin);
    }

    private void initListener() {
        textViewLogin.setOnClickListener(this);
        textViewRegister.setOnClickListener(this);
        textViewAbout.setOnClickListener(this);

        imageViewAbout.setOnClickListener(this);

    }

    private void startFirebaes() {
        mAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        if(mAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }
    }

    private void startGoogleSignIn() {
// Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signIn();

            }
        });


    }

    private void startFacebookSignIn() {
        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton mFacebookBtn = (LoginButton) findViewById(R.id.btnFacebookLogin);
        mFacebookBtn.setReadPermissions("email", "public_profile");
        mFacebookBtn.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");

            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);

            }
        });

    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        //*Dialog Set message
        //*Dialog show

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            final String image = "https://firebasestorage.googleapis.com/v0/b/studyplan-cb45d.appspot.com/o/Profile_images%2Fnobody_profile_image.jpg?alt=media&token=aa0b2ce6-9da2-45af-813e-87431cf2e2cb";
                            final String stid = "(กรุณาเพิ่มข้อมูล)";
                            final String name = "(กรุณาเพิ่มข้อมูล)";
                            final String level = "(กรุณาเพิ่มข้อมูล)";
                            final String faculty = "(กรุณาเพิ่มข้อมูล)";
                            final String status = "(กรุณาเพิ่มข้อมูล)";
                            final String status_id = "student";

                            final String user_id = mAuth.getCurrentUser().getUid();

                            if(user_id == null){

                                databaseReference.child(user_id).child("name").setValue(name);
                                databaseReference.child(user_id).child("stid").setValue(stid);
                                databaseReference.child(user_id).child("level").setValue(level);
                                databaseReference.child(user_id).child("faculty").setValue(faculty);
                                databaseReference.child(user_id).child("status").setValue(status);
                                databaseReference.child(user_id).child("image").setValue(image);
                                databaseReference.child(user_id).child("status_id").setValue(status_id);

                                Toast.makeText(LoginActivity.this, "เพิ่มข้อมูลใหม่", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            }

                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        mProgress.dismiss();

                        finish();
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));

                    }
                });
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            mProgress.setMessage("กำลังเข้าสู่ระบบ...");
            mProgress.show();

            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);

            } else {
                mProgress.dismiss();
            }

        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            final String image = "https://firebasestorage.googleapis.com/v0/b/studyplan-cb45d.appspot.com/o/Profile_images%2Fnobody_profile_image.jpg?alt=media&token=aa0b2ce6-9da2-45af-813e-87431cf2e2cb";
                            final String stid = "(กรุณาเพิ่มข้อมูล)";
                            final String name = "(กรุณาเพิ่มข้อมูล)";
                            final String level = "(กรุณาเพิ่มข้อมูล)";
                            final String faculty = "(กรุณาเพิ่มข้อมูล)";
                            final String status = "(กรุณาเพิ่มข้อมูล)";
                            final String status_id = "student";

                            final String user_id = mAuth.getCurrentUser().getUid();

                            if(user_id == null){

                                databaseReference.child(user_id).child("name").setValue(name);
                                databaseReference.child(user_id).child("stid").setValue(stid);
                                databaseReference.child(user_id).child("level").setValue(level);
                                databaseReference.child(user_id).child("faculty").setValue(faculty);
                                databaseReference.child(user_id).child("status").setValue(status);
                                databaseReference.child(user_id).child("image").setValue(image);
                                databaseReference.child(user_id).child("status_id").setValue(status_id);

                                Toast.makeText(LoginActivity.this, "เพิ่มข้อมูลใหม่", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            }

                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                        mProgress.dismiss();

                        finish();
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));

                        // ...
                    }
                });
    }

    private void userLogin(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            //email is empty
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;
        }
        if(TextUtils.isEmpty(password)){
            //password is empty
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;
        }

        mProgress.setMessage("Login Please Wait...");
        mProgress.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mProgress.dismiss();
                        if(task.isSuccessful()){
                            finish();
                            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        }

                    }
                });

    }


    @Override
    public void onClick(View v) {

        if(v == textViewLogin){
            userLogin();
        }
        if(v == textViewRegister){
            startActivity(new Intent(this, RegisterActivity.class));

        }
        if(v == textViewAbout || v == imageViewAbout){
            startActivity(new Intent(this, AboutActivity.class));
        }

    }

    @Override
    protected void attachBaseContext (Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
