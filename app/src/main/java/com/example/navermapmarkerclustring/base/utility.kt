package com.example.navermapmarkerclustring.base

import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.NaverMap

fun makeKoreaMap(naverMap: NaverMap){
    naverMap.minZoom=5.8
    naverMap.extent= LatLngBounds(LatLng(33.0,124.0),LatLng(43.0,132.0))
}