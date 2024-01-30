import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import styled from 'styled-components';
import { css } from 'styled-components';
import HeaderBack from '../../components/HeaderBack';
import { checkAuthNum, findAccount, getAuthNumEmail } from '../../util/memberAxios';

function FindAccount() {
    const [btnState, setBtnState] = useState(false);
    const [isAuthNumDisabled, setIsAuthNumDisabled] = useState(false);
    const [isToDisabled, setIsToDisabled] = useState(false);
    const [isAuthNumBtnDisabled, setIsAuthNumBtnDisabled] = useState(false);
    const [isCheckAuthNumBtnDisabled, setIsCheckAuthNumBtnDisabled] = useState(false);
    const [isCheckAuthNumInputDisabled, setIsCheckAuthNumInputDisabled] = useState(true); // 새로 추가한 상태

    //다음 컴포넌트로 이메일값을 넘기기위해 사용
    const navigate = useNavigate();

    const [to, setTo] = useState(''); // 이메일 상태
    const [authNum, setAuthNum] = useState(''); // 인증번호 상태
    const handleGetAuthNumBtnClick = (e) => {
        // e.preventDefault();

        if (to.length < 5) {
            alert('값을 입력해주세요');
            return;
        }
        if (!/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(to)) {
            alert('올바른 이메일 형식이 아닙니다.');
            return;
        }



        const memberDTO = {
            email: to,
        };
        console.log(memberDTO);

        // findAccount 호출 후에 getAuthNumEmail을 실행하거나 알림을 표시
        findAccount({ memberDTO })
            .then((response) => {
                const isAccountExist = response.data;
                if (isAccountExist) {
                    getAuthNumEmail(to)
                        .then(() => {
                            alert('인증번호가 발송되었습니다.');
                            setIsAuthNumBtnDisabled(true);
                            setIsToDisabled(true);
                            setIsAuthNumDisabled(false);
                            setIsCheckAuthNumInputDisabled(false); // 활성화로 변경
                            setIsCheckAuthNumBtnDisabled(true);
                        })
                        .catch((err) => {
                            alert('인증번호 발송 중 오류가 발생했습니다.');
                        });
                } else {
                    alert('존재하지 않거나 올바르지 않은 이메일입니다.');
                }
            })
            .catch((err) => {
                alert('서버에서 오류가 발생했습니다.');
            });
    };
    const handleCheckAuthNumBtnClick = (e) => {
        // e.preventDefault(); // 폼의 기본 동작을 막음
        if (!/^[a-zA-Z0-9!@#$%^*+=-]{5}$/.test(authNum)) {
            // 안에 소문자,대문자,숫자, 특수문자 '!' ~ '+' (괄호 제외)를 제외한 값이 있을 경우
            alert('인증번호는 숫자 5자리로 입력해야 합니다.');
            return;
        }

        const data = {
            authNumber: authNum,
            to: to,
        };
        checkAuthNum(data)
            .then((response) => {
                alert('인증번호가 일치합니다.');
                setBtnState(true);
                setIsCheckAuthNumBtnDisabled(false);
                setIsCheckAuthNumInputDisabled(true);
            })
            .catch((err) => {
                alert('인증번호가 일치하지 않습니다.');
            });
    };

    const onChange = (e) => {
        // 버튼 활성화
        const updatedTo = e.target.name === 'to' ? e.target.value : to;
        const updatedAuthNum = e.target.name === 'authNum' ? e.target.value : authNum;

        setTo(updatedTo);
        setAuthNum(updatedAuthNum);
    };

    const handleNavigation = (to) => {
        console.log(to);
        navigate('/updateCellphone', { state: { data: { email: to } } });
    };
    const handleFormSubmit = (e) => {
        e.preventDefault();
        // 나머지 제출 로직을 이곳에 추가
      };
    return (
        <Box>
            <HeaderBack />
            <Content>
                <em>이메일을 입력 해주세요.</em>
                <p>등록된 이메일이 아닐경우 계정을 찾을 수 없습니다.</p>
                <p>이메일 인증 완료 후 휴대폰 번호를 변경해서 로그인을 진행하셔야합니다.</p>
                <Form onSubmit={handleFormSubmit}>
                    <div>
                        <input
                            className="to"
                            type="text"
                            placeholder="이메일"
                            required
                            disabled={isToDisabled}
                            onChange={onChange}
                            name="to"
                        />
                        <Button
                            className="authNumberBtn"
                            onClick={handleGetAuthNumBtnClick}
                            disabled={isAuthNumBtnDisabled}
                            isActive={!isAuthNumBtnDisabled}
                        >
                            인증번호 받기
                        </Button>
                    </div>
                    <div>
                        <input
                            className="authNumber"
                            type="text"
                            placeholder="인증번호"
                            maxLength={5}
                            required
                            onChange={onChange}
                            name="authNum"
                            disabled={isCheckAuthNumInputDisabled || isAuthNumDisabled}
                        />
                        <Button
                            className="authNumberBtn"
                            onClick={handleCheckAuthNumBtnClick}
                            disabled={!isCheckAuthNumBtnDisabled}
                            isActive={isCheckAuthNumBtnDisabled}
                        >
                            인증번호 확인
                        </Button>
                    </div>
                    <Button
                        disabled={!btnState}
                        isActive={btnState}
                        onClick={() => handleNavigation(to)}
                    >
                        이메일로 계정 찾기
                    </Button>
                </Form>
            </Content>
        </Box>
    );
}

const Box = styled.div``;

const Content = styled.div`
    padding: 40px 20px 0;

    em {
        font-size: 20px;
        font-weight: bold;
        line-height: 1.4;
    }

    p {
        font-size: 12px;
        margin-top: 20px;
    }
`;

const Form = styled.form`
    display: flex;
    flex-direction: column;
    margin-top: 30px;

    input {
        border: 1px solid #bbb;
        height: 50px;
        border-radius: 5px;
        padding: 0 10px;

        &::placeholder {
            color: #ccc;
        }
    }

    .authNumber,
    .to {
        width: 50%;
    }
    .authNumberBtn {
        width: 40%;
        margin-left: 10%; /* 오른쪽에 여백 추가 */
        border: 1px solid #bbb;
        height: 50px;
        border-radius: 5px;
        padding: 0 10px;
    }

    input + input {
        margin-top: 10px;
    }

    p {
        margin-top: 30px;
        text-align: center;
        font-size: 18px;
    }
`;

const Button = styled.button`
    margin-top: 20px;
    height: 80px;
    border-radius: 5px;
    border: none;
    background-color: #ddd;
    font-size: 20px;
    color: #fff;
    transition: background 0.3s;
    cursor: pointer;
    ${(props) =>
        props.isActive
            ? css`
                  background-color: #508BFC;
                  &:hover {
                      background-color: #1e5ed9;
                  }
              `
            : css`
                  background-color: #ddd;
              `}
`;

export default FindAccount;
