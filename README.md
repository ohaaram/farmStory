# farmStory

![메인](https://github.com/ohaaram/farmStory/assets/22510560/4e49ce9d-f169-4d6e-a486-ba742adb845e)

![상품상세보기](https://github.com/ohaaram/farmStory/assets/22510560/46ccc73b-587f-4507-b8ce-2deb4a1ed68c)

![장바구니](https://github.com/ohaaram/farmStory/assets/22510560/0a1ace69-0cec-4fba-bde2-834874ce755d)


![주문목록](https://github.com/ohaaram/farmStory/assets/22510560/2224455e-8c77-48e1-b565-1bcbaac2625d)

![리뷰](https://github.com/ohaaram/farmStory/assets/22510560/1c9064b3-a6f1-4dcb-9fa8-9df3a5d8873b)

![로그인](https://github.com/ohaaram/farmStory/assets/22510560/b90e1d97-c87c-4ad2-815e-1d1bc4473402))

![게시판](https://github.com/ohaaram/farmStory/assets/22510560/d07cb42f-a74b-420f-89b6-3a00c9f9376b)


![farmDRD](https://github.com/ohaaram/farmStory/assets/22510560/20a8f2f4-8b22-47ee-93e4-1a5c39a46032)


## 프로젝트 소개
- Spring 웹 쇼핑몰
- 
## 기간 / 인원
- 2024.04.01. ~2024.04.12 (0개월) /4명

## 개발 환경
- Version : Java 17
- IDE : IntelliJ
- Framework : SpringBoot 3.2.3
- ORM : JPA, MyBaties, QueryDsl
  
## 기술 스택
- Server : AWS EC2
- DataBase : MySQL, HeidiSQL
- frontend : javaScript, HTML/css
- backend : java, Spring Boot, JPA, MyBAties, spring security
- etc : OAuth2
  
## 주요 기능
- [회원 관리]
	- 회원가입 및 로그인 기능
	- 회원은 등급별로 구성된다.
	- 관리자는 모든 데이터에 대한 CRUD가 가능하다.
- [관리자 관리]
	- 관리자는 상품 관리 페이지에 (farmstory/admin) 접근 가능하다.
	- 관리자는 회원의 등급을 조정할 수 있다.
- [주문 관리]
	- 관리자에 의한 생성/ 수정/ 삭제가 가능하다.
	- 하나의 주문은 하나의 주문 상세와 연결되어있다.
	- 주문 시 포인트가 적립된다.
- [포인트 관리]
	- 관리자에 의한 생성/ 수정/ 삭제가 가능하다.
	- 포인트 사용 시 결제 금액이 차감된다.
	- 사용자 등급에 따라 적립율이 다르다.
