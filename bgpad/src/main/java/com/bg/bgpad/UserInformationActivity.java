package com.bg.bgpad;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bg.constant.Constant;
import com.bg.constant.InBodyBluetooth;
import com.bg.model.User;
import com.bg.utils.BitmapTool;
import com.bg.utils.SetTitle;

import org.litepal.crud.DataSupport;

import java.io.File;
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
    TextInputLayout username;
    @BindView(R.id.usernumber)
    TextInputLayout usernumber;
    @BindView(R.id.sex)
    TextInputLayout sex;
    @BindView(R.id.birthday)
    TextInputLayout birthday;
    @BindView(R.id.height)
    TextInputLayout height;
    @BindView(R.id.mark)
    TextInputLayout mark;
    @BindView(R.id.test)
    Button test;

    private List<User> user;
    private boolean column;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            showToast(msg.obj.toString());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);
        ButterKnife.bind(this);
        new SetTitle(this).setTitleBar(view, new boolean[]{false, false}, "用户信息", new int[]{R.drawable.back_bt,
                R.drawable.userdelete_bt});

        Intent intent = getIntent();
        column = intent.getBooleanExtra("column", true);
        if (!column) {
            height.setVisibility(View.VISIBLE);
        }
        if (intent.getStringExtra("user_number") != null) {
            user = DataSupport.where(Constant.USER_NUMBER + " = ?", intent.getStringExtra(Constant.USER_NUMBER)).find(User.class);
            username.getEditText().setText(user.get(0).getUser_name());
            usernumber.getEditText().setText(user.get(0).getUser_number());
        }

    }

    @Override
    protected void updateState(boolean bool) {

    }

    CountDownTimer countDownTimer = new CountDownTimer(300, 1) {
        @Override
        public void onTick(long millisUntilFinished) {
        }
        @Override
        public void onFinish() {
            Message message = Message.obtain();
            message.obj = builder.toString();
            handler.sendMessage(message);
            builder.delete(0, builder.length());
        }
    };
    private StringBuilder builder = new StringBuilder();
    private long time = 0;

    @Override
    protected void updateData(String str) {
        builder.append(str);
        if ((System.currentTimeMillis() - time) > 1000) {
            time = System.currentTimeMillis();
            countDownTimer.start();
        }
    }

    @Override
    public void leftBt(Button left) {
        finish();
    }

    @Override
    public void rightBt(Button right) {
        DataSupport.deleteAll(User.class, "user_number = ?", user.get(0).getUser_number());
        Toast.makeText(this, "用户" + user.get(0).getUser_name().toString() + "删除成功！",
                Toast.LENGTH_SHORT).show();
    }

    @OnClick({R.id.image, R.id.test})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image:
                Permission();
                break;
            case R.id.test:
//                String name = username.getEditText().getText().toString();
//                String number = usernumber.getEditText().getText().toString();
//                if (name == null || name.isEmpty() || number == null || number.isEmpty()) {
//                    Toast.makeText(this, "姓名或编号不能为空", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                User user = new User();
//                user.setUser_name(username.getEditText().getText().toString());
//                user.setUser_number(usernumber.getEditText().getText().toString());
//                if (user.save()) {
//                    showToast( "保存成功！");
//                } else {
//                    showToast( "保存失败！");
//                }
                writeBle(InBodyBluetooth.send3);
                break;
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

        File path1 = new File(Constant.mFilePath);
        if (!path1.exists()) {
            path1.mkdirs();
        }
        File file = new File(path1, System.currentTimeMillis() + ".jpg");
        Constant.imageUri = Uri.fromFile(file);

        Intent intent = new Intent(UserInformationActivity.this,CameraActivity.class);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Constant.CAMERA_STA_USERINFO) {
            Bitmap bitmap;
            try {
                // 读取uri所在的图片
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Constant.imageUri);
                image.setBackground(new BitmapDrawable(this.getResources(), bitmap));
            } catch (Exception e) {
                e.printStackTrace();
            }

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