package com.mobitv.ott.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.media.AudioManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.mobitv.ott.R
import com.mobitv.ott.adapter.ListEpisodeRVAdapter
import com.mobitv.ott.adapter.ListVodRVAdapter
import com.mobitv.ott.adapter.OnItemClickListener
import com.mobitv.ott.dialog.LoadingDialog
import com.mobitv.ott.dialog.MyAlertDialog
import com.mobitv.ott.dialog.NotificationDialog
import com.mobitv.ott.model.TvSeason
import com.mobitv.ott.model.VodDetails
import com.mobitv.ott.model.VodModel
import com.mobitv.ott.other.*
import com.mobitv.ott.pojo.*
import com.mobitv.ott.task.InsertOrUpdateWatchDBTask
import kotlinx.android.synthetic.main.activity_vod_details.*
import kotlinx.android.synthetic.main.layout_rating_score.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VodDetailsActivity : BaseActivity() {
    private val trackingInterval = 1000L
    private var apiInterface: APIInterface? = null
    private lateinit var loadingDialog: LoadingDialog
    private val mHandler = Handler()
    private var tm: TelephonyManager? = null
    private var callStateListener: CallStateListener = CallStateListener(this)
    private var listVodRVAdapter: ListVodRVAdapter? = null
    private var listEpisodeRVAdapter: ListEpisodeRVAdapter? = null
    private val listVodRelated = ArrayList<VodModel>()
    private val listEpisode = ArrayList<VodModel>()

    private var currentVideoID = 0
    private var currentVideoIcon: String? = ""
    private var currentVideoType = ""
    private var currentEpisodeID = 0
    private var currentPosition = 0L
    private var isSoughtToPos = false
    private var isLostConnection = false
    private var isFavourite = false
    private var timeFactor = 1000 * 1000

    private var audioManager: AudioManager? = null
    private var isPlayingWhenGoOut = false

    private var countDownTimer: CountDownTimer? = null
    private var headsetConnected = false

    private var insertDataIntoDBTask: InsertOrUpdateWatchDBTask? = null
    private var url = ""
    private var nameVod = ""
    private var vodDetail: VodDetails? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarTransparent()
        setContentView(R.layout.activity_vod_details)
        btnBackVodDetails.setOnClickListener(this)
        btnSaveVodDetails.setOnClickListener(this)
        btnRateVodDetails.setOnClickListener(this)
        btnCommentVodDetails.setOnClickListener(this)
        btnDownloadVodDetails.setOnClickListener(this)
        btnDetailsPlay.setOnClickListener(this)

        imvVodCover.post {
            val set = ConstraintSet()
            set.clone(layoutVodDetails)
            set.setMargin(cvAvatar.id, ConstraintSet.TOP, imvVodCover.height * 3 / 4)
            set.applyTo(layoutVodDetails)
        }

        loadingDialog = LoadingDialog(this)
        val id = intent!!.getIntExtra(Const.ID, 0)
        currentVideoType = intent!!.getStringExtra(Const.TYPE)
        if (currentVideoType == Const.TYPE_TV_SHOWS_SEASON) {
            layoutDownloadVodDetails.visibility = View.INVISIBLE
            tvMoviesRelated.visibility = View.GONE
            rvRelated.layoutManager = LinearLayoutManager(
                this,
                RecyclerView.VERTICAL,
                false
            )
        } else {
            rvRelated.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 4)
            rvRelated.addItemDecoration(GridItemPersonalInsetDecoration(this, 4))
        }
        loadData(id)

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val receiverFilter = IntentFilter(Intent.ACTION_HEADSET_PLUG)
        registerReceiver(headSetStateReceiver, receiverFilter)
    }

    private fun showVodDetails(vodDetails: VodDetails) {
        vodDetail = vodDetails
        currentVideoID = vodDetails.id
        currentVideoIcon = vodDetails.iconUrl!!

        Glide.with(this)
            .load(vodDetails.imageUrl)
            .apply(
                RequestOptions().placeholder(R.drawable.img_placeholder_landscape).error(R.drawable.img_placeholder_landscape).signature(
                    ObjectKey(GlobalParams.VERSION_CODE)
                )
            )
            .into(imvVodCover)
        Glide.with(this)
            .load(vodDetails.iconUrl)
            .apply(
                RequestOptions().placeholder(R.drawable.img_placeholder_portrait).error(R.drawable.img_placeholder_portrait).signature(
                    ObjectKey(GlobalParams.VERSION_CODE)
                )
            )
            .into(imvVodAvatar)
        tvVodName.text = vodDetails.title
        tvVodYear.text = vodDetails.year
        val contentRating = if (vodDetails.contentRating > 0) {
            vodDetails.contentRating.toString() + "+"
        } else {
            ""
        }
        tvVodContentRating.text = contentRating
        val duration = vodDetails.duration.toString() + " " + getString(R.string.minute)
        tvVodDuration.text = duration
        etvVodDescription.text = vodDetails.description
        etvVodDescription.setCollapse(true)
        tvViewAmount.text = vodDetails.clicks.toString()
        //format rating score
        val ratingScore = vodDetails.rate.toDouble().toString()
        val splitRatingScore = ratingScore.split(".")
        tvRatingInteger.text = splitRatingScore[0]
        val rDecimal = "." + splitRatingScore[1]
        tvRatingDecimal.text = rDecimal
        rbScore.progress = splitRatingScore[0].toInt()

        val director = getString(R.string.director) + ": " + vodDetails.director
        tvDirector.text = director
        val starring = getString(R.string.actor) + ": " + vodDetails.starring
//        tvActor.text = starring
        AppUtils.setTextHtml(tvActor, starring)
        listVodRelated.clear()
        var vodModel: VodModel
        for (i in 0 until vodDetails.relatedFilms.size) {
            vodModel = VodModel()
            vodModel.id = vodDetails.relatedFilms[i].id
            vodModel.iconUrl = vodDetails.relatedFilms[i].iconUrl
            vodModel.imageUrl = vodDetails.relatedFilms[i].imageUrl
            listVodRelated.add(vodModel)
        }
        listVodRVAdapter = ListVodRVAdapter(this, listVodRelated, false)
        listVodRVAdapter?.setItemClickListener(vodRelatedListener)
        rvRelated.adapter = listVodRVAdapter
        tvEpisode.visibility = View.GONE
        svContainer.scrollTo(0, 0)

        //check is favourite
        isFavourite = vodDetails.isFavourite == Const.YES
        updateFavourite()
    }

    private fun showTvSeasonDetails(tvSeason: TvSeason) {
        vodDetail = VodDetails()
        vodDetail?.id = tvSeason.id
        currentVideoID = tvSeason.id
        currentVideoIcon = tvSeason.iconUrl
        vodDetail?.isCanWatched = tvSeason.isCanWatched

        Glide.with(this)
            .load(tvSeason.imageUrl)
            .apply(
                RequestOptions().placeholder(R.drawable.img_placeholder_landscape).error(R.drawable.img_placeholder_landscape).signature(
                    ObjectKey(GlobalParams.VERSION_CODE)
                )
            )
            .into(imvVodCover)
        Glide.with(this)
            .load(tvSeason.iconUrl)
            .apply(
                RequestOptions().placeholder(R.drawable.img_placeholder_portrait).error(R.drawable.img_placeholder_portrait).signature(
                    ObjectKey(GlobalParams.VERSION_CODE)
                )
            )
            .into(imvVodAvatar)
        tvVodName.text = tvSeason.title
        etvVodDescription.text = tvSeason.description
        tvVodYear.text = tvSeason.year.toString()
        val contentRating = if (tvSeason.contentRating > 0) {
            tvSeason.contentRating.toString() + "+"
        } else {
            ""
        }
        tvVodContentRating.text = contentRating
        val epNumber = tvSeason.totalEpisode.toString() + " " + getString(R.string.episode)
        tvVodDuration.text = epNumber
        //format rating score
        val ratingScore = tvSeason.rate.toDouble().toString()
        val splitRatingScore = ratingScore.split(".")
        tvRatingInteger.text = splitRatingScore[0]
        val rDecimal = "." + splitRatingScore[1]
        tvRatingDecimal.text = rDecimal
        rbScore.progress = splitRatingScore[0].toInt()

        val director =
            getString(R.string.director) + ": " + if (tvSeason.director != null) tvSeason.director else "Đang cập nhật"
        tvDirector.text = director
        val starring =
            getString(R.string.actor) + ": " + if (tvSeason.starring != null) tvSeason.starring else "Đang cập nhật"
        AppUtils.setTextHtml(tvActor, starring)
//        tvActor.text = starring

        listEpisode.clear()
        var vodModel: VodModel
        for (i in 0 until tvSeason.episodes.size) {
            vodModel = VodModel()
            vodModel.id = tvSeason.episodes[i].id
            vodModel.iconUrl = tvSeason.iconUrl
            vodModel.imageUrl = tvSeason.imageUrl
            vodModel.title = tvSeason.episodes[i].title
            vodModel.duration = tvSeason.episodes[i].duration
            vodModel.description = tvSeason.episodes[i].description
            vodModel.url = tvSeason.episodes[i].url
            vodModel.clicks = tvSeason.episodes[i].clicks
            vodModel.numberInParent = tvSeason.episodes[i].numberInParent
            listEpisode.add(vodModel)
        }
        listEpisodeRVAdapter = ListEpisodeRVAdapter(this, listEpisode)
        listEpisodeRVAdapter?.setItemClickListener(episodeClickListener)
        rvRelated.adapter = listEpisodeRVAdapter

//        tvEpisode.visibility = View.VISIBLE

        //check is favourite
        isFavourite = tvSeason.isFavourite == Const.YES
        updateFavourite()
    }

    private fun reloadDataTvSeason(episode: VodModel) {
        tvViewAmount.text = episode.clicks.toString()
//        tvEpisode.text = episode.title
        etvVodDescription.text = episode.description
        etvVodDescription.setCollapse(true)
        currentEpisodeID = episode.id
        svContainer.scrollTo(0, 0)
    }


    private val vodRelatedListener = object : OnItemClickListener {
        override fun onItemClick(tag: String, position: Int) {
            loadData(listVodRelated[position].id)
        }

        override fun onItemLongClick(tag: String, position: Int) {
        }

    }

    private val episodeClickListener = object : OnItemClickListener {
        override fun onItemClick(tag: String, position: Int) {
//            reloadDataTvSeason(listEpisode[position])

            currentEpisodeID = listEpisode[position].id
            doPlay()
        }

        override fun onItemLongClick(tag: String, position: Int) {
        }

    }


    private fun loadData(id: Int) {
        loadingDialog.showWindow()
        apiInterface = APIClient.getInstance(this).client.create(APIInterface::class.java)
        if (currentVideoType == Const.TYPE_TV_SHOWS_SEASON) {
            System.out.println("Vũ Đăng Hiển TV Session Detail");
            val call =
                apiInterface?.doGetTvSeasonDetails(id)
            call?.enqueue(object : Callback<TvSeasonDetailsResponse> {
                override fun onResponse(
                    call: Call<TvSeasonDetailsResponse>,
                    categoryResponse: Response<TvSeasonDetailsResponse>
                ) {
                    loadingDialog.closeWindow()
                    val responseObject = categoryResponse.body()
                    if (responseObject!!.statusCode == Const.STATUS_CODE_SUCCESS) {
                        val tvSeasonDetails = responseObject!!.tvSeason
//                        if (tvSeasonDetails != null) {

//                        Toast.makeText(
//                            this@VodDetailsActivity,
//                            tvSeasonDetails.isCanWatched.toString(),
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        Log.e("CANWATCH", tvSeasonDetails.isCanWatched.toString());
                        showTvSeasonDetails(tvSeasonDetails)
//                        } else {
//                            finish()
//                        }
                    }
                }

                override fun onFailure(call: Call<TvSeasonDetailsResponse>, t: Throwable) {
                    call.cancel()
                    loadingDialog.closeWindow()
                }
            })
        } else {
            System.out.println("Vũ Đăng Hiển TV Version");
            val call =
                apiInterface?.doGetVodDetails(id)
            call?.enqueue(object : Callback<VodDetailsResponse> {
                override fun onResponse(
                    call: Call<VodDetailsResponse>,
                    categoryResponse: Response<VodDetailsResponse>
                ) {
                    loadingDialog.closeWindow()
                    val responseObject = categoryResponse.body()
                    if (responseObject!!.statusCode == Const.STATUS_CODE_SUCCESS) {
                        val vodDetails = responseObject!!.vodDetails
//                        Toast.makeText(
//                            this@VodDetailsActivity,
//                            vodDetails.isCanWatched.toString(),
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        Log.e("CANWATCH", vodDetails.isCanWatched.toString());
//                        if (vodDetails != null) {
                        showVodDetails(vodDetails)
//                        } else {
//                            finish()
//                        }
                    }
                }

                override fun onFailure(call: Call<VodDetailsResponse>, t: Throwable) {
                    call.cancel()
                    loadingDialog.closeWindow()
                }
            })
        }

    }

    private fun setVodManager() {
        var vodManager = VodManager.getInstance();
        vodManager.currentEpisodeID = currentEpisodeID
        vodManager.episodeList = listEpisode
        vodManager.currentVideoType = currentVideoType
        vodManager.vodDetails = vodDetail
    }

    private fun doPlay() {
        setVodManager()
        if (vodDetail == null || (currentVideoType == Const.TYPE_TV_SHOWS_SEASON && listEpisode.size == 0)) {
            return
        }
        if (!vodDetail!!.isCanWatched) {
            MyAlertDialog.showDialogCanWatch(this)
        } else {
            var intent = Intent(this, PlayerVodActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnBackVodDetails -> {
                doBack()
            }
            R.id.btnDetailsPlay -> {
                doPlay()
            }
            //player

            R.id.btnCommentVodDetails -> {
                val intent = Intent(this, VodCommentActivity::class.java)
                intent.putExtra(Const.ICON, currentVideoIcon)
                intent.putExtra(Const.ID, currentVideoID)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            R.id.btnSaveVodDetails -> {
                doAddFavourite()
            }
        }
    }


    override fun onStart() {
        super.onStart()
        tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        tm?.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE)
        val intentFilter = IntentFilter()
        intentFilter.addAction(Const.FLAG_INCOMING_CALL)
        intentFilter.addAction(Const.FLAG_CALL_ENDED)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(incomingCallReceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
        tm?.listen(callStateListener, PhoneStateListener.LISTEN_NONE)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(incomingCallReceiver)
    }

    private val incomingCallReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                if (intent.action == Const.FLAG_INCOMING_CALL) {
                } else if (intent.action == Const.FLAG_CALL_ENDED
                    && resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
                ) {
//                    play()
                }
            }
        }
    }


    override fun onBackPressed() {
//        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            doBackPlayer()
//        } else {
        doBack()
//        }
    }

    private fun doBack() {
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }


    private fun getCurrentEpPosition(): Int {
        var pos = 0
        for (i in 0 until listEpisode.size) {
            if (currentEpisodeID == listEpisode[i].id) {
                pos = i
            }
        }
        return pos
    }

    private fun goToEpisode(isNext: Boolean) {
        countDownTimer?.cancel()
        val currentEpPos = getCurrentEpPosition()
        if (isNext) {
            if (currentEpPos < listEpisode.size - 1)
                reloadDataTvSeason(listEpisode[currentEpPos + 1])
        } else {
            if (currentEpPos > 0)
                reloadDataTvSeason(listEpisode[currentEpPos - 1])
        }
    }

    private fun doAddFavourite() {
        if (MyPreferenceManager.getInstance(this).isLogIn) {
            loadingDialog.showWindow()
            apiInterface = APIClient.getInstance(this).client.create(APIInterface::class.java)
            if (isFavourite) {
                val call =
                    apiInterface?.doRemoveFavourite(currentVideoID)
                call?.enqueue(object : Callback<CommonResponse> {
                    override fun onResponse(
                        call: Call<CommonResponse>,
                        response: Response<CommonResponse>
                    ) {
                        loadingDialog.closeWindow()
                        val responseObject = response.body()
                        if (responseObject!!.statusCode == Const.STATUS_CODE_SUCCESS) {
                            isFavourite = false
                            updateFavourite()
                            GlobalParams.isReloadDataPager = true
                        } else {
                            val notificationDialog = NotificationDialog(this@VodDetailsActivity)
                            notificationDialog.setMessageDialog(responseObject!!.extraData)
                            notificationDialog.showWindow(false)
                        }
                    }

                    override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                        call.cancel()
                        loadingDialog.closeWindow()
                    }
                })
            } else {
                val call =
                    apiInterface?.doAddFavourite(VodFavouriteParams(currentVideoID))
                call?.enqueue(object : Callback<CommonResponse> {
                    override fun onResponse(
                        call: Call<CommonResponse>,
                        response: Response<CommonResponse>
                    ) {
                        loadingDialog.closeWindow()
                        val responseObject = response.body()
                        if (responseObject!!.statusCode == Const.STATUS_CODE_SUCCESS) {
                            isFavourite = true
                            updateFavourite()
                            GlobalParams.isReloadDataPager = true
                        } else {
                            val notificationDialog = NotificationDialog(this@VodDetailsActivity)
                            notificationDialog.setMessageDialog(responseObject!!.extraData)
                            notificationDialog.showWindow(false)
                        }
                    }

                    override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                        call.cancel()
                        loadingDialog.closeWindow()
                    }
                })
            }
        } else {
            val notificationDialog = NotificationDialog(this)
            notificationDialog.setMessageDialog(getString(R.string.request_login_message))
            notificationDialog.showWindow(false)
        }
    }

    private fun updateFavourite() {
        if (isFavourite) {
            btnSaveVodDetails.setImageResource(R.drawable.ic_save_selected)
            btnSaveVodDetails.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#FFC107"))
        } else {
            btnSaveVodDetails.setImageResource(R.drawable.ic_save)
            btnSaveVodDetails.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
        }
    }

    override fun onDestroy() {
        unregisterReceiver(headSetStateReceiver)
        super.onDestroy()
    }

    private val headSetStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.hasExtra("state")) {
                if (intent.getIntExtra("state", -1) == 0) {
                    headsetConnected = false
                } else {
                    headsetConnected = true
                }
            }
        }
    }
}

