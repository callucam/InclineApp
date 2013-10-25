package ca.cmdl.inclinerecorderv2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class Newship extends Activity implements OnClickListener {
	private EditText mNameText;
	private InclineData mIncline;
	private long mID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newship);
		
		mNameText = (EditText) findViewById(R.id.ship_name_edit);
		
		View confirmButton = findViewById(R.id.confirm);
        confirmButton.setOnClickListener(this);
	}
	
	public void onClick(View v){
		switch (v.getId()) {
    	case R.id.confirm:
    		mIncline = new InclineData(this);
    		mIncline.openDB();
    		try {
    			mID = mIncline.createShip(mNameText.getText().toString());
    		} finally {
    			mIncline.closeDB();
    		}
			Intent i = new Intent(this, InclineRecorderv2.class);
			i.putExtra(InclineData.ROWID, mID);
			startActivity(i);
			finish();
			break;          
		}
	}
	
}
