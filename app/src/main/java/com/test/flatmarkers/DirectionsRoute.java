package com.test.flatmarkers;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

/**
 * Created by ahmed.ibrahim on 11-Oct-15.
 */
public class DirectionsRoute implements Comparable{
    private List<LatLng> geopoints;
    private long duration;
    private String durationText;
    private String summary;
    private PolylineOptions polyline;
    private LatLng southWest;
    private LatLng northEast;
    private LatLng waypoint;
    private LatLng markerPoint;
    private MarkerOptions marker;

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public List<LatLng> getGeopoints() {
        return geopoints;
    }

    public void setGeopoints(List<LatLng> geopoints) {
        this.geopoints = geopoints;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public PolylineOptions getPolyline() {
        return polyline;
    }

    public void setPolyline(PolylineOptions polyline) {
        this.polyline = polyline;
    }

    public LatLng getSouthWest() {
        return southWest;
    }

    public void setSouthWest(LatLng southWest) {
        this.southWest = southWest;
    }

    public LatLng getNorthEast() {
        return northEast;
    }

    public void setNorthEast(LatLng northEast) {
        this.northEast = northEast;
    }

    public LatLng getWaypoint() {
        return waypoint;
    }

    public void setWaypoint(LatLng waypoint) {
        this.waypoint = waypoint;
    }


    public String getDurationText() {
        return durationText;
    }

    public void setDurationText(String durationText) {
        this.durationText = durationText;
    }


    public LatLng getMarkerPoint() {
        return markerPoint;
    }

    public void setMarkerPoint(LatLng markerPoint) {
        this.markerPoint = markerPoint;
    }

    public MarkerOptions getMarker() {
        return marker;
    }

    public void setMarker(MarkerOptions marker) {
        this.marker = marker;
    }

    @Override
    /*
    for Sorting
     */
    public int compareTo(Object another) {
        DirectionsRoute anotherObject = (DirectionsRoute)another;
        return (new Long(this.duration).compareTo(anotherObject.getDuration()));
    }

    /*
    For equality comparison
     */
    @Override
    public boolean equals(Object o){
        DirectionsRoute other = (DirectionsRoute)o;
        return this.getSummary().equals(other.getSummary()) && this.geopoints.size() == other.getGeopoints().size() && this.duration == other.getDuration();
    }
}
