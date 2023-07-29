import React, { useState } from "react";
import { addExpense, GENERAL_API_ERROR_POST } from "../../api";
import "./AddModal.css";
import { formatDate } from "../../utils.js";

const AddModal = (props) => {
  const [errorMessage, setErrorMessage] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  const displayResponse = (response, newExpense, e) => {
    setErrorMessage("");
    if (response.status !== 200) {
      let errMsg = response.message ? response.message : GENERAL_API_ERROR_POST;
      setErrorMessage(errMsg);
    } else {
      setSuccessMessage("Expense added successfully!");
      newExpense.id = response.data.id;
      props.addNewExpense(newExpense);
      setTimeout(() => {
        setSuccessMessage("");
      }, 4000);
      e.target.reset();
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const newExpense = {
      value: e.target.value.value,
      date: props.date,
      categoryId: props.category.id,
      isRegular: e.target.is_regular.checked,
    };
    const response = await addExpense(newExpense);
    displayResponse(response, newExpense, e);
  };

  if (!props.show) return null;

  return (
    <div className="modal">
      <div className="modal-content">
        <div className="modal-header">
          <h4 className="modal-title">Add Expense</h4>
        </div>
        <div className="modal-body">
          <form onSubmit={handleSubmit}>
            <div className="input-container">
              <div>
                <label htmlFor="value">Value:</label>
                <input type="string" id="value" name="value" required />
              </div>
              <div>
                <label htmlFor="date">Date:</label>
                <span>{formatDate(props.date)}</span>
              </div>
              <div>
                <label htmlFor="category">Category:</label>
                <span>{props.category.name}</span>
              </div>
              <div>
                <label htmlFor="is_regular">Is regular:</label>
                <input type="checkbox" id="is_regular" name="is_regular" />
              </div>
            </div>
            <button type="submit" className="btn">
              Add
            </button>
            <button className="btn" onClick={props.onClose}>
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

export default AddModal;
