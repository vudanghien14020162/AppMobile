package com.mobitv.ott.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.mobitv.ott.R;

/**
 * Created by Son on 3/15/2015.
 */
public class ScardDialog implements View.OnClickListener {
    public interface OnButtonClickListener{
        void onAgree();
        void onCancel();
    }
    private Context mContext;
    private Dialog dialog;
    private TextView tvTitleDialog;
    private TextView tvMessageDialog;
    private OnButtonClickListener listener;

    public ScardDialog(Context context) {
        mContext = context;
        init();
    }

    private void init() {
        final View contentView = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.dialog_scard_delete_option, null);
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(contentView);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        if(dialog.getWindow() != null) {
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        View btnOk = contentView.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(this);
        View btnCancel = contentView.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);
        tvTitleDialog = contentView.findViewById(R.id.tvTitleDialog);
        tvMessageDialog = contentView.findViewById(R.id.tvMessageDialog);
    }

    public void setCanceledOnTouchOutside(boolean cancel){
        dialog.setCanceledOnTouchOutside(cancel);
        dialog.setCancelable(cancel);
    }

    public void setTitleDialog(String title) {
        tvTitleDialog.setText(title);
    }

    public void setMessageDialog(String message) {
        tvMessageDialog.setText(message);
    }

    public void setListener(OnButtonClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOk:
                closeWindow();
                if(listener != null){
                    listener.onAgree();
                }
                break;
            case R.id.btnCancel:
                closeWindow();
                if(listener != null){
                    listener.onCancel();
                }
                break;
        }
    }

    public void showWindow(boolean isHideTitle) {
        if(isHideTitle){
            tvTitleDialog.setVisibility(View.GONE);
        }else{
            tvTitleDialog.setVisibility(View.VISIBLE);
        }
        dialog.show();
    }

    private void closeWindow() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
