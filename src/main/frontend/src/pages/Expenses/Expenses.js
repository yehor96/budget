import React, { useEffect, useState } from "react";
import {
  getCategories,
  getMonthlyExpenses,
  getMonthlyTotalPerCategory,
} from "../../api";
import Header from "../../components/Header/Header";
import PageTitle from "../../components/PageTitle/PageTitle";
import "./Expenses.css";
import NavigationBar from "../../components/NavigationBar/NavigationBar";
import HandlerSection from "../../components/HandlerSection/HandlerSection";

const PAGE_NAME = "Expenses";
const MONTH_NAMES = [
  "January",
  "February",
  "March",
  "April",
  "May",
  "June",
  "July",
  "August",
  "September",
  "October",
  "November",
  "December",
];

function Expenses() {
  const [expenses, setExpenses] = useState([]);
  const [columns, setColumns] = useState([]);
  const [categories, setCategories] = useState([]);
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
    const response = await getMonthlyExpenses({
      month: MONTH_NAMES[currentMonth],
      year: currentYear,
    });
    setExpenses(response.data);
  };

  const setupColumns = async () => {
    const days = new Date(currentYear, currentMonth + 1, 0).getDate();
    setColumns(Array.from({ length: days }, (_, index) => index + 1));
  };

  const setupRows = async () => {
    try {
      const categories = await getCategories();
      const updatedCategories = [];
      for (const category of categories.data) {
        let total = await getMonthlyTotalPerCategory({
          categoryId: category.id,
          month: MONTH_NAMES[currentMonth],
          year: currentYear,
        });
        if (!total) {
          total = 0;
        }
        updatedCategories.push({ ...category, total });
      }
      setCategories(updatedCategories);
    } catch (error) {
      console.error(error);
      setCategories([]);
    }
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
    <div className="expenses-page">
      <Header selected={PAGE_NAME} />
      <PageTitle pageName={PAGE_NAME} />

      <div className="expenses-container">
        <NavigationBar
          onPreviousMonth={goToPreviousMonth}
          onNextMonth={goToNextMonth}
          currentMonth={MONTH_NAMES[currentMonth]}
          currentYear={currentYear}
        />
        <table>
          <thead>
            <tr>
              <th>Total</th>
              <th>Category</th>
              {columns.map((column) => (
                <th key={column}>{column}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {categories.map((category) => (
              <tr key={category.id}>
                <td>{category.total}</td>
                <td>{category.name}</td>
                {columns.map((column) => (
                  <td key={column}>
                    {expenses
                      .filter((expense) => expense.category.id === category.id)
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
        <HandlerSection categories={categories} />
      </div>
    </div>
  );
}

export default Expenses;
