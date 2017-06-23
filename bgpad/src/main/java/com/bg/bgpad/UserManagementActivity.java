package com.bg.bgpad;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bg.constant.Constant;
import com.bg.model.InBodyData;
import com.bg.model.User;
import com.bg.utils.ExcelUtils;
import com.bg.utils.MyDialog;
import com.bg.utils.SelectionUser;
import com.bg.utils.SetTitle;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileInputStream;
import java.lang.annotation.ElementType;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserManagementActivity extends BaseActivity implements SelectionUser.SetOnSelectoinUser,
        SetTitle.OnTitleBtClickListener, AdapterView.OnItemClickListener {
    @BindView(R.id.title)
    View view;
    @BindView(R.id.selection)
    View selection;
    @BindView(R.id.datatip)
    TextView datatip;
    @BindView(R.id.listview)
    ListView listview;
    @BindView(R.id.checkBox)
    CheckBox checkBox;
    @BindView(R.id.export)
    Button export;
    @BindView(R.id.delete)
    Button delete;
    @BindView(R.id.clearcache)
    Button clearcache;
    @BindView(R.id.total)
    TextView total;
    File file;
    private List<Integer> index;
    private SimpleAdapter simpleAdapter;
    private int[] colors = new int[]{0xFFFFFFFF, 0xFFEFEFEF};
    private ArrayList<Map<String, String>> list_maps = new ArrayList<>();
    private Map<Integer, Boolean> checkmap = new HashMap<>();// 存放已被选中的CheckBox
    private MyDialog expDialog;
    private Dialog exportDialog;
    private MyHandler handler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<UserManagementActivity> mActivity;

        public MyHandler(UserManagementActivity activity) {
            mActivity = new WeakReference<UserManagementActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            UserManagementActivity act = mActivity.get();
            if (act != null) {
                switch (msg.what) {
                    case 0:
                        act.total.setText("共 " + msg.arg1 + " 条数据");
                    case 1:
                        act.simpleAdapter.notifyDataSetChanged();
                        break;
                    case 2:
                        act.refreshFile();
                        act.exportDialog.dismiss();
                        act.expDialog.setDialog("数据已导出，存储在文件夹Bg\\Test中！", true, false, null).show();
                        break;
                }
            }
        }
    }
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 0:
//                    total.setText("共 " + msg.arg1 + " 条数据");
//                case 1:
//                    simpleAdapter.notifyDataSetChanged();
//                    break;
//                case 2:
//                    refreshFile();
//                    exportDialog.dismiss();
//                    expDialog.setDialog("数据已导出，存储在文件夹Bg\\Test中！", true, false, null).show();
//                    break;
//            }
//
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);
        ButterKnife.bind(this);
        new SetTitle(this, view, new boolean[]{true, false},
                "数据管理", new int[]{R.drawable.back_bt, R.drawable.ble_bt});
        new SelectionUser(this, this, selection, false);
        showCache();
        getData();
        simpleAdapter = new SimpleAdapter(this, list_maps, R.layout.usersetlist_item,
                new String[]{"user_number", "user_name", "strDate"},
                new int[]{R.id.usernumber, R.id.username, R.id.testdate}) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View adapterView = super.getView(position, convertView, parent);
                int colorPos = position % colors.length;
                final int pos = position;
                adapterView.setBackgroundColor(colors[colorPos]);
                CheckBox box = (CheckBox) adapterView.findViewById(R.id.checkBox);
                box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            checkmap.put(pos, true);
                        } else {
                            checkmap.remove(pos);
                        }
                    }
                });
                if (checkmap != null && checkmap.containsKey(position)) {
                    box.setChecked(true);
                } else {
                    box.setChecked(false);
                }
                return adapterView;
            }
        };

        listview.setAdapter(simpleAdapter);
        listview.setOnItemClickListener(this);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    for (int i = 0; i < list_maps.size(); i++) {
                        checkmap.put(i, true);
                    }
                } else {
                    checkmap.clear();
                }
                simpleAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        simpleAdapter.notifyDataSetChanged();
    }

    private void getData() {
        List<InBodyData> inbodylist = DataSupport.order("testDate desc").limit(15).find(InBodyData.class);
        List<Map<String, String>> datalist = new ArrayList<Map<String, String>>();
        for (int i = 0; i < inbodylist.size(); i++) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("data_id", String.valueOf(inbodylist.get(i).getId()));
            map.put("user_number", inbodylist.get(i).getUser_number());
            map.put("user_name", inbodylist.get(i).getUser().getUser_name());
            map.put("sex", inbodylist.get(i).getUser().getSex() == 0 ? "女" : "男");//性别 0 代表女生，1 代表男生
            map.put("strDate", inbodylist.get(i).getStrDate());
            datalist.add(map);
        }
        total.setText("前 " + datalist.size() + " 条数据");
        addData(datalist);
    }

    private void addData(List<Map<String, String>> list) {
        list_maps.clear();
        if (list.size() != 0) {

            for (int i = 0; i < list.size(); i++) {
                String data_id = list.get(i).get("data_id");
                if (data_id != null) {
                    Map<String, String> map = new HashMap<>();
                    map.put("data_id", list.get(i).get("data_id"));
                    map.put("user_number", list.get(i).get("user_number"));
                    map.put("user_name", list.get(i).get("user_name"));
                    map.put("sex", list.get(i).get("sex"));
                    map.put("strDate", list.get(i).get("strDate"));
                    list_maps.add(map);
                }
            }
            if (list_maps.size() != 0) {
                datatip.setVisibility(View.GONE);
            }
        } else {
            datatip.setVisibility(View.VISIBLE);
        }
        handler.sendEmptyMessage(1);
    }

    @Override
    public void sendList(List<Map<String, String>> list) {
        total.setText("共 " + list.size() + " 条数据");
        addData(list);
    }

    @Override
    public void onAdd() {
    }

    @Override
    public void leftBt(ImageButton left) {
        finish();
    }

    @Override
    public void rightBt(ImageButton right) {
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        CheckBox box = (CheckBox) view.findViewById(R.id.checkBox);
//        box.setChecked(!box.isChecked());
        view.setBackgroundColor(ContextCompat.getColor(this, R.color.title_bar));
        Intent intent = new Intent(this, TestReportActivity.class);
        String data_id = list_maps.get(position).get("data_id");
        intent.putExtra("data_id", data_id);
        startActivity(intent);
    }

    @OnClick({R.id.export, R.id.delete, R.id.clearcache})
    public void onClick(View view) {
        if (view.getId() == R.id.clearcache) {
            deleteFolderFile(file.getPath(), false);
            showCache();
            refreshFile();
        } else {
            if (list_maps.size() != 0) {
                if (checkmap.size() != 0) {
                    index = new ArrayList<Integer>();
                    for (Integer key : checkmap.keySet()) {
                        index.add(key);
                    }
                    Collections.sort(index);    //升序排列;
                    switch (view.getId()) {
                        case R.id.delete:
                            new MyDialog(this).setDialog("确定彻底删除所选选项？", true, true, new MyDialog.DialogConfirm() {
                                @Override
                                public void dialogConfirm() {

                                    MyDialog delDialog = new MyDialog(UserManagementActivity.this);
                                    Dialog deleteDialog = delDialog.setDialog("数据正在删除中，请稍后...", false, false, null);
                                    deleteDialog.show();
                                    for (int i = index.size() - 1; 0 <= i; i--) {
                                        Map<String, String> map = list_maps.get(index.get(i));
                                        String data_id = map.get("data_id");
                                        list_maps.remove(map);
                                        checkmap.remove(index.get(i));
                                        DataSupport.deleteAll(InBodyData.class, "id = ?", data_id);
                                    }
                                    if (checkBox.isChecked()) {
                                        checkBox.setChecked(false);
                                    }
                                    deleteDialog.dismiss();
                                    Message message = Message.obtain();
                                    message.what = 0;
                                    message.arg1 = list_maps.size();
                                    handler.sendMessage(message);
                                    delDialog.setDialog("数据已删除！", true, false, null).show();
                                }
                            }).show();
                            break;
                        case R.id.export:
                            Permission();
                            break;
                    }
                } else {
                    showToast("请选择需选项！");
                }
            } else {
                showToast("无数据！");
            }
        }
    }

    private void Permission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.
                        permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, Constant.EXPORT_REQ);
                return;
            }
        }
//        File file = new File(getSDPath() + "/Test");
//        makeDir(file);
//        String fileName = "test.xls";
//        File filePath = new File(file.getPath() + "/" + fileName);
//        if (filePath.exists()) {
//
//        }
//        if (filePath.exists()) {
//            filePath.delete();
//        }
//        String path = file.toString() + "/" + fileName;
        String path = filePath("test");
        ArrayList<ArrayList<String>> exprList = new ArrayList<ArrayList<String>>();
        expDialog = new MyDialog(this);
        exportDialog = expDialog.setDialog("数据正在导出中，请稍后...", false, false, null);
        exportDialog.show();
        ExcelUtils.initExcel(path, new String[]{"测试报告ID", "编号", "姓名", "性别", "出生日期",
                "年龄", "测试日期", "总评分", "身高", "体重", "正常范围", "", "水分", "", "肌肉量", "去脂体重", "细胞内液",
                "正常范围", "细胞外液", "正常范围", "蛋白质", "正常范围", "", "无机盐", "正常范围", "",
                "体脂肪", "", "正常范围", "骨骼肌", "正常范围", "", "体脂肪",
                "正常范围", "", "BMI", "正常范围", "", "体脂率", "正常范围", "", "腰臀比", "正常范围",
                "", "左臂", "正常范围", "", "右臂", "正常范围", "", "躯干", "正常范围", "", "左腿",
                "正常范围", "", "右腿", "正常范围", "", "标准体重", "标准身高", "肌肉控制", "体重控制",
                "脂肪控制", "基础代谢量", "RA 5kHz", "RA 50kHz", "RA 2505kHz", "LA 5kHz", "LA 50kHz",
                "LA 250kHz", "TR 5kHz", "TR 50kHz", "TR 250kHz", "RL 5kHz", "RL 50kHz", "RL 250kHz",
                "LL 5kHz", "LL 50kHz", "LL 250kH", "浮肿", "", "上肢均衡", "下肢均衡", "体型判断"});
        for (int i = index.size() - 1; 0 <= i; i--) {
            Map<String, String> map = list_maps.get(index.get(i));
            ArrayList<String> beanList = new ArrayList<String>();
            String data_id = map.get("data_id");
            InBodyData inBodyData = DataSupport.where("id = ? ", data_id).find(InBodyData.class).get(0);
            User user = inBodyData.getUser();
            beanList.add(String.valueOf(inBodyData.getId()));//测试报告ID
            beanList.add(user.getUser_number()); //编号
            beanList.add(user.getUser_name());//姓名
            beanList.add(user.getSex() == 0 ? "女" : "男"); //性别 0 代表女生，1 代表男生
            beanList.add(user.getBirthday());//出生日期
            beanList.add(String.valueOf(user.getAge()));//年龄
            beanList.add(inBodyData.getStrDate());//测试日期
            beanList.add(String.valueOf(inBodyData.getScore())); //身体总评分 score
            beanList.add(String.valueOf(inBodyData.getHeight()));//身高
            beanList.add(String.valueOf(inBodyData.getWeight()));//体重
            beanList.add(inBodyData.getNormalrange5());//体重正常范围
            beanList.add(inBodyData.getWeightrange());
            beanList.add(String.valueOf(inBodyData.getTotalwater()));//总水分
            beanList.add(inBodyData.getWater());// 健康诊断  身体水分
            beanList.add(String.valueOf(inBodyData.getMuscle()));//肌肉量
            beanList.add(String.valueOf(inBodyData.getFatfree()));//去脂体重
            //身体成分分析
            beanList.add(String.valueOf(inBodyData.getInliquid()));//细胞内液
            beanList.add(inBodyData.getNormalrange0());//细胞内液正常范围
            beanList.add(String.valueOf(inBodyData.getOutliquid()));//细胞外液
            beanList.add(inBodyData.getNormalrange1());//细胞外液正常范围
            beanList.add(String.valueOf(inBodyData.getTotalprotein()));//蛋白质
            beanList.add(inBodyData.getNormalrange2());//蛋白质正常范围
            beanList.add(inBodyData.getProtein());//营养评估蛋白质
            beanList.add(String.valueOf(inBodyData.getTotalinorganicsalt()));//无机盐
            beanList.add(inBodyData.getNormalrange3());//无机盐正常范围
            beanList.add(inBodyData.getInorganicsalt());//营养评估无机盐
            beanList.add(String.valueOf(inBodyData.getBodyfat()));//体脂肪
            beanList.add(inBodyData.getNormalrange4());//体脂肪正常范围
            beanList.add(inBodyData.getFat());//营养评估体脂肪
            //肌肉脂肪分析
            beanList.add(String.valueOf(inBodyData.getBones()));//骨骼肌
            beanList.add(inBodyData.getNormalrange6());//骨骼肌正常范围
            beanList.add(inBodyData.getBonesrange());
            beanList.add(String.valueOf(inBodyData.getMusclefat()));//体脂肪
            beanList.add(inBodyData.getNormalrange7());//体脂肪正常范围
            beanList.add(inBodyData.getFatrange());
            // 肥胖分析
            beanList.add(String.valueOf(inBodyData.getBmi()));//BMI
            beanList.add(inBodyData.getNormalrange8());//BMI正常范围
            beanList.add(inBodyData.getBmirange());
            beanList.add(String.valueOf(inBodyData.getFatrate()));//体脂率
            beanList.add(inBodyData.getNormalrange9());//体脂率正常范围
            beanList.add(inBodyData.getFatraterange());
            beanList.add(String.valueOf(inBodyData.getWaistrate()));//腰臀比
            beanList.add(inBodyData.getNormalrange10());//腰臀比正常范围
            beanList.add(inBodyData.getWaistraterange());
            //节段肌肉分析
            beanList.add(String.valueOf(inBodyData.getLeftarm()));//左臂
            beanList.add(inBodyData.getNormalrange11());//左臂正常范围
            beanList.add(inBodyData.getLeftarmrange());
            beanList.add(String.valueOf(inBodyData.getRightarm()));//右臂
            beanList.add(inBodyData.getNormalrange12());//右臂正常范围
            beanList.add(inBodyData.getRightarmrange());
            beanList.add(String.valueOf(inBodyData.getTrunk()));//躯干
            beanList.add(inBodyData.getNormalrange13());//躯干正常范围
            beanList.add(inBodyData.getTrunkrange());
            beanList.add(String.valueOf(inBodyData.getLeftleg()));//左腿
            beanList.add(inBodyData.getNormalrange14());//左腿正常范围
            beanList.add(inBodyData.getLeftlegrange());
            beanList.add(String.valueOf(inBodyData.getRightleg()));//右腿
            beanList.add(inBodyData.getNormalrange15());//右腿正常范围
            beanList.add(inBodyData.getRightlegrange());
            //体重身高分析
            beanList.add(String.valueOf(inBodyData.getStandardweight()));//标准体重
            beanList.add(String.valueOf(inBodyData.getStandardheight()));//标准身高
            beanList.add(String.valueOf(inBodyData.getMusclecontrol()));//肌肉控制
            beanList.add(String.valueOf(inBodyData.getWeightcontrol()));//体重控制
            beanList.add(String.valueOf(inBodyData.getFatcontrol()));//脂肪控制
            beanList.add(String.valueOf(inBodyData.getBasalmetabolism()));//基础代谢量
            //生物电阻抗 biologyImpedance
            beanList.add(String.valueOf(inBodyData.getRa0()));//RA 5kHz
            beanList.add(String.valueOf(inBodyData.getRa1()));//RA 50kHz
            beanList.add(String.valueOf(inBodyData.getRa2()));//RA 2505kHz
            beanList.add(String.valueOf(inBodyData.getLa0()));//LA 5kHz
            beanList.add(String.valueOf(inBodyData.getLa1()));//LA 50kHz
            beanList.add(String.valueOf(inBodyData.getLa2()));//LA 250kHz
            beanList.add(String.valueOf(inBodyData.getTr0()));//TR 5kHz
            beanList.add(String.valueOf(inBodyData.getTr1()));//TR 50kHz
            beanList.add(String.valueOf(inBodyData.getTr2()));//TR 250kHz
            beanList.add(String.valueOf(inBodyData.getRl0()));//RL 5kHz
            beanList.add(String.valueOf(inBodyData.getRl1()));//RL 50kHz
            beanList.add(String.valueOf(inBodyData.getRl2()));//RL 250kHz
            beanList.add(String.valueOf(inBodyData.getLl0()));//LL 5kHz
            beanList.add(String.valueOf(inBodyData.getLl1()));//LL 50kHz
            beanList.add(String.valueOf(inBodyData.getLl2()));//LL 250kH
            //浮肿分析值 edemaValue
            beanList.add(String.valueOf(inBodyData.getEdemavalue()));//浮肿分析
            // 健康诊断 浮肿 healthEdema
            beanList.add(inBodyData.getEdema());// 浮肿
            //肌肉评估上肢 muscleUp 肌肉评估下肢 muscleDown;
            beanList.add(inBodyData.getUpbalanced()); //上肢均衡
            beanList.add(inBodyData.getDownbalanced());//下肢均衡
            //体型判断 shapeJudgment;
            beanList.add(inBodyData.getShape());
            exprList.add(beanList);
            if (!ExcelUtils.writeObjListToExcel(exprList, path, this)) {
                exportDialog.dismiss();
                expDialog.setDialog("导出失败！", true, false, null).show();
                return;
            }
        }
//                        if (ExcelUtils.writeObjListToExcel(exprList, getSDPath() + "/Test/test.xls", this)) {
//                            expDialog.setDialog("数据已导出，存储在文件夹Bg\\Test中！", true, false, null).show();
//                        } else {
//                            expDialog.setDialog("导出失败！", true, false, null).show();
//                        }
        handler.sendEmptyMessageDelayed(2, 300);
        showCache();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case Constant.EXPORT_REQ:
                    Permission();
                    break;
            }
        } else {
            showToast("请在应用管理中打开访问权限！");
        }
    }

    private void showCache() {
        file = new File(getSDPath() + "/Test");
        if (!file.exists()) {
            file.mkdir();
        }
        double blockSize = getFolderSize(file);
        if (blockSize != 0) {
            clearcache.setVisibility(View.VISIBLE);
            clearcache.setText("清除缓存" + getFormatSize(blockSize));
        } else {
            clearcache.setVisibility(View.GONE);
        }

    }

    private String filePath(String name) {
        File filePath;
        String result;
        int i = 0;
        do {
            result = name + i + ".xls";
            filePath = new File(file.getPath() + "/" + result);
            i += 1;
        } while (filePath.exists());
        return file.getPath() + "/" + result;
    }

}
