package com.mobitv.ott.other

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.media.AudioManager
import android.net.Uri
import android.os.CountDownTimer
import android.os.Handler
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ext.rtmp.RtmpDataSourceFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.util.Util
import com.mobitv.ott.R
import com.mobitv.ott.activity.BaseActivity
import com.mobitv.ott.adapter.DailyEPGRVAdapter
import com.mobitv.ott.adapter.OnItemClickListener
import com.mobitv.ott.fragment.SearchFragment
import com.mobitv.ott.model.DailyEPGModel
import com.mobitv.ott.model.LiveTvChannelModel
import com.mobitv.ott.pojo.APIClient
import com.mobitv.ott.pojo.APIInterface
import com.mobitv.ott.pojo.DailyEPGResponse
import com.mobitv.ott.pojo.LiveTvChannelDetailsResponse
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.layout_live_tv_player.*
import kotlinx.android.synthetic.main.layout_scaling_mode.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.TimeZone.*
import kotlin.math.absoluteValue

class LiveTvPlayerController(
    override val containerView: ConstraintLayout,
    val activity: BaseActivity
) :
    LayoutContainer,
    View.OnClickListener, View.OnTouchListener, AudioManager.OnAudioFocusChangeListener {
    var isShown = false
    var isCollapsed = false
    private var verticalPlayerLayoutSet = ConstraintSet()
    private var horizontalPlayerLayoutSet = ConstraintSet()
    private val listDailyEPG: ArrayList<DailyEPGModel> = ArrayList()
    private var dailyEPGRVAdapter: DailyEPGRVAdapter? = null
    private var apiInterface: APIInterface? = null
    private var tm: TelephonyManager? = null
    private var callStateListener: CallStateListener? = null
    private val mHandler = Handler()
    private var mainBottomNavHeight = 0f
    private var statusBarHeight = 0f
    private var offsetTranslationX = 0f
    private var offsetTranslationY = 0f
    private val duration = 300L
    private var scaleCollapseX = 0f
    private var scaleCollapseY = 0f
    private var lastX = 0f
    private var lastY = 0f
    private var posCollapsedX = 0f
    private var posCollapsedY = 0f
    var isClosedBySwipe = false
    private lateinit var mVelocityTracker: VelocityTracker
    private var widthPlayerView = 0f
    private var heightPlayerView = 0f
    private var widthPlayerViewCollapsed = 0f
    private var heightPlayerViewCollapsed = 0f
    private var isExpandByMove = false
    private var isCollapseByMove = false
    private var isInMoveX = false
    private var isInMoveY = false
    private var isHandleAction = true
    private var lastTime = 0L
    private var isLostConnection = false
    private var audioManager: AudioManager? = null
    private var isPlayingWhenGoOut = false
    private var countDownTimer: CountDownTimer? = null
    private var channelNumber = 0
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var isRelease = false

    private var player: SimpleExoPlayer? = null

    fun setUp() {
        btnCollapse.setOnClickListener(this)
        btnMoreLiveTv.setOnClickListener(this)
        btnToggleDisplay.setOnClickListener(this)
        layoutLiveTvPlayer.setOnClickListener(this)
        horizontalPlayerLayoutSet.connect(
            layoutLiveTvPlayer.id,
            ConstraintSet.START,
            ConstraintSet.PARENT_ID,
            ConstraintSet.START,
            0
        )
        horizontalPlayerLayoutSet.connect(
            layoutLiveTvPlayer.id,
            ConstraintSet.END,
            ConstraintSet.PARENT_ID,
            ConstraintSet.END,
            0
        )
        horizontalPlayerLayoutSet.connect(
            layoutLiveTvPlayer.id,
            ConstraintSet.TOP,
            ConstraintSet.PARENT_ID,
            ConstraintSet.TOP,
            0
        )
        horizontalPlayerLayoutSet.connect(
            layoutLiveTvPlayer.id,
            ConstraintSet.BOTTOM,
            ConstraintSet.PARENT_ID,
            ConstraintSet.BOTTOM,
            0
        )
        callStateListener = CallStateListener(activity)
        layoutLiveTvPlayer.setOnTouchListener(this)

        layoutScalingMode.setOnClickListener(this)
        btnModeCrop.setOnClickListener(this)
        btnModeFit.setOnClickListener(this)
        btnModeFill.setOnClickListener(this)

        audioManager = activity.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        linearLayoutManager = LinearLayoutManager(
            activity,
            RecyclerView.VERTICAL,
            false
        )

        rvDailyEPG.layoutManager = linearLayoutManager
        dailyEPGRVAdapter = DailyEPGRVAdapter(activity, listDailyEPG)
        dailyEPGRVAdapter?.setItemClickListener(itemEPGClickListener)
        rvDailyEPG.adapter = dailyEPGRVAdapter
        tvDateTimePlayer.text = AppUtils.formatCurrentTimeToDateTime(System.currentTimeMillis())
    }

    fun showPlayerAndStartStreaming(
        liveTvChannelModel: LiveTvChannelModel,
        mainBottomNavHeight: Int,
        statusBarHeight: Int
    ) {
        this.mainBottomNavHeight = mainBottomNavHeight.toFloat()
        this.statusBarHeight = statusBarHeight.toFloat()
        val set = ConstraintSet()
        set.clone(containerView)
        set.constrainHeight(vTopLiveTvPlayer.id, statusBarHeight)
        set.applyTo(containerView)
        channelNumber = liveTvChannelModel.channelNumber
        if (!isShown) {
            isShown = true
            containerView.visibility = View.VISIBLE
            val animation = AnimationUtils.loadAnimation(activity, R.anim.in_from_bottom)
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    verticalPlayerLayoutSet.clone(containerView)

                    widthPlayerView = layoutLiveTvPlayer.width.toFloat()
                    heightPlayerView = layoutLiveTvPlayer.height.toFloat()
                    widthPlayerViewCollapsed = widthPlayerView * 2 / 3
                    heightPlayerViewCollapsed = widthPlayerViewCollapsed * 9 / 16

                    scaleCollapseX = widthPlayerViewCollapsed / widthPlayerView
                    scaleCollapseY = heightPlayerViewCollapsed / heightPlayerView
                    offsetTranslationX = (widthPlayerView - widthPlayerViewCollapsed) / 2f
                    offsetTranslationY =
                        (heightPlayerView - heightPlayerViewCollapsed) / 2f + (layoutDailyEPG.height - mainBottomNavHeight)
                    posCollapsedX = layoutLiveTvPlayer.x + offsetTranslationX
                    posCollapsedY = layoutLiveTvPlayer.y + offsetTranslationY
                    isRelease = false
                    loadChannelDetails(liveTvChannelModel.id)
                }

                override fun onAnimationStart(animation: Animation?) {
                }
            })
            containerView.startAnimation(animation)
        } else {
            if (isCollapsed) {
                doExpand()
            }
            mHandler.postDelayed({
                isRelease = true
                loadChannelDetails(liveTvChannelModel.id)
            }, 500)
        }
    }

    private fun hidePlayerController() {
        mHandler.removeCallbacks(countToHideController)
        if (layoutPlayerController.visibility == View.VISIBLE) {
            layoutPlayerController.visibility = View.INVISIBLE
            layoutPlayerController.startAnimation(
                AnimationUtils.loadAnimation(
                    activity,
                    R.anim.fade_out
                )
            )
        }
    }

    private val animatorListener = object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {
        }

        override fun onAnimationEnd(animation: Animator?) {
            if (isCollapsed) {
                layoutDailyEPG.visibility = View.INVISIBLE
                vBorderPlayerView.visibility = View.VISIBLE
                if (isClosedBySwipe) {
                    layoutLiveTvPlayer.visibility = View.INVISIBLE
                } else {
                    layoutLiveTvPlayer.visibility = View.VISIBLE
                }
            } else {
                layoutDailyEPG.visibility = View.VISIBLE
                vBorderPlayerView.visibility = View.INVISIBLE
                layoutLiveTvPlayer.visibility = View.VISIBLE
            }
        }

        override fun onAnimationCancel(animation: Animator?) {
        }

        override fun onAnimationStart(animation: Animator?) {
            layoutDailyEPG.visibility = View.VISIBLE
        }

    }

    private fun loadChannelDetails(channelID: Int) {
        System.out.println("______________Get Detail Live______________________")
        listDailyEPG.clear()
        val deviceTimezone = getDefault().rawOffset / 3600 / 1000
        apiInterface = APIClient.getInstance(activity).client.create(APIInterface::class.java)
        val call =
            apiInterface?.doGetChannelDetails(
                channelID, deviceTimezone, 0
            )
        call?.enqueue(object : Callback<LiveTvChannelDetailsResponse> {
            override fun onResponse(
                call: Call<LiveTvChannelDetailsResponse>,
                response: Response<LiveTvChannelDetailsResponse>
            ) {
                layoutLoadingDailyEPG?.visibility = View.GONE
                val responseObject = response.body()
                if (responseObject != null && responseObject.statusCode == Const.STATUS_CODE_SUCCESS) {
                    setUpPlayer(
                        responseObject.responseObject.streamUrl,
                        responseObject.responseObject.encryption,
                        isRelease
                    )
                    var model: DailyEPGModel
                    for (i in 0 until responseObject.responseObject.dailyEPGModelList.size) {
                        model = responseObject.responseObject.dailyEPGModelList[i]
                        listDailyEPG.add(model)
                    }
                    updateEPG()
                    dailyEPGRVAdapter?.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<LiveTvChannelDetailsResponse>, t: Throwable) {
                call.cancel()
                layoutLoadingDailyEPG?.visibility = View.GONE
            }
        })
    }

    private fun loadDailyEpg(channelNumber: Int) {
        tvDateTimePlayer.text = AppUtils.formatCurrentTimeToDateTime(System.currentTimeMillis())
        listDailyEPG.clear()
        val deviceTimezone = getDefault().rawOffset / 3600 / 1000
        apiInterface = APIClient.getInstance(activity).client.create(APIInterface::class.java)
        val call =
            apiInterface?.doGetDailyEPG(
                channelNumber, deviceTimezone, 0
            )
        call?.enqueue(object : Callback<DailyEPGResponse> {
            override fun onResponse(
                call: Call<DailyEPGResponse>,
                response: Response<DailyEPGResponse>
            ) {
                layoutLoadingDailyEPG?.visibility = View.GONE
                val listResponseObject = response.body()
                if (listResponseObject != null && listResponseObject.statusCode == Const.STATUS_CODE_SUCCESS) {
                    var model: DailyEPGModel
                    for (i in 0 until listResponseObject.listModel.size) {
                        model = listResponseObject.listModel[i]
                        listDailyEPG.add(model)
                    }
                    updateEPG()
                    dailyEPGRVAdapter?.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<DailyEPGResponse>, t: Throwable) {
                call.cancel()
                layoutLoadingDailyEPG?.visibility = View.GONE
            }
        })
    }

    private val itemEPGClickListener = object : OnItemClickListener {
        override fun onItemClick(tag: String, position: Int) {
            if (position < listDailyEPG.size && !listDailyEPG[position].isDisable) {
                //nothing
            }
        }

        override fun onItemLongClick(tag: String, position: Int) {
        }

    }

    private fun setUpPlayer(streamURL: String?, encryption: Int, isRelease: Boolean) {
        if (isRelease) {
            releasePlayer()
        }
//        if (!streamURL.isNullOrEmpty()) {
        btnTogglePlayPause.setOnClickListener(this)
//            btnTogglePlayPause.visibility = View.INVISIBLE
        val scalingMode = MyPreferenceManager.getInstance(activity)
            .getValue(Const.PLAYER_SCALING_MODE, AspectRatioFrameLayout.RESIZE_MODE_FILL)
        playerViewTv.resizeMode = scalingMode
        updateUIScalingMode()
//            playLink("http://45.121.163.67/quochoi/quochoi.isml/index.m3u8")
        playLink(streamURL!!)
//        } else {
//            playerViewTv.clearFocus()
//            tvLiveStreamLoadingError.visibility = View.VISIBLE
//        }

//        old
//        if (isRelease) {
//            pvLiveTv.playerController.release()
//            vBlackLiveTv.visibility = View.VISIBLE
//        }
//        if (!streamURL.isNullOrEmpty()) {
//            btnTogglePlayPause.setOnClickListener(this)
//            btnTogglePlayPause.visibility = View.INVISIBLE
//
//            playerConfigBuilder = PlayerConfig.Builder(streamURL)
//            playerConfigBuilder?.live(true)
//            playerConfigBuilder?.preservePlayerViewSurface(true)
//            if (encryption == 1) {
//                val drmConfiguration = DrmTodayConfiguration.Builder(
//                    DrmTodayConfiguration.DRMTODAY_STAGING,
//                    "purchase",
//                    "sessionId",
//                    "mobitv",
//                    "",
//                    Drm.BestAvailable
//                )
//                playerConfigBuilder?.drmConfiguration(drmConfiguration.get())
//            }
//
//            val scalingMode = MyPreferenceManager.getInstance(activity)
//                .getValue(Const.PLAYER_SCALING_MODE, PlayerView.SCALING_MODE_STRETCH)
//            pvLiveTv.scalingMode = scalingMode
//            updateUIScalingMode()
//            pvLiveTv.playerController.setHdPlaybackEnabled(
//                SdkConsts.ALLOW_HD_CLEAR_CONTENT
//                        or SdkConsts.ALLOW_HD_DRM_SOFTWARE
//                        or SdkConsts.ALLOW_HD_DRM_ROOT_OF_TRUST
//                        or SdkConsts.ALLOW_HD_DRM_SECURE_MEDIA_PATH
//            )
//            playLiveTv()
//        } else {
//            tvLiveStreamLoadingError.visibility = View.INVISIBLE
//        }
    }

    private var playerExoListener: Player.EventListener? = null
    private fun initPlayerListenner() {
        if (playerExoListener == null) {
            playerExoListener = object : Player.EventListener {
                override fun onPlaybackParametersChanged(p0: PlaybackParameters?) {
                }

                override fun onSeekProcessed() {
                }

                override fun onTracksChanged(p0: TrackGroupArray?, p1: TrackSelectionArray?) {
                }

                override fun onPlayerError(p0: ExoPlaybackException?) {
                    tvLiveStreamLoadingError.text = activity.getString(R.string.loading_error)
                    tvLiveStreamLoadingError.visibility = View.VISIBLE
                    btnTogglePlayPause.setImageResource(R.drawable.ic_play)
                    pbLiveTvPlayer.visibility = View.INVISIBLE
                }

                override fun onLoadingChanged(p0: Boolean) {
                }

                override fun onPositionDiscontinuity(p0: Int) {
                }

                override fun onRepeatModeChanged(p0: Int) {
                }

                override fun onShuffleModeEnabledChanged(p0: Boolean) {
                }

                override fun onTimelineChanged(p0: Timeline?, p1: Any?, p2: Int) {
                }

                override fun onPlayerStateChanged(p0: Boolean, playbackState: Int) {
                    when (playbackState) {

                        Player.STATE_IDLE -> {
//                            tvVodStreamLoadingError.visibility = View.INVISIBLE
//                            pbVodPlayer.visibility = View.VISIBLE
                        }
                        Player.STATE_BUFFERING -> {
                            turnOnKeepOnScreen(true)
                            tvLiveStreamLoadingError.visibility = View.INVISIBLE
                            pbLiveTvPlayer.visibility = View.VISIBLE
                            btnTogglePlayPause.setImageResource(R.drawable.ic_pause)
                            if (isLostConnection) {
                                tvLiveStreamLoadingError.visibility = View.VISIBLE
                            }
                        }
                        Player.STATE_ENDED -> {
                        }
                        Player.STATE_READY -> {
                            btnTogglePlayPause.visibility = View.VISIBLE
                            tvLiveStreamLoadingError.visibility = View.INVISIBLE
                            pbLiveTvPlayer.visibility = View.INVISIBLE
                            autoHidePlayerController()
                        }
                    }
                }

            }
        }
        if (player != null) {
            player?.addListener(playerExoListener)
        }
    }

    private fun autoHidePlayerController() {
        mHandler.postDelayed(countToHideController, 4000)
    }

    private val countToHideController = Runnable {
        if (layoutPlayerController.visibility == View.VISIBLE) {
            layoutPlayerController.visibility = View.INVISIBLE
            layoutPlayerController.startAnimation(
                AnimationUtils.loadAnimation(
                    activity,
                    R.anim.fade_out
                )
            )
        }
    }

    @SuppressLint("MissingPermission")
    fun onStart() {
//        pvLiveTv.playerController.addPlayerListener(playerListener)
        tm = activity.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        tm?.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE)
        val intentFilter = IntentFilter()
        intentFilter.addAction(Const.FLAG_INCOMING_CALL)
        intentFilter.addAction(Const.FLAG_CALL_ENDED)
        LocalBroadcastManager.getInstance(activity)
            .registerReceiver(incomingCallReceiver, intentFilter)
    }

    fun onResume() {
        if (player != null) {
//           player.
        }
        if (isPlayingWhenGoOut) {
            isPlayingWhenGoOut = false
            requestAudioFocus()
        }
//        old
//        pvLiveTv.lifecycleDelegate.resume()
//        if (isPlayingWhenGoOut) {
//            isPlayingWhenGoOut = false
//            requestAudioFocus()
//        }
    }

    fun onStop() {
        if (player != null) {
            if (
                player!!.playWhenReady) {
                isPlayingWhenGoOut = true
            }
            player!!.removeListener(playerExoListener)
            player!!.release()
        }
        tm?.listen(callStateListener, PhoneStateListener.LISTEN_NONE)
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(incomingCallReceiver)
//        old
//        if (pvLiveTv.playerController.isPlaying) {
//            isPlayingWhenGoOut = true
//        }
//        pvLiveTv.playerController.removePlayerListener(playerListener)
//        pvLiveTv.lifecycleDelegate.releasePlayer(false)
//        tm?.listen(callStateListener, PhoneStateListener.LISTEN_NONE)
//        LocalBroadcastManager.getInstance(activity).unregisterReceiver(incomingCallReceiver)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnCollapse -> {
                if (activity.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    if (isCollapsed) {
                        doExpand()
                    } else {
                        doCollapse()
                    }
                } else {
                    doDisplaySmallScreen()
                }
            }
            R.id.btnToggleDisplay -> {
                if (activity.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    doDisplayFullScreen()
                } else {
                    doDisplaySmallScreen()
                }
            }
            R.id.layoutLiveTvPlayer -> {
                if (!isCollapsed) {
                    mHandler.removeCallbacks(countToHideController)
                    if (layoutPlayerController.visibility == View.VISIBLE) {
                        layoutPlayerController.visibility = View.INVISIBLE
                        layoutPlayerController.startAnimation(
                            AnimationUtils.loadAnimation(
                                activity,
                                R.anim.fade_out
                            )
                        )
                        if (activity.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                            AppUtils.displaySystemUI(activity, true)
                        }
                    } else {
                        layoutPlayerController.visibility = View.VISIBLE
                        layoutPlayerController.startAnimation(
                            AnimationUtils.loadAnimation(
                                activity,
                                R.anim.fade_in
                            )
                        )
                        autoHidePlayerController()
                    }
                }
            }
            R.id.btnTogglePlayPause -> {
                if (player != null) {
                    if (player!!.playbackState == Player.STATE_READY) {
                        if (player!!.playWhenReady) {
                            pauseLiveTv()

                        } else {
                            playLiveTv()
                        }
                    } else if (player!!.playbackError != null || player!!.playbackState == Player.STATE_IDLE) {
                        player!!.playWhenReady == true
                        player!!.retry()
                    }
                }
//                Old
//                if (pvLiveTv.playerController.isPlaying) {
//                    pvLiveTv.playerController.pause()
//                    pvLiveTv.playerController.release()
//                } else {
//                    playLiveTv()
//                }
            }
            R.id.btnMoreLiveTv -> {
                if (layoutScalingMode.visibility == View.VISIBLE) {
                    layoutScalingMode.visibility = View.GONE
                    layoutScalingMode.startAnimation(
                        AnimationUtils.loadAnimation(
                            activity,
                            R.anim.fade_out
                        )
                    )
                } else {
                    hidePlayerController()
                    layoutScalingMode.visibility = View.VISIBLE
                    layoutScalingMode.startAnimation(
                        AnimationUtils.loadAnimation(
                            activity,
                            R.anim.fade_in
                        )
                    )
                }
            }
            R.id.layoutScalingMode -> {
                if (layoutScalingMode.visibility == View.VISIBLE) {
                    layoutScalingMode.visibility = View.GONE
                    layoutScalingMode.startAnimation(
                        AnimationUtils.loadAnimation(
                            activity,
                            R.anim.fade_out
                        )
                    )
                }
            }
            R.id.btnModeFill -> {
                playerViewTv.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                updateUIScalingMode()
            }
            R.id.btnModeFit -> {
                playerViewTv.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                updateUIScalingMode()
            }
            R.id.btnModeCrop -> {
                playerViewTv.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                updateUIScalingMode()
            }
//            Old
//            R.id.btnModeFill -> {
//                pvLiveTv.scalingMode = PlayerView.SCALING_MODE_STRETCH
//                updateUIScalingMode()
//            }
//            R.id.btnModeFit -> {
//                pvLiveTv.scalingMode = PlayerView.SCALING_MODE_FIT
//                updateUIScalingMode()
//            }
//            R.id.btnModeCrop -> {
//                pvLiveTv.scalingMode = PlayerView.SCALING_MODE_CROP
//                updateUIScalingMode()
//            }
        }
    }

    private fun doDisplayFullScreen() {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        AppUtils.displaySystemUI(activity, true)
        horizontalPlayerLayoutSet.applyTo(containerView)
        vTopLiveTvPlayer.visibility = View.GONE
        layoutDailyEPG.visibility = View.GONE
    }

    private fun doDisplaySmallScreen() {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppUtils.displaySystemUI(activity, false)
        verticalPlayerLayoutSet.applyTo(containerView)
        vTopLiveTvPlayer.visibility = View.VISIBLE
        layoutDailyEPG.visibility = View.VISIBLE
    }

    private fun doCollapse() {
        if (layoutScalingMode.visibility == View.VISIBLE) {
            layoutScalingMode.visibility = View.GONE
            layoutScalingMode.startAnimation(
                AnimationUtils.loadAnimation(
                    activity,
                    R.anim.fade_out
                )
            )
        }
        //hide player controller
        hidePlayerController()
        goCollapseState()
    }

    private fun doExpand() {
        if (isClosedBySwipe) {
            isClosedBySwipe = false
        }
        goExpandState()
    }

    private fun goExpandState() {
        isCollapsed = false
        isExpandByMove = false
        isCollapseByMove = false
        layoutLiveTvPlayer.animate().x(0f).duration = duration
        layoutLiveTvPlayer.animate().y(statusBarHeight).duration = duration
        layoutLiveTvPlayer.animate().scaleX(1f).duration = duration
        layoutLiveTvPlayer.animate().scaleY(1f).duration = duration
        vTopLiveTvPlayer.animate().alpha(1f).duration = duration
        layoutDailyEPG.animate().setListener(animatorListener).alpha(1f).duration = duration
    }

    private fun goCollapseState() {
        isCollapsed = true
        isExpandByMove = false
        isCollapseByMove = false
        layoutLiveTvPlayer.animate().x(posCollapsedX).duration = duration
        layoutLiveTvPlayer.animate().y(posCollapsedY).duration = duration
        layoutLiveTvPlayer.animate().scaleX(scaleCollapseX).duration = duration
        layoutLiveTvPlayer.animate().scaleY(scaleCollapseY).duration = duration
        vTopLiveTvPlayer.animate().alpha(0f).duration = duration
        layoutDailyEPG.animate().setListener(animatorListener).alpha(0f).duration = duration
        hidePlayerController()
    }

    private fun closeBySwipe() {
        isHandleAction = false
        isClosedBySwipe = true
        layoutLiveTvPlayer.animate().setListener(animatorListener).x(-widthPlayerViewCollapsed)
            .duration = duration
        releasePlayer()
    }

    private fun expandByMove() {
        goExpandState()
        isExpandByMove = true
        isHandleAction = false
    }

    private fun collapseByMove() {
        goCollapseState()
        isCollapseByMove = true
        isHandleAction = false
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.x
                lastY = event.y
                mVelocityTracker = VelocityTracker.obtain()
                mVelocityTracker.addMovement(event)
                lastTime = System.currentTimeMillis()
            }
            MotionEvent.ACTION_MOVE -> {
                if (isHandleAction) {
                    mVelocityTracker.addMovement(event)
                    val deltaX = event.x - lastX
                    val deltaY = event.y - lastY
                    if (isCollapsed) {
                        if (deltaX.absoluteValue > 10f && deltaX.absoluteValue <= offsetTranslationX && !isInMoveY) {
                            isInMoveX = true
                            val x = layoutLiveTvPlayer.x + deltaX
                            if (x <= posCollapsedX) {
                                layoutLiveTvPlayer.x = x
                                val distance = widthPlayerView
                                val percent = (offsetTranslationX - x) / distance
//                                layoutLiveTvPlayer.alpha = 1 - percent
                                if (percent > 0.5) {
                                    closeBySwipe()
                                }
                            }
                        }
                        if (deltaY.absoluteValue > 20f && deltaY.absoluteValue <= offsetTranslationY && !isInMoveX) {
                            isInMoveY = true
                            val top =
                                vTopLiveTvPlayer.height.toFloat() //trick: get start y of player view original
                            val y = layoutLiveTvPlayer.y + deltaY
                            val distance = offsetTranslationY
                            if (y > top && y < posCollapsedY) {
                                layoutLiveTvPlayer.y = y
                                val percent = 1 - ((y - top) / distance)
                                val bScaleX = (1 - scaleCollapseX) * percent
                                val bScaleY = (1 - scaleCollapseY) * percent
                                layoutLiveTvPlayer.scaleX = bScaleX + scaleCollapseX
                                layoutLiveTvPlayer.scaleY = bScaleY + scaleCollapseY
                                layoutLiveTvPlayer.x =
                                    posCollapsedX - (widthPlayerViewCollapsed * bScaleX / 2)
                                layoutDailyEPG.visibility = View.VISIBLE
                                vTopLiveTvPlayer.alpha = percent
                                layoutDailyEPG.alpha = percent
                                if (percent >= 0.45) {
                                    expandByMove()
                                }
                            }
                        }
                    } else {
                        if (deltaY.absoluteValue > 20f && deltaY.absoluteValue <= offsetTranslationY) {
                            isInMoveY = true
                            val top =
                                vTopLiveTvPlayer.height.toFloat() //trick: get start y of player view original
                            val y = layoutLiveTvPlayer.y + deltaY
                            val distance = offsetTranslationY
                            if (y > top && y < top + distance) {
                                layoutLiveTvPlayer.y = y
                                val percent = (y + top) / distance
                                val bScaleX = (1 - scaleCollapseX) * percent
                                val bScaleY = (1 - scaleCollapseY) * percent
                                layoutLiveTvPlayer.scaleX = 1 - bScaleX
                                layoutLiveTvPlayer.scaleY = 1 - bScaleY
                                layoutLiveTvPlayer.x = (widthPlayerView * bScaleX / 2)
                                val alpha = 1 - percent
                                vTopLiveTvPlayer.alpha = alpha
                                layoutDailyEPG.alpha = alpha
                                if (percent >= 0.85) {
                                    collapseByMove()
                                }
                            }
                        }
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                if (isHandleAction) {
                    val deltaX = (event.x - lastX)
                    mVelocityTracker.computeCurrentVelocity(100)
                    if (!isInMoveY && deltaX < 0 && mVelocityTracker.xVelocity.absoluteValue > 1000) {
                        closeBySwipe()
                    }
                    if (activity.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
                        && !isClosedBySwipe
                    ) {
                        if (isCollapsed) {
                            if (System.currentTimeMillis() - lastTime < 200
                                && deltaX.absoluteValue < 20
                            ) {
                                doExpand()
                            } else {
                                if (!isClosedBySwipe && isInMoveX) {
                                    layoutLiveTvPlayer.animate().x(posCollapsedX).duration = 300
                                    layoutLiveTvPlayer.animate().alpha(1f).duration = 300
                                }
                                if (!isExpandByMove) {
                                    goCollapseState()
                                }
                            }
                        } else {
                            if (!isCollapseByMove) {
                                goExpandState()
                            }
                        }
                    }
                } else {
                    isHandleAction = true
                }
                mVelocityTracker.recycle()
                v?.performClick()
                isInMoveX = false
                isInMoveY = false
            }
        }
        return true
    }

    private val incomingCallReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                if (intent.action == Const.FLAG_INCOMING_CALL) {
                    pauseLiveTv()
                } else if (intent.action == Const.FLAG_CALL_ENDED
                    && isShown && !isClosedBySwipe
                    && activity.supportFragmentManager.findFragmentByTag(SearchFragment.TAG) == null
                ) {
                    playLiveTv()
                }
            }
        }
    }

    fun onConfigurationChanged(newConfig: Configuration?) {
        if (newConfig!!.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            btnCollapse.rotation = 90f
            btnToggleDisplay.setImageResource(R.drawable.ic_shrink)
            layoutLiveTvPlayer.setOnTouchListener(null)
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            btnCollapse.rotation = 0f
            btnToggleDisplay.setImageResource(R.drawable.ic_stretch)
            layoutLiveTvPlayer.setOnTouchListener(this)
        }
    }

    fun onBackPressed() {
        if (activity.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            doCollapse()
        } else {
            doDisplaySmallScreen()
        }
    }

    fun playLiveTv() {
        if (player != null) {
            btnTogglePlayPause.setImageResource(R.drawable.ic_pause)
            player!!.playWhenReady = true
        }

//        Old
//        pvLiveTv.playerController.open(playerConfigBuilder?.get())
//        pvLiveTv.playerController.play()
//        requestAudioFocus()

    }

    fun pauseLiveTv() {
        if (player != null) {
            btnTogglePlayPause.setImageResource(R.drawable.ic_play)
            player!!.playWhenReady = false
        }
//        Old
//        if (pvLiveTv.playerController.isPlaying) {
//            pvLiveTv.playerController.pause()
//            pvLiveTv.playerController.release()
//        }
    }

    private fun updateUIScalingMode() {
        if (layoutScalingMode.visibility == View.VISIBLE) {
            layoutScalingMode.visibility = View.GONE
            layoutScalingMode.startAnimation(
                AnimationUtils.loadAnimation(
                    activity,
                    R.anim.fade_out
                )
            )
        }
        MyPreferenceManager.getInstance(activity)
            .setValue(Const.PLAYER_SCALING_MODE, playerViewTv.resizeMode)
        when (playerViewTv.resizeMode) {
            AspectRatioFrameLayout.RESIZE_MODE_FILL -> {
                btnModeFill.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary))
                btnModeFit.setTextColor(Color.WHITE)
                btnModeCrop.setTextColor(Color.WHITE)
            }
            AspectRatioFrameLayout.RESIZE_MODE_FIT -> {
                btnModeFill.setTextColor(Color.WHITE)
                btnModeFit.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary))
                btnModeCrop.setTextColor(Color.WHITE)
            }
            AspectRatioFrameLayout.RESIZE_MODE_ZOOM -> {
                btnModeFill.setTextColor(Color.WHITE)
                btnModeFit.setTextColor(Color.WHITE)
                btnModeCrop.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary))
            }
        }
//        old
//        if (layoutScalingMode.visibility == View.VISIBLE) {
//            layoutScalingMode.visibility = View.GONE
//            layoutScalingMode.startAnimation(
//                AnimationUtils.loadAnimation(
//                    activity,
//                    R.anim.fade_out
//                )
//            )
//        }
//        MyPreferenceManager.getInstance(activity)
//            .setValue(Const.PLAYER_SCALING_MODE, pvLiveTv.scalingMode)
//        when (pvLiveTv.scalingMode) {
//            PlayerView.SCALING_MODE_STRETCH -> {
//                btnModeFill.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary))
//                btnModeFit.setTextColor(Color.WHITE)
//                btnModeCrop.setTextColor(Color.WHITE)
//            }
//            PlayerView.SCALING_MODE_FIT -> {
//                btnModeFill.setTextColor(Color.WHITE)
//                btnModeFit.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary))
//                btnModeCrop.setTextColor(Color.WHITE)
//            }
//            PlayerView.SCALING_MODE_CROP -> {
//                btnModeFill.setTextColor(Color.WHITE)
//                btnModeFit.setTextColor(Color.WHITE)
//                btnModeCrop.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary))
//            }
//        }
    }

    private fun requestAudioFocus() {
        val result = audioManager?.requestAudioFocus(
            this,
            // Use the music stream.
            AudioManager.STREAM_MUSIC,
            // Request permanent focus.
            AudioManager.AUDIOFOCUS_GAIN
        )
        if (Const.DEBUG) {
            Log.d("FOCUS", result.toString())
        }
    }

    override fun onAudioFocusChange(focusChange: Int) {
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT
            || focusChange == AudioManager.AUDIOFOCUS_LOSS
        ) {
            pauseLiveTv()
        }
    }

    private fun updateEPG() {
        val currentTime = System.currentTimeMillis()
        var startTime: Long
        var endTime: Long
        var model: DailyEPGModel
        for (i in 0 until listDailyEPG.size) {
            model = listDailyEPG[i]
            startTime = AppUtils.formatDateTimeEPGToTimeInMillis(model.programstart)
            endTime = AppUtils.formatDateTimeEPGToTimeInMillis(model.programend)
            if (currentTime in startTime..endTime) {
                model.isPlaying = true
                countToUpdateEPG(endTime - System.currentTimeMillis() + 1000)
                scrollToCenter(i)
            } else {
                model.isPlaying = false
                model.isDisable = startTime > currentTime
            }
        }
        dailyEPGRVAdapter?.notifyDataSetChanged()
    }

    private fun countToUpdateEPG(millisInFuture: Long) {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(millisInFuture, 1000) {
            override fun onFinish() {
                if (Utils.getSendTime() != MyPreferenceManager.getInstance(activity).getValue(
                        Const.DATE,
                        ""
                    )
                ) {
                    loadDailyEpg(channelNumber)
                } else {
                    updateEPG()
                }
            }

            override fun onTick(millisUntilFinished: Long) {
                //do nothing
            }
        }
        countDownTimer?.start()
    }

    private fun scrollToCenter(position: Int) {
        rvDailyEPG.post {
            val numberItemVisiblePerTime =
                linearLayoutManager.findLastVisibleItemPosition() - linearLayoutManager.findFirstVisibleItemPosition()
            val offset: Int
            if (numberItemVisiblePerTime == 0) {
                offset = 40
            } else {
                offset = rvDailyEPG.height / numberItemVisiblePerTime / 2
            }
            val offsetCenter = rvDailyEPG.height / 2 - offset
            linearLayoutManager.scrollToPositionWithOffset(position, offsetCenter)
        }
    }

    fun playLink(url: String) {
        /* if (4 % 2 == 0) {
            return;
        }*/
        if (playerViewTv == null || activity.isFinishing() || activity.isDestroyed()) {
            return
        }
        releasePlayer()
        if (player == null) {
            initializePlayer()
        }
        mediaDataSourceFactory = buildDataSourceFactory(true)
        Log.e("URLSTREAM", url)
        val mediaSource = buildMediaSource(Uri.parse(url), "")
        player!!.prepare(mediaSource, true, true)
        playerViewTv.requestFocus()
    }

    private fun initializePlayer() {
        //        playerView.setControllerVisibilityListener(this);
        //        playerView.requestFocus();
        val adaptiveTrackSelectionFactory = AdaptiveTrackSelection.Factory()
        trackSelector = DefaultTrackSelector(adaptiveTrackSelectionFactory)
        val renderersFactory = DefaultRenderersFactory(activity)
        player = ExoPlayerFactory.newSimpleInstance(activity, renderersFactory, trackSelector)
        initPlayerListenner()
        playerViewTv.setUseController(false)
        playerViewTv.setPlayer(player)
        playerViewTv.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL)
        player!!.setPlayWhenReady(true)
    }

    fun releasePlayer() {
        if (player != null) {
            try {
                player!!.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            player = null
        }
    }

    private var mediaDataSourceFactory: DataSource.Factory? = null
    private var trackSelector: DefaultTrackSelector? = null
    private val BANDWIDTH_METER = DefaultBandwidthMeter()
    private val rtmpDataSourceFactory = RtmpDataSourceFactory()
    private fun buildMediaSource(uri: Uri, overrideExtension: String): MediaSource {
        @C.ContentType val type = Util.inferContentType(uri, overrideExtension)
        when (type) {
            C.TYPE_SS -> return SsMediaSource.Factory(
                DefaultSsChunkSource.Factory(
                    mediaDataSourceFactory
                ), buildDataSourceFactory(false)
            ).createMediaSource(uri)
            C.TYPE_DASH -> return DashMediaSource.Factory(
                DefaultDashChunkSource.Factory(
                    mediaDataSourceFactory
                ), buildDataSourceFactory(false)
            ).createMediaSource(uri)
            C.TYPE_HLS -> //if (MyApplication.getInstance().isEmpty(imageThumbnailAudioUrl)) {
                return HlsMediaSource.Factory(mediaDataSourceFactory).createMediaSource(uri)
//            } else {
//                val defaultHlsExtractorFactory =
//                    DefaultHlsExtractorFactory(DefaultTsPayloadReaderFactory.FLAG_IGNORE_H264_STREAM)
//                return HlsMediaSource.Factory(mediaDataSourceFactory)
//                    .setExtractorFactory(defaultHlsExtractorFactory).createMediaSource(uri)
//            }
            //                return new HlsMediaSource.Factory(mediaDataSourceFactory).createMediaSource(uri);
            C.TYPE_OTHER -> {
                return if (uri.scheme != null && uri.scheme == "rtmp") {
                    ExtractorMediaSource.Factory(rtmpDataSourceFactory).createMediaSource(uri)
                } else ExtractorMediaSource.Factory(mediaDataSourceFactory).createMediaSource(uri)
            }
            else -> {
                return HlsMediaSource.Factory(mediaDataSourceFactory).createMediaSource(uri)
            }
        }
    }

    /**
     * Returns a new DataSource factory.
     *
     * @param useBandwidthMeter Whether to set [.BANDWIDTH_METER] as a listener to the new
     * DataSource factory.
     * @return A new DataSource factory.
     */
    private fun buildDataSourceFactory(useBandwidthMeter: Boolean): DataSource.Factory {
        return buildDataSourceFactory(if (useBandwidthMeter) BANDWIDTH_METER else null)
    }

    fun buildDataSourceFactory(bandwidthMeter: DefaultBandwidthMeter?): DataSource.Factory {
        return DefaultDataSourceFactory(
            activity,
            bandwidthMeter,
            buildHttpDataSourceFactory(bandwidthMeter)
        )
    }

    fun buildHttpDataSourceFactory(bandwidthMeter: DefaultBandwidthMeter?): HttpDataSource.Factory {
        val exoPlayerUserAgent = Util.getUserAgent(activity, "ExoPlayer")
        return DefaultHttpDataSourceFactory(exoPlayerUserAgent, bandwidthMeter)
    }

    private fun turnOnKeepOnScreen(isOn: Boolean) {
        if (isOn) {
            /** Turn on: */
//TODO restoring from original value
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            /** Turn off: */
            activity.getWindow()
                .addFlags(WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);

        }
    }
}