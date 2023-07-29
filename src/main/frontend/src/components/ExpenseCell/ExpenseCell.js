import React from "react";
import { getDailyExpenses } from "../../api";

const ExpenseCell = (props) => {
  const { expenses, column, category, currentMonth, currentYear } = props;

  const handleCellClick = async () => {
    let month = currentMonth < 10 ? `0${currentMonth}` : currentMonth;
    let day = column < 10 ? `0${column}` : column;
    let date = `${currentYear}-${month}-${day}`;
    const result = await getDailyExpenses({
      date: date,
      categoryId: category.id,
    });
    props.onCellClick();
    props.setDetailedCellInfo({
      expenses: result.data,
      category: category,
      date: date,
    });
  };

  let expenseValues = expenses
    .filter((expense) => expense.category.id === category.id)
    .filter(
      (expense) => parseInt(column) === parseInt(expense.date.split("-")[2])
    )
    .map((expense) => expense.value);
  let cellValue = expenseValues.reduce((val, newVal) => val + newVal, 0);
  let classNames = expenseValues.length > 1 ? "multiple" : null;
  return (
    <td key={column} className={classNames} onClick={() => handleCellClick()}>
      {cellValue === 0 ? null : cellValue}
    </td>
  );
};

export default ExpenseCell;
