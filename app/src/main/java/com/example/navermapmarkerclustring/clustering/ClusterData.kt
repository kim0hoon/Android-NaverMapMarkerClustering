package com.example.navermapmarkerclustring.clustering

import com.naver.maps.geometry.LatLng

//TODO 마커에 사용할 데이터 추가
open class MarkerData(val pos: LatLng, val title: String)

data class ClusterData(val markerData: MarkerData, var basePos: LatLng? = null)