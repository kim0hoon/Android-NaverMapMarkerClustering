# Android-NaverMapMarkerClustering
마커 클러스터링 in NaverMap

### 2021/12/08
- 네이버 맵 불러오기 성공
- NaverMapSDK Key 숨기기

### 2021/12/10
- BaseActivity 적용
- ViewBinding 적용
- 패키지 구조 생성 중

### 2021/12/11
- ClusterData 생성
- QuadTree 구현

### 2021/12/13
- method documentation
- ClusterData에 ClusterBase 변수 추가

### 2021/12/14
- ClusterRenderer(클러스터링 된 마커를 어떻게 그릴 것인 지) 구현
- Clustering Test(1000개)

https://user-images.githubusercontent.com/14107670/146009546-045608e8-6900-4141-9472-248c01c77d04.mp4

- <b>개선하고 싶은 점</b>
  - ~~클러스터링 될 때 애니메이션 추가~~ (우선순위 뒤로, 애니메이션을 지원하지 않음)
  - 클러스터링 범위 조절
  - 클러스터링 줌 레벨 범위 조절

### 2021/12/18
- 테스트 데이터 10000개로 진행
- 클러스터링에 사용할 데이터(ClusteringData)와 마커에 사용할 데이터(MarkerData) 분리
- 클러스터링 연산에 코루틴 적용 
- 동기화 적용

- 코루틴 적용 전 (마커 10000개)

https://user-images.githubusercontent.com/14107670/146641383-3407ab98-8b1a-4f98-b881-8aa9ac35ecd8.mp4

- 코루틴 적용 후 (마커 10000개)

https://user-images.githubusercontent.com/14107670/146641388-27c39696-5b43-49ec-b8b6-f483dff1b3a2.mp4

클러스터링 연산보단 렌더링 연산이 더 크다고 생각됨, 높은 줌 레벨에서 버벅거리는 현상 발생
렌더링을 개선해야 할 것으로 보임

- <b>개선하고 싶은 점</b>
  - 렌더링 최적화(카메라 영역에 대해서만 렌더링)

### 2021/12/21
- 카메라 위치에 따라 Bound를 지정
- Bound내부에 있는 Cluster된 마커만 Rendering
- 높은 줌 레벨(확대된 레벨)에서 버벅거리는 현상 개선
- Bound 적용 후 (마커 10000개)

https://user-images.githubusercontent.com/14107670/146937377-72d777bc-756d-4de0-9a7b-9966a59e4aaa.mp4

