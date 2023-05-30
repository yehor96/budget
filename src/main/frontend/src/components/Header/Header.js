import React from "react";
import BASE_URL from "../../config";
import "./Header.css";

const Header = (props) => {
  const Button = ({ children, disabled }) => (
    <button className={`btn ${props.selected === children ? "selected" : ""} ${disabled ? "disabled" : ""}`}>
      {children}
    </button>
  );

  return (
    <div className="header">
      <div className="btn-container">
        <Button>Expenses</Button>
        <Button disabled>Planning</Button>
        <a href={BASE_URL + "/swagger"}>
          <Button>API</Button>
        </a>
        <Button disabled>Logout</Button>
      </div>
    </div>
  );
};

export default Header;
