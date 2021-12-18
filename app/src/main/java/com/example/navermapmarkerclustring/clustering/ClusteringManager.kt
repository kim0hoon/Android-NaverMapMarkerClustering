package com.example.navermapmarkerclustring.clustering

import com.example.navermapmarkerclustring.base.KOREA_LAT_LNG_BOUNDS
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.NaverMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    fun addData(data: MarkerData) {
        val clusterData = ClusterData(data, data.pos)
        quadTree.addData(clusterData)
        dataList.add(clusterData)
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
        CoroutineScope(IO).launch {
            lastClusterZoomLevel = getZoomLevel()
            val nowLevel = lastClusterZoomLevel

            val latSize =
                naverMap.contentBounds.run { (northLatitude - southLatitude) * CLUSTER_BOUND_RATIO }
            val lngSize =
                naverMap.contentBounds.run { (eastLongitude - westLongitude) * CLUSTER_BOUND_RATIO }
            val dataMap = HashMap<LatLng, MutableList<ClusterData>>()
            for (base in dataList) {
                if (base.lastLevel == nowLevel) continue

                synchronized(base) {
                    if (nowLevel != lastClusterZoomLevel) return@synchronized
                    base.run {
                        basePos = this.markerData.pos
                        lastLevel = nowLevel
                    }

                }
                val southWest = base.markerData.pos.run {
                    LatLng(latitude - latSize / 2, longitude - lngSize / 2)
                }
                val northEast = base.markerData.pos.run {
                    LatLng(latitude + latSize / 2, longitude + lngSize / 2)
                }

                quadTree.searchBoundData(LatLngBounds(southWest, northEast)).forEach { data ->
                    synchronized(data) {
                        if (nowLevel != lastClusterZoomLevel) return@synchronized
                        if (data.lastLevel != nowLevel) {
                            data.basePos = base.markerData.pos
                            data.lastLevel = nowLevel
                        } else if (getDist(data.markerData.pos, data.basePos) > getDist(
                                data.markerData.pos,
                                base.markerData.pos
                            )
                        ) {
                            data.basePos = base.markerData.pos
                        }
                    }
                }
            }

            dataList.forEach {
                (dataMap.getOrPut(it.basePos) { mutableListOf() }).run { add(it) }
            }
            if (nowLevel == lastClusterZoomLevel) {
                withContext(Main) {
                    renderer.rendering(naverMap, dataMap)
                }
            }
        }
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