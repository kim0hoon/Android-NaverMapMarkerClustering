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
    private var clusterMap = HashMap<LatLng, MutableList<ClusterData>>()
    private var displayBound: LatLngBounds = naverMap.contentBounds

    init {
        naverMap.addOnCameraChangeListener { i, b ->
            resizeBound(naverMap.contentBounds)
            if (lastClusterZoomLevel != getZoomLevel()) clustering()
        }
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

            synchronized(clusterMap) {
                if (nowLevel != lastClusterZoomLevel) return@synchronized
                clusterMap = dataMap

            }
            if (nowLevel == lastClusterZoomLevel) {
                withContext(Main) {
                    rendering()
                }
            }
        }
    }

    /**
     * 현재 상태에 따른 rendering을 실행합니다
     */
    private fun rendering() {
        renderer.rendering(naverMap, clusterMap, displayBound)
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

    /**
     * 현재 카메라 영역에 따라 바운드를 재조정
     */
    @Synchronized
    private fun resizeBound(bound: LatLngBounds) {
        if (bound.contains(bound) && !isUnderBound(bound)) return
        val latSize=bound.run{(northLatitude - southLatitude)}
        val lngSize=bound.run{(eastLongitude - westLongitude)}
        val midLat=bound.run{(northLatitude + southLatitude)/2}
        val midLng=bound.run{(eastLongitude + westLongitude)/2}
        val southWest=LatLng(midLat-latSize* BOUND_RATIO_MAX/2,midLng-lngSize* BOUND_RATIO_MAX/2)
        val northEast=LatLng(midLat+latSize* BOUND_RATIO_MAX/2,midLng+lngSize* BOUND_RATIO_MAX/2)
        displayBound=LatLngBounds(southWest,northEast)
        rendering()
    }

    private fun isUnderBound(bound: LatLngBounds): Boolean {
        val latSize = displayBound.run { (northLatitude - southLatitude) / BOUND_RATIO_MIN }
        val lngSize = displayBound.run { (eastLongitude - westLongitude) / BOUND_RATIO_MIN }
        return bound.run { (northLatitude - southLatitude) > latSize && (eastLongitude - westLongitude) > lngSize }
    }

    companion object {
        const val CLUSTER_BOUND_RATIO = 0.3
        const val CLUSTER_ZOOM_LEVEL_RANGE = 0.5
        const val BOUND_RATIO_MAX = 3.0
        const val BOUND_RATIO_MIN = 3.0
    }

}