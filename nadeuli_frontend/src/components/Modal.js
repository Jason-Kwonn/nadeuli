import React from "react";
import modal from "./modal.css";
import { IoClose } from "react-icons/io5";

const Modal = (props) => {
  // 열기, 닫기, 모달 헤더 텍스트를 부모로부터 받아옴
  const { open, close, header } = props;

  const handleModalClick = (e) => {
    // 모달 섹션 안에서 발생한 클릭은 모달을 닫지 않음
    e.stopPropagation();
  };

  return (
    <div className={open ? "openModal modal" : "modal"} onClick={close}>
      {open ? (
        <section className="modalMain" onClick={handleModalClick}>
          <header>
           <IoClose onClick={close} style={{fontSize:'30px'}}/>
          </header>
          <div className="modalContent">
            <h2>{header}</h2>
            <main>{props.children}</main>
          </div>
          <footer>
            <button className="close" onClick={close}>
              닫기
            </button>
          </footer>
        </section>
      ) : null}
    </div>
  );
};

export default Modal;
