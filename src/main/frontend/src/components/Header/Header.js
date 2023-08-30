import React from "react";
import "./Header.css";

const Header = (props) => {
  const Button = ({ children }) => (
    <button
      className={`btn ${props.selected === children ? "current-selected" : ""}`}
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
        <a href="/expenses">
          <Button>Expenses</Button>
        </a>
        <a href="/planning">
          <Button>Planning</Button>
        </a>
        <a href="http://localhost:8080/swagger">
          <Button>API</Button>
        </a>
      </div>
    </div>
  );
};

export default Header;
