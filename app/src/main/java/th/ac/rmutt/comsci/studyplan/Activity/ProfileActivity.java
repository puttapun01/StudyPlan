package th.ac.rmutt.comsci.studyplan.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import co.ceryle.segmentedbutton.SegmentedButtonGroup;
import th.ac.rmutt.comsci.studyplan.Fragment.ClassroomFragment;
import th.ac.rmutt.comsci.studyplan.Fragment.ComingSoonFragment;
import th.ac.rmutt.comsci.studyplan.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUserGetClass;
    private FirebaseUser mCurrentUser;

    private TextView textViewEditProfile;
    private TextView textViewLogout;

    private TextView btnConfirmCancle, btnConfirmLogout;

    private ImageView imageViewSelect;
    private TextView btnSettingProfile;
    private TextView btnMember;
    private TextView btnLogout;

    private SegmentedButtonGroup sbg;

    private TextView textViewUserEmail;
    private TextView textViewName;
    private TextView textViewStudentId;
    private String mClass_key = null;

    private CircularImageView circularProfile;

    private Uri mImageUri = null;

    private SegmentedButtonGroup segmentButtonTabProfile;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private ProgressDialog mProgressDialog;

    //count ZONE
    private TextView textViewCountSubject;
    private TextView textViewCountHomework;
    private TextView textViewCountPlan;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initView();
        initListener();
        setupDialog();
        startConnectFirebase();
        startDataChange();
        setButtonSegment();
        setViewSegment();
        startCount();
    }

    private void startCount() {

    }

    private void setupDialog() {
        mProgressDialog = new ProgressDialog(this);
    }

    private void startConnectFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        String current_uid = mCurrentUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mDatabaseUserGetClass = FirebaseDatabase.getInstance().getReference().child("UserGetClass").child(current_uid);

        FirebaseUser user = firebaseAuth.getCurrentUser();
        textViewUserEmail.setText("Email : " + user.getEmail());

        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        if(user != null){
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();
            String uid = user.getUid();
        } else {
            goMainScreen();
        }
    }

    private void startButtomSheetSelect() {
        final BottomSheetDialog buttomSheetDialog = new BottomSheetDialog(ProfileActivity.this);
        View parentView = getLayoutInflater().inflate(R.layout.view_profile_select, null);
        buttomSheetDialog.setContentView(parentView);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View) parentView.getParent());
        bottomSheetBehavior.setPeekHeight(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics()));
        buttomSheetDialog.show();

        btnSettingProfile = (TextView) parentView.findViewById(R.id.btnSettingClass);
        btnLogout = (TextView) parentView.findViewById(R.id.btnOutClass);
        btnMember = (TextView) parentView.findViewById(R.id.btnMember);

        btnSettingProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttomSheetDialog.dismiss();
                startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
            }
        });

        btnMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttomSheetDialog.dismiss();
                startActivity(new Intent(ProfileActivity.this, AllUserActivity.class));
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttomSheetDialog.dismiss();
                startLogoutConfirm();
            }
        });
    }

    private void startLogoutConfirm() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ProfileActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_confirm_logout, null);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        final TextView btnConfirmCancle = (TextView) mView.findViewById(R.id.btnConfirmCancle);
        final TextView btnConfirmLogout = (TextView) mView.findViewById(R.id.btnConfirmLogout);

        btnConfirmCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnConfirmLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                LoginManager.getInstance().logOut();
                finish();
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)

    private void setViewSegment() {

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);

        mViewPager.setAdapter(mSectionsPagerAdapter);

        SegmentedButtonGroup segmentButtonTabProfile = (SegmentedButtonGroup) findViewById(R.id.segmentButtonTabProfile);
    }

    private void startDataChange() {

        mProgressDialog.setMessage("กำลังโหลดข้อมูล...");
        mProgressDialog.show();

        //Profile Changer

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String stid = dataSnapshot.child("stid").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                textViewName.setText("ชื่อ-สกุล : " + name);
                textViewStudentId.setText("รหัส นศ./ประจำตัว : " + stid);

                Picasso.with(ProfileActivity.this).load(image).into(circularProfile);

                mProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseUserGetClass.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count_subject = (int) dataSnapshot.getChildrenCount();
                String num_subject = String.valueOf(count_subject);
                textViewCountSubject.setText(num_subject);
            }
            public void onCancelled(DatabaseError databaseError) { }
        });



    }

    private void setButtonSegment() {
        sbg = (SegmentedButtonGroup) findViewById(R.id.segmentButtonTabProfile);
        sbg.setOnClickedButtonPosition(new SegmentedButtonGroup.OnClickedButtonPosition() {
            @Override
            public void onClickedButtonPosition(int position) {

                switch (position) {
                    case 0:
                        mViewPager.setCurrentItem(0);
                        break;
                    case 1:
                        mViewPager.setCurrentItem(1);
                        break;
                    case 2:
                        mViewPager.setCurrentItem(2);
                        break;
                    case 3:
                        mViewPager.setCurrentItem(3);
                        break;
                }
            }
        });
        sbg.setPosition(0, 0);

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position){
                case 0 :
                    return new ClassroomFragment();
                case 1 :
                    return new ComingSoonFragment();
                case 2 :
                    return new ComingSoonFragment();
                default :
                    return new ComingSoonFragment();
            }
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }

    private void goMainScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void initView() {
        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewStudentId = (TextView) findViewById(R.id.textViewStudentId);
        circularProfile = (CircularImageView) findViewById(R.id.circularProfile);
        //Button
        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);

        btnConfirmCancle = (TextView) findViewById(R.id.btnConfirmCancle);
        btnConfirmLogout = (TextView) findViewById(R.id.btnConfirmLogout);

        imageViewSelect = (ImageView) findViewById(R.id.imageViewSelect);

        textViewCountSubject = (TextView) findViewById(R.id.textViewCountSubject);
        textViewCountHomework = (TextView) findViewById(R.id.textViewCountHomework);
        textViewCountPlan = (TextView) findViewById(R.id.textViewCountPlan);
    }

    private void initListener() {
        imageViewSelect.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v == imageViewSelect){
            startButtomSheetSelect();
        }

    }

    @Override
    protected void attachBaseContext (Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
