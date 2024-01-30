import React, { useEffect, useRef, useState } from 'react';
import {
    MDBCol,
    MDBContainer,
    MDBRow,
    MDBCard,
    MDBCardText,
    MDBCardBody,
    MDBCardImage,
    MDBBtn,
    MDBBreadcrumb,
    MDBBreadcrumbItem,
    MDBProgress,
    MDBProgressBar,
    MDBIcon,
    MDBListGroup,
    MDBListGroupItem,
    MDBModal,
    MDBModalDialog,
    MDBModalContent,
    MDBModalHeader,
    MDBModalTitle,
    MDBModalBody,
    MDBModalFooter,
    MDBInput,
} from 'mdb-react-ui-kit';
import { useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import HeaderBack from '../../components/HeaderBack';
import styled from 'styled-components';
import { BsHeart } from 'react-icons/bs';
import { get } from '../../util/axios';

export default function GetOtherProfile() {
    // hooks
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const otherMember = useSelector((state) => state.otherMember);
    const [productList, setProductList] = useState([]);
    console.log(otherMember);
    // useEffect
    useEffect(() => {
        getMyProductList(otherMember.tag, 0);
    }, []);

    const getMyProductList = async (tag, currentPage) => {
        try {
            const response = await get(`/product/getMyProductList/${tag}/${currentPage}`);
            console.log(response[0]);
            setProductList(response);
        } catch (error) {
            console.error('Error fetching product list:', error);
        }
    };

    return (
        <section style={{ backgroundColor: '#eee' }}>
            <MDBContainer>
                <MDBRow>
                    <MDBCol>
                        <MDBBreadcrumb className="bg-light rounded-3 p-3 mb-4">
                            <TMenuBar>
                                <HeaderBack />
                                <p>상대 프로필</p>
                            </TMenuBar>
                        </MDBBreadcrumb>
                    </MDBCol>
                </MDBRow>

                <MDBRow>
                    <MDBCol lg="4">
                        <MDBCard className="mb-4">
                            <MDBCardBody className="text-center">
                                <MDBCardImage
                                    src={otherMember.picture}
                                    alt="picture"
                                    className="rounded-circle"
                                    style={{ width: '150px', height: '150px', cursor: 'pointer' }}
                                    fluid
                                />
                                <p className="text-muted mb-1 mt-2">
                                    {otherMember.nickname}
                                    <b style={{ fontSize: '14px', color: 'black' }}>#{otherMember.tag}</b>
                                </p>
                                <p className="text-muted mb-1 mt-2">{otherMember.dongNe}</p>
                                <p className="text-muted mb-1 mt-2">친화력 {otherMember.affinity}점</p>
                            </MDBCardBody>
                        </MDBCard>
                    </MDBCol>
                    <MDBCol lg="8">
                        <MDBRow>
                            <MDBCol md="6">
                                <MDBCard className="mb-4 mb-lg-0">
                                    <MDBCardBody className="p-0">
                                        <MDBListGroup
                                            flush
                                            className="rounded-3"
                                        >
                                            <MDBListGroupItem className="d-flex justify-content-between align-items-center p-3">
                                                <b>&nbsp;즐겨찾기 목록</b>
                                            </MDBListGroupItem>
                                            {productList.map((list, index) => (
                                                <MDBListGroupItem
                                                    key={index}
                                                    className="d-flex justify-content-between align-items-center p-3 position-relative"
                                                >
                                                    <div
                                                        style={{ display: 'flex' }}
                                                        onClick={() => {
                                                            navigate(`/detail/${list.productId}/${list.tradeState}`);
                                                        }}
                                                    >
                                                        <Img src={list?.images[0]} />
                                                        <TextArea>
                                                            <span
                                                                style={{
                                                                    fontSize: '15px',
                                                                    padding: '0 5px',
                                                                    width: '200px',
                                                                    textOverflow: 'ellipsis',
                                                                    overflow: 'hidden',
                                                                    whiteSpace: 'nowrap',
                                                                }}
                                                            >
                                                                {list?.title}
                                                            </span>
                                                            <span
                                                                style={{
                                                                    fontSize: '13px',
                                                                    padding: '0 5px',
                                                                    fontWeight: 'bold',
                                                                }}
                                                            >
                                                                {list?.seller?.dongNe.split(' ')[2]} &nbsp;{list?.product?.timeAgo}
                                                            </span>
                                                            <TradeState>
                                                                <span
                                                                    style={{
                                                                        fontSize: '13px',
                                                                        padding: '5px',
                                                                        fontWeight: 'bold',
                                                                    }}
                                                                >
                                                                    {Number(list?.price).toLocaleString('ko-KR')}원
                                                                </span>
                                                            </TradeState>
                                                            {list?.isBargain ? (
                                                                <span style={{ color: 'blue', marginLeft: '5px' }}>가격 흥정 가능</span>
                                                            ) : (
                                                                <span style={{ color: 'gray', marginLeft: '5px' }}>가격 흥정 불가능</span>
                                                            )}
                                                            {list?.isPremium && (
                                                                <span
                                                                    style={{
                                                                        color: 'red',
                                                                        position: 'absolute',
                                                                        top: 0,
                                                                        right: 0,
                                                                        fontSize: '12px',
                                                                        padding: '5px',
                                                                        fontWeight: 'bold',
                                                                    }}
                                                                >
                                                                    프리미엄
                                                                </span>
                                                            )}
                                                        </TextArea>
                                                    </div>
                                                    <div
                                                        style={{
                                                            display: 'flex',
                                                            alignItems: 'center',
                                                            justifyContent: 'space-between',
                                                            flexDirection: 'column',
                                                            textAlign: 'left',
                                                            width: '50px',
                                                            fontSize: '14px',
                                                        }}
                                                    >
                                                        <div>
                                                            <BsHeart size="15" />
                                                            &nbsp;{list?.likeNum !== null ? list?.likeNum : 0}
                                                        </div>
                                                    </div>
                                                </MDBListGroupItem>
                                            ))}
                                        </MDBListGroup>
                                    </MDBCardBody>
                                </MDBCard>
                            </MDBCol>
                        </MDBRow>
                    </MDBCol>
                </MDBRow>
            </MDBContainer>
        </section>
    );
}

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
const CardBox = styled.div`
    display: flex;
    padding: 20px;
    justify-content: space-between;
    border-bottom: 1px solid #dddddd;
`;

const TextArea = styled.div`
    display: flex;
    flex-direction: column;
    width: 180px;
    padding: 10px;
`;

const FixedButton = styled.div`
    display: flex;
    position: fixed;
    bottom: 120px;
    right: 30px;
    width: 70px;
    height: 70px;
    font-size: 30px;
    background-color: ${(props) => props.theme.color.orange};
    color: ${(props) => props.theme.color.white};
    border-radius: 50%;
    justify-content: center;
    align-items: center;
    box-shadow: 0 0 6px 0 #999;
`;

const Img = styled.img`
    width: 100px;
    height: 100px;
    border-radius: 10px;
    object-fit: cover;
`;

const TradeState = styled.div`
    margin-top: 5px;
    display: flex;
    align-items: center;
`;

const SoldOut = styled.div`
    padding: 6px 5px;
    width: 65px;
    border-radius: 5px;
    background-color: #565656;
    color: white;
    font-size: 12px;
    text-align: center;
`;

const Book = styled(SoldOut)`
    width: 55px;
    background-color: #34bf9e;
`;
