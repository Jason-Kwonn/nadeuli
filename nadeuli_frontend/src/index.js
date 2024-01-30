import React from 'react';
import ReactDOM from 'react-dom/client';
//redux를통해 전역 상태관리
import { Provider } from 'react-redux';
import App from './App';
//redux의 state를 저장하는 .js import
import store from './redux/configStore';
//redux의 state 영속성관리
import { persistStore } from 'redux-persist';	// 추가
import { PersistGate } from 'redux-persist/integration/react';
import 'mdb-react-ui-kit/dist/css/mdb.min.css';
import "@fortawesome/fontawesome-free/css/all.min.css";


const persistor = persistStore(store)	// 추가
const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  
  //각 컴포넌트에서 const 변수명 = useSelector ((state) => state);
  <Provider store={store}>
    <PersistGate loading={null} persistor={persistor}>
    <App />
    </PersistGate>
  </Provider>

);
