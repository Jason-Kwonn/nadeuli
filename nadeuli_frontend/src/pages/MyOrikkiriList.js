import React, { useState, useEffect } from "react";
import { useSelector, useDispatch } from 'react-redux';
import { getOrikkiriList } from '../redux/modules/orikkiri';
import { useNavigate } from 'react-router-dom';
import "../style/css/orikkiriList.css"; // CSS 파일을 임포트합니다.
import { FaUserGroup } from "react-icons/fa6";

function OrikkiriList() {
  const member = useSelector((state) => state.member);
  const tag = useSelector((state) => state.member.tag);
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const [currentPage, setCurrentPage] = useState(0);

  // 데이터 구조 변경에 따라 필드 수정
  const orikkiriList = useSelector((state) => state.orikkiri.myOrikkiriList);

  useEffect(() => {
    dispatch(getOrikkiriList(tag, currentPage));
  }, [tag, currentPage, dispatch]);

  // console.log("dongNePostList:", dongNePostList);

  return (
    <div className="top-toolbar">
      <div className="image-container">
        <FaUserGroup className="orikkiri-image" />
        <div className="orikkiri-text">우리끼리 목록</div>
      </div>
      <div className="vertical-line"></div>

      {orikkiriList && orikkiriList.map((list, index) => (
      <div key={index}>
        <div onClick={() => {
          navigate("/orikkiriHome/" + list.orikkiri.orikkiriId);
        }}>
          <div className="image-container">
            <img className="circle-image" src={list?.orikkiri?.orikkiriPicture } alt="orikkiri Image" />
            <div className="orikkiri-text">{list.orikkiri?.orikkiriName}</div>
          </div>
        </div>
      </div>
      ))}
    </div>
  );
}

export default OrikkiriList;
