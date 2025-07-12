package org.backrooms.backroom_messenger.server;

import org.backrooms.backroom_messenger.entity.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DataBaseManager {
    private static  String JDBC_URL = null;
    private static  String USERNAME = null;
    private static  String PASSWORD = null;

    private static final Lock usersLock = new ReentrantLock();
    
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
                System.out.println(e);
            }
        }
        return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
    }

    public static void addUserToDataBase(User user) throws SQLException {
        usersLock.lock();
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
        usersLock.unlock();
        createUserTable(user.getUsername());
    }

    private static void createUserTable(String user) throws SQLException {
        Connection conn = connectToDataBase();
        String tableName = "users.user_" + user;
        String sql = "CREATE TABLE " + tableName + " (id uuid, name text, type text, PRIMARY KEY (id))";
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

    private static void createChatTable(UUID chatId) throws SQLException {
        Connection conn = connectToDataBase();
        String tableName = "pv_chats.chat_" + chatId.toString().replace("-", "_");
        String sql = "CREATE TABLE "+ tableName + " ( id uuid, sender text, message text, datetime date, PRIMARY KEY (id))";
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
        addtoChatTable(chatId,"pv_chat");
        createChatTable(chatId);
        addPVChatToUsers(chatId,user1,user2);
    }

    private static void addtoChatTable(UUID chatId, String type) throws SQLException {
        Connection conn = connectToDataBase();
        String sql = "INSERT INTO public.chats (id, type) VALUES (?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setObject(1, chatId);
        ps.setString(2, type);
        ps.executeUpdate();
        ps.close();
        conn.close();
    }

    private static void addPVChatToUsers(UUID chatId,String user1,String user2) throws SQLException {
        Connection conn = connectToDataBase();
        String tableName = "users.user_" + user1;
        String sql1 = "INSERT INTO " + tableName + " (id, name, type) VALUES (?, ?, ?)";
        tableName = "users.user_" + user2;
        String sql2 = "INSERT INTO " + tableName + " (id, name, type) VALUES (?, ?, ?)";
        PreparedStatement ps1 = conn.prepareStatement(sql1);
        ps1.setObject(1, chatId);
        ps1.setString(2, user2);
        ps1.setString(3, "pv_chat");
        ps1.executeUpdate();
        ps1.close();
        PreparedStatement ps2 = conn.prepareStatement(sql2);
        ps2.setObject(1, chatId);
        ps2.setString(2, user1);
        ps2.setString(3, "pv_chat");
        ps2.executeUpdate();
        ps2.close();
        conn.close();
    }

    public static List<Message> returnMessages(Chat chat) throws SQLException {
        List<Message> messages = new ArrayList<>();
        Connection conn = connectToDataBase();
        String tableName = "pv_chats.chat_" + chat.getId().toString().replace("-", "_");
        String sql = "SELECT * FROM " + tableName;
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while(rs.next()) {
            User sender = getUserFromDataBase(rs.getString("sender"));
            String text = rs.getString("message");
            UUID messageId = UUID.fromString(rs.getString("id"));
            Date date = rs.getDate("datetime");
            Message message = new Message(messageId,sender,chat,text,date);
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
                    //todo
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
}
