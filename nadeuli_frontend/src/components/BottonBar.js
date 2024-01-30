import React, { useState } from 'react';
import { AiFillHome } from 'react-icons/ai';
import { BiUser } from 'react-icons/bi';
import { useNavigate } from 'react-router-dom';
import styled from 'styled-components';
import { IoChatbubblesSharp } from "react-icons/io5";
import { MdDeliveryDining } from "react-icons/md";
import { HiClipboardDocumentList } from "react-icons/hi2";

const BottomBar = (props) => {
  
  const navigate = useNavigate();
  const [selectedMenu, setSelectedMenu] = useState(props.selected || 'main');

  const handleMenuClick = (menu) => {
    navigate(`/${menu}`);
    setSelectedMenu(menu);
  };

  return (
    <BottomBarContainer>
      <MenuBox
        onClick={() => handleMenuClick('main')}
        selected={selectedMenu === 'main'}
      >
        <AiFillHome size="30px" color={selectedMenu === 'main' ? '#508BFC' : 'black'} />
        <p style={{ color: selectedMenu === 'main' ? '#508BFC' : 'black', fontSize: '12px' }}>HOME</p>
      </MenuBox>
      <MenuBox
        onClick={() => handleMenuClick('dongNeHome')}
        selected={selectedMenu === 'dongNeHome'}
      >
        <HiClipboardDocumentList size="30px" color={selectedMenu === 'dongNeHome' ? '#508BFC' : 'black'} />
        <p style={{ color: selectedMenu === 'dongNeHome' ? '#508BFC' : 'black', fontSize: '12px' }}>동네나드리</p>
      </MenuBox>
      <MenuBox
        onClick={() => handleMenuClick('nadeuliDeliveryHome')}
        selected={selectedMenu === 'nadeuliDeliveryHome'}
      >
        <MdDeliveryDining size="30px" color={selectedMenu === 'nadeuliDeliveryHome' ? '#508BFC' : 'black'} />
        <p style={{ color: selectedMenu === 'nadeuliDeliveryHome' ? '#508BFC' : 'black', fontSize: '12px' }}>나드리부름</p>
      </MenuBox>
      <MenuBox
        onClick={() => handleMenuClick('chat/getChatRoomList')}
        selected={selectedMenu === 'chat'}
      >
        <IoChatbubblesSharp size="30px" color={selectedMenu === 'chat' ? '#508BFC' : 'black'} />
        <p style={{ color: selectedMenu === 'chat' ? '#508BFC' : 'black', fontSize: '12px' }}>채팅</p>
      </MenuBox>

      <MenuBox
        onClick={() => handleMenuClick('getMyProfile')}
        selected={selectedMenu === 'getMyProfile'}
      >
        <BiUser size="30px" color={selectedMenu === 'getMyProfile' ? '#508BFC' : 'black'} />
        <p style={{ color: selectedMenu === 'getMyProfile' ? '#508BFC' : 'black', fontSize: '12px' }}>내 나드리</p>
      </MenuBox>
    </BottomBarContainer>
  );
};

const BottomBarContainer = styled.div`
  background-color: #f8f8f8;
  width: 100%;
  position: fixed;
  bottom: 0;
  display: flex;
  justify-content: space-around;
  align-items: center;
  padding: 10px 0;
`;

const MenuBox = styled.div`
  text-align: center;
  cursor: pointer;
  ${(props) => props.selected && `color: #e78111;`}
`;

export default BottomBar;
