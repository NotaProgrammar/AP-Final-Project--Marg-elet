package org.backrooms.backroom_messenger.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.backrooms.backroom_messenger.entity.Chat;
import org.backrooms.backroom_messenger.entity.PrivateUser;
import org.backrooms.backroom_messenger.entity.PvChat;
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
                System.out.println("name :");
                String username = scanner.next();
                System.out.println("password :");
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
                System.out.println("2. chats");
                option = scanner.nextInt();
                switch (option) {
                    case 1:
                        String searchQuery = scanner.next();
                        search(searchQuery);
                    case 2:
                        showChats();
                }
            }
        }else{
            System.out.println("not logged in ");
        }
    }

    private static void showChats() {
        int k = 0;
        for(Chat chat : loggedUser.getChats()) {
            System.out.println(++k + chat.getName(loggedUser));
        }
        Scanner scanner = new Scanner(System.in);
        int option = scanner.nextInt() - 1;
        Chat chat = loggedUser.getChats().get(option);
        switch(chat.getType()){
            case "pv_chat":
                PvChat pv = (PvChat) chat;

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
        List<PrivateUser> users = sulr.getUsers();
        int i = 0;
        for(PrivateUser user : users){
            System.out.println(++i +"-"+ user.getUsername());
        }
        System.out.println("choose");
        int option = scn.nextInt();
        PrivateUser selectedUser = users.get(option-1);
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

    private static void startChat(PrivateUser selectedUser) {
        if(selectedUser.getUsername().equals(loggedUser.getUsername())){
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
        SearchRequest sr = new SearchRequest(searchedString,loggedUser.getUsername());
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
