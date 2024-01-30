import { BrowserRouter, Routes, Route } from "react-router-dom";
import Start from "./pages/member/Start";
import RegisterLocation from "./pages/RegisterLocation";
import Register from "./pages/member/Register";
import Login from "./pages/member/Login";
import Main from "./pages/Main";
import GetMyProfile from "./pages/member/GetMyProfile";
import Add from "./pages/Add";
import Detail from "./pages/Detail";
import NotFound from "./pages/NotFound";
import Profile from "./pages/Profile";
import Modify from "./pages/Modify";
import SalesList from "./pages/SalesList";
import ProfileLocation from "./pages/ProfileLocation";
import FindAccount from "./pages/member/FindAccount";
import UpdateCellphone from "./pages/member/UpdateCellphone";
import OAuth2RedirectHandler from "./pages/member/OAuth2RedirectHandler";
import DongNeHome from "./pages/dongne/DongNeHome";
import AddDongNePost from "./pages/dongne/AddDongNePost";
import AddStreaming from "./pages/dongne/AddStreaming";
import GetDongNePost from "./pages/dongne/GetDongNePost";
import AddOrikkiri from "./pages/orikkiri/AddOrikkiri";
import OrikkiriHome from "./pages/orikkiri/OrikkiriHome";
import AddTradeReview from "./pages/trade/AddTradeReview";
import Test from "./pages/member/GetMyProfile";
import NadeuliPayCharge from "./pages/nadeuli_pay/NadeuliPayCharge";
import Charge from "./pages/nadeuli_pay/Charge";
import NadeuliPayPay from "./pages/nadeuli_pay/NadeuliPayPay";
import NadeuliPayWithdraw from "./pages/nadeuli_pay/NadeuliPayWithdraw";
import GetNadeuliPayList from "./pages/nadeuli_pay/GetNadeuliPayList";
import GetChatRoomList from "./pages/chat/GetChatRoomList";
import Chatting from "./pages/chat/Chatting";
import AddDeliveryOrder from "./pages/nadeuli_delivery/AddDeliveryOrder";
import UpdateDeliveryOrder from "./pages/nadeuli_delivery/UpdateDeliveryOrder";
import GetDeliveryOrder from "./pages/nadeuli_delivery/GetDeliveryOrder";
import NadeuliDeliveryHome from "./pages/nadeuli_delivery/NadeuliDeliveryHome";
import GetMyOrderHistoryList from "./pages/nadeuli_delivery/GetMyOrderHistoryList";
import SearchLocation from "./pages/nadeuli_delivery/SearchLocation";
import GetMyDeliveryHistoryList from "./pages/nadeuli_delivery/GetMyDeliveryHistoryList";
import GetMyAcceptedDeliveryHistoryList from "./pages/nadeuli_delivery/GetMyAcceptedDeliveryHistoryList";
import Oauth2Register from "./pages/member/Oauth2Register";
import GetOtherProfile from "./pages/member/GetOtherProfile";
import GetMemberList from "./pages/member/GetMemberList";
import Report from "./pages/member/Report";
import GetOrikkiriSignUpList from "./pages/orikkiri/GetOrikkiriSignUpList";
import AddOrikkiriSignUp from "./pages/orikkiri/AddOrikkiriSignUp";
import Bank from "./pages/member/Bank";
import GetShortestWay from "./pages/nadeuli_delivery/GetShortestWay";
import AddOrikkiriSchedule from "./pages/orikkiri/AddOrikkiriSchedule";
import AddOrikkiriPost from "./pages/AddOrikkiriPost";
import AddOrikkiriAlbum from "./pages/AddOrikkiriAlbum";
import AddOrikkiriNotice from "./pages/AddOrikkiriNotice";
import OrikkiriScheduleList from "./pages/orikkiri/OrikkiriScheduleList";
import AddTradeSchedule from "./pages/trade/AddTradeSchedule";
import OrikkiriMemberList from "./pages/OrikkiriMemberList";
import UpdateDongNePost from "./pages/dongne/UpdateDongNePost"
import UpdateOrikkiriPost from "./pages/orikkiri/UpdateOrikkiriPost"
import GetOrikkiriPost from "./pages/orikkiri/GetOrikkiriPost";
import UpdateOrikkiriAlbum from "./pages/orikkiri/UpdateOrikkiriAlbum";

// import GetShortestWay from "./pages/nadeuli_delivery/GetShortestWay";
// import AddTradeReview from "./pages/trade/AddTradeReview";

function Router() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/dongNeHome" element={<DongNeHome />} />
        <Route path="/addDongNePost" element={<AddDongNePost />} />
        <Route path="/getDongNePost/:postId" element={<GetDongNePost />} />
        <Route path="/getOrikkiriPost/:postId" element={<GetOrikkiriPost />} />
        <Route path="/updateDongNePost/:postId" element={<UpdateDongNePost />} />
        <Route path="/updateOrikkiriPost/:postId" element={<UpdateOrikkiriPost />} />
        <Route path="/updateOrikkiriAlbum/:postId" element={<UpdateOrikkiriAlbum />} />
        <Route path="/addStreaming" element={<AddStreaming />} />
        <Route path="/orikkiriHome/:orikkiriId" element={<OrikkiriHome />} />
        <Route path="/addOkkiri" element={<AddOrikkiri />} />
        <Route
          path="/getOrikkiriSignUpList/:orikkiriId"
          element={<GetOrikkiriSignUpList />}
        />
        <Route
          path="/addOrikkiriSignUp/:orikkiriId"
          element={<AddOrikkiriSignUp />}
        />
        <Route path="/addOrikkiriSchedule" element={<AddOrikkiriSchedule />} />
        <Route
          path="/orikkiriScheduleList/:orikkiriId"
          element={<OrikkiriScheduleList />}
        />
        <Route
          path="/addOrikkiriPost/:orikkiriId"
          element={<AddOrikkiriPost />}
        />
        <Route
          path="/addOrikkiriAlbum/:orikkiriId"
          element={<AddOrikkiriAlbum />}
        />
        <Route
          path="/addOrikkiriNotice/:orikkiriId"
          element={<AddOrikkiriNotice />}
        />
        <Route
          path="/orikkiriMemberList/:orikkiriId"
          element={<OrikkiriMemberList />}
        />

        <Route
          path="/nadeuliPay/nadeuliPayCharge"
          element={<NadeuliPayCharge />}
        />
        <Route path="/nadeuliPay/charge" element={<Charge />} />
        <Route
          path="/nadeuliPay/nadeuliPayPay/:productId"
          element={<NadeuliPayPay />}
        />
        <Route
          path="/nadeuliPay/nadeuliPayWithdraw"
          element={<NadeuliPayWithdraw />}
        />
        <Route
          path="/nadeuliPay/getNadeuliPayList"
          element={<GetNadeuliPayList />}
        />
        <Route
          path="/trade/addTradeReview/:productId"
          element={<AddTradeReview />}
        />
        <Route path="/trade/addTradeSchedule/:sellerTag/:buyerTag/:productId" element={<AddTradeSchedule />} />

        <Route path="/chat/getChatRoomList" element={<GetChatRoomList />} />
        <Route
          path="/chat/chatting/:chatRoomId/:id/:isProduct"
          element={<Chatting />}
        />

        <Route path="/location" element={<RegisterLocation />} />
        <Route path="/register" element={<Register />} />
        <Route path="/login" element={<Login />} />
        <Route path="/findAccount" element={<FindAccount />} />
        <Route path="/updateCellphone" element={<UpdateCellphone />} />
        <Route path="/bank" element={<Bank />} />
        <Route
          path="/oAuth2RedirectHandler"
          element={<OAuth2RedirectHandler />}
        />
        <Route path="/product/getMyProductList" element={<SalesList />} />
        <Route path="/oauth2Register" element={<Oauth2Register />} />
        <Route path="/add" element={<Add />} />
        <Route path="/test" element={<Test />} />
        <Route path="/getMyProfile" element={<GetMyProfile />} />
        <Route path="/getOtherProfile" element={<GetOtherProfile />} />
        <Route path="/getMemberList" element={<GetMemberList />} />
        <Route path="/report/:type/:id" element={<Report />} />
        <Route path="/addOrikkiri" element={<AddOrikkiri />} />
        <Route path="/modify/:postid" element={<Modify />} />
        <Route path="/detail/:postid" element={<Detail />} />
        <Route path="/profile/location" element={<ProfileLocation />} />
        <Route path="/profile" element={<Profile />} />
        <Route path="/main" element={<Main />} />
        <Route path="/" element={<Start />} />
        <Route path="*" element={<NotFound />} />

        <Route path="/addDeliveryOrder" element={<AddDeliveryOrder />} />
        <Route
          path="/updateDeliveryOrder/:nadeuliDeliveryId"
          element={<UpdateDeliveryOrder />}
        />
        <Route
          path="/getDeliveryOrder/:nadeuliDeliveryId"
          element={<GetDeliveryOrder />}
        />
        <Route path="/nadeuliDeliveryHome" element={<NadeuliDeliveryHome />} />
        <Route
          path="/getMyOrderHistoryList"
          element={<GetMyOrderHistoryList />}
        />
        <Route path="/searchLocation" element={<SearchLocation />} />
        <Route
          path="/getMyDeliveryHistoryList"
          element={<GetMyDeliveryHistoryList />}
        />
        <Route
          path="/getMyAcceptedDeliveryHistoryList"
          element={<GetMyAcceptedDeliveryHistoryList />}
        />
        <Route path="/getShortestWay" element={<GetShortestWay />} />
      </Routes>
    </BrowserRouter>
  );
}

export default Router;
