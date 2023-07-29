import React, { useState } from "react";
import AddModal from "../../modals/AddModal/AddModal";
import "./DetailedCellModal.css";
import { formatDate } from "../../utils.js";
import { deleteExpense } from "../../api";

const DetailedCellModal = (props) => {
  const { category, date } = props.detailedCellInfo;
  const [showAddModal, setShowAddModal] = useState(false);

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
            {category.name} expenses for {formatDate(date)}
          </h4>
        </div>
        <div className="modal-body">
          {props.detailedCellInfo.expenses.map((expense) => {
            return (
              <div
                key={expense.id}
                className={`expense-container ${
                  expense.isRegular ? "regular" : "non-regular"
                }`}
                id={expense.id}
              >
                <div className="expense-item">
                  <span>Expense #{expense.id}</span>
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
        <div className="modal-btns-container">
          <button className="btn" onClick={props.onClose}>
            Close
          </button>
          <button className="btn" onClick={() => setShowAddModal(true)}>
            Add
          </button>
        </div>
      </div>
      <AddModal
        show={showAddModal}
        category={category}
        date={date}
        addNewExpense={(newExpense) => {
          props.detailedCellInfo.expenses = [...props.detailedCellInfo.expenses, newExpense];
        }}
        onClose={() => {
          setShowAddModal(false);
        }}
      />
    </div>
  );
};

export default DetailedCellModal;
