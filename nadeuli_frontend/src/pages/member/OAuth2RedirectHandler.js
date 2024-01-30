import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { getSocialMember, handleMemberActivate } from '../../util/memberAxios';
import { getCurrentPosition } from '../../util/Location';
import { useDispatch } from 'react-redux';
import { setMember } from '../../redux/modules/member';
import { saveToken } from '../../shared/localStorage';

const OAuth2RedirectHandler = () => {
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const location = useLocation();
    // const [accessToken, setAccessToken] = useState('');
    const queryParams = new URLSearchParams(location.search);
    const accessToken = queryParams.get('accessToken');
    saveToken(accessToken);
    // 쿠키에서 이름이 'Authorization'인 쿠키의 값을 가져오는 함수
    async function getCookieValue(cookieName) {
        const cookies = document.cookie.split(';');
        console.log(cookies)
        for (const cookie of cookies) {
            const [name, value] = cookie.trim().split('=');
            if (name === cookieName) {
                return decodeURIComponent(value);
            }
        }
        return null;
    }

    useEffect(() => {
        const fetchCookieValue = async () => {
            const value = await getCookieValue('Authorization'); // 비동기 처리를 위해 await 사용
            // setAccessToken(value);
            console.log(value);
        };

        fetchCookieValue();
    }, []);

    //정지기간 파싱
    const formatDate = (dateString) => {
        const date = new Date(dateString);
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');

        return `${year}년 ${month}월 ${day}일 까지`;
    };

    useEffect(() => {
        const fetchData = async () => {
            try {
                const tokenDTO = {
                    accessToken: accessToken,
                };

                const response = await getSocialMember(tokenDTO);
                console.log(response.data);
                const shouldDeactivate = response.data.activate;
                if (response.data.blockEnd) {
                    // 정지된 계정인 경우 메시지 표시
                    alert(`정지된 계정입니다. 정지기간은 ${formatDate(response.data.blockEnd)} 입니다.`);
                    return;
                }
                if (shouldDeactivate) {
                    const confirmResult = window.confirm('계정이 비활성화 상태입니다. 활성화하시겠습니까?');
    
                    if (confirmResult) {
                        // 비활성화가 해제되었다는 메시지를 띄우고 API 호출
                        await handleMemberActivate(response.data.tag);
                        alert('비활성화가 해제되었습니다.');
                    } else {
                        // 사용자가 취소한 경우 로그인 불가능
                        alert('비활성 회원은 로그인할 수 없습니다.');
                        navigate('/');
                        return;
                    }
                }
                if (!response.data.nickname) {
                    // nickname이 없는 경우 oauth2Register로 라우팅
                    navigate('/oauth2Register');
                } else {
                    // nickname이 있는 경우 main으로 라우팅
                    dispatch(setMember(response.data));
                    navigate('/main');
                }
            } catch (err) {
                console.error('소셜로그인 에러', err);
                window.alert('로그인에 실패하였습니다.');
                navigate('/');
            }
        };

        if (accessToken) {
            fetchData();
        }
    }, [accessToken, dispatch, navigate]);

    return <>소셜로그인 진행중입니다.</>;
};

export default OAuth2RedirectHandler;
