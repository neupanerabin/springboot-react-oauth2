import axios from "axios";
import { useEffect } from "react";
import { useAuth } from "../context/AuthContext";

function ShowInfo() {
  const { user, loading, isAuthenticated } = useAuth();

  // if information is fetched then show the information
  useEffect(() => {
    if (!loading && isAuthenticated && user?.role === "ADMIN") {
      getInfo();
    }
  }, [loading, isAuthenticated, user]);

  const getInfo = () => {
    axios
      .get("http://localhost:8080/api/info", { withCredentials: true })
      .then((res) => {
        console.log("admin info = ", res);
      })
      .catch((err) => {
        console.log("err = ", err);
      });
  };
  // check the conditions then if user then open this else
  if (user?.role === "USER") {
    return (
      <div
        style={{
          color: "Red",
          fontSize: "40px",
          fontWeight: "bold",
          textAlign: "center",
          marginTop: "20px",
        }}
      >
        User Page
      </div>
    );
  }

  // open the secure admin page
  return (
    <div
      style={{
        color: "Red",
        fontSize: "40px",
        fontWeight: "bold",
        textAlign: "center",
        marginTop: "20px",
      }}
    >
      Secured Admin Page
    </div>
  );
}

export default ShowInfo;
