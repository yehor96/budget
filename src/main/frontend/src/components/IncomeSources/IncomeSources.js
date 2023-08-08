import React, { useEffect, useState } from "react";
import { getIncomeSources } from "../../api";
import "./IncomeSources.css";
import AddIncomeModal from "../../modals/AddIncomeModal/AddIncomeModal";

const IncomeSources = () => {
  const [showAddIncomeModal, setShowAddIncomeModal] = useState(false);
  const [incomeSources, setIncomeSources] = useState({
    incomeSources: [],
    total: 0,
    totalCurrency: "",
  });

  useEffect(() => {
    async function setupData() {
      const response = await getIncomeSources();
      setIncomeSources(response);
    }
    setupData();
  }, []);

  return (
    <div className="income-sources-container">
      <div className="title-container">
        <div className="income-title">Income Sources</div>
        <div>
          <button
            className="btn plus"
            onClick={() => {
              setShowAddIncomeModal(true);
            }}
          >
            +
          </button>
        </div>
        <AddIncomeModal
          show={showAddIncomeModal}
          onClose={() => {
            setShowAddIncomeModal(false);
            window.location.reload();
          }}
        />
      </div>
      <div className="income-table">
        {incomeSources.incomeSources.map((income) => (
          <div className="income-item" key={income.id}>
            <span>{income.name}</span>
            <span>{income.value}</span>
            <span>{income.currency}</span>
          </div>
        ))}
        <div className="income-total">
          <span>Total</span>
          <span>{incomeSources.total}</span>
          <span>{incomeSources.totalCurrency}</span>
        </div>
      </div>
    </div>
  );
};

export default IncomeSources;
