import React from 'react';

const ChatRoomItem = ({ room }) => {
  return (
    <div>
      <img src={room.image} alt="채팅방 사진" />
      <p>위치: {room.location}</p>
      <p>마지막 채팅 시간: {room.lastChatTime}</p>
      <p>닉네임: {room.nickname}</p>
    </div>
  );
};

export default ChatRoomItem;