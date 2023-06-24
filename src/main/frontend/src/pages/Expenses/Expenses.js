import React, { useEffect, useState } from "react";
import { getCategories, getMonthlyExpenses } from "../../api";
import Header from "../../components/Header/Header";
import PageTitle from "../../components/PageTitle/PageTitle";
import "./Expenses.css";
import NavigationBar from "../../components/NavigationBar/NavigationBar";

const PAGE_NAME = "Expenses";

function Expenses() {
  const [expenses, setExpenses] = useState([]);
  const [columns, setColumns] = useState([]);
  const [rows, setRows] = useState([]);
  const [currentMonth, setCurrentMonth] = useState(new Date().getMonth());
  const [currentYear, setCurrentYear] = useState(new Date().getFullYear());

  useEffect(() => {
    const setupData = async () => {
      await setupExpenses();
      await setupColumns();
      await setupRows();
    };

    setupData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [currentMonth, currentYear]);

  const setupExpenses = async () => {
    const currentDate = new Date(currentYear, currentMonth);
    const currentMonthName = currentDate.toLocaleString("default", {
      month: "long",
    });
    const response = await getMonthlyExpenses({
      month: currentMonthName,
      year: currentYear,
    });
    setExpenses(response);
  };

  const setupColumns = async () => {
    const currentDate = new Date(currentYear, currentMonth);
    const days = new Date(
      currentDate.getFullYear(),
      currentDate.getMonth() + 1,
      0
    ).getDate();
    setColumns(Array.from({ length: days }, (_, index) => index + 1));
  };

  const setupRows = async () => {
    const response = await getCategories();
    setRows(response);
  };

  const goToPreviousMonth = () => {
    setCurrentMonth((prevMonth) => {
      const newMonth = prevMonth - 1;
      if (newMonth < 0) {
        setCurrentYear(currentYear - 1);
        return 11;
      }
      return newMonth;
    });
  };

  const goToNextMonth = () => {
    setCurrentMonth((prevMonth) => {
      const newMonth = prevMonth + 1;
      if (newMonth > 11) {
        setCurrentYear(currentYear + 1);
        return 0;
      }
      return newMonth;
    });
  };

  return (
    <div>
      <Header selected={PAGE_NAME} />
      <PageTitle pageName={PAGE_NAME} />

      <div className="expenses-container">
        <NavigationBar
          onPreviousMonth={goToPreviousMonth}
          onNextMonth={goToNextMonth}
          currentMonth={new Date(0, currentMonth).toLocaleString("default", {
            month: "long",
          })}
          currentYear={currentYear}
        />
        <table>
          <thead>
            <tr>
              <th>Category</th>
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
                          parseInt(column) ===
                          parseInt(expense.date.split("-")[2])
                      )
                      .map((expense) => expense.value)
                      .reduce((val, newVal) => val + newVal, 0)}
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default Expenses;
