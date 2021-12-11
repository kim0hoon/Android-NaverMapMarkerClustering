package com.example.navermapmarkerclustring.clustering

import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds

class BoundQuadTree<T : ClusterData>(val bound: LatLngBounds) {
    companion object {
        val DATA_NUM = 30
        val MAX_HEIGHT = 30
    }

    private val dataList = ArrayList<T>()
    private val childList = ArrayList<BoundQuadTree<T>>()


    fun addData(data: T) {
        if (dataList.size >= DATA_NUM) {
            if (childList.isEmpty()) split()
            childList.forEach {
                if (it.bound.contains(data.pos)) {
                    it.addData(data)
                    return
                }
            }
        } else dataList.add(data)
    }

    fun searchBoundData(searchBound: LatLngBounds): List<T> {
        val ret = mutableListOf<T>()
        if(searchBound.contains(bound)){
            //찾는 영역이 전체일 때
            ret.addAll(dataList)
            childList.forEach{
                ret.addAll(it.searchBoundData(searchBound))
            }
        }else {
            //찾는 영역이 전체가 아닐 때
            ret.addAll(dataList.filter{searchBound.contains(it.pos)})
            childList.forEach{
                if(it.bound.intersects(searchBound)) ret.addAll(it.searchBoundData(searchBound))
            }
        }
        return ret
    }

    private fun split() {
        with(bound) {
            val midLat = (southLatitude + northLatitude) / 2
            val midLng = (westLongitude + eastLongitude) / 2
            childList.add(BoundQuadTree(LatLngBounds(southWest, LatLng(midLat, midLng))))
            childList.add(
                BoundQuadTree(
                    LatLngBounds(
                        LatLng(midLat, westLongitude),
                        LatLng(northLatitude, midLng)
                    )
                )
            )
            childList.add(
                BoundQuadTree(
                    LatLngBounds(
                        LatLng(southLatitude, midLng),
                        LatLng(midLat, eastLongitude)
                    )
                )
            )
            childList.add(BoundQuadTree(LatLngBounds(LatLng(midLat, midLng), northEast)))
        }
    }
}