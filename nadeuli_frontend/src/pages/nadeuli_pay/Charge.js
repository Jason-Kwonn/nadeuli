import { post } from "../../util/axios";
import { useLocation, useNavigate } from "react-router-dom";
import { useEffect } from "react";
import { useSelector, useDispatch } from "react-redux";
import { setMember } from "../../redux/modules/member";
import { getMember } from "../../util/memberAxios";


function Charge(){
    const member = useSelector((state) => state.member);
    const dispatch = useDispatch();
    const location = useLocation();
    const navigate = useNavigate();
    const queryParams = new URLSearchParams(location.search);
    const imp_uid = queryParams.get('imp_uid');
    
    useEffect(() => {
        const data = {
            'member': {
                'tag': member.tag,
            },
            'imp_uid': imp_uid,
        }
        post("/nadeuliPay/nadeuliPayCharge", data)
            .then(async (res)=>{
                const mem = await getMember(member.tag);
                dispatch(setMember(mem.data));
                console.log(res);
                navigate("/main");
            })
            .catch((err) => {
                console.log(err);
              });
      }, []);

}


export default Charge;