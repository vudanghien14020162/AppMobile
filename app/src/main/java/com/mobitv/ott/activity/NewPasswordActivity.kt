package com.mobitv.ott.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import com.mobitv.ott.R
import com.mobitv.ott.dialog.LoadingDialog
import com.mobitv.ott.dialog.NotificationDialog
import com.mobitv.ott.other.Const
import com.mobitv.ott.other.KeyboardHeightObserver
import com.mobitv.ott.other.KeyboardHeightProvider
import com.mobitv.ott.pojo.APIClient
import com.mobitv.ott.pojo.APIInterface
import com.mobitv.ott.pojo.NewPassParams
import com.mobitv.ott.pojo.NewPassResponse
import kotlinx.android.synthetic.main.activity_new_password.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NewPasswordActivity : BaseActivity(), TextWatcher, KeyboardHeightObserver, View.OnFocusChangeListener {
    private var isPasswordShow = false
    private var isRePasswordShow = false
    private var apiInterface: APIInterface? = null
    private lateinit var loadingDialog: LoadingDialog
    private var notificationDialog: NotificationDialog? = null
    private var code = ""
    private var phoneNumber = ""
    private var keyboardHeightProvider: KeyboardHeightProvider? = null
    private var originYContent = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarTransparent()
        setContentView(R.layout.activity_new_password)
        btnConfirmNewPassword.setOnClickListener(this)
        edtNewPassword.addTextChangedListener(this)
        edtReNewPassword.addTextChangedListener(this)
        toggleShowHideNewPassword.setOnClickListener(this)
        toggleShowHideReNewPassword.setOnClickListener(this)
        btnBackNewPassword.setOnClickListener(this)

        loadingDialog = LoadingDialog(this)
        notificationDialog = NotificationDialog(this)
        code = intent.getStringExtra(Const.CODE)
        phoneNumber = intent.getStringExtra(Const.USER_NAME)
        keyboardHeightProvider = KeyboardHeightProvider(this)
        layoutContentNewPassword.post {
            originYContent = layoutContentNewPassword.y
            keyboardHeightProvider?.start()
        }
        edtNewPassword.onFocusChangeListener = this
        edtReNewPassword.onFocusChangeListener = this
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        (v?.parent as View).isSelected = hasFocus
        if (v.id == edtNewPassword.id) toggleShowHideNewPassword.visibility =
            if (!edtNewPassword.text.isNullOrEmpty() && hasFocus) View.VISIBLE else View.INVISIBLE
        if (v.id == edtReNewPassword.id) toggleShowHideReNewPassword.visibility =
            if (!edtReNewPassword.text.isNullOrEmpty() && hasFocus) View.VISIBLE else View.INVISIBLE
    }

    override fun onKeyboardHeightChanged(height: Int, orientation: Int) {
        val containerHeight = containerNewPassword.height
        val topMarginContent = resources.getDimension(R.dimen.margin_top_status_bar)
        val contentHeight = layoutContentNewPassword.height
        val bottomMarginContent = containerHeight - topMarginContent - contentHeight
        if (height <= 0) {
            layoutContentNewPassword.animate().y(originYContent)
        } else {
            val offset = containerHeight - keyboardHeightProvider!!.screenHeight
            val delta = height + offset - bottomMarginContent + 5 //5 is padding
            if (delta > 0)
                layoutContentNewPassword.animate().y(originYContent - delta)
        }
    }

    override fun afterTextChanged(s: Editable?) {
        checkToEnableConfirmButton()
        toggleShowHideNewPassword.visibility = if (edtNewPassword.text.isNullOrEmpty() || !edtNewPassword.isFocused) View.INVISIBLE else View.VISIBLE
        toggleShowHideReNewPassword.visibility = if (edtReNewPassword.text.isNullOrEmpty() || !edtReNewPassword.isFocused) View.INVISIBLE else View.VISIBLE
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        //do thing
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        //do nothing
    }

    private fun checkToEnableConfirmButton() {
        btnConfirmNewPassword.isEnabled =
            !edtNewPassword.text.isNullOrEmpty() && !edtReNewPassword.text.isNullOrEmpty()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnConfirmNewPassword -> {
                doUpdatePassword()
            }
            R.id.btnBackNewPassword -> {
                finish()
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            }
            R.id.toggleShowHideNewPassword -> {
                if (isPasswordShow) {
                    edtNewPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                    toggleShowHideNewPassword.setImageResource(R.drawable.ic_show_password)
                    isPasswordShow = false
                } else {
                    edtNewPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    toggleShowHideNewPassword.setImageResource(R.drawable.ic_hide_password)
                    isPasswordShow = true
                }
                edtNewPassword.setSelection(edtNewPassword.length())
            }
            R.id.toggleShowHideReNewPassword -> {
                if (isRePasswordShow) {
                    edtReNewPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                    toggleShowHideReNewPassword.setImageResource(R.drawable.ic_show_password)
                    isRePasswordShow = false
                } else {
                    edtReNewPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    toggleShowHideReNewPassword.setImageResource(R.drawable.ic_hide_password)
                    isRePasswordShow = true
                }
                edtReNewPassword.setSelection(edtReNewPassword.length())
            }
        }
    }

    private fun doUpdatePassword() {
        if(edtNewPassword.length() < 6 || edtReNewPassword.length() < 6){
            notificationDialog?.setMessageDialog("Mật khẩu tối thiểu 6 ký tự.")
            notificationDialog?.showWindow(false)
            if(edtNewPassword.length() < 6){
                edtNewPassword.requestFocus()
            }
            if(edtReNewPassword.length() < 6){
                edtReNewPassword.requestFocus()
            }
        }else {
            if (edtNewPassword.text.toString() == edtReNewPassword.text.toString()) {
                val newPassword = edtNewPassword.text.toString()
                loadingDialog.showWindow()
                apiInterface = APIClient.getInstance(this).client.create(APIInterface::class.java)
                val call =
                    apiInterface?.doUpdatePassword(NewPassParams(code, newPassword, phoneNumber))
                call?.enqueue(object : Callback<NewPassResponse> {
                    override fun onResponse(
                        call: Call<NewPassResponse>,
                        response: Response<NewPassResponse>
                    ) {
                        loadingDialog.closeWindow()
                        val responseObject = response.body()
                        if (responseObject!!.statusCode == Const.STATUS_CODE_SUCCESS) {
                            notificationDialog?.setMessageDialog("Bạn đã cập nhật mật khẩu thành công")
                            notificationDialog?.showWindow(false)
                            Handler().postDelayed({
                                val intent = Intent(this@NewPasswordActivity, SignInActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                startActivity(intent)
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                            }, 1000)
                        } else {
                            notificationDialog?.setMessageDialog(responseObject!!.extraData)
                            notificationDialog?.showWindow(false)
                        }
                    }

                    override fun onFailure(call: Call<NewPassResponse>, t: Throwable) {
                        call.cancel()
                        loadingDialog.closeWindow()
                        notificationDialog?.setMessageDialog(getString(R.string.common_message_error))
                        notificationDialog?.showWindow(false)
                    }
                })
            } else {
                notificationDialog?.setMessageDialog("Mật khẩu không khớp.")
                notificationDialog?.showWindow(false)
            }
        }
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
}
