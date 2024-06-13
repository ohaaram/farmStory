
//유효성 검사에 사용할 상태변수
let isHpOk = false;


// 유효성 검사에 사용할 정규표현식

const reHp = /^01(?:0|1|[6-9])-(?:\d{4})-\d{4}$/;

window.onload = function () {

    // 휴대폰 유효성 검사
    const resultHp = document.getElementById('result_hp');

    document.registerForm.hp.addEventListener('focusout', () => {
        const type = document.registerForm.hp.dataset.type;
        const input = document.registerForm[type];


        console.log('value : ' + input.value);

        // 정규식 검사
        if (!input.value.match(reHp)) {
            input.classList.add('is-invalid');
            resultHp.classList.add('invalid-feedback');
            resultHp.innerText = '휴대폰 형식이 맞지 않습니다.';
            isHpOk = false;
            return;
        }

        async function fetchGet(url) {

            console.log("fetchData1...1");

            try {
                console.log("fetchData1...2");
                const response = await fetch(url);
                console.log("here1");

                if (!response.ok) {
                    console.log("here2");
                    throw new Error('response not ok');
                }

                const data = await response.json();
                console.log("data1 : " + data);
                return data;
            } catch (err) {
                console.log(err)
            }
        }

        setTimeout(async () => {
            const data = await fetchGet(`/farmstory/user/${type}/${input.value}`);

            if (data.result > 0) {
                input.classList.add('is-invalid');

                resultHp.classList.add('invalid-feedback');
                resultHp.innerText = '이미 사용중인 휴대폰 입니다.';
                isHpOk = false;
            } else {
                input.classList.add('is-valid');

                resultHp.classList.add('valid-feedback');
                resultHp.innerText = '사용 가능한 휴대폰 입니다.';
                isHpOk = true;
            }
        }, 1000);
    });

//우편번호 검색
    function postcode() {
        new daum.Postcode({
            oncomplete: function (data) {
                // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.

                // 각 주소의 노출 규칙에 따라 주소를 조합한다.
                // 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
                var addr = ''; // 주소 변수
                var extraAddr = ''; // 참고항목 변수

                //사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
                if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
                    addr = data.roadAddress;
                } else { // 사용자가 지번 주소를 선택했을 경우(J)
                    addr = data.jibunAddress;
                }

                // 우편번호와 주소 정보를 해당 필드에 넣는다.
                document.getElementById('inputZip').value = data.zonecode;
                document.getElementById("inputAddr1").value = addr;
                // 커서를 상세주소 필드로 이동한다.
                document.getElementById("inputAddr2").focus();
            }
        }).open();
    }

    findZip.onclick = function () {
        postcode();
    }

    // 최종 유효성 검사 확인
    document.registerForm.onsubmit = function () {

        if (!isHpOk) {
            alert('휴대폰이 유효하지 않습니다.');
            return false;
        }

        if (document.getElementById('inputZip').value === '') {
            alert('주소를 입력해주세요');
            return false;
        }
        if(document.getElementById('inputAddr2').value==''){
            alert('상세주소를 입력해주세요');
        }
        // 폼 전송
        return true;
    }

}
