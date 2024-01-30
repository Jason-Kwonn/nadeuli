import "../public/css/listForm.css";
import styled, { css } from "styled-components";
import { FiMoreVertical } from "react-icons/fi";

import { useEffect, useState } from "react";
// import { changeTradeStateDB, loadSalseposts } from "../redux/modules/post";
import { useSelector, useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";

import Modal from "../components/Modal";
import { deletePost, loadSalesposts, reloadSalesposts, carrotGetPost } from "../redux/modules/post";
import HeaderBack from "../components/HeaderBack";
import { post } from "../util/axios";

function SalesList() {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const [productId, setProductId] = useState(0);
  // const [boardList, setBoardList] = useState();

  const postList = useSelector((state) => state.post.postList);
  const member = useSelector((state) => state.member); // 유저 정보

  // 현재 탭
  const NOW_SELL = 0;
  const COMPLETE_SELL = 1;
  const COMPLETE_BUY = 2;
  const [tab, setTab] = useState(NOW_SELL);

  useEffect(() => {
    dispatch(loadSalesposts(member.tag, tab));
  }, [dispatch, tab]);

  useEffect(() => {
    console.log(postList);
  }, [postList]);

  const [modalOpen, setModalOpen] = useState(false);
  const openModal = (productId) => {
    setProductId(productId);
    setModalOpen(true);
  };
  const closeModal = () => {
    setModalOpen(false);
    setProductId(null);
  };

  const handleSellComplete = (productId) => {
    console.log(productId);
    post('/product/saleCompleted', {'productId': productId})
    .then(()=>{
      dispatch(loadSalesposts(member.tag, tab));
      closeModal();
    })
    
  };

  return (
    <div>
      <HeaderBack title="판매 내역" />
      <SellMenu active={tab}>
        <button
          onClick={() => {
            setTab(NOW_SELL);
          }}
        >
          판매중
        </button>
        <button
          onClick={() => {
            setTab(COMPLETE_SELL);
          }}
        >
          판매완료
        </button>
        <button
          onClick={() => {
            setTab(COMPLETE_BUY);
          }}
        >
          구매완료
        </button>
      </SellMenu>
      {!postList ? <NotFound> 판매내역이 없어요</NotFound> : ""}
      <div>
        {/* 위 디브에 온클릭 이벤트 걸어둘것! list.pistId */}
        {postList
          ?.map((list, index) => (
            <Card key={index}>
              <CardBox className="card">
                <div style={{ display: "flex", width: "100%" }}>
                  <Img src={list.images?.[0]} />
                  <TextArea>
                    <span
                      style={{
                        fontSize: "15px",
                        marginBottom: "5px",
                        padding: "0 5px",
                        wordBreak: "break-all",
                      }}
                    >
                      {list.title}
                    </span>
                    <span
                      style={{
                        fontSize: "12px",
                        padding: "5px",
                        color: "#AAAAAA",
                      }}
                    >
                      {list.seller?.dongNe}
                    </span>
                    <div
                      style={{
                        display: "flex",
                        alignItems: "center",
                        fontSize: "13px",
                        padding: "5px",
                        fontWeight: "bold",
                      }}
                    >
                      {list.tradeState === "1" && <Book>예약중</Book>}
                      {Number(list.price).toLocaleString("ko-KR")}원
                    </div>
                  </TextArea>
                
                <div style={{ marginLeft: "auto" }}>
                  <FiMoreVertical
                    style={{ marginTop: "5px" }}
                    onClick={() => {
                      openModal(list.productId);
                    }}
                  />
                </div>
                </div>
                {/* 거래완료 API */}
              </CardBox>
              <CardButton>
                {list.tradeState === "0" && (
                  <button
                    onClick={() => {
                      // dispatch(changeTradeStateDB(list.postId, "1")); // 예약으로 바꾸기
                    }}
                  >
                    예약중
                  </button>
                )}
                {list.tradeState === "1" && (
                  <button
                    onClick={() => {
                      // dispatch(changeTradeStateDB(list.postId, "0"));
                    }}
                  >
                    거래중
                  </button>
                )}
                {(list.tradeState === "0" || list.tradeState === "1") && (
                  <button
                    onClick={() => {
                      // dispatch(changeTradeStateDB(list.postId, "2"));
                    }}
                  >
                    거래완료
                  </button>
                )}
              </CardButton>
              <Modal open={modalOpen} close={closeModal}>
                <ButtonWrap>
                  <ButtonModify
                    onClick={() => {
                      dispatch(carrotGetPost(member.tag, productId))
                      navigate("/modify/" + productId);
                    }}
                  >
                    수정
                  </ButtonModify>
                  <ButtonModify
                    onClick={() => {
                      handleSellComplete(productId);
                    }}
                  >
                    판매완료
                  </ButtonModify>
                  <ButtonDelete
                    onClick={() => {
                      // dispatch(deletePost(list.postId, navigate));
                      alert("삭제가 완료되었습니다. ");
                    }}
                  >
                    삭제
                  </ButtonDelete>

                </ButtonWrap>
              </Modal>
            </Card>
          ))}
      </div>
    </div>
  );
}

const Card = styled.div`
  display: flex;
  flex-direction: column;
`;

const CardButton = styled.div`
  display: flex;

  button {
    width: 50%;
    height: 50px;
    border: none;
    border-top: 1px solid #ddd;
    border-bottom: 1px solid #ddd;
    background-color: #ffffff;
    cursor: pointer;
  }

  button + button {
    border-left: 1px solid #ddd;
  }
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
  flex-shrink: 0;
`;

const TextArea = styled.div`
  display: flex;
  flex-direction: column;
  padding: 10px;
`;

const SubTitle = styled.div`
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 24px;
`;

const SellMenu = styled.div`
  display: flex;

  button {
    border: none;
    width: 33%;
    box-sizing: border-box;
    display: block;
    padding: 20px 0;
    font-weight: bold;
    font-size: 20px;
    background-color: #ffffff;
    border-bottom: 1px solid #ddd;
    color: #999;
    cursor: pointer;
  }

  // NOW_SELL
  ${(props) =>
    props.active === 0 &&
    css`
      button:first-of-type {
        border-bottom: 3px solid #333;
        color: #333;
      }
    `}

  // COMPLETE_SELL
  ${(props) =>
    props.active === 1 &&
    css`
      button:nth-of-type(2) {
        border-bottom: 3px solid #333;
        color: #333;
      }
    `}

    // NOW_SELL
  ${(props) =>
    props.active === 2 &&
    css`
      button:last-of-type {
        border-bottom: 3px solid #333;
        color: #333;
      }
    `}
`;

const NotFound = styled.div`
  display: flex;
  height: 100px;
  align-items: center;
  justify-content: center;
`;

// 모달 스타일
const ButtonModify = styled.button`
  width: 100%;
  height: 50px;
  border-top-left-radius: 15px;
  border-top-right-radius: 15px;
  background-color: whitesmoke;
  color: #6bb7e0;
  font-size: 13px;
  border: 0;
  border-bottom: 1px solid #dadada;
`;

const ButtonWrap = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
`;

const ButtonDelete = styled.button`
  width: 100%;
  height: 50px;
  border-bottom-left-radius: 15px;
  border-bottom-right-radius: 15px;
  background-color: whitesmoke;
  color: red;
  font-size: 13px;
  border: 0;
`;
const Book = styled.div`
  padding: 6px 5px;
  border-radius: 5px;
  width: 55px;
  background-color: #34bf9e;
  color: white;
  font-size: 12px;
  text-align: center;
  font-weight: 500;
  margin-right: 5px;
`;

export default SalesList;
