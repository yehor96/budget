import React from "react";
import "./DetailedCellModal.css";
import { formatDate } from "../../utils.js";
import { deleteExpense } from "../../api";

const DetailedCellModal = (props) => {
  const executeDeleteExpense = async (id) => {
    const response = await deleteExpense(id);
    if (response.status === 200) {
      document.getElementById(id).remove();
    }
  };

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
          {props.expenses.map((expense, index) => {
            return (
              <div
                key={index}
                className={`expense-container ${
                  expense.isRegular ? "regular" : "non-regular"
                }`}
                id={expense.id}
              >
                <div className="expense-item">
                  <span>Expense #{index + 1}</span>
                  <div>value: {expense.value}</div>
                  <div>regular: {expense.isRegular.toString()}</div>
                </div>
                <button
                  className="btn-delete"
                  onClick={(event) =>
                    executeDeleteExpense(expense.id, event.target)
                  }
                >
                  Delete
                </button>
              </div>
            );
          })}
        </div>
        <button className="btn" onClick={props.onClose}>
          Close
        </button>
      </div>
    </div>
  );
};

export default DetailedCellModal;
