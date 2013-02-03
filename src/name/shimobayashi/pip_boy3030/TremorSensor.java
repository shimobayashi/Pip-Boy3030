package name.shimobayashi.pip_boy3030;

import java.util.List;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class TremorSensor implements SensorEventListener {
	private SensorManager manager;
	private double[] currentOrientationValues = { 0.0, 0.0, 0.0 };
	private double[] currentAccelerationValues = { 0.0, 0.0, 0.0 };

	public interface TremorSensorListener {
		void onMeasureTremor(double tremor);
	}

	private TremorSensorListener listener;

	public TremorSensor(SensorManager _manager) {
		manager = _manager;
	}

	public void setListener(TremorSensorListener _listener) {
		listener = _listener;
	}

	public void pause() {
		manager.unregisterListener(this);
	}

	public void resume() {
		List<Sensor> sensors = manager.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if (sensors.size() > 0) {
			Sensor s = sensors.get(0);
			manager.registerListener(this, s,
					SensorManager.SENSOR_DELAY_GAME);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			currentOrientationValues[0] = event.values[0] * 0.1f
					+ currentOrientationValues[0] * (1.0f - 0.1f);
			currentOrientationValues[1] = event.values[1] * 0.1f
					+ currentOrientationValues[1] * (1.0f - 0.1f);
			currentOrientationValues[2] = event.values[2] * 0.1f
					+ currentOrientationValues[2] * (1.0f - 0.1f);

			currentAccelerationValues[0] = event.values[0]
					- currentOrientationValues[0];
			currentAccelerationValues[1] = event.values[1]
					- currentOrientationValues[1];
			currentAccelerationValues[2] = event.values[2]
					- currentOrientationValues[2];

			double currentTremor = Math.sqrt(Math.pow(
					currentAccelerationValues[0], 2)
					+ Math.pow(currentAccelerationValues[1], 2)
					+ Math.pow(currentAccelerationValues[2], 2));

			listener.onMeasureTremor(currentTremor);
		}
	}
}
