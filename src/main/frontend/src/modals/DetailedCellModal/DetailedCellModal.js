import React from "react";
import "./DetailedCellModal.css";
import { formatDate } from "../../utils.js";

const DetailedCellModal = (props) => {
  if (!props.show) return null;
  return (
    <div className="modal">
      <div className="modal-content">
        <div className="modal-header">
          <h4 className="modal-title">
            {props.expenses[0].category.name} expenses for{" "}
            {formatDate(props.expenses[0].date)}
          </h4>
        </div>
        <div className="modal-body">
          <div>
            {props.expenses.map((expense, index) => {
              return (
                <div
                  key={index}
                  className={`expense-container ${
                    expense.isRegular ? "regular" : "non-regular"
                  }`}
                >
                  <div className="expense-item">
                    <span>Expense #{index + 1}</span>
                    <div>value: {expense.value}</div>
                    <div>regular: {expense.isRegular.toString()}</div>
                  </div>
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
