package com.orosz.myapp.uberclone.Model;

public class UberDriver {
    private String DriverUID;
    private String longitude;
    private String latitude;
    private String driverStatus;

    public UberDriver() {
    }

    public UberDriver(String driverUID, String longitude, String latitude, String driverStatus) {
        DriverUID = driverUID;
        this.longitude = longitude;
        this.latitude = latitude;
        this.driverStatus = driverStatus;
    }

    public String getDriverUID() {
        return DriverUID;
    }

    public void setDriverUID(String driverUID) {
        DriverUID = driverUID;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getDriverStatus() {
        return driverStatus;
    }

    public void setDriverStatus(String driverStatus) {
        this.driverStatus = driverStatus;
    }
}
