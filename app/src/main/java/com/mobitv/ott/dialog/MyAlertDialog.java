package com.mobitv.ott.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.fonts.Font;
import android.os.Message;
import android.text.Html;

import androidx.appcompat.app.AlertDialog;

import com.mobitv.ott.activity.SignInActivity;
import com.mobitv.ott.other.MyPreferenceManager;
import org.w3c.dom.Document;

import java.awt.font.TextAttribute;
import java.text.AttributedString;

public class MyAlertDialog {
    private Context context;
    private String title;
    private String msg;
    private String leftBtn;
    private String rightBtn;
    private boolean cancelable;
    private AlertDialog.OnClickListener leftListener, rightListener;
    private AlertDialog alertDialog;

    public MyAlertDialog(Context context, String title, String msg, String leftBtn, String rightBtn, boolean cancelable, AlertDialog.OnClickListener leftListener, AlertDialog.OnClickListener rightListener) {
        this.context = context;
        this.title = title;
        this.msg = msg;
        this.leftBtn = leftBtn;
        this.rightBtn = rightBtn;
        this.cancelable = cancelable;
        this.leftListener = leftListener;
        this.rightListener = rightListener;
        init();
    }

    private void init() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(cancelable);
        if (isStringEmpty(msg)) {
            builder.setMessage(msg);
        }
        if (isStringEmpty(title)) {
            builder.setTitle(title);
        }
        if (isStringEmpty(leftBtn)) {
            builder.setNegativeButton(leftBtn, leftListener);
        }
        if (isStringEmpty(rightBtn)) {
            builder.setPositiveButton(rightBtn, rightListener);
        }
        alertDialog = builder.create();

    }

    public void show() {
        if (alertDialog == null) {
            init();
        }
        alertDialog.show();
    }

    public void hide() {
        if (alertDialog != null) {
            alertDialog.cancel();
        }
    }

    private boolean isStringEmpty(String s) {
        return (s != null && !s.equals(""));
    }

    public static void showDialogCanWatch(Context mContext) {
        MyPreferenceManager myPreferenceManager = MyPreferenceManager.getInstance(mContext);
        String msg = "Vui lòng Đăng nhập để xem nội dung này.";
        String leftBtn = "Đăng nhập";
        String righttBtn = "Huỷ";


        if (myPreferenceManager.isLogIn()) {
//            msg = "Tài khoản cần gia hạn để xem được nội dung này. \n" +
//                    "Chi tiết gọi 19001900 (1000đ/phút)";
//            System.out.println("This is [?1] green [?2]");
//            Font f = new Font("LucidaSans", Font.BOLD, 14);
//            AttributedString as= new AttributedString("Example text string");
//            as.addAttribute(TextAttribute.FONT, f);

//            msg = "<b>Bước 1:</b> Vào mục <b>Cá nhân</b>. Chọn <b>biểu tượng bánh xe</b> góc bên phải phía trên màn hình. \n" +
//                    "<b>Bước 2:</b> Cập nhật số Thẻ đầu thu DTT/DTH của bạn. \n" +
//                    "<b>Bước 3:</b> Kiểm tra để đảm bảo còn thời hạn sử dụng. \n\n" +
//                    "     Để gia hạn thời hạn sử dụng xem hướng dẫn tại http://gh.avg.vn. \n" +
//                    "           Hoặc gọi 19001900(1000đ/phút) để được hỗ trợ.";
//            message.set(msg, "text/html; charset=utf-8");
            msg = "Quý khách chưa xem được nội dung này! \n\n" +
                    "Đối với khách hàng đang sử dụng dịch vụ DTH, DTT của AVG, vui lòng nhập số thẻ tại mục “Cá Nhân” -> “Thiết lập” -> “Nhập số thẻ đầu thu” để đăng ký xem. \n\n" +
                    "Đối với các thuê bao khác vui lòng liên hệ 1900.1900 (1.000đ/phút) hoặc truy cập website avg.vn để được hỗ trợ. ";
            leftBtn = "";
            righttBtn = "Đóng";
        }

        MyAlertDialog myAlertDialog = new MyAlertDialog(mContext, "", msg , leftBtn, righttBtn, true, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mContext.startActivity(new Intent(mContext, SignInActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        }, null);
        myAlertDialog.show();
    }
}
