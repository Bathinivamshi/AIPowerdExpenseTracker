import { useState } from "react";


function Login() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [message, setMessage] = useState("");

    async function handleLogin(event) {
        event.preventDefault();
        if (!email.trim()) {
            setMessage("❌ Please enter your email.");
            return;
        }

        if (!password) {
            setMessage("❌ Please enter your password.");
            return;
        }

        if (password.length < 6) {
            setMessage("❌ Password must be at least 6 characters.");
            return;
        }
        const loginData = {
            email: email,
            password: password
        };

        console.log("Login data:", loginData);

        const response = await fetch("http://localhost:8080/api/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(loginData)
        });

        console.log("Response status:", response.status);

        if (response.ok) {
            const data = await response.json();

            localStorage.setItem("token", data.token);

            window.location.reload();
        } else {
            setMessage("❌ Invalid email or password.");
        }
    }


    return (
        <div className="auth-container">
            <div className="auth-card">

                <h1>💰 Expense Tracker</h1>
                <h2>Login</h2>

                {message && (
                    <p className="form-message">
                        {message}
                    </p>
                )}

                <form onSubmit={handleLogin}>

                    <div className="form-group">
                        <label>Email</label>
                        <input
                            type="email"
                            placeholder="Enter your email"
                            value={email}
                            onChange={(event) =>
                                setEmail(event.target.value)
                            }
                        />
                    </div>

                    <div className="form-group">
                        <label>Password</label>
                        <input
                            type="password"
                            placeholder="Enter your password"
                            value={password}
                            onChange={(event) =>
                                setPassword(event.target.value)
                            }
                        />
                    </div>

                    <button
                        className="primary-button"
                        type="submit"
                    >
                        Login
                    </button>

                </form>

            </div>
        </div>
    );
}

export default Login;