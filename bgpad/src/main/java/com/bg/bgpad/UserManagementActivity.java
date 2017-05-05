package com.bg.bgpad;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bg.model.InBodyData;
import com.bg.model.User;
import com.bg.utils.ExcelUtils;
import com.bg.utils.MyDialog;
import com.bg.utils.SelectionUser;
import com.bg.utils.SetTitle;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.lang.annotation.ElementType;
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
    @BindView(R.id.total)
    TextView total;

    private SimpleAdapter simpleAdapter;
    private int[] colors = new int[]{0xFFFFFFFF, 0xFFEFEFEF};
    private ArrayList<Map<String, String>> list_maps = new ArrayList<>();
    private Map<Integer, Boolean> checkmap = new HashMap<>();// 存放已被选中的CheckBox
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    total.setText("共 " + msg.arg1 + " 条数据");
                    break;
            }
            simpleAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);
        ButterKnife.bind(this);
        new SetTitle(this, view, new boolean[]{true, false},
                "用户管理", new int[]{R.drawable.back_bt, R.drawable.ble_bt});
        new SelectionUser(this, this, selection, false);

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
        getData();
    }

    private void getData() {
        List<InBodyData> inbodylist = DataSupport.order("testDate desc").limit(15).find(InBodyData.class);
        List<Map<String, String>> datalist = new ArrayList<Map<String, String>>();
        for (int i = 0; i < inbodylist.size(); i++) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("data_id", inbodylist.get(i).getId()+"");
            map.put("user_number", inbodylist.get(i).getUser_number());
            map.put("user_name", inbodylist.get(i).getUser().getUser_name());
            map.put("sex", inbodylist.get(i).getUser().getSex() == 0 ? "男" : "女");
            map.put("strDate", inbodylist.get(i).getStrDate());
            datalist.add(map);
        }
        total.setText("前 " + datalist.size() + " 条数据");
        addData(datalist);
    }

    private void addData(List<Map<String, String>> list) {
        list_maps.clear();
        if (list.size() != 0) {
            datatip.setVisibility(View.GONE);
            for (int i = 0; i < list.size(); i++) {
                Map<String, String> map = new HashMap<>();
                map.put("data_id", list.get(i).get("data_id"));
                map.put("user_number", list.get(i).get("user_number"));
                map.put("user_name", list.get(i).get("user_name"));
                map.put("sex", list.get(i).get("sex"));
                map.put("strDate", list.get(i).get("strDate"));
                list_maps.add(map);
            }
        } else {
            datatip.setVisibility(View.VISIBLE);
        }
        handler.sendEmptyMessage(0);
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
        Intent intent = new Intent(this,TestReportActivity.class);
        String data_id = list_maps.get(position).get("data_id");
        intent.putExtra("data_id",data_id);
        startActivity(intent);
    }

    @OnClick({R.id.export, R.id.delete})
    public void onClick(View view) {
        if (list_maps.size() != 0) {
            if (checkmap.size() != 0) {
                final List<Integer> index = new ArrayList<Integer>();
                for (Integer key : checkmap.keySet()) {
                    index.add(key);
                }
                Collections.sort(index);    //升序排列;
                switch (view.getId()) {
                    case R.id.export:
                        ArrayList<ArrayList<String>> exprList = new ArrayList<ArrayList<String>>();
                        MyDialog expDialog = new MyDialog(this);
                        Dialog exportDialog = expDialog.setDialog("数据正在导出中，请稍后...", false, false, null);
                        exportDialog.show();
                        for (int i = index.size() - 1; 0 <= i; i--) {
                            Map<String, String> map = list_maps.get(index.get(i));
                            ArrayList<String> beanList = new ArrayList<String>();
                            String data_id = map.get("data_id");
                            List<InBodyData> inbodyList = DataSupport.where("id = ? ", data_id).find(InBodyData.class);
                            User user = inbodyList.get(0).getUser();
                            for (int j = 0; j < inbodyList.size(); j++) {
                                beanList.add(user.getUser_number());
                                beanList.add(user.getUser_name());
                                beanList.add(user.getSex() == 0 ? "男" : "女");
                                beanList.add(user.getBirthday());
                                beanList.add(user.getAge() + "");
                                beanList.add(inbodyList.get(j).getHeight() + "");
                                beanList.add(inbodyList.get(j).getStrDate());
                            }
                            exprList.add(beanList);
                        }
                        File file = new File(getSDPath() + "/Test");
                        makeDir(file);
                        ExcelUtils.initExcel(file.toString() + "/test.xls", new String[]{"编号", "姓名", "性别", "出生日期", "年龄", "身高", "测试日期"});
                        if (ExcelUtils.writeObjListToExcel(exprList, getSDPath()
                                + "/Test/test.xls", this)) {
                            expDialog.setDialog("数据已导出，存储在文件夹Bg\\Test中！", true, false, null).show();
                        } else {
                            expDialog.setDialog("导出失败！", true, false, null).show();
                        }
                        exportDialog.dismiss();
                        break;
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
                                message.what = 1;
                                message.arg1 = list_maps.size();
                                handler.sendMessage(message);
                                delDialog.setDialog("数据已删除！", true, false, null).show();
                            }
                        }).show();
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
