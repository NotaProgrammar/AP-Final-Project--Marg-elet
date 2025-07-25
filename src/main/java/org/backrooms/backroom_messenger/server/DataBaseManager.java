package org.backrooms.backroom_messenger.server;

import org.backrooms.backroom_messenger.entity.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
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

    public static void addMessage(Message message) throws SQLException{
        String type = getChatType(message.getChat());
        switch(type){
            case "pv_chat":
                addMessageToChat(message);
                break;
            case "channel":
                //todo
                break;
            case "group":
                //todo
        }

    }

    private static void addMessageToChat(Message message) throws SQLException{
        Connection connection = connectToDataBase();
        String tableName = "pv_chats.chat_" + message.getChat().toString().replace("-","_");
        String sql = "INSERT INTO " + tableName + " (id,sender,message,datetime) VALUES (?,?,?,?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setObject(1,message.getId());
        ps.setString(2,message.getSender());
        ps.setObject(3,message.getMessage());
        ps.setDate(4,java.sql.Date.valueOf(message.getTimeDate()));
        ps.executeUpdate();
        ps.close();
        connection.close();
    }

    private static String getChatType(UUID chat) throws SQLException{
        Connection connection = connectToDataBase();
        String sql = "SELECT * FROM public.chats WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setObject(1, chat);
        ResultSet rs = ps.executeQuery();
        String type = null;
        if(rs.next()){
            type = rs.getString("type");
        }
        ps.close();
        connection.close();
        return type;
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
        String sql = "CREATE TABLE "+ tableName + " ( id uuid PRIMARY KEY , sender text, message text, datetime date, FOREIGN KEY (sender) REFERENCES public.users(username))";
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
        }else if(chat instanceof Channel){
            tableName = "channels.messages_" + chat.getId().toString().replace("-", "_");
        }

        String sql = "SELECT * FROM " + tableName;
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while(rs.next()) {
            String sender = rs.getString("sender");
            String text = rs.getString("message");
            UUID messageId = UUID.fromString(rs.getString("id"));
            LocalDate localDate = rs.getDate("datetime").toLocalDate();
            java.util.Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Message message = new Message(messageId,sender,chat.getId(),text,date);
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
                case "channel":
                    chat = getChannelDetails(id);
                    break;
                case "group" :
                    //todo
            }
            chats.add(chat);
        }
        rs.close();
        ps.close();
        conn.close();
        return chats;
    }

    private static Channel getChannelDetails(UUID id) throws SQLException {
        Connection conn = connectToDataBase();
        String query = "SELECT * FROM public.channels WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setObject(1, id);
        ResultSet rs = ps.executeQuery();
        Channel channel = null;
        if(rs.next()) {
            String name = rs.getString("name");
            String description = rs.getString("description");
            boolean publicity = rs.getBoolean("publicity");
            String creator = rs.getString("creator");
            channel = new Channel(id,name,description,publicity,creator);
        }
        rs.close();
        ps.close();
        conn.close();
        return channel;
    }

    public static void addNewChannel(Channel channel) throws SQLException {
        addToChatTable(channel.getId(),"channel");
        addChannelToChannelTable(channel);
        createChannelMessageTable(channel.getId());
        createChannelUserTable(channel.getId());
        addChatToUsers(channel.getId(), channel.getCreator() ,channel.getName(null),"channel");
        addUserToChannel(channel,"creator",channel.getCreator());
    }

    private static void addUserToChannel(Channel channel,String role,String username) throws SQLException {
        Connection conn = connectToDataBase();
        String tableName = "channels.users_" + channel.getId().toString().replace("-","_");
        String query = "INSERT INTO " + tableName + " values (?,?,?)";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1,username);
        ps.setString(2,role);
        ps.setDate(3,java.sql.Date.valueOf(LocalDate.now()));
        ps.executeUpdate();
        ps.close();
        conn.close();
    }

    private static void createChannelUserTable(UUID channel) throws SQLException {
        Connection conn = connectToDataBase();
        String tableName = "channels.users_" + channel.toString().replace("-","_");
        String query = "CREATE TABLE "+ tableName + " (username text PRIMARY KEY , role text, subdate date,FOREIGN KEY (username) REFERENCES public.users(username))";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.executeUpdate();
        ps.close();
        conn.close();
    }

    private static void createChannelMessageTable(UUID channel) throws SQLException {
        Connection conn = connectToDataBase();
        String tableName = "channels.messages_" + channel.toString().replace("-","_");
        String query = "CREATE TABLE "+ tableName + " ( id uuid PRIMARY KEY , sender text, message text, datetime date, FOREIGN KEY (sender) REFERENCES public.users(username))";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.executeUpdate();
        ps.close();
        conn.close();
    }

    public static void addChannelToChannelTable(Channel channel) throws SQLException {
        Connection conn = connectToDataBase();
        String sql = "INSERT INTO public.channels (id, name, description, publicity,creator) VALUES (?,?,?,?,?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setObject(1,channel.getId());
        ps.setString(2,channel.getName(null));
        ps.setString(3,channel.getDescription());
        ps.setBoolean(4,channel.getPublicity());
        ps.setString(5,channel.getCreator());
        ps.executeUpdate();
        ps.close();
        conn.close();
    }

    public static void joinChannel(Channel channel, User user) throws SQLException {
        addChatToUsers(channel.getId(),user.getUsername(),channel.getName(null),"channel");
        addUserToChannel(channel,"normal",user.getUsername());
    }

    public static void leaveChannel(Channel channel, User user) throws SQLException {
        deleteUsersFromChat(channel.getId(),user.getUsername());
        deleteChatFromUsers(channel.getId(),user.getUsername());
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
        String tableName = "channels.users_" + id.toString().replace("-","_");
        String querry = "DELETE FROM " + tableName + " WHERE username = ?";
        PreparedStatement ps = conn.prepareStatement(querry);
        ps.setString(1,username);
        ps.executeUpdate();
        ps.close();
        conn.close();
    }

    public static List<Channel> searchChannel(String searched) throws SQLException {
        Connection conn = connectToDataBase();
        String query = "SELECT * FROM public.channels WHERE name ~ ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1,searched);
        ResultSet rs = ps.executeQuery();
        List<Channel> channels = new ArrayList<>();
        while (rs.next()) {
            boolean publicity = rs.getBoolean("publicity");
            if(publicity){
                String description = rs.getString("description");
                UUID id = UUID.fromString(rs.getString("id"));
                String name = rs.getString("name");
                String creator = rs.getString("creator");
                channels.add(new Channel(id,name,description,publicity,creator));
            }
        }
        return channels;
    }

    public static Channel getChannel(UUID id) throws SQLException {
        Channel channel = DataBaseManager.getChannelDetails(id);
        channel.getMessage().addAll(returnMessages(channel));
        return channel;
    }

    public static String getRole(UUID id, String username) throws SQLException {
        Connection conn = connectToDataBase();
        String tableName = "channels.users_" + id.toString().replace("-","_");
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
}
