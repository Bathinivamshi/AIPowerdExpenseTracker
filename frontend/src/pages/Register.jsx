import { useState } from "react";

function Register() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [message, setMessage] = useState("");
    async function handleRegister(event) {
        event.preventDefault();
        if (!email.trim()) {
            setMessage("❌ Please enter your email.");
            return;
        }

        if (!password) {
            setMessage("❌ Please enter a password.");
            return;
        }

        if (password.length < 6) {
            setMessage("❌ Password must be at least 6 characters.");
            return;
        }

        const registerData = {
            email: email,
            password: password
        };

        console.log("Register data:", registerData);

        const response = await fetch(
            "http://localhost:8080/api/auth/register",
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(registerData)
            }
        );

        console.log("Response status:", response.status);

        if (response.ok) {
            setMessage("✅ Registration successful! Please login.");
        } else {
            setMessage("❌ Registration failed. Email may already exist.");
        }
    }

    return (
        <div className="auth-container">
            <div className="auth-card">

                <h1>💰 Expense Tracker</h1>
                <h2>Create Account</h2>

                {message && (
                    <p className="form-message">
                        {message}
                    </p>
                )}

                <form onSubmit={handleRegister}>

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
                            placeholder="Create a password"
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
                        Register
                    </button>

                </form>

            </div>
        </div>
    );
}

export default Register;