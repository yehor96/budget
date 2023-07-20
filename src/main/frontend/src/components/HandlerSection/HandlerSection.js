import React, { useState } from "react";
import AddModal from "../../modals/AddModal/AddModal";

const HandlerSection = (props) => {
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
            categories={props.categories}
            onClose={() => {
              setShowAddModal(false);
              window.location.reload();
            }}
          />
        </div>
      </div>
    </div>
  );
};

export default HandlerSection;
