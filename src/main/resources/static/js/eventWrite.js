// util.js 함수 호출
document.write('<script src="../js/util.js"></script>');
window.onload = function (){
    <!-- thymeleaf 변수를 js 파일에서 사용하기 위해 input value 가져오기 -->
    const cateData = document.getElementById("cate");
    const cate = cateData.value;
    const btnSubmit = document.getElementById('btnSubmit');
    const articleForm = document.getElementById('articleForm');
    // 글쓰기 /////////////////////////////////////////////////////////
    btnSubmit.onclick = function (e){
        e.preventDefault();
        articleForm.submit();
    };
}
// 이미지 파일만 올릴 수 있게 Check
function chkImg(obj) {
    const file_kind = obj.value.lastIndexOf('.');
    const file_name = obj.value.substring(file_kind+1,obj.length);
    const file_type = file_name.toLowerCase();

    const chkImg = ['jpg', 'gif', 'png', 'jpeg', 'bmp'];

    // 이미지 파일이 아닌 경우
    if(chkImg.indexOf(file_type) == -1){
        alert('이미지 파일만 선택할 수 있습니다.');
        const parent_Obj = obj.parentNode
        const node = parent_Obj.replaceChild(obj.cloneNode(true),obj);

        // input 태그 비우기
        const inputFile = document.getElementById("file").select();
        inputFile.value = "";
        return false;
    }
}