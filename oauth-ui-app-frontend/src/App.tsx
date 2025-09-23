import { Outlet } from "react-router";
import "./App.css";
import Login from "./components/Login";

function App() {
  return (
    <>
      <Login />
      <Outlet />
    </>
  );
}

export default App;
