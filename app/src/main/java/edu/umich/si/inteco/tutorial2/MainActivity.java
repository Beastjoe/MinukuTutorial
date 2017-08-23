package edu.umich.si.inteco.tutorial2;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import com.bugfender.sdk.Bugfender;
/*
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
*/
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataSourcesResult;

import java.util.concurrent.TimeUnit;

import edu.umich.si.inteco.minuku.config.Constants;
import edu.umich.si.inteco.minuku.config.PassFitData;
import edu.umich.si.inteco.minuku.config.UserPreferences;
import edu.umich.si.inteco.minuku.dao.ActivityDataRecordDAO;
import edu.umich.si.inteco.minuku.dao.FitDataRecordDAO;
import edu.umich.si.inteco.minuku.dao.LocationDataRecordDAO;
import edu.umich.si.inteco.minuku.dao.SensorDataRecordDAO;
import edu.umich.si.inteco.minuku.logger.Log;
import edu.umich.si.inteco.minuku.manager.MinukuDAOManager;
import edu.umich.si.inteco.minuku.manager.MinukuNotificationManager;
import edu.umich.si.inteco.minuku.manager.MinukuStreamManager;
import edu.umich.si.inteco.minuku.model.ActivityDataRecord;
import edu.umich.si.inteco.minuku.model.FitDataRecord;
import edu.umich.si.inteco.minuku.model.LocationDataRecord;
import edu.umich.si.inteco.minuku.model.SensorDataRecord;
import edu.umich.si.inteco.minuku.streamgenerator.ActivityStreamGenerator;
import edu.umich.si.inteco.minuku.streamgenerator.FitStreamGenerator;
import edu.umich.si.inteco.minuku.streamgenerator.LocationStreamGenerator;
import edu.umich.si.inteco.minuku.streamgenerator.SensorStreamGenerator;
import edu.umich.si.inteco.minukucore.exception.StreamNotFoundException;


public class MainActivity extends AppCompatActivity implements OnDataPointListener,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    TextView latitude = null;
    TextView longitude = null;
    TextView accelerometer_x=null;
    TextView accelerometer_y=null;
    TextView accelerometer_z=null;
    TextView activity=null;
    TextView stepCount=null;
    TextView fit=null;
    LocationDataRecord currentLocation = null;
    SensorDataRecord currentSensor=null;
    ActivityDataRecord currentActivity=null;
    FitDataRecord currentStepCount=null;
    PassFitData currentPassFitData=null;


    private static final int REQUEST_OAUTH = 1;
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;
    private GoogleApiClient mApiClient;//in the main activity
    //private CommonStatusCodes commonStatusCodes=null;


    private static final int READ_LOCATION = 1;
    //private SensorManager sensorManager;
    //private Sensor mAccelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(
                R.layout.activity_main);

        /*sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);*/

        latitude = (TextView) findViewById(R.id.current_latitude);
        longitude = (TextView) findViewById(R.id.current_longitude);
        accelerometer_x=(TextView) findViewById(R.id.curr_accelerometerx) ;
        accelerometer_y=(TextView) findViewById(R.id.curr_accelerometery) ;
        accelerometer_z=(TextView) findViewById(R.id.curr_accelerometerz) ;
        activity=(TextView) findViewById(R.id.curr_activity) ;
        stepCount=(TextView) findViewById(R.id.curr_stepcount) ;
        // TODO: implementation

        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.SENSORS_API)
                .addApi(Fitness.HISTORY_API) //maybe need history API to work
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();



        Constants.getInstance().setFirebaseUrl(getResources().getString(R.string.UNIQUE_FIREBASE_ROOT_URL));
        Constants.getInstance().setAppName(getResources().getString(R.string.app_name));

        setUserID();
        startService(new Intent(getBaseContext(), BackgroundService.class));
        startService(new Intent(getBaseContext(), MinukuNotificationManager.class));

        initialize();
    }


    private void setUserID() {
        if (UserPreferences.getInstance().getPreference(Constants.ID_SHAREDPREF_EMAIL) == null) {
            String email = "starry3@email,com"; // would be replaced by the user's email
            UserPreferences.getInstance().writePreference(Constants.ID_SHAREDPREF_EMAIL, email);
            UserPreferences.getInstance().writePreference(Constants.KEY_ENCODED_EMAIL,
                    encodeEmail(email));
        }
    }

    public static final String encodeEmail(String unencodedEmail) {
        if (unencodedEmail == null) return null;
        return unencodedEmail.replace(".", ",");
    }

    private void initialize() {



        MinukuDAOManager daoManager = MinukuDAOManager.getInstance();
        MinukuNotificationManager notificationManager = new MinukuNotificationManager();

        LocationDataRecordDAO locationDAO = new LocationDataRecordDAO();
        daoManager.registerDaoFor(LocationDataRecord.class, locationDAO);
        FitDataRecordDAO fitDAO = new FitDataRecordDAO();
        daoManager.registerDaoFor(FitDataRecord.class, fitDAO);
        ActivityDataRecordDAO activityDAO = new ActivityDataRecordDAO();
        daoManager.registerDaoFor(ActivityDataRecord.class, activityDAO);
        //SensorDataRecordDAO sensorDAO= new SensorDataRecordDAO();
        //daoManager.registerDaoFor(SensorDataRecord.class, sensorDAO);

        LocationStreamGenerator locationStreamGenerator =
                new LocationStreamGenerator(getApplicationContext());
        //SensorStreamGenerator sensorStreamGenerator= new SensorStreamGenerator(getApplicationContext());
        ActivityStreamGenerator activityStreamGenerator= new ActivityStreamGenerator(getApplicationContext());
        FitStreamGenerator fitStreamGenerator= new FitStreamGenerator(getApplicationContext());

        LocationChangeSituation situation = new LocationChangeSituation();
        LocationChangeAction action = new LocationChangeAction();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mApiClient.connect();


    }

    @Override
    protected void onResume() {
        super.onResume();
        //accelerometer.setText("Unknown");
        try {
            currentSensor =
                    MinukuStreamManager.getInstance().getStreamFor(SensorDataRecord.class).getCurrentValue();
        } catch (StreamNotFoundException e) {
            e.printStackTrace();
        }
        if(currentSensor!= null) {
            accelerometer_x.setText(String.valueOf(currentSensor.getAccelerometerX()));
            accelerometer_y.setText(String.valueOf(currentSensor.getAccelerometerY()));
            accelerometer_z.setText(String.valueOf(currentSensor.getAccelerometerZ()));

        }
        else {
            accelerometer_x.setText("unknown");
            accelerometer_y.setText("unknown");
            accelerometer_z.setText("unknown");
        }

        try {
            currentActivity =
                    MinukuStreamManager.getInstance().getStreamFor(ActivityDataRecord.class).getCurrentValue();
        } catch (StreamNotFoundException e) {
            e.printStackTrace();
        }
        if(currentActivity!= null) {
            activity.setText(String.valueOf(currentActivity.getMostProbableActivity()));
        }
        else {
            activity.setText("unknown");
        }

        try {
            currentStepCount =
                    MinukuStreamManager.getInstance().getStreamFor(FitDataRecord.class).getCurrentValue();
        } catch (StreamNotFoundException e) {
            e.printStackTrace();
        }
        if(currentStepCount!= null) {
            stepCount.setText(String.valueOf(currentStepCount.getStepCount()));
        }
        else {
            stepCount.setText("unknown");
        }


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    READ_LOCATION);
        } else {
            refreshLocation();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case READ_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    refreshLocation();
                } else {
                    finish();
                }
                return;
            }
        }
    }

    private void refreshLocation(){

        try {
            currentLocation =
                    MinukuStreamManager.getInstance().getStreamFor(LocationDataRecord.class).getCurrentValue();
        } catch (StreamNotFoundException e) {
            e.printStackTrace();
        }

        if(currentLocation!= null) {
            latitude.setText(String.valueOf(currentLocation.getLatitude()));
            longitude.setText(String.valueOf(currentLocation.getLongitude()));
        }
        else {
            latitude.setText("unknown");
            longitude.setText("unknown");
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        DataSourcesRequest dataSourceRequest = new DataSourcesRequest.Builder()
                .setDataTypes(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .setDataSourceTypes(DataSource.TYPE_RAW)
                .build();


            //commonStatusCodes= FitnessStatusCodes.getStatusCodeString(commonStatusCodes.toString());




        ResultCallback<DataSourcesResult> dataSourcesResultCallback = new ResultCallback<DataSourcesResult>() {
            @Override
            public void onResult(DataSourcesResult dataSourcesResult) {
                if (dataSourcesResult.getStatus().getStatusCode()==5000)
                {
                    try {
                        dataSourcesResult.getStatus().startResolutionForResult(MainActivity.this,10);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
                for (DataSource dataSource : dataSourcesResult.getDataSources()) {
                    if (DataType.TYPE_STEP_COUNT_CUMULATIVE.equals(dataSource.getDataType())) {
                        registerFitnessDataListener(dataSource, DataType.TYPE_STEP_COUNT_CUMULATIVE);
                    }
                }
            }
        };

        Fitness.SensorsApi.findDataSources(mApiClient, dataSourceRequest)
                .setResultCallback(dataSourcesResultCallback);

    }


        private void registerFitnessDataListener(DataSource dataSource, DataType dataType) {

            SensorRequest request = new SensorRequest.Builder()
                    .setDataSource( dataSource )
                    .setDataType( dataType )
                    .setSamplingRate( 1, TimeUnit.SECONDS )
                    .build();

            Fitness.SensorsApi.add( mApiClient, request, this )
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            if (status.isSuccess()) {
                                Log.e( "GoogleFit", "SensorApi successfully added" );
                            }
                        }
                    });
        }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onDataPoint(DataPoint dataPoint) {

        for( final Field field : dataPoint.getDataType().getFields() ) {
            final Value value = dataPoint.getValue( field );
            currentPassFitData.setFitData(value.asInt());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Field: " + field.getName() + " Value: " + value, Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        if(!authInProgress){
            try{
                authInProgress=true;
                connectionResult.startResolutionForResult( MainActivity.this, REQUEST_OAUTH );
            } catch(IntentSender.SendIntentException e ) {

            }
        } else {
            Log.e( "GoogleFit", "authInProgress" );
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode == REQUEST_OAUTH ) {
            authInProgress = false;
            if( resultCode == RESULT_OK) {
                if( !mApiClient.isConnecting() && !mApiClient.isConnected() ) {
                    mApiClient.connect(); // actual connected
                }
            } else if( resultCode == RESULT_CANCELED ) {
                Log.e( "GoogleFit", "RESULT_CANCELED" );
            }
        } else {
            Log.e("GoogleFit", "requestCode NOT request_oauth");
        }
    }

   /* @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }*/

    @Override
    protected void onStop() {
        super.onStop();

        Fitness.SensorsApi.remove( mApiClient, this )
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            mApiClient.disconnect();
                        }
                    }
                });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(AUTH_PENDING, authInProgress);
    }
}
