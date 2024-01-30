import { createSlice } from '@reduxjs/toolkit';
import { get, post } from "../../util/axios";
import axios from 'axios';

const BASE_URL = process.env.REACT_APP_BASE_URL;

// 우리끼리 생성
export const createOrikkiri = (formData, navigate) => {
    return async function (dispatch) {
      try {
        const res = await axios.post(`${BASE_URL}/orikkiriManage/addOrikkiri`, formData, {
          headers: {
            'Content-Type': 'multipart/form-data',
          },
        });
        // console.log(res.orikkiriId)
        dispatch(addOrikkiri(formData));
      navigate(`/orikkiriHome/${res.orikkiriId}`);
      } catch (err) {
        console.log(err);
      }
    };
  };


// 우리끼리 수정
export const modifyOrikkiri = (formData, navigate) => {
  return async function (dispatch) {
    try {
      const res = await axios.orikkiri(`${BASE_URL}/orikkiriManage/addOrikkiri`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      // console.log(res)
      dispatch(updateOrikkiri(formData));
    navigate("/main");
    } catch (err) {
      console.log(err);
    }
  };
};


// 우리끼리 상세조회
export const getOrikkiriDetail = (orikkiriId) => {
  return async function (dispatch, getState) {
    await get(`/orikkiriManage/getOrikkiri/${orikkiriId}`)
      .then((res) => {
        // console.log(res.orikkiriId);
        dispatch(getLoadOrikkiri(res));
        const currentState = getState();
        // console.log('get이후 Current state:', currentState);
      })
      .catch((err) => {
        console.log(err);
      });
  };
};




// 동네나드리 홈 우리끼리 목록 조회
export const getOrikkiriList= (tag, currentPage) => {
    return async function (dispatch, getState) {
      await get(`/orikkiri/getMyOrikkiriList/${tag}/0`)
        .then((res) => {
          // console.log(res);
          dispatch(loadMyOrikkiris(res));
          const currentState = getState();
          // console.log('Current state:', currentState);
        })
        .catch((err) => {
          console.log(err);
        });
    };
  };

  // 모든 우리끼리 조회
  export const allOrikkiriList= () => {
    return async function (dispatch, getState) {
      await get(`/orikkiriManage/allOrikkiri`)
        .then((res) => {
          // console.log(res);
          dispatch(loadOrikkiris(res));
          const currentState = getState();
          // console.log('Current state:', currentState);
        })
        .catch((err) => {
          console.log(err);
        });
    };
  };


//우리끼리 삭제
export const deleteOrikkiri = (orikkiriId, navigate) => {
  return async function (dispatch) {
    console.log('우리끼리 삭제 실행 중');
    await get(`/orikkiriManage/deleteOrikkiri/${orikkiriId}`)
      .then((res) => {
        dispatch(deletOrikkiri(res.data));
        navigate("/dongNeHome");
      })
      .catch((err) => {
        console.log(err);
      });
  };
};

export const getOrikkiriMemberList= (orikkiriId) => {
  return async function (dispatch, getState) {
    await get(`/orikkiri/getOrikkiriMemberList/${orikkiriId}`)
      .then((res) => {
        // console.log(res);
        dispatch(loadMyOrikkiriMembers(res));
        const currentState = getState();
        // console.log('Current state:', currentState);
      })
      .catch((err) => {
        console.log(err);
      });
  };
};

const orikkiriSlice = createSlice({
  name: "orikkiri",
  initialState: {
    orikkiriMemberList: [],
    orikkiriMember: {},
    myOrikkiriList: [],
    orikkiriList: [],
    orikkiri: {},
  },
  reducers: {
    addOrikkiri: (state, action) => {
      state.orikkiriList.push(action.payload);
    },
    addMyOrikkiri: (state, action) => {
      state.myOrikkiriList.push(action.payload);
    },
    getLoadOrikkiri: (state, action) => {
      state.orikkiri = action.payload;
    },
    loadOrikkiris: (state, action) => {
      state.orikkiriList = action.payload;
    },
    loadMyOrikkiris: (state, action) => {
      state.myOrikkiriList = action.payload;
    },
    loadMyOrikkiriMembers: (state, action) => {
      state.orikkiriMemberList = action.payload;
    },
    updateOrikkiri: (state, action) => {
      state.orikkiri = action.payload;
    },
    deletOrikkiri: (state, action) => {
      state.orikkiriList = state.orikkiriList.filter((orikkiri) => orikkiri.orikkiriId != action.payload);
      state.myOrikkiriList = state.myOrikkiriList.filter((orikkiri) => orikkiri.orikkiriId != action.payload)
    }

  },
});

const { addOrikkiri, addMyOrikkiri, getLoadOrikkiri, loadOrikkiris, loadMyOrikkiriMembers, loadMyOrikkiris, updateOrikkiri, deletOrikkiri } =
orikkiriSlice.actions;
export default orikkiriSlice.reducer;
