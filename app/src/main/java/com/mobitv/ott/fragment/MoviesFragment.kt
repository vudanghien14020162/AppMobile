package com.mobitv.ott.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobitv.ott.R
import com.mobitv.ott.activity.ListVodActivity
import com.mobitv.ott.activity.VodDetailsActivity
import com.mobitv.ott.adapter.ListMoviesCategoryRVAdapter
import com.mobitv.ott.adapter.OnItemNestedListClickListener
import com.mobitv.ott.model.VodCategoryModel
import com.mobitv.ott.other.AppUtils
import com.mobitv.ott.other.Const
import com.mobitv.ott.pojo.APIClient
import com.mobitv.ott.pojo.APIInterface
import com.mobitv.ott.pojo.VodCategoryResponse
import kotlinx.android.synthetic.main.fragment_movies.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.mobitv.ott.fragment.MoviesFragment

class MoviesFragment : Fragment(), OnItemNestedListClickListener {

    companion object {
        const val TAG = "movies"
        fun createInstance(): MoviesFragment {
            return MoviesFragment()
        }
    }

    private var moviesCategoryRVAdapter: ListMoviesCategoryRVAdapter? = null
    private val listVodCategoryModel: ArrayList<VodCategoryModel> = ArrayList()
    private var apiInterface: APIInterface? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_movies, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvMovies.layoutManager = LinearLayoutManager(
            activity!!,
            RecyclerView.VERTICAL,
            false
        )
        rvMovies.setHasFixedSize(true)
        moviesCategoryRVAdapter =
            ListMoviesCategoryRVAdapter(activity!!, listVodCategoryModel, resources.displayMetrics.widthPixels)
        moviesCategoryRVAdapter?.setItemClickListener(this)
        rvMovies.adapter = moviesCategoryRVAdapter
        loadData()
    }

    private fun goCategoryDetails(id: String, name: String) {
        val intent = Intent(activity!!, ListVodActivity::class.java)
        intent.putExtra(Const.ID, id)
        intent.putExtra(Const.NAME, name)
        intent.putExtra(Const.TYPE, Const.FILM)
        startActivity(intent)
        activity!!.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun goMovieDetails(id: Int) {
        AppUtils.goVodDetails(activity!!, id, Const.FILM)
    }

    override fun onHeaderClick(tag: String, position: Int, id: String) {
        goCategoryDetails(id, listVodCategoryModel[position].name)
    }

    override fun onItemClick(tag: String, position: Int, id: Int) {
        goMovieDetails(id)
    }

    override fun onItemLongClick(tag: String, position: Int, id: Int) {
    }

    override fun onMoreClick(tag: String, catID: String, categoryName: String) {
        goCategoryDetails(catID, categoryName)
    }

    private fun loadData() {
        apiInterface = APIClient.getInstance(activity!!).client.create(APIInterface::class.java)
        val call =
            apiInterface?.doGetMoviesList()
        call?.enqueue(object : Callback<VodCategoryResponse> {
            override fun onResponse(
                call: Call<VodCategoryResponse>,
                categoryResponse: Response<VodCategoryResponse>
            ) {
                pbLoadingMovies?.visibility = View.GONE
                val listResponseObject = categoryResponse.body()
                if (listResponseObject?.statusCode == Const.STATUS_CODE_SUCCESS) {
                    for (i in 0 until listResponseObject.listVodCategoryModel.size) {
                        listVodCategoryModel.add(listResponseObject.listVodCategoryModel[i])
                    }
                    moviesCategoryRVAdapter?.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<VodCategoryResponse>, t: Throwable) {
                call.cancel()
                pbLoadingMovies?.visibility = View.GONE
            }
        })
    }
}