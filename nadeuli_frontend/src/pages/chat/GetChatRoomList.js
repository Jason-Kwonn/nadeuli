import React from 'react';
import ChatRoomList from './ChatRoomList';
import BottomBar from '../../components/BottonBar';
import { useState, useEffect } from "react";
import { get, post } from "../../util/axios";
import { get as chatGet } from "../../util/chatAxios";
import { useSelector } from "react-redux";
import socket from "../../util/socket";

const GetChatRoomList = () => {
  const member = useSelector((state) => state.member);
  
  const [chatRooms, setChatRooms] = useState([]);

  const fetchData = async () => {
    try {
      const result = await chatGet(`/api/chatRoom/${member.tag}`);
      const updatedChatRooms = await Promise.all(
        result.map(async (item) => {
          if (item.productId !== 0) {
            const filteredParticipants = item.participants.filter(
              (participant) => participant.tag !== member.tag
            );
            console.log('result',filteredParticipants);
            if (filteredParticipants.length > 0) {
              const res = await get(`/member/getOtherMember/${filteredParticipants[0].tag}`);
              item.picture = res.picture;
              item.roomName = res.nickname;
            } else {
              // 필터된 참가자가 없을 때의 처리
              console.log("No filtered participants found");
            }
          } else if(item.orikkiriId !== 0) {
            const res = await get(`/orikkiriManage/getOrikkiri/${item.orikkiriId}`);
            item.picture = res.orikkiriPicture;
            item.roomName = res.orikkiriName;
          } else {
            const filteredParticipants = item.participants.filter(
              (participant) => participant.tag !== member.tag
            );
            const res = await get(`/member/getOtherMember/${filteredParticipants[0].tag}`);
            item.picture = res.picture;
            item.roomName = res.nickname;
          }
          return item;
        })
      );

      // 모든 비동기 작업이 완료된 후에 상태 업데이트를 수행합니다.
      setChatRooms(updatedChatRooms);
      console.log(updatedChatRooms);
    } catch (error) {
      console.error('채팅방을 불러오는 중 오류가 발생했습니다:', error);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);



  return (
    <div style={{ backgroundColor: '#87CEEB' }}>

      
      <ChatRoomList chatRooms={chatRooms} />
      <BottomBar selected='chat'/>
    </div>
  );
};

export default GetChatRoomList;