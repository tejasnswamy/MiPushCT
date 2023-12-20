package com.example.xioamiintegration

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.clevertap.android.sdk.*
import com.clevertap.android.sdk.inapp.CTInAppNotification
import com.clevertap.android.sdk.inapp.CTLocalInApp
import com.clevertap.android.sdk.pushnotification.PushConstants
import com.example.xioamiintegration.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.xiaomi.channel.commonutils.android.Region
import com.xiaomi.mipush.sdk.MiPushClient

class MainActivity : AppCompatActivity() , InAppNotificationButtonListener ,
    PushPermissionResponseListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var _ctInstance: CleverTapAPI? = null
    private var _location:Location? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        _ctInstance = CleverTapAPI.getDefaultInstance(applicationContext)
        askForNotificationPermission()
        _ctInstance?.setInAppNotificationButtonListener(this)
        _location = _ctInstance?.location
        _ctInstance?.location = _location

        CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.VERBOSE) //Set Log level to VERBOSE
        CleverTapAPI.enableXiaomiPushOn(PushConstants.ALL_DEVICES)
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            CleverTapAPI.getDefaultInstance(this)?.pushEvent("gif event")
            Snackbar.make(view, "gif event", Snackbar.LENGTH_LONG)
                .setAction("Pushed", null).show()
        }
        MiPushClient.setRegion(Region.India);
        MiPushClient.registerPush(
            this, this.resources.getString(R.string.xiaomi_app_id),
            this.resources.getString(R.string.xiaomi_app_key)
        )
        val xiaomiToken = MiPushClient.getRegId(this)
        val xiaomiRegion = MiPushClient.getAppRegion(this)

        _ctInstance?.pushXiaomiRegistrationId(xiaomiToken, xiaomiRegion, true)
            ?: Log.e("XMpush", "CleverTap is NULL")

        val channel = NotificationChannel(
            "got",
            "Game of Thrones",
            NotificationManager.IMPORTANCE_HIGH,
        )
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

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
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)


    @SuppressLint("RestrictedApi")
    private fun askForNotificationPermission() {
        //requestLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)

        Toast.makeText(applicationContext, "Enable Notification", Toast.LENGTH_SHORT).show()

        val jsonObject = CTLocalInApp.builder()
            .setInAppType(CTLocalInApp.InAppType.HALF_INTERSTITIAL)
            .setTitleText("Get Notified \uD83D\uDD14")
            .setMessageText("Please enable notifications on your device to use Push Notifications.")
            .followDeviceOrientation(true)
            .setPositiveBtnText("Allow")
            .setNegativeBtnText("Cancel")
            .setBackgroundColor(Constants.WHITE)
            .setBtnBorderColor(Constants.BLUE)
            .setTitleTextColor(Constants.BLUE)
            .setMessageTextColor(Constants.BLACK)
            .setBtnTextColor(Constants.WHITE)
            .setBtnBackgroundColor(Constants.BLUE)
            .build()
        _ctInstance?.promptPushPrimer(jsonObject)

    }

    override fun onInAppButtonClick(payload: java.util.HashMap<String, String>?) {
        val str = payload?.getValue("event")
        Log.d("Lakshya", "Tejas$str")
        _ctInstance?.pushEvent(str)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPushPermissionResponse(accepted: Boolean) {
        Log.d("Clevertap", "onPushPermissionResponse :  InApp---> response() called accepted=$accepted")
        if (accepted) {
            val channel = NotificationChannel(
                "got",
                "Game of Thrones",
                NotificationManager.IMPORTANCE_HIGH,
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }


}
