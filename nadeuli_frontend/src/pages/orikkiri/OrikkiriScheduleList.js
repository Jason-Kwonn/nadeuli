import React, { useEffect, useRef, useState } from 'react';
import FullCalendar from '@fullcalendar/react';
import dayGridPlugin from '@fullcalendar/daygrid';
import koLocale from '@fullcalendar/core/locales/ko';
import interactionPlugin from '@fullcalendar/interaction';
import { Modal, Button, TextField, Badge } from '@mui/material';
import { useNavigate, useParams } from 'react-router-dom';
import './myCalendar.css';
import {
    MDBBadge,
    MDBBtn,
    MDBModal,
    MDBModalBody,
    MDBModalContent,
    MDBModalDialog,
    MDBModalFooter,
    MDBModalHeader,
    MDBModalTitle,
    MDBTypography,
} from 'mdb-react-ui-kit';
import { format, isBefore, isPast, isSameDay, isToday, sub } from 'date-fns';
import { addOrikkiriSchedule, deleteOrikkiriSchedule, getOrikkiriSchedule, getOrikkiriScheduleList } from '../../util/orikkiriManageAxios';
import { useDaumPostcodePopup } from 'react-daum-postcode';
import { postcodeScriptUrl } from 'react-daum-postcode/lib/loadPostcode';
import { Navigate, useLocation } from 'react-router';
import { StyledButton, StyledContainer } from '../nadeuli_delivery/NadeuliDeliveryStyledComponent';

const OrikkiriScheduleList = ({ orikkiriId }) => {
    const [map, setMap] = useState(null);
    const [geocoder, setGeocoder] = useState(null);
    const [marker, setMarker] = useState(null);
    const [selectedAddress, setSelectedAddress] = useState('');
    const mapContainer = useRef(null);
    const currentDate = new Date(); // 현재 날짜
    const [selectedDate, setSelectedDate] = useState(null);
    const [eventTitle, setEventTitle] = useState('');
    const [basicModal, setBasicModal] = useState(false);
    const [selectedTime, setSelectedTime] = useState('09:00');
    const [events, setEvents] = useState([]);
    const [detailModal, setDetailModal] = useState(false);
    const [selectedSchedule, setSelectedSchedule] = useState(null); // 클릭한 일정 정보를 저장할 state
    const mapContainerDetail = useRef(null);

    const handleTimeChange = (event) => {
        setSelectedTime(event.target.value);
    };
    console.log('받은 orikkiriId는' + orikkiriId);
    useEffect(
        () => {
            // 카카오 지도 스크립트를 동적으로 로드합니다
            const script = document.createElement('script');
            script.src = 'https://dapi.kakao.com/v2/maps/sdk.js?appkey=adee71fb6cb02644a95cd86ef49eeaff&autoload=false&libraries=services';
            document.head.appendChild(script);

            script.onload = () => {
                // 스크립트 로드 완료 후 지도 생성
                kakao.maps.load(() => {
                    // 지도가 표시될 div의 참조
                    const mapContainer = document.getElementById('map'), // 지도를 표시할 div
                        mapOption = {
                            center: new kakao.maps.LatLng(37.566826, 126.9786567), // 지도의 중심좌표
                            level: 3, // 지도의 확대 레벨
                        };

                    // 지도의 초기 옵션 설정
                    // 지도 객체 생성
                    const loadedMap = new kakao.maps.Map(mapContainer, mapOption);
                    setMap(loadedMap);

                    // 주소-좌표 변환 객체를 생성합니다
                    const loadedGeocoder = new kakao.maps.services.Geocoder();
                    setGeocoder(loadedGeocoder);

                    // 클릭한 위치를 표시할 마커입니다
                    const loadedMarker = new kakao.maps.Marker(),
                        // 클릭한 위치에 대한 주소를 표시할 인포윈도우입니다
                        infowindow = new kakao.maps.InfoWindow({ zindex: 1 });

                    setMarker(loadedMarker);

                    // 현재 위치를 가져오는 함수
                    if (navigator.geolocation) {
                        navigator.geolocation.getCurrentPosition(
                            (position) => {
                                // 현재 위치를 기준으로 지도 중심을 재설정
                                const lat = position.coords.latitude;
                                const lng = position.coords.longitude;
                                const newPos = new kakao.maps.LatLng(lat, lng);
                                // 현재 위치에 마커 생성
                                const currentPositionMarker = new kakao.maps.Marker({
                                    position: newPos,
                                });
                                // 마커를 지도에 표시
                                currentPositionMarker.setMap(loadedMap);

                                // 인포윈도우에 표시될 내용
                                const message = `<div class="label" style="display: inline-block; position: relative; bottom: 50px; background: url('https://t1.daumcdn.net/localimg/localimages/07/2011/map/storeview/tip_bg.png') repeat-x; font-size: 12px; line-height: 24px;">
                    <span class="left" style="position: absolute; left: 0; top: 0; width: 6px; height: 24px; background: url('https://t1.daumcdn.net/localimg/localimages/07/2011/map/storeview/tip_l.png') no-repeat;"></span>
                    <span class="center" style="display: inline-block; padding: 0 10px; height: 24px;">현재 위치</span>
                    <span class="right" style="position: absolute; right: 0; top: 0; width: 6px; height: 24px; background: url('https://t1.daumcdn.net/localimg/localimages/07/2011/map/storeview/tip_r.png') no-repeat;"></span>
                  </div>`;

                                // 인포윈도우 생성
                                const customOverlay = new kakao.maps.CustomOverlay({
                                    position: newPos,
                                    content: message,
                                    map: loadedMap,
                                });

                                // 인포윈도우를 마커 위에 표시
                                customOverlay.setMap(loadedMap);

                                // 지도 중심을 현재 위치로 이동
                                loadedMap.setCenter(newPos);
                                // geocoder가 null인 경우에 coord2Address를 호출하지 않도록 예외 처리 추가
                                if (geocoder) {
                                    geocoder.coord2Address(lng, lat, function (result, status) {
                                        if (status === kakao.maps.services.Status.OK) {
                                            const currentAddress = result[0].road_address
                                                ? result[0].road_address.address_name
                                                : result[0].address.address_name;
                                            // 현재 위치의 주소를 업데이트
                                            setSelectedAddress(currentAddress);
                                        }
                                    });
                                } else {
                                    console.error('Geocoder가 초기화되지 않았습니다.');
                                }
                            },
                            () => {
                                console.error('Geolocation failed or permission denied');
                            }
                        );
                    } else {
                        console.error('Browser does not support Geolocation');
                    }

                    // 지도를 클릭했을 때 클릭 위치 좌표에 대한 주소정보를 표시하도록 이벤트를 등록합니다
                    kakao.maps.event.addListener(loadedMap, 'click', function (mouseEvent) {
                        searchDetailAddrFromCoords(mouseEvent.latLng, function (result, status) {
                            if (status === kakao.maps.services.Status.OK) {
                                let detailAddr = result[0].road_address ? result[0].road_address.address_name : result[0].address.address_name;

                                // 마커를 클릭한 위치에 표시합니다
                                loadedMarker.setPosition(mouseEvent.latLng);
                                loadedMarker.setMap(loadedMap);

                                // 클릭한 위치로 중심 이동
                                loadedMap.panTo(mouseEvent.latLng);

                                // 인포윈도우에 클릭한 위치에 대한 법정동 상세 주소정보를 표시합니다
                                infowindow.setContent('<div style="padding:5px;font-size:12px; text-align: center;">' + detailAddr + '</div>');
                                infowindow.open(loadedMap, loadedMarker);

                                // 선택된 주소 상태 업데이트
                                setSelectedAddress(detailAddr);
                            }
                        });
                    });

                    function searchDetailAddrFromCoords(coords, callback) {
                        // 좌표로 법정동 상세 주소 정보를 요청합니다
                        loadedGeocoder.coord2Address(coords.getLng(), coords.getLat(), callback);
                    }
                });
            };

            // 컴포넌트 언마운트 시 스크립트 태그 제거
            return () => {
                document.head.removeChild(script);
            };
        },
        [basicModal],
        [selectedSchedule]
    );

    const open = useDaumPostcodePopup(postcodeScriptUrl);

    const handleMoveToCurrentLocation = () => {
        // 현재 위치를 가져오는 함수
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(
                (position) => {
                    // 현재 위치를 기준으로 지도 중심을 재설정
                    const lat = position.coords.latitude;
                    const lng = position.coords.longitude;
                    const newPos = new kakao.maps.LatLng(lat, lng);

                    // 지도 중심 이동
                    map.setCenter(newPos);
                },
                () => {
                    console.error('Geolocation failed or permission denied');
                }
            );
        } else {
            console.error('Browser does not support Geolocation');
        }
    };

    //주소 검색 시 동네 등록
    const handleComplete = (data) => {
        let fullAddress = data.address;
        let extraAddress = '';

        if (data.addressType === 'R') {
            if (data.bname !== '') {
                extraAddress += data.bname;
            }
            if (data.buildingName !== '') {
                extraAddress += extraAddress !== '' ? `, ${data.buildingName}` : data.buildingName;
            }
            fullAddress += extraAddress !== '' ? ` (${extraAddress})` : '';
        }
        setSelectedAddress(fullAddress);
        console.log('주소찾기를통한 주소는 ' + fullAddress); // e.g. '서울 성동구 왕십리로2길 20 (성수동1가)

        // 주소를 좌표로 변환
        geocoder.addressSearch(fullAddress, function (result, status) {
            if (status === kakao.maps.services.Status.OK) {
                const coords = new kakao.maps.LatLng(result[0].y, result[0].x);

                // 지도 중심을 변경하고 마커를 표시
                map.setCenter(coords);
                marker.setPosition(coords);
                marker.setMap(map);
            }
        });
    };

    // 상세 정보 모달이 열릴 때마다 새로운 지도를 생성하고 초기화하는 함수
    const displayKakaoMapDetail = () => {
        if (selectedSchedule && map) {
            const { meetingDongNe } = selectedSchedule;
            const geocoder = new kakao.maps.services.Geocoder();

            // 주소로 좌표를 검색
            geocoder.addressSearch(meetingDongNe, (result, status) => {
                if (status === kakao.maps.services.Status.OK) {
                    const coords = new kakao.maps.LatLng(result[0].y, result[0].x);

                    // 기존에 마커가 있으면 제거
                    if (marker) {
                        marker.setMap(null);
                    }

                    // 새로운 지도 생성
                    const newMap = new kakao.maps.Map(mapContainerDetail.current, {
                        center: coords,
                        level: 3,
                    });

                    // 마커 생성
                    const newMarker = new kakao.maps.Marker({
                        position: coords,
                    });

                    // 마커 지도에 표시
                    newMarker.setMap(newMap);

                    // 마커 클릭 이벤트 추가 (원하는 동작을 여기에 추가)
                    kakao.maps.event.addListener(newMarker, 'click', () => {
                        // 마커 클릭 시 할 작업
                    });

                    // 생성된 마커를 상태에 저장
                    setMarker(newMarker);
                }
            });
        }
    };
    const handleClick = () => {
        open({ onComplete: handleComplete });
    };

    const toggleOpen = async (arg) => {
        // 과거 날짜는 클릭 안되게 설정
        if (isBefore(arg.date, currentDate)) {
            return;
        }

        // 클릭한 날짜에 이미 3개 이상의 일정이 있는지 확인
        const clickedDate = arg.date;
        const eventsOnClickedDate = events.filter((event) => isSameDay(new Date(event.start), clickedDate));

        if (eventsOnClickedDate.length >= 3) {
            alert('일정을 3개이상 등록할 수 없습니다.');
            return;
        }

        setBasicModal(!basicModal);
        setSelectedDate(arg.date);
        setEventTitle('');
        setSelectedTime('09:00');
    };

    const handleAddEvent = async () => {
        console.log('등록할 일정 데이터:', { title: eventTitle, date: selectedDate });
        // 일정 추가 버튼 클릭 시 호출되는 함수
        // 여기서 서버로 데이터 전송 및 등록 로직 추가
        if (!eventTitle || !selectedDate) {
            alert('일정 제목 또는 날짜를 입력해주세요.');
            return;
        }

        try {
            const selectedDateTime = new Date(selectedDate);
            const [hours, minutes] = selectedTime.split(':');

            console.log(hours, minutes);
            selectedDateTime.setHours(parseInt(hours, 10));
            selectedDateTime.setMinutes(parseInt(minutes, 10));

            const formattedDate = `${selectedDateTime.getFullYear()}-${(selectedDateTime.getMonth() + 1)
                .toString()
                .padStart(2, '0')}-${selectedDateTime.getDate().toString().padStart(2, '0')} ${hours}:${minutes}`;

            const orikkiriScheduleDTO = {
                meetingDay: formattedDate,
                meetingDongNe: selectedAddress,
                meetingTitle: eventTitle,
                scheduleMemberNum: 1,
                orikkiri: {
                    orikkiriId: orikkiriId,
                },
            };
            console.log(orikkiriScheduleDTO);
            console.log('asdasd' + orikkiriId);

            // 서버로 데이터 전송
            const response = await addOrikkiriSchedule(orikkiriScheduleDTO);

            // 성공적으로 등록되면 모달 닫기
            if (response.status === 200) {
                await fetchOrikkiriSchedule();
                setBasicModal(false);
                setSelectedDate(null);
                setEventTitle('');
            } else {
                console.error('일정 추가에 실패했습니다.');
            }
        } catch (error) {
            console.error('일정 추가 중 오류 발생:', error);
        }
    };
    const fetchOrikkiriSchedule = async () => {
        try {
            // 일정 목록 조회 API 호출
            const response = await getOrikkiriScheduleList(orikkiriId); // 오리끼리아이디 받아올것
            const orikkiriEvents = response.data;
            console.log(orikkiriEvents);
            // FullCalendar에서 사용할 형식으로 변환
            const convertedEvents = orikkiriEvents.map((event) => ({
                title: event.meetingTitle,
                start: event.meetingDay,
                orikkiriScheduleId: event.orikkiriScheduleId,
            }));
            console.log(convertedEvents);
            // 기존 events에 추가
            setEvents(convertedEvents);
        } catch (error) {
            console.error('일정 목록 조회 실패', error);
        }
    };
    //일정 상세조회
    const openDetailModal = () => setDetailModal(true);
    const closeDetailModal = () => setDetailModal(false);

    const handleEventClick = async (clickInfo) => {
        const event = clickInfo.event;
        const orikkiriScheduleId = event.extendedProps.orikkiriScheduleId;

        try {
            const response = await getOrikkiriSchedule(orikkiriScheduleId);
            const orikkiriScheduleDetail = response.data;

            // 클릭한 일정 정보를 상태에 저장하고 모달을 열기
            setSelectedSchedule(orikkiriScheduleDetail);
            openDetailModal();
        } catch (error) {
            console.error('상세 정보 조회 중 오류 발생:', error);
        }
    };

    const deleteSchedule = async () => {
        try {
            // deleteOrikkiriSchedule 함수는 orikkirischeduleId를 이미 내부에서 참조하고 있으므로
            // 따로 인자로 넘겨줄 필요가 없습니다.
            await deleteOrikkiriSchedule(selectedSchedule.orikkiriScheduleId);
            // 삭제 후 다시 일정 리스트를 불러오기
            await fetchOrikkiriSchedule();
            closeDetailModal();
        } catch (error) {
            console.error('일정 삭제 중 오류 발생:', error);
        }
    };
    const handleEventContent = (arg) => {
        const { start, title } = arg.event;
        const formattedStart = start.toLocaleString('ko-KR', {
            hour: 'numeric',
            minute: 'numeric',
            hour12: true,
        });

        return (
            <div style={{ fontSize: '10px', lineHeight: 1, textAlign: 'left', cursor: 'pointer' }}>
                {/* {formattedStart} */}
                <MDBTypography
                    tag="mark"
                    style={{
                        display: 'block' /* 또는 display: inline-block; */,
                        whiteSpace: 'nowrap',
                        overflow: 'hidden',
                        textOverflow: 'ellipsis',
                        maxWidth: '5em' /* 최대 글자 수 */,
                    }}
                >
                    {title}
                </MDBTypography>
            </div>
        );
    };
    // 상세 정보 모달이 열릴 때마다 Ref를 새로운 요소로 업데이트
    useEffect(() => {
        displayKakaoMapDetail();
    }, [basicModal, selectedSchedule]);
    useEffect(() => {
        // 함수 호출
        fetchOrikkiriSchedule();
    }, []); // orikkiriId가 변경될 때마다 조회하려면 orikkiriId를 useEffect의 dependency로 추가

    return (
        <div className="App">
            <FullCalendar
                initialView="dayGridMonth"
                plugins={[dayGridPlugin, interactionPlugin]}
                events={events}
                eventClick={handleEventClick}
                eventContent={handleEventContent}
                dateClick={toggleOpen}
                locales={koLocale}
                selectMirror={true}
                height={600}
                locale="ko"
                headerToolbar={{
                    left: 'today',
                    center: 'title',
                    right: 'prev,next',
                }}
                customButtons={{
                    customPrevButton: {
                        text: '이전',
                    },
                    customNextButton: {
                        text: '다음',
                    },
                }}
            />

            <MDBModal
                open={basicModal}
                setOpen={setBasicModal}
                tabIndex="-1"
            >
                <MDBModalDialog>
                    <MDBModalContent>
                        <MDBModalHeader>
                            <MDBModalTitle>일정 추가</MDBModalTitle>
                            <MDBBtn
                                className="btn-close"
                                color="none"
                                onClick={toggleOpen}
                            ></MDBBtn>
                        </MDBModalHeader>
                        <MDBModalBody>
                            {selectedDate && (
                                <div style={{ display: 'flex', alignItems: 'center' }}>
                                    <MDBBadge
                                        color="success"
                                        light
                                        style={{ marginRight: '5px' }}
                                    >
                                        만남 시간
                                    </MDBBadge>
                                    {selectedDate.toLocaleDateString('ko-KR', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' })}
                                    <input
                                        type="time"
                                        value={selectedTime}
                                        onChange={handleTimeChange}
                                        style={{ marginLeft: '5px', fontSize: '10px' }}
                                    />
                                </div>
                            )}

                            {/* 일정 입력 필드 */}
                            <TextField
                                label="만남 제목"
                                variant="outlined"
                                fullWidth
                                value={eventTitle}
                                onChange={(e) => setEventTitle(e.target.value)}
                                margin="normal"
                            />
                            <StyledContainer style={{ justifyContent: 'center' }}>
                                <div
                                    id="map"
                                    ref={mapContainer}
                                    style={{ width: '100%', height: '300px' }}
                                ></div>
                                <br />
                                <p style={{ fontWeight: 'bold' }}>만남 동네 : {selectedAddress}</p>
                                <div style={{ textAlign: 'center', justifyContent: 'center' }}>
                                    <StyledButton
                                        type="button"
                                        onClick={handleClick}
                                        style={{ width: '90%' }}
                                    >
                                        주소 검색
                                    </StyledButton>
                                    <StyledButton
                                        type="button"
                                        onClick={handleMoveToCurrentLocation}
                                        style={{ width: '90%' }}
                                    >
                                        현재 위치로 이동
                                    </StyledButton>
                                </div>
                            </StyledContainer>
                        </MDBModalBody>

                        <MDBModalFooter>
                            {/* 모달 닫기 버튼 */}
                            <MDBBtn
                                color="secondary"
                                onClick={() => {
                                    setBasicModal(false);
                                    setSelectedDate(null);
                                    setEventTitle('');
                                }}
                            >
                                닫기
                            </MDBBtn>

                            {/* 일정 추가 버튼 */}
                            <MDBBtn onClick={handleAddEvent}>일정 추가</MDBBtn>
                        </MDBModalFooter>
                    </MDBModalContent>
                </MDBModalDialog>
            </MDBModal>
            {/* 상세 정보 모달 */}
            <MDBModal
                open={detailModal}
                setOpen={setDetailModal}
                tabIndex="-1"
            >
                <MDBModalDialog>
                    <MDBModalContent>
                        <MDBModalHeader>
                            <MDBModalTitle>일정 상세 정보</MDBModalTitle>
                            <MDBBtn
                                className="btn-close"
                                color="none"
                                onClick={closeDetailModal}
                            ></MDBBtn>
                        </MDBModalHeader>
                        <MDBModalBody>
                            {/* 일정 상세 정보를 보여주는 내용을 추가 */}
                            {selectedSchedule && (
                                <div>
                                    <p>
                                        <MDBBadge
                                            color="primary"
                                            light
                                            style={{ marginRight: '5px' }}
                                        >
                                            만남 제목
                                        </MDBBadge>
                                        {/* 만남 제목 표시 */}
                                        <b>{selectedSchedule.meetingTitle}</b>
                                    </p>
                                    <p style={{ fontSize: '16px', textDecoration: 'none', color: 'black' }}>
                                        <MDBBadge
                                            color="success"
                                            light
                                            style={{ marginRight: '5px' }}
                                        >
                                            만남 시간
                                        </MDBBadge>
                                        {/* 만남 시간 표시 */}
                                        {new Date(selectedSchedule.meetingDay).toLocaleString('ko-KR', {
                                            year: 'numeric',
                                            month: 'long',
                                            day: 'numeric',
                                            weekday: 'long',
                                            hour: 'numeric',
                                            minute: 'numeric', // 시간에 분 추가
                                        })}
                                    </p>
                                    <p style={{ fontSize: '16px', textDecoration: 'none', color: 'black' }}>
                                        <MDBBadge
                                            color="info"
                                            light
                                            style={{ marginRight: '5px' }}
                                        >
                                            만남 동네
                                        </MDBBadge>
                                        {/* 만남 동네 표시 */}
                                        {selectedSchedule.meetingDongNe}
                                    </p>
                                    <div
                                        id="mapDetail"
                                        ref={mapContainerDetail}
                                        style={{ width: '100%', height: '300px' }}
                                    ></div>
                                    {/* (기타 상세 정보 필드 추가) */}
                                </div>
                            )}
                        </MDBModalBody>
                        <MDBModalFooter>
                            <MDBBtn
                                color="danger"
                                onClick={deleteSchedule}
                            >
                                삭제
                            </MDBBtn>
                            {/* 상세 정보 모달 닫기 버튼 */}
                            <MDBBtn
                                color="secondary"
                                onClick={closeDetailModal}
                            >
                                닫기
                            </MDBBtn>
                        </MDBModalFooter>
                    </MDBModalContent>
                </MDBModalDialog>
            </MDBModal>
        </div>
    );
};

export default OrikkiriScheduleList;
