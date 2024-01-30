import React from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";

// import GetNadeuliPayList from "./page/nadeuli_pay/GetNadeuliPayList";
// import NadeuliPayCharge from "./page/nadeuli_pay/NadeuliPayCharge";
// import NadeuliPayWithdraw from "./page/nadeuli_pay/NadeuliPayWithdraw";
// import NadeuliPayPay from "./page/nadeuli_pay/NadeuliPayPay";

import GetProductList from "./page/product/GetProductList";
// import GetProduct from "./page/product/GetProduct";
// import UpdateProduct from "./page/product/UpdateProduct";
// import AddProduct from "./page/product/AddProduct";
// import GetMyProductList from "./page/product/GetMyProductList";
// import DeleteProduct from "./page/product/DeleteProduct";
// import SaleCompleted from "./page/product/SaleCompleted";

// import AddTradeReview from "./page/trade/AddTradeReview";
// import UpdateTradeReview from "./page/trade/UpdateTradeReview";
// import GetTradeReviewList from "./page/trade/GetTradeReviewList";
// import DeleteTradeReview from "./page/trade/DeleteTradeReview";
// import AddTradeSchedule from "./page/trade/AddTradeSchedule";
// import GetTradeSchedule from "./page/trade/GetTradeSchedule";
// import UpdateTradeSchedule from "./page/trade/UpdateTradeSchedule";
// import GetTradeScheduleList from "./page/trade/GetTradeScheduleList";

// import GetAddOrUpdateUsedDeliveryOrder from "./page/nadeuli_delivery/GetAddOrUpdateUsedDeliveryOrder";
// import AddDeliveryOrder from "./page/nadeuli_delivery/AddDeliveryOrder";
// import UpdateDeliveryOrder from "./page/nadeuli_delivery/UpdateDeliveryOrder";
// import GetDeliveryOrder from "./page/nadeuli_delivery/GetDeliveryOrder";
// import GetDeliveryOrderList from "./page/nadeuli_delivery/GetDeliveryOrderList";
// import GetMyOrderHistoryList from "./page/nadeuli_delivery/GetMyOrderHistoryList";
// import GetMyDeliveryHistoryList from "./page/nadeuli_delivery/GetMyDeliveryHistoryList";
// import GetMyAcceptedDeliveryHistoryList from "./page/nadeuli_delivery/GetMyAcceptedDeliveryHistoryList";
// import CancelDeliveryOrder from "./page/nadeuli_delivery/CancelDeliveryOrder";
// import AcceptDeliveryOrder from "./page/nadeuli_delivery/AcceptDeliveryOrder";
// import CancelDelivery from "./page/nadeuli_delivery/CancelDelivery";
// import CompleteDelivery from "./page/nadeuli_delivery/CompleteDelivery";
// import GetDeliveryNotificationList from "./page/nadeuli_delivery/GetDeliveryNotificationList";
// import UpdateIsRead from "./page/nadeuli_delivery/UpdateIsRead";
// import DeleteDeliveryNotification from "./page/nadeuli_delivery/DeleteDeliveryNotification";

function App() {
  return (
    <Routes>
      {/* <Route path="/nadeuliPay/getNadeuliPayList/:currentPage/:tradeType/:tag" element={<GetNadeuliPayList />} /> */}
      {/* <Route path="/nadeuliPay/nadeuliPayCharge" element={<NadeuliPayCharge />} /> */}
      {/* <Route path="/nadeuliPay/nadeuliPayWithdraw" element={<NadeuliPayWithdraw />} /> */}
      {/* <Route path="/nadeuliPay/nadeuliPayPay" element={<NadeuliPayPay />} /> */}
 
      <Route path="/product/home/:currentPage/:gu" element={<GetProductList />} />
      {/* <Route path="/product/getProduct/:productId" element={<GetProduct />} /> */}
      {/* <Route path="/product/updateProduct" element={<UpdateProduct />} /> */}
      {/* <Route path="/product/addProduct" element={<AddProduct />} /> */}
      {/* <Route path="/product/getMyProductList/:tag/:currentPage" element={<GetMyProductList />} /> */}
      {/* <Route path="/product/deleteProduct/:productId" element={<DeleteProduct />} /> */}
      {/* <Route path="/product/saleCompleted" element={<SaleCompleted />} /> */}

      {/* <Route path="/trade/addTradeReview" element={<AddTradeReview />} /> */}
      {/* <Route path="/trade/updateTradeReview" element={<UpdateTradeReview />} /> */}
      {/* <Route path="/trade/getTradeReviewList/:tag/:currentPage" element={<GetTradeReviewList />} /> */}
      {/* <Route path="/trade/deleteTradeReview/:tradeReviewId" element={<DeleteTradeReview />} /> */}
      {/* <Route path="/trade/addTradingSchedule" element={<AddTradeSchedule />} /> */}
      {/* <Route path="/trade/getTradingSchedule/:tradeScheduleId" element={<GetTradeSchedule />} /> */}
      {/* <Route path="/trade/updateTradingSchedule" element={<UpdateTradeSchedule />} /> */}
      {/* <Route path="/trade/getTradingScheduleList/:tag/:currentPage" element={<GetTradeScheduleList />} /> */}

      {/* <Route path="/nadeulidelivery/getAddOrUpdateUsedDeliveryOrder/:tag" element={<GetAddOrUpdateUsedDeliveryOrder />} /> */}
      {/* <Route path="/nadeulidelivery/addDeliveryOrder" element={<AddDeliveryOrder />} /> */}
      {/* <Route path="/nadeulidelivery/updateDeliveryOrder" element={<UpdateDeliveryOrder />} /> */}
      {/* <Route path="/nadeulidelivery/getDeliveryOrder/:nadeuliDeliveryId" element={<GetDeliveryOrder />} /> */}
      {/* <Route path="/nadeulidelivery/getDeliveryOrderList" element={<GetDeliveryOrderList />} /> */}
      {/* <Route path="/nadeulidelivery/getMyOrderHistoryList" element={<GetMyOrderHistoryList />} /> */}
      {/* <Route path="/nadeulidelivery/getMyDeliveryHistoryList" element={<GetMyDeliveryHistoryList />} /> */}
      {/* <Route path="/nadeulidelivery/getMyAcceptedDeliveryHistoryList" element={<GetMyAcceptedDeliveryHistoryList />} /> */}
      {/* <Route path="/nadeulidelivery/cancelDeliveryOrder/:nadeuliDeliveryId" element={<CancelDeliveryOrder />} /> */}
      {/* <Route path="/nadeulidelivery/acceptDeliveryOrder" element={<AcceptDeliveryOrder />} /> */}
      {/* <Route path="/nadeulidelivery/cancelDelivery/:nadeuliDeliveryId" element={<CancelDelivery />} /> */}
      {/* <Route path="/nadeulidelivery/completeDelivery/:nadeuliDeliveryId" element={<CompleteDelivery />} /> */}
      {/* <Route path="/nadeulidelivery/getDeliveryNotificationList" element={<GetDeliveryNotificationList />} /> */}
      {/* <Route path="/nadeulidelivery/updateIsRead/:deliveryNotificationId" element={<UpdateIsRead />} /> */}
      {/* <Route path="/nadeulidelivery/deleteDeliveryNotification/:deliveryNotificationId" element={<DeleteDeliveryNotification />} /> */}
    </Routes>
  );
}

export default App;
