package com.example.navermapmarkerclustring.base

import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.NaverMap

const val KOREA_ZOOM_LEVEL = 5.8
const val KOREA_MIN_LAT = 33.0
const val KOREA_MAX_LAT = 43.0
const val KOREA_MIN_LNG = 124.0
const val KOREA_MAX_LNG = 132.0
val KOREA_LAT_LNG_BOUNDS =
    LatLngBounds(LatLng(KOREA_MIN_LAT, KOREA_MIN_LNG), LatLng(KOREA_MAX_LAT, KOREA_MAX_LNG))

fun makeKoreaMap(naverMap: NaverMap) {
    naverMap.minZoom = KOREA_ZOOM_LEVEL
    naverMap.extent = KOREA_LAT_LNG_BOUNDS
}