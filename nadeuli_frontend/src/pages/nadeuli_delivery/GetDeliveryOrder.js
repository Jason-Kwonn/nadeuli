import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import ImageSlider from "./ImageSlider";
import { get, post } from "../../util/axios";
import {
  Box,
  ButtonContainer,
  DeliveryState,
  DetailColumn,
  DetailContainer,
  DetailInfo,
  DetailLabel,
  DetailRow,
  HeaderContainer,
  OrderTitle,
  SliderWrapper,
  StyledButton,
  StyledContainer,
} from "./NadeuliDeliveryStyledComponent";
import HeaderBack from "../../components/HeaderBack";
import { useDispatch, useSelector } from "react-redux";
import { setMember } from "../../redux/modules/member";
import TopDropdownMenu from "../../components/TopDropdownMenu";
import AlertDialog from "../../components/AlertDialog";

const GetDeliveryOrder = () => {
  // 배달 주문을 조회한다.
  const { nadeuliDeliveryId } = useParams();
  const [nadeuliDeliveryDTO, setNadeuliDeliveryDTO] = useState({});
  const navigate = useNavigate();
  // const memberTag = useSelector((state) => state.member.tag);
  // const memberNickName = useSelector((state) => state.member.nickname);
  const member = useSelector((state) => state.member);
  const dispatch = useDispatch();
  const [dialogOpen, setDialogOpen] = useState(false);
  const [dialogTitle, setDialogTitle] = useState("");
  const [dialogDescription, setDialogDescription] = useState("");
  const [pendingAction, setPendingAction] = useState(null);
  const [dialogAgreeText, setDialogAgreeText] = useState("");
  const [dialogDisagreeText, setDialogDisagreeText] = useState("");

  useEffect(() => {
    get(`/nadeulidelivery/getDeliveryOrder/${nadeuliDeliveryId}`)
      .then((response) => {
        console.log(response);
        setNadeuliDeliveryDTO(response);
      })
      .catch((error) => {
        console.log("배달 주문 에러", error);
      });
  }, [nadeuliDeliveryId]);

  const handleUpdateDeliveryOrder = () => {
    navigate(`/updateDeliveryOrder/${nadeuliDeliveryId}`);
  };

  const handleCancelDeliveryOrder = () => {
    get(`/nadeulidelivery/cancelDeliveryOrder/${nadeuliDeliveryId}`)
      .then((response) => {
        const balance = Number(member.nadeuliPayBalance);
        const depositAmount = Number(nadeuliDeliveryDTO.deposit);
        console.log("계산 할 나드리페이 잔액 : " + balance);
        console.log("주문 취소 후 더할 보증금 : " + depositAmount);

        if (!isNaN(balance) && !isNaN(depositAmount)) {
          // 계산된 새로운 잔액을 계산합니다.
          const newBalance = balance + depositAmount;

          if (!isNaN(newBalance)) {
            // 새로운 잔액으로 member 객체를 업데이트합니다.
            const updatedMember = {
              ...member,
              nadeuliPayBalance: newBalance,
            };

            // 업데이트된 member 객체로 상태를 업데이트합니다.
            dispatch(setMember(updatedMember));
          }

          console.log("갱신된 나드리페이 잔액 : " + member.nadeuliPayBalance);
        }

        console.log(response);
        alert("주문 취소 완료!");
        navigate("/nadeuliDeliveryHome");
      })
      .catch((error) => {
        console.error("주문 취소 처리 에러", error);
      });
  };

  const handleAcceptOrder = () => {
    // nadeuliDeliveryDTO의 복사본을 만듭니다.
    const updatedDeliveryDTO = {
      ...nadeuliDeliveryDTO,
      deliveryPerson: {
        tag: member.tag, // 전역 상태에서 가져온 tag
        nickname: member.nickname, // 전역 상태에서 가져온 nickname
      },
    };
    post(`/nadeulidelivery/acceptDeliveryOrder`, updatedDeliveryDTO)
      .then((response) => {
        console.log(response);
        alert("주문 수락 완료!");
        navigate("/nadeuliDeliveryHome");
      })
      .catch((error) => {
        console.error("주문 수락 처리 에러", error);
      });
  };

  const handleCancelDelivery = () => {
    get(`/nadeulidelivery/cancelDelivery/${nadeuliDeliveryId}`)
      .then((response) => {
        console.log(response);
        alert("배달 취소 완료!");
        navigate("/nadeuliDeliveryHome");
      })
      .catch((error) => {
        console.error("배달 취소 처리 에러", error);
      });
  };

  const handleCompleteDelivery = () => {
    get(`/nadeulidelivery/completeDelivery/${nadeuliDeliveryId}`)
      .then((response) => {
        console.log(response);
        alert("배달 완료!");
        navigate("/nadeuliDeliveryHome");
      })
      .catch((error) => {
        console.error("배달 완료 처리 에러", error);
      });
  };

  const formatCurrency = (value) => {
    return new Intl.NumberFormat("ko-KR", {
      minimumFractionDigits: 0, // 소수점 이하 자릿수 (0으로 설정하면 소수점 없음)
    }).format(value);
  };

  const openReportDialog = () => {
    setPendingAction(() => () => {
      // 신고 관련 로직
    });
    setDialogTitle("신고 확인");
    setDialogDescription("이 채널을 신고하시겠습니까?");
    setDialogAgreeText("신고");
    setDialogDisagreeText("취소");
    setDialogOpen(true);
  };

  const closeDialog = () => {
    setDialogOpen(false);
  };

  const executePendingAction = () => {
    if (pendingAction) pendingAction();
    setPendingAction(null);
    closeDialog();
    navigate(`/report/nadeuliDelivery/${nadeuliDeliveryId}`);
  };

  const dropdownMenus3 = [
    { label: "신고", onClick: openReportDialog },
    // 원하는 만큼 추가
  ];

  return (
    <>
      <HeaderContainer>
        <HeaderBack />
        <Box>
          <OrderTitle>배달 주문</OrderTitle>
        </Box>
        <TopDropdownMenu dropdownMenus={dropdownMenus3} />
      </HeaderContainer>
      <StyledContainer>
        <DetailContainer>
          <DetailLabel>{nadeuliDeliveryDTO.title}</DetailLabel>
        </DetailContainer>
        <SliderWrapper style={{ marginBottom: "20px", marginTop: "20px" }}>
          <ImageSlider style images={nadeuliDeliveryDTO.images || []} />
        </SliderWrapper>
        <DetailContainer>
          <DetailRow>
            <DetailColumn>
              <DetailLabel>구매자</DetailLabel>
            </DetailColumn>
            <DetailColumn>
              <DetailLabel>배송자</DetailLabel>
            </DetailColumn>
          </DetailRow>
          <DetailRow>
            <DetailColumn>
              {nadeuliDeliveryDTO.buyer && (
                <>
                  <DetailInfo>{nadeuliDeliveryDTO.buyer.tag}</DetailInfo>
                  <DetailInfo>{nadeuliDeliveryDTO.buyer.nickname}</DetailInfo>
                </>
              )}
            </DetailColumn>
            <DetailColumn>
              {nadeuliDeliveryDTO.deliveryPerson && (
                <>
                  <DetailInfo>
                    {nadeuliDeliveryDTO.deliveryPerson.tag}
                  </DetailInfo>
                  <DetailInfo>
                    {nadeuliDeliveryDTO.deliveryPerson.nickname}
                  </DetailInfo>
                </>
              )}
            </DetailColumn>
          </DetailRow>
        </DetailContainer>
        <DetailContainer>
          <DetailLabel>배달 내용</DetailLabel>
          <DetailRow>
            <DetailInfo>{nadeuliDeliveryDTO.content}</DetailInfo>
          </DetailRow>
        </DetailContainer>
        <DetailContainer>
          <DetailLabel>상품 구분</DetailLabel>

          {nadeuliDeliveryDTO.productNum ? (
            <DetailInfo>일반 상품</DetailInfo>
          ) : (
            <DetailInfo>중고 상품</DetailInfo>
          )}
        </DetailContainer>
        <DetailContainer>
          <DetailLabel>상품명</DetailLabel>
          <DetailInfo>{nadeuliDeliveryDTO.productName}</DetailInfo>
        </DetailContainer>
        <DetailContainer>
          <DetailLabel>구매 금액</DetailLabel>
          <DetailInfo>
            {formatCurrency(nadeuliDeliveryDTO.productPrice)} 원
          </DetailInfo>
        </DetailContainer>
        {nadeuliDeliveryDTO.productNum > 0 && (
          <DetailContainer>
            <DetailLabel>상품 수량</DetailLabel>
            <DetailInfo>
              {formatCurrency(nadeuliDeliveryDTO.productNum)} 개
            </DetailInfo>
          </DetailContainer>
        )}
        <DetailContainer>
          <DetailLabel>배달비</DetailLabel>
          <DetailInfo>
            {formatCurrency(nadeuliDeliveryDTO.deliveryFee)} 원
          </DetailInfo>
        </DetailContainer>
        <DetailContainer>
          <DetailLabel>나드리페이 보증금</DetailLabel>
          <DetailInfo>
            {formatCurrency(nadeuliDeliveryDTO.deposit)} 원
          </DetailInfo>
        </DetailContainer>
        <DetailContainer>
          <DetailLabel>출발지</DetailLabel>
          <DetailInfo>{nadeuliDeliveryDTO.departure}</DetailInfo>
        </DetailContainer>
        <DetailContainer>
          <DetailLabel>도착지</DetailLabel>
          <DetailInfo>{nadeuliDeliveryDTO.arrival}</DetailInfo>
        </DetailContainer>
        <DeliveryState>배달 현황</DeliveryState>
        <DetailContainer>
          <DetailLabel>주문 등록</DetailLabel>
          <DetailInfo>{nadeuliDeliveryDTO.regDate}</DetailInfo>
          {nadeuliDeliveryDTO.deliveryState === "CANCEL_ORDER" && (
            <>
              <DetailLabel>주문 취소</DetailLabel>
              <DetailInfo>{nadeuliDeliveryDTO.orderCancelDate}</DetailInfo>
            </>
          )}
          {(nadeuliDeliveryDTO.deliveryState === "ACCEPT_ORDER" ||
            nadeuliDeliveryDTO.orderAcceptDate) && (
            <>
              <DetailLabel>주문 수락</DetailLabel>
              <DetailInfo>{nadeuliDeliveryDTO.orderAcceptDate}</DetailInfo>
            </>
          )}
          {(nadeuliDeliveryDTO.deliveryState === "CANCEL_DELIVERY" ||
            nadeuliDeliveryDTO.deliveryCancelDate) && (
            <>
              <DetailLabel>배달 취소</DetailLabel>
              <DetailInfo>{nadeuliDeliveryDTO.deliveryCancelDate}</DetailInfo>
            </>
          )}
          {(nadeuliDeliveryDTO.deliveryState === "COMPLETE_DELIVERY" ||
            nadeuliDeliveryDTO.orderCancelDate) && (
            <>
              <DetailLabel>배달 완료</DetailLabel>
              <DetailInfo>{nadeuliDeliveryDTO.deliveryCompleteDate}</DetailInfo>
            </>
          )}
        </DetailContainer>
        <ButtonContainer>
          {member.tag === nadeuliDeliveryDTO.buyer?.tag &&
          nadeuliDeliveryDTO.deliveryState === "DELIVERY_ORDER" ? (
            <>
              <StyledButton onClick={handleUpdateDeliveryOrder}>
                주문 수정
              </StyledButton>
              <StyledButton onClick={handleCancelDeliveryOrder}>
                주문 취소
              </StyledButton>
            </>
          ) : member.tag !== nadeuliDeliveryDTO.buyer?.tag &&
            nadeuliDeliveryDTO.deliveryState === "DELIVERY_ORDER" ? (
            <StyledButton style={{ width: "100%" }} onClick={handleAcceptOrder}>
              주문 수락
            </StyledButton>
          ) : member.tag === nadeuliDeliveryDTO.deliveryPerson?.tag &&
            nadeuliDeliveryDTO.deliveryState === "ACCEPT_ORDER" ? (
            <>
              <StyledButton onClick={handleCancelDelivery}>
                배달 취소
              </StyledButton>
              <StyledButton onClick={handleCompleteDelivery}>
                배달 완료
              </StyledButton>
            </>
          ) : null}
        </ButtonContainer>
        <AlertDialog
          open={dialogOpen}
          handleClose={closeDialog}
          onAgree={executePendingAction}
          title={dialogTitle}
          description={dialogDescription}
          agreeText={dialogAgreeText}
          disagreeText={dialogDisagreeText}
        />
      </StyledContainer>
    </>
  );
};

export default GetDeliveryOrder;
