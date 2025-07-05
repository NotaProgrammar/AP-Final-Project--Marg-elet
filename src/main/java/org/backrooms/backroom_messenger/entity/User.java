package org.backrooms.backroom_messenger.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class User {
    @JsonProperty
    private String username;
    @JsonProperty
    private String password;
    @JsonProperty
    private Date dateOfBirth;
    @JsonProperty
    private byte[] salt;

    //todo add profile picture
    List<Chat> chats = new ArrayList<Chat>();

    public User(@JsonProperty("username") String username,@JsonProperty("password") String password,@JsonProperty("salt") byte[] salt) {
        this.username = username;
        this.password = password;
        this.salt = salt;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }



}
