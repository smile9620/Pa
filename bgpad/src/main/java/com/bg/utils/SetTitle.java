package com.bg.utils;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bg.bgpad.R;

/**
 * Created by Administrator on 2017-01-10.
 */

public class SetTitle implements View.OnClickListener {
    private ImageButton left;
    private ImageButton right;
    private OnTitleBtClickListener onTitleBtClickListener;

    public SetTitle(OnTitleBtClickListener onTitleBtClickListener, View view, boolean[] btShow,
                    String titlestr, int[] btBg) {
        this.onTitleBtClickListener = onTitleBtClickListener;


        TextView title = (TextView) view.findViewById(R.id.title_bar);
        title.setText(titlestr);
        left = (ImageButton) view.findViewById(R.id.left);
        right = (ImageButton) view.findViewById(R.id.right);

        if (btShow[0]) {
          left.setBackgroundResource(btBg[0]);
//            left.setImageResource(btBg[0]);
        } else {
            left.setVisibility(View.GONE);
        }
        if (btShow[1]) {
          right.setBackgroundResource(btBg[1]);
//            right.setImageResource(btBg[1]);
        } else {
            right.setVisibility(View.GONE);
        }
        left.setOnClickListener(this);
        right.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left:
                onTitleBtClickListener.leftBt(left);
                break;
            case R.id.right:
                onTitleBtClickListener.rightBt(right);
                break;
        }
    }

    public interface OnTitleBtClickListener {
        public void leftBt(ImageButton left);

        public void rightBt(ImageButton right);
    }
}
