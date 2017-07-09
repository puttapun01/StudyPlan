package th.ac.rmutt.comsci.studyplan.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import th.ac.rmutt.comsci.studyplan.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PlanActivity extends AppCompatActivity implements View.OnClickListener {
    //---------- START Private XML ----------

    //Connect plan_activity
    private RecyclerView mBlogList;

    //Connect dialog_add_item_plan
    private ImageView btnPlanSelectColor, btnPlanSelectIcon;
    private TextView btnPlanSelectTime, btnSave;
    private EditText editTextDetail;

    //Connect view_plan_item
    private ImageView ic_plan_color, ic_plan_select;
    private TextView tv_plan_time, tv_plan_detail;
    private FrameLayout fl_edit, fl_delete;

    //Connect dialog_select_plan_icon
    private ImageView btnColor1, btnColor2, btnColor3, btnColor4, btnColor5, btnColor6, btnColor7, btnColor8, btnColor9, btnColor10, btnColor11, btnColor12 ;

    //---------- END Private XML ----------

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private StorageReference mStorage;
    private StorageReference mStorageImage;

    private static final int GALLERY_REQUEST = 1;

    private Uri mColorUri = null;
    private Uri mIconUri = null;

    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        startConnectFirebase();
        initView();
        initListener();

        mProgress = new ProgressDialog(this);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));


        /*btnPlanSelectColor.setOnClickListener(this);
        btnPlanSelectIcon.setOnClickListener(this);
        btnPlanSelectTime.setOnClickListener(this);
        btnSave.setOnClickListener(this);*/

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                mProgress.setTitle("อัพโหลดรูปภาพ");
                mProgress.setMessage("กรุณารอซักครู่...");
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.show();

                mColorUri = result.getUri();
                mIconUri = result.getUri();

                btnPlanSelectColor.setImageURI(mColorUri);
                btnPlanSelectIcon.setImageURI(mIconUri);

                if(mColorUri != null) {
                    StorageReference filepath = mStorageImage.child(mColorUri.getLastPathSegment());
                    StorageReference filepath2 = mStorageImage.child(mIconUri.getLastPathSegment());

                    filepath.putFile(mColorUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mProgress.dismiss();
                            String downloadUri = taskSnapshot.getDownloadUrl().toString();
                            mDatabase.child("image").setValue(downloadUri);
                        }
                    });

                    filepath2.putFile(mIconUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mProgress.dismiss();
                            String downloadUri = taskSnapshot.getDownloadUrl().toString();
                            mDatabase.child("image").setValue(downloadUri);
                        }
                    });

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    mProgress.setTitle("ไฟล์ภาพมีปัญหา");
                    mProgress.setMessage("กรุณาตรวจสอบไฟล์รูปภาพใหม่อีกครั้ง");
                    mProgress.setCanceledOnTouchOutside(false);
                    mProgress.show();
                }

            }
        }
    }


    private void startConnectFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    Intent loginIntent = new Intent(PlanActivity.this, RegisterActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
            }
        };

        mStorageImage = FirebaseStorage.getInstance().getReference().child("Plan_images");
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Plan");
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);

        FirebaseRecyclerAdapter<PlanBlog, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<PlanBlog, BlogViewHolder>(

                PlanBlog.class,
                R.layout.view_plan_item,
                BlogViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, PlanBlog model, int position) {

                viewHolder.setTime(model.getTime());
                viewHolder.setDetail(model.getDetail());
                viewHolder.setIc(getApplicationContext(), model.getIc());

            }
        };

        mBlogList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView ;
        }

        public void setTime(String time){
            TextView tv_plan_time = (TextView) mView.findViewById(R.id.tv_plan_time);
            tv_plan_time.setText(time);
        }

        public void setDetail(String detail){
            TextView tv_plan_detail = (TextView) mView.findViewById(R.id.tv_plan_detail);
            tv_plan_detail.setText(detail);
        }

        public void setIc(Context ctx, String ic){
            ImageView ic_plan_select = (ImageView) mView.findViewById(R.id.ic_plan_select);
            Picasso.with(ctx).load(ic).into(ic_plan_select);
        }

    }
    
    private void StartDialogAddPlan() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(PlanActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_add_item_plan, null);

        mBuilder.setView(mView);
        AlertDialog dialog = mBuilder.create();
        dialog.show();

    }


    private void initView() {

        //Connect plan_activity
        mBlogList = (RecyclerView) findViewById(R.id.blog_list);

        //Connect dialog_add_item_plan
        btnPlanSelectColor = (ImageView) findViewById(R.id.btnPlanSelectColor);
        btnPlanSelectIcon = (ImageView) findViewById(R.id.btnPlanSelectIcon);
        btnPlanSelectTime = (TextView) findViewById(R.id.btnPlanSelectTime);
        btnSave = (TextView) findViewById(R.id.btnSave);
        editTextDetail = (EditText) findViewById(R.id.editTextDetail);

        //Connect view_plan_item
        ic_plan_color = (ImageView) findViewById(R.id.ic_plan_color);
        ic_plan_select = (ImageView) findViewById(R.id.ic_plan_select);
        tv_plan_time = (TextView) findViewById(R.id.tv_plan_time);
        tv_plan_detail = (TextView) findViewById(R.id.tv_plan_detail);
        fl_edit = (FrameLayout) findViewById(R.id.fl_edit);
        fl_delete = (FrameLayout) findViewById(R.id.fl_delete);

        //Connect dialog_select_plan_icon
        btnColor1 = (ImageView) findViewById(R.id.btnColor1);
        btnColor2 = (ImageView) findViewById(R.id.btnColor2);
        btnColor3 = (ImageView) findViewById(R.id.btnColor3);
        btnColor4 = (ImageView) findViewById(R.id.btnColor4);
        btnColor5 = (ImageView) findViewById(R.id.btnColor5);
        btnColor6 = (ImageView) findViewById(R.id.btnColor6);
        btnColor7 = (ImageView) findViewById(R.id.btnColor7);
        btnColor8 = (ImageView) findViewById(R.id.btnColor8);
        btnColor9 = (ImageView) findViewById(R.id.btnColor9);
        btnColor10 = (ImageView) findViewById(R.id.btnColor10);
        btnColor11 = (ImageView) findViewById(R.id.btnColor11);
        btnColor12 = (ImageView) findViewById(R.id.btnColor12);

    }

    private void initListener() {

    }

    @Override
    public void onClick(View v) {


    }

    @Override
    protected void attachBaseContext (Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    
}
