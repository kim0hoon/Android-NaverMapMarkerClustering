package com.example.navermapmarkerclustring.clustering

import com.example.navermapmarkerclustring.base.KOREA_LAT_LNG_BOUNDS
import com.naver.maps.map.NaverMap

class ClusteringManager<T : ClusterData>(private val naverMap: NaverMap) {
    private var quadTree = BoundQuadTree<T>(naverMap.extent ?: KOREA_LAT_LNG_BOUNDS)

    fun clearData() {
        quadTree = BoundQuadTree(naverMap.extent ?: KOREA_LAT_LNG_BOUNDS)
    }

    fun addData(data: T) {
        quadTree.addData(data)
    }
}