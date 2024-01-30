import React, { useState, useEffect } from "react";
import { useSelector, useDispatch } from 'react-redux';
import { GetDongNePostList } from '../redux/modules/dongNePost';
import '../style/css/postInfo.css';
import { FaRegComment } from "react-icons/fa";
import styled from 'styled-components';
import { useNavigate } from 'react-router-dom';

function OrikkiriPostList({orikkiriId ,updatePostCount}) {
  
  const checkOrikkiriId = orikkiriId;
  const location = useSelector((state) => state.member.gu);
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const [currentPage] = useState(0);
  const [selectedCategory] = useState(1);
  

  const dongNePostList = useSelector((state) => state.dongNePost.dongNePostList);

  
  const filteredDongNePostList = dongNePostList.filter(dongNePost => {
    if (selectedCategory !== null && dongNePost.postCategory !== selectedCategory) {
      return false;
    }
    // if (dongNePost.images && dongNePost.images.length > 0) {
    //   const extension = dongNePost.images[0].split('.').pop().toLowerCase();
    //   if (extension === 'mp4') {
    //     return false;
    //   }
    // }
    // console.log(dongNePost?.orikkiri?.orikkiriId)
    if (dongNePost.orikkiri?.orikkiriId != checkOrikkiriId) {
      return false;
    }

    return dongNePost.gu === location;
  });
  
  useEffect(() => {
    dispatch(GetDongNePostList(currentPage, location, ''));
  }, [currentPage, location, dispatch]);

  // useEffect(() => {
  //   // 컴포넌트가 마운트되거나 filteredDongNePostList가 변경될 때마다 게시물 수 업데이트
  //   updatePostCount(filteredDongNePostList.length);
  // }, [filteredDongNePostList, updatePostCount]);
  

  return (
    <div>
      <MainListBox>
        {filteredDongNePostList.map((dongNePost) => (
          <CardBox key={dongNePost.postId}>
            <div onClick={() => navigate("/getOrikkiriPost/" + dongNePost.postId)}>
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
        ))}
      </MainListBox>
      {selectedCategory === 1 && (
        <FixedButton2 onClick={() => navigate(`/addOrikkiriPost/${orikkiriId}`)}>+ 글쓰기</FixedButton2>
      )}
    </div>
  );
}

// Styled Components
const FixedButton2 = styled.div`
  display: flex;
  position: fixed;
  bottom: 100px;
  right: 30px;
  width: 120px;
  height: 50px;
  font-size: 20px;
  font-weight: bold;
  background-color: #508BFC;
  color: white;
  border-radius: 40px;
  justify-content: center;
  align-items: center;
  box-shadow: 0 0 6px 0 #999;
`;

const CardBox = styled.div`
  display: flex;
  padding: 10px 20px;
  justify-content: space-between;
  border-bottom: 1px solid #dddddd;
`;

const TextArea = styled.div`
  display: flex;
  flex-direction: column;
  padding: 5px 10px;
`;

const Img = styled.img`
  width: 100px;
  height: 80px;
  border-radius: 10px;
  object-fit: cover;
  margin-right: 10px; /* 이미지와 텍스트 간 간격 */
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

const MainListBox = styled.div`
  display: flex;
  width: 100%;
  flex-direction: column;
`;

const CommentSection = styled.div`
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  width: 35px;
  font-size: 16px;
`;

export default OrikkiriPostList;
