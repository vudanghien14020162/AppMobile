package com.mobitv.ott.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.*;
import android.widget.TextView;
import com.mobitv.ott.R;

/**
 * Created by Son on 3/15/2015.
 */
public class NotificationDialog implements View.OnClickListener {
    private Context mContext;
    private Dialog dialog;
    private TextView tvTitleDialog;
    private TextView tvMessageDialog;

    public NotificationDialog(Context context) {
        mContext = context;
        init();
    }

    private void init() {
        final View contentView = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.dialog_notification, null);
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
        tvTitleDialog = contentView.findViewById(R.id.tvTitleDialog);
        tvMessageDialog = contentView.findViewById(R.id.tvMessageDialog);
    }

    public void setTitleDialog(String title) {
        tvTitleDialog.setText(title);
    }

    public void setMessageDialog(String message) {
        tvMessageDialog.setText(message);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOk:
                closeWindow();
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
