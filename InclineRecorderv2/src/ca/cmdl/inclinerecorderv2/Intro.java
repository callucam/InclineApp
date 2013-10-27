package ca.cmdl.inclinerecorderv2;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import java.util.Arrays;

import no.geosoft.cc.geometry.Geometry;
import android.app.Activity;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class Intro extends Activity implements OnClickListener, SensorEventListener {
	
	private SensorManager mSensorManager;
	private Sensor mAccel;
	private TextView curAngle_output;
	private TextView curAngle_output1;
	private TextView condA_output;
	private TextView condB_output;
	private TextView condC_output;
	private TextView condD_output;
	private TextView condE_output;
	private TextView condF_output;
	private TextView condG_output;
	private TextView condH_output;
	private TextView condI_output;
	// Alpha seek bar and variable
	private SeekBar alpha_bar;
	private TextView alpha_value;
	private float alpha;
	// Array of current gravity vector
	private double[] gravity = new double[3];
	// Array of gravity vectors for each condition
	private double[] gravity1 = new double[3];
	
	private double[][] condGravity = new double[9][3];
	// Array of angles for each condition
	private double[] condAngle = new double[9];
	// Database variables
	private Long mRowId;
	private InclineData mIncline;
	
	   
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.experiment);
		
		//mIncline = new InclineData(this);
		
		// Extract the current new or loaded ship id from the intent
		//mRowId = InclineRecorderv2.CURRENT_ROWID;
		
		// Accelerometer manager
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        
        // Condition angle indicating text views
        curAngle_output = (TextView) findViewById(R.id.curAngle_output);
        curAngle_output1 = (TextView) findViewById(R.id.curAngle_output1);
        condA_output = (TextView) findViewById(R.id.condA_output);
        condB_output = (TextView) findViewById(R.id.condB_output);
        condC_output = (TextView) findViewById(R.id.condC_output);
        condD_output = (TextView) findViewById(R.id.condD_output);
        condE_output = (TextView) findViewById(R.id.condE_output);
        condF_output = (TextView) findViewById(R.id.condF_output);
        condG_output = (TextView) findViewById(R.id.condG_output);
        condH_output = (TextView) findViewById(R.id.condH_output);
        condI_output = (TextView) findViewById(R.id.condI_output);
        
        // Condition angle set buttons
        View condAButton = findViewById(R.id.condA_button);
        condAButton.setOnClickListener(this);
        View condBButton = findViewById(R.id.condB_button);
        condBButton.setOnClickListener(this);
        View condCButton = findViewById(R.id.condC_button);
        condCButton.setOnClickListener(this);
        View condDButton = findViewById(R.id.condD_button);
        condDButton.setOnClickListener(this);
        View condEButton = findViewById(R.id.condE_button);
        condEButton.setOnClickListener(this);
        View condFButton = findViewById(R.id.condF_button);
        condFButton.setOnClickListener(this);
        View condGButton = findViewById(R.id.condG_button);
        condGButton.setOnClickListener(this);
        View condHButton = findViewById(R.id.condH_button);
        condHButton.setOnClickListener(this);
        View condIButton = findViewById(R.id.condI_button);
        condIButton.setOnClickListener(this);
        
        // Alpha seek bar listener
        alpha_bar = (SeekBar) findViewById(R.id.alpha_bar);
        alpha_value = (TextView) findViewById(R.id.alpha_value);
        alpha_value.setText("alpha: " + alpha_bar.getProgress());
        alpha_bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
        {

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				
				// Set alpha and display value
				alpha = (float) progress/100;
				alpha_value.setText("alpha: " + progress);
				
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
        	
        });
        
        
        // Set impossible default value of condAngle to detect if not recorded
        Arrays.fill(condAngle, 888.88);
	}
	
	@Override
    protected void onResume() {
    	super.onResume();
    	// Start updates from accelerometer
    	mSensorManager.registerListener(this, mAccel, SensorManager.SENSOR_DELAY_NORMAL);
    	
    	// Fill the angle output fields with angles from database (blank if no previous data)
        //fillFields(mRowId);
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	// Stop updates from accelerometer
    	mSensorManager.unregisterListener(this);
    	
    	// Open database
		// mIncline.openDB();
				
		// mIncline.updateExperiment(mRowId, 
		//		condAngle[1], condAngle[2], condAngle[3], condAngle[4], condAngle[5], condAngle[6], condAngle[7],  
		//		condAngle[8], condGravity[0][0], condGravity[0][1], condGravity[0][2]);
    }

    
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	
	
	@Override
	protected void onDestroy() {
		// Close database
		// mIncline.closeDB();
		super.onDestroy();
	}
    
public void onSensorChanged(SensorEvent event) {
		
		double[] origin = new double[3];
    	
		// Alpha value (smoothing factor) for low pass filter (set by seek bar)
    	//final float alpha = (float) 0.9;
    	
    	// Check if proper event
    	if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
    		
    		// Extract gravity vector and apply low pass filter to isolate gravity
    		gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
    		gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
    		gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
    		
    		// Display current angle
    		double angle_rad = Geometry.computeAngle(origin, condGravity[0], gravity);
    		double angle_deg = Math.toDegrees(angle_rad);
    		curAngle_output.setText("Current angle: " + String.format("%1.4f", angle_deg));
    	}
    	
    	
    	
if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
    		
    		// Extract gravity vector and apply low pass filter to isolate gravity
    		gravity1[0] = alpha * gravity1[0] + (1 - alpha) * event.values[0];
    		gravity1[1] = alpha * gravity1[1] + (1 - alpha) * event.values[1];
    		gravity1[2] = alpha * gravity1[2] + (1 - alpha) * event.values[2];
    		
    		// Display current angle
    		double angle_rad = Geometry.computeAngle(origin, condGravity[0], gravity1);
    		double angle_deg = Math.toDegrees(angle_rad);
//    		curAngle_output1.setText("Current angle1: " + String.format("%1.4f", gravity1[0]));
    		condA_output.setText(String.format("%1.4f", gravity1[0]));
    	}
    	
if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
	
	// Extract gravity vector and apply low pass filter to isolate gravity
	gravity1[0] = alpha * gravity1[0] + (1 - alpha) * event.values[0];
	gravity1[1] = alpha * gravity1[1] + (1 - alpha) * event.values[1];
	gravity1[2] = alpha * gravity1[2] + (1 - alpha) * event.values[2];
	
	// Display current angle
	double angle_rad = Geometry.computeAngle(origin, condGravity[0], gravity1);
	double angle_deg = Math.toDegrees(angle_rad);
//	curAngle_output1.setText("Current angle1: " + String.format("%1.4f", gravity1[0]));
	condB_output.setText(String.format("%1.4f", gravity1[1]));
}
if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
	
	// Extract gravity vector and apply low pass filter to isolate gravity
	gravity1[0] = alpha * gravity1[0] + (1 - alpha) * event.values[0];
	gravity1[1] = alpha * gravity1[1] + (1 - alpha) * event.values[1];
	gravity1[2] = alpha * gravity1[2] + (1 - alpha) * event.values[2];
	
	// Display current angle
	double angle_rad = Geometry.computeAngle(origin, condGravity[0], gravity1);
	double angle_deg = Math.toDegrees(angle_rad);
//	curAngle_output1.setText("Current angle1: " + String.format("%1.4f", gravity1[0]));
	condC_output.setText(String.format("%1.4f", gravity1[2]));
}   	
    	
    }
	
public void onClick(View v) {
	
//	double[] origin = new double[3];
//	double angle_rad, angle_deg;
//	
//	switch (v.getId()) {
//	case R.id.condA_button:
//		// Record first gravity vector
//		System.arraycopy(gravity, 0, condGravity[0], 0, 3);
//		// Indicate that vector is recorded
//		condA_output.setText("Set");
//		break;
//		
//	case R.id.condB_button:
//		// Set gravity vector
//		System.arraycopy(gravity, 0, condGravity[1], 0, 3);
//		// Compute angle in degrees relative to condition A
//		angle_rad = Geometry.computeAngle(origin, condGravity[0], condGravity[1]);
//		angle_deg = Math.toDegrees(angle_rad);
//		// Display angle below button
//		condB_output.setText(String.format("%1.2f", angle_deg));
//		// Save angle to condAngle
//		condAngle[1] = angle_deg;
//		break;
//		
//	case R.id.condC_button:
//		// Set gravity vector
//		System.arraycopy(gravity, 0, condGravity[2], 0, 3);
//		// Compute angle in degrees relative to condition A
//		angle_rad = Geometry.computeAngle(origin, condGravity[0], condGravity[2]);
//		angle_deg = Math.toDegrees(angle_rad);
//		// Display angle below button
//		condC_output.setText(String.format("%1.2f", angle_deg));
//		// Save angle to condAngle
//		condAngle[2] = angle_deg;
//		break;
//		
//	case R.id.condD_button:
//		// Set gravity vector
//		System.arraycopy(gravity, 0, condGravity[3], 0, 3);
//		// Compute angle in degrees relative to condition A
//		angle_rad = Geometry.computeAngle(origin, condGravity[0], condGravity[3]);
//		angle_deg = Math.toDegrees(angle_rad);
//		// Display angle below button
//		condD_output.setText(String.format("%1.2f", angle_deg));
//		// Save angle to condAngle
//		condAngle[3] = angle_deg;
//		break;
//		
//	case R.id.condE_button:
//		// Set gravity vector
//		System.arraycopy(gravity, 0, condGravity[4], 0, 3);
//		// Compute angle in degrees relative to condition A
//		angle_rad = Geometry.computeAngle(origin, condGravity[0], condGravity[4]);
//		angle_deg = Math.toDegrees(angle_rad);
//		// Display angle below button
//		condE_output.setText(String.format("%1.2f", angle_deg));
//		// Save angle to condAngle
//		condAngle[4] = angle_deg;
//		break;
//		
//	case R.id.condF_button:
//		// Set gravity vector
//		System.arraycopy(gravity, 0, condGravity[5], 0, 3);
//		// Compute angle in degrees relative to condition A
//		angle_rad = Geometry.computeAngle(origin, condGravity[0], condGravity[5]);
//		angle_deg = Math.toDegrees(angle_rad);
//		// Display angle below button
//		condF_output.setText(String.format("%1.2f", angle_deg));
//		// Save angle to condAngle
//		condAngle[5] = angle_deg;
//		break;
//		
//	case R.id.condG_button:
//		// Set gravity vector
//		System.arraycopy(gravity, 0, condGravity[6], 0, 3);
//		// Compute angle in degrees relative to condition A
//		angle_rad = Geometry.computeAngle(origin, condGravity[0], condGravity[6]);
//		angle_deg = Math.toDegrees(angle_rad);
//		// Display angle below button
//		condG_output.setText(String.format("%1.2f", angle_deg));
//		// Save angle to condAngle
//		condAngle[6] = angle_deg;
//		break;
//		
//	case R.id.condH_button:
//		// Set gravity vector
//		System.arraycopy(gravity, 0, condGravity[7], 0, 3);
//		// Compute angle in degrees relative to condition A
//		angle_rad = Geometry.computeAngle(origin, condGravity[0], condGravity[7]);
//		angle_deg = Math.toDegrees(angle_rad);
//		// Display angle below button
//		condH_output.setText(String.format("%1.2f", angle_deg));
//		// Save angle to condAngle
//		condAngle[7] = angle_deg;
//		break;
//		
//	case R.id.condI_button:
//		// Set gravity vector
//		System.arraycopy(gravity, 0, condGravity[8], 0, 3);
//		// Compute angle in degrees relative to condition A
//		angle_rad = Geometry.computeAngle(origin, condGravity[0], condGravity[8]);
//		angle_deg = Math.toDegrees(angle_rad);
//		// Display angle below button
//		condI_output.setText(String.format("%1.2f", angle_deg));
//		// Save angle to condAngle
//		condAngle[8] = angle_deg;
//		break;
//	}    		
}
	
}
