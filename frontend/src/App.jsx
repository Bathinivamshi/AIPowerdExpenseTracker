import { useState } from "react";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Dashboard from "./components/DashBoard";
import "./App.css";

function App() {
    const [page, setPage] = useState("login");

    const token = localStorage.getItem("token");

    if (token) {
        return <Dashboard />;
    }

    if (page === "register") {
        return (
            <div className="auth-container">
                <div className="auth-card">
                    <Register />

                    <button
                        className="secondary-button"
                        onClick={() => setPage("login")}
                    >
                        ← Back to Login
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="auth-container">
            <div className="auth-card">
                <Login />

                <button
                    className="secondary-button"
                    onClick={() => setPage("register")}
                >
                    Create an Account
                </button>
            </div>
        </div>
    );
}

export default App;