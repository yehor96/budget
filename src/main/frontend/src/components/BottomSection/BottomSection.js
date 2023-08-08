import React from "react";
import "./BottomSection.css";

const BottomSection = (props) => {
  const { totalExpense, totalRegular, totalNonRegular } = props.statistics;

  return (
    <div className="botton-section-container">
      <div className="additional-info-container">
        <div className="row">
          <span>Total</span>
          <span>{totalExpense}</span>
        </div>
        <div className="row">
          <span>Total Regular</span>
          <span>{totalRegular}</span>
        </div>
        <div className="row">
          <span>Total Non-regular</span>
          <span>{totalNonRegular}</span>
        </div>
      </div>
    </div>
  );
};

export default BottomSection;
