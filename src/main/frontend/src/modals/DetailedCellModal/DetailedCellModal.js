import React, { useState } from "react";
import AddExpenseModal from "../AddExpenseModal/AddExpenseModal";
import "./DetailedCellModal.css";
import { formatDate } from "../../utils.js";
import { deleteExpense } from "../../api";
import EditExpenseModal from "../EditExpenseModal/EditExpenseModal";

const DetailedCellModal = (props) => {
  const { category, date } = props.detailedCellInfo;
  const [showAddExpenseModal, setShowAddExpenseModal] = useState(false);
  const [showEditExpenseModal, setShowEditExpenseModal] = useState(false);
  const [expenseForEdit, setExpenseForEdit] = useState(null);

  // If user opens empty DetailedCellModal -> go straight to AddExpenseModal
  const [passthroughToAddExpense, setPassthroughToAddExpense] = useState(false);

  const executeDeleteExpense = async (id) => {
    const response = await deleteExpense(id);
    if (response.status === 200) {
      document.getElementById(id).remove();
    }
  };

  if (!props.show) return null;
  if (
    props.detailedCellInfo.expenses.length === 0 &&
    !passthroughToAddExpense
  ) {
    setShowAddExpenseModal(true);
    setPassthroughToAddExpense(true);
  }
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
                  {expense.note && <div>note: {expense.note}</div>}
                </div>
                <div className="expense-btns-container">
                  <button
                    className="btn-exp edit"
                    onClick={() => {
                      expense.category = category;
                      setExpenseForEdit(expense);
                      setShowEditExpenseModal(true);
                    }}
                  >
                    Edit
                  </button>
                  <button
                    className="btn-exp delete"
                    onClick={(event) =>
                      executeDeleteExpense(expense.id, event.target)
                    }
                  >
                    Delete
                  </button>
                </div>
              </div>
            );
          })}
        </div>
        <div className="modal-btns-container">
          <button className="btn" onClick={() => setShowAddExpenseModal(true)}>
            Add
          </button>
          <button className="btn" onClick={props.onClose}>
            Close
          </button>
        </div>
      </div>
      <AddExpenseModal
        show={showAddExpenseModal}
        category={category}
        date={date}
        addNewExpense={(newExpense) => {
          props.detailedCellInfo.expenses = [
            ...props.detailedCellInfo.expenses,
            newExpense,
          ];
          setPassthroughToAddExpense(false);
        }}
        onClose={() => {
          setShowAddExpenseModal(false);
          if (passthroughToAddExpense) {
            props.onClose();
          }
        }}
      />
      <EditExpenseModal
        show={showEditExpenseModal}
        expense={expenseForEdit}
        date={date}
        editExpense={(editedExpense) => {
          props.detailedCellInfo.expenses = props.detailedCellInfo.expenses.map(
            (exisingExpense) =>
              exisingExpense.id === editedExpense.id
                ? editedExpense
                : exisingExpense
          );
        }}
        onClose={() => {
          setShowEditExpenseModal(false);
          setExpenseForEdit(null);
        }}
      />
    </div>
  );
};

export default DetailedCellModal;
