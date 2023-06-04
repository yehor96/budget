import axios from "axios";

const baseUrl = "http://localhost:8080"; // for development

export const getMonthlyExpenses = (date) => {
  try {
    return axios.get(baseUrl + "/api/v1/expenses/monthly", {
      params: {
        month: date.month,
        year: date.year,
      },
    });
  } catch (error) {
    console.error("Error fetching data:", error);
  }
};

export const getCategories = () => {
  try {
    return axios.get(baseUrl + "/api/v1/categories");
  } catch (error) {
    console.error("Error fetching data:", error);
  }
};
