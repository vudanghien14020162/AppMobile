package com.mobitv.ott.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import com.mobitv.ott.R
import com.mobitv.ott.dialog.LoadingDialog
import com.mobitv.ott.dialog.NotificationDialog
import com.mobitv.ott.dialog.TwoOptionDialog
import com.mobitv.ott.fragment.ScardCheckFragment
import com.mobitv.ott.other.Const
import com.mobitv.ott.other.MyPreferenceManager
import com.mobitv.ott.other.Utils
import com.mobitv.ott.pojo.APIClient
import com.mobitv.ott.pojo.APIInterface
import com.mobitv.ott.pojo.LogOutResponse
import com.mobitv.ott.pojo.ScardRegisterResponse
import kotlinx.android.synthetic.main.activity_scard_update.*
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.fragment_scard_check.*
import kotlinx.android.synthetic.main.scard_info_fragment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SettingsActivity : BaseActivity() {

    private var apiInterface: APIInterface? = null
    private var myPreferenceManager: MyPreferenceManager? = null
    private lateinit var loadingDialog: LoadingDialog
    private var notificationDialog: NotificationDialog? = null
    private var scardCheckFragment:ScardCheckFragment?=null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarTransparent()
        setContentView(R.layout.activity_settings)
        btnBackSettings.setOnClickListener(this)
        btnLogOut.setOnClickListener(this)
        layoutAsk.setOnClickListener(this)
        layoutAbout.setOnClickListener(this)
        layoutPolicy.setOnClickListener(this)
        layoutScardRegister.setOnClickListener(this)
        var scardNum = myPreferenceManager?.getValue(Const.SCARD_NUM, "0")
        var apiInterface: APIInterface? = null
        apiInterface = APIClient.getInstance(this).client.   create(APIInterface::class.java)
        var phone =  myPreferenceManager?.getValue(Const.PHONE_NUMBER,"")
        var user_id= myPreferenceManager?.getValue(Const.PHONE_NUMBER,"")
        myPreferenceManager = MyPreferenceManager.getInstance(this)
        loadingDialog = LoadingDialog(this)
        notificationDialog = NotificationDialog(this)
        btnLogOut.isEnabled = myPreferenceManager!!.isLogIn
        if(myPreferenceManager?.getValue(Const.SCARD_NUM,null)!=null && myPreferenceManager!!.isLogIn){
            layoutScardRegister.visibility=View.INVISIBLE
            layoutScardInfo.visibility=View.VISIBLE
            Log.d("TAG", "XUẤT HIỆN THÔNG TIN")
        }else{
            layoutScardRegister.visibility=View.VISIBLE
            layoutScardInfo.visibility=View.INVISIBLE
            Log.d("TAG", "KHÔNG XUẤT HIỆN THÔNG TIN")
        }
        val bundle = getIntent().getExtras();
        if(bundle != null){
            if(bundle.getString(Const.BUNDLE_CODE_EXPIRED_DATE) != null && !bundle.getString(Const.BUNDLE_CODE_EXPIRED_DATE).equals("")){
                createNotificationDialog(bundle.getString(Const.BUNDLE_CODE_EXPIRED_DATE).toString())
            }
        }
//        progressBar?.visibility=View.GONE
        var call = apiInterface?.getScardNumber(scardNum,user_id, phone)
        call?.enqueue(object : Callback<ScardRegisterResponse> {
            override fun onResponse(

                call: Call<ScardRegisterResponse>,
                response: Response<ScardRegisterResponse>
            ) {
                var responseObject = response.body()
                if (responseObject != null && responseObject?.statusCode == Const.STATUS_CODE_SUCCESS) {
                    if(responseObject?.response_object.expired_date != null && responseObject?.response_object.scard_number != null){
                        scard_num.text = responseObject?.response_object.scard_number
                        date_expired.text = responseObject?.response_object.expired_date
                        myPreferenceManager?.setValue(Const.SCARD_NUM, responseObject?.response_object.scard_number)
                        myPreferenceManager?.setValue(Const.SCARD_EXPIRED, responseObject?.response_object.expired_date)
                        layoutScardRegister.visibility=View.INVISIBLE
                        layoutScardInfo.visibility=View.VISIBLE
                        Log.d("TAG DU LIEU", "KHÔNG XUẤT HIỆN")
                    }
                } else {

                }
            }
            override fun onFailure(call: Call<ScardRegisterResponse>, t: Throwable) {
                Log.d("Log Loi: ",  t.message.toString())
            }
        })

//        if(myPreferenceManager?.getValue(Const.SCARD_NUM,null)!=null && myPreferenceManager!!.isLogIn){
//            layoutScardRegister.visibility=View.INVISIBLE
//            layoutScardInfo.visibility=View.VISIBLE
//            Log.d("TAG", "XUẤT HIỆN THÔNG TIN")
//        }else{
//            layoutScardRegister.visibility=View.VISIBLE
//            layoutScardInfo.visibility=View.INVISIBLE
//            Log.d("TAG", "KHÔNG XUẤT HIỆN THÔNG TIN")
//        }

    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnBackSettings -> {
                backToMainApp()
            }
            R.id.btnLogOut -> {
                doLogOut()
            }
            R.id.layoutAsk -> {
                doLayoutAsk()
            }
            R.id.layoutAbout -> {
                doLayoutAbout()
            }
            R.id.layoutPolicy -> {
                doLayoutPolicy()
            }
            R.id.layoutScardRegister->{
                doLayoutScard()
            }
        }
    }

    private fun doLayoutAsk() {
        openUrl("http://aq.avg.vn/")
    }

    private fun doLayoutScard(){
        if(!myPreferenceManager!!.isLogIn){
            notificationDialog?.setMessageDialog(getString(R.string.common_message_login_card))
            notificationDialog?.showWindow(false)
            return;
        }
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        scardCheckFragment = ScardCheckFragment.newInstance();
        if (supportFragmentManager.findFragmentByTag(ScardCheckFragment.TAG) == null) {
            fragmentTransaction.add(R.id.containerSettings, scardCheckFragment!!, ScardCheckFragment.TAG)
            fragmentTransaction.commit()
        }else{
            if(fragment_scard_check.visibility == View.INVISIBLE){
                fragment_scard_check.visibility = View.VISIBLE

            }else{
                fragment_scard_check.visibility = View.INVISIBLE
            }
        }

    }

    private fun doLayoutPolicy() {
        openUrl("http://policy.avg.vn/")
    }

    private fun doLayoutAbout() {
        openUrl("http://about.avg.vn/")
    }

    private fun openUrl(url: String) {
        val uris = Uri.parse(url)
        val intents = Intent(Intent.ACTION_VIEW, uris)
        startActivity(intents)
    }



    private fun doLogOut() {
        val dialog = TwoOptionDialog(this)
        dialog.setMessageDialog("Bạn muốn đăng xuất?")
        dialog.setListener(object : TwoOptionDialog.OnButtonClickListener {
            override fun onAgree() {
                callLogOut()
            }

            override fun onCancel() {

            }

        })
//        dialog.showWindow(  )
    }

    private fun callLogOut() {
        myPreferenceManager?.setValue(Const.DATE_CHECK_SCARD, null)
        myPreferenceManager?.setValue(Const.SCARD_NUM, null)
        myPreferenceManager?.setValue(Const.SCARD_EXPIRED, null)
        myPreferenceManager?.setValue(Const.SCARD_STATUS, null)
        loadingDialog.showWindow()
        apiInterface = APIClient.getInstance(this).client.create(APIInterface::class.java)
        val call =
            apiInterface?.doLogOut()
        call?.enqueue(object : Callback<LogOutResponse> {
            override fun onResponse(
                call: Call<LogOutResponse>,
                response: Response<LogOutResponse>
            ) {
                loadingDialog.closeWindow()
                val responseObject = response.body()
                if (responseObject!!.statusCode == Const.STATUS_CODE_SUCCESS) {
                    myPreferenceManager?.isLogIn = false
                    myPreferenceManager?.setValue(Const.AUTH, "")
                    backToSignIn()
                } else {
                    if (Const.DEBUG) {
                        Log.d("RESPONSE", responseObject!!.toString())
                    }
                    notificationDialog?.setMessageDialog(responseObject.extraData)
                    notificationDialog?.showWindow(false)
                }
            }

            override fun onFailure(call: Call<LogOutResponse>, t: Throwable) {
                call.cancel()
                loadingDialog.closeWindow()
                notificationDialog?.setMessageDialog(getString(R.string.common_message_error))
                notificationDialog?.showWindow(false)
            }
        })

    }

    private fun backToSignIn() {
        val intent = Intent(this, SignInActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun backToMainApp(){
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun createNotificationDialog(message : String){
        if(notificationDialog == null){
            notificationDialog = NotificationDialog(this);
        }
        notificationDialog?.setMessageDialog(message.toString());
        notificationDialog?.setTitleDialog(getString(R.string.notification));
        notificationDialog?.showWindow(false);
    }

    private fun getScradNumber(user_id: String){
        var scardNum = myPreferenceManager?.getValue(Const.SCARD_NUM, "0")
        var apiInterface: APIInterface? = null
        apiInterface = APIClient.getInstance(this).client.   create(APIInterface::class.java)
        var phone = user_id
        Log.d("Phone", phone)
        var call = apiInterface?.getScardNumber(scardNum,user_id, phone)
        call?.enqueue(object : Callback<ScardRegisterResponse> {
            override fun onResponse(
                call: Call<ScardRegisterResponse>,
                response: Response<ScardRegisterResponse>
            ) {
                Log.d("START getScradNumber "," Check API Scard Number")
                var responseObject = response.body()
                if (responseObject != null && responseObject?.statusCode == Const.STATUS_CODE_SUCCESS) {
                    Log.d("TAG MAIN ACTIVITY: ", "VŨ ĐĂNG HIỂN"+responseObject.toString())
                    myPreferenceManager?.setValue(Const.DATE_CHECK_SCARD, Utils.getSendTime());
                    myPreferenceManager?.setValue(Const.SCARD_NUM, responseObject?.response_object.scard_number)
                    myPreferenceManager?.setValue(Const.SCARD_EXPIRED, responseObject?.response_object.expired_date)
//                    Log.d("TAG DỮ LIỆU: ", "DỮ LIỆU TRẢ VỀ")
                } else {

                }
            }
            override fun onFailure(call: Call<ScardRegisterResponse>, t: Throwable) {
                Log.d("Log Loi: ",  t.message.toString())
            }
        })
    }

}
