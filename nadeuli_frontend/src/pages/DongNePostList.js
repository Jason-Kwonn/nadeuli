import React, { useState, useEffect } from "react";
import { useSelector, useDispatch } from 'react-redux';
import { GetDongNePostList } from '../redux/modules/dongNePost';
import Orikkiri, { allOrikkiriList } from '../redux/modules/orikkiri';
import '../style/css/postInfo.css';
import { FaRegComment } from "react-icons/fa";
import styled from 'styled-components';
import { useNavigate } from 'react-router-dom';
import Promotion from './promotion';
import OrikkiriList from "./OrikkiriList";

function DongNePostList({searchQuery}) {
  const location = useSelector((state) => state.member.gu);
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const [currentPage, setCurrentPage] = useState(0);
  const [selectedCategory, setSelectedCategory] = useState(1);

  useEffect(() => {
    dispatch(GetDongNePostList(currentPage, location, searchQuery));
  }, [currentPage, location, searchQuery, dispatch]);

  useEffect(() => {
    dispatch(allOrikkiriList());
  }, [dispatch]);


  const handleChitChatClick = () => {
    setSelectedCategory(1);
  };

  const handlePromotionClick = () => {
    setSelectedCategory(2);
  };

  const handleOrikkiriClick = () => {
    setSelectedCategory(3);
  };

  const dongNePostList = useSelector((state) => state.dongNePost.dongNePostList);

  const filteredDongNePostList = dongNePostList.filter(dongNePost => {
    if (selectedCategory !== null && dongNePost.postCategory !== selectedCategory) {
      return false;
    }
    if (dongNePost.images && dongNePost.images.length > 0) {
      const extension = dongNePost.images[0].split('.').pop().toLowerCase();
      if (extension === 'mp4') {
        return false;
      }
    }

      // 추가 조건: orikkiriId 값이 없는 경우
    if (dongNePost.orikkiri && dongNePost.orikkiri.orikkiriId) {
        return false;
    }
    

    return dongNePost.gu === location;
  });
  
  useEffect(() => {
    dispatch(GetDongNePostList(currentPage, location, searchQuery));
  }, [currentPage, location, searchQuery, dispatch]);

  return (
    <div>
      <MainListBox>
      <Promotion/>
      <div style={{ display: 'flex', alignItems: 'center' }}>
        <FixedButton onClick={handleChitChatClick}>잡담</FixedButton>
        <FixedButton onClick={handlePromotionClick}>홍보</FixedButton>
        <FixedButton onClick={handleOrikkiriClick}>우리끼리</FixedButton>
      </div>
      {selectedCategory === 3 ? (
        // 우리끼리 카테고리 선택 시, OrikkiriList만 렌더링
        <OrikkiriList/>
      ) : (
        // 그 외의 경우 기존 포스트 목록 렌더링
        <div className="MainListBox">
        {filteredDongNePostList.map((dongNePost) => (
          <div key={dongNePost.postId}>
            <CardBox className="card">
              <div
                onClick={() => {
                  navigate("/getDongNePost/" + dongNePost.postId);
                }}
              >
                {/* 조건부 렌더링: 이미지가 있을 때만 Img 컴포넌트 렌더링 */}
                {dongNePost.images && dongNePost.images.length > 0 && (
                  <Img src={dongNePost.images[0]} alt="Post Image" />
                )}
                <TextArea>
                  <TitleSpan>{dongNePost.title}</TitleSpan>
                  <DetailSpan>{dongNePost.gu}</DetailSpan>
                </TextArea>
              </div>
            <CommentSection>
              <FaRegComment size="20" />
              {dongNePost.CommentNum}
            </CommentSection>
            </CardBox>
          </div>
        ))}
        </div>
      )}
      {selectedCategory == 1 && (
        <div>
          <FixedButton1 onClick={() => navigate("/addOrikkiri")}>+ 우리끼리</FixedButton1>
          <FixedButton2 onClick={() => navigate("/addDongNePost")}>+ 글쓰기</FixedButton2>
        </div>
      )}
      {selectedCategory == 2 && (
        <div>
          <FixedButton2 onClick={() => navigate("/addStreaming")}>+ 스트리밍</FixedButton2>
        </div>
      )}
      </MainListBox>
    </div>
  );
}

const ButtonStyle = styled.div`
  margin: 10px 0px; // 위 아래로 10px 여백 추가
  margin-left: 30px; // 왼쪽 여백 추가
  width: 100px;
  height: 45px;
  font-size: 20px;
  font-weight: bold;
  background-color: #508BFC;
  color: white;
  border-radius: 40px;
  justify-content: center;
  align-items: center;
  display: flex;
  box-shadow: 0 0 6px 0 #999;
`;

const TitleSpan = styled.span`
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 5px;
  padding: 0 5px;
  width: 100%;
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
`;

const DetailSpan = styled.span`
  font-size: 15px;
  font-weight: bold;
  padding: 5px;
  color: #AAAAAA;
`;



const CommentSection = styled.div`
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  width: 35px;
  font-size: 16px;
`;

const MainListBox = styled.div`
  display: flex;
  width: 100%;
  flex-direction: column;
`;

const CardBox = styled.div`
  display: flex;
  padding: 20px;
  justify-content: space-between;
  border-bottom: 1px solid #dddddd;
`;

const TextArea = styled.div`
  display: flex;
  flex-direction: column;
  width: 100%;
  padding: 10px;
`;

const FixedButton = styled(ButtonStyle)``;
const FixedButton1 = styled(ButtonStyle)`
  position: fixed;
  bottom: 160px;
  right: 30px;
  width: 120px; // 크기를 조금 더 크게
  height: 50px;
`;
const FixedButton2 = styled(ButtonStyle)`
  position: fixed;
  bottom: 100px;
  right: 30px;
  width: 120px; // 크기를 조금 더 크게
  height: 50px;
`;


const Img = styled.img`
  width: 100px;
  height: 100px;
  border-radius: 10px;
  object-fit: cover;
`;

export default DongNePostList;
