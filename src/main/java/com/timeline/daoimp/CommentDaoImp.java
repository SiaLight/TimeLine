package com.timeline.daoimp;

import com.timeline.entity.*;
import com.timeline.dao.*;
import com.timeline.util.*;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.DoubleBinaryOperator;

public class CommentDaoImp implements CommentDao {
    public static final String URL = "jdbc:mysql://localhost:3306/timeline";
    public static final String USER = "root";
    public static final String PASSWORD ="123456";

    //创建与数据库的连接


    @Override
    public Connection getConnection(){
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(URL,USER,PASSWORD);
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return conn;
    }

    @Override
    public void close(Connection conn, PreparedStatement pstmt, ResultSet rs){
        try{
            if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            if(conn != null) conn.close();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public boolean insertComment(Comment comment) {
        String sql = "insert into comments(uid,username,comment,picture,timestamp) values (?,?,?,?,?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try{
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1,comment.getUserID());
            pstmt.setString(2,comment.getUserName());
            pstmt.setString(3,comment.getCommetText());
            pstmt.setString(4,comment.getPicture());
            pstmt.setString(5,comment.getTimeStamp());

            pstmt.executeUpdate();
            return true;
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            close( conn, pstmt,null);
        }
        return false;
    }

    @Override
    public boolean deleteComment(int uid) {
        String sql = "delete from comments where uid = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try{
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1,uid);

            pstmt.executeUpdate();
            return true;
        } catch (SQLException e){
            e.printStackTrace();
        }finally {
            close(conn,pstmt,null);
        }
        return false;
    }

    @Override
    public List<Comment> findCommentList() {
        String sql = "select * from comments order by timestamp desc";
        Connection conn = null;
        PreparedStatement pstmt  = null;
        ResultSet rs = null;

        //创建一个集合对象来存放查询到的数据
        List<Comment> commentList = new ArrayList<>();
        try{
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while(rs.next()){
                int uid = rs.getInt("uid");
                String username = rs.getString("username");
                String comment = rs.getString("comment");
                String picture = rs.getString("picture");
                String timestamp = rs.getString("timestamp");
                //
                Comment comment1 = new Comment(uid,username,comment,picture,timestamp);
                commentList.add(comment1);
            }
            return commentList;
        }catch (SQLException e){
            e.printStackTrace();
        } finally {
            close(conn,pstmt,rs);
        }
        return null;
    }
    public String convert(String dateTimeStamp) throws ParseException {
        String result;
        int  minute = 1000 * 60;
        int hour = minute * 60;
        int day = hour * 24;
        int halfamonth = day * 15;
        int month = day * 30;
        long now = new java.util.Date().getTime();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final Date datetime = sdf.parse(dateTimeStamp);
        final long time = datetime.getTime();

        long diffValue = now - time;
        if(diffValue < 0){
            return "";
        }
        long monthC = diffValue / month;
        long weekC = diffValue / (7 * day);
        long dayC = diffValue / day;
        long hourC = diffValue / hour;
        long minC = diffValue / minute;
        if(monthC>=1){
            if(monthC<=12)
                result="" + monthC+ "月前";
            else{
                result=dateTimeStamp;
            }
        }
        else if(weekC>=1){
            result="" + weekC + "周前";
        }
        else if(dayC>=1){
            result=""+ dayC +"天前";
        }
        else if(hourC>=1){
            result=""+ hourC +"小时前";
        }
        else if(minC>=1){
            result=""+ minC +"分钟前";
        }else{
            result="刚刚";
        }
        return result;

    }

}
