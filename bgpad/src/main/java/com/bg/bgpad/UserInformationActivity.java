package com.bg.bgpad;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableRow;
import android.widget.TextView;

import com.bg.constant.Constant;
import com.bg.constant.InBodyBluetooth;
import com.bg.model.InBodyData;
import com.bg.model.User;
import com.bg.utils.FormatString;
import com.bg.utils.MyDialog;
import com.bg.utils.SetTitle;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserInformationActivity extends BleActivityResult implements SetTitle.OnTitleBtClickListener {

    @BindView(R.id.title)
    View view;
    @BindView(R.id.image)
    ImageButton image;
    @BindView(R.id.username)
    EditText username;
    @BindView(R.id.usernumber)
    EditText usernumber;
    @BindView(R.id.sex)
    RadioGroup sex;
    @BindView(R.id.boy)
    RadioButton boy;
    @BindView(R.id.girl)
    RadioButton girl;
    @BindView(R.id.birthday)
    EditText birthday;
    @BindView(R.id.age)
    TextView age;
    @BindView(R.id.height)
    EditText height;
    @BindView(R.id.test)
    Button test;
    @BindView(R.id.showheight)
    TableRow showheight;

    private List<User> users;
    private User user;
    private boolean column;
    private boolean showdialog = true;
    private int selectsex = 0;
    private String photopath = "";
    public static UserInformationActivity instance;
    private Intent intent;
    private boolean user_exist;
    private boolean ble_enable = true;
    private String strBirth;

    public UserInformationActivity() {
        instance = UserInformationActivity.this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);
        ButterKnife.bind(this);
        intent = getIntent();
        user_exist = intent.getStringExtra("user_number") == null ? false : true;
        new SetTitle(this, view, new boolean[]{true, user_exist},
                "测试", new int[]{R.drawable.back_bt, R.drawable.delete_bt});
        column = intent.getBooleanExtra("column", true);
        if (!column) {
            showheight.setVisibility(View.VISIBLE);
        } else {
            showheight.setVisibility(View.GONE);
        }
        sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (boy.isChecked()) {
                    selectsex = 0;
                } else {
                    selectsex = 1;
                }
            }
        });

        if (user_exist) {
            showdialog = false;
            usernumber.setFocusable(false);
            username.setFocusable(false);
            boy.setClickable(false);
            girl.setClickable(false);
            birthday.setClickable(false);
            image.setClickable(false);

            users = DataSupport.where(" user_number = ? ", intent.getStringExtra("user_number")).find(User.class);
            username.setText(users.get(0).getUser_name());
            usernumber.setText(users.get(0).getUser_number());
            if (users.get(0).getSex() == 0) {
                boy.setChecked(true);
            } else {
                girl.setChecked(true);
            }
            birthday.setText(users.get(0).getBirthday());
            age.setText(users.get(0).getAge() + "");
            if (users.get(0).getImage_path() != null) {
                File path1 = new File(getSDPath() + "/Image");
                if (path1.exists()) {
                    File file = new File(path1, users.get(0).getImage_path());
                    Uri imaUri = Uri.fromFile(file);
                    showImage(imaUri);
                }
            }
        }
    }

    @Override
    protected void updateState(boolean bool) {
        ble_enable = bool;
        if (!bool) {
            new MyDialog(this).setDialog("蓝牙已断开，请重新连接！", false, true, new MyDialog.DialogConfirm() {
                @Override
                public void dialogConfirm() {
                    finish();
                }
            }).show();
        }
    }

    @Override
    protected void updateData(String str) {
        String[] datas = str.split(" ");
        if (datas[3].equals("13")) {
            Intent intent = new Intent(this, InBodyTestReportActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        }
    }

    @Override
    public void leftBt(ImageButton left) {
        finish();
    }

    @Override
    public void rightBt(ImageButton right) {
        final String number = intent.getStringExtra("user_number");
        final List<InBodyData> list = DataSupport.where("user_number = ?", number).find(InBodyData.class);
        new MyDialog(this).setDialog("是否删除该用户以及该用户下的" + list.size() + "条测试数据！", true, true, new MyDialog.DialogConfirm() {
            @Override
            public void dialogConfirm() {
                DataSupport.deleteAll(User.class, "user_number = ?", number);
                DataSupport.deleteAll(InBodyData.class, "user_number = ?", number);
                if (users.get(0).getImage_path() != null) {  //删除头像
                    File path1 = new File(getSDPath() + "/Image");
                    File file = new File(path1, users.get(0).getImage_path());
                    if (file.exists()) {
                        file.delete();
                    }
                }
                showToast("删除成功！");
                finish();
            }
        }).show();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @OnClick({R.id.image, R.id.test, R.id.birthday})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image:
                Permission();
                break;
            case R.id.birthday:
                showDialog(showdialog);
                break;
            case R.id.test:
                String usernu = usernumber.getText().toString().trim();
                String userna = username.getText().toString().trim();
                String bir = birthday.getText().toString().trim();
                String heig = height.getText().toString().trim();
                String sendData = checkUser(usernu, userna, bir, heig);
                if (sendData != null) {
                    user = new User();
                    user.setUser_number(usernu);
                    user.setUser_name(userna);
                    user.setBirthday(bir);
                    user.setSex(selectsex);
                    if (photopath != "") {
                        user.setImage_path(photopath);
                    }
                    if (ble_enable) { //蓝牙可用时，才能写数据
//                        writeData("34122219870611503X诸葛亮  1200006110180", new byte[]{(byte) 0xEA, (byte) 0x52, (byte) 0x29, (byte) 0x22, (byte) 0xFF});
                        writeData(sendData, new byte[]{(byte) 0xEA, (byte) 0x52, (byte) 0x29, (byte) 0x22, (byte) 0xFF});
//                        writeData(null, InBodyBluetooth.send2);
                    } else {
                        showToast("蓝牙已断开，无法测试！");
                    }
                }
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private String checkUser(String number, String name, String birth, String he) {
        String result;
        if (number == null || number.isEmpty()) {
            usernumber.setHint("编号不能为空！");
            usernumber.setHintTextColor(Color.RED);
            return null;
        } else {
            if (!user_exist) {
                List<User> list = DataSupport.where("user_number = ?", number).find(User.class);
                if (list.size() != 0) {
                    showToast("编号为" + number + "的用户已存在");
                    return null;
                }
            }
        }
        if (name == null || name.isEmpty()) {
            username.setHint("姓名不能为空！");
            username.setHintTextColor(Color.RED);
            return null;
        }
        if (birth == null || birth.isEmpty()) {
            birthday.setHint("出生日期不能为空！");
            birthday.setHintTextColor(Color.RED);
            return null;
        }
        if (!column) {
            if (height.isCursorVisible()) {
                if (he == null || he.isEmpty()) {
                    height.setHint("身高不能为空！");
                    height.setHintTextColor(Color.RED);
                    return null;
                }
            }
        }

        int number_length = 0; //编号18个字节
        int name_length = 0;//姓名8个字节
        int he_length = 0;//身高3个字节

        number_length = number.getBytes().length;//编号长度18字节
        try {
            name_length = name.getBytes("gbk").length;//姓名长度8字节
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        he_length = he.getBytes().length;//身高长度3字节

        if (number_length < 18) {
            for (int i = 0; i < 18 - number_length; i++) {
                number = number + " ";
            }
        }
        if (name_length < 8) {
            for (int i = 0; i < 8 - name_length; i++) {
                name = name + " ";
            }
        }
        if (!column) {
            if (he_length < 3) {
                for (int i = 0; i < 18 - name_length; i++) {
                    he = he + " ";
                }
            }
        } else {
            he = "   ";
        }
        String bi = birthday.getText().toString();
        String[] b = bi.split("-");
        strBirth = b[0] + b[1] + b[2];

        result = number + name + (boy.isChecked() ? 1 : 0) + strBirth +
                (column == false ? 0 : 1) + he;
        return result;
    }

    private void showDialog(Boolean bool) {
        if (bool) {
            final Calendar cal = Calendar.getInstance();
            final Date currentTime = new Date();
            new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    String mon = month + 1 + "";
                    if ((month + 1) < 10) {
                        mon = "0" + (month + 1);
                    }
                    String day = dayOfMonth + "";
                    if (dayOfMonth < 10) {
                        day = "0" + dayOfMonth;
                    }
                    String data = year + "-" + mon + "-" + day;
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = null;
                    try {
                        date = formatter.parse(data);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (date.getTime() > currentTime.getTime()) {
                        showToast("出生日期大于当前日期！");
                    } else {
                        birthday.setText(data);
                        age.setText((cal.get(Calendar.YEAR) - year) + "");
                    }
                }
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        }
    }

    private void Permission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                //申请CAMERA权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.
                        permission.CAMERA}, Constant.CAMERA_REQ);
                return;
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.
                        permission.WRITE_EXTERNAL_STORAGE}, Constant.PICTURE_REQ);
                return;
            }
        }

//        File path1 = new File(Constant.mFilePath+"/image");
//        if (!path1.exists()) {
//            path1.mkdirs();
//        }
        File path1 = new File(getSDPath() + "/Image");
        makeDir(path1);
        photopath = System.currentTimeMillis() + ".jpg";
        File file = new File(path1, photopath);
        Constant.imageUri = Uri.fromFile(file);

        Intent intent = new Intent(UserInformationActivity.this, CameraActivity.class);
        startActivityForResult(intent, Constant.CAMERA_STA_USERINFO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case Constant.CAMERA_REQ:
                case Constant.PICTURE_REQ:
                    Permission();
                    break;
            }
        } else {
            showToast("请在应用管理中打开访问权限！");
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void showImage(Uri uri) {
        Bitmap bitmap;
        try {
            // 读取uri所在的图片
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

            Matrix matrix = new Matrix(); //抗锯齿
            matrix.postScale(0.2f, 0.2f);
            Bitmap temp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

            image.setBackground(new BitmapDrawable(this.getResources(), temp));
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
        } catch (Exception e) {
            photopath = "";
            e.printStackTrace();
        }

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Constant.CAMERA_STA_USERINFO) {
            showImage(Constant.imageUri);

//            FileInputStream fis = null;
//            try {
//                fis = new FileInputStream(mFilePath);
//                Bitmap bitmap = BitmapFactory.decodeStream(fis);
//                image.setBackground(new BitmapDrawable(this.getResources(), bitmap));
//
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    fis.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }


//         File file = new File(Environment.getExternalStorageDirectory()
//                 .getPath() + "/share_pic.png");// 保存到sdcard根目录下，文件名为share_pic.png
//         FileOutputStream fos = null;
//         try {
//             fos = new FileOutputStream(file);
//             bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);//表示压缩70%; 如果不压缩是100，表示压缩率为0
//             fos.close();
//         } catch (FileNotFoundException e) {
//             e.printStackTrace();
//         } catch (IOException e) {
//             e.printStackTrace();
//         }

        }
    }

}