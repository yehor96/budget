import React, { useState } from "react";
import AddModal from "../../modals/AddModal/AddModal";
import "./HandlerSection.css";

const HandlerSection = (props) => {
  const [showAddModal, setShowAddModal] = useState(false);

  const { totalExpense, totalRegular, totalNonRegular } = props.statistics;

  return (
    <div className="handler-container">
      <div className="additional-info-container">
        <div className="row">
          <span>Total:</span>
          <span>{totalExpense}</span>
        </div>
        <div className="row">
          <span>Total Regular:</span>
          <span>{totalRegular}</span>
        </div>
        <div className="row">
          <span>Total Non-regular:</span>
          <span>{totalNonRegular}</span>
        </div>
      </div>
      <div className="btns-container">
        <div className="btn-container">
          <button className="btn" onClick={() => setShowAddModal(true)}>
            Add Expense
          </button>
          <AddModal
            show={showAddModal}
            categories={props.categories}
            onClose={() => {
              setShowAddModal(false);
              window.location.reload();
            }}
          />
        </div>
      </div>
    </div>
  );
};

export default HandlerSection;
