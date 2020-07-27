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
public class ContinueDialog implements View.OnClickListener {
    public interface ButtonClickListener{
        void onButtonClick(boolean isContinue);
    }
    private Context mContext;
    private Dialog saveDialog;
    private TextView tvMessageDialog;
    private ButtonClickListener listener;

    public ContinueDialog(Context context) {
        mContext = context;
        init();
    }

    private void init() {
        final View contentView = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.dialog_continue_watching, null);
        saveDialog = new Dialog(mContext);
        saveDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        saveDialog.setContentView(contentView);
        saveDialog.setCanceledOnTouchOutside(true);
        saveDialog.setCancelable(true);
        if(saveDialog.getWindow() != null) {
            saveDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            saveDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        View btnWatching = contentView.findViewById(R.id.btnWatching);
        btnWatching.setOnClickListener(this);
        View btnCancel = contentView.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);
        tvMessageDialog = contentView.findViewById(R.id.tvMessageDialog);
    }

    public void setListener(ButtonClickListener listener) {
        this.listener = listener;
    }

    public void setMessageDialog(String message) {
        tvMessageDialog.setText(message);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnWatching:
                closeWindow();
                if(listener != null){
                    listener.onButtonClick(true);
                }
                break;
            case R.id.btnCancel:
                closeWindow();
                if(listener != null){
                    listener.onButtonClick(false);
                }
                break;
        }
    }

    public void showWindow() {
        if (saveDialog != null) {
            saveDialog.show();
        }
    }

    private void closeWindow() {
        if (saveDialog != null) {
            saveDialog.dismiss();
        }
    }
}
