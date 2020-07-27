package com.mobitv.ott.other

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Handler
import android.text.Html
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobitv.ott.R
import com.mobitv.ott.activity.BaseActivity
import com.mobitv.ott.activity.VodDetailsActivity
import java.io.*
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*


class AppUtils {
    companion object {
        fun copyAssetsToFile(inputStream: InputStream, des: String) {
            val fileOutputStream = FileOutputStream(des)
            inputStream.copyTo(fileOutputStream)
            fileOutputStream.close()
            inputStream.close()
        }

        fun decodeBitmapDependOnMemory(context: Context, path: String, assets: Boolean): Bitmap? {
            var b: Bitmap? = null
            try {
                // Decode image size
                val o = BitmapFactory.Options()
                o.inJustDecodeBounds = true
                var fis: InputStream
                if (assets) {
                    fis = context.assets.open(path)
                } else {
                    fis = FileInputStream(File(path))
                }
                try {
                    BitmapFactory.decodeStream(fis, null, o)
                } finally {
                    fis.close()
                }
                // In Samsung Galaxy S3, typically max memory is 64mb
                // Camera max resolution is 3264 x 2448, times 4 to get Bitmap memory of 30.5mb for one bitmap
                // If we use scale of 2, resolution will be halved, 1632 x 1224 and x 4 to get Bitmap memory of 7.62mb
                // We try use 25% memory which equals to 16mb maximum for one bitmap
                val maxMemory = Runtime.getRuntime().maxMemory()
                val maxMemoryForImage = (maxMemory / 100 * 25).toInt()
                // Refer to
                // http://developer.android.com/training/displaying-bitmaps/cache-bitmap.html
                // A full screen GridView filled with images on a device with
                // 800x480 resolution would use around 1.5MB (800*480*4 bytes)
                // When bitmap option's inSampleSize doubled, pixel height and
                // weight both reduce in half
                var scale = 1
                while (o.outWidth / scale * (o.outHeight / scale) * 4 > maxMemoryForImage)
                    scale *= 2
                // Decode with inSampleSize
                val o2 = BitmapFactory.Options()
                o2.inSampleSize = scale
                if (assets) {
                    fis = context.assets.open(path)
                } else {
                    fis = FileInputStream(File(path))
                }
                try {
                    b = BitmapFactory.decodeStream(fis, null, o2)
                } finally {
                    fis.close()
                }
            } catch (e: IOException) {
            }

            return b
        }

        internal fun loadJSON(context: Context, path: String, isAssets: Boolean): String? {
            val json: String
            try {
                if (isAssets) {
                    val inputStream = context.assets.open(path)
                    val size = inputStream.available()
                    val buffer = ByteArray(size)
                    inputStream.read(buffer)
                    inputStream.close()
                    json = String(buffer, Charset.forName("UTF-8"))
                } else {
                    val file = File(path)
                    val fileInputStream = FileInputStream(file)
                    val size = fileInputStream.available()
                    val buffer = ByteArray(size)
                    fileInputStream.read(buffer)
                    fileInputStream.close()
                    json = String(buffer, Charset.forName("UTF-8"))
                }
            } catch (ex: IOException) {
                ex.printStackTrace()
                return null
            }

            return json
        }

        internal fun writeJsonToFile(json: String, path: String) {
            try {
                if (!isExist(path)) {
                    val file = File(path)
                    file.createNewFile()
                }
                val fw = FileWriter(path, false)
                fw.write(json)
                fw.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        fun isExist(path: String): Boolean {
            val f = File(path)
            return f.exists()
        }

        fun checkNetworkState(activity: Activity): Boolean {
            val cm = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting
        }

        fun createFolder(path: String) {
            val f = File(path)
            if (!f.exists()) {
                f.mkdirs()
            }
        }

        fun scrollToNearItem(
            recyclerView: androidx.recyclerview.widget.RecyclerView,
            position: Int
        ) {
            val lastVisiblePos =
                (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
            val firstVisiblePos =
                (recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
            if (position == lastVisiblePos) {
                recyclerView.smoothScrollToPosition(lastVisiblePos + 1)
            } else if (position == firstVisiblePos) {
                if (firstVisiblePos > 1) recyclerView.smoothScrollToPosition(firstVisiblePos - 1)
            }
        }

        fun hideSoftKeyboard(activity: Activity) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
        }

        fun showSoftKeyboard(activity: Activity, view: View) {
            Handler().postDelayed({
                if (view.requestFocus()) {
                    val imm =
                        activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
                }
            }, 200)
        }

        fun scanImages(strFolderPath: String): List<String>? {
            val folder = File(strFolderPath)
            val result = ArrayList<String>()
            if (folder.exists()) {
                val listFile = folder.list { _, filename ->
                    var fName = filename
                    fName = fName.toLowerCase()
                    fName.endsWith(".jpg") || fName.endsWith(".png")
                }
                if (listFile != null) {
                    for (aListFile in listFile) {
                        result.add("$strFolderPath/$aListFile")
                    }
                    return result
                }
            } else {
                createFolder(strFolderPath)
            }
            return null
        }

        fun refreshGallery(file: File, context: Context) {
            val scanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val contentUri = Uri.fromFile(file)
            scanIntent.data = contentUri
            context.sendBroadcast(scanIntent)
        }

        fun deleteFile(path: String) {
            val f = File(path)
            f.delete()
        }

        fun showToast(context: Context, message: String) {
            val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.CENTER, 0, 0)
            if (toast.view != null && !toast.view.isShown) {
                toast.show()
            }
        }

        fun convertStreamToString(inputStream: InputStream?): String {
            if (inputStream != null) {
                val r = BufferedReader(InputStreamReader(inputStream))
                val total = StringBuilder()
                var line: String
                try {
                    do {
                        line = r.readLine()
                        total.append(line)
                    } while (r.readLine() != null)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                return total.toString()
            }
            return ""
        }

        fun displaySystemUI(activity: BaseActivity, isFull: Boolean) {
            if (isFull) {
//                activity.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                activity.window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE)
            } else {
//                activity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                activity.setStatusBarTransparent()
                activity.window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE)
            }
        }

        fun formatDuration(durationInSecond: Long): String {
            var strDuration = ""
            var second = durationInSecond.toInt()
            var minute = 0
            var hour = 0
            if (second >= 60) {
                minute = second / 60
                second %= 60
                if (minute >= 60) {
                    hour = minute / 60
                    minute %= 60
                }
            }
            if (hour == 0) {
                strDuration = String.format(Locale.US, "%02d:%02d", minute, second)
            } else {
                strDuration = String.format(Locale.US, "%02d:%02d:%02d", hour, minute, second)
            }
            return strDuration
        }

        fun formatDateTimeToHour(dateTime: String): String {
            var sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale("vi"))
            sdf.timeZone = TimeZone.getTimeZone("GMT+7")
            val res: Date
            var result = ""
            try {
                res = sdf.parse(dateTime)
                sdf = SimpleDateFormat("HH:mm", Locale("vi"))
                result = sdf.format(res)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            return result
        }

        fun formatCurrentTimeToDateTime(currentTimeInMillis: Long): String {
            val sdf = SimpleDateFormat("EEEE, dd-MM-yyyy", Locale("vi"))
            val resultDate = Date(currentTimeInMillis)
            return sdf.format(resultDate)
        }

        fun formatDateStringToTimeInMillis(dateInString: String): Long {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("vi"))
            return sdf.parse(dateInString).time
        }

        fun formatDateTimeEPGToTimeInMillis(dateInString: String): Long {
            val sdf = SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale("vi"))
            return sdf.parse(dateInString).time
        }

        fun goVodDetails(activity: Activity, id: Int, type: String) {
            val intent = Intent(activity, VodDetailsActivity::class.java)
            intent.putExtra(Const.ID, id)
            intent.putExtra(Const.TYPE, type)
            activity.startActivity(intent)
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        fun setTextHtml(textView: TextView, strHtml: String) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                textView.text = Html.fromHtml(strHtml, Html.FROM_HTML_MODE_LEGACY)
            } else {
                textView.text = Html.fromHtml(strHtml)
            }
        }
    }

}