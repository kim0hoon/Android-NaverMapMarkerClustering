package com.example.navermapmarkerclustring

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.navermapmarkerclustring.base.BaseActivity
import com.example.navermapmarkerclustring.databinding.ActivityMainBinding
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapSdk
import com.naver.maps.map.OnMapReadyCallback

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate),OnMapReadyCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initMap()
    }

    private fun initMap(){
        NaverMapSdk.getInstance(this).client=
            NaverMapSdk.NaverCloudPlatformClient(BuildConfig.NAVER_MAP_CLIENT_ID)

    }

    override fun onMapReady(p0: NaverMap) {
        //TODO("Not yet implemented")
    }
}