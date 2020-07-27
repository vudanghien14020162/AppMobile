package com.mobitv.ott.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.mobitv.ott.R
import com.mobitv.ott.activity.ScardUpdateActivity
import com.mobitv.ott.activity.SettingsActivity
import com.mobitv.ott.dialog.NotificationDialog
import com.mobitv.ott.dialog.ScardDialog
import com.mobitv.ott.dialog.TwoOptionDialog
import com.mobitv.ott.other.Const
import com.mobitv.ott.other.MyPreferenceManager
import com.mobitv.ott.other.Utils
import com.mobitv.ott.pojo.APIClient
import com.mobitv.ott.pojo.APIInterface
import com.mobitv.ott.pojo.ScardRegisterResponse
import kotlinx.android.synthetic.main.scard_info_fragment.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 */
class ScardInfoFragment : Fragment() {
    private var notificationDialog: NotificationDialog? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var myPreferenceManager= MyPreferenceManager.getInstance(activity!!)
        var v=inflater.inflate(R.layout.scard_info_fragment, container, false)
        v.scard_num.text= myPreferenceManager.getValue(Const.SCARD_NUM,null)
        v.date_expired.text= myPreferenceManager.getValue(Const.SCARD_EXPIRED,null)
        v.edit_scard_number.setOnClickListener(View.OnClickListener {
            var intent=Intent(activity!!,ScardUpdateActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        })

        v.delete_scard_number.setOnClickListener(View.OnClickListener {

            val dialog = ScardDialog(activity!!)
            dialog.setMessageDialog("Quý khách chọn Đồng ý để xác nhận xóa thẻ đầu thu khỏi tài khoản?")
            dialog.setListener(object : ScardDialog.OnButtonClickListener {
                override fun onAgree() {
                    deleteScardNumber(myPreferenceManager)
                }
                override fun onCancel() {
                }

            })
            dialog.showWindow(false)
        })
        return v
    }

    private fun deleteScardNumber(myPreferenceManager: MyPreferenceManager){
        var scardNumber = myPreferenceManager.getValue(Const.SCARD_NUM, "0")
        var apiInterface: APIInterface? = null
        apiInterface = APIClient.getInstance(activity!!).client.create(APIInterface::class.java)
        var call = apiInterface?.deleteScardNumber(scardNumber)
        call?.enqueue(object : Callback<ScardRegisterResponse> {
            override fun onResponse(
                call: Call<ScardRegisterResponse>,
                response: Response<ScardRegisterResponse>
            ) {
                var responseObject = response.body()
                if (responseObject != null && responseObject?.statusCode == Const.STATUS_CODE_SUCCESS) {
                    myPreferenceManager.setValue(Const.DATE_CHECK_SCARD, null)
                    myPreferenceManager.setValue(Const.SCARD_NUM, null)
                    myPreferenceManager.setValue(Const.SCARD_EXPIRED, null)
                    val bundle = Bundle()
                    bundle.putString(Const.BUNDLE_CODE_EXPIRED_DATE, (responseObject?.extraData).toString())
                    var intent=Intent(activity!!,SettingsActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtras(bundle)
                    startActivity(intent, bundle)
                } else {
                    createNotificationDialog((responseObject?.extraData).toString())
                }
            }
            override fun onFailure(call: Call<ScardRegisterResponse>, t: Throwable) {
                Log.d("Log Loi: ",  t.message.toString())
            }
        })
    }
    private fun createNotificationDialog(message : String){
        if(notificationDialog == null){
            notificationDialog = NotificationDialog(activity!!)
        }
        notificationDialog?.setMessageDialog(message.toString());
        notificationDialog?.setTitleDialog(getString(R.string.notification));
        notificationDialog?.showWindow(false);
    }


}
