package org.backrooms.backroom_messenger.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import org.backrooms.backroom_messenger.StaticMethods;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class User extends PrivateUser{
    @JsonProperty
    private String password;
    @JsonProperty
    private Date dateOfBirth;
    @JsonProperty
    private byte[] salt;

    @JsonProperty
    List<Chat> chats = new ArrayList<>();

    public User(@JsonProperty("username") String username,@JsonProperty("password") String password,@JsonProperty("salt") byte[] salt) {
        super(username,username);
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

    public String getPassword() {
        return password;
    }

    public List<Chat> getChats() {
        return chats;
    }

    public static PrivateUser changeToPrivate(User user){
        return new PrivateUser(user.getUsername(),user.getName());
    }

    public boolean isSubed(MultiUserChat muc){
        for(Chat chat : chats){
            if(chat.getId().equals(muc.getId())){
                return true;
            }
        }
        return false;
    }


    @JsonIgnore
    public boolean checkPassword(String password){
        try {
            String hashedPassword = StaticMethods.hashPassword(password,salt);
            return hashedPassword.equals(this.password);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public void setPassword(String password){

    }


}
