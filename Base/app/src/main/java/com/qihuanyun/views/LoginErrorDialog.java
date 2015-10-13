package com.qihuanyun.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qihuanyun.R;

public class LoginErrorDialog {

    public interface PolesOnClickListener{
        void onPositiveClickListener(Dialog mDialog);
        void onNegativeClickListener(Dialog mDialog);
    }

    public interface SingleOnClickListener{
        void onSingleClickListener(Dialog mDialog);
    }

    /**
     * @param context 上下文
     * @param content   Dialog 的内容
     * @param leftButtonName   左边按钮的文字
     * @param rightButtonName  右边按钮的文字
     * @param listener       回调接口
     * @return Dialog
     */
    public static Dialog CreatePolesDialog(Context context, String content,String leftButtonName,String rightButtonName,final PolesOnClickListener listener) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.poles_dialog, null);
        TextView mTextViewRight = (TextView) v.findViewById(R.id.tv_right);
        mTextViewRight.setText(rightButtonName);
        TextView mTextViewLeft = (TextView) v.findViewById(R.id.tv_left);
        mTextViewLeft.setText(leftButtonName);
        TextView mTextViewContent = (TextView) v.findViewById(R.id.tv_content);
        mTextViewContent.setText(content);
        final Dialog dialog = new Dialog(context,com.wzl.dialoglib.R.style.yes_or_no_dialog);
        dialog.setCancelable(true);
        dialog.setContentView(v);
        ViewGroup.LayoutParams params = dialog.getWindow().getAttributes();
        @SuppressWarnings("deprecation")
        int width = (int) (((Activity) context).getWindowManager()
                .getDefaultDisplay().getWidth());
        params.width = (int) (width * 0.9);
        mTextViewRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onPositiveClickListener(dialog);
            }
        });
        mTextViewLeft.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onNegativeClickListener(dialog);
            }
        });
        return dialog;
    }

    /**
     * @param context 上下文
     * @param content   Dialog 的内容
     * @param buttonName   按钮文字
     * @param listener       回调接口
     * @return Dialog
     */
    public static Dialog CreateSingleDialog(Context context, String content,String buttonName,final SingleOnClickListener listener) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.single_dialog, null);
        TextView mTextView = (TextView) v.findViewById(R.id.tv);
        mTextView.setText(buttonName);
        TextView mTextViewContent = (TextView) v.findViewById(R.id.tv_content);
        mTextViewContent.setText(content);
        final Dialog dialog = new Dialog(context,com.wzl.dialoglib.R.style.yes_or_no_dialog);
        dialog.setCancelable(true);
        dialog.setContentView(v);
        ViewGroup.LayoutParams params = dialog.getWindow().getAttributes();
        @SuppressWarnings("deprecation")
        int width = (int) (((Activity) context).getWindowManager()
                .getDefaultDisplay().getWidth());
        params.width = (int) (width * 0.9);
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onSingleClickListener(dialog);
            }
        });
        return dialog;
    }
}
