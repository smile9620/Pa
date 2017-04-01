package com.bg.model;

import android.database.Cursor;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017-02-14.
 */

public class InBodyData extends DataSupport{
    private long id;
    private Date testDate; //测试日期，用于排序
    private String strDate; // 用于搜索
    private float height; // 身高
    private User user;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getTestdate() {
        return testDate;
    }

    public void setTestdate(Date testDate) {
        this.testDate = testDate;
    }

    public String getStrDate() {
        return strDate;
    }

    public void setStrDate(String strDate) {
        this.strDate = strDate;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public User getUser(){
        return user;
    }
//    public List<User> getUser(String info) {
//        return DataSupport.where("news_id = ?", String.valueOf(id)).find(Comment.class);
//        List<User> users = new ArrayList<User>();
//        Cursor cursor = DataSupport.findBySQL("select distinct user_id from inbodydata " +
//                "where strDate=? order by testDate desc limit 10", info);
//        int cur_len = cursor.getColumnCount();
//        if (cursor != null && cursor.moveToFirst()) {
//            for (int i = 0; i <cur_len ; i++) {
//                String user_id = cursor.getColumnName(i);
//               User user = DataSupport.where("id = ?",user_id).find(User.class);
//            }
//        }
//             return users;
//    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public synchronized boolean save() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentTime);
        setTestdate(currentTime);
        setStrDate(dateString);
        return super.save();
    }
}
