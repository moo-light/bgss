import React from "react";
import { Carousel } from "antd";

const CarouselComponent = () => (
  <Carousel
    autoplay
    style={{
      width: "100%",
      borderRadius: "8px",
      overflow: "hidden",
      maxHeight: "600px",
    }}
  >
    <div>
      <img
        src="/images/bannerBGSS.png"
        alt="Banner 1"
        style={{ width: "100%", borderRadius: "8px" }}
      />
    </div>
    <div>
      <img
        src="/images/bannerBGSS2.png"
        alt="Banner 2"
        style={{ width: "100%", borderRadius: "8px" }}
      />
    </div>
    <div>
      <img
        src="/images/bannerBGSS3.png"
        alt="Banner 3"
        style={{ width: "100%", borderRadius: "8px" }}
      />
    </div>
  </Carousel>
);

export default CarouselComponent;
