package de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler.domain;

public class AirplanePosition {
	
	private double latitude;
	private double longitude;
	private double altitude;
	private int pitch;
	private int bank;
	private int heading;
	private int speed;
	
	public double getLatitude() {
		return latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public double getAltitude() {
		return altitude;
	}
	
	public int getPitch() {
		return pitch;
    }
	
	public int getBank() {
		return bank;
	}
	
	public int getHeading() {
		return heading;
	}

	public int getSpeed() {
		return speed;
	}

	public AirplanePosition(double latitude, double longitude, double altitude, int pitch, int bank, int heading, int speed) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
		this.pitch = pitch;
		this.bank = bank;
		this.heading = heading;
		this.speed = speed;
	}

	@Override
	public String toString() {
		return "AirplanePosition [latitude=" + latitude + ", longitude=" + longitude + ", altitude=" + altitude + ", pitch=" + pitch + ", bank="
				+ bank + ", heading=" + heading + ", speed=" + speed + "]";
	}

}
