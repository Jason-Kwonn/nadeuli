
import React, { useState, useRef, useEffect } from "react";
import { IoIosClose } from "react-icons/io";
import { useNavigate, useParams} from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import styled from "styled-components";
import {dongNePost, modifyDongNePost,GetDongNePostDetail } from "../../redux/modules/dongNePost";

function UpdateDongNePost() {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const params = useParams();
  const postId = params.postId;
  const getDongNePost = useSelector((state) => state.dongNePost.dongNePost);
  const title_ref = useRef(null);
  const content_ref = useRef(null);
  const [category, setCategory] = useState();
  const fileInput = useRef(); // 하나의 file input으로 모든 파일 처리
  const [previewImages, setPreviewImages] = useState([]);
  const getMember = useSelector((state) => state.member);
  const location = useSelector((state) => state.member.gu);
  const member = useSelector((state) => state.member);

  useEffect(() => {
    console.log(member);
  }, [member]);

  useEffect(() => {
    dispatch(GetDongNePostDetail(postId));
  }, [postId]);

  useEffect(() => {
    // 게시물 데이터가 로드되면 input 참조에 값을 할당
    if (getDongNePost) {
      title_ref.current.value = getDongNePost?.title;
      content_ref.current.value = getDongNePost?.content;
      setCategory(getDongNePost?.category);  // 카테고리 상태도 업데이트
      // 이미지 미리보기나 다른 상태를 업데이트할 수도 있음
    }
  }, [getDongNePost]);  // getDongNePost가 변경될 때마다 실행


  const changeCategory = (e) => {
    setCategory(e.target.value);
  };

  // 파일 업로드 및 미리보기 생성
  const selectFile = (e) => {
    const files = e.target.files;
    if (!files || files.length === 0) {
      console.error('No files selected.');
      return;
    }

    // 파일 미리보기 생성
    Array.from(files).forEach(file => {
      const reader = new FileReader();
      reader.onloadend = () => {
        setPreviewImages(prev => [...prev, reader.result]);
      };
      reader.readAsDataURL(file);
    });
  };

  console.log(postId)

  const addDongNePost = () => {
    // const orikkiri = {orikkiriId: }
    const writer = {tag: member.tag};
    const title = title_ref.current.value;
    const content = content_ref.current.value;
    const postCategory = (category === "잡담" ? 1 : 2);

    if (!title || !content) {
      alert('모든 칸을 입력해주세요.');
      return;
    }

    if (!category || category === "none") {
      alert("게시물 카테고리를 선택해주세요!");
      return;
    }
    const formData = new FormData();
    const postDTOData = {
      postId: postId,
      title,
      content,
      postCategory,
      gu: location,
      dongNe: getMember.dongNe,
      writer: writer
    };

    formData.append('postDTO', new Blob([JSON.stringify(postDTOData)], { type: "application/json" }));

    // 파일 추가
    const files = fileInput.current.files;
    for (let i = 0; i < files.length; i++) {
      formData.append('images', files[i]);
    }
    dispatch(modifyDongNePost(formData, navigate, `/GetDongNePost/${postId}`));
  };

  return (
    <Wrap>
      <Header>
        <IoIosClose
          size="30"
          onClick={() => navigate("/dongNeHome")}
        />
        <h4>동네나드리 수정</h4>
        <h5 onClick={addDongNePost}>완료</h5>
      </Header>

      <Container>
        <div>
          <Title>
            <input placeholder="제목을 입력하세요" ref={title_ref} />
          </Title>

          <Categorie>
            <select name="category" id="category" onChange={changeCategory}>
              <option value="none">게시물 카테고리를 선택해주세요.</option>
              <option value="잡담">잡담</option>
              <option value="홍보">홍보</option>
            </select>
          </Categorie>
        </div>

        <textarea
          cols="40"
          rows="5"
          placeholder={`${location}와 관련된 질문이나 이야기를 해보세요.`}
          ref={content_ref}
        />

        {/* 파일 업로드 */}
        <File>
          <label htmlFor="file-input">
            파일 선택
          </label>
          <input type="file" id="file-input" ref={fileInput} onChange={selectFile} multiple />
          {previewImages.map((preview, index) => (
            <img
              key={index}
              src={preview}
              alt={`Preview ${index + 1}`}
              style={{ maxWidth: '100px', maxHeight: '100px', margin: '5px' }}
            />
          ))}
        </File>
      </Container>
    </Wrap>
  );
}


const Wrap = styled.div`
box-sizing: border-box;
font-size: 18px;
max-width: 100%; /* 최대 너비 설정 */

input {
  font-size: 13px;
}

textarea {
  margin-top: 45px;
  border: none;
  outline: none;
  resize: none;
  font-size: 20px;
  height: 400px; /* 높이 조절 */
  width: 100%; /* 너비 100% 설정 */
  padding: 10px; /* 내부 여백 추가 */
}
textarea::placeholder {
  color: #dadada;
  font-size: 20px;
}
`;
const Header = styled.header`
display: flex;
justify-content: space-between;
align-items: center;

padding: 25px 25px;
border-bottom: 1px solid #dadada;

h4 {
  font-weight: bold;
  font-size: 20px; /* 원하는 크기로 수정 */
}
h5 {
  color: #4da6ff;
  font-size: 18px;
}
`;
const Container = styled.div`
padding: 0 16px;
`;

const File = styled.div`
padding: 30px 0px;
border-bottom: 1px solid #dadada;

.camera {
  font-size: 50px;
  margin-right: 10px; 
}
.video {
  font-size: 50px;
}
label {
  cursor: pointer;
}
input[type="file"] {
  display: none;
}

img {
  width: 50px;
  height: 50;
  margin-left: 10px;
  border-radius: 5px;
}
`;

const Title = styled.div`
padding: 25px 0px;
border-bottom: 1px solid #dadada;

outline: none;
input {
  border: none;
  outline: none;
  font-size: 25px;
  width: 100%; /* 너비 100% 설정 */
}

input::placeholder {
  color: #dadada;
  font-size: 25px;
}
`;

const Categorie = styled(Title)`
display: flex;
justify-content: space-between;

select {
  width: 100%;
  border: none;
  outline: none;
  font-size: 20px;
}
`;


export default UpdateDongNePost;