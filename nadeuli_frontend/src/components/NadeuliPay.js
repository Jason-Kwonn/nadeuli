import Button from '@material-ui/core/Button';
import HeaderBack from './HeaderBack';
import React from 'react';
import { useEffect } from 'react';
import styled from 'styled-components';

const NadeuliPay = ({ title, buttonName, handleButton, inputNumber, setInputNumber, setPayMoney }) => {
    useEffect(() => {
        setPayMoney(inputNumber);
    }, [inputNumber]);

    return (
        <div
            style={{
                marginTop: '20px',
                marginLeft: '10px',
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
            }}
        >
            <TMenuBar>
                <HeaderBack />
                <p style={{ fontWeight: 'bold', fontSize: '20px' }}>나드리페이 충전</p>
            </TMenuBar>
            <div
                style={{
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center', // Center align
                    justifyContent: 'space-between',
                    position: 'relative',
                    height: '400px',
                }}
            >
                <img
                    src="/nadeuli_icon.ico"
                    alt="NadeuliPay"
                    style={{ position: 'absolute', display: 'block', marginTop: '100px', width: '200px' }}
                />
                <div style={{ marginTop: '300px' }}>
                    <input
                        type="number"
                        placeholder="금액을 입력해주세요."
                        value={inputNumber}
                        onChange={(e) => setInputNumber(e.target.value)}
                        style={{
                            marginTop: '10%',
                            marginBottom: '60px',
                            padding: '10px',
                            width: '250px', // Adjust width
                        }}
                    />
                    <Button
                        variant="contained"
                        disableElevation
                        style={{ margin: '0 auto', display: 'block', backgroundColor: '#1976d2', color: 'white', marginBottom: '10px' }}
                        onClick={handleButton}
                    >
                        {buttonName}
                    </Button>
                </div>
            </div>
        </div>
    );
};

const TMenuBar = styled.div`
    position: relative;
    width: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;
    text-align: center;

    p {
        position: absolute;
        left: 50%;
        transform: translateX(-50%);
        margin: 0;
    }
`;

export default NadeuliPay;
