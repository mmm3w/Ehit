package com.mitsuki.ehit.ui.download.fragment

import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BindingFragment
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.crutch.event.receiver
import com.mitsuki.ehit.crutch.extensions.isClick
import com.mitsuki.ehit.crutch.extensions.observe
import com.mitsuki.ehit.crutch.extensions.text
import com.mitsuki.ehit.databinding.FragmentDownloadBinding
import com.mitsuki.ehit.model.activityresult.ExportZipActivityResultContract
import com.mitsuki.ehit.model.entity.DownloadListInfo
import com.mitsuki.ehit.model.entity.Gallery
import com.mitsuki.ehit.service.download.DownloadBroadcast
import com.mitsuki.ehit.service.download.DownloadEvent
import com.mitsuki.ehit.ui.common.adapter.ListStatesAdapter
import com.mitsuki.ehit.ui.common.dialog.BottomMenuDialogFragment
import com.mitsuki.ehit.ui.download.adapter.DownloadAdapter
import com.mitsuki.ehit.ui.download.adapter.ListItemTouchCallback
import com.mitsuki.ehit.ui.download.dialog.ExportProgressDialog
import com.mitsuki.ehit.viewmodel.DownloadViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DownloadFragment : BindingFragment<FragmentDownloadBinding>(
    R.layout.fragment_download,
    FragmentDownloadBinding::bind
) {

    private val mViewModel: DownloadViewModel by viewModels()

    private val mStatesAdapter by lazy { ListStatesAdapter() }
    private val mMainAdapter by lazy { DownloadAdapter() }
    private val mAdapter by lazy { ConcatAdapter(mStatesAdapter, mMainAdapter) }

    private val mExportZip = registerForActivityResult(ExportZipActivityResultContract()) {
        val uri = it.second
        val folder = it.first
        if (uri == null) {
            return@registerForActivityResult
        }

        ExportProgressDialog(uri, folder).show(childFragmentManager, "export")
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding?.apply {
            topBar.topBarText.text = getText(R.string.text_download)
            topBar.topBarBack.setOnClickListener { requireActivity().onBackPressed() }
            topBar.topBarStart.setOnClickListener { DownloadEvent.startAllDownload(requireContext()) }
            topBar.topBarPause.setOnClickListener { DownloadEvent.stopAllDownload() }
            requireContext().resources.run {
                getDimensionPixelSize(getIdentifier("status_bar_height", "dimen", "android"))
            }.also { downloadParent.setPadding(0, it, 0, 0) }

            ItemTouchHelper(ListItemTouchCallback {
                mMainAdapter.item(it).apply {
                    DownloadBroadcast.sendStop(gid, token)
                    mViewModel.deleteDownload(gid, token)
                }
            }).attachToRecyclerView(downloadList)

            downloadList.layoutManager = LinearLayoutManager(requireContext())
            downloadList.adapter = mAdapter
        }


        mMainAdapter.receiver<Pair<View, DownloadListInfo>>("option")
            .isClick()
            .observe(viewLifecycleOwner) { onItemOption(it.first, it.second) }
        mMainAdapter.receiver<Pair<View, DownloadListInfo>>("detail")
            .isClick()
            .observe(viewLifecycleOwner) { openDetail(it.first, it.second.toGallery()) }
        mMainAdapter.receiver<DownloadListInfo>("read")
            .isClick()
            .observe(viewLifecycleOwner, this::openRead)

        lifecycleScope.launchWhenStarted {
            mViewModel.downloadList().collect {
                mStatesAdapter.listState =
                    if (it.isEmpty()) ListStatesAdapter.ListState.Message(text(R.string.text_content_empty)) else ListStatesAdapter.ListState.None
                mMainAdapter.submitData(it)
            }
        }
    }

    private fun onItemOption(view: View, info: DownloadListInfo) {
        BottomMenuDialogFragment(
            intArrayOf(
                R.string.text_read,
                R.string.text_detail,
                R.string.text_export_zip,
                R.string.text_export_image,
                R.string.text_delete,
            )
        ) {
            when (it) {
                0 -> openRead(info)
                1 -> openDetail(view, info.toGallery())
                2 -> mExportZip.launch(arrayOf(info.title, info.gid.toString(), info.token))
                3 -> {
                    //TODO
                }
                4 -> with(info) {
                    DownloadBroadcast.sendStop(gid, token)
                    mViewModel.deleteDownload(gid, token)
                }
            }
            true
        }.show(childFragmentManager, "option")
    }

    private fun openDetail(view: View, data: Gallery) {
        Navigation.findNavController(requireActivity(), R.id.main_nav_fragment)
            .navigate(
                R.id.action_download_fragment_to_gallery_detail_fragment,
                bundleOf(DataKey.GALLERY_INFO to data),
                null,
                FragmentNavigatorExtras(view to data.itemTransitionName)
            )
    }

    private fun openRead(info: DownloadListInfo) {

    }
}