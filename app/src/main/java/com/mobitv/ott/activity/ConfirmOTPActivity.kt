package com.mobitv.ott.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.mobitv.ott.BuildConfig
import com.mobitv.ott.R
import com.mobitv.ott.dialog.LoadingDialog
import com.mobitv.ott.dialog.NotificationDialog
import com.mobitv.ott.other.*
import com.mobitv.ott.pojo.*
import kotlinx.android.synthetic.main.activity_confirm_otp.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class ConfirmOTPActivity : BaseActivity(), TextWatcher, KeyboardHeightObserver, View.OnFocusChangeListener {

    private var myPreferenceManager: MyPreferenceManager? = null
    private var apiInterface: APIInterface? = null
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var phoneNumber: String
    private lateinit var timeout: String
    private lateinit var type: String
    private var countDownTimer: CountDownTimer? = null
    private var notificationDialog: NotificationDialog? = null
    private var keyboardHeightProvider: KeyboardHeightProvider? = null
    private var originYContent = 0f
    private var isAlwaysDisable = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarTransparent()
        setContentView(R.layout.activity_confirm_otp)
        btnBackConfirmOTP.setOnClickListener(this)
        btnConfirmOTP.setOnClickListener(this)
        edtOTP.addTextChangedListener(this)
        btnResendOTP.setOnClickListener(this)
        myPreferenceManager = MyPreferenceManager.getInstance(this)
        loadingDialog = LoadingDialog(this)

        phoneNumber = intent.getStringExtra(Const.USER_NAME)
        timeout = intent.getStringExtra(Const.TIMEOUT)
        type = intent.getStringExtra(Const.TYPE)
        val part1 = phoneNumber.substring(0, 3)
        val part2 = phoneNumber.substring(7, phoneNumber.length)
        val title = "Mã xác nhận đã gửi về số điện thoại $part1****$part2"
        tvConfirmOTPTitle.text = title
        notificationDialog = NotificationDialog(this)
        keyboardHeightProvider = KeyboardHeightProvider(this)
        layoutContentConfirmOtp.post {
            originYContent = layoutContentConfirmOtp.y
            keyboardHeightProvider?.start()
        }
        startCountDownExpireOTP(false)
        edtOTP.onFocusChangeListener = this
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        (v?.parent as View).isSelected = hasFocus
    }

    override fun onKeyboardHeightChanged(height: Int, orientation: Int) {
        val containerHeight = layoutContainerConfirmOtp.height
        val topMarginContent = resources.getDimension(R.dimen.margin_top_status_bar)
        val contentHeight = layoutContentConfirmOtp.height
        val bottomMarginContent = containerHeight - topMarginContent - contentHeight
        if (height <= 0) {
            layoutContentConfirmOtp.animate().y(originYContent)
        } else {
            val offset = containerHeight - keyboardHeightProvider!!.screenHeight
            val delta = height + offset - bottomMarginContent + 5 //5 is padding
            if (delta > 0)
                layoutContentConfirmOtp.animate().y(originYContent - delta)
        }
    }

    override fun afterTextChanged(s: Editable?) {
        btnConfirmOTP.isEnabled  = !edtOTP.text.isNullOrEmpty() && !isAlwaysDisable
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        //do thing
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        //do nothing
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnConfirmOTP -> {
                if (type == Const.REGISTER) {
                    doConfirmOTPSignUp()
                } else if (type == Const.UPDATE_PASSWORD) {
                    doConfirmOTPNewPass()
                }
            }
            R.id.btnBackConfirmOTP -> {
                finish()
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            }
            R.id.btnResendOTP -> {
                if (type == Const.REGISTER) {
                    doResendOTPSignUp()
                } else if (type == Const.UPDATE_PASSWORD) {
                    doResendOTPNewPass()
                }
            }
        }
    }

    @SuppressLint("HardwareIds")
    private fun doConfirmOTPSignUp() {
        val code = edtOTP.text.toString()
        val os = "android"
        val googleAppId = ""
        val hdmi = 1
        val tel = phoneNumber
        val appVersion = BuildConfig.VERSION_NAME
        val nType = 1
        val deviceId = Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)

        loadingDialog.showWindow()
        apiInterface = APIClient.getInstance(this).client.create(APIInterface::class.java)
        val call =
            apiInterface?.doConfirmOTPSingUp(
                ConfirmOtpSignUpParams(
                    code,
                    os,
                    googleAppId,
                    hdmi,
                    tel,
                    appVersion,
                    nType,
                    deviceId
                )
            )
        call?.enqueue(object : Callback<ConfirmOtpSignUpResponse> {
            override fun onResponse(
                call: Call<ConfirmOtpSignUpResponse>,
                response: Response<ConfirmOtpSignUpResponse>
            ) {
                loadingDialog.closeWindow()
                val responseObject = response.body()

                if (responseObject!!.statusCode == Const.STATUS_CODE_SUCCESS) {
                    myPreferenceManager?.isLogIn = true
                    myPreferenceManager?.setValue(Const.AUTH, responseObject!!.responseObject.authKey)
                    notificationDialog?.setMessageDialog("Bạn đã tạo tài khoản AVG thành công. Đăng nhập để thưởng thức trọn bộ video chất lượng.")
                    notificationDialog?.showWindow(false)
                    Handler().postDelayed({
                        val intent = Intent(this@ConfirmOTPActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    }, 1000)
                } else {
                    if (Const.DEBUG) {
                        Log.d("RESPONSE", responseObject.toString())
                    }
                    notificationDialog?.setMessageDialog(responseObject!!.extraData)
                    notificationDialog?.showWindow(false)
                    if(responseObject!!.statusCode == Const.STATUS_CODE_ERROR_CONFIRM_OTP){
                        disableConfirm()
                    }
                }
            }

            override fun onFailure(call: Call<ConfirmOtpSignUpResponse>, t: Throwable) {
                call.cancel()
                loadingDialog.closeWindow()
                notificationDialog?.setMessageDialog(getString(R.string.common_message_error))
                notificationDialog?.showWindow(false)
            }
        })
    }

    private fun doResendOTPSignUp() {
        loadingDialog.showWindow()
        apiInterface = APIClient.getInstance(this).client.create(APIInterface::class.java)
        val call =
            apiInterface?.doResendOTPSignUp(ResendOTPSignUpParams(phoneNumber))
        call?.enqueue(object : Callback<ResendOtpSignUpResponse> {
            override fun onResponse(
                call: Call<ResendOtpSignUpResponse>,
                response: Response<ResendOtpSignUpResponse>
            ) {
                loadingDialog.closeWindow()
                val responseObject = response.body()

                if (responseObject!!.statusCode == Const.STATUS_CODE_SUCCESS) {
                    timeout = responseObject!!.resendResponseObject.timeout
                    notificationDialog?.setMessageDialog("Mã xác thực đã được gửi tới số điện thoại của bạn, vui lòng kiểm tra.")
                    notificationDialog?.showWindow(false)
                    startCountDownExpireOTP(true)
                    enableConfirm()
                } else {
                    if (Const.DEBUG) {
                        Log.d("RESPONSE", responseObject.toString())
                    }
                    notificationDialog?.setMessageDialog(responseObject!!.extraData)
                    notificationDialog?.showWindow(false)
                    if(responseObject!!.statusCode == Const.STATUS_CODE_ERROR_RESEND_OTP){
                        btnResendOTP.isEnabled = false
                    }
                }
            }

            override fun onFailure(call: Call<ResendOtpSignUpResponse>, t: Throwable) {
                call.cancel()
                loadingDialog.closeWindow()
                notificationDialog?.setMessageDialog(getString(R.string.common_message_error))
                notificationDialog?.showWindow(false)
            }
        })
    }

    private fun doConfirmOTPNewPass() {
        val code = edtOTP.text.toString()
        loadingDialog.showWindow()
        apiInterface = APIClient.getInstance(this).client.create(APIInterface::class.java)
        val call =
            apiInterface?.doConfirmOTPNewPass(
                ConfirmOtpNewPassParams(code, phoneNumber)
            )
        call?.enqueue(object : Callback<ConfirmOtpNewPassResponse> {
            override fun onResponse(
                call: Call<ConfirmOtpNewPassResponse>,
                response: Response<ConfirmOtpNewPassResponse>
            ) {
                loadingDialog.closeWindow()
                val responseObject = response.body()

                if (responseObject!!.statusCode == Const.STATUS_CODE_SUCCESS) {
                    val intent = Intent(this@ConfirmOTPActivity, NewPasswordActivity::class.java)
                    intent.putExtra(Const.USER_NAME, phoneNumber)
                    intent.putExtra(Const.CODE, code)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                } else {
                    if (Const.DEBUG) {
                        Log.d("RESPONSE", responseObject.toString())
                    }
                    notificationDialog?.setMessageDialog(responseObject!!.extraData)
                    notificationDialog?.showWindow(false)
                    if(responseObject!!.statusCode == Const.STATUS_CODE_ERROR_CONFIRM_OTP){
                        disableConfirm()
                    }
                }
            }

            override fun onFailure(call: Call<ConfirmOtpNewPassResponse>, t: Throwable) {
                call.cancel()
                loadingDialog.closeWindow()
                notificationDialog?.setMessageDialog(getString(R.string.common_message_error))
                notificationDialog?.showWindow(false)
            }
        })
    }

    private fun doResendOTPNewPass() {
        loadingDialog.showWindow()
        apiInterface = APIClient.getInstance(this).client.create(APIInterface::class.java)
        val call =
            apiInterface?.doResendOTPNewPass(ResendOTPNewPassParams(phoneNumber))
        call?.enqueue(object : Callback<ResendOtpNewPassResponse> {
            override fun onResponse(
                call: Call<ResendOtpNewPassResponse>,
                response: Response<ResendOtpNewPassResponse>
            ) {
                loadingDialog.closeWindow()
                val responseObject = response.body()

                if (responseObject!!.statusCode == Const.STATUS_CODE_SUCCESS) {
                    timeout = responseObject!!.responseObject.timeout
                    notificationDialog?.setMessageDialog("Mã xác thực đã được gửi tới số điện thoại của bạn, vui lòng kiểm tra.")
                    notificationDialog?.showWindow(false)
                    startCountDownExpireOTP(true)
                    enableConfirm()
                } else {
                    if (Const.DEBUG) {
                        Log.d("RESPONSE", responseObject!!.toString())
                    }
                    notificationDialog?.setMessageDialog(responseObject!!.extraData)
                    notificationDialog?.showWindow(false)
                    if(responseObject!!.statusCode == Const.STATUS_CODE_ERROR_RESEND_OTP){
                        btnResendOTP.isEnabled = false
                    }
                }
            }

            override fun onFailure(call: Call<ResendOtpNewPassResponse>, t: Throwable) {
                call.cancel()
                loadingDialog.closeWindow()
                notificationDialog?.setMessageDialog(getString(R.string.common_message_error))
                notificationDialog?.showWindow(false)
            }
        })
    }

    private fun startCountDownExpireOTP(isResent : Boolean) {
        countDownTimer?.cancel()
        val date = Date()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK)
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val currentDateTime = dateFormat.format(date)
        val expireTimeInSecond =
            (AppUtils.formatDateStringToTimeInMillis(timeout) - AppUtils.formatDateStringToTimeInMillis(currentDateTime)) / 1000
        var timeLeft = expireTimeInSecond.toString() + " " + getString(R.string.second)
        tvCountDownOTP.text = timeLeft
        countDownTimer = object : CountDownTimer(expireTimeInSecond * 1000, 1000) {
            override fun onFinish() {
                btnResendOTP.isEnabled = true
            }

            override fun onTick(millisUntilFinished: Long) {
                timeLeft = (millisUntilFinished / 1000).toString() + " " + getString(R.string.second)
                tvCountDownOTP?.text = timeLeft
                if (btnResendOTP.isEnabled && isResent)
                    btnResendOTP.isEnabled = false
            }
        }
        countDownTimer?.start()
    }


    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
    override fun onResume() {
        super.onResume()
        keyboardHeightProvider?.setKeyboardHeightObserver(this)
    }

    override fun onPause() {
        super.onPause()
        keyboardHeightProvider?.setKeyboardHeightObserver(null)
    }

    override fun onDestroy() {
        keyboardHeightProvider?.close()
        super.onDestroy()
    }

    private fun disableConfirm(){
        btnConfirmOTP.isEnabled = false
        isAlwaysDisable = true
    }
    private fun enableConfirm(){
        isAlwaysDisable = false
        btnConfirmOTP.isEnabled = !edtOTP.text.isNullOrEmpty() && !isAlwaysDisable
    }
}
