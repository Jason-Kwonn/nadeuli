import React, { useState } from "react";
import {
  Image,
  ImageSliderComponent,
  SlideButton,
  SliderContainer,
} from "./NadeuliDeliveryStyledComponent";

// 컴포넌트 구현
const ImageSlider = ({ images }) => {
  const [slideIndex, setSlideIndex] = useState(0);

  // images 배열이 정의되어 있고, 길이가 0보다 큰지 확인
  if (!images || images.length === 0) {
    // images 배열이 없거나 비어있으면 렌더링하지 않음
    return null;
  }

  const handlePrev = () => {
    setSlideIndex(slideIndex > 0 ? slideIndex - 1 : 0);
  };

  const handleNext = () => {
    setSlideIndex(
      slideIndex < images.length - 1 ? slideIndex + 1 : images.length - 1
    );
  };

  return (
    <SliderContainer>
      <SlideButton type="button" onClick={handlePrev}>
        {"<"}
      </SlideButton>
      <ImageSliderComponent slideIndex={slideIndex}>
        {images.map((img, index) => (
          <Image key={index} src={img} alt="이미지" />
        ))}
      </ImageSliderComponent>
      <SlideButton type="button" onClick={handleNext}>
        {">"}
      </SlideButton>
    </SliderContainer>
  );
};

export default ImageSlider;
