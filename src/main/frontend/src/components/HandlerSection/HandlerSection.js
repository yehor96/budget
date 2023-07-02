import React, { useState } from "react";
import AddModal from "../AddModal/AddModal";

const HandlerSection = () => {
  const [showAddModal, setShowAddModal] = useState(false);

  return (
    <div className="handler-container">
      <div className="btns-container">
        <div className="btn-container">
          <button className="btn" onClick={() => setShowAddModal(true)}>
            Add Expense
          </button>
          <AddModal
            show={showAddModal}
            onClose={() => setShowAddModal(false)}
          />
        </div>
      </div>
    </div>
  );
};

export default HandlerSection;
