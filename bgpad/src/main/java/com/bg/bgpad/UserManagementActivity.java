package com.bg.bgpad;
import android.os.Bundle;
import android.view.View;
import com.bg.utils.SelectionUser;

public class UserManagementActivity extends BaseActivity implements SelectionUser.SetOnSelectoinUser{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        View view = this.findViewById(R.id.selection);
        new SelectionUser(this,this,view,true);
    }

    @Override
    public void onSearch(String type,String selectStr) {
        showToast(selectStr);
    }

    @Override
    public void onAdd() {
        showToast("add");
    }
}
