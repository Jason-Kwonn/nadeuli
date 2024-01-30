import { useState } from 'react';
import styled from 'styled-components';
import { css } from 'styled-components';
import HeaderBack from '../../components/HeaderBack';
import { checkAuthNum, getAuthNumCellphone, login, updateCellphone, updateMember } from '../../util/memberAxios';
import { Link, useLocation, useNavigate } from 'react-router-dom';

function UpdateCellphone() {
    const [btnState, setBtnState] = useState(false);
    const [isAuthNumDisabled, setIsAuthNumDisabled] = useState(false);
    const [isToDisabled, setIsToDisabled] = useState(false);
    const [isAuthNumBtnDisabled, setIsAuthNumBtnDisabled] = useState(false);
    const [isCheckAuthNumBtnDisabled, setIsCheckAuthNumBtnDisabled] = useState(false);
    const [isCheckAuthNumInputDisabled, setIsCheckAuthNumInputDisabled] = useState(true); // 새로 추가한 상태
    const [to, setTo] = useState('');
    const [authNum, setAuthNum] = useState('');

    const navigate = useNavigate();

    const location = useLocation();
    const data = location.state?.data;
    console.log(data);

    const handleGetAuthNumBtnClick = (e) => {
        console.log(to);
        e.preventDefault();
        if (/[^0-9]/g.test(to) || to.length < 8) {
            alert('번호는 숫자만, 길이는 8자 이상 입력해주세요');
            return;
        }
        getAuthNumCellphone(to)
            .then((response) => {
                alert('인증번호가 발송되었습니다.');
                setIsAuthNumBtnDisabled(true);
                setIsToDisabled(true);
                setIsAuthNumDisabled(false);
                setIsCheckAuthNumInputDisabled(false); // 활성화로 변경
                setIsCheckAuthNumBtnDisabled(true);
            })
            .catch((err) => {
                alert('이미 존재하거나 올바르지 않은 휴대폰 번호입니다.');
            });
    };

    const handleCheckAuthNumBtnClick = (e) => {
        e.preventDefault();
        if (!/^[a-zA-Z0-9!@#$%^*+=-]{5}$/.test(authNum)) {
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

    const handleUpdateCellphone = () => {
        const memberDTO = {
            email: data?.email,
            cellphone: to,
        };

        updateCellphone(memberDTO)
            .then((response) => {
                if (response.data === 1) {
                    alert('휴대폰 번호 변경 완료!');
                    navigate('/login');
                } else {
                    alert('이미 등록된 휴대폰 번호입니다.');
                    setTo('');
                    setAuthNum('');
                    setIsAuthNumBtnDisabled(false);
                    setIsToDisabled(false);
                    setIsAuthNumDisabled(true);
                    setBtnState(false);
                }
            })
            .catch((err) => {
                alert('서버에서 에러가 발생하였습니다. 잠시 후에 시도해주세요');
                navigate('/login');
            });
    };
    const handleFormSubmit = (e) => {
        e.preventDefault();
        // 나머지 제출 로직을 이곳에 추가
    };
    return (
        <Box>
            <HeaderBack />
            <Content>
                <em>휴대폰 번호를 입력해주세요</em>
                <p>휴대폰 번호 변경 후 다시 로그인 하셔야 합니다.</p>
                <Form onSubmit={handleFormSubmit}>
                    <div>
                        <input
                            className="to"
                            type="text"
                            disabled={isToDisabled}
                            placeholder="휴대폰 번호 (- 없이 숫자만 입력)"
                            required
                            value={to}
                            maxLength={11}
                            onChange={(e) => {
                                const updatedTo = e.target.value;
                                setTo(updatedTo);
                            }}
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
                            value={authNum}
                            onChange={(e) => {
                                const updatedAuthNum = e.target.value;
                                setAuthNum(updatedAuthNum);
                            }}
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
                        onClick={handleUpdateCellphone}
                        disabled={!btnState}
                        isActive={btnState}
                    >
                        휴대폰 번호 변경
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
                  background-color: #508bfc;
                  &:hover {
                      background-color: #1e5ed9;
                  }
              `
            : css`
                  background-color: #ddd;
              `}
`;

export default UpdateCellphone;
