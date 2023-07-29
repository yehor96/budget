import React, { useEffect, useState } from "react";
import {
  getCategories,
  getMonthlyExpenses,
  getMonthyStatistics,
} from "../../api";
import Header from "../../components/Header/Header";
import PageTitle from "../../components/PageTitle/PageTitle";
import "./Expenses.css";
import NavigationBar from "../../components/NavigationBar/NavigationBar";
import HandlerSection from "../../components/HandlerSection/HandlerSection";
import ExpenseCell from "../../components/ExpenseCell/ExpenseCell";
import DetailedCellModal from "../../modals/DetailedCellModal/DetailedCellModal";

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
  const [statistics, setStatistics] = useState({});
  const [columns, setColumns] = useState([]);
  const [categories, setCategories] = useState([]);
  const [currentMonth, setCurrentMonth] = useState(new Date().getMonth());
  const [currentYear, setCurrentYear] = useState(new Date().getFullYear());
  const [showDetailCellModal, setShowDetailCellModal] = useState(false);
  const [detailedCellExpenses, setDetailedCellExpenses] = useState([]);

  useEffect(() => {
    const setupData = async () => {
      await setupStatistics();
      await setupExpenses();
      await setupColumns();
    };

    setupData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [currentMonth, currentYear]);

  useEffect(() => {
    if (statistics && Object.keys(statistics).length > 0) {
      setupRows();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [statistics]);

  const setupExpenses = async () => {
    const response = await getMonthlyExpenses({
      month: MONTH_NAMES[currentMonth],
      year: currentYear,
    });
    setExpenses(response.data);
  };

  const setupStatistics = async () => {
    const response = await getMonthyStatistics({
      month: MONTH_NAMES[currentMonth],
      year: currentYear,
    });
    setStatistics(response);
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
        let total = statistics.totalsPerCategory[category.name];
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

  const isToday = (dayValue) => {
    let now = new Date();
    return (
      now.getFullYear() === parseInt(currentYear) &&
      now.getMonth() === parseInt(currentMonth) &&
      now.getDate() === parseInt(dayValue)
    );
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
                <th
                  key={column}
                  className={isToday(column) ? "current-selected" : ""}
                >
                  {column}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {categories.map((category) => (
              <tr key={category.id}>
                <td>{category.total}</td>
                <td>{category.name}</td>
                {columns.map((column) => (
                  <ExpenseCell
                    key={column}
                    expenses={expenses}
                    column={column}
                    category={category}
                    currentMonth={currentMonth + 1}
                    currentYear={currentYear}
                    onCellClick={() => {
                      setShowDetailCellModal(true);
                    }}
                    setDetailedCellExpenses={setDetailedCellExpenses}
                  />
                ))}
              </tr>
            ))}
          </tbody>
        </table>
        <DetailedCellModal
          show={showDetailCellModal}
          onClose={() => {
            setShowDetailCellModal(false);
            window.location.reload();
          }}
          expenses={detailedCellExpenses}
        />
        <HandlerSection categories={categories} statistics={statistics}/>
      </div>
    </div>
  );
}

export default Expenses;
