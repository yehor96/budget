import React, { useState } from "react";
import { editExpense, GENERAL_API_ERROR_POST } from "../../api";
import { formatDate } from "../../utils.js";

const EditExpenseModal = (props) => {
  const [errorMessage, setErrorMessage] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  const [value, setValue] = useState(null);
  const [note, setNote] = useState(null);
  const [isRegular, setIsRegular] = useState(null);

  const displayResponse = (response, expense, e) => {
    setErrorMessage("");
    if (response.status !== 200) {
      let errMsg = response.message ? response.message : GENERAL_API_ERROR_POST;
      setErrorMessage(errMsg);
    } else {
      setSuccessMessage("Expense edited successfully!");
      props.editExpense(expense);
      setTimeout(() => {
        setSuccessMessage("");
      }, 4000);
      e.target.reset();
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const updatedExpense = {
      id: props.expense.id,
      value: e.target.value.value,
      date: props.date,
      categoryId: props.expense.category.id,
      isRegular: e.target.is_regular.checked,
      note: e.target.note.value,
    };
    const response = await editExpense(updatedExpense);
    displayResponse(response, updatedExpense, e);
  };

  const clearFields = () => {
    setValue(null);
    setNote(null);
    setIsRegular(null);
  };

  if (!props.show) return null;

  return (
    <div className="modal">
      <div className="modal-content">
        <div className="modal-header">
          <h4 className="modal-title">Edit Expense</h4>
        </div>
        <div className="modal-body">
          <form onSubmit={handleSubmit}>
            <div className="input-container">
              <div>
                <label htmlFor="value">Value:</label>
                <input
                  type="string"
                  id="value"
                  name="value"
                  required
                  value={value == null ? props.expense.value : value}
                  onChange={(e) => setValue(e.target.value)}
                />
              </div>
              <div>
                <label htmlFor="date">Date:</label>
                <span>{formatDate(props.expense.date)}</span>
              </div>
              <div>
                <label htmlFor="category">Category:</label>
                <span>{props.expense.category.name}</span>
              </div>
              <div>
                <label htmlFor="note">Note:</label>
                <input
                  type="string"
                  id="note"
                  name="note"
                  value={note == null ? props.expense.note : note}
                  onChange={(e) => setNote(e.target.value)}
                />
              </div>
              <div>
                <label htmlFor="is_regular">Is regular:</label>
                <input
                  type="checkbox"
                  id="is_regular"
                  name="is_regular"
                  checked={
                    isRegular == null ? props.expense.isRegular : isRegular
                  }
                  onChange={(e) => {
                    console.log("setting " + e.target.checked);
                    setIsRegular(e.target.checked);
                  }}
                />
              </div>
            </div>
            <button type="submit" className="btn">
              Edit
            </button>
            <button
              className="btn"
              onClick={() => {
                clearFields();
                props.onClose();
              }}
            >
              Close
            </button>
          </form>
        </div>
        {errorMessage && <div className="error-message">{errorMessage}</div>}
        {successMessage && (
          <div className="success-message">{successMessage}</div>
        )}
      </div>
    </div>
  );
};

export default EditExpenseModal;
