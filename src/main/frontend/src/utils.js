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

// 2023-07-06 => July 6, 2023
export const formatDate = (dateInput) => {
  const date = new Date(dateInput);
  const monthIndex = date.getMonth();
  const day = date.getDate();
  const year = date.getFullYear();

  return `${MONTH_NAMES[monthIndex]} ${day}, ${year}`;
};
