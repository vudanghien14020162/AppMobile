package com.mobitv.ott.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.core.content.ContextCompat
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
import com.mobitv.ott.database.entity.CommonEntity
import com.mobitv.ott.database.entity.WatchHistoryEntity
import com.mobitv.ott.dialog.ContinueDialog
import com.mobitv.ott.model.VodDetails
import com.mobitv.ott.model.VodModel
import com.mobitv.ott.other.*
import com.mobitv.ott.task.ExecuteTaskSingleResultListener
import com.mobitv.ott.task.GetCurentPositionDBTask
import com.mobitv.ott.task.GetWatchDataInDBTask
import com.mobitv.ott.task.InsertOrUpdateWatchDBTask
import kotlinx.android.synthetic.main.activity_player_vod.*
import kotlinx.android.synthetic.main.layout_scaling_mode.*
import kotlinx.android.synthetic.main.layout_vod_player.*

class PlayerVodActivity : BaseActivity(), AudioManager.OnAudioFocusChangeListener {
    // This is the player view that we use to start playback
    private val mHandler = Handler()
    private val listVodRelated = ArrayList<VodModel>()
    private var listEpisode = ArrayList<VodModel>()
    private val trackingInterval = 500L
    //    private val trackingInterval = 1000L
    private var currentVideoID = 0
    private var currentVideoIcon = ""
    private var currentVideoType = ""
    private var currentEpisodeID = 0
    private var currentPosition = 0L
    private var posEpisode = 0
    private var isSoughtToPos = false
    private var isLostConnection = false
    private var isFavourite = false
    private var timeFactor = 1000

    private var audioManager: AudioManager? = null
    private var isPlayingWhenGoOut = false

    private var countDownTimer: CountDownTimer? = null
    private var headsetConnected = false

    private var insertDataIntoDBTask: InsertOrUpdateWatchDBTask? = null
    private var url = ""
    private var vodDetails: VodDetails? = null
    private var player: SimpleExoPlayer? = null
    //    private val playerControllerViewListener = PlayerControllerView.Listener { visibility ->
//        val svc = pvVOD!!.getComponent(SubtitlesViewComponent::class.java)
//        if (svc != null && svc.view != null) {
//            val height = if (visibility == View.VISIBLE) playerControllerView!!.height else 0
//            svc.view!!.setPadding(0, 0, 0, height)
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_vod)
        initClick()
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val receiverFilter = IntentFilter(Intent.ACTION_HEADSET_PLUG)
        registerReceiver(headSetStateReceiver, receiverFilter)
        var vodManager = VodManager.getInstance()
        listEpisode = vodManager!!.episodeList;
        currentEpisodeID = vodManager.currentEpisodeID
        vodDetails = vodManager.vodDetails
        currentVideoType = vodManager.currentVideoType
        currentVideoID = vodDetails!!.id
        if (listEpisode!!.size == 0) {
            tvVodNamePlayer.text = vodDetails?.title
            btnVodPlayerPrevious.visibility = View.GONE
            btnVodPlayerNext.visibility = View.GONE
            setUpPlayer(vodDetails?.url)
            val getDataInDBTask = GetWatchDataInDBTask(vodDetails!!.id, vodDetails!!.vodType)
            getDataInDBTask.listener = object : ExecuteTaskSingleResultListener {
                override fun onStartTask() {
                }

                override fun onResultSuccessfully(entity: CommonEntity?) {
                    if (entity != null && entity is WatchHistoryEntity) {
                        currentPosition = entity.currentPosition
                    } else {
                        currentPosition = 0L
                    }
                    doPlay()
                }

                override fun onResultFailed() {
                }

            }
            getDataInDBTask.execute(this)
        } else {
            posEpisode = getCurrentEpPosition()
            reloadDataTvSeason(listEpisode[posEpisode])
        }
        //check in database

    }

    private fun initClick() {
        layoutVodPlayerContainer.setOnClickListener(this)
        btnBackVodPlayer.setOnClickListener(this)
        btnToggleVodPlayPause.setOnClickListener(this)
        btnToggleDisplayVodPlayer.setOnClickListener(this)
        layoutScalingMode.setOnClickListener(this)
        btnMoreVODPlayer.setOnClickListener(this)
        btnModeCrop.setOnClickListener(this)
        btnModeFit.setOnClickListener(this)
        btnModeFill.setOnClickListener(this)
        btnVodPlayerNext.setOnClickListener(this)
        btnVodPlayerPrevious.setOnClickListener(this)
    }

    private fun autoHidePlayerController() {
        mHandler.postDelayed(countToHideController, 4000)
    }

    private val countToHideController = Runnable {
        if (layoutVodPlayerController.visibility == View.VISIBLE) {
            layoutVodPlayerController.visibility = View.INVISIBLE
            layoutVodPlayerController.startAnimation(
                AnimationUtils.loadAnimation(
                    this@PlayerVodActivity,
                    R.anim.fade_out
                )
            )
        }
    }

    private val trackingProgress = object : Runnable {
        override fun run() {
            if (player != null) {
//                currentPosition = player!!.currentPosition
                val currentTimeInSecond = player!!.currentPosition / timeFactor
                val currentBufferedInSecond = player!!.bufferedPosition / timeFactor
                sbVodPlayingProgress.progress = currentTimeInSecond.toInt()
                sbVodPlayingProgress.secondaryProgress = currentBufferedInSecond.toInt()
                tvVodPlayerCurrentTime.text = AppUtils.formatDuration(currentTimeInSecond)
                mHandler.postDelayed(this, trackingInterval)
            }
        }
    }

    private val onSeekToListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if (fromUser) {
                val pos: Long = progress.toLong() * timeFactor
                if (player != null) {
                    player!!.seekTo(pos)
                }
                tvVodPlayerCurrentTime.text = AppUtils.formatDuration(progress.toLong())
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
        }

    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        var handled = false
//        if (playerControllerView != null && playerControllerView!!.visibility == View.VISIBLE) {
//            handled = playerControllerView!!.dispatchKeyEvent(event)
//        } else { // Not visible
//            if (event.action == KeyEvent.ACTION_DOWN) {
//                handled = playerControllerView!!.dispatchKeyEvent(event)
//            }
//        }
        return handled || super.dispatchKeyEvent(event)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onPause() {
        super.onPause()
        pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
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
    // Save the playback state when the activity is destroyed in order to correctly
    // resume after the activity is re-created again i.e. onCreate is called

    companion object {
        private val TAG = "PlayerVodActivity"
        private val SAVED_PLAYBACK_STATE_BUNDLE_KEY = "SAVED_PLAYBACK_STATE_BUNDLE_KEY"
    }

    private fun requestAudioFocus() {
        val result = audioManager?.requestAudioFocus(
            this,
            // Use the music stream.
            AudioManager.STREAM_MUSIC,
            // Request permanent focus.
            AudioManager.AUDIOFOCUS_GAIN
        );
        if (Const.DEBUG) {
            Log.d("FOCUS", result.toString())
        }
    }

    override fun onAudioFocusChange(focusChange: Int) {
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT
            || focusChange == AudioManager.AUDIOFOCUS_LOSS
        ) {
            pause()
        }
    }

    private fun getCurrentEpPosition(): Int {
        var pos = 0
        for (i in 0 until listEpisode!!.size) {
            if (currentEpisodeID == listEpisode!![i].id) {
                pos = i
            }
        }
        return pos
    }

    private fun goToEpisode(isNext: Boolean) {
        isLostConnection = false
        countDownTimer?.cancel()
        val currentEpPos = getCurrentEpPosition()
        if (isNext) {
            if (currentEpPos < listEpisode!!.size - 1)
                reloadDataTvSeason(listEpisode!![currentEpPos + 1])
        } else {
            if (currentEpPos > 0)
                reloadDataTvSeason(listEpisode!![currentEpPos - 1])
        }
    }

    private fun reloadDataTvSeason(episode: VodModel) {
        releasePlayer()
        tvVodNamePlayer.text = episode.title
        currentEpisodeID = episode.id
        setUpPlayer(episode.url)
        //check in database
        val getCurrentPos =
            GetCurentPositionDBTask(currentVideoID, currentVideoType, currentEpisodeID)
        getCurrentPos.listener = object : ExecuteTaskSingleResultListener {
            override fun onStartTask() {
            }

            override fun onResultSuccessfully(entity: CommonEntity?) {
                if (entity != null && entity is WatchHistoryEntity) {
                    currentPosition = entity.currentPosition
                } else {
                    currentPosition = 0
                }

                val currentEpPos = getCurrentEpPosition()
                btnVodPlayerPrevious.visibility =
                    if (currentEpPos == 0) View.INVISIBLE else View.VISIBLE
                btnVodPlayerNext.visibility =
                    if (currentEpPos == listEpisode.size - 1) View.INVISIBLE else View.VISIBLE
                doPlay()
            }

            override fun onResultFailed() {
            }
        }
        getCurrentPos.execute(this)
    }

    private fun setUpPlayer(streamURL: String?) {
        if (!streamURL.isNullOrEmpty()) {
            url = streamURL
            val e = Log.e("URL", url)
            btnToggleVodPlayPause.setOnClickListener(this)
            val scalingMode = MyPreferenceManager.getInstance(this)
                .getValue(Const.PLAYER_SCALING_MODE, AspectRatioFrameLayout.RESIZE_MODE_FILL)
            playerView.resizeMode = scalingMode
            updateUIScalingMode()
            playLink(streamURL)
            sbVodPlayingProgress.setOnSeekBarChangeListener(onSeekToListener)
        } else {
            tvVodStreamLoadingError.visibility = View.VISIBLE
            btnToggleVodPlayPause.setImageResource(R.drawable.ic_play)
            pbVodPlayer.visibility = View.INVISIBLE
        }
    }

    private var playerListener: Player.EventListener? = null
    private fun initPlayerListenner() {
        if (playerListener == null) {
            playerListener = object : Player.EventListener {
                override fun onTracksChanged(
                    trackGroupArray: TrackGroupArray,
                    trackSelectionArray: TrackSelectionArray
                ) {

                }

                override fun onLoadingChanged(b: Boolean) {

                }

                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    when (playbackState) {

                        Player.STATE_BUFFERING -> {
                            turnOnKeepOnScreen(true)
                            pbVodPlayer.visibility = View.VISIBLE
                            btnToggleVodPlayPause.setImageResource(R.drawable.ic_pause)
//                            if (isLostConnection) {
//                                tvVodStreamLoadingError.visibility = View.VISIBLE
//                                tvVodStreamLoadingError.text =
//                                    getString(R.string.connectivity_lost_error)
//                            }
                        }
                        Player.STATE_ENDED -> {
//                            turnOnKeepOnScreen(false)
                            mHandler.removeCallbacks(trackingProgress)
                            btnToggleVodPlayPause.setImageResource(R.drawable.ic_play)
                            pbVodPlayer.visibility = View.INVISIBLE
                            if (currentVideoType == Const.TYPE_TV_SHOWS_SEASON
                                && getCurrentEpPosition() < listEpisode!!.size - 1
                            ) {
                                startCountDownToNextEpisode()
                            }
                        }
                        Player.STATE_READY -> {
//                            if (!isSoughtToPos && player != null) {
//                                isSoughtToPos = true
//                                player!!.seekTo(currentPosition)
//                            }

                            turnOnKeepOnScreen(true)
                            isLostConnection = false
                            updateSeekbar()
                            tvVodStreamLoadingError.visibility = View.INVISIBLE
                            pbVodPlayer.visibility = View.INVISIBLE
                            mHandler.postDelayed(trackingProgress, trackingInterval)
                            autoHidePlayerController()
                        }
                    }
                }

                override fun onRepeatModeChanged(i: Int) {

                }

                override fun onShuffleModeEnabledChanged(b: Boolean) {

                }

                override fun onPlayerError(e: ExoPlaybackException) {
                    tvVodStreamLoadingError.text = getString(R.string.loading_error)
                    tvVodStreamLoadingError.visibility = View.VISIBLE
                    btnToggleVodPlayPause.setImageResource(R.drawable.ic_play)
                    pbVodPlayer.visibility = View.INVISIBLE
                    isLostConnection = true
                }

                override fun onPositionDiscontinuity(i: Int) {
                }

                override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {

                }

                override fun onSeekProcessed() {

                }


            }
        }
        if (player != null) {
            player!!.addListener(playerListener)
        }

    }

    private fun updateSeekbar() {
        val durationInMilliSecond = player!!.contentDuration
        val durationInSecond = durationInMilliSecond / 1000
        tvVodPlayerEndTime.text = AppUtils.formatDuration(durationInSecond)
        sbVodPlayingProgress.max = durationInSecond.toInt()
        mHandler.postDelayed({
            val params =
                tvVodPlayerCurrentTime.layoutParams as LinearLayout.LayoutParams
            params.width = tvVodPlayerEndTime.width + 10
            tvVodPlayerCurrentTime.layoutParams = params
        }, 300)
        mHandler.postDelayed(trackingProgress, trackingInterval)
    }

    private fun doPlay() {
        if (currentPosition > 0) {
            val dialog = ContinueDialog(this)
            dialog.setListener { isContinue ->
                if (!isContinue) {
                    currentPosition = 0
                }
                AppUtils.displaySystemUI(this@PlayerVodActivity, true)
                start()
            }
            dialog.showWindow()
        } else {
            AppUtils.displaySystemUI(this@PlayerVodActivity, true)
            start()
        }
    }

    private fun doUpdateDataInDB() {
        val userID = 0
        if (insertDataIntoDBTask == null) {
            val currentTime = System.currentTimeMillis()
            insertDataIntoDBTask = InsertOrUpdateWatchDBTask(
                userID, currentVideoID, currentVideoType, currentEpisodeID,
                currentPosition, currentTime
            )
            insertDataIntoDBTask?.listener = object : ExecuteTaskSingleResultListener {
                override fun onStartTask() {
                }

                override fun onResultSuccessfully(entity: CommonEntity?) {
                    if (entity != null && entity is WatchHistoryEntity) {
                        insertDataIntoDBTask = null
                        GlobalParams.isReloadDataPager = true
                    }
                }

                override fun onResultFailed() {
                }

            }
            insertDataIntoDBTask?.execute(this)
        }
    }

    private fun startCountDownToNextEpisode() {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(10 * 1000, 1000) {
            override fun onFinish() {
                tvVodCountDownToNext.visibility = View.INVISIBLE
                goToEpisode(true)
            }

            override fun onTick(millisUntilFinished: Long) {
                tvVodCountDownToNext.visibility = View.VISIBLE
                val timeLeft =
                    getString(R.string.auto_next_message) + " " + (millisUntilFinished / 1000).toString() + " " + getString(
                        R.string.second
                    )
                tvVodCountDownToNext?.text = timeLeft
            }
        }
        countDownTimer?.start()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            //player
            R.id.btnToggleDisplayVodPlayer -> {

            }
            R.id.btnBackVodPlayer -> {
                isSoughtToPos = false
                doBack()
            }
            R.id.layoutVodPlayerContainer -> {
                mHandler.removeCallbacks(countToHideController)
                if (layoutVodPlayerController.visibility == View.VISIBLE) {
                    layoutVodPlayerController.visibility = View.INVISIBLE
                    AppUtils.displaySystemUI(this, true)
                } else {
                    layoutVodPlayerController.visibility = View.VISIBLE
                    autoHidePlayerController()
                }
            }
            R.id.btnToggleVodPlayPause -> {
                if (player != null) {
                    if (player!!.playbackState == Player.STATE_ENDED) {
                        currentPosition = 0
                        doPlay()
                        return
                    } else if (player!!.playbackState == Player.STATE_READY) {
                        if (player!!.playWhenReady) {
                            pause()
                        } else {
                            play()
                        }
                    } else if (player!!.playbackState == Player.STATE_IDLE || player!!.getPlaybackError() != null) {
                        player!!.retry()
                        player!!.playWhenReady = true
                        tvVodStreamLoadingError.visibility = View.INVISIBLE
                    }
                }
            }
            R.id.btnMoreVODPlayer -> {
                if (layoutScalingMode.visibility == View.VISIBLE) {
                    layoutScalingMode.visibility = View.GONE
                    layoutScalingMode.startAnimation(
                        AnimationUtils.loadAnimation(
                            this,
                            R.anim.fade_out
                        )
                    )
                } else {
                    layoutScalingMode.visibility = View.VISIBLE
                    layoutScalingMode.startAnimation(
                        AnimationUtils.loadAnimation(
                            this,
                            R.anim.fade_in
                        )
                    )
                }
            }
            R.id.layoutScalingMode -> {
                layoutScalingMode.visibility = View.GONE
                layoutScalingMode.startAnimation(
                    AnimationUtils.loadAnimation(
                        this,
                        R.anim.fade_out
                    )
                )
            }
            R.id.btnModeFill -> {
                playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                updateUIScalingMode()
            }
            R.id.btnModeFit -> {
                playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                updateUIScalingMode()
            }
            R.id.btnModeCrop -> {
                playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                updateUIScalingMode()
            }
            R.id.btnVodPlayerPrevious -> {
                goToEpisode(false)
            }
            R.id.btnVodPlayerNext -> {
                goToEpisode(true)
            }

        }
    }

    private fun start() {
        if (player != null) {
            tvVodStreamLoadingError.visibility = View.INVISIBLE
            btnToggleVodPlayPause.setImageResource(R.drawable.ic_pause)
            player!!.seekTo(currentPosition)
            player!!.setPlayWhenReady(true)
        }
    }


    private fun play() {
        if (player != null) {
            btnToggleVodPlayPause.setImageResource(R.drawable.ic_pause)
            player!!.setPlayWhenReady(true)
        }
    }

    private fun pause() {
        if (player != null) {
            btnToggleVodPlayPause.setImageResource(R.drawable.ic_play)
            player!!.setPlayWhenReady(false)
        }
    }

    private fun updateUIScalingMode() {
        layoutScalingMode.visibility = View.GONE
        layoutScalingMode.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out))
        MyPreferenceManager.getInstance(this)
            .setValue(Const.PLAYER_SCALING_MODE, playerView.resizeMode)
        when (playerView.resizeMode) {
            AspectRatioFrameLayout.RESIZE_MODE_FILL -> {
                btnModeFill.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                btnModeFit.setTextColor(Color.WHITE)
                btnModeCrop.setTextColor(Color.WHITE)
            }
            AspectRatioFrameLayout.RESIZE_MODE_FIT -> {
                btnModeFill.setTextColor(Color.WHITE)
                btnModeFit.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                btnModeCrop.setTextColor(Color.WHITE)
            }
            AspectRatioFrameLayout.RESIZE_MODE_ZOOM -> {
                btnModeFill.setTextColor(Color.WHITE)
                btnModeFit.setTextColor(Color.WHITE)
                btnModeCrop.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            }
        }
    }

    private val headSetStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.hasExtra("state")) {
                if (intent.getIntExtra("state", -1) == 0) {
                    headsetConnected = false
                    pause()
                } else {
                    headsetConnected = true
                }
            }
        }
    }

    fun playLink(url: String) {
        /* if (4 % 2 == 0) {
            return;
        }*/
        if (playerView == null || isFinishing() || isDestroyed()) {
            return
        }
        releasePlayer()
        if (player == null) {
            initializePlayer()
        }
        mediaDataSourceFactory = buildDataSourceFactory(true)
        val mediaSource = buildMediaSource(Uri.parse(url), "")
        player!!.prepare(mediaSource, true, true)
        playerView.requestFocus()
    }

    private fun initializePlayer() {
        //        playerView.setControllerVisibilityListener(this);
        //        playerView.requestFocus();
        val adaptiveTrackSelectionFactory = AdaptiveTrackSelection.Factory()
        trackSelector = DefaultTrackSelector(adaptiveTrackSelectionFactory)
        trackSelector!!.buildUponParameters().setMaxVideoBitrate(600000)
        val renderersFactory = DefaultRenderersFactory(this)
        player = ExoPlayerFactory.newSimpleInstance(this, renderersFactory, trackSelector)
        initPlayerListenner()
        playerView.setUseController(false)
        playerView.setPlayer(player)
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL)
        player!!.setPlayWhenReady(false)
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
            this,
            bandwidthMeter,
            buildHttpDataSourceFactory(bandwidthMeter)
        )
    }

    fun buildHttpDataSourceFactory(bandwidthMeter: DefaultBandwidthMeter?): HttpDataSource.Factory {
        val exoPlayerUserAgent = Util.getUserAgent(this, "ExoPlayer")
        return DefaultHttpDataSourceFactory(exoPlayerUserAgent, bandwidthMeter)
    }


    override fun onBackPressed() {
        doBack()
    }

    private fun doBack() {
        if (player != null) {
            if (player!!.playbackState == Player.STATE_ENDED) {
                currentPosition = 0
            } else {
                currentPosition = player!!.currentPosition
            }
        }
        doUpdateDataInDB()
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun turnOnKeepOnScreen(isOn: Boolean) {
        if (isOn) {
            /** Turn on: */
//TODO restoring from original value
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            /** Turn off: */
            this.getWindow()
                .addFlags(WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);

        }
    }
}
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_player_vod);
//
//		// Get the view components from the layout
//		playerView = (PlayerView) findViewById(R.id.player_view);
//		// Get the controller view
//		playerControllerView = (PlayerControllerView) findViewById(R.id.player_controls);
//
//		Bundle playbackBundle = null;
//		if (savedInstanceState == null) {
//			Log.d(TAG, "Opening playback from intent bundle");
//			// This demo assumes that you send an intent to this Activity that contains the
//			// playback information.
//			if (getIntent() != null) {
//				playbackBundle = getIntent().getExtras();
//			} else {
//				Snackbar.make(playerView, "No intent specified", Snackbar.LENGTH_INDEFINITE).show();
//			}
//		} else {
//			Log.d(TAG, "Opening playback from saved state bundle");
//			playbackBundle = savedInstanceState.getBundle(SAVED_PLAYBACK_STATE_BUNDLE_KEY);
//		}
//
//		if (playbackBundle != null) {
//			try {
//				// Subscribe for the playback errors
//				playerView.getPlayerController().addPlayerListener(new AbstractPlayerListener() {
//					@Override
//					public void onError(@NonNull CastlabsPlayerException error) {
//						if (error.getSeverity() == CastlabsPlayerException.SEVERITY_ERROR) {
//							Snackbar.make(playerView, "Error while opening player: " + error.getMessage(),
//									Snackbar.LENGTH_INDEFINITE).show();
//						} else {
//							Log.e(TAG, "Error while opening player: " + error.getMessage());
//						}
//					}
//				});
//				// (Optional) Install the CDN fallback implementation
//				playerView.getPlayerController().setSourceSelector(new SourceSelector() {
//					@Nullable
//					@Override
//					public SourceSelector.SourceData onError(@NonNull String manifestUrl, @NonNull Exception error) {
//						// Have the fallback CDN only for one particular URL
//						if (manifestUrl.equals("https://demo.cf.castlabs.com/media/QA/QA_BBB_single_4/Manifest.mpd")) {
//							return new SourceData("https://demo.cf.castlabs.com/media/QA/QA_BBB_single_2/Manifest.mpd");
//						}
//						// No fallback CDN for other URLs
//						return null;
//					}
//
//					@Nullable
//					@Override
//					public PlayerConfig onRestart(@NonNull String manifestUrl, @Nullable PlayerConfig playerConfig) {
//						// Do the 'hard' restart for the selected URL when the 'soft' one fails
//						if (manifestUrl.equals("https://demo.cf.castlabs.com/media/QA/QA_BBB_single_4/Manifest.mpd")) {
//							// Simplify here and just update the currently failed player config with the fallback CDN URL
//							// Usually, ensure that the DRM and other player config parameters are valid for the
//							// returned player config or create a completely new player config
//							if (playerConfig != null) {
//								return new PlayerConfig.Builder(playerConfig)
//										.contentUrl("https://demo.cf.castlabs.com/media/QA/QA_BBB_single_2/Manifest.mpd")
//										.get();
//							}
//						}
//						// No fallback CDN for other URLs
//						return null;
//					}
//				});
//
//				// Need to pass the bundle on to the PlayerController
//				// to start playback. The open() method might throw an Exception in case the bundle
//				// contains not all mandatory parameters or the parameters are malformed.
//				playerView.getPlayerController().open(playbackBundle);
//			} catch (Exception e) {
//				Log.e(TAG, "Error while opening player: " + e.getMessage(), e);
//				Snackbar.make(playerView, "Error while opening player: " + e.getMessage(),
//						Snackbar.LENGTH_INDEFINITE).show();
//			}
//		} else {
//			Snackbar.make(playerView, "Can not start playback: no bundle specified", Snackbar.LENGTH_INDEFINITE).show();
//		}
//	}
//
//
//	@Override
//	public boolean dispatchKeyEvent(KeyEvent event) {
//		boolean handled = false;
//		if (playerControllerView != null && playerControllerView.getVisibility() == View.VISIBLE) {
//			handled = playerControllerView.dispatchKeyEvent(event);
//		} else { // Not visible
//			if (event.getAction() == KeyEvent.ACTION_DOWN){
//				handled = playerControllerView.dispatchKeyEvent(event);
//			}
//		}
//		return handled || super.dispatchKeyEvent(event);
//	}
//
//	// Delegate the onStart event to the player views lifecycle delegate.
//	// The delegate will make sure that the screen safer will be disabled and
//	// the display will not go to sleep
//	@Override
//	protected void onStart() {
//		super.onStart();
//		playerView.getLifecycleDelegate().start(this);
//	}
//
//	// Delegate the onResume event to the player views lifecycle delegate.
//	// The delegate ensures that the player recovers from a saved state. This needs to
//	// be implemented to ensure the the user can for example go to the home screen and
//	// come back to this activity.
//	@Override
//	protected void onResume() {
//		super.onResume();
//		// Bind the controller view and its listener
//		playerControllerView.bind(playerView);
//		playerControllerView.addListener(playerControllerViewListener);
//
//		PlayerControllerProgressBar progressBar = (PlayerControllerProgressBar) findViewById(R.id.progress_bar);
//		progressBar.bind(playerView.getPlayerController());
//
//		playerView.getLifecycleDelegate().resume();
//	}
//
//	// Delegate the onStop event to the player views lifecycle delegate.
//	// We release the player when the activity is stopped. This will release all the player
//	// resources and save the current playback state. Saving the state is required so the
//	// onResume callback can recover properly.
//	@Override
//	protected void onStop() {
//		super.onStop();
//
//		// Unbind the player controller view and remove the listener
//		playerControllerView.unbind();
//		playerControllerView.removeListener(playerControllerViewListener);
//
//		playerView.getLifecycleDelegate().releasePlayer(false);
//	}
//
//	// Save the playback state when the activity is destroyed in order to correctly
//	// resume after the activity is re-created again i.e. onCreate is called
//	@Override
//	public void onSaveInstanceState(Bundle outState) {
//		Bundle savedStateBundle = new Bundle();
//		PlayerConfig playerConfig = playerView.getPlayerController().getPlayerConfig();
//		if (playerConfig != null) {
//			playerView.getPlayerController().getPlayerConfig().save(savedStateBundle);
//			outState.putBundle(SAVED_PLAYBACK_STATE_BUNDLE_KEY, savedStateBundle);
//		}
//		super.onSaveInstanceState(outState);
//	}