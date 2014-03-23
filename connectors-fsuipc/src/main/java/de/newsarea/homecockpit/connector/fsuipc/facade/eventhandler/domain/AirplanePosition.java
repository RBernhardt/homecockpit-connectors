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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AirplanePosition)) return false;

        AirplanePosition that = (AirplanePosition) o;

        if (Double.compare(that.altitude, altitude) != 0) return false;
        if (bank != that.bank) return false;
        if (heading != that.heading) return false;
        if (Double.compare(that.latitude, latitude) != 0) return false;
        if (Double.compare(that.longitude, longitude) != 0) return false;
        if (pitch != that.pitch) return false;
        if (speed != that.speed) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(altitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + pitch;
        result = 31 * result + bank;
        result = 31 * result + heading;
        result = 31 * result + speed;
        return result;
    }

    @Override
	public String toString() {
		return "AirplanePosition [latitude=" + latitude + ", longitude=" + longitude + ", altitude=" + altitude + ", pitch=" + pitch + ", bank="
				+ bank + ", heading=" + heading + ", speed=" + speed + "]";
	}

}
