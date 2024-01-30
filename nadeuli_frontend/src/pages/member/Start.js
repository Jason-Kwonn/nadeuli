import styled from 'styled-components';
import logo from '../public/images/nadeuli.png';
import kakao from '../public/images/kakao.png';
import google from '../public/images/google.png';
import naver from '../public/images/naver.png';
import { Link, useNavigate } from 'react-router-dom';
import { useState } from 'react';

function Start() {
    // console.log(process.env.REACT_APP_KAKAO_AUTH_URL)
    const navigate = useNavigate();

    const kakaoLogin = () => {
        window.location.href = "http://110.165.19.112:82/oauth2/authorization/kakao";
        // window.location.href = 'http://localhost:8080/oauth2/authorization/kakao';
    };
    // const googleLogin = () => {
    //   window.location.href = "http://110.165.19.112:82/oauth2/authorization/google";
    // }
    const naverLogin = () => {
        window.location.href = "http://110.165.19.112:82/oauth2/authorization/naver";
        // window.location.href = 'http://localhost:8080/oauth2/authorization/naver';
    };
    return (
            <section
                className="vh-100"
                style={{ backgroundColor: '#508bfc' }}
            >
                <div className="container py-1 h-100">
                    <div className="row d-flex justify-content-center align-items-center h-100">
                        <div className="col-12 col-md-8 col-lg-6 col-xl-5">
                            <div
                                className="card shadow-2-strong"
                                style={{ borderRadius: '1rem' }}
                            >
                                <div className="card-body p-5 text-center">
                                    <h3>
                                        {' '}
                                        <img
                                            src={logo}
                                            style={{width:"60%"}}
                                            alt="나드리로고"
                                        />
                                    </h3>

                                    <button
                                        className="btn btn-primary btn-lg btn-block"
                                        style={{ height: '75px' }}
                                        onClick={() => navigate('/register')}
                                    >
                                        나드리 회원가입
                                    </button>
                                    <button
                                        className="btn btn-primary btn-lg btn-block"
                                        style={{ height: '75px' }}
                                        onClick={() => navigate('/login')}
                                    >
                                        나드리 로그인
                                    </button>

                                    <button
                                        className="btn btn-lg btn-block btn-primary"
                                        style={{ backgroundColor: '#FEE500' }}
                                        onClick={kakaoLogin}
                                    >
                                        <img
                                            src={kakao}
                                            style={{ width: '35%' }}
                                            alt="카카오로그인 아이콘"
                                        />
                                    </button>
                                    <button
                                        className="btn btn-lg btn-block btn-primary mb-2"
                                        style={{ backgroundColor: '#03C75A' }}
                                        onClick={naverLogin}
                                    >
                                        <img
                                            src={naver}
                                            style={{ width: '50%' }}
                                            alt="네이버로그인 아이콘"
                                        />
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </section>
    );
}

const Box = styled.div`
    width: 100%;
    height: 100vh;
    display: flex;
    flex-direction: column;
`;

const Intro = styled.div`
    height: 40%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;

    h1 {
        display: flex;
        justify-content: center;
        img {
            width: 40%;
        }
    }

    h2 {
        font-weight: bold;
        margin-top: 10px;
    }

    p {
        text-align: center;
        margin-top: 20px;
    }
`;

const Select = styled.div`
    position: relative;
    height: 50%;
`;

const WrapBottom = styled.div`
    width: 100%;
    height: 100%;
    flex-shrink: 0;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    align-items: center;

    .signUp {
        width: 40%;
    }

    button {
        width: 75%;
        height: 13%;
        border-radius: 5px;
        margin-top: 10px;
        color: ${(props) => props.theme.color.white};
        overflow: hidden;
        font-weight: bold;

        a {
            width: 100%;
            height: 100%;
            display: flex;
            justify-content: center;
            align-items: center;
            background-color: ${(props) => props.theme.color.orange};
            transition: background 0.3s;

            &:hover {
                background-color: ${(props) => props.theme.hoverColor.orange};
            }
        }

        img {
            width: 100%;
        }
    }

    p {
        margin-top: 30px;
        span {
            color: black;
            font-weight: bold;
        }
    }
`;

export default Start;
