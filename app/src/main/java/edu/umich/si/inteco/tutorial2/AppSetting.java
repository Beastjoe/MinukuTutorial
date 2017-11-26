package edu.umich.si.inteco.tutorial2;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import edu.umich.si.inteco.minuku.config.Constants;
import edu.umich.si.inteco.minuku.config.SensorRate;
import edu.umich.si.inteco.minuku.dao.SensorDataRecordDAO;
import edu.umich.si.inteco.minuku.logger.Log;
import edu.umich.si.inteco.minuku.manager.MinukuDAOManager;
import edu.umich.si.inteco.minuku.model.SensorDataRecord;
import edu.umich.si.inteco.minuku.streamgenerator.SensorStreamGenerator;

/**
 * Created by Joe on 2017/11/19.
 */

public class AppSetting extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        //Change Frequency of Sampling
        RadioGroup rateGroup = (RadioGroup)findViewById(R.id.rateGroup);
        rateGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                RadioButton rateButton = (RadioButton)findViewById(radioGroup.getCheckedRadioButtonId());
                if (rateButton.getText().toString().equals("20Hz")){
                    SensorRate.SENSOR_RATE_CUSTOM = 50000;
                }
                else if (rateButton.getText().toString().equals("30Hz")){
                    SensorRate.SENSOR_RATE_CUSTOM = 33333;
                }
                else if (rateButton.getText().toString().equals("40Hz")){
                    SensorRate.SENSOR_RATE_CUSTOM = 25000;
                }
                else if (rateButton.getText().toString().equals("50Hz")){
                    SensorRate.SENSOR_RATE_CUSTOM = 20000;
                }
                else SensorRate.SENSOR_RATE_CUSTOM = 12500;
            }
        });

        //Intermittent Sampling
        final Switch intermittentSampling = (Switch) findViewById(R.id.intermittent_sampling);
        if (Constants.INTERMITTENT_SAMPLING) intermittentSampling.setChecked(true);
        else intermittentSampling.setChecked(false);
        intermittentSampling.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean flag) {
                if (flag) {
                    Constants.INTERMITTENT_SAMPLING = true;
                    intermittentSampling.setChecked(true);
                }
                else {
                    Constants.INTERMITTENT_SAMPLING = false;
                    intermittentSampling.setChecked(false);
                }
            }
        });


        //Optimized Data Record
        final Switch optimizedDateRecord = (Switch) findViewById(R.id.optimized_dataRecord);
        if (Constants.DATA_OPTIMZED) optimizedDateRecord.setChecked(true);
        else optimizedDateRecord.setChecked(false);
        optimizedDateRecord.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean flag) {
                if (flag) {
                    Constants.DATA_OPTIMZED = true;
                }
                else {
                    Constants.DATA_OPTIMZED = false;
                }
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
