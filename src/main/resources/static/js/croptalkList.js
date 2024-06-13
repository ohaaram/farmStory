// util.js 함수 호출
window.onload = function (){
    <!-- thymeleaf 변수를 js 파일에서 사용하기 위해 input value 가져오기 -->
    const cateData = document.getElementById("cate");
    const cate = cateData.value;
    // 커뮤니티 공통 ///////////////////////////////////////////////////////////////////
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
    // croptalk 상단 Nav 표시하기
    if(cate === 'story'){
        communityNav.innerHTML = `<h2>농작물이야기</h2>
                                                <h5>HOME > 농작물이야기 > <span>농작물이야기</span></h5>`;
    } else if(cate === 'grow'){
        communityNav.innerHTML = `<h2>귀농학교</h2>
                                                <h5>HOME > 농작물이야기 > <span>귀농학교</span></h5>`;
    } else if(cate === 'chef'){
        communityNav.innerHTML = `<h2>텃밭가꾸기</h2>
                                                <h5>HOME > 농작물이야기 > <span>텃밭가꾸기</span></h5>`;
    }

}