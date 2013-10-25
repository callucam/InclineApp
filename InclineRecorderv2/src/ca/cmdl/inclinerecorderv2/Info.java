package ca.cmdl.inclinerecorderv2;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class Info extends Activity implements OnItemSelectedListener {
	// Database variables
	private Long mRowId;
	private InclineData mIncline;
	
	private TextView mShipNameText;
	
	// Input fields
	 private EditText mProjectNumText;
	 private EditText mShipTypeText;
	 private EditText mExpDateText;
	 private EditText mWeatherText;
	 private EditText mDisplText;
	 private EditText mWindHeadText;
	 private EditText mShipFreeText;
	 private EditText mAttendText;
	 private EditText mClientText;
	 private EditText mDimWhtText;
	 
	 private EditText mMassWhtAText;
	 private EditText mMassWhtBText;
	 private EditText mMassWhtCText;
	 private EditText mMassWhtDText;
	 private EditText mDistWhtAText;
	 private EditText mDistWhtBText;
	 private EditText mDistWhtCText;
	 private EditText mDistWhtDText;
	 
	 private TextView mMassWhtALabelText;
	 private TextView mMassWhtBLabelText;
	 private TextView mMassWhtCLabelText;
	 private TextView mMassWhtDLabelText;
	 private TextView mDistWhtALabelText;
	 private TextView mDistWhtBLabelText;
	 private TextView mDistWhtCLabelText;
	 private TextView mDistWhtDLabelText;
	 
	 private Spinner mUnitsSpinner;
	 
	 // Variable describing ship units (metric = 0, imperial = 1)
	 private int units;
	 // Constants describing units of each system
	 private static final String[] MASS_UNITS = new String[2];
	 private static final String[] DIST_UNITS = new String[2];
	 
    
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info);
		
		// Define unit labels
		DIST_UNITS[0] = " (m)";
		DIST_UNITS[1] = " (dec. ft)";
		MASS_UNITS[0] = " (kg)";
		MASS_UNITS[1] = " (lb)";
		
		mIncline = new InclineData(this);
		
		// Extract the current new or loaded ship id from the intent
		mRowId = InclineRecorderv2.CURRENT_ROWID;
		
		// Set ship name text field
		mShipNameText = (TextView) findViewById(R.id.ship_name_set);
		
		mProjectNumText = (EditText) findViewById(R.id.project_number);
		mShipTypeText = (EditText) findViewById(R.id.ship_type);
		mExpDateText = (EditText) findViewById(R.id.exp_date);
		mWeatherText = (EditText) findViewById(R.id.weather);
		mDisplText = (EditText) findViewById(R.id.displ);
		mWindHeadText = (EditText) findViewById(R.id.ship_head);
		mShipFreeText = (EditText) findViewById(R.id.ship_free);
		mAttendText = (EditText) findViewById(R.id.attend);
		mClientText = (EditText) findViewById(R.id.client);
		mDimWhtText = (EditText) findViewById(R.id.dim_wht);
		mMassWhtAText = (EditText) findViewById(R.id.mass_whta);
		mMassWhtBText = (EditText) findViewById(R.id.mass_whtb);
		mMassWhtCText = (EditText) findViewById(R.id.mass_whtc);
		mMassWhtDText = (EditText) findViewById(R.id.mass_whtd);
		mDistWhtAText = (EditText) findViewById(R.id.dist_whta);
		mDistWhtBText = (EditText) findViewById(R.id.dist_whtb);
		mDistWhtCText = (EditText) findViewById(R.id.dist_whtc);
		mDistWhtDText = (EditText) findViewById(R.id.dist_whtd);		
		
		mMassWhtALabelText = (TextView) findViewById(R.id.mass_whta_label);
		mMassWhtBLabelText = (TextView) findViewById(R.id.mass_whtb_label);
		mMassWhtCLabelText = (TextView) findViewById(R.id.mass_whtc_label);
		mMassWhtDLabelText = (TextView) findViewById(R.id.mass_whtd_label);
		mDistWhtALabelText = (TextView) findViewById(R.id.dist_whta_label);
		mDistWhtBLabelText = (TextView) findViewById(R.id.dist_whtb_label);
		mDistWhtCLabelText = (TextView) findViewById(R.id.dist_whtc_label);
		mDistWhtDLabelText = (TextView) findViewById(R.id.dist_whtd_label);
		
		// Initializing spinner and listener
		mUnitsSpinner = (Spinner) findViewById(R.id.units_spinner);
		mUnitsSpinner.setOnItemSelectedListener(this);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, 
				R.array.units_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mUnitsSpinner.setAdapter(adapter);
	}
	
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		// Set units to selection
		units = pos;
		// Reset labels with units
    	mMassWhtALabelText.setText(this.getString(R.string.mass_whta) + MASS_UNITS[units]);
    	mMassWhtBLabelText.setText(this.getString(R.string.mass_whtb) + MASS_UNITS[units]);
    	mMassWhtCLabelText.setText(this.getString(R.string.mass_whtc) + MASS_UNITS[units]);
    	mMassWhtDLabelText.setText(this.getString(R.string.mass_whtd) + MASS_UNITS[units]);
    	mDistWhtALabelText.setText(this.getString(R.string.dist_whta) + DIST_UNITS[units]);
    	mDistWhtBLabelText.setText(this.getString(R.string.dist_whtb) + DIST_UNITS[units]);
    	mDistWhtCLabelText.setText(this.getString(R.string.dist_whtc) + DIST_UNITS[units]);
    	mDistWhtDLabelText.setText(this.getString(R.string.dist_whtd) + DIST_UNITS[units]);		
	}

	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		// Fill the input fields with ship info (blank if no previous data)
		fillFields(mRowId);
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		// Fill in blank weight info
		if (mMassWhtAText.getText().toString().matches("")) {
			mMassWhtAText.setText("0");
		}
		if (mMassWhtBText.getText().toString().matches("")) {
			mMassWhtBText.setText(mMassWhtAText.getText());
		}
		if (mMassWhtCText.getText().toString().matches("")) {
			mMassWhtCText.setText(mMassWhtAText.getText());
		}
		if (mMassWhtDText.getText().toString().matches("")) {
			mMassWhtDText.setText(mMassWhtAText.getText());
		}
		
		if (mDistWhtAText.getText().toString().matches("")) {
			mDistWhtAText.setText("0");
		}
		if (mDistWhtBText.getText().toString().matches("")) {
			mDistWhtBText.setText(mDistWhtAText.getText());
		}
		if (mDistWhtCText.getText().toString().matches("")) {
			mDistWhtCText.setText(mDistWhtAText.getText());
		}
		if (mDistWhtDText.getText().toString().matches("")) {
			mDistWhtDText.setText(mDistWhtAText.getText());
		}
		
		// Open database
		mIncline.openDB();
				
		mIncline.updateInfo(mRowId, 
				String.valueOf(mUnitsSpinner.getSelectedItemPosition()),
				mProjectNumText.getText().toString(), 
				mShipTypeText.getText().toString(),
				mExpDateText.getText().toString(),
				mWeatherText.getText().toString(),
				mDisplText.getText().toString(),
				mWindHeadText.getText().toString(),
				mShipFreeText.getText().toString(),
				mAttendText.getText().toString(),
				mClientText.getText().toString(),
				mDimWhtText.getText().toString(),
				mMassWhtAText.getText().toString(),
				mMassWhtBText.getText().toString(),
				mMassWhtCText.getText().toString(),
				mMassWhtDText.getText().toString(),
				mDistWhtAText.getText().toString(),
				mDistWhtBText.getText().toString(),
				mDistWhtCText.getText().toString(),
				mDistWhtDText.getText().toString());
	}
	
	@Override
	protected void onDestroy() {
		// Close database
		mIncline.closeDB();
		super.onDestroy();
	}

	private void fillFields(long shipId) {
		// Open database
		mIncline.openDB();
		
		// Get cursor from InclineData
		Cursor inclineCursor = mIncline.getShip(mRowId);
        startManagingCursor(inclineCursor);
        mShipNameText.setText(inclineCursor.getString(
        		inclineCursor.getColumnIndexOrThrow(InclineData.SHIPNAME)));
        mProjectNumText.setText(inclineCursor.getString(
        		inclineCursor.getColumnIndexOrThrow(InclineData.PROJECTNUM)));
        mShipTypeText.setText(inclineCursor.getString(
        		inclineCursor.getColumnIndexOrThrow(InclineData.SHIPTYPE)));
        mExpDateText.setText(inclineCursor.getString(
        		inclineCursor.getColumnIndexOrThrow(InclineData.EXPDATE)));
        mWeatherText.setText(inclineCursor.getString(
        		inclineCursor.getColumnIndexOrThrow(InclineData.WEATHER)));
        mDisplText.setText(inclineCursor.getString(
        		inclineCursor.getColumnIndexOrThrow(InclineData.DISPL)));
        mWindHeadText.setText(inclineCursor.getString(
        		inclineCursor.getColumnIndexOrThrow(InclineData.WINDHEAD)));
        mShipFreeText.setText(inclineCursor.getString(
        		inclineCursor.getColumnIndexOrThrow(InclineData.SHIPFREE)));
        mAttendText.setText(inclineCursor.getString(
        		inclineCursor.getColumnIndexOrThrow(InclineData.ATTEND)));
        mClientText.setText(inclineCursor.getString(
        		inclineCursor.getColumnIndexOrThrow(InclineData.CLIENT)));
        
        mDimWhtText.setText(inclineCursor.getString(
        		inclineCursor.getColumnIndexOrThrow(InclineData.DIMWHT)));
        mMassWhtAText.setText(inclineCursor.getString(
        		inclineCursor.getColumnIndexOrThrow(InclineData.MASSWHTA)));
        mMassWhtBText.setText(inclineCursor.getString(
        		inclineCursor.getColumnIndexOrThrow(InclineData.MASSWHTB)));
        mMassWhtCText.setText(inclineCursor.getString(
        		inclineCursor.getColumnIndexOrThrow(InclineData.MASSWHTC)));
        mMassWhtDText.setText(inclineCursor.getString(
        		inclineCursor.getColumnIndexOrThrow(InclineData.MASSWHTD)));
        mDistWhtAText.setText(inclineCursor.getString(
        		inclineCursor.getColumnIndexOrThrow(InclineData.DISTWHTA)));
        mDistWhtBText.setText(inclineCursor.getString(
        		inclineCursor.getColumnIndexOrThrow(InclineData.DISTWHTB)));
        mDistWhtCText.setText(inclineCursor.getString(
        		inclineCursor.getColumnIndexOrThrow(InclineData.DISTWHTC)));
        mDistWhtDText.setText(inclineCursor.getString(
        		inclineCursor.getColumnIndexOrThrow(InclineData.DISTWHTD)));
        
        // If no unit system is specified, default to metric
        String unitsQuery = inclineCursor.getString(
        		inclineCursor.getColumnIndexOrThrow(InclineData.UNITS));
        if (unitsQuery != null) {
        	units = Integer.parseInt(unitsQuery);
        } else {
        	units = 0;
        }
        
        mUnitsSpinner.setSelection(units);
	}

	
}
