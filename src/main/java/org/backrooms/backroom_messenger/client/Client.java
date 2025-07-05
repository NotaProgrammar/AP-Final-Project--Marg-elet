package org.backrooms.backroom_messenger.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.backrooms.backroom_messenger.entity.User;
import org.backrooms.backroom_messenger.response_and_requests.serverRequest.*;
import org.backrooms.backroom_messenger.response_and_requests.serverResopnse.AvailableUserResponse;
import org.backrooms.backroom_messenger.response_and_requests.serverResopnse.SearchedUsersListResponse;
import org.backrooms.backroom_messenger.response_and_requests.serverResopnse.ServerResponse;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Scanner;

import static org.backrooms.backroom_messenger.StaticMethods.generateSalt;

public class Client  {


    static ObjectMapper mapper = new ObjectMapper();
    static User loggedUser;
    static Socket socket;
    static DataInputStream dis;
    static DataOutputStream dos;
    public Client(String host) throws IOException {

    }

    public static void main(String[] args) throws Exception {
        socket = new Socket("localhost",8888);
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());

        System.out.println("1.Login\n 2.sign up");
        Scanner scanner = new Scanner(System.in);
        int option = scanner.nextInt();
        switch (option) {
            case 1:
                String username = scanner.next();
                String password = scanner.next();
                login(username,password);
                break;
            case 2:
                System.out.println("name :");
                String name = scanner.next();
                System.out.println("password :");
                String pass = scanner.next();
                signup(name,pass);
                break;

        }
        if(loggedUser != null) {
            System.out.println("Logged in");
            while(true) {
                System.out.println("1. search");
                option = scanner.nextInt();
                switch (option) {
                    case 1:
                        String searchQuery = scanner.next();
                        search(searchQuery);
                }
            }
        }else{
            System.out.println("not logged in ");
        }
    }

    public static void login(String username, String password) throws Exception {
        String message = username + "--" + password;
        LoginRequest lr = new LoginRequest(message);
        mapper.registerSubtypes(new NamedType(LoginRequest.class, "loginRequest"));
        sendRequest(lr);
    }

    public static void signup(String username, String password) throws Exception, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = generateSalt();

        String message = username + "--" + password + "--" + salt;

        SignupRequest sr = new SignupRequest(message);
        mapper.registerSubtypes(new NamedType(SignupRequest.class, "signupRequest"));
        sendRequest(sr);
    }

    private static void responseCheck(ServerResponse sr){
        if(sr instanceof AvailableUserResponse aur){
            signupLoginCheck(aur);
        }else if(sr instanceof SearchedUsersListResponse sulr){
            userListHandle(sulr);
        }
    }

    private static void userListHandle(SearchedUsersListResponse sulr) {
        Scanner scn = new Scanner(System.in);
        List<User> users = sulr.getUsers();
        int i = 0;
        for(User user : users){
            System.out.println(++i +"-"+user);
        }
        System.out.println("choose");
        int option = scn.nextInt();
        User selectedUser = users.get(option-1);
        System.out.println(selectedUser.getUsername());
        System.out.println("do you want to chat?");
        System.out.println("1.yes, 2.no");
        option = scn.nextInt();
        switch(option){
            case 1:
                startChat(selectedUser);
                break;
            case 2:
                return;
        }
    }

    private static void startChat(User selectedUser) {
        if(selectedUser.equals(loggedUser)){
            System.out.println("that's you motherfucker");
        }else{
            try {
                NewChatRequest ncr = new NewChatRequest(mapper.writeValueAsString(selectedUser),loggedUser.getUsername());
                mapper.registerSubtypes(new NamedType(NewChatRequest.class, "newChatRequest"));
                sendRequest(ncr);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    private static void signupLoginCheck(AvailableUserResponse aur){
        if(aur.isUserFound()){
            loggedUser = aur.getUser();
        }
    }

    private static void search(String searchedString) throws Exception {
        String search = searchedString;
        SearchRequest sr = new SearchRequest(search,loggedUser.getUsername());
        mapper.registerSubtypes(new NamedType(SearchRequest.class, "searchRequest"));
        sendRequest(sr);
    }

    private static void sendRequest(ServerRequest sr) throws Exception {
        String request = mapper.writeValueAsString(sr);
        dos.writeUTF(request);
        dos.flush();
        String response = dis.readUTF();
        ServerResponse serverResponse = mapper.readValue(response,ServerResponse.class);
        responseCheck(serverResponse);
    }

}
