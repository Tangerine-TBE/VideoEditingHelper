package com.twx.module_videoediting.ui.widget.video.music

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import com.tencent.qcloud.ugckit.component.slider.RangeSlider
import com.tencent.qcloud.ugckit.module.effect.bgm.view.IEditMusicPannel
import com.tencent.qcloud.ugckit.module.record.MusicInfo
import com.tencent.qcloud.ugckit.utils.DateTimeUtil
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.TwLayoutEditMusicBinding
import com.twx.module_videoediting.ui.widget.video.ganeral.BaseVideoUi

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.widget.video.music
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/9 17:51:21
 * @class describe
 */
class EditMusicView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseVideoUi(context, attrs, defStyleAttr), IEditMusicPannel, SeekBar.OnSeekBarChangeListener,
    RangeSlider.OnRangeChangeListener {
    private var mMicVolume = 100
    private var mBGMVolume = 100
    private val binding = DataBindingUtil.inflate<TwLayoutEditMusicBinding>(
        LayoutInflater.from(context),
        R.layout.tw_layout_edit_music,
        this,
        true
    )

    init {

        initEvent()

    }

    fun initEvent() {
        binding.apply {
            seekbarBgmVolume.setOnSeekBarChangeListener(this@EditMusicView)
            seekbarMicVolume.setOnSeekBarChangeListener(this@EditMusicView)
            bgmRangeSlider.setRangeChangeListener(this@EditMusicView)

            btnBgmDelete.setOnClickListener {
                mMusicChangeListener?.onMusicDelete()
            }

            btnBgmReplace.setOnClickListener {
                mMusicChangeListener?.onMusicReplace()
            }

        }
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        if (seekBar.id == com.tencent.qcloud.ugckit.R.id.seekbar_mic_volume) {
            mMicVolume = progress
            mMusicChangeListener?.onMicVolumeChanged(mMicVolume / 100.toFloat())

        } else if (seekBar.id == com.tencent.qcloud.ugckit.R.id.seekbar_bgm_volume) {
            mBGMVolume = progress
            mMusicChangeListener?.onMusicVolumChanged(mBGMVolume / 100.toFloat())
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onKeyDown(type: Int) {

    }

    override fun onKeyUp(type: Int, leftPinIndex: Int, rightPinIndex: Int) {
        val leftTime = mBgmDuration * leftPinIndex / 100
        val rightTime = mBgmDuration * rightPinIndex / 100
        binding.tvBgmStartTime.text = String.format(
            resources.getString(com.tencent.qcloud.ugckit.R.string.bgm_start_position),
            DateTimeUtil.millsecondToMinuteSecond(leftTime.toInt())
        )
        if (mMusicChangeListener != null) {
            mMusicChangeListener!!.onMusicTimeChanged(leftTime, rightTime)
        }

        binding.tvBgmStartTime.text = String.format(
            resources.getString(com.tencent.qcloud.ugckit.R.string.bgm_start_position),
            DateTimeUtil.millsecondToMinuteSecond(
                leftTime.toInt()
            )
        )
    }

    private var mBgmDuration: Long = 0
    override fun setMusicInfo(musicInfo: MusicInfo) {
        binding.apply {
            if (!TextUtils.isEmpty(musicInfo.name)) {
                txMusicName.text = musicInfo.name
            }
            if (seekbarMicVolume != null && musicInfo.videoVolume != -1f) {
                seekbarMicVolume.progress = (musicInfo.videoVolume * 100).toInt()
            }

            if (seekbarBgmVolume != null && musicInfo.bgmVolume != -1f) {
                seekbarBgmVolume.progress = (musicInfo.bgmVolume * 100).toInt()
            }
            mBgmDuration = musicInfo.duration
            setCutRange(musicInfo.startTime, musicInfo.endTime)
        }

    }

    private var mMusicChangeListener: IEditMusicPannel.MusicChangeListener? = null
    override fun setOnMusicChangeListener(listener: IEditMusicPannel.MusicChangeListener?) {
        mMusicChangeListener = listener
    }

    private fun setCutRange(startTime: Long, endTime: Long) {
        binding.apply {
            if (startTime == -1L || endTime == -1L) return
            if (bgmRangeSlider != null && mBgmDuration != 0L) {
                val left = (startTime * 100 / mBgmDuration).toInt()
                val right = (endTime * 100 / mBgmDuration).toInt()
                bgmRangeSlider.setCutRange(left, right)
            }
            if (tvBgmStartTime != null) {
                tvBgmStartTime.text = String.format(
                    resources.getString(R.string.bgm_start_position),
                    DateTimeUtil.millsecondToMinuteSecond(
                        startTime.toInt()
                    )
                )
            }
        }
    }


}