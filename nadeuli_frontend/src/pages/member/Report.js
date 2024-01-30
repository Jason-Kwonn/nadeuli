import React, { useState } from 'react';
import { MDBBtn, MDBTextArea, MDBContainer, MDBRow, MDBCol, MDBBreadcrumb } from 'mdb-react-ui-kit';
import HeaderBack from '../../components/HeaderBack';
import styled from 'styled-components';
import { addReport } from '../../util/memberAxios';
import { useSelector } from 'react-redux';
import { post } from '../../util/axios';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import socket from '../../util/socket';
export default function Report() {
    const [content, setContent] = useState('');
    const member = useSelector((state) => state.member);
    const { type, id } = useParams();
    const location = useLocation();
    const currentUrl = location.state?.currentUrl || '';
    const navigate = useNavigate();
    const handleReport = async () => {
        try {
            const reportData = {
                content: `${content}\nURL: ${currentUrl}`,
                reporter: {
                    tag: member.tag,
                },
                post: type && type === 'post' ? { postId: id } : null,
                product: type && type === 'product' ? { productId: id } : null,
                nadeuliDelivery: type && type === 'nadeuliDelivery' ? { nadeuliDeliveryId: id } : null,
            };

            // Report 생성 서비스 함수 호출
            await addReport(reportData);

            // 신고 내용을 관리자에게 채팅으로 전송
            const reportMessage = `[${member.tag}] ${member.nickname}님이 신고했습니다: ${content}\n URL:https://www.nadeuli.kr${currentUrl}`;
            const chatReq = {
                tag: '51eR',
                nickname: '신고접수봇', // 표시할 멤버닉네임이지만 "신고채팅봇"으로 할 예정
                roomId: '658988682f1015459a0681ef', // 고정된 채팅방 아이디
                message: reportMessage,
            };
            // post('/api/chatRoom/sendMessage', chatReq);
            socket.emit('sendMessage', chatReq);
            alert('신고 접수가 완료되었습니다.');
            navigate(-1);
        } catch (error) {
            console.error('Error creating report:', error);
            // 에러 처리
        }
    };

    return (
        <MDBContainer>
            <MDBRow>
                <MDBCol>
                    <MDBBreadcrumb className="bg-light rounded-3 p-3 mb-4">
                        <TMenuBar>
                            <HeaderBack />
                            <p>신고</p>
                        </TMenuBar>
                    </MDBBreadcrumb>
                </MDBCol>
            </MDBRow>
            <p className="fw-bold mb-0">[신고] 아래 신고 사유를 입력해주세요.</p>
            <MDBTextArea
                wrapperClass="mb-2 mt-4"
                label="신고 사유"
                value={content}
                onChange={(e) => setContent(e.target.value)}
            />

            <MDBBtn
                color="primary"
                block
                className="my-4"
                onClick={handleReport} // 신고 버튼 클릭 시 handleReport 함수 호출
            >
                신고하기
            </MDBBtn>
        </MDBContainer>
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
