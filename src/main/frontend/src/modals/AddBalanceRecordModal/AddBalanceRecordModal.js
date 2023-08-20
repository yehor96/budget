import React, { useState } from "react";
import { addBalanceRecord, GENERAL_API_ERROR_POST } from "../../api";
import { formatDate } from "../../utils.js";
import "./AddBalanceRecordModal.css";

const AddBalanceRecordModal = (props) => {
  const [errorMessage, setErrorMessage] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const [balanceItems, setBalanceItems] = useState([
    { itemName: "", card: "", cash: "" },
  ]);

  const displayResponse = (response) => {
    setErrorMessage("");
    if (response.status !== 200) {
      let errMsg = response.message ? response.message : GENERAL_API_ERROR_POST;
      setErrorMessage(errMsg);
    } else {
      setSuccessMessage("Balance record added successfully!");
      setTimeout(() => {
        setSuccessMessage("");
        props.onClose();
      }, 4000);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (e.nativeEvent.submitter.name === "submit") {
      if (!areBalanceItemsValid()) {
        return;
      }
      const balanceRecord = {
        balanceItems: balanceItems,
        date: new Date(),
      };
      const response = await addBalanceRecord(balanceRecord);
      displayResponse(response);
    }
  };

  function areBalanceItemsValid() {
    for (const item of balanceItems) {
      if (!item.itemName || !item.card || !item.cash) {
        setErrorMessage("Some fields are empty!");
        setTimeout(() => {
          setErrorMessage("");
        }, 4000);
        return false;
      }
      if (isNaN(item.card) || isNaN(item.card)) {
        setErrorMessage("Card and cash fields should be numbers!");
        setTimeout(() => {
          setErrorMessage("");
        }, 4000);
        return false;
      }
    }
    return true;
  }

  const handleFormChange = (event, index) => {
    let data = [...balanceItems];
    data[index][event.target.name] = event.target.value;
    setBalanceItems(data);
  };

  const addBalanceItem = () => {
    let item = { itemName: "", card: "", cash: "" };
    setBalanceItems([...balanceItems, item]);
  };

  const removeStorageItem = (removeIndex) => {
    if (balanceItems.length === 1) {
      setErrorMessage("Balance record should have at least one entry!");
      setTimeout(() => {
        setErrorMessage("");
      }, 4000);
      return;
    }
    const data = balanceItems.filter((_, index) => index !== removeIndex);
    setBalanceItems(data);
  };

  if (!props.show) return null;

  return (
    <div className="modal">
      <div className="modal-content">
        <div className="modal-header">
          <h4 className="modal-title">Add Balance Record</h4>
        </div>
        <div className="modal-body">
          <form onSubmit={handleSubmit}>
            <div className="input-container">
              {balanceItems.map((form, index) => {
                return (
                  <div className="balance-item" key={index}>
                    <div>
                      <label htmlFor="name">Name:</label>
                      <input
                        type="string"
                        id="itemName"
                        name="itemName"
                        value={form.itemName}
                        onChange={(event) => handleFormChange(event, index)}
                      />
                    </div>
                    <div>
                      <label htmlFor="value">Card:</label>
                      <input
                        type="string"
                        id="card"
                        name="card"
                        value={form.card}
                        onChange={(event) => handleFormChange(event, index)}
                      />
                    </div>
                    <div>
                      <label htmlFor="value">Cash:</label>
                      <input
                        type="string"
                        id="cash"
                        name="cash"
                        value={form.cash}
                        onChange={(event) => handleFormChange(event, index)}
                      />
                    </div>
                    <button
                      className="btn-delete"
                      onClick={() => removeStorageItem(index)}
                    >
                      Delete
                    </button>
                  </div>
                );
              })}
              <div>
                <label htmlFor="name">Date:</label>
                <span>{formatDate(new Date())}</span>
              </div>
            </div>
            <button type="submit" className="btn" name="submit">
              Add
            </button>
            <button className="btn" onClick={props.onClose}>
              Close
            </button>
            <button className="btn plus" onClick={addBalanceItem}>
              +
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

export default AddBalanceRecordModal;
