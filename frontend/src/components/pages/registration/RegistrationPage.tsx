import { useState } from "react"
import { RegistrationModel } from "../../../models/RegistrationModel";
import { FieldErrors } from "../../commons/field_errors/FieldErrors";
import { FormLoader } from "../../commons/form_loader/FormLoader";
import { useAuthenticationContext } from "../../../authentication/authenticationContext";
import { Link } from "react-router-dom";
import { HttpErrorMessage } from "../../commons/http_error_message/HttpErrorMessage";

export const RegistrationPage = () => {

    const { authentication, register } = useAuthenticationContext();

    const [userDetails, setUserDetails] = useState<RegistrationModel>({firstName: "", lastName: "", dateOfBirth: new Date("1800-01-01"), email: "", password: ""});
    const [isLoading, setIsLoading] = useState(false);
    const [httpError, setHttpError] = useState<string | null>(null);

    const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {

        setUserDetails({ ...userDetails, [event.target.name]: event.target.value });
    };

    const handleRegisterClick = async () => {

        await register(userDetails, setIsLoading, setHttpError);
    };

    return (

        <div className="page-container">

            <div className="container-centered px-5">

                <div className="custom-form max-w-md mx-auto">

                    <h1 className="text-center text-3xl font-bold text-gray-800 mb-6">Create Account</h1>

                    <FormLoader isLoading={isLoading} />

                    {(httpError && !httpError.startsWith("Some")) && <HttpErrorMessage httpError={httpError} />}

                    {authentication.isAuthenticated ?

                        <>

                            <div className="flex flex-col gap-4 text-center">
                                <div className="text-green-600 text-5xl">✓</div>
                                <h2 className="text-2xl font-bold text-gray-800">Welcome!</h2>
                                <div className="text-gray-700 space-y-2">
                                    <p>Your account has been created successfully.</p>
                                    <p>Thank you for joining our community.</p>
                                </div>
                            </div>

                            <Link to={"/home"} className="btn-primary w-full">
                                Start Browsing
                            </Link>

                        </>

                        :

                        <>

                            <form className="flex flex-col gap-5 w-full">

                                <div className="flex flex-col gap-1">
                                    <FieldErrors fieldName="firstName" httpError={httpError} />
                                    <input
                                        type="text"
                                        name="firstName"
                                        onChange={handleChange}
                                        placeholder="First name"
                                        className="input"
                                    />
                                </div>

                                <div className="flex flex-col gap-1">
                                    <FieldErrors fieldName="lastName" httpError={httpError} />
                                    <input
                                        type="text"
                                        name="lastName"
                                        onChange={handleChange}
                                        placeholder="Last name"
                                        className="input"
                                    />
                                </div>

                                <div className="flex flex-col gap-1">
                                    <FieldErrors fieldName="dateOfBirth" httpError={httpError} />
                                    <div className="flex gap-3 items-center">
                                        <span className="text-gray-700 font-medium whitespace-nowrap">
                                            Date of birth:
                                        </span>
                                        <input
                                            type="date"
                                            name="dateOfBirth"
                                            onChange={handleChange}
                                            className="input flex-1"
                                        />
                                    </div>
                                </div>

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

                            <button className="btn-primary w-full" onClick={handleRegisterClick}>
                                Create Account
                            </button>

                            <div className="divider" />

                            <div className="text-center">
                                <p className="text-gray-600 mb-3">Already have an account?</p>
                                <Link to={"/login"} className="btn-secondary w-full">
                                    Sign In
                                </Link>
                            </div>

                        </>

                    }

                </div>

            </div>

        </div>

    )

}
