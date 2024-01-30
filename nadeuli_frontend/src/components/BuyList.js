import { useDispatch, useSelector } from "react-redux";
import styled from "styled-components";
import { changeTradeStateDB } from "../redux/modules/post";
import { AiOutlineMenu } from "react-icons/ai";

function BuyList () {
  const postList = useSelector((state) => state.post.postList);
  const dispatch = useDispatch();

  return (
    <div style={{ height: "450px", overflowY: "scroll" }}>
      <SubTitle>구매 내역</SubTitle>
      <div>
        {postList.sellList?.map((list, index) => (
          <Card key={index}>
            <CardBox className="card">
              <div style={{ display: "flex" }}>
                <Img src={list.postImg} />
                <TextArea>
                  <span style={{ fontSize: "15px", marginBottom: "5px" }}>
                    {list.title}
                  </span>
                  <span
                    style={{
                      fontSize: "12px",
                      padding: "5px",
                      color: "#AAAAAA",
                    }}
                  >
                    {list.userLocation}
                  </span>
                  <span
                    style={{
                      fontSize: "13px",
                      padding: "5px",
                      fontWeight: "bold",
                    }}
                  >
                    {list.tradeState === "1" && <span>예약중</span>}
                    {Number(list.price).toLocaleString("ko-KR")}원
                  </span>
                </TextArea>
              </div>

              <AiOutlineMenu />
              {/* 거래완료 API */}
            </CardBox>
          </Card>
        ))}
      </div>
    </div>
  );
}

const SubTitle = styled.div`
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 24px;
  border-bottom: 1px solid #ddd;
`;

const Card = styled.div`
  display: flex;
  flex-direction: column;
  border-bottom: 1px solid #ddd;
`;

const CardBox = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 130px;
  padding: 15px;
  align-items: flex-start;
`;

const Img = styled.img`
  width: 100px;
  height: 100px;
  border-radius: 10px;
`;

const TextArea = styled.div`
  display: flex;
  flex-direction: column;
  padding: 10px;
`;

export default BuyList;