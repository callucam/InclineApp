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

public class Intro extends Activity implements OnClickListener {
	
	private InclineData mIncline;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intro);
		mIncline = new InclineData(this);
		
		View newButton = findViewById(R.id.new_button);
	    newButton.setOnClickListener(this);
	    View loadButton = findViewById(R.id.load_button);
	    loadButton.setOnClickListener(this);
	    View exportButton = findViewById(R.id.export_button);
	    exportButton.setOnClickListener(this);
	    View importButton = findViewById(R.id.import_button);
	    importButton.setOnClickListener(this);
	}
    
    // Set up click listeners for all buttons
    public void onClick(View v) {
		switch (v.getId()) {
		case R.id.new_button:
			Intent iNew = new Intent(this, Newship.class);
    		startActivity(iNew);
    		break;
		case R.id.load_button:
			Intent iLoad = new Intent(this, Loadship.class);
			startActivity(iLoad);
			break;
		case R.id.export_button:
			exportDB();
			break;
		case R.id.import_button:
			importDB();
			break;
		}
    }
    
    // Exports all ships in database as a .csv file in app directory on phone
    private void exportDB() {
    	boolean mkdirsResult = false;
    	
    	// Gets path of ship database
    	File dbFile = getDatabasePath(InclineData.DATABASE_NAME);
    	
    	// Gets path to default external storage directory of device (eg. SD card)
    	File exportDir = new File(Environment.getExternalStorageDirectory(), "shipData");
    	
    	// Creates new directory if necessary
    	if (!exportDir.exists()) {
    		mkdirsResult = exportDir.mkdirs();
    	}
    	
    	// Creates new file object
    	File file = new File(exportDir, InclineData.TABLE_NAME + ".csv");
    	
    	ResultSet rs = null;
    	
    	Connection con = null;
    	
    	try {
    		// Delete old .csv file and create a new one
    		if (file.isFile()) {
    			file.delete();
    		}
    		file.createNewFile();
    		
    		// Load driver class
    		Class.forName("org.sqldroid.SQLDroidDriver").newInstance();
    	    // Connect to SQLite
    	    con = DriverManager.getConnection("jdbc:sqldroid:" + dbFile.getPath());
    		
    		// Build the ResultSet
    		Statement stmt = con.createStatement();
    		rs = stmt.executeQuery("SELECT * FROM " + InclineData.TABLE_NAME);
    		
    		// Writes database to .csv file
    		CSVWriter writer = new CSVWriter(new FileWriter(file));
    		ArrayList<String[]> strTable = new ArrayList<String[]>();
    		ResultSetMetaData rsmd = rs.getMetaData();
    		int columns = rsmd.getColumnCount();
    		
    		String[] colNames = new String[columns];
    		for (int i = 0; i < columns; i++) {
    			colNames[i] = rsmd.getColumnName(i+1);
    		}
    		strTable.add(colNames);
    		
    		while(rs.next()){
    			String[] line = new String[columns];
    			for (int i = 0; i < columns; i++) {
    				line[i] = rs.getString(i+1);
    			}
    			strTable.add(line);
    		}
    		
    		writer.writeAll(strTable);
    		writer.close();
    	}
    	catch (Exception ex) {
    		Log.e("CSVWriter", ex.getMessage());
    	}
    	finally {
    		if (rs !=null) {
    			try {
    				rs.close();
    			}
    			catch (SQLException ex) {
    				Log.e("ResultSet Close", ex.getMessage());
    			}
    		}
    	}
    }
    
    private void importDB() {
    	
    	// Gets path of ship database
    	File dbFile = getDatabasePath(InclineData.DATABASE_NAME);
    	
    	// Gets path to default external storage directory of device (eg. SD card)
    	File exportDir = new File(Environment.getExternalStorageDirectory(), "shipData");
    	
    	// Creates new file object
    	File file = new File(exportDir, InclineData.TABLE_NAME + ".csv");
    	
    	// Creates list of strings to store data
    	List<String[]> strTable = new ArrayList<String[]>();
    	
    	try {
    		// Load driver class
    		Class.forName("org.sqldroid.SQLDroidDriver").newInstance();
    	    // Connect to SQLite
    		Connection con = null;
    	    con = DriverManager.getConnection("jdbc:sqldroid:" + dbFile.getPath());
    		Statement stmt = con.createStatement();
    		
    		// Delete all rows
    		mIncline.openDB();
    		mIncline.clearDB();
    		
    		// Reads database to .csv file
    		CSVReader reader = new CSVReader(new FileReader(file));
    		
    		// Read line with column headers
    		String[] columnHeaders = reader.readNext();
    		
    		String[] nextLine;
    		
    		while((nextLine = reader.readNext()) != null){
    			int numCols = nextLine.length;
    			ContentValues args = new ContentValues();
    			for (int i = 0; i < (numCols); i++) {
    				args.put(columnHeaders[i], nextLine[i]);
    			}
    			// Insert line into database
    			mIncline.importShip(args);
    		}
    		
    		reader.close();    		
    	}
    	catch (Exception ex) {
    		Log.e("CSVReader", ex.getMessage());
    	}
    	
    }
}
