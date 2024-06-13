// 레이아웃 상단 버튼 로그인 유무 확인 함수 //
function notLogin() {
    const confirmed = confirm("로그인 후 사용 가능합니다. 로그인 페이지로 이동하시겠습니까?");
    if (confirmed) {
        window.location.href = "/farmstory/user/login";
    }
}
// 최상단 이동 (topButton) 함수 //
function movePageTop() {
    window.scrollTo(0, 0);
}