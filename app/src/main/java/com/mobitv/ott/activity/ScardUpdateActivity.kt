package com.mobitv.ott.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import com.mobitv.ott.R
import com.mobitv.ott.dialog.NotificationDialog
import com.mobitv.ott.fragment.ScardCheckFragment
import com.mobitv.ott.other.Const
import com.mobitv.ott.other.MyPreferenceManager
import com.mobitv.ott.pojo.APIClient
import com.mobitv.ott.pojo.APIInterface
import com.mobitv.ott.pojo.ScardRegisterResponse
import kotlinx.android.synthetic.main.activity_scard_update.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScardUpdateActivity : BaseActivity() {
    private var myPreferenceManager: MyPreferenceManager?=null
    private var apiInterface: APIInterface? = null
    private var  progressBar: ProgressBar?=null
    private var notificationDialog: NotificationDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scard_update)
        myPreferenceManager=MyPreferenceManager.getInstance(this)
        var scardNumberOld = getValueScardNumberOld(MyPreferenceManager.getInstance(this))
        saveButton.setOnClickListener(View.OnClickListener {
            var scardNumberNew = editScard.text.toString()
            val intent = Intent(this, SettingsActivity::class.java)
            submitUpdateCard(scardNumberOld, scardNumberNew, intent)
        });
        backButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        })
    }

    private fun submitUpdateCard(scardNumberOld: String, scardNum : String, intent: Intent){
        if(scardNum.trim() == "" || scardNum.equals("")){
            createNotificationDialog(getString(R.string.common_message_empty_card_error));
            return;
        }
        if(scardNumberOld != "" && scardNum.equals(scardNumberOld)){
            createNotificationDialog(getString(R.string.common_message_duplicate_card));
            return;
        }
        var myPreferenceManager=MyPreferenceManager.getInstance(this)
        var tel=myPreferenceManager.getValue(Const.PHONE_NUMBER,null);
        apiInterface= APIClient.getInstance(this).client.create(APIInterface::class.java)
        var call=apiInterface?.updateScardNumber(tel,scardNum, scardNumberOld)
        call?.enqueue(object: Callback<ScardRegisterResponse> {
            override fun onResponse(
                call: Call<ScardRegisterResponse>,
                response: Response<ScardRegisterResponse>
            ) {
                var responseObject=response.body()
                if(responseObject!=null && responseObject?.statusCode==Const.STATUS_CODE_SUCCESS ){
                    myPreferenceManager.setValue(Const.SCARD_NUM, scardNum)
                    myPreferenceManager.setValue(Const.SCARD_EXPIRED,responseObject?.response_object.expired_date)
                    myPreferenceManager.setValue(Const.SCARD_STATUS,responseObject?.response_object.status)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    var stringExpiredDate = "Thời gian sử dụng thẻ của quý khách đến ngày " + responseObject?.response_object.expired_date;
                    val bundle = Bundle()
                    bundle.putString(Const.BUNDLE_CODE_EXPIRED_DATE, stringExpiredDate)
                    intent.putExtras(bundle)
                    startActivity(intent, bundle)
                }else{
                    progressBar?.visibility=View.GONE
                    createNotificationDialog((responseObject?.extraData).toString());
                }
            }
            override fun onFailure(call: Call<ScardRegisterResponse>, t: Throwable) {
                createNotificationDialog(t.message.toString());
                progressBar?.visibility=View.GONE;

            }
        })
    }
    companion object {
        const val TAG = "scard_register"
        @JvmStatic
        fun newInstance() =
            ScardCheckFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
    private fun createNotificationDialog(message : String){
        if(notificationDialog == null){
            notificationDialog = NotificationDialog(this);
        }
        notificationDialog?.setMessageDialog(message.toString())
        notificationDialog?.setTitleDialog(getString(R.string.notification))
        notificationDialog?.showWindow(false)
    }

    private fun getValueScardNumberOld(myPreferenceManager: MyPreferenceManager): String {
        var scardNumberOld = myPreferenceManager.getValue(Const.SCARD_NUM, null)
        return scardNumberOld.toString()
    }
}
