package sensrbie.com.progressbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import sensrbie.com.ViewLibrary.ProgressBar.SensProgressBar;

public class MainActivity extends AppCompatActivity {

    SensProgressBar mProgressBar;
    final int PROGRESS_BAR_MAX = 5;
    final String[] PROGRESS_BAR_DESCRIPTION = new String[]{"400","800","1500","3000","5000"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = (SensProgressBar)findViewById(R.id.pb_test);
        mProgressBar.setMaxProgress(PROGRESS_BAR_MAX);
        mProgressBar.setProgressDescription(PROGRESS_BAR_DESCRIPTION);
        mProgressBar.setOnProgressChangeListener(new SensProgressBar.ProgressChangeListener() {
            @Override
            public void onProgressChange(int new_value) {

                Log.d("SensProgressBar","new value:"+new_value);
            }
        });
    }
}
