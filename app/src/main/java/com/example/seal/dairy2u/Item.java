package com.example.seal.dairy2u;

import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Parcelable{

    public String name;
    public String price;
    public String type;
    public int unit;

    public Item(){}

    public Item(String name, String price, String type){
        this.name = name;
        this.price = price;
        this.type = type;
    }

    public Item(Parcel in) {
        super();
        readFromParcel(in);
    }

    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        public Item[] newArray(int size) {

            return new Item[size];
        }

    };

    public void readFromParcel(Parcel in) {
        this.name = in.readString();
        this.price = in.readString();
        this.type = in.readString();
    }
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(price);
        dest.writeString(type);
    }
}
