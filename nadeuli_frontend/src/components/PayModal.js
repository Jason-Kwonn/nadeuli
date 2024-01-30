import * as React from 'react';
import PropTypes from 'prop-types';
import Button from '@mui/material/Button';
import Avatar from '@mui/material/Avatar';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemAvatar from '@mui/material/ListItemAvatar';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemText from '@mui/material/ListItemText';
import DialogTitle from '@mui/material/DialogTitle';
import Dialog from '@mui/material/Dialog';
import PersonIcon from '@mui/icons-material/Person';
import AddIcon from '@mui/icons-material/Add';
import { blue } from '@mui/material/colors';
import { useSelector } from "react-redux";
import { post } from "../util/axios";
import { useNavigate } from "react-router-dom";

const payments = ['토스페이', '카카오페이'];

function PayModal(props) {
  const navigate = useNavigate();
  const member = useSelector((state) => state.member);
  const { onClose, selectedValue, open, payMoney } = props;

  const requestTossPay = async (payMoney) => {
    const IMP  = window.IMP;
    IMP.init("imp32856038");

    console.log(payMoney);
    const data = {
        pg: "tosspay.tosstest", // PG사
        pay_method: "card", // 결제수단
        merchant_uid: `mid_${new Date().getTime()}`, // 주문번호
        amount: payMoney, // 결제금액
        name: "test", // 주문명
        buyer_name: member.nickname, // 구매자 이름
        buyer_tel: member.cellphone, // 구매자 전화번호
        buyer_email: member.email, // 구매자 이메일
        buyer_addr: member.dongNe, // 구매자 주소
        buyer_postcode: member.postcode, // 구매자 우편번호
        m_redirect_url: "https://nadeuli.kr/nadeuliPay/charge",
    };

    IMP.request_pay(data, (res) => {
        return async function () {
            await post("/nadeuliPayCharge", res)
            .then((res)=>{
                console.log(res);
                navigate("/main");
            })
            .catch((err) => {
                console.log(err);
              });
            console.log(res);

        }
    });
  }


  const requestKakaoPay = async (payMoney) => {
    const IMP  = window.IMP;
    IMP.init("imp32856038");
    console.log(payMoney);
    const data = {
        pg: "kakaopay.TC0ONETIME", // PG사
        pay_method: "card", // 결제수단
        merchant_uid: `mid_${new Date().getTime()}`, // 주문번호
        amount: payMoney, // 결제금액
        name: "test", // 주문명
        buyer_name: member.nickname, // 구매자 이름
        buyer_tel: member.cellphone, // 구매자 전화번호
        buyer_email: member.email, // 구매자 이메일
        buyer_addr: member.dongNe, // 구매자 주소
        buyer_postcode: member.postcode, // 구매자 우편번호
        m_redirect_url: "https://nadeuli.kr/nadeuliPay/charge",
    };

    IMP.request_pay(data, (res) => {
      alert(res);
            post("/nadeuliPayCharge", res)
            .then((res)=>{
                console.log(res);
                navigate("/main");
            })
            .catch((err) => {
                console.log(err);
              });
            console.log(res);

        
    });
  }

  const handleClose = () => {
    onClose(selectedValue);
  };

  const handleListItemClick = (value) => {
    switch (value) {
        case '토스페이':
          console.log(payMoney);
          requestTossPay(payMoney)
          console.log('토스페이 선택!');
          // 추가로 수행할 작업을 여기에 추가할 수 있습니다.
          break;
        case '카카오페이':
          requestKakaoPay(payMoney);
          console.log('카카오페이 선택!');
          // 추가로 수행할 작업을 여기에 추가할 수 있습니다.
          break;
    }
  };

  return (
    <Dialog onClose={handleClose} open={open}>
      <DialogTitle>결제 수단</DialogTitle>
      <List sx={{ pt: 0 }}>
        {payments.map((payment) => (
          <ListItem disableGutters key={payment}>
            <ListItemButton onClick={() => handleListItemClick(payment)}>
              <ListItemAvatar>
                <Avatar sx={{ bgcolor: blue[100], color: blue[600] }}>
                  <PersonIcon />
                </Avatar>
              </ListItemAvatar>
              <ListItemText primary={payment} />
            </ListItemButton>
          </ListItem>
        ))}
      </List>
    </Dialog>
  );
}


export default PayModal;