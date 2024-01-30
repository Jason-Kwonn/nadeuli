import axios from 'axios';

const BASE_URL = process.env.REACT_APP_BASE_URL;

const axiosInstance = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Content-Type': 'application/json',
    // 필요한 헤더 추가
  },
});
// Axios 요청 인터셉터를 설정합니다.
axiosInstance.interceptors.request.use(
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



export const get = async (url) => {
  try {
    const response = await axiosInstance.get(url);
    return response.data;
  } catch (error) {
    throw error;
  }
};

export const post = async (url, data) => {
  try {
    const response = await axiosInstance.post(url, data);
    return response.data;
  } catch (error) {
    throw error;
  }
};

export default axiosInstance;