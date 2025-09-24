import { useAuth } from "../context/AuthContext";
import { Navigate } from "react-router";

function Home() {
  // get the state from the context
  const { isAuthenticated, loading, user, setUser } = useAuth();

  if (loading) {
    //while checking bakend session
    return <div>Loading...</div>;
  }

  // if not logged in, then redirect to /login
  if (!isAuthenticated || !user) {
    return <Navigate to="/login" replace />;
  }

  // handle logout
  const handleLogout = () => {
    // call spring security to the logout page
    setUser(null);
    window.location.href = "http://localhost:8080/logout";  // call springboot logout endpoint
  };

  return (
    <div className="text-center mt-5">
      {user?.name} : {user?.email}
      <div className="flex flex-col items-center justify-center h-screen space-y-4">
        <h1 className="text-xl font-bold">Welcome! You are logged in 🎉</h1>
        <button
          onClick={handleLogout}
          className="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600"
        >
          Logout
        </button>
      </div>
    </div>
  );
}

export default Home;
