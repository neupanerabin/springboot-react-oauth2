import { useLocation, useNavigate } from "react-router";
import { useAuth } from "../context/AuthContext";
import { useEffect } from "react";

function Login() {
  const { isAuthenticated } = useAuth();

  const navigate = useNavigate();
  const location = useLocation();

  const fromLocation = location.state?.from?.pathname || "/";

  // check whether it is authenticated or not
  useEffect(() => {
    if (isAuthenticated) {
      navigate(fromLocation, { replace: true }); // then it goes to the authentication
    }
  }, [isAuthenticated, fromLocation, navigate]);

  const handleLogin = (provider: string) => {
    if (provider === "google") {
      window.location.href = "http://localhost:8080/oauth2/authorization/google"; // url take to the google & do authentication and authorization
    } else return;
  };

  return (
    <div className="flex flex-col items-center justify-center h-screen space-y-4">
      <h1 className="text-x1 mb-4"> Login Page</h1>

      {/* Google Login Button */}
      <button
        className="bg-white border border-gray-300 px-6 py-2 rounded flex items-center gap-2 cursor-pointer"
        onClick={() => handleLogin("google")} 
      >
        <img
          src="http://developers.google.com/identity/images/g-logo.png"
          alt="Google"
          className="w-5 h-5"
        />
        Sign in with google
      </button>
    </div>
  );
}

export default Login;
