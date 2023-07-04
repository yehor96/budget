import axios from "axios";

export const GENERAL_API_ERROR_POST = "Error posting data to server";
export const GENERAL_API_ERROR_GET = "Error fetching data from server";

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
    console.error(GENERAL_API_ERROR_GET + ": ", error);
    throw error;
  }
};

export const addExpense = async (expense) => {
  try {
    const response = await axios.post("/api/v1/expenses", expense);
    return response;
  } catch (error) {
    console.error(GENERAL_API_ERROR_POST + ": ", error);
    return error.response.data;
  }
};

export const getCategories = async () => {
  try {
    const response = await axios.get("/api/v1/categories");
    return response;
  } catch (error) {
    console.error(GENERAL_API_ERROR_GET + ": ", error);
    throw error;
  }
};
