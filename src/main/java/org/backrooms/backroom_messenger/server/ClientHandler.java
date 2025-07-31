package org.backrooms.backroom_messenger.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.backrooms.backroom_messenger.entity.*;
import org.backrooms.backroom_messenger.response_and_requests.serverRequest.*;
import org.backrooms.backroom_messenger.response_and_requests.serverResopnse.*;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.backrooms.backroom_messenger.StaticMethods.hashPassword;
import static org.backrooms.backroom_messenger.server.DataBaseManager.*;

public class ClientHandler implements Runnable {
    private User activeUser;
    Socket socket;
    DataInputStream in;
    DataOutputStream out;
    private final ObjectMapper mapper = new ObjectMapper();

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        mapperRegister();
    }

    private void mapperRegister() {
        mapper.registerSubtypes(new NamedType(MultiUserChat.class,"MultiUserChat"));
        mapper.registerSubtypes(new NamedType(ChatModifyResponse.class, "chatModifyResponse"));
        mapper.registerSubtypes(new NamedType(PvChat.class, "PvChat"));
        mapper.registerSubtypes(new NamedType(ReceivedMessage.class,"receivedMessage"));
    }

    @Override
    public void run() {
        ObjectMapper mapper = new ObjectMapper();
        if(!Thread.currentThread().isInterrupted()){
            while(true){
                try {
                    String request = in.readUTF();
                    ServerRequest sr = mapper.readValue(request, ServerRequest.class);
                    Thread thread = new Thread(() -> {
                        try {
                            CheckRequest(sr);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
                    thread.start();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }

    }

    private void CheckRequest(ServerRequest sr) throws Exception {
        if(sr instanceof LoginRequest lr){
            loginHandle(lr);
        }else if(sr instanceof SignupRequest sur){
            signupHandle(sur);
        }else if(sr instanceof SearchRequest search){
            searchUser(search);
        }else if(sr instanceof NewChatRequest ncr){
            checkChat(ncr);
        }else if(sr instanceof SendMessageRequest smr){
            sendMessage(smr);
        }else if(sr instanceof NewMultiChatRequest nmcr){
            createMultiChat(nmcr);
        }else if(sr instanceof SubRequest sur){
            subCheck(sur);
        }else if(sr instanceof OpenMultiChatRequest omcr){
            openMultiUserChat(omcr);
        }else if(sr instanceof ChangePropertyRequest cpr){
            changeProperty(cpr);
        }else if(sr instanceof ChangeRoleRequest crr){
             changeRole(crr);
        }else if(sr instanceof RemoveUserRequest rur){
            removeUser(rur);
        }else if(sr instanceof SignOutRequest){
            signOut();
        }else if(sr instanceof ChatReadRequest crr){
            checkRead(crr);
        }else if(sr instanceof FindMultiChatForLink fmcfl){
            findMultiChat(fmcfl);
        }else if(sr instanceof ChangeUserPropertyRequest cupr){
            changeUserProperty(cupr);
        }
    }

    private void changeUserProperty(ChangeUserPropertyRequest cupr) throws SQLException {
        String name = cupr.getName();
        String password = cupr.getPassword();
        String bio = cupr.getBio();
        DataBaseManager.changeUserProperty(activeUser.getUsername(),name,password,bio);

    }

    private void signOut() throws SQLException {
        setAvailability(false);
        Server.terminateSession(this);
        Thread.currentThread().interrupt();
    }

    private void findMultiChat(FindMultiChatForLink fmcfl) throws JsonProcessingException {
        UUID uuid = fmcfl.getMucId();
        MultiUserChat muc = null;
        try{
            muc = DataBaseManager.getMultiChatDetails(uuid);
        }catch (Exception ignored){

        }
        String message = "founded##muc##" + mapper.writeValueAsString(muc);
        ChatModifyResponse cmr = new ChatModifyResponse(message);
        sendResponse(cmr);
    }

    private void checkRead(ChatReadRequest crr) throws SQLException {
        UUID messageId = crr.getMessageId();
        UUID chatId = crr.getChatId();
        String type = crr.getChatType();
        DataBaseManager.checkAsRead(chatId,type,messageId);
        if(type.equals("pv_chat")){
            String sender = crr.getMsgSender();
            String message = chatId.toString() + "##" + messageId.toString() + "##" + type;
            UserReadResponse urr = new UserReadResponse(message);
            List<String> usernames = new ArrayList<>();
            usernames.add(sender);
            Server.sendResponse(urr, usernames);
        }
    }

    private void removeUser(RemoveUserRequest rur) throws SQLException {
        String userName = rur.getUserName();
        UUID chatId = rur.getChatId();
        DataBaseManager.leaveChat(chatId,userName);
        //todo : notify the removed user
    }

    private void changeRole(ChangeRoleRequest crr) throws SQLException {
        String role = crr.getRole();
        if(role.equals("admin")){
            role = "normal";
        }else if(role.equals("normal")){
            role = "admin";
        }
        String user = crr.getUserName();
        UUID uuid = crr.getId();
        DataBaseManager.changeRole(uuid,user,role);
    }

    private void changeProperty(ChangePropertyRequest cpr) throws SQLException {
        String property = cpr.getProperty();
        UUID uuid = cpr.getId();
        String newProperty = cpr.getNewProperty();
        if(property.equals("name")){
            DataBaseManager.changeName(uuid,newProperty);
        }else if(property.equals("description")){
            DataBaseManager.changeDescription(uuid,newProperty);
        }
    }

    private void openMultiUserChat(OpenMultiChatRequest omcr) throws SQLException, JsonProcessingException {
        DataBaseManager.readAllMessages(omcr.getId(),activeUser.getUsername(),"muc");
        MultiUserChat muc = DataBaseManager.getMultiUserChat(omcr.getId());
        String role = DataBaseManager.getRole(omcr.getId(),activeUser.getUsername());
        if(role.equals("creator") || role.equals("admin")){
            DataBaseManager.returnUsers(muc);
        }else{
            muc.getUsers().add(User.changeToPrivate(activeUser));
            muc.getRoles().add(role);
        }
        String message = "open##muc##" + mapper.writeValueAsString(muc) +"##"+ role ;
        ChatModifyResponse cmr = new ChatModifyResponse(message);
        sendResponse(cmr);
    }

    private void subCheck(SubRequest sur) throws Exception {
        MultiUserChat muc = sur.getMultiUserChat();
        boolean flag = false;
        for(Chat chat : activeUser.getChats()){
            if(muc.getId().equals(chat.getId())){
                activeUser.getChats().remove(chat);
                flag = true;
                break;
            }
        }
        if(!flag){
            joinMultiUserChat(muc);
        }else{
            leaveMultiChat(muc);
        }
    }

    private void leaveMultiChat(MultiUserChat muc) throws Exception {
        DataBaseManager.leaveChat(muc.getId(),activeUser.getUsername());
        String message = "remove##muc##" + mapper.writeValueAsString(muc);
        ChatModifyResponse cmr = new ChatModifyResponse(message);

        String json = mapper.writeValueAsString(cmr);
        out.writeUTF(json);
        out.flush();
    }

    private void joinMultiUserChat(MultiUserChat muc) throws Exception {
        DataBaseManager.joinMultiChat(muc,activeUser);
        activeUser.getChats().add(muc);
        String message = null;
        if(muc.isChannel()){
            muc.getUsers().add(User.changeToPrivate(activeUser));
            muc.getRoles().add("normal");
            message = "add##muc##" + mapper.writeValueAsString(muc)+"##normal";
        }else{
            muc.getUsers().add(User.changeToPrivate(activeUser));
            muc.getRoles().add("admin");
            message = "add##muc##" + mapper.writeValueAsString(muc)+"##admin";
        }

        ChatModifyResponse cmr = new ChatModifyResponse(message);
        String json = mapper.writeValueAsString(cmr);
        out.writeUTF(json);
        out.flush();
    }

    private void createMultiChat(NewMultiChatRequest ncr) throws SQLException {
        MultiUserChat muc = ncr.getMultiUserChat();
        DataBaseManager.addNewMultiChat(muc);
        activeUser.getChats().add(muc);
        muc.getUsers().add(User.changeToPrivate(activeUser));
        muc.getRoles().add("creator");
    }

    private void sendMessage(SendMessageRequest smr) throws SQLException {
        Message message = smr.getSendedMessage();
        addMessageToChat(message,message.getChatType());
        Server.broadcast(message);
    }

    private void loginHandle(LoginRequest loginRequest) throws IOException, SQLException {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        User loggedUser = null;
        try{
            loggedUser = DataBaseManager.getUserFromDataBase(username);
            loggedUser.getChats().addAll(DataBaseManager.getUserChats(loggedUser.getUsername()));
            String hashedPassword = hashPassword(password, loggedUser.getSalt());
            if(hashedPassword.equals(loggedUser.getPassword())){
                activeUser = loggedUser;
                setAvailability(true);
            }else{
                throw new Exception("Wrong password");
            }
        }catch (Exception e){
            loggedUser = null;
            notify(e);
        }




        AvailableUserResponse aur = new AvailableUserResponse(mapper.writeValueAsString(activeUser));
        String response = mapper.writeValueAsString(aur);
        out.writeUTF(response);
        out.flush();

    }

    private void signupHandle(SignupRequest signupRequest) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SQLException {

        String username = signupRequest.getUsername();
        String password = signupRequest.getPassword();
        byte[] salt = signupRequest.getSalt();

        String hashedPassword = hashPassword(password,salt);

        User signedUser = null;
        try {
            signedUser = new User(username,hashedPassword,salt);
            DataBaseManager.addUserToDataBase(signedUser);
            activeUser = signedUser;
            setAvailability(true);
        } catch (Exception e) {
            notify(e);
        }

        AvailableUserResponse aur = new AvailableUserResponse(mapper.writeValueAsString(activeUser));
        String response = mapper.writeValueAsString(aur);
        out.writeUTF(response);
        out.flush();
    }

    private void setAvailability(boolean online) throws SQLException {
        Date lastSeen = new Date();
        DataBaseManager.setLastSeen(activeUser.getUsername(),lastSeen,online);
        String message = null;
        if(online){
            message = activeUser.getUsername() + "##" + lastSeen.getTime() + "##online";
        }else{
            message = activeUser.getUsername() + "##" + lastSeen.getTime() + "##offline";
        }
        UserLogResponse ulr = new UserLogResponse(message);
        List<String> usernames = new ArrayList<>();
        Thread thread = new Thread(() -> {
            for(Chat chat : activeUser.getChats()){
                if(chat instanceof PvChat pv){
                    usernames.add(pv.getUser(activeUser).getUsername());
                }
            }
            Server.sendResponse(ulr,usernames);
        });
        thread.start();
    }

    private void searchUser(SearchRequest searchRequest) throws Exception {
        String searched = searchRequest.getSearchTerm();
        //todo to be updated for group
        List<Chat> chats = new ArrayList<>();
        List<PrivateUser> searchedUsers = DataBaseManager.searchUser(searched);
        List<MultiUserChat> searchedMultiChats = DataBaseManager.searchMultiUserChat(searched);
        for(PrivateUser pu : searchedUsers){
            if(pu.getUsername().equals(activeUser.getUsername())){
                continue;
            }
            PvChat pv = new PvChat(null,pu,searchRequest.getSender());
            chats.add(pv);
        }
        chats.addAll(searchedMultiChats);

        String responseMessage = mapper
                .writerFor(new TypeReference<List<Chat>>() {})
                .writeValueAsString(chats);


        SearchedUsersListResponse sulr = new SearchedUsersListResponse(responseMessage);
        String response = mapper.writeValueAsString(sulr);
        out.writeUTF(response);
        out.flush();
    }

    private void notify(Exception e) {
        System.out.println(e);
    }

    public void receiveMessage(Message message) throws SQLException {
        try {
            String messageJson = mapper.writeValueAsString(message);
            ReceivedMessage rm = new ReceivedMessage(messageJson);

            String sending = mapper.writeValueAsString(rm);
            out.writeUTF(sending);
            out.flush();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void checkChat(NewChatRequest ncr) throws Exception {
        String user1 =  ncr.getSender().getUsername();
        String user2 = ncr.getUser().getUsername();
        if (user2.compareTo(user1) < 0){
            String temp = user2;
            user2 = user1;
            user1 = temp;
        }

        UUID chatId = searchForPV(user1,user2);

        if(chatId == null) {
            chatId = createChat(user1, user2);
        }

        PrivateUser us1 = getPrivateUser(user1);
        PrivateUser us2 = getPrivateUser(user2);

        PvChat pv = new PvChat(chatId,us1,us2);

        openChat(pv);
    }

    private void openChat(PvChat pv) throws IOException, SQLException {
        DataBaseManager.readAllMessages(pv.getId(),activeUser.getUsername(),"pv_chat");
        pv.getMessage().addAll(DataBaseManager.returnMessages(pv));
        String message = "add##pv_chat##" + mapper.writeValueAsString(pv) + "##sender";
        ChatModifyResponse cmr = new ChatModifyResponse(message);
        String response = mapper.writeValueAsString(cmr);
        out.writeUTF(response);
        out.flush();

        String message2 = "add##pv_chat##" + mapper.writeValueAsString(pv) + "##receiver";
        ChatModifyResponse cmr2 = new ChatModifyResponse(message2);
        List<String> usernames = new ArrayList<>();
        usernames.add(pv.getUser(activeUser).getUsername());
        Server.sendResponse(cmr2,usernames);
    }

    private UUID createChat(String user1, String user2) throws SQLException {
        UUID chatId = UUID.randomUUID();
        addPvChat(chatId, user1,user2);
        return chatId;
    }

    public User getActiveUser(){
        return activeUser;
    }

    public void sendResponse(ServerResponse response) {
        String responseString = null;
        try {
            responseString = mapper.writeValueAsString(response);
            out.writeUTF(responseString);
            out.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
