import axios from 'axios';
import member from '../redux/modules/member';

// 환경 변수에서 기본 URL을 가져옵니다.
const BASE_URL = process.env.REACT_APP_BASE_URL;

// Axios의 인스턴스를 생성합니다.
export const instance = axios.create({
    baseURL: BASE_URL,
});

// Axios 요청 인터셉터를 설정합니다.
instance.interceptors.request.use(
    // 요청 전달되기 전 작업을 처리합니다.
    (config) => {
        // 토큰을 가져옵니다.
        const token = localStorage.getItem('token');
        console.log('로컬스토리지에서 토큰을 가져옵니다', token);

        // 토큰이 존재하면 요청 헤더에 Authorization 헤더를 추가하여 토큰을 전송합니다.
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
            console.log('react에서 spring으로 토큰을 헤더에 담아서 보냈습니다.');
            console.log(config);
        }

        // 처리된 config를 반환합니다.
        return config;
    },
    // 요청 에러가 있는 작업을 처리합니다.
    (error) => {
        // 에러가 발생하면 콘솔에 에러 로그를 출력합니다.
        console.error(error);

        // 에러를 Promise를 통해 다음 핸들러로 전파합니다.
        return Promise.reject(error);
    }
);

// 사용자 로그인 요청을 처리하는 함수
export const login = async (memberDTO) => {
    console.log(memberDTO);
    return await instance.post('/login', { memberDTO });
};

//사용자 로그아웃
export const logout = async (memberDTO) => {
    console.log(memberDTO);
    return await instance.post('/logout', { memberDTO });
};

// 사용자 회원가입 요청을 처리하는 함수
export const addMember = async (memberDTO, gpsDTO) => {
    return await instance.post('/addMember', { memberDTO, gpsDTO });
};

// 현재 로그인한 사용자의 프로필 정보를 불러오는 함수
export const getMember = async (tag) => {
    return await instance.get(`/member/getMember/${tag}`);
};

// 현재 로그인한 사용자의 프로필 정보를 불러오는 함수
export const getOtherMember = async (tag) => {
    return await instance.get(`/member/getOtherMember/${tag}`);
};

// 휴대폰 번호로 인증번호를 요청하는 함수
export const getAuthNumCellphone = async (to) => {
    return await instance.post(`/auth/sendSms`, { to });
};

// 이메일로 인증번호를 요청하는 함수
export const getAuthNumEmail = async (to) => {
    return await instance.post(`/auth/sendMail`, { to });
};

// 인증번호를 확인하는 함수
export const checkAuthNum = async (data) => {
    return await instance.post(`/auth/verifyNum`, data);
};

// 사용자 정보를 업데이트하는 함수
export const updateMember = async (memberDTO) => {
    return await instance.post(`/member/updateMember`, memberDTO);
};

// 회원의 이메일을 체크하는 함수
export const findAccount = async (memberDTO) => {
    return await instance.post('/findAccount', memberDTO);
};

// 계정찾기에서 휴대폰번호를 변경하는 함수
export const updateCellphone = async (memberDTO) => {
    return await instance.post('/updateCellphone', memberDTO);
};

//동네 수정 함수
export const updateDongNe = async (memberDTO, gpsDTO) => {
    return await instance.post(`/member/updateDongNe`, { memberDTO, gpsDTO });
};

//소셜로그인 닉네임 등록 함수
export const addNickname = async (tokenDTO, gpsDTO, memberDTO) => {
    return await instance.post(`/member/addNickname`, { tokenDTO, gpsDTO, memberDTO });
};

//소셜로그인 회원 정보를 가져오는 함수
export const getSocialMember = async (tokenDTO) => {
    return await instance.post(`/member/getSocialMember`, { tokenDTO });
};

// 현재 로그인한 사용자의 프로필 정보를 불러오는 함수
export const getMemberList = async (searchDTO) => {
    return await instance.post(`/member/getMemberList`, { searchDTO });
};

// 현재 로그인한 사용자의 프로필 정보를 불러오는 함수
export const addFavorite = async (tag, prodId) => {
    return await instance.get(`/member/addFavorite/${tag}/${prodId}`);
};

export const deleteFavorite = async (tag, prodId) => {
    return await instance.get(`/member/deleteFavorite/${tag}/${prodId}`);
};

// 현재 로그인한 사용자의 즐겨찾기 정보를 불러오는 함수
export const getMemberFavoriteList = async (memberDTO, searchDTO) => {
    return await instance.post(`/member/getFavoriteList`, { memberDTO, searchDTO });
};

// 회원 비활성화/활성화 함수
export const handleMemberActivate = async (tag) => {
    return await instance.get(`/member/handleMemberActivate/${tag}`);
};

// 회원 정지 함수
export const addBlockMember = async (memberDTO, blockDTO) => {
    return await instance.post(`/member/addBlockMember`,{memberDTO, blockDTO});
};

// 회원 정지해제 함수
export const deleteBlockMember = async (tag) => {
    return await instance.get(`/member/deleteBlockMember/${tag}`);
};
