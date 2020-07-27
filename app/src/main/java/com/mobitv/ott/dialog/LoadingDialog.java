package com.mobitv.ott.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.mobitv.ott.R;

/**
 * Created by Son on 3/15/2015.
 */
public class LoadingDialog implements View.OnClickListener {
    private Context mContext;
    private Dialog saveDialog;

    public LoadingDialog(Context context) {
        mContext = context;
        init();
    }

    private void init() {
        final View contentView = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.dialog_loading, null);
        saveDialog = new Dialog(mContext);
        saveDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        saveDialog.setContentView(contentView);
        saveDialog.setCanceledOnTouchOutside(false);
        saveDialog.setCancelable(false);
        saveDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOk:
                closeWindow();
                break;
        }
    }

    public void showWindow() {
        saveDialog.show();
    }

    public void closeWindow() {
        if (saveDialog != null) {
            saveDialog.dismiss();
        }
    }
}
