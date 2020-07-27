package com.mobitv.ott.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.mobitv.ott.R
import com.mobitv.ott.activity.SettingsActivity
import com.mobitv.ott.dialog.NotificationDialog
import com.mobitv.ott.other.Const
import com.mobitv.ott.other.MyPreferenceManager
import com.mobitv.ott.pojo.APIClient
import com.mobitv.ott.pojo.APIInterface
import com.mobitv.ott.pojo.ScardRegisterResponse
import kotlinx.android.synthetic.main.fragment_scard_check.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScardCheckFragment : Fragment() {

    private var apiInterface: APIInterface? = null
    private var myPreferenceManager: MyPreferenceManager?=null
    private var  progressBar: ProgressBar?=null
    private var notificationDialog: NotificationDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var v=inflater.inflate(R.layout.fragment_scard_check, container, false)
        progressBar=v.progressSubmit
        v.submitScard.setOnClickListener(View.OnClickListener {
            v.progressSubmit.visibility=View.VISIBLE
//            v.visibility=View.INVISIBLE
            submitRegister(v.editScard.text.toString())
        })

        return v
    }

    fun submitRegister(scardNum : String ){
        if(scardNum.trim() == "" || scardNum.equals("")){
            createNotificationDialog(getString(R.string.common_message_empty_card_error));
            progressBar?.visibility=View.GONE
            return;
        }
        var myPreferenceManager=MyPreferenceManager.getInstance(activity!!)
        var tel=myPreferenceManager.getValue(Const.PHONE_NUMBER,null);
        apiInterface= APIClient.getInstance(activity!!).client.create(APIInterface::class.java)
        var call=apiInterface?.doScardRegister(tel,scardNum)
        call?.enqueue(object: Callback<ScardRegisterResponse>{
            override fun onResponse(
                call: Call<ScardRegisterResponse>,
                response: Response<ScardRegisterResponse>
            ) {
                var responseObject=response.body()
                if(responseObject!=null && responseObject?.statusCode==Const.STATUS_CODE_SUCCESS ){
                    Log.d("TAG THANH CONG", responseObject.toString())
//                    myPreferenceManager.setValue(Const.SCARD_NUM,responseObject?.response_object.scard_number)
                    myPreferenceManager.setValue(Const.SCARD_NUM, scardNum)
                    myPreferenceManager.setValue(Const.SCARD_EXPIRED,responseObject?.response_object.expired_date)
                    myPreferenceManager.setValue(Const.SCARD_STATUS,responseObject?.response_object.status)
                    var stringExpiredDate = "Thời gian sử dụng thẻ của quý khách đến ngày " + responseObject?.response_object.expired_date
                    val bundle = Bundle()
                    bundle.putString(Const.BUNDLE_CODE_EXPIRED_DATE, stringExpiredDate)
                    val intent = Intent(activity!!, SettingsActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtras(bundle)
                    startActivity(intent, bundle)
                    activity!!.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }else{
                    Log.d("TAG LOI ", responseObject.toString())
                    createNotificationDialog((responseObject?.extraData).toString());
                    progressBar?.visibility=View.GONE
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
            notificationDialog = NotificationDialog(context);
        }
        notificationDialog?.setMessageDialog(message.toString());
        notificationDialog?.setTitleDialog(getString(R.string.notification));
        notificationDialog?.showWindow(false);
    }

}
