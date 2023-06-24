import React from "react";
import "./NavigationBar.css";

const NavigationBar = (props) => {
  return (
    <nav className="nav-container">
      <div className="year">{props.currentYear}</div>
      <div className="btns-container">
        <button onClick={props.onPreviousMonth} className="btn arr">
        ← Prev Month
        </button>
        <span className="current-month">{props.currentMonth}</span>
        <button onClick={props.onNextMonth} className="btn arr">
        Next Month →
        </button>
      </div>
    </nav>
  );
};

export default NavigationBar;
