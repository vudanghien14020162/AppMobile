package com.mobitv.ott.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.core.content.res.ResourcesCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import com.mobitv.ott.R

open class BaseActivity : AppCompatActivity(), View.OnClickListener {

    fun setStatusBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = this.window
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            window.statusBarColor = ResourcesCompat.getColor(resources, R.color.colorAlmostTransparent, null)
        }
    }

    fun getStatusBarHeight(): Int {
        var height = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            height = resources.getDimensionPixelSize(resourceId)
        }
        return height
    }
    override fun onClick(v: View?) {
       //do nothing
    }

    internal class Demo(
        val name: String,
        val description: String,
        private val activityClass: Class<out Activity>,
        private val bundle: Bundle
    ) {

        fun start(context: Context) {
            context.startActivity(Intent(context, activityClass).putExtras(bundle))
        }
    }

    internal class BundleBuilder {
        private val bundle: Bundle = Bundle()

        fun put(key: String, value: String): BundleBuilder {
            bundle.putString(key, value)
            return this
        }

        fun put(key: String, value: Int): BundleBuilder {
            bundle.putInt(key, value)
            return this
        }

        fun put(key: String, value: Boolean): BundleBuilder {
            bundle.putBoolean(key, value)
            return this
        }

        fun put(key: String, value: Parcelable): BundleBuilder {
            bundle.putParcelable(key, value)
            return this
        }

        fun put(key: String, value: Array<Parcelable>): BundleBuilder {
            bundle.putParcelableArray(key, value)
            return this
        }

        fun put(key: String, value: Long): BundleBuilder {
            bundle.putLong(key, value)
            return this
        }

        fun get(): Bundle {
            return bundle
        }
    }
}
