package org.backrooms.backroom_messenger.server;

import org.backrooms.backroom_messenger.entity.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;


public class DataBaseManager {
    private static  String JDBC_URL = null;
    private static  String USERNAME = null;
    private static  String PASSWORD = null;

    //todo adding locks


    public static Connection connectToDataBase() throws SQLException {
        if(JDBC_URL == null || USERNAME == null || PASSWORD == null){
            String filePath = "src\\main\\resources\\org\\backrooms\\backroom_messenger\\database_details.txt";
            File file = new File(filePath);
            try {
                Scanner sc = new Scanner(file);
                JDBC_URL = sc.nextLine();
                USERNAME = sc.nextLine();
                PASSWORD = sc.nextLine();
            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }
        return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
    }


    public static void addMessageToChat(Message message,String type) throws SQLException{
        Connection connection = connectToDataBase();
        String tableName = null;
        switch(type){
            case "pv_chat":
                tableName = "pv_chats.chat_" + message.getChat().toString().replace("-","_");
                break;
            case "channel":
            case "group":
                tableName = "multi_user_chats.messages_" + message.getChat().toString().replace("-","_");
                break;
        }
        String sql = "INSERT INTO " + tableName + " (id,sender,message,datetime,read_status) VALUES (?,?,?,?,?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setObject(1,message.getId());
        ps.setString(2,message.getSender());
        ps.setObject(3,message.getMessage());
        ps.setTimestamp(4,new Timestamp(message.getDate().getTime()));
        ps.setBoolean(5, message.isRead());
        ps.executeUpdate();
        ps.close();
        connection.close();
    }

    private static String isChatChannel(UUID chat) throws SQLException{
        //todo
        return null;
    }

    public static void addUserToDataBase(User user) throws SQLException {
        Connection con = connectToDataBase();
        String sql = "INSERT INTO public.users (username, password, salt, name) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, user.getUsername());
        ps.setString(2, user.getPassword());
        ps.setBytes(3, user.getSalt());
        ps.setString(4, user.getName());
        ps.executeUpdate();

        ps.close();
        con.close();
        createUserTable(user.getUsername());
    }

    private static void createUserTable(String user) throws SQLException {
        Connection conn = connectToDataBase();
        String tableName = "users.user_" + user;
        String sql = "CREATE TABLE " + tableName + " (id uuid PRIMARY KEY , name text, type text,  FOREIGN KEY (id) REFERENCES public.chats(id))";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.executeUpdate();
        ps.close();
        conn.close();
    }

    public static User getUserFromDataBase(String username) throws SQLException {
        Connection con = connectToDataBase();
        String sql = "SELECT * FROM public.users WHERE username = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();

        User user = null;
        if (rs.next()) {
            String password = rs.getString("password");
            byte[] salt = rs.getBytes("salt");
            user = new User(username, password, salt);
        }

        rs.close();
        ps.close();
        con.close();
        return user;
    }

    public static PrivateUser getPrivateUser(String username) throws SQLException {
        Connection con = connectToDataBase();
        String sql = "SELECT * FROM public.users WHERE username = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();

        PrivateUser user = null;
        if (rs.next()) {
            String name = rs.getString("name");
            user = new PrivateUser(username, name);
            try{
                Timestamp time = rs.getTimestamp("last_seen");
                Date lastSeen = new Date(time.getTime());
                Boolean online = rs.getBoolean("online");
                user.setLastSeen(lastSeen);
                user.setOnline(online);
            }catch(Exception e){

            }
        }

        rs.close();
        ps.close();
        con.close();
        return user;
    }

    public static List<PrivateUser> searchUser(String searched) throws SQLException {
        List<PrivateUser> users = new ArrayList<>();
        Connection con = connectToDataBase();
        String sql = "SELECT * FROM public.users WHERE username ~ ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, searched);
        ResultSet rs = ps.executeQuery();
        PrivateUser user = null;
        while(rs.next()) {
            String username = rs.getString("username");
            String name = rs.getString("name");
            user = new PrivateUser(username, name);
            users.add(user);
        }
        rs.close();
        ps.close();
        con.close();
        return users;
    }

    public static UUID searchForPV(String user1, String user2) throws SQLException {
        Connection conn = connectToDataBase();
        String sql = "SELECT * FROM pv_chats WHERE user1 = ? AND user2 = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, user1);
        ps.setString(2, user2);
        ResultSet rs = ps.executeQuery();
        UUID uuid = null;
        if(rs.next()) {
            uuid = UUID.fromString(rs.getString("id"));
        }
        rs.close();
        ps.close();
        conn.close();
        return uuid;
    }

    private static void createPvChatTable(UUID chatId) throws SQLException {
        Connection conn = connectToDataBase();
        String tableName = "pv_chats.chat_" + chatId.toString().replace("-", "_");
        String sql = "CREATE TABLE "+ tableName + " ( id uuid PRIMARY KEY , sender text, message text, datetime TIMESTAMP, read_status boolean, FOREIGN KEY (sender) REFERENCES public.users(username))";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.executeUpdate();
        ps.close();
        conn.close();
    }

    public static void addPvChat(UUID chatId, String user1, String user2) throws SQLException {
        Connection conn = connectToDataBase();
        String sql = "INSERT INTO public.pv_chats (id, user1, user2) VALUES (?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setObject(1, chatId);
        ps.setString(2, user1);
        ps.setString(3, user2);
        ps.executeUpdate();
        ps.close();
        conn.close();
        addToChatTable(chatId,"pv_chat");
        createPvChatTable(chatId);
        addChatToUsers(chatId,user1,user2,"pv_chat");
        addChatToUsers(chatId,user2,user1,"pv_chat");
    }

    private static void addToChatTable(UUID chatId, String type) throws SQLException {
        Connection conn = connectToDataBase();
        String sql = "INSERT INTO public.chats (id, type) VALUES (?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setObject(1, chatId);
        ps.setString(2, type);
        ps.executeUpdate();
        ps.close();
        conn.close();
    }

    private static void addChatToUsers(UUID chatId, String user, String name, String type) throws SQLException {
        Connection conn = connectToDataBase();
        String tableName = "users.user_" + user;
        String sql = "INSERT INTO " + tableName + " (id, name, type) VALUES (?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setObject(1, chatId);
        ps.setString(2, name);
        ps.setString(3, type);
        ps.executeUpdate();
        ps.close();
        conn.close();
    }

    public static List<Message> returnMessages(Chat chat) throws SQLException {
        List<Message> messages = new ArrayList<>();
        Connection conn = connectToDataBase();
        String tableName = null;
        if(chat instanceof PvChat){
            tableName = "pv_chats.chat_" + chat.getId().toString().replace("-", "_");
        }else if(chat instanceof MultiUserChat){
            tableName = "multi_user_chats.messages_" + chat.getId().toString().replace("-", "_");
        }

        String sql = "SELECT * FROM " + tableName;
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while(rs.next()) {
            String sender = rs.getString("sender");
            String text = rs.getString("message");
            UUID messageId = UUID.fromString(rs.getString("id"));
            boolean readStatus = rs.getBoolean("read_status");
            java.util.Date date = new java.util.Date(rs.getTimestamp("datetime").getTime());
            Message message = new Message(messageId,sender,chat.getId(),text,date,chat.getType(),readStatus);
            try{
                UUID mucId = UUID.fromString(text.replace("\n",""));
                MultiUserChat muc = getMultiChatDetails(mucId);
                message.setLinkToMultiUserChat(muc);
            }catch (Exception ignored){

            }
            messages.add(message);
        }
        rs.close();
        ps.close();
        conn.close();
        return messages;
    }

    public static List<Chat> getUserChats(String username) throws SQLException {
        Connection conn = connectToDataBase();
        String tableName = "users.user_" + username;
        String sql = "SELECT * FROM " + tableName;
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        List<Chat> chats = new ArrayList<>();
        while(rs.next()) {
            UUID id = UUID.fromString(rs.getString("id"));
            String name = rs.getString("name");
            String type = rs.getString("type");
            Chat chat = null;
            switch(type) {
                case "pv_chat":
                    String user1 = username;
                    String user2 = name;
                    if(user2.compareTo(user1)<0){
                        String temp = user2;
                        user2 = user1;
                        user1 = temp;
                    }
                    PvChat pv = new PvChat(id,getPrivateUser(user1),getPrivateUser(user2));
                    chat = pv;
                    break;
                case "muc":
                    chat = getMultiChatDetails(id);
                    break;
            }
            chats.add(chat);
        }
        rs.close();
        ps.close();
        conn.close();
        return chats;
    }

    public static MultiUserChat getMultiChatDetails(UUID id) throws SQLException {
        Connection conn = connectToDataBase();
        String query = "SELECT * FROM public.multi_user_chats WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setObject(1, id);
        ResultSet rs = ps.executeQuery();
        MultiUserChat muc = null;
        if(rs.next()) {
            String name = rs.getString("name");
            String description = rs.getString("description");
            boolean publicity = rs.getBoolean("publicity");
            String creator = rs.getString("creator");
            boolean channel = rs.getBoolean("channel");
            muc = new MultiUserChat(id,name,description,publicity,creator,channel);
        }
        rs.close();
        ps.close();
        conn.close();
        return muc;
    }

    public static void addNewMultiChat(MultiUserChat muc) throws SQLException {
        addToChatTable(muc.getId(),"muc");
        addMultiChatToMultiChatTable(muc);
        createMultiChatUserTable(muc.getId());
        createMultiChatMessageTable(muc.getId());
        addChatToUsers(muc.getId(), muc.getCreator() ,muc.getName(null),"muc");
        addUserToMultiChat(muc,"creator",muc.getCreator());
    }

    private static void addUserToMultiChat(MultiUserChat muc, String role, String username) throws SQLException {
        Connection conn = connectToDataBase();
        String tableName = "multi_user_chats.users_" + muc.getId().toString().replace("-","_");
        String query = "INSERT INTO " + tableName + " values (?,?,?)";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1,username);
        ps.setString(2,role);
        ps.setDate(3,java.sql.Date.valueOf(LocalDate.now()));
        ps.executeUpdate();
        ps.close();
        conn.close();
    }

    private static void createMultiChatUserTable(UUID muc) throws SQLException {
        Connection conn = connectToDataBase();
        String tableName = "multi_user_chats.users_" + muc.toString().replace("-","_");
        String query = "CREATE TABLE "+ tableName + " (username text PRIMARY KEY , role text, subdate date,FOREIGN KEY (username) REFERENCES public.users(username))";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.executeUpdate();
        ps.close();
        conn.close();
    }

    private static void createMultiChatMessageTable(UUID muc) throws SQLException {
        Connection conn = connectToDataBase();
        String tableName = "multi_user_chats.messages_" + muc.toString().replace("-","_");
        String query = "CREATE TABLE "+ tableName + " ( id uuid PRIMARY KEY , sender text, message text, datetime TIMESTAMP, read_status boolean, FOREIGN KEY (sender) REFERENCES public.users(username))";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.executeUpdate();
        ps.close();
        conn.close();
    }

    public static void addMultiChatToMultiChatTable(MultiUserChat muc) throws SQLException {
        Connection conn = connectToDataBase();
        String sql = "INSERT INTO public.multi_user_chats (id, name, description, publicity,creator,channel) VALUES (?,?,?,?,?,?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setObject(1,muc.getId());
        ps.setString(2,muc.getName(null));
        ps.setString(3,muc.getDescription());
        ps.setBoolean(4,muc.getPublicity());
        ps.setString(5,muc.getCreator());
        ps.setBoolean(6,muc.isChannel());
        ps.executeUpdate();
        ps.close();
        conn.close();
    }

    public static void joinMultiChat(MultiUserChat muc, User user) throws SQLException {
        addChatToUsers(muc.getId(),user.getUsername(),muc.getName(null),"muc");
        if(muc.isChannel()){
            addUserToMultiChat(muc,"normal",user.getUsername());
        }else{
            addUserToMultiChat(muc,"admin",user.getUsername());
        }
    }

    public static void leaveChat(UUID chat, String user) throws SQLException {
        deleteUsersFromChat(chat,user);
        deleteChatFromUsers(chat,user);
    }

    private static void deleteChatFromUsers(UUID id, String username) throws SQLException {
        Connection conn = connectToDataBase();
        String tableName = "users.user_" + username;
        String querry = "DELETE FROM " + tableName + " WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(querry);
        ps.setObject(1,id);
        ps.executeUpdate();
        ps.close();
        conn.close();
    }

    private static void deleteUsersFromChat(UUID id, String username) throws SQLException {
        Connection conn = connectToDataBase();
        String tableName = "multi_user_chats.users_" + id.toString().replace("-","_");
        String querry = "DELETE FROM " + tableName + " WHERE username = ?";
        PreparedStatement ps = conn.prepareStatement(querry);
        ps.setString(1,username);
        ps.executeUpdate();
        ps.close();
        conn.close();
    }

    public static List<MultiUserChat> searchMultiUserChat(String searched) throws SQLException {
        Connection conn = connectToDataBase();
        String query = "SELECT * FROM public.multi_user_chats WHERE name ~ ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1,searched);
        ResultSet rs = ps.executeQuery();
        List<MultiUserChat> mucs = new ArrayList<>();
        while (rs.next()) {
            boolean publicity = rs.getBoolean("publicity");
            if(publicity){
                String description = rs.getString("description");
                UUID id = UUID.fromString(rs.getString("id"));
                String name = rs.getString("name");
                String creator = rs.getString("creator");
                boolean channel = rs.getBoolean("channel");
                mucs.add(new MultiUserChat(id,name,description,publicity,creator,channel));
            }
        }
        return mucs;
    }

    public static MultiUserChat getMultiUserChat(UUID id) throws SQLException {
        MultiUserChat muc = DataBaseManager.getMultiChatDetails(id);
        muc.getMessage().addAll(returnMessages(muc));
        return muc;
    }

    public static String getRole(UUID id, String username) throws SQLException {
        Connection conn = connectToDataBase();
        String tableName = "multi_user_chats.users_" + id.toString().replace("-","_");
        String query = "SELECT role FROM " + tableName + " WHERE username = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1,username);
        ResultSet rs = ps.executeQuery();
        String role = "not a member";
        if(rs.next()){
            role = rs.getString("role");
        }
        rs.close();
        ps.close();
        conn.close();

        return role;
    }

    public static void returnUsers(MultiUserChat muc) throws SQLException {
        List<PrivateUser> users = new ArrayList<>();
        List<String> roles = new ArrayList<>();
        Connection conn = connectToDataBase();
        String tableName = "multi_user_chats.users_" + muc.getId().toString().replace("-","_");
        String querry = "SELECT * FROM " + tableName;
        PreparedStatement ps = conn.prepareStatement(querry);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            String userName = rs.getString("username");
            String role = rs.getString("role");
            PrivateUser user = new PrivateUser(userName,userName);
            users.add(user);
            roles.add(role);
        }
        rs.close();
        ps.close();
        conn.close();
        muc.getUsers().addAll(users);
        muc.getRoles().addAll(roles);
    }

    public static void changeName(UUID uuid, String newProperty) throws SQLException {
        changeNameInMultiChatTable(uuid,newProperty);
        changeNameForUsers(uuid,newProperty);
    }

    private static void changeNameInMultiChatTable(UUID uuid, String newProperty) throws SQLException {
        Connection conn = connectToDataBase();
        String query = "UPDATE public.multi_user_chats SET name = ? WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1,newProperty);
        ps.setObject(2,uuid);
        ps.executeUpdate();
        ps.close();
        conn.close();
    }

    private static void changeNameForUsers(UUID uuid,String newProperty) throws SQLException {
        List<String> usernames = new ArrayList<>();
        Connection conn = connectToDataBase();
        String query = "SELECT * FROM multi_user_chats.users_" + uuid.toString().replace("-","_");
        PreparedStatement ps = conn.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            usernames.add(rs.getString("username"));
        }
        rs.close();
        ps.close();
        conn.close();
        changeNameForUsersWithList(usernames,uuid,newProperty);
    }

    private static void changeNameForUsersWithList(List<String> usernames, UUID uuid, String newProperty) throws SQLException {
        Connection conn = connectToDataBase();
        for(String username : usernames){
            String tableName = "users.user_" + username;
            String query = "UPDATE " + tableName + " SET name = ? WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1,newProperty);
            ps.setObject(2,uuid);
            ps.executeUpdate();
            ps.close();
        }
        conn.close();
    }

    public static void changeDescription(UUID uuid, String newProperty) throws SQLException {
        Connection conn = connectToDataBase();
        String query = "UPDATE public.multi_user_chats SET description = ? WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1,newProperty);
        ps.setObject(2,uuid);
        ps.executeUpdate();
        ps.close();
        conn.close();
    }

    public static void changeRole(UUID uuid, String user, String role) throws SQLException {
        Connection conn = connectToDataBase();
        String tableName = "multi_user_chats.users_" + uuid.toString().replace("-","_");
        String query = "UPDATE " + tableName + " SET role = ? WHERE username = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1,role);
        ps.setString(2,user);
        ps.executeUpdate();
        ps.close();
        conn.close();
    }

    public static void setLastSeen(String username, java.util.Date date,boolean online) throws SQLException {
        Connection conn = connectToDataBase();
        String query = "UPDATE public.users SET last_seen = ? ,online = ? WHERE username = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setTimestamp(1,new Timestamp(date.getTime()));
        ps.setBoolean(2,online);
        ps.setString(3,username);
        ps.executeUpdate();
        ps.close();
        conn.close();
    }

    public static void readAllMessages(UUID chat, String username,String type) throws SQLException {
        Connection conn = connectToDataBase();
        String tableName = null;
        if(type.equals("pv_chat")){
            tableName = "pv_chats.chat_" + chat.toString().replace("-", "_");
        }else if(type.equals("muc")){
            tableName = "multi_user_chats.messages_" + chat.toString().replace("-", "_");
        }

        String query = "UPDATE " + tableName + " SET read_status = ? WHERE sender != ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setBoolean(1,true);
        ps.setString(2,username);
        ps.executeUpdate();
        ps.close();
        conn.close();

    }

    public static void checkAsRead(UUID chatId, String type, UUID messageId) throws SQLException {
        Connection conn = connectToDataBase();
        String tableName = null;
        switch (type){
            case "pv_chat":
                tableName = "pv_chats.chat_" + chatId.toString().replace("-","_");
                break;
            case "muc":
                tableName = "multi_user_chats.messages_" + chatId.toString().replace("-","_");
                break;
        }
        String query = "UPDATE " + tableName + " SET read_status = ? WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setBoolean(1,true);
        ps.setObject(2,messageId);
        ps.executeUpdate();
        ps.close();
        conn.close();
    }
}
