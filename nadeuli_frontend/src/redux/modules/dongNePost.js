import { createSlice } from '@reduxjs/toolkit';
import axiosInstance from '../../util/axios';
import axios from 'axios';

const BASE_URL = process.env.REACT_APP_BASE_URL;

// 게시물 등록
export const dongNePost = (formData, navigate, redirectPath) => {
  return async function (dispatch) {
    try {
      const res = await axios.post(`${BASE_URL}/dongNe/addPost`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        },
      });
      const postId = res.data.postId;
      // console.log(postId); 
      dispatch(addDongNePost(formData));
      navigate(redirectPath);
      // navigate('/getDongNePost');
    } catch (err) {
      console.log(err);
    }
  };
};


// 게시물 수정
export const modifyDongNePost = (formData, navigate, redirectPath) => {
  return async function (dispatch) {
    try {
      const res = await axios.post(`${BASE_URL}/dongNe/updatePost`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      // console.log(res)
      dispatch(updateDongNePost(formData));
    navigate(redirectPath);
    } catch (err) {
      console.log(err);
    }
  };
};

// 게시물 상세 조회
export const GetDongNePostDetail = (postId) => {
  return async function (dispatch) {
    try {
      const res = await axiosInstance.get(`/dongNe/getPost/${postId}`);
      // console.log(res.data);
      dispatch(getLoadDongNePost(res.data));
    } catch (err) {
      console.log(err);
    }
  };
};


// 홈 게시물 목록 리드
export const GetDongNePostList= (currentPage, gu, searchKeyword) => {
  return async function (dispatch) {
    try {
      const res = await axiosInstance.get(`/dongNe/dongNeHome/${currentPage}?gu=${gu}&searchKeyword=${searchKeyword}`);
      dispatch(loadDongNePosts(res.data));
      // console.log(res.data);
      // console.log("searchKeyword:" + searchKeyword)
    } catch (error) {
      console.log(error);
    }
  };
};


// 동네 게시물 삭제
export const removeDongNePost = (postId, navigate) => {
  return async function (dispatch) {
    console.log('동네 게시물 삭제 실행 중');
    await axios.get(`${BASE_URL}/dongNe/deletePost/${postId}`)
      .then((res) => {
        dispatch(deleteDongNePost(res.data));
      })
      .catch((err) => {
        console.log(err);
      });
  };
};


// 리덕스슬라이스
const dongNePostSlice = createSlice({
  name: 'dongNePost',
  initialState: {
    dongNePostList: [],
    dongNePost: {},
  },
  reducers: {
    addDongNePost: (state, action) => {
      // action.payload로 새로운 게시물을 추가할 수 있음
      state.dongNePostList.push(action.payload);
    },
    loadDongNePosts: (state, action) => {
      // action.payload를 통해 서버로부터 받은 게시물 목록을 저장할 수 있음
      state.dongNePostList = action.payload;
    },
    getLoadDongNePost: (state, action) => {
      state.dongNePost = action.payload;
    },
    // getLoadDongNePost: (state, action) => {
    //   const {
    //   postId,
    //   title,
    //   content,
    //   video,
    //   streaming,
    //   orikkiriId,
    //   orikkiriName,
    //   orikkiriPicture,
    //   postCategory,
    //   gu,
    //   dongNe,
    //   timeAgo,
    //   writer:{
    //     tag,
    //     picture,
    //     nickname,
    //     dongNe: writerDongNe
    //   },
    //   images
    //   } = action.payload;

    //   state.dongNePost.postId = postId;
    //   state.dongNePost.title = title;
    //   state.dongNePost.content = content;
    //   state.dongNePost.video = video;
    //   state.dongNePost.streaming = streaming;
    //   state.dongNePost.orikkiriId = orikkiriId;
    //   state.dongNePost.orikkiriName = orikkiriName;
    //   state.dongNePost.orikkiriPicture = orikkiriPicture;
    //   state.dongNePost.postCategory = postCategory;
    //   state.dongNePost.gu = gu;
    //   state.dongNePost.dongNe = dongNe;
    //   state.dongNePost.timeAgo = timeAgo;
    //   state.dongNePost.writerTag = tag;
    //   state.dongNePost.writerPicture = picture;
    //   state.dongNePost.writerNickname = nickname;
    //   state.dongNePost.writerDongNe = writerDongNe;
    //   state.dongNePost.images = images;
    // },
    updateDongNePost: (state, action) => {
      state.dongNePost = action.payload;
    },
    deleteDongNePost: (state, action) => {
      // action.payload로 특정 게시물을 삭제할 수 있음
      state.dongNePostList = state.dongNePostList.filter((post) => post.id !== action.payload);
    },
  },
});

export const { addDongNePost, loadDongNePosts,  getLoadDongNePost, updateDongNePost, deleteDongNePost } = dongNePostSlice.actions;

export default dongNePostSlice.reducer;
