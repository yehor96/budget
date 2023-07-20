import React, { useState } from "react";
import { addExpense, GENERAL_API_ERROR_POST } from "../../api";
import "./AddModal.css";

const AddModal = (props) => {
  const [errorMessage, setErrorMessage] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  const displayResponse = (response, e) => {
    setErrorMessage("");
    if (response.status !== 200) {
      let errMsg = response.message ? response.message : GENERAL_API_ERROR_POST;
      setErrorMessage(errMsg);
    } else {
      setSuccessMessage("Expense added successfully!");
      setTimeout(() => {
        setSuccessMessage("");
      }, 4000);
      e.target.reset();
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const response = await addExpense({
      value: e.target.value.value,
      date: e.target.date.value,
      categoryId: e.target.category.value,
    });
    displayResponse(response, e);
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
                <input type="date" id="date" name="date" required />
              </div>
              <div>
                <label htmlFor="category">Category:</label>
                <select id="category" name="category" required>
                  {props.categories.map((category) => (
                    <option key={category.id} value={category.id}>
                      {category.name}
                    </option>
                  ))}
                </select>
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
