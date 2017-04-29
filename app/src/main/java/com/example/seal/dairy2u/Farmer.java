package com.example.seal.dairy2u;

public class Farmer {
    public String name;
    public String email;
    public boolean delivery = false; //If the farmer is taking up a delivery

    public Farmer() {
        // Default constructor
    }

    public Farmer(String name, String email) {
        this.name = name;
        this.email = email;
    }

}
