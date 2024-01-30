import { useEffect, useState } from "react";
import BottomBar from '../../components/BottonBar';
import { BiLeftArrowAlt } from "react-icons/bi";
import { FiMoreVertical } from "react-icons/fi";
import styled from "styled-components";
import { useNavigate, useParams } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { getOrikkiriDetail, modifyOrikkiri, deleteOrikkiri} from "../../redux/modules/orikkiri";
import Modal from "../../components/Modal";
import { FaMapMarkerAlt } from "react-icons/fa";
import OrikkiriPostList from "../../pages/OrikkiriPostList";
import OrikkiriNoticeList from "../../pages/OrikkiriNoticeList";
import OrikkiriAlbumList from "../../pages/OrikkiriAlbumList";
import TopDropdownMenu from '../../components/TopDropdownMenu';
import AlertDialog from '../../components/AlertDialog'
import OrikkiriScheduleList from "./OrikkiriScheduleList";


function OrikkiriHome() {
  const { orikkiriId } = useParams();
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const orikkiriDetail = useSelector((state) => state.orikkiri.orikkiri);
  const member = useSelector((state) => state.member); // 유저 정보
  const [selectedContent, setSelectedContent] = useState('home');
  const handleOrikkiriHome = () => setSelectedContent('home');
  const handleOrikkirPost = () => setSelectedContent('post');
  const handleSchule = () => setSelectedContent('schedule');
  const handleAlbum = () => setSelectedContent('album');
  const handleNotice = () => setSelectedContent('notice');
  const [dialogOpen, setDialogOpen] = useState(false);
  const [dialogTitle, setDialogTitle] = useState('');
  const [dialogDescription, setDialogDescription] = useState('');
  const [pendingAction, setPendingAction] = useState(null);
  const [dialogAgreeText, setDialogAgreeText] = useState('');
  const [dialogDisagreeText, setDialogDisagreeText] = useState('');
  const [postCount, setPostCount] = useState(0);

  // const updatePostCount = (count) => {
  //   setPostCount(count);
  // };

  const executePendingAction = () => {
    if (pendingAction) pendingAction();
    setPendingAction(null);
    closeDialog();
  };

  const closeDialog = () => {
    setDialogOpen(false);
  };

  useEffect(() => {
    dispatch(getOrikkiriDetail(orikkiriId));
  }, [dispatch, orikkiriId]);

  // 우리끼리 가입 리스트 이동
  const handleSignUpList = () => {
      navigate(`/getOrikkiriSignUpList/${orikkiriId}`);
  };

    // 우리끼리 회원 리스트 이동
  const handleMemberList = () => {
      navigate(`/orikkiriMemberList/${orikkiriId}`);
  };

    // 우리끼리 업데이트 확인
  const openUpdateDialog = () => {
    setPendingAction(() => () => {
      // dispatch(removeDongNePost(postId));
      navigate('/dongNeHome');
    });
    setDialogTitle("우리끼리 수정 확인");
    setDialogDescription("우리끼리를 수정 하시겠습니까?");
    setDialogAgreeText("우리끼리 수정");
    setDialogDisagreeText("취소");
    setDialogOpen(true);
  };

    // 우리끼리 삭제 확인
  const openDeleteDialog = () => {
    setPendingAction(() => () => {
      dispatch(deleteOrikkiri(orikkiriId));
      navigate('/dongNeHome');
    });
    setDialogTitle("우리끼리 삭제 확인");
    setDialogDescription("우리끼리를 삭제하시겠습니까?");
    setDialogAgreeText("우리끼리 삭제");
    setDialogDisagreeText("취소");
    setDialogOpen(true);
  };

      // 우리끼리 탈퇴 확인
  const openOutOrikkiri = () => {
    setPendingAction(() => () => {
      // dispatch(removeDongNePost(postId));
      navigate('/dongNeHome');
    });
    setDialogTitle("우리끼리 탈퇴 확인");
    setDialogDescription("우리끼리를 탈퇴 하시겠습니까?");
    setDialogAgreeText("우리끼리 탈퇴");
    setDialogDisagreeText("취소");
    setDialogOpen(true);
  };
  
  const dropdownMenuMaster = [
    { label: '가입 신청 조회', onClick: handleSignUpList },
    { label: '회원 목록 조회', onClick: handleMemberList },
    { label: '우리끼리 수정', onClick: openUpdateDialog },
    { label: '우리끼리 삭제', onClick: openDeleteDialog }
  ];

  const dropdownMenuMember = [
    { label: '회원 목록 조회', onClick: handleMemberList },
    { label: '탈퇴', onClick: openOutOrikkiri },
  ];
  
  console.log(orikkiriDetail.masterTag);

  return (
    <Wrap>
      <Header>
          <BiLeftArrowAlt
            size={30}
            color="black" // 여기에서 색상을 지정합니다.
            onClick={() => {
              navigate("/dongNeHome");
            }}
          />
          {/* 모달창 열기 */}
          {
           member.tag === orikkiriDetail.masterTag ? <TopDropdownMenu dropdownMenus={dropdownMenuMaster}/> : <TopDropdownMenu dropdownMenus={dropdownMenuMember}/>
          }
      </Header>

      <div>
        {orikkiriDetail && <SmallImage src={orikkiriDetail?.orikkiriPicture}/>}
      </div>

      <Container>
        <ProfileBar>
          <Profile>
            <RoundedImage src={orikkiriDetail?.orikkiriPicture} alt="orikkiriImg" />
            <Nickname>
              <p>{orikkiriDetail?.orikkiriName}</p>
              {/* {selectedContent === 'post' && (
              <OrikkiriPostList orikkiriId={orikkiriId} updatePostCount={updatePostCount}/>
              )}
              <p>게시물 수: {postCount}</p> */}
            </Nickname>
          </Profile>

        </ProfileBar>
          <StyledButton onClick={handleOrikkiriHome}>홈</StyledButton>
          <StyledButton onClick={handleOrikkirPost}>게시판</StyledButton>
          <StyledButton onClick={handleSchule}>일정</StyledButton>
          <StyledButton onClick={handleAlbum}>앨범</StyledButton>
          <StyledButton onClick={handleNotice}>공지사항</StyledButton>
        
        <Contents>
          
          {selectedContent === 'home' && (
            <>
              <p>우리끼리 소개</p>
              <OrikiriInfo>
                <OrikiriText>{orikkiriDetail?.orikkiriIntroduction}</OrikiriText>
                <FaMapMarkerAlt />
                <OrikiriText>{orikkiriDetail?.dongNe}</OrikiriText>
              </OrikiriInfo>
            </>
          )}
          {selectedContent === 'post' && <OrikkiriPostList orikkiriId={orikkiriId}/>}
          {/* 여기 아래에 일점 컴포넌트로 이름 변경 */}
          {/* {selectedContent === 'schedule' && <DongNePostList orikkiriId={orikkiriId}/>} */}
          {selectedContent === 'album' && <OrikkiriAlbumList orikkiriId={orikkiriId}/>}
          {selectedContent === 'notice' && <OrikkiriNoticeList orikkiriId={orikkiriId} orikkiriMasterTag={orikkiriDetail.masterTag}/>}
          {selectedContent === 'schedule' && <OrikkiriScheduleList orikkiriId={orikkiriId} orikkiriMasterTag={orikkiriDetail.masterTag}/>}
        
        </Contents>
      
      
      </Container>
      <Footer>
        <BottomBar selected='dongNeHome'/>
      </Footer>

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
  overflow-y:hidden;
  
  img {
    background-size: cover;
    background-position: center;
    height: 300px;
    width: 100%;
  }
  `;
  
  const Container = styled.div`
  padding: 16px 16px;
  position: relative;
  `;
  const Header = styled.header`
  position:relative;
  display: flex;
  justify-content: space-between;
  width: 100%;
  align-items: center;
  padding: 16px 10px;
  color: white;
  font-size: 23px;

  svg {
    filter: drop-shadow(0px 0px 1px rgb(0 0 0 / 0.4));
  }
`;

const ProfileBar = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 16px;
  border-bottom: 1px solid #dadada;

  p {
    font-size: 13px;
  }
`;

const Profile = styled.div`
  display: flex;
  justify-content: space-around;
  align-items: center;
  //width: 180px;
  line-height: 20px;


  img {
    width: 80px;
    height: 80px;
    border-radius: 50%;
    object-fit: cover;
  }

  div > p:first-child {
    font-weight: 600;
    font-size: 16px;
  }
`;

const Nickname = styled.div`
  //width: 100px;
  padding-left: 10px;

`;


const Contents = styled.div`
  padding-top: 30px;
  padding-bottom: 70px;
  line-height: 30px;
  position: relative;

  & p:first-child {
    font-weight: 600;
  }
  & p:nth-child(2) {
    font-size: 13px;
    text-decoration: underline;
    color: #aaa;
  }
  & p:nth-child(3) {
    font-size: 14px;
    padding-bottom:15px;
  }

  & p:last-child {
    font-size: 13px;
    color: #aaa;
  }
`;

const Footer = styled.div`
  background-color: white;
  display: flex;
  align-items: center;
  height: 70px;
  position: fixed;
  width: 100%;
  bottom: 0;
  border-top: 1px solid #dadada;
`;

const SmallImage = styled.img`
  height: 50px; /* 원하는 높이 값으로 설정 */
`;

const RoundedImage = styled.img`
  border-radius: 50%; /* 원형 이미지 */
  border: 1px solid black; /* 검은색 구분선 */
  width: 100px; /* 원하는 너비 */
  height: 100px; /* 원하는 높이 */
`;

const StyledButton = styled.button`
  width: 60px; /* 버튼의 너비 */
  height: 30px; /* 버튼의 높이 */
  background-color: #34bf9e; /* 버튼의 배경색 */
  color: white; /* 텍스트 색상 */
  font-size: 13px; /* 글꼴 크기 */
  border: none; /* 테두리 없애기 */
  border-radius: 25px; /* 모서리 둥글게 */
  cursor: pointer; /* 마우스 커서 스타일 */
  outline: none; /* 아웃라인 없애기 */
  margin-top: 10px;
  margin-right: 10px;
  transition: background-color 0.3s; /* 배경색 변경시 애니메이션 효과 */

  &:hover {
    background-color: #28a08d; /* 호버 시 배경색 변경 */
  }
`;

const OrikiriInfo = styled.div`
  // 기존 스타일
  border: 1px solid #ccc;
  border-radius: 10px;
  padding: 15px;
  margin: 10px 0;
  
  display: flex;
  flex-direction: column; /* 세로 방향으로 변경 */
  align-items: flex-start; /* 왼쪽 정렬 */
  gap: 10px; /* 요소들 사이의 간격 */
`;

const OrikiriText = styled.p`
  word-wrap: break-word; /* 단어가 길어도 줄바꿈 */
`;


export default OrikkiriHome;
