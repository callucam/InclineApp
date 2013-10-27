package ca.cmdl.inclinerecorderv2;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class InclineRecorderv2 extends TabActivity {
	public static Long CURRENT_ROWID;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Extract the current loaded ship id from the intent
		Bundle extras = getIntent().getExtras();
		CURRENT_ROWID = extras != null ? extras.getLong(InclineData.ROWID) : null;
        
        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Reusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab

//        // Create an Intent to launch the Info tab
//        intent = new Intent().setClass(this, Info.class);
//        //intent.putExtra(InclineData.ROWID, mRowId);
//
//        // Initialize a TabSpec for each tab and add it to the TabHost
//        spec = tabHost.newTabSpec("info").setIndicator("Info",
//                          res.getDrawable(R.drawable.boat_icon))
//                      .setContent(intent);
//        tabHost.addTab(spec);

        // Do the same the Experiment tab
        intent = new Intent().setClass(this, Experiment.class);
        spec = tabHost.newTabSpec("experiment").setIndicator("Experiment",
                          res.getDrawable(R.drawable.boat_icon))
                      .setContent(intent);
        tabHost.addTab(spec);
        
//        // Do the same the Drafts tab
//        intent = new Intent().setClass(this, Drafts.class);
//        spec = tabHost.newTabSpec("drafts").setIndicator("Drafts",
//                          res.getDrawable(R.drawable.boat_icon))
//                      .setContent(intent);
//        tabHost.addTab(spec);
//        
//        // Do the same the Analysis tab
//        intent = new Intent().setClass(this, Analysis.class);
//        spec = tabHost.newTabSpec("analysis").setIndicator("Analysis",
//                          res.getDrawable(R.drawable.boat_icon))
//                      .setContent(intent);
//        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);
    }
}