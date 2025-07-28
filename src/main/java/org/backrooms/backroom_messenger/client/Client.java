package org.backrooms.backroom_messenger.client;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.backrooms.backroom_messenger.ClientReceiverGUI;
import org.backrooms.backroom_messenger.entity.*;
import org.backrooms.backroom_messenger.response_and_requests.serverRequest.*;
import org.backrooms.backroom_messenger.response_and_requests.serverResopnse.AvailableUserResponse;
import org.backrooms.backroom_messenger.response_and_requests.serverResopnse.ChatModifyResponse;
import org.backrooms.backroom_messenger.response_and_requests.serverResopnse.SearchedUsersListResponse;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.backrooms.backroom_messenger.StaticMethods.generateSalt;


public class Client  {


    static ObjectMapper mapper = new ObjectMapper();
    static User loggedUser;
    static Socket socket;
    static DataInputStream dis;
    static DataOutputStream dos;
    static int sender;

    public static void initializeClient() throws IOException {
        socket = new Socket("localhost",8888);
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());
    }


    //for GUI
    public static void openChat(Chat chat,int sender){
        Client.sender = sender;
        if (chat instanceof PvChat pv) {
            if (loggedUser.getUsername().equals(pv.getUser1().getUsername())) {
                startChat(pv.getUser2());
            } else if (loggedUser.getUsername().equals(pv.getUser2().getUsername())) {
                startChat(pv.getUser1());
            } else {
                System.out.println("what the fuck??");
            }
        }else if(chat instanceof Channel channel){
            openChannel(channel);
        }
    }

    private static void openChannel(Channel channel) {
        try {
            OpenChannelRequest ocr = new OpenChannelRequest(channel.getId().toString(),User.changeToPrivate(loggedUser));
            mapper.registerSubtypes(new NamedType(OpenChannelRequest.class,"openChannelRequest"));
            sendRequest(ocr);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //for GUI
    public static Message sendMessage(String messageString, Chat chat){
        Message message = new Message(UUID.randomUUID(), loggedUser.getUsername(), chat.getId(), messageString,new Date(),chat.getType());
        try {
            mapper.registerSubtypes(new NamedType(PvChat.class, "PvChat"));
            mapper.registerSubtypes(new NamedType(Channel.class,"channel"));
            String messageJson = mapper.writeValueAsString(message);
            SendMessageRequest smr = new SendMessageRequest(messageJson,User.changeToPrivate(loggedUser));
            sendRequest(smr);
        } catch (Exception e) {
            message = null;
            System.out.println(e);
        }

        return message;
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

    //for GUI
    public static Channel createChannel(String name,String description,boolean publicOrPrivate){
        Channel newChannel = new Channel(UUID.randomUUID(),name,description,publicOrPrivate,loggedUser.getUsername());
        try {
            mapper.registerSubtypes(new NamedType(Channel.class,"channel"));
            String message = mapper.writeValueAsString(newChannel);
            NewChannelRequest ncr = new NewChannelRequest(message,User.changeToPrivate(loggedUser));
            sendRequest(ncr);
        } catch (Exception e) {
            System.out.println(e);
        }
        return newChannel;
    }

    //for GUI
    public static void Subscribe(Channel channel){
        try {
            mapper.registerSubtypes(new NamedType(Channel.class,"channel"));
            String message = mapper.writeValueAsString(channel);
            SubRequest sr = new SubRequest(message,User.changeToPrivate(loggedUser));
            mapper.registerSubtypes(new NamedType(SubRequest.class,"subRequest"));
            sendRequest(sr);
            for(Chat chat : loggedUser.getChats()){
                if(chat.getId().equals(channel.getId())){
                    loggedUser.getChats().remove(chat);
                    break;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    //for GUI
    public static void changeName(Chat chat,String newName){
        try {
            String message = chat.getId().toString() + "##name##channel##" + newName ;
            ChangePropertyRequest cpr = new ChangePropertyRequest(message,User.changeToPrivate(loggedUser));
            mapper.registerSubtypes(new NamedType(ChangePropertyRequest.class,"changePropertyRequest"));
            sendRequest(cpr);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    //for GUI
    public static void changeDescription(Chat chat,String newDescription){
        try{
            String message = chat.getId().toString() + "##description##channel##" + newDescription;
            ChangePropertyRequest cpr = new ChangePropertyRequest(message,User.changeToPrivate(loggedUser));
            mapper.registerSubtypes(new NamedType(ChangePropertyRequest.class,"changePropertyRequest"));
            sendRequest(cpr);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    //for GUI
    public static void changeUserRole(Chat chat,PrivateUser user){
        String role = null;
        //todo change
        if(chat instanceof Channel channel){
            role = channel.getRole(user);
            channel.changeRole(user);
        }
        //todo group
        try{
            String type = chat.getType();
            String message = chat.getId().toString() + "##" + user.getUsername() + "##" + role + "##" +type  ;
            ChangeRoleRequest crr = new ChangeRoleRequest(message,User.changeToPrivate(loggedUser));
            mapper.registerSubtypes(new NamedType(ChangeRoleRequest.class,"changeRoleRequest"));
            sendRequest(crr);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    //for GUI
    public static void removeUser(PrivateUser user,MultiUserChat chat){
        try{
            String type = chat.getType();
            String message = user.getUsername() + "##" + type + "##" + chat.getId();
            RemoveUserRequest rur = new RemoveUserRequest(message,User.changeToPrivate(loggedUser));
            mapper.registerSubtypes(new NamedType(RemoveUserRequest.class,"removeUserRequest"));
            sendRequest(rur);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    //for GUI
    public static void signOut(){
        try{
            SignOutRequest sor = new SignOutRequest(loggedUser.getUsername(),User.changeToPrivate(loggedUser));
            mapper.registerSubtypes(new NamedType(SignOutRequest.class,"signOutRequest"));
            sendRequest(sor);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }



    //calls GUI
    static void userListHandle(SearchedUsersListResponse sulr) {
        List<Chat> chats = sulr.getChats();
        ClientReceiverGUI.searchResult(chats);
    }

    //calls GUI
    public static void openChat(Chat newChat){
        if(sender==1){
            ClientReceiverGUI.openPvChat(newChat);
        }else if(sender==2){
            ClientReceiverGUI.openChatInSearch(newChat);
        }
    }



    private static void sendRequest(ServerRequest sr) throws Exception {
        String request = mapper.writeValueAsString(sr);
        dos.writeUTF(request);
        dos.flush();
        if (loggedUser == null){
            String response = dis.readUTF();
            mapper.registerSubtypes(new NamedType(PvChat.class,"PvChat"));
            mapper.registerSubtypes(new NamedType(Channel.class,"channel"));
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

    public static void chatModifyHandle(ChatModifyResponse cmr){
        if(cmr.getModification().equals("add")){
            switch(cmr.getType()){
                case "pv_chat":
                    if(cmr.getRole().equals("sender")){
                        addChat(cmr.getChat());
                        openChat(cmr.getChat());
                    }else{
                        addChat(cmr.getChat());
                    }

                    break;
                case "channel":
                    addChat(cmr.getChat());
            }
        }else if(cmr.getModification().equals("remove")){
            switch(cmr.getType()){
                case "pv_chat":
                    break;
                case "channel":
                    removeChat(cmr.getChat());
                    break;
            }

        }else if(cmr.getModification().equals("open")){
            openChat(cmr.getChat());
        }

    }

    private static void removeChat(Chat removingChat) {
        for(Chat chat : loggedUser.getChats()){
            if(removingChat.getId().equals(chat.getId())){
                loggedUser.getChats().remove(chat);
                break;
            }
        }
    }


    private static void addChat(Chat newChat) {
        boolean flag = false;
        for(Chat chat : loggedUser.getChats()){
            if(chat.getId().equals(newChat.getId())){
                flag = true;
                break;
            }
        }
        if(!flag){
            loggedUser.getChats().add(newChat);
        }
    }
}
