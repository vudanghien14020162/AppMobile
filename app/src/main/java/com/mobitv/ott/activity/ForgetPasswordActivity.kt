package com.mobitv.ott.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.mobitv.ott.R
import com.mobitv.ott.dialog.LoadingDialog
import com.mobitv.ott.dialog.NotificationDialog
import com.mobitv.ott.other.Const
import com.mobitv.ott.other.KeyboardHeightObserver
import com.mobitv.ott.other.KeyboardHeightProvider
import com.mobitv.ott.other.MyPreferenceManager
import com.mobitv.ott.pojo.APIClient
import com.mobitv.ott.pojo.APIInterface
import com.mobitv.ott.pojo.ResendOTPNewPassParams
import com.mobitv.ott.pojo.ResendOtpNewPassResponse
import kotlinx.android.synthetic.main.activity_forget_password.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ForgetPasswordActivity : BaseActivity(), TextWatcher, KeyboardHeightObserver, View.OnFocusChangeListener {
    private var apiInterface: APIInterface? = null
    private lateinit var loadingDialog: LoadingDialog
    private var notificationDialog: NotificationDialog? = null
    private var keyboardHeightProvider: KeyboardHeightProvider? = null
    private var originYContent = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarTransparent()
        setContentView(R.layout.activity_forget_password)
        btnConfirmForgetPassword.setOnClickListener(this)
        btnClearPhoneForgetPassword.setOnClickListener(this)
        btnBackForgetPassword.setOnClickListener(this)
        edtPhoneForgetPassword.addTextChangedListener(this)

        loadingDialog = LoadingDialog(this)
        notificationDialog = NotificationDialog(this)
        keyboardHeightProvider = KeyboardHeightProvider(this)
        layoutContentForgetPassword.post {
            originYContent = layoutContentForgetPassword.y
            keyboardHeightProvider?.start()
        }
        edtPhoneForgetPassword.onFocusChangeListener = this
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        (v?.parent as View).isSelected = hasFocus
        if (v.id == edtPhoneForgetPassword.id) btnClearPhoneForgetPassword.visibility =
            if (!edtPhoneForgetPassword.text.isNullOrEmpty() && hasFocus) View.VISIBLE else View.INVISIBLE
    }

    override fun onKeyboardHeightChanged(height: Int, orientation: Int) {
        val containerHeight = layoutContainerForgetPassword.height
        val topMarginContent = resources.getDimension(R.dimen.margin_top_status_bar)
        val contentHeight = layoutContentForgetPassword.height
        val bottomMarginContent = containerHeight - topMarginContent - contentHeight
        if (height <= 0) {
            layoutContentForgetPassword.animate().y(originYContent)
        } else {
            val offset = containerHeight - keyboardHeightProvider!!.screenHeight
            val delta = height + offset - bottomMarginContent + 5 //5 is padding
            if (delta > 0)
                layoutContentForgetPassword.animate().y(originYContent - delta)
        }
    }

    override fun afterTextChanged(s: Editable?) {
        btnClearPhoneForgetPassword.visibility =
            if (edtPhoneForgetPassword.text.isNullOrEmpty()  || !edtPhoneForgetPassword.isFocused) View.INVISIBLE else View.VISIBLE
        btnConfirmForgetPassword.isEnabled =
            !edtPhoneForgetPassword.text.isNullOrEmpty() && edtPhoneForgetPassword.length() >= 10
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        //do thing
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        //do nothing
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnConfirmForgetPassword -> {
                doForgetPassword()
            }
            R.id.btnClearPhoneForgetPassword -> {
                edtPhoneForgetPassword.setText("")
            }
            R.id.btnBackForgetPassword -> {
                finish()
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            }
        }
    }

    private fun doForgetPassword() {
        val phoneNumber = edtPhoneForgetPassword.text.toString()
        MyPreferenceManager.getInstance(this).setValue(Const.PHONE_NUMBER, phoneNumber)
        loadingDialog.showWindow()
        apiInterface = APIClient.getInstance(this).client.create(APIInterface::class.java)
        val call =
            apiInterface?.doRequestOTPNewPass(ResendOTPNewPassParams(phoneNumber))
        call?.enqueue(object : Callback<ResendOtpNewPassResponse> {
            override fun onResponse(
                call: Call<ResendOtpNewPassResponse>,
                response: Response<ResendOtpNewPassResponse>
            ) {
                loadingDialog.closeWindow()
                val responseObject = response.body()
                if (responseObject?.statusCode == Const.STATUS_CODE_SUCCESS) {
                    val intent = Intent(this@ForgetPasswordActivity, ConfirmOTPActivity::class.java)
                    intent.putExtra(Const.USER_NAME, phoneNumber)
                    intent.putExtra(Const.TIMEOUT, responseObject.responseObject.timeout)
                    intent.putExtra(Const.TYPE, Const.UPDATE_PASSWORD)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                } else {
                    notificationDialog?.setMessageDialog(responseObject!!.extraData)
                    notificationDialog?.showWindow(false)
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

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    override fun onResume() {
        super.onResume()
        keyboardHeightProvider?.setKeyboardHeightObserver(this)
        val phoneNumber = MyPreferenceManager.getInstance(this).getValue(Const.PHONE_NUMBER, "")
        if(phoneNumber.isNotEmpty()){
            edtPhoneForgetPassword.setText(phoneNumber)
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
