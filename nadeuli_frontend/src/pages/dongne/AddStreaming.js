import React, { useState, useRef, useEffect } from "react";
import { IoIosClose } from "react-icons/io";
import { useNavigate } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import styled from "styled-components";
import Modal from '../../components/Modal';
import { createliveChannel, getChannelDetail, getChannelUrl } from '../../redux/modules/streaming';
import { getOrikkiriList } from '../../redux/modules/orikkiri';
import { dongNePost } from "../../redux/modules/dongNePost";
import { IoCopyOutline } from "react-icons/io5";


function AddStreaming() {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const title_ref = useRef();
  const [step, setStep] = useState(1);
  const [channelId, setChannelId] = useState('');
  const [streamKey, setStreamKey] = useState('');
  const [streamingUrl, setStreamingUrl] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const location = useSelector((state) => state.member.gu);
  const member = useSelector((state) => state.member);
  const [channelTitle, setChannelTitle] = useState('');
  const [titleError, setTitleError] = useState(''); // New state for title validation error

  const streaming = useSelector((state) => state.streaming.channel);
  const [justAdded, setJustAdded] = useState(false); // 추가한 채널 추적을 위한 새로운 상태 변수

  const validateTitle = (title) => {
    const regex = /^[가-힣a-zA-Z0-9_]{3,20}$/; // 3-20 글자, 한글, 영문자, 숫자 및 밑줄 허용
    if (title.length < 3 || title.length > 20) {
      return "제목은 3글자 이상 20글자 이하여야 합니다.";
    } else if (!regex.test(title)) {
      return "한글, 영문자, 숫자, 밑줄(_)만 사용 가능합니다.";
    } else {
      return "";
    }
  };

  const handleTitleChange = (e) => {
    const newTitle = e.target.value;
    const validationResult = validateTitle(newTitle);
    setTitleError(validationResult); // 유효성 검사 오류 메시지 설정
    setChannelTitle(newTitle); // 제목 업데이트
  };


  useEffect(() => {
    if (streaming && streaming.channelId) {
      setChannelId(streaming.channelId);
      setStreamKey(streaming.streamKey);
      setStreamingUrl(streaming.url);
    }
  
    // addStreaming 함수가 실행된 직후에는 경고 메시지를 표시하지 않습니다.
    if (streaming && streaming.channelStatus !== 'PUBLISHING' && step === 2 && !justAdded) {
      alert("스트림키 값을 입력 후 방송을 시작해 주세요.");
    }
  
    if (streaming && streaming.channelStatus === 'PUBLISHING' && step === 2) {
      setStep(3);
    }

  
    // 상태 초기화
    if (justAdded) {
      setJustAdded(false);
    }
  }, [streaming, step, justAdded]);
  

  const handleNextStep = () => {
    if (titleError) {
      // titleError가 존재한다면 경고 메시지를 표시하고 다음 단계로 진행하지 않습니다.
      alert("제목을 확인해주세요. " + titleError);
    } else {
      // 유효성 검사를 통과했다면 다음 단계로 진행합니다.
      if (step === 1) {
        addStreaming();
      } else {
        // 다음 단계로 진행할 추가 로직
      }
    }
  };


  const addDongNePost = () => {
    console.log(title_ref.current);
    const writer = {tag: member.tag};
    const title = channelTitle;
    const postCategory = 3;

    const formData = new FormData();
    const postDTOData = {
      title: title,
      postCategory: postCategory,
      gu: location,
      dongNe: member.dongNe,
      writer: writer,
      streaming : streamingUrl,
      video : streamingUrl
    };

    formData.append('postDTO', new Blob([JSON.stringify(postDTOData)], { type: "application/json" }));

    // 파일 추가
    const files = streamingUrl;
    for (let i = 0; i < files.length; i++) {
      formData.append('images', files[i]);
    }
    dispatch(dongNePost(formData, navigate, '/dongNeHome'));
  };


  const copyStreamKey = () => {
    navigator.clipboard.writeText(streamKey)
      .then(() => alert("스트림 키가 복사되었습니다."))
      .catch(err => console.error("복사 실패:", err));
  };

  const copyGlobalRTMP = () => {
    navigator.clipboard.writeText(streaming?.globalRtmpUrl)
      .then(() => alert("Global RTMP 주소가 복사되었습니다."))
      .catch(err => console.error("복사 실패:", err));
  };

  const copyRTMP = () => {
    navigator.clipboard.writeText(streaming?.rtmpUrl)
      .then(() => alert("RTMP 주소가 복사되었습니다."))
      .catch(err => console.error("복사 실패:", err));
  };

  const addStreaming = async () => {
    setIsSubmitting(true);
    try {
      const channelName = channelTitle.replace(/\s+/g, '_');
      const formData = new FormData();
      formData.append('channelName', channelName);
      await dispatch(createliveChannel(formData, navigate));
      setIsSubmitting(false);
      setStep(2);
      setJustAdded(true); // 채널 추가 후 상태 업데이트
    } catch (error) {
      console.error('Streaming 처리 중 에러 발생', error);
      setIsSubmitting(false);
    }
  };

  const checkStreaming = async () => {
    setIsSubmitting(true);
    try {
      await dispatch(getChannelDetail(channelId));
      // 상태 업데이트는 useEffect에서 처리합니다.
    } catch (error) {
      console.error('Streaming 처리 중 에러 발생', error);
    } finally {
      setIsSubmitting(false);
    }
  };

  const renderCreateChannel = () => (
    <div>
      <Title>
        <input
          placeholder="채널 제목을 입력해주세요."
          value={channelTitle}
          onChange={handleTitleChange} // 핸들러를 새로운 함수로 업데이트
        />
        {titleError && <p style={{ color: "red" }}>{titleError}</p>}
      </Title>
      <Button onClick={handleNextStep}>
        {isSubmitting ? '처리 중...' : '다음'}
      </Button>
    </div>
  );

  const renderChannelDetails = () => (
    <DetailContainer>
      <p>채널 이름: {streaming?.channelName}</p>
      <DetailRow>
        <p>스트림 키: {streaming?.streamKey}</p>
        <CopyButton onClick={copyStreamKey}><IoCopyOutline size="20" /></CopyButton>
      </DetailRow>
      <DetailRow>
        <p>Global RTMP: {streaming?.globalRtmpUrl}</p>
        <CopyButton onClick={copyGlobalRTMP}><IoCopyOutline size="20" /></CopyButton>
      </DetailRow>
      <DetailRow>
        <p>RTMP: {streaming?.rtmpUrl}</p>
        <CopyButton onClick={copyRTMP}><IoCopyOutline size="20" /></CopyButton>
      </DetailRow>
      <Button onClick={checkStreaming}>다음</Button>
    </DetailContainer>
  );

  const renderAddStreamingPost = () => (
    <div>
      <p>채널 이름: {streaming?.channelName}</p>
      <p>스트림 키: {streaming?.streamKey}</p>
      <p></p>
      <p>위 정보로 스트리밍을 시작합니다.</p>
      <Button onClick={addDongNePost}>완료</Button>
    </div>
  );

  return (
    <Wrap>
      <Header>
        <IoIosClose size="30" onClick={() => navigate("/dongNeHome")} />
        <h4>스트리밍 생성</h4>
      </Header>

      <Container>
        {step === 1 && renderCreateChannel()}
        {step === 2 && renderChannelDetails()}
        {step === 3 && renderAddStreamingPost()}
      </Container>
    </Wrap>
  );
}

// Styled components
const Wrap = styled.div`
  font-size: 18px;
  max-width: 100%;
  background: #f5f5f5; // 밝은 배경색 추가
`;

const Header = styled.header`
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px; // 패딩 조정
  border-bottom: 1px solid #eaeaea; // 경계선 색상 변경

  h4 {
    font-weight: 600; // 글자 굵기 변경
    color: #333; // 글자색 변경
  }
`;

const Container = styled.div`
  padding: 20px; // 패딩 조정
`;

const Title = styled.div`
  margin-bottom: 20px; // 마진 조정

  input {
    border: 2px solid #508BFC; // 테두리 색상 변경
    border-radius: 8px; // 테두리 둥글게
    font-size: 18px; // 글자 크기 조정
    width: 100%;
    padding: 12px; // 패딩 조정
    margin-top: 10px; // 마진 추가

    ::placeholder {
      color: #aaa; // 플레이스홀더 색상 변경
    }
  }
`;

const Button = styled.button`
  display: block;
  width: 100%;
  padding: 10px 0; // 패딩 조정
  background-color: #508BFC; // 기본 버튼 색상
  color: white;
  border: none;
  border-radius: 8px; // 둥글게 처리
  cursor: pointer;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); // 그림자 효과 추가

  &:hover {
    background-color: #4071f4; // 호버시 색상 변경
  }
`;

const DetailContainer = styled.div`
  // 컨테이너 내부 스타일링
`;

const DetailRow = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px; // 각 세부 사항 사이의 간격 조정
`;

const CopyButton = styled.button`
  background-color: transparent;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #508BFC; // 색상 변경
  &:hover {
    color: #4071f4;
  }
`;

export default AddStreaming;
