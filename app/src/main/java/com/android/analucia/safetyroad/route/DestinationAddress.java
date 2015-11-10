package com.android.analucia.safetyroad.route;


import android.os.Parcel;
import android.os.Parcelable;

public class DestinationAddress implements Parcelable{

    String dAddress;


    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * Storing the Student data to Parcel object
     **/
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dAddress);

    }

    /**
     * A constructor that initializes the Student object
     **/
    public DestinationAddress(String destination){
        this.dAddress = destination;

    }

    /**
     * Retrieving Student data from Parcel object
     * This constructor is invoked by the method createFromParcel(Parcel source) of
     * the object CREATOR
     **/
    private DestinationAddress(Parcel in){
        this.dAddress = in.readString();

    }

    public static final Parcelable.Creator<DestinationAddress> CREATOR = new Parcelable.Creator<DestinationAddress>() {

        @Override
        public DestinationAddress createFromParcel(Parcel source) {
            return new DestinationAddress(source);
        }

        @Override
        public DestinationAddress[] newArray(int size) {
            return new DestinationAddress[size];
        }
    };

}
