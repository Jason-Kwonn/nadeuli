import { createSlice } from '@reduxjs/toolkit';

// 회원의 상태를 관리하는 reducer
// 각 컴포넌트에서 reducer에 정의 된 함수를 호출하면된다.
// 각 컴포넌트에서는 dispathch를 통해 호출한다
/*
const dispatch = useDispatch()
<button onClick()={()=>{dispatch({type: 타입명})}}</button>
*/

// 새로운 액션 및 리듀서 함수 추가
export const setOtherMember = (data) => {
    return async function (dispatch, getState) {
        try {
            console.log('data는', data);
            dispatch(setOtherMemberState(data));
            const currentState = getState();
            console.log('get 이후 Current state:', currentState);
        } catch (err) {
            console.log(err);
        }
    };
};


//Reducer
//createSlice는 리듀서 함수와 액션 생성자를 함께 생성하는 유틸리티 함수
const otherMemberSlice = createSlice({
    // createSlice 함수에서 name 속성은 리듀서의 이름을 지정
    // createSlice로 생성된 객체는 리듀서와 액션을 모두 포함하며 그 객체를 슬라이스 라고한다.
    // 슬라이스는 일종의 모듈화된 Redux 리듀서와 액션을 담고 있는 단위
    // 각각의 리듀서 함수는 reducers 객체 내에 정의된다

    //1. 슬라이스의 이름을 정의한다.
    name: 'otherMember',
    //2. 초기상태를 정의함으로써 현재상태를 set
    initialState: {
        tag: 'Bss2',
        cellphone: '010-1111-1111',
        nickname: '독감환자',
        affinity: '0',
        email: 'guest@gmail.com',
        dongNe: '서울특별시 강서구 공항동',
        picture: 'https://kr.object.ncloudstorage.com/nadeuli/image/a20231212100248393.png',
        nadeuliPayBalance: 100,
        isActivate: false,
        isNadeuliDelivery: false,
        role: null,
        gu: '강서구',
        bankName: '국민은행',
        bankAccountNum: '41191025584607',
        blockReason: '',
        blockEnd: null,
        blockDay: null,
        socialId: '',
    },
    //3. 리듀서 함수의 첫 번째 매개변수 state는 현재의 상태를 나타내며,
    //   두 번째 매개변수 action은 디스패치된 액션 객체
    reducers: {
        setOtherMemberState: (state, action) => {
            const {
                nickname,
                tag,
                cellphone,
                affinity,
                email,
                dongNe,
                picture,
                nadeuliPayBalance,
                isActivate,
                isNadeuliDelivery,
                role,
                gu,
                bankName,
                bankAccountNum,
                blockReason,
                blockEnd,
                blockDay,
                socialId,
            } = action.payload;

            state.tag = tag;
            state.cellphone = cellphone;
            state.nickname = nickname;
            state.affinity = affinity;
            state.email = email;
            state.dongNe = dongNe;
            state.picture = picture;
            state.nadeuliPayBalance = nadeuliPayBalance;
            state.isActivate = isActivate;
            state.isNadeuliDelivery = isNadeuliDelivery;
            state.role = role;
            state.gu = gu;
            state.bankName = bankName;
            state.bankAccountNum = bankAccountNum;
            state.blockReason = blockReason;
            state.blockEnd = blockEnd;
            state.blockDay = blockDay;
            state.socialId = socialId;
        },
    },
});

const { setOtherMemberState, } = otherMemberSlice.actions;
export default otherMemberSlice.reducer;
