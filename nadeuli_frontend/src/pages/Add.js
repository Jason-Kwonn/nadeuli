import styled from "styled-components";
import { IoIosClose, IoIosCamera } from "react-icons/io";
import { useNavigate } from "react-router-dom";
import { useState, useRef, useEffect } from "react";
// import { getDownloadURL, ref, uploadBytes } from "firebase/storage";
// import { storage } from "../shared/firebase";
import { carrotPost } from "../redux/modules/post";
import { useDispatch, useSelector } from "react-redux";

// 이미지 업로드

function Add() {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const fileInput = useRef();
  const img_ref = useRef();
  const title_ref = useRef();
  const price_ref = useRef();
  const content_ref = useRef();
  const premium_ref = useRef();
  const premiumTime_ref = useRef();
  const isBargain_ref = useRef();
  const [isBargain, setBargain] = useState(false);
  const [isPremium, setPremium] = useState(false);
  const [premiumTime, setPremiumTime] = useState();
  const [selectedImages, setSelectedImages] = useState([]);
  const [previewImages, setPreviewImages] = useState([]);
  const [enteredNum, setEnterdNum] = useState();
  const [price, setPrice] = useState(0);
  const post = useSelector((state) => state.post.postList);
  const member = useSelector((state) => state.member);
  
  const location = useSelector((state) => state.member.gu);

  // useEffect(() => {
  //   if (price) {
  //     chk_ref.current.checked = true;
  //     chk_ref.current.disabled = false; // 비활성화
  //   } else {
  //     chk_ref.current.checked = false;
  //     chk_ref.current.disabled = true;
  //   }
  // }, [price]);

  useEffect(() => {
    console.log(member);

  },[member])

  // 파일 업로드
  const selectFile = async (e) => {
    
    const files = fileInput.current.files;

    if (!files || files.length === 0) {
      console.error('No files selected.');
      return;
    }

    // 파일 미리보기 생성
    Array.from(files).forEach((file) => {
      const reader = new FileReader();
      reader.onloadend = () => {
        setPreviewImages((prevPreviews) => [...(prevPreviews || []), reader.result]);
      };
      reader.readAsDataURL(file);
    });
    // 선택된 이미지들을 상태에 저장
    setSelectedImages(files);
  };

  // 금액 콤마(,) 찍기

  const priceComma = (e) => {
    setPrice(e.target.value);
    let value = e.target.value;
    value = Number(value.replaceAll(",", ""));
    if (isNaN(value)) {
      //NaN인지 판별
      value = 0;
    } else {
      setEnterdNum(value.toLocaleString("ko-KR"));
    }
  };

  // 콤마제거
  const commaRemovePrice = enteredNum?.replace(/,/g, ""); // g -> global
  let numberPrice = parseInt(commaRemovePrice);
  console.log(numberPrice);

  const upload = () => {
    const files = fileInput.current.files;
    const title = title_ref.current.value;
    const content = content_ref.current.value;
    const price = numberPrice;
    //const location = location;
    // !location 잠시 빼둠
    if (!title || !content || price === undefined) {
      alert('모든 칸을 입력해주세요.');
      return;
    }

    if (!files || files.length === 0) {
      console.error('No files selected.');
      return;
    }

    if (member.nadeuliPayBalance < premiumTime*100){
      alert('나드리페이 잔액이 부족합니다 충전페이지로 이동합니다');
      navigate("/nadeuliPay/nadeuliPayCharge");
      return;
    }
    console.log('체크박스상태 :', isBargain);
    const seller = { tag: member.tag };
    const formData = new FormData();
    formData.append('title', title);
    formData.append('content', content);
    formData.append('price', price);
    formData.append('gu', location);
    formData.append('tradingLocation', location);
    formData.append('isPremium', premium_ref.current.checked);
    formData.append('isBargain', isBargain_ref.current.checked);
    if(premiumTime){
      formData.append('premiumTime', premiumTime_ref.current.value);
    }
    for (let i = 0; i < files.length; i++) {
      formData.append('image', files[i]);
    }
    formData.append('seller.tag', member.tag);


    console.log(member);
    dispatch(carrotPost(formData, navigate));
  };

  return (
    <Wrap>
      <Header>
        <IoIosClose
          size="25"
          onClick={() => {
            navigate("/main");
          }}
        />
        <h4>중고거래 글쓰기</h4>
        <h5 onClick={upload}>완료</h5>
      </Header>

      {/* 사진업로드 */}
      <Container>
        <File>
          <label htmlFor="file">
            <IoIosCamera className="camera" />
          </label>
          <input type="file" id="file" ref={fileInput} onChange={selectFile} multiple/>
          {previewImages.map((preview, index) => (
        <img
          key={index}
          src={preview}
          alt={`Preview ${index + 1}`}
          style={{ maxWidth: '100px', maxHeight: '100px', margin: '5px' }}
        />
      ))}
          {/* {previewImages && <img src={previewImages} alt="preview-img" />} */}
        </File>

        <div>
          <Title>
            <input placeholder="글 제목" ref={title_ref} />
          </Title>
          <br/>
          
          <br/>
          <p style={{ marginLeft: '5px' }}>나의 나드리페이 잔액: {member.nadeuliPayBalance.toLocaleString()}원</p>
          <Categorie>
            {/* <div>카테고리 선택</div> */}
            <select name="premiumTime" id="premiumTime" value={premiumTime} ref={premiumTime_ref} onChange={(e) => setPremiumTime(e.target.value)} disabled={!isPremium}>
              <option value="0">프리미엄 시간 설정</option>
              <option value="1">1시간(100원)</option>
              <option value="2">2시간(200원)</option>
              <option value="3">3시간(300원)</option>
            </select>
            <label htmlFor="isPremium">
            <input type="checkbox" id="isPremium" checked={isPremium} ref={premium_ref} onChange={(e) => setPremium(e.target.checked)}/>
              프리미엄 설정하기
            </label>
            {/* <IoIosArrowForward /> */}
          </Categorie>

          {/* <Locate>
             <div>{location}</div> 
             <IoIosArrowForward /> 
          </Locate> */}
        </div>
        
        <Price>
          
          <input
            type="text"
            placeholder="가격"
            ref={price_ref}
            onChange={priceComma}
            value={enteredNum || ""}
          />
          <label htmlFor="price">
            <input type="checkbox" id="isBargain" onChange={(e) => setBargain(e.target.checked)} ref={isBargain_ref}/>
            가격 흥정 받기
          </label>
        </Price>
          
        <textarea
          cols="40"
          rows="5"
          placeholder="올릴 게시글 내용을 작성해주세요. (가품 및 판매금지품목은 게시가 제한될 수 있어요.)"
          ref={content_ref}
        />
      </Container>
    </Wrap>
  );
}
const Wrap = styled.div`
  box-sizing: border-box;
  font-size: 13px;

  input {
    font-size: 13px;
  }

  textarea {
    margin-top: 45px;
    border: none;
    outline: none;
    resize: none;
  }
  textarea::placeholder {
    color: #dadada;
    font-size: 13px;
  }
`;
const Header = styled.header`
  display: flex;
  justify-content: space-between;
  align-items: center;

  padding: 16px 15px;
  border-bottom: 1px solid #dadada;

  h4 {
    font-weight: 800;
  }
  h5 {
    color: #ff7e36;
  }
`;
const Container = styled.div`
  padding: 0 16px;
`;

const File = styled.div`
  padding: 30px 0px;
  border-bottom: 1px solid #dadada;

  .camera {
    font-size: 35px;
  }
  label {
    cursor: pointer;
  }
  input[type="file"] {
    display: none;
  }

  img {
    width: 130px;
    height: 130px;
    margin-left: 10px;
    border-radius: 5px;
  }
`;

const Title = styled.div`
  padding: 20px 0px;
  border-bottom: 1px solid #dadada;

  outline: none;
  input {
    border: none;
    outline: none;
  }

  input::placeholder {
    color: #dadada;
  }
`;
const Categorie = styled(Title)`
  display: flex;
  justify-content: space-between;

  select {
    width: 60%;
    border: none;
    outline: none;
    font-size: 14px;
  }
`;

const Locate = styled(Title)`
  display: flex;
  justify-content: space-between;
`;

const Price = styled(Title)`
  white-space: pre-line
  display: flex;
  align-items: center;
  justify-content: space-between;

  input[type="text"] {
    width: 61%;
  }
`;

export default Add;