package com.mobitv.ott.activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import com.mobitv.ott.R
import com.mobitv.ott.fragment.*
import com.mobitv.ott.model.LiveTvChannelModel
import com.mobitv.ott.model.SearchResultModel
import com.mobitv.ott.other.*
import com.mobitv.ott.pojo.APIClient
import com.mobitv.ott.pojo.APIInterface
import com.mobitv.ott.pojo.ScardRegisterResponse
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.layout_live_tv_player.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : BaseActivity() {
    private var liveTvPlayerController: LiveTvPlayerController? = null
    private var currentPageTag: String = ""
    private var myPreferenceManager=MyPreferenceManager.getInstance(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarTransparent()
        setContentView(R.layout.activity_main)
        btnLiveTV.setOnClickListener(this)
        btnMovies.setOnClickListener(this)
        btnTvShows.setOnClickListener(this)
        btnPageMore.setOnClickListener(this)
        btnSetting.setOnClickListener(this)
        btnGoSearch.setOnClickListener(this)
        if(myPreferenceManager.isLogIn){
            var date_check= myPreferenceManager.getValue(Const.DATE_CHECK_SCARD,"")
            if(!date_check.equals(Utils.getSendTime())){
                var user_id= myPreferenceManager.getValue(Const.PHONE_NUMBER,"")
                getScradNumber(user_id)
            }
        }else{

        }
        goLiveTV()
        liveTvPlayerController = LiveTvPlayerController(layoutContainerLiveTvPlayer, this)
        liveTvPlayerController?.setUp()

    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnLiveTV -> {
                goLiveTV()
            }
            R.id.btnMovies -> {
                goMovies()
            }
            R.id.btnTvShows -> {
                goTvShows()
            }
            R.id.btnPageMore -> {
//                goPageMore()
                goPersonal()
            }
            R.id.btnSetting -> {
                goSettings()
            }
            R.id.btnGoSearch -> {
                goSearch()
            }
        }
    }

    private fun goLiveTV() {
        removePageMore()
        if (currentPageTag != LiveTvFragment.TAG) {
            currentPageTag = LiveTvFragment.TAG
            btnLiveTV.isSelected = true
            btnMovies.isSelected = false
            btnTvShows.isSelected = false
            btnPageMore.isSelected = false
            for (fragment in supportFragmentManager.fragments) {
                if (fragment != null) {
                    supportFragmentManager.beginTransaction().remove(fragment).commit()
                }
            }
            if (supportFragmentManager.findFragmentByTag(LiveTvFragment.TAG) == null) {
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction.add(R.id.layoutContent, LiveTvFragment.createInstance(), LiveTvFragment.TAG)
                    .commit()
            }
            tvTitlePage.text = getString(R.string.live_tv)
            btnSetting.visibility = View.INVISIBLE
            btnGoSearch.visibility = View.VISIBLE
            imgBackgroundTop.setImageResource(R.drawable.img_bg_title)
        } else {
            btnLiveTV.isSelected = true
        }
    }

    private fun goMovies() {
        removePageMore()
        if (currentPageTag != MoviesFragment.TAG) {
            currentPageTag = MoviesFragment.TAG
            btnLiveTV.isSelected = false
            btnMovies.isSelected = true
            btnTvShows.isSelected = false
            btnPageMore.isSelected = false
            for (fragment in supportFragmentManager.fragments) {
                if (fragment != null) {
                    supportFragmentManager.beginTransaction().remove(fragment).commit()
                }
            }
            if (supportFragmentManager.findFragmentByTag(MoviesFragment.TAG) == null) {
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction.add(R.id.layoutContent, MoviesFragment.createInstance(), MoviesFragment.TAG)
                    .commit()
            }
            tvTitlePage.text = getString(R.string.movies)
            btnSetting.visibility = View.INVISIBLE
            btnGoSearch.visibility = View.VISIBLE
            imgBackgroundTop.setImageResource(R.drawable.img_bg_title)
        } else {
            btnMovies.isSelected = true
        }
    }

    private fun goTvShows() {
        removePageMore()
        if (currentPageTag != TvShowsFragment.TAG) {
            currentPageTag = TvShowsFragment.TAG
            btnLiveTV.isSelected = false
            btnMovies.isSelected = false
            btnTvShows.isSelected = true
            btnPageMore.isSelected = false
            for (fragment in supportFragmentManager.fragments) {
                if (fragment != null) {
                    supportFragmentManager.beginTransaction().remove(fragment).commit()
                }
            }
            if (supportFragmentManager.findFragmentByTag(TvShowsFragment.TAG) == null) {
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction.add(R.id.layoutContent, TvShowsFragment.createInstance(), TvShowsFragment.TAG)
                    .commit()
            }
            tvTitlePage.text = getString(R.string.tv_shows)
            btnSetting.visibility = View.INVISIBLE
            btnGoSearch.visibility = View.VISIBLE
            imgBackgroundTop.setImageResource(R.drawable.img_bg_title)
        } else {
            btnTvShows.isSelected = true
        }
    }

    fun goPersonal() {
        if (currentPageTag != PersonalFragment.TAG) {
            currentPageTag = PersonalFragment.TAG
            removePageMore()
            btnLiveTV.isSelected = false
            btnMovies.isSelected = false
            btnTvShows.isSelected = false
            for (fragment in supportFragmentManager.fragments) {
                if (fragment != null) {
                    supportFragmentManager.beginTransaction().remove(fragment).commit()
                }
            }
            if (supportFragmentManager.findFragmentByTag(PersonalFragment.TAG) == null) {
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction.add(R.id.layoutContent, PersonalFragment.createInstance(), PersonalFragment.TAG)
                    .commit()
            }
            tvTitlePage.text = getString(R.string.personal)
            btnSetting.visibility = View.VISIBLE
            btnGoSearch.visibility = View.INVISIBLE
            imgBackgroundTop.setImageResource(R.drawable.img_bg_top_1_personal)
        }
    }
    private fun goPageMore() {
        val fragment = supportFragmentManager.findFragmentByTag(PageMoreFragment.TAG)
        if (fragment == null) {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.add(R.id.layoutContentPageMore, PageMoreFragment.createInstance(), PageMoreFragment.TAG)
                .commit()
            btnPageMore.isSelected = true
            btnLiveTV.isSelected = false
            btnMovies.isSelected = false
            btnTvShows.isSelected = false
        } else {
            supportFragmentManager.beginTransaction()
                .remove(supportFragmentManager.findFragmentByTag(PageMoreFragment.TAG)!!).commit()
            btnPageMore.isSelected = false
            when (currentPageTag) {
                LiveTvFragment.TAG -> {
                    btnLiveTV.isSelected = true
                }
                MoviesFragment.TAG -> {
                    btnMovies.isSelected = true
                }
                TvShowsFragment.TAG -> {
                    btnTvShows.isSelected = true
                }
            }
        }
    }

    private fun removePageMore(): Boolean {
        btnPageMore.isSelected = false
        val fragment = supportFragmentManager.findFragmentByTag(PageMoreFragment.TAG)
        if (fragment != null) {
            supportFragmentManager.beginTransaction().remove(fragment).commit()
            return true
        }
        return false
    }

    private fun goSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

    }

    private fun goSearch() {
        liveTvPlayerController?.pauseLiveTv()
        if (supportFragmentManager.findFragmentByTag(SearchFragment.TAG) == null) {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(SearchFragment.TAG)
            fragmentTransaction.setCustomAnimations(
                R.anim.in_from_bottom,
                R.anim.out_to_bottom,
                R.anim.in_from_bottom,
                R.anim.out_to_bottom
            )
            fragmentTransaction.add(R.id.containerMain, SearchFragment.createInstance(), SearchFragment.TAG)
                .commit()
        }
    }

    override fun onBackPressed() {
        if (liveTvPlayerController != null && (liveTvPlayerController!!.isCollapsed
                    || !liveTvPlayerController!!.isShown)
        ) {
            val searchFrag = supportFragmentManager.findFragmentByTag(SearchFragment.TAG)
            if (searchFrag == null) {
                finish()
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            } else {
                doCloseSearchWindow()
            }
        } else {
            liveTvPlayerController?.onBackPressed()
        }
    }

    fun startStreamingLiveTv(liveTvChannelModel: LiveTvChannelModel) {
        liveTvPlayerController?.showPlayerAndStartStreaming(
            liveTvChannelModel,
            layoutBottomNav.height,
            getStatusBarHeight()
        )
    }

    //call from search
    fun goResult(model: SearchResultModel) {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStackImmediate()
        }
        Handler().postDelayed({
            when (model.type) {
                Const.TYPE_CHANNEL -> {
                    val channelModel = LiveTvChannelModel()
                    channelModel.channelNumber = model.channelNumber
                    channelModel.streamUrl = model.streamUrl
                    channelModel.encryption = model.encryption
                    startStreamingLiveTv(channelModel)
                }
                Const.TYPE_FILM -> {
                    AppUtils.goVodDetails(this, model.id, Const.FILM)
                }
                Const.TYPE_TV_SHOWS_SEASON -> {
                    AppUtils.goVodDetails(this, model.id, Const.TYPE_TV_SHOWS_SEASON)
                }
            }
        }, 200)
    }

    fun doCloseSearchWindow() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStackImmediate()
        }
        AppUtils.hideSoftKeyboard(this)
        if (liveTvPlayerController != null && !liveTvPlayerController!!.isClosedBySwipe)
            liveTvPlayerController?.playLiveTv()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        liveTvPlayerController?.onConfigurationChanged(newConfig)
    }

    override fun onStart() {
        super.onStart()
        liveTvPlayerController?.onStart()
    }

    override fun onResume() {
        super.onResume()
        liveTvPlayerController?.onResume()
    }

//    override fun onStop() {
//        super.onStop()
//        liveTvPlayerController?.onStop()
//    }

    override fun onDestroy() {
        super.onDestroy()
        liveTvPlayerController?.onStop()
    }

    override fun onPause() {
        super.onPause()
        liveTvPlayerController?.pauseLiveTv()
    }


    private fun getScradNumber(user_id: String){
        var scardNum = myPreferenceManager.getValue(Const.SCARD_NUM, "0")
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
                    myPreferenceManager.setValue(Const.DATE_CHECK_SCARD,Utils.getSendTime());
                    myPreferenceManager.setValue(Const.SCARD_NUM, responseObject?.response_object.scard_number)
                    myPreferenceManager.setValue(Const.SCARD_EXPIRED, responseObject?.response_object.expired_date)
//                    Log.d("TAG DỮ LIỆU: ", "DỮ LIỆU TRẢ VỀ")
                } else {

                }
            }
            override fun onFailure(call: Call<ScardRegisterResponse>, t: Throwable) {
                Log.d("Log Loi: ",  t.message.toString())
            }
        })
    }

//    override fun onRestart() {
//        // TODO Auto-generated method stub
//        super.onRestart()
//        var user_id= myPreferenceManager.getValue(Const.PHONE_NUMBER,"")
//        getScradNumber(user_id)
//    }

}
