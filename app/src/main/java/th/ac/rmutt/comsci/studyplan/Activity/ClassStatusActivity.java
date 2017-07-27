package th.ac.rmutt.comsci.studyplan.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import th.ac.rmutt.comsci.studyplan.Adapter.StatusClassroom;
import th.ac.rmutt.comsci.studyplan.Adapter.StatusClassroomViewHolder;
import th.ac.rmutt.comsci.studyplan.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ClassStatusActivity extends AppCompatActivity implements View.OnClickListener{

    private String comment_key = null;
    private String mClass_key = null;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser mCurrentUser;

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUser;
    private DatabaseReference mDatabaseRegClass;
    private DatabaseReference mDatabaseUserGetClass;
    private DatabaseReference mDatabasePost;
    private DatabaseReference mDatabaseAll;
    private DatabaseReference mDatabaseLike;
    private DatabaseReference mDatabaseComment;
    private DatabaseReference mDatabaseHomework;
    private DatabaseReference mComment;

    private DatabaseReference mDatabaseStatus;

    private TextView tv_subject_id;
    private TextView tv_subject_name;
    private TextView tv_subject_sec;
    private ImageView imageViewSelect;
    private ImageView imageViewProfile;
    private TextView btnSettingClass;
    private TextView btnMember;
    private TextView btnOutClass;
    private ImageView btnBack;

    private TextView btnSettingPost;
    private TextView btnDeletePost;

    private RecyclerView rvStatus;

    private RecyclerView rvComment;
    private EditText etComment;
    private ImageView btnSend;

    private TextView tvPost;

    private String current_uid;
    private String class_key;

    private ProgressDialog mProgressDialog;

    private SwipeRefreshLayout mSwipe;

    private FirebaseRecyclerAdapter<StatusClassroom, StatusClassroomViewHolder> postClassAdapter;

    private boolean mProcessLike = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_status);

        initView();
        initListener();
        startRefresh();
        startDatabaseChange();

        mProgressDialog = new ProgressDialog(this);

        startStatusView();

    }

    private void startRefresh() {
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                mSwipe.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipe.setRefreshing(false);
                            /*Add Method*/
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                            /*---------*/
                    }
                },2000);
            }
        });
    }

    private void startDatabaseChange() {
        firebaseAuth = FirebaseAuth.getInstance();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        //My ID
        current_uid = mCurrentUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("ClassRoom");
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mDatabaseRegClass = FirebaseDatabase.getInstance().getReference().child("RegClass");
        mDatabaseUserGetClass = FirebaseDatabase.getInstance().getReference().child("UserGetClass");
        mDatabaseAll = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseStatus = FirebaseDatabase.getInstance().getReference().child("Status");
        mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
        mDatabaseComment = FirebaseDatabase.getInstance().getReference().child("Comments");
        mDatabaseHomework = FirebaseDatabase.getInstance().getReference().child("Homework");

        mClass_key = getIntent().getExtras().getString("class_id");
        class_key = mClass_key;

        mDatabasePost = FirebaseDatabase.getInstance().getReference().child("Posts");

        mDatabase.child(mClass_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String image = (String) dataSnapshot.child("image").getValue();
                String password = (String) dataSnapshot.child("password").getValue();
                String sec = (String) dataSnapshot.child("sec").getValue();
                String subject_id = (String) dataSnapshot.child("subject_id").getValue();
                String subject_name = (String) dataSnapshot.child("subject_name").getValue();
                String uid = (String) dataSnapshot.child("uid").getValue();
                String username = (String) dataSnapshot.child("username").getValue();

                tv_subject_id.setText(subject_id);
                tv_subject_name.setText(subject_name);
                tv_subject_sec.setText(sec);


//                Picasso.with(ClassStatusActivity.this).load(image).into(...);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String image = dataSnapshot.child("image").getValue().toString();
                Picasso.with(ClassStatusActivity.this).load(image).into(imageViewProfile);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void startStatusView() {

        rvStatus.setHasFixedSize(true);
        rvStatus.setLayoutManager(new LinearLayoutManager(ClassStatusActivity.this));

        LinearLayoutManager layoutStatus = new LinearLayoutManager(ClassStatusActivity.this);
        layoutStatus.setReverseLayout(true);
        layoutStatus.setStackFromEnd(true);

        rvStatus.setLayoutManager(layoutStatus);

        postClassAdapter = new FirebaseRecyclerAdapter<StatusClassroom, StatusClassroomViewHolder>(
                StatusClassroom.class,
                R.layout.view_status,
                StatusClassroomViewHolder.class,
                mDatabasePost.child(class_key)
        ) {
            @Override
            protected void populateViewHolder(final StatusClassroomViewHolder viewHolder, final StatusClassroom model, final int position) {

                final String post_key = getRef(position).getKey();

                viewHolder.setSwitchDate(model.getSwitchDate());
                viewHolder.setTime(model.getTime());
                viewHolder.setDate(model.getDate());

                viewHolder.setTimestamp(model.getTimestamp());
                viewHolder.setDatestamp(model.getDatestamp());

                viewHolder.setText(model.getText());
                viewHolder.setImage(getApplicationContext(), model.getImage());
                viewHolder.setFile(model.getFile());

                viewHolder.setBtnLike(post_key);

                final String imagePost = model.getImage();
                final String filePost = model.getFile();

                mDatabaseLike.child(post_key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int count_like = (int) dataSnapshot.getChildrenCount();
                        String numLike = String.valueOf(count_like);

                        viewHolder.setNumLike(numLike);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mDatabaseComment.child(post_key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int count_comment = (int) dataSnapshot.getChildrenCount();
                        String numComment = String.valueOf(count_comment);

                        viewHolder.setNumComment(numComment);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mDatabaseHomework.child(post_key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int count_homework = (int) dataSnapshot.getChildrenCount();
                        String numHomework = String.valueOf(count_homework);

                        viewHolder.setNumHomework(numHomework);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                viewHolder.mView.findViewById(R.id.llFile).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent singleClassIntent = new Intent(ClassStatusActivity.this, ViewFilePostActivity.class);
                        singleClassIntent.putExtra("filePostUri", filePost);
                        startActivity(singleClassIntent);
                    }
                });

                viewHolder.mView.findViewById(R.id.ivImage).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent singleClassIntent = new Intent(ClassStatusActivity.this, ViewImagePostActivity.class);
                        singleClassIntent.putExtra("imagePostUri", imagePost);
                        startActivity(singleClassIntent);
                    }
                });

                viewHolder.mView.findViewById(R.id.btnComment).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startComment();
                    }

                    private void startComment() {
                        Intent singleClassIntent = new Intent(ClassStatusActivity.this, CommentActivity.class);
                        singleClassIntent.putExtra("post_key", post_key);
                        startActivity(singleClassIntent);
                    }
                });

                viewHolder.mView.findViewById(R.id.btnSendHomework).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startSendHomework();
                    }

                    private void startSendHomework() {
                        Intent singleClassIntent = new Intent(ClassStatusActivity.this, SendHomeworkActivity.class);
                        singleClassIntent.putExtra("post_key", post_key);
                        startActivity(singleClassIntent);
                    }
                });

                viewHolder.mView.findViewById(R.id.llEditPost).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final BottomSheetDialog buttomSheetDialog = new BottomSheetDialog(ClassStatusActivity.this);
                        View parentView = getLayoutInflater().inflate(R.layout.view_post_status_select, null);
                        buttomSheetDialog.setContentView(parentView);
                        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View) parentView.getParent());
                        bottomSheetBehavior.setPeekHeight(
                                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics()));
                        buttomSheetDialog.show();

                        btnSettingPost = (TextView) parentView.findViewById(R.id.btnSettingPost);
                        btnDeletePost = (TextView) parentView.findViewById(R.id.btnDeletePost);

                        btnSettingPost.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                buttomSheetDialog.dismiss();
                                Intent singleClassIntent = new Intent(ClassStatusActivity.this, EditPostActivity.class);
                                singleClassIntent.putExtra("post_key", post_key);
                                singleClassIntent.putExtra("class_id", class_key);
                                startActivity(singleClassIntent);
                            }
                        });

                        btnDeletePost.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                buttomSheetDialog.dismiss();
                                startConfirmDelete();
                            }
                        });

                    }

                    private void startConfirmDelete() {

                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ClassStatusActivity.this);
                        View mView = getLayoutInflater().inflate(R.layout.dialog_confirm_delete_post, null);

                        mBuilder.setView(mView);
                        final AlertDialog dialog = mBuilder.create();
                        dialog.show();

                        //Setting Prevent Dialog
                        dialog.setCancelable(false);
                        dialog.setCanceledOnTouchOutside(false);

                        final TextView btnConfirmCancle = (TextView) mView.findViewById(R.id.btnConfirmCancle);
                        final TextView btnConfirmDelete = (TextView) mView.findViewById(R.id.btnConfirmDelete);

                        btnConfirmDelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                mDatabasePost.child(class_key).child(post_key).removeValue();
                            }
                        });

                        btnConfirmCancle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });


                    }

                });

                mDatabasePost.child(class_key).child(post_key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String uid = dataSnapshot.child("uid").getValue().toString();
                        viewHolder.setPostSelect(uid);

                        mDatabaseAll.child(uid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String name = dataSnapshot.child("name").getValue().toString();
                                final String image = dataSnapshot.child("image").getValue().toString();

                                viewHolder.setUsername(name);
                                viewHolder.setImageProfile(getApplicationContext(), image);

                                viewHolder.mView.findViewById(R.id.btnLike).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        mProcessLike = true;

                                            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                    ImageView s = (ImageView) viewHolder.mView.findViewById(R.id.ivLike);

                                                    if(mProcessLike) {
                                                        if (dataSnapshot.child(post_key).hasChild(current_uid)) {

                                                            mDatabaseLike.child(post_key).child(current_uid).removeValue();

                                                            mProcessLike = false;

                                                        } else {

                                                            mDatabaseLike.child(post_key).child(current_uid).setValue("RandomValue");

                                                            mProcessLike = false;
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });

                                    }
                                });

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        rvStatus.setAdapter(postClassAdapter);

    }

    private void startButtomSheetSelect() {
        final BottomSheetDialog buttomSheetDialog = new BottomSheetDialog(ClassStatusActivity.this);
        View parentView = getLayoutInflater().inflate(R.layout.view_classroom_status_select, null);
        buttomSheetDialog.setContentView(parentView);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View) parentView.getParent());
        bottomSheetBehavior.setPeekHeight(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics()));
        buttomSheetDialog.show();

        btnSettingClass = (TextView) parentView.findViewById(R.id.btnSettingClass);
        btnMember = (TextView) parentView.findViewById(R.id.btnMember);
        btnOutClass = (TextView) parentView.findViewById(R.id.btnOutClass);

        btnSettingClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttomSheetDialog.dismiss();
                Intent singleClassIntent = new Intent(ClassStatusActivity.this, SettingClassroomActivity.class);
                singleClassIntent.putExtra("class_id", class_key);
                startActivity(singleClassIntent);
            }
        });

        btnMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttomSheetDialog.dismiss();
                Intent singleClassIntent = new Intent(ClassStatusActivity.this, ClassroomMemberActivity.class);
                singleClassIntent.putExtra("class_id", class_key);
                startActivity(singleClassIntent);
            }
        });

        btnOutClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttomSheetDialog.dismiss();
                startConfirmOutClass();
            }
        });

    }

    private void startConfirmOutClass() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ClassStatusActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_confirm_outclass, null);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        //Setting Prevent Dialog
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        final TextView btnConfirmCancle = (TextView) mView.findViewById(R.id.btnConfirmCancle);
        final TextView btnConfirmOut = (TextView) mView.findViewById(R.id.btnConfirmOut);
        final TextView tvClassroomName = (TextView) mView.findViewById(R.id.tvClassroomName);

        mDatabase.child(mClass_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String subject_name = (String) dataSnapshot.child("subject_name").getValue();
                tvClassroomName.setText(subject_name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnConfirmCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnConfirmOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDatabaseRegClass.child(class_key).child(current_uid).removeValue();
                mDatabaseUserGetClass.child(current_uid).child(class_key).removeValue();

                finish();
                startActivity(new Intent(ClassStatusActivity.this, ProfileActivity.class));
            }
        });

    }

    private void startPost() {
        final String class_key = mClass_key;
        Intent singleClassIntent = new Intent(ClassStatusActivity.this, PostStatusClassActivity.class);
        singleClassIntent.putExtra("class_id", class_key);
        startActivity(singleClassIntent);
    }

    private void initView() {
        mSwipe = (SwipeRefreshLayout) findViewById(R.id.layoutAuto);

        tv_subject_id = (TextView) findViewById(R.id.tv_subject_id);
        tv_subject_name = (TextView) findViewById(R.id.tv_subject_name);
        tv_subject_sec = (TextView) findViewById(R.id.tv_subject_sec);
        tvPost = (TextView) findViewById(R.id.tvPost);

        imageViewSelect = (ImageView) findViewById(R.id.imageViewSelect);
        imageViewProfile = (ImageView) findViewById(R.id.imageViewProfile);
        btnBack = (ImageView) findViewById(R.id.btnBack);

        rvStatus = (RecyclerView) findViewById(R.id.rvStatus);

    }

    private void initListener() {

        imageViewSelect.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        tvPost.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == imageViewSelect) {
            startButtomSheetSelect();
        }

        if (v == btnBack) {
            finish();
        }

        if (v == tvPost) {
            startPost();
        }
    }

    @Override
    protected void attachBaseContext (Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


}
