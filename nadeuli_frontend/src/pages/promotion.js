import React, { useState, useEffect, useRef } from "react";
import { useSelector, useDispatch } from 'react-redux';
import { GetDongNePostList } from '../redux/modules/dongNePost';
import '../style/css/postInfo.css';
import { FaRegComment } from "react-icons/fa";
import styled from 'styled-components';
import { useNavigate } from 'react-router-dom';
import '../style/css/promotion.css';
import Hls from 'hls.js';

function Promotion({searchQuery}) {
  const location = useSelector((state) => state.member.gu);
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const [currentPage, setCurrentPage] = useState(0);
  const dongNePostList = useSelector((state) => state.dongNePost.dongNePostList);
  const videoRefs = useRef({});


  document.addEventListener("DOMContentLoaded", function() {
    const headings = document.querySelectorAll('.video-card h3');
  
    headings.forEach(heading => {
      if (heading.scrollWidth > heading.offsetWidth) {
        heading.classList.add('marquee');
      }
    });
  });
  
  useEffect(() => {
    dispatch(GetDongNePostList(currentPage, location, searchQuery));
  }, [currentPage, location, searchQuery, dispatch]);

  useEffect(() => {
    dongNePostList.forEach((dongNePost, index) => {
      const videoElement = videoRefs.current[index];

      if (dongNePost.streaming && videoElement) {
        if (Hls.isSupported()) {
          const hls = new Hls();
          hls.loadSource(dongNePost.streaming);
          hls.attachMedia(videoElement);
        } else if (videoElement.canPlayType('application/vnd.apple.mpegurl')) {
          videoElement.src = dongNePost.streaming;
        }
      }
    });
  }, [dongNePostList]);

  return (
    <div className="promotion-container">
      {dongNePostList.map((dongNePost, index) => {
        let videoSource = '';

        // dongNePost.streaming 값이 있으면 그것을 사용
        if (dongNePost.streaming) {
          videoSource = dongNePost.streaming;
        } else if (dongNePost.images && dongNePost.images.length > 0) {
          // dongNePost.images 중 확장자가 .mp4인 것만 필터링
          const mp4Image = dongNePost.images.find(image => image.endsWith('.mp4'));
          if (mp4Image) {
            videoSource = mp4Image;
          }
        }

        if (videoSource && dongNePost.gu === location) {
          return (
            <div className="promotion-scroll" key={dongNePost.postId}>
              <div className="video-card" onClick={() => navigate("/getDongNePost/" + dongNePost.postId)}>
                <video 
                  ref={(el) => videoRefs.current[index] = el}
                  src={videoSource} 
                  playsInline controls autoPlay muted 
                />
                <div className="video-overlay">
                  <h3>{dongNePost.title}</h3>
                </div>
              </div>
            </div>
          );
        }

        return null;
      })}
    </div>
  );
}

export default Promotion;
