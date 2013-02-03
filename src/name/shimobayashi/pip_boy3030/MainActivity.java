package name.shimobayashi.pip_boy3030;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {
	private final static int TIMER_INTERVAL = 1000;

	private Handler handler;
	private Timer timer;
	private SoundLevelMeter soundLevelMeter;
	private TremorSensor tremorSensor;

	private double tremor = -1;
	private double db = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Setup TremorSensor
		tremorSensor = new TremorSensor((SensorManager) getSystemService(SENSOR_SERVICE));
		tremorSensor.setListener(new TremorSensor.TremorSensorListener() {
			@Override
			public void onMeasureTremor(double _tremor) {
				tremor = _tremor;
			}
		});

		// Setup SoundLevelMeter
		soundLevelMeter = new SoundLevelMeter();
		soundLevelMeter
				.setListener(new SoundLevelMeter.SoundLevelMeterListener() {
					@Override
					public void onMeasureSoundLevel(double _db) {
						db = _db;
					}
				});
		(new Thread(soundLevelMeter)).start();

		// Call onTimer every TIMER_INTERVAL
		handler = new Handler();
		timer = new Timer(true);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				handler.post(new Runnable() {
					@Override
					public void run() {
						onTimer();
					}
				});
			}
		}, TIMER_INTERVAL, TIMER_INTERVAL);
	}

	private void onTimer() {
		TextView textView;

		// Show tremor
		textView = (TextView) findViewById(R.id.tremor_textview);
		textView.setText("Tremor : " + tremor);

		// Show sound level
		textView = (TextView) findViewById(R.id.soundlevel_textview);
		textView.setText("Sound Level : " + db + "(dB)");

		// Show time
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
		textView = (TextView) findViewById(R.id.datetime_textview);
		textView.setText(sdf.format(date));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	protected void onStop() {
		super.onStop();
		tremorSensor.pause();
		soundLevelMeter.pause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		tremorSensor.resume();
		soundLevelMeter.resume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		soundLevelMeter.stop();
	}

}
