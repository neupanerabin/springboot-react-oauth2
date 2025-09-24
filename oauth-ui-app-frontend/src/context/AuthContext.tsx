import {
  createContext,
  useContext,
  useEffect,
  useState,
  type ReactNode,
} from "react";

type User = {
  name: string;
  email: string;
  picture: string;
  role: "USER" | "ADMIN";
};

{
  /*these are 2 types
  1. for the authentication
  2. for loading
  */
}
type AuthContextType = {
  isAuthenticated: boolean;
  loading: boolean;
  user: User | null;
  setUser: (user: User | null) => void;
};

const AuthContext = createContext<AuthContextType>({
  isAuthenticated: false,
  loading: false,
  user: null,
  setUser: () => {},
});

// create custom hook
export const useAuth = () => useContext(AuthContext);

type Props = {
  children: ReactNode;
};

// export from here
export const AuthProvider = ({ children }: Props) => {
  const [user, setUser] = useState<User | null>(null);
  // 2 use state
  const [loading, setLoading] = useState(true);

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
        if (!data) {
          setUser(null);
        } else {
          setUser({
            name: data.name,
            email: data.email,
            picture: data.picture,
            role: data.role,
          });
        }
      })
      .catch((e) => {
        console.log("error = ", e);
        setUser(null);
      })
      .finally(() => setLoading(false));
  }, []);

  const isAuthenticated = !!user;   // define authenticated state

  return (
    <AuthContext.Provider value={{ isAuthenticated, loading, user, setUser }}>
      {children}
    </AuthContext.Provider>
  );
};
