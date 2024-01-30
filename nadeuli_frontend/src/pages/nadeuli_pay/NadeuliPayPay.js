import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useSelector } from "react-redux";
import React from "react";
import { post } from "../../util/axios"
import NadeuliPay from "../../components/NadeuliPay";
import YesNoModal from "../../components/YesNoModal";

function NadeuliPayPay() {
    const [inputNumber, setInputNumber] = useState('');
    const [payMoney, setPayMoney] = useState(null);
    const [open, setOpen] = React.useState(false);
    const params = useParams();
    const navigate = useNavigate();
    const member = useSelector((state) => state.member);

    useEffect(() => {
        setPayMoney(inputNumber);
    }, [inputNumber]);

    const handlePayButton = () => {
        setOpen(true);
        console.log(open);
      };

    const pay = () => {
        return async function () {
            const member = {'tag': member.tag}
            const formData = new FormData();
            formData.append('tradingMoney', payMoney);
            formData.append('member.tag', member.tag);
            // 상품이름
            // 상품
            
            await post("/nadeuliPay/nadeuliPayPay")
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
                title='나드리페이 결제'
                buttonName='결제하기'
                handleButton={handlePayButton}
                setInputNumber={setInputNumber}
                inputNumber={inputNumber}
                setPayMoney={setPayMoney}
            />
            <YesNoModal
                title='결제 확인 알림'
                content='정말 결제하시겠어요?'
                open={open}
                setOpen={setOpen}
                callBack={pay}
            />
        </div>
    );
}

export default NadeuliPayPay;