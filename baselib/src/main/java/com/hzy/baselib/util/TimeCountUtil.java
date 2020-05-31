package com.hzy.baselib.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.CountDownTimer;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.hzy.baselib.R;


/**
 * 倒计时获取验证码
 */
public class TimeCountUtil extends CountDownTimer {
    private Activity mActivity;
    private TextView textView;//按钮
    private int color;

    // 在这个构造方法里需要传入三个参数，一个是Activity，一个是总的时间millisInFuture，一个是countDownInterval，然后就是你在哪个按钮上做这个是，就把这个按钮传过来就可以了
    public TimeCountUtil(Activity mActivity,
                         long millisInFuture, long countDownInterval, TextView textView ) {
        super(millisInFuture, countDownInterval);
        this.mActivity = mActivity;
        this.textView = textView;

    }

    @SuppressLint("NewApi")
    @Override
    public void onTick(long millisUntilFinished) {
        textView.setClickable(false);//设置不能点击
        textView.setText(millisUntilFinished / 1000 +"s 后重发");//设置倒计时时间
    }

    @SuppressLint("NewApi")
    @Override
    public void onFinish() {
        textView.setText(mActivity.getResources().getString(R.string.re_send));
        textView.setClickable(true);//重新获得点击
        textView.setTextColor(ContextCompat.getColor(mActivity,R.color.black_text));//还原字体颜色
    }

}