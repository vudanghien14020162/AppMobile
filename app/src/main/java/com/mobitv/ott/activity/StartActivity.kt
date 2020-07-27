package com.mobitv.ott.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.mobitv.ott.R
import com.mobitv.ott.adapter.StartSlidesPagerAdapter
import com.mobitv.ott.dialog.NotificationDialog
import com.mobitv.ott.fragment.SlidePageFragment
import com.mobitv.ott.other.Const
import com.mobitv.ott.other.GlobalParams
import com.mobitv.ott.other.MyPreferenceManager
import com.mobitv.ott.other.Utils
import com.mobitv.ott.pojo.*
import io.fabric.sdk.android.BuildConfig
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_start.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class StartActivity : BaseActivity(), androidx.viewpager.widget.ViewPager.OnPageChangeListener {

    private val MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 1002
    private var isScheduled = false

    //hard code for testing
    val content = arrayOf(
        "Ứng dụng\n" + "xem truyền hình\n" + "tối ưu nhất",
        "Kho phim ảnh\n" + "phong phú\n" + "và đặc sắc",
        "Cập nhật\n" + "liên tục các\n" + "kênh phát sóng"
    )
    private var apiInterface: APIInterface? = null
    private var myPreferenceManager: MyPreferenceManager? = null
    private val mHandler = Handler()
    private var isLoad = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarTransparent()
        setContentView(R.layout.activity_start)
//        checkAccessApp()
        init();
    }

    private fun init() {
        val pInfo: PackageInfo
        try {
            pInfo = packageManager.getPackageInfo(packageName, 0)
            GlobalParams.VERSION_CODE = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pInfo.longVersionCode
            } else {
                pInfo.versionCode.toLong()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_PHONE_STATE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_PHONE_STATE),
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE
                )
                mHandler.postDelayed(loadSettingsThread, 5000) //delay 5s
            } else {
                loadBasicSettings()
            }
        } else {
            loadBasicSettings()
        }
        myPreferenceManager = MyPreferenceManager.getInstance(this)

        val crashlyticsKit = Crashlytics.Builder()
            .core(
                CrashlyticsCore.Builder()
                    .disabled(BuildConfig.DEBUG).build()
            )
            .build()

        Fabric.with(this, crashlyticsKit)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_PHONE_STATE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()) {
                    mHandler.removeCallbacks(loadSettingsThread)
                    if (!isLoad)
                        loadBasicSettings()
                }
            }
        }
    }

    private val loadSettingsThread = Runnable {
        loadBasicSettings()
        isLoad = true
    }

    private fun loadBasicSettings() {
        apiInterface = APIClient.getInstance(this).client.create(APIInterface::class.java)
        val call =
            apiInterface?.doGetBasicSettings()
        call?.enqueue(object : Callback<BasicSettingsResponse> {
            override fun onResponse(
                call: Call<BasicSettingsResponse>,
                response: Response<BasicSettingsResponse>
            ) {
                val responseObject = response.body()
                if (responseObject?.statusCode == Const.STATUS_CODE_SUCCESS) {
                    val basicSettings = responseObject.responseObject
                    myPreferenceManager?.setValue(Const.SERVER_KEY, basicSettings.newEncryptionKey)
                    if (!Utils.getSendTime().equals(
                            myPreferenceManager?.getValue(
                                Const.DATE,
                                ""
                            )
                        )
                    ) {
                        val newGUID = UUID.randomUUID().toString()
                        myPreferenceManager?.setValue(Const.GUID, newGUID)
                        myPreferenceManager?.setValue(Const.DATE, Utils.getSendTime())
                    }
                    myPreferenceManager?.setValue(Const.USER_ID, "0")
                    val currentVersionCode = basicSettings.versionCode

//                    if((currentVersionCode > GlobalParams.VERSION_CODE)){
//                        val notificationDialog = TwoOptionDialog(this@StartActivity)
//                        notificationDialog.setMessageDialog("Vui lòng cập nhật phiên bản mới nhất của ứng dụng để tiếp tục.")
//                        notificationDialog.setCanceledOnTouchOutside(false)
//                        notificationDialog.setListener(object : TwoOptionDialog.OnButtonClickListener{
//                            override fun onAgree() {
//                                openAppInMarket(this@StartActivity, packageName)
//                            }
//
//                            override fun onCancel() {
//                                finish()
//                            }
//
//                        })
//                        notificationDialog.showWindow(false)
//                    }else
                    // {
                    mHandler.postDelayed({
                        hideSplashAndDoMore()
                    }, 300)
                    // }
//                    val notificationDialog = NotificationDialog(this@StartActivity)
//                    notificationDialog.setMessageDialog(getString(R.string.common_message_error))
//                    notificationDialog.showWindow(false)
                } else {
                    val notificationDialog = NotificationDialog(this@StartActivity)
                    notificationDialog.setMessageDialog(getString(R.string.common_message_error))
                    notificationDialog.showWindow(false)
                }
            }

            override fun onFailure(call: Call<BasicSettingsResponse>, t:  Throwable) {
                call.cancel()
                pbLoadingBasicSettings?.visibility = View.GONE
            }
        })
    }


    private fun hideSplashAndDoMore() {
        pbLoadingBasicSettings?.visibility = View.GONE
        layoutSplash.startAnimation(
            AnimationUtils.loadAnimation(
                this@StartActivity,
                R.anim.slide_out_left
            )
        )
        layoutSplash.visibility = View.GONE
        if (myPreferenceManager!!.isLogIn) {
            doAfterSlidingFinished()
        } else {
            loadSlides()
        }
    }

    private fun loadSlides() {
        btnNext.setOnClickListener(this)
        val fragmentList = ArrayList<androidx.fragment.app.Fragment>()
        fragmentList.add(SlidePageFragment.createInstance("file:///android_asset/image/img_slide_1.jpg"))
        fragmentList.add(SlidePageFragment.createInstance("file:///android_asset/image/img_slide_2.jpg"))
        fragmentList.add(SlidePageFragment.createInstance("file:///android_asset/image/img_slide_3.jpg"))
        val vpAdapter = StartSlidesPagerAdapter(supportFragmentManager, fragmentList)
        vpSlides.adapter = vpAdapter
        vpSlides.addOnPageChangeListener(this)
        vpSlides.currentItem = 0
        pagerIndicator.setupWithViewPager(vpSlides, true)
        for (i in 0 until pagerIndicator.tabCount) {
            val tab = (pagerIndicator.getChildAt(0) as ViewGroup).getChildAt(i)
            val p = tab.layoutParams as ViewGroup.MarginLayoutParams
            p.setMargins(0, 0, resources.getDimension(R.dimen.indicator_padding).toInt(), 0)
            tab.requestLayout()
        }
        tvContent.text = content[0]
        Handler().postDelayed(autoNext, 3000)
        isScheduled = true
    }

    private fun doAfterSlidingFinished() {
        if (myPreferenceManager?.isLogIn!!) {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        } else {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    private val autoNext = object : Runnable {
        override fun run() {
            if (isScheduled)
                if (vpSlides.currentItem < content.size - 1) {
                    vpSlides.currentItem = vpSlides.currentItem + 1
                    Handler().postDelayed(this, 3000)
                } else {
                    doAfterSlidingFinished()
                }
        }
    }

    override fun onPageScrollStateChanged(p0: Int) {
        //do nothing
    }

    override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
        //do nothing
    }

    override fun onPageSelected(p0: Int) {
        if (p0 < content.size)
            tvContent.text = content[p0]
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnNext -> {
                if (vpSlides.currentItem < content.size - 1) {
                    vpSlides.currentItem = vpSlides.currentItem + 1
                } else {
                    isScheduled = false
                    doAfterSlidingFinished()
                }
            }
        }
    }

    private fun openAppInMarket(activity: Activity, appPackageName: String) {
        try {
            activity.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$appPackageName")
                )
            )
        } catch (anfe: android.content.ActivityNotFoundException) {
            activity.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                )
            )
        }

    }

    private fun checkAccessApp() {
        var apiInterface1 =
            APIClient.getInstanceAccess(this).client.create(APIInterface::class.java)
        val call =
            apiInterface1?.checkAccess(
                AccessParams(
                    packageName
                )
            )
        call?.enqueue(object : Callback<AccessResponse> {
            override fun onResponse(
                call: Call<AccessResponse>,
                response: Response<AccessResponse>
            ) {
                val accessResponse = response.body()
                if (accessResponse!!.activeCode == 0) {
                    finish()
                    return
                } else {
                    init()
                }
            }

            override fun onFailure(call: Call<AccessResponse>, t: Throwable) {
                finish()
                return
            }
        })
    }

}
