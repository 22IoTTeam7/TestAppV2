# TestAppV3

## 수정해야할 사항
####[완료]와이파이명(ssid) 변경 -> gachon free wifi로   
####[완료]MAC 테스트용 주소 add 삭제


## 주의사항
####기기에서 자주 wifi 건들면 나타나는 문제인 것 같은데 중간에 로그에 [wifi 안됩니다 초기화해주세요] 느낌으로 뜰 수 있습니다. 이럴땐 그냥 와이파이 껏다가 키면 됩니다.(천천히 해야함 뭐든,,)   
####그리고 버튼이 한번 누르면 측정, 측정 끝나고 한번 더 누르면 꺼집니다. 재측정하려면 이 상태에서 한번 더 눌러주세요!(천천히..!)   

## 안내 사항
####HTTP 통신 구현 완료했습니다 자세한 사용법은 callRetrofit.java 파일 상단에 주석을 달아두었습니다.   
####AP 신호세기 매핑할때 같이 구현한 AP 층별 리스트에 따라서 매핑해주시고 마지막 인자는 호실 번호입니다.   
####2층 AP 33개 + 방번호 : 배열 크기 34 // 4층 AP 44개 + 방번호 : 배열 크기 45 // 5층 AP 49개 + 방번호 : 배열 크기 50   

## 추가 구현해야할 사항
####[완료] UI 변경 needed   
####[완료]잡히는 신호에 대한 정보를 로그 창처럼 볼 수 있게 하거나 표 자체가 보이면 좋을 듯 하여 이후 구현하겠습니다.   
#### 번호 추가하기 및 UI 개선?   

