package com.mobitv.ott.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.*
import android.text.method.HideReturnsTransformationMethod
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.CompoundButton
import com.mobitv.ott.R
import com.mobitv.ott.dialog.LoadingDialog
import com.mobitv.ott.dialog.NotificationDialog
import com.mobitv.ott.other.*
import com.mobitv.ott.other.ObservableWebView.OnScrollChangedCallback
import com.mobitv.ott.pojo.APIClient
import com.mobitv.ott.pojo.APIInterface
import com.mobitv.ott.pojo.RegisterParams
import com.mobitv.ott.pojo.RegisterResponse
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.layout_term_of_use.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SignUpActivity : BaseActivity(), TextWatcher, KeyboardHeightObserver,
    View.OnFocusChangeListener {
    private var isPasswordShow = false
    private var isRePasswordShow = false
    private var myPreferenceManager: MyPreferenceManager? = null
    private var apiInterface: APIInterface? = null
    private lateinit var loadingDialog: LoadingDialog
    private var notificationDialog: NotificationDialog? = null
    private var keyboardHeightProvider: KeyboardHeightProvider? = null
    private var originYContent = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarTransparent()
        setContentView(R.layout.activity_signup)
        btnConfirmSignUp.setOnClickListener(this)
        edtPhoneSignUp.addTextChangedListener(this)
        edtPasswordSignUp.addTextChangedListener(this)
        edtRePasswordSignUp.addTextChangedListener(this)
        btnClearPhoneSignUp.setOnClickListener(this)
        toggleShowHidePassword.setOnClickListener(this)
        toggleShowHideRePassword.setOnClickListener(this)
        btnBackSignUp.setOnClickListener(this)

        //set up term of use clickable
        val ss = SpannableString(getString(R.string.term_of_use))
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                goTermOfUse()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                if (tvTermOfUse.isPressed) {
                    ds.color = resources.getColor(R.color.colorAlmostTransparent)
                } else {
                    ds.color = Color.parseColor("#e64354")
                }
                ds.isUnderlineText = false
                tvTermOfUse.invalidate()
            }
        }
        ss.setSpan(clickableSpan, 29, 47, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvTermOfUse.highlightColor = Color.TRANSPARENT
        tvTermOfUse.text = ss
        tvTermOfUse.movementMethod = LinkMovementMethod.getInstance()
        cbTermOfUse.setOnCheckedChangeListener(onCheckedChangedListener)

        myPreferenceManager = MyPreferenceManager.getInstance(this)
        loadingDialog = LoadingDialog(this)
        notificationDialog = NotificationDialog(this)
        keyboardHeightProvider = KeyboardHeightProvider(this)
        layoutContentSignUp.post {
            originYContent = layoutContentSignUp.y
            keyboardHeightProvider?.start()
        }
        edtPhoneSignUp.onFocusChangeListener = this
        edtPasswordSignUp.onFocusChangeListener = this
        edtRePasswordSignUp.onFocusChangeListener = this
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        (v?.parent as View).isSelected = hasFocus
        if (v.id == edtPhoneSignUp.id) btnClearPhoneSignUp.visibility =
            if (!edtPhoneSignUp.text.isNullOrEmpty() && hasFocus) View.VISIBLE else View.INVISIBLE
        if (v.id == edtPasswordSignUp.id) toggleShowHidePassword.visibility =
            if (!edtPasswordSignUp.text.isNullOrEmpty() && hasFocus) View.VISIBLE else View.INVISIBLE
        if (v.id == edtRePasswordSignUp.id) toggleShowHideRePassword.visibility =
            if (!edtRePasswordSignUp.text.isNullOrEmpty() && hasFocus) View.VISIBLE else View.INVISIBLE
    }

    override fun onKeyboardHeightChanged(height: Int, orientation: Int) {
        val containerHeight = layoutContainerSignUp.height
        val topMarginContent = resources.getDimension(R.dimen.margin_top_status_bar)
        val contentHeight = layoutContentSignUp.height
        val bottomMarginContent = containerHeight - topMarginContent - contentHeight
        if (height <= 0) {
            layoutContentSignUp.animate().y(originYContent)
        } else {
            val offset = containerHeight - keyboardHeightProvider!!.screenHeight
            val delta = height + offset - bottomMarginContent + 5 //5 is padding
            if (delta > 0)
                layoutContentSignUp.animate().y(originYContent - delta)
        }
    }

    override fun onResume() {
        super.onResume()
        keyboardHeightProvider?.setKeyboardHeightObserver(this)
        val phoneNumber = MyPreferenceManager.getInstance(this).getValue(Const.PHONE_NUMBER, "")
        if (phoneNumber.isNotEmpty()) {
            edtPhoneSignUp.setText(phoneNumber)
            edtPasswordSignUp.requestFocus()
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

    override fun afterTextChanged(s: Editable?) {
        if (edtPhoneSignUp.length() == 11 && !edtRePasswordSignUp.isFocused) {
            edtPasswordSignUp.requestFocus()
        }
        checkToEnableConfirmButton()
        btnClearPhoneSignUp.visibility =
            if (edtPhoneSignUp.text.isNullOrEmpty() || !edtPhoneSignUp.isFocused) View.INVISIBLE else View.VISIBLE
        toggleShowHidePassword.visibility =
            if (edtPasswordSignUp.text.isNullOrEmpty() || !edtPasswordSignUp.isFocused) View.INVISIBLE else View.VISIBLE
        toggleShowHideRePassword.visibility =
            if (edtRePasswordSignUp.text.isNullOrEmpty() || !edtRePasswordSignUp.isFocused) View.INVISIBLE else View.VISIBLE
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        //do thing
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        //do nothing
    }

    private val onCheckedChangedListener = object : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
            checkToEnableConfirmButton()
        }
    }

    private fun checkToEnableConfirmButton() {
        btnConfirmSignUp.isEnabled =
            !edtPhoneSignUp.text.isNullOrEmpty() && !edtPasswordSignUp.text.isNullOrEmpty() && !edtRePasswordSignUp.text.isNullOrEmpty() && cbTermOfUse.isChecked
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnConfirmSignUp -> {
                doSignUp()
            }
            R.id.btnClearPhoneSignUp -> {
                edtPhoneSignUp.setText("")
            }
            R.id.btnBackSignUp -> {
                finish()
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            }
            R.id.btnCloseTermOfUse -> {
                layoutTermOfUseContent.visibility = View.GONE
                layoutTermOfUseContent.startAnimation(
                    AnimationUtils.loadAnimation(
                        this,
                        R.anim.out_to_bottom
                    )
                )
            }
            R.id.toggleShowHidePassword -> {
                if (isPasswordShow) {
                    edtPasswordSignUp.transformationMethod =
                        PasswordTransformationMethod.getInstance()
                    toggleShowHidePassword.setImageResource(R.drawable.ic_show_password)
                    isPasswordShow = false
                } else {
                    edtPasswordSignUp.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                    toggleShowHidePassword.setImageResource(R.drawable.ic_hide_password)
                    isPasswordShow = true
                }
                edtPasswordSignUp.setSelection(edtPasswordSignUp.length())
            }
            R.id.toggleShowHideRePassword -> {
                if (isRePasswordShow) {
                    edtRePasswordSignUp.transformationMethod =
                        PasswordTransformationMethod.getInstance()
                    toggleShowHideRePassword.setImageResource(R.drawable.ic_show_password)
                    isRePasswordShow = false
                } else {
                    edtRePasswordSignUp.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                    toggleShowHideRePassword.setImageResource(R.drawable.ic_hide_password)
                    isRePasswordShow = true
                }
                edtRePasswordSignUp.setSelection(edtRePasswordSignUp.length())
            }
            R.id.btnBackToTop -> {
                wvTermOfUse.scrollTo(0, 0)
            }
        }
    }

    private fun doSignUp() {
        when (checkInputValidate()) {
            InputValidate.PHONE_NUMBER_INVALID -> {
                //check on server
            }
            InputValidate.PASSWORD_NOT_MATCH -> {
                notificationDialog?.setMessageDialog("Mật khẩu không khớp")
                notificationDialog?.showWindow(false)
            }
            InputValidate.PASSWORD_LENGTH_MINIMUM -> {
                notificationDialog?.setMessageDialog("Mật khẩu tối thiểu là 6 ký tự")
                notificationDialog?.showWindow(false)
            }
            InputValidate.VALID -> {
                myPreferenceManager?.setValue(Const.PHONE_NUMBER, edtPhoneSignUp.text.toString())
                loadingDialog.showWindow()
                apiInterface = APIClient.getInstance(this).client.create(APIInterface::class.java)
                val call =
                    apiInterface?.doRegister(
                        RegisterParams(
                            edtPhoneSignUp.text.toString(),
                            edtPasswordSignUp.text.toString()
                        )
                    )
                call?.enqueue(object : Callback<RegisterResponse> {
                    override fun onResponse(
                        call: Call<RegisterResponse>,
                        response: Response<RegisterResponse>
                    ) {
                        loadingDialog.closeWindow()
                        val responseObject = response.body()
                        if (responseObject!!.statusCode == Const.STATUS_CODE_SUCCESS) {
                            if (responseObject!!.registerResponseObject.username != null) {
                                var timeout = responseObject!!.registerResponseObject.timeout
                                if (timeout == null) {
                                    timeout = ""
                                }

                                finish()
                                // dung khi ap dung OTP
//                                doAfterRegisterSuccess(
//                                    responseObject!!.registerResponseObject.username,
//                                    timeout
//                                )
                            }
                        } else {
                            doAfterRegisterFailed(responseObject!!.extraData)
                        }
                    }

                    override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                        call.cancel()
                        loadingDialog.closeWindow()
                        doAfterRegisterFailed(getString(R.string.common_message_error))
                    }
                })
            }
        }
    }

    private fun doAfterRegisterSuccess(phone: String, timeout: String) {
        myPreferenceManager?.setValue(Const.USER_NAME, phone)
        val intent = Intent(this@SignUpActivity, ConfirmOTPActivity::class.java)
        intent.putExtra(Const.USER_NAME, phone)
        if (timeout != null) {
            intent.putExtra(Const.TIMEOUT, timeout)
        } else {
            intent.putExtra(Const.TIMEOUT, "")
        }
        intent.putExtra(Const.TYPE, Const.REGISTER)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun doAfterRegisterFailed(mes: String) {
        notificationDialog?.setMessageDialog(mes)
        notificationDialog?.showWindow(false)
    }

    private fun goTermOfUse() {
        layoutTermOfUseContent.visibility = View.VISIBLE
        layoutTermOfUseContent.startAnimation(
            AnimationUtils.loadAnimation(
                this,
                R.anim.in_from_bottom
            )
        )
        btnCloseTermOfUse.setOnClickListener(this)
        wvTermOfUse.loadUrl("file:///android_asset/html/" + getString(R.string.term_of_use_content))
        wvTermOfUse.setBackgroundColor(Color.TRANSPARENT)
        wvTermOfUse.onScrollChangedCallback = object : OnScrollChangedCallback {
            override fun onScroll(l: Int, t: Int, oldl: Int, oldt: Int) {
                val tek =
                    Math.floor((wvTermOfUse.contentHeight * wvTermOfUse.scale).toDouble()).toInt()
                if (tek - wvTermOfUse.scrollY - 10 <= wvTermOfUse.height) {
                    btnBackToTop.visibility = View.VISIBLE
                } else {
                    btnBackToTop.visibility = View.INVISIBLE
                }
            }
        }
        btnBackToTop.setOnClickListener(this)
    }

    private fun checkInputValidate(): InputValidate {
        if (edtPasswordSignUp.text.toString() != edtRePasswordSignUp.text.toString()) {
            return InputValidate.PASSWORD_NOT_MATCH
        } else {
            if (edtPasswordSignUp.length() < 6 || edtRePasswordSignUp.length() < 6) {
                return InputValidate.PASSWORD_LENGTH_MINIMUM
            } else {
                return InputValidate.VALID
            }
        }
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}
