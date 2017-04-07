package com.bg.bgpad;

import android.os.Bundle;
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

import com.bg.utils.MyDialog;
import com.bg.utils.SelectionUser;
import com.bg.utils.SetTitle;

import java.util.ArrayList;
import java.util.HashMap;
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
    private SimpleAdapter simpleAdapter;
    private int[] colors = new int[]{0xFFFFFFFF, 0xFFEFEFEF};
    private List<Map<String, String>> list_maps = new ArrayList<>();
    private Map<Integer, Boolean> map = new HashMap<>();// 存放已被选中的CheckBox

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);
        ButterKnife.bind(this);
        new SetTitle(this, view, new boolean[]{true, false},
                "用户管理", new int[]{R.drawable.back_bt, R.drawable.ble_bt});
        new SelectionUser(this, this, selection, false);

        adddata();
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
                            map.put(pos, true);
                        } else {
                            map.remove(pos);
                        }
                    }
                });
                if (map != null && map.containsKey(position)) {
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
                        map.put(i, true);
                    }
                } else {
                    map.clear();
                }
                simpleAdapter.notifyDataSetChanged();
            }
        });
    }

    private void adddata() {
        for (int i = 0; i < 3; i++) {
            Map<String, String> map = new HashMap<>();
            map.put("user_number", "编号" + i);
            map.put("user_name", "姓名" + i);
            map.put("strDate", "测试日期" + i);
            list_maps.add(map);
        }
    }

    @Override
    public void onSearch(String type, String selectStr) {
        showToast(selectStr);
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
        CheckBox box = (CheckBox) view.findViewById(R.id.checkBox);
        box.setChecked(!box.isChecked());
    }

    @OnClick({R.id.export, R.id.delete})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.export:
                new MyDialog(this).setDialog("数据正在导出中，请稍后...", false,false,null).show();
                break;
            case R.id.delete:
                new MyDialog(this).setDialog( "确定彻底删除所选选项？", true,true, new MyDialog.DialogConfirm() {
                    @Override
                    public void dialogConfirm() {
                        showToast("我知道了！");
                    }
                }).show();
                break;
        }
    }
}
