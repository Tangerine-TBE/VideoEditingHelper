package com.twx.module_videoediting.ui.fragment

import android.net.Uri
import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.tamsiree.rxkit.view.RxToast
import com.twx.module_base.base.BaseVmFragment
import com.twx.module_base.utils.IntentUtils
import com.twx.module_base.utils.LogUtils
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.FragmentFileBinding
import com.twx.module_videoediting.livedata.ThemeChangeLiveData
import com.twx.module_base.utils.viewThemeColor
import com.twx.module_videoediting.domain.ItemBean
import com.twx.module_videoediting.domain.MediaInformation
import com.twx.module_videoediting.livedata.VideoFileLiveData
import com.twx.module_videoediting.repository.DataProvider
import com.twx.module_videoediting.ui.activity.ExportActivity
import com.twx.module_videoediting.ui.adapter.recycleview.VideoFileAdapter
import com.twx.module_videoediting.ui.popup.InputPopup
import com.twx.module_videoediting.ui.popup.ItemSelectPopup
import com.twx.module_videoediting.ui.popup.RemindDeletePopup
import com.twx.module_videoediting.utils.FileUtils
import com.twx.module_videoediting.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import java.io.File

/**
 * @name VideoEditingHelper
 * @class name：com.example.module_videoediting.ui.fragment
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/3/23 16:48:54
 * @class describe
 */
class FileFragment : BaseVmFragment<FragmentFileBinding, MainViewModel>() {

    private val mVideoFileAdapter by lazy {
        VideoFileAdapter()
    }

    private val mDeletePopup by lazy {
        RemindDeletePopup(activity)
    }

    private val mDeleteSinglePopup by lazy {
        RemindDeletePopup(activity)
    }


    private val mSelectPopup by lazy {
        ItemSelectPopup(activity)
    }

    private val mReNamePopup by lazy {
        InputPopup(activity)
    }

    override fun getChildLayout(): Int = R.layout.fragment_file
    override fun getViewModelClass(): Class<MainViewModel> {
        return MainViewModel::class.java
    }


    override fun initView() {
        binding.apply {
            data = viewModel
            videoFileContainer.apply {
                layoutManager = GridLayoutManager(activity, 2)
                adapter = mVideoFileAdapter
            }

        }

    }

    private val mDeleteSelectList = ArrayList<MediaInformation>()
    private val mCurrentVideoList = ArrayList<MediaInformation>()


    override fun observerData() {
        binding.apply {
            viewModel.apply {
                val that = this@FileFragment
                ThemeChangeLiveData.observe(that, {
                    viewThemeColor(it, fileTitle, fileEdit, fileSelectAll)
                })

                VideoFileLiveData.observe(that, {
                    if (!getEditAction_()) {
                        setVideoList(it)
                    }
                })


                editAction.observe(that, {
                    mVideoFileAdapter.setEditAction(it)
                    if (!it) {
                        mVideoFileAdapter.clearAllItems()
                        viewModel.setSelectAllState(false)
                    }
                })

                selectAllState.observe(that, {
                    viewModel.setSelectItems(mVideoFileAdapter.getSelectList())
                })


                selectItems.observe(that, {
                    mDeleteSelectList.clear()
                    mDeleteSelectList.addAll(it)
                    LogUtils.i("-----selectItems------------${it.size}-------------")
                })

                currentVideoList.observe(that, {
                    mVideoFileAdapter.setItemList(it)
                })

                //重命名状态
                renameState.observe(that, {
                    if (it.state) {
                        it.msg.apply {
                            name = it.name
                            mCurrentVideoList[it.position] = this
                            mVideoFileAdapter.setItemList(mCurrentVideoList)
                        }
                        LogUtils.i("---------renameState--------------$it----------------")
                    }
                })

            }
        }
    }

    private fun setVideoList(it: MutableList<MediaInformation>) {
        mCurrentVideoList.clear()
        mCurrentVideoList.addAll(it)
        mVideoFileAdapter.setItemList(mCurrentVideoList)
    }

    private var updatePosition = -1
    private var mItemValue: MediaInformation? = null
    override fun initEvent() {
        binding.apply {
            deleteVideo.setOnClickListener {
                mDeletePopup.apply {
                    setContent(mDeleteSelectList)
                    showPopupView(videoFileContainer)
                }
            }

            mDeletePopup.doSure {
                if (mDeleteSelectList.size > 0) {
                    viewModel.deleteMediaFile(mCurrentVideoList, mDeleteSelectList)
                }
                viewModel.setEditAction(false)
            }


            fileEdit.setOnClickListener {
                viewModel.setEditAction(!viewModel.getEditAction_())
            }

            fileSelectAll.setOnClickListener {
                mVideoFileAdapter.apply {
                    if (getSelectState()) {
                        clearAllItems()
                    } else {
                        selectAllItems()
                    }
                    viewModel.setSelectAllState(getSelectState())
                }
            }

            mVideoFileAdapter.setOnItemClickListener(object : VideoFileAdapter.OnItemClickListener {
                override fun onItemClick(item: MediaInformation, position: Int, view: View) {
                    if (viewModel.getEditAction_()) {
                        viewModel.setSelectAllState(mVideoFileAdapter.getSelectState())
                    } else {
                        ExportActivity.toExportPage(activity,false,item.path)
                    }
                }

                override fun onItemSubClick(item: MediaInformation, position: Int) {
                    mSelectPopup.apply {
                        updatePosition = position
                        mItemValue = item
                        setTitleNormal(item, DataProvider.itemSelectPopupList)
                        showPopupView(videoFileContainer)
                    }
                }
            })
            //更多操作
            mSelectPopup.setItemAction({
                //分享操作
                doItemAction {
                    IntentUtils.shareVideo(activity, File(it.path), "分享")
                }
            }, {
                //重命名操作
                mReNamePopup.apply {
                    doItemAction {
                        setHint(it.name)
                        showPopupView(videoFileContainer)
                    }
                }
            }, {
                //删除操作
                mDeleteSinglePopup.apply {
                    doItemAction {
                        setContent(arrayListOf(it))
                        showPopupView(videoFileContainer)
                    }
                }

            })

            //重命名
            mReNamePopup.apply {
                doSure {
                    doItemAction {
                        val name = getContent()
                        if (!TextUtils.isEmpty(name)) {
                            viewModel.reNameToMediaFile(name, it, updatePosition)
                        } else {
                            RxToast.normal("文件名不能为空！")
                        }
                    }
                }
            }
            //删除
            mDeleteSinglePopup.doSure {
                 doItemAction {
                     mCurrentVideoList.remove(it)
                     mVideoFileAdapter.setItemList(mCurrentVideoList)
                     viewModel.deleteMediaFile(Uri.parse(it.uri),it.path)
                 }
            }
        }

    }


    private fun doItemAction(block: (MediaInformation) -> Unit) {
        mItemValue?.let {
            block(it)
        }
    }


    override fun release() {
        mDeletePopup.dismiss()
        mDeleteSinglePopup.dismiss()
        mSelectPopup.dismiss()
        mReNamePopup.dismiss()
    }
}