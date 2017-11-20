package edu.umich.si.inteco.tutorial2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import edu.umich.si.inteco.minuku.config.Constants;
import edu.umich.si.inteco.minuku.logger.Log;

/**
 * Created by Joe on 2017/11/19.
 */

public class AppSetting extends Activity {
    public static boolean INTERMITTENT_SAMPLING = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        //Change Frequency of Sampling
        SeekBar samplingFrequency = (SeekBar) findViewById(R.id.samplingFrequency);
        samplingFrequency.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int factor, boolean b) {
                Constants.LOCATION_SAMPLE_FREQUENCY = 24 - factor * 3; // Default 15 is one minute
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //Intermittent Sampling
        Switch intermittentSampling = (Switch) findViewById(R.id.intermittent_sampling);
        intermittentSampling.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean flag) {
                if (flag) {
                    AppSetting.INTERMITTENT_SAMPLING = true;
                }
                else AppSetting.INTERMITTENT_SAMPLING = false;
            }
        });

        //Save and Return
        Button saveButton = (Button) findViewById(R.id.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText userEmail = (EditText) findViewById(R.id.userEmail);
                Intent intent = new Intent(AppSetting.this, MainActivity.class);
                intent.putExtra("User_Email",userEmail.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
                //startActivity(intent);
            }
        });
    }
}
