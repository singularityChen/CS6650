package org.example;


import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SwipeDao {
    private static BasicDataSource dataSource;
    public SwipeDao() throws SQLException {
        this.dataSource = DBCPDataSource.getDataSource();
    }

    public void createSwipeTable(){
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String createTableQueryStatement = "CREATE TABLE Swipe (\n" +
                "   id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                "   swipee VARCHAR(50) NOT NULL,\n" +
                "   swiper VARCHAR(50) NOT NULL,\n" +
                "   leftOrRight VARCHAR(50) NOT NULL\n" +
                ");";

        try {
            conn = dataSource.getConnection();
            preparedStatement = conn.prepareStatement(createTableQueryStatement);

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


    public static void main(String[] args) throws Exception{
        SwipeDao swipeDao = new SwipeDao();
        //swipeDao.createSwipeTable();
        swipeDao.createSwipe(new Swipe("A", "B", "left"));
        swipeDao.createSwipe(new Swipe("A", "C", "left"));
        swipeDao.createSwipe(new Swipe("B", "C", "left"));
    }
}
