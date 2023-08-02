package com.codesw.funnyvoice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.*;
import android.app.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.webkit.*;
import android.animation.*;
import android.view.animation.*;
import java.util.*;
import java.util.regex.*;
import java.text.*;
import org.json.*;
import java.util.ArrayList;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import com.google.android.material.button.*;
import android.content.Intent;
import android.net.Uri;
import android.app.AlertDialog;
import android.content.DialogInterface;
import java.util.Timer;
import java.util.TimerTask;
import android.speech.SpeechRecognizer;
import android.speech.RecognizerIntent;
import android.speech.RecognitionListener;
import android.widget.AdapterView;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;


public class MainActivity extends  AppCompatActivity  { 
	
	private Timer _timer = new Timer();
	
	private  AudioTrack audioTrack;
	private boolean recording = false;
	private double freqset = 0;
	private  java.io.File file = new java.io.File(Environment.getExternalStorageDirectory(), "test.pcm");;
	private double time = 0;
	
	private ArrayList<String> list = new ArrayList<>();
	
	private LinearLayout linear1;
	private LinearLayout linear3;
	private ImageView imageview1;
	private LinearLayout linear2;
	private Spinner sp_frequency;
	private MaterialButton materialbutton2;
	private MaterialButton materialbutton1;
	
	private Intent i = new Intent();
	private AlertDialog.Builder d;
	private TimerTask t;
	private SpeechRecognizer record;
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.main);
		initialize(_savedInstanceState);
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
		|| ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
		|| ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
			ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 1000);
		}
		else {
			initializeLogic();
		}
	}
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 1000) {
			initializeLogic();
		}
	}
	
	private void initialize(Bundle _savedInstanceState) {
		
		linear1 = (LinearLayout) findViewById(R.id.linear1);
		linear3 = (LinearLayout) findViewById(R.id.linear3);
		imageview1 = (ImageView) findViewById(R.id.imageview1);
		linear2 = (LinearLayout) findViewById(R.id.linear2);
		sp_frequency = (Spinner) findViewById(R.id.sp_frequency);
		materialbutton2 = (MaterialButton) findViewById(R.id.materialbutton2);
		materialbutton1 = (MaterialButton) findViewById(R.id.materialbutton1);
		d = new AlertDialog.Builder(this);
		record = SpeechRecognizer.createSpeechRecognizer(this);
		
		sp_frequency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> _param1, View _param2, int _param3, long _param4) {
				final int _position = _param3;
				if (_position == 0) {
					freqset = 5000;
				}
				if (_position == 1) {
					freqset = 6050;
				}
				if (_position == 2) {
					freqset = 8500;
				}
				if (_position == 3) {
					freqset = 11025;
				}
				if (_position == 4) {
					freqset = 16000;
				}
				if (_position == 5) {
					freqset = 22050;
				}
				if (_position == 6) {
					freqset = 41000;
				}
				if (_position == 7) {
					freqset = 30000;
				}
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> _param1) {
				
			}
		});
		
		materialbutton2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				t = new TimerTask() {
					@Override
					public void run() {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								time++;
							}
						});
					}
				};
				_timer.scheduleAtFixedRate(t, (int)(100), (int)(100));
				d.setTitle("Recording");
				d.setMessage(String.valueOf((long)(time)));
				d.setPositiveButton("Stop", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface _dialog, int _which) {
						recording = false;
						t.cancel();
						time = 0;
					}
				});
				d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface _dialog, int _which) {
						t.cancel();
					}
				});
				d.create().show();
				new Thread(new Runnable()
									  {
											  public void run()
											  {
													  recording = true;
													  _startRecord();
												  }
										  })
									  .start();
			}
		});
		
		materialbutton1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				if (file.exists()) {
					_playRecord();
				}
			}
		});
	}
	
	private void initializeLogic() {
		i.setClass(getApplicationContext(), SplashActivity.class);
		startActivity(i);
		
		list.add("Ghost");
		list.add("Slow Motion");
		list.add("Robot");
		list.add("Normal");
		list.add("Chipmunk");
		list.add("Funny");
		list.add("Bee");
		list.add("Elephant");
		sp_frequency.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, list));
		((ArrayAdapter)sp_frequency.getAdapter()).notifyDataSetChanged();
	}
	
	@Override
	protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
		
		super.onActivityResult(_requestCode, _resultCode, _data);
		
		switch (_requestCode) {
			
			default:
			break;
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		recording = false;
		if (audioTrack != null)
				{
						audioTrack.release();
				}
	}
	public void _startRecord () {
		try {
						file.createNewFile();
			
						java.io.OutputStream outputStream = new java.io.FileOutputStream(file);
						java.io.BufferedOutputStream bufferedOutputStream = new java.io.BufferedOutputStream(outputStream);
						java.io.DataOutputStream dataOutputStream = new java.io.DataOutputStream(bufferedOutputStream);
			
						int minBufferSize = AudioRecord.getMinBufferSize(11025, 2, 2);
			
						short[] audioData = new short[minBufferSize];
			
						AudioRecord audioRecord = new AudioRecord(1, 11025, 2, 2,minBufferSize);
			
						audioRecord.startRecording();
			
						while(recording){
								int numberOfShort = audioRecord.read(audioData, 0, minBufferSize);
								for(int i = 0; i < numberOfShort; i++){
										dataOutputStream.writeShort(audioData[i]);
								}
						}
						if (!recording)
				        {
								audioRecord.stop();
								dataOutputStream.close();
					        }
				} catch (java.io.IOException e) {
						e.printStackTrace();
				}
	}
	
	
	public void _playRecord () {
		int i = 0;
				file = new java.io.File(Environment.getExternalStorageDirectory(), "test.pcm");
				int shortSizeInBytes = Short.SIZE/Byte.SIZE;
				int bufferSizeInBytes = (int)(file.length()/shortSizeInBytes);
				short[] audioData = new short[bufferSizeInBytes];
				try {
						java.io.InputStream inputStream = new java.io.FileInputStream(file);
						java.io.BufferedInputStream bufferedInputStream = new java.io.BufferedInputStream(inputStream);
						java.io.DataInputStream dataInputStream = new java.io.DataInputStream(bufferedInputStream);
			
						int j = 0;
						while(dataInputStream.available() > 0){
								audioData[j] = dataInputStream.readShort();
								j++;
						}
			
						dataInputStream.close();
						i = (int)freqset;
						audioTrack = new AudioTrack(3,i,2,2,bufferSizeInBytes,1);
			
						audioTrack.play();
						audioTrack.write(audioData, 0, bufferSizeInBytes);
			
			
				} catch (java.io.FileNotFoundException e) {
						e.printStackTrace();
				} catch (java.io.IOException e) {
						e.printStackTrace();
				}
	}
	
	
	@Deprecated
	public void showMessage(String _s) {
		Toast.makeText(getApplicationContext(), _s, Toast.LENGTH_SHORT).show();
	}
	
	@Deprecated
	public int getLocationX(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[0];
	}
	
	@Deprecated
	public int getLocationY(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[1];
	}
	
	@Deprecated
	public int getRandom(int _min, int _max) {
		Random random = new Random();
		return random.nextInt(_max - _min + 1) + _min;
	}
	
	@Deprecated
	public ArrayList<Double> getCheckedItemPositionsToArray(ListView _list) {
		ArrayList<Double> _result = new ArrayList<Double>();
		SparseBooleanArray _arr = _list.getCheckedItemPositions();
		for (int _iIdx = 0; _iIdx < _arr.size(); _iIdx++) {
			if (_arr.valueAt(_iIdx))
			_result.add((double)_arr.keyAt(_iIdx));
		}
		return _result;
	}
	
	@Deprecated
	public float getDip(int _input){
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, _input, getResources().getDisplayMetrics());
	}
	
	@Deprecated
	public int getDisplayWidthPixels(){
		return getResources().getDisplayMetrics().widthPixels;
	}
	
	@Deprecated
	public int getDisplayHeightPixels(){
		return getResources().getDisplayMetrics().heightPixels;
	}
	
}