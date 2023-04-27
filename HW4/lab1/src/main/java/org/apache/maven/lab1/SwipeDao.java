package org.apache.maven.lab1;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SwipeDao {
    private static BasicDataSource dataSource;
    public SwipeDao() throws SQLException {
        this.dataSource = DBCPDataSource.getDataSource();
    }

    public List<String> getAllSwiper(String id){
        List<String> swipers = new ArrayList<>();

        Connection conn = null;
        Statement statement = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            conn = dataSource.getConnection();
            statement = conn.createStatement();
            String getQueryStatement = "SELECT swiper FROM Swipe WHERE swipee = '"+ id +"'  AND leftOrRight = 'left';";

            resultSet = statement.executeQuery(getQueryStatement);

            while (resultSet.next()) {
                String swiper = resultSet.getString("swiper");
                swipers.add(swiper);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return swipers;
    }

    public void createSwipe(Swipe swipe){
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String insertQueryStatement = "INSERT INTO Swipe (swiper, swipee, leftOrRight) " +
                "VALUES (?,?,?)";
        try {
            conn = dataSource.getConnection();
            preparedStatement = conn.prepareStatement(insertQueryStatement);
            preparedStatement.setString(1, swipe.getSwiper());
            preparedStatement.setString(2, swipe.getSwipee());
            preparedStatement.setString(3, swipe.getLeftOrRight());

            // execute insert SQL statement
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public int getLikes(String id){

        int ans = -1;
        Connection conn = null;
        Statement statement = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            conn = dataSource.getConnection();
            statement = conn.createStatement();
            String getQueryStatement = "SELECT COUNT(*) FROM Swipe WHERE swipee = '"+ id +"' AND leftOrRight = 'left';";


            resultSet = statement.executeQuery(getQueryStatement);

            if (resultSet.next()) {
                ans = resultSet.getInt(1); // Get the count value from the first column
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return ans;
    }

    public int getDisLikes(String id){

        int ans = -1;
        Connection conn = null;
        Statement statement = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            conn = dataSource.getConnection();
            statement = conn.createStatement();
            String getQueryStatement = "SELECT COUNT(*) FROM Swipe WHERE swipee = '"+ id +"' AND leftOrRight = 'right';";


            resultSet = statement.executeQuery(getQueryStatement);

            if (resultSet.next()) {
                ans = resultSet.getInt(1); // Get the count value from the first column
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return ans;
    }


    public static void main(String[] args) throws Exception{
          SwipeDao swipeDao = new SwipeDao();

//        swipeDao.createSwipe(new Swipe("A", "B", "left"));
//        swipeDao.createSwipe(new Swipe("A", "C", "left"));
//        swipeDao.createSwipe(new Swipe("B", "C", "left"));


        List<String> list = swipeDao.getAllSwiper("C");
        for(String s: list){
            System.out.println(s);
        }

        int c = swipeDao.getLikes("C");
        System.out.println(c);

//        int c = swipeDao.getDisLikes("C");
//        System.out.println(c);

    }
}