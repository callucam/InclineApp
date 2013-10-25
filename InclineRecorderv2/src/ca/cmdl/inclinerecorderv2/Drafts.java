package ca.cmdl.inclinerecorderv2;

import java.util.Arrays;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class Drafts extends Activity {
	
	// Input fields
	private EditText mFwdPtText;
	private EditText mFwdStbdText;
	private EditText mMidPtText;
	private EditText mMidStbdText;
	private EditText mAftPtText;
	private EditText mAftStbdText;
	
	private TextView mDraftsTitleText;
	
	// Database variables
	private Long mRowId;
	private InclineData mIncline;
	
	// Variable describing ship units (metric = 0, imperial = 1)
	 private int units;
	 // Constants describing units of each system
	 private static final String[] DIST_UNITS = new String[2];
	
	private String[] drafts = new String[6];

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drafts);
		
		mIncline = new InclineData(this);
		
		// Extract the current new or loaded ship id from the intent
		mRowId = InclineRecorderv2.CURRENT_ROWID;
		
		mFwdPtText = (EditText) findViewById(R.id.fwdpt_draft);
		mFwdStbdText = (EditText) findViewById(R.id.fwdstbd_draft);
		mMidPtText = (EditText) findViewById(R.id.midpt_draft);
		mMidStbdText = (EditText) findViewById(R.id.midstbd_draft);
		mAftPtText = (EditText) findViewById(R.id.aftpt_draft);
		mAftStbdText = (EditText) findViewById(R.id.aftstbd_draft);
		
		mDraftsTitleText = (TextView) findViewById(R.id.drafts_title);

		// Define unit labels
		DIST_UNITS[0] = " (m)";
		DIST_UNITS[1] = " (dec. ft)";
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
    	
    	// Overwrite default values with known drafts
    	Arrays.fill(drafts, "88888");
    	drafts[0] = mFwdPtText.getText().toString();
    	drafts[1] = mFwdStbdText.getText().toString();
    	drafts[2] = mMidPtText.getText().toString();
    	drafts[3] = mMidStbdText.getText().toString();
    	drafts[4] = mAftPtText.getText().toString();
    	drafts[5] = mAftStbdText.getText().toString();
    	
    	// Open database
		mIncline.openDB();
				
		mIncline.updateDrafts(mRowId, drafts[0], drafts[1], drafts[2],
				drafts[3], drafts[4], drafts[5]);
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
        mFwdPtText.setText(inclineCursor.getString(
        		inclineCursor.getColumnIndexOrThrow(InclineData.FWDPT)));
        mFwdStbdText.setText(inclineCursor.getString(
        		inclineCursor.getColumnIndexOrThrow(InclineData.FWDSTBD)));
        mMidPtText.setText(inclineCursor.getString(
        		inclineCursor.getColumnIndexOrThrow(InclineData.MIDPT)));
        mMidStbdText.setText(inclineCursor.getString(
        		inclineCursor.getColumnIndexOrThrow(InclineData.MIDSTBD)));
        mAftPtText.setText(inclineCursor.getString(
        		inclineCursor.getColumnIndexOrThrow(InclineData.AFTPT)));
        mAftStbdText.setText(inclineCursor.getString(
        		inclineCursor.getColumnIndexOrThrow(InclineData.AFTSTBD)));
        units = Integer.parseInt(inclineCursor.getString(
        		inclineCursor.getColumnIndexOrThrow(InclineData.UNITS)));
        
        // Add unit hint to title
        mDraftsTitleText.setText(this.getString(R.string.drafts_title) + DIST_UNITS[units]);
	}
}
