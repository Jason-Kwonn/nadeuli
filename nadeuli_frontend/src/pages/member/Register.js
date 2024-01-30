import HeaderBack from '../../components/HeaderBack';
import styled from 'styled-components';
import { useNavigate } from 'react-router-dom';
import { useEffect, useState } from 'react';
import { addMember, checkAuthNum, getAuthNumCellphone} from '../../util/memberAxios';
import { css } from 'styled-components';
import { getCurrentPosition } from '../../util/Location';
import { saveToken } from '../../shared/localStorage';
import { useDispatch } from 'react-redux';
import { getMember, setMember } from '../../redux/modules/member';

function Register() {
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const [btnState, setBtnState] = useState(false);
    const [isAuthNumDisabled, setIsAuthNumDisabled] = useState(false);
    const [isToDisabled, setIsToDisabled] = useState(false);
    const [isAuthNumBtnDisabled, setIsAuthNumBtnDisabled] = useState(false);
    const [isCheckAuthNumBtnDisabled, setIsCheckAuthNumBtnDisabled] = useState(false);
    const [isCheckAuthNumInputDisabled, setIsCheckAuthNumInputDisabled] = useState(true); // 새로 추가한 상태

    const [to, setTo] = useState('');
    const [authNum, setAuthNum] = useState('');
    const [nickname, setNickname] = useState('');
    const [latitude, setLatitude] = useState(null);
    const [longitude, setLongitude] = useState(null);

    const fetchLocation = async () => {
        try {
            const { latitude, longitude } = await getCurrentPosition();
            console.log('현재 위치:', latitude, longitude);
            setLatitude(latitude);
            setLongitude(longitude);
        } catch (error) {
            if (error.code === 1) {
                console.error('위치정보 수집을 허용해주세요.');
                navigate('/');
            } else {
                console.error('위치 정보를 가져오는 중 오류가 발생했습니다:', error);
            }
        }
    };

    useEffect(() => {
        fetchLocation();
    }, []); // 빈 배열을 전달하여 최초 렌더링 시에만 실행되도록 설정

    const handleGetAuthNumBtnClick = (e) => {
        e.preventDefault(); // 폼의 기본 동작을 막음
        if (/[^0-9]/g.test(to) || to.length < 8) {
            // 안에 숫자가 아닌 값이 있을 경우
            alert('번호는 숫자만, 길이는 8자 이상 입력해주세요');
            return;
        }

        // 휴대폰 번호가 유효하다면, 인증번호를 받기 위한 요청을 보냄
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
        e.preventDefault(); // 폼의 기본 동작을 막음
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
    const handleAddMemberBtnClick = (e) => {
        e.preventDefault();
        if (/[^a-zA-Z0-9ㄱ-ㅎㅏ-ㅣ가-힣]/g.test(nickname) || nickname.length < 6 || nickname.length > 12) {
            // 영문자 영어대문자 한글 숫자가 아닐 경우, 문자가 6~12글자인 경우
            alert('닉네임은 숫자, 영문 대/소문자, 한글을 사용하여 6~12자를 입력해야 합니다. 특수문자는 사용할 수 없습니다');
            return;
        }

        const memberDTO = {
            cellphone: to,
            nickname,
        };

        const gpsDTO = {
            y: latitude,
            x: longitude,
        };

        addMember(memberDTO, gpsDTO)
            .then((response) => {
                if (response.data) {
                    console.log(response.data.tag);
                    // 헤더에서 Authorization 헤더를 찾아 토큰을 추출
                    const receivedToken = response.headers.get('Authorization');
                    console.log('receivedToken:', receivedToken);
                    // 추출한 토큰을 저장 등의 작업 수행
                    if (receivedToken) {
                        const token = receivedToken.replace('Bearer ', ''); // 'Bearer ' 부분을 제거
                        saveToken(token);
                    }
                    alert('회원가입이 완료되었습니다.');
                    dispatch(setMember(response.data));
                    navigate('/main');
                }
            })
            .catch((err) => {
                alert("이미 가입된 휴대폰 번호이거나 위치설정에 문제가 발생했습니다.");
                setTo('')
                setAuthNum('')
                setIsAuthNumBtnDisabled(false);
                setIsToDisabled(false);
                setIsAuthNumDisabled(true);
                setBtnState(false);
            });
    };
    

    const onChange = (e) => {
        // 버튼 활성화
        const updatedTo = e.target.name === 'to' ? e.target.value : to;
        const updatedAuthNum = e.target.name === 'authNum' ? e.target.value : authNum;
        const updatedNickname = e.target.name === 'nickname' ? e.target.value : nickname;

        setTo(updatedTo);
        setAuthNum(updatedAuthNum);
        setNickname(updatedNickname);
    };

    return (
        <Box>
            <HeaderBack />
            <Content>
                <em>
                    안녕하세요!
                    <br />
                    휴대폰 번호로 가입 해주세요.
                </em>
                <p>휴대폰 번호는 안전하게 보관되며 이웃들에게 공개되지 않아요</p>
                <Form>
                    <input
                        type="text"
                        placeholder="영문 대/소문자, 한글을 사용하여 6~12자 닉네임"
                        required
                        maxLength={12}
                        value={nickname}
                        onChange={onChange}
                        name="nickname"
                    />
                    <div>
                        <input
                            className="to"
                            type="text"
                            placeholder="휴대폰 번호 (- 없이 숫자만 입력)"
                            required
                            maxLength={11}
                            disabled={isToDisabled}
                            onChange={onChange}
                            name="to"
                            value={to} // 값 추가
                        />
                        <Button
                            className="authNumberBtn"
                            onClick={handleGetAuthNumBtnClick}
                            disabled={isAuthNumBtnDisabled}
                            isActive={!isAuthNumBtnDisabled}
                            style={{fontSize:'16px'}}
                        >
                            인증번호 받기
                        </Button>
                    </div>
                    <div>
                        <input
                            className="authNum"
                            type="text"
                            placeholder="인증번호"
                            maxLength={5}
                            required
                            onChange={onChange}
                            name="authNum"
                            disabled={isCheckAuthNumInputDisabled || isAuthNumDisabled}
                            value={authNum} // 값 추가
                        />
                        <Button
                            className="authNumberBtn"
                            onClick={handleCheckAuthNumBtnClick}
                            disabled={!isCheckAuthNumBtnDisabled}
                            isActive={isCheckAuthNumBtnDisabled}
                            style={{fontSize:'16px'}}
                        >
                            인증번호 확인
                        </Button>
                    </div>
                    <Button
                        onClick={handleAddMemberBtnClick}
                        disabled={!btnState}
                        isActive={btnState}
                    >
                        회원가입
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
        width: 100%;
        border: 1px solid #bbb;
        height: 50px;
        border-radius: 5px;
        padding: 0 10px;

        &::placeholder {
            color: #ccc;
        }
    }

    .authNum,
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
`;

const Button = styled.button`
    width: 80%;
    margin: 20px auto 0;
    margin-top: 20px;
    height: 80px;
    border-radius: 5px;
    border: none;
    font-size: 20px;
    color: #fff;
    transition: background 0.3s;
    cursor: pointer;

    &.authNumberBtn {
        width: 40%;
    }
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

export default Register;
