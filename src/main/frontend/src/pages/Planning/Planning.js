import React from "react";
import Header from "../../components/Header/Header";
import PageTitle from "../../components/PageTitle/PageTitle";
import IncomeSources from "../../components/IncomeSources/IncomeSources";
import Storage from "../../components/Storage/Storage";
import Balance from "../../components/Balance/Balance";
import EstimatedExpenses from "../../components/EstimatedExpenses/EstimatedExpenses";

const PAGE_NAME = "Planning";

function Planning() {
  return (
    <div className="planning-page">
      <Header selected={PAGE_NAME} />
      <PageTitle pageName={PAGE_NAME} />
      <IncomeSources />
      <Storage />
      <Balance />
      <EstimatedExpenses />
    </div>
  );
}

export default Planning;
