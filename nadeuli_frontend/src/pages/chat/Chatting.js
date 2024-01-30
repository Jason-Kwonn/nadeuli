import "../public/css/chatting.css"
import styled from "styled-components";
import moment from 'moment';

import React, { useState, useEffect, useRef } from "react";
// import { changeTradeStateDB } from "../redux/modules/post";

import { useSelector, useDispatch } from "react-redux";
import { useNavigate, useParams } from "react-router-dom";
import { get, post } from "../../util/axios";
import { get as chatGet } from "../../util/chatAxios";
import { AiOutlineArrowLeft } from "react-icons/ai";
import {AiOutlineCaretRight} from "react-icons/ai";
import socket from "../../util/socket";


function Chatting(){
// //   const dispatch = useDispatch();
  const member = useSelector((state) => state.member); // 유저 정보
  const navigate = useNavigate();
  const messageRef = useRef();
  const scrollRef = useRef();
  const params = useParams();
  const roomId = params.chatRoomId;
  const nickname = member.nickname;
// //   const state = params.trade;
// //   const [sellState,setSellState] =useState(state)
  const [title,setTitle] =useState('');
  const [chat,setChat] =useState([]);  // 데이터가 실시간으로 쌓여서 출력하는 스테이트 받아서 뿌리는 건 쳇이야 
  const [price,setPrice] =useState('');
  const [picture,setPicture] =useState('');
  const [isSold,setIsSolde] =useState('');
  const [productId,setProductId] =useState('');
  const [buyerTag,setBuyerTag] =useState('');


  // const handleButtonClick = () => {
  //   // 클릭 시 네비게이션 처리
  //   navigate(`/nadeuliPay/nadeuliPayPay/${productId}`); // 원하는 경로로 수정
  // };

  const handleTradeButtonClick = () => {
    navigate(`/trade/addTradeSchedule/${member.tag}/${buyerTag}/${productId}`); // 원하는 경로로 수정
  };

  const handlePayButtonClick = () => {
    // 클릭 시 네비게이션 처리
    // navigate(`/nadeuliPay/nadeuliPayPay/${productId}`); // 원하는 경로로 수정
  };

  useEffect(()=>{    //데이터 실시간으로 받는 곳 
    socket.on('sendMessage', async ({sender, message, createdAt})=>{
      const res = await get(`/member/getOtherMember/${sender.tag}`);
      const picture = res.picture;
      const addChat = {
        sender : sender,
        message : message,
        picture: picture,
        createdAt : createdAt
      }
      setChat((chat)=> chat.concat(addChat));
      console.log('메세지 받아옴');
    });

    socket.on('connect_error', (error) => {
      console.error('Socket connection error:', error);
    });
  },[])

  useEffect(() => {
      const fetchData = async () => {
        try {
          const result = await chatGet(`/api/chatRoom/${params.chatRoomId}/${member.tag}`);
          socket.emit('joinRoom', {"roomId": roomId});
          if(params.isProduct === '1'){
            console.log('2');
            const res = await get(`/product/getProduct/${params.id}/${member.tag}`);
            setTitle(res.title);
            setPrice(res.price);
            setPicture(res.images);
            setIsSolde(res.isSold);
            setProductId(res.productId);
            console.log(res);
          } else if(params.id !== '0'){
            console.log('1');
            const res = await get(`/orikkiriManage/getOrikkiri/${params.id}`);
            setTitle(res.orikkiriName);
          } else {
            console.log('result는 : ', result?.[0].sender.name);
            setTitle(result?.[0].sender.name);
          }
          const updatedChat = await Promise.all(
            result.map(async (item) => {
                const res = await get(`/member/getOtherMember/${item.sender.tag}`);
                item.picture = res.picture;
                item.nickname = res.nickname;
                return item;
            })
          );
    
          // 모든 비동기 작업이 완료된 후에 상태 업데이트를 수행합니다.
          setChat(result);
          console.log(result);
          console.log(title);
        } catch (error) {
          console.error('채팅방을 불러오는 중 오류가 발생했습니다:', error);
        }
      };
  
    fetchData();
  }, []);

  useEffect(()=>{
    scrollRef.current.scrollTo(0, scrollRef.current.scrollHeight); 
  },[chat]);


  const cleanInput = () => {let input = document.getElementById('inputBox'); input.value="";}
  const handleOnKeyPress = e => {
    if (e.key === 'Enter') {
      handleOnClick();
      cleanInput();
    }
  };

// tag, roomId, message
  const handleOnClick = () =>{
    const message =  messageRef.current.value;
    const tag = member.tag;
    console.log('소켓에 데이터 전송');
    socket.emit('sendMessage', {tag, nickname, roomId, message});
    cleanInput();
 }

return (
    <div className="wrapper">
      
      <div className="userInfo"> 
        <div className="arrow" onClick={()=>{
          navigate(-1);
        }}><AiOutlineArrowLeft /></div>
        <label className="nickname">{title}</label>
      </div>
      
       {params.isProduct === "1" && (<div className="saleList">
        <img src={picture?.[0]}/>
        <span className="productInfo">
          
          <p>{title}</p>
          <p>{Number(price).toLocaleString("ko-KR")}원</p>
          <ButtonContainer>
            <button onClick={handleTradeButtonClick}>
              <SoldOut>거래일정</SoldOut>
            </button>
            <button onClick={handlePayButtonClick}>
              <Book>결제하기</Book>
            </button>
          </ButtonContainer>
          {/* <button onClick={handleButtonClick}>
            {!isSold ? <Book>판매중</Book> : isSold ? <SoldOut>판매완료</SoldOut> : ''}
          </button> */}
        </span>
      </div>
)}
    
      <div className="chattingBody" ref={scrollRef}>
      {chat && chat.map((list, index) => (
        <div key={index}>
          {list.sender.tag === member.tag ?

            <div className="chattingList" style={{flexDirection : 'row-reverse', float:'right'}}>
              {/* <span className="profile">
                <span className="user">{list.nickname}</span>
                <img className="img" src={list.picture} style={list.style} />
              </span> */}

              <span className="message" style={{background :'#508bfc', color:'white'}}>
                {list.message}</span>
              <span className="time">{moment(list.createdAt).format("A h:mm")}</span>

            </div>
            :
            <div className="chattingList">

              <span className="profile">
                <span className="user">{list.sender.name}</span>
                <img className="img" src={list.picture} style={list.style} />
              </span>

              <span className="message" style={{background :'lightgray'}}>{list.message}</span>
              <span className="time">{moment(list.createdAt).format("A h:mm")}</span>

            </div>
          }
          </div>
        ))}

      
      </div>

      <div className="inputArea"> 
        <span>
          <input type="text" className="chattingInput" ref={messageRef} 
                onKeyPress={handleOnKeyPress} id='inputBox'/>
          <AiOutlineCaretRight className="sendButton" onClick={handleOnClick}/>
        </span>
      </div> 
      
    </div>

  );

}

const SoldOut = styled.div`
  
  padding: 6px 5px;
  width: 65px;
  border-radius: 5px;
  background-color: #565656;
  color: white;
  font-size: 12px;
  text-align: center;
`;

const Book = styled(SoldOut)`
  width: 55px;
  background-color: #34bf9e;
`;

const ButtonContainer = styled.div`
  display: flex;
  gap: 10px;
  justify-content: flex-center;
  button {
    border: none;
    background-color: transparent;
  }
`;


export default Chatting;