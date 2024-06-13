// util.js 함수 호출
window.onload = function (){
    // 커뮤니티 공통 ///////////////////////////////////////////////////////////////////
    <!-- thymeleaf 변수를 js 파일에서 사용하기 위해 input value 가져오기 -->
    const cateData = document.getElementById("cate");
    const cate = cateData.value;
    const sideBoxLi = document.querySelectorAll(".lnb > li > a");
    const communityNav = document.getElementById('communityNav');

    // aside 현재 카테고리 표시하기 - 반복처리
    sideBoxLi.forEach(function(item) {

        const dataCate = item.getAttribute("data-cate");

        // 현재 cate와 li의 data-cate 값을 비교하여 일치하는 경우
        if (dataCate === cate) {
            // 해당 요소에 클래스 추가
            item.classList.add("tabOn");
        }
    });
    // community 상단 Nav 표시하기
    if(cate === 'notice'){
        communityNav.innerHTML = `<h2>공지사항</h2>
                                    <h5>HOME > 커뮤니티 > <span>공지사항</span></h5>`;
    } else if(cate === 'menu'){
        communityNav.innerHTML = `<h2>오늘의식단</h2>
                                                <h5>HOME > 커뮤니티 > <span>오늘의식단</span></h5>`;
    } else if(cate === 'chef'){
        communityNav.innerHTML = `<h2>나도요리사</h2>
                                                <h5>HOME > 커뮤니티 > <span>나도요리사</span></h5>`;
    } else if(cate === 'qna'){
        communityNav.innerHTML = `<h2>1:1고객문의</h2>
                                                <h5>HOME > 커뮤니티 > <span>1:1고객문의</span></h5>`;
    } else if(cate === 'faq'){
        communityNav.innerHTML = `<h2>자주묻는질문</h2>
                                                <h5>HOME > 커뮤니티 > <span>자주묻는질문</span></h5>`;
    }

}