import { createSlice } from "@reduxjs/toolkit";
import { get, post } from "../../util/axios";
import axios from 'axios';
import { addFavorite, deleteFavorite } from '../../shared/axios';

// 찜하기
export const postLike = (tag, postId, likeNum) => {
  return function (dispatch, getState) {
    addFavorite(tag, postId)
      .then((res) => {
        dispatch(setLike({ isLike: true, likeNum: likeNum+1 }));
        const currentState = getState();
        console.log('찜이후 Current state:', currentState);
      })
      .catch((err) => {
        console.log(err);
      });
  };
};

// 찜하기 취소
export const postUnLike = (tag, postId, likeNum) => {
  return function (dispatch, getState) {
    deleteFavorite(tag, postId)
      .then((res) => {
        dispatch(setLike({ isLike: false, likeNum: likeNum-1 }));
        const currentState = getState();
        console.log('찜 해제 이후 Current state:', currentState);
      })
      .catch((err) => {
        console.log(err);
      });
  };
};

// 게시물 등록
export const carrotPost = (formData, navigate) => {
  return async function (dispatch) {
    try {
      const apiUrl = process.env.REACT_APP_BASE_URL
      const res = await axios.post(`${apiUrl}/product/addProduct`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      console.log(res)
      dispatch(uploadPost(formData));
    navigate("/main");
    } catch (err) {
      console.log(err);
    }
  };
};

// 게시물 수정
export const modifyPost = (formData, navigate) => {
  return async function (dispatch) {
    try {
      const apiUrl = process.env.REACT_APP_BASE_URL
      const res = await axios.post(`${apiUrl}/product/updateProduct`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      console.log(res)
      // dispatch(getLoadPost(formData));
    navigate("/main");
    } catch (err) {
      console.log(err);
    }
  };
};

//게시물 삭제
export const deletePost = (postId, navigate) => {
  return async function (dispatch) {
    console.log('딜릿 실행됨');
    await get(`/product/deleteProduct/${postId}`)
      .then((re) => {
        // dispatch(roadPosts(re.data));
        navigate("/main");
      })
      .catch((err) => {
        console.log(err);
      });
  };
};

// 게시물 상세 조회
export const carrotGetPost = (postId, tag) => {
  return async function (dispatch, getState) {
    await get(`/product/getProduct/${postId}/${tag}`)
      .then((res) => {
        console.log(res);
        dispatch(getLoadPost(res));
        const currentState = getState();
        console.log('get이후 Current state:', currentState);
      })
      .catch((err) => {
        console.log(err);
      });
  };
};

// 메인화면 포스트 리드
export const loadMainposts = (dongNe) => {
  return async function (dispatch, getState) {
    await get(`/product/home/0/${dongNe}`)
      .then((res) => {
        console.log(res);
        dispatch(roadPosts(res));
        const currentState = getState();
        console.log('Current state:', currentState);
      })
      .catch((err) => {
        console.log(err);
      });
  };
};

// 판매목록 리드
export const loadSalesposts = (tag, type) => {
  return async function (dispatch, getState) {
    const url = `/product/getMyProductList/${tag}/0${type ? `?type=${type}` : ''}`;
    await get(url)
      .then((res) => {
        console.log(res)
        dispatch(roadPosts(res));
        const currentState = getState();
        console.log('Current state:', currentState);
      })
      .catch((err) => {
        console.log("판매목록" + err);
      });
  };
};

// // 관심목록 리드
// export const loadConcernsposts = () => {
//   return async function (dispatch) {
//     await instance
//       .get("/api/user/likeList")
//       .then((re) => {
//         console.log(re);
//         dispatch(roadPosts(re.data.likeList));
//       })
//       .catch((err) => {
//         console.log("관심목록" + err);
//       });
//   };
// };

// export const changeTradeStateDB = (postId, state) => {
//   return async function (dispatch) {
//     const response = await instance.patch(`/api/post/tradeState/${postId}`, {
//       tradeState: state,
//     });
//     console.log(response);
//     dispatch(changeTradeState({ id: postId, tradeState: state }));
//   };
// };

const postSlice = createSlice({
  name: "post",
  initialState: {
    postList: [],
    post: {},
  },
  reducers: {
    uploadPost: (state, action) => {
      state.postList.push(action.payload);
    },
    getLoadPost: (state, action) => {
      state.post = action.payload;
    },
    roadPosts: (state, action) => {
      state.postList = action.payload;
    },
    setLike: (state, action) => {
      state.post.likeNum = action.payload.likeNum;
      state.post.isLike = action.payload.isLike;
    },
    changeTradeState: (state, action) => {
      state.postList = state.postList.map((post) => {
        if (post.postId === action.payload.id) {
          post.tradeState = action.payload.tradeState;
        }
        return post;
      });
    },
  },
});

const { uploadPost, getLoadPost, roadPosts, changeTradeState, setLike } =
  postSlice.actions;
export default postSlice.reducer;
