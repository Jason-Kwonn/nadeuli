import * as React from 'react';
import Button from '@mui/material/Button';
import ButtonGroup from '@mui/material/ButtonGroup';
import Box from '@mui/material/Box';

export default function NadeuliButton(props) {
  const {handleAll, handleCharge, handleWithdraw, handlePayment} = props;
  return (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        '& > *': {
          m: 1,
        },
      }}
    >
      <ButtonGroup variant="text" aria-label="text button group">
      <Button onClick={handleAll}>전체</Button>
        <Button onClick={handleCharge}>충전</Button>
        <Button onClick={handleWithdraw}>출금</Button>
        <Button onClick={handlePayment}>결제</Button>
      </ButtonGroup>
    </Box>
  );
}