package com.example.navermapmarkerclustring.clustering

import com.example.navermapmarkerclustring.base.KOREA_LAT_LNG_BOUNDS
import com.naver.maps.map.NaverMap

class ClusteringManager<T : ClusterData>(private val naverMap: NaverMap) {
    private var quadTree = BoundQuadTree<T>(naverMap.extent ?: KOREA_LAT_LNG_BOUNDS)
    private val dataList = mutableListOf<T>()

    init {
        addOnCameraChangeListener { _, _ -> }
    }

    /**
     * 데이터를 초기화합니다
     */
    fun clearData() {
        quadTree = BoundQuadTree(naverMap.extent ?: KOREA_LAT_LNG_BOUNDS)
        dataList.clear()
    }

    /**
     * 마커 데이터를 추가합니다
     */
    fun addData(data: T) {
        quadTree.addData(data)
        dataList.add(data)
    }

    /**
     * 지도의 카메라 이동 Listener을 추가합니다
     * 기존 NaverMap에 Listener을 추가할 경우 클러스터링이 작동 안할 수 있습니다
     */
    fun addOnCameraChangeListener(listener: NaverMap.OnCameraChangeListener) {
        naverMap.addOnCameraChangeListener { i, b ->

            listener.onCameraChange(i, b)
        }
    }

    /**
     * 클러스터링을 시행합니다
     */
    fun clustering() {

    }
}