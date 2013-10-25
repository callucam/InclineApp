package ca.cmdl.inclinerecorderv2;

import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import au.com.bytecode.opencsv.CSVReader;

public class Analysis extends Activity implements OnClickListener {
	
	// Database variables
	private Long mRowId;
	private InclineData mIncline;
	
	private TextView mCalcDisplText;
	private TextView mGMtText;
	private TextView mKMtText;
	private TextView mKGText;
	
	// Variables for calculations
	private double calcDispl,gmt,kmt,kg = 0;
	private double[] weight = new double[4];
	private double [] distance = new double[4];
	private double[] angleDeg = new double[9];
	private double[] angleRad = new double[9];
	private double[] moment = new double[9];
	private double[] draft = new double[6];
	
	// Creates list of doubles to store hydrostatics
	// Table is oriented horizontally, each column being one draft
	private List<double[]> hydroTable = new ArrayList<double[]>();
	private int numCols = 0;
	
	// Variable describing ship units (metric = 0, imperial = 1)
	 private int units;
	 // Constants describing units of each system
	 private static final String[] DISPL_UNITS = new String[2];
	 private static final String[] DIST_UNITS = new String[2];
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.analysis);
		
		// Define unit labels
		DISPL_UNITS[0] = " mt";
		DISPL_UNITS[1] = " lt";
		DIST_UNITS[0] = " m";
		DIST_UNITS[1] = " ft";
		
		mIncline = new InclineData(this);
		
		// Extract the current new or loaded ship id from the intent
		mRowId = InclineRecorderv2.CURRENT_ROWID;
		
		mCalcDisplText = (TextView) findViewById(R.id.calc_displ);
		mGMtText = (TextView) findViewById(R.id.gmt);
		mKMtText = (TextView) findViewById(R.id.kmt);
		mKGText = (TextView) findViewById(R.id.kg);
		
		View importHydroButton = findViewById(R.id.import_hydrostatics_button);
	    importHydroButton.setOnClickListener(this);
		View analyzeButton = findViewById(R.id.analyze_button);
	    analyzeButton.setOnClickListener(this);
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.import_hydrostatics_button:
    		importHydrostatics();
    		break;
		case R.id.analyze_button:
			performAnalysis();
    		break;
		}
	}

	@Override
    protected void onResume() {
    	super.onResume();
    	
    	// Fill the angle output fields with angles from database (blank if no previous data)
        fillFields(mRowId);
    }
	
	@Override
    protected void onPause() {
    	super.onPause();
    	
    	// Open database
		mIncline.openDB();
				
		mIncline.updateAnalysis(mRowId, 
				String.valueOf(calcDispl), 
				String.valueOf(gmt),
				String.valueOf(kmt),
				String.valueOf(kg));
    }
	
	@Override
	protected void onDestroy() {
		// Close database
		//mIncline.closeDB();
		super.onDestroy();
	}
	
	private void fillFields(long shipId) {
		// Open database
		mIncline.openDB();
		
		// Get cursor from InclineData
		Cursor inclineCursor = mIncline.getShip(mRowId);
        startManagingCursor(inclineCursor);
        units = Integer.parseInt(inclineCursor.getString(
        		inclineCursor.getColumnIndexOrThrow(InclineData.UNITS)));
        
        mCalcDisplText.setText(this.getString(R.string.calc_displ) + inclineCursor.getString(
        		inclineCursor.getColumnIndexOrThrow(InclineData.CALCDISPL)) + DISPL_UNITS[units]);
        mGMtText.setText(this.getString(R.string.gmt) + inclineCursor.getString(
        		inclineCursor.getColumnIndexOrThrow(InclineData.GMT)) + DIST_UNITS[units]);
        mKMtText.setText(this.getString(R.string.kmt) + inclineCursor.getString(
        		inclineCursor.getColumnIndexOrThrow(InclineData.KMT)) + DIST_UNITS[units]);
        mKGText.setText(this.getString(R.string.kg) + inclineCursor.getString(
        		inclineCursor.getColumnIndexOrThrow(InclineData.KG)) + DIST_UNITS[units]);

        weight[0] = Double.parseDouble(inclineCursor.getString(inclineCursor.getColumnIndexOrThrow(InclineData.MASSWHTA)));
        weight[1] = Double.parseDouble(inclineCursor.getString(inclineCursor.getColumnIndexOrThrow(InclineData.MASSWHTB)));
        weight[2] = Double.parseDouble(inclineCursor.getString(inclineCursor.getColumnIndexOrThrow(InclineData.MASSWHTC)));
        weight[3] = Double.parseDouble(inclineCursor.getString(inclineCursor.getColumnIndexOrThrow(InclineData.MASSWHTD)));
        distance[0] = Double.parseDouble(inclineCursor.getString(inclineCursor.getColumnIndexOrThrow(InclineData.DISTWHTA)));
        distance[1] = Double.parseDouble(inclineCursor.getString(inclineCursor.getColumnIndexOrThrow(InclineData.DISTWHTB)));
        distance[2] = Double.parseDouble(inclineCursor.getString(inclineCursor.getColumnIndexOrThrow(InclineData.DISTWHTC)));
        distance[3] = Double.parseDouble(inclineCursor.getString(inclineCursor.getColumnIndexOrThrow(InclineData.DISTWHTD)));
        		
		angleDeg[1] = Double.parseDouble(inclineCursor.getString(inclineCursor.getColumnIndexOrThrow(InclineData.ANGLEB)));
		angleDeg[2] = Double.parseDouble(inclineCursor.getString(inclineCursor.getColumnIndexOrThrow(InclineData.ANGLEC)));
		angleDeg[3] = Double.parseDouble(inclineCursor.getString(inclineCursor.getColumnIndexOrThrow(InclineData.ANGLED)));
		angleDeg[4] = Double.parseDouble(inclineCursor.getString(inclineCursor.getColumnIndexOrThrow(InclineData.ANGLEE)));
		angleDeg[5] = Double.parseDouble(inclineCursor.getString(inclineCursor.getColumnIndexOrThrow(InclineData.ANGLEF)));
		angleDeg[6] = Double.parseDouble(inclineCursor.getString(inclineCursor.getColumnIndexOrThrow(InclineData.ANGLEG)));
		angleDeg[7] = Double.parseDouble(inclineCursor.getString(inclineCursor.getColumnIndexOrThrow(InclineData.ANGLEH)));
		angleDeg[8] = Double.parseDouble(inclineCursor.getString(inclineCursor.getColumnIndexOrThrow(InclineData.ANGLEI)));
		
		draft[0] = Double.parseDouble(inclineCursor.getString(inclineCursor.getColumnIndexOrThrow(InclineData.FWDPT)));
		draft[1] = Double.parseDouble(inclineCursor.getString(inclineCursor.getColumnIndexOrThrow(InclineData.FWDSTBD)));
		draft[2] = Double.parseDouble(inclineCursor.getString(inclineCursor.getColumnIndexOrThrow(InclineData.MIDPT)));
		draft[3] = Double.parseDouble(inclineCursor.getString(inclineCursor.getColumnIndexOrThrow(InclineData.MIDSTBD)));
		draft[4] = Double.parseDouble(inclineCursor.getString(inclineCursor.getColumnIndexOrThrow(InclineData.AFTPT)));
		draft[5] = Double.parseDouble(inclineCursor.getString(inclineCursor.getColumnIndexOrThrow(InclineData.AFTSTBD)));
	}
	
	private void importHydrostatics() {    	
    	// Gets path to default external storage directory of device (eg. SD card)
    	File exportDir = new File(Environment.getExternalStorageDirectory(), "shipData");
    	
    	// Creates new file object
    	File file = new File(exportDir, "hydrostatics" + ".csv");
    	
    	try {
    		// Reads database to .csv file
    		CSVReader reader = new CSVReader(new FileReader(file));
    		
    		String[] nextLine;
    		hydroTable.clear();
    		
    		while((nextLine = reader.readNext()) != null){
    			numCols = nextLine.length;
    			double[] convLine = new double[numCols-1];
    			
    			// Convert all table values to doubles, and remove row headers
    			for (int i = 1; i < (numCols); i++) {
	    			try {
	    				convLine[i-1] = Double.parseDouble(nextLine[i]);	
	    			}
	    			catch (Exception ex) {
	    				Log.e("ParsingLine", ex.getMessage());
	    				convLine[i-1] = 0;
	    			}
    			}
    			
    			hydroTable.add(convLine);
    		}
    		
    		reader.close();    		
    	}
    	catch (Exception ex) {
    		Log.e("CSVReader", ex.getMessage());
    	}
	}
	
	private void performAnalysis() {
		double sumMoments = 0;
		double sumAngles = 0;
		// Sum of the products of moment and angle
		double sumProduct = 0;
		// Sum of the moments squared
		double sumSqrMoments = 0;
		// Slope of angle to moment graph
		double slope = 0;
		// Average draft
		double avgDraft = 0;
		
		// Calculate all 9 moments
		// Index 0,4,8 are zero
		Arrays.fill(moment, 0);
		moment[1] = (-1) * weight[0] * distance[0];
		moment[2] = (-1) * (weight[0] * distance[0] + weight[1] * distance[1]);
		moment[3] = (-1) * weight[0] * distance[0];
		moment[5] = weight[2] * distance[2];
		moment[6] = weight[2] * distance[2] + weight[3] * distance[3];
		moment[7] = weight[2] * distance[2];
		
		// Convert all angles to tan(angle)
		for (int i = 0; i < 9; i++) {
			if (moment[i] < 0) {
				angleRad[i] =  Math.tan((-1) * Math.toRadians(angleDeg[i]));
			} else {
				angleRad[i] = Math.tan(Math.toRadians(angleDeg[i]));
			}
			
			sumMoments += moment[i];
			sumAngles += angleRad[i];
			sumProduct += moment[i] * angleRad[i];
			sumSqrMoments += moment[i] * moment[i];
		}
		
		// Calculate slope using least squares
		slope = (sumProduct - sumMoments * sumAngles / 9) / (sumSqrMoments - sumMoments * sumMoments / 9);
		
		// Calculate average draft, excluding drafts with default value of 88888 (unrecorded)
		int count = 0;
		for (int i = 0; i < 6; i++) {
			if (draft[i] != 88888) {
				avgDraft += draft[i];
				count++;
			}
		}
		avgDraft /= count;
		
		// Loop through hydrostatics table to find displacement and KMt at average draft
		// Assume 0 trim (recorded drafts should all be the same)
		// Use drafts from table on row 5 (LCF)
		int index = 0;
		double[] tblDrafts = new double[numCols-1];
		double[] tblDispl = new double[numCols-1];
		double[] tblKMt = new double[numCols-1];
		tblDrafts = hydroTable.get(4);
		tblDispl = hydroTable.get(0);
		tblKMt = hydroTable.get(22);
		while (avgDraft > tblDrafts[index]) {
			index++;
		}
		// Interpolate to find displacement
		calcDispl = (avgDraft-tblDrafts[index]) * (tblDispl[index+1]-tblDispl[index]) /
				(tblDrafts[index+1]-tblDrafts[index]) + tblDispl[index];
		// Interpolate to find KMt
		kmt = (avgDraft-tblDrafts[index]) * (tblKMt[index+1]-tblKMt[index]) /
				(tblDrafts[index+1]-tblDrafts[index]) + tblKMt[index];
		
		// Calculate GMt, depending on units
		if (units == 0) {
			// Metric
			gmt = (1/slope)/(calcDispl*1000);
		} else {
			// Imperial
			gmt = (1/slope)/(calcDispl*2240);
		}
		
		
		// Calculate KG
		kg = kmt - gmt;
		
		// Round variables to 2 decimals
		DecimalFormat df = new DecimalFormat("#.##");
		calcDispl = Double.parseDouble(df.format(calcDispl));
		gmt = Double.parseDouble(df.format(gmt));
		kmt = Double.parseDouble(df.format(kmt));
		kg = Double.parseDouble(df.format(kg));
		
		// Display results
		mCalcDisplText.setText(this.getString(R.string.calc_displ) + String.format("%1.2f", calcDispl) + DISPL_UNITS[units]);
        mGMtText.setText(this.getString(R.string.gmt) + String.format("%1.2f", gmt) + DIST_UNITS[units]);
        mKMtText.setText(this.getString(R.string.kmt) + String.format("%1.2f", kmt) + DIST_UNITS[units]);
        mKGText.setText(this.getString(R.string.kg) + String.format("%1.2f", kg) + DIST_UNITS[units]);
	}
}
