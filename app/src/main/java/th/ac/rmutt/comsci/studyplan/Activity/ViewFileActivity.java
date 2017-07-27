package th.ac.rmutt.comsci.studyplan.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import th.ac.rmutt.comsci.studyplan.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ViewFileActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvFileAddress;
    private TextView btnDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_file);

        btnBack = (ImageView) findViewById(R.id.btnBack);
        tvFileAddress = (TextView) findViewById(R.id.tvFileAddress);
        btnDownload = (TextView) findViewById(R.id.btnDownload);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String mFileUri = getIntent().getExtras().getString("fileUri");
        tvFileAddress.setText(mFileUri);
        btnDownload.setVisibility(View.GONE);
    }

    @Override
    protected void attachBaseContext (Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
