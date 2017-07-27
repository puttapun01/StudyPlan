package th.ac.rmutt.comsci.studyplan.Activity;

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

import java.util.Calendar;

import th.ac.rmutt.comsci.studyplan.Adapter.Comment;
import th.ac.rmutt.comsci.studyplan.Adapter.CommentViewHolder;
import th.ac.rmutt.comsci.studyplan.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CommentActivity extends AppCompatActivity implements View.OnClickListener {

    //Change Date Time
    private int mYear, mMonth, mDay, mHour, mMinute;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseComment;
    private DatabaseReference mDatabaseAll;
    private String current_uid;

    private RecyclerView rvComment;
    private ImageView btnSend;
    private ImageView btnBack;
    private EditText etComment;

    private TextView btnEditComment;
    private TextView btnDeleteComment;

    private String post_key;
    private String dateStamp = null;
    private String timeStamp = null;

    private SwipeRefreshLayout mSwipe;

    private FirebaseRecyclerAdapter<Comment, CommentViewHolder> postClassAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        initView();
        initListener();
        startFirebase();
        startRefresh();
        startStamp();
        startListView();


    }
    private void startFirebase() {

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        //My ID
        current_uid = mCurrentUser.getUid();

        mDatabaseComment = FirebaseDatabase.getInstance().getReference().child("Comments");
        mDatabaseAll = FirebaseDatabase.getInstance().getReference().child("Users");

        post_key = getIntent().getExtras().getString("post_key");

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

    private void startListView() {

        rvComment.setHasFixedSize(true);
        rvComment.setLayoutManager(new LinearLayoutManager(CommentActivity.this));

        LinearLayoutManager layoutComment = new LinearLayoutManager(CommentActivity.this);
        layoutComment.setReverseLayout(true);
        layoutComment.setStackFromEnd(true);

        rvComment.setLayoutManager(layoutComment);

        postClassAdapter = new FirebaseRecyclerAdapter<Comment, CommentViewHolder>(
                Comment.class,
                R.layout.view_comment,
                CommentViewHolder.class,
                mDatabaseComment.child(post_key)
        ) {
            @Override
            protected void populateViewHolder(final CommentViewHolder viewHolder, final Comment model, int position) {

                final String comment_key = getRef(position).getKey();

                viewHolder.setText_comment(model.getText_comment());
                viewHolder.setTimestamp(model.getTimestamp());
                viewHolder.setDatestamp(model.getDatestamp());


                mDatabaseComment.child(post_key).child(comment_key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String uid = dataSnapshot.child("uid").getValue().toString();

                        viewHolder.setEditComment(uid);

                        mDatabaseAll.child(uid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                String username = dataSnapshot.child("name").getValue().toString();
                                String imageProfile = dataSnapshot.child("image").getValue().toString();

                                viewHolder.setImageProfile(getApplicationContext(), imageProfile);
                                viewHolder.setUsername(username);
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

                viewHolder.mView.findViewById(R.id.btnEditComment).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startSeletComment();
                    }

                    private void startSeletComment() {

                        final DatabaseReference addComment = mDatabaseComment.child(post_key).child(comment_key);

                        final BottomSheetDialog buttomSheetDialog = new BottomSheetDialog(CommentActivity.this);
                        View parentView = getLayoutInflater().inflate(R.layout.view_edit_comment, null);
                        buttomSheetDialog.setContentView(parentView);
                        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View) parentView.getParent());
                        bottomSheetBehavior.setPeekHeight(
                                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics()));
                        buttomSheetDialog.show();

                        btnEditComment = (TextView) parentView.findViewById(R.id.btnEditComment);
                        btnDeleteComment = (TextView) parentView.findViewById(R.id.btnDeleteComment);

                        btnEditComment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                buttomSheetDialog.dismiss();

                                AlertDialog.Builder mBuilder = new AlertDialog.Builder(CommentActivity.this);
                                View mView = getLayoutInflater().inflate(R.layout.dialog_edit_comment, null);

                                final TextView btnSave = (TextView) mView.findViewById(R.id.btnSave);
                                final TextView btnClose = (TextView) mView.findViewById(R.id.btnClose);
                                final EditText editTextComment = (EditText) mView.findViewById(R.id.editTextComment);

                                mBuilder.setView(mView);
                                final AlertDialog dialog = mBuilder.create();
                                dialog.setCancelable(false);
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.show();

                                mDatabaseComment.child(post_key).child(comment_key).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String text_comment = dataSnapshot.child("text_comment").getValue().toString();
                                        editTextComment.setText(text_comment);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                btnSave.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String text_comment_new = editTextComment.getText().toString();
                                        addComment.child("timestamp").setValue(timeStamp);
                                        addComment.child("datestamp").setValue(dateStamp);
                                        mDatabaseComment.child(post_key).child(comment_key).child("text_comment").setValue(text_comment_new);
                                        dialog.dismiss();
                                    }
                                });

                                btnClose.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });

                            }
                        });

                        btnDeleteComment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                buttomSheetDialog.dismiss();

                                AlertDialog.Builder mBuilder = new AlertDialog.Builder(CommentActivity.this);
                                View mView = getLayoutInflater().inflate(R.layout.dialog_c_remove_comment, null);

                                final TextView btnConfirmCancle = (TextView) mView.findViewById(R.id.btnConfirmCancle);
                                final TextView btnConfirmDelete = (TextView) mView.findViewById(R.id.btnConfirmDelete);

                                mBuilder.setView(mView);
                                final AlertDialog dialog = mBuilder.create();
                                dialog.setCancelable(false);
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.show();

                                btnConfirmDelete.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                        Intent intent = getIntent();
                                        finish();
                                        startActivity(intent);
                                        mDatabaseComment.child(post_key).child(comment_key).removeValue();

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

                    }
                });

            }
        };

        rvComment.setAdapter(postClassAdapter);

    }




    private void startStamp() {
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        timeStamp = mHour + ":" + mMinute +" น.";

        final String newMonth;
        String newMonth1 = null;
        if(mMonth+1 == 1){
            newMonth1 = "มกราคม";
        }
        if(mMonth+1 == 2){
            newMonth1 = "กุมภาพันธ์";
        }
        if(mMonth+1 == 3){
            newMonth1 = "มีนาคม";
        }
        if(mMonth+1 == 4){
            newMonth1 = "เมษายน";
        }
        if(mMonth+1 == 5){
            newMonth1 = "พฤษภาคม";
        }
        if(mMonth+1 == 6){
            newMonth1 = "มิถุนายน";
        }
        if(mMonth+1 == 7){
            newMonth1 = "กรกฎาคม";
        }
        if(mMonth+1 == 8){
            newMonth1 = "สิงหาคม";
        }
        if(mMonth+1 == 9){
            newMonth1 = "กันยายน";
        }
        if(mMonth+1 == 10){
            newMonth1 = "ตุลาคม";
        }
        if(mMonth+1 == 11){
            newMonth1 = "พฤศจิกายน";
        }
        if(mMonth+1 == 12){
            newMonth1 = "ธันวาคม";
        }
        newMonth = newMonth1;
        dateStamp = mDay + " "  + newMonth + " " + (mYear+543);
    }

    private void startComment() {

        final DatabaseReference addComment = mDatabaseComment.child(post_key).push();

        String textComment = etComment.getText().toString();

        addComment.child("text_comment").setValue(textComment);
        addComment.child("uid").setValue(current_uid);
        addComment.child("timestamp").setValue(timeStamp);
        addComment.child("datestamp").setValue(dateStamp);

        Intent intent = getIntent();
        finish();
        startActivity(intent);

    }

    private void initView() {

        rvComment = (RecyclerView) findViewById(R.id.rvComment);
        btnSend = (ImageView) findViewById(R.id.btnSend);
        btnBack = (ImageView) findViewById(R.id.btnBack);
        etComment = (EditText) findViewById(R.id.etComment);

        mSwipe = (SwipeRefreshLayout) findViewById(R.id.mSwipe);
    }

    private void initListener() {

        btnSend.setOnClickListener(this);
        btnBack.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if(v == btnSend){
            startComment();
        }
        if(v == btnBack){
            finish();
        }

    }

    @Override
    protected void attachBaseContext (Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
