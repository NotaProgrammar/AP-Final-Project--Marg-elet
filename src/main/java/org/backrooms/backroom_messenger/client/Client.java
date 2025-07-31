package org.backrooms.backroom_messenger.client;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.backrooms.backroom_messenger.ClientReceiverGUI;
import org.backrooms.backroom_messenger.entity.*;
import org.backrooms.backroom_messenger.response_and_requests.serverRequest.*;
import org.backrooms.backroom_messenger.response_and_requests.serverResopnse.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.backrooms.backroom_messenger.StaticMethods.generateSalt;


public class Client  {


    static Chat foundedChat = null;
    static boolean chatFound = false;
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
        mapperSetup();
    }

    public static void mapperSetup(){
        mapper.registerSubtypes(new NamedType(OpenMultiChatRequest.class,"openMultiChatRequest"));
        mapper.registerSubtypes(new NamedType(FindMultiChatForLink.class,"findMultiChatForLink"));
        mapper.registerSubtypes(new NamedType(PvChat.class, "PvChat"));
        mapper.registerSubtypes(new NamedType(MultiUserChat.class,"MultiUserChat"));
        mapper.registerSubtypes(new NamedType(LoginRequest.class, "loginRequest"));
        mapper.registerSubtypes(new NamedType(SignupRequest.class, "signupRequest"));
        mapper.registerSubtypes(new NamedType(SearchRequest.class, "searchRequest"));
        mapper.registerSubtypes(new NamedType(SubRequest.class,"subRequest"));
        mapper.registerSubtypes(new NamedType(ChangePropertyRequest.class,"changePropertyRequest"));
        mapper.registerSubtypes(new NamedType(ChangeRoleRequest.class,"changeRoleRequest"));
        mapper.registerSubtypes(new NamedType(RemoveUserRequest.class,"removeUserRequest"));
        mapper.registerSubtypes(new NamedType(SignOutRequest.class,"signOutRequest"));
        mapper.registerSubtypes(new NamedType(NewChatRequest.class, "newChatRequest"));
        mapper.registerSubtypes(new NamedType(ChatReadRequest.class, "chatReadRequest"));
    }


    //for GUI
    public static void openChat(Chat chat,int sender){
        Client.sender = sender;
        if (chat instanceof PvChat pv) {
            if (loggedUser.getUsername().equals(pv.getUser1().getUsername())) {
                startChat(pv.getUser2());
            } else if (loggedUser.getUsername().equals(pv.getUser2().getUsername())) {
                startChat(pv.getUser1());
            }
        }else if(chat instanceof MultiUserChat muc){
            openMultiUserChat(muc);
        }
    }

    private static void openMultiUserChat(MultiUserChat muc) {
        try {
            OpenMultiChatRequest ocr = new OpenMultiChatRequest(muc.getId().toString(),User.changeToPrivate(loggedUser));
            sendRequest(ocr);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //for GUI
    public static Message sendMessage(String messageString, Chat chat){
        Message message = new Message(UUID.randomUUID(),
                loggedUser.getUsername(), chat.getId(),
                messageString,new Date(),chat.getType(),
                false);

        try{
            foundedChat = null;
            chatFound = false;
            UUID uuid = UUID.fromString(messageString);
            FindMultiChatForLink fmcfl = new FindMultiChatForLink(uuid.toString(),User.changeToPrivate(loggedUser));
            sendRequest(fmcfl);
            while(!chatFound){
                Thread.sleep(10);
            }
            if(foundedChat != null){
                message.setLinkToMultiUserChat((MultiUserChat) foundedChat);
            }

        }catch(Exception ignored){

        }
        try {
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
        sendRequest(sr);
        return loggedUser;
    }

    //for GUI
    public static void search(String searchedString) throws Exception {
        SearchRequest sr = new SearchRequest(searchedString,User.changeToPrivate(loggedUser));
        sendRequest(sr);
    }

    //for GUI
    public static MultiUserChat createMultiUserChat(String name,String description,boolean publicOrPrivate,boolean isChannel){
        MultiUserChat newMuc = new MultiUserChat(UUID.randomUUID(),name,description,publicOrPrivate,loggedUser.getUsername(),isChannel);
        try {

            String message = mapper.writeValueAsString(newMuc);
            NewMultiChatRequest ncr = new NewMultiChatRequest(message,User.changeToPrivate(loggedUser));
            sendRequest(ncr);
        } catch (Exception e) {
            System.out.println(e);
        }
        return newMuc;
    }

    //for GUI
    public static void Subscribe(MultiUserChat channel){
        try {
            String message = mapper.writeValueAsString(channel);
            SubRequest sr = new SubRequest(message,User.changeToPrivate(loggedUser));

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
            String message = chat.getId().toString() + "##name##" + newName ;
            ChangePropertyRequest cpr = new ChangePropertyRequest(message,User.changeToPrivate(loggedUser));
            sendRequest(cpr);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    //for GUI
    public static void changeDescription(Chat chat,String newDescription){
        try{
            String message = chat.getId().toString() + "##description##" + newDescription;
            ChangePropertyRequest cpr = new ChangePropertyRequest(message,User.changeToPrivate(loggedUser));
            sendRequest(cpr);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    //for GUI
    public static void changeUserRole(Chat chat,PrivateUser user){
        String role = null;
        //todo change
        if(chat instanceof MultiUserChat channel){
            role = channel.getRole(user);
        }
        try{
            String message = chat.getId().toString() + "##" + user.getUsername() + "##" + role ;
            ChangeRoleRequest crr = new ChangeRoleRequest(message,User.changeToPrivate(loggedUser));
            sendRequest(crr);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    //for GUI
    public static void removeUser(PrivateUser user,MultiUserChat chat){
        try{

            String message = user.getUsername() + chat.getId();
            RemoveUserRequest rur = new RemoveUserRequest(message,User.changeToPrivate(loggedUser));
            sendRequest(rur);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    //for GUI
    public static void signOut(){
        try{
            SignOutRequest sor = new SignOutRequest(loggedUser.getUsername(),User.changeToPrivate(loggedUser));

            sendRequest(sor);
            System.exit(0);
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
        } else if (sender >= 3) {
            ClientReceiverGUI.giveChatToChatPage((MultiUserChat) newChat,sender);
        }
    }



    private static void sendRequest(ServerRequest sr) throws Exception {
        String request = mapper.writeValueAsString(sr);
        dos.writeUTF(request);
        dos.flush();
        if (loggedUser == null){
            String response = dis.readUTF();
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
                case "muc":
                    addChat(cmr.getChat());
            }
        }else if(cmr.getModification().equals("remove")){
            switch(cmr.getType()){
                case "pv_chat":
                    break;
                case "muc":
                    removeChat(cmr.getChat());
                    break;
            }

        }else if(cmr.getModification().equals("open")){
            openChat(cmr.getChat());
        } else if (cmr.getModification().equals("founded")) {
            fillFoundedChat(cmr.getChat());
        }

    }

    private static void fillFoundedChat(Chat chat) {
        foundedChat = chat;
        chatFound = true;
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

    public static void setLastSeen(UserLogResponse ulr) {
        boolean online = ulr.isOnline();
        String username = ulr.getUsername();
        for(Chat chat : loggedUser.getChats()){
            if(chat instanceof PvChat pv && pv.getUser(loggedUser).getUsername().equals(username)){
                PrivateUser loggedOutUser = pv.getUser(loggedUser);
                loggedOutUser.setLastSeen(ulr.getLastSeen());
                loggedOutUser.setOnline(online);
                break;
            }
        }
    }

    public static void readMessage(Message message) {
        try{
            UUID messageId = message.getId();
            UUID chatId = message.getChat();
            String type = message.getChatType();
            String sender = message.getSender();
            String msg = messageId + "##" + chatId + "##" + type + "##" + sender;
            ChatReadRequest crr = new ChatReadRequest(msg,User.changeToPrivate(loggedUser));
            sendRequest(crr);
        }catch(Exception e){
            System.out.println(e);
        }

    }

    public static User getUser(){
        return loggedUser;
    }

}
