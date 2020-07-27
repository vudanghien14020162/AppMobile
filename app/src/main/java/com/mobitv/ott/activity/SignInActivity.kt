package com.mobitv.ott.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.mobitv.ott.BuildConfig
import com.mobitv.ott.R
import com.mobitv.ott.dialog.LoadingDialog
import com.mobitv.ott.dialog.MyAlertDialog
import com.mobitv.ott.dialog.NotificationDialog
import com.mobitv.ott.other.Const
import com.mobitv.ott.other.KeyboardHeightObserver
import com.mobitv.ott.other.KeyboardHeightProvider
import com.mobitv.ott.other.MyPreferenceManager
import com.mobitv.ott.pojo.*
import kotlinx.android.synthetic.main.activity_signin.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInActivity : BaseActivity(), TextWatcher, KeyboardHeightObserver,
    View.OnFocusChangeListener {

    private var notificationDialog: NotificationDialog? = null
    private var keyboardHeightProvider: KeyboardHeightProvider? = null
    private var originYContent = 0f
    private var myPreferenceManager=MyPreferenceManager.getInstance(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarTransparent()
        setContentView(R.layout.activity_signin)
        btnLogin.setOnClickListener(this)
        btnIgnore.setOnClickListener(this)
        edtPhoneSignIn.addTextChangedListener(this)
        edtPasswordSignIn.addTextChangedListener(this)
        btnClearPhoneSignIn.setOnClickListener(this)
        btnClearPasswordSignIn.setOnClickListener(this)
        btnSignUp.setOnClickListener(this)
        btnForgetPassword.setOnClickListener(this)

        notificationDialog = NotificationDialog(this)
        keyboardHeightProvider = KeyboardHeightProvider(this)
        layoutContentSignIn.post {
            originYContent = layoutContentSignIn.y
            keyboardHeightProvider?.start()
        }

        edtPhoneSignIn.onFocusChangeListener = this
        edtPasswordSignIn.onFocusChangeListener = this

    }

    override fun onKeyboardHeightChanged(height: Int, orientation: Int) {
        val containerHeight = layoutContainerSignIn.height
        val topMarginContent = resources.getDimension(R.dimen.margin_top_status_bar)
        val contentHeight = layoutContentSignIn.height
        val bottomMarginContent = containerHeight - topMarginContent - contentHeight
        if (height <= 0) {
            layoutContentSignIn.animate().y(originYContent)
        } else {
            val offset = containerHeight - keyboardHeightProvider!!.screenHeight
            val delta = height + offset - bottomMarginContent + 5 //5 is padding
            if (delta > 0)
                layoutContentSignIn.animate().y(originYContent - delta)
        }
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        (v?.parent as View).isSelected = hasFocus
        if (v.id == edtPhoneSignIn.id) btnClearPhoneSignIn.visibility =
            if (!edtPhoneSignIn.text.isNullOrEmpty() && hasFocus) View.VISIBLE else View.INVISIBLE
        if (v.id == edtPasswordSignIn.id) btnClearPasswordSignIn.visibility =
            if (!edtPasswordSignIn.text.isNullOrEmpty() && hasFocus) View.VISIBLE else View.INVISIBLE
    }

    override fun afterTextChanged(s: Editable?) {
        if (edtPhoneSignIn.length() == 11) {
            edtPasswordSignIn.requestFocus()
        }
        btnLogin.isEnabled =
            !edtPhoneSignIn.text.isNullOrEmpty() && !edtPasswordSignIn.text.isNullOrEmpty()
        btnClearPhoneSignIn.visibility =
            if (edtPhoneSignIn.text.isNullOrEmpty() || !edtPhoneSignIn.isFocused) View.INVISIBLE else View.VISIBLE
        btnClearPasswordSignIn.visibility =
            if (edtPasswordSignIn.text.isNullOrEmpty() || !edtPasswordSignIn.isFocused) View.INVISIBLE else View.VISIBLE
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        //do thing
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        //do nothing
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnIgnore -> {
                goMainScreen()
            }
            R.id.btnLogin -> {
                doLogIn()
            }
            R.id.btnClearPhoneSignIn -> {
                edtPhoneSignIn.setText("")
            }
            R.id.btnClearPasswordSignIn -> {
                edtPasswordSignIn.setText("")
            }
            R.id.btnSignUp -> {
                goSignUp()
            }
            R.id.btnForgetPassword -> {
                goForgetPassword()
            }
        }
    }

    @SuppressLint("HardwareIds")
    private fun doLogIn() {
        val password = edtPasswordSignIn.text.toString()
        val os = "android"
        val googleAppId = ""
        val hdmi = 1
        val tel = edtPhoneSignIn.text.toString()
        val appVersion = BuildConfig.VERSION_NAME
        val nType = 1
        val deviceId = Settings.Secure.getString(
            applicationContext.contentResolver,
            Settings.Secure.ANDROID_ID
        )

        val myPreferenceManager = MyPreferenceManager.getInstance(this)
        myPreferenceManager?.setValue(Const.PHONE_NUMBER, tel)
        val loadingDialog = LoadingDialog(this)
        loadingDialog.showWindow()
        val apiInterface = APIClient.getInstance(this).client.create(APIInterface::class.java)
        val call =
            apiInterface?.doLogIn(
                LogInParams(
                    password,
                    os,
                    googleAppId,
                    hdmi,
                    tel,
                    appVersion,
                    nType,
                    deviceId
                )
            )
        call?.enqueue(object : Callback<LogInResponse> {
            override fun onResponse(
                call: Call<LogInResponse>,
                response: Response<LogInResponse>
            ) {
                loadingDialog.closeWindow()
                val responseObject = response.body()

                if (responseObject!!.statusCode == Const.STATUS_CODE_SUCCESS) {
                    Log.d("LOG TAG USER" , responseObject.toString())
                    myPreferenceManager?.isLogIn = true
                    myPreferenceManager?.setValue(
                        Const.AUTH,
                        responseObject!!.responseObject.authKey
                    )
                    myPreferenceManager?.setValue(Const.USER_NAME, tel)
                    myPreferenceManager?.setValue(Const.USER_ID, tel)
                    val intent = Intent(this@SignInActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                } else {
                    if (Const.DEBUG) {
                        Log.d("RESPONSE", responseObject.toString())
                    }
                    notificationDialog?.setMessageDialog(responseObject!!.extraData)
                    notificationDialog?.showWindow(false)
                }
            }

            override fun onFailure(call: Call<LogInResponse>, t: Throwable) {
                call.cancel()
                loadingDialog.closeWindow()
                notificationDialog?.setMessageDialog(getString(R.string.common_message_error))
                notificationDialog?.showWindow(false)
            }
        })

    }

    private fun goMainScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun goSignUp() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun goForgetPassword() {
        val msg = "    Để thay đổi Mật khẩu truy cập\n" +
                "    Vui lòng gọi hotline: 19001900 (1000đ/phút)"
        MyAlertDialog(this, "", msg, "", "Đóng", true, null, null).show()
//        val intent = Intent(this, ForgetPasswordActivity::class.java)
//        startActivity(intent)
//        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    override fun onResume() {
        super.onResume()
        keyboardHeightProvider?.setKeyboardHeightObserver(this)
        val phoneNumber = MyPreferenceManager.getInstance(this).getValue(Const.PHONE_NUMBER, "")
        if (phoneNumber.isNotEmpty()) {
            edtPhoneSignIn.setText(phoneNumber)
            edtPasswordSignIn.requestFocus()
        }
    }

    override fun onPause() {
        super.onPause()
        keyboardHeightProvider?.setKeyboardHeightObserver(null)
    }

    override fun onDestroy() {
        keyboardHeightProvider?.close()
        super.onDestroy()
    }


}
