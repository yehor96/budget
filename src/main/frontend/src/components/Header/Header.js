import React from "react";
import "./Header.css";

const Header = (props) => {
  const Button = ({ children, disabled }) => (
    <button
      className={`btn ${props.selected === children ? "selected" : ""} ${
        disabled ? "disabled" : ""
      }`}
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
      <div className="btn-container">
        <Button>Expenses</Button>
        <Button disabled>Planning</Button>
        <a href="/swagger">
          <Button>API</Button>
        </a>
        <Button disabled>Logout</Button>
      </div>
    </div>
  );
};

export default Header;
