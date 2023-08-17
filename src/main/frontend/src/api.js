import axios from "axios";

const API_PATH = "/api/v1";
const STATISTICS = API_PATH + "/statistics";
const EXPENSES = API_PATH + "/expenses";
const CATEGORIES = API_PATH + "/categories";
const INCOME_SOURCES = API_PATH + "/income-sources";
const STORAGE = API_PATH + "/storage";
const ESTIMATED_EXPENSES = API_PATH + "/estimated-expenses";

export const GENERAL_API_ERROR_POST = "Error posting data to server";
export const GENERAL_API_ERROR_GET = "Error fetching data from server";
export const GENERAL_API_ERROR_DELETE = "Error deleting data from server";

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

export const getMonthyStatistics = async (props) => {
  try {
    const response = await axios.get(`${STATISTICS}/monthly`, {
      params: {
        month: props.month,
        year: props.year,
      },
    });
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

export const deleteExpense = async (expenseId) => {
  try {
    const response = await axios.delete(EXPENSES, {
      params: {
        id: expenseId,
      },
    });
    return response;
  } catch (error) {
    console.error(GENERAL_API_ERROR_DELETE + ": ", error);
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

export const getIncomeSources = async () => {
  try {
    const response = await axios.get(INCOME_SOURCES);
    return response.data;
  } catch (error) {
    console.error(GENERAL_API_ERROR_GET + ": ", error);
    throw error;
  }
};

export const addIncomeSource = async (income) => {
  try {
    const response = await axios.post(INCOME_SOURCES, income);
    return response;
  } catch (error) {
    console.error(GENERAL_API_ERROR_POST + ": ", error);
    return error.response.data;
  }
};

export const getLatestStorageRecord = async () => {
  try {
    const response = await axios.get(STORAGE);
    return response.data;
  } catch (error) {
    console.error(GENERAL_API_ERROR_GET + ": ", error);
    throw error;
  }
};

export const addStroageRecord = async (storage) => {
  try {
    const response = await axios.post(STORAGE, storage);
    return response;
  } catch (error) {
    console.error(GENERAL_API_ERROR_POST + ": ", error);
    return error.response.data;
  }
};

export const getEstimatedExpenses = async () => {
  try {
    const response = await axios.get(ESTIMATED_EXPENSES);
    return response.data;
  } catch (error) {
    console.error(GENERAL_API_ERROR_GET + ": ", error);
    throw error;
  }
};
