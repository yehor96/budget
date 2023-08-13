import React, { useState } from "react";
import { addStroageRecord, GENERAL_API_ERROR_POST } from "../../api";
import { formatDate } from "../../utils.js";
import "./AddStorageRecordModal.css";

const AddStorageRecordModal = (props) => {
  const [errorMessage, setErrorMessage] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const [storageItems, setStorageItems] = useState([
    { name: "", value: "", currency: "" },
  ]);

  const displayResponse = (response, e) => {
    setErrorMessage("");
    if (response.status !== 200) {
      let errMsg = response.message ? response.message : GENERAL_API_ERROR_POST;
      setErrorMessage(errMsg);
    } else {
      setSuccessMessage("Storage record added successfully!");
      setTimeout(() => {
        setSuccessMessage("");
        props.onClose();
      }, 4000);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (e.nativeEvent.submitter.name === "submit") {
      if (!areStorageItemsValid()) {
        return;
      }
      const storageRecord = {
        storageItems: storageItems,
        date: new Date(),
      };
      const response = await addStroageRecord(storageRecord);
      displayResponse(response, e);
    }
  };

  function areStorageItemsValid() {
    for (const item of storageItems) {
      if (!item.name || !item.value || !item.currency) {
        setErrorMessage("Some fields are empty!");
        setTimeout(() => {
          setErrorMessage("");
        }, 4000);
        return false;
      }
      if (isNaN(item.value)) {
        setErrorMessage("Value field should be a number!");
        setTimeout(() => {
          setErrorMessage("");
        }, 4000);
        return false;
      }
    }
    return true;
  }

  const handleFormChange = (event, index) => {
    let data = [...storageItems];
    data[index][event.target.name] = event.target.value;
    setStorageItems(data);
  };

  const addStorageItem = () => {
    let item = { name: "", value: "", currency: "" };
    setStorageItems([...storageItems, item]);
  };

  const removeStorageItem = (removeIndex) => {
    if (storageItems.length === 1) {
      setErrorMessage("Storage record should have at least one entry!");
      setTimeout(() => {
        setErrorMessage("");
      }, 4000);
      return;
    }
    const data = storageItems.filter((_, index) => index !== removeIndex);
    setStorageItems(data);
  };

  if (!props.show) return null;

  return (
    <div className="modal">
      <div className="modal-content">
        <div className="modal-header">
          <h4 className="modal-title">Add Storage Record</h4>
        </div>
        <div className="modal-body">
          <form onSubmit={handleSubmit}>
            <div className="input-container">
              {storageItems.map((form, index) => {
                return (
                  <div className="storage-item" key={index}>
                    <div>
                      <label htmlFor="name">Name:</label>
                      <input
                        type="string"
                        id="name"
                        name="name"
                        value={form.name}
                        onChange={(event) => handleFormChange(event, index)}
                      />
                    </div>
                    <div>
                      <label htmlFor="value">Value:</label>
                      <input
                        type="string"
                        id="value"
                        name="value"
                        value={form.value}
                        onChange={(event) => handleFormChange(event, index)}
                      />
                    </div>
                    <div>
                      <label htmlFor="currency">Currency:</label>
                      <select
                        id="currency"
                        name="currency"
                        value={form.currency}
                        onChange={(event) => handleFormChange(event, index)}
                      >
                        <option></option>
                        <option value="USD">USD</option>
                        <option value="EUR">EUR</option>
                        <option value="UAH">UAH</option>
                      </select>
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
            <button className="btn plus" onClick={addStorageItem}>
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

export default AddStorageRecordModal;
