package application;

import java.sql.Date;

public class Autoclaves {
	private int entry_no;
	private String prop_id;
	private Date run_date;
	private int runtime;
	private int temperature;
	private int pressure;
	private String t_result;
	private String load_desc;
	
	
	public Autoclaves(int entry_no, String prop_id, Date run_date, int runtime, int temperature,
			int pressure, String t_result, String load_desc) {
		super();
		this.entry_no = entry_no;
		this.prop_id = prop_id;
		this.run_date = run_date;
		this.runtime = runtime;
		this.temperature = temperature;
		this.pressure = pressure;
		this.t_result = t_result;
		this.load_desc = load_desc;
	}


	public int getEntry_no() {
		return entry_no;
	}


	public String getProp_id() {
		return prop_id;
	}


	public Date getRun_date() {
		return run_date;
	}


	public int getRuntime() {
		return runtime;
	}


	public int getTemperature() {
		return temperature;
	}


	public int getPressure() {
		return pressure;
	}


	public String getT_result() {
		return t_result;
	}


	public String getLoad_desc() {
		return load_desc;
	}
	
	
	

}
