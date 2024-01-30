import flatpickr from 'flatpickr';
import 'flatpickr/dist/flatpickr.min.css';
import { useEffect, useRef, useState } from 'react';
import ko from 'flatpickr/dist/l10n/ko';
function AddOrikkiriSchedule() {
    const dateTimeRef = useRef(null);

    useEffect(() => {
        flatpickr.localize(ko);

        const today = new Date().setHours(0, 0, 0, 0);

        const config = {
            enableTime: true,
            dateFormat: 'Y년m월d일 l H:i', // 날짜 및 시간 형식 변경
            altInput: true,
            altFormat: 'Y년m월d일 H:i',
            time_24hr: true,
            disable: [
                function (date) {
                    return date.getDay() === 0 || date.getDay() === 6;
                },
            ],
            locale: {
                weekdays: {
                    shorthand: ['일', '월', '화', '수', '목', '금', '토'],
                    longhand: ['일요일', '월요일', '화요일', '수요일', '목요일', '금요일', '토요일'],
                },
                months: {
                    shorthand: ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'],
                    longhand: ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'],
                },
            },
            locale: ko.ko,
            minDate: today,
            minuteIncrement: 30,
            minTime: '09:00',
            maxTime: '18:00',
            defaultHour: '09',
            defaultMinute: '00',
            inline: true,
        };

        dateTimeRef.current = flatpickr('input[type="text"]', config); // input type을 text로 변경

        return () => {
            // 컴포넌트가 언마운트될 때 Flatpickr 인스턴스 제거
            if (dateTimeRef.current) {
                dateTimeRef.current.destroy();
            }
        };
    }, []); // 빈 배열은 컴포넌트가 마운트될 때 한 번만 실행

    return (
        <>
            <input
                class="form-control dateTime"
                type="datetime-local"
                required
            />
        </>
    );
}

export default AddOrikkiriSchedule;
