import "./App.css";
import React from "react";
import Expenses from "./pages/Expenses/Expenses";
import Planning from "./pages/Planning/Planning";
import { createBrowserRouter, RouterProvider } from "react-router-dom";

const router = createBrowserRouter([
  {
    path: "/",
    element: <Expenses />,
  },
  {
    path: "expenses/",
    element: <Expenses />,
  },
  {
    path: "planning/",
    element: <Planning />,
  },
]);

function App() {
  return <RouterProvider router={router} />;
}

export default App;
