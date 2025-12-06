import { Link, Navigate } from "react-router-dom"
import { FieldErrors } from "../../commons/field_errors/FieldErrors"
import { useState } from "react";
import { LoginModel } from "../../../models/LoginModel";
import { useAuthenticationContext } from "../../../authentication/authenticationContext";
import { FormLoader } from "../../commons/form_loader/FormLoader";
import { HttpErrorMessage } from "../../commons/http_error_message/HttpErrorMessage";

export const LoginPage = () => {

    const { authentication, login } = useAuthenticationContext();

    const [userDetails, setUserDetails] = useState<LoginModel>({ email: "", password: "" });
    const [isLoading, setIsLoading] = useState(false);
    const [httpError, setHttpError] = useState<string | null>(null);

    const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {

        setUserDetails({ ...userDetails, [event.target.name]: event.target.value });
    };

    const handleSignInClick = async () => {

        await login(userDetails, setIsLoading, setHttpError);
    };

    if (authentication.isAuthenticated) return <Navigate to={"/"} />

    return (

        <div className="page-container">

            <div className="container-centered px-5">

                <div className="custom-form max-w-md mx-auto">

                    <h1 className="text-center text-3xl font-bold text-gray-800 mb-6">Sign In</h1>

                    <FormLoader isLoading={isLoading} />

                    {(httpError && !httpError.startsWith("Some")) && <HttpErrorMessage httpError={httpError} />}

                    <form className="flex flex-col gap-5 w-full">

                        <div className="flex flex-col gap-1">
                            <FieldErrors fieldName="email" httpError={httpError} />
                            <input
                                type="email"
                                name="email"
                                onChange={handleChange}
                                placeholder="Email"
                                className="input"
                            />
                        </div>

                        <div className="flex flex-col gap-1">
                            <FieldErrors fieldName="password" httpError={httpError} />
                            <input
                                type="password"
                                name="password"
                                onChange={handleChange}
                                placeholder="Password"
                                className="input"
                            />
                        </div>

                    </form>

                    <button className="btn-primary w-full" onClick={handleSignInClick}>
                        Sign In
                    </button>

                    <div className="divider" />

                    <div className="text-center">
                        <p className="text-gray-600 mb-3">Don't have an account?</p>
                        <Link to={"/register"} className="btn-secondary w-full">
                            Create Account
                        </Link>
                    </div>

                </div>

            </div>

        </div>

    )

}
