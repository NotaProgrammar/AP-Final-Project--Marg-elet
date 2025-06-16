package org.backrooms.backroom_messenger.server;

import org.backrooms.backroom_messenger.entity.User;

import java.sql.*;

public class DataBaseManager {
    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/Margelet";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "D29011385m";
    public static Connection connectToDataBase() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
    }

    public static void addUserToDataBase(User user) throws SQLException {
        Connection con = connectToDataBase();
        String sql = "INSERT INTO public.users (username, password, salt) VALUES (?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, user.getUsername());
        ps.setString(2, user.getPassword());
        ps.setBytes(3, user.getSalt());
        ps.executeUpdate();

        ps.close();
        con.close();
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
}
