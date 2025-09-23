import {
  createContext,
  useContext,
  useEffect,
  useState,
  type ReactNode,
} from "react";

{
  /*these are 2 types
  1. for the authentication
  2. for loading
  */
}
type AuthContextType = {
  isAuthenticated: boolean;
  loading: boolean;
};

const AuthContext = createContext<AuthContextType>({
  isAuthenticated: false,
  loading: false,
});

// create custom hook
export const useAuth = () => useContext(AuthContext);

type Props = {
  children: ReactNode;
};

// export from here
export const AuthProvider = ({ children }: Props) => {
  // 2 use state
  const [loading, setLoading] = useState(true);
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  useEffect(() => {
    fetch("http://localhost:8080/api/user", {
      credentials: "include",
    })
      .then((res) => {
        if (!res.ok) throw new Error("Not Authenticated");
        return res.json();
      })
      .then((data) => {
        console.log("data = ", data);
        if (data !== null) {
          setIsAuthenticated(true);
        }
      })
      .catch((e) => console.log("error = ", e))
      .finally(() => setLoading(false));
  }, []);

  return (
    <AuthContext.Provider value={{ isAuthenticated, loading }}>
      {children}
    </AuthContext.Provider>
  );
};
