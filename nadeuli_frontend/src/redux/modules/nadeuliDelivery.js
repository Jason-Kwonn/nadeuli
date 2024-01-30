import { createSlice } from "@reduxjs/toolkit";

//Reducer
//createSlice는 리듀서 함수와 액션 생성자를 함께 생성하는 유틸리티 함수
const nadeuliDeliverySlice = createSlice({
  // createSlice 함수에서 name 속성은 리듀서의 이름을 지정
  // createSlice로 생성된 객체는 리듀서와 액션을 모두 포함하며 그 객체를 슬라이스 라고한다.
  // 슬라이스는 일종의 모듈화된 Redux 리듀서와 액션을 담고 있는 단위
  // 각각의 리듀서 함수는 reducers 객체 내에 정의된다

  //1. 슬라이스의 이름을 정의한다.
  name: "nadeuliDelivery",
  //2. 초기상태를 정의함으로써 현재상태를 set
  initialState: {
    nadeuliDeliveryList: [],
    files: [],
    deliveryLocations: [], // departure와 arrival을 포함하는 객체들의 배열
  },
  //3. 리듀서 함수의 첫 번째 매개변수 state는 현재의 상태를 나타내며,
  //   두 번째 매개변수 action은 디스패치된 액션 객체
  reducers: {
    setNadeuliDeliveryList: (state, action) => {
      state.nadeuliDeliveryList = action.payload.nadeuliDeliveryList;
    },
    setFiles: (state, action) => {
      state.files = action.payload.files;
    },
    setDeliveryLocations: (state, action) => {
      state.deliveryLocations = action.payload.deliveryLocations;
    },
  },
});

export const { setFiles, setDeliveryLocations, setNadeuliDeliveryList } =
  nadeuliDeliverySlice.actions;
export default nadeuliDeliverySlice.reducer;
