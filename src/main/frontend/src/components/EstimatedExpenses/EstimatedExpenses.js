import React, { useEffect, useState } from "react";
import { getEstimatedExpenses } from "../../api";
import "./EstimatedExpenses.css";

const EstimatedExpenses = () => {
  const [estimatedExpenses, setEstimatedExpenses] = useState({
    rows: [],
    total1to7: 0,
    total8to14: 0,
    total15to21: 0,
    total22to31: 0,
    total: 0,
    totalUsd: 0,
  });

  useEffect(() => {
    async function setupData() {
      const response = await getEstimatedExpenses();
      setEstimatedExpenses(response);
    }
    setupData();
  }, []);

  return (
    <div className="estimated-expenses-container">
      <div className="estimated-expense-title">
        <span>Estimated Expenses</span>
      </div>
      <table className="estimated-expenses-table">
        <thead>
          <tr>
            <th>Category</th>
            <th>Days 1 - 7</th>
            <th>Days 8 - 14</th>
            <th>Days 15 - 21</th>
            <th>Days 22 - 31</th>
            <th>Total</th>
          </tr>
        </thead>
        <tbody>
          {estimatedExpenses.rows.map((row) => (
            <tr key={row.category.id}>
              <td>{row.category.name}</td>
              <td>{row.days1to7}</td>
              <td>{row.days8to14}</td>
              <td>{row.days15to21}</td>
              <td>{row.days22to31}</td>
              <td>{row.totalPerRow}</td>
            </tr>
          ))}
          <tr>
            <td>Weekly Totals</td>
            <td>{estimatedExpenses.total1to7}</td>
            <td>{estimatedExpenses.total8to14}</td>
            <td>{estimatedExpenses.total15to21}</td>
            <td>{estimatedExpenses.total22to31}</td>
            <td>{estimatedExpenses.total}</td>
          </tr>
        </tbody>
      </table>
    </div>
  );
};

export default EstimatedExpenses;
