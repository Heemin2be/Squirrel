import axios from 'axios';

const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api', // 백엔드 API의 기본 URL
  headers: {
    'Content-Type': 'application/json',
  },
});

// 요청 인터셉터: 모든 요청에 JWT 토큰을 추가하는 로직 (나중에 구현)
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default apiClient;
