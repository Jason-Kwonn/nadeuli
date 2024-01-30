import styled from "styled-components";
import { FaArrowLeft } from "react-icons/fa";
import { Link, useNavigate } from "react-router-dom";

function HeaderBack({ title }) {
  const navigate = useNavigate();

  return (
    <Box>
      <FaArrowLeft onClick={() => navigate(-1)} />
      <HeaderText>{title}</HeaderText>
    </Box>
  );
}

const Box = styled.div`
  height: 30px;
  display: flex;
  align-items: center;
  font-size: 24px;
  padding: 0 15px;
`;

const HeaderText = styled.div`
  font-weight: bold;
  font-size: 20px;
  margin-left: 23%;
`;

export default HeaderBack;