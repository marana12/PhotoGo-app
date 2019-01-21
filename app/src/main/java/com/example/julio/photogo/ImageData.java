package com.example.julio.photogo;

import java.io.Serializable;

public class ImageData implements Serializable{
    private String markerId;
    private int ImgId;
    private String user;
    private String localname;
    private String zone;
    private String category;
    private String picture;
    private double latitude;
    private double longitude;
    private String coment;
    private String address;
    private String routeImg;
    private String date;

    public ImageData(String markerId, int imgId, String us, String locname, String zone, String category, String picture, double lat, double longi, String coment, String address){
        this.markerId = markerId;
        this.ImgId=imgId;
        this.user=us;
        this.localname=locname;
        this.zone = zone;
        this.category = category;
        this.picture = picture;
        this.latitude=lat;
        this.longitude=longi;
        this.coment = coment;
        this.address = address;
    }
    public ImageData(){

    }
    public int getImgId() {
        return ImgId;
    }

    public void setImgId(int imgId) {
        ImgId = imgId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getLocalname() {
        return localname;
    }

    public void setLocalname(String localname) {
        this.localname = localname;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getComent() {
        return coment;
    }

    public void setComent(String coment) {
        this.coment = coment;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMarkerId() {
        return markerId;
    }

    public void setMarkerId(String markerId) {
        this.markerId = markerId;
    }

    public String getRouteImg() {
        return routeImg;
    }

    public void setRouteImg(String rootImg) {
        this.routeImg = rootImg;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
