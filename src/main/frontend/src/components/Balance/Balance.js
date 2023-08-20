import React, { useEffect, useState } from "react";
import { getLatestBalanceRecord } from "../../api";
import "./Balance.css";
import { formatDate } from "../../utils.js";
import AddBalanceRecordModal from "../../modals/AddBalanceRecordModal/AddBalanceRecordModal";

const Balance = () => {
  const [showAddBalanceRecordModal, setShowAddBalanceRecordModal] =
    useState(false);
  const [balanceRecord, setBalanceRecord] = useState({
    balanceItems: [],
    date: "",
    totalBalance: 0,
  });

  useEffect(() => {
    async function setupData() {
      const response = await getLatestBalanceRecord();
      setBalanceRecord(response);
    }
    setupData();
  }, []);

  return (
    <div className="balance-container">
      <div className="title-container">
        <div className="balance-title">Balance</div>
        <div>
          <button
            className="btn plus"
            onClick={() => {
              setShowAddBalanceRecordModal(true);
            }}
          >
            +
          </button>
        </div>
        <AddBalanceRecordModal
          show={showAddBalanceRecordModal}
          onClose={() => {
            setShowAddBalanceRecordModal(false);
            window.location.reload();
          }}
        />
      </div>
      <div className="balance-table">
        {balanceRecord.balanceItems.map((item) => (
          <div className="balance-item" key={item.id}>
            <span>{item.itemName}</span>
            <span>{item.cash}</span>
            <span>{item.card}</span>
          </div>
        ))}
        <div className="balance-total">
          <span>Total</span>
          <span>{balanceRecord.totalBalance}</span>
          <span>UAH</span>
        </div>
        <div className="balance-date">
          <span>Record for {formatDate(balanceRecord.date)}</span>
        </div>
      </div>
    </div>
  );
};

export default Balance;
