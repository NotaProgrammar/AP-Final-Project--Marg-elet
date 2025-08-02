package org.backrooms.backroom_messenger.client;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import javafx.scene.image.Image;
import org.backrooms.backroom_messenger.ClientReceiverGUI;
import org.backrooms.backroom_messenger.StaticMethods;
import org.backrooms.backroom_messenger.entity.*;
import org.backrooms.backroom_messenger.response_and_requests.serverRequest.*;
import org.backrooms.backroom_messenger.response_and_requests.serverResopnse.*;

import java.io.*;
import java.net.Socket;
import java.util.Base64;
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
    static BufferedReader reader;
    static BufferedWriter writer;
    static int sender;
    static PrivateUser privateLoggedUser;

    public static void initializeClient() throws IOException {
        socket = new Socket("localhost",8888);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
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
        mapper.registerSubtypes(new NamedType(ChangeUserPropertyRequest.class,"changeUserPropertyRequest"));
        mapper.registerSubtypes(new NamedType(SetImageRequest.class,"setImageRequest"));
        mapper.registerSubtypes(new NamedType(DownloadFileRequest.class,"downloadFileRequest"));

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
            OpenMultiChatRequest ocr = new OpenMultiChatRequest(muc.getId().toString(),privateLoggedUser);
            sendRequest(ocr);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //for GUI
    public static Message sendMessage(String messageString, Chat chat,boolean file){
        Message message = new Message(UUID.randomUUID(),
                loggedUser.getUsername(), chat.getId(),
                messageString,new Date(),chat.getType(),
                false,file);

        try{
            foundedChat = null;
            chatFound = false;
            UUID uuid = UUID.fromString(messageString);
            FindMultiChatForLink fmcfl = new FindMultiChatForLink(uuid.toString(),privateLoggedUser);
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
            SendMessageRequest smr = new SendMessageRequest(messageJson,privateLoggedUser);
            sendRequest(smr);
        } catch (Exception e) {
            message = null;
            System.out.println(e);
        }

        return message;
    }

    //for GUI
    public static User login(String username, String password) throws IOException {
        if(writer == null || reader ==null || socket == null){
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
        if(writer == null || reader ==null || socket == null){
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
        SearchRequest sr = new SearchRequest(searchedString,privateLoggedUser);
        sendRequest(sr);
    }

    //for GUI
    public static MultiUserChat createMultiUserChat(String name,String description,boolean publicOrPrivate,boolean isChannel){
        MultiUserChat newMuc = new MultiUserChat(UUID.randomUUID(),name,description,publicOrPrivate,loggedUser.getUsername(),isChannel);
        try {

            String message = mapper.writeValueAsString(newMuc);
            NewMultiChatRequest ncr = new NewMultiChatRequest(message,privateLoggedUser);
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
            SubRequest sr = new SubRequest(message,privateLoggedUser);

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
            ChangePropertyRequest cpr = new ChangePropertyRequest(message,privateLoggedUser);
            sendRequest(cpr);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    //for GUI
    public static void changeDescription(Chat chat,String newDescription){
        try{
            String message = chat.getId().toString() + "##description##" + newDescription;
            ChangePropertyRequest cpr = new ChangePropertyRequest(message,privateLoggedUser);
            sendRequest(cpr);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    //for GUI
    public static void changeUserRole(Chat chat,PrivateUser user){
        String role = null;
        if(chat instanceof MultiUserChat channel){
            role = channel.getRole(user);
        }
        try{
            String message = chat.getId().toString() + "##" + user.getUsername() + "##" + role ;
            ChangeRoleRequest crr = new ChangeRoleRequest(message,privateLoggedUser);
            sendRequest(crr);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    //for GUI
    public static void changeUserProperty(String property,String newProperty) throws Exception {
        String message = null;
        switch(property){
            case "name" :
                message = newProperty + "##" + loggedUser.getPassword() + "##" + loggedUser.getBio();
                break;
            case "password" :
                String hashedPassword = StaticMethods.hashPassword(newProperty,loggedUser.getSalt());
                loggedUser.setPassword(hashedPassword);
                message = loggedUser.getName() + "##" + hashedPassword + "##" + loggedUser.getBio();
                break;
            case "bio":
                message = loggedUser.getName() + "##" + loggedUser.getPassword() + "##" + newProperty;
                loggedUser.setBio(newProperty);
                break;
        }
        ChangeUserPropertyRequest cupr = new ChangeUserPropertyRequest(message,privateLoggedUser);
        sendRequest(cupr);
    }

    //for GUI
    public static void setImageForUsers(byte[] image){
        try {
            String base64 = null;
            if(image != null){
                base64 = Base64.getEncoder().encodeToString(image);
            }
            loggedUser.setImageBase64(base64);
            String message = base64+ "###user";
            SetImageRequest sir = new SetImageRequest( message,privateLoggedUser );
            sendRequest(sir);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void setImageForMuc(byte[] image,UUID id){
        try {
            String base64 = null;
            if(image != null){
                base64 = Base64.getEncoder().encodeToString(image);
            }
            loggedUser.setImageBase64(base64);
            String message = base64+ "###muc###" + id.toString();
            SetImageRequest sir = new SetImageRequest( message,privateLoggedUser );
            sendRequest(sir);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //for GUI
    public static void removeUser(PrivateUser user,MultiUserChat chat){
        try{

            String message = user.getUsername() + "##" + chat.getId();
            RemoveUserRequest rur = new RemoveUserRequest(message,privateLoggedUser);
            sendRequest(rur);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    //for GUI
    public static void signOut(){
        try{
            SignOutRequest sor = new SignOutRequest(loggedUser.getUsername(),privateLoggedUser);
            sendRequest(sor);
            loggedUser = null;
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
        String endMarker = "###END###";

        int chunkSize = 2048;

        for (int i = 0; i < request.length(); i += chunkSize) {
            int end = Math.min(request.length(), i + chunkSize);
            writer.write(request, i, end - i);
            writer.flush();
        }

        writer.write(endMarker);
        writer.flush();

        if (loggedUser == null){
            StringBuilder sb = new StringBuilder();

            char[] buffer = new char[1024];
            int charsRead;

            while ((charsRead = reader.read(buffer)) != -1) {
                String chunk = new String(buffer, 0, charsRead);
                sb.append(chunk);
                if (sb.toString().contains(endMarker)) {
                    break;
                }
            }

            String json = sb.toString().replace(endMarker, "");
            AvailableUserResponse aur = mapper.readValue(json,AvailableUserResponse.class);
            signupLoginCheck(aur);
        }
    }

    private static void startChat(PrivateUser selectedUser) {
        if(selectedUser.getUsername().equals(loggedUser.getUsername())){
            System.out.println("that's you motherfucker");
        }else{
            try {
                NewChatRequest ncr = new NewChatRequest(mapper.writeValueAsString(selectedUser),privateLoggedUser);
                sendRequest(ncr);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    private static void signupLoginCheck(AvailableUserResponse aur) {
        if(aur.isUserFound()){
            loggedUser = aur.getUser();
            privateLoggedUser = User.changeToPrivate(loggedUser);
            clientReceiverStarter();
        }
    }

    private static void clientReceiverStarter () {
        ClientReceiver cr = new ClientReceiver(reader);
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
            ChatReadRequest crr = new ChatReadRequest(msg,privateLoggedUser);
            sendRequest(crr);
        }catch(Exception e){
            System.out.println(e);
        }

    }

    public static User getUser(){
        return loggedUser;
    }

    public static Message sendFile(String name, byte[] bytes, Chat chat, boolean file) {
        try{
            Message message = new Message(UUID.randomUUID(),
                    loggedUser.getUsername(), chat.getId(),
                    name,new Date(),chat.getType(),
                    false,file);
            String base64 = Base64.getEncoder().encodeToString(bytes);
            message.setFileBase64(base64);
            String messageJson = mapper.writeValueAsString(message);
            SendMessageRequest smr = new SendMessageRequest(messageJson,privateLoggedUser);
            sendRequest(smr);
            return message;
        }catch(Exception e){
            System.out.println(e);
        }
        return null;
    }

    public static void downloadFile(Message message,String savePath){
        try{
            String messageString = message.getId().toString() + "##" + message.getChat() + "##" + message.getChatType() + "##" + savePath + "##" + message.getMessage();
            DownloadFileRequest dfr = new DownloadFileRequest(messageString,privateLoggedUser);
            sendRequest(dfr);
        }catch(Exception e){
            System.out.println(e);
        }
    }


}
