import React from 'react';
import { SlArrowLeft } from "react-icons/sl";
import '../style/css/dropdownMenu.css';
import { useNavigate } from "react-router-dom";


function TopArrowLeft({onBackClick}) {
  const navigate = useNavigate();

  return (
    <div className="arrow-menu">
        <SlArrowLeft size="30" onClick={() => navigate(-1)} />
    </div>
  );
}

export default TopArrowLeft;