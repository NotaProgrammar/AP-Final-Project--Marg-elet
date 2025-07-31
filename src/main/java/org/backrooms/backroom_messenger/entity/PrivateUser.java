package org.backrooms.backroom_messenger.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;


public class PrivateUser {
    @JsonProperty
    private String username;
    @JsonProperty
    private String name;
    @JsonProperty
    private boolean online = true;
    @JsonProperty
    private Date lastSeen;
    @JsonProperty
    private String bio = "";
    //todo profile

    public PrivateUser(@JsonProperty("username") String username,@JsonProperty("name") String name) {
        this.username = username;
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String toString(){
        if(online){
            return " Name: " + name + " Online: " + online;
        }else{
            return " Name: " + name + " last seen : " + lastSeen;
        }

    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public Date getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Date lastSeen) {
        this.lastSeen = lastSeen;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setBio(String bio){
        this.bio = bio;
    }

    public String getBio(){
        return bio;
    }
}
