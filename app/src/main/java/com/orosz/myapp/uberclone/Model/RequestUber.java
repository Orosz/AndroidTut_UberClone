package com.orosz.myapp.uberclone.Model;

public class RequestUber {

    private String RiderUID;
    private String DriverUID;
    private String riderLongtitude;
    private String riderLatitude;
    private String driverLongtitude;
    private String driverLatitude;
    private String ReqStatus;

    public RequestUber() {
    }

    public RequestUber(String riderUID, String driverUID, String riderLongtitude, String riderLatitude, String driverLongtitude, String driverLatitude, String reqStatus) {
        RiderUID = riderUID;
        DriverUID = driverUID;
        this.riderLongtitude = riderLongtitude;
        this.riderLatitude = riderLatitude;
        this.driverLongtitude = driverLongtitude;
        this.driverLatitude = driverLatitude;
        ReqStatus = reqStatus;
    }

    public String getRiderUID() {
        return RiderUID;
    }

    public void setRiderUID(String riderUID) {
        RiderUID = riderUID;
    }

    public String getDriverUID() {
        return DriverUID;
    }

    public void setDriverUID(String driverUID) {
        DriverUID = driverUID;
    }

    public String getRiderLongtitude() {
        return riderLongtitude;
    }

    public void setRiderLongtitude(String riderLongtitude) {
        this.riderLongtitude = riderLongtitude;
    }

    public String getRiderLatitude() {
        return riderLatitude;
    }

    public void setRiderLatitude(String riderLatitude) {
        this.riderLatitude = riderLatitude;
    }

    public String getDriverLongtitude() {
        return driverLongtitude;
    }

    public void setDriverLongtitude(String driverLongtitude) {
        this.driverLongtitude = driverLongtitude;
    }

    public String getDriverLatitude() {
        return driverLatitude;
    }

    public void setDriverLatitude(String driverLatitude) {
        this.driverLatitude = driverLatitude;
    }

    public String getReqStatus() {
        return ReqStatus;
    }

    public void setReqStatus(String reqStatus) {
        ReqStatus = reqStatus;
    }
}


