import React, { useEffect, useState } from "react";
import {
  Box,
  FormRow,
  HeaderContainer,
  HiddenInput,
  InfoText,
  OrderTitle,
  SliderWrapper,
  StyledButton,
  StyledContainer,
  StyledDeliveryFeeInput,
  StyledForm,
  StyledInput,
  StyledLabel,
  StyledSelect,
  StyledTextArea,
  UploadButton,
} from "./NadeuliDeliveryStyledComponent";
import ImageSlider from "./ImageSlider";
import { post } from "../../util/axios";
import HeaderBack from "../../components/HeaderBack";
import { useDispatch, useSelector } from "react-redux";
import { postMultipart } from "../../util/postMultipart";
import { useLocation, useNavigate } from "react-router-dom";
import { setMember } from "../../redux/modules/member";
import SearchBar from "./SearchBar";
import WebCrawler from "./WebCrawler";

const AddDeliveryOrder = () => {
  const [orderData, setOrderData] = useState({});
  const [productType, setProductType] = useState(null);
  const [tradingOptions, setTradingOptions] = useState([]);
  const [productDetails, setProductDetails] = useState(null);
  const [previewImage, setPreviewImage] = useState(null);
  const [files, setFiles] = useState([]);
  const [searchQuery, setSearchQuery] = useState("");
  const member = useSelector((state) => state.member);
  const navigate = useNavigate();
  const location = useLocation();
  const dispatch = useDispatch();

  // 웹크롤링으로 불러올 가격을 상품 키워드로 검색한다.
  const handleSearch = (query) => {
    setSearchQuery(query);
    setOrderData((prevOrderData) => ({
      ...prevOrderData,
      productName: query,
    }));
  };

  // 검색으로 가져온 평균 가격을 상품 가격에 설정한다.
  const handleAveragePriceChange = (newAveragePrice) => {
    setOrderData((prevOrderData) => ({
      ...prevOrderData,
      productPrice: newAveragePrice,
    }));
  };

  // 출발지 설정 함수
  const handleSetLocation = () => {
    // orderData를 상태로 전달하며 목적지 설정 페이지로 이동
    navigate("/searchLocation", {
      state: { orderData: orderData, productType: productType },
    });
  };

  useEffect(() => {
    console.log("Location State:", location.state);

    // 주문 정보와 목적지 정보 불러오기
    if (location.state) {
      const { orderData, departure, arrival, productType } = location.state;

      // 출발지와 도착지 정보가 존재하는 경우, orderData 상태 업데이트
      if (departure && arrival) {
        setOrderData({
          ...orderData,
          departure: departure,
          arrival: arrival,
        });
      } else {
        // 출발지와 도착지 정보가 없는 경우, 기존 orderData 상태 유지
        setOrderData(orderData);
      }

      // productType 설정
      setProductType(productType);
    }
  }, [location.state]);

  const handleChange = (e) => {
    setOrderData((prev) => ({
      ...prev,
      [e.target.name]: e.target.value,
    }));
  };

  const handleProductTypeClick = (type) => {
    setProductType(type);
  };

  const handleFileChange = (e) => {
    const files = Array.from(e.target.files);
    console.log("받은 이미지들 : ", files);
    setFiles(files);

    // FileReader를 사용하여 각 파일에 대한 미리보기 생성
    Promise.all(
      files.map((file) => {
        return new Promise((resolve, reject) => {
          const reader = new FileReader();
          reader.onload = (e) => resolve(e.target.result);
          reader.onerror = (e) => reject(e);
          reader.readAsDataURL(file);
        });
      })
    )
      .then((images) => {
        setPreviewImage(images);
      })
      .catch((error) => console.error("미리보기 생성 오류", error));
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    // 나드리페이 보증금 계산
    const deposit = calculatedDeposit().replace(/,/g, ""); // 모든 쉼표 제거

    if (member.nadeuliPayBalance < deposit) {
      alert(
        "보유하신 나드리페이 잔액보다 보증금 금액이 큽니다. 금액을 조정해주세요. 현재 잔액 : " +
          member.nadeuliPayBalance +
          "원"
      );
      return;
    }

    // orderData 객체에 buyer.tag와 buyer.nickname 추가
    const updatedOrderData = {
      ...orderData,
      buyer: {
        tag: member.tag,
        nickname: member.nickname,
      },
      deliveryState: "DELIVERY_ORDER",
      deposit: parseInt(deposit, 10),
    };

    // 전달할 양식 데이터 준비
    const formData = new FormData();

    // 'nadeuliDeliveryDTO'라는 파트에 JSON 문자열로 변환된 데이터 추가
    formData.append(
      "nadeuliDeliveryDTO",
      new Blob([JSON.stringify(updatedOrderData)], {
        type: "application/json",
      })
    );

    // 파일을 formData에 추가
    for (const file of files) {
      formData.append("images", file);
    }

    // axios를 사용하여 데이터 전송
    postMultipart("/nadeulidelivery/addDeliveryOrder", formData)
      .then((response) => {
        const balance = Number(member.nadeuliPayBalance);
        const depositAmount = Number(deposit);
        console.log("계산 할 나드리페이 잔액 : " + balance);
        console.log("계산 할 보증금 : " + depositAmount);
        if (!isNaN(balance) && !isNaN(depositAmount)) {
          // 계산된 새로운 잔액을 계산합니다.
          const newBalance = balance - depositAmount;

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

        console.log("배달 주문 완료!", response);
        alert("배달 주문 완료!!");
        navigate("/nadeuliDeliveryHome");
      })
      .catch((error) => console.log("주문 등록 실패", error));
  };

  useEffect(() => {
    if (productType === "used") {
      // member 의 tag를 사용하여 거래 옵션 가져오기
      post(`/nadeulidelivery/getAddOrUpdateUsedDeliveryOrder/${member.tag}`, {
        currentPage: 0,
      })
        .then((response) => {
          console.log("거래일정으로 불러온 데이터 : ", response);
          setTradingOptions(
            response.map((item) => ({
              ...item,
              productId: item.product.productId,
            }))
          );
        })
        .catch((error) => {
          console.error("Error fetching trading options", error);
        });
    }
  }, [productType, member.tag]);

  const maxLength = 15; // 최대 표시 길이

  const truncateTitle = (title) => {
    if (title.length > maxLength) {
      return `${title.substring(0, maxLength)}...`; // 길이가 maxLength보다 길면 잘라내고 "..." 추가
    }
    return title;
  };

  const handleTradeScheduleChange = (e) => {
    const selectedTradeOption = tradingOptions.find(
      (option) => option.productId.toString() === e.target.value
    );

    if (selectedTradeOption) {
      setProductDetails(selectedTradeOption.product);
    }
  };

  // 받아온 거래 일정에서 시간 포맷을 한국 기준으로 변경(연월일시분)
  const formatTradingTime = (timeString) => {
    const date = new Date(timeString);
    const formatter = new Intl.DateTimeFormat("ko-KR", {
      year: "numeric",
      month: "long",
      day: "numeric",
      hour: "numeric",
      minute: "numeric",
    });
    return formatter.format(date);
  };

  useEffect(() => {
    if (productType === "used" && productDetails) {
      setOrderData((prevOrderData) => ({
        ...prevOrderData,
        productName: productDetails.title,
        productPrice: productDetails.price,
      }));
    }
  }, [productType, productDetails]);

  // 나드리페이 보증금 계산
  const calculatedDeposit = () => {
    const productPrice = parseInt(orderData.productPrice, 10) || 0;
    const productNum = parseInt(orderData.productNum, 10) || 0;
    const deliveryFee = parseInt(orderData.deliveryFee, 10) || 0;

    let total;
    if (productType === "new" && productNum) {
      // 일반상품은 (구매 금액 * 상품 수량) + 부름비
      total = productPrice * productNum + deliveryFee;
    } else {
      // 중고상품은 구매 금액 + 부름비
      total = productPrice + deliveryFee;
    }
    return total.toLocaleString();
  };

  return (
    <>
      <HeaderContainer>
        <HeaderBack />
        <Box>
          <OrderTitle style={{ paddingRight: "40px" }}>
            배달 주문 등록
          </OrderTitle>
        </Box>
        <Box></Box>
      </HeaderContainer>
      <StyledContainer>
        <StyledForm onSubmit={handleSubmit}>
          <FormRow>
            <StyledLabel htmlFor="title">제목</StyledLabel>
          </FormRow>
          <FormRow>
            <StyledInput
              type="text"
              id="title"
              name="title"
              value={orderData.title || ""}
              placeholder="제목을 작성해 주세요."
              onChange={handleChange}
            />
          </FormRow>
          <FormRow>
            <StyledButton
              type="button"
              onClick={() => handleProductTypeClick("used")}
            >
              중고상품
            </StyledButton>
            <StyledButton
              type="button"
              onClick={() => handleProductTypeClick("new")}
            >
              일반상품
            </StyledButton>
          </FormRow>
          {productType ? (
            <>
              <FormRow>
                <StyledButton type="button" onClick={handleSetLocation}>
                  목적지 설정
                </StyledButton>
                {/* <StyledButton type="button" onClick={handleSetArrival}>
                  도착지 설정
                </StyledButton> */}
              </FormRow>
              <FormRow>
                <StyledLabel htmlFor="departure">출발지</StyledLabel>
              </FormRow>
              {/* id="departure" name="departure" */}
              <FormRow>
                {orderData.departure || ""}
                <HiddenInput
                  type="text"
                  id="departure"
                  name="departure"
                  value={orderData.departure || ""}
                  onChange={handleChange}
                />
              </FormRow>
              <FormRow>
                <StyledLabel htmlFor="arrival">도착지</StyledLabel>
              </FormRow>
              {/* id="arrival" name="arrival" */}
              <FormRow>
                {orderData.arrival || ""}
                <HiddenInput
                  type="text"
                  id="arrival"
                  name="arrival"
                  value={orderData.arrival || ""}
                  onChange={handleChange}
                />
              </FormRow>
              {productType === "used" && (
                <FormRow>
                  <StyledSelect
                    id="tradeSchedule"
                    name="tradeSchedule"
                    onChange={handleTradeScheduleChange}
                  >
                    <option value="">거래 일정 선택</option>
                    {tradingOptions.map((option, index) => (
                      <option key={index} value={option.productId}>
                        {`${formatTradingTime(
                          option.tradingTime
                        )} - ${truncateTitle(option.product.title)}`}
                      </option>
                    ))}
                  </StyledSelect>
                </FormRow>
              )}

              <FormRow>
                <StyledLabel htmlFor="content">내용</StyledLabel>
              </FormRow>
              <FormRow>
                <StyledTextArea
                  id="content"
                  name="content"
                  value={orderData.content || ""}
                  placeholder="게시물 내용을 작성해 주세요."
                  rows={6}
                  onChange={handleChange}
                />
              </FormRow>
              {productType === "used" && productDetails ? (
                <>
                  <FormRow>
                    <StyledLabel
                      style={{ textAlign: "right" }}
                      htmlFor="productName"
                    >
                      상품 게시물 제목
                    </StyledLabel>
                  </FormRow>
                  <FormRow style={{ justifyContent: "right" }}>
                    {productDetails.title}
                    <HiddenInput
                      type="text"
                      id="productName"
                      name="productName"
                      value={productDetails.title}
                      onChange={handleChange}
                    />
                  </FormRow>
                </>
              ) : (
                <>
                  {productType === "new" && (
                    <>
                      <SearchBar onSearch={handleSearch} />
                      <WebCrawler
                        searchQuery={searchQuery}
                        onAveragePriceChange={handleAveragePriceChange}
                      />
                    </>
                  )}
                  <FormRow>
                    <StyledLabel
                      style={{ textAlign: "right" }}
                      htmlFor="productName"
                    >
                      상품 이름
                    </StyledLabel>
                  </FormRow>
                  <FormRow
                    style={{
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "flex-end",
                    }}
                  >
                    <StyledInput
                      type="text"
                      id="productName"
                      name="productName"
                      value={orderData.productName || ""}
                      placeholder="상품 이름"
                      onChange={handleChange}
                    />
                  </FormRow>
                </>
              )}
              {productType === "used" && productDetails ? (
                <>
                  <FormRow>
                    <StyledLabel
                      style={{ textAlign: "right" }}
                      htmlFor="productPrice"
                    >
                      구매 금액
                    </StyledLabel>
                  </FormRow>
                  <FormRow
                    style={{
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "flex-end",
                    }}
                  >
                    {productDetails.price.toLocaleString()}
                    <span style={{ alignSelf: "center" }}>원</span>
                    <HiddenInput
                      type="number"
                      id="productPrice"
                      name="productPrice"
                      value={productDetails.price}
                      onChange={handleChange}
                    />
                  </FormRow>
                </>
              ) : (
                <>
                  <FormRow>
                    <StyledLabel
                      style={{ textAlign: "right" }}
                      htmlFor="productPrice"
                    >
                      구매 금액
                    </StyledLabel>
                  </FormRow>
                  <FormRow
                    style={{
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "flex-end",
                    }}
                  >
                    <StyledInput
                      type="number"
                      id="productPrice"
                      name="productPrice"
                      value={orderData.productPrice || ""}
                      placeholder="구매 금액"
                      onChange={handleChange}
                    />
                    <span style={{ alignSelf: "center" }}>원</span>
                  </FormRow>
                </>
              )}
              {productType === "new" && (
                <>
                  <FormRow>
                    <StyledLabel
                      style={{ textAlign: "right" }}
                      htmlFor="productNum"
                    >
                      상품 수량
                    </StyledLabel>
                  </FormRow>
                  <FormRow
                    style={{
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "flex-end",
                    }}
                  >
                    <StyledInput
                      type="number"
                      id="productNum"
                      name="productNum"
                      value={orderData.productNum || 1}
                      placeholder="상품 수량"
                      onChange={handleChange}
                    />
                    <span style={{ alignSelf: "center" }}>개</span>
                  </FormRow>
                </>
              )}
              <FormRow>
                <StyledLabel
                  style={{ textAlign: "right" }}
                  htmlFor="deliveryFee"
                >
                  부름비
                </StyledLabel>
              </FormRow>
              <FormRow
                style={{
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "flex-end",
                }}
              >
                <StyledDeliveryFeeInput
                  type="number"
                  id="deliveryFee"
                  name="deliveryFee"
                  value={orderData.deliveryFee || ""}
                  placeholder="부름비를 입력해 주세요."
                  onChange={handleChange}
                  style={{ marginRight: "10px" }}
                />
                <span style={{ alignSelf: "center" }}>원</span>
              </FormRow>

              <FormRow>
                <StyledLabel style={{ textAlign: "right" }} htmlFor="deposit">
                  나드리페이 보증금
                </StyledLabel>
              </FormRow>
              <FormRow style={{ justifyContent: "right" }}>
                {calculatedDeposit()}&nbsp;&nbsp;&nbsp;원
                <HiddenInput
                  type="number"
                  id="deposit"
                  name="deposit"
                  onChange={handleChange}
                />
              </FormRow>
              {productType === "used" && productDetails ? (
                <>
                  <SliderWrapper>
                    {previewImage && previewImage.length > 0 ? (
                      <ImageSlider images={previewImage} />
                    ) : null}
                    {/* : productDetails && productDetails.images ? (
                      <ImageSlider images={productDetails.images} />
                    ) */}
                  </SliderWrapper>
                  <FormRow>
                    <HiddenInput
                      type="file"
                      id="images"
                      name="images"
                      multiple
                      onChange={handleFileChange}
                    />
                    <UploadButton htmlFor="images">사진 업로드</UploadButton>
                  </FormRow>
                </>
              ) : (
                <>
                  {/* Redux에서 가져온 이미지가 있으면 해당 이미지 사용, 그렇지 않으면 previewImage 사용 */}
                  {previewImage && previewImage.length > 0 ? (
                    <SliderWrapper>
                      <ImageSlider images={previewImage} />
                    </SliderWrapper>
                  ) : null}
                  <FormRow>
                    <HiddenInput
                      type="file"
                      id="images"
                      name="images"
                      multiple
                      onChange={handleFileChange}
                    />
                    <UploadButton htmlFor="images">사진 업로드</UploadButton>
                  </FormRow>
                </>
              )}
              <FormRow>
                <StyledButton type="submit">작성 완료</StyledButton>
              </FormRow>
            </>
          ) : (
            <InfoText>중고 상품/일반 상품 버튼을 눌러주세요.</InfoText>
          )}
        </StyledForm>
      </StyledContainer>
    </>
  );
};

export default AddDeliveryOrder;
