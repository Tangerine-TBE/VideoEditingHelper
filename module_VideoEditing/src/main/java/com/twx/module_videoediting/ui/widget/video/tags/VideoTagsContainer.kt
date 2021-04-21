package com.twx.module_videoediting.ui.widget.video.tags

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tencent.liteav.basic.log.TXCLog
import com.tencent.qcloud.ugckit.component.floatlayer.FloatLayerView
import com.tencent.qcloud.ugckit.component.floatlayer.FloatLayerViewGroup
import com.tencent.qcloud.ugckit.component.timeline.RangeSliderViewContainer
import com.tencent.qcloud.ugckit.component.timeline.RangeSliderViewContainer.OnDurationChangeListener
import com.tencent.qcloud.ugckit.component.timeline.VideoProgressController
import com.tencent.qcloud.ugckit.component.timeline.ViewConst
import com.tencent.qcloud.ugckit.module.effect.BaseRecyclerAdapter
import com.tencent.qcloud.ugckit.module.effect.IFloatLayerView
import com.tencent.qcloud.ugckit.module.effect.paster.view.PasterView
import com.tencent.qcloud.ugckit.module.effect.paster.view.TCPasterOperationViewFactory
import com.tencent.qcloud.ugckit.module.effect.utils.PlayState
import com.tencent.ugc.TXVideoEditConstants.*
import com.twx.module_base.utils.viewThemeColor
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.LayoutVideoTagsContainerBinding
import com.twx.module_videoediting.domain.PasterInfo
import com.twx.module_videoediting.ui.adapter.recycleview.video.tags.AddPasterAdapter
import com.twx.module_videoediting.ui.widget.video.ganeral.BaseVideoEditUi
import com.twx.module_videoediting.utils.video.PlayerManager
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.collections.ArrayList

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.widget.video.tags
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/19 16:58:39
 * @class describe
 */
class VideoTagsContainer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseVideoEditUi(context, attrs, defStyleAttr),
    VideoProgressController.VideoProgressSeekListener,
    FloatLayerViewGroup.OnItemClickListener, IFloatLayerView.IOperationViewClickListener,
    BaseRecyclerAdapter.OnItemClickListener {
    private val TAG = "VideoTagsContainer"
    private var mVideoProgressController: VideoProgressController? = null
    private val binding = DataBindingUtil.inflate<LayoutVideoTagsContainerBinding>(
        LayoutInflater.from(context),
        R.layout.layout_video_tags_container,
        this,
        true
    )
    private var mDuration: Long = 0
    private var mDefaultWordStartTime: Long = 0
    private var mDefaultWordEndTime: Long = 0
    private val mResource = context.resources
    private val mContext = context
    private var mCurrentSelectedPos = -1 // 当前被选中的贴纸控件
    private val addIcon = R.mipmap.icon_eidt_add
    private val mAddPasterInfoList:MutableList<PasterInfo> = ArrayList()

    private  var mAddPasterAdapter:AddPasterAdapter


    init {
        binding.apply {
            viewThemeColor(themeState,secondContainer)

            timelineView.initVideoProgressLayout()
            mVideoProgressController = timelineView.videoProgressController


            mRvPaster.apply {
                val mFootView = LayoutInflater.from(context).inflate(R.layout.item_add, null)
                val addView= mFootView.findViewById<CircleImageView>(R.id.add_paster_image)
                addView.setImageResource(addIcon)

                mAddPasterAdapter=AddPasterAdapter(mAddPasterInfoList, context)
                mAddPasterAdapter.setOnItemClickListener(this@VideoTagsContainer)
                mAddPasterAdapter.setFooterView(mFootView)
                layoutManager=LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                adapter=mAddPasterAdapter

            }


            mFloatLayerViewGroup.apply {
                setOnItemClickListener(this@VideoTagsContainer)
                enableChildSingleClick(false) // 在容器里不响应子控件的单击事件
                enableDoubleChildClick(false) // 在容器里不响应子控件的双击事件
            }
            mDuration = mVideoEditorHelper.cutterEndTime - mVideoEditorHelper.cutterStartTime
            updateDefaultTime()
            initEvent()
            initRangeDurationChangeListener()
        }
    }

    private var mOnDurationChangeListener: OnDurationChangeListener? = null
    private fun initRangeDurationChangeListener() {
        mOnDurationChangeListener =
            OnDurationChangeListener { startTime, endTime -> // 获取当选中的贴纸，并且将时间设置进去
                val view = binding.mFloatLayerViewGroup.selectedLayerOperationView as? PasterView
                view?.setStartToEndTime(startTime, endTime)
                // 时间范围修改也马上设置到sdk中去
                addPasterListVideo()
                // saveIntoManager()
            }
    }


    fun getPlayerView() = binding.mVideoPasterPlayerControl
    override fun initPlayerLayout() {
        mVideoEditorHelper.let {
            binding.apply {
                //   loadVideoInfo()
                // 初始化播放器界面[必须在setPictureList/setVideoPath设置数据源之后]
                mVideoPasterPlayerControl.initPlayerLayout()
                it.resetDuration()
            }
        }
    }

    override fun onItemClick(view: View?, position: Int) {
        binding.apply {
            if (position == mAddPasterInfoList.size) {
                // 新增
                clickBtnAdd()
            } else {
                if (!mFloatLayerViewGroup.isShown) {
                    mFloatLayerViewGroup.visibility = VISIBLE
                    // 暂停播放
                    mVideoEditorHelper.editer.refreshOneFrame() // 将视频画面中的字幕清除  ，避免与上层控件造成混淆导致体验不好的问题。
                    PlayerManager.pausePlay()
                }
                // 列表选中
                mAddPasterAdapter.setCurrentSelectedPos(position)
                // 预览界面选中
                mFloatLayerViewGroup.selectOperationView(position)
                // 进度条范围选中
                val lastSlider = mVideoProgressController?.getRangeSliderView(
                    ViewConst.VIEW_TYPE_PASTER,
                    mCurrentSelectedPos
                )
                lastSlider?.setEditComplete()
                val currentSlider =
                    mVideoProgressController?.getRangeSliderView(
                        ViewConst.VIEW_TYPE_PASTER,
                        position
                    )
                currentSlider?.showEdit()
                mCurrentSelectedPos = position
            }
        }
    }

    private fun clickBtnAdd() {
        binding.apply {
            mPasterPannel.visibility=View.VISIBLE
            mFloatLayerViewGroup.visibility = VISIBLE
            // 暂停播放
            mVideoEditorHelper.editer.refreshOneFrame() // 将视频画面中的字幕清除  ，避免与上层控件造成混淆导致体验不好的问题。
            PlayerManager.pausePlay()
        }
    }


    private fun initEvent() {
        binding.apply {
            mPasterPannel.setDismissAction {
                mPasterPannel?.visibility=View.GONE
            }

            mVideoPasterPlayerControl.getDeleteAction()?.setOnClickListener {
                deletePaster()
            }


            mVideoPasterPlayerControl.setPlayState {
                when(it){
                    PlayState.STATE_PLAY, PlayState.STATE_RESUME -> mFloatLayerViewGroup?.visibility =
                        View.GONE
                    PlayState.STATE_PAUSE -> {
                        // 将视频画面中的字幕清除  ，避免与上层控件造成混淆导致体验不好的问题。
                        mVideoEditorHelper.editer.refreshOneFrame()
                        mFloatLayerViewGroup?.visibility = View.VISIBLE
                    }
                }
            }

            val pasterAdapter = mPasterPannel.getPasterAdapter()
            val pasterList = pasterAdapter.getListData()
            if (pasterList.size > 0) {
                pasterAdapter.setOnItemClickListener { adapter, view, position ->
                    val index = mFloatLayerViewGroup.selectedViewIndex
                    var bitmap: Bitmap? = null
                    val lastSlider = mVideoProgressController?.getRangeSliderView(index)
                    lastSlider?.setEditComplete()
                    val pasterInfo = pasterList[position]

                    if (pasterInfo.type == PasterView.TYPE_CHILD_VIEW_ANIMATED_PASTER) {

                    } else {
                        bitmap = BitmapFactory.decodeResource(mResource, pasterInfo.icon)
                    }


                    // 更新一下默认配置的时间
                    updateDefaultTime()

                    val pasterOperationView =
                        TCPasterOperationViewFactory.newOperationView(mContext)
                    pasterOperationView.childType = pasterInfo.type
                    pasterOperationView.centerX = (mFloatLayerViewGroup.width / 2).toFloat()
                    pasterOperationView.centerY = (mFloatLayerViewGroup.height / 2).toFloat()
                    pasterOperationView.setStartToEndTime(
                        mDefaultWordStartTime,
                        mDefaultWordEndTime
                    )
                    pasterOperationView.setIOperationViewClickListener(this@VideoTagsContainer)
                    pasterOperationView.pasterName = pasterInfo.name
                    pasterOperationView.showDelete(false)
                    pasterOperationView.showEdit(false)


                    val rangeSliderView = RangeSliderViewContainer(mContext)
                    rangeSliderView.init(
                        mVideoProgressController!!,
                        mDefaultWordStartTime,
                        mDefaultWordEndTime - mDefaultWordStartTime,
                        mDuration
                    )
                    rangeSliderView.setDurationChangeListener(mOnDurationChangeListener)
                    mVideoProgressController?.addRangeSliderView(
                        ViewConst.VIEW_TYPE_PASTER,
                        rangeSliderView
                    )
                    mVideoProgressController?.currentTimeMs = mDefaultWordStartTime


                    mFloatLayerViewGroup.addOperationView(pasterOperationView)
                    bitmap?.let {
                        pasterOperationView.setImageBitamp(it)
                    }
                     mPasterPannel.visibility=View.GONE

                    // 更新下方的贴纸列表
                    mAddPasterInfoList.add(pasterInfo)
                    mAddPasterAdapter.notifyDataSetChanged()
                    mAddPasterAdapter.setCurrentSelectedPos(mAddPasterInfoList.size - 1)

                    mCurrentSelectedPos = mAddPasterInfoList.size - 1


                    addPasterListVideo()
                    // saveIntoManager()

                }
            }
        }

    }

    private fun deletePaster() {
        binding.apply {
            val index: Int = mFloatLayerViewGroup.selectedViewIndex
            if (index < 0) {
                return
            }
            val view = mFloatLayerViewGroup.selectedLayerOperationView as PasterView
            if (view != null) {
                mFloatLayerViewGroup.removeOperationView(view)
            }
            mVideoProgressController!!.removeRangeSliderView(ViewConst.VIEW_TYPE_PASTER, index)
            if (mAddPasterInfoList.size > 0) {
                mAddPasterInfoList.removeAt(index)
            }
            mAddPasterAdapter.notifyDataSetChanged()
            mCurrentSelectedPos = -1
            mAddPasterAdapter.setCurrentSelectedPos(mCurrentSelectedPos)
            addPasterListVideo()
            //saveIntoManager()
        }
    }



    /**
     * 根据当前控件数量 更新默认的一个控件开始时间和结束时间
     */
    private fun updateDefaultTime() {
        binding.apply {
            val count = mFloatLayerViewGroup?.childCount
            mDefaultWordStartTime = (count * 1000).toLong() // 两个之间间隔1秒
            mDefaultWordEndTime = mDefaultWordStartTime + 2000
            if (mDefaultWordStartTime > mDuration) {
                mDefaultWordStartTime = mDuration - 2000
                mDefaultWordEndTime = mDuration
            } else if (mDefaultWordEndTime > mDuration) {
                mDefaultWordEndTime = mDuration
            }
        }

    }


    /**
     * ===========================将贴纸添加到SDK中去=================================
     */
    private fun addPasterListVideo() {
        binding.apply {
            val animatedPasterList: MutableList<TXAnimatedPaster> = ArrayList()
            val pasterList: MutableList<TXPaster> = ArrayList()
            for (i in 0 until mFloatLayerViewGroup.childCount) {
                val view = mFloatLayerViewGroup.getOperationView(i) as PasterView
                val rect = TXRect()
                rect.x = view.imageX.toFloat()
                rect.y = view.imageY.toFloat()
                rect.width = view.imageWidth.toFloat()
                TXCLog.i(
                    TAG,
                    "addPasterListVideoToEditer, adjustPasterRect, paster x y = " + rect.x + "," + rect.y
                )
                val childType = view.childType
                if (childType == PasterView.TYPE_CHILD_VIEW_ANIMATED_PASTER) {
                    val txAnimatedPaster = TXAnimatedPaster()
                    //  txAnimatedPaster.animatedPasterPathFolder = mAnimatedPasterSDcardFolder + view.pasterName + File.separator
                    txAnimatedPaster.startTime = view.startTime
                    txAnimatedPaster.endTime = view.endTime
                    txAnimatedPaster.frame = rect
                    txAnimatedPaster.rotation = view.imageRotate
                    animatedPasterList.add(txAnimatedPaster)
                    TXCLog.i(
                        TAG,
                        "addPasterListVideoToEditer, txAnimatedPaster startTimeMs, endTime is : " + txAnimatedPaster.startTime + ", " + txAnimatedPaster.endTime
                    )
                } else if (childType == PasterView.TYPE_CHILD_VIEW_PASTER) {
                    val txPaster = TXPaster()
                    txPaster.pasterImage = view.rotateBitmap
                    txPaster.startTime = view.startTime
                    txPaster.endTime = view.endTime
                    txPaster.frame = rect
                    pasterList.add(txPaster)
                    TXCLog.i(
                        TAG,
                        "addPasterListVideoToEditer, txPaster startTimeMs, endTime is : " + txPaster.startTime + ", " + txPaster.endTime
                    )
                }
            }
            mVideoEditorHelper.editer.setAnimatedPasterList(animatedPasterList)
            mVideoEditorHelper.editer.setPasterList(pasterList)
        }

    }


    override fun onVideoProgressSeek(currentTimeMs: Long) {
        PlayerManager.previewAtTime(currentTimeMs)
    }

    override fun onVideoProgressSeekFinish(currentTimeMs: Long) {
        PlayerManager.previewAtTime(currentTimeMs)
    }


    override fun onLayerOperationViewItemClick(
        view: FloatLayerView?,
        lastSelectedPos: Int,
        currentSelectedPos: Int
    ) {
        pausePlay(true)

        val lastSlider = mVideoProgressController!!.getRangeSliderView(lastSelectedPos)
        lastSlider?.setEditComplete()

        val currentSlider = mVideoProgressController!!.getRangeSliderView(currentSelectedPos)
        currentSlider?.showEdit()

        mCurrentSelectedPos = currentSelectedPos


    }

    private fun pausePlay(isShow: Boolean) {
        binding.apply {
            PlayerManager.pausePlay()
            if (isShow) {
                // 将字幕控件显示出来
                mFloatLayerViewGroup.visibility = VISIBLE
                mVideoEditorHelper.editer.refreshOneFrame() // 将视频画面中的字幕清除  ，避免与上层控件造成混淆导致体验不好的问题。
            }
            val selectedIndex: Int = mFloatLayerViewGroup.selectedViewIndex
            if (selectedIndex != -1) { // 说明有控件被选中 那么显示出时间区间的选择
                val view = mVideoProgressController?.getRangeSliderView(selectedIndex)
                if (isShow) {
                    view?.showEdit()
                } else {
                    view?.setEditComplete()
                }
            }
        }

    }


    override fun onDeleteClick() {

    }

    override fun onEditClick() {

    }

    override fun onRotateClick() {
        addPasterListVideo()
    }



}