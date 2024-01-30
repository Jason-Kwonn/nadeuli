import { createSlice } from '@reduxjs/toolkit';
import { get, post } from "../../util/axios";
import axios from 'axios';

const BASE_URL = process.env.REACT_APP_BASE_URL;

// 스트리밍 채널 생성
export const createliveChannel= (formData, navigate) => {
  return async function (dispatch) {
    try {
      const res = await axios.post(`${BASE_URL}/dongNe/addChannel`, formData); // 헤더 제거
      console.log(res.data); 
      dispatch(addChannel(res.data));
    } catch (err) {
      console.log(err);
      }
    }
  };

// 스트리밍 채널 조회
export const getChannelDetail = (channelId) => {
  return async function (dispatch, getState) {
    await get(`/dongNe/getChannel/${channelId}`)
    .then((res) => {
      console.log(res);
      dispatch(getChannel(res));
      const currentState = getState();
      console.log('get이후 Current state:', currentState);
    })
    .catch((err) => {
      console.log(err);
    });
};
};  


// // 스트리밍 리스트 조회
// export const channelList = (channelId) => {
//   return async function (dispatch, getState) {
//       await get(`/dongNe/getChannel/${channelId}`)
//       .then((res) => {
//         console.log(res);
//         dispatch(getChannelList(res));
//         const currentState = getState();
//         console.log('Current state:', currentState);
//       })
//       .catch((err) => {
//         console.log(err);
//       });
//   };
// };

// 스트리밍 채널 삭제
export const removeChannel = (postId, navigate) => {
  return async function (dispatch) {
    console.log('동네 게시물 삭제 실행 중');
    await axios.get(`${BASE_URL}/dongNe/deleteChannel/${postId}`)
      .then((res) => {
        dispatch(deleteChannel(res.data));
        navigate("/dongNeHome");
      })
      .catch((err) => {
        console.log(err);
      });
  };
};


// 리덕스슬라이스
const liveChannelSlice = createSlice({
  name: 'channel',
  initialState: {
    channelList: [],
    channel: {},
  },
  reducers: {
    addChannel: (state, action) => {
      state.channel = action.payload;
      // state.channelList.push(action.payload);
    },
    getChannelList: (state, action) => {
      // action.payload를 통해 서버로부터 받은 게시물 목록을 저장할 수 있음
      state.channelList = action.payload;
    },
    getChannel: (state, action) => {
      state.channel = action.payload;
    },
    deleteChannel: (state, action) => {
      // action.payload로 특정 게시물을 삭제할 수 있음
      state.channelList = state.streamingList.filter((channel) => channel.id !== action.payload);
    },
  },
});

export const { addChannel, getChannelList, getChannel, deleteChannel } = liveChannelSlice.actions;

export default liveChannelSlice.reducer;
