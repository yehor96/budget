import React from "react";
import "./DetailedCellModal.css";

const DetailedCellModal = (props) => {
  if (!props.show) return null;
  return (
    <div className="modal">
      <div className="modal-content">
        <div className="modal-header">
          <h4 className="modal-title">Detailed Cell Modal</h4>
        </div>
        <div className="modal-body">
          <div>
            {props.expenses.map((expense, index) => {
              return (
                <div>
                  Expense #{index + 1} value: {expense.value}
                </div>
              );
            })}
          </div>
        </div>
        <button className="btn" onClick={props.onClose}>
          Close
        </button>
      </div>
    </div>
  );
};

export default DetailedCellModal;
