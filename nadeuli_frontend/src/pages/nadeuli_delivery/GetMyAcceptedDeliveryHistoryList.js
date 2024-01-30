import React, { useEffect, useState } from "react";
import { post } from "../../util/axios";
import {
  Box,
  CardBox,
  DetailColumn,
  DetailLabel,
  DetailRow,
  DetailTimeAgoColumn,
  HeaderContainer,
  InfoText,
  OrderButton,
  OrderImage,
  OrderInfo,
  OrderTitle,
} from "./NadeuliDeliveryStyledComponent";
import HeaderBack from "../../components/HeaderBack";
import { useNavigate } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { setDeliveryLocations } from "../../redux/modules/nadeuliDelivery";

const GetMyAcceptedDeliveryHistoryList = () => {
  // 나의 주문 수락 내역을 목록 조회한다.
  const [responseDTOList, setResponseDTOList] = useState([]);
  const navigate = useNavigate();
  const memberTag = useSelector((state) => state.member.tag);
  const dispatch = useDispatch();
  const [isVisible, setIsVisible] = useState(true);

  useEffect(() => {
    const handleScroll = () => {
      setIsVisible(window.scrollY < 100);
    };

    window.addEventListener("scroll", handleScroll);
    return () => window.removeEventListener("scroll", handleScroll);
  }, []);

  useEffect(() => {
    const requestData = {
      deliveryPerson: {
        tag: memberTag,
      },
    };

    post("/nadeulidelivery/getMyAcceptedDeliveryHistoryList", requestData, {
      headers: {
        "Content-Type": "application/json",
      },
      params: {
        currentPage: 0,
      },
    })
      .then((response) => {
        console.log("getMyAcceptedDeliveryHistoryList 호출 완료!", response);
        setResponseDTOList(response);
        const deliveryLocations = response.map((dto) => ({
          departure: dto.departure,
          arrival: dto.arrival,
        }));

        console.log("dispatch 할 장소 리스트 : ", deliveryLocations);

        dispatch(setDeliveryLocations({ deliveryLocations }));
      })
      .catch((error) => {
        console.log("getMyAcceptedDeliveryHistoryList 호출 에러!", error);
      });
  }, [memberTag, dispatch]);

  const handleNavigateToOrder = (nadeuliDeliveryId) => {
    navigate(`/getDeliveryOrder/${nadeuliDeliveryId}`);
  };

  const maxLength = 9;

  const truncateTitle = (title) => {
    if (title.length > maxLength) {
      return `${title.substring(0, maxLength)}...`; // 길이가 maxLength보다 길면 잘라내고 "..." 추가
    }
    return title;
  };

  const formatCurrency = (value) => {
    return new Intl.NumberFormat("ko-KR", {
      minimumFractionDigits: 0, // 소수점 이하 자릿수 (0으로 설정하면 소수점 없음)
    }).format(value);
  };

  const handleGetShortestWay = () => {
    if (!responseDTOList.length > 0) {
      alert("주문을 먼저 수락 해주세요.");
      return;
    }
    navigate(`/getShortestWay`);
  };

  return (
    <div className="Wrap">
      <HeaderContainer>
        <HeaderBack />
        <Box>
          <OrderTitle style={{ paddingRight: "40px" }}>
            주문 수락 목록
          </OrderTitle>
        </Box>
        <Box></Box>
      </HeaderContainer>
      {!responseDTOList.length > 0 && (
        <InfoText style={{ paddingTop: "300px", fontWeight: "bold" }}>
          주문 수락 목록이 없습니다.
        </InfoText>
      )}
      {responseDTOList.map((responseDTO, index) => (
        <CardBox
          className="card"
          key={index}
          onClick={() => handleNavigateToOrder(responseDTO.nadeuliDeliveryId)}
        >
          <DetailRow>
            <DetailColumn>
              <OrderImage src={responseDTO.images[0]} alt="이미지" />
            </DetailColumn>
            <DetailColumn>
              <DetailLabel style={{ fontWeight: "bold" }}>
                {truncateTitle(responseDTO.title)}
              </DetailLabel>
              <OrderInfo>
                구매금액 {formatCurrency(responseDTO.productPrice)}원
              </OrderInfo>
              {responseDTO.productNum > 0 && (
                <OrderInfo>
                  수량 {formatCurrency(responseDTO.productNum)}개
                </OrderInfo>
              )}
              <OrderInfo>
                부름비 {formatCurrency(responseDTO.deliveryFee)}원
              </OrderInfo>
              <OrderInfo>
                보증금 {formatCurrency(responseDTO.deposit)}원
              </OrderInfo>
            </DetailColumn>
            <DetailTimeAgoColumn>
              {responseDTO.deliveryState === "DELIVERY_ORDER" && (
                <DetailLabel>주문 등록</DetailLabel>
              )}
              {responseDTO.deliveryState === "CANCEL_ORDER" && (
                <DetailLabel>주문 취소</DetailLabel>
              )}
              {responseDTO.deliveryState === "ACCEPT_ORDER" && (
                <DetailLabel>주문 수락</DetailLabel>
              )}
              {responseDTO.deliveryState === "CANCEL_DELIVERY" && (
                <DetailLabel>배달 취소</DetailLabel>
              )}
              {responseDTO.deliveryState === "COMPLETE_DELIVERY" && (
                <DetailLabel>배달 완료</DetailLabel>
              )}
              <DetailLabel>{responseDTO.timeAgo}</DetailLabel>
            </DetailTimeAgoColumn>
          </DetailRow>
        </CardBox>
      ))}
      {responseDTOList.length > 0 && isVisible && (
        <OrderButton
          style={{ transition: "opacity 0.5s", opacity: 1 }}
          onClick={handleGetShortestWay}
        >
          추천경로 조회
        </OrderButton>
      )}
    </div>
  );
};

export default GetMyAcceptedDeliveryHistoryList;
