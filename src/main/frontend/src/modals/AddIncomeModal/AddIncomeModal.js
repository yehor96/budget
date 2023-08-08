import React, { useState } from "react";
import { addIncomeSource, GENERAL_API_ERROR_POST } from "../../api";

const AddIncomeModal = (props) => {
  const [errorMessage, setErrorMessage] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  const displayResponse = (response, e) => {
    setErrorMessage("");
    if (response.status !== 200) {
      let errMsg = response.message ? response.message : GENERAL_API_ERROR_POST;
      setErrorMessage(errMsg);
    } else {
      setSuccessMessage("Income source added successfully!");
      setTimeout(() => {
        setSuccessMessage("");
      }, 4000);
      e.target.reset();
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const incomeSource = {
      name: e.target.name.value,
      value: e.target.value.value,
      currency: e.target.currency.value,
    };
    const response = await addIncomeSource(incomeSource);
    displayResponse(response, e);
  };

  if (!props.show) return null;

  return (
    <div className="modal">
      <div className="modal-content">
        <div className="modal-header">
          <h4 className="modal-title">Add Income Source</h4>
        </div>
        <div className="modal-body">
          <form onSubmit={handleSubmit}>
            <div className="input-container">
              <div>
                <label htmlFor="name">Name:</label>
                <input type="string" id="name" name="name" required />
              </div>
              <div>
                <label htmlFor="value">Value:</label>
                <input type="string" id="value" name="value" required />
              </div>
              <div>
                <label htmlFor="currency">Currency:</label>
                <select id="currency" name="currency" required>
                  <option value="USD">USD</option>
                  <option value="EUR">EUR</option>
                  <option value="UAH">UAH</option>
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

export default AddIncomeModal;
