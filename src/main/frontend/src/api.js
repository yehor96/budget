import axios from "axios";

export const getMonthlyExpenses = async (date) => {
  try {
    const response = await axios.get("/api/v1/expenses/monthly", {
      params: {
        month: date.month,
        year: date.year,
      },
    });
    return response;
  } catch (error) {
    console.error("Error fetching data from server:", error);
    throw error;
  }
};

export const addExpense = async (expense) => {
  try {
    const response = await axios.post("/api/v1/expenses", expense);
    return response;
  } catch (error) {
    console.error("Error posting data to server:", error);
    return error.response;
  }
};

export const getCategories = async () => {
  try {
    const response = await axios.get("/api/v1/categories");
    return response;
  } catch (error) {
    console.error("Error fetching data from server:", error);
    throw error;
  }
};
