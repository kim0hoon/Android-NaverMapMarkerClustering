package com.example.navermapmarkerclustring

import android.os.Bundle
import com.example.navermapmarkerclustring.base.BaseActivity
import com.example.navermapmarkerclustring.base.initTestData
import com.example.navermapmarkerclustring.base.makeKoreaMap
import com.example.navermapmarkerclustring.clustering.ClusterData
import com.example.navermapmarkerclustring.clustering.ClusterRenderer
import com.example.navermapmarkerclustring.clustering.ClusteringManager
import com.example.navermapmarkerclustring.databinding.ActivityMainBinding
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapSdk
import com.naver.maps.map.OnMapReadyCallback

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
        clusteringManager = ClusteringManager(naverMap, ClusterRenderer())
        initTestData().forEach{
            clusteringManager.addData(it)
        }
        clusteringManager.clustering()
    }
}
