import axios from "axios";

const API_PATH = "/api/v1";
const EXPENSES = API_PATH + "/expenses";
const CATEGORIES = API_PATH + "/categories";

export const GENERAL_API_ERROR_POST = "Error posting data to server";
export const GENERAL_API_ERROR_GET = "Error fetching data from server";

export const getMonthlyExpenses = async (props) => {
  try {
    const response = await axios.get(EXPENSES + "/monthly", {
      params: {
        month: props.month,
        year: props.year,
      },
    });
    return response;
  } catch (error) {
    console.error(GENERAL_API_ERROR_GET + ": ", error);
    throw error;
  }
};

export const getDailyExpenses = async (props) => {
  try {
    const response = await axios.get(
      `${EXPENSES}/daily/category/${props.categoryId}`,
      {
        params: {
          date: props.date,
        },
      }
    );
    return response;
  } catch (error) {
    console.error(GENERAL_API_ERROR_GET + ": ", error);
    throw error;
  }
};

export const getMonthlyTotalPerCategory = async (props) => {
  try {
    const response = await axios.get(
      `${EXPENSES}/monthly/category/${props.categoryId}`,
      {
        params: {
          month: props.month,
          year: props.year,
        },
      }
    );
    return response.data;
  } catch (error) {
    console.error(GENERAL_API_ERROR_GET + ": ", error);
    throw error;
  }
};

export const addExpense = async (expense) => {
  try {
    const response = await axios.post(EXPENSES, expense);
    return response;
  } catch (error) {
    console.error(GENERAL_API_ERROR_POST + ": ", error);
    return error.response.data;
  }
};

export const getCategories = async () => {
  try {
    const response = await axios.get(CATEGORIES);
    return response;
  } catch (error) {
    console.error(GENERAL_API_ERROR_GET + ": ", error);
    throw error;
  }
};
