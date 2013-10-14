/**
 * 
 */
package dgz.bluetooth;

/**
 * @author Administrator
 *
 */
public class dataViewItem {
	String str;
	float temperature;
	float pressure;
	
	public float getTemperature() {
		return temperature;
	}

	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}

	public float getPressure() {
		
		return pressure;
	}

	public void setPressure(float pressure) {
		this.pressure = pressure;
	}

		
	public dataViewItem(String string) {
		// TODO 自动生成的构造函数存根
		this.str=string;
	}
	

}
