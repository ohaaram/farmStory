# farmStory

![메인](https://github.com/ohaaram/farmStory/assets/22510560/4e49ce9d-f169-4d6e-a486-ba742adb845e)




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
