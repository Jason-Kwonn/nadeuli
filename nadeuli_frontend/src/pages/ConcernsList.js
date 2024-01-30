import '../public/css/listForm.css';
import styled from "styled-components";

import { useEffect } from "react";
import { loadConcernsposts } from '../redux/modules/post'
import { useSelector,  useDispatch } from 'react-redux';

function ConcernsList () {
    const dispatch = useDispatch();
  
    // useEffect(() => {
    //     dispatch(loadConcernsposts());
    // }, [dispatch]);

    const postList = useSelector((state) => state.post.postList);

    return (
        <div>
            <SubTitle>관심 목록</SubTitle>
            <div>
                {!(postList) ? <NotFound> 판매내역이 없어요 </NotFound> : ""}
                {postList && postList.map((list, index) => (
                    <Card key={index}>
                        <CardBox className="card" >
                            <div style={{ display: "flex" }}>
                                <Img src={list.postImg} />
                                <TextArea>
                                    <span style={{ fontSize: "15px", marginBottom: "5px" }}>
                                        {list.title}
                                    </span>
                                    <span style={{ fontSize: "12px", padding: "5px", color: "#AAAAAA" }}>
                                        {list.userLocation}
                                    </span>
                                    <span style={{ fontSize: "13px", padding: "5px", fontWeight: "bold" }} >
                                        {list.price}
                                    </span>
                                </TextArea>
                            </div>
                        </CardBox>
                    </Card>
                ))}
            </div>
        </div>
    )
}


const Card = styled.div`
  display: flex;
  flex-direction: column;
  border-bottom: 1px solid #DDDDDD;

  &:nth-of-type(1) {
    border-top: 1px solid #DDDDDD;
  }
`;

const CardBox = styled.div`
    display: flex;
    align-items: center;
    justify-content: space-between;;
    height: 130px;
    padding: 15px;
    align-items: flex-start;
`;

const Img = styled.img`
    width: 100px;
    height:100px;
    border-radius: 10px;
`;

const TextArea = styled.div`
    display: flex;
    flex-direction: column;
    padding: 10px;
`;

const SubTitle = styled.div`
    height: 70px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: bold;
    font-size: 24px;
`;

const NotFound = styled.div`
    display: flex;
    height: 100px;
    align-items: center;
    justify-content: center;
`;

export default ConcernsList;