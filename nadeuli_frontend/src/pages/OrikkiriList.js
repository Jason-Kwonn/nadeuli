import { useState, useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { allOrikkiriList } from '../redux/modules/orikkiri';
import '../style/css/postInfo.css';
import styled from 'styled-components';
import { useNavigate } from 'react-router-dom';
import * as React from 'react';
import AspectRatio from '@mui/joy/AspectRatio';
import Card from '@mui/joy/Card';
import CardContent from '@mui/joy/CardContent';
import CardOverflow from '@mui/joy/CardOverflow';
import Typography from '@mui/joy/Typography';
import { MDBBtn } from 'mdb-react-ui-kit';

function OrikkiriList() {
    const memberDongNe = useSelector((state) => state.member.dongNe);
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const [currentPage, setCurrentPage] = useState(0);

    const handleSignUpClick = (orikkiriId) => {
        console.log(orikkiriId);
        navigate(`/addOrikkiriSignUp/${orikkiriId}`);
    };

    useEffect(() => {
        dispatch(allOrikkiriList(memberDongNe));
    }, [dispatch]);

    const OrikkiriList = useSelector((state) => state.orikkiri.orikkiriList);

    const filteredOrikkiriList = OrikkiriList.filter((orikkiri) => orikkiri.dongNe === memberDongNe);

    return (
        <div className="MainListBox">
            {filteredOrikkiriList.map((orikkiri, index) => {
                const hasPicture = orikkiri.orikkiriPicture;
                return (
                    <div
                        key={orikkiri.orikkiriId || index}
                        style={{ position: 'relative' }}
                    >
                        {/* <CardBox className="card" onClick={() => navigate("/orikkiriHome/" + orikkiri.orikkiriId)}> */}
                        <CardBox className="card">
                            {hasPicture && (
                                <Img
                                    src={orikkiri?.orikkiriPicture}
                                    alt="Orikkiri Image"
                                />
                            )}
                            <TextArea>
                                <span>{orikkiri.orikkiriName}</span>
                            </TextArea>
                            <div>{orikkiri.orikkiriIntroduction}</div>
                            <MDBBtn
                                style={{ position: 'absolute', right: '20px', top: '50%', transform: 'translateY(-50%)' }}
                                onClick={() => handleSignUpClick(orikkiri.orikkiriId)}
                            >
                                상세 조회
                            </MDBBtn>
                        </CardBox>
                    </div>
                );
            })}
        </div>
    );
}

const CardBox = styled.div`
    display: flex;
    padding: 20px;
    justify-content: space-between;
    border-bottom: 1px solid #dddddd;
`;

const TextArea = styled.div`
    display: flex;
    flex-direction: column;
    width: 100%;
    padding: 10px;
`;

const Img = styled.img`
    width: 100px;
    height: 100px;
    border-radius: 10px;
    object-fit: cover;
`;

export default OrikkiriList;
