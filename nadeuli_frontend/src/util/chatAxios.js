import axios from 'axios';

const REACT_APP_CHAT_APP_BASE_URL = process.env.REACT_APP_CHAT_APP_BASE_URL;

const axiosInstance = axios.create({
  baseURL: REACT_APP_CHAT_APP_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
    // 필요한 헤더 추가
  },
});

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