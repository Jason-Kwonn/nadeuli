import HeaderBack from '../../components/HeaderBack';
import styled from 'styled-components';
import { useNavigate } from 'react-router-dom';
import { useEffect, useState } from 'react';
import { addMember, addNickname, checkAuthNum, getAuthNumCellphone, updateSocial } from '../../util/memberAxios';
import { css } from 'styled-components';
import { getCurrentPosition } from '../../util/Location';
import { getToken, saveToken } from '../../shared/localStorage';
import { useDispatch } from 'react-redux';
import { getMember, setMember } from '../../redux/modules/member';

function Oauth2Register() {
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const [btnState, setBtnState] = useState(false);

    const [accessToken, setAccessToken] = useState(getToken());
    const [nickname, setNickname] = useState('');
    const [latitude, setLatitude] = useState(null);
    const [longitude, setLongitude] = useState(null);

    //현재 위치를 가져오는 함수
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

    // 쿠키에서 이름이 'Authorization'인 쿠키의 값을 가져오는 함수
    function getCookieValue(cookieName) {
        const cookies = document.cookie.split(';');

        for (const cookie of cookies) {
            const [name, value] = cookie.trim().split('=');

            if (name === cookieName) {
                return decodeURIComponent(value);
            }
        }

        return null;
    }
    // useEffect(() => {
    //     const fetchCookieValue = async () => {
    //         const value = await getCookieValue('Authorization');
    //         setAccessToken(value);
    //     };

    //     fetchCookieValue();
    // }, []);

    const handleAddMemberBtnClick = (e) => {
        e.preventDefault();
        if (/[^a-zA-Z0-9ㄱ-ㅎㅏ-ㅣ가-힣]/g.test(nickname) || nickname.length < 6 || nickname.length > 12) {
            // 영문자 영어대문자 한글 숫자가 아닐 경우, 문자가 6~12글자인 경우
            alert('닉네임은 숫자, 영문 대/소문자, 한글을 사용하여 6~12자를 입력해야 합니다. 특수문자는 사용할 수 없습니다');
            return;
        }

        const memberDTO = {
            nickname: nickname,
        };

        const tokenDTO = {
            accessToken: accessToken,
        };

        const gpsDTO = {
            y: latitude,
            x: longitude,
        };

        addNickname(tokenDTO, gpsDTO, memberDTO)
            .then((response) => {
                if (response.data) {
                    alert('닉네임 설정이 완료되었습니다.');
                    dispatch(setMember(response.data));
                    navigate('/main');
                }
            })
            .catch((err) => {
                alert('닉네임 설정에 실패하였습니다 재시도 해주세요');
            });
    };

    const onChange = (e) => {
        // 버튼 활성화
        const updatedNickname = e.target.name === 'nickname' ? e.target.value : nickname;

        setNickname(updatedNickname);
    };

    return (
        <Box>
            <HeaderBack />
            <Content>
                <em>
                    안녕하세요!
                    <br />
                    나드리에서 사용할 닉네임을 적어주세요
                </em>
                <p>닉네임은 중복가능합니다</p>
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
                    {/* <Button isActive={btnState}> */}
                    <Button
                        onClick={handleAddMemberBtnClick}
                        isActive={!btnState}
                    >
                        닉네임 설정
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
                  background-color: #508bfc;
                  &:hover {
                      background-color: #1e5ed9;
                  }
              `
            : css`
                  background-color: #ddd;
              `}
`;

export default Oauth2Register;
