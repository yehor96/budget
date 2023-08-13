import React, { useEffect, useState } from "react";
import { getLatestStorageRecord } from "../../api";
import "./Storage.css";
import { formatDate } from "../../utils.js";
import AddStorageRecordModal from "../../modals/AddStorageRecordModal/AddStorageRecordModal";

const Storage = () => {
  const [showAddStorageRecord, setShowAddStorageRecord] = useState(false);
  const [storageRecord, setStorageRecord] = useState({
    storageItems: [],
    date: "",
    storedInTotal: 0,
  });

  useEffect(() => {
    async function setupData() {
      const response = await getLatestStorageRecord();
      setStorageRecord(response);
    }
    setupData();
  }, []);

  return (
    <div className="storage-container">
      <div className="title-container">
        <div className="storage-title">Storage</div>
        <div>
          <button
            className="btn plus"
            onClick={() => {
              setShowAddStorageRecord(true);
            }}
          >
            +
          </button>
        </div>
        <AddStorageRecordModal
          show={showAddStorageRecord}
          onClose={() => {
            setShowAddStorageRecord(false);
            window.location.reload();
          }}
        />
      </div>
      <div className="storage-table">
        {storageRecord.storageItems.map((item) => (
          <div className="storage-item" key={item.id}>
            <span>{item.name}</span>
            <span>{item.value}</span>
            <span>{item.currency}</span>
          </div>
        ))}
        <div className="storage-total">
          <span>Total</span>
          <span>{storageRecord.storedInTotal}</span>
          <span>USD</span>
        </div>
        <div className="storage-date">
          <span>Record for {formatDate(storageRecord.date)}</span>
        </div>
      </div>
    </div>
  );
};

export default Storage;
