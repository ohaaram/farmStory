// util.js 함수 호출
document.write('<script src="../js/util.js"></script>');
window.onload = function (){
    <!-- thymeleaf 변수를 js 파일에서 사용하기 위해 input value 가져오기 -->
    const cateData = document.getElementById("cate");
    const cate = cateData.value;
    const anoData = document.getElementById("ano");
    const ano = anoData.value;

    const commentBox = document.getElementById('commentBox');
    const commentForm = document.getElementById('commentForm');
    const commentRemove = document.getElementById('commentRemove');
    const commentModify = document.getElementById('commentModify');

    // 커뮤니티 공통 ///////////////////////////////////////////////////////////////////
    const cateLi = document.querySelectorAll(".lnb > li > a");
    const communityNav = document.getElementById('communityNav');

    // aside 현재 카테고리 표시하기 - 반복처리
    cateLi.forEach(function(item) {

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
    // 페이지 로드시 textarea 자동 높이 조절
    const textareas = document.querySelectorAll('textarea');

    // 선택된 각 textarea 요소에 대해 autoResize 함수를 호출합니다.
    textareas.forEach(textarea => {
        autoResize(textarea);
    });
    // 댓글 수정 삭제 /////////////////////////////////////////////////////////////////////////
    document.addEventListener('click', async function (e) {

        // 버튼태그 이고, data-mode 가 있는 경우 => 다른 페이지 이동, 댓글 등록 등은 제외 해야함
        if (e.target.tagName === 'BUTTON' && e.target.dataset.mode != null) {
            e.preventDefault();
            const comment = e.target.closest('.comment');
            const cno = e.target.dataset.cno;
            const textarea = comment.getElementsByTagName('textarea')[0];
            // 댓글 삭제 /////////////////////////////////////////////////////////////////////////
            if (e.target.dataset.mode == 'remove') {
                console.log("cno : " + cno);
                if (confirm('댓글을 삭제하시겠습니까?')) {
                    const data = await fetchDelete(`/farmstory/comment/${cno}`);
                    if (data) {
                        comment.remove();
                    }
                }
                // 댓글 수정 /////////////////////////////////////////////////////////////////////////
            } else if (e.target.dataset.mode == 'modify') {
                // 수정 시작인 경우
                // 댓글 수정 모드
                textarea.readOnly = false;
                textarea.style.outline = "1px solid #111"
                textarea.focus();
                textarea.setSelectionRange(textarea.value.length, textarea.value.length);

                commentModify.textContent = '수정';
                commentModify.dataset.mode = 'update';
                commentRemove.textContent = '취소';
                commentRemove.dataset.mode = 'cancel';

                // 댓글 수정 취소 클릭
            } else if (e.target.dataset.mode == 'cancel') {
                // 댓글 수정 모드 해제
                textarea.readOnly = true;
                textarea.style.outline = "none"
                commentModify.textContent = ' 수정';
                commentModify.dataset.mode = 'modify';
                commentRemove.textContent = ' 삭제';
                commentRemove.dataset.mode = 'remove';

                // 댓글 수정 완료 클릭
            } else if (e.target.dataset.mode == 'update') {
                const jsonData = {
                    "cno": cno,
                    "content": textarea.value
                };

                console.log(jsonData);
                const data = await fetchPut('/farmstory/comment', jsonData);

                // 댓글 수정 모드 해제
                textarea.readOnly = true;
                textarea.style.outline = "none"
                commentModify.textContent = ' 수정';
                commentModify.dataset.mode = 'modify';
                commentRemove.textContent = ' 삭제';
                commentRemove.dataset.mode = 'remove';
            }
        }else if(e.target.tagName === 'BUTTON' && e.target.dataset.form != null){
            // 댓글 작성 /////////////////////////////////////////////////////////////////////////
            if(e.target.dataset.form == 'commentWrite'){
                e.preventDefault();
                const uid = commentForm.uid.value;
                const content = commentForm.content.value;
                const jsonData = {
                    "uid": uid,
                    "ano": ano,
                    "content": content
                };
                console.log(jsonData);
                // 댓글 내용이 있는 경우만 작성 요청
                if(content != null) {
                    const data = await fetchPost('/farmstory/comment', jsonData);
                    console.log(data);
                    const noComment = document.getElementById('noComment');
                    // 만약 댓글이 없는 상태였다면, 'noComment' 태그 삭제
                    if (noComment) {
                        noComment.remove();
                    }
                    const lineCount = data.content.split('\n').length;
                    // 새 댓글 목록에 추가
                    const commentArticle = `<div class="comment">
                                            <p><span>${data.nick}</span>&nbsp;&nbsp;<span>${data.rdate.substring(0, 10)}</span></p>
                                            <textarea name="" id="" readonly>${data.content}</textarea>
                                            <div>
                                            <button id="commentModify">수정</button>
                                            <button id="commentRemove">삭제</button>
                                            </div>
                                            </div>`;
                    // 태그 문자열 삽입
                    commentBox.insertAdjacentHTML('beforeend', commentArticle);
                    // 댓글 작성 폼 비우기
                    commentForm.content.value = "";
                }
                // 댓글 작성 취소 /////////////////////////////////////////////////////////////////////////
            }else if(e.target.dataset.form == 'commentCancel'){
                e.preventDefault();
                if(confirm('댓글 작성을 취소하시겠습니까?')){
                    // 댓글 작성 폼 비우기
                    commentForm.content.value = "";
                }
            }
        }
    });
}
// 텍스트 입력시 textarea 자동 높이 조절 - onload 밖에 둬야함
function autoResize(textarea) {
    // 텍스트 영역의 스크롤 높이 설정을 임시로 해제
    textarea.style.height = 'auto';

    // 텍스트 영역의 스크롤 높이를 내용에 맞게 조절
    textarea.style.height = textarea.scrollHeight + 'px';
}
function confirmDelete() {
    return confirm("게시글을 삭제하시겠습니까?");
}