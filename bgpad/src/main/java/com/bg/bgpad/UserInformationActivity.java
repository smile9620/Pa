package com.bg.bgpad;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewDebug;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bg.constant.Constant;
import com.bg.constant.InBodyBluetooth;
import com.bg.model.InBodyData;
import com.bg.model.User;
import com.bg.utils.BitmapTool;
import com.bg.utils.MyDialog;
import com.bg.utils.SetTitle;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.Calendar;
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

    private List<User> user;
    private boolean column;
    private boolean showdialog = true;

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
        new SetTitle(this, view, new boolean[]{true, false},
                "测试", new int[]{R.drawable.back_bt, R.drawable.ble_bt});
//        new MyDialog(this).showDialog("对不起，编号重复，请重新输入！", false,true, new MyDialog.DialogConfirm() {
//            @Override
//            public void dialogConfirm() {
//                showToast("我知道了！");
//            }
//        });
        Intent intent = getIntent();
        column = intent.getBooleanExtra("column", true);
        if (!column) {
            height.setVisibility(View.VISIBLE);
        }
        sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (boy.isChecked()) {
                    showToast("男生");
                } else {
                    showToast("女生");
                }
            }
        });

        if (intent.getStringExtra("user_number") != null) {
            showdialog = false;
            usernumber.setFocusable(false);
            username.setFocusable(false);
            boy.setClickable(false);
            girl.setClickable(false);
            birthday.setClickable(false);
//            user = DataSupport.where(Constant.USER_NUMBER + " = ?", intent.getStringExtra(Constant.USER_NUMBER)).find(User.class);
//            username.setText(user.get(0).getUser_name());
//            usernumber.setText(user.get(0).getUser_number());
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
    public void leftBt(ImageButton left) {
        finish();
    }

    @Override
    public void rightBt(ImageButton right) {
        DataSupport.deleteAll(User.class, "user_number = ?", user.get(0).getUser_number());
        Toast.makeText(this, "用户" + user.get(0).getUser_name().toString() + "删除成功！",
                Toast.LENGTH_SHORT).show();
    }

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
//                String name = username.getText().toString();
//                String number = usernumber.getText().toString();
//                String heig = height.getText().toString();
////                if (name == null || name.isEmpty() || number == null || number.isEmpty()) {
////                    Toast.makeText(this, "姓名或编号不能为空", Toast.LENGTH_SHORT).show();
////                    return;
////                }
//
//                User user = new User();
//                InBodyData inBodyData = new InBodyData();
//                user.setUser_name(name);
//                user.setUser_number(number);
//                inBodyData.setHeight(Float.parseFloat(heig));
//                inBodyData.setUser(user);
//                user.getData_list().add(inBodyData);
//                if (user.save() && inBodyData.save()) {
//                    showToast("保存成功！");
//                } else {
//                    showToast("保存失败！");
//                }
////                writeBle(InBodyBluetooth.send3);
//                break;
        }
    }

    private void showDialog(Boolean bool) {
        if (bool) {
            final Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    String mon = null;
                    if ((month + 1) < 10) {
                        mon = "0" + (month + 1);
                    }
                    birthday.setText(year + "-" + mon + "-" + dayOfMonth);
                    age.setText((cal.get(Calendar.YEAR) - year) + "");
                }
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void alertDialog(){

        Dialog mDeleteDialog = new Dialog(this, R.style.myDialog);
        View vi = this.getLayoutInflater().inflate(R.layout.error_tip,null);
        mDeleteDialog.setContentView(vi);
        mDeleteDialog.show();
        mDeleteDialog.getWindow().setGravity(Gravity.CENTER);

//        AlertDialog.Builder buldier = new AlertDialog.Builder(this);
//        View vi = this.getLayoutInflater().inflate(R.layout.error_tip,null);
//        buldier.setView(vi);
//        AlertDialog dialog = buldier.create();
//        Window window = dialog.getWindow();
//        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.alpha = 0.9f;
//        window.setAttributes(lp);
//        dialog.show();
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Constant.CAMERA_STA_USERINFO) {
            Bitmap bitmap;
            try {
                // 读取uri所在的图片
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Constant.imageUri);

                Matrix matrix=new Matrix(); //抗锯齿
                matrix.postScale(0.2f, 0.2f);
                Bitmap temp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);

                image.setBackground(new BitmapDrawable(this.getResources(), temp));
                if (!bitmap.isRecycled()) {
                    bitmap.recycle();
                }
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