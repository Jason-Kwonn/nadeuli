import React from 'react';
import '../style/css/postInfo.css';

const PostInfo = ({ text, writerPicture, writerNickName, writerDongNe, timeAgo }) => {
  // console.log(writerPicture)
  return (
    <div>
      <div className="rectangle-container">
        <div className="text">{text}</div>
      </div>
      <div className="profile-img-container">
        <img src={writerPicture} alt="Profile" className="profile-img" />
        <div className="post-info">
          <p className="writer-name">{writerNickName}</p>
          <p className="writer-dongne">{writerDongNe}</p>
          <p className="post-time">{timeAgo}</p>
        </div>
      </div>
    </div>
  );
};

export default PostInfo;