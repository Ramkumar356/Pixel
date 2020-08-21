package com.android.pexels.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
        pexelImageGridView.addOnScrollListener(object :
            PaginationScrollListener(pexelImageGridView.layoutManager as GridLayoutManager) {
            var page = 0
            override fun loadMoreItems() {
                page += 1
                viewModel.loadPhotos(page, 30)
            }

            override val isLastPage: Boolean
                get() = false
            override val isLoading: Boolean
                get() = false

        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PhotoListViewModel::class.java)
        viewModel.photoList.observe(
            this,
            Observer {
                val adapter = (pexelImageGridView.adapter as PhotoListAdapter)
                adapter.photoList.also { list ->
                    list.addAll(it)
                    adapter.notifyItemRangeInserted(list.size - it.size, it.size)
                }
            })
        viewModel.photoFetchError.observe(this, Observer {
            val adapter = (pexelImageGridView.adapter as PhotoListAdapter)
            adapter.photoList.run {
                if (isEmpty()) {
                    Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
                }
            }
        })
        viewModel.loadPhotos(0, 30)
    }
}
