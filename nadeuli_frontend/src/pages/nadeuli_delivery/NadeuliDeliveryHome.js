import React, { useEffect, useState } from "react";
import { get, post } from "../../util/axios";
import {
  Box,
  CardBox,
  DetailColumn,
  DetailLabel,
  DetailRow,
  DetailTimeAgoColumn,
  GetMyAcceptedDeliveryHistoryListButton,
  InfoText,
  OrderButton,
  OrderImage,
  OrderInfo,
} from "./NadeuliDeliveryStyledComponent";
import BottomBar from "../../components/BottonBar";
import { useNavigate } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import NadeuliDeliveryHomeTopBar from "./NadeuliDeliveryHomeTopBar";
import { setMember } from "../../redux/modules/member";

const NadeuliDeliveryHome = () => {
  const [responseDTOList, setResponseDTOList] = useState([]);
  const [filteredDTOList, setFilteredDTOList] = useState([]); // 필터링된 리스트를 위한 상태
  const navigate = useNavigate();
  // const memberGu = useSelector((state) => state.member.gu);
  const [searchQuery, setSearchQuery] = useState("");
  const [isVisible, setIsVisible] = useState(true);
  const member = useSelector((state) => state.member);
  const dispatch = useDispatch();

  useEffect(() => {
    get(`/member/getMember/${member.tag}`).then((response) => {
      const balance = Number(member.nadeuliPayBalance);
      const depositAmount = Number(response.nadeuliPayBalance);
      console.log("프론트엔드 나드리페이 잔액 : " + balance);
      console.log("백엔드에서 가져온 보증금 : " + depositAmount);

      // 새로운 잔액으로 member 객체를 업데이트합니다.
      const updatedMember = {
        ...member,
        nadeuliPayBalance: depositAmount,
      };

      // 업데이트된 member 객체로 상태를 업데이트합니다.
      dispatch(setMember(updatedMember));

      console.log("갱신된 나드리페이 잔액 : " + member.nadeuliPayBalance);
    });
  }, [member, dispatch]);

  useEffect(() => {
    const handleScroll = () => {
      setIsVisible(window.scrollY < 100);
    };

    window.addEventListener("scroll", handleScroll);
    return () => window.removeEventListener("scroll", handleScroll);
  }, []);

  const handleSearch = (query) => {
    setSearchQuery(query); // 검색 쿼리 업데이트
  };

  useEffect(() => {
    const requestData = {
      gu: member.gu,
    };

    post("/nadeulidelivery/getDeliveryOrderList", requestData, {
      headers: {
        "Content-Type": "application/json",
      },
      params: {
        currentPage: 0,
      },
    })
      .then((response) => {
        console.log("getDeliveryOrderList 호출 완료!", response);
        setResponseDTOList(response);
      })
      .catch((error) => {
        console.log("getDeliveryOrderList 호출 에러!", error);
      });
  }, [member.gu]);

  const handleNavigateToOrder = (nadeuliDeliveryId) => {
    navigate(`/getDeliveryOrder/${nadeuliDeliveryId}`);
  };

  const handleAddDeliveryOrder = () => {
    navigate("/addDeliveryOrder");
  };

  const handleNavigateMyAcceptedDeliveryHistoryList = () => {
    navigate("/getMyAcceptedDeliveryHistoryList");
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

  // 검색 쿼리가 변경될 때마다 리스트 필터링
  useEffect(() => {
    if (searchQuery.trim() === "") {
      setFilteredDTOList(responseDTOList);
    } else {
      const filtered = responseDTOList.filter((dto) =>
        dto.title.toLowerCase().includes(searchQuery.toLowerCase())
      );
      setFilteredDTOList(filtered);
    }
  }, [searchQuery, responseDTOList]);

  return (
    <div className="Wrap">
      <NadeuliDeliveryHomeTopBar onSearch={handleSearch} />
      <Box></Box>

      {!responseDTOList.length > 0 && (
        <InfoText style={{ paddingTop: "300px", fontWeight: "bold" }}>
          게시물이 없습니다.
        </InfoText>
      )}
      {filteredDTOList.map((responseDTO, index) => (
        <div className="MainListBox">
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
                <DetailLabel
                  style={{ fontWeight: "bold" }}
                  searchQuery={searchQuery}
                >
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
                <DetailLabel>주문 등록</DetailLabel>
                <DetailLabel>{responseDTO.timeAgo}</DetailLabel>
              </DetailTimeAgoColumn>
            </DetailRow>
          </CardBox>
        </div>
      ))}
      <Box style={{ marginTop: "20px" }}></Box>
      {isVisible && (
        <>
          <GetMyAcceptedDeliveryHistoryListButton
            style={{ transition: "opacity 0.5s", opacity: 1 }}
            onClick={handleNavigateMyAcceptedDeliveryHistoryList}
          >
            주문수락 목록
          </GetMyAcceptedDeliveryHistoryListButton>
          <OrderButton
            style={{ transition: "opacity 0.5s", opacity: 1 }}
            onClick={handleAddDeliveryOrder}
          >
            배달 주문하기
          </OrderButton>
        </>
      )}
      <BottomBar selected="nadeuliDeliveryHome" />
    </div>
  );
};

export default NadeuliDeliveryHome;
