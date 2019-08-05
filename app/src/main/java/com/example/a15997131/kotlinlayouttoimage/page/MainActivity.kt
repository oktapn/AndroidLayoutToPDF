package com.example.a15997131.kotlinlayouttoimage.page

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.a15997131.kotlinlayouttoimage.R
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var b: Bitmap

    private val TAG = "PermissionDemo"
    private val STORAGE_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        f_view.isDrawingCacheEnabled = true
        f_view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
        f_view.layout(0, 0, f_view.measuredWidth, f_view.measuredHeight)
        f_view.buildDrawingCache(true)

    }

    private fun setCapture() {
        val displaymetrics = DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(displaymetrics)
        val hight = displaymetrics.heightPixels.toFloat()
        val width = displaymetrics.widthPixels.toFloat()

        val convertHighet = hight.toInt()
        val convertWidth = width.toInt()
        b = loadBitmapFromView(f_view, f_view.width, f_view.height)
        b = Bitmap.createScaledBitmap(b, convertWidth, convertHighet, true)
        f_view.isDrawingCacheEnabled = false
        val bytes = ByteArrayOutputStream()
        b.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val currentTime = Calendar.getInstance().time.toString()
        Log.i("TIME","$currentTime")
        val f = File(Environment.getExternalStorageDirectory().toString() + File.separator + "MUFON$currentTime.jpg")
        try {
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            fo.close()
        } catch (e: Exception) {
        }

        finish()
    }

    private fun loadBitmapFromView(v: View, width: Int, height: Int): Bitmap {
        val b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        v.draw(c)

        return b
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to record denied")
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                STORAGE_REQUEST_CODE)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        if (id == R.id.action_favorite) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                setupPermissions()
            } else {
                setCapture()
                openGeneratedIMAGE()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun openGeneratedIMAGE() {
        val file = File("/sdcard/v2i.jpg")
        if (file.exists()) {
            val intent = Intent(Intent.ACTION_VIEW)
            val uri = FileProvider.getUriForFile(applicationContext, this.applicationContext.packageName + ".my.package.name.provider", file)
            intent.setDataAndType(uri, "image/*")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "No Application available to view jpg", Toast.LENGTH_LONG).show()
            }
        }
    }
}


