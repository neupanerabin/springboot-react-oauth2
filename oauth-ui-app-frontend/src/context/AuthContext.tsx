import {
  createContext,
  useContext,
  useEffect,
  useState,
  type ReactNode,
} from "react";

// Define the structure of a user object returned from the backend
type User = {
  name: string;
  email: string;
  picture: string;
  role: "USER" | "ADMIN"; // role type is restricted to either USER or ADMIN
};

/* 
  Define the shape of the AuthContext. 
  It provides:
  1. isAuthenticated: boolean -> whether the user is logged in
  2. loading: boolean -> whether auth state is still being determined
  3. user: User | null -> current user data or null if not logged in
  4. setUser: function -> updates the user state
*/
type AuthContextType = {
  isAuthenticated: boolean;
  loading: boolean;
  user: User | null;
  setUser: (user: User | null) => void;
};

// Create the AuthContext with default values
const AuthContext = createContext<AuthContextType>({
  isAuthenticated: false,
  loading: false,
  user: null,
  setUser: () => {}, // default no-op function
});

// Custom hook to access AuthContext easily
export const useAuth = () => useContext(AuthContext);

type Props = {
  children: ReactNode; // children components wrapped inside AuthProvider
};

// Context Provider implementation
export const AuthProvider = ({ children }: Props) => {
  // Store logged-in user info
  const [user, setUser] = useState<User | null>(null);

  // Track loading state (true until fetch completes)
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Call backend API to check if user is authenticated
    fetch("http://localhost:8080/api/user", {
      credentials: "include", // include cookies for session auth
    })
      .then((res) => {
        if (!res.ok) throw new Error("Not Authenticated");
        return res.json(); // Parse user JSON from backend
      })
      .then((data) => {
        console.log("data = ", data);

        // If no user data, set to null (unauthenticated)
        if (!data) {
          setUser(null);
        } else {
          // Otherwise set the user info from backend response
          setUser({
            name: data.name,
            email: data.email,
            picture: data.picture,
            role: data.role,
          });
        }
      })
      .catch((e) => {
        // Handle error (likely means not logged in)
        console.log("error = ", e);
        setUser(null);
      })
      .finally(() => setLoading(false)); // Mark loading as complete
  }, []);

  // Boolean that indicates if the user is authenticated
  const isAuthenticated = !!user;

  return (
    // Provide context values to children
    <AuthContext.Provider value={{ isAuthenticated, loading, user, setUser }}>
      {children}
    </AuthContext.Provider>
  );
};
