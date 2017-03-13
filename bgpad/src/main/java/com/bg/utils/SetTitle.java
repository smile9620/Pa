package com.bg.utils;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bg.bgpad.R;

/**
 * Created by Administrator on 2017-01-10.
 */

public class SetTitle {
    private Button left;
    private Button right;
    protected OnTitleBtClickListener onTitleBtClickListener;

    public SetTitle(OnTitleBtClickListener onTitleBtClickListener){
        this.onTitleBtClickListener = onTitleBtClickListener;
    }
    public void setTitleBar(View view, boolean[] btShow,
                                String titlestr, int[] btBg) {

        TextView title = (TextView) view.findViewById(R.id.title_bar);
        title.setText(titlestr);

         left= (Button) view.findViewById(R.id.left);
         right = (Button) view.findViewById(R.id.right);
        if (btShow[0]) {
            left.setVisibility(View.GONE);
        } else {
            left.setBackgroundResource(btBg[0]);
            left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onTitleBtClickListener.leftBt(left);
                }
            });

        }
        if (btShow[1]) {
            right.setVisibility(View.GONE);
        } else {
            right.setBackgroundResource(btBg[1]);

            right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onTitleBtClickListener.rightBt(right);
                }
            });
        }

    }

   public interface OnTitleBtClickListener{
       public void leftBt(Button left);
       public void rightBt(Button right);
   }
}
