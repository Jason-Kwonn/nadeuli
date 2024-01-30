import React, { useState } from 'react';
import { SlOptionsVertical } from "react-icons/sl";
import '../style/css/dropdownMenu.css';

/*
사용방법
1. 메뉴바 공통컴포넌트인 TopDropdownMenu.js를 사용할 컴포넌트에서 객체배열 생성 후 값 입력
 1-1. label은 메뉴이름
 1-2. 클릭이벤트는 컴포넌트에서 생성한 함수할당
 
    const openModal = () => {
        setIsModalVisible(true);
    };

 const dropdownMenus = [
        { label: '프로필 수정', onClick: "" },
        { label: '비활성화', onClick: openModal },
        // 원하는 만큼 추가
      ];


2. TopDropdownMenu컴포넌트 Prop에 객체배열 전달

  <TopDropdownMenu dropdownMenus={dropdownMenus}/>
*/

function TopDropdownMenu({dropdownMenus }) {
  const [isOpen, setIsOpen] = useState(false);

  const toggleDropdown = () => {
    setIsOpen(!isOpen);
  };

  return (
    // zIndex:999는 태그 선택 시 최상위로 올려서 선택가능하게함
    <div className="dropdown-menu" style={{ zIndex: 999, display: "flex", top: "0", background: "none", minWidth: "0", boxShadow: "none", position:"inherit"}}>
      <div className="icons">
        <div className="divider" />
        <SlOptionsVertical size="30" onClick={toggleDropdown} />
        </div>
        {isOpen && (
        <ul className="dropdown-list">
          {/* 전달받은 객체배열을 map으로 li생성 */}
          {dropdownMenus.map((option, index) => (
            <li key={index} onClick={option.onClick}>
              {option.label}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

export default TopDropdownMenu;