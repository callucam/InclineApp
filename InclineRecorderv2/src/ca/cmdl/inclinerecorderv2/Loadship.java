package ca.cmdl.inclinerecorderv2;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class Loadship extends ListActivity {
	private InclineData mIncline;
	
	private static final int DELETE_ID = Menu.FIRST + 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loadship);
		mIncline = new InclineData(this);
		registerForContextMenu(getListView());
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		fillNames();
	}

	@Override
	protected void onDestroy() {
		// Close database
		mIncline.closeDB();
		super.onDestroy();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
			mIncline.deleteShip(info.id);
			fillNames();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, InclineRecorderv2.class);
		i.putExtra(InclineData.ROWID, id);
		startActivity(i);
		finish();
	}

	/**
	 * Populates the list with all ship names stored in the database.
	 */
	private void fillNames() {
		// Open database
		mIncline.openDB();
		
		// Get cursor from InclineData
		Cursor inclineCursor = mIncline.getAllNames();
		startManagingCursor(inclineCursor);
		
		// Create an array to specify the fields to display (SHIPNAME)
		String[] from = new String[]{InclineData.SHIPNAME};
		
		// An array to bind the names to the list
		int[] to = new int[]{R.id.ship_row};
		
		// Create simple cursor adapter and set it to display
		SimpleCursorAdapter ships = new SimpleCursorAdapter(this, R.layout.shiprow, inclineCursor, from, to);
		setListAdapter(ships);
	}
}
