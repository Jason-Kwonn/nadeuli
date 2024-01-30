import React, { useEffect, useState } from "react";
import { ImSearch } from "react-icons/im";
import { useDispatch, useSelector } from "react-redux";
import { IoIosNotifications } from "react-icons/io";
import { MdDelete } from "react-icons/md";
import "../public/css/nadeuliDeliveryListForm.css";
import { get, post } from "../../util/axios";
import { setMember } from "../../redux/modules/member";
import { useNavigate } from "react-router";

const NadeuliDeliveryHomeTopBar = ({ onSearch }) => {
  const member = useSelector((state) => state.member);
  const location = useSelector((state) => state.member.gu);
  const memberTag = useSelector((state) => state.member.tag);
  const [isSearchVisible, setSearchVisible] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const [isNadeuliDeliveryEnabled, setIsNadeuliDeliveryEnabled] =
    useState(false);
  const [notifications, setNotifications] = useState([]);
  const [showNotifications, setShowNotifications] = useState(false);
  const dispatch = useDispatch();
  const navigate = useNavigate();

  useEffect(() => {
    get(`/member/getMember/${memberTag}`)
      .then((response) => {
        if (response) {
          console.log(response);
          setIsNadeuliDeliveryEnabled(response.nadeuliDelivery);
        } else {
          console.error("nadeuliDelivery 값을 찾을 수 없음");
        }
      })
      .catch((error) => {
        console.error("Error fetching member data:", error);
      });
  }, [memberTag]); // isNadeuliDeliveryEnabled는 이곳에 포함시키지 않아야 합니다.

  const handleNotificationToggle = () => {
    // 현재 체크 상태에 따라 반대 상태로 업데이트
    const newIsNadeuliDeliveryStatus = !isNadeuliDeliveryEnabled;
    get(`/member/handleNadeuliDelivery/${memberTag}`)
      .then((response) => {
        console.log("Server Response:", response); // 서버 응답 로그 출력
        if (response && response.success) {
          const newMemberData = {
            ...member,
            isNadeuliDelivery: newIsNadeuliDeliveryStatus, // 현재 Redux 상태 기반으로 업데이트
          };
          dispatch(setMember(newMemberData));
          setIsNadeuliDeliveryEnabled(newIsNadeuliDeliveryStatus);
          if (newIsNadeuliDeliveryStatus === true) {
            alert("부름 알림을 켰습니다.");
          } else {
            alert("부름 알림 차단 완료.");
          }
        } else {
          console.error("Failed to update NadeuliDelivery status");
          setIsNadeuliDeliveryEnabled(!newIsNadeuliDeliveryStatus);
        }
      })
      .catch((error) => {
        console.error("Error updating NadeuliDelivery status:", error);
        setIsNadeuliDeliveryEnabled(!newIsNadeuliDeliveryStatus);
      });
  };

  useEffect(() => {
    if (showNotifications && isNadeuliDeliveryEnabled) {
      const deliveryNotificationDTO = {
        nadeuliDelivery: {
          buyer: {
            tag: memberTag,
          },
        },
      };

      post(
        "/nadeulidelivery/getDeliveryNotificationList",
        deliveryNotificationDTO,
        {
          headers: {
            "Content-Type": "application/json",
          },
          params: {
            currentPage: 0,
          },
        }
      )
        .then((response) => {
          console.log(response);
          setNotifications(response);
        })
        .catch((error) => {
          console.error("Error fetching delivery notifications:", error);
        });
    }
  }, [showNotifications, isNadeuliDeliveryEnabled, memberTag]);

  const handleNotificationsClick = () => {
    if (isNadeuliDeliveryEnabled) {
      setShowNotifications(!showNotifications); // 단순히 상태 토글
    } else {
      setShowNotifications(false); // 스위치가 꺼져 있으면 숨김
    }
  };

  const handelDeleteNotification = (deliveryNotificationId, event) => {
    event.stopPropagation(); // 이벤트 버블링 중단
    // deliveryNotificationId를 사용하여 API 요청
    get(`/nadeulidelivery/deleteDeliveryNotification/${deliveryNotificationId}`)
      .then((response) => {
        // 성공적으로 알림이 삭제되었다면, UI를 업데이트 하거나 사용자에게 피드백 제공
        console.log(response);
        // 필요하다면 여기서 알림 목록을 업데이트하는 로직 추가
        const updatedNotifications = notifications.filter(
          (notification) =>
            notification.deliveryNotificationId !== deliveryNotificationId
        );
        setNotifications(updatedNotifications);
      })
      .catch((error) => {
        // 에러 처리
        console.error("부름 알림 삭제 처리 에러 :", error);
      });
  };

  const handleIsRead = (deliveryNotificationId, event) => {
    event.stopPropagation(); // 이벤트 버블링 중단
    get(`/nadeulidelivery/updateIsRead/${deliveryNotificationId}`)
      .then((response) => {
        console.log(response);
        // 알림 목록에서 해당 아이템의 상태를 업데이트
        const updatedNotifications = notifications.map((notification) => {
          if (notification.deliveryNotificationId === deliveryNotificationId) {
            return { ...notification, read: true };
          }
          return notification;
        });

        setNotifications(updatedNotifications);

        // 상태 업데이트 후 네비게이션 처리
        const readNotification = updatedNotifications.find(
          (notification) =>
            notification.deliveryNotificationId === deliveryNotificationId
        );
        if (readNotification) {
          navigate(
            `/getDeliveryOrder/${readNotification.nadeuliDelivery.nadeuliDeliveryId}`
          );
        }
      })
      .catch((error) => {
        // 에러 처리
        console.error("부름 알림 읽음 처리 에러 : ", error);
      });
  };

  const handleSearchClick = () => {
    // 검색 입력 필드 확인용
    setSearchVisible(!isSearchVisible);
  };

  const handleInputChange = (e) => {
    const query = e.target.value;
    setSearchQuery(query); // 입력 값 업데이트
    onSearch(query); // 검색 쿼리를 상위 컴포넌트로 전달
  };

  const handleSearch = () => {
    // 검색 기능을 구현합니다.
    console.log("검색어:", searchQuery);
    onSearch(searchQuery);
  };

  const handleKeyPress = (e) => {
    // Enter 키가 눌릴 때 검색을 트리거합니다.
    if (e.key === "Enter") {
      handleSearch();
    }
  };

  return (
    <div className="deliveryTMenuBar">
      <div className="left-group">
        <span className="location">{location}</span>
      </div>
      <div className="right-group">
        <div className="switch-group">
          <span style={{ fontSize: "12px" }}>알림 상태</span>
          <label className="switch">
            <input
              type="checkbox"
              checked={isNadeuliDeliveryEnabled || false}
              onChange={handleNotificationToggle}
            />
            <span className="slider"></span>
          </label>
        </div>
        <IoIosNotifications
          style={{ fontSize: "30px", marginRight: "20px" }}
          onClick={handleNotificationsClick}
        />
        {showNotifications && (
          <div className="notifications-dropdown">
            {notifications.map((notification, index) => (
              <div
                key={index}
                className={`notification-item ${
                  notification.read ? "read" : ""
                }`}
                onClick={(event) =>
                  handleIsRead(notification.deliveryNotificationId, event)
                }
              >
                <div className="notification-content-container">
                  <div className="notification-content">
                    {notification.notificationContent}
                  </div>
                  <MdDelete
                    className="delete-icon"
                    onClick={(event) =>
                      handelDeleteNotification(
                        notification.deliveryNotificationId,
                        event
                      )
                    }
                  />
                </div>
              </div>
            ))}
          </div>
        )}
        <div className={`search-container ${isSearchVisible ? "active" : ""}`}>
          <ImSearch onClick={handleSearchClick} className="search-icon" />
          {isSearchVisible && (
            <input
              type="text"
              className="search-input"
              placeholder="검색어를 입력하세요"
              value={searchQuery}
              onChange={handleInputChange}
              onKeyPress={handleKeyPress}
            />
          )}
        </div>
      </div>
    </div>
  );
};

export default NadeuliDeliveryHomeTopBar;
