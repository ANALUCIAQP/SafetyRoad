package com.android.analucia.safetyroad;


public class DistanceAndSpeeds implements Comparable{
    private final double distance;
    private final int suggestedSpeed;
    private final int speedLimit;

    public DistanceAndSpeeds(double distance, int suggestedSpeed, int speedLimit) {
        this.distance = distance;
        this.suggestedSpeed = suggestedSpeed;
        this.speedLimit = speedLimit;
    }

    public double getDistance() {
        return distance;
    }

    public int getSuggestedSpeed() {
        return suggestedSpeed;
    }

    public int getSpeedLimit() {
        return speedLimit;
    }

    @Override
    public int compareTo(Object obj) {
        DistanceAndSpeeds distanceAndSpeeds = (DistanceAndSpeeds) obj;
        return Double.valueOf(this.distance).compareTo(Double.valueOf(distanceAndSpeeds.getDistance()));
    }
}
