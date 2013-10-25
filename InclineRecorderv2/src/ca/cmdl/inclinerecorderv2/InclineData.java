/**
 * Checklist for adding info items:
 * 1 - Add Strings to strings.xml
 * 2 - Add Text and Edittext fields to layout.xml
 * 3 - Add columns to database in InclineData.java (static variables) and update methods (onCreate, updateInfo (dont forget params), getShip)
 * 4 - In Info.java, initialize edittext and update methods (onCreate, onPause, fillFields)
 * 5 - Reinstall app on phone or clear databases on emulator
 */

package ca.cmdl.inclinerecorderv2;

import java.text.DateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class InclineData extends SQLiteOpenHelper {
	
   public static final String DATABASE_NAME = "inclineVa.db";
   public static final int DATABASE_VERSION = 1;
   public static final String TABLE_NAME = "inclinedataVa";
   
   // Columns in the Incline database
   public static final String ROWID = "_id";
   public static final String DATE = "date";
   public static final String SHIPNAME = "shipname";
   public static final String UNITS = "units";
   public static final String PROJECTNUM = "projectnumber";
   public static final String SHIPTYPE = "shiptype";
   // Date experiment was conducted
   public static final String EXPDATE = "experimentdate";
   public static final String WEATHER = "weather";
   // Ship displacement at inclining
   public static final String DISPL = "displacement";
   // Ship heading wrt wind
   public static final String WINDHEAD = "windheading";
   // Is ship free to incline
   public static final String SHIPFREE = "shipfreetoincline";
   // People in attendance
   public static final String ATTEND = "inattendance";
   public static final String CLIENT = "client";
   // Dimensions of weights
   public static final String DIMWHT = "dimensionweight";
   // Position of weight A
   public static final String MASSWHTA = "massweighta";
   // Position of weight B
   public static final String MASSWHTB = "massweightb";
   // Position of weight C
   public static final String MASSWHTC = "massweightc";
   // Position of weight D
   public static final String MASSWHTD = "massweightd";
   // Distance to move weight A
   public static final String DISTWHTA = "distanceweightsmoveda";
   // Distance to move weight B
   public static final String DISTWHTB = "distanceweightsmovedb";
   // Distance to move weight C
   public static final String DISTWHTC = "distanceweightsmovedc";
   // Distance to move weight D
   public static final String DISTWHTD = "distanceweightsmovedd";
   
   public static final String ANGLEB = "angleB";
   public static final String ANGLEC = "angleC";
   public static final String ANGLED = "angleD";
   public static final String ANGLEE = "angleE";
   public static final String ANGLEF = "angleF";
   public static final String ANGLEG = "angleG";
   public static final String ANGLEH = "angleH";
   public static final String ANGLEI = "angleI";
   public static final String GRAVX = "gravX";
   public static final String GRAVY = "gravY";
   public static final String GRAVZ = "gravZ";
   
   // Drafts
   public static final String FWDPT = "fwdpt";
   public static final String FWDSTBD = "fwdstbd";
   public static final String MIDPT = "midpt";
   public static final String MIDSTBD = "midstbd";
   public static final String AFTPT = "aftpt";
   public static final String AFTSTBD = "aftstbd";
   
   // Analysis
   public static final String CALCDISPL = "calculateddisplacement";
   public static final String GMT = "gmt";
   public static final String KMT = "kmt";
   public static final String KG = "kg";
   
   private InclineData mIncline;
   private SQLiteDatabase mDb;
   private final Context mCtx;
   
   /** Create a helper object for the Ship database */
   public InclineData(Context ctx) { 
      super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
      this.mCtx = ctx;
   }
   
   // Creates new database using specified headers
   @Override
   public void onCreate(SQLiteDatabase db) { 
      db.execSQL("CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " 
    		  + DATE + " INTEGER," 
    		  + SHIPNAME + " TEXT NOT NULL," 
    		  + UNITS + " Text,"
    		  + PROJECTNUM + " TEXT," 
    		  + SHIPTYPE + " TEXT," 
    		  + EXPDATE + " TEXT,"
    		  + WEATHER + " TEXT,"
    		  + DISPL + " TEXT,"
    		  + WINDHEAD + " TEXT,"
    		  + SHIPFREE + " TEXT,"
    		  + ATTEND + " TEXT,"
    		  + CLIENT + " TEXT,"
    		  
    		  + DIMWHT + " TEXT,"
    		  + MASSWHTA + " TEXT,"
    		  + MASSWHTB + " TEXT,"
    		  + MASSWHTC + " TEXT,"
    		  + MASSWHTD + " TEXT,"
    		  + DISTWHTA + " TEXT,"
    		  + DISTWHTB + " TEXT,"
    		  + DISTWHTC + " TEXT,"
    		  + DISTWHTD + " TEXT,"
    		  
    		  + ANGLEB + " TEXT," 
    		  + ANGLEC + " TEXT,"
    		  + ANGLED + " TEXT,"
    		  + ANGLEE + " TEXT,"
    		  + ANGLEF + " TEXT,"
    		  + ANGLEG + " TEXT,"
    		  + ANGLEH + " TEXT,"
    		  + ANGLEI + " TEXT,"
    		  + GRAVX + " TEXT,"
    		  + GRAVY + " TEXT,"
    		  + GRAVZ + " TEXT,"
    		  
    		  + FWDPT + " TEXT,"
    		  + FWDSTBD + " TEXT,"
    		  + MIDPT + " TEXT,"
    		  + MIDSTBD + " TEXT,"
    		  + AFTPT + " TEXT,"
    		  + AFTSTBD + " TEXT,"

    		  + CALCDISPL + " TEXT,"
    		  + GMT + " TEXT,"
    		  + KMT + " TEXT,"
    		  + KG + " TEXT);");
   }

   @Override
   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	  // Drop old table
      db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
      // Create new updated table
      onCreate(db);
   }
   
   /**
    * Constructor - takes the context to allow the database to be
    * opened/created
    * 
    * @param ctx the Context within which to work
    */
   /*public InclineData(Context ctx) {
       this.mCtx = ctx;
   }*/
   
   /**
    * Open the ship database. If it cannot be opened, try to create a new
    * instance of the database. If it cannot be created, throws an exception to
    * signal the failure
    * 
    * @return this (self reference, allowing this to be chained in an
    *         initialization call)
    * @throws SQLException if the database could be neither opened or created
    */
   public InclineData openDB() throws SQLException {
	   mIncline = new InclineData(mCtx);
       mDb = this.getWritableDatabase();
       return this;
   }
   
   public void closeDB() {
       this.close();
   }
   
   public void clearDB() {
	   mDb.delete(TABLE_NAME, null, null);
   }
   
   /**
    * Create a new row using the ship name from the new activity. If successful,
    * returns the new rowID, otherwise -1 for failure.
    * 
    * @param shipname
    * @return rowID or -1 if failed
    */
   public long createShip(String name) {
	   // Create a date and time object
       Date mDate = new Date();
       // Create a new record into the Incline database
       // DATE and Shipname are specified, all other cells are null
       //mDb = this.getWritableDatabase();
       ContentValues contentValues = new ContentValues();
       contentValues.put(DATE, DateFormat.getDateInstance().format(mDate));
       contentValues.put(SHIPNAME, name);
       
       return mDb.insert(TABLE_NAME, null, contentValues);
   }
   
   /**
    * Delete the ship with the given rowId
    * 
    * @param rowId id of note to delete
    * @return true if deleted, false otherwise
    */
   public boolean deleteShip(long rowId) {

       return mDb.delete(TABLE_NAME, ROWID + "=" + rowId, null) > 0;
   }
   
   /**
    * Return a Cursor over the list of all notes in the database
    * 
    * @return Cursor over all notes
    */
   public Cursor getAllNames() {
	   return mDb.query(TABLE_NAME, new String[] {ROWID, DATE, SHIPNAME, }, null, null, null, null, null, null);
   }
   
   /**
    * Add ship as described in args specified
    * 
    * @param All columns of one ship row
    * @return rowID or -1 if failed
    */
   public long importShip(ContentValues args) {       
       return mDb.insert(TABLE_NAME, null, args);
   }
   
   /**
    * Update the ship info data using the details provided. The ship to be updated is
    * specified using the rowId
    * 
    * @param rowId id of ship to update
    * @param all input strings to update from the info tab
    * @return true if the ship was successfully updated, false otherwise
    */
   public boolean updateInfo(long rowId, String units, String projectnum, String shiptype, String expdate, String weather, 
		   String displ, String windhead, String shipfree, String attend, String client, String dimwht, 
		   String masswhta, String masswhtb, String masswhtc, String masswhtd, String distwhta, String distwhtb, 
		   String distwhtc, String distwhtd) {
       ContentValues args = new ContentValues();
       // Add new args.put line for each input field
       args.put(PROJECTNUM, projectnum);
       args.put(SHIPTYPE, shiptype);
       args.put(UNITS, units);
       args.put(EXPDATE, expdate);
       args.put(WEATHER, weather);
       args.put(DISPL, displ);
       args.put(WINDHEAD, windhead);
       args.put(SHIPFREE, shipfree);
       args.put(ATTEND, attend);
       args.put(CLIENT, client);
       args.put(DIMWHT, dimwht);
       args.put(MASSWHTA, masswhta);
       args.put(MASSWHTB, masswhtb);
       args.put(MASSWHTC, masswhtc);
       args.put(MASSWHTD, masswhtd);
       args.put(DISTWHTA, distwhta);
       args.put(DISTWHTB, distwhtb);
       args.put(DISTWHTC, distwhtc);
       args.put(DISTWHTD, distwhtd);

       return mDb.update(TABLE_NAME, args, ROWID + "=" + rowId, null) > 0;
   }
   
   /**
    * Update the ship condition angles data using the details provided. The ship to be updated is
    * specified using the rowId
    * 
    * @param rowId id of ship to update
    * @param all input doubles to update from the experiment tab
    * @return true if the ship was successfully updated, false otherwise
    */
   public boolean updateExperiment(long rowId, Double angleB, Double angleC, Double angleD, Double angleE,
		   Double angleF, Double angleG, Double angleH, Double angleI, Double gravX, Double gravY, Double gravZ) {
       ContentValues args = new ContentValues();
       // Add new args.put line for each input field
       args.put(ANGLEB, angleB);
       args.put(ANGLEC, angleC);
       args.put(ANGLED, angleD);
       args.put(ANGLEE, angleE);
       args.put(ANGLEF, angleF);
       args.put(ANGLEG, angleG);
       args.put(ANGLEH, angleH);
       args.put(ANGLEI, angleI);
       args.put(GRAVX, gravX);
       args.put(GRAVY, gravY);
       args.put(GRAVZ, gravZ);

       return mDb.update(TABLE_NAME, args, ROWID + "=" + rowId, null) > 0;
   }
   
   /**
    * Update the ship draft data using the details provided. The ship to be updated is
    * specified using the rowId
    * 
    * @param rowId id of ship to update
    * @param all drafts to update from the drafts tab
    * @return true if the ship was successfully updated, false otherwise
    */
   public boolean updateDrafts(long rowId, String fwdpt, String fwdstbd, String midpt, String midstbd,
		   String aftpt, String aftstbd) {
       ContentValues args = new ContentValues();
       // Add new args.put line for each input field
       args.put(FWDPT, fwdpt);
       args.put(FWDSTBD, fwdstbd);
       args.put(MIDPT, midpt);
       args.put(MIDSTBD, midstbd);
       args.put(AFTPT, aftpt);
       args.put(AFTSTBD, aftstbd);

       return mDb.update(TABLE_NAME, args, ROWID + "=" + rowId, null) > 0;
   }
   
   /**
    * Update the ship analysis data using the details provided. The ship to be updated is
    * specified using the rowId
    * 
    * @param rowId id of ship to update
    * @param all analysis data to update from the analysis tab
    * @return true if the ship was successfully updated, false otherwise
    */
   public boolean updateAnalysis(long rowId, String calcdispl, String gmt, String kmt, String kg) {
       ContentValues args = new ContentValues();
       // Add new args.put line for each input field
       args.put(CALCDISPL, calcdispl);
       args.put(GMT, gmt);
       args.put(KMT, kmt);
       args.put(KG, kg);

       return mDb.update(TABLE_NAME, args, ROWID + "=" + rowId, null) > 0;
   }
   
   /**
    * Return a Cursor positioned at the ship that matches the given rowId
    * 
    * @param rowId id of ship to extract
    * @return Cursor positioned to matching ship, if found
    * @throws SQLException if note could not be found/retrieved
    */
   public Cursor getShip(long rowId) throws SQLException {

       Cursor mCursor =

           mDb.query(true, TABLE_NAME, new String[] {ROWID, SHIPNAME, UNITS,
        		   PROJECTNUM, SHIPTYPE, EXPDATE, WEATHER, DISPL, WINDHEAD, SHIPFREE, ATTEND, CLIENT, DIMWHT, 
        		   MASSWHTA, MASSWHTB, MASSWHTC, MASSWHTD, DISTWHTA, DISTWHTB, DISTWHTC, DISTWHTD, 
        		   ANGLEB, ANGLEC, ANGLED, ANGLEE, ANGLEF, ANGLEG, ANGLEH, ANGLEI, GRAVX, GRAVY, GRAVZ,
        		   FWDPT, FWDSTBD, MIDPT, MIDSTBD, AFTPT, AFTSTBD, 
        		   CALCDISPL, GMT, KMT, KG},
        		   ROWID + "=" + rowId, null, null, null, null, null);
       if (mCursor != null) {
           mCursor.moveToFirst();
       }
       return mCursor;
   }
}