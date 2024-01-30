import React from 'react';
import { Link } from 'react-router-dom';
import { formatMessageTimestamp } from '../../util/date';

const ChatRoomList = (probs) => {
  const { chatRooms } = probs;

  return (
    <section style={{ backgroundColor: '#fff', minHeight: '100vh' }}>
        <div className="container py-5">
            <div className="row">
                <div className="col-md-6 col-lg-5 col-xl-4 mb-4 mb-md-0">
                    <h5 className="font-weight-bold mb-3 text-center text-lg-start" style={{ position: 'absolute', top: '10px', left: '10px', zIndex: '1', fontWeight: 'bold', fontSize: '1.5rem' }}>채팅</h5>
                    <div className="card">
                        <div className="card-body">
                            <ul className="list-unstyled mb-0">
                                {/* 멤버 리스트 매핑 */}
                                {chatRooms.map((chatRoom) => (
                                    <li
                                        key={chatRoom.id}
                                        className="p-2 border-bottom"
                                        style={{ backgroundColor: '#fff' }}
                                    >
                                         <Link to={`/chat/chatting/${chatRoom._id}${chatRoom.productId !== 0 ? `/${chatRoom.productId}/1` : `/${chatRoom.orikkiriId}/0`}`} className="d-flex justify-content-between">
                                            <div className="d-flex flex-row">
                                                <img
                                                    src={chatRoom.picture}
                                                    alt="avatar"
                                                    className="rounded-circle d-flex align-self-center me-3 shadow-1-strong"
                                                    width="60"
                                                />
                                                <div className="pt-1">
                                                    <p className="fw-bold mb-0">{chatRoom.roomName}</p>
                                                    <p className="small text-muted">{chatRoom.lastMessage?.message}</p>
                                                </div>
                                            </div>
                                            <div className="pt-1">
                                                <p className="small text-muted mb-1">{formatMessageTimestamp(chatRoom.lastMessage?.createdAt)}</p>
                                                {chatRoom.unreadCount > 0 && (
                                                    <span className="badge bg-danger float-end">{chatRoom.unreadCount}</span>
                                                )}
                                            </div>
                                        </Link>
                                    </li>
                                ))}
                            </ul>
                        </div>
                    </div>
                </div>
                {/* 나머지 부분은 이전과 동일 */}
            </div>
        </div>
    </section>
);
};

export default ChatRoomList;