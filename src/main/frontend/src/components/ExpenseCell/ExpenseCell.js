import React from "react";
import { getDailyExpenses } from "../../api";

const ExpenseCell = (props) => {
  const { expenses, column, category, currentMonth, currentYear } = props;

  const handleCellClick = async () => {
    let month = currentMonth < 10 ? `0${currentMonth}` : currentMonth;
    let day = column < 10 ? `0${column}` : column;
    const result = await getDailyExpenses({
      date: `${currentYear}-${month}-${day}`,
      categoryId: category.id,
    });
    props.onCellClick();
    props.setDetailedCellExpenses(result.data);
  };

  let expenseValues = expenses
    .filter((expense) => expense.category.id === category.id)
    .filter(
      (expense) => parseInt(column) === parseInt(expense.date.split("-")[2])
    )
    .map((expense) => expense.value);
  let isFilledCell = expenseValues.length > 0;
  let cellValue = expenseValues.reduce((val, newVal) => val + newVal, 0);
  let classNames = `${isFilledCell ? "filled" : "empty"}${
    expenseValues.length > 1 ? " multiple" : ""
  }`;
  return (
    <td
      key={column}
      className={classNames}
      onClick={
        isFilledCell
          ? () => {
              handleCellClick();
            }
          : null
      }
    >
      {cellValue === 0 ? null : cellValue}
    </td>
  );
};

export default ExpenseCell;
