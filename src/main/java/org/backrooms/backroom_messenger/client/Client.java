package org.backrooms.backroom_messenger.client;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.backrooms.backroom_messenger.entity.*;
import org.backrooms.backroom_messenger.response_and_requests.serverRequest.*;
import org.backrooms.backroom_messenger.response_and_requests.serverResopnse.AvailableUserResponse;
import org.backrooms.backroom_messenger.response_and_requests.serverResopnse.ChatOpenedResponse;
import org.backrooms.backroom_messenger.response_and_requests.serverResopnse.SearchedUsersListResponse;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import static org.backrooms.backroom_messenger.StaticMethods.generateSalt;


public class Client  {


    static ObjectMapper mapper = new ObjectMapper();
    static User loggedUser;
    static Socket socket;
    static DataInputStream dis;
    static DataOutputStream dos;

    public static void initializeClient() throws IOException {
        socket = new Socket("localhost",8888);
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());
    }



    //TODO should be removed
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
            //while(true) {
                System.out.println("1. search");
                System.out.println("2. chats");
                option = scanner.nextInt();
                switch (option) {
                    case 1:
                        String searchQuery = scanner.next();
                        search(searchQuery);
                        break;
                    case 2:
                        showChats();
                }
            //}
        }else{
            System.out.println("not logged in ");
        }
    }

    //todo should be removed
    private static void showChats() {
        int k = 0;
        for(Chat chat : loggedUser.getChats()) {
            System.out.println(++k + "-" + chat.getName(loggedUser));
        }
        Scanner scanner = new Scanner(System.in);
        int option = scanner.nextInt() - 1;
        Chat chat = loggedUser.getChats().get(option);
        openChat(chat);
    }



    //for GUI
    public static void openChat(Chat chat){
        if (chat instanceof PvChat pv) {
            if(loggedUser.getUsername().equals(pv.getUser1().getUsername())){
                startChat(pv.getUser2());
            } else if (loggedUser.getUsername().equals(pv.getUser2().getUsername())) {
                startChat(pv.getUser1());
            }else{
                System.out.println("what the fuck??");
            }
        }
    }

    //for GUI
    public static Message sendMessage(String messageString, Chat chat){
        
    }

    //for GUI
    public static User login(String username, String password) throws IOException {
        if(dis == null || dos ==null || socket == null){
            initializeClient();
        }
        String message = username + "--" + password;
        LoginRequest lr = new LoginRequest(message);
        mapper.registerSubtypes(new NamedType(LoginRequest.class, "loginRequest"));
        try {
            sendRequest(lr);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return loggedUser;
    }

    //for GUI
    public static User signup(String username, String password) throws Exception {
        if(dis == null || dos ==null || socket == null){
            initializeClient();
        }
        byte[] salt = generateSalt();

        String message = username + "--" + password + "--" + salt;

        SignupRequest sr = new SignupRequest(message);
        mapper.registerSubtypes(new NamedType(SignupRequest.class, "signupRequest"));
        sendRequest(sr);
        return loggedUser;
    }

    //for GUI
    public static void search(String searchedString) throws Exception {
        SearchRequest sr = new SearchRequest(searchedString,User.changeToPrivate(loggedUser));
        mapper.registerSubtypes(new NamedType(PvChat.class,"PvChat"));
        mapper.registerSubtypes(new NamedType(SearchRequest.class, "searchRequest"));
        sendRequest(sr);
    }



    //calls GUI
    static void userListHandle(SearchedUsersListResponse sulr) {
        Scanner scn = new Scanner(System.in);
        List<Chat> chats = sulr.getChats();
        //TODO call GUI and send users list
        //redundant
        int i = 0;
        for(Chat chat : chats){
            System.out.println(++i +"-"+ chat.getName(loggedUser));
        }
        System.out.println("choose");
        int option = scn.nextInt();
        Chat selectedChat = chats.get(option-1);
        System.out.println(selectedChat.getName(loggedUser));
        System.out.println("do you want to chat?");
        System.out.println("1.yes, 2.no");
        option = scn.nextInt();
        if (option == 1){
            openChat(selectedChat);
        }
    }

    //calls GUI
    public static void openChat(ChatOpenedResponse cor){
        Chat chat = cor.getChat();
        if (!loggedUser.getChats().contains(chat)) {
            loggedUser.getChats().add(chat);
        }
        System.out.println("chat opened");

    }



    private static void sendRequest(ServerRequest sr) throws Exception {
        String request = mapper.writeValueAsString(sr);
        dos.writeUTF(request);
        dos.flush();
        if (loggedUser == null){
            String response = dis.readUTF();
            mapper.registerSubtypes(new NamedType(PvChat.class,"PvChat"));
            AvailableUserResponse aur = mapper.readValue(response,AvailableUserResponse.class);
            signupLoginCheck(aur);
        }
    }

    private static void startChat(PrivateUser selectedUser) {
        if(selectedUser.getUsername().equals(loggedUser.getUsername())){
            System.out.println("that's you motherfucker");
        }else{
            try {
                NewChatRequest ncr = new NewChatRequest(mapper.writeValueAsString(selectedUser),User.changeToPrivate(loggedUser));
                mapper.registerSubtypes(new NamedType(NewChatRequest.class, "newChatRequest"));
                sendRequest(ncr);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    private static void signupLoginCheck(AvailableUserResponse aur) {
        if(aur.isUserFound()){
            loggedUser = aur.getUser();
            clientReceiverStarter();
        }
    }

    private static void clientReceiverStarter () {
        ClientReceiver cr = new ClientReceiver(dis);
        Thread tr = new Thread(cr);
        tr.start();
    }
}
