import React, { useRef, useState } from 'react';
import HeaderBack from '../../components/HeaderBack';
import styled from 'styled-components';
import {
    Input,
    initMDB,
    MDBBreadcrumb,
    MDBCard,
    MDBCardBody,
    MDBCol,
    MDBContainer,
    MDBInput,
    MDBRow,
    MDBTextArea,
    MDBCardImage,
    MDBTypography,
    MDBIcon,
    MDBBtn,
} from 'mdb-react-ui-kit';
import { useSelector } from 'react-redux';
import { addAnsQuestion, addOrikkiri } from '../../util/orikkiriManageAxios';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import socket from '../../util/socket';
import { post } from '../../util/axios';
import { post as chatPost } from "../../util/chatAxios";

const AddOrikkiri = () => {
    const BASE_URL = process.env.REACT_APP_BASE_URL;
    const navigate = useNavigate();
    const [orikkiriName, setOrikkiriName] = useState('');
    const [orikkiriIntroduction, setOrikkiriIntroduction] = useState('');
    const imageInput = useRef();
    const member = useSelector((state) => state.member);
    //이미지 추가
    const [imageSrc, setImageSrc] = useState(
        'https://kr.object.ncloudstorage.com/nadeuli/image/%EB%B3%84%EC%9E%90%EB%A6%AC%20%EB%AA%A8%EC%9E%8420231213095749026.png'
    );

    //질문 추가
    const [questions, setQuestions] = useState([
        {
            id: 1,
            label: '내용',
            maxLength: 12,
            minLength: 6,
        },
    ]);

    const addQuestion = () => {
        if (questions.length < 5) {
            const newId = questions.length + 1;
            const newQuestion = {
                id: newId,
                label: '내용',
                maxLength: 12,
                minLength: 6,
            };
            setQuestions([...questions, newQuestion]);
        }
    };

    const removeQuestion = (id) => {
        if (questions.length > 1) {
            const updatedQuestions = questions.filter((question) => question.id !== id);
            // 고정된 번호를 부여
            updatedQuestions.forEach((question, index) => {
                question.id = index + 1;
            });
            setQuestions(updatedQuestions);
        }
    };

    // 입력 필드에 대한 onChange 이벤트 핸들러
    const handleOrikkiriNameChange = (e) => {
        setOrikkiriName(e.target.value);
    };

    const handleOrikkiriIntroductionChange = (e) => {
        setOrikkiriIntroduction(e.target.value);
    };

    // 이미지 추가
    const selectFile = (e) => {
        const file = e.target.files[0];
        const fileType = file.type.split('/')[0];

        if (fileType === 'image') {
            const reader = new FileReader();
            reader.onloadend = () => {
                setImageSrc(reader.result);
            };
            reader.readAsDataURL(file);
        } else {
            alert('.jpg와 .png 파일만 업로드 가능합니다.');
            e.target.value = null;
        }
    };

    const handleCreateOrikkiri = async () => {
        try {
            const formData = new FormData();

            formData.append('image', imageInput.current.files[0]);

            formData.append('dongNe', member.dongNe);
            formData.append('masterTag', member.tag);
            formData.append('orikkiriName', orikkiriName);
            formData.append('orikkiriIntroduction', orikkiriIntroduction);

            const response = await axios.post(`${BASE_URL}/orikkiriManage/addOrikkiri`, formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });

            // createdOrikkiri에서 orikkiriId 추출
            const orikkiriId = response.data.orikkiriId;
            console.log('orikkiriId :' + orikkiriId);

            // 각각의 질문에 대한 처리
            // question의 입력값을 가져와서 addAnsQuestionDTO 구성
            const inputValue = document.getElementById(`question`).value; // 사용자 입력 값 가져오기
            console.log('inputValue :' + inputValue);
            const newFormData = new FormData();

            newFormData.append('orikkiriId', orikkiriId);
            newFormData.append('content', inputValue);

            await axios.post(`${BASE_URL}/orikkiriManage/addAnsQuestion`, newFormData, {
                headers: {
                    'Content-Type': 'multipart/form-data', // 변경된 부분
                },
            });
            const participants = [
                { tag: member.tag, name: member.nickname },
            ];
            const chatReq = {
                tag: member.tag,
                roomName: "", 
                orikkiriId:orikkiriId,
                participants,
            };

            await chatPost('/api/chatRoom/findOrCreate', chatReq);
            navigate(`/orikkiriHome/${orikkiriId}`);
            // 추가적인 로직 또는 리다이렉션 등을 수행할 수 있음
        } catch (error) {
            console.error('Error creating orikkiri:', error);
        }
    };

    // const handleCreateOrikkiri = async () => {
    //     try {
    //         const formData = new FormData();

    //         formData.append('image', imageInput.current.files[0]);

    //         formData.append('dongNe', member.dongNe);
    //         formData.append('masterTag', member.tag);
    //         formData.append('orikkiriName', orikkiriName);
    //         formData.append('orikkiriIntroduction', orikkiriIntroduction);

    //         const response = await axios.post(`${BASE_URL}/orikkiriManage/addOrikkiri`, formData, {
    //             headers: {
    //                 'Content-Type': 'multipart/form-data',
    //             },
    //         });

    //         // createdOrikkiri에서 orikkiriId 추출
    //         const orikkiriId = response.data.orikkiriId;
    //         console.log('orikkiriId :' + orikkiriId);

    //         // 각각의 질문에 대한 처리
    //         for (const question of questions) {
    //             // question의 입력값을 가져와서 addAnsQuestionDTO 구성
    //             const inputValue = document.getElementById(`question-${question.id}`).value; // 사용자 입력 값 가져오기
    //             console.log('inputValue :' + inputValue);
    //             const formData = new FormData();

    //             formData.append('orikkiriId', orikkiriId);
    //             formData.append('content', inputValue);

    //             await axios.post(`${BASE_URL}/orikkiriManage/addAnsQuestion`, formData, {
    //                 headers: {
    //                     'Content-Type': 'multipart/form-data', // 변경된 부분
    //                 },
    //             });
    //         }
    //         navigate('/dongNeHome');
    //         // 추가적인 로직 또는 리다이렉션 등을 수행할 수 있음
    //     } catch (error) {
    //         console.error('Error creating orikkiri:', error);
    //     }
    // };

    return (
        <section style={{ backgroundColor: '#eee' }}>
            <MDBContainer>
                <MDBRow>
                    <MDBCol>
                        <MDBBreadcrumb className="bg-light rounded-3 p-3 mb-4">
                            <TMenuBar>
                                <HeaderBack />
                                <p>우리끼리 생성</p>
                            </TMenuBar>
                        </MDBBreadcrumb>
                    </MDBCol>
                </MDBRow>

                <MDBRow>
                    <MDBCol lg="4">
                        <MDBCard className="mb-4">
                            <MDBCardBody className="text-center">
                                <MDBCardImage
                                    alt="picture"
                                    src={imageSrc}
                                    className="mb-5"
                                    style={{ width: '150px', height: '150px', cursor: 'pointer', borderRadius: '8px' }}
                                    fluid
                                    onClick={() => imageInput.current.click()} // 클릭 시 input 엘리먼트 클릭 이벤트 호출
                                />
                                {/* 사진 업로드 */}
                                <input
                                    type="file"
                                    id="image-file"
                                    ref={imageInput}
                                    onChange={selectFile}
                                    accept="image/*"
                                    style={{ display: 'none' }} // 화면에 보이지 않도록 스타일 설정
                                />
                                <MDBInput
                                    label="우리끼리 이름"
                                    id="orikkiriName"
                                    value={orikkiriName} // 현재 상태값 적용
                                    onChange={handleOrikkiriNameChange} // onChange 이벤트 핸들러 적용
                                    type="text"
                                    maxLength={20}
                                    minLength={6}
                                    style={{ position: 'relative' }}
                                    className="mb-5"
                                ></MDBInput>
                                <MDBTextArea
                                    label="우리끼리 소개"
                                    id="orikkiriIntroduction"
                                    type="text"
                                    value={orikkiriIntroduction} // 현재 상태값 적용
                                    onChange={handleOrikkiriIntroductionChange} // onChange 이벤트 핸들러 적용
                                    style={{ position: 'relative' }}
                                    className="mb-4"
                                ></MDBTextArea>
                                <MDBTypography
                                    variant="h4"
                                    className="mb-1"
                                    style={{ textAlign: 'left' }}
                                >
                                    가입 질문
                                </MDBTypography>
                                <MDBTypography
                                    tag="h6"
                                    className="mb-4"
                                    style={{ textAlign: 'left' }}
                                >
                                    우리끼리 가입 신청 질문을 입력해주세요
                                </MDBTypography>
                                {/* {questions.map((question) => (
                                    <React.Fragment key={question.id}>
                                        <MDBTypography
                                            tag="strong"
                                            style={{ textAlign: 'left', display: 'block', marginTop: '20px' }}
                                        >
                                            질문{question.id}
                                        </MDBTypography>
                                        <MDBInput
                                            label={question.label}
                                            id={`question-${question.id}`}
                                            type="text"
                                            maxLength={question.maxLength}
                                            minLength={question.minLength}
                                            style={{ position: 'relative' }}
                                            className="mt-1"
                                        >
                                            {question.id > 1 && (
                                                <MDBIcon
                                                    far
                                                    icon="minus-square"
                                                    style={{ fontSize: '25px', position: 'absolute', top: -30, right: 5, cursor: 'pointer' }}
                                                    onClick={() => removeQuestion(question.id)}
                                                />
                                            )}
                                        </MDBInput>
                                    </React.Fragment>
                                ))}
                                {questions.length < 5 && (
                                    <MDBIcon
                                        far
                                        icon="plus-square"
                                        style={{ fontSize: '30px', marginTop: '10px', cursor: 'pointer' }}
                                        onClick={addQuestion}
                                    />
                                )} */}

                                <MDBTypography
                                    tag="strong"
                                    style={{ textAlign: 'left', display: 'block', marginTop: '20px' }}
                                >
                                    질문
                                </MDBTypography>
                                <MDBInput
                                    label="내용"
                                    id='question'
                                    type="text"
                                    style={{ position: 'relative' }}
                                    className="mt-1"
                                ></MDBInput>

                                <br />
                                <MDBBtn
                                    className="mt-1"
                                    onClick={handleCreateOrikkiri}
                                >
                                    우리끼리 생성
                                </MDBBtn>
                            </MDBCardBody>
                        </MDBCard>
                    </MDBCol>
                </MDBRow>
            </MDBContainer>
        </section>
    );
};

export default AddOrikkiri;
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