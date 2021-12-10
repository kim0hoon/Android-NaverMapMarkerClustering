package com.example.navermapmarkerclustring

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.navermapmarkerclustring.base.BaseActivity
import com.example.navermapmarkerclustring.base.makeKoreaMap
import com.example.navermapmarkerclustring.clustering.ClusteringManager
import com.example.navermapmarkerclustring.databinding.ActivityMainBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate),
    OnMapReadyCallback {

    private lateinit var clusteringManager: ClusteringManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initMap()
    }

    private fun initMap() {
        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NaverCloudPlatformClient(BuildConfig.NAVER_MAP_CLIENT_ID)

        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map_fragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map_fragment, it).commit()
            }

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(naverMap: NaverMap) {
        makeKoreaMap(naverMap)
        clusteringManager = ClusteringManager(naverMap)
    }
}
