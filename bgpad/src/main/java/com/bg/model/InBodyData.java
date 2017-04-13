package com.bg.model;

import android.database.Cursor;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017-02-14.
 */

public class InBodyData extends DataSupport {
    private long id;
    private Date testDate;  //测试日期，用于排序
    private String strDate; // 用于显示
    private int date;       //用于搜索
    private float height;   // 身高
    private String user_number;        //编号 外键
    protected User user;

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }
    public User getUser() {
        return DataSupport.where("user_number = ?", user_number).find(User.class).get(0);
    }

    public String getUser_number() {
        return user_number;
    }

    public void setUser_number(String user_number) {
        this.user_number = user_number;
    }

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

    @Override
    public synchronized boolean save() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentTime);
        setTestdate(currentTime);
        setStrDate(dateString);

        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;
        String mon = null;
        if ((month + 1) < 10) {
            mon = "0" + month;
        }
        String fillDate =cal.get(Calendar.YEAR) + mon + cal.get(Calendar.DAY_OF_MONTH);
        setDate(Integer.parseInt(fillDate));
        return super.save();
    }
}
