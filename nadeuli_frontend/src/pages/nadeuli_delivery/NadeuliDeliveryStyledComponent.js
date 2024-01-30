import styled from "styled-components";

export const StyledContainer = styled.div`
  padding: 1.3rem;
  border-radius: 8px;
  align-items: center;
`;

export const CardBox = styled.div`
  display: flex;
  padding: 20px;
  justify-content: space-between;
  border-bottom: 1px solid #dddddd;
`;

export const OrderItem = styled.div`
  display: flex;
  align-items: center; // 모든 항목을 수직 방향으로 가운데 정렬
  margin-bottom: 1rem; // 항목 간 여백
  justify-content: space-between;
`;

export const OrderImage = styled.img`
  width: 120px; // 너비 고정
  height: 120px; // 높이 고정
  align-items: center;
  border-radius: 4px;
  border: 0.5px solid black; // 검은색 테두리 추가
`;

export const OrderDetails = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  text-align: flex-start;
`;

export const Box = styled.div`
  // width: 100%;
  height: 60px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 20px;
`;

export const OrderTitle = styled.span`
  display: flex;
  font-weight: bold;
  text-align: center;
`;

export const HeaderContainer = styled.div`
  position: relative;
  display: flex;
  justify-content: space-between;
  align-items: center;
`;

export const OrderInfo = styled.span`
  color: #666;
  display: block; // 블록 레벨 요소로 만들어 줄바꿈 허용
  white-space: normal; // 자동 줄바꿈
  text-overflow: ellipsis; // 내용이 넘칠 경우 말줄임표 사용
  display: flex;
  font-size: 14px;
  margin-top: 5px;
  margin-bottom: 5px;
  align-items: center;
  text-align: flex-start;
  justify-content: space-between;
`;

export const DetailRow = styled.div`
  display: flex;
  align-items: center;
  text-align: flex-start;
  justify-content: space-between;
  width: 100%; // 필요에 따라 조정
`;

export const DetailColumn = styled.div`
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  text-align: flex-start;
`;

export const DetailTimeAgoColumn = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
`;

export const StyledForm = styled.form`
  display: flex;
  flex-direction: column;
  max-width: 500px;
  margin: auto;
`;

export const StyledSelect = styled.select`
  flex-basis: 100%;
  max-width: 100%; // 너비를 화면 너비에 맞춤
  padding: 0.375rem 0.75rem;
  font-size: 1rem;
  line-height: 1.5;
  border: 1px solid #ced4da;
  border-radius: 0.25rem;
  overflow: hidden; // 오버플로 숨김
  text-overflow: ellipsis; // 텍스트가 넘칠 경우 말줄임표 사용

  @media (max-width: 768px) {
    // 뷰포트 크기에 따라 조절 가능
    font-size: 0.875rem; // 모바일 환경에서 폰트 크기 축소
  }
`;

export const FormRow = styled.div`
  display: flex;
  flex-basis: 100%
  align-items: center;
  margin-bottom: 1.3rem;
`;

export const StyledLabel = styled.label`
  flex-basis: 100%;
  font-weight: bold; // 텍스트 굵게
`;

export const StyledInput = styled.input`
  flex-basis: 100%;
  margin-right: 5px;
  padding: 0.375rem 0.75rem;
  font-size: 1rem;
  line-height: 1.5;
  border: 1px solid #ced4da;
  border-radius: 0.25rem;
`;

export const StyledDeliveryFeeInput = styled.input`
  flex-basis: 30%;
  padding: 0.375rem 0.75rem;
  font-size: 1rem;
  line-height: 1.5;
  border: 1px solid #ced4da;
  border-radius: 0.25rem;
`;

export const HiddenInput = styled.input`
  display: none;
`;

export const StyledTextArea = styled.textarea`
  flex-basis: 100%;
  padding: 0.375rem 0.75rem;
  font-size: 1rem;
  line-height: 1.5;
  border: 1px solid #ced4da;
  border-radius: 0.25rem;
  resize: vertical;
  white-space: pre-wrap; // 줄 바꿈 적용
`;

export const ButtonContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
`;

export const UploadButton = styled.label`
  flex-basis: 100%;
  color: #fff;
  background-color: #508bfc;
  border-color: #508bfc;
  padding: 0.375rem 0.75rem;
  font-size: 1rem;
  line-height: 1.5;
  border-radius: 0.25rem;
  margin: 0.375rem 0.75rem;
  display: inline-block;
  cursor: pointer;
  text-align: center;

  &:hover {
    background-color: #508bfc;
    border-color: #508bfc;
  }
`;

export const OrderButton = styled.button`
  position: fixed; // 고정 위치
  bottom: 100px; // 하단에서 20px 떨어진 위치
  right: 20px; // 우측에서 20px 떨어진 위치
  padding: 10px 15px; // 패딩
  background-color: #508bfc; // 배경색
  color: #fff; // 텍스트 색상
  border: none; // 테두리 없음
  border-radius: 5px; // 테두리 둥글게
  font-size: 16px; // 폰트 크기
  cursor: pointer; // 커서 스타일
  z-index: 1000; // 화면 맨 앞으로 오게 하기

  &:hover {
    background-color: #508bfc; // 호버 시 배경색 변경
  }
`;

export const GetMyAcceptedDeliveryHistoryListButton = styled.button`
  position: fixed; // 고정 위치
  bottom: 150px; // 하단에서 떨어진 위치
  right: 20px; // 우측에서 떨어진 위치
  padding: 10px 15px; // 패딩
  background-color: #508bfc; // 배경색
  color: #fff; // 텍스트 색상
  border: none; // 테두리 없음
  border-radius: 5px; // 테두리 둥글게
  font-size: 16px; // 폰트 크기
  cursor: pointer; // 커서 스타일
  z-index: 1000; // 화면 맨 앞으로 오게 하기

  &:hover {
    background-color: #508bfc; // 호버 시 배경색 변경
  }
`;

export const StyledButton = styled.button`
  display: flex;
  width: 100%;
  color: #fff;
  background-color: #508bfc;
  border-color: #508bfc;
  padding: 0.375rem 0.75rem;
  font-size: 1rem;
  border-radius: 0.25rem;
  line-height: 1.5;
  margin: 0.375rem 0.75rem;
  justify-content: center;
  align-items: center;

  &:hover {
    background-color: #508bfc;
    border-color: #508bfc;
  }
`;

export const StyledQueryButton = styled.button`
  display: flex;
  width: 40%;
  color: #fff;
  background-color: #508bfc;
  border-color: #508bfc;
  padding: 0.375rem 0.75rem;
  font-size: 1rem;
  border-radius: 0.25rem;
  line-height: 1.5;
  margin: 0.375rem 0.75rem;
  justify-content: center;
  align-items: center;

  &:hover {
    background-color: #508bfc;
    border-color: #508bfc;
  }
`;

export const InfoText = styled.p`
  text-align: center;
`;

export const ImageSlider = styled.div`
  display: flex;
  overflow-x: scroll;
  margin-bottom: 1rem;
  justify-content: center;
`;

export const SliderContainer = styled.div`
  display: flex;
  width: 300px; // 각 이미지의 너비와 일치시킬 값
  overflow: hidden;
  flex-direction: column;
  align-items: center;
  position: relative;
`;

export const ImageSliderComponent = styled.div`
  display: flex;
  transform: ${(props) => `translateX(-${props.slideIndex * 100}%)`};
  transition: transform 0.5s ease;
  // 기타 스타일
`;

export const SliderWrapper = styled.div`
  display: flex;
  justify-content: center;
  align-items: center; // 수직 중앙 정렬을 위해 추가
  height: 100%; // 높이 설정 (필요에 따라 조절)
  width: 100%;
  margin-bottom: 20px;
`;

export const SlideButton = styled.button`
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background-color: transparent;
  border: none;
  cursor: pointer;
  z-index: 1;

  &:first-child {
    left: 0;
  }

  &:last-child {
    right: 0;
  }
`;

export const Image = styled.img`
  max-width: 100%; // SliderContainer의 너비와 일치
  max-height: 100%; // 높이도 조정할 수 있음
  display: block; // 이미지를 블록 요소로 만듦
  border: 0.5px solid black; // 검은색 테두리 추가
`;

// export const StyledContainer = styled.div`
//   padding: 1rem;
//   margin-top: 2.5rem;
//   max-width: 500px;
//   margin: auto;
//   background: #f8f9fa;
//   border-radius: 8px;
//   box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
// `;

export const DetailContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: flex-start;
`;

export const DetailLabel = styled.span`
  display: flex;
  flex-direction: column;
  font-weight: bold;
  margin-top: 10px;
  margin-bottom: 0.5rem;
  justify-content: center;
  align-items: center;
  text-align: center;
`;

export const DeliveryState = styled.div`
  display: flex;
  font-weight: bold;
  margin-top: 20px;
  margin-bottom: 10px;
  justify-content: center;
  align-items: center;
`;

export const DetailInfo = styled.span`
  margin-bottom: 10px;
`;
