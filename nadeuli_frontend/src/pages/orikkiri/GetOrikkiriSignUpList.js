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
import TopArrowLeft from '../../components/TopArrowLeft';
import HeaderBack from '../../components/HeaderBack';
import { addBlockMember, deleteBlockMember, getMember, getMemberList } from '../../util/memberAxios';
import { useInView } from 'react-intersection-observer';
import styled from 'styled-components';
import axios from 'axios';
import { get, post } from '../../util/axios';
import { useParams } from 'react-router-dom';
import { addSignUp, deleteSignUp } from '../../util/orikkiriManageAxios';
import { post as chatPost } from "../../util/chatAxios";

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
    const getQuestion = async () => {
        try {
            const response = await axios.get(`${BASE_URL}/orikkiriManage/getAnsQuestionList/${orikkiriId}`);

            setQuestion(response.data[0].content);
        } catch (error) {
            console.error('데이터를 불러오는 데 실패했습니다.', error);
        }
    };

    // 서버에서 답변과 가입신청 멤버를 가지고 오는 함수
    const getItems = async () => {
        try {
            const response = await axios.get(`${BASE_URL}/orikkiri/getOrikkrirSignupList/${orikkiriId}`);
            // 각 객체의 tag를 이용하여 getMember 함수 호출 및 content 추가
            const updatedItems = await Promise.all(
                response.data.map(async (item) => {
                    const memberDTO = await getMember(item.tag);
                    return {
                        ...memberDTO.data,
                        content: item.content,
                        ansQuestionId: item.ansQuestionId,
                        oriScheMemChatFavId: item.oriScheMemChatFavId,
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
        getQuestion();
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

    // 신청 상세조회 함수
    const handleDetailClick = async (item) => {
        try {
            console.log(item);

            // 받아온 회원 정보를 state에 저장
            setSelectedMember(item);
            // 모달 열기
            toggleOpen();
        } catch (error) {
            console.error('가입신청서를 못받아왔습니다.', error);
        }
    };

    // 가입 신청 수락 함수
    const handleAddSignUpClick = async (ansQuestionId) => {
        const participant = { tag: selectedMember.tag, name: selectedMember.nickname };


        console.log("받은 orikkiriId는 "+orikkiriId)
        console.log("받은 participant는 "+participant)

        const chatReq = {
            orikkiriId: orikkiriId,
            participant: participant,
            
        };

        console.log(ansQuestionId);
        console.log(chatReq);
        try {
            await addSignUp(ansQuestionId);
            await chatPost("/api/chatRoom/joinChatRoom",chatReq)
            toggleOpen();
            getItems(); // 데이터 재로딩
        } catch (error) {
            console.error('가입신청 수락 실패했습니다', error);
        }
    };
    // 가입 신청 거절 함수
    const handleDeleteSignUpClick = async (oriScheMemChatFavId) => {
        console.log(oriScheMemChatFavId);
        try {
            await deleteSignUp(oriScheMemChatFavId);
            toggleOpen();
            getItems(); // 데이터 재로딩
        } catch (error) {
            // 에러 처리
            console.error('가입신청이 거절 되었습니다.', error);
        }
    };

    //정지기간 파싱
    const formatDate = (dateString) => {
        const date = new Date(dateString);
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');

        return `${year}년 ${month}월 ${day}일 까지`;
    };

    return (
        <div>
            <MDBNavbar
                light
                bgColor="light"
            >
                <TMenuBar>
                    <HeaderBack />
                    <p>우리끼리 가입신청 목록</p>
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
                                    <MDBBtn
                                        style={{ position: 'absolute', right: '20px' }}
                                        onClick={() => handleDetailClick(item)}
                                    >
                                        상세 조회
                                    </MDBBtn>
                                </div>
                            </MDBListGroupItem>
                        </div>
                    ))}
            </MDBListGroup>
            {/* 정지모달 */}
            <MDBModal
                open={basicModal}
                setopen={setBasicModal}
                onClick={() => setBasicModal(false)}
                tabIndex="-999"
            >
                <MDBModalDialog onClick={(e) => e.stopPropagation()}>
                    <MDBModalContent>
                        <MDBModalHeader>
                            <MDBModalTitle>
                                <MDBBadge
                                    style={{ fontSize: '18px' }}
                                    className="mx-1"
                                    color="primary"
                                    light
                                >
                                    {selectedMember?.nickname}
                                </MDBBadge>
                                님의 가입 신청서
                            </MDBModalTitle>
                            <MDBBtn
                                className="btn-close"
                                color="none"
                                onClick={toggleOpen}
                            ></MDBBtn>
                        </MDBModalHeader>
                        <MDBModalBody>
                            <MDBCol lg="4">
                                <MDBCard className="mb-4">
                                    <MDBCardBody className="p-0 d-flex align-items-center">
                                        <MDBCardImage
                                            src={selectedMember?.picture}
                                            alt="picture"
                                            className="rounded-circle"
                                            style={{ width: '150px', height: '150px' }}
                                            fluid
                                        />
                                        <div>
                                            <p className="text-muted mb-1 mt-2">
                                                {selectedMember?.nickname}
                                                <b style={{ fontSize: '14px', color: 'black' }}>#{selectedMember?.tag}</b>
                                            </p>
                                            <p className="text-muted mb-1 mt-2">{selectedMember?.dongNe}</p>
                                            <p className="text-muted mb-1 mt-2">친화력 {selectedMember?.affinity}점</p>
                                        </div>
                                    </MDBCardBody>
                                    <MDBCardBody className="p-0 d-flex align-items-center">
                                        <div>
                                            <p className="text-muted mb-1 mt-2">
                                                <MDBBadge
                                                    color="info"
                                                    style={{ fontSize: '16px', margin: '0 5px' }}
                                                    light
                                                >
                                                    질문
                                                </MDBBadge>
                                                {question}
                                            </p>
                                            <p className="text-muted mb-1 mt-2">
                                                <MDBBadge
                                                    color="danger"
                                                    style={{ fontSize: '16px', margin: '0 5px' }}
                                                    light
                                                >
                                                    답변
                                                </MDBBadge>
                                                {selectedMember?.content}
                                            </p>
                                        </div>
                                    </MDBCardBody>
                                </MDBCard>
                            </MDBCol>
                        </MDBModalBody>
                        <MDBModalFooter>
                            <MDBBtn
                                color="secondary"
                                onClick={() => handleDeleteSignUpClick(selectedMember.oriScheMemChatFavId)}
                            >
                                거절
                            </MDBBtn>
                            <MDBBtn onClick={() => handleAddSignUpClick(selectedMember.ansQuestionId)}>수락</MDBBtn>
                        </MDBModalFooter>
                    </MDBModalContent>
                </MDBModalDialog>
            </MDBModal>
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
