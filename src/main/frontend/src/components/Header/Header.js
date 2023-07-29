import React from "react";
import "./Header.css";

const Header = (props) => {
  const Button = ({ children, disabled }) => (
    <button
      className={`btn ${
        props.selected === children ? "current-selected" : ""
      } ${disabled ? "disabled" : ""}`}
    >
      {children}
    </button>
  );

  return (
    <div className="header">
      <div className="logo-container">
        <img src="/favicon.png" alt="Logo" />
      </div>
      <div className="title">Budget App</div>
      <div className="btn-container-header">
        <Button>Expenses</Button>
        <Button disabled>Planning</Button>
        <a href="http://localhost:8080/swagger">
          <Button>API</Button>
        </a>
      </div>
    </div>
  );
};

export default Header;
