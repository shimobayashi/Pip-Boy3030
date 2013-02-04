package name.shimobayashi.pip_boy3030;

import java.io.IOException;

import org.microbridge.server.Server;
import org.microbridge.server.AbstractServerListener;

import android.util.Log;

public class SensorsViaMicrobridge extends AbstractServerListener {
	private Server server;

	public interface SensorsViaMicrobridgeListener {
		void onMeasureViaMicrobridge(double temperature, double humidity,
				int pressure);
	}

	private SensorsViaMicrobridgeListener listener;
	
	public void setListener(SensorsViaMicrobridgeListener _listener) {
		listener = _listener;
	}

	public SensorsViaMicrobridge() {
		// Create TCP server
		try {
			server = new Server(4567);
			server.start();
		} catch (IOException e) {
			Log.e("microbridge", "Unable to start TCP server", e);
			System.exit(-1);
		}
		
		server.addListener(this);
	}

	@Override
	public void onReceive(org.microbridge.server.Client client, byte[] data) {
		if (data.length < 8)
			return;

		int thermoValue = (data[0] & 0xff) | ((data[1] & 0xff) << 8);
		int humidityValue = (data[2] & 0xff) | ((data[3] & 0xff) << 8);
		long pressureValue = (data[4] & 0xff) | ((data[5] & 0xff) << 8)
				| ((data[6] & 0xff) << 16) | ((data[7] & 0xff) << 24);

		// Voltage * 100C
		double temperature = (thermoValue * (5.0 / 1024)) * 100; 
		// Voltage * 100%
		double humidity = (humidityValue * (5.0 / 1024)) * 100;
		// Pascal
		int pressure = (int) pressureValue;
		
		listener.onMeasureViaMicrobridge(temperature, humidity, pressure);
	};
	
	public void stop() {
		server.stop();
	}

}
