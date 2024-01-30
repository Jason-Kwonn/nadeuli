import React, { useState } from 'react';
import "../style/css/topBar.css";
import { ImSearch } from "react-icons/im";
import { useSelector } from 'react-redux';

const Topbar = ({onSearch}) => {
  const location = useSelector((state) => state.member.gu);
  const [isSearchVisible, setSearchVisible] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  
  const handleSearchClick = () => {
    // 검색 입력 필드 확인용
    setSearchVisible(!isSearchVisible);
  };

  const handleInputChange = (e) => {
    const query = e.target.value;
    setSearchQuery(query); // 입력 값 업데이트
    if (e.key === 'Enter') {
    onSearch(query); // 검색 쿼리를 상위 컴포넌트로 전달
    }
  };

  const handleSearch = () => {
    // 검색 기능을 구현합니다.
    console.log('검색어:', searchQuery);
    onSearch(searchQuery);

  };

  const handleKeyPress = (e) => {
    // Enter 키가 눌릴 때 검색을 트리거합니다.
    if (e.key === 'Enter') {
      handleSearch();
    }
  };

  return (
    <div className="TMenuBar">
      <span className="location">{location}</span>
      <div className={`search-container ${isSearchVisible ? 'active' : ''}`}>
        <ImSearch onClick={handleSearchClick} className="search-icon" />
        {isSearchVisible && (
          <input
            type="text"
            className="search-input"
            placeholder="검색어를 입력하세요"
            value={searchQuery}
            onChange={handleInputChange}
            onKeyPress={handleKeyPress}
          />
        )}
      </div>
    </div>
  );
};

export default Topbar;
