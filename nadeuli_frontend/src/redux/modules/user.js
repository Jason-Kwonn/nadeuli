import { createSlice } from "@reduxjs/toolkit";
import { loadProfile } from "../../shared/axios";

// 회원의 상태를 관리하는 reducer
// 각 컴포넌트에서 reducer에 정의 된 함수를 호출하면된다.
// 각 컴포넌트에서는 dispathch를 통해 호출한다
/*
const dispatch = useDispatch()
<button onClick()={()=>{dispatch({type: 타입명})}}</button>
*/
export const carrotLoginStatus = (status) => {
  return async function (dispatch) {
    dispatch(updateLogin(status));
  }
};

export const getCarrotUserInfo = () => {
  return async function (dispatch) {
    try {
      const response = await loadProfile();
      dispatch(setUser(response.data.user));
    } catch (err) {
      console.log(err);
    }
  }
}

export const backupCarrotUserProfile = (data) => {
  return async function (dispatch) {
    try {
      dispatch(backupUser(data));
      console.log(data);
    } catch (err) {
      console.log(err);
    }
  }
}

//Reducer
const userSlice = createSlice({
  name: "user",
  initialState: {
    isLogin: false,
    nickname: "",
    userLocation: "강남구",
    userImg: "",
    save: {
      nickname: null,
      userLocation: null,
      userImg: null,
    },
  },
  reducers: {
    setUser: (state, action) => {
      state.nickname = action.payload.nickname;
      state.userLocation = action.payload.userLocation;
      state.userImg = action.payload.userImg;

      state.save.nickname = action.payload.nickname;
      state.save.userLocation = action.payload.userLocation;
      state.save.userImg = action.payload.userImg;
    },
    updateLogin: (state, action) => {
      state.isLogin = action.payload;
    },
    backupUser: (state, action) => {
      if (action.payload.nickname) {
        state.save.nickname = action.payload.nickname;
      }
      if (action.payload.userLocation) {
        state.save.userLocation = action.payload.userLocation;
      }
      if (action.payload.userImg) {
        state.save.userImg = action.payload.userImg;
      }
    },
    resetSavedUser: (state, action) => {
      state.save.location = null;
      state.save.image = null;
    }
  },
});

const { updateLogin, setUser, backupUser, resetSavedUser } = userSlice.actions;
export default userSlice.reducer;