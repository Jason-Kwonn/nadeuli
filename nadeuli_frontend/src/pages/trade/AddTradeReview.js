import { useState, useRef, useEffect } from "react";
import { useSelector } from "react-redux";
import { useNavigate, useParams } from "react-router-dom";
import React from "react";
import { post } from "../../util/axios"
import { Button } from "@mui/material";
import HeaderBack from "../../components/HeaderBack";

function AddTradeReview() {
  const navigate = useNavigate();
  const { productId } = useParams();
  const [score, setScore] = useState("");
  const [review, setReview] = useState("");

  const member = useSelector((state) => state.member);


  const handleScoreChange = (e) => {
    setScore(e.target.value);
  };

  const handleReviewChange = (e) => {
    setReview(e.target.value);
  };
  

  const handleButtonClick = async () => {
      console.log("score : " + score);
      console.log("review : " + review);
      const formData = new FormData();
      formData.append('affinityScore', score);
      formData.append('content', review);
      formData.append('writer.tag', member.tag);
      formData.append('product.productId', productId);
      // 임시로 해둠 trader
      formData.append('trader.tag', member.tag);

      
      await post("/trade/addTradeReview", formData)
        .then((res) => {
          console.log(res);
          navigate("/main");
        })
        .catch((err) => {
          console.log(err);
        });
  };

  return (
    <div>
      <HeaderBack title="후기 남기기" />

      {/* 큰 입력 텍스트 필드 */}
      <div style={{ textAlign: "center", marginTop: 200 }}>
        <input
          type="text"
          value={score}
          onChange={handleScoreChange}
          style={{
            width: "70%",
            padding: 5,
            fontSize: 16,
            borderRadius: 8,
            border: "1px solid #ccc",
            textAlign: "left",
          }}
          placeholder="후기 점수(0~100점)"
        />
      </div>
  
      {/* 큰 입력 텍스트 필드 */}
      <div style={{ textAlign: "center", marginTop: 20 }}>
        <input
          type="text"
          value={review}
          onChange={handleReviewChange}
          style={{
            width: "70%",
            padding: 25,
            fontSize: 16,
            borderRadius: 8,
            border: "1px solid #ccc",
            textAlign: "left",
          }}
          placeholder="후기를 남겨주세요"
        />
      </div>
  
      {/* 버튼 */}
      <div style={{ textAlign: "center", marginTop: 20 }}>
        <Button
          variant="contained"
          disableElevation
          style={{
            backgroundColor: "yourColor",
            color: "yourTextColor",
            width: "70%",  // 입력 텍스트 필드와 동일한 좌우 길이
          }}
          onClick={handleButtonClick}
        >
          작성 완료
        </Button>
      </div>
    </div>
  );
}

export default AddTradeReview;