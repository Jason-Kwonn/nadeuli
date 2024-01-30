import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";
import React from "react";
import { post } from "../../util/axios"
import NadeuliPay from "../../components/NadeuliPay";
import YesNoModal from "../../components/YesNoModal";

function NadeuliPayWithdraw() {
    const [inputNumber, setInputNumber] = useState('');
    const [payMoney, setPayMoney] = useState(null);
    const [open, setOpen] = React.useState(false);

    const navigate = useNavigate();
    const member = useSelector((state) => state.member);

    useEffect(() => {
        setPayMoney(inputNumber);
    }, [inputNumber]);

    const handlePayButton = () => {
        setOpen(true);
        console.log(open);
      };

    const withdraw = () => {
        return async function () {
            const member = {'tag': member.tag}
            let bankAccountBackNum = member.bankAccountNum;
            bankAccountBackNum = bankAccountBackNum.substring(bankAccountBackNum.length()-4, bankAccountBackNum.length());
            const formData = new FormData();
            formData.append('tradingMoney', payMoney);
            formData.append('member.tag', member.tag);
            formData.append('bankName', member.bankName);
            formData.append('bankAccountBackNum', bankAccountBackNum);

            
            await post("/nadeuliPay/nadeuliPayWithdraw", formData)
              .then((res) => {
                console.log(res);
                navigate("/main");
              })
              .catch((err) => {
                console.log(err);
              });
          };
        
    }


    return (
        <div>
            <NadeuliPay 
                title='나드리페이 출금'
                buttonName='출금하기'
                handleButton={handlePayButton}
                setInputNumber={setInputNumber}
                inputNumber={inputNumber}
                setPayMoney={setPayMoney}
            />
            <YesNoModal
                title='출금 확인 알림'
                content='정말 출금하시겠어요?'
                open={open}
                setOpen={setOpen}
                callBack={withdraw}
            />
        </div>
    );
}

export default NadeuliPayWithdraw;