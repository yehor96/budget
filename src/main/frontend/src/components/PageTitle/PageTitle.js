import React from "react";
import "./PageTitle.css";

const PageTitle = (props) => {
  return <div className="title">{props.pageName}</div>;
};

export default PageTitle;
