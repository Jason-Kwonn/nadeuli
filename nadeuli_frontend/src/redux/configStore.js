import { combineReducers, configureStore } from "@reduxjs/toolkit";
import { persistReducer } from "redux-persist"; // 추가
import storage from "redux-persist/lib/storage"; // 추가
import post from "./modules/post";
import member from "./modules/member";
import dongNePost from "./modules/dongNePost";
import orikkiri from "./modules/orikkiri";
import otherMember from "./modules/otherMember";
import nadeuliDelivery from "./modules/nadeuliDelivery";
import comment from "./modules/Comment";
import streaming from "./modules/streaming";

/*
Redux는 상태 관리 라이브러리로, 
애플리케이션의 상태를 효과적으로 관리하기 위해 사용된다. 
Reducer는 이 상태 변화를 어떻게 다룰지 정의하는 함수다.

Reducer 함수는 '현재 상태'와 '액션'을 전달받아서 새로운 상태를 반환한다.
이때, 리듀서는 이전 상태를 직접 변경하지 않고 복사하여 새로운 상태를 생성한다.

*/

const persistConfig = {
  key: "root",
  storage,
  whitelist: [
    "member",
    "dongNePost",
    "post",
    "orikkiri",
    "otherMember",
    "nadeuliDelivery",
  ], //******추가할 리듀서들을 옆에 나열******
  whitelist: ["member","dongNePost" ,"comment", "streaming", "post", "orikkiri"]//******추가할 리듀서들을 옆에 나열******
};

// 리듀서들을 바로 configureStore에서 합치기
const rootReducer = combineReducers({
  member,
  post,
  dongNePost,
  orikkiri,
  otherMember,
  nadeuliDelivery, //******추가할 리듀서들을 밑에 나열******
  comment,
  streaming,
  orikkiri,//******추가할 리듀서들을 밑에 나열******
});

//Redux 스토어
const store = configureStore({
  //Redux 스토어에 등록할 Reducer들을 지정한다.
  //user와 post는 각각 다른 Reducer다.
  //Reducer는 상태 변화를 다루는 함수로,
  //여러 개의 Reducer를 합쳐 루트 Reducer를 만들 수 있다.
  reducer: persistReducer(persistConfig, rootReducer),

  middleware:getDefaultMiddleware => getDefaultMiddleware({serializableCheck:false})
  });


//반환된 스토어를 내보내어 다른 파일에서 사용할 수 있게 한다.
//이렇게 생성된 스토어는 Redux의 상태를 관리하고
//액션을 처리하는 중앙 허브 역할을 한다.
// 스토어 내보내기
export default store;
