import type { ReactNode } from "react";
import { useState, useMemo } from "react";
import { LoginModel } from "../models/LoginModel";
import { loginUser } from "../utils/api_fetchers/authentication_controller/loginUser";
import { AuthenticationContext } from "./authenticationContext";
import { useAuthenticationState } from "./useAuthenticationState";
import { RegistrationModel } from "../models/RegistrationModel";
import { registerUser } from "../utils/api_fetchers/authentication_controller/registerUser";

/**
 * Extract user email from JWT token
 */
const extractUserEmailFromToken = (token: string): string => {
    try {
        // JWT format: header.payload.signature
        const parts = token.split('.');
        if (parts.length !== 3) {
            console.error("Invalid JWT format");
            return "";
        }

        // Decode payload
        const payload = JSON.parse(atob(parts[1]));

        // Extract email from 'sub' (subject) field
        // The backend stores email in the 'sub' claim
        return payload.sub || "";
    } catch (error) {
        console.error("Failed to extract email from token:", error);
        return "";
    }
};

type AuthenticationProviderProps = {
    children: ReactNode
}

export const AuthenticationProvider = ({ children }: AuthenticationProviderProps) => {

    const authenticationState = useAuthenticationState();

    // 初始化时从 token 中提取邮箱
    const initState = {
        ...authenticationState,
        userEmail: authenticationState.token ? extractUserEmailFromToken(authenticationState.token) : ""
    };

    const [authentication, setAuthentication] = useState(initState);

    const setAuthenticationWithEmail = (newAuth: any) => {
        const authWithEmail = {
            ...newAuth,
            userEmail: newAuth.token ? extractUserEmailFromToken(newAuth.token) : ""
        };
        setAuthentication(authWithEmail);
    };

    const register = async (userDetails: RegistrationModel,
                            setIsLoading: React.Dispatch<React.SetStateAction<boolean>>,
                            setHttpError: React.Dispatch<React.SetStateAction<string | null>>) => {

        await registerUser(userDetails, setIsLoading, setHttpError, setAuthenticationWithEmail);
    };

    const login = async (userDetails: LoginModel,
                         setIsLoading: React.Dispatch<React.SetStateAction<boolean>>,
                         setHttpError: React.Dispatch<React.SetStateAction<string | null>>) => {

        await loginUser(userDetails, setIsLoading, setHttpError, setAuthenticationWithEmail);
    }

    const logout = () => {

        const logoutState = { isAuthenticated: false, token: "", authority: "", userEmail: "" };
        setAuthentication(logoutState);
        localStorage.setItem("authenticationState", JSON.stringify(logoutState));
    }

    const contextValue = useMemo(() => ({
        authentication,
        register,
        login,
        logout
    }), [authentication]);

    return (

        <AuthenticationContext.Provider value={contextValue}>

            {children}

        </AuthenticationContext.Provider>

    );

}
