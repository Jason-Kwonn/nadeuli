import React, { useEffect, useState, useRef, useCallback } from 'react';
import {
    MDBBadge,
    MDBListGroup,
    MDBListGroupItem,
    MDBContainer,
    MDBNavbar,
    MDBBtn,
    MDBInputGroup,
    MDBModal,
    MDBModalDialog,
    MDBModalContent,
    MDBModalHeader,
    MDBModalTitle,
    MDBModalBody,
    MDBModalFooter,
    MDBTextArea,
    MDBRadio,
    MDBCol,
    MDBCard,
    MDBCardBody,
    MDBCardImage,
} from 'mdb-react-ui-kit';
import TopArrowLeft from '../components/TopArrowLeft';
import HeaderBack from '../components/HeaderBack';
import { addBlockMember, deleteBlockMember, getMember, getMemberList } from '../util/memberAxios';
import { useInView } from 'react-intersection-observer';
import styled from 'styled-components';
import axios from 'axios';
import { get } from '../util/axios';
import { useParams } from 'react-router-dom';
import { addSignUp, deleteSignUp } from '../util/orikkiriManageAxios';

export default function GetOrikkiriSignUpList() {
    const [items, setItems] = useState([]);
    const [page, setPage] = useState(0);
    const [question, setQuestion] = useState('');
    const [searchKeyword, setSearchKeyword] = useState('');
    const [basicModal, setBasicModal] = useState(false);
    const [blockReason, setBlockReason] = useState('');
    const [blockDay, setBlockDay] = useState('');
    const [selectedMember, setSelectedMember] = useState(null);
    const params = useParams();
    const orikkiriId = params.orikkiriId;
    const BASE_URL = process.env.REACT_APP_BASE_URL;

    // 서버에서 질문을 가지고 오는 함수


    // 서버에서 답변과 가입신청 멤버를 가지고 오는 함수
    const getItems = async () => {
        try {
            const response = await axios.get(`${BASE_URL}/orikkiri/getOrikkiriMemberList/${orikkiriId}`);
            // 각 객체의 tag를 이용하여 getMember 함수 호출 및 content 추가
            const updatedItems = await Promise.all(
                response.data.map(async (item) => {
                    const memberDTO = await getMember(item.tag);
                    return {
                        ...memberDTO.data,
                    };
                })
            );

            console.log(updatedItems);

            // 서버 응답 결과를 전부 setItems 호출
            setItems(updatedItems);
        } catch (error) {
            console.error('데이터를 불러오는 데 실패했습니다.', error);
        }
    };

    useEffect(() => {
        getItems(); // 초기 렌더링 시에 데이터 불러오기
    }, []);

    // 모달 열기
    const toggleOpen = () => {
        setBasicModal(!basicModal);
    };

    useEffect(() => {
        // 모달이 열릴 때 값 초기화
        if (basicModal) {
            setBlockReason('');
            setBlockDay('');
        }
    }, [basicModal]);


    // 가입 신청 수락 함수
    const handleAddSignUpClick = async (ansQuestionId) => {
        console.log(ansQuestionId);
        try {
            await addSignUp(ansQuestionId);
            toggleOpen();
            getItems(); // 데이터 재로딩
        } catch (error) {
            console.error('가입신청 수락 실패했습니다', error);
        }
    };



    return (
        <div>
            <MDBNavbar
                light
                bgColor="light"
            >
                <TMenuBar>
                    <HeaderBack />
                    <p>우리끼리 회원 목록</p>
                </TMenuBar>
            </MDBNavbar>
            <MDBListGroup
                style={{ minWidth: '22rem' }}
                light
            >
                {items.length > 0 &&
                    items.map((item, index) => (
                        <div
                            key={index}
                            className={`list-item ${items.length - 1 === index ? 'last-item' : ''}`}
                        >
                            <MDBListGroupItem className="relative d-flex justify-content-between align-items-center">
                                <div className="d-flex align-items-center">
                                    <img
                                        src={item.picture}
                                        alt={`Profile ${index}`}
                                        style={{ width: '45px', height: '45px' }}
                                        className="rounded-circle ms-3"
                                    />
                                    <div className="ms-3">
                                        <p className="fw-bold mb-0">
                                            {item.nickname}
                                            <i>#{item.tag}</i>
                                        </p>
                                        <p className="text-muted mb-0">친화력 {item.affinity}점</p>
                                    </div>
                                 
                                </div>
                            </MDBListGroupItem>
                        </div>
                    ))}
            </MDBListGroup>
        </div>
    );
}
const TMenuBar = styled.div`
    position: relative;
    width: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;
    text-align: center;

    p {
        position: absolute;
        left: 50%;
        transform: translateX(-50%);
        margin: 0;
    }
`;
