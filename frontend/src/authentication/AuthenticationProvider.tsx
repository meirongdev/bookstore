import type { ReactNode } from "react";
import { useState } from "react";
import { LoginModel } from "../models/LoginModel";
import { useLogin } from "../utils/api_fetchers/authentication_controller/useLogin";
import { AuthenticationContext } from "./authenticationContext";
import { useAuthenticationState } from "./useAuthenticationState";
import { RegistrationModel } from "../models/RegistrationModel";
import { useRegister } from "../utils/api_fetchers/authentication_controller/useRegister";

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

    const [authentication, setAuthenticationState] = useState(initState);

    const setAuthenticationWithEmail = (newAuth: any) => {
        const authWithEmail = {
            ...newAuth,
            userEmail: newAuth.token ? extractUserEmailFromToken(newAuth.token) : ""
        };
        setAuthenticationState(authWithEmail);
    };

    const register = async (userDetails: RegistrationModel,
                            setIsLoading: React.Dispatch<React.SetStateAction<boolean>>,
                            setHttpError: React.Dispatch<React.SetStateAction<string | null>>) => {

        await useRegister(userDetails, setIsLoading, setHttpError, setAuthenticationWithEmail);
    };

    const login = async (userDetails: LoginModel,
                         setIsLoading: React.Dispatch<React.SetStateAction<boolean>>,
                         setHttpError: React.Dispatch<React.SetStateAction<string | null>>) => {

        await useLogin(userDetails, setIsLoading, setHttpError, setAuthenticationWithEmail);
    }

    const logout = () => {

        const logoutState = { isAuthenticated: false, token: "", authority: "", userEmail: "" };
        setAuthenticationState(logoutState);
        localStorage.setItem("authenticationState", JSON.stringify(logoutState));
    }

    return (

        <AuthenticationContext.Provider value={{ authentication, register, login, logout }}>

            {children}

        </AuthenticationContext.Provider>

    );

}
