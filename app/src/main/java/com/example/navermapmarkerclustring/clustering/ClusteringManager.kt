package com.example.navermapmarkerclustring.clustering

import com.example.navermapmarkerclustring.base.KOREA_LAT_LNG_BOUNDS
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.NaverMap
import kotlin.math.sqrt

class ClusteringManager(
    private val naverMap: NaverMap,
    private var renderer: ClusterRenderer
) {
    private var quadTree = BoundQuadTree(naverMap.extent ?: KOREA_LAT_LNG_BOUNDS)
    private val dataList = mutableListOf<ClusterData>()
    private var lastClusterZoomLevel = getZoomLevel()

    init {
        addOnCameraChangeListener { _, _ -> }
        clustering()
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
    fun addData(data: ClusterData) {
        quadTree.addData(data)
        dataList.add(data)
    }

    /**
     * 지도의 카메라 이동 Listener을 추가합니다
     * 기존 NaverMap에 Listener을 추가할 경우 클러스터링이 작동 안할 수 있습니다
     */
    fun addOnCameraChangeListener(listener: NaverMap.OnCameraChangeListener) {
        naverMap.addOnCameraChangeListener { i, b ->
            if (lastClusterZoomLevel != getZoomLevel()) clustering()
            listener.onCameraChange(i, b)
        }
    }

    /**
     * 클러스터링을 시행합니다
     */
    fun clustering() {
        lastClusterZoomLevel = getZoomLevel()
        dataList.forEach { it.clusterBase = null }
        val latSize =
            naverMap.contentBounds.run { (northLatitude - southLatitude) * CLUSTER_BOUND_RATIO }
        val lngSize =
            naverMap.contentBounds.run { (eastLongitude - westLongitude) * CLUSTER_BOUND_RATIO }
        val dataMap = HashMap<ClusterData, MutableList<ClusterData>>()
        for (base in dataList) {
            if (base.clusterBase != null) continue
            base.run { clusterBase = this }
            val southWest = base.pos.run {
                LatLng(latitude - latSize / 2, longitude - lngSize / 2)
            }
            val northEast = base.pos.run {
                LatLng(latitude + latSize / 2, longitude + lngSize / 2)
            }

            quadTree.searchBoundData(LatLngBounds(southWest, northEast)).forEach { data ->
                if (data.clusterBase == null) data.clusterBase = base
                else if (getDist(data.pos, data.clusterBase!!.pos) > getDist(data.pos, base.pos)) {
                    data.clusterBase = base
                }
            }
        }
        dataList.forEach {
            (dataMap.getOrPut(it.clusterBase!!) { mutableListOf() }).run { add(it) }
        }
        renderer.rendering(naverMap,dataMap)
    }

    /**
     * renderer를 재정의합니다
     */
    fun setRenderer(renderer: ClusterRenderer) {
        this.renderer = renderer
    }

    private fun getZoomLevel() = (naverMap.cameraPosition.zoom / CLUSTER_ZOOM_LEVEL_RANGE).toInt()
    private fun getDist(s: LatLng, e: LatLng) =
        sqrt((s.latitude - e.latitude) * (s.latitude - e.latitude) + (s.longitude - e.longitude) * (s.longitude - e.longitude))

    companion object {
        const val CLUSTER_BOUND_RATIO = 0.3
        const val CLUSTER_ZOOM_LEVEL_RANGE = 0.5
    }

}