import React from "react";
import { addExpense } from "../../api";

const AddModal = (props) => {
  const handleSubmit = async (e) => {
    e.preventDefault();
    const response = await addExpense({
      value: e.target.value.value,
      date: e.target.date.value,
      categoryId: e.target.category.value,
    });
    if (response.status !== 200) {
        // todo display error message
        console.log("error: " + response.message);
    } else {
        // todo display success message
        console.log("success: " + response.message);
        e.target.reset();
        // todo close modal
        // todo refresh expense table
    }
  };

  if (!props.show) return null;
  // todo use drop down with category names
  // todo beautify form with css
  return (
    <div className="modal">
      <div className="modal-content">
        <div className="modal-header">
          <h4 className="modal-title">Add Expense</h4>
        </div>
        <div className="modal-body">
          <form onSubmit={handleSubmit}>
            <div>
              <label htmlFor="value">Value:</label>
              <input type="number" id="value" name="value" required />
            </div>
            <div>
              <label htmlFor="date">Date:</label>
              <input type="date" id="date" name="date" required />
            </div>
            <div>
              <label htmlFor="category">Category:</label>
              <input type="number" id="category" name="category" required />
            </div>
            <button type="submit" className="btn">
              Add
            </button>
            <button className="btn" onClick={props.onClose}>
              Close
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default AddModal;
