package th.ac.rmutt.comsci.studyplan;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Puttapan on 23/12/2559.
 */

public class CustomTextView extends Application {

    @Override
    public void onCreate(){
        super.onCreate();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/thaisanslite.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
    }
}
