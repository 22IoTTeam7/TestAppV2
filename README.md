# TestAppV2_3

## 수정해야할 사항(test버전이라 테스트 케이스에 맞춰져있음)
와이파이명(ssid) 변경 -> gachon free wifi로
MAC 테스트용 주소 add 삭제

## 주의사항
edit text 칸에 호실 번호만 적어야합니다. exception 처리 따로 안해서 안적거나 이상한거 적으면 튕깁니다 ㅎㅎ,,

## 구현 사항
int array에 rssi 세기 저장
###
마지막 int array (index 번호 44번(45번째))에 호실 번호 저장
###
맥주소는 List 형식으로 넣어두었습니다.(엑셀 파일 기준)

## 추가 구현해야할 사항
### UI 변경 needed 
잡히는 신호에 대한 정보를 로그 창처럼 볼 수 있게 하거나 
표 자체가 보이면 좋을 듯 하여 이후 구현하겠습니다.
###
주의사항 저것도.. 처리해놓을게요..
