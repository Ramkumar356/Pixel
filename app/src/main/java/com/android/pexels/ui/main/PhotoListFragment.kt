package com.android.pexels.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.mygithub.adapters.PhotoListAdapter
import com.android.pexels.R
import com.android.pexels.utilities.PaginationScrollListener
import com.android.pexels.viewmodels.PhotoListViewModel

class PhotoListFragment : Fragment() {


    companion object {
        fun newInstance() = PhotoListFragment()
    }

    private lateinit var viewModel: PhotoListViewModel

    private lateinit var pexelImageGridView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pexelImageGridView = view.findViewById(R.id.pexelPhotoList)
        pexelImageGridView.adapter = PhotoListAdapter(arrayListOf())
        pexelImageGridView.setHasFixedSize(true)
        pexelImageGridView.layoutManager =
            GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PhotoListViewModel::class.java)
        viewModel.photoList.observe(
            this,
            Observer {
                (pexelImageGridView.adapter as PhotoListAdapter).photoList.also { list ->
                    list.addAll(it)
                    (pexelImageGridView.adapter as PhotoListAdapter).notifyDataSetChanged()
                }

            })

        viewModel.loadPhotos(0, 30)
        pexelImageGridView.addOnScrollListener(object :
            PaginationScrollListener(pexelImageGridView.layoutManager as GridLayoutManager) {
            var page = 0
            var loading = false

            override fun loadMoreItems() {
                page += 1
                loading = true
                viewModel.loadPhotos(page, 30)
            }

            override val isLastPage: Boolean
                get() = false
            override val isLoading: Boolean
                get() = false

        });
    }

}
