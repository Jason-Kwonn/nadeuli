import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";
import React from "react";

import PayModal from "../../components/PayModal";
import NadeuliPay from "../../components/NadeuliPay";


function NadeuliPayCharge() {
  const navigate = useNavigate();
  const member = useSelector((state) => state.member);
  const [inputNumber, setInputNumber] = useState('');
  const [open, setOpen] = React.useState(false);
  const [selectedValue, setSelectedValue] = React.useState();
  const [payMoney, setPayMoney] = useState(null);

  useEffect(() => {
    setPayMoney(inputNumber);
  }, [inputNumber]);

  const handleClose = (value) => {
    setOpen(false);
    setSelectedValue(value);
  };

  const handleChargeButton = () => {
    setOpen(true);
  };

  const requestPay = () => {
    const IMP  = window.IMP;
    IMP.init("imp32856038");

    const data = {
        pg: "kakaopay.TC0ONETIME", // PG사
        pay_method: "card", // 결제수단
        merchant_uid: `mid_${new Date().getTime()}`, // 주문번호
        amount: 10, // 결제금액
        name: "test", // 주문명
        buyer_name: member.nickname, // 구매자 이름
        buyer_tel: member.cellphone, // 구매자 전화번호
        buyer_email: member.email, // 구매자 이메일
        buyer_addr: member.dongNe, // 구매자 주소
        buyer_postcode: member.postcode, // 구매자 우편번호
    };

    IMP.request_pay(data, (res) => {
        console.log(res);
    });
  }

  return (
    <div>
        <NadeuliPay 
            title='나드리페이 충전'
            buttonName='충전하기'
            handleButton={handleChargeButton}
            setInputNumber={setInputNumber}
            inputNumber={inputNumber}
            setPayMoney={setPayMoney}
            />
        <PayModal
            selectedValue={selectedValue}
            open={open}
            onClose={handleClose}
            payMoney={payMoney}
        />
      </div>
  );
}

export default NadeuliPayCharge;