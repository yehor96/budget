import React, { useEffect, useState } from "react";
import Header from "../../components/Header/Header";
import PageTitle from "../../components/PageTitle/PageTitle";
import { getCategories, getMonthlyExpenses } from "../../api";
import "./Expenses.css";

const PAGE_NAME = "Expenses";

function Expenses() {
  const [expenses, setExpenses] = useState([]);
  const [columns, setColumns] = useState([]);
  const [rows, setRows] = useState([]);

  useEffect(() => {
    initialDataFetch();
  }, []);

  const initialDataFetch = async () => {
    setupExpenses();
    setupColumns();
    setupRows();
  };

  const setupExpenses = async () => {
    const currentDate = new Date();
    const currentMonthName = currentDate.toLocaleString("default", {
      month: "long",
    });
    const currentYear = currentDate.getFullYear();
    const response = await getMonthlyExpenses({
      month: currentMonthName,
      year: currentYear,
    });
    setExpenses(response.data);
  };

  const setupColumns = async () => {
    const currentDate = new Date();
    const currentMonth = currentDate.getMonth();
    const currentYear = currentDate.getFullYear();
    const days = new Date(currentYear, currentMonth + 1, 0).getDate();
    setColumns(Array.from({ length: days }, (_, index) => index + 1));
  };

  const setupRows = async () => {
    const response = await getCategories();
    setRows(response.data);
  };

  return (
    <div>
      <Header selected={PAGE_NAME} />
      <PageTitle pageName={PAGE_NAME} />

      <table>
        <thead>
          <tr>
            <th></th>
            {columns.map((column) => (
              <th key={column}>{column}</th>
            ))}
          </tr>
        </thead>

        <tbody>
          {rows.map((row) => (
            <tr key={row.id}>
              <td>{row.name}</td>
              {columns.map((column) => (
                <td key={column}>
                  {expenses
                    .filter((expense) => expense.category.id === row.id)
                    .filter(
                      (expense) =>
                        column == parseInt(expense.date.split("-")[2])
                    )
                    .map((expense) => expense.value)
                    .reduce((val, newVal) => (val + newVal), 0)}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default Expenses;
