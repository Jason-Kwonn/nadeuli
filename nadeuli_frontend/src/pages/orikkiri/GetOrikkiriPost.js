import React, { useEffect, useRef, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import styled from "styled-components";
import TopDropdownMenu from '../../components/TopDropdownMenu';
import TopArrowLeft from '../../components/TopArrowLeft';
import PostInfo from "../../components/PostInfo";
import { GetDongNePostDetail, deleteDongNePost, removeDongNePost } from '../../redux/modules/dongNePost';
import Comment from "../Comment";
import Hls from 'hls.js';
import AlertDialog from '../../components/AlertDialog'


function GetOrikkiriPost() {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const getDongNePost = useSelector((state) => state.dongNePost.dongNePost);
  const member = useSelector((state) => state.member);
  const params = useParams();
  const postId = params.postId;
  const videoRef = useRef({});
  const [dialogOpen, setDialogOpen] = useState(false);
  const [dialogTitle, setDialogTitle] = useState('');
  const [dialogDescription, setDialogDescription] = useState('');
  const [pendingAction, setPendingAction] = useState(null);
  const [dialogAgreeText, setDialogAgreeText] = useState('');
  const [dialogDisagreeText, setDialogDisagreeText] = useState('');

  useEffect(() => {
    dispatch(GetDongNePostDetail(postId));
  }, [dispatch, postId]);

  const executePendingAction = () => {
    if (pendingAction) pendingAction();
    setPendingAction(null);
    closeDialog();
  };

  const openDeleteDialog = () => {
    setPendingAction(() => () => {
      dispatch(removeDongNePost(postId));
      navigate('/dongNeHome');
    });
    setDialogTitle("삭제 확인");
    setDialogDescription("이 게시물을 삭제하시겠습니까?");
    setDialogAgreeText("삭제");
    setDialogDisagreeText("취소");
    setDialogOpen(true);
  };


  const openReportDialog = () => {
    setPendingAction(() => () => {
      // 신고 관련 로직
    });
    setDialogTitle("신고 확인");
    setDialogDescription("이 채널을 신고하시겠습니까?");
    setDialogAgreeText("신고");
    setDialogDisagreeText("취소");
    setDialogOpen(true);
  };

  const closeDialog = () => {
    setDialogOpen(false);
  };

  const handleupdate = async () => {
    setPendingAction(() => () => {
      // postCategory가 4인지 확인하고, 맞다면 /updateOrikkiriAlbum/{postId}로 네비게이션합니다.
      if (getDongNePost?.postCategory === 4) {
        navigate(`/updateOrikkiriAlbum/${postId}`);
      } else {
        navigate(`/updateOrikkiriPost/${postId}`);
      }
    });
    setDialogTitle("수정 확인");
    setDialogDescription("이 게시물을 수정하시겠습니까?");
    setDialogAgreeText("수정");
    setDialogDisagreeText("취소");
    setDialogOpen(true);
  }
  

  const dropdownMenus1 = [
    { label: '수정', onClick: handleupdate },
    { label: '삭제', onClick: openDeleteDialog },
    // 원하는 만큼 추가
  ];

  const dropdownMenus3 = [
    { label: '신고', onClick: openReportDialog }
    // 원하는 만큼 추가
  ];

  useEffect(() => {
    const videoElement = videoRef.current[0]; // 첫 번째 비디오 요소 참조
    if (getDongNePost.streaming && videoElement) {
      if (Hls.isSupported()) {
        const hls = new Hls();
        hls.loadSource(getDongNePost.streaming);
        hls.attachMedia(videoElement);
      } else if (videoElement.canPlayType('application/vnd.apple.mpegurl')) {
        videoElement.src = getDongNePost.streaming;
      }
    }
  }, [getDongNePost.streaming]);


  return (
    <Wrap>
      <Header>
        <TopArrowLeft/>
        {
          member?.tag !== getDongNePost?.writer?.tag ? 
            <TopDropdownMenu dropdownMenus={dropdownMenus3}/> :
              <TopDropdownMenu dropdownMenus={dropdownMenus1}/>
        }
      </Header>

      <Container>
        <div>
          <PostInfo
           text={
            getDongNePost?.postCategory === 0 ? "공지사항" :
            getDongNePost?.postCategory === 1 ? "잡담" :
            getDongNePost?.postCategory === 4 ? "앨범" : "기타"
          }
            writerPicture={getDongNePost?.writer?.picture}
            writerNickName={getDongNePost?.writer?.nickname}
            writerDongNe={getDongNePost?.writer?.dongNe}
            timeAgo={getDongNePost?.timeAgo}
          />
        </div>
        <div>
          <Title>
            <p>{getDongNePost.title}</p>
          </Title>
        
        {/* 파일(이미지/비디오)이 있는 경우에만 File 컴포넌트를 렌더링 */}
        {(getDongNePost?.streaming || (getDongNePost?.images && getDongNePost?.images.length > 0)) && (
          <File>
            {/* HLS 스트리밍 비디오 처리 */}
              {getDongNePost?.streaming && (
                <video
                  ref={(el) => videoRef.current[0] = el} // 비디오 참조 설정
                  src={getDongNePost?.streaming}
                  playsInline
                  controls
                  autoPlay
                  muted
                  width="100%"
                  height="auto"
                />
              )}

            {/* .mp4 이미지 비디오 처리 */}
            {getDongNePost?.images && getDongNePost?.images.map((image, index) => {
              if (image.endsWith('.mp4')) {
                return (
                  <video key={index} ref={(el) => (videoRef.current[index] = el)} src={image} playsInline controls autoPlay muted />
                );
              } else {
                return (
                  <img key={index} src={image} alt={`Image ${index}`} />
                );
              }
            })}
          </File>
        )}
          <Content>
            <p>{getDongNePost?.content}</p>
          </Content>
        </div>
        
        {
          ![0, 3, 4].includes(getDongNePost?.postCategory) && 
            <Comment postId={getDongNePost?.postId}></Comment>
        }

      </Container>
      <AlertDialog
        open={dialogOpen}
        handleClose={closeDialog}
        onAgree={executePendingAction}
        title={dialogTitle}
        description={dialogDescription}
        agreeText={dialogAgreeText}
        disagreeText={dialogDisagreeText}
      />

    </Wrap>
  );
}


const Wrap = styled.div`
box-sizing: border-box;
font-size: 18px;

input {
  font-size: 13px;
}

textarea {
  margin-top: 45px;
  border: none;
  outline: none;
  resize: none;
  font-size: 18px;
  height: 400px; /* 높이 조절 */
}
textarea::placeholder {
  color: #dadada;
  font-size: 18px;
}
`;
const Header = styled.header`
  position:relative;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 25px 20px;
  border-bottom: 1px solid #dadada;
  
  
  h4 {
    font-weight: 800;
    font-size: 20px; /* 원하는 크기로 수정 */
    margin: 0; /* 기본 마진 제거 */
  }

  h5 {
    color: #4da6ff;
    font-size: 16px;
    margin: 0; /* 기본 마진 제거 */
  }
`;

const Container = styled.div`
  padding: 10px 10px; /* 여백을 조정할 수 있습니다 */
`;

const File = styled.div`
padding: 30px 30px;
// border-bottom: 1px solid #dadada;

.camera {
  font-size: 50px;
  margin-right: 10px; 
}
.video {
  font-size: 50px;
}
label {
  cursor: pointer;
}
input[type="file"] {
  display: none;
}

img {
  max-width: 100%; /* 최대 너비를 100%로 설정 */
  max-height: 100%; /* 최대 높이를 100%로 설정 */
  margin-left: 10px;
  margin-right: 10px;
  border-radius: 5px;
}

video {
  max-width: 100%; /* 최대 너비를 100%로 설정 */
  max-height: 100%; /* 최대 높이를 100%로 설정 */
  margin-left: 10px;
  margin-right: 10px;
  border-radius: 5px;
}
`;

const Title = styled.div`
padding: 25px 0px;
border-bottom: 1px solid #dadada;

outline: none;
input {
  border: none;
  outline: none;
  font-size: 25px;
}

input::placeholder {
  color: #dadada;
  border: none;
  font-size: 25px;
}
`;
const Content = styled(Title)`
display: flex;
justify-content: space-between;

select {
  width: 100%;
  border: none;
  outline: none;
  font-size: 20px;
}
`;

const Img = styled.img`
    border-radius: 100px;
    width: 100px;
    border: 1px solid black;
    margin-left: 20px;
    height: 100px;
`;



export default GetOrikkiriPost;