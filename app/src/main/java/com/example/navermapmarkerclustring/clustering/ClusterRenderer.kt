package com.example.navermapmarkerclustring.clustering

import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Align
import com.naver.maps.map.overlay.Marker

open class ClusterRenderer {
    private val markerList = mutableListOf<Marker>()

    /**
     * cluster가 지정된 map을 이용하여 NaverMap에 렌더링
     */
    fun rendering(naverMap: NaverMap, dataMap: HashMap<ClusterData, MutableList<ClusterData>>) {
        clearMarker()
        dataMap.forEach { clusterData ->
            if (clusterData.value.size >= MIN_CLUSTER_NUM) clusterRendering(
                clusterData.key,
                clusterData.value
            ).map = naverMap
            else {
                clusterData.value.forEach { data ->
                    baseRendering(data).map = naverMap
                }
            }
        }
    }

    /**
     * 클러스터링 된 마커에 대한 정의
     */
    protected open fun clusterRendering(cluster: ClusterData, dataList: List<ClusterData>) =
        Marker().apply {
            position = cluster.pos
            width = 50 + dataList.size / 2
            height = 80 + dataList.size / 2
            captionRequestedWidth = 200
            setCaptionAligns(Align.Top)
            captionText = dataList.sumOf { it.title.toInt() }.toString()
        }


    /**
     * 기본 마커에 대한 정의
     */
    protected open fun baseRendering(data: ClusterData) =
        Marker().apply {
            position = data.pos
            width = 50
            height = 80
            captionRequestedWidth = 200
            setCaptionAligns(Align.Top)
            captionText = data.title
        }

    /**
     * 지도상의 마커들을 초기화합니다
     */
    private fun clearMarker() {
        markerList.forEach {
            it.map = null
        }
        markerList.clear()
    }

    companion object {
        const val MIN_CLUSTER_NUM = 3
    }
}