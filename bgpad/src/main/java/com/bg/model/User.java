package com.bg.model;

import android.view.Gravity;
import android.widget.Toast;

import com.bg.bgpad.AppContext;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by Administrator on 2017-02-14.
 */

public class User extends DataSupport {

    private long id;            //id
    private String user_name;   //姓名
    @Column(unique = true, nullable = false)
    private String user_number;        //编号
    private int sex;           //性别 0 代表男生，1 代表女生
    private String birthday;      //出生年月
    private String image_path;  //头像路径
    private String mark;       //备注
    private Date createDate;   //创建日期，用于排序
    private String strDate;    //用于搜索 如：2017-03-20
    protected int age;           //年龄
    protected List<InBodyData> data_list = new ArrayList<InBodyData>(); //测试数据

    public List<InBodyData> getData_list() {
        return DataSupport.where("user_number = ?", user_number).find(InBodyData.class);
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_number() {
        return user_number;
    }

    public void setUser_number(String user_id) {
        this.user_number = user_id;
    }

    public int getAge() {
        char[] bir = birthday.toCharArray();
        String year = bir[0] + "" + bir[1] + bir[2] + bir[3];
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.YEAR) - Integer.parseInt(year);
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getStrDate() {
        return strDate;
    }

    public void setStrDate(String strDate) {
        this.strDate = strDate;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    @Override
    public synchronized boolean save() {
        if (DataSupport.where("user_number = ?", getUser_number()).find(User.class).size() > 0) {
            Toast toast = Toast.makeText(AppContext.getContext(), "编号为" + getUser_number() +
                    "的用户已存在！", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentTime);
        setCreateDate(currentTime);
        setStrDate(dateString);
        return super.save();
    }
}
