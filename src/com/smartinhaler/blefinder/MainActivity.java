package com.smartinhaler.blefinder;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private LocalBLEService s;
	private ListAdapter adapter;
	private ArrayList<String> wordList = new ArrayList<String>();
	private ListView deviceList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		deviceList = (ListView) findViewById(R.id.listDevicesView);
		
		wordList = new ArrayList<String>();
		wordList.add("Item1");
		wordList.add("Item2");
		wordList.add("Item3");
		
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1,wordList);
		deviceList.setAdapter(adapter);
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		bindService(new Intent(this,LocalBLEService.class), mConnection, Context.BIND_AUTO_CREATE);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unbindService(mConnection);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder binder) {
			s = ((LocalBLEService.MyBinder) binder).getService();
			Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
		}
		
		public void onServiceDisconnected(ComponentName className ) {
			s = null;
		}
	};
	

	public void showServiceData(View view) {
		if (s != null) {
			Toast.makeText(this, "Number of Elements" + s.getWordList().size(), Toast.LENGTH_SHORT).show();
			wordList.clear();
			wordList.addAll(s.getWordList());
			wordList.add("Test1");
			//adapter.notifyDataSetChanged();
			//deviceList.getAdapter().
			((BaseAdapter) deviceList.getAdapter()).notifyDataSetChanged(); 
		}
	}
	
	public void doScan(View view) {
		s.scan(true);
	}
	public void stopScan(View view) {
		s.scan(false);
	}
	
	
}
