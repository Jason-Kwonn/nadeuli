import React, { useEffect, useState, useRef } from "react";
import { useSelector } from "react-redux";
import {
  Box,
  HeaderContainer,
  OrderTitle,
  StyledButton,
  StyledContainer,
} from "./NadeuliDeliveryStyledComponent";
import HeaderBack from "../../components/HeaderBack";

const GetShortestWay = () => {
  const deliveryLocations = useSelector(
    (state) => state.nadeuliDelivery.deliveryLocations
  );
  const mapContainer = useRef(null);
  const [map, setMap] = useState(null);
  const [currentPosition, setCurrentPosition] = useState(null);
  const [locationCoords, setLocationCoords] = useState([]); // 좌표 배열 상태
  // 섹션 정보를 저장할 상태 추가
  const [sectionsInfo, setSectionsInfo] = useState([]);

  // 지도 스크립트 로드 및 초기 지도 설정
  useEffect(() => {
    const script = document.createElement("script");
    script.src =
      "https://dapi.kakao.com/v2/maps/sdk.js?appkey=adee71fb6cb02644a95cd86ef49eeaff&autoload=false&libraries=services";
    document.head.appendChild(script);

    script.onload = () => {
      window.kakao.maps.load(() => {
        const initialMap = new window.kakao.maps.Map(mapContainer.current, {
          center: new window.kakao.maps.LatLng(37.566826, 126.9786567),
          level: 3,
        });
        setMap(initialMap);
      });
    };

    return () => {
      document.head.removeChild(script);
    };
  }, []);

  // 현재 위치 설정
  useEffect(() => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          const lat = position.coords.latitude;
          const lng = position.coords.longitude;
          const newPos = new window.kakao.maps.LatLng(lat, lng);
          setCurrentPosition(newPos);

          if (map) {
            map.setCenter(newPos);

            // 현재 위치에 마커 추가
            const currentPositionMarker = new window.kakao.maps.Marker({
              position: newPos,
              map: map,
            });
            const currentPositionLabel = new kakao.maps.CustomOverlay({
              position: newPos,
              content: `
              <div class="label" style="display: inline-block; position: relative; bottom: 50px; background: url('https://t1.daumcdn.net/localimg/localimages/07/2011/map/storeview/tip_bg.png') repeat-x; font-size: 12px; line-height: 24px;">
                <span class="left" style="position: absolute; left: 0; top: 0; width: 6px; height: 24px; background: url('https://t1.daumcdn.net/localimg/localimages/07/2011/map/storeview/tip_l.png') no-repeat;"></span>
                <span class="center" style="display: inline-block; padding: 0 10px; height: 24px;">현위치</span>
                <span class="right" style="position: absolute; right: 0; top: 0; width: 6px; height: 24px; background: url('https://t1.daumcdn.net/localimg/localimages/07/2011/map/storeview/tip_r.png') no-repeat;"></span>
              </div>`,
              zIndex: 9999,
            });
            currentPositionMarker.setMap(map);
            currentPositionLabel.setMap(map);
          }
        },
        () => {
          console.error("Geolocation failed or permission denied");
        }
      );
    }
  }, [map]); // 현재 위치 설정을 위해 map을 종속성 배열에 추가

  // 주소를 좌표로 변환하여 마커 생성
  useEffect(() => {
    if (map && deliveryLocations.length > 0 && currentPosition) {
      const geocoder = new window.kakao.maps.services.Geocoder();

      // 모든 주소에 대한 좌표 변환 요청을 Promise 배열로 생성
      const promises = deliveryLocations.flatMap((loc, index) =>
        ["departure", "arrival"].map(
          (key) =>
            new Promise((resolve, reject) => {
              geocoder.addressSearch(loc[key], (result, status) => {
                if (status === window.kakao.maps.services.Status.OK) {
                  const coords = new window.kakao.maps.LatLng(
                    result[0].y,
                    result[0].x
                  );
                  resolve({
                    coords,
                    label: key === "departure" ? "출발" : "도착",
                    index,
                  });
                } else {
                  reject(new Error("Address search failed"));
                }
              });
            })
        )
      );

      // 모든 좌표 변환 요청이 완료된 후 처리
      Promise.all(promises)
        .then((coordsWithLabels) => {
          // coordsWithLabels.forEach(({ coords, label, index }) => {
          //   const marker = new window.kakao.maps.Marker({
          //     map: map,
          //     position: coords,
          //   });

          //   const customOverlay = new kakao.maps.CustomOverlay({
          //     position: coords,
          //     content: `
          //   <div class="label" style="display: inline-block; position: relative; bottom: 50px; background: url('https://t1.daumcdn.net/localimg/localimages/07/2011/map/storeview/tip_bg.png') repeat-x; font-size: 12px; line-height: 24px;">
          //     <span class="left" style="position: absolute; left: 0; top: 0; width: 6px; height: 24px; background: url('https://t1.daumcdn.net/localimg/localimages/07/2011/map/storeview/tip_l.png') no-repeat;"></span>
          //     <span class="center" style="display: inline-block; padding: 0 10px; height: 24px;">${label}${
          //       index + 1
          //     }</span>
          //     <span class="right" style="position: absolute; right: 0; top: 0; width: 6px; height: 24px; background: url('https://t1.daumcdn.net/localimg/localimages/07/2011/map/storeview/tip_r.png') no-repeat;"></span>
          //   </div>`,
          //   });

          //   marker.setMap(map);
          //   customOverlay.setMap(map);
          // });

          // 좌표 변환한 값이 순서가 바뀌지 않았는지 확인
          console.log(coordsWithLabels.map(({ coords }) => coords));

          // coordsWithLabels에서 coords만 추출하여 상태 업데이트
          setLocationCoords(coordsWithLabels.map(({ coords }) => coords));
        })
        .catch((error) => {
          console.error("An error occurred during address search:", error);
        });
    }
  }, [map, deliveryLocations, currentPosition]);

  // 카카오 모빌리티로 추천경로 적용하여 선을 이음.
  async function getDirection() {
    const REST_API_KEY = "73471f7386896401933753c4ecd6d0c1";
    const url = "https://apis-navi.kakaomobility.com/v1/directions";

    const origin = `${currentPosition.getLng()},${currentPosition.getLat()}`;
    const destinationCoords = locationCoords[locationCoords.length - 1];
    const destination = `${destinationCoords.getLng()},${destinationCoords.getLat()}`;
    const waypoints = locationCoords
      .slice(0, -1)
      .map((coords) => `${coords.getLng()},${coords.getLat()}`)
      .join("|");

    console.log("Origin:", origin);
    console.log("Destination:", destination);
    console.log("Waypoints:", waypoints);

    const headers = {
      Authorization: `KakaoAK ${REST_API_KEY}`,
      "Content-Type": "application/json",
    };

    const queryParams = new URLSearchParams({
      origin: origin,
      destination: destination,
      waypoints: waypoints,
      priority: "RECOMMEND",
    });

    const requestUrl = `${url}?${queryParams}`;

    try {
      const response = await fetch(requestUrl, {
        method: "GET",
        headers: headers,
      });

      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }

      const data = await response.json();
      console.log(data);
      // 전체 경로에 대한 폴리라인 그리기
      drawPolyline(data);
      const newSectionsInfo = data.routes[0].sections.map((section, index) => {
        // 마커와 라벨 생성 로직
        const markers = [];
        const labels = [];
        section.guides.forEach((guide) => {
          if (guide.guidance.includes("경유지")) {
            // 번호를 붙여서 경유지 라벨 생성
            const position = new kakao.maps.LatLng(guide.y, guide.x);
            const marker = new kakao.maps.Marker({
              position: position,
              map: map,
            });
            markers.push(marker);

            const label = new kakao.maps.CustomOverlay({
              position: position,
              content: `
                <div class="label" style="display: inline-block; position: relative; bottom: 50px; background: url('https://t1.daumcdn.net/localimg/localimages/07/2011/map/storeview/tip_bg.png') repeat-x; font-size: 12px; line-height: 24px;">
                  <span class="left" style="position: absolute; left: 0; top: 0; width: 6px; height: 24px; background: url('https://t1.daumcdn.net/localimg/localimages/07/2011/map/storeview/tip_l.png') no-repeat;"></span>
                  <span class="center" style="display: inline-block; padding: 0 10px; height: 24px;">경유지${index}</span>
                  <span class="right" style="position: absolute; right: 0; top: 0; width: 6px; height: 24px; background: url('https://t1.daumcdn.net/localimg/localimages/07/2011/map/storeview/tip_r.png') no-repeat;"></span>
                </div>`,
              map: map,
            });

            labels.push(label);
          } else if (
            guide.guidance === "목적지" &&
            index === data.routes[0].sections.length - 1
          ) {
            // 마지막 섹션의 목적지 라벨 생성
            const position = new kakao.maps.LatLng(guide.y, guide.x);
            const marker = new kakao.maps.Marker({
              position: position,
              map: map,
            });
            markers.push(marker);
            const label = new kakao.maps.CustomOverlay({
              position: position,
              content: `
                <div class="label" style="display: inline-block; position: relative; bottom: 50px; background: url('https://t1.daumcdn.net/localimg/localimages/07/2011/map/storeview/tip_bg.png') repeat-x; font-size: 12px; line-height: 24px;">
                  <span class="left" style="position: absolute; left: 0; top: 0; width: 6px; height: 24px; background: url('https://t1.daumcdn.net/localimg/localimages/07/2011/map/storeview/tip_l.png') no-repeat;"></span>
                  <span class="center" style="display: inline-block; padding: 0 10px; height: 24px;">목적지</span>
                  <span class="right" style="position: absolute; right: 0; top: 0; width: 6px; height: 24px; background: url('https://t1.daumcdn.net/localimg/localimages/07/2011/map/storeview/tip_r.png') no-repeat;"></span>
                </div>`,
              map: map,
            });

            labels.push(label);
          }
        });

        return { markers, labels };
      });

      setSectionsInfo(newSectionsInfo);
    } catch (error) {
      console.error("Error:", error);
    }
  }

  function drawPolyline(data) {
    const linePath = [];

    data.routes[0].sections.forEach((section) => {
      section.roads.forEach((road) => {
        road.vertexes.forEach((vertex, index) => {
          if (index % 2 === 0) {
            linePath.push(
              new kakao.maps.LatLng(
                road.vertexes[index + 1],
                road.vertexes[index]
              )
            );
          }
        });
      });
    });

    const polyline = new kakao.maps.Polyline({
      path: linePath,
      strokeWeight: 3,
      strokeColor: "#db4040",
      strokeOpacity: 0.7,
      strokeStyle: "solid",
    });
    polyline.setMap(map);
  }

  // // 기존 섹션의 라벨을 지움
  // const clearMarkersAndLabels = () => {
  //   sectionsInfo.forEach((section) => {
  //     section.markers.forEach((marker) => marker.setMap(null));
  //     section.labels.forEach((label) => label.setMap(null));
  //   });
  // };

  // const displaySectionPath = (sectionIndex) => {
  //   clearMarkersAndLabels();

  //   const sectionInfo = sectionsInfo[sectionIndex];
  //   sectionInfo.markers.forEach((marker) => marker.setMap(map));
  //   // sectionInfo.labels.forEach((label) => label.setMap(map));
  // };

  const handleGetDirectionClick = async () => {
    await getDirection();

    sectionsInfo.forEach((section) => {
      section.markers.forEach((marker) => marker.setMap(map));
      section.labels.forEach((label) => label.setMap(map));
    });
  };

  return (
    <>
      <HeaderContainer>
        <HeaderBack />
        <Box style={{ paddingRight: "40px" }}>
          <OrderTitle>추천 경로 조회</OrderTitle>
        </Box>
        <Box></Box>
      </HeaderContainer>
      <StyledContainer>
        <div
          id="map"
          ref={mapContainer}
          style={{ width: "100%", height: "400px" }}
        ></div>
        <br />
        <StyledButton
          onClick={handleGetDirectionClick}
          style={{ width: "90%" }}
        >
          추천경로 조회
        </StyledButton>
        <br />
        {/* 각 출발지 및 도착지의 한글 주소 표시 */}
        {deliveryLocations &&
          deliveryLocations.map((dto, index) => (
            <div key={index}>
              {/* <p>{dto.productName}</p> */}
              <p style={{ fontWeight: "bold" }}>{`출발${index + 1}: ${
                dto.departure
              }`}</p>
              <p style={{ fontWeight: "bold" }}>{`도착${index + 1}: ${
                dto.arrival
              }`}</p>
            </div>
          ))}
      </StyledContainer>
    </>
  );
};

export default GetShortestWay;
