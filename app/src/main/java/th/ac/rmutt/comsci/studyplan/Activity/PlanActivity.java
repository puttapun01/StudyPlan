package th.ac.rmutt.comsci.studyplan.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;

import petrov.kristiyan.colorpicker.ColorPicker;
import th.ac.rmutt.comsci.studyplan.GridAdapter;
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

    private int mYear, mMonth, mDay, mHour, mMinute;

    private GridView gvSelectPlanIcons;

    ArrayAdapter<String> adapter;

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
    int images[] = {
      R.drawable.ic_book,
      R.drawable.ic_book,
      R.drawable.ic_book,
      R.drawable.ic_book,
      R.drawable.ic_book,
      R.drawable.ic_book,
      R.drawable.ic_book,
      R.drawable.ic_book,
      R.drawable.ic_book,
      R.drawable.ic_book,

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        initView();
        initListener();

        mProgress = new ProgressDialog(this);

    }

    private void startSelectColorDialog() {
        final ColorPicker colorPicker = new ColorPicker(PlanActivity.this);
        colorPicker.setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener() {
            @Override
            public void setOnFastChooseColorListener(int position, int color) {
                btnPlanSelectColor.setColorFilter(color);
            }

            @Override
            public void onCancel(){
                // put code
            }
        }).setDefaultColorButton(Color.parseColor("#f84c44")).setColumns(5).show();
    }

    private void startSelectIconDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(PlanActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_select_plan_icon, null);

        gvSelectPlanIcons = (GridView) mView.findViewById(R.id.gvSelectPlanIcons);
        GridAdapter gridAdapter = new GridAdapter(PlanActivity.this , images);
        gvSelectPlanIcons.setAdapter(gridAdapter);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();

        dialog.setTitle("เลือกไอคอน");
        dialog.show();

        gvSelectPlanIcons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(PlanActivity.this, "55555", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private void startSelectTimeDialog() {

        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        btnPlanSelectTime.setText(hourOfDay + ":" + minute);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();

    }

    private void initView() {

        //Connect plan_activity

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

    }

    private void initListener() {
        btnPlanSelectColor.setOnClickListener(this);
        btnPlanSelectTime.setOnClickListener(this);
        btnPlanSelectIcon.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == btnPlanSelectColor){
            startSelectColorDialog();
        }

        if(v == btnPlanSelectIcon){
            startSelectIconDialog();
        }

        if(v == btnPlanSelectTime){
            startSelectTimeDialog();
        }

    }

    @Override
    protected void attachBaseContext (Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    
}
