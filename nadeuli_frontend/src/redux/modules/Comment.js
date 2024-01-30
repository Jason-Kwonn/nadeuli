import { createSlice } from '@reduxjs/toolkit';
import axiosInstance from '../../util/axios';
import axios from 'axios';

const BASE_URL = process.env.REACT_APP_BASE_URL;

// 게시물 댓글 생성
export const postComment = (commentData, postId) => {
  return async function (dispatch) {
    try {
      const res = await axios.post(`${BASE_URL}/dongNe/addComment`, commentData);
      dispatch(addComment(res.data)); // 여기서 res.data를 사용해서 새 댓글을 추가합니다.
      // dispatch(GetDongNeCommentList(postId));
    } catch (error) {
      // 에러 처리 로직 추가
    }
  };
};

// 게시물 댓글 리드
export const GetDongNeCommentList = (postId) => {
  return async function (dispatch) {
    try {
      const res = await axiosInstance.get(`/dongNe/getCommentList/${postId}`);
      dispatch(loadComments(res.data));
      // console.log(res.data);
      console.log("getCommentList:" + postId)
    } catch (error) {
      // 에러 처리 로직 추가
    }
  };
};

// 게시물 댓글 수정
export const modifyDongNeComment = (formData, navigate) => {
  return async function (dispatch) {
    try {
      const res = await axios.post(`${BASE_URL}/dongNe/updateComment`, formData);

      console.log(res)
      dispatch(updateComment(res.data)); // 여기서 res.data를 사용해서 수정된 댓글을 업데이트합니다.
      navigate("/main");
    } catch (err) {
      // 에러 처리 로직 추가
    }
  };
};

// 게시물 댓글 삭제
export const removeDongNeComment = (commentId, navigate) => {
  return async function (dispatch) {
    try {
      console.log('동네 게시물 삭제 실행 중');
      await axios.get(`/dongNe/deleteComment/${commentId}`)
        .then((res) => {
          dispatch(deleteComment(commentId)); // 여기서 commentId를 사용해서 댓글을 삭제합니다.
          navigate("/dongNeHome");
        })
        .catch((err) => {
          // 에러 처리 로직 추가
        });
    } catch (error) {
      // 에러 처리 로직 추가
    }
  };
};

// 리덕스 슬라이스
const commentSlice = createSlice({
  name: 'comment',
  initialState: {
    commentList: [],
    comment: {},
  },
  reducers: {
    addComment: (state, action) => {
      state.commentList.push(action.payload);
    },
    loadComments: (state, action) => {
      state.commentList = action.payload;
    },
    updateComment: (state, action) => {
      // 수정된 댓글을 업데이트하는 로직 추가
      const updatedComment = action.payload;
      const index = state.commentList.findIndex((comment) => comment.id === updatedComment.id);
      if (index !== -1) {
        state.commentList[index] = updatedComment;
      }
    },
    deleteComment: (state, action) => {
      // 특정 댓글을 삭제하는 로직 추가
      const commentId = action.payload;
      state.commentList = state.commentList.filter((comment) => comment.id !== commentId);
    },
  },
});

export const { addComment, loadComments, updateComment, deleteComment } = commentSlice.actions;

export default commentSlice.reducer;
