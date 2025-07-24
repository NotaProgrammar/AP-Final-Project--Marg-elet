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
import java.util.List;
import java.util.UUID;

import static org.backrooms.backroom_messenger.StaticMethods.hashPassword;
import static org.backrooms.backroom_messenger.server.DataBaseManager.*;

public class ClientHandler implements Runnable {
    private User activeUser;
    Socket socket;
    DataInputStream in;
    DataOutputStream out;
    ObjectMapper mapper = new ObjectMapper();

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        ObjectMapper mapper = new ObjectMapper();
        while(true){
            try {
                String request = in.readUTF();
                ServerRequest sr = mapper.readValue(request, ServerRequest.class);
                CheckRequest(sr);
            } catch (Exception e) {
                System.out.println(e);
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
        }else if(sr instanceof NewChannelRequest ncr){
            createChannel(ncr);
        }else if(sr instanceof SubRequest sur){
            subCheck(sur);
        }
    }

    private void subCheck(SubRequest sur) throws Exception {
        Channel channel = sur.getChannel();
        boolean flag = false;
        for(Chat chat : activeUser.getChats()){
            if(channel.getId().equals(chat.getId())){
                activeUser.getChats().remove(chat);
                flag = true;
            }
        }
        if(!flag){
            joinChannel(channel);
        }else{
            leaveChannel(channel);
        }
    }

    private void leaveChannel(Channel channel) throws Exception {
        DataBaseManager.leaveChannel(channel,activeUser);
        String message = "remove##channel##" + mapper.writeValueAsString(channel);
        ChatModifyResponse cmr = new ChatModifyResponse(message);
        mapper.registerSubtypes(new NamedType(ChatModifyResponse.class, "chatModifyResponse"));
        String json = mapper.writeValueAsString(cmr);
        out.writeUTF(json);
        out.flush();
    }

    private void joinChannel(Channel channel) throws Exception {
        DataBaseManager.joinChannel(channel,activeUser);
        activeUser.getChats().add(channel);
        channel.getUsers().put(User.changeToPrivate(activeUser),"normal");
        String message = "add##channel##" + mapper.writeValueAsString(channel)+"##normal";
        ChatModifyResponse cmr = new ChatModifyResponse(message);
        mapper.registerSubtypes(new NamedType(ChatModifyResponse.class, "chatModifyResponse"));
        String json = mapper.writeValueAsString(cmr);
        out.writeUTF(json);
        out.flush();
    }

    private void createChannel(NewChannelRequest ncr) throws SQLException {
        Channel channel = ncr.getChannel();
        DataBaseManager.addNewChannel(channel);
        activeUser.getChats().add(channel);
    }

    private void sendMessage(SendMessageRequest smr) throws SQLException {
        Message message = smr.getSendedMessage();
        addMessage(message);
        Server.broadcast(message);
    }

    private void loginHandle(LoginRequest loginRequest) throws IOException {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        User loggedUser = null;
        try{
            loggedUser = DataBaseManager.getUserFromDataBase(username);
            loggedUser.getChats().addAll(DataBaseManager.getUserChats(loggedUser.getUsername()));
            String hashedPassword = hashPassword(password, loggedUser.getSalt());
            if(hashedPassword.equals(loggedUser.getPassword())){
                activeUser = loggedUser;
            }else{
                throw new Exception("Wrong password");
            }
        }catch (Exception e){
            notify(e);
        }

        mapper.registerSubtypes(new NamedType(PvChat.class, "PvChat"));
        AvailableUserResponse aur = new AvailableUserResponse(mapper.writeValueAsString(activeUser));
        String response = mapper.writeValueAsString(aur);
        out.writeUTF(response);
        out.flush();
    }

    private  void signupHandle(SignupRequest signupRequest) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {

        String username = signupRequest.getUsername();
        String password = signupRequest.getPassword();
        byte[] salt = signupRequest.getSalt();

        String hashedPassword = hashPassword(password,salt);

        User signedUser = null;
        try {
            signedUser = new User(username,hashedPassword,salt);
            DataBaseManager.addUserToDataBase(signedUser);
        } catch (SQLException e) {
            signedUser = null;
            notify(e);
        }

        activeUser = signedUser;
        AvailableUserResponse aur = new AvailableUserResponse(mapper.writeValueAsString(activeUser));
        String response = mapper.writeValueAsString(aur);
        out.writeUTF(response);
        out.flush();

    }

    private void searchUser(SearchRequest searchRequest) throws Exception {
        String searched = searchRequest.getSearchTerm();
        //todo to be updated
        List<Chat> chats = new ArrayList<>();
        List<PrivateUser> searchedUsers = DataBaseManager.searchUser(searched);
        for(PrivateUser pu : searchedUsers){
            if(pu.getUsername().equals(activeUser.getUsername())){
                continue;
            }
            PvChat pv = new PvChat(null,pu,searchRequest.getSender());
            chats.add(pv);
        }

        mapper.registerSubtypes(new NamedType(PvChat.class,"PvChat"));
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
            mapper.registerSubtypes(new NamedType(ReceivedMessage.class,"receivedMessage"));
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
        pv.getMessage().addAll(DataBaseManager.returnMessages(pv));
        mapper.registerSubtypes(new NamedType(PvChat.class,"PvChat"));
        String message = "add##pv_chat##" + mapper.writeValueAsString(pv) + "##sender";
        ChatModifyResponse cmr = new ChatModifyResponse(message);
        mapper.registerSubtypes(new NamedType(ChatModifyResponse.class, "chatModifyResponse"));
        String response = mapper.writeValueAsString(cmr);
        out.writeUTF(response);
        out.flush();

        String message2 = "add##pv_chat##" + mapper.writeValueAsString(pv) + "##receiver";
        ChatModifyResponse cmr2 = new ChatModifyResponse(message2);
        List<String> usernames = new ArrayList<>();
        usernames.add(pv.getUserName(activeUser));
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
